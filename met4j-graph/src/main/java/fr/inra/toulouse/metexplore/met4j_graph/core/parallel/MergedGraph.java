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

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

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
		super(new MetaEdgeFactory<V,E>());
	}
	
	
	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public GraphFactory<V,MetaEdge<V,E>,MergedGraph<V,E>> getFactory(){
		return new GraphFactory<V, MetaEdge<V,E>, MergedGraph<V,E>>() {
			@Override
			public MergedGraph<V, E> createGraph() {
				return new MergedGraph<>();
			}
		};
	}


	/* (non-Javadoc)
	 * @see parsebionet.computation.graphe.BioGraph#getEdgeFactory()
	 */
	@Override
	public MetaEdgeFactory<V, E> getEdgeFactory() {
		return new MetaEdgeFactory<V, E>();
	}

	/* (non-Javadoc)
	 * @see parsebionet.computation.graphe.BioGraph#copyEdge(parsebionet.computation.graphe.Edge)
	 */
	@Override
	public MetaEdge<V, E> copyEdge(MetaEdge<V, E> edge) {
		MetaEdge<V, E> newEdge = new MetaEdge<V, E>(edge.getV1(), edge.getV2(), edge.getEdgeList());
		return newEdge;
	}


	@Override
	public MetaEdge<V, E> reverseEdge(MetaEdge<V, E> edge) {
		MetaEdge<V, E> reversed = new MetaEdge<V, E>(edge.getV2(), edge.getV1(), edge.getEdgeList());
		return reversed;
	}

}