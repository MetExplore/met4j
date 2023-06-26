package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A class to create, from a network with multiple compartments, a new network with a single compartment, avoiding duplicated compounds.
 */
public class CompartmentMerger {

    //criterion for identifying same compound over multiple compartments
    //default use name as common field
    Function<BioMetabolite,String> getUniqIdFunction = BioMetabolite::getName;
    //criterion for selecting a unique representative from a group of compound instances
    //default takes first id in alphabetical order
    Function<List<BioMetabolite>,BioMetabolite> pickFunction = (l -> {l.sort(Comparator.comparing(BioMetabolite::getId));return l.get(0);});

    //unique compartment
    //default is named "NA"
    BioCompartment uniqComp = new BioCompartment("1", "NA");

    // map for each compound toward their unique counterpart  (can be themselves)
    HashMap<BioMetabolite,BioMetabolite> convert;
    // merged bioNetwork
    BioNetwork merged;

    // merged network name
    String name;

    /**
     * Create a new Compartment Merger
     */
    public CompartmentMerger(){
    }

    /**
     * Fluent builder setting the function that provides the criterion used for identifying same compounds over multiple compartments,
     * using a custom function provided as argument.
     * Default use "name" as common field for the same compound over multiple compartments
     * @param uniqIdFunction the function
     * @return a CompartmentMerger instance
     */
    public CompartmentMerger setGetUniqIdFunction(Function<BioMetabolite, String> uniqIdFunction) {
        this.getUniqIdFunction = uniqIdFunction;
        return this;
    }

    /**
     * Fluent builder setting both functions that provides the criterion used for identifying same compounds over multiple compartments,
     * and that creates a unique representative for such compounds, using a common identifier convention.
     * This will strip the two last characters from compound identifiers to create shared ids that will be used in final merged network.
     * This should be used for SBML that use the naming convention "xxx_y" for compounds, where xxx is the base identifier and y is the compound identifier (single letter).
     * @return a CompartmentMerger instance
     */
    public CompartmentMerger usePalssonIdentifierConvention() {
        this.getUniqIdFunction =  c -> c.getId().substring(0,c.getId().length()-2);
        this.pickFunction = (l -> {
            BioMetabolite oldComp = l.get(0);
            return new BioMetabolite(oldComp,oldComp.getId().substring(0,oldComp.getId().length()-2));
        });
        return this;
    }

    /**
     * Fluent builder setting both functions that provides the criterion used for identifying same compounds over multiple compartments,
     * and that creates a unique representative for such compounds, when compound identifiers contains explicit compartment info.
     * This use a provided regex to extract a shared base identifier from compound identifiers, and used it in final merged network.
     * This should be used for SBML that use a compound identifier convention containing a base identifier and a compartment suffix/prefix,
     * such as "xxx_y" (regex "^(\\w+)_\\w$") "xxx[y]" or "xxx-yyy", where xxx is the base identifier and y is the compound identifier.
     * @return a CompartmentMerger instance
     */
    public CompartmentMerger useBaseIdentifierRegex(String regex) {
        this.getUniqIdFunction =  (v ->{
            String id = v.getId();
            Matcher m = Pattern.compile(regex).matcher(id);
            if(m.matches()) id=m.group(1);
            return id;});
        this.pickFunction = (l -> {
            BioMetabolite oldComp = l.get(0);
            String id = oldComp.getId();
            Matcher m = Pattern.compile(regex).matcher(id);
            if(m.matches()) id=m.group(1);
            return new BioMetabolite(oldComp,id);
        });
        return this;
    }

    /**
     * Fluent builder setting the function that select or create a unique representative from a group of compound instances
     * default return compound from list with first id in alphabetical order.
     * A new compound with custom id can be returned, but if an id is generated twice or more, the corresponding groups will be merged into a single one
     * @param compoundMergeFunction the function
     * @return a CompartmentMerger instance
     */
    public CompartmentMerger setCompoundMergeFunction(Function<List<BioMetabolite>, BioMetabolite> compoundMergeFunction) {
        this.pickFunction = compoundMergeFunction;
        return this;
    }

    /**
     * Fluent builder setting the compartment where all the compounds will be merged.
     * Default is a compartment named "NA".
     * @param uniqComp the single compartment
     * @return a CompartmentMerger instance
     */
    public CompartmentMerger setUniqCompartment(BioCompartment uniqComp) {
        this.uniqComp = uniqComp;
        return this;
    }

    /**
     * Fluent builder setting the merged network name.
     * Default append "_compartments-merged" to original network name.
     * @param name the name
     * @return a CompartmentMerger instance
     */
    public CompartmentMerger setNewNetworkName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Merge compartments by indexing compounds to identify groups of same compounds over different compartments, and select or
     * create a unique compound to be added to a new single compartment.
     * @param original the original network
     * @return a network with merged compartments
     */
    public BioNetwork merge(BioNetwork original){

        //create new network with same metadata and single compartment
        buildNetwork(original);

        //group corresponding compounds
        Map<String, List<BioMetabolite>> compoundGroups = original.getMetabolitesView().stream().collect(Collectors.groupingBy(getUniqIdFunction));

        //for each group, create a unique compound
        convert = new HashMap<>();
        for(List<BioMetabolite> toContract : compoundGroups.values()){
            BioMetabolite uniq = buildCompound(toContract); //(add newly created compound to new network)
            //populate map for each compound toward their unique counterpart  (can be themselves)
            for(BioMetabolite m : toContract){
                convert.put(m,uniq);
            }
        }

        //copy Gene, Protein and Enzyme
        keepGPR(original);

        //for each reaction, replace reactants by their unique counterpart
        for(BioReaction r : original.getReactionsView()){
            // create deep copy, except for reactants
            buildReaction(r);
        }

        //copy Pathways
        for (BioPathway pathway : original.getPathwaysView()) {

            BioPathway newPathway = new BioPathway(pathway);
            merged.add(newPathway);

            // Add reactions into pathway
            BioCollection<BioReaction> reactions = original.getReactionsFromPathways(pathway);

            for (BioReaction reaction : reactions) {
                BioReaction newReaction = merged.getReaction(reaction.getId());
                merged.affectToPathway(newPathway, newReaction);
            }
        }

        //remove reactions that create loops
        removeLoops();

        //remove redundant reactions?
        //TODO

        return merged;
    }

