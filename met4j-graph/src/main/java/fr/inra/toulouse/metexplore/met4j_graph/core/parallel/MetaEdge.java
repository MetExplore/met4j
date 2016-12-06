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
package fr.inra.toulouse.metexplore.met4j_graph.core.parallel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * Edges that represent a set of sub-edges
 * It can be used to store the merging of edges sharing same source and target
 * @author clement
 * @param <V> the vertex Type
 * @param <E> the sub-edges Type
 */
public class MetaEdge<V extends BioEntity,E extends Edge<V>> extends Edge<V> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4274083588408838186L;

	/** The reaction map. */
	Set<E> mergedEdges;
	
	/**
	 * Instantiates a new merged edge.
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 * @param mergedEdges the set of mergedEdges
	 */
	public MetaEdge(V v1, V v2, Set<E> mergedEdges) {
		super(v1, v2);
		this.mergedEdges = mergedEdges;
	}
	
	/**
	 * Instantiates a new merged edge.
	 * @param v1 the first vertex
	 * @param v2 the second vertex
	 */
	public MetaEdge(V v1, V v2) {
		super(v1, v2);
		this.mergedEdges = new HashSet<E>();
	}
	
	/**
	 * Gets the reaction list.
	 * @return the merged edges list
	 */
	public Set<E> getEdgeList(){
		return mergedEdges;
	}
	
	/**
	 * Adds reactions.
	 * @param edges the merged edges list
	 */
	public void addEdges(Collection<E> edges){
		for(E e : edges){
			this.addEdge(e);
		}
	}
	
	/**
	 * Adds one merged edge.
	 * @param e the edges
	 */
	public void addEdge(E e){
		mergedEdges.add(e);
	}
	
	/**
	 * Removes reactions.
	 * @param rList the merged edges list
	 */
	public void removeEdges(Collection<E> edges){
		for(E e : edges){
			this.removeEdge(e);
		}
	}
	
	/**
	 * Removes one merged edge.
	 * @param e the edge
	 */
	public void removeEdge(E e){
		mergedEdges.remove(e);
	}

}
