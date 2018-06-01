package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.LoadPoint;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestLoadPoint {

	public static CompoundGraph toyGraph;
	public static CompoundGraph linearGraph;
	public static CompoundGraph starGraph;
	public static BioPhysicalEntity a,b,c,d,e,f,h,g;
	public static LoadPoint<BioPhysicalEntity, ReactionEdge, CompoundGraph> toyMeasure;
	public static LoadPoint<BioPhysicalEntity, ReactionEdge, CompoundGraph> linearMeasure;
	public static LoadPoint<BioPhysicalEntity, ReactionEdge, CompoundGraph> starMeasure;
	
	
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
		ReactionEdge ab = new ReactionEdge(a, b, new BioReaction());
		ReactionEdge bc = new ReactionEdge(b, c, new BioReaction());
		ReactionEdge cd = new ReactionEdge(c, d, new BioReaction());
		ReactionEdge de = new ReactionEdge(d, e, new BioReaction());
		ReactionEdge ec = new ReactionEdge(e, c, new BioReaction());
		ReactionEdge af = new ReactionEdge(a, f, new BioReaction());
		ReactionEdge fg = new ReactionEdge(f, g, new BioReaction());
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction());
		ReactionEdge ca = new ReactionEdge(c, a, new BioReaction());
		ReactionEdge da = new ReactionEdge(d, a, new BioReaction());
		ReactionEdge ea = new ReactionEdge(e, a, new BioReaction());
		ReactionEdge fa = new ReactionEdge(f, a, new BioReaction());
		ReactionEdge ga = new ReactionEdge(g, a, new BioReaction());
		ReactionEdge cb = new ReactionEdge(c, b, new BioReaction());
		ReactionEdge dc = new ReactionEdge(d, c, new BioReaction());
		ReactionEdge ed = new ReactionEdge(e, d, new BioReaction());
		ReactionEdge ce = new ReactionEdge(c, e, new BioReaction());
		ReactionEdge gf = new ReactionEdge(g, f, new BioReaction());
		ReactionEdge ac = new ReactionEdge(a, c, new BioReaction());
		ReactionEdge ad = new ReactionEdge(a, d, new BioReaction());
		ReactionEdge ae = new ReactionEdge(a, e, new BioReaction());
		ReactionEdge ag = new ReactionEdge(a, g, new BioReaction());
		
		
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
		
		
		toyMeasure = new LoadPoint<BioPhysicalEntity, ReactionEdge, CompoundGraph>(toyGraph);
		linearMeasure = new LoadPoint<BioPhysicalEntity, ReactionEdge, CompoundGraph>(linearGraph);
		starMeasure = new LoadPoint<BioPhysicalEntity, ReactionEdge, CompoundGraph>(starGraph);
	}
	
	@Test
	public void testGetLoadPoint() {
		
		Map<BioPhysicalEntity, Double> toyLoadPoint = toyMeasure.getLoads(1);
		Map<BioPhysicalEntity, Double> linearLoadPoint = linearMeasure.getLoads(1);
		Map<BioPhysicalEntity, Double> starLoadPoint = starMeasure.getLoads(1);
		
		assertEquals(Math.log((16.0/6)/(42.0/32.0)), toyLoadPoint.get(a), 0.00000001);
		assertEquals(Math.log((18.0/4)/(42.0/32.0)), toyLoadPoint.get(b), 0.00000001);
		assertEquals(Math.log((16.0/6)/(42.0/32.0)), toyLoadPoint.get(c), 0.00000001);
		assertEquals(Math.log(0.0), toyLoadPoint.get(d), 0.00000001);
		assertEquals(Math.log(0.0), toyLoadPoint.get(e), 0.00000001);
		assertEquals(Math.log(0.0), toyLoadPoint.get(f), 0.00000001);
		assertEquals(Math.log(0.0), toyLoadPoint.get(g), 0.00000001);
		
		assertEquals(Math.log(0.0), linearLoadPoint.get(a), 0.00000001);
		assertEquals(Math.log((6.0/4)/(20.0/16.0)), linearLoadPoint.get(b), 0.00000001);
		assertEquals(Math.log((8.0/4)/(20.0/16.0)), linearLoadPoint.get(c), 0.00000001);
		assertEquals(Math.log((6.0/4)/(20.0/16.0)), linearLoadPoint.get(d), 0.00000001);
		assertEquals(Math.log(0.0), linearLoadPoint.get(e), 0.00000001);

		assertEquals(Math.log((20.0/10)/(30.0/20.0)), starLoadPoint.get(a), 0.00000001);
		assertEquals(Math.log(0.0), starLoadPoint.get(b), 0.00000001);
		assertEquals(Math.log(0.0), starLoadPoint.get(c), 0.00000001);
		assertEquals(Math.log(0.0), starLoadPoint.get(d), 0.00000001);
		assertEquals(Math.log(0.0), starLoadPoint.get(e), 0.00000001);
		assertEquals(Math.log(0.0), starLoadPoint.get(f), 0.00000001);
		
		
	}
	
}
