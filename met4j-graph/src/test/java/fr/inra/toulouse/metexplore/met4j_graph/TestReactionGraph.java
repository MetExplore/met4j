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
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;

public class TestReactionGraph {

	public static ReactionGraph rg;
	public static BioMetabolite v1,v2,v3;
	public static BioReaction r1,r2,r3,r4;
	public static BioPathway p;
	public static CompoundEdge e1,e2;
	public static BioNetwork bn;
	public static BioCompartment comp;
	
	@BeforeClass
	public static void init(){
		
		rg = new ReactionGraph();
		bn = new BioNetwork();
		comp = new BioCompartment("comp");bn.add(comp);
		
		v1 = new BioMetabolite("v1");bn.add(v1);bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2");bn.add(v2);bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3");bn.add(v3);bn.affectToCompartment(comp, v3);
		
		p = new BioPathway("p");bn.add(p);
		
		r1 = new BioReaction("r1");bn.add(r1);
		bn.affectLeft(v1, 1.0, comp, r1);
		bn.affectRight(v2, 1.0, comp, r1);
		bn.affectToPathway(p, r1);
		r2 = new BioReaction("r2");bn.add(r2);
		bn.affectLeft(v2, 1.0, comp, r2);
		bn.affectRight(v3, 1.0, comp, r2);
		bn.affectToPathway(p, r2);
		r3 = new BioReaction("r3");bn.add(r3);
		bn.affectLeft(v2, 1.0, comp, r3);
		bn.affectRight(v3, 1.0, comp, r3);
		r3.setReversible(true);
		r4 = new BioReaction("r4");bn.add(r4);
		bn.affectLeft(v3, 1.0, comp, r4);
		bn.affectRight(v1, 1.0, comp, r4);
		r4.setReversible(true);
		
		e1 = new CompoundEdge(r1, r2, v2);
		e2 = new CompoundEdge(r2, r3, v3);
		
		rg.addVertex(r1);
		rg.addVertex(r2);
		rg.addVertex(r3);
		rg.addVertex(r4);
		rg.addEdge(r1, r2, e1);
		rg.addEdge(r2, r3, e2);
		
		assertEquals(4, rg.vertexSet().size());
		assertEquals(2, rg.edgeSet().size());
	}
	
	@Test
	public void testGetEdgesFromCompound() {
		HashSet<CompoundEdge> edgesFromV2 = rg.getEdgesFromCompound("v2");
		assertEquals(1, edgesFromV2.size());
		assertTrue(edgesFromV2.contains(e1));
		
		edgesFromV2 = rg.getEdgesFromCompound("vX");
		 assertTrue(edgesFromV2.isEmpty());
	}
	
	@Test
	public void testGetBiochemicalReactionList(){
		 HashMap<String, BioMetabolite> cpds = rg.getCompoundList();
		 assertTrue(cpds.containsKey("v2"));
		 assertTrue(cpds.containsKey("v3"));
		 assertEquals(v2, cpds.get("v2"));
		 assertEquals(v3, cpds.get("v3"));
	}
	
//	@Test
//	public void testAddEdgesFromCompound() {
//		ReactionGraph rg2 = (ReactionGraph) rg.clone();
//		rg2.addEdgesFromCompound(v2);
//		assertEquals(4, rg2.vertexSet().size());
//		assertEquals(4, rg2.edgeSet().size());
//	}
//	
	@Test
	public void testGetEdge() {
		CompoundEdge e = rg.getEdge("r1", "r2", "v2");
		assertEquals(e1, e);
	}
	
	@Test
	public void testCopyEdge() {
		CompoundEdge e = rg.copyEdge(e1);
		assertEquals(r1, e.getV1());
		assertEquals(r2, e.getV2());
		assertEquals(v2, e.getCompound());
	}
	
	@Test
	public void testReverseEdge() {
		CompoundEdge e = rg.reverseEdge(e1);
		assertEquals(r2, e.getV1());
		assertEquals(r1, e.getV2());
		assertEquals(v2, e.getCompound());
	}
	
	@Test
	public void testCopyConstructor() {
		ReactionGraph g2 = new ReactionGraph(rg);
		assertEquals(rg.vertexSet().size(), g2.vertexSet().size());
		assertEquals(rg.edgeSet().size(), g2.edgeSet().size());
		assertTrue(g2.containsEdge(e1));
		assertTrue(g2.containsEdge(e2));
		assertTrue(g2.containsVertex(r1));
		assertTrue(g2.containsVertex(r2));
		assertTrue(g2.containsVertex(r3));
	}
	
	@Test
	public void testAddEdge(){
		ReactionGraph g2 = (ReactionGraph) rg.clone();
		g2.addEdge(r3, r4);
		assertEquals(3, g2.edgeSet().size());
	}
	
}
