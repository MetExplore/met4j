/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.core.compressed;

import java.util.ArrayList;

import org.jgrapht.EdgeFactory;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class PathEdgeFactory<V extends BioEntity,E extends Edge<V>> implements EdgeFactory<V, PathEdge<V,E>> {
	BioGraph<V,E> g;
	
	public 	PathEdgeFactory(BioGraph<V,E> g){
		this.g=g;
	}
	
	@Override
	public PathEdge<V, E> createEdge(V arg0, V arg1) {
		BioPath<V, E> path = new BioPath<V, E>(g, arg0, arg1, new ArrayList<E>(), 0.0);
		PathEdge<V, E> edge = new PathEdge<V, E>(arg0, arg1, path);
		return edge;
	}
}
