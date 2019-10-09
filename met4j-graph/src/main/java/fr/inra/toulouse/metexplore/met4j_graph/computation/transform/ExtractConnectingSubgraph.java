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
package fr.inra.toulouse.metexplore.met4j_graph.computation.transform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * Class to remove all nodes not connecting a given set of nodes of interest.
 * 'Loops' of nodes that only connects a node of interest to itself are also be removed.
 * @author clement
 * @param <V>
 * @param <E>
 */
public class ExtractConnectingSubgraph<V extends BioEntity, E extends Edge<V>> {

	/** The graph. */
	public BioGraph<V, E> g;
	
	/** The nodes of interest. */
	public Set<String> nodesOfInterest;

//
	/** The reach by matrix. */
public Integer[][] reachby;
	
	/** The reach matrix. */
	public Integer[][] reach;
	
	/** The column index map. */
	HashMap<V,Integer> colIndexMap;
	
	/** The row index map. */
	HashMap<V,Integer> rowIndexMap;
//	
	/**
 * Instantiates a new removes the unconnected nodes class.
 *
 * @param g the graph
 * @param nodesOfInterest the nodes of interest
 */
public ExtractConnectingSubgraph(BioGraph<V, E> g, BioCollection<V> nodesOfInterest) {
		this.g=g;
		this.nodesOfInterest=nodesOfInterest.getIds();
		init();
	}
		
	
	/**
	 * Compute the links between nodes.
	 */
	public void init(){
		colIndexMap = new HashMap<V,Integer>();
		rowIndexMap = new HashMap<V,Integer>();
		
		int colIndex = 0;
		int rowIndex = 0;
		
		for (V entity : g.vertexSet()){
			if(!nodesOfInterest.contains(entity.getId())){
				rowIndexMap.put(entity, rowIndex);
				rowIndex++;
			}else{
				colIndexMap.put(entity, colIndex);
				colIndex++;
			}
		}
		
		EdgeReversedGraph<V, E> g2 = new EdgeReversedGraph<V, E>(g);
		reachby = getReachMatrix(g, colIndexMap, rowIndexMap, false);
		reach = getReachMatrix(g2, colIndexMap, rowIndexMap, true);
		return;
	}
	
	/**
	 * Clean graph.
	 */
	public void cleanGraph(){
		removeNotInBetween();
		removeLoops();
	}
	
	/**
	 * Removes nodes not "between" nodes of interest.
	 */
	public void removeNotInBetween(){
		Set<V> noPathbetween = new HashSet<V>(rowIndexMap.keySet());

		for (V node : rowIndexMap.keySet()){
			boolean existingPath=false;
			int i = rowIndexMap.get(node);
			int j = 0;
			while(!existingPath && j<colIndexMap.size()){
				if (reach[i][j]==1){
					int j2=0;
					while(!existingPath && j2<colIndexMap.size()){
						if(reachby[i][j2] == 1 && j!=j2){
							existingPath=true;
						}
						j2++;
					}
				}
				j++;
			}
			if(existingPath){
				noPathbetween.remove(node);
			}
		}

		System.err.println("Removing "+noPathbetween.size()+" nodes out of paths between nodes of interest...");
		g.removeAllVertices(noPathbetween);	
		return;
	}
	
	/**
	 * Removes the loops.
	 */
	public void removeLoops(){
		int removed=0;
		Stack<V> stack = new Stack<V>();
		for(V e : g.vertexSet()){
			stack.add(e);
		}
		while (!stack.isEmpty()){
			//get stack element
			V e = stack.pop();
			
			if(!nodesOfInterest.contains(e.getId())){
				//get predecessor list
				Set<E> incomingEdges = g.incomingEdgesOf(e);
				ArrayList<V> predecessor = new ArrayList<V>();
				for(E incomingEdge : incomingEdges){
					if(!predecessor.contains(incomingEdge.getV1())){
						predecessor.add(incomingEdge.getV1());
					}
				}
				
				//get successor list
				Set<E> outgoingEdges = g.outgoingEdgesOf(e);
				ArrayList<V> successor = new ArrayList<V>();
				for(E outgoingEdge : outgoingEdges){
					if(!successor.contains(outgoingEdge.getV2())){
						successor.add(outgoingEdge.getV2());
					}
				}
				
				//check for loop
				if(predecessor.size()==1 && successor.size()==1){
					if(predecessor.get(0).equals(successor.get(0))){
						if(!stack.contains(predecessor.get(0))){
							stack.add(predecessor.get(0));
						}
						g.removeVertex(e);
						removed++;
					}
				}
			}	
		}
		System.err.println("Removing "+removed+" nodes involved in loops...");
	}
	
	
	/**
	 * Gets the reach matrix.
	 *
	 * @param g the graph
	 * @param colIndexMap the column index map
	 * @param rowIndexMap the row index map
	 * @param reversed if the graph has inverted edges
	 * @return the reach matrix
	 */
	protected Integer[][] getReachMatrix(DirectedGraph<V, E> g, HashMap<V,Integer> colIndexMap,HashMap<V,Integer> rowIndexMap, boolean reversed){
		
		Integer[][] reach = new Integer[rowIndexMap.size()][colIndexMap.size()];
		for(int i =0; i< rowIndexMap.size();i++){
			for(int j =0; j< colIndexMap.size();j++){
				reach[i][j]=0;
			}
		}
		for (V start : colIndexMap.keySet()){
			
			Set<E> edgesToRemove = new HashSet<E>();
			edgesToRemove.addAll(g.incomingEdgesOf(start));	
			for (V end :colIndexMap.keySet()){
				if(!end.equals(start)){
					edgesToRemove.addAll(g.outgoingEdgesOf(end));
				}
			}
			g.removeAllEdges(edgesToRemove);
			
			DepthFirstIterator<V, E> dfs = new DepthFirstIterator<V, E>(g,start);
			//System.out.println(start.getId());
			while (dfs.hasNext()) {
				V reached = dfs.next();
				//System.out.println("\t"+reached.getId());
				if(!colIndexMap.containsKey(reached)){
					int i = rowIndexMap.get(reached);
					int j = colIndexMap.get(start);
					reach[i][j]=1;
				}
			}
			
			for(E edge : edgesToRemove){
				//System.out.println(edge.getV1()+"-"+edge.getV2());
				if(!reversed){
					g.addEdge(edge.getV1(),edge.getV2(),edge);
				}else{
					g.addEdge(edge.getV2(),edge.getV1(),edge);
				}
				
			}
		}
		return reach;
	}
}
