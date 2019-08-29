package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.GraphSampler;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

public class TestGraphSampler {
	
	public static CompoundGraph g;
	public static BioNetwork bn;
	
	public static BioMetabolite a,b,c,d,e;
	public static BioCompartment comp;
	
	public static ReactionEdge ab,ac,ae,ad;
	
	public static GraphSampler<BioMetabolite, ReactionEdge, CompoundGraph> sampler;
	 
	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		comp = new BioCompartment("comp"); bn.add(comp);
		a = new BioMetabolite("a"); a.setCompartment(comp);g.addVertex(a);
		b = new BioMetabolite("b"); g.addVertex(b);
		c = new BioMetabolite("c"); g.addVertex(c);
		d = new BioMetabolite("d"); g.addVertex(d);
		e = new BioMetabolite("e"); g.addVertex(e);
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 1.0);
		ac = new ReactionEdge(a,c,new BioReaction("ac"));g.addEdge(a, c, ac);g.setEdgeWeight(ac, 1.0);
		ae = new ReactionEdge(a,e,new BioReaction("ae"));g.addEdge(a, e, ae);g.setEdgeWeight(ae, 1.0);
		ad = new ReactionEdge(a,d,new BioReaction("ad"));g.addEdge(a, d, ad);g.setEdgeWeight(ad, 1.0);
		sampler = new GraphSampler<BioMetabolite, ReactionEdge, CompoundGraph>(g);

	}
	
	@Test
	public void testGetRandomVertex() {
		assertNotNull(sampler.getRandomVertex());
	}
	
	@Test
	public void testGetRandomVertexList() {
		assertEquals(5,sampler.getRandomVertexList(5).size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetRandomVertexList2() {
		sampler.getRandomVertexList(42);
	}
	
	@Test
	public void testGetRandomVertexListinComp(){
		Set<BioMetabolite> sample = sampler.getRandomVertexListinComp(1, "comp");
		assertEquals(1, sample.size());
		assertTrue(sample.contains(a));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetRandomVertexListinComp2(){
		sampler.getRandomVertexListinComp(42, "comp");
	}
	
	@Test
	public void testGetRandomVertexListinScope(){
		Set<BioMetabolite> sample = sampler.getRandomVertexListinScope(3, 1);
		assertEquals(3, sample.size());
		assertTrue(sample.contains(a));
	}
	
	@Test(expected = IllegalArgumentException.class)	
	public void testGetRandomVertexListinScope2(){
		sampler.getRandomVertexListinScope(5, 1);
	}
	
	@Test
	public void testGetRandomEdgeList(){
		Set<ReactionEdge> sample = sampler.getRandomEdgeList(2);
		assertEquals(2, sample.size());
	}
	
	@Test(expected = IllegalArgumentException.class)	
	public void testGetRandomEdgeList2(){
		sampler.getRandomEdgeList(42);
	}
	
	@Test
	public void testGetRandomTransitionMatrix(){
		EjmlMatrix m = new EjmlMatrix(2, 2);
		m.set(1, 0, 1);
		m.set(1, 1, 1);
		BioMatrix m2 = sampler.getRandomTransitionMatrix(m);
		assertEquals(2, m2.numCols());
		assertEquals(2, m2.numRows());
		assertEquals(0.0, m2.getRowSum(0),Double.MIN_VALUE);
		assertEquals(1.0, m2.getRowSum(1),Double.MIN_VALUE);
	}	
	
}
