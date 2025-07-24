package fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class that creates a meta-network from multiple sub-networks. A meta-network is a single model which contains several
 * sub-networks that remain individualized within the meta-network (as opposed to models fusion), but which can share
 * some of their components with other sub-networks through shared compartments.
 */
public class MetaNetworkBuilder implements MultiNetworkBuilder{

    private Map<BioMetabolite,BioMetabolite> metaboliteConversion;
    private Map<BioCompartment,BioCompartment> compartmentConversion;
    private Map<BioReaction,BioReaction> reactionConversion;
    private Map<BioProtein,BioProtein> proteinConversion;
    private Map<BioEnzyme,BioEnzyme> enzymeConversion;
    private Map<BioGene,BioGene> geneConversion;

    protected BioCollection<BioNetwork> networks = new BioCollection<>();
    private Map<BioCompartment, Set<MetaBioMetabolite>> metaCompComposition = new HashMap<>();

    @Setter
    protected Function<BioMetabolite,String> getSharedIdFunction = BioEntity::getName;
    @Setter
    private String poolReactionPrefix = "poolReaction_";
    @Setter
    private String sharedTransportPrefix = "transport_";
    @Setter
    private String exchangeReactionPrefix = "exchange_";
    @Setter
    protected BiFunction<BioMetabolite, BioMetabolite, BioReaction> createLinkWithSharedPool = (m, pool) -> {
        BioReaction r=new BioReaction(poolReactionPrefix+ m.getId());
        r.setReversible(true);
        return r;
    };

    @Setter
    protected Function<BioMetabolite, BioReaction> createPoolExchangeReaction = (pool) -> {
        BioReaction r=new BioReaction(exchangeReactionPrefix+pool.getId());
        r.setReversible(true);
        return r;
    };

    @Setter
    protected BiFunction<BioMetabolite, BioCompartment, BioReaction> createTransportWithSharedComp = (m, comp) -> {
        BioReaction r=new BioReaction(sharedTransportPrefix+ m.getId()+"_to_"+comp.getId());
        r.setReversible(true);
        return r;
    };
    @Setter
    @Getter
    protected MetaEntityFactory entityFactory;
    protected HashMap<BioCompartment,BioCompartment> fuseMap= new HashMap<>();
    protected Boolean keepGPR = false;
    @Setter
    protected Boolean addExchangeReaction = true;

    /**
     * enable a compartment to be shared between multiple organisms
     * @param sc a compartment that will be shared between multiple bionetworks
     */
    public void addNewSharedCompartment(BioCompartment sc) {
        metaCompComposition.put(sc,new HashSet<>());
    }

    /**
     * Fuse a sub-network's compartment into a meta-network's shared compartment. The former will be replaced by the latter during build.
     * All compartment's components will be added to the shared compartment
     * @param n the sub-network
     * @param c the sub-network's compartment to be fused
     * @param sc the shared meta-compartment that will receive the compartment's components
     */
    public void fuseCompartmentIntoSharedCompartment(BioNetwork n, BioCompartment c, BioCompartment sc) {
        if(!metaCompComposition.containsKey(sc)) throw new IllegalArgumentException("Shared meta-compartment "+sc.getId()+" not found in network");
        fuseMap.put(c,sc);
    }

    /**
     * Bump a sub-network's compartment into a meta-network's shared compartment. For each compartment's component,
     * a copy is created within the shared compartment
     * @param n the sub-network
     * @param c the sub-network's compartment to be bumped
     * @param sc the shared meta-compartment that will receive the compartment's components
     */
    public void bumpCompartmentIntoSharedCompartment(BioNetwork n, BioCompartment c, BioCompartment sc) {
        BioCollection<BioMetabolite> toShare = n.getMetabolitesView().stream()
                .filter(x -> n.getCompartmentsOf(x).contains(c))
                .collect(BioCollection::new,BioCollection::add,BioCollection::addAll);
        this.exchangeWithSharedCompartment(n,c,toShare,sc);
    }

