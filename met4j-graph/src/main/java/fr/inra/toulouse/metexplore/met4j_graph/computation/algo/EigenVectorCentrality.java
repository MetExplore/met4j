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
import java.util.Map;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.ComputeAdjacencyMatrix;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

/**
 * Class to compute the eigen vector centrality of each vertex in a BioGraph.
 * A subgraph can be extracted from this class by filtering nodes using their centrality
 * PageRank can also be computed by adding a scale factor : from each node, a small probabilty to "jump" to any other node is added
 * This kind of measure can be computed relatively to a given set of nodes.
 * @author clement
 *
 */
public class EigenVectorCentrality<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V ,E>> {
	
	/** The adjacency matrix. */
	public BioMatrix adjacencyMatrix;
	
	
	/**
	 * Instantiates a new eigen vector centrality computor.
	 *
	 * @param g the graph
	 */
	public EigenVectorCentrality(G g) {
		ComputeAdjacencyMatrix<V,E,G> adjC = new ComputeAdjacencyMatrix<>(g);
		adjacencyMatrix =adjC.getadjacencyMatrix();
	}
	
	
	/**
	 * Instantiates a new eigen vector centrality computor.
	 *
	 * @param adjacencyMatrix the adjacency matrix
	 * @param labelMap the label map
	 * @param indexMap the index map
	 * @throws Exception
	 */
	public EigenVectorCentrality(BioMatrix adjacencyMatrix) throws Exception{
		if(adjacencyMatrix.numRows()!=adjacencyMatrix.numCols()) throw new IllegalArgumentException("adjacency matrix must be square");
		this.adjacencyMatrix=adjacencyMatrix;
	}
	
	/**
	 * Gets the principal eigen vector from adjacency matrix
	 *
	 * @return the principal eigen vector
	 */
	private double[] getPrincipalEigenVector(){
		BioMatrix eigenVector = adjacencyMatrix.getPrincipalEigenVector();
		return (eigenVector.transpose()).toDoubleArray()[0];
	}
	
	
	/**
	 * Normalize eigen vector.
	 *
	 * @param eigenVector the eigen vector
	 * @return the normalized eigen vector
	 */
	private double[] normalizeEigenVector(double[] eigenVector){
		double[] normalizedEigenVector = new double[eigenVector.length];
		double sum = 0;
		for(double e : eigenVector){
			sum+=e;
		}
		for(int i=0; i<eigenVector.length; i++){
			normalizedEigenVector[i]=eigenVector[i]/sum;
		}
		return normalizedEigenVector;
		
	}
	
	/**
	 * Gets a map with all nodes id as key and global eigen vector centrality
	 *
	 * @return the map with node identifier and corresponding centrality
	 */
	public HashMap<String, Double> computeEigenVectorCentrality(){	
		HashMap<String, Double> result = new HashMap<>();
		double[] pev = getPrincipalEigenVector();
		pev = normalizeEigenVector(pev);
		for(Map.Entry<Integer,String> indexEntry : adjacencyMatrix.getRowIndexMap().entrySet()){
			result.put(indexEntry.getValue(), pev[indexEntry.getKey()]);
		}
		return result;
	}

	/**
	 * Gets a map with all nodes id as key and global page rank
	 *
	 * @param d the damping factor
	 * @return the map with node identifier and corresponding centrality
	 */
	public HashMap<String, Double> computePageRank(double d){
		BioMatrix tmp = adjacencyMatrix.copy();
		addJumpProb(adjacencyMatrix.getRowLabelMap().keySet(),d);
		HashMap<String, Double> result = computeEigenVectorCentrality();
		adjacencyMatrix = tmp;
		return result;
	}


