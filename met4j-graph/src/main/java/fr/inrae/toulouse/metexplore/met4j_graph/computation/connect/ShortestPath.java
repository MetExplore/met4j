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

import java.util.*;
import java.util.stream.Collectors;

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compressed.CompressedGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compressed.PathEdge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import org.jgrapht.alg.util.Pair;

/**
 * Class to use the shortest paths in a graph
 *
 * @author clement
 * @version $Id: $Id
 */
public class ShortestPath<V extends BioEntity,E extends Edge<V>, G extends BioGraph<V ,E>>{

	/** The graph. */
	public final G g;

	/**
	 * Instantiates a new shortest paths computor.
	 *
	 * @param g the graph
	 */
	public ShortestPath(G g) {
		this.g=g;
	}

	/**
	 * compute the shortest paths (or lightest paths if the graph is weighted) between 2 nodes
	 *
	 * @param start the start vertex
	 * @param end the end vertex
	 * @return the list of edges involved in the shortest path
	 * @throws java.lang.IllegalArgumentException if any.
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public BioPath<V, E> getShortest(V start, V end) throws IllegalArgumentException{
		if(!g.containsVertex(start)){
			throw(new IllegalArgumentException("Error: start node "+start.getId()+" not found in graph"));
		}
		if(!g.containsVertex(end)){
			throw(new IllegalArgumentException("Error: end node "+end.getId()+" not found in graph"));
		}

		HashMap<V,E> incoming = new HashMap<>();
		HashMap<V,Double> distMap = new HashMap<>();
		Set<V> unseen = new HashSet<>();
		Set<V> seen = new HashSet<>();

		//init dist from start
		for(V v : g.vertexSet()){
			distMap.put(v, Double.POSITIVE_INFINITY);
		}

		unseen.add(start);
		distMap.put(start,0.0);


		while (!unseen.isEmpty()) {

			//get the closest node from the start vertex
			V n = getNearest(distMap,unseen);
			unseen.remove(n);
			seen.add(n);


			//add current nodes's successor to the list of node to process, if not already seen
			//	skip outgoing edges from reaction already used in path
			for(E e : g.outgoingEdgesOf(n)){
				V successor = e.getV2();
				if(successor!=end && !seen.contains(successor)) unseen.add(successor);
				//store incoming edge minimizing the distance from start node
				//	update distance from start node
				double weight = g.getEdgeWeight(e);
				if(weight<0 || Double.isNaN(weight)) throw(new IllegalArgumentException("Error: edge weights must be real positive values ("+e.getV1()+" -> "+e.getV2()+" : "+weight+")"));
				double dist = distMap.get(n)+weight;
				if(distMap.get(successor) > dist){
					distMap.put(successor, dist);
					incoming.put(successor, e);
				}
			}
		}
		if(!incoming.containsKey(end)) return null;

		//backtracking
		double weight = 0.0;
		List<E> sp = new ArrayList<>();
		V currentVertex = end;
		while(currentVertex!=start){
			E e = incoming.get(currentVertex);
			weight+= g.getEdgeWeight(e);
			sp.add(e);
			incoming.remove(currentVertex);
			currentVertex=e.getV1();
			if(incoming.isEmpty() && currentVertex!=start ) return null;
		}
		Collections.reverse(sp);
		return new BioPath<>(g, start, end, sp, weight);
	}


	/**
	 * compute the shortest paths (or lightest paths if the graph is weighted) between 2 nodes as if the graph was undirected
	 *
	 * @param start the start vertex
	 * @param end the end vertex
	 * @return the list of edges involved in the shortest path
	 * @throws java.lang.IllegalArgumentException if any.
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public BioPath<V, E> getShortestAsUndirected(V start, V end) throws IllegalArgumentException{
		if(!g.containsVertex(start)){
			throw(new IllegalArgumentException("Error: start node "+start.getId()+" not found in graph"));
		}
		if(!g.containsVertex(end)){
			throw(new IllegalArgumentException("Error: end node "+end.getId()+" not found in graph"));
		}

		HashMap<V,E> incoming = new HashMap<>();
		HashMap<V,Double> distMap = new HashMap<>();
		Set<V> unseen = new HashSet<>();
		Set<V> seen = new HashSet<>();

		//init dist from start
		for(V v : g.vertexSet()){
			distMap.put(v, Double.POSITIVE_INFINITY);
		}

		unseen.add(start);
		distMap.put(start,0.0);

		while (!unseen.isEmpty()) {

			//get the closest node from the start vertex
			V n = getNearest(distMap,unseen);
			unseen.remove(n);
			seen.add(n);


			//add current nodes's successor to the list of node to process, if not already seen
			//	skip outgoing edges from reaction already used in path
			for(E e : g.outgoingEdgesOf(n)){
				V successor = e.getV2();
				if(successor!=end && !seen.contains(successor)) unseen.add(successor);
				//store incoming edge minimizing the distance from start node
				//	update distance from start node
				double weight = g.getEdgeWeight(e);
				if(weight<0 || Double.isNaN(weight)) throw(new IllegalArgumentException("Error: edge weights must be real positive values ("+e.getV1()+" -> "+e.getV2()+" : "+weight+")"));
				double dist = distMap.get(n)+weight;
				if(distMap.get(successor) > dist){
					distMap.put(successor, dist);
					incoming.put(successor, e);
				}
			}
			for(E e : g.incomingEdgesOf(n)){
				V successor = e.getV1();
				if(successor!=end && !seen.contains(successor)) unseen.add(successor);
				//store incoming edge minimizing the distance from start node
				//	update distance from start node
				double weight = g.getEdgeWeight(e);
				if(weight<0 || Double.isNaN(weight)) throw(new IllegalArgumentException("Error: edge weights must be real positive values ("+e.getV1()+" -> "+e.getV2()+" : "+weight+")"));
				double dist = distMap.get(n)+weight;
				if(distMap.get(successor) > dist){
					distMap.put(successor, dist);
					incoming.put(successor, e);
				}
			}
		}
		if(!incoming.containsKey(end)) return null;

		//backtracking
		double weight = 0.0;
		List<E> sp = new ArrayList<>();
		V currentVertex = end;
		while(currentVertex!=start){
			E e = incoming.get(currentVertex);
			weight+= g.getEdgeWeight(e);
			sp.add(e);
			incoming.remove(currentVertex);
			if(currentVertex==e.getV1()){
				currentVertex=e.getV2();
			}else{
				currentVertex=e.getV1();
			}
			if(incoming.isEmpty() && currentVertex!=start ) return null;
		}
		Collections.reverse(sp);
		return new BioPath<>(g, start, end, sp, weight);
	}

	/**
	 * get the nearest vertex to a given seed node from a list of node to check
	 *
	 * @param distMap the map containing the distance between seed node and each node in graph
	 * @param unseen the list of node to check
	 * @return the nearest vertex
	 */
	protected V getNearest(HashMap<V,Double> distMap, Collection<V> unseen){
		double mindist = Double.POSITIVE_INFINITY;
		V nearest = null;
		for(V v : unseen){
			double dist = distMap.get(v);
			if(dist<mindist){
				mindist=dist;
				nearest=v;
			}
		}
		return nearest;
	}


