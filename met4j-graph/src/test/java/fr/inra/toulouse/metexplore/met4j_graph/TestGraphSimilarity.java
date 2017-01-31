package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
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
		
		BioPhysicalEntity a = new BioPhysicalEntity("a");g1.addVertex(a);
		BioPhysicalEntity b = new BioPhysicalEntity("b");g1.addVertex(b);g2.addVertex(b);
		BioPhysicalEntity c = new BioPhysicalEntity("c");g1.addVertex(c);g2.addVertex(c);
		BioPhysicalEntity d = new BioPhysicalEntity("d");g1.addVertex(d);g2.addVertex(d);
		BioPhysicalEntity e = new BioPhysicalEntity("e");g1.addVertex(e);g2.addVertex(e);
		BioPhysicalEntity f = new BioPhysicalEntity("f");g2.addVertex(f);
		
		ReactionEdge ab = new ReactionEdge(a, b, new BioChemicalReaction());
		g1.addEdge(a, b, ab);
		ReactionEdge bc = new ReactionEdge(b, c, new BioChemicalReaction());
		g1.addEdge(b, c, bc);
		g2.addEdge(b, c, bc);
		ReactionEdge cd1 = new ReactionEdge(c, d, new BioChemicalReaction());
		ReactionEdge cd2 = new ReactionEdge(c, d, new BioChemicalReaction());
		g1.addEdge(c, d, cd1);
		g2.addEdge(c, d, cd2);
		ReactionEdge de = new ReactionEdge(d, e, new BioChemicalReaction());
		g1.addEdge(d, e, de);
		g2.addEdge(d, e, de);
		ReactionEdge ef = new ReactionEdge(e, f, new BioChemicalReaction());
		g2.addEdge(e, f, ef);
	}
	
	@Test
	public void testGetNumberOfSharedLinks() {
		GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph> gSim = new GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g1, g2);
		assertEquals(3, gSim.getNumberOfSharedLinks());
		
		GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph> gSim2 = new GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g2, g1);
		assertEquals(3, gSim2.getNumberOfSharedLinks());
	}
	
	
	@Test
	public void testGetTanimoto() {
		GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph> gSim = new GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g1, g2);
		assertEquals(3.0/5.0, gSim.getTanimoto(), 0.0000001);
		
		GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph> gSim2 = new GraphSimilarity<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g2, g1);
		assertEquals(3.0/5.0, gSim2.getTanimoto(), 0.0000001);
	}

}
