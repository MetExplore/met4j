package fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class PrecursorNetwork {

    /**
     * The graph
     */
    private final BipartiteGraph g;

    /**
     * The available compounds.
     */
    private BioCollection<BioMetabolite> availableCpds;

    /**
     * The compounds to reach (optional)
     */
    private BioCollection<BioMetabolite> cpdToProduce;

    /**
     *  add by-products, compounds produced by reactions of the precursor set whitout being a precursor of the compounds to produce.
     */
    private boolean addByProducts = false; //TODO

    /**
     * Instantiates a new precursor class
     *
     * @param bipartiteGraph  the bipartite graph
     * @param availableCpds   the available compounds (ubiquitous compounds like Water and ATP)
     * @param cpdToProduce      The compounds to reach (optional)
     * @param reactionToAvoid the reactions to avoid
     */
    public PrecursorNetwork(BipartiteGraph bipartiteGraph, BioCollection<BioMetabolite> availableCpds, BioCollection<BioMetabolite> cpdToProduce, BioCollection<BioReaction> reactionToAvoid) {
        this.g = bipartiteGraph;
        this.availableCpds = availableCpds;
        this.cpdToProduce = cpdToProduce;
        g.removeAllVertices(reactionToAvoid);
    }

    /**
     * Instantiates a new precursor class.
     *
     * @param bipartiteGraph  the bipartite graph
     * @param availableCpds   the available compounds (ubiquitous compounds like Water and ATP)
     */
    public PrecursorNetwork(BipartiteGraph bipartiteGraph, BioCollection<BioMetabolite> availableCpds, BioCollection<BioMetabolite> cpdToProduce) {
        this.g = bipartiteGraph;
        this.availableCpds = availableCpds;
        this.cpdToProduce = cpdToProduce;
    }


    /**
     * Gets the precursor network: all the upstream reactions that leads to the production of a set of compounds
     *
     * @return the precursor network
     */
    public BipartiteGraph getPrecursorNetwork() throws IllegalArgumentException {

        //check network consistency
        if (!this.g.isConsistent())
            throw new IllegalArgumentException("The network structure must be consistent with reaction reactant lists");

        BipartiteGraph precusorNetwork = new BipartiteGraph();

        HashSet<BioMetabolite> visited = new HashSet<>(availableCpds);
        Queue<BioMetabolite> metabolitesToProduce = new LinkedList<>(cpdToProduce);

        //Go until their is no required precursor to produce.
        while (!metabolitesToProduce.isEmpty()) {

            BioMetabolite m = metabolitesToProduce.remove();

            //add compound to network
            if(!precusorNetwork.containsVertex(m)) precusorNetwork.addVertex(m);

            for(BioEntity p : g.predecessorListOf(m)) {
                BioReaction r = (BioReaction) p;

                if (!precusorNetwork.containsVertex(r)) {

                    //add producing reaction to network
                    precusorNetwork.addVertex(r);

                    //get predecessors
                    BioCollection<BioMetabolite> predecessors = new BioCollection<>();
                    if (!r.isReversible()) {
                        predecessors = r.getLeftsView();
                    }else if(r.getLeftsView().contains(m)){
                        predecessors = r.getRightsView();
                    }else{
                        predecessors = r.getLeftsView();
                    }

                    for (BioMetabolite s : predecessors) {
                        //add substrate of producing reaction to queue, if not already visited.
                        if (!visited.contains(s) && !metabolitesToProduce.contains(s)) {
                            metabolitesToProduce.add(s);
                        }

                        if(!precusorNetwork.containsVertex(s)) precusorNetwork.addVertex(s);
                        precusorNetwork.addEdge(s, r, g.getEdge(s, r));

                    }
                }

                precusorNetwork.addEdge(r, m, g.getEdge(r, m));

            }
            visited.add(m);
        }


        return precusorNetwork;
    }

    /**
     * get precursor set from a precursor network.
     * compounds not produced or only produced by exchange reactions are considered precursors.
     * @param precusorNetwork
     * @return
     */
    public static BioCollection<BioMetabolite> getPrecursors(BipartiteGraph precusorNetwork){
        BioCollection<BioMetabolite> precursor = new BioCollection<>();
        BioCollection<BioReaction> exchange = PrecursorNetwork.getExchangeReactions(precusorNetwork);
        for(BioMetabolite v : precusorNetwork.compoundVertexSet()){
            Set<BioEntity> pList = precusorNetwork.predecessorListOf(v);
            if(pList.isEmpty() || exchange.containsAll(pList)){
                precursor.add(v);
            }else if(pList.size()==1){
                BioReaction r = (BioReaction) pList.toArray()[0];
                if(r.isReversible()) precursor.add(v);
            }
        }
        return precursor;
    }

    /**
     * get incoming exchange reactions in precursor network. (reaction without substrate)
     * @param precusorNetwork
     * @return
     */
    public static BioCollection<BioReaction> getExchangeReactions(BipartiteGraph precusorNetwork){
        BioCollection<BioReaction> precursor = new BioCollection<>();
        for(BioReaction v : precusorNetwork.reactionVertexSet()){
            if(precusorNetwork.inDegreeOf(v)==0) precursor.add(v);
        }
        return precursor;
    }

}
