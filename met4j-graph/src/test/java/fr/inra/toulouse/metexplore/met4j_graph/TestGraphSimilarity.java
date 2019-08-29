package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.GraphSimilarity;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestGraphSimilarity {
	
	public static CompoundGraph g1;
	public static CompoundGraph g2;
	
	@BeforeClass
	public static void init(){
		
		g1 = new CompoundGraph();
		g2 = new CompoundGraph();
		
		BioMetabolite a = new BioMetabolite("a");g1.addVertex(a);
		BioMetabolite b = new BioMetabolite("b");g1.addVertex(b);g2.addVertex(b);
		BioMetabolite c = new BioMetabolite("c");g1.addVertex(c);g2.addVertex(c);
		BioMetabolite d = new BioMetabolite("d");g1.addVertex(d);g2.addVertex(d);
		BioMetabolite e = new BioMetabolite("e");g1.addVertex(e);g2.addVertex(e);
		BioMetabolite f = new BioMetabolite("f");g2.addVertex(f);
		
		ReactionEdge ab = new ReactionEdge(a, b, new BioReaction("r1"));
		g1.addEdge(a, b, ab);
		ReactionEdge bc = new ReactionEdge(b, c, new BioReaction("r2"));
		g1.addEdge(b, c, bc);
		g2.addEdge(b, c, bc);
		ReactionEdge cd1 = new ReactionEdge(c, d, new BioReaction("r3"));
		ReactionEdge cd2 = new ReactionEdge(c, d, new BioReaction("r4"));
		g1.addEdge(c, d, cd1);
		g2.addEdge(c, d, cd2);
		ReactionEdge de = new ReactionEdge(d, e, new BioReaction("r5"));
		g1.addEdge(d, e, de);
		g2.addEdge(d, e, de);
		ReactionEdge ef = new ReactionEdge(e, f, new BioReaction("r6"));
		g2.addEdge(e, f, ef);
	}
	
	@Test
	public void testGetNumberOfSharedLinks() {
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim = new GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph>(g1, g2);
		assertEquals(3, gSim.getNumberOfSharedLinks());
		
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim2 = new GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph>(g2, g1);
		assertEquals(3, gSim2.getNumberOfSharedLinks());
	}
	
	
	@Test
	public void testGetTanimoto() {
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim = new GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph>(g1, g2);
		assertEquals(3.0/5.0, gSim.getTanimoto(), 0.0000001);
		
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim2 = new GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph>(g2, g1);
		assertEquals(3.0/5.0, gSim2.getTanimoto(), 0.0000001);
	}

}
