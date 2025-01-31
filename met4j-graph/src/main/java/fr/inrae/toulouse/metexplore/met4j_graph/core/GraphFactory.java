/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */
package fr.inrae.toulouse.metexplore.met4j_graph.core;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

import java.util.*;

/**
 * <p>Abstract GraphFactory class.</p>
 *
 * @author clement
 * An abstract factory class for {@link fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph}
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <G> the graph type
 * @version $Id: $Id
 */
public abstract class GraphFactory<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> {
	/**
	 * <p>createGraph.</p>
	 *
	 * @return a G object.
	 */
	public abstract G createGraph();
	
	/**
	 * Create a graph from a list of edges and a list of vertices.
	 * Vertex connected by the edges from the list should be present in the given vertex collection
	 *
	 * @param vertexList a collection of vertex
	 * @param edgeList a collection of edges
	 * @return a graph
	 */
	public G createGraphFromElements(Collection<V> vertexList, Collection<E> edgeList){
		G graph = createGraph();
		for (V vertex : vertexList){
			graph.addVertex(vertex);
		}
		for(E edge : edgeList){
			V v1 = edge.getV1();
			V v2 = edge.getV2();
			graph.addEdge(v1, v2, edge);
		}
		return graph;
	}
	
	/**
	 * Create a graph from a list of edges. Source and target vertices are automatically added to the graph.
	 *
	 * @param edgeList a collection of edges
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
	 *
	 * @param kShort the K-Shortest path results
	 * @param nodeOfInterest the set of nodes to connect
	 * @return a graph
	 */
	public G createGraphFromPathList(List<BioPath<V,E>> kShort, Set<V> nodeOfInterest){
		ArrayList<BioPath<V,E>> tmpKShort = new ArrayList<>(kShort);
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
	 *
	 * @param paths the list of path.
	 * @return a graph
	 */
	public G createGraphFromPathList(Collection<BioPath<V,E>> paths){
		
		G subGraph = createGraph();
		for(BioPath<V, E> path : paths){
			subGraph.addPath(path);
		}
		return subGraph;
	}
	
	/**
	 * Create a copy of a graph given as parameter.
	 * Both graph share the same vertex objects, but have their own set of edges.
	 *
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
	 *
	 * @return the edge-reversed graph
	 * @param g a G object.
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
	
	/**
	 * Extract sub-graph of a main graph given a list of vertex.
	 * The obtained sub-graph contains all the edges from the main graph linking vertices from the given list
	 * The obtained sub-graph is not a deep copy of the main graph, any changes to the shared edges attributes will be effective in both graphs
	 *
	 * @param g the main graph
	 * @param vertices the vertex
	 * @return the subnetwork
	 */
	public G createSubGraph(G g, Collection<V> vertices){
		Collection<E> edges = new HashSet<>();
		for(V vertex1 : vertices){
			for(V vertex2 : vertices){
				if(vertex1!=vertex2){
					Collection<E> v1v2Edges = g.getAllEdges(vertex1, vertex2);
					if(v1v2Edges!=null && !v1v2Edges.isEmpty()){
						edges.addAll(g.getAllEdges(vertex1, vertex2));
					}
				}
			}
		}
		return createGraphFromEdgeList(edges);
	}
}
