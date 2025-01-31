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
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.PathBasedCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.FloydWarshall;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TestPathBasedCentrality {
	
	public static CompoundGraph toyGraph;
	public static CompoundGraph linearGraph;
	public static CompoundGraph starGraph;
	public static BioMetabolite a,b,c,d,e,f,h,g;
	public static PathBasedCentrality<BioMetabolite, ReactionEdge, CompoundGraph> toyMeasure;
	public static PathBasedCentrality<BioMetabolite, ReactionEdge, CompoundGraph> linearMeasure;
	public static PathBasedCentrality<BioMetabolite, ReactionEdge, CompoundGraph> starMeasure;
	
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
		
		
		toyMeasure = new PathBasedCentrality<>(toyGraph);
		linearMeasure = new PathBasedCentrality<>(linearGraph);
		starMeasure = new PathBasedCentrality<>(starGraph);
	}
	@Test
	public void testBetweenness() {
		Map<BioMetabolite, Integer> toyBetweenness = toyMeasure.getGeodesicBetweenness();
		Map<BioMetabolite, Integer> linearBetweenness = linearMeasure.getGeodesicBetweenness();
		Map<BioMetabolite, Integer> starBetweenness = starMeasure.getGeodesicBetweenness();
		
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
		Map<BioMetabolite, Double> toyCloseness = toyMeasure.getInCloseness(toyMeasure.getAllShortestPaths());
		Map<BioMetabolite, Double> linearCloseness = linearMeasure.getInCloseness(linearMeasure.getAllShortestPaths());
		Map<BioMetabolite, Double> starCloseness= starMeasure.getInCloseness(starMeasure.getAllShortestPaths());
		
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
		
		toyCloseness = toyMeasure.getCloseness(toyMeasure.getAllShortestPaths());
		linearCloseness = linearMeasure.getCloseness(linearMeasure.getAllShortestPaths());
		starCloseness= starMeasure.getCloseness(starMeasure.getAllShortestPaths());
		
		assertEquals((0.55/6.0)/2, toyCloseness.get(a), 0.01);
		assertEquals((0.6/6.0)/2, toyCloseness.get(b), 0.01);
		assertEquals((0.55/6.0)/2, toyCloseness.get(c), 0.01);
		assertEquals((0.4/6.0)/2, toyCloseness.get(d), 0.01);
		assertEquals((0.4/6.0)/2, toyCloseness.get(e), 0.01);
		assertEquals((0.4/6.0)/2, toyCloseness.get(f), 0.01);
		assertEquals((0.4/6.0)/2, toyCloseness.get(g), 0.01);
		
		assertEquals((0.4/4.0)/2, linearCloseness.get(a), 0.01);
		assertEquals((0.57/4.0)/2, linearCloseness.get(b), 0.01);
		assertEquals((0.67/4.0)/2, linearCloseness.get(c), 0.01);
		assertEquals((0.57/4.0)/2, linearCloseness.get(d), 0.01);
		assertEquals((0.4/4.0)/2, linearCloseness.get(e), 0.01);
		
		assertEquals((1.0/5)/2, starCloseness.get(a), 0.01);
		assertEquals((0.56/5)/2, starCloseness.get(b), 0.01);
		assertEquals((0.56/5)/2, starCloseness.get(c), 0.01);
		assertEquals((0.56/5)/2, starCloseness.get(d), 0.01);
		assertEquals((0.56/5)/2, starCloseness.get(e), 0.01);
		assertEquals((0.56/5)/2, starCloseness.get(f), 0.01);

		assertArrayEquals(linearCloseness.values().toArray(),
				linearMeasure.getCloseness((new FloydWarshall<>(linearGraph, false)).getDistances()).values().toArray());
		assertArrayEquals(starCloseness.values().toArray(),
				starMeasure.getCloseness((new FloydWarshall<>(starGraph)).getDistances()).values().toArray());
		assertArrayEquals(toyCloseness.values().toArray(),
				toyMeasure.getCloseness((new FloydWarshall<>(toyGraph)).getDistances()).values().toArray());
		
	}

	@Test
	public void testFarness() {
		Map<BioMetabolite, Double> toyBetweenness = toyMeasure.getFarness(toyMeasure.getAllShortestPaths());
		Map<BioMetabolite, Double> linearBetweenness = linearMeasure.getFarness(linearMeasure.getAllShortestPaths());
		Map<BioMetabolite, Double> starBetweenness = starMeasure.getFarness(starMeasure.getAllShortestPaths());
		
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
	public void testEccentricity() {
		Map<BioMetabolite, Double> toyEccentricity = toyMeasure.getEccentricity(toyMeasure.getAllShortestPaths());
		Map<BioMetabolite, Double> linearEccentricity = linearMeasure.getEccentricity(linearMeasure.getAllShortestPaths());
		Map<BioMetabolite, Double> starEccentricity = starMeasure.getEccentricity(starMeasure.getAllShortestPaths());
		
		assertEquals(3.0, toyEccentricity.get(a), 0.00000001);
		assertEquals(2.0, toyEccentricity.get(b), 0.00000001);
		assertEquals(3.0, toyEccentricity.get(c), 0.00000001);
		assertEquals(4.0, toyEccentricity.get(d), 0.00000001);
		assertEquals(4.0, toyEccentricity.get(e), 0.00000001);
		assertEquals(4.0, toyEccentricity.get(f), 0.00000001);
		assertEquals(4.0, toyEccentricity.get(g), 0.00000001);
		
		assertEquals(4.0, linearEccentricity.get(a), 0.00000001);
		assertEquals(3.0, linearEccentricity.get(b), 0.00000001);
		assertEquals(2.0, linearEccentricity.get(c), 0.00000001);
		assertEquals(3.0, linearEccentricity.get(d), 0.00000001);
		assertEquals(4.0, linearEccentricity.get(e), 0.00000001);
		
		assertEquals(1.0, starEccentricity.get(a), 0.00000001);
		assertEquals(2.0, starEccentricity.get(b), 0.00000001);
		assertEquals(2.0, starEccentricity.get(c), 0.00000001);
		assertEquals(2.0, starEccentricity.get(d), 0.00000001);
		assertEquals(2.0, starEccentricity.get(e), 0.00000001);
		assertEquals(2.0, starEccentricity.get(f), 0.00000001);
	}
	
	@Test
	public void testOCCI(){
		GraphMeasure<BioMetabolite, ReactionEdge> measure =
                new GraphMeasure<>(starGraph);
		
		assertEquals(1.0, measure.getOCCI(), 0.000001);
		
	}
}
