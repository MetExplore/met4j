/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.computation.transform.GraphFilter;
/**
 * The Class TestGraphFilter.
 * @author clement
 */
public class TestGraphFilter {
	
/** The graph. */
public static CompoundGraph g;
	
	/** The nodes. */
	public static BioPhysicalEntity a,b,c,d,e,f,x,y,z;
	
	/** The edges. */
	public static ReactionEdge az,zb,ab,xb,bc,cx,yx,ay,ea,ey,xd,ed,fe,df;
	 

	/**
	 * Inits the.
	 */
	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		a = new BioPhysicalEntity("a"); g.addVertex(a);
		b = new BioPhysicalEntity("b"); g.addVertex(b);
		c = new BioPhysicalEntity("c"); g.addVertex(c);
		d = new BioPhysicalEntity("d"); g.addVertex(d);
		e = new BioPhysicalEntity("e"); g.addVertex(e);
		f = new BioPhysicalEntity("f"); g.addVertex(f);
		x = new BioPhysicalEntity("x"); g.addVertex(x);
		y = new BioPhysicalEntity("y"); g.addVertex(y);
		z = new BioPhysicalEntity("z"); g.addVertex(z);
		az = new ReactionEdge(a,z,new BioChemicalReaction("az"));g.addEdge(a, z, az);g.setEdgeWeight(az, 2);g.setEdgeScore(az, 4);
		zb = new ReactionEdge(z,b,new BioChemicalReaction("zb"));g.addEdge(z, b, zb);g.setEdgeWeight(zb, 8);g.setEdgeScore(zb, 16);
		ab = new ReactionEdge(a,b,new BioChemicalReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 9);g.setEdgeScore(ab, 18);
		xb = new ReactionEdge(x,b,new BioChemicalReaction("xb"));g.addEdge(x, b, xb);g.setEdgeWeight(xb, 4);g.setEdgeScore(xb, 8);
		bc = new ReactionEdge(b,c,new BioChemicalReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 8);g.setEdgeScore(bc, 16);
		cx = new ReactionEdge(c,x,new BioChemicalReaction("cx"));g.addEdge(c, x, cx);g.setEdgeWeight(cx, 3);g.setEdgeScore(cx, 6);
		yx = new ReactionEdge(y,x,new BioChemicalReaction("yx"));g.addEdge(y, x, yx);g.setEdgeWeight(yx, 1);g.setEdgeScore(yx, 2);
		ay = new ReactionEdge(a,y,new BioChemicalReaction("ay"));g.addEdge(a, y, ay);g.setEdgeWeight(ay, 2);g.setEdgeScore(ay, 4);
		ea = new ReactionEdge(e,a,new BioChemicalReaction("ea"));g.addEdge(e, a, ea);g.setEdgeWeight(ea, 8);g.setEdgeScore(ea, 16);
		ey = new ReactionEdge(e,y,new BioChemicalReaction("ey"));g.addEdge(e, y, ey);g.setEdgeWeight(ey, 2);g.setEdgeScore(ey, 4);
		xd = new ReactionEdge(x,d,new BioChemicalReaction("xd"));g.addEdge(x, d, xd);g.setEdgeWeight(xd, 6);g.setEdgeScore(xd, 12);
		ed = new ReactionEdge(e,d,new BioChemicalReaction("ed"));g.addEdge(e, d, ed);g.setEdgeWeight(ed, 5);g.setEdgeScore(ed, 10);
		fe = new ReactionEdge(f,e,new BioChemicalReaction("fe"));g.addEdge(f, e, fe);g.setEdgeWeight(fe, 8);g.setEdgeScore(fe, 16);
		df = new ReactionEdge(d,f,new BioChemicalReaction("df"));g.addEdge(d, f, df);g.setEdgeWeight(df, 8);g.setEdgeScore(df, 16);
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
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<ReactionEdge>();
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
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightFilter(g2, 8, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<ReactionEdge>();
		expected.add(ab);
		assertEquals(expected,g2.edgeSet());
	}
	
	@Test
	public void testWeightPercentileFilter() {
		CompoundGraph g2;
		int removed =0;
		Set<ReactionEdge> expected;
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.EQUALITY);
		assertEquals(removed, 5);
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<ReactionEdge>();
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
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightPercentileFilter(g2, 75, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<ReactionEdge>();
		expected.add(ab);
		assertEquals(expected,g2.edgeSet());
	}
	
	@Test
	public void testWeightRankFilter() {
		CompoundGraph g2;
		int removed =0;
		Set<ReactionEdge> expected;
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.EQUALITY);
		assertEquals(removed, 5);
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<ReactionEdge>();
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
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.weightRankFilter(g2, 2, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<ReactionEdge>();
		expected.add(ab);
		assertEquals(expected,g2.edgeSet());
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
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(ab);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.GREATER);
		assertEquals(removed, 1);
		expected = new HashSet<ReactionEdge>();
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
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.GREATEROREQUAL);
		assertEquals(removed, 6);
		expected = new HashSet<ReactionEdge>();
		expected.add(az);
		expected.add(xb);
		expected.add(cx);
		expected.add(yx);
		expected.add(ay);
		expected.add(ey);
		expected.add(xd);
		expected.add(ed);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.INEQUALITY);
		assertEquals(removed, 9);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.LESS);
		assertEquals(removed, 8);
		expected = new HashSet<ReactionEdge>();
		expected.add(zb);
		expected.add(ab);
		expected.add(bc);
		expected.add(ea);
		expected.add(fe);
		expected.add(df);
		assertEquals(expected,g2.edgeSet());
		
		g2 = new CompoundGraph(g);
		removed = GraphFilter.scoreFilter(g2, 16, GraphFilter.LESSOREQUAL);
		assertEquals(removed, 13);
		expected = new HashSet<ReactionEdge>();
		expected.add(ab);
		assertEquals(expected,g2.edgeSet());
	}

}
