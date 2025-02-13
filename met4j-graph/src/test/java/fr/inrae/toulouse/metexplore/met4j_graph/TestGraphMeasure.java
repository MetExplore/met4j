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
package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.GraphMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test {@link GraphMeasure}
 * @author clement
 */
public class TestGraphMeasure {
	
	public static CompoundGraph g;
	
	public static GraphMeasure<BioMetabolite, ReactionEdge> m;
	
	public static BioMetabolite a,b,c,d,e,f,x,y,z,l; //v=10
	
	public static ReactionEdge az,zb,ab,xb,bc,cx,yx,ay,ea,ey,xd,ed,fe,df,ax;	//e=15
	
	/** The expected alpha. */
	public final double expectedAlpha = 0.166666;	// (e-(v-1))/((v(v-1)/2)-(v-1)) = 6/36 = 0,166666667
	
	/** The expected beta. */
	public final double expectedBeta = 1.5;	// e/v = 15/10 = 1.5
	
	/** The expected diameter. */
	public final double expectedDiameter = 49; // max sp path length :  z -> a = 49
	
	/** The expected length. */
	public final double expectedLength = 81;	//2+8+9+4+8+3+1+2+8+2+6+5+8+8+7=81
	
	/** The expected number of connected comp. */
	public final int expectedNumberOfConnectedComp = 2;
	
	/** The expected number of cycle. */
	public final int expectedNumberOfCycle = 7; 	// e-v+p = 15-10+2 = 7
	
	/** The expected pi. */
	public final double expectedPi = 0.604938;	// D/L = 49/81 = 0.604938
	
	/** The expected eta. */
	public final double expectedEta = 5.4;	// L/e = 81/15 = 5.4
	
