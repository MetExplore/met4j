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

import java.util.HashMap;
import java.util.Map;

import fr.inra.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;


/**
 * This class extends the {@link ProbabilityWeightPolicy} used to normalize weights into probabilities.
 * This class holds a special case for compound graphs, by adding a reaction-probability level.
 * Using the {@link ProbabilityWeightPolicy} on compound graphs tends to over-weighting reactions that have many products
 * The method provided by this class allow to reduce this bias, by considering all reactions equally.
 * Custom reaction probabilities can be used by setting reactions weights.
 * @author clement
 */
public class ReactionProbabilityWeight extends ProbabilityWeightPolicy<BioPhysicalEntity, ReactionEdge, CompoundGraph>{
	
	
	Map<BioChemicalReaction,Double> reactionWeights;
	
	public ReactionProbabilityWeight() {
		super();
	}
	public ReactionProbabilityWeight(WeightingPolicy<BioPhysicalEntity, ReactionEdge, CompoundGraph> wp) {
		super(wp);
	}
	public ReactionProbabilityWeight(Map<BioChemicalReaction,Double> reactionWeights) {
		super();
		this.reactionWeights=reactionWeights;
	}
	public ReactionProbabilityWeight(WeightingPolicy<BioPhysicalEntity, ReactionEdge, CompoundGraph> wp, Map<BioChemicalReaction,Double> reactionWeights) {
		super(wp);
		this.reactionWeights=reactionWeights;
	}
	
	
	/* (non-Javadoc)
	 * @see parsebionet.computation.graphe.weighting.ProbabilityWeightPolicy#computeProba(parsebionet.computation.graphe.BioGraph)
	 */
	@Override
	public void computeProba(CompoundGraph g){
		
		for(BioPhysicalEntity v : g.vertexSet()){
			
			//get edge weight sum for each bio-chemical reaction consuming the node
			Map<BioChemicalReaction,Double> sumMap = new HashMap<BioChemicalReaction, Double>();
			
			for(ReactionEdge e : g.outgoingEdgesOf(v)){
				BioChemicalReaction reaction = e.getReaction();
				
				if(!sumMap.containsKey(reaction)){
					sumMap.put(reaction, g.getEdgeWeight(e));
				}else{
					sumMap.put(reaction, sumMap.get(reaction)+g.getEdgeWeight(e));
				}
			
			}
			
			//compute reactions probabilities
			Map<BioChemicalReaction,Double> reactionsProbabilities = new HashMap<BioChemicalReaction, Double>();
			double nbOfConsumingReaction = sumMap.size();
			if(reactionWeights==null){
				//if no reaction weights set, 
				for(BioChemicalReaction r : sumMap.keySet()){
					reactionsProbabilities.put(r, 1.0/nbOfConsumingReaction);
				}
			}else{
				double reactionWeightSum = 0.0;
				for(BioChemicalReaction r : sumMap.keySet()){
					reactionWeightSum+=reactionWeights.get(r);
				}
				
				for(BioChemicalReaction r : sumMap.keySet()){
					double reactionWeight = reactionWeights.get(r);
					reactionsProbabilities.put(r, reactionWeight/reactionWeightSum);
				}
			}
			
			
			//update weights
			if(!sumMap.isEmpty()){
				for(ReactionEdge e : g.outgoingEdgesOf(v)){
					BioChemicalReaction r = e.getReaction();
					double weight = g.getEdgeWeight(e);
					double weightSum = sumMap.get(r);
					double reactionProba = reactionsProbabilities.get(r);
					g.setEdgeWeight(e, (weight/weightSum)*reactionProba);
				}
			}
		}
		return;
	}


	/**
	 * @return the reaction-weight map
	 */
	public Map<BioChemicalReaction, Double> getReactionWeights() {
		return reactionWeights;
	}


	/**
	 * @param reactionWeights the reaction-weight map to set
	 */
	public void setReactionWeights(Map<BioChemicalReaction, Double> reactionWeights) {
		this.reactionWeights = reactionWeights;
	}
}