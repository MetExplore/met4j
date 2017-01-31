package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;

public class TestBipartiteGraph {

	public static BipartiteGraph bg;
	public static BioPhysicalEntity v1,v2,v3,side;
	public static BioChemicalReaction r1,r2;

	public static BipartiteEdge e1,e2,e3,e4,e5,e6;
	
	@BeforeClass
	public static void init(){
		
		bg = new BipartiteGraph();
		v1 = new BioPhysicalEntity("v1");
		v2 = new BioPhysicalEntity("v2");
		v3 = new BioPhysicalEntity("v3");
		side = new BioPhysicalEntity("adp");
		side.setIsSide(true);
		
		r1 = new BioChemicalReaction("r1");
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(v1));
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(side));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(v2));
		r2 = new BioChemicalReaction("r2");
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(v2));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(v3));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(side));
		r2.setReversibility(true);
		
		bg.addVertex(v1);
		bg.addVertex(v2);
		bg.addVertex(v3);
		bg.addVertex(r1);
		bg.addVertex(r2);
		
		e1 = new BipartiteEdge(v1, r1);
		bg.addEdge(v1, r1, e1);
		e2 = new BipartiteEdge(r1, v2);
		bg.addEdge(r1, v2, e2);
		e3 = new BipartiteEdge(v2, r2, true);
		e5 = new BipartiteEdge(r2, v2, true);
		bg.addEdge(v2, r2, e3);
		bg.addEdge(r2, v2, e5);
		e4 = new BipartiteEdge(r2, v3, true);
		e6 = new BipartiteEdge(v3, r2, true);		
		bg.addEdge(r2, v3, e4);
		bg.addEdge(v3, r2, e6);
		
		assertEquals(5, bg.vertexSet().size());
		assertEquals(6, bg.edgeSet().size());
	}
	
	@Test
	public void testCompoundVertexSet(){
		Set<BioPhysicalEntity> cpds = bg.compoundVertexSet();
		assertEquals(3, cpds.size());
		assertTrue(cpds.contains(v1));
		assertTrue(cpds.contains(v2));
		assertTrue(cpds.contains(v3));
	}
	
	@Test
	public void testReactionVertexSet(){
		Set<BioChemicalReaction> rxns = bg.reactionVertexSet();
		assertEquals(2, rxns.size());
		assertTrue(rxns.contains(r1));
		assertTrue(rxns.contains(r2));
	}
	
	@Test
	public void testCopyEdge(){
		BipartiteEdge e = bg.copyEdge(e6);
		assertEquals(v3, e.getV1());
		assertEquals(r2, e.getV2());
		assertEquals(true, e.isReversible());
		assertEquals(false, e.isSide());
	}
	
	@Test
	public void testMergeReversibleEdges(){
		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
		bg2.mergeReversibleEdges();
		assertEquals(5, bg2.vertexSet().size());
		assertEquals(4, bg2.edgeSet().size());
	}
	
	@Test
	public void testAddMissingCompoundAsSide(){
		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
		bg2.addMissingCompoundAsSide();
		assertEquals(6, bg2.vertexSet().size());
		assertEquals(9, bg2.edgeSet().size());
		assertTrue(bg2.vertexSet().contains(side));
		for(BipartiteEdge e : bg2.edgesOf(side)){
			assertTrue(e.isSide());
		}
	}
	
	@Test
	public void testDuplicateSideCompounds(){
		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
		bg2.addVertex(side);
		BipartiteEdge e1 = new BipartiteEdge(side, r1, false);
		e1.setSide(true);
		BipartiteEdge e2 = new BipartiteEdge(r2, side, true);
		bg2.addEdge(side, r1, e1);
		bg2.addEdge(r2, side, e2);
		
		bg2.duplicateSideCompounds();
		assertEquals(7, bg2.vertexSet().size());
		assertEquals(8, bg2.edgeSet().size());
		assertFalse(bg2.vertexSet().contains(side));
		
		for(BioPhysicalEntity v : bg2.compoundVertexSet()){
			if(v.getIsSide()){
				for(BipartiteEdge e : bg2.edgesOf(v)){
					assertTrue(e.isSide());
				}
			}
		}
	}
	
	@Test
	public void testReverseEdge(){
		BipartiteEdge r = bg.reverseEdge(e1);
		assertEquals(v1, r.getV2());
		assertEquals(r1, r.getV1());
		assertEquals(e1.getLabel(), r.getLabel());
		assertEquals(false, r.isReversible());
		assertEquals(false, r.isSide());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testaddEdge1(){
		BipartiteEdge e = new BipartiteEdge(v1, v2, true);
		e.toString();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testaddEdge2(){
		BipartiteEdge e = new BipartiteEdge(r1, r2, true);
		e.toString();
	}	
	
	@Test
	public void testaddEdge4(){
		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
		bg2.addEdge(v1, r1);
		bg2.addEdge(r1,v1);
		assertEquals(8, bg2.edgeSet().size());
	}
	
}
