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

package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.*;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollections;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inra.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.ScopeCompounds;

public class TestScopeCompounds {
	
	
	public static BioMetabolite v0,v1,v2,v3,v4,v5,v6,v7,side,side2;
	public static BioReaction r0,r1,r2,r3,r4,r5,r6;
	public static BioCompartment comp;
	public static BipartiteGraph g;
	public static BioNetwork bn;
	public static BioCollection<BioMetabolite> in;
	public static BioCollection<BioMetabolite> bs;
	public static BioCollection<BioMetabolite> cpdToReach;
	public static BioCollection<BioReaction> reactionsToAvoid;
	
	@BeforeClass
	public static void init(){
		bn=new BioNetwork();

		comp = new BioCompartment("comp");
		bn.add(comp);

		v0 = new BioMetabolite("v0");
		v1 = new BioMetabolite("v1");
		v2 = new BioMetabolite("v2");
		v3 = new BioMetabolite("v3");
		v4 = new BioMetabolite("v4");
		v5 = new BioMetabolite("v5");
		v6 = new BioMetabolite("v6");
		v7 = new BioMetabolite("v7");
		side = new BioMetabolite("side");
		side2 = new BioMetabolite("side2");

		bn.add(v0,v1,v2,v3,v4,v5,v6,v7,side,side2);
		bn.affectToCompartment(comp, v0,v1,v2,v3,v4,v5,v6,v7,side,side2);

		r0 = new BioReaction("r0");
		r1 = new BioReaction("r1");
		r2 = new BioReaction("r2");
		r3 = new BioReaction("r3");
		r4 = new BioReaction("r4");
		r5 = new BioReaction("r5");
		r6 = new BioReaction("r6");
		bn.add(r0,r1,r2,r3,r4,r5,r6);


		bn.affectLeft(v0,1.0,comp,r0);
		bn.affectRight(v1,1.0,comp,r0);
		r0.setReversible(false);

		bn.affectLeft(v1,1.0,comp,r1);
		bn.affectRight(v3,1.0,comp,r1);
		bn.affectRight(v4,1.0,comp,r1);
		r1.setReversible(true);
		

		bn.affectLeft(v1,1.0,comp,r2);
		bn.affectLeft(v2,1.0,comp,r2);
		bn.affectRight(v3,1.0,comp,r2);
		bn.affectRight(v4,1.0,comp,r2);
		r2.setReversible(false);

		bn.affectRight(v3,1.0,comp,r3);
		bn.affectRight(v4,1.0,comp,r3);
		bn.affectRight(side,1.0,comp,r3);
		bn.affectLeft(v5,1.0,comp,r3);
		r3.setReversible(true);
		

		bn.affectLeft(side,1.0,comp,r4);
		bn.affectRight(side2,1.0,comp,r4);
		r4.setReversible(false);

		bn.affectLeft(v3,1.0,comp,r5);
		bn.affectRight(v6,1.0,comp,r5);
		r5.setReversible(false);

		bn.affectLeft(v5,1.0,comp,r6);
		bn.affectRight(v7,1.0,comp,r6);
		r6.setReversible(false);

		g = new Bionetwork2BioGraph(bn).getBipartiteGraph();

		in = new BioCollection<BioMetabolite>();
			in.add(v1);
		bs = new BioCollection<BioMetabolite>();
			bs.add(side);
		cpdToReach = new BioCollection<BioMetabolite>();
			cpdToReach.add(v5);
			cpdToReach.add(v6);
		reactionsToAvoid = new BioCollection<BioReaction>();
			reactionsToAvoid.add(r5);
	}

	@Test
	public void testGetScopeNetwork1() {
		ScopeCompounds sc = new ScopeCompounds(g, in, bs, cpdToReach, reactionsToAvoid);
		BipartiteGraph scope = sc.getScopeNetwork();

		System.err.println(scope.vertexSet().toString());

		assertEquals(4, scope.compoundVertexSet().size());
		assertEquals(2, scope.reactionVertexSet().size());

		assertTrue(scope.compoundVertexSet().contains(v1));
		assertTrue(scope.compoundVertexSet().contains(v3));
		assertTrue(scope.compoundVertexSet().contains(v4));
		assertTrue(scope.compoundVertexSet().contains(v5));

		assertTrue(scope.reactionVertexSet().contains(r1));
		assertTrue(scope.reactionVertexSet().contains(r3));
	}

	@Test
	public void testGetScopeNetwork2() {
		ScopeCompounds sc = new ScopeCompounds(g, in, bs, reactionsToAvoid);
		BipartiteGraph scope = sc.getScopeNetwork();

		System.err.println(scope.vertexSet().toString());

		assertEquals(5, scope.compoundVertexSet().size());
		assertEquals(3, scope.reactionVertexSet().size());

		assertTrue(scope.compoundVertexSet().contains(v1));
		assertTrue(scope.compoundVertexSet().contains(v3));
		assertTrue(scope.compoundVertexSet().contains(v4));
		assertTrue(scope.compoundVertexSet().contains(v5));
		assertTrue(scope.compoundVertexSet().contains(v7));

		assertTrue(scope.reactionVertexSet().contains(r1));
		assertTrue(scope.reactionVertexSet().contains(r3));
		assertTrue(scope.reactionVertexSet().contains(r6));
	}

	@Test
	public void testGetScopeNetwork3() {
		BioCollection<BioMetabolite> toReach = new BioCollection<BioMetabolite>();
		toReach.add(v5);
		ScopeCompounds sc = new ScopeCompounds(g, in, bs, toReach, reactionsToAvoid);
		BipartiteGraph scope = sc.getScopeNetwork();

		System.err.println(scope.vertexSet().toString());

		assertEquals(4, scope.compoundVertexSet().size());
		assertEquals(2, scope.reactionVertexSet().size());

		assertTrue(scope.compoundVertexSet().contains(v1));
		assertTrue(scope.compoundVertexSet().contains(v3));
		assertTrue(scope.compoundVertexSet().contains(v4));
		assertTrue(scope.compoundVertexSet().contains(v5));

		assertTrue(scope.reactionVertexSet().contains(r1));
		assertTrue(scope.reactionVertexSet().contains(r3));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetScopeNetwork4() {

		BipartiteGraph ig = new GraphFactory<BioEntity, BipartiteEdge, BipartiteGraph>() {
			@Override
			public BipartiteGraph createGraph() {
				return new BipartiteGraph();
			}
		}.reverse(g);
		ScopeCompounds sc = new ScopeCompounds(ig, cpdToReach, bs, in, reactionsToAvoid);
		BipartiteGraph scope = sc.getScopeNetwork();


	}

}
