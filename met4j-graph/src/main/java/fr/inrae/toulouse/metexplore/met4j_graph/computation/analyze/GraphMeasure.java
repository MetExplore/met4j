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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.PathBasedCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.FloydWarshall;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.jgrapht.alg.connectivity.ConnectivityInspector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * compute several measures of the level or connectivity, size or shape of a given graph using lazy builder to avoid redundant calculus
 *
 * @author clement
 * @param <V>
 * @param <E>
 */
public class GraphMeasure<V extends BioEntity, E extends Edge<V>> {

	/** The graph. */
	private final BioGraph<V, E> g;

	/** The number of edges. */
	private double numberOfEdges;

	/** The number of vertex. */
	private final double numberOfVertex;

	/** The number of connected component. */
	private Integer numberOfConnectedComp;

	/** The diameter. */
	private Double diameter;

	/** The length. */
	private Double length;

	/** if the graph is directed */
	private boolean directed = true;


	/**
	 * Instantiates a new graph measure.
	 *
	 * @param g the graph
	 */
	public GraphMeasure(BioGraph<V, E> g) {
		this.g=g;
        this.numberOfEdges = Integer.valueOf(g.edgeSet().size()).doubleValue();
        this.numberOfVertex = Integer.valueOf(g.vertexSet().size()).doubleValue();
	}

	/**
	 * Get list of set of BioMetabolite, each BioMetabolite in a set belong to the same connected component
	 * @param <V> vertex class
	 * @param <E> edge class
	 *
	 * @param g the graph
	 * @return the connected component
	 */
	public static <V extends BioEntity, E extends Edge<V>> List<Set<V>> getConnectedComponents(BioGraph<V,E> g){
		return new ConnectivityInspector<>(g).connectedSets();
	}
	/**
	 * Get a set of all vertices that are in the maximally connected component together with the specified vertex.
	 * @param <V> vertex class
	 * @param <E> edge class
	 *
	 * @param g the graph
	 * @param t the target vertex
	 * @return the set of all vertices maximally connected to the target vertex
	 */
	//public static <V extends BioEntity, E extends Edge<V>,  BioEntity<T>> Set<V>> getConnectedSetOf(BioGraph<V,E> g, Vertex <T>){
	public static <V extends BioEntity, E extends Edge<V>> Set<V> getConnectedSetOf(BioGraph<V,E> g, V t){
		return new ConnectivityInspector<>(g).connectedSetOf(t);
	}

	/**
	 * Get the position of the component (ordered by size) for a given vertex
	 * @param <V> vertex class
	 * @param <E> edge class
	 *
	 * @param g the graph
	 * @param t the target vertex
	 * @return the component containing the vertex and output component ranking in the console.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Set<V> isPartofNComponent(BioGraph<V,E> g, V t){
		//init connectivity inspector
		ConnectivityInspector CI  = new ConnectivityInspector<>(g);
		//Sort the list of components by size
		List<Set<V>> components = CI.connectedSets();
		//sort with lambdafunction
		Comparator<Set<V>> reversedComparator = (s1,s2) -> Integer.valueOf(s2.size()).compareTo(s1.size());
		List<Set<V>> componentsReverseOrder = components.stream().sorted(reversedComparator).collect(Collectors.toList());
		// Collections.sort(components,Comparator.reverseOrder());
		//Get the set of all vertices maximally connected to the target vertex
		Set<V> SetOfT = CI.connectedSetOf(t);
		int counter = 0;
		for(Set s:componentsReverseOrder){
			counter++;
			if (s.containsAll(SetOfT)){
				if (counter == 1){
					System.out.println("Metabolite "+t.getName()+" is part of the "+counter+" st component");
				}else if (counter == 2){
					System.out.println("Metabolite "+t.getName()+" is part of the "+counter+" nd component");
				}else if (counter == 3){
					System.out.println("Metabolite "+t.getName()+" is part of the "+counter+" rd component");
				}else{
					System.out.println("Metabolite "+t.getName()+" is part of the "+counter+" th component");
				}
				return s;
			}
		}
		return null;
	}
	/**
	 * Get the number of edges of the provided component
	 * @param <V> vertex class
	 * @param <E> edge class
	 *
	 * @param g the graph
	 * @param comp the connected component
	 * @return the number of edges in this component
	 */
	public static <V extends BioEntity, E extends Edge<V>> Integer getNumberEdgesOfComponent(BioGraph<V,E> g, Set<V> comp){
		Integer n_edges = 0;
		for(V v1 : comp) {
			for(V v2 : comp) {
				if((v1 != v2) && (g.containsEdge(v1, v2))) {
					n_edges++;
				}
			}
		}
		return n_edges;
	}

