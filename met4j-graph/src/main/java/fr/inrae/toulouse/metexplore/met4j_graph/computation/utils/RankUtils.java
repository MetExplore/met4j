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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.utils;

import java.io.Serializable;
import java.util.*;

import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;


/**
 * provide static method to build reaction ranking from graph edges' score, and measure distance/dissimilarity between two ranking
 * @author clement
 */
public class RankUtils {
	
	/**
	 * Instantiates a new compare ranking.
	 */
	public RankUtils() {}
	
	/**
	 * compute the kendall Tau coefficient between 2 ranking.
	 * i.e. the number of discordant pairs
	 *
	 * @param r1 the first ranking
	 * @param r2 the second ranking
	 * @return the kendall Tau coefficient
	 */
	public static double kendallTau(int[] r1, int[] r2){
		if(r1.length!=r2.length){
			System.err.println("rankings must have same number of elements");
			return Double.NaN;
		}
		double kendall=0;
		for(int i=0; i<r1.length; i++){
			for(int j=i+1; j<r1.length; j++){
				if((r1[i]<r1[j] && r2[i]>r2[j]) || (r1[i]>r1[j] && r2[i]<r2[j])){
					kendall++;
				}
			}
		}
		
		// normalize the kendall coeff by the total number of unordered pairs, i.e. the maximal number of discordant pairs
		// k=0 : same ranking, k=1 : inverted ranking
		kendall = (kendall/ ((r1.length * (r1.length - 1)) / 2));
		return kendall;
	}
		
	/**
	 * compute the Spearman's rank correlation coefficient
	 *
	 * @param r1 the first ranking
	 * @param r2 the second ranking
	 * @return the Spearman rank coefficient
	 */
	public static double SpearmanRankCoeff(int[] r1, int[] r2){
		if(r1.length!=r2.length){
			System.err.println("rankings must have same number of elements");
			return Double.NaN;
		}
		
		double distSum = 0;
		for(int i=0; i<r1.length; i++){
			distSum+= StrictMath.pow(r1[i]-r2[i], 2);
		}
		
		// p = 1 - 6*Sum(d²)/n(n²-1)
		double spearCoeff = 1 - ((6*distSum) / (r1.length*(StrictMath.pow(r1.length, 2)-1)));
		return spearCoeff;
	}
	
	
	/**
	 * Computes reaction ranks from edges score.
	 *
	 * @param g the graph
	 * @return the hash map
	 */
	public static LinkedHashMap<String, Integer> computeRank(CompoundGraph g){
		LinkedHashMap<String, Double> scoreMap = new LinkedHashMap<>();
		for(ReactionEdge e : g.edgeSet()){
			String reactionId = e.getReaction().getId();
			if(scoreMap.containsKey(reactionId)){
				scoreMap.put(reactionId, scoreMap.get(reactionId)+g.getEdgeScore(e));
			}else{
				scoreMap.put(reactionId,g.getEdgeScore(e));
			}
		}
		return computeRank(scoreMap);
	}
	
	
	
	/**
	 * Computes ranks from hashmap with entity identifier as key and score as value.
	 * @param <T> the class of object to be ranked
	 *
	 * @param map the score map
	 * @return the rank map
	 */
	public static <T> LinkedHashMap<T, Integer> computeRank(Map<T, Double> map){
		LinkedHashMap<T, Integer> reactionRankMap = new LinkedHashMap<>();
		
		List<T> reactions = getOrderedList(map);
		for(T rId : reactions){
			reactionRankMap.put(rId, reactions.indexOf(rId));
		}
		return reactionRankMap;
	}


	
	/**
	 * Return an ordered list of values, given a map with score associated to each value
	 * @param <T> the class of object to be ranked
	 *
	 * @param map the score map
	 * @return the ordered list of keys
	 */
	public static <T> List<T> getOrderedList(Map<T, Double> map){
		ArrayList<T> keys = new ArrayList<>(map.keySet());
		keys.sort(new ScoreComparator<>(map));
		return keys;
	}
	
	
	/**
	 * compute the kendall Tau coefficient between 2 score maps.
	 * i.e. the number of discordant pairs
	 *
	 * @param r1 the first ranking
	 * @param r2 the second ranking
	 * @return the kendall Tau coefficient
	 */
	public static double kendallTau(Map<String, Integer> r1, Map<String, Integer> r2){
		if(!r1.keySet().equals(r2.keySet())){
			System.err.println("rankings must involve same set of elements");
			return Double.NaN;
		}
		int i=0;
		int[] r1array = new int[r1.size()];
		int[] r2array = new int[r1.size()];
		for(Map.Entry<String, Integer> entry : r1.entrySet()){
			r1array[i]= entry.getValue();
			r2array[i]=r2.get(entry.getKey());
			i++;
		}
		return kendallTau(r1array,r2array);
	}
	
	/**
	 * compute the Spearman rank correlation coefficient  between 2 score maps.
	 *
	 * @param r1 the first ranking
	 * @param r2 the second ranking
	 * @return the Spearman rank coefficient
	 */
	public static double SpearmanRankCoeff(Map<String, Integer> r1, Map<String, Integer> r2){
		if(!r1.keySet().equals(r2.keySet())){
			System.err.println("rankings must involve same set of elements");
			return Double.NaN;
		}
		int i=0;
		int[] r1array = new int[r1.size()];
		int[] r2array = new int[r1.size()];
		for(Map.Entry<String, Integer> entry : r1.entrySet()){
			r1array[i]= entry.getValue();
			r2array[i]=r2.get(entry.getKey());
			i++;
		}
		return SpearmanRankCoeff(r1array,r2array);
	}
	
	/**
	 * The Class ScoreComparator.
	 * @param <T>
	 */
	static class ScoreComparator<T> implements Comparator<T>, Serializable {
		
		/** The reaction score map. */
		final Map<T, Double> scoreMap;
		
		/**
		 * Instantiates a new score comparator.
		 *
		 * @param scoreMap the reaction score map
		 */
		public ScoreComparator(Map<T, Double> scoreMap){
			this.scoreMap=scoreMap;
		}
		
		/* (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(T o1, T o2) {
			return -Double.compare(scoreMap.get(o1), scoreMap.get(o2));
		}
		
	}
}
