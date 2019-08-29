/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MetaEdge;
import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.Merger;

public class TestMerger {
	
	public static CompoundGraph g;
	public static BioNetwork bn;
	public static BioMetabolite a1,b1,a2,b2;
	public static ReactionEdge ab1,ab2,ab3,ab4,ba1,ba2,aa1,aa2,bb1,bb2;
	
	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		bn = new BioNetwork();
		
		BioCompartment comp1 = new BioCompartment("1", "1");
		BioCompartment comp2 = new BioCompartment("2", "2");
		bn.add(comp1);
		bn.add(comp2);
		
		a1 = new BioMetabolite("M_a_1"); 
		bn.affectToCompartment(a1, comp1); 
		a1.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
		g.addVertex(a1);bn.add(a1);
		b1 = new BioMetabolite("M_b_1");
		bn.affectToCompartment(b1,comp1);
		b1.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
		g.addVertex(b1);bn.add(b1);
		a2 = new BioMetabolite("M_a_2");
		bn.affectToCompartment(a2,comp2);
		a2.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
		g.addVertex(a2);bn.add(a2);
		b2 = new BioMetabolite("M_b_2");
		bn.affectToCompartment(b2,comp2);
		b2.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
		g.addVertex(b2);bn.add(b2);
		
		BioProtein p1 = new BioProtein("p1");
		bn.add(p1);
		BioEnzyme enz1 = new BioEnzyme("p1");
		bn.add(enz1);
		bn.affectSubUnit(p1, 1.0, enz1);
		
				
		BioReaction r1 = new BioReaction("ab1");r1.setReversible(false);
		bn.add(r1);
		bn.affectLeft(a1, 1.0, comp1, r1);
		bn.affectRight(b1, 1.0, comp1, r1);
		bn.affectEnzyme(enz1,r1);
		BioReaction r2 = new BioReaction("ab2");r2.setReversible(false);
		bn.add(r2);
		bn.affectLeft(a1, 1.0, comp1, r2);
		bn.affectRight(b1, 1.0, comp1, r2);
		BioReaction r3 = new BioReaction("ab3");r3.setReversible(true);
		bn.add(r3);
		bn.affectLeft(a1, 1.0, comp1, r3);
		bn.affectRight(b1, 1.0, comp1, r3);
		BioReaction r4 = new BioReaction("ab4");r4.setReversible(false);
		bn.add(r4);
		bn.affectLeft(a2, 1.0, comp2, r4);
		bn.affectRight(b2, 1.0, comp2, r4);
		BioReaction r5 = new BioReaction("ba2");r5.setReversible(false);
		bn.add(r5);
		bn.affectLeft(b1, 1.0, comp1, r5);
		bn.affectRight(a2, 1.0, comp2, r5);
		BioReaction r6 = new BioReaction("aa");r6.setReversible(true);
		bn.add(r6);
		bn.affectLeft(a1, 1.0, comp1, r6);
		bn.affectRight(a2, 1.0, comp2, r6);
		BioReaction r7 = new BioReaction("bb");r7.setReversible(true);
		bn.add(r7);
		bn.affectLeft(b1, 1.0, comp1, r7);
		bn.affectRight(b2, 1.0, comp2, r7);
		
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
		
		MergedGraph<BioMetabolite, ReactionEdge> g3 = Merger.mergeEdges(g);
		MetaEdge<BioMetabolite, ReactionEdge> m = g3.getEdge(a1, b1);
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
		for(BioMetabolite v : g2.vertexSet()){
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
		for(BioMetabolite v : g2.vertexSet()){
			assertEquals("Wrong compartment id", "metaComp", v.getCompartment().getId());
		}
	}
	
	@Test
	public void testMergeDuplicatedReation() {
		BioNetwork bn2 = new BioNetwork(bn, bn.getReaction().keySet(), bn.getPhysicalEntityList().keySet());
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