	/**
	 * Gets the number of connected component
	 *
	 * @return the number of connected component
	 */
	public int getNumberOfConnectedComponent(){
		if(numberOfConnectedComp !=null) return numberOfConnectedComp;
        numberOfConnectedComp = getConnectedComponents(g).size();
		return numberOfConnectedComp;
	}

	/**
	 * Gets the number of cycle.
	 *
	 * @return the number of cycle
	 */
	public int getNumberOfCycle(){
		int numberOfConnectedComp = getNumberOfConnectedComponent();
		int numberOfEdges = g.edgeSet().size();
		int numberOfVertex = g.vertexSet().size();
		return numberOfEdges-numberOfVertex+numberOfConnectedComp;
	}

	/**
	 * Gets the diameter of the graph, i.e. the maximum length of a shortest path between two node in the graph
	 * If the graph is disconnected, return the longest distance found in any connected component
	 * @return the diameter
	 */
	public double getDiameter(){
		if(diameter !=null) return diameter;
		FloydWarshall distComputor = new FloydWarshall(this.g);
		BioMatrix distM = distComputor.getDistances();

		//  compute distance stats
		DescriptiveStatistics distStats = new DescriptiveStatistics();
		//  gather all elements in matrix, remove infinity
		for(int i=0; i<distM.numRows(); i++){
			for(int j=0; j<distM.numCols(); j++){
				if(i!=j){
					Double d=distM.get(i,j);
					if(!d.equals(Double.POSITIVE_INFINITY)){
						distStats.addValue(d);
					}
				}
			}
		}

		int diameter = (int) distStats.getMax();
		return diameter;
	}

	/**
	 * Gets the gamma index of the graph, i.e. the ratio between the observed number of edges and the expected maximal number of possible edge, as measure of the level of connectivity
	 *
	 * @return the gamma
	 */
	public double getGamma(){
		double maxNumberOfEdges = numberOfVertex *(numberOfVertex -1);
		if(!directed) maxNumberOfEdges = maxNumberOfEdges*0.5;
		return numberOfEdges /maxNumberOfEdges;
	}

	/**
	 * Gets the alpha index of the graph, i.e. the ratio between the observed number of cycle and the expected maximal number of possible cycle, as measure of the level of connectivity
	 * cannot be computed on directed graph
	 * alpha = (e-(v-1))/((v(v-1)/2)-(v-1))
	 *
	 * @return the alpha
	 */
	public double getAlpha(){
		if(directed) throw new IllegalArgumentException("unable to compute alpha index on directed graph"); //TODO  get the undirected number of edges, counting a->b and b->a as only one edge
		double maxNumberOfCycle = (numberOfVertex *(numberOfVertex -1))*0.5 - (numberOfVertex -1);
		return (numberOfEdges -(numberOfVertex -1))/maxNumberOfCycle;
	}

	/**
	 * Gets the beta index of the graph, i.e. the ratio between the number of edges and the number of vertex as measure of the level of connectivity
	 *
	 * @return the beta
	 */
	public double getBeta(){
		return numberOfEdges / numberOfVertex;
	}

	/**
	 * Gets the total length of the graph, i.e. the sum of each edge weight
	 *
	 * @return the length
	 */
	public double getLength(){
		if(this.length !=null) return this.length;
        length =0.0;
		for(E e : g.edgeSet()){
            length += g.getEdgeWeight(e);
		}
		return length;
	}

