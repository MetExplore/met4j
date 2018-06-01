/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.ScoreUtils;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestScoreUtils {
	
public static CompoundGraph g;
	
	public static BioPhysicalEntity a,b,c,d,e,f,x,y;
	public static BioReaction r1,r2,r3,r4;
	public static ReactionEdge ab,bc,ad,de,ef,fc,bx,eb,yc;
	 
	@BeforeClass
	public static void init(){
		//create empty graph
		g=new CompoundGraph();
		
		//add nodes
		a = new BioPhysicalEntity("a");g.addVertex(a);
		e = new BioPhysicalEntity("e");g.addVertex(e);
		d = new BioPhysicalEntity("d");g.addVertex(d);
		b = new BioPhysicalEntity("b");g.addVertex(b);
		c = new BioPhysicalEntity("c");g.addVertex(c);
		
		//create genes and enzyme
		BioGene g1 = new BioGene("1.2","1.2");
		BioProtein enz1 = new BioProtein("1.2","1.2");
		enz1.addGene(g1);
		BioGene g2 = new BioGene("3.4","3.4");
		BioProtein enz2 = new BioProtein("3.4","3.4");
		enz2.addGene(g2);
		
		//create reactions
		r1 = new BioReaction("abed");
		r1.addLeftParticipant(new BioParticipant(a));
		r1.addLeftParticipant(new BioParticipant(b));
		r1.addLeftParticipant(new BioParticipant(e));
		r1.addRightParticipant(new BioParticipant(d));
		r1.addEnz(enz1);
		r2 = new BioReaction("cad");
		r2.addLeftParticipant(new BioParticipant(c));
		r2.addRightParticipant(new BioParticipant(a));
		r2.addRightParticipant(new BioParticipant(d));
		r2.addEnz(enz1);
		r3 = new BioReaction("ade");
		r3.addLeftParticipant(new BioParticipant(a));
		r3.addLeftParticipant(new BioParticipant(d));
		r3.addRightParticipant(new BioParticipant(e));
		r3.addEnz(enz2);
		r4 = new BioReaction("eca");
		r4.addLeftParticipant(new BioParticipant(e));
		r4.addRightParticipant(new BioParticipant(a));
		r4.addRightParticipant(new BioParticipant(c));
		r4.addEnz(enz2);
		
		//add egdges
		ReactionEdge ad = new ReactionEdge(a, d, r1);
		g.addEdge(a, d, ad); g.setEdgeScore(ad, 3.0);
		ReactionEdge bd = new ReactionEdge(b, d, r1);
		g.addEdge(b, d, bd); g.setEdgeScore(bd, 3.0);
		ReactionEdge ed = new ReactionEdge(e, d, r1);
		g.addEdge(e, d, ed); g.setEdgeScore(ed, 3.0);
		ReactionEdge cd = new ReactionEdge(c, d, r3);
		g.addEdge(c, d, cd); g.setEdgeScore(cd, 2.0);
		ReactionEdge ca = new ReactionEdge(c, a, r3);
		g.addEdge(c, a, ca); g.setEdgeScore(ca, 2.0);
		ReactionEdge ae = new ReactionEdge(a, e, r2);
		g.addEdge(a, e, ae); g.setEdgeScore(ae, 1.0);
		ReactionEdge de = new ReactionEdge(d, e, r2);
		g.addEdge(d, e, de); g.setEdgeScore(de, 1.0);
		ReactionEdge ec = new ReactionEdge(e, c, r4);
		g.addEdge(e, c, ec); g.setEdgeScore(ec, 5.0);
		ReactionEdge ea = new ReactionEdge(e, a, r4);
		g.addEdge(e, a, ea); g.setEdgeScore(ea, 5.0);
	}
	
	@Test
	public void testScoreByReaction() {
		HashMap<String, Double> rScoreMap = ScoreUtils.getScoreByReaction(g);
		assertEquals(9.0, rScoreMap.get("abed"), Double.MIN_VALUE);
		assertEquals(2.0, rScoreMap.get("cad"), Double.MIN_VALUE);
		assertEquals(4.0, rScoreMap.get("ade"), Double.MIN_VALUE);
		assertEquals(10.0, rScoreMap.get("eca"), Double.MIN_VALUE);
	}
	
	@Test
	public void testScoreByGene() {
		HashMap<String, Double> rGeneMap = ScoreUtils.getScoreByGene(g);
		assertEquals(11.0, rGeneMap.get("1.2"), Double.MIN_VALUE);
		assertEquals(14.0, rGeneMap.get("3.4"), Double.MIN_VALUE);
	}
	
	@Test
	public void testNormalizeScoreFromGraph() {
		CompoundGraph gCopy = new CompoundGraph(g);
		ScoreUtils.normalizeScore(gCopy);
		assertEquals(3.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("a", "d", "abed")), Double.MIN_VALUE);
		assertEquals(3.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("b", "d", "abed")), Double.MIN_VALUE); 
		assertEquals(3.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("e", "d", "abed")), Double.MIN_VALUE);
		assertEquals(2.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("c", "d", "ade")), Double.MIN_VALUE);
		assertEquals(2.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("c", "a", "ade")), Double.MIN_VALUE);
		assertEquals(1.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("a", "e", "cad")), Double.MIN_VALUE);
		assertEquals(1.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("d", "e", "cad")), Double.MIN_VALUE);
		assertEquals(5.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("e", "c", "eca")), Double.MIN_VALUE);
		assertEquals(5.0/5.0, gCopy.getEdgeScore(gCopy.getEdge("e", "a", "eca")), Double.MIN_VALUE);
	}
	
	@Test
	public void testNormalizeScoreFromList() {
		CompoundGraph gCopy = new CompoundGraph(g);
		HashMap<String, Double> rScoreMap = ScoreUtils.getScoreByReaction(gCopy);
		ScoreUtils.normalizeScore(rScoreMap);
		assertEquals(9.0/10.0, rScoreMap.get("abed"), Double.MIN_VALUE);
		assertEquals(2.0/10.0, rScoreMap.get("cad"), Double.MIN_VALUE);
		assertEquals(4.0/10.0, rScoreMap.get("ade"), Double.MIN_VALUE);
		assertEquals(10.0/10.0, rScoreMap.get("eca"), Double.MIN_VALUE);
	}
	
	@Test
	public void testImportScore() {
		CompoundGraph gCopy=new CompoundGraph();
		gCopy.addVertex(a);
		gCopy.addVertex(e);
		gCopy.addVertex(d);
		gCopy.addVertex(b);
		gCopy.addVertex(c);
		ReactionEdge ad = new ReactionEdge(a, d, r1);gCopy.addEdge(a, d, ad);
		ReactionEdge bd = new ReactionEdge(b, d, r1);gCopy.addEdge(b, d, bd);
		ReactionEdge ed = new ReactionEdge(e, d, r1);gCopy.addEdge(e, d, ed);
		ReactionEdge cd = new ReactionEdge(c, d, r3);gCopy.addEdge(c, d, cd);
		ReactionEdge ca = new ReactionEdge(c, a, r3);gCopy.addEdge(c, a, ca);
		ReactionEdge ae = new ReactionEdge(a, e, r2);gCopy.addEdge(a, e, ae);
		ReactionEdge de = new ReactionEdge(d, e, r2);gCopy.addEdge(d, e, de);
		ReactionEdge ec = new ReactionEdge(e, c, r4);gCopy.addEdge(e, c, ec);
		ReactionEdge ea = new ReactionEdge(e, a, r4);gCopy.addEdge(e, a, ea);
		
		
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_edgeWeightmport", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		String filePath = "EdgeScoreTestFile.tab";
		try {
			filePath = TestUtils.copyProjectResource(filePath, tmpPath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
			fail("problem while reading edge score file");
		}
		
		ScoreUtils.importScores(gCopy, filePath, 0, 1, "\t");
		
		assertEquals(3.0, gCopy.getEdgeScore(gCopy.getEdge("a", "d", "abed")), Double.MIN_VALUE);
		assertEquals(3.0, gCopy.getEdgeScore(gCopy.getEdge("b", "d", "abed")), Double.MIN_VALUE); 
		assertEquals(3.0, gCopy.getEdgeScore(gCopy.getEdge("e", "d", "abed")), Double.MIN_VALUE);
		assertEquals(2.0, gCopy.getEdgeScore(gCopy.getEdge("c", "d", "ade")), Double.MIN_VALUE);
		assertEquals(2.0, gCopy.getEdgeScore(gCopy.getEdge("c", "a", "ade")), Double.MIN_VALUE);
		assertEquals(1.0, gCopy.getEdgeScore(gCopy.getEdge("a", "e", "cad")), Double.MIN_VALUE);
		assertEquals(1.0, gCopy.getEdgeScore(gCopy.getEdge("d", "e", "cad")), Double.MIN_VALUE);
		assertEquals(5.0, gCopy.getEdgeScore(gCopy.getEdge("e", "c", "eca")), Double.MIN_VALUE);
		assertEquals(5.0, gCopy.getEdgeScore(gCopy.getEdge("e", "a", "eca")), Double.MIN_VALUE);
	}
}
