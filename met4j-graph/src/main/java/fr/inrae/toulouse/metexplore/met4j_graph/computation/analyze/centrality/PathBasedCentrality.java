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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;

/**
 * The Class used to compute several centrality measure and other classical vertex' global measures
 *
 * @author clement
 * @param <V> the vertex type
 * @param <E> the edge type
 * @param <G> the graph type
 * @version $Id: $Id
 */
public class PathBasedCentrality<V extends BioEntity,E extends Edge<V>, G extends BioGraph<V, E>> {

	/** The graph. */
	final G g;
	
	/** The shortest paths. */
	private Set<BioPath<V,E>> allShortestPaths;
	
	
	/**
	 * Instantiates a new graph centrality measure.
	 *
	 * @param g the graph
	 */
	public PathBasedCentrality(G g) {
		this.g=g;
	}
	
	/**
	 * Gets the number of paths passing through a given node.
	 * The betweenness according to its formal definition can be obtained by dividing this number by the total number of paths
	 *
	 * @param allPaths the paths set
	 * @return the betweenness
	 */
	public Map<V, Integer> getBetweenness(Set<BioPath<V,E>> allPaths){
		Map<V, Integer>  passingThrough = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			passingThrough.put(vertex, 0);
		}
		
		
		for(BioPath<V,E> path : allPaths){
			for(V vertex : path.getVertexList()){
				if(!vertex.equals(path.getStartVertex()) && !vertex.equals(path.getEndVertex())){
					int nbOfPath = passingThrough.get(vertex);
					nbOfPath+=1;
					passingThrough.put(vertex, nbOfPath);
				}
			}
		}
		return passingThrough;
	}
	
	/**
	 * Gets the geodesic betweenness.
	 *
	 * @return the geodesic betweenness
	 */
	public Map<V, Integer> getGeodesicBetweenness(){
		return getBetweenness(this.getAllShortestPaths());
	}
	
	/**
	 * Gets the neighborhood centrality.
	 * This reflect the number of nodes that can be reached by a given node.
	 *
	 * @param allPaths the paths set
	 * @return the neighborhood centrality
	 */
	public Map<V, Integer> getNeighborhoodCentrality(Set<BioPath<V,E>> allPaths){
		Map<V, Integer>  centrality = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			centrality.put(vertex, 0);
		}
		
		for(BioPath<V,E> path : allPaths){
			V vertex = path.getStartVertex();
			int nbOfPath = centrality.get(vertex);
			nbOfPath+=1;
			centrality.put(vertex, nbOfPath);
		}
		return centrality;
	}
	
	/**
	 * Gets the geodesic neighborhood centrality.
	 *
	 * @return the geodesic neighborhood centrality
	 */
	public Map<V, Integer> getGeodesicNeighborhoodCentrality(){
		return getNeighborhoodCentrality(this.getAllShortestPaths());
	}
	
	/**
	 * Gets the farness.
	 * The farness of a node is the sum of its distances from all other nodes
	 *
	 * @param allPaths the paths set
	 * @return the farness
	 */
	public Map<V, Double> getFarness(Set<BioPath<V,E>> allPaths){
		Map<V, Double>  farness = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			farness.put(vertex, Double.POSITIVE_INFINITY);
		}
		
		for(BioPath<V,E> path : allPaths){
			V vertex = path.getEndVertex();
			double dist = farness.get(vertex);
			if(dist==Double.POSITIVE_INFINITY) dist=0.0;
			farness.put(vertex, dist+path.getWeight());
		}
		return farness;
	}
	
	/**
	 * Gets the closeness.
	 * The closeness of a node is the sum of reciprocal of its distances from all other nodes.
	 * By convention, in a graph that is not strongly connected, the reciprocal of distance between unconnected nodes is 0.
	 *
	 * @param allPaths the paths set
	 * @return the closeness
	 */
	public Map<V, Double> getCloseness(Set<BioPath<V,E>> allPaths){
		Map<V, Double>  closeness = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			closeness.put(vertex, 0.0);
		}
		
		for(BioPath<V,E> path : allPaths){
			V vertex1 = path.getEndVertex();
			double dist1 = closeness.get(vertex1);
			closeness.put(vertex1, dist1+path.getWeight());
			
			V vertex2 = path.getStartVertex();
			double dist2 = closeness.get(vertex2);
			closeness.put(vertex2, dist2+path.getWeight());
		}


		closeness.replaceAll((k, v) -> (1.0 / v));
		
