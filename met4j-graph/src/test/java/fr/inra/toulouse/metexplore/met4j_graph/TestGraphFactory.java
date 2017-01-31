package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestGraphFactory {
	
	public static CompoundGraph cg;
	public static GraphFactory<BioPhysicalEntity, ReactionEdge, CompoundGraph> f;
	public static BioPhysicalEntity v1,v2,v3;
	public static BioChemicalReaction r1,r2,r3;
	public static ReactionEdge e1,e2;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		v3 = new BioPhysicalEntity("v3");
		
		r1 = new BioChemicalReaction("r1");
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(v1));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(v2));

		r2 = new BioChemicalReaction("r2");
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(v2));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(v3));

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
		
		f=CompoundGraph.getFactory();
	}

	@Test
	public void testCreateGraphFromEdgeList() {
		HashSet<ReactionEdge> edges = new HashSet<ReactionEdge>();
		edges.add(e1);
		edges.add(e2);
		
		CompoundGraph g2 = f.createGraphFromEdgeList(edges);
		
		assertEquals(3, g2.vertexSet().size());
		assertEquals(2, g2.edgeSet().size());
	}

	@Test
	public void testCreateGraphFromPathList() {
		ArrayList<ReactionEdge> edges = new ArrayList<ReactionEdge>();
		edges.add(e1);
		edges.add(e2);
		BioPath<BioPhysicalEntity, ReactionEdge> path = 
				new BioPath<BioPhysicalEntity, ReactionEdge>(cg, v1, v3, edges, 2.0);
		HashSet<BioPath<BioPhysicalEntity, ReactionEdge>> paths = new HashSet<BioPath<BioPhysicalEntity,ReactionEdge>>();
		paths.add(path);
		CompoundGraph g2 = f.createGraphFromPathList(paths);
		
		assertEquals(3, g2.vertexSet().size());
		assertEquals(2, g2.edgeSet().size());
	}

	@Test
	public void testCreateCopy() {
		CompoundGraph g2 = f.createCopy(cg);
		assertEquals(3, g2.vertexSet().size());
		assertEquals(2, g2.edgeSet().size());
	}

	@Test
	public void testReverse() {
		CompoundGraph g2 = f.reverse(cg);
		assertEquals(3, g2.vertexSet().size());
		assertEquals(2, g2.edgeSet().size());
		assertTrue(g2.containsEdge(v3, v2));
		assertTrue(g2.containsEdge(v2, v1));
	}

}
