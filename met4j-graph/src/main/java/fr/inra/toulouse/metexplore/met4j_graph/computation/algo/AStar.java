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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.computation.algo.heuristic.AStarHeuristic;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
* implementation of the A* algorithm (shortest path with estimated remaining distance computed at each step)
 * The estimated remaining distance has to be computed by a class implementing the AstarHeuristic interface
 * An heuristic function using chemical similarity is available in ChemicalSimilarityHeuristic class
 * @author clement
 */
public class AStar<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V ,E>>{
	
	/** The graph. */
	public G g;
	
	/** The heuristic. */
	public AStarHeuristic<V> h;

	
	/**
	 * Instantiates a new a A* computor.
	 *
	 * @param g the graph
	 * @param h the heuristic
	 */
	public AStar(G g, AStarHeuristic<V> h) {
		this.g=g;
		this.h=h;
	}
	
	/**
	 * Find best path.
	 *
	 * @param start the start vertex
	 * @param end the end vertex
	 * @return the list of edges involved in the best path
	 */
	public List<E> findBestPath(V start, V end){
		HashMap<V, Double> open= new HashMap<>();
		HashMap<V, Double> closed= new HashMap<>();
		HashSet<E> path = new HashSet<>();
		open.put(start, 0.0);
		while(!open.isEmpty() && !closed.containsKey(end)){
//			String openS = "open : ";
//			for(V e1 : open.keySet()){
//				openS+=(e1.getName()+"("+open.get(e1)+") ");
//			}
//			String closedS = "closed : ";
//			for(V e1 : closed.keySet()){
//				closedS+=(e1.getName()+"("+closed.get(e1)+") ");
//			}
//			System.err.println(openS);
//			System.err.println(closedS);
			
			//get the nearest predicted Neighbor
			V bestN = getBestNeighbor(open, end);
//			System.err.println("best neighbor : "+bestN.getName());
			
			//if not already visited
			if(!closed.containsKey(bestN)){
				closed.put(bestN, open.get(bestN));
				
				//add successors to the list
				for(E e : g.outgoingEdgesOf(bestN)){
					V nei = e.getV2();
					if(!open.containsKey(nei)){
						open.put(nei, open.get(bestN)+ g.getEdgeWeight(e));
						path.add(e);
					
					}else if(open.get(bestN)+ g.getEdgeWeight(e)<open.get(nei)){
						//update the cost to reach successor
						open.put(nei, open.get(bestN)+ g.getEdgeWeight(e));
						//update edges in path
						E old=null;
						for(E edgePath : path){
							if(edgePath.getV2()==nei) old=edgePath;
						}
						path.remove(old);
						path.add(e);
					}
				}
//				System.err.println("path : "+path);
				
			}
//			System.err.println("-------------------");
			open.remove(bestN);
		}
		if(closed.containsKey(end)){
//			System.err.println("BACKTRACKING ");
			return backtracking(path, new ArrayList<>(), start, end);
		}
		return null;
	}
	
	/**
	 * Backtracking.
	 *
	 * @param edges the edges
	 * @param path the path
	 * @param start the start vertex
	 * @param end the end vertex
	 * @return the list of seen edges
	 */
	private ArrayList<E> backtracking(HashSet<E> edges, ArrayList<E> path, V start, V end){
//		System.err.println("\t"+path+" "+start+" "+end);
		for(E e: edges){
			if(e.getV2()==end){
				boolean checkLoop = true;
				for(E e1 : path){
					if (e1.getV2().equals(e.getV1())) {
						checkLoop = false;
						break;
					}
				}
				if(checkLoop){
					path.add(e);
					if(e.getV1()==start) return path;
//					HashSet<E> edges2 = new HashSet<>(edges);
//					edges2.remove(e);
					ArrayList<E> path2 = backtracking(edges, path, start, e.getV1());
//					ArrayList<E> path2 = backtracking(edges2, path, start, e.getV1());
					for(E e2: path2){
						if(e2.getV1()==start) return path2;
					}
				}
			}
		}
		return path;
	}
	
	
	/**
	 * Gets the best neighbor.
	 *
	 * @param open the open
	 * @param end the end vertex
	 * @return the best neighbor
	 */
	private V getBestNeighbor(HashMap<V, Double> open, V end){
		double minCost = Double.MAX_VALUE;
		V bestN = null;
		for(V n : open.keySet()){
			double cost = open.get(n)+ h.getHeuristicCost(n,end);
//			System.err.println("\t"+n.getName()+" = "+cost);
			if(cost<minCost){
				minCost=cost;
				bestN=n;
			}
		}
		return bestN;
	}
	
	/**
	 * Gets the best path union edge list.
	 *
	 * @param graph the graph
	 * @param nodeOfInterest the node of interest list
	 * @return the best path union edge list
	 */
	public List<E> getBestPathUnionList(G graph, Set<V> nodeOfInterest){
		ArrayList<E> best = new ArrayList<>();
		for(V start : nodeOfInterest){
			for(V end : nodeOfInterest){
				if(start!=end){
					
					List<E> bestPaths = findBestPath(start,end);
					if(bestPaths!=null && !bestPaths.isEmpty()){
						best.addAll(findBestPath(start,end));
					}
				}
			}
		}
		return best;
	}
	
	/**
	 * Gets the best path union edge list.
	 *
	 * @param graph the graph
	 * @param startList the starting node of interest list
	 * @param endList the target node of interest list
	 * @return the best path union edge list
	 */
	public List<E> getBestPathUnionList(G graph, Set<V> startList, Set<V> endList){
		ArrayList<E> best = new ArrayList<>();
		for(V start : startList){
			for(V end : endList){
				if(start!=end){
					best.addAll(findBestPath(start,end));
				}
			}
		}
		return best;
	}
	
}
