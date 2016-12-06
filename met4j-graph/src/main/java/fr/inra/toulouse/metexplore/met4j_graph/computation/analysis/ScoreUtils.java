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
package fr.inra.toulouse.metexplore.met4j_graph.computation.analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

/**
 * A class providing several method to manipulate edge score
 */
public class ScoreUtils {

	/**
	 * Instantiates a new score utils.
	 */
	public ScoreUtils() {
	}
	
	/**
	 * Gets the score by reaction.
	 *
	 * @param g the graph
	 * @return the score by reaction
	 */
	public static HashMap<String, Double> getScoreByReaction(CompoundGraph g){
		HashMap<String, Double> reactionScoreMap = new HashMap<String, Double>();
		for(ReactionEdge e : g.edgeSet()){
			String reactionId = e.getReaction().getId();
			if(reactionScoreMap.containsKey(reactionId)){
				reactionScoreMap.put(reactionId, reactionScoreMap.get(reactionId)+g.getEdgeScore(e));
			}else{
				reactionScoreMap.put(reactionId,g.getEdgeScore(e));
			}
		}
		return reactionScoreMap;
	}
	
	/**
	 * Gets the score by gene.
	 *
	 * @param g the graph
	 * @return the score by gene
	 */
	public static HashMap<String, Double> getScoreByGene(CompoundGraph g){
		HashMap<String, Double> geneScoreMap = new HashMap<String, Double>();
		for(ReactionEdge e : g.edgeSet()){
			Set<String> geneIds = e.getReaction().getListOfGenes().keySet();
			for(String geneId : geneIds){
				if(geneScoreMap.containsKey(geneId)){
					geneScoreMap.put(geneId, geneScoreMap.get(geneId)+g.getEdgeScore(e));
				}else{
					geneScoreMap.put(geneId,g.getEdgeScore(e));
				}
			}
		}
		return geneScoreMap;
	}
	
	/**
	 * Normalize score.
	 *
	 * @param g the graph
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void normalizeScore(G g){
		double maxScore = 0.0;
		for(E e : g.edgeSet()){
			double score = g.getEdgeScore(e);
			if(score>maxScore) maxScore = score;
		}
		
		for(E e : g.edgeSet()){
			double score = g.getEdgeScore(e);
			g.setEdgeScore(e, score/maxScore);
		}
		return;
	}
	
	/**
	 * Import scores.
	 *
	 * @param g the graph
	 * @param filePath the score file path
	 * @param edgeIdColumn the edge id column number
	 * @param scoreColumn the score column number
	 * @param sep the separator character
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void importScores(
			G g, String filePath, int edgeIdColumn, int scoreColumn, String sep){
		HashSet<E> seenEdges = new HashSet<E>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String inputLine;
			int n = 0;
			while ((inputLine = in.readLine()) != null){
				n++;
				String[] splitedLine = inputLine.split(sep);
				if(scoreColumn<splitedLine.length && edgeIdColumn<splitedLine.length){
					String edgeId = splitedLine[edgeIdColumn];
					try{
						Double score = Double.parseDouble(splitedLine[scoreColumn]);
						String[] splitedEgeId = edgeId.split(" ");
						if(splitedEgeId.length==3){
							E edge = g.getEdge(splitedEgeId[0], splitedEgeId[2], splitedEgeId[1]);
							if(edge==null){
								System.err.println("Unable to find edge "+splitedEgeId[0]+" -> "
										+splitedEgeId[2]+" [label "+splitedEgeId[1]+"] in graph (line "+n+")");
								
							}else{
								g.setEdgeScore(edge, score);
								seenEdges.add(edge);
							}
						}else{
							System.err.println("bad edge id format : "+splitedLine[edgeIdColumn]+" line "+n);
							System.err.println("Should be : source_id edge_id target_id");
						}
						
					}catch(NumberFormatException nfe){
						System.err.println("bad score format : "+splitedLine[scoreColumn]+" line "+n);
						nfe.printStackTrace();
					}
					
				}else{
					System.err.println("bad input line format : line "+n);
				}
				
			}
			in.close();
		} catch (IOException e) {
			System.err.println("error while reading file");
			e.printStackTrace();
		}
		System.err.println(seenEdges.size()+" score set among "+g.edgeSet().size()+" edges in graph");
	}
	
	/**
	 * Normalize score.
	 *
	 * @param scoreMap the score map
	 */
	public static void normalizeScore(HashMap<String, Double> scoreMap){
		double maxScore = 0.0;
		for(String id : scoreMap.keySet()){
			double score = scoreMap.get(id);
			if(score>maxScore) maxScore = score;
		}
		
		for(String id : scoreMap.keySet()){
			double score = scoreMap.get(id);
			scoreMap.put(id, score/maxScore);
		}
		return;
	}
	
}
