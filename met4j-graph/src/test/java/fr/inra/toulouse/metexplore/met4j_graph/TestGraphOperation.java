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

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.GraphOperation;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

/**
 *  Test {@link GraphOperation}
 *  @author clement
 */
public class TestGraphOperation {
	
	public static CompoundGraph g1;
	
	public static CompoundGraph g2;
	
	public static BioPhysicalEntity a,b,c,d,e;
	
	public static ReactionEdge ab,bc,ac,cd,ce;
	 
	@BeforeClass
	public static void init(){
		g1 = new CompoundGraph();
		g2 = new CompoundGraph();
		a = new BioPhysicalEntity("a"); g1.addVertex(a);g2.addVertex(a);
		b = new BioPhysicalEntity("b"); g1.addVertex(b);g2.addVertex(b);
		c = new BioPhysicalEntity("c"); g1.addVertex(c);g2.addVertex(c);
		d = new BioPhysicalEntity("d"); g1.addVertex(d);
		e = new BioPhysicalEntity("e"); g2.addVertex(e);
		
		ab = new ReactionEdge(a,b,new BioChemicalReaction("ab"));g1.addEdge(a, b, ab);g2.addEdge(a, b, ab);
		bc = new ReactionEdge(b,c,new BioChemicalReaction("bc"));g1.addEdge(b, c, bc);g2.addEdge(a, b, ab);
		ac = new ReactionEdge(a,c,new BioChemicalReaction("ac"));g1.addEdge(a, c, ac);
		cd = new ReactionEdge(c,d,new BioChemicalReaction("cd"));g1.addEdge(c, d, cd);
		ce = new ReactionEdge(c,e,new BioChemicalReaction("ce"));g2.addEdge(c, e, ce);
	}
	
	/**
	 * Test the intersection.
	 */
	@Test
	public void testIntersect() {
		CompoundGraph i = new CompoundGraph();
		i=GraphOperation.intersect(g1, g2,CompoundGraph.getFactory());
		BioPhysicalEntity[] expectedVertex = {a,b,c};
		ReactionEdge[] expectedEdges = {ab,bc};
		assertTrue("missing edge in intersection", Arrays.asList(expectedEdges).containsAll(i.edgeSet()));
		assertTrue("missing vertex in intersection", Arrays.asList(expectedVertex).containsAll(i.vertexSet()));
		assertEquals("error in intersection order", i.vertexSet().size(), GraphOperation.intersectOrder(g1, g2));
		assertEquals("error in intersection size", i.edgeSet().size(), GraphOperation.intersectSize(g1, g2));
	}
	
	/**
	 * Test the union.
	 */
	@Test
	public void testUnion() {
		CompoundGraph u = new CompoundGraph();
		u=GraphOperation.union(g1, g2, CompoundGraph.getFactory());
		BioPhysicalEntity[] expectedVertex = {a,b,c,d,e};
		ReactionEdge[] expectedEdges = {ab,bc,ac,cd,ce};
		assertTrue("missing edge in intersection", Arrays.asList(expectedEdges).containsAll(u.edgeSet()));
		assertTrue("missing vertex in intersection", Arrays.asList(expectedVertex).containsAll(u.vertexSet()));
		assertEquals("error in intersection order", u.vertexSet().size(), GraphOperation.unionOrder(g1, g2));
		assertEquals("error in intersection size", u.edgeSet().size(), GraphOperation.unionSize(g1, g2));
	}
	
}
