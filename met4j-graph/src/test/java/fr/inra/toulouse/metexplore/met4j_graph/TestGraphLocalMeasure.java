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