	/**
	 * add a constant probability to "jump" (i.e. go to another node without necessarily following an edge) to defined set of node
	 * Correspond to the PageRank damping factor when the set of roots include all nodes in graph.
	 *
	 * @param roots the set of jump target node
	 * @param p the probability to jump
	 */
	public void addJumpProb(Set<String> roots, double p) {

		double dp = p / (new Integer(roots.size()).doubleValue());
		Map<Integer, List<Integer>> nonZero = getNonZeroValues();
		List<Integer> cols = new ArrayList<>();
		//get roots index
		for (String node : roots) {
			cols.add(adjacencyMatrix.getColumnFromLabel(node));
		}

		//update transition probability
		for (Map.Entry<Integer,List<Integer>> entry : nonZero.entrySet()){
			int i = entry.getKey();
			List<Integer> nzcols = entry.getValue();

			if(!nzcols.isEmpty()){
				//new transition probability = initial probability x (1 - probability to jump)
				for(Integer j : nzcols){
					double initialProba = adjacencyMatrix.get(i,j);
					adjacencyMatrix.set(i, j, initialProba * (1-p));
				}

				//for columns corresponding to seeds, add the probability of coming from a jump
				for(Integer seed : cols){
					double initialProba = adjacencyMatrix.get(i,seed);
					adjacencyMatrix.set(i, seed, initialProba + dp);
				}
			}
			//sink case
			else{
				// by default, if a vertex has no outgoing edge, the probability to
				// jump to a node of interest is 1
				double dp2 = 1 / (new Integer(roots.size()).doubleValue());
				for(Integer seed : cols){
					adjacencyMatrix.set(i, seed, dp2);
				}
			}

		}

	}


	/**
	 * add a probability to "jump" (i.e. go to another node without necessarily following an edge) to defined set of node
	 * Correspond to the PageRank damping factor when the set of roots include all nodes in graph.
	 * A vector of prior is given, biasing the jump according to the corresponding value. All the vector values as to sum up to 1.
	 *
	 * @param roots the set of jump target node
	 * @param p the probability to jump
	 */
	public void addJumpProb(Map<String,Double> roots, double p) {
		//check sum
		double sum = 0;
		for(Double prior : roots.values()){
			sum+=prior;
		}
		if(Math.abs(sum - 1.0) > 1.0e-9) throw new IllegalArgumentException();


		Map<Integer, List<Integer>> nonZero = getNonZeroValues();

		//update transition probability
		for (Map.Entry<Integer,List<Integer>> entry : nonZero.entrySet()){
			int i = entry.getKey();
			List<Integer> nzcols = entry.getValue();

			if(!nzcols.isEmpty()){
				//new transition probability = initial probability x (1 - probability to jump)
				for(Integer j : nzcols){
					double initialProba = adjacencyMatrix.get(i,j);
					adjacencyMatrix.set(i, j, initialProba * (1-p));
				}

				//for columns corresponding to seeds, add the probability of coming from a jump
				for(Map.Entry<String,Double> seedEntry : roots.entrySet()){
					Integer seedCol = adjacencyMatrix.getColumnFromLabel(seedEntry.getKey());
					double dp = p * seedEntry.getValue();
					double initialProba = adjacencyMatrix.get(i,seedCol);
					adjacencyMatrix.set(i, seedCol, initialProba + dp);
				}
			}
			//sink case
			else{
				// by default, if a vertex has no outgoing edge, the probability to
				// jump to a node of interest is 1
				for(Map.Entry<String,Double> seedEntry : roots.entrySet()){
					Integer seedCol = adjacencyMatrix.getColumnFromLabel(seedEntry.getKey());
					double dp = 1 * seedEntry.getValue();
					double initialProba = adjacencyMatrix.get(i,seedCol);
					adjacencyMatrix.set(i, seedCol, initialProba + dp);
				}
			}

		}
	}


	/**
	 * Gets map with row index as key, array of non-zero-element column index as value
	 *
	 * @return the non zero values
	 */
	private Map<Integer, List<Integer>> getNonZeroValues(){
		Map<Integer, List<Integer>> nonZero = new HashMap<>();
		for(int i = 0; i< adjacencyMatrix.numRows(); i++){
			ArrayList<Integer> colsIndex = new ArrayList<>();
			for(int j = 0; j< adjacencyMatrix.numCols(); j++){
				if(adjacencyMatrix.get(i, j)!=0.0){
					colsIndex.add(j);
				}
			}
			nonZero.put(i, colsIndex);
		}
		return nonZero;
	}

