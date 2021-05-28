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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;


/**
 * Weighting policy using weights in file
 * Handled format :
 * tab separated
 * source vertex id,target vertex id,edge label,weight
 * @author clement
 * @version $Id: $Id
 */
public class WeightsFromFile<V extends BioEntity, E extends Edge<V>,G extends BioGraph<V,E>>
	extends WeightingPolicy<V,E,G> {
	
	/** The file path. */
	final String filePath;
	boolean removeEdgeNotInFile=false;

	private String sep = "\t";
	private int sourceCol = 0;
	private int targetCol = 1;
	private int edgeLabelCol = 2;
	private int weightCol = 3;
	private boolean skipHeader = false;

	public WeightsFromFile<V,E,G> removeEdgeNotInFile() {
		this.removeEdgeNotInFile = true;
		return this;
	}
	public WeightsFromFile<V,E,G> sep(String sep) {
		this.sep = sep;
		return this;
	}
	public WeightsFromFile<V,E,G> sourceCol(int sourceCol) {
		this.sourceCol = sourceCol;
		return this;
	}
	public WeightsFromFile<V,E,G> targetCol(int targetCol) {
		this.targetCol = targetCol;
		return this;
	}
	public WeightsFromFile<V,E,G> edgeLabelCol(int edgeLabelCol) {
		this.edgeLabelCol = edgeLabelCol;
		return this;
	}
	public WeightsFromFile<V,E,G> weightCol(int weightCol) {
		this.weightCol = weightCol;
		return this;
	}
	public WeightsFromFile<V,E,G> skipHeader() {
		this.skipHeader = true;
		return this;
	}

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
	 * @param removeEdgeNotInFile if the edges not in the file should be removed from the graph
	 */
	public WeightsFromFile(String filePath, boolean removeEdgeNotInFile) {
		this.filePath=filePath;
		this.removeEdgeNotInFile=removeEdgeNotInFile;
	}

	@Override
	public void setWeight(G g) {
		HashSet<E> seenEdges = new HashSet<>();
		int notInGraph = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String inputLine;
			int n = 0;
			if(skipHeader){
				in.readLine();
				n++;
			}
			while ((inputLine = in.readLine()) != null){
				n++;
				String[] lineParts = inputLine.split(sep);
				if(lineParts.length > Collections.max(Arrays.asList(sourceCol,targetCol,edgeLabelCol,weightCol))){
					E e = g.getEdge(lineParts[sourceCol], lineParts[targetCol], lineParts[edgeLabelCol]);
					if(e!=null){
						if(!seenEdges.contains(e)){
							try{
								double w = Double.parseDouble(lineParts[weightCol]);
								if(!Double.isNaN(w)){
									g.setEdgeWeight(e, w);
									seenEdges.add(e);
								}else{
									System.err.println("Edge "+lineParts[sourceCol]+"-("+lineParts[edgeLabelCol]+")-"+lineParts[targetCol]+" has NaN weight. line "+n+" skipped.");
								}
								
							}catch (NumberFormatException enf){
								System.err.println("bad weight format line "+n+" : "+lineParts[weightCol]);
							}
							
						}else{
							System.err.println("Edge "+lineParts[sourceCol]+"-("+lineParts[edgeLabelCol]+")-"+lineParts[targetCol]+" already set. line "+n+" skipped.");
						}
					}else{
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
			Set<E> edgesToRemove = new HashSet<>(g.edgeSet());
			edgesToRemove.removeAll(seenEdges);
			g.removeAllEdges(edgesToRemove);
		}
	}
}
