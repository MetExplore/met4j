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
import java.util.HashSet;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.computation.algo.SteinerTreeApprox;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;


/**
 * Test {@link SteinerTreeApprox}
 * @author clement
 */
public class TestSteinerTreeApprox {
	

	public static CompoundGraph g;
	
	public static BioPhysicalEntity a,b,c,d,e,f,x,y,z;
	
	public static ReactionEdge az,zb,ab,xb,bc,cx,yx,ay,ea,ey,xd,ed,fe,df;
	 

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
		az = new ReactionEdge(a,z,new BioChemicalReaction("az"));g.addEdge(a, z, az);g.setEdgeWeight(az, 2);
		zb = new ReactionEdge(z,b,new BioChemicalReaction("zb"));g.addEdge(z, b, zb);g.setEdgeWeight(zb, 8);
		ab = new ReactionEdge(a,b,new BioChemicalReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 9);
		xb = new ReactionEdge(x,b,new BioChemicalReaction("xb"));g.addEdge(x, b, xb);g.setEdgeWeight(xb, 4);
		bc = new ReactionEdge(b,c,new BioChemicalReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 8);
		cx = new ReactionEdge(c,x,new BioChemicalReaction("cx"));g.addEdge(c, x, cx);g.setEdgeWeight(cx, 3);
		yx = new ReactionEdge(y,x,new BioChemicalReaction("yx"));g.addEdge(y, x, yx);g.setEdgeWeight(yx, 1);
		ay = new ReactionEdge(a,y,new BioChemicalReaction("ay"));g.addEdge(a, y, ay);g.setEdgeWeight(ay, 2);
		ea = new ReactionEdge(e,a,new BioChemicalReaction("ea"));g.addEdge(e, a, ea);g.setEdgeWeight(ea, 8);
		ey = new ReactionEdge(e,y,new BioChemicalReaction("ey"));g.addEdge(e, y, ey);g.setEdgeWeight(ey, 2);
		xd = new ReactionEdge(x,d,new BioChemicalReaction("xd"));g.addEdge(x, d, xd);g.setEdgeWeight(xd, 6);
		ed = new ReactionEdge(e,d,new BioChemicalReaction("ed"));g.addEdge(e, d, ed);g.setEdgeWeight(ed, 5);
		fe = new ReactionEdge(f,e,new BioChemicalReaction("fe"));g.addEdge(f, e, fe);g.setEdgeWeight(fe, 8);
		df = new ReactionEdge(d,f,new BioChemicalReaction("df"));g.addEdge(d, f, df);g.setEdgeWeight(df, 8);

	}
	
	/**
	 * Test the steiner tree list.
	 */
	@Test
	public void testSteinerTreeList(){
		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
		noi.add(a);noi.add(b);noi.add(c);noi.add(d);noi.add(e);
		
		ReactionEdge[] expectedPath = {ey, yx, ay, ed, cx, xb};
		SteinerTreeApprox<BioPhysicalEntity, ReactionEdge, CompoundGraph> steinerComputer 
			= new SteinerTreeApprox<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g, true);
		List<ReactionEdge> treeList = steinerComputer.getSteinerTreeList(noi, true);
		
		assertNotNull("No path found", treeList);
		System.out.println(treeList);
		
		assertTrue("wrong path",Arrays.asList(expectedPath).containsAll(treeList));
	}
	
	
	/**
	 * Test the steiner tree list.
	 */
	@Test
	public void testSteinerTreeList2(){
		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
		noi.add(a);noi.add(b);noi.add(c);noi.add(d);noi.add(e);
		
		ReactionEdge[] expectedPath = {ey, yx, ay, ed, cx, xb};
		SteinerTreeApprox<BioPhysicalEntity, ReactionEdge, CompoundGraph> steinerComputer 
			= new SteinerTreeApprox<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		List<ReactionEdge> treeList = steinerComputer.getSteinerTreeList(noi,noi, true);
		
		assertNotNull("No path found", treeList);
		System.out.println(treeList);
		
		assertTrue("wrong path",Arrays.asList(expectedPath).containsAll(treeList));
	}

//	/**
//	 * Test the steiner tree sub graph.
//	 */
//	@Test
//	public void testSteinerTreeSubGraph() {
//		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
//		noi.add(a);noi.add(b);noi.add(c);noi.add(d);noi.add(e);
//		
//		SteinerTreeApprox<BioPhysicalEntity, ReactionEdge, CompoundGraph> steinerComputer 
//			= new SteinerTreeApprox<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g, true);
//		CompoundGraph subGraph = steinerComputer.getSteinerTree(noi, true);
//		assertEquals(6, subGraph.edgeSet().size());
//		assertEquals(7, subGraph.vertexSet().size());
//	}
}
