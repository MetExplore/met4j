package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.GraphMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.GraphFilter;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Weighting policy that mimic RPAIRS's now discontinued tags. Define Main and Side transitions within each reaction.
 * This 'main' tag also include the RPAIRS 'trans' tag.
 */
public class RPAIRSlikePolicy extends WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> {

    AtomMappingWeightPolicy preprocess;
    Double mainValue = 1.0;
    Double sideValue = 0.0;

    Double spuriousValue = -1.0;
    Boolean removeSide = false;
    Boolean removeSpurious = false;

    public RPAIRSlikePolicy(AtomMappingWeightPolicy aam){
        preprocess=aam.binarize();
        preprocess.removeNoCC=false;
        preprocess.removeNotFound=false;
    }

    /**
     * remove side transitions , estimated from edge redundancy
     * @return
     */
    public RPAIRSlikePolicy removeSideTransitions(){
        removeSide=true;
        removeSpurious=true;
        return this;
    }

    /**
     * remove spurious transitions, which don't appear in the carbon skeleton graph (not involving at least two C)
     * @return this instance
     */
    public RPAIRSlikePolicy removeSpuriousTransitions(){
        removeSpurious=true;
        return this;
    }

    @Override
    public void setWeight(CompoundGraph compoundGraph) {
        preprocess.setWeight(compoundGraph);
        Set<ReactionEdge> spuriousEdges = new HashSet<>();
        Set<ReactionEdge> validEdges = new HashSet<>();

        //spurious edges are not directly tagged but stored in first step, to avoid conflict if spuriousValue match with some AtomMappingWeightPolicy weights
        for(ReactionEdge e : compoundGraph.edgeSet()){
            if(Double.isNaN(compoundGraph.getEdgeWeight(e)) || compoundGraph.getEdgeWeight(e)==0.0){
                spuriousEdges.add(e);
            }else {
                validEdges.add(e);
            }
        }

        for(ReactionEdge e : validEdges){
            Set<BioMetabolite> mainCC = getMainComponent(compoundGraph,e.getReaction(), spuriousEdges);
            if(mainCC.contains(e.getV1()) && mainCC.contains(e.getV2())){
                compoundGraph.setEdgeWeight(e,mainValue);
            }else{
                compoundGraph.setEdgeWeight(e,sideValue);
            }
        }

        if(removeSide){
            GraphFilter.weightFilter(compoundGraph,sideValue,GraphFilter.EQUALITY);
        }
        if(removeSpurious){
           compoundGraph.removeAllEdges(spuriousEdges);
        }else{
            for(ReactionEdge e : spuriousEdges){
                compoundGraph.setEdgeWeight(e,spuriousValue);
            }
        }
        if(removeSpurious || removeSide) compoundGraph.removeIsolatedNodes();
    }

    /**
     * Compute the highest scored connected component of a reaction subgraph
     *
     * @param g the cpd graph skeleton network
     * @param reaction the reaction
     * @return a set of compounds in the best connected component
     */
    public Set<BioMetabolite> getMainComponent(CompoundGraph g, BioReaction reaction, Set<ReactionEdge> spuriousEdges){
        //Compute reaction subgraph
        CompoundGraph rG = g.getReactionSubGraph(reaction);

        //remove spurious edges
        Set<ReactionEdge> edgesToRemove = new HashSet<>(rG.edgeSet());
        edgesToRemove.retainAll(spuriousEdges);
        rG.removeAllEdges(edgesToRemove);
        if(rG.edgeSet().size()==0) return new HashSet<>();

        //Compute connected components
        List<Set<BioMetabolite>> cc = GraphMeasure.getConnectedComponents(rG);
        Double bestScore = 0.0;
        Set<BioMetabolite> mainCC = null;

        for(Set<BioMetabolite> component : cc) {
            CompoundGraph ccSubGraph = CompoundGraph.getFactory().createSubGraph(rG,component);
            Double score = getComponentScore(g,ccSubGraph);
            if(score>bestScore){
                bestScore = score;
                mainCC = component;
            }
        }
        return mainCC;
    }

    /**
     * Get the number of parallel edges of the provided component
     * @param <V> vertex class
     * @param <E> edge class
     *
     * @param wholeG the complete graph
     * @param subG the reaction connected component
     * @return the redundancy score, i.e. estimation of how many other reactions share the same edges as the component
     */
    private <V extends BioEntity, E extends Edge<V>> Double getComponentScore(CompoundGraph wholeG, CompoundGraph subG){
        Double Nedge = Double.valueOf(subG.edgeSet().size());
        Double NPedge = 0.0;
        for(ReactionEdge e : subG.edgeSet()){
            BioMetabolite v1 = e.getV1();
            BioMetabolite v2 = e.getV2();
            Integer redundancy = wholeG.getAllEdges(v1,v2).size();
            NPedge = NPedge + Double.valueOf(redundancy);
        }
        return Nedge/NPedge;
    }
}
