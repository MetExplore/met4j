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
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.PrecursorNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.ScopeCompounds;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPrecursorNetwork {
	
	
	public static BioMetabolite v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,side;
	public static BioReaction r1,r2,r3,r4,r5,r6,r7,ex,no;
	public static BioCompartment comp;
	public static BipartiteGraph g;
	public static BioNetwork bn;
	public static BioCollection<BioMetabolite> bs;
	public static BioCollection<BioMetabolite> cpdToReach;
	public static BioCollection<BioReaction> reactionsToAvoid;
	
	@BeforeClass
	public static void init(){
		bn=new BioNetwork();

		comp = new BioCompartment("comp");
		bn.add(comp);

		v1 = new BioMetabolite("v1");
		v2 = new BioMetabolite("v2");
		v3 = new BioMetabolite("v3");
		v4 = new BioMetabolite("v4");
		v5 = new BioMetabolite("v5");
		v6 = new BioMetabolite("v6");
		v7 = new BioMetabolite("v7");
		v8 = new BioMetabolite("v8");
		v9 = new BioMetabolite("v9");
		v10 = new BioMetabolite("v10");
		v11 = new BioMetabolite("v11");
		side = new BioMetabolite("side");

		bn.add(v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,side);
		bn.affectToCompartment(comp, v1,v2,v3,v4,v5,v6,v7,v8,v9,v10,v11,side);

		r1 = new BioReaction("r1");
		r2 = new BioReaction("r2");
		r3 = new BioReaction("r3");
		r4 = new BioReaction("r4");
		r5 = new BioReaction("r5");
		r6 = new BioReaction("r6");
		r7 = new BioReaction("r7");
		ex = new BioReaction("ex");
		no = new BioReaction("no");
		bn.add(r1,r2,r3,r4,r5,r6,r7,ex,no);


		bn.affectLeft(r1, 1.0, comp, v1);
		bn.affectRight(r1, 1.0, comp, v3);
		r1.setReversible(false);

		bn.affectLeft(r2, 1.0, comp, v2);
		bn.affectRight(r2, 1.0, comp, side);
		r2.setReversible(false);

		bn.affectLeft(r3, 1.0, comp, v3);
		bn.affectRight(r3, 1.0, comp, v5);
		r3.setReversible(false);

		bn.affectLeft(r4, 1.0, comp, side);
		bn.affectLeft(r4, 1.0, comp, v3);
		bn.affectRight(r4, 1.0, comp, v6);
		r4.setReversible(false);

		bn.affectLeft(r5, 1.0, comp, side);
		bn.affectLeft(r5, 1.0, comp, v4);
		bn.affectRight(r5, 1.0, comp, v7);
		r5.setReversible(false);

		bn.affectLeft(r6, 1.0, comp, v6);
		bn.affectRight(r6, 1.0, comp, v9);
		r6.setReversible(false);

		bn.affectLeft(r7, 1.0, comp, v7);
		bn.affectLeft(r7, 1.0, comp, v8);
		bn.affectRight(r7, 1.0, comp, v10);
		r7.setReversible(true);

		bn.affectRight(ex, 1.0, comp, v1);
		ex.setReversible(false);

		bn.affectLeft(no, 1.0, comp, v11);
		bn.affectRight(no, 1.0, comp, v4);
		no.setReversible(false);

		g = new Bionetwork2BioGraph(bn).getBipartiteGraph();


		bs = new BioCollection<>();
			bs.add(side);
		cpdToReach = new BioCollection<>();
			cpdToReach.add(v6);
			cpdToReach.add(v7);
		reactionsToAvoid = new BioCollection<>();
			reactionsToAvoid.add(no);
	}

	@Test
	public void testGetScopeNetwork1() {
		PrecursorNetwork pn = new PrecursorNetwork(g,bs,cpdToReach,reactionsToAvoid);
		BipartiteGraph precursor = pn.getPrecursorNetwork();

		System.err.println(precursor.edgeSet().toString());

		assertEquals(6, precursor.compoundVertexSet().size());
		assertEquals(5, precursor.reactionVertexSet().size());
		assertEquals(11, precursor.edgeSet().size());

		assertTrue(precursor.compoundVertexSet().contains(v1));
		assertTrue(precursor.compoundVertexSet().contains(v3));
		assertTrue(precursor.compoundVertexSet().contains(v4));
		assertTrue(precursor.compoundVertexSet().contains(v6));
		assertTrue(precursor.compoundVertexSet().contains(v7));
		assertTrue(precursor.compoundVertexSet().contains(v10));

		assertTrue(precursor.reactionVertexSet().contains(ex));
		assertTrue(precursor.reactionVertexSet().contains(r1));
		assertTrue(precursor.reactionVertexSet().contains(r4));
		assertTrue(precursor.reactionVertexSet().contains(r5));
		assertTrue(precursor.reactionVertexSet().contains(r7));

		BioCollection<BioReaction> e = PrecursorNetwork.getExchangeReactions(precursor);
		System.err.println(e.toString());

		System.out.println(precursor.degreeOf(r4));
		assertEquals(1, e.size());
		assertTrue(e.contains(ex));

		BioCollection<BioMetabolite> p = PrecursorNetwork.getPrecursors(precursor);
		System.err.println(p.toString());

		assertEquals(3, p.size());
		assertTrue(p.contains(v1));
		assertTrue(p.contains(v4));
		assertTrue(p.contains(v10));
	}



}
