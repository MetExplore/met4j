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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Provide graph random sampling method on edges or vertex (with or without scope or defined compartment)
 * also provide method to build random transition matrix, keeping graph structure
 *
 * @author clement
 */
public class GraphSampler<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V, E>> {
	
	/** The graph. */
	private G g;

	/**
	 * Instantiates a new graph sampler.
	 *
	 * @param g the graph
	 */
	public GraphSampler(G g) {
		this.g=g;
	}
	
	/**
	 * Gets a random vertex.
	 *
	 * @return the random vertex
	 * @throws java.lang.IllegalArgumentException if any.
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public V getRandomVertex() throws IllegalArgumentException{
		if(g.vertexSet().size()<1){
			throw new IllegalArgumentException("requested sample size greater than population size");
		}
		
		ArrayList<V> vertices = new ArrayList<>(g.vertexSet());
		int rand = new Random().nextInt(vertices.size());
		return vertices.get(rand);
	}
	
//	public HashSet<V> getRandomVertexList(int n){
//		V[] vertices = (V[]) g.vertexSet().toArray();
//		int size = vertices.length;
//		HashSet<V> randomList = new HashSet<V>();
//		Random random = new Random();
//		
//		for(int i=0;i<n;i++){
//			boolean next = false;
//			V choosenOne;
//			while(!next){
//				int rand = random.nextInt(size);
//				choosenOne = vertices[rand];
//				if(!randomList.contains(choosenOne)){
//					randomList.add(choosenOne);
//					next = true;
//				}
//			}
//		}
//		return randomList;
//	}	
	
	/**
	 * Gets a random vertex list.
	 *
	 * @param n the size of the sample
	 * @return the random vertex list
	 * @throws java.lang.IllegalArgumentException if any.
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public HashSet<V> getRandomVertexList(int n) throws IllegalArgumentException{
		if(g.vertexSet().size()<n){
			throw new IllegalArgumentException("requested sample size greater than population size");
		}
		
		ArrayList<V> vertices = new ArrayList<>(g.vertexSet());
		HashSet<V> randomList = new HashSet<>();
		Random random = new Random();
		
		for(int i=0;i<n;i++){
			int rand = random.nextInt(vertices.size());
			randomList.add(vertices.get(rand));
			vertices.remove(rand);
		}
		
		return randomList;
	}

	
	/**
	 * Gets the random vertex list in scope.
	 *
	 * @param n the size of the sample
	 * @param scope the scope
	 * @return the random vertex list in scope
	 * @throws java.lang.IllegalArgumentException if any.
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public HashSet<V> getRandomVertexListinScope(int n, int scope) throws IllegalArgumentException{
		if(g.vertexSet().size()<n){
			throw new IllegalArgumentException("requested sample size greater than population size");
		}
		return getRandomVertexListinScope(n,scope, g.vertexSet());
	}
		
		
		
	private HashSet<V> getRandomVertexListinScope(int n, int scope, Set<V> elements) throws IllegalArgumentException{
		ArrayList<V> vertices = new ArrayList<>(elements);
		HashSet<V> randomList = new HashSet<>();
		Random random = new Random();
		
		//get centroid
		V choosenOne = vertices.get(random.nextInt(vertices.size()));
		randomList.add(choosenOne);
		
		//get scope
		ArrayList<V> verticesInScope = new ArrayList<>();
		ArrayList<V> toCompute = new ArrayList<>();
		toCompute.add(choosenOne);

		for(int i=0;i<scope;i++){
			ArrayList<V> newlyAdded = new ArrayList<>();
			for(V vertex:toCompute){
//				Set<E> edges = g.edgesOf(vertex);
				Set<E> edges = g.outgoingEdgesOf(vertex);
				for(E edge:edges){
//					V neighbor;
//					if(!edge.getV2().equals(vertex)){
//						neighbor=edge.getV2();
//					}else{
//						neighbor=edge.getV1();
//					}
					V neighbor = edge.getV2();
					if(!verticesInScope.contains(neighbor)){
						verticesInScope.add(neighbor);
						newlyAdded.add(neighbor);
					}
				}
			}
			toCompute=newlyAdded;
		}
		
		if(verticesInScope.size()<n){
			Set<V> newElement = new HashSet<>(elements);
			newElement.remove(choosenOne);
			if(newElement.isEmpty()){
				throw new IllegalArgumentException("sample size incompatible with given scope");
			}
			return getRandomVertexListinScope(n, scope, newElement);
		}
		
		//get random in scope
		for(int i=0;i<n-1;i++){
			int rand = random.nextInt(verticesInScope.size());
			randomList.add(verticesInScope.get(rand));
			verticesInScope.remove(rand);
		}
		
		return randomList;
	}
	
	/**
	 * Gets the random edge list.
	 *
	 * @param n the size of the sample
	 * @return the random edge list
	 * @throws java.lang.IllegalArgumentException if any.
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public HashSet<E> getRandomEdgeList(int n) throws IllegalArgumentException{
		if(g.edgeSet().size()<n){
			throw new IllegalArgumentException("requested sample size greater than population size");
		}
		
		ArrayList<E> edges = new ArrayList<>(g.edgeSet());
		HashSet<E> randomList = new HashSet<>();
		Random random = new Random();
		
		for(int i=0;i<n;i++){
			int rand = random.nextInt(edges.size());
			randomList.add(edges.get(rand));
			edges.remove(rand);
		}
		
		return randomList;
	}
	
	/**
	 * Gets the random transition matrix.
	 *
	 * @param adjacencyMatrix the adjacency matrix
	 * @return the random transition matrix
	 */
	public BioMatrix getRandomTransitionMatrix(EjmlMatrix adjacencyMatrix){
		BioMatrix RandTransitionMatrix = new EjmlMatrix(adjacencyMatrix.numRows(), adjacencyMatrix.numCols());
		Random random = new Random();
		
		for(int i=0;i<adjacencyMatrix.numRows();i++){
			
			double rowSum=0.0;
			for(int j=0;j<adjacencyMatrix.numCols();j++){
				if(adjacencyMatrix.get(i, j)==1.0){
					double rand = random.nextDouble();
					RandTransitionMatrix.set(i, j, rand);
					rowSum+=rand;
				}
			}
			
			if(rowSum>0.0){
				for(int j=0;j<RandTransitionMatrix.numCols();j++){
					RandTransitionMatrix.set(i, j, RandTransitionMatrix.get(i, j)/rowSum);
				}
			}
		}
		
		return RandTransitionMatrix;
	}
	
	/**
	 * Sets the graph.
	 *
	 * @param g the new graph
	 */
	public void setGraph(G g){
		this.g=g;
	}
}
