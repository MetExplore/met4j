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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compressed.CompressedGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compressed.PathEdge;
import org.jgrapht.alg.spanning.KruskalMinimumSpanningTree;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

/**
 * Class to compute approximation of Steiner Tree (minimum cost tree between nodes of interest), using minimum spanning tree
 *
 * @author clement
 */
public class SteinerTreeApprox<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V ,E>>{

	/** The graph. */
	public final G g;

	/** if the graph should be treated as weighted or not **/
	public boolean weighted = true;
	public boolean undirected = false;
	public boolean pruning = true;

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
	 * @param g the graph
	 * @param useWeights consider edge weights sum rather than path length (default = true)
	 * @param useEdgeDirection consider edge direction in path search (default = true)
	 * @param prune prune union of shortest paths to remove cycle (default = true)
	 */
	public SteinerTreeApprox(G g, boolean useWeights, boolean useEdgeDirection, boolean prune) {
		this.g=g;
		this.weighted = useWeights;
		this.undirected = !useEdgeDirection;
		this.pruning = prune;
	}

	/**
	 * Gets the lightest union of shortest paths connecting all nodes in set
	 * Which correspond to the minimum spanning tree of the metric closure graph
	 * @param terminal the targets list
	 * @return the steiner tree list
	 */
	public List<E> getLightestUnionOfShortestPaths(Set<V> terminal){
		Collection<V> unfound = new HashSet<>();
		for(V v:terminal){
			if(!g.containsVertex(v)) {
				System.err.println(v.getId()+" not found in graph");
				unfound.add(v);
			}
		}
		terminal.removeAll(unfound);

		ArrayList<E> list = new ArrayList<>();
		CompressedGraph<V, E, G> cg = (new ShortestPath<>(g,!undirected)).getMetricClosureGraph(terminal, terminal, weighted);
		KruskalMinimumSpanningTree<V, PathEdge<V,E>> kruskal = new KruskalMinimumSpanningTree<>(cg);
		Set<PathEdge<V,E>> mst = kruskal.getSpanningTree().getEdges();
		for(PathEdge<V,E> edge : mst){
			list.addAll(edge.getPath().getEdgeList());
		}
		return list;
	}

	/**
	 * Gets the lightest union of shortest paths connecting all nodes in set
	 * Which correspond to the minimum spanning tree of the metric closure graph
	 *
	 * @param startNodes the targets list
	 * @param endNodes the targets list
	 * @return the steiner tree list
	 */
	public List<E> getLightestUnionOfShortestPaths(Set<V> startNodes, Set<V> endNodes){
		Collection<V> unfound = new HashSet<>();
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

		ArrayList<E> list = new ArrayList<>();
		DirectedWeightedMultigraph<V, PathEdge<V,E>> cg = (new ShortestPath<>(g,!undirected)).getMetricClosureGraph(startNodes, endNodes, weighted);
		KruskalMinimumSpanningTree<V, PathEdge<V,E>> kruskal = new KruskalMinimumSpanningTree<>(cg);
		Set<PathEdge<V,E>> mst = kruskal.getSpanningTree().getEdges();
		for(PathEdge<V,E> edge : mst){
			list.addAll(edge.getPath().getEdgeList());
		}
		return list;
	}

	/**
	 * Gets the steiner tree.
	 *
	 * @param startNodes the targets list
	 * @param endNodes the targets list
	 * @return the steiner tree
	 */
	public G getSteinerTree(Set<V> startNodes, Set<V> endNodes, GraphFactory<V,E,G> graphFactory){
		List<E> edgeList = getLightestUnionOfShortestPaths(startNodes, endNodes);
		G g2 = graphFactory.createGraphFromEdgeList(edgeList);
		if(pruning) pruning(g2);
		return g2;
	}

	/**
	 * Gets the steiner tree.
	 *
	 * @param targetNodes the targets list
	 * @return the steiner tree
	 */
	public G getSteinerTree(Set<V> targetNodes, GraphFactory<V,E,G> graphFactory){
		return getSteinerTree(targetNodes,targetNodes,graphFactory);
	}

	private void pruning(G g2){
		KruskalMinimumSpanningTree<V, E> kruskal = new KruskalMinimumSpanningTree<>(g2);
		Set<E> mst = kruskal.getSpanningTree().getEdges();
		Set<E> edges = new HashSet<>(g2.edgeSet());
		edges.removeAll(mst);
		g2.removeAllEdges(edges);
	}

}
