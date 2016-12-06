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

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

/**
 * The Parallel version of RandomWalk computor.
 * @author clement
 */
public class ParallelizedRandomWalk<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> extends RandomWalk<V,E,G>{
	
	
	/**
	 * Instantiates a new parallelized random walk.
	 *
	 * @param g the graph
	 * @param blackNodesSet the node of interest list
	 */
	public ParallelizedRandomWalk(G g, Set<String> blackNodesSet) {
		super(g,blackNodesSet);
	}	
	
	/**
	 * Instantiates a new parallelized random walk.
	 *
	 * @param g the graph
	 * @param nodeweightFile the path to the file containing starting weight table
	 */
	public ParallelizedRandomWalk(G g, String nodeweightFile) {
		super(g,nodeweightFile);
	}
	
	/**
	 * Instantiates a new parallelized random walk.
	 *
	 * @param g the graph
	 * @param nodeWeight the node weight
	 */
	public ParallelizedRandomWalk(G g, HashMap<String, Double> nodeWeight) {
		super(g,nodeWeight);
	}	
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.RandomWalk#computePassageTime()
	 */
	@Override
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
//				sum+=adjancyMatrix.get(index,jadj);
				sum+=adjancyMatrix.get(index,jadj);
			}
			outVector.set(i, 0, (1.0-sum));	
		}
		
		walkList = new HashMap<String,double[]>();
		startProb = new HashMap<String,Double>();
		
		numberOfWalks = new HashMap<String, Double>();
		edgeNumberOfWalks = new EjmlMatrix(adjancyMatrix.numRows(),adjancyMatrix.numCols());
		
		//compute fundamental BioMatrix
//		HashMap<String,Future<FundamentalMatrixResult>> futureList = new HashMap<String,Future<FundamentalMatrixResult>>();
//		List<Future<FundamentalMatrixResult>> futureList = new ArrayList<Future<FundamentalMatrixResult>>();
		
		int nrOfProcessors = Runtime.getRuntime().availableProcessors();
//		int nrOfProcessors = Runtime.getRuntime().availableProcessors() - 1 > 2 ? Runtime.getRuntime().availableProcessors() - 1 : 2;
		System.out.println("using "+nrOfProcessors+" processors");
		System.out.println("max Memory: "+Runtime.getRuntime().maxMemory());
		
		ExecutorService exec = Executors.newFixedThreadPool(nrOfProcessors);
//		ExecutorService exec = new ThreadPoolExecutor(2, nrOfProcessors, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
		
		CompletionService < FundamentalMatrixResult > cservice = new ExecutorCompletionService < FundamentalMatrixResult > (exec);
		BioMatrix identity = (new EjmlMatrix(subQ.numRows()+1,subQ.numCols()+1)).identity();
		for (String x : nodesOfInterest){
			if(!targetNodesOfInterest.contains(x)){
				BioMatrix p = getXabsorbingMatrix(x, adjancyMatrix);
				Callable<FundamentalMatrixResult> task = new FundamentalMatrixComputor(x,p,identity);
//				futureList.add(exec.submit(task));
				cservice.submit(task);
			}
		}
		exec.shutdown();
		
		//compute expected time passage through each node
		//iterate over each starting states
		int progress = 0;
//		for (String x : nodesOfInterest){
//			if(!targetNodesOfInterest.contains(x)){
		for(int task=0; task<(nodesOfInterest.size()-targetNodesOfInterest.size()); task++){
//		for(Future<FundamentalMatrixResult> future : futureList){
				progress++;
				long t0 = System.nanoTime();
				//compute fundamental matrix with x as starting state
				FundamentalMatrixResult fRes = null;
				try {
					fRes = cservice.take().get();
					
					
//					fRes = future.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				String x = fRes.getStartNode();
				BioMatrix n = fRes.getMatrix();
				
				System.err.println("COMPUTING WALKS FROM "+x+" ("+progress+"/"+(nodesOfInterest.size()-targetNodesOfInterest.size())+")");
			
				
				
//				BioMatrix n = getFundamentalMatrix(getXabsorbingMatrix(x, adjancyMatrix));
//				BioMatrix n=null;
//				try {
//					n = futureList.get(x).get();
//					futureList.put(x,null);
//				} catch (InterruptedException | ExecutionException e) {
//					e.printStackTrace();
//				}
//				
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
//				outprob = n.multiply(outVector);
				
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
				startProb.put(x, (1-outprob.get(row, 0))*nodeWeight.get(x));
				
				//nwalks[n.numCols()-1]=1.0;

				walkList.put(x, nwalks);
				
				BioMatrix res = computeEdgesPassageTime(x, nwalks, n, outVector);

				edgeNumberOfWalks=edgeNumberOfWalks.plus(res.scale(startProb.get(x)));
//				for(int i=0;i<res.numRows();i++){
//					for(int j=0;j<res.numCols();j++){
//						res.multiplyEntry(i, j, startProb.get(x));
//					}
//				}
//				edgeNumberOfWalks=edgeNumberOfWalks.add(res);
		
				long t2 = System.nanoTime();
				System.err.println("\tdone. ("+(((t2-t0)/1000000000))+"sec)");
//			}
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
		
		edgeNumberOfWalks.scale(1.0/tot);
//		for(int i=0;i<edgeNumberOfWalks.numRows();i++){
//			for(int j=0;j<edgeNumberOfWalks.numCols();j++){
//				edgeNumberOfWalks.t(i, j, 1.0/tot);
//			}
//		}
		
		return;
	}
	
	
	
	
//	public class FundamentalMatrixComputor implements Callable<FundamentalMatrixResult>{
//		
//		String x;
//		BioMatrix p;
//		BioMatrix i;
//		long t0;
//		long t1;
//		public FundamentalMatrixComputor(String x, BioMatrix p, BioMatrix i){
//			this.i=i;
//			this.x=x;
//			this.p=p;
//		}
//		
//		@Override
//		public FundamentalMatrixResult call() throws Exception {
//			t0 = System.nanoTime();System.out.println("\t"+x+" on "+Thread.currentThread().getName()+": computing...");
//			FundamentalMatrixResult res = new FundamentalMatrixResult(x, this.getFundamentalMatrix(p,i));
//			t1 = System.nanoTime();System.out.println("\t"+x+" on "+Thread.currentThread().getName()+": done ("+(((t1-t0)/1000000000))+"sec)");
//			return res;
//		}
//		
//		//compute fundamental matrix [I-Q]^-1
//		public BioMatrix getFundamentalMatrix(BioMatrix q, BioMatrix i){
////			System.err.print("Computing fundamental BioMatrix...");
////			int size = q.numCols();
////			assert q.numRows() == size;
//			
//			//create identity matrix
//			
////			BioMatrix i = (new BioMatrix(0,0)).identity(size, size);
////			BlockRealMatrix i = (BlockRealMatrix) MatrixUtils.createRealIdentityMatrix(size); 
//			
//			//substract transient matrix to identity matrix and invert resulting matrix
//			i.minusEquals(q);
////			i=i.subtract(q);
//			
////			BioMatrix n = i.inverse();
//			BioMatrix n = i.inverse();
//
//			return n;
//		}
//	}
	
//	public class FundamentalMatrixResult{
//		String x;
//		BioMatrix n;
//		public FundamentalMatrixResult(String x,BioMatrix n){
//			this.x=x;
//			this.n=n;
//		}
//		public String getStartNode(){
//			return x;
//		}
//		public BioMatrix getMatrix(){
//			return n;
//		}
//	}
}

