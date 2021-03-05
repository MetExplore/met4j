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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.weighting;

import java.util.HashMap;
import java.util.Map;

import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;


/**
 * This class extends the {@link ProbabilityWeightPolicy} used to normalize weights into probabilities.
 * This class holds a special case for compound graphs, by adding a reaction-probability level.
 * Using the {@link ProbabilityWeightPolicy} on compound graphs tends to over-weighting reactions that have many products
 * The method provided by this class allow to reduce this bias, by considering all reactions equally.
 * Custom reaction probabilities can be used by setting reactions weights.
 * @author clement
 */
public class ReactionProbabilityWeight extends ProbabilityWeightPolicy<BioMetabolite, ReactionEdge, CompoundGraph>{
	
	
	Map<BioReaction,Double> reactionWeights;
	
	public ReactionProbabilityWeight() {
    }
	public ReactionProbabilityWeight(WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> wp) {
		super(wp);
	}
	public ReactionProbabilityWeight(Map<BioReaction,Double> reactionWeights) {
        this.reactionWeights=reactionWeights;
	}
	public ReactionProbabilityWeight(WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> wp, Map<BioReaction,Double> reactionWeights) {
		super(wp);
		this.reactionWeights=reactionWeights;
	}
	
	
	/* (non-Javadoc)
	 * @see parsebionet.computation.graphe.weighting.ProbabilityWeightPolicy#computeProba(parsebionet.computation.graphe.BioGraph)
	 */
	@Override
	public void computeProba(CompoundGraph g){
		
		for(BioMetabolite v : g.vertexSet()){
			
			//get edge weight sum for each bio-chemical reaction consuming the node
			Map<BioReaction,Double> sumMap = new HashMap<>();
			
			for(ReactionEdge e : g.outgoingEdgesOf(v)){
				BioReaction reaction = e.getReaction();
				
				if(!sumMap.containsKey(reaction)){
					sumMap.put(reaction, g.getEdgeWeight(e));
				}else{
					sumMap.put(reaction, sumMap.get(reaction)+g.getEdgeWeight(e));
				}
			
			}
			
			//compute reactions probabilities
			Map<BioReaction,Double> reactionsProbabilities = new HashMap<>();
			double nbOfConsumingReaction = sumMap.size();
			if(reactionWeights ==null){
				//if no reaction weights set, 
				for(BioReaction r : sumMap.keySet()){
					reactionsProbabilities.put(r, 1.0/nbOfConsumingReaction);
				}
			}else{
				double reactionWeightSum = 0.0;
				for(BioReaction r : sumMap.keySet()){
					reactionWeightSum+= reactionWeights.get(r);
				}
				
				for(BioReaction r : sumMap.keySet()){
					double reactionWeight = reactionWeights.get(r);
					reactionsProbabilities.put(r, reactionWeight/reactionWeightSum);
				}
			}
			
			
			//update weights
			if(!sumMap.isEmpty()){
				for(ReactionEdge e : g.outgoingEdgesOf(v)){
					BioReaction r = e.getReaction();
					double weight = g.getEdgeWeight(e);
					double weightSum = sumMap.get(r);
					double reactionProba = reactionsProbabilities.get(r);
					g.setEdgeWeight(e, (weight/weightSum)*reactionProba);
				}
			}
		}
    }


	/**
	 * @return the reaction-weight map
	 */
	public Map<BioReaction, Double> getReactionWeights() {
		return reactionWeights;
	}


	/**
	 * @param reactionWeights the reaction-weight map to set
	 */
	public void setReactionWeights(Map<BioReaction, Double> reactionWeights) {
		this.reactionWeights = reactionWeights;
	}
}
