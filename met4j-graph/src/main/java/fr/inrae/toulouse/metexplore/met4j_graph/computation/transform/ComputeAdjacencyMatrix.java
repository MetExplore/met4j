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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.transform;

import java.util.Arrays;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

/**
 * The adjacency Matrix computor.
 * @author clement
 */
public class ComputeAdjacencyMatrix<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V, E>> {
	
	/** The graph. */
	G g;
	
	/** The adjacency matrix. */
	BioMatrix adjacencyMatrix;
	
	/**consider as undirected */
	boolean undirected=false;

	DoubleBinaryOperator l = ((a,b) -> a+b);
	
	/**
	 * Instantiates a new adjacency matrix computor.
	 * The adjacency matrix can be constructed considering the graph as undirected, resulting in a symmetric matrix
	 * @param g the graph
	 * @param undirected if the graph should be consider as undirected
	 */
	public ComputeAdjacencyMatrix(G g, boolean undirected) {
		this.g=g;
		this.undirected=undirected;
        adjacencyMatrix = new EjmlMatrix(g.vertexSet().size(),g.vertexSet().size());
	}
	
	
	/**
	 * Instantiates a new adjacency matrix computor.
	 *
	 * @param g the graph
	 */
	public ComputeAdjacencyMatrix(G g) {
		this.g=g;
        adjacencyMatrix = new EjmlMatrix(g.vertexSet().size(),g.vertexSet().size());
	}
	
	/**
	 * Instantiates a new adjacency matrix computor.
	 *
	 * @param g the graph
	 * @param matrixClass the matrix class
	 * @throws Exception
	 */
	@Deprecated
	public ComputeAdjacencyMatrix(G g, Class<?> matrixClass) throws Exception{
		this.g=g;
		if(!Arrays.asList(matrixClass.getInterfaces()).contains(BioMatrix.class)){
			throw new IllegalArgumentException("Matrix class argument must implements BioMatrix interface");
		}

        adjacencyMatrix = (BioMatrix) matrixClass.getDeclaredConstructor(int.class, int.class).newInstance(g.vertexSet().size(),g.vertexSet().size());

	}

	/**
	 * Builds the adjacency matrix.
	 */
	private void buildadjacencyMatrix(){

		this.adjacencyMatrix = new EjmlMatrix(g.vertexSet().size(),g.vertexSet().size());
		int index = 0;
		Set<V> vertexSet = g.vertexSet();
		
		//affect an index for all vertex
		for (V node : vertexSet){
            adjacencyMatrix.setRowLabel(index, node.getId());
            adjacencyMatrix.setColumnLabel(index, node.getId());
			index++;
		}
		
		//import edge weight as initial transition probability
		for (V node : vertexSet){
			int i = adjacencyMatrix.getRowFromLabel(node.getId());
			for (E edge : g.outgoingEdgesOf(node)){
				int j = adjacencyMatrix.getColumnFromLabel(edge.getV2().getId());
				if(adjacencyMatrix.get(i, j)!=0.0){
					//resolve final weight if exists edges with same source/target
                    adjacencyMatrix.set(i, j, l.applyAsDouble(adjacencyMatrix.get(i, j), g.getEdgeWeight(edge)));
				}else{
                    adjacencyMatrix.set(i, j, g.getEdgeWeight(edge));
				}
				
				if(undirected){
					if(adjacencyMatrix.get(j, i)!=0.0){
						//resolve final weight if exists edges with same source/target
                        adjacencyMatrix.set(j, i, l.applyAsDouble(adjacencyMatrix.get(j, i), g.getEdgeWeight(edge)));
					}else{
                        adjacencyMatrix.set(j, i, g.getEdgeWeight(edge));
					}
				}
			}
		}
		
		return;
	}

	
	/**
	 * Gets the adjacency matrix.
	 *
	 * @return the adjacency matrix
	 */
	public BioMatrix getadjacencyMatrix(){
		this.buildadjacencyMatrix();
		return adjacencyMatrix;
	}

	/**
	 * if the graph should be considered as undirected
	 */
	public void asUndirected() {
		this.undirected = true;
	}

	/**
	 * lambda expression for resolving weight conflicts in case of parallel edges. Default use weight sum
	 * @param l
	 */
	public void parallelEdgeWeightsHandling(DoubleBinaryOperator l) {
		this.l = l;
	}
}
