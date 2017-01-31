package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestCompoundGraph {

	public static CompoundGraph cg;
	public static BioPhysicalEntity v1,v2,v3;
	public static BioChemicalReaction r1,r2,r3;
	public static BioPathway p;
	public static ReactionEdge e1,e2;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		v3 = new BioPhysicalEntity("v3");
		
		p = new BioPathway("p");
		
		r1 = new BioChemicalReaction("r1");
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(v1));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(v2));
		r1.addPathway(p);
		r2 = new BioChemicalReaction("r2");
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(v2));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(v3));
		r2.addPathway(p);
		r3 = new BioChemicalReaction("r3");
		r3.addLeftParticipant(new BioPhysicalEntityParticipant(v2));
		r3.addRightParticipant(new BioPhysicalEntityParticipant(v3));
		r3.setReversibility(true);
		
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
	
	@Test
	public void testGetEdgesFromPathway() {
		HashSet<ReactionEdge> edgesFromP = cg.getEdgesFromPathway("p");
		assertEquals(2, edgesFromP.size());
		assertTrue(edgesFromP.contains(e1));
		assertTrue(edgesFromP.contains(e2));
		
		edgesFromP = cg.getEdgesFromReaction("pX");
		 assertTrue(edgesFromP.isEmpty());
	}
	
	@Test
	public void testGetBiochemicalReactionList(){
		 HashMap<String, BioChemicalReaction> reactions = cg.getBiochemicalReactionList();
		 assertTrue(reactions.containsKey("r1"));
		 assertTrue(reactions.containsKey("r2"));
		 assertEquals(r1, reactions.get("r1"));
		 assertEquals(r2, reactions.get("r2"));
	}
	
	@Test
	public void testAddEdgesFromReaction() {
		cg.addEdgesFromReaction(r3);
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
		assertEquals(p, e.getPathway().iterator().next());
	}
	
	@Test
	public void testReverseEdge() {
		ReactionEdge e = cg.reverseEdge(e1);
		assertEquals(v2, e.getV1());
		assertEquals(v1, e.getV2());
		assertEquals(r1, e.getReaction());
		assertEquals(p, e.getPathway().iterator().next());
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
	
}
