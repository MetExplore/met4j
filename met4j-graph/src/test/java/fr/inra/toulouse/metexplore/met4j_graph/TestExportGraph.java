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
package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inra.toulouse.metexplore.met4j_core.io.Sbml2BioNetworkLite;


/**
 * Test {@link ExportGraph}
 * @author clement
 */
public class TestExportGraph{
	
	static CompoundGraph g;
	
	static BioNetwork bn;
	
	@BeforeClass
	public static void init(){
		String inputSbml="recon2v03.xml";

		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_parseBioNet", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		File temp = tmpPath.toFile();
		
		try {
			inputSbml = TestUtils.copyProjectResource(inputSbml, temp);
		} catch (IOException e) {
			e.printStackTrace();
			fail("problem while reading sbml");
		}
		
		bn = new Sbml2BioNetworkLite(inputSbml,true).getBioNetwork();
		System.err.println("sbml import done");
		Bionetwork2BioGraph bn2g = new Bionetwork2BioGraph(bn);
		g = bn2g.getCompoundGraph();
		g.removeIsolatedNodes();
		System.err.println("graph build done");
	}
	

//	/**
//	 * Test the sbml export.
//	 */
//	@Test
//	public void testSbmlExport(){
//		File exportedGraph = null;
//		BioNetwork bn2 =null;
//		try {
//			exportedGraph = File.createTempFile("exportedGraph", ".sbml");
//			ExportGraph.toSbml(g, exportedGraph.getAbsolutePath());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//			fail("unable to export graph");
//		}
//		
//		bn2 = new Sbml2BioNetworkLite(exportedGraph.getAbsolutePath(),true).getBioNetwork();
////		bn2 = new JSBMLToBionetwork(exportedGraph.getAbsolutePath()).getBioNetwork();
//		exportedGraph.delete();
//		
//		assertNotNull("unable to parse exported sbml file", bn2);
//		CompoundGraph g2 = new Bionetwork2BioGraph(bn2).getCompoundGraph();
//		for(String v1 : bn.getPhysicalEntityList().keySet()){
//			if(!bn2.getPhysicalEntityList().containsKey(v1)) System.out.println(v1);
//		}
//		assertEquals("wrong number of vertex after sbml export", g.vertexSet().size(), g2.vertexSet().size());
//		assertEquals("wrong number of edges after sbml export", g.edgeSet().size(), g2.edgeSet().size());
//	}
	
	/**
	 * Test the gml export.
	 */
	@Test
	public void testGmlExport(){
		//test export to .gml format
		File exportedGraph = null;
		try {
			exportedGraph = File.createTempFile("exportedGraph", ".gml");
			ExportGraph.toGml(g, exportedGraph.getAbsolutePath());

		} catch (IOException e1) {
			e1.printStackTrace();
			fail("unable to export graph");
		}
		
		//test if their is as many vertex and edges in the .gml file as in the original graph
		int vertexCount=0;
		int edgeCount=0;
		try {
			BufferedReader file = new BufferedReader(new FileReader(exportedGraph));
			String line;
			while((line = file.readLine())!= null){
				if(line.matches("^.*node.*$")){
					vertexCount++;
				}else if(line.matches("^.*edge.*$")){
					edgeCount++;
				}
			}
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("exported file doesn't exist");
		}
		
		assertEquals("missing vertex in exported file", g.vertexSet().size(), vertexCount);
		assertEquals("missing edge in exported file", g.edgeSet().size(), edgeCount);
		exportedGraph.delete();
	}
	
	/**
	 * Test the tab export.
	 */
	@Test
	public void testTabExport(){
		//test export to .tab format
		File exportedGraph = null;
		try {
			exportedGraph = File.createTempFile("exportedGraph", ".tab");
			ExportGraph.toTab(g, exportedGraph.getAbsolutePath());

		} catch (IOException e1) {
			e1.printStackTrace();
			fail("unable to export graph");
		}
		
		//test if their is as many vertex and edges in the .tab file as in the original graph
		HashSet<String> vertex=new HashSet<String>();
		int edgeCount=0;
		HashSet<String> reactions=new HashSet<String>();
		try {
			BufferedReader file = new BufferedReader(new FileReader(exportedGraph));
			String line;
			line=file.readLine(); //skip header
			while((line = file.readLine())!= null){
				if(line.matches("^\\w+\t\\w+\t\\w+\\s*")){
					edgeCount++;
					String[] col = line.split("\t");
					vertex.add(col[0]);
					vertex.add(col[2]);
					reactions.add(col[1]);
				}
			}
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("exported file doesn't exist");
		}
		
		
		assertEquals("missing vertex in exported file", g.vertexSet().size(), vertex.size());
		assertEquals("missing edge in exported file", g.edgeSet().size(), edgeCount);
		
		HashSet<String> bnReactions=new HashSet<String>();
		for(BioReaction r : bn.getBiochemicalReactionList().values()){
			if(r.getLeftList().size()!=0 && r.getRightList().size()!=0){
				bnReactions.add(r.getId());
			}
		}
		
		assertEquals("missing reaction in exported file", bnReactions, reactions);
		exportedGraph.delete();
	}
	
