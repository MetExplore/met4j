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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;


/**
 * Compute similarity measure for two graph.
 *
 * @author clement
 * @version $Id: $Id
 */
public class GraphSimilarity<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V, E>> {
	
	private final G g1;
	private final G g2;
	private Integer sharedLink;
	
	/**
	 * Instantiates a new graph similarity.
	 *
	 * @param g1 a G object.
	 * @param g2 a G object.
	 */
	public GraphSimilarity(G g1, G g2) {
		this.g1=g1;
		this.g2=g2;
	}
	
	/**
	 * get the number of shared links between the two graph,
	 * meaning the number of shared edges without taking edge label nor associated reaction into account
	 *
	 * @return the number of sharedLink
	 */
	public int getNumberOfSharedLinks(){
		if(sharedLink !=null) return sharedLink;
        sharedLink = 0;
		for(E e : g1.edgeSet()){
			if(g2.areConnected(e.getV1(), e.getV2())) sharedLink++;
		}
		return sharedLink;
	}
	
	/**
	 * compute the Tanimoto similarity coefficient
	 * also known as Jaccard index
	 * sim(A,B) = c/(a+b-c) = |A n B|/|A u B|
	 * with a and b the number of edges in graph A and B, and c the number of shared links.
	 * Consider edges with same source and target as a single edge.
	 *
	 * @return the Tanimoto coefficient
	 */
	public double getTanimoto(){
		G g1 = (G) this.g1.clone();
		G g2 = (G) this.g2.clone();
		EdgeMerger.mergeEdges(g1);
		EdgeMerger.mergeEdges(g2);
		double a = Integer.valueOf(g1.edgeSet().size()).doubleValue();
		double b = Integer.valueOf(g2.edgeSet().size()).doubleValue();
		double c = Integer.valueOf(getNumberOfSharedLinks()).doubleValue();
		double sim = c/(a+b-c);
		return sim;
	}

}
