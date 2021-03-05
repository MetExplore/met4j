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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.GraphWalk;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;


/**
 * The Class used to store path
 * @author clement
 */
public class BioPath<V extends BioEntity,E extends Edge<V>> extends GraphWalk<V,E> implements Comparable<BioPath<V,E>>, Iterable<E>{
	
	/**
	 * Instantiates a new bio path.
	 *
	 * @param graphPath the graph path
	 */
	public BioPath(GraphPath<V, E> graphPath){
		super(graphPath.getGraph(), graphPath.getStartVertex(), graphPath.getEndVertex(), graphPath.getEdgeList(), graphPath.getWeight());
	}
	
	/**
	 * Instantiates a new bio path.
	 *
	 * @param graph the graph
	 * @param startVertex the start vertex
	 * @param endVertex the end vertex
	 * @param edgeList the edge list
	 * @param weight the weight
	 */
	public BioPath(Graph<V, E> graph, V startVertex, V endVertex,
				   List<E> edgeList, double weight) {
		super(graph, startVertex, endVertex, edgeList, weight);
	}
	
	/**
	 * Instantiates a new empty bio path.
	 *
	 * @param graph the graph
	 * @param startVertex the start vertex
	 * @param endVertex the end vertex
	 */
	public BioPath(Graph<V, E> graph, V startVertex, V endVertex) {
		super(graph, startVertex, endVertex, new ArrayList<>(), 0.0);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(BioPath<V,E> o) {
		return (int)Math.signum(this.getWeight()-o.getWeight());
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj) {
		
        // ref equality checking
        if (obj==this) {
            return true;
        }
        // type equality checking
        if (obj instanceof BioPath<?,?>) {
            // attribute value checking
            BioPath<?,?> p = ((BioPath<?,?>) obj);
            if(p.getGraph()== this.getGraph() &&
            		p.getEdgeList()== this.getEdgeList() &&
            		p.getStartVertex()== this.getStartVertex() &&
            		p.getEndVertex()== this.getEndVertex()
            		){
            	return true;
            }else return p.getGraph().equals(this.getGraph()) &&
					p.getEdgeList().equals(this.getEdgeList()) &&
					p.getStartVertex().equals(this.getStartVertex()) &&
					p.getEndVertex().equals(this.getEndVertex()) &&
					p.getWeight() == this.getWeight();
        }
        return false;
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
    public int hashCode() {
		//return Objects.hash(this.getEdgeList().toArray(),this.getGraph(),this.getStartVertex(),this.getEndVertex(),this.getWeight());
		return Objects.hash(this.getGraph(), this.getStartVertex(), this.getEndVertex());
	}
	
	
	/**
	 * Gets the sub-path.
	 *
	 * @param start the start vertex
	 * @param end the end vertex
	 * @return the sub-path
	 */
	public BioPath<V,E> getSubPath(V start, V end){
		if(start.equals(end)) return new BioPath<>(this.getGraph(), start, end, new ArrayList<>(), 0.0);
		boolean keep=false;
		double weight=0.0;
		ArrayList<E> edgesToKepp = new ArrayList<>();
		for(E e : this){
			if(!keep){
				if(e.getV1().equals(start)) keep=true;
			}
			
			if(keep){
				edgesToKepp.add(e);
				weight+= this.getGraph().getEdgeWeight(e);
			}
			
			if(e.getV2().equals(end)) break;
		}
		BioPath<V,E> path = new BioPath<>(this.getGraph(), start, end, edgesToKepp, weight);
		return path;
	}
	
	/**
	 * Append the given path.
	 *
	 * @param p2 the second path
	 * @return the two path
	 */
	public BioPath<V, E> appendPath(GraphPath<V, E> p2){
		if(p2.getStartVertex()!= this.getEndVertex()) throw new IllegalArgumentException("the path to add have to start from the current path's end vertex");
		List<E> edgeList = this.getEdgeList();
		edgeList.addAll(p2.getEdgeList());
		double weight = this.getWeight()+p2.getWeight();
		return new BioPath<>(this.getGraph(), this.getStartVertex(), p2.getEndVertex(), edgeList, weight);
	}
	
	/**
	 * Gets the path length.
	 *
	 * @return the length
	 */
	public int getLength(){
		return this.getEdgeList().size();
	}
	
	@Override
	public String toString(){
		String currentNode = this.getStartVertex().getId();
		String label = currentNode;
		
		for(E edge : this.getEdgeList()){
			
			if(!edge.getV1().getId().equals(currentNode)){
				//undirected case
				String nextNode = edge.getV1().getId();
				label+="<-["+ edge +"]->"+nextNode;
				currentNode=nextNode;
			}else{
				//directed case
				String nextNode = edge.getV2().getId();
				label+="-["+ edge +"]->"+nextNode;
				currentNode=nextNode;
			}
			
		}
		return label;
	}

	@Override
	public Iterator<E> iterator() {
		Iterator<E> it = new Iterator<>() {

			private final ArrayList<E> edgeList = new ArrayList<>(getEdgeList());
			private V currentVertex = getStartVertex();

			@Override
			public boolean hasNext() {
				return !currentVertex.equals(getEndVertex());
			}

			@Override
			public E next() {
				E nextEdge = null;
				for (E edge : edgeList) {
					if (edge.getV1().equals(currentVertex)) {
						nextEdge = edge;
						break;
					}
				}

				if (nextEdge == null) throw new IllegalStateException("Discontinuous path");

				edgeList.remove(nextEdge);
				currentVertex = nextEdge.getV2();
				return nextEdge;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		
		return it;
	}
}
