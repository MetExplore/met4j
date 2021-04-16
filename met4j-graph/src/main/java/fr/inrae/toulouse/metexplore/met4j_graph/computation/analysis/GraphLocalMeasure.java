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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

/**
 * compute several measures of the proximity or relatedness of two nodes
 *
 * @author clement
 * @version $Id: $Id
 */
public class GraphLocalMeasure<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> {
	
	/** The graph. */
	private final G g;
	
	/**
	 * Instantiates a new graph local measure.
	 *
	 * @param g the graph
	 */
	public GraphLocalMeasure(G g) {
		this.g=g;
	}
	
	/**
	 * Compute the number of common neighbors of two nodes
	 *
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @return the number of common neighbors
	 */
	public double getCommonNeighbor(V v1, V v2){
		double commonNeighbor = 0.0;
		Set<V> neighbors1 = g.neighborListOf(v1);
		Set<V> neighbors2 = g.neighborListOf(v2);
		for(V n : neighbors1){
			if(neighbors2.contains(n)) commonNeighbor++;
		}
		return commonNeighbor;
	}
	
	/**
	 * Compute the Adamic-Adar index for two nodes.
	 * The Adamic-Adar index is a measure of proximity of nodes, it correspond to the number of common neighbor lowered by the degree of each neighbor
	 *
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @return the Adamic-Adar index
	 */
	public double getAdamicAdar(V v1, V v2){
		double aaIndex = 0.0;
		Set<V> neighbors1 = g.neighborListOf(v1);
		Set<V> neighbors2 = g.neighborListOf(v2);
		for(V n : neighbors1){
			if(neighbors2.contains(n)){
				aaIndex+=(1/ StrictMath.log10(g.degreeOf(n)));
			}
		}
		return aaIndex;
	}
	
	/**
	 * Compute the Salton index
	 *
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @return the Salton index
	 */
	public double getSaltonIndex(V v1, V v2){
		double commonNeighbor = this.getCommonNeighbor(v1, v2);
		double salton = commonNeighbor/Math.sqrt(g.neighborListOf(v1).size()* g.neighborListOf(v2).size());
		//note that using degree here can cause false results if graph is directed, and one neighbor is connected through both incoming and outgoing edges
		return salton;
	}
	
	/**
	 * Compute the local clustering coefficient, which is for a vertex the number of edges between vertices of its neighborhood
	 * divided by the expected maximum number of edges that could exist between them (if the neighborhood was a clique)
	 *
	 * @param v1 the vertex
	 * @return a double.
	 */
	public double getLocalClusteringCoeff(V v1){
		ArrayList<V> neighbors = new ArrayList<>(g.neighborListOf(v1));
		double numberOfNeighbors = neighbors.size();
		if(numberOfNeighbors==1 || numberOfNeighbors==0) return 0;
		double connectedNeighbors = 0;
		for(V n1 : neighbors){
			for(V n2 : neighbors){
				if(n1!=n2 && g.areConnected(n1, n2)) connectedNeighbors++;
			}
		}
		
		double clusteringCoeff = connectedNeighbors/(numberOfNeighbors*(numberOfNeighbors-1));
		return clusteringCoeff;
	}
	
	/**
	 * Compute the local clustering coefficient, which is for a vertex the number of edges between vertices of its neighborhood
	 * divided by the expected maximum number of edges that could exist between them (if the neighborhood was a clique)
	 *
	 * @param v1 the vertex
	 * @return a double.
	 */
	public double getUndirectedLocalClusteringCoeff(V v1){
		ArrayList<V> neighbors = new ArrayList<>(g.neighborListOf(v1));
		double numberOfNeighbors = neighbors.size();
		if(numberOfNeighbors==1 || numberOfNeighbors==0) return 0;
		double connectedNeighbors = 0;
		for(int i=0; i<neighbors.size(); i++){
			V n1 = neighbors.get(i);
			for(int j=i; j<neighbors.size(); j++){
				V n2 = neighbors.get(j);
				if(n1!=n2 && (g.areConnected(n1, n2) || g.areConnected(n2, n1))) connectedNeighbors++;
			}
		}
		
		
		double clusteringCoeff = (2*connectedNeighbors)/(numberOfNeighbors*(numberOfNeighbors-1));
		return clusteringCoeff;
	}	

	
	/**
	 * Compute the Katz index
	 * C = ((I_alphaA^T)^-1 - I)
	 *
	 * @param alpha a double.
	 * @return the Katz index
	 */
	public Map<V,Double> getKatzIndex(double alpha){
		ComputeAdjacencyMatrix<V,E,G> adjComputor = new ComputeAdjacencyMatrix<>(g);
		BioMatrix adj = adjComputor.getadjacencyMatrix();
		BioMatrix i = adj.identity();
		BioMatrix factor1 = (i.minus(adj.transpose().scale(alpha))).invert().minus(i);
		BioMatrix identityVector = new EjmlMatrix(1,adj.numCols());
		for( int j=0;j<identityVector.numCols();j++){
			identityVector.set(0, j, 1.0);
		}
		BioMatrix katzVector = factor1.minus(identityVector);
		
		
		HashMap<V, Double> res = new HashMap<>();
		HashMap<Integer, String> index = adjComputor.getIndexMap();
		
		for( int j=0;j<katzVector.numCols();j++){
			V node = g.getVertex(index.get(j));
			res.put(node, katzVector.get(0, j));
		}
		
		return res;
	}
}
