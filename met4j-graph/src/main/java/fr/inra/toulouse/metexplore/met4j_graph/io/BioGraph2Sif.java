package fr.inra.toulouse.metexplore.met4j_graph.io;

import java.io.FileWriter;
import java.io.IOException;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;

public class BioGraph2Sif<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V, E>> {
	
	G graph;
	
	public BioGraph2Sif(G graph){
		this.graph=graph;
	}
	
	
	
	/**
	 * write Sif file
	 * @author ludo + clement
	 * 
	 * @param filename
	 * @param sbmlEncode
	 * @throws IOException
	 */
	public void writeSif(String filename, Boolean sbmlEncode) throws IOException {
		
		FileWriter fw = new FileWriter(filename);
		
		for(V node : graph.vertexSet()) {
			
			String nodeId = node.getId();
			if(sbmlEncode) {
				nodeId = StringUtils.sbmlEncode(nodeId);
			}
			
			if(graph.neighborListOf(node).isEmpty()) {
				// Orphan node
				fw.write(nodeId+"\n");
			}
			else {
				for(E outgoingEdge : graph.outgoingEdgesOf(node)) {
					
					V successor = outgoingEdge.getV2();
					String successorId = successor.getId();
					if(sbmlEncode) {
						successorId = StringUtils.sbmlEncode(successorId);
					}
					
					fw.write(nodeId+"\tlinkedWith\t"+successorId+"\n");
				}
				
				
			}
		}
		fw.close();
	}

	
}
