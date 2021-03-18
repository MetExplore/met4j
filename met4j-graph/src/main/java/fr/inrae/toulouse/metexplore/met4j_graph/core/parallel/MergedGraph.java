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
package fr.inrae.toulouse.metexplore.met4j_graph.core.parallel;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.UUID;

/**
 * The Class MergedGraph. Type of graph for which each edges contain a collection of sub-edges.
 * It can be used to convert a multigraph to a simple one without loosing information contained by
 * merged parallel edges
 *
 * @param <V> the value type
 * @param <E> the element type
 */
public class MergedGraph<V extends BioEntity, E extends Edge<V>> extends BioGraph<V,MetaEdge<V,E>> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Instantiates a new merged graph.
	 */
	public MergedGraph() {
		super();
	}
	
	
	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public GraphFactory<V,MetaEdge<V,E>,MergedGraph<V,E>> getFactory(){
		return new GraphFactory<>() {
			@Override
			public MergedGraph<V, E> createGraph() {
				return new MergedGraph<>();
			}
		};
	}


	/* (non-Javadoc)
	 * @see parsebionet.computation.graphe.BioGraph#copyEdge(parsebionet.computation.graphe.Edge)
	 */
	@Override
	public MetaEdge<V, E> copyEdge(MetaEdge<V, E> edge) {
		MetaEdge<V, E> newEdge = new MetaEdge<>(edge.getV1(), edge.getV2(), edge.getEdgeList());
		return newEdge;
	}

	@Override
	public V createVertex() {
		V v = null;
		try {
			Constructor<? extends BioEntity> declaredConstructor = null;
			declaredConstructor = this.vertexSet().iterator().next().getClass().getDeclaredConstructor(String.class);
			v = (V) declaredConstructor.newInstance(UUID.randomUUID().toString());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return v;
	}

	@Override
	public MetaEdge<V, E> createEdge(V v1, V v2) {
		return new MetaEdge<V, E>(v1,v2, new HashSet<>());
	}


	@Override
	public MetaEdge<V, E> reverseEdge(MetaEdge<V, E> edge) {
		MetaEdge<V, E> reversed = new MetaEdge<>(edge.getV2(), edge.getV1(), edge.getEdgeList());
		return reversed;
	}
	@Override
	public MetaEdge<V, E> createEdgeFromModel(V v1, V v2, MetaEdge<V, E> edge){
		return new MetaEdge(v1, v2, edge.getEdgeList());
	}

}