    /**
     * Exchange a compound from a sub-network's compartment to a meta-network's shared compartment. A copy of the compound will be created
     * @param sourceNetwork the original subnetwork of the compound
     * @param sourceCompartment  the original compartment of the compound in the subnetwork
     * @param metabolite the compound
     * @param sc the target shared meta-compartment that will receive the compound
     */
    public void exchangeWithSharedCompartment(BioNetwork sourceNetwork, BioCompartment sourceCompartment, BioMetabolite metabolite, BioCompartment sc) {
        if(!metaCompComposition.containsKey(sc)) throw new IllegalArgumentException("Shared meta-compartment "+sc.getId()+" not found in network");
        metaCompComposition.get(sc).add(new MetaBioMetabolite(metabolite,sourceCompartment,sourceNetwork));
    }

    /**
     * Exchange compounds from a sub-network's compartment to a meta-network's shared compartment. A copy of each exchanged compound will be created
     * @param sourceNetwork the original subnetwork of the compounds
     * @param sourceCompartment  the original compartment of the compounds in the subnetwork
     * @param metabolites the compounds
     * @param sc the target shared meta-compartment that will receive the compounds
     */
    public void exchangeWithSharedCompartment(BioNetwork sourceNetwork, BioCompartment sourceCompartment, BioCollection<BioMetabolite> metabolites, BioCompartment sc) {
        if(!metaCompComposition.containsKey(sc)) throw new IllegalArgumentException("Shared meta-compartment "+sc.getId()+" not found in network");
        Set<MetaBioMetabolite> scCompo = metaCompComposition.get(sc);
        for(BioMetabolite m : metabolites ){
            scCompo.add(new MetaBioMetabolite(m,sourceCompartment,sourceNetwork));
        }
    }

    @Override
    public void add(BioNetwork bn) {
        networks.add(bn);
    }

    /**
     * creates all maps for conversion (original bioentity, new meta-entity)
     * instantiate meta-network as new bionetwork
     * @return an empty meta-network
     */
    protected BioNetwork initMetaNetwork(){
        this.metaboliteConversion = new HashMap<>();
        this.compartmentConversion = new HashMap<>();
        this.reactionConversion = new HashMap<>();
        this.proteinConversion = new HashMap<>();
        this.enzymeConversion = new HashMap<>();
        this.geneConversion = new HashMap<>();
        return new BioNetwork();
    }

