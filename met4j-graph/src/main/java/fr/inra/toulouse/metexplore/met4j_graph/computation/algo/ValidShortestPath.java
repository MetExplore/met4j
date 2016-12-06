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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;

/**
 * Class to compute the shortest paths in a compound graph, disable the use of a reaction twice.
 * @author clement
 */
public class ValidShortestPath extends ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>{
	

	public ValidShortestPath(CompoundGraph g) {
		super(g);
	}
	
	/**
	 * compute the shortest paths (or lightest paths if the graph is weighted) between 2 nodes in a compound graph,
	 * disallow the use of a reaction twice.
	 *
	 * @param graph the graph
	 * @param start the start vertex
	 * @param end the end vertex
	 * @return the list of edges involved in the shortest path
	 */
	@Override
	public BioPath<BioPhysicalEntity, ReactionEdge> getShortest(BioPhysicalEntity start, BioPhysicalEntity end) throws IllegalArgumentException{
		if(!g.containsVertex(start)){
			throw(new IllegalArgumentException("Error: start node "+start.getId()+" not found in graph"));
		}
		if(!g.containsVertex(end)){
			throw(new IllegalArgumentException("Error: end node "+end.getId()+" not found in graph"));
		}
		
		HashMap<BioPhysicalEntity,ReactionEdge> incoming = new HashMap<BioPhysicalEntity, ReactionEdge>();
		HashMap<BioPhysicalEntity,Double> distMap = new HashMap<BioPhysicalEntity, Double>();
		Set<BioPhysicalEntity> unseen = new HashSet<BioPhysicalEntity>();
		Set<BioPhysicalEntity> seen = new HashSet<BioPhysicalEntity>();
		
		//init dist from start
		for(BioPhysicalEntity v : g.vertexSet()){
			distMap.put(v, Double.POSITIVE_INFINITY);
		}
		
		unseen.add(start);
		distMap.put(start,0.0);
		
		while (!unseen.isEmpty()) {
			
			//get the closest node from the start vertex
			BioPhysicalEntity n = super.getNearest(distMap,unseen);
			unseen.remove(n);
			seen.add(n);
			
			//get list of reactions used in candidate path
			Set<BioChemicalReaction> reactionUsed = new HashSet<BioChemicalReaction>();
			BioPhysicalEntity currentVertex = n;
			while(currentVertex!=start){
				ReactionEdge pathEdge = incoming.get(currentVertex);
				reactionUsed.add(pathEdge.getReaction());
				currentVertex=pathEdge.getV1();
			}
			
			//add current nodes's successor to the list of node to process, if not already seen
			//	skip outgoing edges from reaction already used in path
			for(ReactionEdge e : g.outgoingEdgesOf(n)){
				if(n==start || !reactionUsed.contains(e.getReaction())){
					BioPhysicalEntity successor = e.getV2();
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

		}
		if(!incoming.containsKey(end)) return null;
		
		//backtracking
		double weight = 0.0;
		List<ReactionEdge> sp = new ArrayList<ReactionEdge>();
		BioPhysicalEntity currentVertex = end;
		while(currentVertex!=start){
			ReactionEdge e = incoming.get(currentVertex);
			weight+=g.getEdgeWeight(e);
			sp.add(e);
			incoming.remove(currentVertex);
			currentVertex=e.getV1();
			if(incoming.isEmpty() && currentVertex!=start ) return null;
		}
		Collections.reverse(sp);
		
		BioPath<BioPhysicalEntity,ReactionEdge> path1 = new BioPath<BioPhysicalEntity,ReactionEdge>(g, start, end, sp, weight);
		
		
		
		return path1;
	}

}