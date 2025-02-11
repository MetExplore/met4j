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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class FloydWarshall. This class is used to compute all shortest paths in a graph.
 * A light implementation allows to compute only the distance matrix, while another perform node tracking during traversal
 * and allows to compute both the distance matrix and the shortest paths.
 * @param <V> the node type
 * @param <E> the edge type
 * @param <G> the graph type
 * @author lcottret
 * @version $Id: $Id
 */
public class FloydWarshall<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> {
	
	/** The graph. */
	final G g;


	private boolean undirected = false;

	private final BioMatrix matrix;

	private BioMatrix distMatrix;

	/**
	 * Instantiates a new floyd warshall computor.
	 *
	 * @param g the graph
	 */
	public FloydWarshall(G g) {
		this.g=g;
		ComputeAdjacencyMatrix<V,E,G> computor = new ComputeAdjacencyMatrix<>(g,this.undirected);
		computor.parallelEdgeWeightsHandling((a,b)->Math.min(a,b));
		matrix = computor.getadjacencyMatrix();
	}

	/**
	 * Instantiates a new floyd warshall computor.
	 *
	 * @param g the graph
	 * @param  undirected if the graph should be considered undirected
	 */
	public FloydWarshall(G g, Boolean undirected) {
		this.g=g;
		this.undirected=undirected;
		ComputeAdjacencyMatrix<V,E,G> computor = new ComputeAdjacencyMatrix<>(g, undirected);
		computor.parallelEdgeWeightsHandling((a,b)->Math.min(a,b));
		matrix = computor.getadjacencyMatrix();
	}

	/**
	 * Instantiates a new floyd warshall computor.
	 *
	 * @param g the graph
	 * @param  computor the adjacency matrix builder
	 */
	public FloydWarshall(G g, ComputeAdjacencyMatrix<V,E,G> computor) {
		this.g=g;
		this.setUndirected(computor.isUndirected());
		matrix = computor.getadjacencyMatrix();
	}
	
	/**
	 * Gets shortest paths distance between each nodes (if one exists).
	 *
	 * @return the distance matrix
	 */
	public BioMatrix getDistances(){
		if(distMatrix!=null) return distMatrix;
		distMatrix = this.matrix.copy();
		for(int i = 0; i< g.vertexSet().size(); i++){
			for(int j = 0; j< g.vertexSet().size(); j++){
				if(i!=j && distMatrix.get(i, j)==0.0){
					distMatrix.set(i, j, Double.POSITIVE_INFINITY);
				}
			}
		}
		
		for(int k = 0; k< g.vertexSet().size(); k++){
			for(int i = 0; i< g.vertexSet().size(); i++){
				for(int j = 0; j< g.vertexSet().size(); j++){
					
					double ab = distMatrix.get(i, j);
					double ac = distMatrix.get(i, k);
					double cb = distMatrix.get(k, j);
					
					if(ab>(ac+cb)){
						distMatrix.set(i, j, (ac+cb));
					}
				}
			}
		}
		
		return distMatrix;
	}
	
	/**
	 * Gets shortest paths between each nodes (if one exists). Build the distance matrix in the process, and should be called
	 * prior to getDistances() if both are required, in order to avoid redundant computation.
	 * @return the paths
	 */
	public HashMap<String, HashMap<String, BioPath<V, E>>> getPaths(){


		HashMap<Integer,HashMap<Integer,Integer>> next = new HashMap<>();

		this.distMatrix = this.matrix.copy();
		for(int i = 0; i< g.vertexSet().size(); i++){
			next.put(i, new HashMap<>());
			for(int j = 0; j< g.vertexSet().size(); j++){
				if(i!=j){
					if(distMatrix.get(i, j)==0.0){
						distMatrix.set(i, j, Double.POSITIVE_INFINITY);
					}else{
						next.get(i).put(j, j);
					}
				}
				
			}
		}
		
		for(int k = 0; k< g.vertexSet().size(); k++){
			for(int i = 0; i< g.vertexSet().size(); i++){
				for(int j = 0; j< g.vertexSet().size(); j++){
					
					double ab = distMatrix.get(i, j);
					double ac = distMatrix.get(i, k);
					double cb = distMatrix.get(k, j);
					
					if(!Double.isInfinite(ac) && !Double.isInfinite(cb) && ab>(ac+cb)){
						distMatrix.set(i, j, (ac+cb));
						next.get(i).put(j, next.get(i).get(k));
					}
				}
			}
		}
		
		
		HashMap<String,HashMap<String,BioPath<V,E>>> res = new HashMap<>();
		for(Map.Entry<Integer, HashMap<Integer, Integer>> entry : next.entrySet()){
			int i = entry.getKey();
			String iLabel = distMatrix.getRowIndexMap().get(i);
			HashMap<String,BioPath<V,E>> map = new HashMap<>();
			
			for(int j : entry.getValue().keySet()){
				
				String jLabel = distMatrix.getRowIndexMap().get(j);
				List<E> path = new ArrayList<>();
				double w = 0.0;
				
				int k = i;
				while(k!=j){
					String kLabel = distMatrix.getRowIndexMap().get(k);
					V v1 = g.getVertex(kLabel);
					
					k = next.get(k).get(j);
					kLabel = distMatrix.getRowIndexMap().get(k);
					V v2 = g.getVertex(kLabel);
					
					E edge = g.getEdge(v1, v2);
					if(undirected && edge==null){
						edge = g.getEdge(v2, v1);
					}
					path.add(edge);
					w+= g.getEdgeWeight(edge);
				}
				
				map.put(jLabel, new BioPath<>(g, g.getVertex(iLabel), g.getVertex(jLabel), path, w));
			}
			res.put(iLabel, map);
		}
		
		return res;
	}

	public boolean isUndirected() {
		return undirected;
	}

	public void setUndirected(boolean undirected) {
		this.undirected = undirected;
	}

	public void asUndirected() {
		this.setUndirected(true);
	}
	
}