    /**
     * add all copied bioentity to the newly built meta-network
     * @param meta the (usually empty) meta-network
     */
    protected void populateMetaNetwork(BioNetwork meta){

        for(BioNetwork sub : networks){

            for(BioMetabolite e : sub.getMetabolitesView()){
                BioMetabolite e2 = entityFactory.createMetaEntity(e,sub);
                meta.add(e2);
                metaboliteConversion.put(e,e2);
            }
            for(BioCompartment c : sub.getCompartmentsView()){
                BioCompartment c2 = fuseMap.get(c);
                if(c2==null){
                    c2 = entityFactory.createMetaEntity(c,sub);
                    meta.add(c2);
                }
                compartmentConversion.put(c,c2);
                BioCollection<BioMetabolite> content = c.getComponentsView().stream()
                        .filter((e) -> e.getClass().equals(BioMetabolite.class))
                        .map(metaboliteConversion::get)
                        .collect(BioCollection::new,BioCollection::add,BioCollection::addAll);

                meta.affectToCompartment(c2, content);
            }

            // Copy genes
            if (keepGPR) {
                //TODO case shared Genome
                for (BioGene gene : sub.getGenesView()) {
                    BioGene newGene = entityFactory.createMetaEntity(gene,sub);
                    meta.add(newGene);
                    geneConversion.put(gene,newGene);
                }
                for (BioProtein protein : sub.getProteinsView()) {
                    BioProtein newProtein = entityFactory.createMetaEntity(protein,sub);
                    meta.add(newProtein);
                    proteinConversion.put(protein,newProtein);

                    if (protein.getGene() != null) {
                        BioGene newGene = geneConversion.get(protein.getGene());
                        meta.affectGeneProduct(newProtein, newGene);
                    }
                }
                for (BioEnzyme enzyme : sub.getEnzymesView()) {

                    BioEnzyme newEnzyme = entityFactory.createMetaEntity(enzyme,sub);
                    meta.add(newEnzyme);
                    enzymeConversion.put(enzyme,newEnzyme);

                    BioCollection<BioEnzymeParticipant> participants = enzyme.getParticipantsView();

                    for (BioEnzymeParticipant participant : participants) {
                        Double quantity = participant.getQuantity();

                        if (participant.getPhysicalEntity().getClass().equals(BioMetabolite.class)) {
                            BioMetabolite metabolite = (BioMetabolite) participant.getPhysicalEntity();
                            BioMetabolite newMetabolite = metaboliteConversion.get(metabolite);
                            meta.affectSubUnit(newEnzyme, quantity, newMetabolite);
                        } else if (participant.getPhysicalEntity().getClass().equals(BioProtein.class)) {
                            BioProtein protein = (BioProtein) participant.getPhysicalEntity();
                            BioProtein newProtein  = proteinConversion.get(protein);
                            meta.affectSubUnit(newEnzyme, quantity, newProtein);
                        }
                    }
                }
            }

            for (BioReaction r : sub.getReactionsView()) {

                BioReaction newReaction = entityFactory.createMetaEntity(r,sub);
                newReaction.setSpontaneous(r.isSpontaneous());
                newReaction.setReversible(r.isReversible());
                newReaction.setEcNumber(r.getEcNumber());

                meta.add(newReaction);
                reactionConversion.put(r,newReaction);

                // Copy lefts
                for (BioReactant reactant : r.getLeftReactantsView()) {
                    BioMetabolite newMetabolite = metaboliteConversion.get(reactant.getMetabolite());
                    BioCompartment newCpt = compartmentConversion.get(reactant.getLocation());
                    Double sto = reactant.getQuantity();
                    meta.affectLeft(newReaction, sto, newCpt, newMetabolite);
                }

                // Copy rights
                for (BioReactant reactant : r.getRightReactantsView()) {
                    BioMetabolite newMetabolite = metaboliteConversion.get(reactant.getMetabolite());
                    BioCompartment newCpt = compartmentConversion.get(reactant.getLocation());
                    Double sto = reactant.getQuantity();
                    meta.affectRight(newReaction, sto, newCpt, newMetabolite);
                }

                // Copy enzymes
                if (keepGPR) {
                    for (BioEnzyme enzyme : r.getEnzymesView()) {
                        BioEnzyme newEnzyme = enzymeConversion.get(enzyme);
                        meta.affectEnzyme(newReaction, newEnzyme);
                    }
                }
            }

            for (BioPathway pathway : sub.getPathwaysView()) {
                BioPathway newPathway = entityFactory.createMetaEntity(pathway,sub);
                meta.add(newPathway);
                // Add reactions into pathway
                BioCollection<BioReaction> reactions = sub.getReactionsFromPathways(pathway);

                for (BioReaction reaction : reactions) {
                    BioReaction newReaction = reactionConversion.get(reaction);
                    meta.affectToPathway(newPathway, newReaction);
                }
            }
        }
    }

    /**
     * Set the alias prefixes for the entities of the sub-networks. This allows to distinguish entities from different sub-networks
     * that have the same name. The prefixes are added to the entity names in the meta-network.
     * @param aliases a map of sub-networks and their respective prefixes
     *                (e.g. {subNetwork1: "prefix1", subNetwork2: "prefix2"})
     *                The prefixes are added to the entity names in the meta-network.
     *                If a sub-network is not in the map, or is in the map but has a null or empty prefix, its entities will not be prefixed.
     **/
    public void setAliasPrefixes(Map<BioNetwork,String> aliases, String poolPrefix){
        this.setEntityFactory(new PrefixedMetaEntityFactory(aliases, poolPrefix));
    }

