package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.LoadPoint;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestLoadPoint {

	public static CompoundGraph toyGraph;
	public static CompoundGraph linearGraph;
	public static CompoundGraph starGraph;
	public static BioMetabolite a,b,c,d,e,f,h,g;
	public static LoadPoint<BioMetabolite, ReactionEdge, CompoundGraph> toyMeasure;
	public static LoadPoint<BioMetabolite, ReactionEdge, CompoundGraph> linearMeasure;
	public static LoadPoint<BioMetabolite, ReactionEdge, CompoundGraph> starMeasure;
	
	
	@BeforeClass
	public static void init(){
		
		a = new BioMetabolite("a");
		b = new BioMetabolite("b");
		c = new BioMetabolite("c");
		d = new BioMetabolite("d");
		e = new BioMetabolite("e"); 
		f = new BioMetabolite("f"); 
		h = new BioMetabolite("h"); 
		g = new BioMetabolite("g"); 
		ReactionEdge ab = new ReactionEdge(a, b, new BioReaction("r1"));
		ReactionEdge bc = new ReactionEdge(b, c, new BioReaction("r2"));
		ReactionEdge cd = new ReactionEdge(c, d, new BioReaction("r3"));
		ReactionEdge de = new ReactionEdge(d, e, new BioReaction("r4"));
		ReactionEdge ec = new ReactionEdge(e, c, new BioReaction("r5"));
		ReactionEdge af = new ReactionEdge(a, f, new BioReaction("r6"));
		ReactionEdge fg = new ReactionEdge(f, g, new BioReaction("r7"));
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction("r8"));
		ReactionEdge ca = new ReactionEdge(c, a, new BioReaction("r9"));
		ReactionEdge da = new ReactionEdge(d, a, new BioReaction("r10"));
		ReactionEdge ea = new ReactionEdge(e, a, new BioReaction("r11"));
		ReactionEdge fa = new ReactionEdge(f, a, new BioReaction("r12"));
		ReactionEdge ga = new ReactionEdge(g, a, new BioReaction("r13"));
		ReactionEdge cb = new ReactionEdge(c, b, new BioReaction("r14"));
		ReactionEdge dc = new ReactionEdge(d, c, new BioReaction("r15"));
		ReactionEdge ed = new ReactionEdge(e, d, new BioReaction("r16"));
		ReactionEdge ce = new ReactionEdge(c, e, new BioReaction("r17"));
		ReactionEdge gf = new ReactionEdge(g, f, new BioReaction("r18"));
		ReactionEdge ac = new ReactionEdge(a, c, new BioReaction("r19"));
		ReactionEdge ad = new ReactionEdge(a, d, new BioReaction("r20"));
		ReactionEdge ae = new ReactionEdge(a, e, new BioReaction("r21"));
		ReactionEdge ag = new ReactionEdge(a, g, new BioReaction("r22"));
		
		
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
		
		
		toyMeasure = new LoadPoint<BioMetabolite, ReactionEdge, CompoundGraph>(toyGraph);
		linearMeasure = new LoadPoint<BioMetabolite, ReactionEdge, CompoundGraph>(linearGraph);
		starMeasure = new LoadPoint<BioMetabolite, ReactionEdge, CompoundGraph>(starGraph);
	}
	
	@Test
	public void testGetLoadPoint() {
		
		Map<BioMetabolite, Double> toyLoadPoint = toyMeasure.getLoads(1);
		Map<BioMetabolite, Double> linearLoadPoint = linearMeasure.getLoads(1);
		Map<BioMetabolite, Double> starLoadPoint = starMeasure.getLoads(1);
		
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
