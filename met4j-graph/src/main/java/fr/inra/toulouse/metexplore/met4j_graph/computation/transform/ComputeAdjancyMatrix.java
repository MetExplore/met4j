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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

/**
 * The Adjancy Matrix computor.
 * @author clement
 */
public class ComputeAdjancyMatrix<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V, E>> {
	
	/** The graph. */
	G g;
	
	/** The adjancy matrix. */
	BioMatrix adjancyMatrix;
	
	/**consider as undirected */
	boolean undirected = false;
	
	/**
	 * Instantiates a new adjancy matrix computor.
	 * The adjancy matrix can be constructed considering the graph as undirected, resulting in a symmetric matrix
	 * @param g the graph
	 * @param undirected if the graph should be consider as undirected
	 */
	public ComputeAdjancyMatrix(G g, boolean undirected) {
		this.g=g;
		this.undirected=undirected;
		adjancyMatrix = new EjmlMatrix(g.vertexSet().size(),g.vertexSet().size());
		buildAdjancyMatrix();
	}
	
	
	/**
	 * Instantiates a new adjancy matrix computor.
	 *
	 * @param g the graph
	 */
	public ComputeAdjancyMatrix(G g) {
		this.g=g;
		adjancyMatrix = new EjmlMatrix(g.vertexSet().size(),g.vertexSet().size());
		buildAdjancyMatrix();
	}
	
	/**
	 * Instantiates a new adjancy matrix computor.
	 *
	 * @param g the graph
	 * @param matrixClass the matrix class
	 * @throws Exception
	 */
	public ComputeAdjancyMatrix(G g, Class<?> matrixClass) throws Exception{
		this.g=g;
		if(!Arrays.asList(matrixClass.getInterfaces()).contains(BioMatrix.class)){
			throw new IllegalArgumentException("Matrix class argument must implements BioMatrix interface");
		}

		adjancyMatrix = (BioMatrix) matrixClass.getDeclaredConstructor(int.class, int.class).newInstance(g.vertexSet().size(),g.vertexSet().size());

		buildAdjancyMatrix();
	}
	
	/**
	 * Builds the adjancy matrix.
	 */
	private void buildAdjancyMatrix(){
		
		int index = 0;
		Set<V> vertexSet = g.vertexSet();
		
		//affect an index for all vertex
		for (V node : vertexSet){
			adjancyMatrix.setRowLabel(index, node.getId());
			adjancyMatrix.setColumnLabel(index, node.getId());
			index++;
		}
		
		//import edge weight as initial transition probability
		for (V node : vertexSet){
			int i = adjancyMatrix.getRowFromLabel(node.getId());
			for (E edge : g.outgoingEdgesOf(node)){
				int j = adjancyMatrix.getColumnFromLabel(edge.getV2().getId());
				if(adjancyMatrix.get(i, j)!=0.0){
					//sum weight from edges with same source/target
					adjancyMatrix.set(i, j,adjancyMatrix.get(i, j)+g.getEdgeWeight(edge));
				}else{
					adjancyMatrix.set(i, j,g.getEdgeWeight(edge));
				}
				
				if(undirected){
					if(adjancyMatrix.get(j, i)!=0.0){
						//sum weight from edges with same source/target
						adjancyMatrix.set(j, i,adjancyMatrix.get(i, j)+g.getEdgeWeight(edge));
					}else{
						adjancyMatrix.set(j, i,g.getEdgeWeight(edge));
					}
				}
			}
		}
		
		return;
	}
	
	/**
	 * Gets the adjancy matrix.
	 *
	 * @return the adjancy matrix
	 */
	public BioMatrix getAdjancyMatrix(){
		return adjancyMatrix;
	}
	
	/**
	 * Gets the label map.
	 *
	 * @return the label map
	 */
	public HashMap<String, Integer> getLabelMap(){
		return adjancyMatrix.getRowLabelMap();
	}
	
	/**
	 * Gets the index map.
	 *
	 * @return the index map
	 */
	public HashMap<Integer, String> getIndexMap(){
		return adjancyMatrix.getRowIndexMap();
	}
	

}
