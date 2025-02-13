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
package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestVertexContraction {
    public static CompoundGraph g;
    public static BioNetwork bn;
    public static BioMetabolite a1,b1,a2,b2,a3;
    public static ReactionEdge ab1,ab2,ab3,ab4,ba1,ba2,aa1,aa2,bb1,bb2,aa3,aa4;

    @BeforeClass
    public static void init(){
        g = new CompoundGraph();
        bn = new BioNetwork();

        BioCompartment comp1 = new BioCompartment("1", "1");
        BioCompartment comp2 = new BioCompartment("2", "2");
        BioCompartment comp3 = new BioCompartment("3", "3");
        bn.add(comp1);
        bn.add(comp2);
        bn.add(comp3);

        a1 = new BioMetabolite("M_a_1");  bn.add(a1);
        bn.affectToCompartment(comp1, a1);
        a1.setName("A");
        a1.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
        g.addVertex(a1);
        b1 = new BioMetabolite("M_b_1");   bn.add(b1);
        bn.affectToCompartment(comp1, b1);
        b1.setName("B");
        b1.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
        g.addVertex(b1);
        a2 = new BioMetabolite("M_a_2");   bn.add(a2);
        bn.affectToCompartment(comp2, a2);
        a2.setName("A");
        a2.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
        g.addVertex(a2);
        b2 = new BioMetabolite("M_b_2");  bn.add(b2);
        bn.affectToCompartment(comp2, b2);
        b2.setName("B");
        b2.setInchi("InChI=1S/C4H10NO6P/c5-3(4(6)7)1-2-11-12(8,9)10/h3H,1-2,5H2,(H,6,7)(H2,8,9,10)/t3-/m0/s1");
        g.addVertex(b2);
        a3 = new BioMetabolite("M_a_3");  bn.add(a3);
        bn.affectToCompartment(comp3, a3);
        a3.setName("A");
        a3.setInchi("InChI=1S/C4H6O4/c1-2(3(5)6)4(7)8/h2H,1H3,(H,5,6)(H,7,8)");
        g.addVertex(a3);

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
        BioReaction r8 = new BioReaction("aa2");r8.setReversible(false);
        bn.add(r8);
        bn.affectLeft(r8, 1.0, comp3, a3);
        bn.affectRight(r8, 1.0, comp1, a1);
        BioReaction r9 = new BioReaction("aa3");r9.setReversible(false);
        bn.add(r9);
        bn.affectLeft(r9, 1.0, comp3, a3);
        bn.affectRight(r9, 1.0, comp2, a2);

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
        aa3 = new ReactionEdge(a3,a1,r8);g.addEdge(a3,a1, aa3);
        aa4 = new ReactionEdge(a3,a2,r9);g.addEdge(a3,a2, aa4);

    }

	@Test
	public void testDecompartmentalize() {
        VertexContraction vc = new VertexContraction();
		CompoundGraph g2 = vc.decompartmentalize(g);
		assertEquals("Error in the initial graph", 5, g.vertexSet().size());
		assertEquals("Error in the initial graph", 12, g.edgeSet().size());
		assertEquals("Wrong final number of nodes", 2, g2.vertexSet().size());
		assertEquals("Wrong final number of edges", 6, g2.edgeSet().size());
	}

    @Test
    public void testDecompartmentalizeByName() {
        VertexContraction vc = new VertexContraction();
        CompoundGraph g2 = vc.decompartmentalize(g, new VertexContraction.MapByName());
        assertEquals("Error in the initial graph", 5, g.vertexSet().size());
        assertEquals("Error in the initial graph", 12, g.edgeSet().size());
        assertEquals("Wrong final number of nodes", 2, g2.vertexSet().size());
        assertEquals("Wrong final number of edges", 6, g2.edgeSet().size());
    }

    @Test
    public void testDecompartmentalizeByInChI() {
        VertexContraction vc = new VertexContraction();
        CompoundGraph g2 = vc.decompartmentalize(g, new VertexContraction.MapByInChI());
        assertEquals("Error in the initial graph", 5, g.vertexSet().size());
        assertEquals("Error in the initial graph", 12, g.edgeSet().size());
        assertEquals("Wrong final number of nodes", 2, g2.vertexSet().size());
        assertEquals("Wrong final number of edges", 6, g2.edgeSet().size());
    }

    @Test
    public void testDecompartmentalizeByIdSubString() {
        VertexContraction vc = new VertexContraction();
        CompoundGraph g2 = vc.decompartmentalize(g, new VertexContraction.MapByIdSubString("(M_\\w+)_\\d"));
        assertEquals("Error in the initial graph", 5, g.vertexSet().size());
        assertEquals("Error in the initial graph", 12, g.edgeSet().size());
        assertEquals("Wrong final number of nodes", 2, g2.vertexSet().size());
        assertEquals("Wrong final number of edges", 6, g2.edgeSet().size());
    }

/*	@Test
	public void testMergeCompartmentFromId() {
		CompoundGraph g2 = Merger.mergeCompartmentFromId(g, "^(.+)_\\w$");
		assertEquals("Error while creating the initial graph", 4, g.vertexSet().size());
		assertEquals("Error while creating the initial graph", 10, g.edgeSet().size());
		assertEquals("Wrong final number of nodes", 2, g2.vertexSet().size());
		assertEquals("Wrong final number of edges", 6, g2.edgeSet().size());
		assertNotNull("Wrong vertex id in merged graph", g2.getVertex("M_a"));
		assertNotNull("Wrong vertex id in merged graph", g2.getVertex("M_b"));
	}

	@Test
	public void testMergeDuplicatedReation() {
		BioNetwork bn2 = new BioNetwork(bn, bn.getReactions(), bn.getMe.keySet());
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
	}*/
}