//		//STRONGLY CONNECTED GRAPHS CASE
//		Map<V, Double>  farness = getFarness(allPaths);
//		for(V vertex : farness.keySet()){
//			closeness.put(vertex, 1.0/farness.get(vertex));
//		}
		return closeness;
	}

	/**
	 * Gets the closeness.
	 * The closeness of a node is the sum of reciprocal of its distances from all other nodes.
	 * By convention, in a graph that is not strongly connected, the reciprocal of distance between unconnected nodes is 0.
	 *
	 * @param distanceMatrix the distance matrix
	 * @return the closeness
	 */
	public Map<V, Double> getCloseness(BioMatrix distanceMatrix){
		if(distanceMatrix.numRows()!=g.vertexSet().size()) throw new IllegalArgumentException("distance matrix size does not fit graph order");
		if(distanceMatrix.numCols()!=g.vertexSet().size()) throw new IllegalArgumentException("distance matrix size does not fit graph order");
		Map<V, Double>  closeness = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			double cc = 0.0;
			for(Double d : distanceMatrix.getRow(distanceMatrix.getRowFromLabel(vertex.getId()))){
				if(Double.isFinite(d)){
					cc+=d;
				}
			}
			for(Double d : distanceMatrix.getCol(distanceMatrix.getColumnFromLabel(vertex.getId()))){
				if(Double.isFinite(d)){
					cc+=d;
				}
			}
			closeness.put(vertex, 1.0/cc);
		}

		return closeness;
	}
	
	/**
	 * <p>getInCloseness.</p>
	 *
	 * @param allPaths a {@link java.util.Set} object.
	 * @return a {@link java.util.Map} object.
	 */
	public Map<V, Double> getInCloseness(Set<BioPath<V,E>> allPaths){
		Map<V, Double>  closeness = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			closeness.put(vertex, 0.0);
		}
		
		for(BioPath<V,E> path : allPaths){
			V vertex = path.getEndVertex();
			double dist = closeness.get(vertex);
			closeness.put(vertex, dist+path.getWeight());
		}

		closeness.replaceAll((k, v) -> (1.0 / v));
		
		return closeness;
	}
	
	/**
	 * <p>getOutCloseness.</p>
	 *
	 * @param allPaths a {@link java.util.Set} object.
	 * @return a {@link java.util.Map} object.
	 */
	public Map<V, Double> getOutCloseness(Set<BioPath<V,E>> allPaths){
		Map<V, Double>  closeness = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			closeness.put(vertex, 0.0);
		}
		
		for(BioPath<V,E> path : allPaths){
			V vertex = path.getStartVertex();
			double dist = closeness.get(vertex);
			closeness.put(vertex, dist+path.getWeight());
		}

		closeness.replaceAll((k, v) -> (1.0 / v));
		
		return closeness;
	}
	
	
	
	/**
	 * <p>getGeodesicCloseness.</p>
	 *
	 * @return a {@link java.util.Map} object.
	 */
	public Map<V, Double> getGeodesicCloseness(){
		return getCloseness(this.getAllShortestPaths());
	}
	
	/**
	 * Gets the eccentricity.
	 * The eccentricity of a vertex is the largest distance that exist between this vertex and another one.
	 *
	 * @param allPaths the paths set
	 * @return the eccentricity
	 */
	public Map<V, Double> getEccentricity(Set<BioPath<V,E>> allPaths){
		Map<V, Double>  eccentricity = new HashMap<>();
		//Instantiate the map;
		for(V vertex : g.vertexSet()){
			eccentricity.put(vertex, Double.NEGATIVE_INFINITY);
		}
		
		for(BioPath<V,E> path : allPaths){
			V vertex = path.getEndVertex();
			double dist = eccentricity.get(vertex);
			if(dist<path.getWeight()){
				eccentricity.put(vertex, path.getWeight());
			}
		}
		
		for(Map.Entry<V, Double> entry : eccentricity.entrySet()){
			if(entry.getValue() ==Double.NEGATIVE_INFINITY){
				eccentricity.put(entry.getKey(), Double.POSITIVE_INFINITY);
			}
		}
		return eccentricity;
	}

	/**
	 * <p>Getter for the field <code>allShortestPaths</code>.</p>
	 *
	 * @return the allShortestPaths
	 */
	public Set<BioPath<V, E>> getAllShortestPaths() {
		if(this.allShortestPaths ==null){
			ShortestPath<V, E, G> pathComputor = new ShortestPath<>(g);
            allShortestPaths = new HashSet<>(pathComputor.getAllShortestPaths());
		}
		return allShortestPaths;
	}
	
}
