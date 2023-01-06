package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;

import java.util.function.Function;

/**
 * An all-purpose class to provides edge weights from a given function
 * @param <V>
 * @param <E>
 * @param <G>
 */
public class CustomWeightPolicy<V extends BioEntity, E extends Edge<V>,G extends BioGraph<V,E>> extends WeightingPolicy<V,E,G> {

    Function<E,Double> lambda;

    /**
     * Create a CustomWeightPolicy
     * @param function that takes an edge an return its weight
     */
    public CustomWeightPolicy(Function<E,Double> function){
        this.lambda=function;
    }

    @Override
    public void setWeight(G bioGraph) {
        for(E edge : bioGraph.edgeSet()){
            bioGraph.setEdgeWeight(edge,lambda.apply(edge));
        }
    }
}
