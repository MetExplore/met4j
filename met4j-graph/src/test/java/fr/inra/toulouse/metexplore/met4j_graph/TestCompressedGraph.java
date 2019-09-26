package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.CompressedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.PathEdge;

public class TestCompressedGraph {

	public static CompoundGraph cg;
	public static CompressedGraph<BioMetabolite, ReactionEdge, CompoundGraph> cg2;
	public static BioMetabolite v1,v2,v3;
	public static BioReaction r1,r2;
	public static ReactionEdge e1,e2;
	public static PathEdge<BioMetabolite, ReactionEdge> e;
	public static BioPath<BioMetabolite, ReactionEdge> path;
	public static BioNetwork bn;
	public static BioCompartment comp;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		bn = new BioNetwork();
		comp = new BioCompartment("comp"); bn.add(comp);
		v1 = new BioMetabolite("v1"); bn.add(v1); bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2"); bn.add(v2); bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3"); bn.add(v3); bn.affectToCompartment(comp, v3);
		
		r1 = new BioReaction("r1"); bn.add(r1);
		bn.affectLeft(v1, 1.0, comp, r1);
		bn.affectRight(v2, 1.0, comp, r1);
		r2 = new BioReaction("r2"); bn.add(r2);
		bn.affectLeft(v2, 1.0, comp, r2);
		bn.affectRight(v3, 1.0, comp, r2);
		
		e1 = new ReactionEdge(v1, v2, r1);
		e2 = new ReactionEdge(v2, v3, r2);
		
		cg.addVertex(v1);
		cg.addVertex(v2);
		cg.addVertex(v3);
		cg.addEdge(v1, v2, e1);
		cg.addEdge(v2, v3, e2);
		
		ArrayList<ReactionEdge> reactionList = new ArrayList<ReactionEdge>();
		reactionList.add(e1);
		reactionList.add(e2);
		path = new BioPath<BioMetabolite, ReactionEdge>(cg, v1, v3, reactionList, 2.0);
		
		cg2 = new CompressedGraph<BioMetabolite, ReactionEdge, CompoundGraph>(cg);
		cg2.addVertex(v1);
		cg2.addVertex(v3);
		
		e = new PathEdge<BioMetabolite, ReactionEdge>(v1, v3, path);
		cg2.addEdge(v1, v3, e);
		
		assertEquals(2, cg2.vertexSet().size());
		assertEquals(1, cg2.edgeSet().size());
	}
	
	@Test
	public void testCopyEdge() {
		PathEdge<BioMetabolite, ReactionEdge> ec = cg2.copyEdge(e);
		assertEquals(v1, ec.getV1());
		assertEquals(v3, ec.getV2());
		assertEquals(path, ec.getPath());
		assertEquals(v1, ec.getPath().getStartVertex());
		assertEquals(v3, ec.getPath().getEndVertex());
		assertTrue(ec.getPath().getEdgeList().contains(e1));
		assertTrue(ec.getPath().getEdgeList().contains(e2));
	}
	
	@Test
	public void testReverseEdge() {
		PathEdge<BioMetabolite, ReactionEdge> er = cg2.reverseEdge(e);
		assertEquals(v1, er.getV2());
		assertEquals(v3, er.getV1());
		assertEquals(path, er.getPath());
		assertEquals(v1, er.getPath().getStartVertex());
		assertEquals(v3, er.getPath().getEndVertex());
		assertTrue(er.getPath().getEdgeList().contains(e1));
		assertTrue(er.getPath().getEdgeList().contains(e2));
	}
	
	
	@Test
	public void testAddEdge(){
		CompressedGraph<BioMetabolite, ReactionEdge, CompoundGraph> cg3 
		= (CompressedGraph<BioMetabolite, ReactionEdge, CompoundGraph>) cg2.clone();
		cg3.addEdge(v3, v1);
		assertEquals(2, cg3.edgeSet().size());
	}
	
}
