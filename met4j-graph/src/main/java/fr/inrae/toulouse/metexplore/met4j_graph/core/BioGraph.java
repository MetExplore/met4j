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
package fr.inrae.toulouse.metexplore.met4j_graph.core;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import java.util.*;

/**
 * The Class BioGraph.
 *
 * @author clement
 * @version $Id: $Id
 */
public abstract class BioGraph<V extends BioEntity, E extends Edge<V>> extends DirectedWeightedMultigraph<V, E>{
	
	private static final long serialVersionUID = 1L;
	private String name = "MetabolicGraph";

	public BioGraph(){
		super(null, null);
		super.setEdgeSupplier(this::createEdge);
		super.setVertexSupplier(this::createVertex);
	}
	
	/**
	 * Gets vertex from id.
	 *
	 * @param bioEntityId the bio entity identifier
	 * @return the vertex
	 */
	public final V getVertex(String bioEntityId){
		for(V v : this.vertexSet()){
			if(v.getId().equals(bioEntityId)){
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Checks if the graph contains a vertex with the given identifier.
	 *
	 * @param bioEntityId the bio entity identifier
	 * @return true, if successful
	 */
	public final boolean hasVertex(String bioEntityId){
		for(V v : this.vertexSet()){
			if(v.getId().equalsIgnoreCase(bioEntityId)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes all isolated nodes.
	 */
	public final void removeIsolatedNodes(){
		ArrayList<V> vertToRemove = new ArrayList<>();
		for (V n: this.vertexSet()){
			if(this.edgesOf(n).isEmpty()){
				vertToRemove.add(n);
			}
		}
		System.err.println(vertToRemove.size()+" isolated nodes removed ");
		this.removeAllVertices(vertToRemove);
	}
	
	
//	/**
//	 * Extract sub graph from edge list.
//	 *
//	 * @param path the path
//	 * @return the bio graph
//	 */
//	public Graph<V,E> extractSubGraphFromEdgeList(List<E> path){
//		Graph<V,E> subGraph = newInstance();
//		LinkedList<E> edges = new LinkedList<E>(subGraph.edgeSet());
//		edges.removeAll(path);
//		subGraph.removeAllEdges(edges);
//		subGraph.removeIsolatedNodes();
//		return subGraph;
//	}

	/**
	 * Gets the neighbor list of a given vertex.
	 *
	 * @param vertex the vertex
	 * @return the vertex neighbor list
	 */
	public final Set<V> neighborListOf(V vertex){
		return new HashSet<>(Graphs.neighborListOf(this, vertex));
	}
	
	/**
	 * Gets the predecessor list of a given vertex.
	 *
	 * @param vertex the vertex
	 * @return the vertex predecessor list
	 */
	public final Set<V> predecessorListOf(V vertex){
		return new HashSet<>(Graphs.predecessorListOf(this, vertex));
	}
	
	/**
	 * Gets the successor list of a given vertex.
	 *
	 * @param vertex the vertex
	 * @return the vertex successor list
	 */
	public final Set<V> successorListOf(V vertex){
		return new HashSet<>(Graphs.successorListOf(this, vertex));
	}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#addEdge(java.lang.Object, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public E addEdge(V arg0, V arg1) {
		E e = this.createEdge(arg0, arg1);
		if(this.addEdge(arg0, arg1,e)) return e;
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public boolean addEdge(V sourceVertex, V targetVertex, E e) {
		return super.addEdge(sourceVertex, targetVertex, e);
	}

	public boolean addEdge(E e) {
		return super.addEdge(e.getV1(), e.getV2(), e);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#addVertex(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public boolean addVertex(V v) {
		return super.addVertex(v);
	}

	public boolean addVertex(String id) {
		return super.addVertex(this.createVertex(id));
	}

	
	/**
	 * add a path to the graph
	 *
	 * @param path the path to add
	 */
	public void addPath(BioPath<V,E> path){
		for(E e : path.getEdgeList()){
			if(!this.containsVertex(e.getV1())) this.addVertex(e.getV1());
			if(!this.containsVertex(e.getV2())) this.addVertex(e.getV2());
			if(!this.containsEdge(e)) this.addEdge(e.getV1(), e.getV2(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#containsEdge(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public  final boolean containsEdge(E e1) {
		for(E e2 : this.edgeSet()){
			if(e1.equals(e2)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return true if their exist an edge with the first vertex as source and the second one as target, false otherwise
	 *
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @return if v1 and v2 are connected
	 */
	public final boolean areConnected(V v1, V v2) {
		if(!this.containsVertex(v1) || !this.containsVertex(v2)) return false;
		return this.successorListOf(v1).contains(v2);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#containsVertex(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final boolean containsVertex(V v) {
		return super.containsVertex(v);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#degreeOf(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final int degreeOf(V vertex) {
		return super.inDegreeOf(vertex)+super.outDegreeOf(vertex);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#edgeSet()
	 */
	/** {@inheritDoc} */
	@Override
	public final Set<E> edgeSet() {
		return super.edgeSet();
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#edgesOf(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final Set<E> edgesOf(V vertex) {
		return super.edgesOf(vertex);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#getAllEdges(java.lang.Object, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final Set<E> getAllEdges(V sourceVertex, V targetVertex) {
		return super.getAllEdges(sourceVertex, targetVertex);
	}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#getAllEdges(java.lang.Object, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final E getEdge(V sourceVertex, V targetVertex) {
		Set<E> allEdges = getAllEdges(sourceVertex, targetVertex);
		if(allEdges==null || allEdges.isEmpty()) return null;
		
		E edgeWithMinWeight = allEdges.iterator().next(); //if edges with NaN weight, return at least one edge
		if(allEdges.size()==1) return edgeWithMinWeight;
		
		double minWeight = Double.MAX_VALUE;
		for(E edge : allEdges){
			double weight = this.getEdgeWeight(edge);
			if(weight<minWeight){
				minWeight=weight;
				edgeWithMinWeight=edge;
			}
		}
		
		return edgeWithMinWeight;
	}

	
	/**
	 * Gets edge from source, target and associated reaction.
	 *
	 * @param sourceVertex the source vertex
	 * @param targetVertex the target vertex
	 * @return the edge
	 * @param label a {@link java.lang.String} object.
	 */
	public E getEdge(String sourceVertex, String targetVertex, String label) {
		for(E e : this.edgeSet()){
			if(e.getV1().getId().equals(sourceVertex) && e.getV2().getId().equals(targetVertex) && e.toString().equals(label)){
				return e;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#getEdgeSource(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final V getEdgeSource(E e) {
		return super.getEdgeSource(e);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#getEdgeTarget(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final V getEdgeTarget(E e) {
		return super.getEdgeTarget(e);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#getEdgeWeight(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final double getEdgeWeight(E e) {
		return super.getEdgeWeight(e);
	}
	
	/**
	 * Get the edge weights map
	 *
	 * @return the map
	 */
	public final HashMap<E, Double> getEdgeWeightMap() {
		HashMap<E, Double> weightMap = new HashMap<>();
		for(E e : this.edgeSet()){
			weightMap.put(e, this.getEdgeWeight(e));
		}
		return weightMap;
	}
	
	/**
	 * Get the edge score map
	 *
	 * @return the map
	 */
	public final HashMap<E, Double> getEdgeScoreMap() {
		HashMap<E, Double> scoreMap = new HashMap<>();
		for(E e : this.edgeSet()){
			scoreMap.put(e, this.getEdgeScore(e));
		}
		return scoreMap;
	}
	
	/**
	 * Gets the edge score.
	 *
	 * @param e the edge
	 * @return the edge score
	 */
	public final double getEdgeScore(E e) {
		return e.getScore();
	}
	
	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#inDegreeOf(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final int inDegreeOf(V vertex) {
		return super.inDegreeOf(vertex);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#incomingEdgesOf(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final Set<E> incomingEdgesOf(V vertex) {
		return super.incomingEdgesOf(vertex);
	}


	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#outDegreeOf(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final int outDegreeOf(V vertex) {
		return super.outDegreeOf(vertex);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#outgoingEdgesOf(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final Set<E> outgoingEdgesOf(V vertex) {
		return super.outgoingEdgesOf(vertex);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#removeEdge(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final boolean removeEdge(E e) {
		return super.removeEdge(e);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#removeVertex(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final boolean removeVertex(V arg0) {
		return super.removeVertex(arg0);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#setEdgeWeight(java.lang.Object, double)
	 */
	/** {@inheritDoc} */
	@Override
	public final void setEdgeWeight(E e, double weight) {
		super.setEdgeWeight(e, weight);
	}
	
	/**
	 * Sets the edge score.
	 *
	 * @param e the e
	 * @param score the score
	 */
	public final void setEdgeScore(E e, double score) {
		e.setScore(score);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractBaseGraph#vertexSet()
	 */
	/** {@inheritDoc} */
	@Override
	public final Set<V> vertexSet() {
		return super.vertexSet();
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractGraph#removeAllEdges(java.lang.Object, java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
	public final Set<E> removeAllEdges(V sourceVertex, V targetVertex) {
		return super.removeAllEdges(sourceVertex, targetVertex);
	}

	/* (non-Javadoc)
	 * @see org.jgrapht.graph.AbstractGraph#removeAllVertices(java.util.Collection)
	 */
	/** {@inheritDoc} */
	@Override
	public final boolean removeAllVertices(Collection<? extends V> arg0) {
		return super.removeAllVertices(arg0);
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * Create an edge with extra attributes cloned from an existing edge passed as parameter.
	 * @param v1 the source vertex
	 * @param v2 the target vertex
	 * @param edge the template edge which provides other attributes
	 * @return an edge
	 */
	public abstract E createEdgeFromModel(V v1, V v2, E edge);
	public abstract E copyEdge(E edge);
	public abstract V createVertex(String id);
	public V createVertex(){
		return this.createVertex(UUID.randomUUID().toString());
	}
	public abstract E createEdge(V v1, V v2);
	public E createEdge(){
		return createEdge(createVertex(), createVertex());
	}

	
	/**
	 * create an edge e'(x,y) from an existing edge e(y,x)
	 *
	 * @param edge the edge to reverse
	 * @return the reverse edge
	 */
	public E reverseEdge(E edge){
		return createEdgeFromModel(edge.getV2(),edge.getV1(),edge);
	}

	/**
	 * For each edges in the graph, create a copy with reversed source and target (if not existing already).
	 * This makes this directed graph effectively undirected, but with twice the number of edges.
	 * Reversed edges keep the same weight as their origin.
	 */
	public void asUndirected(){
		for(E edge : new HashSet<>(this.edgeSet())){
			E reversedEdge = this.reverseEdge(edge);
			this.addEdge(reversedEdge);
			this.setEdgeWeight(reversedEdge, this.getEdgeWeight(edge));
		}
	}

}
