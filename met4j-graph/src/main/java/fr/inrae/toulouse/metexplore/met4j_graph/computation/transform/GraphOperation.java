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

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * Provide static method to compute some basic set operation on graph such as union or interesct.
 * Result can be returned as sub-network or just the size/order of the expected sub-network
 *
 * @author clement
 * @version $Id: $Id
 */
public class GraphOperation {

	/**
	 * Instantiates a new graph operation.
	 */
	public GraphOperation() {}
	
	/**
	 * compute the intersection of two graph.
	 *
	 * @param g1 the first graph
	 * @param g2 the second graph
	 * @return the interest
	 * @param factory a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory} object.
	 * @param <V> a V object.
	 * @param <E> a E object.
	 * @param <G> a G object.
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> G intersect(G g1, G g2, GraphFactory<V,E,G> factory){
		G i = factory.createGraph();
		for(V v : g1.vertexSet()){
			if(g2.hasVertex(v.getId())) i.addVertex(v);
		}
		for(E e : g1.edgeSet()){
			if(g2.containsEdge(e)) i.addEdge(e.getV1(), e.getV2(), e);
		}
		return i;
	}
	
	/**
	 * compute the union of two graph.
	 *
	 * @param g1 the first graph
	 * @param g2 the second graph
	 * @return the union
	 * @param factory a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory} object.
	 * @param <V> a V object.
	 * @param <E> a E object.
	 * @param <G> a G object.
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> G union(G g1, G g2, GraphFactory<V,E,G> factory){
		G u = factory.createGraph();
		for(V v : g1.vertexSet()){
			u.addVertex(v);
		}
		for(E e : g1.edgeSet()){
			u.addEdge(e.getV1(), e.getV2(), e);
		}
		for(V v : g2.vertexSet()){
			if(!u.hasVertex(v.getId())) u.addVertex(v);
		}
		for(E e : g2.edgeSet()){
			if(!u.containsEdge(e)) u.addEdge(e.getV1(), e.getV2(), e);
		}
		return u;
	}
	
	/**
	 * compute the size of the intersect of two graph.
	 *
	 * @param g1 the first graph
	 * @param g2 the second graph
	 * @return the size
	 * @param <V> a V object.
	 * @param <E> a E object.
	 * @param <G> a G object.
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int intersectSize(G g1, G g2){
		int size=0;
		for(E e : g1.edgeSet()){
			if(g2.containsEdge(e)) size++;
		}
		return size;
	}
	
	/**
	 * compute the size of the union of two graph.
	 *
	 * @param g1 the first graph
	 * @param g2 the second graph
	 * @return the size
	 * @param <V> a V object.
	 * @param <E> a E object.
	 * @param <G> a G object.
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int unionSize(G g1, G g2){
		return g1.edgeSet().size()+g2.edgeSet().size()- intersectSize(g1, g2);
	}
	
	/**
	 * compute the order of the intersect of two graph.
	 *
	 * @param g1 the first graph
	 * @param g2 the second graph
	 * @return the order
	 * @param <G> a G object.
	 */
	public static <G extends BioGraph<? extends BioEntity, ? extends Edge<?>>> int intersectOrder(G g1, G g2){
		int order=0;
		for(BioEntity v : g1.vertexSet()){
			if(g2.hasVertex(v.getId())) order++;
		}
		return order;
	}
	
	/**
	 * compute the order of the union of two graph.
	 * @param <G> the graph class
	 *
	 * @param g1 the first graph
	 * @param g2 the second graph
	 * @return the order
	 */
	public static <G extends BioGraph<? extends BioEntity, ? extends Edge<?>>> int unionOrder(G g1, G g2){
		return g1.vertexSet().size()+g2.vertexSet().size()- intersectOrder(g1, g2);
	}
	
}
