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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.ExtractConnectingSubgraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

/**
 * Class to compute RandomWalk betweenness.
 * @author clement
 */
public class RandomWalk<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>>{
	
	/** The adjacency matrix. */
	private final BioMatrix adjacencyMatrix;
	
	/** The label map. */
	private final HashMap<V,Integer> labelMap;
	
	/** The index map. */
	private final HashMap<Integer,V> indexMap;
	
	/** The label map of the transient matrix. */
	private HashMap<V, Integer> subQlabelMap;
	
	/** The index map of the transient matrix. */
	private HashMap<Integer,V> subQindexMap;
	
	/** The nodes of interest list. */
	private final BioCollection<V> nodesOfInterest;
	
	/** The transient matrix. */
	private BioMatrix subQ;
	
	/** The target matrix. */
	private BioMatrix subR;
	
	/** The label map of the target matrix. */
	private HashMap<V, Integer> subRlabelMap;
	
	/** The index map of the target matrix. */
	private HashMap<Integer,V> subRindexMap;
	
	/** The walk list. */
	private HashMap<V,double[]> walkList;
	
	/** The start vertex probability map. */
	private HashMap<V,Double> startProb;
	
	/** The node weights. */
	private HashMap<V,Double> nodeWeight;
	
	/** The target nodes of interest. */
	private final BioCollection<V> targetNodesOfInterest;
	
	/** The number of walks. */
	private HashMap<V, Double> numberOfWalks;
	
	/** The edge number of walks. */
	private BioMatrix edgeNumberOfWalks;
	
	/** The outgoing probability matrix. */
	private BioMatrix outprob;
	
	/**
	 * Instantiates a new random walk.
	 *
	 * @param g the graph
	 * @param nodesOfInterest the node of interest list
	 */
	public RandomWalk(G g, BioCollection<V> nodesOfInterest) {
		
		this.nodesOfInterest=nodesOfInterest;
        labelMap = new HashMap<>();
        indexMap = new HashMap<>();
		
		//set node weight
        nodeWeight = new HashMap<>();
		for(V noi : this.nodesOfInterest){
            nodeWeight.put(noi, 1.0);
		}
		
		//remove nodes that can't be in a path between 2 nodes of interest
		System.err.println("cleaning input graph... ");
		System.err.println("input graph size: "+g.vertexSet().size());
		
		ExtractConnectingSubgraph<V,E> rm = new ExtractConnectingSubgraph<>(g, nodesOfInterest);
		rm.cleanGraph();
//		rm.remove3();

		System.err.println("cleaned graph size: "+g.vertexSet().size()+"\n");
		
		//compute list of starting states to skip i.e paths from "target" nodes of interest
		// and remove nodes of interest not found in graph
        cleanNodeOfInterestList(g, this.nodesOfInterest);
        this.targetNodesOfInterest = getTargetNodesOfInterest(g, this.nodesOfInterest);
		
		//compute adjacency matrix
		int n = g.vertexSet().size();
        adjacencyMatrix = new EjmlMatrix(n, n);
        setAdjacencyMatrix(g);
		
		//compute transients states matrix (without starting state)
        setConstantPartOfQ(adjacencyMatrix);
	}
	
	/**
	 * Instantiates a new random walk.
	 *
	 * @param g the graph
	 * @param nodeWeight the node weight map
	 */
	public RandomWalk(G g, HashMap<V, Double> nodeWeight) {
		this.nodeWeight=nodeWeight;
        this.nodesOfInterest =new BioCollection<>(nodeWeight.keySet());

        labelMap = new HashMap<>();
        indexMap = new HashMap<>();
		
//		//remove nodes that can't be in a path between 2 nodes of interest
//		Bionetwork2similarityGraph.exportGraph("/home/clement/Documents/raw_graph.gml", g);
		System.err.println("cleaning input graph... ");
		System.err.println("input graph size: "+g.vertexSet().size());
		
		ExtractConnectingSubgraph<V,E> rm = new ExtractConnectingSubgraph<>(g, nodesOfInterest);
		rm.cleanGraph();

		System.err.println("cleaned graph size: "+g.vertexSet().size()+"\n");
		
		//compute list of starting states to skip i.e paths from "target" nodes of interest
		// and remove nodes of interest not found in graph
        cleanNodeOfInterestList(g, this.nodesOfInterest);
        this.targetNodesOfInterest = getTargetNodesOfInterest(g, this.nodesOfInterest);
		
		//compute adjacency matrix
		int n = g.vertexSet().size();
        adjacencyMatrix = new EjmlMatrix(n, n);
        setAdjacencyMatrix(g);
		
		//compute transients states matrix (without starting state)
        setConstantPartOfQ(adjacencyMatrix);
	}
	