	/**
	 * Test the bipartite export.
	 */
	@Test
	public void testBipartiteExport(){
		//test export to .gml format
		File exportedGraph = null;
		BipartiteGraph bip = new Bionetwork2BioGraph(bn).getBipartiteGraph();
		try {
			exportedGraph = File.createTempFile("exportedBipartite", ".gml");
			ExportGraph.toGml(bip, exportedGraph.getAbsolutePath());

		} catch (IOException e1) {
			e1.printStackTrace();
			fail("unable to export graph");
		}
		
		//test if their is as many vertex and edges in the .gml file as in the original graph
		int vertexCount=0;
		int edgeCount=0;
		try {
			BufferedReader file = new BufferedReader(new FileReader(exportedGraph));
			String line;
			while((line = file.readLine())!= null){
				if(line.matches("^.*node.*$")){
					vertexCount++;
				}else if(line.matches("^.*edge.*$")){
					edgeCount++;
				}
			}
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("exported file doesn't exist");
		}
		
		assertEquals("missing vertex in exported file", bip.vertexSet().size(), vertexCount);
		assertEquals("missing edge in exported file", bip.edgeSet().size(), edgeCount);
		exportedGraph.delete();
	}
	
	/**
	 * Test the bipartite edges export.
	 */
	@Test
	public void testBipartiteEdgesExport(){
		//test attributes export to .tab format
		File exportedEdges = null;
		BipartiteGraph bip = new Bionetwork2BioGraph(bn).getBipartiteGraph();
		try {
			exportedEdges = File.createTempFile("exportedBipAtt", ".tab");
			ExportGraph.exportBipartiteEdge(bip, exportedEdges.getAbsolutePath());

		} catch (IOException e1) {
			e1.printStackTrace();
			fail("unable to export edges info");
		}
		
		//test if their is as many edges in the .tab file as in the original graph
		int edgeCount=0;
//		final int NBOFATTRIBUTES = 13;
		try {
			BufferedReader file = new BufferedReader(new FileReader(exportedEdges));
			String line;
			boolean first = true;
			while((line = file.readLine())!= null){
				if(first){
					first=false;
				}else{
					if(!line.matches("^\\s*$")){
						edgeCount++;
						
						//test not empty attributes count
//						String[] att = line.split("\t");
//						assertEquals("wrong number of attributes", NBOFATTRIBUTES, att.length);
//						for(int i=0; i<att.length; i++){
//							assertFalse("empty attribute",att[i].isEmpty());
//						}
					}
				}
			}
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("exported file doesn't exist");
		}
		assertEquals("missing edge in exported file", bip.edgeSet().size(), edgeCount);
		exportedEdges.delete();
	}
	
	/**
	 * Test the edges export.
	 */
	@Test
	public void testEdgesExport(){
		//test attributes export to .tab format
		File exportedEdges = null;
		try {
			exportedEdges = File.createTempFile("exportedAtt", ".tab");
			ExportGraph.exportEdgeTabular(g, exportedEdges.getAbsolutePath());

		} catch (IOException e1) {
			e1.printStackTrace();
			fail("unable to export edges info");
		}
		
		//test if their is as many edges in the .tab file as in the original graph
		int edgeCount=0;
		final int NBOFATTRIBUTES = 13;
		try {
			BufferedReader file = new BufferedReader(new FileReader(exportedEdges));
			String line;
			boolean first = true;
			while((line = file.readLine())!= null){
				if(first){
					first=false;
				}else{
					if(!line.matches("^\\s*$")){
						edgeCount++;
						
						//test not empty attributes count
						String[] att = line.split("\t");
						assertEquals("wrong number of attributes", NBOFATTRIBUTES, att.length);
						for(int i=0; i<att.length; i++){
							assertFalse("empty attribute (column "+i+")",att[i].isEmpty());
						}
					}
				}
			}
			file.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			fail("exported file doesn't exist");
		}
		assertEquals("missing edge in exported file", g.edgeSet().size(), edgeCount);
		exportedEdges.delete();
	}
	
}
