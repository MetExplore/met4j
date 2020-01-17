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
package fr.inra.toulouse.metexplore.met4j_graph.computation.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.GraphPath;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class KShortestPath<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V ,E>>{
	
	/** The graph. */
	public G g;
	
	/**
	 * Instantiates a new K-shortest paths computor.
	 *
	 * @param g the graph
	 */
	public KShortestPath(G g) {
		this.g=g;
	}
	
	/**
	 * compute the list of edges from the union of all K-shortest paths between all nodes in a given set
	 *
	 * @param graph the graph
	 * @param nodeOfInterest the node of interest list
	 * @param k the maximum ranked path to consider
	 * @return the list of edges involved in the k-shortest path union
	 */
	public List<BioPath<V,E>> getKShortestPathsUnionList(Set<V> nodeOfInterest, int k){
		return getKShortestPathsUnionList(nodeOfInterest,nodeOfInterest,k);
	}
	
	/**
	 * compute the list of edges from the union of all K-shortest paths between sources and target nodes
	 *
	 * @param graph the graph
	 * @param startNodes the start vertex nodes
	 * @param targetNodes the target nodes
	 * @param k the maximum ranked path to consider
	 * @return the list of edges involved in the k-shortest path union
	 */
	public List<BioPath<V,E>> getKShortestPathsUnionList(Set<V> startNodes, Set<V> targetNodes, int k){
		ArrayList<BioPath<V,E>> shortest = new ArrayList<>();
		for(V start : startNodes){
			for(V end : targetNodes){
				if(start!=end){
					shortest.addAll(getKShortest(start, end, k));
				}
			}
		}
		return shortest;
	}
	
	/**
	 * compute the K-shortest paths (or lightest paths if the graph is weighted) between 2 nodes
	 *
	 * @param graph the graph
	 * @param start the start vertex
	 * @param end the end vertex
	 * @param k the maximum ranked path to consider
	 * @return the list of edges involved in the K-shortest path
	 */
	public List<BioPath<V,E>> getKShortest(V start, V end, int k){
		List<BioPath<V,E>> kPaths = new ArrayList<>();
		ShortestPath<V, E, G> sp = new ShortestPath<>(g);
		BioPath<V,E> shortest = sp.getShortest(start, end);
		if(shortest==null) return new ArrayList<>();
		kPaths.add(shortest);
//		List<ReactionEdge> sp = ShortestPath.getShortest(graph, start, end);
//		if(sp==null || sp.isEmpty()) return new ArrayList<ReactionEdge>();
//		double weight = 0.0;
//		for(ReactionEdge e : sp){
//			weight+=graph.getEdgeWeight(e);
//		}
//		a.add(new BioPath(graph, start, end, sp, weight));
		
//		DijkstraShortestPath<V, ReactionEdge> dj = new DijkstraShortestPath<V, ReactionEdge>(graph, start, end);
//		if(dj.getPath()==null) return new ArrayList<ReactionEdge>();
//		a.add(new BioPath(dj.getPath()));
		
		List<BioPath<V,E>> b = new ArrayList<>();

		for(int k2=1; k2<k; k2++){
			//get shortest path from previous iteration
			BioPath<V,E> previousPath = kPaths.get(k2-1);
			List<V> previousPathVertex = previousPath.getVertexList();
			
			//for each vertex in shortest path (except target)
			for(int i=0; i<previousPathVertex.size()-1; i++){
				ArrayList<E> removedEdges = new ArrayList<>();
				
				V spur = previousPathVertex.get(i);
				
				//store path from source to spur node
				List<V> rootPathVertex = previousPathVertex.subList(0, i+1);
				
				for(GraphPath<V, E> p : kPaths){
					List<V> pVertexList = p.getVertexList();
					
					//remove spur node's outgoing edges if already present in a shortest path from previous iteration sharing same path to spur
					if(pVertexList.size()>i+1 && (pVertexList.subList(0, i+1)).equals(rootPathVertex)){
						for(E e : p.getEdgeList()){
							if(e.getV1().equals(pVertexList.get(i)) && e.getV2().equals(pVertexList.get(i+1))){
								removedEdges.add(e);
                                g.removeEdge(e);
							}
						}
					}
				}
				
				//compute the shortest path from spur node to target on the updated graph
//				DijkstraShortestPath<V, ReactionEdge> dj2 = new DijkstraShortestPath<V, ReactionEdge>(graph, spur, end);
//				if(dj2.getPath()!=null){
				BioPath<V,E> spurPath = sp.getShortest(spur, end);
				if(spurPath!=null){
//					double weight2 = 0.0;
//					for(ReactionEdge e : sp2){
//						weight2+=graph.getEdgeWeight(e);
//					}
					BioPath<V,E> rootPath = previousPath.getSubPath(previousPath.getStartVertex(),spur);
//					BioPath spurPath = new BioPath(dj2.getPath());
//					BioPath spurPath = new BioPath(graph,spur,end,sp2,weight2);
					BioPath<V,E> finalPath = rootPath.appendPath(spurPath);
					//add path from root to spur + path from spur to target to the list of potential shortest path
					b.add(finalPath);
				}
				
				//restore graph
				for(E e : removedEdges){
                    g.addEdge(e.getV1(), e.getV2(), e);
				}

			}
			
			if(b.isEmpty()) break;
			Collections.sort(b);
			//add the potential path with lowest cost to the K-shortest path list
			kPaths.add(k2, b.get(0));

			b.remove(0);
		}
		return kPaths;
	}
	
	/**
	 * return all the k-shortest paths in the given graph
	 * @param k
	 * @return
	 */
	public Set<BioPath<V,E>> getAllShortestPaths(int k){
		HashSet<BioPath<V, E>> paths = new HashSet<>();
		
		KShortestPath<V, E, G> pathComputor = new KShortestPath<>(g);
		
		for(V v1 : g.vertexSet()){
			for(V v2 : g.vertexSet()){
				if(v1!=v2){
					Collection<BioPath<V,E>> ksp = pathComputor.getKShortest(v1, v2, k);
					if(!ksp.isEmpty()) paths.addAll(ksp);
				}
			}
		}
		return paths;
	}
}
