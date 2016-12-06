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
package fr.inra.toulouse.metexplore.met4j_graph.computation.algo.randomWalks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.traverse.BreadthFirstIterator;

import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.Merger;
import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.RemoveUnconnectedNodes;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

/**
 * Class to compute RandomWalk betweenness.
 * @author clement
 */
public class RandomWalk<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>>{
	
	/** The adjancy matrix. */
	public BioMatrix adjancyMatrix;
	
	/** The label map. */
	public HashMap<String,Integer> labelMap;
	
	/** The index map. */
	public HashMap<Integer,String> indexMap;
	
	/** The label map of the transient matrix. */
	public HashMap<String, Integer> subQlabelMap;
	
	/** The index map of the transient matrix. */
	public HashMap<Integer,String> subQindexMap;
	
	/** The nodes of interest list. */
	public Set<String> nodesOfInterest;
	
	/** The transient matrix. */
	public BioMatrix subQ;
	
	/** The target matrix. */
	public BioMatrix subR;
	
	/** The label map of the target matrix. */
	public HashMap<String, Integer> subRlabelMap;
	
	/** The index map of the target matrix. */
	public HashMap<Integer,String> subRindexMap;
	
	/** The walk list. */
	public HashMap<String,double[]> walkList;
	
	/** The start vertex probability map. */
	public HashMap<String,Double> startProb;
	
	/** The node weights. */
	public HashMap<String,Double> nodeWeight;
	
	/** The target nodes of interest. */
	public Set<String> targetNodesOfInterest;
	
	/** The number of walks. */
	public HashMap<String, Double> numberOfWalks;
	
	/** The edge number of walks. */
	public BioMatrix edgeNumberOfWalks;
	
	/** The outgoing probabilty matrix. */
	public BioMatrix outprob;
	
	/**
	 * Instantiates a new random walk.
	 *
	 * @param g the graph
	 * @param blackNodesSet the noode of interest list
	 */
	public RandomWalk(G g, Set<String> blackNodesSet) {
		
		this.nodesOfInterest=blackNodesSet;
		labelMap = new HashMap<String, Integer>();
		indexMap = new HashMap<Integer, String>();
		
		//set node weight
		nodeWeight = new HashMap<String, Double>();
		for(String noi : nodesOfInterest){
			nodeWeight.put(noi, 1.0);
		}
		
		//remove nodes that can't be in a path between 2 nodes of interest
		System.err.println("cleaning input graph... ");
		System.err.println("input graph size: "+g.vertexSet().size());
		
		RemoveUnconnectedNodes<V,E> rm = new RemoveUnconnectedNodes<V,E>(g, nodesOfInterest);
		rm.cleanGraph();
//		rm.remove3();

		System.err.println("cleaned graph size: "+g.vertexSet().size()+"\n");
		
		//compute list of starting states to skip i.e paths from "target" nodes of interest
		// and remove nodes of interest not found in graph
		cleanNodeOfInterestList(g, this.nodesOfInterest);
		this.targetNodesOfInterest = getTargetNodesOfInterest(g, this.nodesOfInterest);
		
		//compute adjancy matrix
		int n = g.vertexSet().size();
		adjancyMatrix = new EjmlMatrix(n, n);
		setAdjancyMatrix(g);
		
		//compute transients states matrix (without starting state)
		setConstantPartOfQ(adjancyMatrix);
	}	
	
