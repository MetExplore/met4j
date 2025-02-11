/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

/*
 * 
 */
package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.parallel.MetaEdge;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertTrue;

public class TestEdgeMerger {
	
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
		
		a1 = new BioMetabolite("M_a_1");  bn.add(a1);
		bn.affectToCompartment(comp1, a1); 
		a1.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
		g.addVertex(a1);
		b1 = new BioMetabolite("M_b_1");   bn.add(b1);
		bn.affectToCompartment(comp1, b1);
		b1.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
		g.addVertex(b1);
		a2 = new BioMetabolite("M_a_2");   bn.add(a2);
		bn.affectToCompartment(comp2, a2);
		a2.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
		g.addVertex(a2);
		b2 = new BioMetabolite("M_b_2");  bn.add(b2);
		bn.affectToCompartment(comp2, b2);
		b2.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
		g.addVertex(b2);
		
		BioProtein p1 = new BioProtein("p1");
		bn.add(p1);
		BioEnzyme enz1 = new BioEnzyme("p1");
		bn.add(enz1);
		bn.affectSubUnit(enz1, 1.0, p1);
		
				
		BioReaction r1 = new BioReaction("ab1");r1.setReversible(false);
		bn.add(r1);
		bn.affectLeft(r1, 1.0, comp1, a1);
		bn.affectRight(r1, 1.0, comp1, b1);
		bn.affectEnzyme(r1, enz1);
		BioReaction r2 = new BioReaction("ab2");r2.setReversible(false);
		bn.add(r2);
		bn.affectLeft(r2, 1.0, comp1, a1);
		bn.affectRight(r2, 1.0, comp1, b1);
		BioReaction r3 = new BioReaction("ab3");r3.setReversible(true);
		bn.add(r3);
		bn.affectLeft(r3, 1.0, comp1, a1);
		bn.affectRight(r3, 1.0, comp1, b1);
		BioReaction r4 = new BioReaction("ab4");r4.setReversible(false);
		bn.add(r4);
		bn.affectLeft(r4, 1.0, comp2, a2);
		bn.affectRight(r4, 1.0, comp2, b2);
		BioReaction r5 = new BioReaction("ba2");r5.setReversible(false);
		bn.add(r5);
		bn.affectLeft(r5, 1.0, comp1, b1);
		bn.affectRight(r5, 1.0, comp2, a2);
		BioReaction r6 = new BioReaction("aa");r6.setReversible(true);
		bn.add(r6);
		bn.affectLeft(r6, 1.0, comp1, a1);
		bn.affectRight(r6, 1.0, comp2, a2);
		BioReaction r7 = new BioReaction("bb");r7.setReversible(true);
		bn.add(r7);
		bn.affectLeft(r7, 1.0, comp1, b1);
		bn.affectRight(r7, 1.0, comp2, b2);
		
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
	public void testMergeEdgeOverride() {
		CompoundGraph g2 = new CompoundGraph(g);
		EdgeMerger.mergeEdgesWithOverride(g2);
		Assert.assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		Assert.assertEquals("Wrong final number of edges", 8, g2.edgeSet().size());
	}

	@Test
	public void testMergeEdge() {
		MergedGraph<BioMetabolite, ReactionEdge> g3 = EdgeMerger.mergeEdges(g);
		MetaEdge<BioMetabolite, ReactionEdge> m = g3.getEdge(a1, b1);
//		assertEquals("wrong number of reaction in merged edge",3,m.getEdgeList().size());
		assertTrue("wrong edge merged",m.getEdgeList().contains(ab1));
		assertTrue("wrong edge merged",m.getEdgeList().contains(ab2));
		assertTrue("wrong edge merged",m.getEdgeList().contains(ab3));

	}

	@Test
	public void testMergeEdgeComparator() {
		CompoundGraph g2 = new CompoundGraph(g);
		EdgeMerger.mergeEdgesWithOverride(g2, new Comparator<>() {
			@Override
			public int compare(ReactionEdge reactionEdge, ReactionEdge t1) {
				return reactionEdge.toString().compareTo(t1.toString());
			}
		});
		Assert.assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		Assert.assertEquals("Wrong final number of edges", 8, g2.edgeSet().size());

	}

	@Test
	public void testMergeEdgeComparator1() {
		CompoundGraph g2 = new CompoundGraph(g);
		EdgeMerger.mergeEdgesWithOverride(g2, EdgeMerger.alphabeticalOrder());
		g2.setEdgeWeight(ab1,2.0);
		g2.setEdgeWeight(ab2,5.0);
		g2.setEdgeWeight(ab3,0.5);
		Assert.assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		Assert.assertEquals("Wrong final number of edges", 8, g2.edgeSet().size());
		Assert.assertTrue("Wrong edge kept", g2.containsEdge(ab1));
	}

	@Test
	public void testMergeEdgeComparator2() {
		CompoundGraph g2 = new CompoundGraph(g);
		g2.setEdgeWeight(ab1,2.0);
		g2.setEdgeWeight(ab2,5.0);
		g2.setEdgeWeight(ab3,0.5);
		EdgeMerger.mergeEdgesWithOverride(g2, EdgeMerger.highWeightFirst(g2));
		Assert.assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		Assert.assertEquals("Wrong final number of edges", 8, g2.edgeSet().size());
		Assert.assertTrue("Wrong edge kept", g2.containsEdge(ab2));
	}

	@Test
	public void testMergeEdgeComparator3() {
		CompoundGraph g2 = new CompoundGraph(g);
		g2.setEdgeWeight(ab1,2.0);
		g2.setEdgeWeight(ab2,5.0);
		g2.setEdgeWeight(ab3,0.5);
		EdgeMerger.mergeEdgesWithOverride(g2, EdgeMerger.lowWeightFirst(g2));
		Assert.assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		Assert.assertEquals("Wrong final number of edges", 8, g2.edgeSet().size());
		Assert.assertTrue("Wrong edge kept", g2.containsEdge(ab3));
	}

	@Test
	public void testMergeUndirected() {
		CompoundGraph g4 = new CompoundGraph(g);
		g4.setEdgeWeight(g4.getEdge(ba1.getV1(),ba1.getV2(),ba1.getReaction()),0.1);
		g4.setEdgeWeight(g4.getEdge(ab1.getV1(),ab1.getV2(),ab1.getReaction()),2.0);
		g4.setEdgeWeight(g4.getEdge(ab2.getV1(),ab2.getV2(),ab2.getReaction()),5.0);
		g4.setEdgeWeight(g4.getEdge(ab3.getV1(),ab3.getV2(),ab3.getReaction()),10.0);
		EdgeMerger.undirectedMergeEdgesWithOverride(g4, EdgeMerger.lowWeightFirst(g4));
		Assert.assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		Assert.assertEquals("Wrong final number of edges", 5, g4.edgeSet().size());
		Assert.assertTrue("Wrong edge kept", g4.containsEdge(ba1));
	}

}
