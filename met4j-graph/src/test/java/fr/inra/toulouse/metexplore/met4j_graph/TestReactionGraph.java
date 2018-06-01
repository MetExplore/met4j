package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;

public class TestReactionGraph {

	public static ReactionGraph rg;
	public static BioPhysicalEntity v1,v2,v3;
	public static BioReaction r1,r2,r3,r4;
	public static BioPathway p;
	public static CompoundEdge e1,e2;
	
	@BeforeClass
	public static void init(){
		
		rg = new ReactionGraph();
		
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		v3 = new BioPhysicalEntity("v3");
		
		p = new BioPathway("p");
		
		r1 = new BioReaction("r1");
		r1.addLeftParticipant(new BioParticipant(v1));
		r1.addRightParticipant(new BioParticipant(v2));
		r1.addPathway(p);
		r2 = new BioReaction("r2");
		r2.addLeftParticipant(new BioParticipant(v2));
		r2.addRightParticipant(new BioParticipant(v3));
		r2.addPathway(p);
		r3 = new BioReaction("r3");
		r3.addLeftParticipant(new BioParticipant(v2));
		r3.addRightParticipant(new BioParticipant(v3));
		r3.setReversibility(true);
		r4 = new BioReaction("r4");
		r4.addLeftParticipant(new BioParticipant(v3));
		r4.addRightParticipant(new BioParticipant(v1));
		r4.setReversibility(true);
		
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
		 HashMap<String, BioPhysicalEntity> cpds = rg.getCompoundList();
		 assertTrue(cpds.containsKey("v2"));
		 assertTrue(cpds.containsKey("v3"));
		 assertEquals(v2, cpds.get("v2"));
		 assertEquals(v3, cpds.get("v3"));
	}
	
	@Test
	public void testAddEdgesFromCompound() {
		ReactionGraph rg2 = (ReactionGraph) rg.clone();
		rg2.addEdgesFromCompound(v2);
		assertEquals(4, rg2.vertexSet().size());
		assertEquals(4, rg2.edgeSet().size());
	}
	
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
