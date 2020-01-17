package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestCompoundGraph {

	public static CompoundGraph cg;
	public static BioMetabolite v1,v2,v3;
	public static BioReaction r1,r2,r3;
	public static BioPathway p;
	public static ReactionEdge e1,e2;
	public static BioNetwork bn;
	public static BioCompartment comp;
	public static BioCompartment comp2;

	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		bn= new BioNetwork();
		comp = new BioCompartment("comp"); bn.add(comp);
		comp2 = new BioCompartment("comp2"); bn.add(comp2);
		v1 = new BioMetabolite("v1");bn.add(v1); bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2");bn.add(v2); bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3");bn.add(v3); bn.affectToCompartment(comp2, v3);
		
		p = new BioPathway("p");bn.add(p);
		
		r1 = new BioReaction("r1");bn.add(r1);
		bn.affectLeft(v1, 1.0, comp, r1);
		bn.affectRight(v2, 1.0, comp, r1);
		bn.affectToPathway(p, r1);
		r2 = new BioReaction("r2");bn.add(r2);
		bn.affectLeft(v2, 1.0, comp, r2);
		bn.affectRight(v3, 1.0, comp2, r2);
		bn.affectToPathway(p, r2);
		r3 = new BioReaction("r3");bn.add(r3);
		bn.affectLeft(v2, 1.0, comp, r3);
		bn.affectRight(v3, 1.0, comp2, r3);
		r3.setReversible(true);
		
		e1 = new ReactionEdge(v1, v2, r1);
		e2 = new ReactionEdge(v2, v3, r2);
		
		cg.addVertex(v1);
		cg.addVertex(v2);
		cg.addVertex(v3);
		cg.addEdge(v1, v2, e1);
		cg.addEdge(v2, v3, e2);
		
		assertEquals(3, cg.vertexSet().size());
		assertEquals(2, cg.edgeSet().size());
	}
	
	@Test
	public void testGetEdgesFromReaction() {
		HashSet<ReactionEdge> edgesFromR2 = cg.getEdgesFromReaction("r1");
		assertEquals(1, edgesFromR2.size());
		assertTrue(edgesFromR2.contains(e1));
		
		 edgesFromR2 = cg.getEdgesFromReaction("rX");
		 assertTrue(edgesFromR2.isEmpty());
	}
	
//	@Test
//	public void testGetEdgesFromPathway() {
//		HashSet<ReactionEdge> edgesFromP = cg.getEdgesFromPathway("p");
//		assertEquals(2, edgesFromP.size());
//		assertTrue(edgesFromP.contains(e1));
//		assertTrue(edgesFromP.contains(e2));
//		
//		edgesFromP = cg.getEdgesFromReaction("pX");
//		 assertTrue(edgesFromP.isEmpty());
//	}
	
	@Test
	public void testGetBiochemicalReactionList(){
		 HashMap<String, BioReaction> reactions = cg.getBiochemicalReactionList();
		 assertTrue(reactions.containsKey("r1"));
		 assertTrue(reactions.containsKey("r2"));
		 assertEquals(r1, reactions.get("r1"));
		 assertEquals(r2, reactions.get("r2"));
	}
	
	@Test
	public void testAddEdgesFromReaction() {
		cg.addEdgesFromReaction(bn,r3);
		assertEquals(3, cg.vertexSet().size());
		assertEquals(4, cg.edgeSet().size());
	}
	
	@Test
	public void testGetEdge() {
		ReactionEdge e = cg.getEdge("v1", "v2", "r1");
		assertEquals(e1, e);
	}
	
	@Test
	public void testCopyEdge() {
		ReactionEdge e = cg.copyEdge(e1);
		assertEquals(v1, e.getV1());
		assertEquals(v2, e.getV2());
		assertEquals(r1, e.getReaction());
	}
	
	@Test
	public void testReverseEdge() {
		ReactionEdge e = cg.reverseEdge(e1);
		assertEquals(v2, e.getV1());
		assertEquals(v1, e.getV2());
		assertEquals(r1, e.getReaction());
	}
	
	@Test
	public void testCopyConstructor() {
		CompoundGraph g2 = new CompoundGraph(cg);
		assertEquals(cg.vertexSet().size(), g2.vertexSet().size());
		assertEquals(cg.edgeSet().size(), g2.edgeSet().size());
		assertTrue(g2.containsEdge(e1));
		assertTrue(g2.containsEdge(e2));
		assertTrue(g2.containsVertex(v1));
		assertTrue(g2.containsVertex(v2));
	}
	
	@Test
	public void testAddEdge(){
		CompoundGraph g2 = (CompoundGraph) cg.clone();
		g2.addEdge(v3, v1);
		assertEquals(3, g2.edgeSet().size());
	}

	@Test
	public void testGetEdgesFromCompartment(){
		assertEquals(1,cg.getEdgesFromCompartment(bn, comp).size());
		assertEquals(0,cg.getEdgesFromCompartment(bn, comp2).size());
		assertTrue(cg.getEdgesFromCompartment(bn, comp).contains(e1));
	}
}
