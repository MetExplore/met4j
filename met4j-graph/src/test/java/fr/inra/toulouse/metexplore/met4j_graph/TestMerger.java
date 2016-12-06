/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MetaEdge;
import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.Merger;

public class TestMerger {
	
	public static CompoundGraph g;
	public static BioNetwork bn;
	public static BioPhysicalEntity a1,b1,a2,b2;
	public static ReactionEdge ab1,ab2,ab3,ab4,ba1,ba2,aa1,aa2,bb1,bb2;
	
	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		bn = new BioNetwork();
		
		BioCompartment comp1 = new BioCompartment("1", "1");
		BioCompartment comp2 = new BioCompartment("2", "2");
		bn.addCompartment(comp1);
		bn.addCompartment(comp2);
		
		a1 = new BioPhysicalEntity("M_a_1"); 
		a1.setCompartment(comp1); 
		a1.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
		g.addVertex(a1);bn.addPhysicalEntity(a1);
		b1 = new BioPhysicalEntity("M_b_1");
		b1.setCompartment(comp1);
		b1.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
		g.addVertex(b1);bn.addPhysicalEntity(b1);
		a2 = new BioPhysicalEntity("M_a_2");
		a2.setCompartment(comp2);
		a2.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
		g.addVertex(a2);bn.addPhysicalEntity(a2);
		b2 = new BioPhysicalEntity("M_b_2");
		b2.setCompartment(comp2);
		b2.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
		g.addVertex(b2);bn.addPhysicalEntity(b2);
		
		
		BioChemicalReaction r1 = new BioChemicalReaction("ab1");r1.setReversibility(false);
		bn.addBiochemicalReaction(r1);
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(a1));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(b1));
		BioChemicalReaction r2 = new BioChemicalReaction("ab2");r2.setReversibility(false);
		bn.addBiochemicalReaction(r2);
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(a1));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(b1));
		BioChemicalReaction r3 = new BioChemicalReaction("ab3");r3.setReversibility(true);
		bn.addBiochemicalReaction(r3);
		r3.addLeftParticipant(new BioPhysicalEntityParticipant(a1));
		r3.addRightParticipant(new BioPhysicalEntityParticipant(b1));
		BioChemicalReaction r4 = new BioChemicalReaction("ab4");r4.setReversibility(false);
		bn.addBiochemicalReaction(r4);
		r4.addLeftParticipant(new BioPhysicalEntityParticipant(a2));
		r4.addRightParticipant(new BioPhysicalEntityParticipant(b2));
		BioChemicalReaction r5 = new BioChemicalReaction("ba2");r5.setReversibility(false);
		bn.addBiochemicalReaction(r5);
		r5.addLeftParticipant(new BioPhysicalEntityParticipant(b1));
		r5.addRightParticipant(new BioPhysicalEntityParticipant(a2));
		BioChemicalReaction r6 = new BioChemicalReaction("aa");r6.setReversibility(true);
		bn.addBiochemicalReaction(r6);
		r6.addLeftParticipant(new BioPhysicalEntityParticipant(a1));
		r6.addRightParticipant(new BioPhysicalEntityParticipant(a2));
		BioChemicalReaction r7 = new BioChemicalReaction("bb");r7.setReversibility(true);
		bn.addBiochemicalReaction(r7);
		r7.addLeftParticipant(new BioPhysicalEntityParticipant(b1));
		r7.addRightParticipant(new BioPhysicalEntityParticipant(b2));
		
		ab1 = new ReactionEdge(a1,b1,r1);g.addEdge(a1, b1, ab1);
		ab2 = new ReactionEdge(a1,b1,r2);g.addEdge(a1, b1, ab2);
		ab3 = new ReactionEdge(a1,b1,r3);g.addEdge(a1, b1, ab3);
		ab4 = new ReactionEdge(a2,b2,r4);g.addEdge(a2, b2, ab4);
		ba1 = new ReactionEdge(b1,a1,r3);g.addEdge(b1, a1, ba1);
		ba2 = new ReactionEdge(b1,a2,r5);g.addEdge(b1, a2, ba2);
		aa1 = new ReactionEdge(a1,a2,r6);g.addEdge(a1,a2, aa1);
		aa2 = new ReactionEdge(a2,a1,r6);g.addEdge(a2,a1, aa2);
		bb1 = new ReactionEdge(b1,b2,r7);g.addEdge(b1,b2, bb1);
		bb2 = new ReactionEdge(b2,b1,r7);g.addEdge(b2,b1, bb2);
	}
	
	@Test
	public void testMergeEdge() {
		CompoundGraph g2 = new CompoundGraph(g);
		Merger.mergeEdges(g2);
		assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		assertEquals("Wrong final number of edges", 8, g2.edgeSet().size());
		
		MergedGraph<BioPhysicalEntity, ReactionEdge> g3 = Merger.mergeEdgesII(g);
		MetaEdge<BioPhysicalEntity, ReactionEdge> m = g3.getEdge(a1, b1);
		assertEquals("wrong number of reaction in merged edge",3,m.getEdgeList().size());
		assertTrue("wrong edge merged",m.getEdgeList().contains(ab1));
		assertTrue("wrong edge merged",m.getEdgeList().contains(ab2));
		assertTrue("wrong edge merged",m.getEdgeList().contains(ab3));

	}
	
	@Test
	public void testMergeCompartment() {
		CompoundGraph g2 = Merger.mergeCompartment(g);
		assertEquals("Error while creating the initial graph", 4, g.vertexSet().size());
		assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		assertEquals("Wrong final number of nodes", 2, g2.vertexSet().size());
		assertEquals("Wrong final number of edges", 6, g2.edgeSet().size());
		for(BioPhysicalEntity v : g2.vertexSet()){
			assertEquals("Wrong compartment id", "metaComp", v.getCompartment().getId());
		}
	}
	
	@Test
	public void testMergeCompartmentFromId() {
		CompoundGraph g2 = Merger.mergeCompartmentFromId(g, "^(.+)_\\w$");
		assertEquals("Error while creating the initial graph", 4, g.vertexSet().size());
		assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		assertEquals("Wrong final number of nodes", 2, g2.vertexSet().size());
		assertEquals("Wrong final number of edges", 6, g2.edgeSet().size());
		assertNotNull("Wrong vertex id in merged graph", g2.getVertex("M_a"));
		assertNotNull("Wrong vertex id in merged graph", g2.getVertex("M_b"));
		for(BioPhysicalEntity v : g2.vertexSet()){
			assertEquals("Wrong compartment id", "metaComp", v.getCompartment().getId());
		}
	}
	
	@Test
	public void testMergeDuplicatedReation() {
		BioNetwork bn2 = new BioNetwork(bn, bn.getBiochemicalReactionList().keySet(), bn.getPhysicalEntityList().keySet());
		int removed = Merger.mergeReactions(bn2);
		assertEquals("wrong number of removed reaction after merging",1, removed); //ab3 is consider different from ab2 and ab1 as their reversibility are different
		assertTrue("duplicated reaction not merged",!bn2.getBiochemicalReactionList().containsKey("ab1") || !bn2.getBiochemicalReactionList().containsKey("ab2"));
	}
	
	@Test
	public void testTransportReationRemoving() {
		BioNetwork bn2 = Merger.mergeCompartmentFromId(bn, "^(.+)_\\w$");
		assertEquals("Error while creating the initial bionetwork", 4, bn.getPhysicalEntityList().size());
		assertEquals("Error while creating the initial bionetwork", 7, bn.getBiochemicalReactionList().size());
		assertEquals("Wrong final number of compounds", 2,  bn2.getPhysicalEntityList().size());
		assertEquals("Wrong final number of reaction", 7, bn2.getBiochemicalReactionList().size());
		assertNotNull("Wrong compound id in merged graph", bn2.getBioPhysicalEntityById("M_a"));
		assertNotNull("Wrong compound id in merged graph", bn2.getBioPhysicalEntityById("M_b"));
		
		int removed = Merger.removeTransport(bn2);
		assertEquals("wrong number of removed reaction after merging",2, removed);
		assertTrue("transport reaction not removed",!bn2.getBiochemicalReactionList().containsKey("aa"));
		assertTrue("transport reaction not removed",!bn2.getBiochemicalReactionList().containsKey("bb"));
	}
}
