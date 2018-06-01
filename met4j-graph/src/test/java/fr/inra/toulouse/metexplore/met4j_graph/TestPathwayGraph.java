package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.CompressedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.PathEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MetaEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;

public class TestPathwayGraph {

	public static PathwayGraph pg;
	public static BioPathway p1,p2;
	public static BioPhysicalEntity v1,v2,v3;
	public static BioReaction r1,r2,r3;
	public static PathwayGraphEdge e;
	
	@BeforeClass
	public static void init(){
		
		pg = new PathwayGraph();
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		v3 = new BioPhysicalEntity("v3");
		
		p1 = new BioPathway("p1");
		p2 = new BioPathway("p2");
		
		r1 = new BioReaction("r1");
		r1.addLeftParticipant(new BioParticipant(v1));
		r1.addRightParticipant(new BioParticipant(v2));
		r2 = new BioReaction("r2");
		r2.addLeftParticipant(new BioParticipant(v2));
		r2.addRightParticipant(new BioParticipant(v3));
		r3 = new BioReaction("r3");
		r3.addLeftParticipant(new BioParticipant(v3));
		r3.addRightParticipant(new BioParticipant(v1));
		r3.setReversibility(true);
		
		p1.addReaction(r1);
		p1.addReaction(r3);
		p2.addReaction(r2);
		
		HashSet<BioPhysicalEntity> connectingCompounds = new HashSet<BioPhysicalEntity>();
		connectingCompounds.add(v1);
		connectingCompounds.add(v3);
		
		e = new PathwayGraphEdge(p1, p2, connectingCompounds);
		
		pg.addVertex(p1);
		pg.addVertex(p2);
		pg.addEdge(p1, p2, e);
		
		
		assertEquals(2, pg.vertexSet().size());
		assertEquals(1, pg.edgeSet().size());
	}
	
	@Test
	public void testCopyEdge() {
		PathwayGraphEdge ec = pg.copyEdge(e);
		assertEquals(p1, ec.getV1());
		assertEquals(p2, ec.getV2());
		assertEquals(2, ec.getConnectingCompounds().size());
		assertTrue(ec.getConnectingCompounds().contains(v1));
		assertTrue(ec.getConnectingCompounds().contains(v3));
	}
	
	@Test
	public void testReverseEdge() {
		PathwayGraphEdge er = pg.reverseEdge(e);
		assertEquals(p1, er.getV2());
		assertEquals(p2, er.getV1());
		assertEquals(2, er.getConnectingCompounds().size());
		assertTrue(er.getConnectingCompounds().contains(v1));
		assertTrue(er.getConnectingCompounds().contains(v3));
	}
	
	
	@Test
	public void testAddEdge(){
		PathwayGraph pg2 = (PathwayGraph) pg.clone();
		pg2.addEdge(p2, p1);
		assertEquals(2, pg2.edgeSet().size());
	}
	
}