	/**
	 * compute adjacency matrix from graph input
	 *
	 * @param g the new adjacency matrix
	 */
	public void setAdjacencyMatrix(G g){
		int index = 0;
		Set<V> vertexSet = g.vertexSet();
		
		//affect an index for all vertex
		for (V node : vertexSet){
            labelMap.put(node,index);
            indexMap.put(index, node);
			index++;
		}
		
		//import edge weight as initial transition probability
		for (V node : vertexSet){
			int i = labelMap.get(node);
			for (E edge : g.outgoingEdgesOf(node)){
				int j = labelMap.get(edge.getV2());
				if(adjacencyMatrix.get(i, j)!=0.0){
					//sum weight from edges with same source/target
                    adjacencyMatrix.set(i, j, adjacencyMatrix.get(i, j)+g.getEdgeWeight(edge));
				}else{
                    adjacencyMatrix.set(i, j,g.getEdgeWeight(edge));
				}
			}
		}
		
    }
	
	/**
	 * compute transients states matrix (without starting state)
	 *
	 * @param p transients states matrix
	 */
	public void setConstantPartOfQ(BioMatrix p){

        subQindexMap = new HashMap<>();
        subQlabelMap = new HashMap<>();
        subRindexMap = new HashMap<>();
        subRlabelMap = new HashMap<>();
		assert p.numCols()- nodesOfInterest.size()>0;
		int[] rowNcol2keepQ = new int[p.numCols()- nodesOfInterest.size()];
		int[] col2keepR = new int[nodesOfInterest.size()];
		
		//affect new index for transient states
		// and list transient row index from original matrix (row and column to keep)
		int iteratorQ=0;
		int iteratorR=0;
		for (Map.Entry<V, Integer> entry : labelMap.entrySet()){
			V n = entry.getKey();
			if (!nodesOfInterest.contains(n)){
				rowNcol2keepQ[iteratorQ]= entry.getValue();
                subQindexMap.put(iteratorQ,n);
                subQlabelMap.put(n,iteratorQ);
				iteratorQ++;
			}else{
				col2keepR[iteratorR]= entry.getValue();
                subRindexMap.put(iteratorR,n);
                subRlabelMap.put(n,iteratorR);
				iteratorR++;
			}
		}
		
		//extract transient row and column from original matrix
        subQ = p.getSubMatrix(rowNcol2keepQ, rowNcol2keepQ);
        subR = p.getSubMatrix(rowNcol2keepQ,col2keepR);
	}
	
