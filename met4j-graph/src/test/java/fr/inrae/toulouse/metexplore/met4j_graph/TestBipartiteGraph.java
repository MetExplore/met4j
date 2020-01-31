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

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;

public class TestBipartiteGraph {

	public static BipartiteGraph bg;
	public static BioMetabolite v1,v2,v3,side;
	public static BioReaction r1,r2;

	public static BipartiteEdge e1,e2,e3,e4,e5,e6;
	public static BioCompartment comp;
	public static BioNetwork bn;
	
	@BeforeClass
	public static void init(){
		
		bg = new BipartiteGraph();
		bn = new BioNetwork();
		
		comp = new BioCompartment("comp");bn.add(comp);
		v1 = new BioMetabolite("v1");bn.add(v1); bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2");bn.add(v2); bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3");bn.add(v3); bn.affectToCompartment(comp, v3);
		side = new BioMetabolite("adp");bn.add(side); bn.affectToCompartment(comp, side);
		
		
		r1 = new BioReaction("r1"); bn.add(r1);
		bn.affectLeft(r1, 1.0, comp, v1);
		bn.affectLeft(r1, 1.0, comp, side);
		bn.affectRight(r1, 1.0, comp, v2);
		r1.setReversible(false);
		r2 = new BioReaction("r2"); bn.add(r2);
		bn.affectLeft(r2, 1.0, comp, v2);
		bn.affectRight(r2, 1.0, comp, v3);
		bn.affectRight(r2, 1.0, comp, side);
		r2.setReversible(true);
		
		bg.addVertex(v1);
		bg.addVertex(v2);
		bg.addVertex(v3);
		bg.addVertex(r1);
		bg.addVertex(r2);
		
		e1 = new BipartiteEdge(v1, r1);
		bg.addEdge(v1, r1, e1);
		e2 = new BipartiteEdge(r1, v2);
		bg.addEdge(r1, v2, e2);
		e3 = new BipartiteEdge(v2, r2, true);
		e5 = new BipartiteEdge(r2, v2, true);
		bg.addEdge(v2, r2, e3);
		bg.addEdge(r2, v2, e5);
		e4 = new BipartiteEdge(r2, v3, true);
		e6 = new BipartiteEdge(v3, r2, true);		
		bg.addEdge(r2, v3, e4);
		bg.addEdge(v3, r2, e6);
		
		assertEquals(5, bg.vertexSet().size());
		assertEquals(6, bg.edgeSet().size());
	}
	
	@Test
	public void testCompoundVertexSet(){
		Set<BioMetabolite> cpds = bg.compoundVertexSet();
		assertEquals(3, cpds.size());
		assertTrue(cpds.contains(v1));
		assertTrue(cpds.contains(v2));
		assertTrue(cpds.contains(v3));
	}
	
	@Test
	public void testReactionVertexSet(){
		Set<BioReaction> rxns = bg.reactionVertexSet();
		assertEquals(2, rxns.size());
		assertTrue(rxns.contains(r1));
		assertTrue(rxns.contains(r2));
	}
	
	@Test
	public void testCopyEdge(){
		BipartiteEdge e = bg.copyEdge(e6);
		assertEquals(v3, e.getV1());
		assertEquals(r2, e.getV2());
		assertEquals(true, e.isReversible());
		assertEquals(false, e.isSide());
	}
	
	@Test
	public void testMergeReversibleEdges(){
		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
		bg2.mergeReversibleEdges(bn);
		assertEquals(5, bg2.vertexSet().size());
		assertEquals(4, bg2.edgeSet().size());
	}
	
	@Test
	public void testAddMissingCompoundAsSide(){
		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
		bg2.addMissingCompoundAsSide(bn);
		assertEquals(6, bg2.vertexSet().size());
		assertEquals(9, bg2.edgeSet().size());
		assertTrue(bg2.vertexSet().contains(side));
		for(BipartiteEdge e : bg2.edgesOf(side)){
			assertTrue(e.isSide());
		}
	}
	
//	@Test
//	public void testDuplicateSideCompounds(){
//		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
//		bg2.addVertex(side);
//		BipartiteEdge e1 = new BipartiteEdge(side, r1, false);
//		e1.setSide(true);
//		BipartiteEdge e2 = new BipartiteEdge(r2, side, true);
//		bg2.addEdge(side, r1, e1);
//		bg2.addEdge(r2, side, e2);
//		
//		bg2.duplicateSideCompounds();
//		assertEquals(7, bg2.vertexSet().size());
//		assertEquals(8, bg2.edgeSet().size());
//		assertFalse(bg2.vertexSet().contains(side));
//		
//		for(BioMetabolite v : bg2.compoundVertexSet()){
//			if(v.getIsSide()){
//				for(BipartiteEdge e : bg2.edgesOf(v)){
//					assertTrue(e.isSide());
//				}
//			}
//		}
//	}
	
	@Test
	public void testReverseEdge(){
		BipartiteEdge r = bg.reverseEdge(e1);
		assertEquals(v1, r.getV2());
		assertEquals(r1, r.getV1());
		assertEquals(e1.getLabel(), r.getLabel());
		assertEquals(false, r.isReversible());
		assertEquals(false, r.isSide());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testaddEdge1(){
		BipartiteEdge e = new BipartiteEdge(v1, v2, true);
		e.toString();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testaddEdge2(){
		BipartiteEdge e = new BipartiteEdge(r1, r2, true);
		e.toString();
	}	
	
	@Test
	public void testaddEdge4(){
		BipartiteGraph bg2 = (BipartiteGraph) bg.clone();
		bg2.addEdge(v1, r1);
		bg2.addEdge(r1,v1);
		assertEquals(8, bg2.edgeSet().size());
	}


	@Test
	public void testIsConsistent(){
		BipartiteGraph g1 = new BipartiteGraph();
		g1.addVertex(v1);
		g1.addVertex(v2);
		g1.addVertex(v3);
		g1.addVertex(r1);
		g1.addVertex(side);
		g1.addEdge(v1,r1, new BipartiteEdge(v1,r1));
		g1.addEdge(r1,v2, new BipartiteEdge(r1,v2));

		assertFalse(g1.isConsistent());

		g1.addEdge(side,r1, new BipartiteEdge(side,r1));
		assertTrue(g1.isConsistent());

		g1.addEdge(r1,v3, new BipartiteEdge(r1,v3));
		assertFalse(g1.isConsistent());
	}
	
}
