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
package fr.inrae.toulouse.metexplore.met4j_graph.core.compressed;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * <p>CompressedGraph class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class CompressedGraph<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> extends BioGraph<V,PathEdge<V,E>> {

	private static final long serialVersionUID = 1L;
	private final G originalGraph;
	
	
	/**
	 * <p>Constructor for CompressedGraph.</p>
	 *
	 * @param originalGraph a G object.
	 */
	public CompressedGraph(G originalGraph) {
		super();
		this.originalGraph=originalGraph;
	}	

	
	/**
	 * <p>getFactory.</p>
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory} object.
	 */
	public GraphFactory<V, PathEdge<V,E>, CompressedGraph<V,E,G>> getFactory(){
		return new GraphFactory<>() {
            @Override
            public CompressedGraph<V, E, G> createGraph() {
                return new CompressedGraph<>(originalGraph);
            }
        };
	}

	/** {@inheritDoc} */
	@Override
	public PathEdge<V, E> copyEdge(PathEdge<V, E> edge) {
		return new PathEdge<>(edge.getV1(), edge.getV2(), edge.getPath());
	}

	@Override
	public V createVertex(String id) {
		V v = null;
		try {
			Constructor<? extends BioEntity> declaredConstructor = null;
			declaredConstructor = this.vertexSet().iterator().next().getClass().getDeclaredConstructor(String.class);
			v = (V) declaredConstructor.newInstance(id);
		} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return v;
	}

	@Override
	public PathEdge<V, E> createEdge(V arg0, V arg1) {
		BioPath<V, E> path = new BioPath<>(this.originalGraph, arg0, arg1, new ArrayList<>(), 0.0);
		PathEdge<V, E> edge = new PathEdge<>(arg0, arg1, path);
		return edge;
	}

	@Override
	public PathEdge<V, E> createEdgeFromModel(V v1, V v2, PathEdge<V, E> edge){
		return new PathEdge<V, E>(v1, v2, edge.getPath());
	}


	/** {@inheritDoc} */
	@Override
	public PathEdge<V, E> reverseEdge(PathEdge<V, E> edge) {
		PathEdge<V, E> reversed = new PathEdge<>(edge.getV2(), edge.getV1(), edge.getPath());
		return reversed;
	}
	
//	public EdgeFactory<V, PathEdge<V,E>> getFactory() {
//		EdgeFactory<V, PathEdge<V,E>> factory = new EdgeFactory<V, PathEdge<V,E>>(Class<? extends V> vertexClass,Class<? extends E> edgeClass) {
//			@Override
//			public PathEdge<V, E> createEdge(V arg0, V arg1) {
//				PathEdge<V, E> edge = new PathEdge<V, E>(arg0,arg1);
//				return edge;
//			}
//		};
//		return factory;
//	}
//		
//	
//	public class BipartiteEdgeFactory{
//		public BipartiteEdgeFactory(){
//			
//		}
//		@Override
//		public PathEdge<V, E> createEdge(V arg0, V arg1) {
//			PathEdge<V, E> edge = new PathEdge<V, E>(arg0,arg1);
//			return edge;
//		}
//		
//	}
}
