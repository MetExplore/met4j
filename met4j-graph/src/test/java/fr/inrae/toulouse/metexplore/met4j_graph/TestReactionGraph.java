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
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestReactionGraph {

	public static ReactionGraph rg;
	public static BioMetabolite v1,v2,v3;
	public static BioReaction r1,r2,r3,r4;
	public static BioPathway p;
	public static CompoundEdge e1,e2;
	public static BioNetwork bn;
	public static BioCompartment comp;
	
	@BeforeClass
	public static void init(){
		
		rg = new ReactionGraph();
		bn = new BioNetwork();
		comp = new BioCompartment("comp");bn.add(comp);
		
		v1 = new BioMetabolite("v1");bn.add(v1);bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2");bn.add(v2);bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3");bn.add(v3);bn.affectToCompartment(comp, v3);
		
		p = new BioPathway("p");bn.add(p);
		
		r1 = new BioReaction("r1");bn.add(r1);
		bn.affectLeft(r1, 1.0, comp, v1);
		bn.affectRight(r1, 1.0, comp, v2);
		bn.affectToPathway(p, r1);
		r2 = new BioReaction("r2");bn.add(r2);
		bn.affectLeft(r2, 1.0, comp, v2);
		bn.affectRight(r2, 1.0, comp, v3);
		bn.affectToPathway(p, r2);
		r3 = new BioReaction("r3");bn.add(r3);
		bn.affectLeft(r3, 1.0, comp, v2);
		bn.affectRight(r3, 1.0, comp, v3);
		r3.setReversible(true);
		r4 = new BioReaction("r4");bn.add(r4);
		bn.affectLeft(r4, 1.0, comp, v3);
		bn.affectRight(r4, 1.0, comp, v1);
		r4.setReversible(true);
		
		e1 = new CompoundEdge(r1, r2, v2);
		e2 = new CompoundEdge(r2, r3, v3);
		
		rg.addVertex(r1);
		rg.addVertex(r2);
		rg.addVertex(r3);
		rg.addVertex(r4);
		rg.addEdge(r1, r2, e1);
		rg.addEdge(r2, r3, e2);
		
		assertEquals(4, rg.vertexSet().size());
		assertEquals(2, rg.edgeSet().size());
	}
	
	@Test
	public void testGetEdgesFromCompound() {
		HashSet<CompoundEdge> edgesFromV2 = rg.getEdgesFromCompound("v2");
		assertEquals(1, edgesFromV2.size());
		assertTrue(edgesFromV2.contains(e1));
		
		edgesFromV2 = rg.getEdgesFromCompound("vX");
		 assertTrue(edgesFromV2.isEmpty());
	}
	
	@Test
	public void testGetBiochemicalReactionList(){
		 HashMap<String, BioMetabolite> cpds = rg.getCompoundList();
		 assertTrue(cpds.containsKey("v2"));
		 assertTrue(cpds.containsKey("v3"));
		 assertEquals(v2, cpds.get("v2"));
		 assertEquals(v3, cpds.get("v3"));
	}
	
//	@Test
//	public void testAddEdgesFromCompound() {
//		ReactionGraph rg2 = (ReactionGraph) rg.clone();
//		rg2.addEdgesFromCompound(v2);
//		assertEquals(4, rg2.vertexSet().size());
//		assertEquals(4, rg2.edgeSet().size());
//	}
//	
	@Test
	public void testGetEdge() {
		CompoundEdge e = rg.getEdge("r1", "r2", "v2");
		assertEquals(e1, e);
	}
	
	@Test
	public void testCopyEdge() {
		CompoundEdge e = rg.copyEdge(e1);
		assertEquals(r1, e.getV1());
		assertEquals(r2, e.getV2());
		assertEquals(v2, e.getCompound());
	}
	
	@Test
	public void testReverseEdge() {
		CompoundEdge e = rg.reverseEdge(e1);
		assertEquals(r2, e.getV1());
		assertEquals(r1, e.getV2());
		assertEquals(v2, e.getCompound());
	}
	
	@Test
	public void testCopyConstructor() {
		ReactionGraph g2 = new ReactionGraph(rg);
		assertEquals(rg.vertexSet().size(), g2.vertexSet().size());
		assertEquals(rg.edgeSet().size(), g2.edgeSet().size());
		assertTrue(g2.containsEdge(e1));
		assertTrue(g2.containsEdge(e2));
		assertTrue(g2.containsVertex(r1));
		assertTrue(g2.containsVertex(r2));
		assertTrue(g2.containsVertex(r3));
	}
	
	@Test
	public void testAddEdge(){
		ReactionGraph g2 = (ReactionGraph) rg.clone();
		g2.addEdge(r3, r4);
		assertEquals(3, g2.edgeSet().size());
	}
	
}