    @Override
    public BioNetwork build() {
        BioNetwork meta = this.initMetaNetwork();
        //create shared compartment
        this.initSharedComp(meta);
        //add each subnetwork content
        this.populateMetaNetwork(meta);
        //add shared compartment components and exchanges
        this.populateSharedComp(meta);
        //link compounds in shared compartment
        this.linkCompoundsInSharedComp(meta);
        return meta;
    }
    protected void initSharedComp(BioNetwork meta) {
        for (Map.Entry<BioCompartment, Set<MetaBioMetabolite>> compDescriptor : metaCompComposition.entrySet()) {
            BioCompartment sc = compDescriptor.getKey();
            meta.add(sc);
        }
    }

    protected void populateSharedComp(BioNetwork meta){
        for(Map.Entry<BioCompartment, Set<MetaBioMetabolite>> compDescriptor : metaCompComposition.entrySet()){
            for(MetaBioMetabolite m : compDescriptor.getValue()){
                BioMetabolite m1 = metaboliteConversion.get(m.metabolite());
                BioMetabolite m2 = entityFactory.createSharedCompound(m1,compDescriptor.getKey());
                meta.add(m2);
                meta.affectToCompartment(compDescriptor.getKey(),m2);
                BioReaction t = createTransportWithSharedComp.apply(m1,compDescriptor.getKey());
                meta.add(t);
                meta.affectLeft(t,1.0,compartmentConversion.get(m.sourceCompartment()),metaboliteConversion.get(m.metabolite()));
                meta.affectLeft(t,1.0,compDescriptor.getKey(),m2);
            }
        }
    }

    /**
     * Links the source-specific metabolites representing the same compounds in the shared compartments, by creating a shared "pool" entity
     * alongside their interconversion reactions.
     * @param meta the meta-network
     */
    protected void linkCompoundsInSharedComp(BioNetwork meta) {
        //loop over each shared compartment
        for(BioCompartment sharedComp : metaCompComposition.keySet()){
            //retrieve all metabolite components that has been previously set, using whole compartment fusing or individual additions
            BioCollection<BioMetabolite> content = sharedComp.getComponentsView().stream()
                    .filter((e) -> e.getClass().equals(BioMetabolite.class))
                    .map(o -> (BioMetabolite)o)
                    .collect(BioCollection::new,BioCollection::add,BioCollection::addAll);

            //all entities that represent the same compounds from different source are grouped
            Map<String, List<BioMetabolite>> compoundGroups = content.stream().collect(Collectors.groupingBy(getSharedIdFunction));

            //for each group, create a "pool" metabolite that represent them
            for(Map.Entry<String, List<BioMetabolite>> group : compoundGroups.entrySet()){
                BioMetabolite pool = this.entityFactory.createPoolCompound(group.getValue(),sharedComp);
                meta.add(pool);
                meta.affectToCompartment(sharedComp,pool);
                //for each member of the group, create a reversible reaction linking them to their "pool" counterpart
                for(BioMetabolite e : group.getValue()){
                    BioReaction r = this.createLinkWithSharedPool.apply(e,pool);
                    meta.add(r);
                    meta.affectRight(r,1.0, sharedComp,e);
                    meta.affectLeft(r,1.0, sharedComp,pool);
                }
                //if option selected, add exchange reaction to the pool for flux modeling
                if(addExchangeReaction){
                    BioReaction r = this.createPoolExchangeReaction.apply(pool);
                    meta.add(r);
                    meta.affectLeft(r,1.0, sharedComp,pool);
                }
            }
        }
    }
}