	/** The expected gamma. */
	public final double expectedGamma = 0.333333;	// (v*(v-1))/2 = 90/2 = 45 ; e/(v*(v-1))/2) = 15/45 = 0.3333333

	
	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		a = new BioMetabolite("a"); g.addVertex(a);
		b = new BioMetabolite("b"); g.addVertex(b);
		c = new BioMetabolite("c"); g.addVertex(c);
		d = new BioMetabolite("d"); g.addVertex(d);
		e = new BioMetabolite("e"); g.addVertex(e);
		f = new BioMetabolite("f"); g.addVertex(f);
		x = new BioMetabolite("x"); g.addVertex(x);
		y = new BioMetabolite("y"); g.addVertex(y);
		z = new BioMetabolite("z"); g.addVertex(z);
		l = new BioMetabolite("lonelyBoy"); g.addVertex(l);
		az = new ReactionEdge(a,z,new BioReaction("az"));g.addEdge(a, z, az);g.setEdgeWeight(az, 2);
		zb = new ReactionEdge(z,b,new BioReaction("zb"));g.addEdge(z, b, zb);g.setEdgeWeight(zb, 8);
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 9);
		xb = new ReactionEdge(x,b,new BioReaction("xb"));g.addEdge(x, b, xb);g.setEdgeWeight(xb, 4);
		bc = new ReactionEdge(b,c,new BioReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 8);
		cx = new ReactionEdge(c,x,new BioReaction("cx"));g.addEdge(c, x, cx);g.setEdgeWeight(cx, 3);
		yx = new ReactionEdge(y,x,new BioReaction("yx"));g.addEdge(y, x, yx);g.setEdgeWeight(yx, 1);
		ay = new ReactionEdge(a,y,new BioReaction("ay"));g.addEdge(a, y, ay);g.setEdgeWeight(ay, 2);
		ea = new ReactionEdge(e,a,new BioReaction("ea"));g.addEdge(e, a, ea);g.setEdgeWeight(ea, 8);
		ey = new ReactionEdge(e,y,new BioReaction("ey"));g.addEdge(e, y, ey);g.setEdgeWeight(ey, 2);
		xd = new ReactionEdge(x,d,new BioReaction("xd"));g.addEdge(x, d, xd);g.setEdgeWeight(xd, 6);
		ed = new ReactionEdge(e,d,new BioReaction("ed"));g.addEdge(e, d, ed);g.setEdgeWeight(ed, 5);
		fe = new ReactionEdge(f,e,new BioReaction("fe"));g.addEdge(f, e, fe);g.setEdgeWeight(fe, 8);
		df = new ReactionEdge(d,f,new BioReaction("df"));g.addEdge(d, f, df);g.setEdgeWeight(df, 8);
		ax = new ReactionEdge(a,x,new BioReaction("ax"));g.addEdge(a, x, ax);g.setEdgeWeight(ax, 7);
		m = new GraphMeasure<>(g);
		m.setDirected(false);
	}
	
	/**
	 * test the edge count adjustment for multigraph
	 */
	@Test
	public void testEdgeCountAdjustement(){
		CompoundGraph g2 = new CompoundGraph(g);
		assertEquals("error while creating copy of graph", 15, g2.edgeSet().size());
		ReactionEdge az2 = new ReactionEdge(a,z,new BioReaction("az2"));
		g2.addEdge(a, z, az2);
		GraphMeasure<BioMetabolite, ReactionEdge> m = new GraphMeasure<>(g2);
		m.adjustEdgeCountForMultiGraph();
		assertEquals("error while creating multi-edge", 16, g2.edgeSet().size());
		assertEquals("error while creating multi-edge", 15, m.getNumberOfEdges(), Double.MIN_VALUE);
	}
	
	/**
	 * Test the alpha.
	 */
	@Test
	public void testAlpha() {
		assertEquals("error on alpha computation", expectedAlpha, m.getAlpha(), 0.000001);
	}
	
	/**
	 * Test the beta.
	 */
	@Test
	public void testBeta() {
		assertEquals("error on beta computation", expectedBeta, m.getBeta(), 0.000001);
	}
	
	/**
	 * Test the diameter.
	 */
	@Test
	public void testDiameter() {
		assertEquals("error on diameter computation", expectedDiameter, m.getDiameter(), 0.000001);
	}
	
	/**
	 * Test the length.
	 */
	@Test
	public void testLength() {
		assertEquals("error on length computation", expectedLength, m.getLength(), 0.000001);
	}
	
	/**
	 * Test the number of connected comp.
	 */
	@Test
	public void testNumberOfConnectedComp() {
		assertEquals("error on number of connected component", expectedNumberOfConnectedComp, m.getNumberOfConnectedComponent());
	}
	
	/**
	 * Test the number of cycle.
	 */
	@Test
	public void testNumberOfCycle() {
		assertEquals("error on number of cycle computation", expectedNumberOfCycle, m.getNumberOfCycle());
	}
	
	/**
	 * Test the pi.
	 */
	@Test
	public void testPi() {
		assertEquals("error on pi computation", expectedPi, m.getPi(), 0.000001);
	}
	
	/**
	 * Test the eta.
	 */
	@Test
	public void testEta() {
		assertEquals("error on eta computation", expectedEta, m.getEta(), 0.000001);
	}
	
	/**
	 * Test the gamma.
	 */
	@Test
	public void testGamma() {
		assertEquals("error on gamma computation", expectedGamma, m.getGamma(), 0.000001);
	}
	
	@Test
	public void testDegree(){
		assertTrue(g.areConnected(a, z));
		assertFalse(g.areConnected(a, e));
		assertEquals(0,g.degreeOf(l));
		assertEquals(5,g.degreeOf(a));
		assertEquals(5,g.neighborListOf(a).size());
		assertEquals(1,g.inDegreeOf(a));
		assertEquals(1,g.incomingEdgesOf(a).size());
		assertEquals(1,g.predecessorListOf(a).size());
		assertEquals(4,g.outDegreeOf(a));
		assertEquals(4,g.outgoingEdgesOf(a).size());
		assertEquals(4,g.successorListOf(a).size());
	}
}
