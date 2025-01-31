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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCompoundGraph {

	public static CompoundGraph cg;
	public static BioMetabolite v1,v2,v3;
	public static BioReaction r1,r2,r3;
	public static BioPathway p;
	public static ReactionEdge e1,e2;
	public static BioNetwork bn;
	public static BioCompartment comp;
	public static BioCompartment comp2;

	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		bn= new BioNetwork();
		comp = new BioCompartment("comp"); bn.add(comp);
		comp2 = new BioCompartment("comp2"); bn.add(comp2);
		v1 = new BioMetabolite("v1");bn.add(v1); bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2");bn.add(v2); bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3");bn.add(v3); bn.affectToCompartment(comp2, v3);
		
		p = new BioPathway("p");bn.add(p);
		
		r1 = new BioReaction("r1");bn.add(r1);
		bn.affectLeft(r1, 1.0, comp, v1);
		bn.affectRight(r1, 1.0, comp, v2);
		bn.affectToPathway(p, r1);
		r2 = new BioReaction("r2");bn.add(r2);
		bn.affectLeft(r2, 1.0, comp, v2);
		bn.affectRight(r2, 1.0, comp2, v3);
		bn.affectToPathway(p, r2);
		r3 = new BioReaction("r3");bn.add(r3);
		bn.affectLeft(r3, 1.0, comp, v2);
		bn.affectRight(r3, 1.0, comp2, v3);
		r3.setReversible(true);
		
		e1 = new ReactionEdge(v1, v2, r1);
		e2 = new ReactionEdge(v2, v3, r2);
		
		cg.addVertex(v1);
		cg.addVertex(v2);
		cg.addVertex(v3);
		cg.addEdge(v1, v2, e1);
		cg.addEdge(v2, v3, e2);
		
		Assert.assertEquals(3, cg.vertexSet().size());
		Assert.assertEquals(2, cg.edgeSet().size());
	}
	
	@Test
	public void testGetEdgesFromReaction() {
		HashSet<ReactionEdge> edgesFromR2 = cg.getEdgesFromReaction("r1");
		assertEquals(1, edgesFromR2.size());
		assertTrue(edgesFromR2.contains(e1));
		
		 edgesFromR2 = cg.getEdgesFromReaction("rX");
		 assertTrue(edgesFromR2.isEmpty());
	}
	
