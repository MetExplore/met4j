package fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;

public interface MultiNetworkBuilder {

    /**
     * add network into meta-network
     * @param bn
     */
    public void add(BioNetwork bn);

    /**
     * create a new network which encompass all added networks
     * @return
     */
    public BioNetwork build();


}