	/**
	 * compute the list of edges from the union of all shortest paths between all nodes in a given set
	 *
	 * @param nodeOfInterest the node of interest list
	 * @return the list of edges involved in the shortest path union
	 */
	public List<BioPath<V,E>> getShortestPathsUnionList(Set<V> nodeOfInterest){
		return getShortestPathsUnionList(nodeOfInterest,nodeOfInterest);
	}


	/**
	 * compute the list of edges from the union of all shortest paths between sources and target nodes
	 *
	 * @param startNodes the start nodes
	 * @param targetNodes the target nodes
	 * @return the list of edges involved in the shortest path union
	 */
	public List<BioPath<V,E>> getShortestPathsUnionList(Set<V> startNodes, Set<V> targetNodes){
		Collection<Pair<V,V>> pairs = prepareSPcomputation(startNodes,targetNodes);
		if(pairs.isEmpty()) return new ArrayList<>();
		return pairs.parallelStream()
				.map(p -> this.getShortest(p.getFirst(),p.getSecond()))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	/**
	 * Compute a graph where each edge correspond to an existing path between source and target
	 *
	 * @param sources the sources
	 * @param targets the targets
	 * @param weighted if the graph is weighted
	 * @return the metric closure graph
	 */
	public CompressedGraph<V, E, G> getMetricClosureGraph(Set<V> sources, Set<V> targets, boolean weighted){
		CompressedGraph<V, E, G> cg = new CompressedGraph<>(g);
		for(V v : sources){
			cg.addVertex(v);
		}
		for(V v : targets){
			if(!cg.containsVertex(v)){
				cg.addVertex(v);
			}
		}
		for(V v1 : sources){
			for(V v2: targets){
				if(v1!=v2){
					BioPath<V,E> sp = this.getShortest(v1, v2);
					if(sp!=null){
						PathEdge<V, E> e = new PathEdge<>(v1, v2, sp);
						cg.addEdge(v1, v2, e);
						if(weighted){
//							double weightSum = 0;
//							for(E edge : sp){
//								weightSum+=g.getEdgeWeight(edge);
//							}
//							cg.setEdgeWeight(e, weightSum);
							cg.setEdgeWeight(e,sp.getWeight());
						}else{
							cg.setEdgeWeight(e,sp.getLength());
						}

					}
				}
			}
		}

		return cg;
	}

	/**
	 * compute for each node in the first list, the minimum path length to be reached by nodes in the second set
	 *
	 * @param sources the sources
	 * @param targets the targets
	 * @param weighted if the graph is weighted
	 * @return the minimum shortest path distance
	 */
	public HashMap<V, Double> getMinSpDistance(Set<V> sources, Set<V> targets, boolean weighted){
		CompressedGraph<V, E, G> closureGraph = getMetricClosureGraph(targets,sources,weighted);
		HashMap<V, Double> minSpDist = new HashMap<>();
		for(V node : sources){
			if(closureGraph.containsVertex(node)){
				Set<PathEdge<V, E>> closureEdges = closureGraph.incomingEdgesOf(node);
				if(closureEdges.isEmpty()){
					minSpDist.put(node, Double.POSITIVE_INFINITY);
				}else{
					double min = Double.MAX_VALUE;
					for(PathEdge<V, E> e : closureEdges){
						double w = closureGraph.getEdgeWeight(e);
						if(w<min) min=w;
					}
					minSpDist.put(node, min);
				}
			}
		}
		return minSpDist;
	}

	/**
	 * compute for each node in the first list, the average minimum path length to be reached by nodes in the second set
	 *
	 * @param sources the sources
	 * @param targets the targets
	 * @param weighted if the graph is weighted
	 * @return the average shortest path distance
	 */
	public HashMap<V, Double> getAverageSpDistance(Set<V> sources, Set<V> targets, boolean weighted){
		CompressedGraph<V, E, G> closureGraph = getMetricClosureGraph(targets,sources,weighted);
		HashMap<V, Double> avgSpDist = new HashMap<>();
		for(V node : sources){
			if(closureGraph.containsVertex(node)){
				Set<PathEdge<V, E>> closureEdges = closureGraph.incomingEdgesOf(node);
				if(closureEdges.isEmpty()){
					avgSpDist.put(node, Double.POSITIVE_INFINITY);
				}else{
					double sum = 0;
					for(PathEdge<V, E> e : closureEdges){
						sum+=closureGraph.getEdgeWeight(e);
					}
					avgSpDist.put(node, sum/(closureGraph.incomingEdgesOf(node).size()));
				}
			}
		}
		return avgSpDist;
	}

	/**
	 * return all the shortest path in the given graph.
	 * @return all the shortest path in the given graph.
	 */
	public List<BioPath<V,E>> getAllShortestPaths(){
		return getShortestPathsUnionList(g.vertexSet());
	}

	private Collection<Pair<V,V>> prepareSPcomputation(Set<V> sources, Set<V> targets){
		ArrayList<Pair<V,V>> pairs = new ArrayList<>();
		for(V v1 : sources){
			for(V v2 : targets){
				if(v1!=v2){
					pairs.add(new Pair<>(v1,v2));
				}
			}
		}
		return pairs;
	}

}
