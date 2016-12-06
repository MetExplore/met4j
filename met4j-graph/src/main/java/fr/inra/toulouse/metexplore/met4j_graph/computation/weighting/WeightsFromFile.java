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
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;


/**
 * Weighting policy using weights in file
 * Handled format :
 * tab separated
 * source vertex id,target vertex id,reaction id,weight
 * @author clement
 */
public class WeightsFromFile<V extends BioEntity, E extends Edge<V>,G extends BioGraph<V,E>> 
	extends WeightingPolicy<V,E,G>{
	
	/** The file path. */
	String filePath;
	boolean removeEdgeNotInFile=false;
	
	/**
	 * Instantiates a new weights from file.
	 *
	 * @param filePath the file path
	 */
	public WeightsFromFile(String filePath) {
		this.filePath=filePath;
	}
	
	/**
	 * Instantiates a new weights from file.
	 *
	 * @param filePath the file path
	 * @param removeEdgeNotInFile
	 */
	public WeightsFromFile(String filePath,boolean removeEdgeNotInFile) {
		this.filePath=filePath;
		this.removeEdgeNotInFile=removeEdgeNotInFile;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.WeightingPolicy#setWeight(parsebionet.applications.graphe.BioGraph)
	 */
	@Override
	public void setWeight(G g) {
		HashSet<E> seenEdges = new HashSet<E>();
		int notInGraph = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String inputLine;
			int n = 0;
			while ((inputLine = in.readLine()) != null){
				n++;
				if(inputLine.matches("\\S+\t\\S+\t\\S+\t\\S+")){
					String[] lineParts = inputLine.split("\t");
					E e = g.getEdge(lineParts[0], lineParts[1], lineParts[2]);
					if(e!=null){
						if(!seenEdges.contains(e)){
							try{
								double w = Double.parseDouble(lineParts[3]);
								if(!Double.isNaN(w)){
									g.setEdgeWeight(e, w);
									seenEdges.add(e);
								}else{
									System.err.println("Edge "+lineParts[0]+"-("+lineParts[2]+")-"+lineParts[1]+" has NaN weight. line "+n+" skipped.");
								}
								
							}catch (NumberFormatException enf){
								System.err.println("bad weight format line "+n+" : "+lineParts[3]);
							}
							
						}else{
							System.err.println("Edge "+lineParts[0]+"-("+lineParts[2]+")-"+lineParts[1]+" already set. line "+n+" skipped.");
						}
					}else{
//						System.err.println("Edge "+lineParts[0]+"-("+lineParts[2]+")-"+lineParts[1]+" not in graph. line "+n+" skipped.");
						notInGraph++;
					}
				}else{
					System.err.println("bad line format at "+n);
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println(notInGraph+" edges in file not found in the graph");
		System.err.println(seenEdges.size()+" weights set among "+g.edgeSet().size()+" edges in graph");
		
		if(removeEdgeNotInFile){
			Set<E> edgesToRemove = new HashSet<E>(g.edgeSet());
			edgesToRemove.removeAll(seenEdges);
			g.removeAllEdges(edgesToRemove);
		}
	}

}
