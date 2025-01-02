package fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import lombok.Setter;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

/**
 * Factory to create new instances of BioEntity using prefixed ids from user-defined alias, in order to avoid id conflicts between subnetworks
 * in a meta-graph.
 **/
public class PrefixedMetaEntityFactory implements MetaEntityFactory {

    Map<BioNetwork,String> sourcePrefixMap;
    @Setter
    protected String poolPrefix;
    @Setter
    protected String sep = "_";
    
    public PrefixedMetaEntityFactory(Map<BioNetwork,String> sourcePrefixMap, String poolPrefix){
        this.sourcePrefixMap=sourcePrefixMap;
        this.poolPrefix=poolPrefix;
    }

    /**
     * create prefixed id. This function will be called to create a new id for a BioEntity, using the
     * alias of the network it comes from (if it exists) and its original id.
     */
    public BiFunction<String, BioNetwork, String> addSourcePrefix = (id, bn) -> isVoid(sourcePrefixMap.get(bn)) ? id : sourcePrefixMap.get(bn)+sep+id;

    /**
     * create sharedCompartment-suffix id
     */
    public BiFunction<String, BioCompartment, String> addCompSuffix = (id, comp) -> id+sep+comp.getId();
    /**
     * create sharedCompartment-prefix id
     */
    public BiFunction<String, BioCompartment, String> addCompPrefix = (id, comp) -> comp.getId()+sep+id;


    /**
     * remove prefix-id from network alias
     */
    public Function<String, String> removeSourceSuffix = id -> {
            for(String s : sourcePrefixMap.values()) {
                id = id.replaceAll("^" + s + sep,"");
            };
            return id;
        };

    /**
     * create a pool-prefix id. This is used to identify a pool of compounds originating from different subnetworks.
     */
    public Function<String, String> addPoolFlag = (id) -> poolPrefix+sep+id;


    @SneakyThrows
    @Override
    public <E extends BioEntity> E createMetaEntity(E originalEntity, BioNetwork source) {
        return newEntityInstance(originalEntity, this.addSourcePrefix.apply(originalEntity.getId(),source));
    }

    @SneakyThrows
    @Override
    public BioMetabolite createSharedCompound(BioMetabolite entity, BioCompartment sharedCompartment) {
        return newEntityInstance(entity, this.addCompSuffix.apply(entity.getId(),sharedCompartment));
    }

    @SneakyThrows
    @Override
    public BioMetabolite createPoolCompound(Collection<BioMetabolite> entities, BioCompartment sharedCompartment) {
        BioMetabolite entity = entities.iterator().next();
        return newEntityInstance(entity, removeSourceSuffix
                .andThen(s -> addCompPrefix.apply(s,sharedCompartment))
                .andThen(addPoolFlag)
                .apply(entity.getId()));
    }

    private static <E extends BioEntity> E newEntityInstance(E entity, String newId) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        E newEntity = (E) entity.getClass().getDeclaredConstructor(String.class).newInstance(newId);
        newEntity.setName(entity.getName());
        newEntity.setSynonyms(new ArrayList<>(entity.getSynonyms()));
        newEntity.setComment(entity.getComment());
        newEntity.setRefs(new HashMap<>(entity.getRefs()));
        newEntity.setAttributes(new HashMap<>(entity.getAttributes()));
        return newEntity;
    }
}
