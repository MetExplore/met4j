/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.computation.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;







import fr.inra.toulouse.metexplore.met4j_graph.computation.algo.KShortestPath;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class LoadPoint<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V, E>> {
	
	G g;
	
	public LoadPoint(G g) {
		this.g=g;
	}
	
	public HashMap<V, Double> getLoads(int k){
		HashMap<V, Double> loadsMap = new HashMap<V, Double>();
		
		Set<BioPath<V,E>> paths = (new KShortestPath<V,E,G>(g)).getAllShortestPaths(k);
		Map<V, Integer> numberOfPathPassingThrough = (new GraphCentralityMeasure<V,E,G>(g)).getBetweenness(paths);
		double degreeSum = getDegreeSum();
		double totalNbOfSp = paths.size();
		
		double averageLoad = totalNbOfSp/degreeSum;
		
		for(V vertex: g.vertexSet()){
			double nbOfPath = numberOfPathPassingThrough.get(vertex).doubleValue();
			double degree = g.degreeOf(vertex);
			double load = nbOfPath/degree;
			load = load/averageLoad;
			load = Math.log(load);
			loadsMap.put(vertex, load);
		}
		
		return loadsMap;
	}
	
	private int getDegreeSum(){
		int degreeSum=0;
		
		for(V vertex : g.vertexSet()){
			degreeSum+=g.degreeOf(vertex);
		}
		
		return degreeSum;
	}
	
}
