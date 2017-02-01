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
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.ChokePoint;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestChokePoint {

	public static CompoundGraph cg;
	public static BipartiteGraph bg;
	public static BioPhysicalEntity v1,v2,v3,v4;
	public static BioChemicalReaction r1,r2,r3;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		bg = new BipartiteGraph();
		
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		v3 = new BioPhysicalEntity("v3");
		v4 = new BioPhysicalEntity("v4");
		
		r1 = new BioChemicalReaction("r1");
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(v1));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(v2));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(v3));

		r2 = new BioChemicalReaction("r2");
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(v1));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(v4));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(v3));

		r3 = new BioChemicalReaction("r3");
		r3.addLeftParticipant(new BioPhysicalEntityParticipant(v2));
		r3.addLeftParticipant(new BioPhysicalEntityParticipant(v3));
		r3.addRightParticipant(new BioPhysicalEntityParticipant(v4));

		
		ReactionEdge e1 = new ReactionEdge(v1, v2, r1);
		ReactionEdge e2 = new ReactionEdge(v1, v3, r1);
		ReactionEdge e3 = new ReactionEdge(v1, v2, r2);
		ReactionEdge e4 = new ReactionEdge(v1, v3, r2);
		ReactionEdge e5 = new ReactionEdge(v2, v4, r3);
		ReactionEdge e6 = new ReactionEdge(v3, v4, r3);
		
		cg.addVertex(v1);
		cg.addVertex(v2);
		cg.addVertex(v3);
		cg.addVertex(v4);
		cg.addEdge(v1, v2, e1);
		cg.addEdge(v1, v3, e2);
		cg.addEdge(v1, v2, e3);
		cg.addEdge(v1, v3, e4);
		cg.addEdge(v2, v4, e5);
		cg.addEdge(v3, v4, e6);
		
		BipartiteEdge be1 = new BipartiteEdge(v1, r1);
		BipartiteEdge be2 = new BipartiteEdge(v1, r2);
		BipartiteEdge be3 = new BipartiteEdge(v2, r3);
		BipartiteEdge be4 = new BipartiteEdge(v3, r3);
		BipartiteEdge be5 = new BipartiteEdge(r1, v2);
		BipartiteEdge be6 = new BipartiteEdge(r1, v3);
		BipartiteEdge be7 = new BipartiteEdge(r2, v2);
		BipartiteEdge be8 = new BipartiteEdge(r2, v3);
		BipartiteEdge be9 = new BipartiteEdge(r3, v4);
		
		bg.addVertex(v1);
		bg.addVertex(v2);
		bg.addVertex(v3);
		bg.addVertex(v4);
		bg.addVertex(r1);
		bg.addVertex(r2);
		bg.addVertex(r3);
		bg.addEdge(v1, r1, be1);
		bg.addEdge(v1, r2, be2);
		bg.addEdge(v2, r3, be3);
		bg.addEdge(v3, r3, be4);
		bg.addEdge(r1, v2, be5);
		bg.addEdge(r1, v3, be6);
		bg.addEdge(r2, v2, be7);
		bg.addEdge(r2, v3, be8);
		bg.addEdge(r3, v4, be9);

	}
	
	@Test
	public void testGetChokePointFromCompoundGraph() {
		HashSet<BioChemicalReaction> cp = ChokePoint.getChokePoint(cg);
		assertEquals(1, cp.size());
		assertTrue(cp.contains(r3));
	}
	
	@Test
	public void testGetChokePointFromBipartiteGraph() {
		HashSet<BioChemicalReaction> cp = ChokePoint.getChokePoint(bg);
		assertEquals(1, cp.size());
		assertTrue(cp.contains(r3));
	}
	
}
