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
package fr.inra.toulouse.metexplore.met4j_graph.computation.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.PrimMinimumSpanningTree;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.CompressedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.PathEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * Class to compute approximation of Steiner Tree (minimum cost tree between nodes of interest), using minimum spanning tree
 * @author clement
 */
public class SteinerTreeApprox<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V ,E>>{
	
	/** The graph. */
	public G g;
	
	/** if the graph should be treated as weighted or not **/
	public boolean weighted = true;

	/**
	 * Instantiates a new steiner tree computor.
	 * 
	 * @param g the graph
	 */
	public SteinerTreeApprox(G g) {
		this.g=g;
	}
	
	/**
	 * Instantiates a new steiner tree computor.
	 * 
	 * @param g the graph
	 * @param weighted if the graph should be treated as weighted
	 */
	public SteinerTreeApprox(G g, boolean weighted) {
		this.g=g;
		this.weighted=weighted;
	}
	
	/**
	 * Gets the steiner tree list.
	 *
	 * @param g the graph
	 * @param terminal the targets list
	 * @param weighted if the graph is weighted
	 * @return the steiner tree list
	 */
	public List<E> getSteinerTreeList(Set<V> terminal, boolean weighted){
		Collection<V> unfound = new HashSet<V>();
		for(V v:terminal){
			if(!g.containsVertex(v)) {
				System.err.println(v.getId()+" not found in graph");
				unfound.add(v);
			}
		}
		terminal.removeAll(unfound);
		
		ArrayList<E> list = new ArrayList<E>();
		CompressedGraph<V, E> cg = (new ShortestPath<V,E,G>(g)).getMetricClosureGraph(terminal, terminal, weighted);
		PrimMinimumSpanningTree<V, PathEdge<V,E>> prim = new PrimMinimumSpanningTree<V, PathEdge<V,E>>(cg);
		Set<PathEdge<V,E>> mst = prim.getMinimumSpanningTreeEdgeSet();
		for(PathEdge<V,E> edge : mst){
			list.addAll(edge.getPath().getEdgeList());
		}
		return list;
	}
	
	/**
	 * Gets the steiner tree list.
	 *
	 * @param g the graph
	 * @param startNodes the targets list
	 * @param endNodes the targets list
	 * @param weighted if the graph is weighted
	 * @return the steiner tree list
	 */
	public List<E> getSteinerTreeList(Set<V> startNodes, Set<V> endNodes, boolean weighted){
		Collection<V> unfound = new HashSet<V>();
		for(V v:startNodes){
			if(!g.containsVertex(v)) {
				System.err.println(v.getId()+" not found in graph");
				unfound.add(v);
			}
		}
		startNodes.removeAll(unfound);
		for(V v:endNodes){
			if(!g.containsVertex(v)) {
				System.err.println(v.getId()+" not found in graph");
				unfound.add(v);
			}
		}
		endNodes.removeAll(unfound);
		
		ArrayList<E> list = new ArrayList<E>();
		DirectedWeightedMultigraph<V, PathEdge<V,E>> cg = (new ShortestPath<V,E,G>(g)).getMetricClosureGraph(startNodes, endNodes, weighted);
		PrimMinimumSpanningTree<V, PathEdge<V,E>> prim = new PrimMinimumSpanningTree<V, PathEdge<V,E>>(cg);
		Set<PathEdge<V,E>> mst = prim.getMinimumSpanningTreeEdgeSet();
		for(PathEdge<V,E> edge : mst){
			list.addAll(edge.getPath().getEdgeList());
		}
		return list;
	}
	
//	/**
//	 * Gets the steiner tree.
//	 *
//	 * @param g the graph
//	 * @param nodeOfInterest the node of interest list
//	 * @param weighted if the graph is weighted
//	 * @return the steiner tree
//	 */
//	public G getSteinerTree(Set<V> nodeOfInterest, boolean weighted){
//		return(g.extractSubGraphFromEdgeList(getSteinerTreeList(nodeOfInterest, weighted)));
//	}
//	
//	/**
//	 * Gets the steiner tree.
//	 *
//	 * @param g the graph
//	 * @param startNodes the list of source nodes
//	 * @param endNodes  the list of target nodes
//	 * @param weighted if the graph is weighted
//	 * @return the steiner tree
//	 */
//	public G getSteinerTree(Set<V> startNodes,Set<V> endNodes, boolean weighted){
//		return(g.extractSubGraphFromEdgeList(getSteinerTreeList(startNodes, endNodes, weighted)));
//	}



}
