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
package fr.inra.toulouse.metexplore.met4j_graph.computation.weighting;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * The Class ProbabilityWeightPolicy use to set probability as edge weight (resulting in stochastic network)
 * @author clement
 */
public class ProbabilityWeightPolicy<V extends BioEntity, E extends Edge<V>,G extends BioGraph<V,E>> 
	extends WeightingPolicy<V,E,G>{
	
	/** The weighting policy. */
	WeightingPolicy<V,E,G> wp;
		
	/**
	 * Instantiates a new probability weight policy.
	 */
	public ProbabilityWeightPolicy() {
		this.wp=new DefaultWeightPolicy<V,E,G>();
	}
	
	/**
	 * Instantiates a new probability weight policy.
	 *
	 * @param wp the initial weighting policy
	 */
	public ProbabilityWeightPolicy(WeightingPolicy<V,E,G> wp) {
		this.wp=wp;
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.computation.graphe.WeightingPolicy#setWeight(parsebionet.computation.graphe.G)
	 */
	@Override
	public void setWeight(G g) {
		wp.setWeight(g);
		computeProba(g);

	}
	
	/**
	 * Computes the probability by normalizing edge weight by the sum of all edges outgoing from the same source.
	 *
	 * @param g the graph
	 */
	public void computeProba(G g){
		
		for(V v : g.vertexSet()){
			double sum =0.0;
			for(E e : g.outgoingEdgesOf(v)){
				sum+=g.getEdgeWeight(e);
			}
			if(sum!=0.0){
				for(E e : g.outgoingEdgesOf(v)){
					g.setEdgeWeight(e, g.getEdgeWeight(e)/sum);
				}
			}
		}
		return;
	}
}