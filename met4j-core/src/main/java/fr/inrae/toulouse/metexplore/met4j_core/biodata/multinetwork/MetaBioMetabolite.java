package fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;

public record MetaBioMetabolite(BioMetabolite metabolite, BioCompartment sourceCompartment, BioNetwork sourceNetwork) {
    public MetaBioMetabolite(BioMetabolite metabolite, BioCompartment sourceCompartment, BioNetwork sourceNetwork){
        if(!sourceNetwork.containsMetabolite(metabolite.getId())) throw new IllegalArgumentException("Source network does not contains metabolites");
        if(!sourceNetwork.containsCompartment(sourceCompartment.getId())) throw new IllegalArgumentException("Source network does not contains source compartment");
        if(sourceCompartment.getComponentsView().get(metabolite.getId())==null) throw new IllegalArgumentException("Source compartment does not contains metabolites");
        this.metabolite = metabolite;
        this.sourceCompartment = sourceCompartment;
        this.sourceNetwork = sourceNetwork;
    }
}