	/**
	 * create transients states matrix with a starting node from the node of interest list
	 *
	 * @param x the starting node
	 * @param p transients states matrix
	 * @return the absorbing matrix with x as starting node
	 */
	public BioMatrix getXabsorbingMatrix(V x, BioMatrix p){

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
			V node = subQindexMap.get(j);
			int index = labelMap.get(node);
			px.set(n, j, p.get(xindex,index));
		}		
		for (int i=0;i<n;i++){
			px.set(i, m, p.get(labelMap.get(subQindexMap.get(i)), labelMap.get(x)));
		}
		px.set(n, m,  p.get(labelMap.get(x), labelMap.get(x)));
		return px;
	}
	
	/**
	 * compute fundamental matrix [I-Q]^-1
	 *
	 * @param q the transient matrix
	 * @return the fundamental matrix
	 */
	public BioMatrix getFundamentalMatrix(BioMatrix q){

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
			EdgeMerger.mergeEdges(g);
			for(E edge : g.edgeSet()){
				V source=edge.getV1();
				V target=edge.getV2();
				int sourceIndex= labelMap.get(source);
				int targetIndex= labelMap.get(target);
				g.setEdgeScore(edge, edgeNumberOfWalks.get(sourceIndex, targetIndex));
			}
		}else{
//<<<<<<<<<<<<<<<<<<UNDER TEST
//			StochasticWeightPolicy wp = new StochasticWeightPolicy();
//			wp.setWeight(g);
//>>>>>>>>>>>>>>>>>>UNDER TEST
			for(E edge : g.edgeSet()){
				V source=edge.getV1();
				V target=edge.getV2();

				int sourceIndex= labelMap.get(source);
				int targetIndex= labelMap.get(target);
				//the transitions probability is split between the reactions using the initial probability
				g.setEdgeScore(edge, g.getEdgeWeight(edge)* edgeNumberOfWalks.get(sourceIndex, targetIndex));
			}
		}
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
	public BioMatrix computeEdgesPassageTime(V x, double[] nwalk, BioMatrix fundamentalMatrix, BioMatrix outProbabilityVector){
		BioMatrix nwalkEdges = new EjmlMatrix(adjacencyMatrix.numCols(), adjacencyMatrix.numRows());
		for(int i = 0; i< adjacencyMatrix.numRows(); i++){
			
			V source = indexMap.get(i);
			if(subQlabelMap.containsKey(source) || source.equals(x)){

				for(int j = 0; j< adjacencyMatrix.numCols(); j++){
					
					V target = indexMap.get(j);
					if(subQlabelMap.containsKey(target)){
						int j2 = subQlabelMap.get(indexMap.get(j));
						
						//raw source-target transition probability
						double score = adjacencyMatrix.get(i, j);
						//transition probability weight by probability to not go out after using the transition
						score=score*(1-outProbabilityVector.get(j2, 0));
						//transition probability weight by probability to not reach the source after using the transition
//						score=score*(1-(fundamentalMatrix.get(j2, i2)/fundamentalMatrix.get(j2, j2)));
						
						nwalkEdges.set(i, j, score);
						
					}else if(nodesOfInterest.contains(target) && !target.equals(x)){
						nwalkEdges.set(i, j, adjacencyMatrix.get(i, j));
					}else{
						nwalkEdges.set(i, j,0.0);
					}
				}
			}else{
				for(int j = 0; j< adjacencyMatrix.numCols(); j++){
					nwalkEdges.set(i, j,0.0);
				}
			}
		}
		
		//normalize outgoing probability and weight by expected nb of walk through the source 
		for(int i=0; i<nwalkEdges.numRows();i++){
			
			V node = indexMap.get(i);
			int i2=0;
			if(nodesOfInterest.contains(node)){
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
		
		//extract "out" transition probability (1- adjacency row sum) for each state
		BioMatrix outVector = new EjmlMatrix(subQ.numRows()+1,1) ;
		for (int i = 0; i< subQ.numRows(); i++){
			
			//extract original transition matrix index
			V node = subQindexMap.get(i);
			int index = labelMap.get(node);
			
			//sum transition probability of each transient state from original transition matrix
			//as the graph as been filtered to keep only vertex in path between nodes of interest,
			//the probability of losses is the probability to reach a vertex not in any paths
			double sum=0.0;
			for(int jadj = 0; jadj< adjacencyMatrix.numCols(); jadj++){
				sum+= adjacencyMatrix.get(index,jadj);
			}
			outVector.set(i, 0, (1.0-sum));	
		}

        walkList = new HashMap<>();
        startProb = new HashMap<>();

        numberOfWalks = new HashMap<>();
        edgeNumberOfWalks = new EjmlMatrix(adjacencyMatrix.numRows(), adjacencyMatrix.numCols());
		
		//compute expected time passage through each node
		//iterate over each starting states
		int progress = 0;
		for (V x : nodesOfInterest){
			if(!targetNodesOfInterest.contains(x)){
				progress++;
				long t0 = System.nanoTime();
				
				System.err.println("COMPUTING WALKS FROM "+x+" ("+progress+"/"+(nodesOfInterest.size()- targetNodesOfInterest.size())+")");
				
				//compute fundamental matrix with x as starting state
				BioMatrix n = getFundamentalMatrix(getXabsorbingMatrix(x, adjacencyMatrix));

				double[] nwalks = new double[n.numCols()];
				
				int row = n.numRows()-1;//index of the starting state in the transient matrix
				
				//compute probability of reach a node not in path between node of interest, from the starting state
				double sum = 0.0;
				int index = labelMap.get(x);
				for(int j = 0; j < adjacencyMatrix.numCols(); j++){
					sum+= adjacencyMatrix.get(index, j);
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
					nwalks[i]=n.get(row, i) * (1- outprob.get(i, 0)) * ((1-(n.get(i, i)-1)/n.get(i, i)));
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
                edgeNumberOfWalks = edgeNumberOfWalks.plus(res.scale(startProb.get(x)));
				
				long t1 = System.nanoTime();
				System.err.println("\tdone. ("+(((t1-t0)/1000000000))+"sec)");
			}
		}
		
		//compute the sum of probability of absorbing in a node of interest for each starting one
		double tot = 0.0;
		for(double p : startProb.values()){
			tot+=p;
		}
		
		//compute betweenness centrality as the mean expected node passage time for "successful" random walk
		//i.e walks which reach a node of interest
		for (Map.Entry<Integer, V> e : subQindexMap.entrySet()){
			double centrality = 0.0;
			for (Map.Entry<V, double[]> entry : walkList.entrySet()){
				double[] nwalks = entry.getValue();
				centrality += nwalks[e.getKey()]*(startProb.get(entry.getKey())/tot);
			}
            numberOfWalks.put(e.getValue(), centrality);
		}
		for(Map.Entry<V, Double> entry : startProb.entrySet()){
            numberOfWalks.put(entry.getKey(), entry.getValue() /tot);
		}

        edgeNumberOfWalks = edgeNumberOfWalks.scale(1.0/tot);

    }
	
	/**
	 * Computes the node of interest table.
	 *
	 * @return the node of interest matrix
	 */
	public BioMatrix computeNodeOfInterestTable(){
		assert (!walkList.isEmpty());
		BioMatrix entry = new EjmlMatrix(nodesOfInterest.size(), subQindexMap.size());
		for(Map.Entry<V, double[]> e : walkList.entrySet()){
			V x = e.getKey();
			int i = subRlabelMap.get(x);
			double[] nwalks = e.getValue();
			for(int j=0; j<nwalks.length-1;j++){
				entry.set(i, j, nwalks[j]* startProb.get(x));
			}
		}
		BioMatrix noiTable = entry.mult(subR);
		return noiTable;
	}
	
	/**
	 * Gets the adjacency matrix.
	 *
	 * @return the adjacency matrix
	 */
	public BioMatrix getadjacencyMatrix() {
		return adjacencyMatrix;
	}
	
	/**
	 * Gets the index map.
	 *
	 * @return the index map
	 */
	public HashMap<V, Integer> getIndexMap() {
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
	public HashMap<Integer, V > getsubQindexMap() {
		return subQindexMap;
	}	
	
	/**
	 * Clean node of interest list.
	 *
	 * @param g the graph
	 * @param nodesOfInterest the nodes of interest list
	 * @return the number of removed vertex
	 */
	public int cleanNodeOfInterestList(G g, BioCollection<V> nodesOfInterest){
		BioCollection<V> toRemove = new BioCollection<>();
		for (V noi : nodesOfInterest){
			//assert that this nodes is in graph
			boolean isInGraph=false;
			for (V e : g.vertexSet()){
				if (e.equals(noi)) {
					isInGraph = true;
					break;
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
			for (V n2r : toRemove){
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
	public BioCollection<V> getTargetNodesOfInterest(G g, BioCollection<V> nodesOfInterest){
		
		BioCollection<V> targetNodesOfInterest = new BioCollection<>();
		for (V noi : nodesOfInterest){
			if(nodeWeight.get(noi)==0.0){
				System.err.println(noi+" weight set to 0.0");
				targetNodesOfInterest.add(noi);
			}else{
				boolean connected = false;
				V node = null;
				for (V entity : g.vertexSet()){
					if (entity.equals(noi)){
						node = entity;
					}
				}
				
				if (!g.outgoingEdgesOf(node).isEmpty()){
					//assert that at least one node of interest can be reached from this node
					BreadthFirstIterator<V, E> bfs = new BreadthFirstIterator<>(g, node);
					bfs.next();
					while (bfs.hasNext() && !connected){
						V next = bfs.next();
						if (nodesOfInterest.contains(next)){
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
	public HashMap<V, Double> getNodeWeight() {
		return nodeWeight;
	}

	/**
	 * Sets the node weight map.
	 *
	 * @param nodeWeight the node weight map
	 */
	public void setNodeWeight(HashMap<V, Double> nodeWeight) {
		this.nodeWeight = nodeWeight;
	}

}

