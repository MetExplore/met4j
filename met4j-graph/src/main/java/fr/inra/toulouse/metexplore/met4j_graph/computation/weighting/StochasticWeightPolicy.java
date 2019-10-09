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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

/**
 * The Stochastic weighting policy based on chemical similarity.
 * @author clement
 */
public class StochasticWeightPolicy extends SimilarityWeightPolicy {
	
	/** The reactions to low. */
	private HashSet<String> meanReaction;
	
	/** The reaction weight file. */
	private String reactionWeightFile;
	
	/**
	 * Instantiates a new stochastic weight policy.
	 */
	public StochasticWeightPolicy() {}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.randomWalk.similarity.SimilarityWeightPolicy#setWeight(parsebionet.applications.graphe.CompoundGraph)
	 */
	@Override
	public void setWeight(CompoundGraph g){
		super.setWeight(g);
		if(reactionWeightFile !=null){
			normalizeWeight(reactionWeightFile, g);
		}else if(meanReaction !=null){
			normalizeWeight(meanReaction, g);
		}else{
			normalizeWeight(g);
		}		
	}
	
	/**
	 * Normalize weight.
	 *
	 * @param g the graph
	 */
	public void normalizeWeight(CompoundGraph g){
		this.noStructFilter(g);
		for (BioMetabolite v : g.vertexSet()){
			//double sum = 0;
			Set<ReactionEdge> edgeSet = g.outgoingEdgesOf(v);
			HashMap<String, Double> reactionMap = new HashMap<>();
			if (!edgeSet.isEmpty()){
				//compute edge weight normalized by reaction
				for (ReactionEdge e : edgeSet){
					//sum += g.getEdgeWeight(e);
					String rId = e.toString();
					if (!reactionMap.containsKey(e.toString())){
						reactionMap.put(rId,g.getEdgeWeight(e));
					}else{
						reactionMap.put(rId,reactionMap.get(rId)+g.getEdgeWeight(e));
					}
				}
				//update weight
				for (ReactionEdge e : edgeSet){
					g.setEdgeWeight(e, (g.getEdgeWeight(e)/ reactionMap.get(e.toString()))/ reactionMap.keySet().size());
				}
			}
		}
	}
	
	/**
	 * Normalize weight.
	 *
	 * @param reactionWeight the reaction weight map
	 * @param g the graph
	 */
	public void normalizeWeight(HashMap<String, Double> reactionWeight, CompoundGraph g){
		this.noStructFilter(g);
		for (BioMetabolite v : g.vertexSet()){
			Set<ReactionEdge> edgeSet = g.outgoingEdgesOf(v);
			HashMap<String, Double> reactionMap = new HashMap<>();
			if (!edgeSet.isEmpty()){
				//compute edge weight normalized by reaction
				for (ReactionEdge e : edgeSet){
					String rId = e.toString();
					if (!reactionMap.containsKey(e.toString())){
						reactionMap.put(rId,g.getEdgeWeight(e));
					}else{
						reactionMap.put(rId,reactionMap.get(rId)+g.getEdgeWeight(e));
					}
				}
				
				//get consuming reaction's weight sum
				double reactionWeightSum = 0.0;
				for (String rId : reactionMap.keySet()){
					if(!reactionWeight.containsKey(rId)){
						reactionWeight.put(rId,1.0);
					}
					reactionWeightSum += reactionWeight.get(rId);				
				}
				
				//update weight
				for (ReactionEdge e : edgeSet){
					double pR = reactionWeight.get(e.toString())/reactionWeightSum;
					g.setEdgeWeight(e, (g.getEdgeWeight(e)/ reactionMap.get(e.toString()))*pR);
				}
			}
		}
	}
	
	/**
	 * Normalize weight.
	 *
	 * @param file the path to weight file
	 * @param g the graph
	 */
	public void normalizeWeight(String file, CompoundGraph g){
		this.noStructFilter(g);
		HashMap<String,Double> reactionWeight;
		try {
			reactionWeight = new HashMap<>();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;

			while((line = br.readLine())!= null){
				String[] splitLine = line.split("\t");
				double weight = Double.parseDouble(splitLine[1]);
				assert g.getBiochemicalReactionList().containsKey(splitLine[0]);
				reactionWeight.put(splitLine[0], weight);
			}
			br.close();

			this.normalizeWeight(reactionWeight,g);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Normalize weight.
	 *
	 * @param meanReaction the reaction to low
	 * @param g the graph
	 */
	public void normalizeWeight(HashSet<String> meanReaction, CompoundGraph g){
		this.noStructFilter(g);
		int numberOfReactionSum = 0;
		for (BioMetabolite v : g.vertexSet()){
			Set<ReactionEdge> edgeSet = g.outgoingEdgesOf(v);
			HashSet<String> reactionSet = new HashSet<>();
			if (!edgeSet.isEmpty()){
				//compute edge weight normalized by reaction
				for (ReactionEdge e : edgeSet){
					String rId = e.toString();
					reactionSet.add(rId);
				}
			}
			numberOfReactionSum += reactionSet.size();
		}
		double meanNumberOfReaction = (double)numberOfReactionSum/ g.vertexSet().size();
		
		HashMap<String, Double> map = new HashMap<>();
		for(String reaction:meanReaction){
			assert g.getBiochemicalReactionList().containsKey(reaction);
			map.put(reaction, 1.0/meanNumberOfReaction);
		}
		normalizeWeight(map,g);
	}
	
	/**
	 * Adds the reactions to low.
	 *
	 * @param meanReaction the reactions to low
	 */
	public void addMeanReaction(HashSet<String> meanReaction){
		this.meanReaction=meanReaction;
	}
	
	/**
	 * Adds the weight file path.
	 *
	 * @param reactionWeightFile the reaction weight file
	 */
	public void addWeightFile(String reactionWeightFile){
		this.reactionWeightFile=reactionWeightFile;
	}
}
