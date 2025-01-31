package fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;

import java.util.Collection;

/**
* Factory to create new instances of BioEntity and avoid id conflicts between subnetworks in a meta-graph.
**/
public interface MetaEntityFactory {

    /**
    * Create a copy of a subnetwork's BioEntity for its MetaGraph, while guaranteeing no id conflicts if the same
    * original id was used in another subnetworks. This may be done by referencing in the new id the source network
    * passed as parameter.
    * @param originalEntity the original entity
    * @param source the source network
    **/
    <E extends BioEntity> E createMetaEntity(E originalEntity, BioNetwork source);

    /**
     * Create a copy of a subnetwork's BioMetabolite in a shared compartment.
     * @param originalEntity the original entity
     * @param sharedComp the target shared compartment
     * @return
     */
    BioMetabolite createSharedCompound(BioMetabolite originalEntity, BioCompartment sharedComp);

    /**
     * Create a new BioMetabolite in a shared compartment, that represent a pool of compounds from different subnetworks.
     * @param entities the matching entities in different subnetworks
     * @param sharedComp the target shared compartment
     * @return
     */
    BioMetabolite createPoolCompound (Collection<BioMetabolite> entities, BioCompartment sharedComp);

}