	/**
	 * Gets the eta index, i.e. the mean length of edges.
	 *
	 * @return the eta
	 */
	public double getEta(){
		return getLength()/ numberOfEdges;
	}

	/**
	 * Gets the pi index, i.e. the ratio between the diameter of the graph and the total graph length.
	 *
	 * @return the pi
	 */
	public double getPi(){
		return getDiameter()/ getLength();
	}

	/**
	 * get the overall closeness centralization index (OCCI), according to Freeman,L.C. (1979) Centrality in social networks: Conceptual clarification. Social Networks, 1, 215–239.
	 *
	 * OCCI = [ (2n-3) sum_x(C*-Cx) ] / [(n-1)(n-2)]
	 * where n is the number of nodes in the network, C∗ is the highest value of closeness centrality and Cx the closeness centrality for the node x
	 *
	 * @return the OCCI
	 */
	public double getOCCI(){
		PathBasedCentrality<V, E, BioGraph<V, E>> centralityComputor = new PathBasedCentrality<>(g);
		Set<BioPath<V, E>> paths = centralityComputor.getAllShortestPaths();
		Map<V, Double> closenessIndex = centralityComputor.getInCloseness(paths);

		double max = 0.0;
		for(Double closeness : closenessIndex.values()){
			if(closeness>max) max=closeness;
		}

		double sum = 0.0;
		for(Double closeness : closenessIndex.values()){
			sum += (max - closeness);
		}
		//normalize centrality
		sum = sum*(g.vertexSet().size()-1);

		double occi = (2* numberOfVertex - 3) * sum;
		occi = occi/((numberOfVertex -1)*(numberOfVertex -2));
		return occi;
	}

	/**
	 * get the overall closeness centralization index (OCCI), according to Freeman,L.C. (1979) Centrality in social networks: Conceptual clarification. Social Networks, 1, 215–239.
	 *
	 * OCCI = [ (2n-3) sum_x(C*-Cx) ] / [(n-1)(n-2)]
	 * where n is the number of nodes in the network, C∗ is the highest value of closeness centrality and Cx the closeness centrality for the node x
	 *
	 * @return the OCCI
	 * @param validPaths a {@link java.util.Set} object.
	 */
	public double getOCCI(Set<BioPath<V, E>> validPaths){
		PathBasedCentrality<V, E, BioGraph<V, E>> centralityComputor = new PathBasedCentrality<>(g);
		Map<V, Double> closenessIndex = centralityComputor.getCloseness(validPaths);

		double max = 0.0;
		for(Double closeness : closenessIndex.values()){
			if(closeness>max) max=closeness;
		}

		double sum = 0.0;
		for(Double closeness : closenessIndex.values()){
			sum += (max - closeness);
		}

		double occi = (2* numberOfVertex - 3) * sum;
		occi = (occi / ((numberOfVertex -1)*(numberOfVertex -2)));

		return occi;
	}

	/**
	 * adjust the edge count for multigraph. Edges having the same source and target will be counted as one edge.
	 */
	public void adjustEdgeCountForMultiGraph(){
		HashSet<String> links = new HashSet<>();
		for(E edge : g.edgeSet()){
			links.add(edge.getV1().getId()+"_"+edge.getV2().getId());
		}
        this.numberOfEdges =links.size();
	}

	/**
	 * get whether or not the graph is considered as directed
	 *
	 * @return true if the graph is considered as directed, false otherwise
	 */
	public boolean isDirected() {
		return directed;
	}

	/**
	 * set whether or not the graph should be considered as directed
	 * @param directed if the network should be considered as directed
	 */
	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	/**
	 * <p>Getter for the field <code>numberOfEdges</code>.</p>
	 *
	 * @return the number of edges
	 */
	public double getNumberOfEdges() {
		return numberOfEdges;
	}

	/**
	 * <p>Getter for the field <code>numberOfVertex</code>.</p>
	 *
	 * @return the number of vertices
	 */
	public double getNumberOfVertex() {
		return numberOfVertex;
	}
}
