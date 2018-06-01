package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestGraphFactory {
	
	public static CompoundGraph cg;
	public static GraphFactory<BioPhysicalEntity, ReactionEdge, CompoundGraph> f;
	public static BioPhysicalEntity v1,v2,v3;
	public static BioReaction r1,r2,r3;
	public static ReactionEdge e1,e2;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		v3 = new BioPhysicalEntity("v3");
		
		r1 = new BioReaction("r1");
		r1.addLeftParticipant(new BioParticipant(v1));
		r1.addRightParticipant(new BioParticipant(v2));

		r2 = new BioReaction("r2");
		r2.addLeftParticipant(new BioParticipant(v2));
		r2.addRightParticipant(new BioParticipant(v3));

		r3 = new BioReaction("r3");
		r3.addLeftParticipant(new BioParticipant(v2));
		r3.addRightParticipant(new BioParticipant(v3));
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
	public void testCreateGraphFromElements() {
		HashSet<ReactionEdge> edges = new HashSet<ReactionEdge>();
		edges.add(e1);
		edges.add(e2);
		HashSet<BioPhysicalEntity> vertexSet1 = new HashSet<BioPhysicalEntity>();
		vertexSet1.add(v1);
		vertexSet1.add(v2);
		vertexSet1.add(v3);
		vertexSet1.add(new BioPhysicalEntity("v4"));
		
		CompoundGraph g2 = f.createGraphFromElements(vertexSet1, edges);
		
		assertEquals(4, g2.vertexSet().size());
		assertEquals(2, g2.edgeSet().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEsceptionCreateGraphFromElements() {
		HashSet<ReactionEdge> edges = new HashSet<ReactionEdge>();
		edges.add(e1);
		edges.add(e2);
		HashSet<BioPhysicalEntity> vertexSet1 = new HashSet<BioPhysicalEntity>();
		vertexSet1.add(v1);
		vertexSet1.add(v2);
		
		CompoundGraph g2 = f.createGraphFromElements(vertexSet1, edges);
		g2.toString();
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
	
	@Test
	public void testCreateSubgraph(){
		HashSet<BioPhysicalEntity> vertexSet1 = new HashSet<BioPhysicalEntity>();
		vertexSet1.add(v1);
		vertexSet1.add(v2);
		CompoundGraph g2 = f.createSubGraph(cg, vertexSet1);
		assertEquals(2, g2.vertexSet().size());
		assertEquals(1, g2.edgeSet().size());
		assertTrue(g2.vertexSet().contains(v1));
		assertTrue(g2.vertexSet().contains(v2));
		assertTrue(g2.edgeSet().contains(e1));
	}

}