	/**
	 * Instantiates a new random walk.
	 *
	 * @param g the graph
	 * @param nodeweightFile the path to the file containing starting weight table
	 */
	public RandomWalk(G g, String nodeweightFile) {
		//import node of interest weights from file
		nodesOfInterest = new HashSet<String>();
		try {
			nodeWeight = new HashMap<String, Double>();
			BufferedReader file = new BufferedReader(new FileReader(nodeweightFile));
			String line;

			while((line = file.readLine())!= null){
				String[] splitLine = line.split("\t");
				nodesOfInterest.add(splitLine[0]);
				double weight = Double.parseDouble(splitLine[1]);
				nodeWeight.put(splitLine[0], weight);
			}
			file.close();  
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		labelMap = new HashMap<String, Integer>();
		indexMap = new HashMap<Integer, String>();
		
		//remove nodes that can't be in a path between 2 nodes of interest
//		Bionetwork2similarityGraph.exportGraph("/home/clement/Documents/raw_graph.gml", g);
		System.err.println("cleaning input graph... ");
		System.err.println("input graph size: "+g.vertexSet().size());
		
		RemoveUnconnectedNodes<V,E> rm = new RemoveUnconnectedNodes<V,E>(g, nodesOfInterest);
		rm.cleanGraph();

		System.err.println("cleaned graph size: "+g.vertexSet().size()+"\n");
		
		//compute list of starting states to skip i.e paths from "target" nodes of interest
		// and remove nodes of interest not found in graph
		cleanNodeOfInterestList(g, this.nodesOfInterest);
		this.targetNodesOfInterest = getTargetNodesOfInterest(g, this.nodesOfInterest);
		
		//compute adjancy matrix
		int n = g.vertexSet().size();
		adjancyMatrix = new EjmlMatrix(n, n);
		setAdjancyMatrix(g);
		
		//compute transients states matrix (without starting state)
		setConstantPartOfQ(adjancyMatrix);
	}
	
	/**
	 * Instantiates a new random walk.
	 *
	 * @param g the graph
	 * @param nodeWeight the node weight map
	 */
	public RandomWalk(G g, HashMap<String, Double> nodeWeight) {
		this.nodeWeight=nodeWeight;
		this.nodesOfInterest=nodeWeight.keySet();
		
		labelMap = new HashMap<String, Integer>();
		indexMap = new HashMap<Integer, String>();
		
//		//remove nodes that can't be in a path between 2 nodes of interest
//		Bionetwork2similarityGraph.exportGraph("/home/clement/Documents/raw_graph.gml", g);
		System.err.println("cleaning input graph... ");
		System.err.println("input graph size: "+g.vertexSet().size());
		
		RemoveUnconnectedNodes<V,E> rm = new RemoveUnconnectedNodes<V,E>(g, nodesOfInterest);
		rm.cleanGraph();

		System.err.println("cleaned graph size: "+g.vertexSet().size()+"\n");
		
		//compute list of starting states to skip i.e paths from "target" nodes of interest
		// and remove nodes of interest not found in graph
		cleanNodeOfInterestList(g, this.nodesOfInterest);
		this.targetNodesOfInterest = getTargetNodesOfInterest(g, this.nodesOfInterest);
		
		//compute adjancy matrix
		int n = g.vertexSet().size();
		adjancyMatrix = new EjmlMatrix(n, n);
		setAdjancyMatrix(g);
		
		//compute transients states matrix (without starting state)
		setConstantPartOfQ(adjancyMatrix);
	}
	

	/**
	 * compute adjancy matrix from graph input
	 *
	 * @param g the new adjancy matrix
	 */
	public void setAdjancyMatrix(G g){
//		System.err.print("computing adjancy matrix...");
		int index = 0;
		Set<V> vertexSet = g.vertexSet();
		
		//affect an index for all vertex
		for (V node : vertexSet){
			labelMap.put(node.getId(),index);
			indexMap.put(index, node.getId());
			index++;
		}
		
		//import edge weight as initial transition probability
		for (V node : vertexSet){
			int i = labelMap.get(node.getId());
			for (E edge : g.outgoingEdgesOf(node)){
				int j = labelMap.get(edge.getV2().getId());
				if(adjancyMatrix.get(i, j)!=0.0){
					//sum weight from edges with same source/target
					adjancyMatrix.set(i, j,adjancyMatrix.get(i, j)+g.getEdgeWeight(edge));
				}else{
					adjancyMatrix.set(i, j,g.getEdgeWeight(edge));
				}
			}
		}
		
//		System.err.println("\tdone.");
		return;
	}
	
	/**
	 * compute transients states matrix (without starting state)
	 *
	 * @param p transients states matrix
	 */
	public void setConstantPartOfQ(BioMatrix p){
//		System.err.print("extracting constant part of Q...");
		
		subQindexMap = new HashMap<Integer,String>();
		subQlabelMap = new HashMap<String, Integer>();
		subRindexMap = new HashMap<Integer,String>();
		subRlabelMap = new HashMap<String, Integer>();
		assert p.numCols()-nodesOfInterest.size()>0;
		int[] rowNcol2keepQ = new int[p.numCols()-nodesOfInterest.size()];
		int[] col2keepR = new int[nodesOfInterest.size()];
		
		//affect new index for transient states
		// and list transient row index from original matrix (row and column to keep)
		int iteratorQ=0;
		int iteratorR=0;
		for (String id : labelMap.keySet()){
			if (!nodesOfInterest.contains(id)){
				rowNcol2keepQ[iteratorQ]=labelMap.get(id);
				subQindexMap.put(iteratorQ,id);
				subQlabelMap.put(id,iteratorQ);
				iteratorQ++;
			}else{
				col2keepR[iteratorR]=labelMap.get(id);
				subRindexMap.put(iteratorR,id);
				subRlabelMap.put(id,iteratorR);
				iteratorR++;
			}
		}
		
		//extract transient row and column from original matrix
		subQ = p.getSubMatrix(rowNcol2keepQ, rowNcol2keepQ);
		subR = p.getSubMatrix(rowNcol2keepQ,col2keepR);
//		System.err.println("\tdone.");
	}
	
	/**
	 * create transients states matrix with a starting node from the node of interest list
	 *
	 * @param x the starting node
	 * @param p transients states matrix
	 * @return the absorbing matrix with x as starting node
	 */
	public BioMatrix getXabsorbingMatrix(String x, BioMatrix p){
//		System.err.print("Transform nodes of interest into absorbing nodes ...");
		
		//add 1 column and 1 row to the original transient matrix
		int m = subQ.numCols();
		int n = subQ.numRows();
		BioMatrix px = new EjmlMatrix(n+1, m+1);
		
		//copy original transient matrix
		for (int i=0;i<n;i++){
			for (int j=0;j<m;j++){
				px.set(i, j, subQ.get(i, j));
			}
		}
		
		//extract start transition probability from the original transition matrix
		int xindex = labelMap.get(x);
		for (int j=0;j<m;j++){
			String label = subQindexMap.get(j);
			int index = labelMap.get(label);
			px.set(n, j, p.get(xindex,index));
		}		
		for (int i=0;i<n;i++){
			px.set(i, m, p.get(labelMap.get(subQindexMap.get(i)),labelMap.get(x)));
		}
		px.set(n, m,  p.get(labelMap.get(x),labelMap.get(x)));
//		System.err.println("\tdone.");
		return px;
	}
	
	/**
	 * compute fundamental matrix [I-Q]^-1
	 *
	 * @param q the transient matrix
	 * @return the fundamental matrix
	 */
	public BioMatrix getFundamentalMatrix(BioMatrix q){
//		System.err.print("Computing fundamental BioMatrix...");
		
		//create identity matrix
		BioMatrix i = q.identity();
		
		//substract transient matrix to identity matrix and invert resulting matrix
		i=i.minus(q);
		BioMatrix n = i.invert();

		return n;
	}
	
	/**
	 * set edge betweenness centrality as edge weight
	 *
	 * @param g the graph
	 * @param merge the merge
	 */
	public void setEdgesPassageTime(G g, boolean merge){
		
		computePassageTime();
		//merge-> several transitions from distinct reaction with same source and target is considered as 1 transitions
		if(merge){
			Merger.mergeEdges(g);
			for(E edge : g.edgeSet()){
				String sourceId=edge.getV1().getId();
				String targetId=edge.getV2().getId();
				int sourceIndex=labelMap.get(sourceId);
				int targetIndex=labelMap.get(targetId);
				g.setEdgeScore(edge, edgeNumberOfWalks.get(sourceIndex, targetIndex));
			}
		}else{
//<<<<<<<<<<<<<<<<<<UNDER TEST
//			StochasticWeightPolicy wp = new StochasticWeightPolicy();
//			wp.setWeight(g);
//>>>>>>>>>>>>>>>>>>UNDER TEST
			for(E edge : g.edgeSet()){
				String sourceId=edge.getV1().getId();
				String targetId=edge.getV2().getId();	

				int sourceIndex=labelMap.get(sourceId);
				int targetIndex=labelMap.get(targetId);
				//the transitions probability is split between the reactions using the initial probability
				g.setEdgeScore(edge, g.getEdgeWeight(edge)*edgeNumberOfWalks.get(sourceIndex, targetIndex));
			}
		}
		return;
	}
	
	/**
	 * Computes the edges passage time.
	 *
	 * @param x the x
	 * @param nwalk the number of walks
	 * @param fundamentalMatrix the fundamental matrix
	 * @param outProbabilityVector the out-going probability vector
	 * @return the bio matrix
	 */
	public BioMatrix computeEdgesPassageTime(String x, double[] nwalk, BioMatrix fundamentalMatrix, BioMatrix outProbabilityVector){
		BioMatrix nwalkEdges = new EjmlMatrix(adjancyMatrix.numCols(),adjancyMatrix.numRows());
		for(int i=0; i<adjancyMatrix.numRows();i++){
			
			String sourceLabel = indexMap.get(i);
			if(subQlabelMap.keySet().contains(sourceLabel) || sourceLabel.equals(x)){

				for(int j=0; j<adjancyMatrix.numCols();j++){
					
					String targetLabel = indexMap.get(j);
					if(subQlabelMap.keySet().contains(targetLabel)){
						int j2 = subQlabelMap.get(indexMap.get(j));
						
						//raw source-target transition probability
						double score = adjancyMatrix.get(i, j);
						//transition probability weight by probability to not go out after using the transition
						score=score*(1-outProbabilityVector.get(j2, 0));
						//transition probability weight by probability to not reach the source after using the transition
//						score=score*(1-(fundamentalMatrix.get(j2, i2)/fundamentalMatrix.get(j2, j2)));
						
						nwalkEdges.set(i, j, score);
						
					}else if(nodesOfInterest.contains(targetLabel) && !targetLabel.equals(x)){
						nwalkEdges.set(i, j,adjancyMatrix.get(i, j));
					}else{
						nwalkEdges.set(i, j,0.0);
					}
				}
			}else{
				for(int j=0; j<adjancyMatrix.numCols();j++){
					nwalkEdges.set(i, j,0.0);
				}
			}
		}
		
		//normalize outgoing probability and weight by expected nb of walk through the source 
		for(int i=0; i<nwalkEdges.numRows();i++){
			
			String label = indexMap.get(i);
			int i2=0;
			if(nodesOfInterest.contains(label)){
				i2=nwalk.length-1;
			}else{
				i2 = subQlabelMap.get(indexMap.get(i));
			}
			
			double sum=0.0;
			for(int j=0; j<nwalkEdges.numCols();j++){
				sum+=nwalkEdges.get(i,j);
			}
			
			if(sum!=0.0){
				for(int j=0; j<nwalkEdges.numCols();j++){
					nwalkEdges.set(i,j,nwalk[i2]*nwalkEdges.get(i,j)/sum);
				}
			}

		}
		
		return nwalkEdges;
	}
	
	/**
	 * compute mean node passage time for each states
	 */
	public void computePassageTime(){
		
		//extract "out" transition probability (1- adjancy row sum) for each state
		BioMatrix outVector = new EjmlMatrix(subQ.numRows()+1,1) ;
		for (int i=0;i<subQ.numRows();i++){
			
			//extract original transition matrix index
			String label = subQindexMap.get(i);
			int index = labelMap.get(label);
			
			//sum transition probability of each transient state from original transition matrix
			//as the graph as been filtered to keep only vertex in path between nodes of interest,
			//the probability of losses is the probability to reach a vertex not in any paths
			double sum=0.0;
			for(int jadj=0;jadj<adjancyMatrix.numCols();jadj++){
				sum+=adjancyMatrix.get(index,jadj);
			}
			outVector.set(i, 0, (1.0-sum));	
		}
		
		walkList = new HashMap<String,double[]>();
		startProb = new HashMap<String,Double>();
		
		numberOfWalks = new HashMap<String, Double>();
		edgeNumberOfWalks = new EjmlMatrix(adjancyMatrix.numRows(),adjancyMatrix.numCols());
		
		//compute expected time passage through each node
		//iterate over each starting states
		int progress = 0;
		for (String x : nodesOfInterest){
			if(!targetNodesOfInterest.contains(x)){
				progress++;
				long t0 = System.nanoTime();
				
				System.err.println("COMPUTING WALKS FROM "+x+" ("+progress+"/"+(nodesOfInterest.size()-targetNodesOfInterest.size())+")");
				
				//compute fundamental matrix with x as starting state
				BioMatrix n = getFundamentalMatrix(getXabsorbingMatrix(x, adjancyMatrix));
//				System.err.println("\tdone.");

				double[] nwalks = new double[n.numCols()];
				
				int row = n.numRows()-1;//index of the starting state in the transient matrix
				
				//compute probability of reach a node not in path between node of interest, from the starting state
				double sum = 0.0;
				int index = labelMap.get(x);
				for(int j=0; j < adjancyMatrix.numCols();j++){
					sum+=adjancyMatrix.get(index, j);
				}
				outVector.set(row, 0, (1-sum));
				
				//consider the probability of reach a node not in path between node of interest during the walk,
				//by multiplying the original "out" probability from a state by the expected number of time this state is reached
				outprob = n.mult(outVector);
				
				//compute states expected time passage as the raw time passage times the probability that a walk from this state reach a node of interest
				//for(int i=0; i<n.numCols()-1; i++){		
				for(int i=0; i<n.numCols(); i++){	
					//raw count
					//nwalks[i]=n.get(row, i);
					
					//walks count without "out" state reached
					//nwalks[i]=n.get(row, i) * (1-outprob.get(i, 0));
					
					//walks count without "out" state reached nor "repass"
					nwalks[i]=n.get(row, i) * (1-outprob.get(i, 0)) * ((1-(n.get(i, i)-1)/n.get(i, i)));					
				}
				
				//compute the probabilty for a walk starting from a node of interest to reach another one 
				//weight by the input noi weights
//<<<<<<<<<<<<<<UNDER_TEST				
				startProb.put(x, nodeWeight.get(x));
//				startProb.put(x, (1-outprob.get(row, 0))*nodeWeight.get(x));
//>>>>>>>>>>>>>>>UNDERTEST
				//nwalks[n.numCols()-1]=1.0;

				walkList.put(x, nwalks);
				
				BioMatrix res = computeEdgesPassageTime(x, nwalks, n, outVector);
				edgeNumberOfWalks=edgeNumberOfWalks.plus(res.scale(startProb.get(x)));
				
				long t1 = System.nanoTime();
				System.err.println("\tdone. ("+(((t1-t0)/1000000000))+"sec)");
			}
		}
		
		//compute the sum of probability of absorbing in a node of interest for each starting one
		double tot = 0.0;
		for(double p : startProb.values()){
			tot+=p;
		}
		
		//compute betweenness centrality as the mean expected node passage time for "successfull" random walk
		//i.e walks which reach a node of interest
//		System.out.println("\n=================\nfinal Centrality:\n=================");
		for (int index : subQindexMap.keySet()){
			double centrality = 0.0;
			for (String x : walkList.keySet()){
				double[] nwalks = walkList.get(x);
				centrality += nwalks[index]*(startProb.get(x)/tot);
			}
			numberOfWalks.put(subQindexMap.get(index), centrality);			
			
//			System.out.println(subQindexMap.get(index)+"\t"+centrality);
		}
		for(String x:startProb.keySet()){
			numberOfWalks.put(x, startProb.get(x)/tot);
//			System.out.println(x+"\t"+startProb.get(x)/tot);
		}
		
		edgeNumberOfWalks=edgeNumberOfWalks.scale(1.0/tot);

		return;
	}
	
	/**
	 * Computes the node of interest table.
	 *
	 * @return the node of interest matrix
	 */
	public BioMatrix computeNodeOfInterestTable(){
		assert (!walkList.isEmpty());
		BioMatrix entry = new EjmlMatrix(nodesOfInterest.size(),subQindexMap.size());
		for(String x:walkList.keySet()){
			int i = subRlabelMap.get(x);
			double[] nwalks = walkList.get(x);
			for(int j=0; j<nwalks.length-1;j++){
				entry.set(i, j, nwalks[j]*startProb.get(x));
			}
		}
		BioMatrix noiTable = entry.mult(subR);
		return noiTable;
	}
	
	/**
	 * Gets the adjancy matrix.
	 *
	 * @return the adjancy matrix
	 */
	public BioMatrix getAdjancyMatrix() {
		return adjancyMatrix;
	}
	
	/**
	 * Gets the index map.
	 *
	 * @return the index map
	 */
	public HashMap<String, Integer> getIndexMap() {
		return labelMap;
	}
	
	/**
	 * Gets the constant part of the transient matrix.
	 *
	 * @return the constant part of the transient matrix.
	 */
	public BioMatrix getSubQ() {
		return subQ;
	}
	
	/**
	 * Gets the index map of the transient matrix.
	 *
	 * @return the sub index map of the transient matrix
	 */
	public HashMap<Integer, String > getsubQindexMap() {
		return subQindexMap;
	}	
	
	/**
	 * Clean node of interest list.
	 *
	 * @param g the graph
	 * @param nodesOfInterest the nodes of interest list
	 * @return the number of removed vertex
	 */
	public int cleanNodeOfInterestList(G g, Set<String> nodesOfInterest){
		Set<String> toRemove = new HashSet<String>();
		for (String noi : nodesOfInterest){
			//assert that this nodes is in graph
			boolean isInGraph=false;
			for (V e : g.vertexSet()){
				if (e.getId().equals(noi)){
					isInGraph=true;
				}
			}
			//removing node of interest not in graph
			if (!isInGraph){
				System.err.println("Error: node of interest "+noi+" not found in graph!");
				System.err.println("\tRemoving "+noi+"...");
				toRemove.add(noi);
			}
		}
		if (!toRemove.isEmpty()){
			for (String n2r : toRemove){
				nodesOfInterest.remove(n2r);
			}
		}
		return toRemove.size();
	}
	
	/**
	 * Gets the target nodes of interest.
	 *
	 * @param g the graph
	 * @param nodesOfInterest the nodes of interest list
	 * @return the target nodes of interest list
	 */
	public Set<String> getTargetNodesOfInterest(G g, Set<String> nodesOfInterest){
		
		Set<String> targetNodesOfInterest = new HashSet<String>();
		for (String noi : nodesOfInterest){
			if(nodeWeight.get(noi)==0.0){
				System.err.println(noi+" weight set to 0.0");
				targetNodesOfInterest.add(noi);
			}else{
				boolean connected = false;
				V node = null;
				for (V entity : g.vertexSet()){
					if (entity.getId().equals(noi)){
						node = entity;
					}
				}
				
				if (!g.outgoingEdgesOf(node).isEmpty()){
					//assert that at least one node of interest can be reached from this node
					BreadthFirstIterator<V, E> bfs = new BreadthFirstIterator<V, E>(g,node);
					bfs.next();
					while (bfs.hasNext() && !connected){
						V next = bfs.next();
						//System.out.println("\t"+next.getId());
						if (nodesOfInterest.contains(next.getId())){
							connected = true;
						}
					}
					//removing unconnected node of interest
					if (!connected){		
						System.err.println("can't reach any node of interest from "+noi+"!");
						targetNodesOfInterest.add(noi);	
					}
				}else{
					System.err.println("can't reach any node of interest from "+noi+"!");
					targetNodesOfInterest.add(noi);
				}
			}
		}	
		return targetNodesOfInterest;
	}
	

	/**
	 * Gets the node weight map.
	 *
	 * @return the node weight
	 */
	public HashMap<String, Double> getNodeWeight() {
		return nodeWeight;
	}

	/**
	 * Sets the node weight map.
	 *
	 * @param nodeWeight the node weight map
	 */
	public void setNodeWeight(HashMap<String, Double> nodeWeight) {
		this.nodeWeight = nodeWeight;
	}

}

