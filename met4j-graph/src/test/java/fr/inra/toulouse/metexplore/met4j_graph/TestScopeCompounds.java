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
