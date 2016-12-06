package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.GraphCentralityMeasure;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.GraphMeasure;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestGraphCentrality {
	
	public static CompoundGraph toyGraph;
	public static CompoundGraph linearGraph;
	public static CompoundGraph starGraph;
	public static BioPhysicalEntity a,b,c,d,e,f,h,g;
	public static GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph> toyMeasure;
	public static GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph> linearMeasure;
	public static GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph> starMeasure;
	
	@BeforeClass
	public static void init(){
		
		a = new BioPhysicalEntity("a");
		b = new BioPhysicalEntity("b");
		c = new BioPhysicalEntity("c");
		d = new BioPhysicalEntity("d");
		e = new BioPhysicalEntity("e"); 
		f = new BioPhysicalEntity("f"); 
		h = new BioPhysicalEntity("h"); 
		g = new BioPhysicalEntity("g"); 
		ReactionEdge ab = new ReactionEdge(a, b, new BioChemicalReaction());
		ReactionEdge bc = new ReactionEdge(b, c, new BioChemicalReaction());
		ReactionEdge cd = new ReactionEdge(c, d, new BioChemicalReaction());
		ReactionEdge de = new ReactionEdge(d, e, new BioChemicalReaction());
		ReactionEdge ec = new ReactionEdge(e, c, new BioChemicalReaction());
		ReactionEdge af = new ReactionEdge(a, f, new BioChemicalReaction());
		ReactionEdge fg = new ReactionEdge(f, g, new BioChemicalReaction());
		ReactionEdge ba = new ReactionEdge(b, a, new BioChemicalReaction());
		ReactionEdge ca = new ReactionEdge(c, a, new BioChemicalReaction());
		ReactionEdge da = new ReactionEdge(d, a, new BioChemicalReaction());
		ReactionEdge ea = new ReactionEdge(e, a, new BioChemicalReaction());
		ReactionEdge fa = new ReactionEdge(f, a, new BioChemicalReaction());
		ReactionEdge ga = new ReactionEdge(g, a, new BioChemicalReaction());
		ReactionEdge cb = new ReactionEdge(c, b, new BioChemicalReaction());
		ReactionEdge dc = new ReactionEdge(d, c, new BioChemicalReaction());
		ReactionEdge ed = new ReactionEdge(e, d, new BioChemicalReaction());
		ReactionEdge ce = new ReactionEdge(c, e, new BioChemicalReaction());
		ReactionEdge gf = new ReactionEdge(g, f, new BioChemicalReaction());
		ReactionEdge ac = new ReactionEdge(a, c, new BioChemicalReaction());
		ReactionEdge ad = new ReactionEdge(a, d, new BioChemicalReaction());
		ReactionEdge ae = new ReactionEdge(a, e, new BioChemicalReaction());
		ReactionEdge ag = new ReactionEdge(a, g, new BioChemicalReaction());
		
		
		toyGraph = new CompoundGraph();
		toyGraph.addVertex(a);
		toyGraph.addVertex(b);
		toyGraph.addVertex(c);
		toyGraph.addVertex(d);
		toyGraph.addVertex(e);
		toyGraph.addVertex(f);
		toyGraph.addVertex(g);
		toyGraph.addEdge(a, b, ab);
		toyGraph.addEdge(b, c, bc);
		toyGraph.addEdge(c, d, cd);
		toyGraph.addEdge(d, e, de);
		toyGraph.addEdge(e, c, ec);
		toyGraph.addEdge(a, f, af);
		toyGraph.addEdge(f, g, fg);
		toyGraph.addEdge(g, a, ga);
		toyGraph.addEdge(b, a, ba);
		toyGraph.addEdge(c, b, cb);
		toyGraph.addEdge(d, c, dc);
		toyGraph.addEdge(e, d, ed);
		toyGraph.addEdge(c, e, ce);
		toyGraph.addEdge(f, a, fa);
		toyGraph.addEdge(g, f, gf);
		toyGraph.addEdge(a, g, ag);
		
		starGraph = new CompoundGraph();
		starGraph.addVertex(a);
		starGraph.addVertex(b);
		starGraph.addVertex(c);
		starGraph.addVertex(d);
		starGraph.addVertex(e);
		starGraph.addVertex(f);
		starGraph.addEdge(b, a, ba);
		starGraph.addEdge(c, a, ca);
		starGraph.addEdge(d, a, da);
		starGraph.addEdge(e, a, ea);
		starGraph.addEdge(f, a, fa);
		starGraph.addEdge(a, b, ab);
		starGraph.addEdge(a, c, ac);
		starGraph.addEdge(a, d, ad);
		starGraph.addEdge(a, e, ae);
		starGraph.addEdge(a, f, af);
		
		linearGraph = new CompoundGraph();
		linearGraph.addVertex(a);
		linearGraph.addVertex(b);
		linearGraph.addVertex(c);
		linearGraph.addVertex(d);
		linearGraph.addVertex(e);
		linearGraph.addEdge(a, b, ab);
		linearGraph.addEdge(b, c, bc);
		linearGraph.addEdge(c, d, cd);
		linearGraph.addEdge(d, e, de);
		linearGraph.addEdge(b, a, ba);
		linearGraph.addEdge(c, b, cb);
		linearGraph.addEdge(d, c, dc);
		linearGraph.addEdge(e, d, ed);
		
		
		toyMeasure = new GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph>(toyGraph);
		linearMeasure = new GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph>(linearGraph);
		starMeasure = new GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph>(starGraph);
	}
	@Test
	public void testBetweenness() {
		Map<BioPhysicalEntity, Integer> toyBetweenness = toyMeasure.getGeodesicBetweenness();
		Map<BioPhysicalEntity, Integer> linearBetweenness = linearMeasure.getGeodesicBetweenness();
		Map<BioPhysicalEntity, Integer> starBetweenness = starMeasure.getGeodesicBetweenness();
		
		assertEquals(8.0*2, toyBetweenness.get(a), 0.00000001);
		assertEquals(9.0*2, toyBetweenness.get(b), 0.00000001);
		assertEquals(8.0*2, toyBetweenness.get(c), 0.00000001);
		assertEquals(0.0, toyBetweenness.get(d), 0.00000001);
		assertEquals(0.0, toyBetweenness.get(e), 0.00000001);
		assertEquals(0.0, toyBetweenness.get(f), 0.00000001);
		assertEquals(0.0, toyBetweenness.get(g), 0.00000001);
		
		assertEquals(0.0, linearBetweenness.get(a), 0.00000001);
		assertEquals(3.0*2, linearBetweenness.get(b), 0.00000001);
		assertEquals(4.0*2, linearBetweenness.get(c), 0.00000001);
		assertEquals(3.0*2, linearBetweenness.get(d), 0.00000001);
		assertEquals(0.0, linearBetweenness.get(e), 0.00000001);
		
		assertEquals(10.0*2, starBetweenness.get(a), 0.00000001);
		assertEquals(0.0, starBetweenness.get(b), 0.00000001);
		assertEquals(0.0, starBetweenness.get(c), 0.00000001);
		assertEquals(0.0, starBetweenness.get(d), 0.00000001);
		assertEquals(0.0, starBetweenness.get(e), 0.00000001);
		assertEquals(0.0, starBetweenness.get(f), 0.00000001);		
	}
	
	@Test
	public void testCloseness() {
		Map<BioPhysicalEntity, Double> toyCloseness = toyMeasure.getInCloseness(toyMeasure.getAllShortestPaths());
		Map<BioPhysicalEntity, Double> linearCloseness = linearMeasure.getInCloseness(linearMeasure.getAllShortestPaths());
		Map<BioPhysicalEntity, Double> starCloseness= starMeasure.getInCloseness(starMeasure.getAllShortestPaths());
		
		assertEquals(0.55/6.0, toyCloseness.get(a), 0.01);
		assertEquals(0.6/6.0, toyCloseness.get(b), 0.01);
		assertEquals(0.55/6.0, toyCloseness.get(c), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(d), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(e), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(f), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(g), 0.01);
		
		assertEquals(0.4/4.0, linearCloseness.get(a), 0.01);
		assertEquals(0.57/4.0, linearCloseness.get(b), 0.01);
		assertEquals(0.67/4.0, linearCloseness.get(c), 0.01);
		assertEquals(0.57/4.0, linearCloseness.get(d), 0.01);
		assertEquals(0.4/4.0, linearCloseness.get(e), 0.01);
		
		assertEquals(1.0/5, starCloseness.get(a), 0.01);
		assertEquals(0.56/5, starCloseness.get(b), 0.01);
		assertEquals(0.56/5, starCloseness.get(c), 0.01);
		assertEquals(0.56/5, starCloseness.get(d), 0.01);
		assertEquals(0.56/5, starCloseness.get(e), 0.01);
		assertEquals(0.56/5, starCloseness.get(f), 0.01);
		
		
		toyCloseness = toyMeasure.getOutCloseness(toyMeasure.getAllShortestPaths());
		linearCloseness = linearMeasure.getOutCloseness(linearMeasure.getAllShortestPaths());
		starCloseness= starMeasure.getOutCloseness(starMeasure.getAllShortestPaths());
		
		assertEquals(0.55/6.0, toyCloseness.get(a), 0.01);
		assertEquals(0.6/6.0, toyCloseness.get(b), 0.01);
		assertEquals(0.55/6.0, toyCloseness.get(c), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(d), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(e), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(f), 0.01);
		assertEquals(0.4/6.0, toyCloseness.get(g), 0.01);
		
		assertEquals(0.4/4.0, linearCloseness.get(a), 0.01);
		assertEquals(0.57/4.0, linearCloseness.get(b), 0.01);
		assertEquals(0.67/4.0, linearCloseness.get(c), 0.01);
		assertEquals(0.57/4.0, linearCloseness.get(d), 0.01);
		assertEquals(0.4/4.0, linearCloseness.get(e), 0.01);
		
		assertEquals(1.0/5, starCloseness.get(a), 0.01);
		assertEquals(0.56/5, starCloseness.get(b), 0.01);
		assertEquals(0.56/5, starCloseness.get(c), 0.01);
		assertEquals(0.56/5, starCloseness.get(d), 0.01);
		assertEquals(0.56/5, starCloseness.get(e), 0.01);
		assertEquals(0.56/5, starCloseness.get(f), 0.01);
	}

	@Test
	public void testFarness() {
		Map<BioPhysicalEntity, Double> toyBetweenness = toyMeasure.getFarness(toyMeasure.getAllShortestPaths());
		Map<BioPhysicalEntity, Double> linearBetweenness = linearMeasure.getFarness(linearMeasure.getAllShortestPaths());
		Map<BioPhysicalEntity, Double> starBetweenness = starMeasure.getFarness(starMeasure.getAllShortestPaths());
		
		assertEquals(11.0, toyBetweenness.get(a), 0.00000001);
		assertEquals(10.0, toyBetweenness.get(b), 0.00000001);
		assertEquals(11.0, toyBetweenness.get(c), 0.00000001);
		assertEquals(15.0, toyBetweenness.get(d), 0.00000001);
		assertEquals(15.0, toyBetweenness.get(e), 0.00000001);
		assertEquals(15.0, toyBetweenness.get(f), 0.00000001);
		assertEquals(15.0, toyBetweenness.get(g), 0.00000001);
		
		assertEquals(10.0, linearBetweenness.get(a), 0.00000001);
		assertEquals(7.0, linearBetweenness.get(b), 0.00000001);
		assertEquals(6.0, linearBetweenness.get(c), 0.00000001);
		assertEquals(7.0, linearBetweenness.get(d), 0.00000001);
		assertEquals(10.0, linearBetweenness.get(e), 0.00000001);
		
		assertEquals(5.0, starBetweenness.get(a), 0.00000001);
		assertEquals(9.0, starBetweenness.get(b), 0.00000001);
		assertEquals(9.0, starBetweenness.get(c), 0.00000001);
		assertEquals(9.0, starBetweenness.get(d), 0.00000001);
		assertEquals(9.0, starBetweenness.get(e), 0.00000001);
		assertEquals(9.0, starBetweenness.get(f), 0.00000001);
	}
	
	@Test
	public void testOCCI(){
		GraphMeasure<BioPhysicalEntity, ReactionEdge> measure = 
				new GraphMeasure<BioPhysicalEntity, ReactionEdge>(starGraph);
		
		assertEquals(1.0, measure.getOCCI(), 0.000001);
		
	}
}