	/**
	 * Extract a subnetwork by removing all node with a eigen vector centrality below a given threshold
	 *
	 * @param g the graph
	 * @param threshold
	 * @param seeds
	 * @param JumpProba the jump probability
	 * @param maxIter the maximum number of iteration
	 * @param tolerance
	 * @return
	 * @return the bio graph
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> void extractEigenVectorCentralitySubNet(G g, double threshold, HashMap<V,Double> seeds, double JumpProba, int maxIter, double tolerance){

		EigenVectorCentrality<V,E,G> evc = new EigenVectorCentrality<>(g);

		//build prior vector
		HashMap<String,Double> priorMap = new HashMap<>();
		HashSet<String> roots = new HashSet<>();
		for(V e: g.vertexSet()){
			if(!seeds.containsKey(e)){
				priorMap.put(e.getId(), 0.0);
//				roots.add(e.getId());
			}else{
				priorMap.put(e.getId(),seeds.get(e));
//
				roots.add(e.getId());
//
			}
		}
		evc.addJumpProb(roots, JumpProba);

		//compute eigen vector centrality
		HashMap<String, Double> evcMap = evc.powerIteration(priorMap, maxIter, tolerance);

		//remove node with centrality below threshold
		for(Map.Entry<String, Double> entry : evcMap.entrySet()){
			if(entry.getValue() <threshold) g.removeVertex(g.getVertex(entry.getKey()));
		}
	}

	/**
	 * Extract a subnetwork by removing all node with a eigen vector centrality below a given threshold
	 * use default parameter jumpProba = 0.02, max number of iteration = 100, tolerance for convergence = 0.000001
	 *
	 * @param g the graph
	 * @param threshold the threshold
	 * @param seeds the seeds
	 * @return
	 * @return the bio graph
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> void extractEigenVectorCentralitySubNet(G g, double threshold, HashMap<V,Double> seeds){
		extractEigenVectorCentralitySubNet(g,threshold,seeds,0.02,100,0.000001);
	}

	/**
	 * Compute centrality using power iteration method to approximate principal eigen vector
	 *
	 * @param seeds the set of node used to defined the relative centrality
	 * @param maxIter the maximum number of iteration, if this number is reached, the result is returned
	 * @param tol the tolerance, if the max delta between two iteration is below this value, the result is returned
	 * @return map with node id as key and eigen vector centrality as value
	 */
	public HashMap<String, Double> powerIteration(HashMap<String, Double> seeds, int maxIter, double tol){
		BioMatrix rank = new EjmlMatrix(1, adjacencyMatrix.numCols());
		for(Map.Entry<String,Integer> entry : adjacencyMatrix.getRowLabelMap().entrySet()){
			String e = entry.getKey();
			int index = entry.getValue();
			rank.set(0, index, seeds.getOrDefault(e, 0.0));
		}

		int i=0;
		double maxDelta = Double.MAX_VALUE;
		while(i<maxIter && maxDelta>tol){
			BioMatrix newRank = rank.mult(adjacencyMatrix);
			maxDelta= getMaxDelta(rank.getRow(0), newRank.getRow(0));
			rank = newRank;
			i++;
		}

		HashMap<String, Double> finalRank = new HashMap<>();
		for(Map.Entry<String,Integer> entry : adjacencyMatrix.getRowLabelMap().entrySet()){
			finalRank.put(entry.getKey(), rank.get(0, entry.getValue()));
		}
		return finalRank;
	}
	
	
	/**
	 * Gets the maximum observed difference between two element with same index in both vectors
	 *
	 * @param rank the rank
	 * @param newRank the new rank
	 * @return the the maximum delta
	 */
	private double getMaxDelta(double[] rank, double[] newRank){
		double maxDelta = Double.MIN_VALUE;
		for(int i=0; i<rank.length; i++){
			double delta = Math.abs(rank[i]-newRank[i]);
			if(delta>maxDelta) maxDelta = delta;
		}
		return maxDelta;
	}
}
