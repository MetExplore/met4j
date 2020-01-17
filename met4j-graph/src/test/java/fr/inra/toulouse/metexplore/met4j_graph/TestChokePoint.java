package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.ChokePoint;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestChokePoint {

	public static CompoundGraph cg;
	public static BipartiteGraph bg;
	public static BioMetabolite v1,v2,v3,v4;
	public static BioReaction r1,r2,r3;
	public static BioNetwork bn;
	public static BioCompartment comp;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		bg = new BipartiteGraph();
		bn = new BioNetwork();
		comp = new BioCompartment("comp");bn.add(comp);
		v1 = new BioMetabolite("v1");bn.add(v1);bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2");bn.add(v2);bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3");bn.add(v3);bn.affectToCompartment(comp, v3);
		v4 = new BioMetabolite("v4");bn.add(v4);bn.affectToCompartment(comp, v4);
		
		r1 = new BioReaction("r1");bn.add(r1);
		bn.affectLeft(v1, 1.0, comp, r1);
		bn.affectRight(v2, 1.0, comp, r1);
		bn.affectRight(v3, 1.0, comp, r1);

		r2 = new BioReaction("r2");bn.add(r2);
		bn.affectLeft(v1, 1.0, comp, r2);
		bn.affectRight(v4, 1.0, comp, r2);
		bn.affectRight(v3, 1.0, comp, r2);

		r3 = new BioReaction("r3");bn.add(r3);
		bn.affectLeft(v2, 1.0, comp, r3);
		bn.affectLeft(v3, 1.0, comp, r3);
		bn.affectRight(v4, 1.0, comp, r3);

		
		ReactionEdge e1 = new ReactionEdge(v1, v2, r1);
		ReactionEdge e2 = new ReactionEdge(v1, v3, r1);
		ReactionEdge e3 = new ReactionEdge(v1, v2, r2);
		ReactionEdge e4 = new ReactionEdge(v1, v3, r2);
		ReactionEdge e5 = new ReactionEdge(v2, v4, r3);
		ReactionEdge e6 = new ReactionEdge(v3, v4, r3);
		
		cg.addVertex(v1);
		cg.addVertex(v2);
		cg.addVertex(v3);
		cg.addVertex(v4);
		cg.addEdge(v1, v2, e1);
		cg.addEdge(v1, v3, e2);
		cg.addEdge(v1, v2, e3);
		cg.addEdge(v1, v3, e4);
		cg.addEdge(v2, v4, e5);
		cg.addEdge(v3, v4, e6);
		
		BipartiteEdge be1 = new BipartiteEdge(v1, r1);
		BipartiteEdge be2 = new BipartiteEdge(v1, r2);
		BipartiteEdge be3 = new BipartiteEdge(v2, r3);
		BipartiteEdge be4 = new BipartiteEdge(v3, r3);
		BipartiteEdge be5 = new BipartiteEdge(r1, v2);
		BipartiteEdge be6 = new BipartiteEdge(r1, v3);
		BipartiteEdge be7 = new BipartiteEdge(r2, v2);
		BipartiteEdge be8 = new BipartiteEdge(r2, v3);
		BipartiteEdge be9 = new BipartiteEdge(r3, v4);
		
		bg.addVertex(v1);
		bg.addVertex(v2);
		bg.addVertex(v3);
		bg.addVertex(v4);
		bg.addVertex(r1);
		bg.addVertex(r2);
		bg.addVertex(r3);
		bg.addEdge(v1, r1, be1);
		bg.addEdge(v1, r2, be2);
		bg.addEdge(v2, r3, be3);
		bg.addEdge(v3, r3, be4);
		bg.addEdge(r1, v2, be5);
		bg.addEdge(r1, v3, be6);
		bg.addEdge(r2, v2, be7);
		bg.addEdge(r2, v3, be8);
		bg.addEdge(r3, v4, be9);

	}
	
	@Test
	public void testGetChokePointFromCompoundGraph() {
		HashSet<BioReaction> cp = ChokePoint.getChokePoint(cg);
		assertEquals(1, cp.size());
		assertTrue(cp.contains(r3));
	}
	
	@Test
	public void testGetChokePointFromBipartiteGraph() {
		HashSet<BioReaction> cp = ChokePoint.getChokePoint(bg);
		assertEquals(1, cp.size());
		assertTrue(cp.contains(r3));
	}
	
}
