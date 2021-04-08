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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;







import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.KShortestPath;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.GraphCentralityMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * Class to compute load points, i.e. hotspot in metabolic networks.
 * Load points constitute an indicator of lethality and can help identifying drug target 
 * See : Syed Asad Rahman, Dietmar Schomburg; Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinformatics 2006; 22 (14): 1767-1774. doi: 10.1093/bioinformatics/btl181
 * @author clement
 *
 */
public class LoadPoint<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V, E>> {
	
	final G g;
	
	/**
	 * Instantiate load points computor
	 * @param g the network
	 */
	public LoadPoint(G g) {
		this.g=g;
	}
	
	/**
	 * From Rahman et al. Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinf. (2006):
	 * For a given metabolic network, the load L on metabolite m can be defined as :
	 * ln [(pm/km)/(∑Mi=1Pi)/(∑Mi=1Ki)]
	 * p is the number of shortest paths passing through a metabolite m;
	 * k is the number of nearest neighbour links for m in the network;
	 * P is the total number of shortest paths;
	 * K is the sum of links in the metabolic network of M metabolites (where M is the number of metabolites in the network).
	 * Use of the logarithm makes the relevant values more distinguishable.
	 * @param k number of shortest paths to consider
	 * @return loads values map
	 */
	public HashMap<V, Double> getLoads(int k){
		HashMap<V, Double> loadsMap = new HashMap<>();
		
		Set<BioPath<V,E>> paths = (new KShortestPath<>(g)).getAllShortestPaths(k);
		Map<V, Integer> numberOfPathPassingThrough = (new GraphCentralityMeasure<>(g)).getBetweenness(paths);
		double degreeSum = getDegreeSum();
		double totalNbOfSp = paths.size();
		
		double averageLoad = totalNbOfSp/degreeSum;
		
		for(V vertex: g.vertexSet()){
			double nbOfPath = numberOfPathPassingThrough.get(vertex).doubleValue();
			double degree = g.degreeOf(vertex);
			double load = nbOfPath/degree;
			load = load/averageLoad;
			load = StrictMath.log(load);
			loadsMap.put(vertex, load);
		}
		
		return loadsMap;
	}
	
	private int getDegreeSum(){
		int degreeSum=0;
		
		for(V vertex : g.vertexSet()){
			degreeSum+= g.degreeOf(vertex);
		}
		
		return degreeSum;
	}
	
}
