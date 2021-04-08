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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.SteinerTreeApprox;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;


/**
 * Test {@link SteinerTreeApprox}
 * @author clement
 */
public class TestSteinerTreeApprox {
	

	public static CompoundGraph g;
	
	public static BioMetabolite a,b,c,d,e,f,x,y,z;
	
	public static ReactionEdge az,zb,ab,xb,bc,cx,yx,ay,ea,ey,xd,ed,fe,df;
	 

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
		az = new ReactionEdge(a,z,new BioReaction("az"));g.addEdge(a, z, az);g.setEdgeWeight(az, 2);
		zb = new ReactionEdge(z,b,new BioReaction("zb"));g.addEdge(z, b, zb);g.setEdgeWeight(zb, 8);
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 9);
		xb = new ReactionEdge(x,b,new BioReaction("xb"));g.addEdge(x, b, xb);g.setEdgeWeight(xb, 4);
		bc = new ReactionEdge(b,c,new BioReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 8);
		cx = new ReactionEdge(c,x,new BioReaction("cx"));g.addEdge(c, x, cx);g.setEdgeWeight(cx, 3);
		yx = new ReactionEdge(y,x,new BioReaction("yx"));g.addEdge(y, x, yx);g.setEdgeWeight(yx, 1);
		ay = new ReactionEdge(a,y,new BioReaction("ay"));g.addEdge(a, y, ay);g.setEdgeWeight(ay, 2);
		ea = new ReactionEdge(e,a,new BioReaction("ea"));g.addEdge(e, a, ea);g.setEdgeWeight(ea, 8);
		ey = new ReactionEdge(e,y,new BioReaction("ey"));g.addEdge(e, y, ey);g.setEdgeWeight(ey, 2);
		xd = new ReactionEdge(x,d,new BioReaction("xd"));g.addEdge(x, d, xd);g.setEdgeWeight(xd, 6);
		ed = new ReactionEdge(e,d,new BioReaction("ed"));g.addEdge(e, d, ed);g.setEdgeWeight(ed, 5);
		fe = new ReactionEdge(f,e,new BioReaction("fe"));g.addEdge(f, e, fe);g.setEdgeWeight(fe, 8);
		df = new ReactionEdge(d,f,new BioReaction("df"));g.addEdge(d, f, df);g.setEdgeWeight(df, 8);

	}
	
	/**
	 * Test the steiner tree list.
	 */
	@Test
	public void testSteinerTreeList(){
		HashSet<BioMetabolite> noi = new HashSet<>();
		noi.add(a);noi.add(b);noi.add(c);noi.add(d);noi.add(e);
		
		ReactionEdge[] expectedPath = {ey, yx, ay, ed, cx, xb};
		SteinerTreeApprox<BioMetabolite, ReactionEdge, CompoundGraph> steinerComputer 
			= new SteinerTreeApprox<>(g, true);
		List<ReactionEdge> treeList = steinerComputer.getSteinerTreeList(noi, true);
		
		assertNotNull("No path found", treeList);

		assertTrue("wrong path",Arrays.asList(expectedPath).containsAll(treeList));
	}
	
	
	/**
	 * Test the steiner tree list.
	 */
	@Test
	public void testSteinerTreeList2(){
		HashSet<BioMetabolite> noi = new HashSet<>();
		noi.add(a);noi.add(b);noi.add(c);noi.add(d);noi.add(e);
		
		ReactionEdge[] expectedPath = {ey, yx, ay, ed, cx, xb};
		SteinerTreeApprox<BioMetabolite, ReactionEdge, CompoundGraph> steinerComputer 
			= new SteinerTreeApprox<>(g);
		List<ReactionEdge> treeList = steinerComputer.getSteinerTreeList(noi,noi, true);
		
		assertNotNull("No path found", treeList);

		assertTrue("wrong path",Arrays.asList(expectedPath).containsAll(treeList));
	}

//	/**
//	 * Test the steiner tree sub graph.
//	 */
//	@Test
//	public void testSteinerTreeSubGraph() {
//		HashSet<BioMetabolite> noi = new HashSet<BioMetabolite>();
//		noi.add(a);noi.add(b);noi.add(c);noi.add(d);noi.add(e);
//		
//		SteinerTreeApprox<BioMetabolite, ReactionEdge, CompoundGraph> steinerComputer 
//			= new SteinerTreeApprox<BioMetabolite, ReactionEdge, CompoundGraph>(g, true);
//		CompoundGraph subGraph = steinerComputer.getSteinerTree(noi, true);
//		assertEquals(6, subGraph.edgeSet().size());
//		assertEquals(7, subGraph.vertexSet().size());
//	}
}
