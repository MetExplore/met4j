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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting;

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;

/**
 * Class used to perform mathematical operation and IO methods on weights in graph
 *
 * @author clement
 * @version $Id: $Id
 */
public class WeightUtils {

	/**
	 * Instantiates a new weight utils.
	 */
	public WeightUtils() {
	}

	/**
	 * Apply a function on every edge weight
	 * @param g the graph
	 * @param lambda a function that takes an edge weight (Double) and produce a Double
	 * @param <E> Edge type
	 * @param <G> Graph type
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void process(G g, DoubleFunction<Double> lambda){
		for(E e : g.edgeSet()){
			double w = g.getEdgeWeight(e);
			g.setEdgeWeight(e, lambda.apply(w));
		}
	}
	
	/**
	 * Scale weights between 0 and 1.
	 *
	 * @param g the graph
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void scale(G g){
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for(E e : g.edgeSet()){
			double w = g.getEdgeWeight(e);
			if(w<min) min = w;
			if(w>max) max = w;
		}
		if(min==max){
			for(E e : g.edgeSet()){
				g.setEdgeWeight(e,0.5);
			}
			return;
		}
		
		for(E e : g.edgeSet()){
			double w = g.getEdgeWeight(e);
			g.setEdgeWeight(e,(w-min)/(max-min));
		}
    }
	
	/**
	 * Put all weights to the power of the given number.
	 *
	 * @param g the graph
	 * @param pow the pow
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void pow(G g, int pow){
		for(E e : g.edgeSet()){
			double w = g.getEdgeWeight(e);
			g.setEdgeWeight(e, StrictMath.pow(w, pow));
		}
    }
	
	/**
	 * Invert weights (weights have to be between 0 and 1, which can be done using the {@link WeightUtils#scale(BioGraph)})
	 *
	 * @param g the graph
	 * @throws java.lang.IllegalArgumentException the illegal argument exception
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void invert(G g) throws IllegalArgumentException{
		for(E e : g.edgeSet()){
			double w = g.getEdgeWeight(e);
			if(w>1 || w<0) throw (new IllegalArgumentException("weights have to be between 0 and 1 : "+e.getV1().getId()+" -> "+e.getV2().getId()+" ("+ e +") "+g.getEdgeWeight(e)));
			g.setEdgeWeight(e, 1.0-w);
		}
    }
	
	/**
	 * add a given value to all weights in the graph
	 *
	 * @param g the graph
	 * @param n the number to add
	 * @throws java.lang.IllegalArgumentException the illegal argument exception
	 * @throws java.lang.IllegalArgumentException if any.
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void add(G g, double n) throws IllegalArgumentException{
		for(E e : g.edgeSet()){
			double w = g.getEdgeWeight(e);
			if(Double.isNaN(w)) throw (new IllegalArgumentException("NaN weight"));
			g.setEdgeWeight(e, n+w);
		}
    }
	
	/**
	 * Removes the edge with an invalid weight (Not a number).
	 *
	 * @param g the graph
	 * @return the number of removed edge
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> int removeEdgeWithNaNWeight(G g){
		List<E> edgesToRemove = new ArrayList<>();
		for(E e : g.edgeSet()){
			if(Double.isNaN(g.getEdgeWeight(e))){
				edgesToRemove.add(e);
			}
		}
		g.removeAllEdges(edgesToRemove);
		return edgesToRemove.size();
	}
	
	/**
	 * Export edge weights to the given path
	 * Format :
	 * tab separated
	 * source vertex id,target vertex id,reaction id,weight
	 *
	 * @param g the graph
	 * @param outputPath the output path
	 * @throws java.io.IOException if any.
	 */
	public static <E extends Edge<?>, G extends BioGraph<?,E>> void export(G g, String outputPath) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(outputPath));
		for(E e : g.edgeSet()){
			StringBuilder entry = new StringBuilder(e.getV1().getId());
			entry.append("\t").append(e.getV2().getId());
			entry.append("\t").append(e);
			entry.append("\t").append(g.getEdgeWeight(e));
			out.write(entry.toString());
			out.newLine();
		}
		out.close();
    }

}
