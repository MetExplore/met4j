package fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommunityNetworkBuilder extends MetaNetworkBuilder implements MultiNetworkBuilder{

    private BioCompartment medium;

    public CommunityNetworkBuilder(){
        setMedium();
    }

    public CommunityNetworkBuilder(BioCompartment medium){
        this.medium=medium;
        this.addNewSharedCompartment(medium);
    }

    public CommunityNetworkBuilder(Set<BioNetwork> networks){
        setMedium();
        Map<BioNetwork,String> prefixes = networks.stream()
                .collect(Collectors.toMap(n->n,n->n.getId()+"_"));
        this.setEntityFactory(new PrefixedMetaEntityFactory(prefixes,"pool"));
        for(BioNetwork bn : networks){
            this.add(bn);
        }
    }

    private void setMedium(){
        this.medium = new BioCompartment(UUID.randomUUID().toString(),"medium");
        this.addNewSharedCompartment(medium);
    }

    public void exchangeWithMedium(BioNetwork sourceNetwork, BioCompartment sourceCompartment, BioMetabolite metabolite){
        this.exchangeWithSharedCompartment(sourceNetwork, sourceCompartment, metabolite, medium);
    };

    public void add(BioNetwork bn, BioCompartment externalComp) {
        super.add(bn);
        this.fuseCompartmentIntoSharedCompartment(bn,externalComp,medium);
    }

}
