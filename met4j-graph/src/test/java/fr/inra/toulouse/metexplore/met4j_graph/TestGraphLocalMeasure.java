package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.GraphLocalMeasure;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

public class TestGraphLocalMeasure {
	
	public static CompoundGraph g;
	
	public static BioMetabolite a,b,c,d,e;
	
	public static ReactionEdge ab,ac,ae,ea,ec,ed;
	
	public static GraphLocalMeasure<BioMetabolite, ReactionEdge, CompoundGraph> measure;
	 
	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		a = new BioMetabolite("a"); g.addVertex(a);
		b = new BioMetabolite("b"); g.addVertex(b);
		c = new BioMetabolite("c"); g.addVertex(c);
		d = new BioMetabolite("d"); g.addVertex(d);
		e = new BioMetabolite("e"); g.addVertex(e);
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 1.0);
		ac = new ReactionEdge(a,c,new BioReaction("ac"));g.addEdge(a, c, ac);g.setEdgeWeight(ac, 1.0);
		ae = new ReactionEdge(a,e,new BioReaction("ae"));g.addEdge(a, e, ae);g.setEdgeWeight(ae, 1.0);
		ea = new ReactionEdge(e,a,new BioReaction("ea"));g.addEdge(e, a, ea);g.setEdgeWeight(ea, 1.0);
		ec = new ReactionEdge(e,c,new BioReaction("ec"));g.addEdge(e, c, ec);g.setEdgeWeight(ec, 1.0);
		ed = new ReactionEdge(e,d,new BioReaction("ed"));g.addEdge(e, d, ed);g.setEdgeWeight(ed, 1.0);
		measure = new GraphLocalMeasure<BioMetabolite, ReactionEdge, CompoundGraph>(g);

	}
	
	@Test
	public void testGetCommonNeighbor() {
		assertEquals(1.0, measure.getCommonNeighbor(a, e), Double.MIN_VALUE);
		assertEquals(1.0, measure.getCommonNeighbor(e, a), Double.MIN_VALUE);
		
		assertEquals(0.0, measure.getCommonNeighbor(b, d), Double.MIN_VALUE);
		assertEquals(0.0, measure.getCommonNeighbor(d, b), Double.MIN_VALUE);
	}
	
	@Test
	public void testGetAdamicAdar() {
		assertEquals(3.321928095, measure.getAdamicAdar(a, e), 0.000000001);
		assertEquals(3.321928095, measure.getAdamicAdar(e, a), 0.000000001);
	
		assertEquals(0.0, measure.getAdamicAdar(b, d), Double.MIN_VALUE);
		assertEquals(0.0, measure.getAdamicAdar(d, b), Double.MIN_VALUE);
	}
	
	@Test
	public void testGetSaltonIndex() {
		assertEquals(1.0/3.0, measure.getSaltonIndex(a, e), 0.000000001);
		assertEquals(1.0/3.0, measure.getSaltonIndex(e, a), 0.000000001);
	
		assertEquals(0.0, measure.getSaltonIndex(b, d), Double.MIN_VALUE);
		assertEquals(0.0, measure.getSaltonIndex(d, b), Double.MIN_VALUE);
	}
	
	@Test
	public void testUndirectedLocalClusteringCoeff() {
		assertEquals(1.0/3.0, measure.getUndirectedLocalClusteringCoeff(a), 0.000000001);
		assertEquals(1.0/3.0, measure.getUndirectedLocalClusteringCoeff(e), 0.000000001);
	
		assertEquals(0.0, measure.getUndirectedLocalClusteringCoeff(b), Double.MIN_VALUE);
		assertEquals(0.0, measure.getUndirectedLocalClusteringCoeff(d), Double.MIN_VALUE);
	}
	
	@Test
	public void testLocalClusteringCoeff() {
		assertEquals(1.0/6.0, measure.getLocalClusteringCoeff(a), 0.000000001);
		assertEquals(1.0/6.0, measure.getLocalClusteringCoeff(e), 0.000000001);
	
		assertEquals(0.0, measure.getLocalClusteringCoeff(b), Double.MIN_VALUE);
		assertEquals(0.0, measure.getLocalClusteringCoeff(d), Double.MIN_VALUE);
	}

}
