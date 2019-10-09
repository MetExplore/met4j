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
package fr.inra.toulouse.metexplore.met4j_graph.computation.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;




/**
 * Graph Filtering utils.
 * @author clement
 */
public class GraphFilter {

	/** The Constant EQUALITY. */
	public static final String EQUALITY = "=";
	
	/** The Constant INEQUALITY. */
	public static final String INEQUALITY = "<>";
	
	/** The Constant GREATER. */
	public static final String GREATER = ">";
	
	/** The Constant LESS. */
	public static final String LESS = "<";
	
	/** The Constant GREATEROREQUAL. */
	public static final String GREATEROREQUAL = ">=";
	
	/** The Constant LESSOREQUAL. */
	public static final String LESSOREQUAL = "<=";
	
	/**
	 * Filter graph's edges according to a weight value and a given operator.
	 * Example : weightFilter(g,0.5,'<') will remove from graph g all edges with a weight below 0.5
	 *
	 * @param g the graph
	 * @param value the value used for filtering
	 * @param operator the operator
	 * @return the number of filtered edge
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int weightFilter(G g, double value, String operator){
		List<E> edgesToRemove = new ArrayList<>();
		switch(operator){
		case EQUALITY:
			for(E e : g.edgeSet()){
				if(g.getEdgeWeight(e)==value){
					edgesToRemove.add(e);
				}
			}
			break;
		case INEQUALITY:
			for(E e : g.edgeSet()){
				if(g.getEdgeWeight(e)!=value){
					edgesToRemove.add(e);
				}
			}
			break;
		case GREATER:
			for(E e : g.edgeSet()){
				if(g.getEdgeWeight(e)>value){
					edgesToRemove.add(e);
				}
			}
			break;
		case LESS:
			for(E e : g.edgeSet()){
				if(g.getEdgeWeight(e)<value){
					edgesToRemove.add(e);
				}
			}
			break;
		case GREATEROREQUAL:
			for(E e : g.edgeSet()){
				if(g.getEdgeWeight(e)>=value){
					edgesToRemove.add(e);
				}
			}
			break;
		case LESSOREQUAL:
			for(E e : g.edgeSet()){
				if(g.getEdgeWeight(e)<=value){
					edgesToRemove.add(e);
				}
			}
			break;
		default :
			System.err.println("unrecoginze filter operator");
			throw (new IllegalArgumentException());
		}
		g.removeAllEdges(edgesToRemove);
		return edgesToRemove.size();
	}
	
	/**
	 * Filter graph's edges according to a score value and a given operator.
	 *
	 * @param g the graph
	 * @param value the value used for filtering
	 * @param operator the operator
	 * @return the number of filtered edge
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int scoreFilter(G g, double value, String operator){
		List<E> edgesToRemove = new ArrayList<>();
		switch(operator){
		case EQUALITY:
			for(E e : g.edgeSet()){
				if(g.getEdgeScore(e)==value){
					edgesToRemove.add(e);
				}
			}
			break;
		case INEQUALITY:
			for(E e : g.edgeSet()){
				if(g.getEdgeScore(e)!=value){
					edgesToRemove.add(e);
				}
			}
			break;
		case GREATER:
			for(E e : g.edgeSet()){
				if(g.getEdgeScore(e)>value){
					edgesToRemove.add(e);
				}
			}
			break;
		case LESS:
			for(E e : g.edgeSet()){
				if(g.getEdgeScore(e)<value){
					edgesToRemove.add(e);
				}
			}
			break;
		case GREATEROREQUAL:
			for(E e : g.edgeSet()){
				if(g.getEdgeScore(e)>=value){
					edgesToRemove.add(e);
				}
			}
			break;
		case LESSOREQUAL:
			for(E e : g.edgeSet()){
				if(g.getEdgeScore(e)<=value){
					edgesToRemove.add(e);
				}
			}
			break;
		default :
			System.err.println("unrecoginze filter operator");
			throw (new IllegalArgumentException());
		}
		g.removeAllEdges(edgesToRemove);
		return edgesToRemove.size();
	}
	
	/**
	 * Filter graph's edges according to a weight rank (in decreasing order) and a given operator.
	 * 
	 * @param g the graph
	 * @param n the rank used for filtering
	 * @param operator the operator
	 * @return the number of filtered edge
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int weightRankFilter(G g, int n, String operator){
		if(n<1) throw new IllegalArgumentException();
		List<Double> weights = new ArrayList<>(new HashSet<>(g.getEdgeWeightMap().values()));
		Collections.sort(weights);
		Collections.reverse(weights);
		return weightFilter(g, weights.get(n-1), operator);
	}
	
	/**
	 *  Filter graph's edges according to a weight percentile and a given operator.
	 *  
	 * @param g the graph
	 * @param p the percentile used for filtering
	 * @param operator the operator
	 * @return the number of filtered edge
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int weightPercentileFilter(G g, double p, String operator){
		DescriptiveStatistics stat = new DescriptiveStatistics();
		for(E edge : g.edgeSet()){
			stat.addValue(g.getEdgeWeight(edge));
		}
		
		double t = stat.getPercentile(p);
		return weightFilter(g, t, operator);
	}
	
	/**
	 * Filter graph's edges according to a score rank (in decreasing order) and a given operator.
	 * 
	 * @param g the graph
	 * @param n the rank used for filtering
	 * @param operator the operator
	 * @return the number of filtered edge
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int scoreRankFilter(G g, int n, String operator){
		List<Double> scores = new ArrayList<>(g.getEdgeScoreMap().values());
		Collections.sort(scores);
		Collections.reverse(scores);
		return scoreFilter(g, scores.get(n), operator);
	}
	
	/**
	 *  Filter graph's edges according to a score percentile and a given operator.
	 *  
	 * @param g the graph
	 * @param p the percentile used for filtering
	 * @param operator the operator
	 * @return the number of filtered edge
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> int scorePercentileFilter(G g, double p, String operator){
		DescriptiveStatistics stat = new DescriptiveStatistics();
		for(E edge : g.edgeSet()){
			stat.addValue(g.getEdgeScore(edge));
		}
		
		double t = stat.getPercentile(p);
		return scoreFilter(g, t, operator);
	}
}
