/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_graph.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * 
 * @author clement
 * An abstract factory class for {@link BioGraph}
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <G> the graph type
 */
public abstract class GraphFactory<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> {
	public abstract G createGraph();
	
	/**
	 * Create a graph from a list of edges.
	 * @param edgeList a list of edges
	 * @return a graph
	 */
	public G createGraphFromEdgeList(Collection<E> edgeList){
		G graph = createGraph();
		for(E edge : edgeList){
			V v1 = edge.getV1();
			V v2 = edge.getV2();
			if(!graph.containsVertex(v1)) graph.addVertex(v1);
			if(!graph.containsVertex(v2)) graph.addVertex(v2);
			graph.addEdge(v1, v2, edge);
		}
		return graph;
	}
	
	/**
	 * Sub-network extraction from a list of paths :
	 * Add each path iteratively, in ascendant order of weight, until all nodes of interest are connected
	 * @param kShort the K-Shortest path results
	 * @param nodeOfInterest the list of nodes to connect
	 * @return
	 */
	public G createGraphFromPathList(List<BioPath<V,E>> kShort, Set<V> nodeOfInterest){
		ArrayList<BioPath<V,E>> tmpKShort = new ArrayList<BioPath<V,E>>(kShort);
		Collections.sort(tmpKShort);
		
		G subGraph = createGraph();
		
		while(!tmpKShort.isEmpty() && !subGraph.vertexSet().containsAll(nodeOfInterest)){
			BioPath<V,E> path = tmpKShort.get(0);
			tmpKShort.remove(0);
			subGraph.addPath(path);
		}
		return subGraph;
	}
	
	/**
	 * Sub-network extraction from a list of paths
	 * @param kShort the K-Shortest path results
	 * @param nodeOfInterest the list of nodes to connect
	 * @return
	 */
	public G createGraphFromPathList(Collection<BioPath<V,E>> paths){
		
		G subGraph = createGraph();
		for(BioPath<V, E> path : paths){
			subGraph.addPath(path);
		}
		return subGraph;
	}
	
	/**
	 * Create a copy of a graph given as parameter
	 * @param g1 the graph to copy
	 * @return a copy of the graph
	 */
	public G createCopy(G g1){
		G g2 = createGraph();
		for(V vertex : g1.vertexSet()){
			g2.addVertex(vertex);
		}
		for(E edge : g1.edgeSet()){
			E newEdge = g1.copyEdge(edge);
			g2.addEdge(edge.getV1(), edge.getV2(), newEdge);
			g2.setEdgeWeight(newEdge, g1.getEdgeWeight(edge));
		}
		return g2;
	}
	
	/**
	 * create a graph g' from this graph g where for each edge e(x,y) in g their exist an edge e'(y,x) in g'
	 * @return the edge-reversed graph
	 */
	public G reverse(G g){
		G reversed = createGraph();
		for(V vertex : g.vertexSet()){
			reversed.addVertex(vertex);
		}
		for(E edge : g.edgeSet()){
			E newEdge = g.reverseEdge(edge);
			reversed.addEdge(newEdge.getV1(), newEdge.getV2(), newEdge);
			reversed.setEdgeWeight(newEdge, g.getEdgeWeight(edge));
		}		
		return reversed;
	}
}