    private void buildNetwork(BioNetwork original){
        //create new network with single compartment
        merged = new BioNetwork();
        if(name == null){
            merged.setName(original.getName()+"_compartments-merged");
        }else{
            merged.setName(name);
        }
        merged.addCompartment(uniqComp);
        //update metadata
        merged.setSynonyms(new ArrayList<>(original.getSynonyms()));
        merged.setComment(original.getComment());
        merged.setRefs(new HashMap<>(original.getRefs()));
        merged.setAttributes(new HashMap<>(original.getAttributes()));
    }

    private BioMetabolite buildCompound(List<BioMetabolite> originalCtoMerge){
        //from compounds to merge, pick one as template for new unique compound
        BioMetabolite chosen = pickFunction.apply(originalCtoMerge);
        BioMetabolite newMetabolite = new BioMetabolite(chosen);
        BioMetabolite old = merged.getMetabolite(newMetabolite.getId());
        if(old!=null){
            System.err.println("WARNING: collision in new compound identifiers. Compounds with different unique ids will be merged under the same new entity "+newMetabolite.getId()+".");
            System.err.println("If it is an expected behaviour that the provided merge function may not produce a different compounds at each call, please review the following merge:");
            System.err.println(originalCtoMerge.stream().map(c -> c.getId()+" : "+ getUniqIdFunction.apply(c)).collect(Collectors.toList()));
            System.err.println(convert.entrySet().stream().filter(e -> old.equals(e.getValue())).map(e  -> e.getKey().getId()+" : "+ getUniqIdFunction.apply(e.getKey())).collect(Collectors.toList()));
            return old;
        }
        merged.add(newMetabolite);
        merged.affectToCompartment(uniqComp,newMetabolite);
        return newMetabolite;
    }

    private BioReaction buildReaction(BioReaction originalR){
        BioReaction newReaction = new BioReaction(originalR);
        newReaction.setSpontaneous(originalR.isSpontaneous());
        newReaction.setReversible(originalR.isReversible());
        newReaction.setEcNumber(originalR.getEcNumber());

        merged.add(newReaction);

        // Create substrates, swap to unique compound
        for (BioReactant reactant : originalR.getLeftReactantsView()) {
            BioMetabolite newMetabolite = convert.get(reactant.getMetabolite());
            Double sto = reactant.getQuantity();
            merged.affectLeft(newReaction, sto, uniqComp, newMetabolite);
        }

        //  Create products, swap to unique compound
        for (BioReactant reactant : originalR.getRightReactantsView()) {
            BioMetabolite newMetabolite = convert.get(reactant.getMetabolite());
            Double sto = reactant.getQuantity();
            merged.affectRight(newReaction, sto, uniqComp, newMetabolite);
        }

        //copy GPR
        for (BioEnzyme enzyme : originalR.getEnzymesView()) {
            BioEnzyme newEnzyme = merged.getEnzyme(enzyme.getId());
            merged.affectEnzyme(newReaction, newEnzyme);
        }

        return newReaction;
    }

    // remove reactions that create loops, i.e. transport reactions between compartments
    private void removeLoops(){
        BioCollection<BioReaction> toRemove = new BioCollection<>();
        for(BioReaction r : merged.getReactionsView()){
            if(r.getLeftsView().stream().anyMatch(r.getRightsView()::contains)) toRemove.add(r);
        }
        merged.removeOnCascade(toRemove);
    }

    // copy Gene, Protein and Enzyme from original network
    private void keepGPR(BioNetwork original){
        // Copy genes
        for (BioGene gene : original.getGenesView()) {
            BioGene newGene = new BioGene(gene);
            merged.add(newGene);
        }

        // Copy proteins
        for (BioProtein protein : original.getProteinsView()) {
            BioProtein newProtein = new BioProtein(protein);
            merged.add(newProtein);
            if (protein.getGene() != null) {
                String geneId = protein.getGene().getId();
                BioGene newGene = merged.getGene(geneId);
                merged.affectGeneProduct(newProtein, newGene);
            }
        }

        // Copy enzymes
        for (BioEnzyme enzyme : original.getEnzymesView()) {
            BioEnzyme newEnzyme = new BioEnzyme(enzyme);
            merged.add(newEnzyme);

            BioCollection<BioEnzymeParticipant> participants = enzyme.getParticipantsView();

            for (BioEnzymeParticipant participant : participants) {
                Double quantity = participant.getQuantity();
                if (participant.getPhysicalEntity().getClass().equals(BioMetabolite.class)) {
                    BioMetabolite metabolite = (BioMetabolite) participant.getPhysicalEntity();
                    // swap to unique compound
                    merged.affectSubUnit(newEnzyme, quantity, convert.get(metabolite));
                } else if (participant.getPhysicalEntity().getClass().equals(BioProtein.class)) {
                    BioProtein protein = (BioProtein) participant.getPhysicalEntity();
                    BioProtein newProtein = merged.getProtein(protein.getId());
                    merged.affectSubUnit(newEnzyme, quantity, newProtein);
                }
            }
        }
    }

}
