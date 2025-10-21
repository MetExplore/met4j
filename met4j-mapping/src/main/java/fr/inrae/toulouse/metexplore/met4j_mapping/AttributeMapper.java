package fr.inrae.toulouse.metexplore.met4j_mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A generic class to retrieve any BioEntity in a BioNetwork from a value of one of its attribute
 *
 * @param <E> a type of BioEntity
 * @param <A> a type of attribute to map
 *
 * @author clement
 * @version $Id: $Id
 */
public class AttributeMapper<E extends BioEntity,A>{

    private final BioNetwork bn;
    private final Function<BioNetwork, Collection<E>> getEntities;
    private final Function<E, Collection<A>> getAttributes;
    @Getter
    @Setter
    private BiFunction<A,A,Boolean> matcher = Object::equals;

    /**
     * A generic class to retrieve any BioEntity in a BioNetwork from a value of one of its attribute
     * @param bioNetwork a bionetwork
     * @param getCollectionToMap a function that takes the bionetwork and return the collection where the entities will
     *                           be searched
     * @param getAttribute a function that takes an entity and return the collection of attribute values to map
     */
    public AttributeMapper(BioNetwork bioNetwork,
                           Function<BioNetwork, Collection<E>> getCollectionToMap,
                           Function<E, Collection<A>> getAttribute){
        this.bn=bioNetwork;
        this.getEntities = getCollectionToMap;
        this.getAttributes = getAttribute;
    }

    /**
     * A generic class to retrieve any BioEntity in a BioNetwork from a value of one of its attribute
     * @param bioNetwork a bionetwork
     * @param getCollectionToMap a function that takes the bionetwork and return the collection where the entities will
     *                           be searched
     * @param getAttribute a function that takes an entity and return the collection of attribute values to map
     * @param matcher a function that takes two attribute values (the expected, then the observed) and return true if they match
     */
    public AttributeMapper(BioNetwork bioNetwork,
                           Function<BioNetwork, Collection<E>> getCollectionToMap,
                           Function<E, Collection<A>> getAttribute,
                           BiFunction<A,A,Boolean> matcher){
        this(bioNetwork,
            getCollectionToMap,
            getAttribute);
        this.matcher = matcher;
    }

    /**
     * From a set of attribute values, return a map of attribute values to the list of entities that match them
     * @param query a set of attribute values to search for
     * @return a map of attribute values to the list of entities that match them
     */
    public Map<A, List<E>> map(Set<A> query){
        Map<A,List<E>> results = new java.util.HashMap<>();
        for(E e : getEntities.apply(bn)){
            Collection<A> atts = getAttributes.apply(e);
            if(atts!=null && !atts.isEmpty()){
                for(A att : atts){
                    for(A valueToMatch : query){
                        if(matcher.apply(att,valueToMatch)){
                            results.computeIfAbsent(valueToMatch, hits -> new java.util.ArrayList<>());
                            results.get(valueToMatch).add(e);
                        }
                    }
                }
            }
        }
        return results;
    }

    /**
     * A function that can be used to retrieve external identifiers from a given database name
     * @param dbName the name of the database to search in the references of the entity
     * @param <E> a type of BioEntity
     * @return a function that takes an entity and return the collection of external identifiers from the given database
     */
    public static <E extends BioEntity> Function<E,  Collection<String>> selectByExternalId(String dbName){
        return e -> {
            if(e.getRefs(dbName)!=null && !e.getRefs(dbName).isEmpty()){
                return e.getRefs(dbName).stream().map(BioRef::getId).toList();
            }
            return null;
        };
    }

    /**
     * A function that can be used to retrieve molecular weights from BioMetabolite entities
     * @return a function that takes a BioMetabolite and return its molecular weight
     */
    public static Function<BioMetabolite,  Collection<Double>> selectByMass(){
        return e -> {
            if(e.getMolecularWeight()!=null){
                return List.of(e.getMolecularWeight());
            }
            return null;
        };
    }

    /**
     * A function that can be used to retrieve chemical formulas from BioMetabolite entities
     * @return a function that takes a BioMetabolite and return its chemical formula
     */
    public static Function<BioMetabolite,  Collection<String>> selectByFormula(){
        return e -> {
            if(e.getChemicalFormula()!=null){
                return List.of(e.getChemicalFormula());
            }
            return null;
        };
    }

    /**
     * A function that can be used to retrieve EC numbers from BioReaction entities
     * @param transitive if true, retrieve also all parent classes of the EC number(s)
     * @return a function that takes a BioReaction and return its EC number(s). If transitive is true, all parent classes of the EC number(s) will also be returned
     */
    public static Function<BioReaction, Collection<String>> selectByEC(boolean transitive){
        return e -> {
            String EC = e.getEcNumber();
            if(EC!=null){
                return transitive ? propagateECtoParentClasses(EC) : List.of(EC);
            }else{
                Collection<BioRef> refs = e.getRefs("ec-code");
                if(refs!=null && !refs.isEmpty()){
                    List<String> ecs = refs.stream().map(BioRef::getId).toList();
                    if(transitive){
                        List<String> propagatedEcs = new java.util.ArrayList<>();
                        for(String ec : ecs){
                            propagatedEcs.addAll(propagateECtoParentClasses(ec));
                        }
                        return propagatedEcs;
                    }
                    return ecs;
                }
            }
            return null;
        };
    }

    /*
    * A function that, given an EC number, will return it in a list also containing all its parent classes
    * E.g. given 1.2.3.4, it will return [1.2.3.4,1.2.3,1.2,1]
    */
    private static List<String> propagateECtoParentClasses(String ec){

        List<String> ecs = new java.util.ArrayList<>();
        Pattern parentLvlsPattern = Pattern.compile("(.+)\\.[^\\.]+$"); //keep everything but last digits
        boolean go = true;
        while (go) {
            ecs.add(ec);
            Matcher m = parentLvlsPattern.matcher(ec);
            if (m.matches()) {
                ec = m.group(1);
            } else {
                go = false;
            }
        }
        return ecs;
    }



    /**
     * A function that can be used to retrieved Bioentity from a Double-typed attribute (typically mass), using a ppm threshold to define a match.
     * Returns true for a,b if abs(a-b)/a*1,000,000 <= ppmThreshold.
     * @param relativeThreshold the maximum ppm difference to define a match
     * @return a BiFunction that takes two Double values and return true if they match within the ppm threshold
     */
    public static BiFunction<Double,Double,Boolean> useRelativeThreshold(Double relativeThreshold){
        return (a,b) -> (Math.abs(a-b)/a) <= relativeThreshold;
    }

}