//	@Test
//	public void testGetEdgesFromPathway() {
//		HashSet<ReactionEdge> edgesFromP = cg.getEdgesFromPathway("p");
//		assertEquals(2, edgesFromP.size());
//		assertTrue(edgesFromP.contains(e1));
//		assertTrue(edgesFromP.contains(e2));
//		
//		edgesFromP = cg.getEdgesFromReaction("pX");
//		 assertTrue(edgesFromP.isEmpty());
//	}
	
	@Test
	public void testGetBiochemicalReactionList(){
		 HashMap<String, BioReaction> reactions = cg.getBiochemicalReactionList();
		 assertTrue(reactions.containsKey("r1"));
		 assertTrue(reactions.containsKey("r2"));
		 assertEquals(r1, reactions.get("r1"));
		 assertEquals(r2, reactions.get("r2"));
	}
	
	@Test
	public void testAddEdgesFromReaction() {
		cg.addEdgesFromReaction(bn,r3);
		Assert.assertEquals(3, cg.vertexSet().size());
		Assert.assertEquals(4, cg.edgeSet().size());
		cg.removeEdge(cg.getEdge(v2,v3,r3));
		Assert.assertEquals(3, cg.vertexSet().size());
		Assert.assertEquals(3, cg.edgeSet().size());
	}
	
	@Test
	public void testGetEdge() {
		ReactionEdge e = cg.getEdge("v1", "v2", "r1");
		assertEquals(e1, e);
	}
	
	@Test
	public void testCopyEdge() {
		ReactionEdge e = cg.copyEdge(e1);
		assertEquals(v1, e.getV1());
		assertEquals(v2, e.getV2());
		assertEquals(r1, e.getReaction());
	}
	
	@Test
	public void testReverseEdge() {
		ReactionEdge e = cg.reverseEdge(e1);
		assertEquals(v2, e.getV1());
		assertEquals(v1, e.getV2());
		assertEquals(r1, e.getReaction());
	}
	
	@Test
	public void testCopyConstructor() {
		CompoundGraph g2 = new CompoundGraph(cg);
		Assert.assertEquals(cg.vertexSet().size(), g2.vertexSet().size());
		Assert.assertEquals(cg.edgeSet().size(), g2.edgeSet().size());
		assertTrue(g2.containsEdge(e1));
		assertTrue(g2.containsEdge(e2));
		assertTrue(g2.containsVertex(v1));
		assertTrue(g2.containsVertex(v2));
		BioMetabolite v4 = new BioMetabolite("v4");
		g2.addVertex(v4);
		g2.addEdge(v3,v4,new ReactionEdge(v3,v4,new BioReaction("r4")));
		Assert.assertEquals(cg.vertexSet().size()+1, g2.vertexSet().size());
		Assert.assertEquals(cg.edgeSet().size()+1, g2.edgeSet().size());

	}
	
	@Test
	public void testAddEdge(){
		cg.addEdge(v3, v1);
		Assert.assertEquals(3, cg.edgeSet().size());
		cg.removeEdge(v3,v1);
		Assert.assertEquals(2, cg.edgeSet().size());
	}

	@Test
	public void testGetEdgesFromCompartment(){
		assertEquals(1,cg.getEdgesFromCompartment(bn, comp).size());
		assertEquals(0,cg.getEdgesFromCompartment(bn, comp2).size());
		assertTrue(cg.getEdgesFromCompartment(bn, comp).contains(e1));
	}


	@Test
	public void testGetReactionSubGraph() {
		CompoundGraph gr1 = cg.getReactionSubGraph(r1);
		assertEquals(2, gr1.vertexSet().size());
		assertEquals(1, gr1.edgeSet().size());
		assertTrue(gr1.containsVertex(v1));
		assertTrue(gr1.containsVertex(v2));

		CompoundGraph gr2 = cg.getReactionSubGraph(r2);
		assertEquals(2, gr2.vertexSet().size());
		assertEquals(1, gr2.edgeSet().size());
		assertTrue(gr2.containsVertex(v2));
		assertTrue(gr2.containsVertex(v3));
	}

	@Test
	public void testAsUndirected() {
		cg.setEdgeWeight(e1,42);
		CompoundGraph g2 = new CompoundGraph(cg);
		g2.asUndirected();
		Assert.assertEquals(cg.vertexSet().size(), g2.vertexSet().size());
		Assert.assertEquals(cg.edgeSet().size()*2, g2.edgeSet().size());
		for(ReactionEdge e : cg.edgeSet()){
			assertTrue(g2.containsEdge(e.getV1(),e.getV2()));
			assertTrue(g2.containsEdge(e.getV2(),e.getV1()));
		}
		assertEquals(42, g2.getEdgeWeight(g2.getEdge(v1, v2, r1)),Double.MIN_VALUE);
		assertEquals(42, g2.getEdgeWeight(g2.getEdge(v2, v1, r1)),Double.MIN_VALUE);
		cg.setEdgeWeight(e1,1.0);
	}

	@Test
	public void testAsUndirected2() {
		CompoundGraph g2 = new CompoundGraph(cg);
		ReactionEdge e3 = new ReactionEdge(v2, v1, r1);
		ReactionEdge e4 = new ReactionEdge(v3, v2, new BioReaction("r4"));
		g2.addEdge(e3);	g2.setEdgeWeight(e3,42);
		g2.addEdge(e4);
		g2.asUndirected();

		Assert.assertEquals(cg.vertexSet().size(), g2.vertexSet().size());
		Assert.assertEquals(cg.edgeSet().size()*2+2, g2.edgeSet().size());
		for(ReactionEdge e : cg.edgeSet()){
			assertTrue(g2.containsEdge(e.getV1(),e.getV2()));
			assertTrue(g2.containsEdge(e.getV2(),e.getV1()));
		}
		assertEquals(1.0, g2.getEdgeWeight(g2.getEdge(v1, v2, r1)),Double.MIN_VALUE);
		assertEquals(42.0, g2.getEdgeWeight(g2.getEdge(v2, v1, r1)),Double.MIN_VALUE);
	}

}
