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

import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.GraphSampler;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;

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
		bn = new BioNetwork();
		comp = new BioCompartment("comp"); bn.add(comp);
		a = new BioMetabolite("a"); bn.add(a);bn.affectToCompartment(comp, a); g.addVertex(a);
		b = new BioMetabolite("b"); bn.add(b);bn.affectToCompartment(comp, b); g.addVertex(b);
		c = new BioMetabolite("c"); bn.add(c);bn.affectToCompartment(comp, c); g.addVertex(c);
		d = new BioMetabolite("d"); bn.add(d);bn.affectToCompartment(comp, d); g.addVertex(d);
		e = new BioMetabolite("e"); bn.add(e);bn.affectToCompartment(comp, e); g.addVertex(e);
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 1.0);
		ac = new ReactionEdge(a,c,new BioReaction("ac"));g.addEdge(a, c, ac);g.setEdgeWeight(ac, 1.0);
		ae = new ReactionEdge(a,e,new BioReaction("ae"));g.addEdge(a, e, ae);g.setEdgeWeight(ae, 1.0);
		ad = new ReactionEdge(a,d,new BioReaction("ad"));g.addEdge(a, d, ad);g.setEdgeWeight(ad, 1.0);
		sampler = new GraphSampler<>(g);

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
	
//	@Test
//	public void testGetRandomVertexListinComp(){
//		Set<BioMetabolite> sample = sampler.getRandomVertexListinComp(1, "comp");
//		assertEquals(1, sample.size());
//		assertTrue(sample.contains(a));
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void testGetRandomVertexListinComp2(){
//		sampler.getRandomVertexListinComp(42, "comp");
//	}
	
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
		assertEquals(0.0, m2.getRowSum(0),0.00000001);
		assertEquals(1.0, m2.getRowSum(1),0.00000001);
	}	
	
}
