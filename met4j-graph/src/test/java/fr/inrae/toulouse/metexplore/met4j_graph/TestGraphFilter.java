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
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.GraphFilter;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
/**
 * The Class TestGraphFilter.
 * @author clement
 */
public class TestGraphFilter {
	
/** The graph. */
public static CompoundGraph g;
	
	/** The nodes. */
	public static BioMetabolite a,b,c,d,e,f,x,y,z;
	
	/** The edges. */
	public static ReactionEdge az,zb,ab,xb,bc,cx,yx,ay,ea,ey,xd,ed,fe,df;
	 

	/**
	 * Inits the.
	 */
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
		az = new ReactionEdge(a,z,new BioReaction("az"));g.addEdge(a, z, az);g.setEdgeWeight(az, 2);g.setEdgeScore(az, 4);
		zb = new ReactionEdge(z,b,new BioReaction("zb"));g.addEdge(z, b, zb);g.setEdgeWeight(zb, 8);g.setEdgeScore(zb, 16);
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 9);g.setEdgeScore(ab, 18);
		xb = new ReactionEdge(x,b,new BioReaction("xb"));g.addEdge(x, b, xb);g.setEdgeWeight(xb, 4);g.setEdgeScore(xb, 8);
		bc = new ReactionEdge(b,c,new BioReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 8);g.setEdgeScore(bc, 16);
		cx = new ReactionEdge(c,x,new BioReaction("cx"));g.addEdge(c, x, cx);g.setEdgeWeight(cx, 3);g.setEdgeScore(cx, 6);
		yx = new ReactionEdge(y,x,new BioReaction("yx"));g.addEdge(y, x, yx);g.setEdgeWeight(yx, 1);g.setEdgeScore(yx, 2);
		ay = new ReactionEdge(a,y,new BioReaction("ay"));g.addEdge(a, y, ay);g.setEdgeWeight(ay, 2);g.setEdgeScore(ay, 4);
		ea = new ReactionEdge(e,a,new BioReaction("ea"));g.addEdge(e, a, ea);g.setEdgeWeight(ea, 8);g.setEdgeScore(ea, 16);
		ey = new ReactionEdge(e,y,new BioReaction("ey"));g.addEdge(e, y, ey);g.setEdgeWeight(ey, 2);g.setEdgeScore(ey, 4);
		xd = new ReactionEdge(x,d,new BioReaction("xd"));g.addEdge(x, d, xd);g.setEdgeWeight(xd, 6);g.setEdgeScore(xd, 12);
		ed = new ReactionEdge(e,d,new BioReaction("ed"));g.addEdge(e, d, ed);g.setEdgeWeight(ed, 5);g.setEdgeScore(ed, 10);
		fe = new ReactionEdge(f,e,new BioReaction("fe"));g.addEdge(f, e, fe);g.setEdgeWeight(fe, 8);g.setEdgeScore(fe, 16);
		df = new ReactionEdge(d,f,new BioReaction("df"));g.addEdge(d, f, df);g.setEdgeWeight(df, 8);g.setEdgeScore(df, 16);
	}
	
	/**
	 * Test the weight filter.
	 */
	@Test
	public void testWeightFilter() {
		CompoundGraph g2;
		int removed =0;
		Set<ReactionEdge> expected;
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.EQUALITY);
		assertEquals(removed, 5);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(zb);
		expected.add(xb);
		expected.add(bc);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ea);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<>();
		expected.add(ab);
		Assert.assertEquals(expected,g2.edgeSet());
	}
	
	@Test
	public void testWeightPercentileFilter() {
		CompoundGraph g2;
		int removed =0;
		Set<ReactionEdge> expected;
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.EQUALITY);
		assertEquals(removed, 5);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(zb);
		expected.add(xb);
		expected.add(bc);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ea);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<>();
		expected.add(ab);
		Assert.assertEquals(expected,g2.edgeSet());
	}
	
	@Test
	public void testWeightRankFilter() {
		CompoundGraph g2;
		int removed =0;
		Set<ReactionEdge> expected;
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.EQUALITY);
		assertEquals(removed, 5);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(zb);
		expected.add(xb);
		expected.add(bc);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ea);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<>();
		expected.add(ab);
		Assert.assertEquals(expected,g2.edgeSet());
	}
	
	/**
	 * Test the score filter.
	 */
	@Test
	public void testScoreFilter() {
		CompoundGraph g2;
		int removed =0;
		Set<ReactionEdge> expected;
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.EQUALITY);
		assertEquals(removed, 5);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(zb);
		expected.add(xb);
		expected.add(bc);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ea);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		Assert.assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<>();
		expected.add(ab);
		Assert.assertEquals(expected,g2.edgeSet());
	}

}
