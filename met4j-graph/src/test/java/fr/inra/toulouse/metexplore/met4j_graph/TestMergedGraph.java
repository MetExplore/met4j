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

public class TestMergedGraph {

	public static CompoundGraph cg;
	public static MergedGraph<BioPhysicalEntity,ReactionEdge> cg2;
	public static BioPhysicalEntity v1,v2;
	public static BioReaction r1,r2;
	public static ReactionEdge e1,e2;
	public static MetaEdge<BioPhysicalEntity, ReactionEdge> e;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		
		r1 = new BioReaction("r1");
		r1.addLeftParticipant(new BioParticipant(v1));
		r1.addRightParticipant(new BioParticipant(v2));
		r2 = new BioReaction("r2");
		r2.addLeftParticipant(new BioParticipant(v1));
		r2.addRightParticipant(new BioParticipant(v2));
		
		e1 = new ReactionEdge(v1, v2, r1);
		e2 = new ReactionEdge(v1, v2, r2);
		
		cg.addVertex(v1);
		cg.addVertex(v2);
		cg.addEdge(v1, v2, e1);
		cg.addEdge(v1, v2, e2);
		
		HashSet<ReactionEdge> reactionList = new HashSet<ReactionEdge>();
		reactionList.add(e1);
		reactionList.add(e2);
		
		cg2 = new MergedGraph<BioPhysicalEntity,ReactionEdge>();
		cg2.addVertex(v1);
		cg2.addVertex(v2);
		
		e = new MetaEdge<BioPhysicalEntity, ReactionEdge>(v1, v2, reactionList);
		cg2.addEdge(v1, v2, e);
		
		assertEquals(2, cg2.vertexSet().size());
		assertEquals(1, cg2.edgeSet().size());
	}
	
	@Test
	public void testCopyEdge() {
		MetaEdge<BioPhysicalEntity, ReactionEdge> ec = cg2.copyEdge(e);
		assertEquals(v1, ec.getV1());
		assertEquals(v2, ec.getV2());

		assertTrue(ec.getEdgeList().contains(e1));
		assertTrue(ec.getEdgeList().contains(e2));
	}
	
	@Test
	public void testReverseEdge() {
		MetaEdge<BioPhysicalEntity, ReactionEdge> er = cg2.reverseEdge(e);
		assertEquals(v1, er.getV2());
		assertEquals(v2, er.getV1());

		assertTrue(er.getEdgeList().contains(e1));
		assertTrue(er.getEdgeList().contains(e2));
	}
	
	
	@Test
	public void testAddEdge(){
		MergedGraph<BioPhysicalEntity, ReactionEdge> cg3 
		= (MergedGraph<BioPhysicalEntity, ReactionEdge>) cg2.clone();
		cg3.addEdge(v2, v1);
		assertEquals(2, cg3.edgeSet().size());
	}
	
}
