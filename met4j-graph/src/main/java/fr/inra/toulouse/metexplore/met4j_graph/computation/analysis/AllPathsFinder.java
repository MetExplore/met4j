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
package fr.inra.toulouse.metexplore.met4j_graph.computation.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

/**
 * The Class AllPathsFinder.
 * @author clement
 */
public class AllPathsFinder {
	
	/** The graph. */
	public DirectedWeightedMultigraph<BioPhysicalEntity, ReactionEdge> g;
	
	/** The sources. */
	public HashSet<BioPhysicalEntity> sources;
	
	/** The targets. */
	public HashSet<BioPhysicalEntity> targets;
	
	/** The edges to keep. */
	public HashSet<ReactionEdge> edgesTokeep;
	
	/** The color. */
	public HashMap<BioPhysicalEntity, Boolean> color = new HashMap<BioPhysicalEntity, Boolean>();      // to remember visit

	/** The edge color. */
	public HashMap<ReactionEdge, Boolean> edgeColor = new HashMap<ReactionEdge, Boolean>();      // to remember visit

	public ArrayList<BioPhysicalEntity> al = new ArrayList<BioPhysicalEntity>();
   	
   public ArrayList<ReactionEdge> al2 = new ArrayList<ReactionEdge>();

   /** The size. */
   public int size = 0;
   	
	/**
	 * Instantiates a new all paths finder.
	 *
	 * @param g the graph
	 * @param sources the sources
	 * @param targets the targets
	 */
	@SuppressWarnings("unchecked")
	public AllPathsFinder(DirectedWeightedMultigraph<BioPhysicalEntity, ReactionEdge> g,HashSet<BioPhysicalEntity> sources, HashSet<BioPhysicalEntity> targets) {
		this.g=g;
		this.targets=(HashSet<BioPhysicalEntity>) targets.clone();
		this.sources=(HashSet<BioPhysicalEntity>) sources.clone();
		for(BioPhysicalEntity entity: g.vertexSet()){
			color.put(entity, false);
		}
//		
		for(ReactionEdge edge : g.edgeSet()){
			edgeColor.put(edge, false);
		}
//		
		edgesTokeep = new HashSet<ReactionEdge>();
	}
	
	/**
	 * Shortest path.
	 *
	 * @param start the start vertex
	 * @param end the end vertex
	 * @return the list
	 */
	public List<ReactionEdge> shortestPath(BioPhysicalEntity start, BioPhysicalEntity end){
		return DijkstraShortestPath.findPathBetween(g, start, end);
	}
	
	/**
	 * Deep first search algorithm
	 *
	 * @param src the source
	 */
	public void dfs(BioPhysicalEntity src) {
        al.add(src);
        size++;
//        
        if(size>=2){
	        for(ReactionEdge edge : g.edgeSet()){
	        	if(edge.getV1().equals(al.get(size-2)) && edge.getV2().equals(al.get(size-1))){
	        		al2.add(edge);
	        	}
	        }
        }
//       
        color.put(src, true);
        if (targets.contains(src)) {       // tests for base condition to stop
            for (ReactionEdge edge : al2) {
                //     Prints the path
                System.out.print(edge.getV1().getId()+"->"+edge.getV2().getId() + "  ");
                //targets.add(edge.getV1());
            }
            System.out.println();            
            return;
        }
        
        for(ReactionEdge edge : g.outgoingEdgesOf(src)){
        	BioPhysicalEntity next = edge.getV2();
        	if(!color.get(next)){
        		this.dfs(next);
        		color.put(next, false);
        		size--;
                al.remove(size);             
                if(size>=2){
                	al2.remove(size-1);
                }              
        	}
        }
    }
	
	/**
	 * Deep first search algorithm
	 *
	 * @param srcEdge the source edge
	 */
	public void dfs(ReactionEdge srcEdge) {
//      System.out.println(srcEdge.getV1().getId()+"->"+srcEdge.getV2().getId());
		al2.add(srcEdge);
		edgeColor.put(srcEdge, true);
        BioPhysicalEntity src = srcEdge.getV2();
        size++;
        color.put(src, true);
//        edgeColor.put(srcEdge, true);
        
        if (targets.contains(src)) {       // tests for base condition to stop
        	for (ReactionEdge edge : al2) {
//        		System.out.print(edge.getV1().getId()+"->"+edge.getV2().getId() + "  ");    		
        		edgesTokeep.add(edge);
        		//targets.add(edge.getV1());
        		al.add(edge.getV1());
        	}
//        	System.out.println();
//        	targets.add(srcEdge.getV1());
            return;
        }
        
        for(ReactionEdge edge : g.outgoingEdgesOf(src)){
        	BioPhysicalEntity next = edge.getV2();
        	if(!color.get(next)){
  //      		if(!edgeColor.get(edge)){
        			this.dfs(edge);
        			color.put(next, false);
        			size--;
                	al2.remove(size);
 //               }
        	}
        }
        
        if(al.contains(src)){
        	boolean complete=true;
        	for(ReactionEdge edge : g.outgoingEdgesOf(src)){
        		if(!edgeColor.get(edge)) complete=false;
        	}
        	if(complete) targets.add(src);
        }

    }
	
	
	/**
	 * Extract all path union.
	 */
	public void extractAllPathUnion(){
		System.out.println("Extract all simple paths between source/targets");
		//extract edges from simple paths between source/targets
		edgesTokeep = new HashSet<ReactionEdge>();
		for (BioPhysicalEntity source : sources){
			System.out.println("\tstarting from "+source.getId()+"...");
			boolean sourceToSource = targets.contains(source);
			if (sourceToSource) targets.remove(source);
			for (ReactionEdge sourceEdge : g.outgoingEdgesOf(source)){
				dfs(sourceEdge);
			}
			//dfs(source);
			if (sourceToSource) targets.add(source);
		}
		
		//filter graph to keep only thoses edges
		Set<ReactionEdge> edgesToRemove = new HashSet<ReactionEdge>();
		edgesToRemove.addAll(g.edgeSet());
		edgesToRemove.removeAll(edgesTokeep);
		System.err.println("removing "+edgesToRemove.size()+" edges");
		g.removeAllEdges(edgesToRemove);	
	}
}
