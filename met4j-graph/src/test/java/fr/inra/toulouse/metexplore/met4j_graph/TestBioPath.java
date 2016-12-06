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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

/**
 * The Class TestBioPath.
 * @author clement
 */
public class TestBioPath {
	
	/** The graph. */
	public static CompoundGraph g;
	
	/** The path. */
	public static BioPath<BioPhysicalEntity,ReactionEdge> p;
	
	/** The nodes. */
	public static BioPhysicalEntity a,b,c,d,e,f,h,i;
	
	/** The edges. */
	public static ReactionEdge ab,bc,ad,de,ef,fc,bh,eb,ic;
	
	/**
	 * Inits the graph.
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
		h = new BioPhysicalEntity("h"); g.addVertex(h);
		i = new BioPhysicalEntity("i"); g.addVertex(i);
		ab = new ReactionEdge(a,b,new BioChemicalReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 1.0);
		bc = new ReactionEdge(b,c,new BioChemicalReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 1.0);
		ad = new ReactionEdge(a,d,new BioChemicalReaction("ad"));g.addEdge(a, d, ad);g.setEdgeWeight(ad, 1.0);
		de = new ReactionEdge(d,e,new BioChemicalReaction("de"));g.addEdge(d, e, de);g.setEdgeWeight(de, 2.0);
		ef = new ReactionEdge(e,f,new BioChemicalReaction("ef"));g.addEdge(e, f, ef);g.setEdgeWeight(ef, 1.0);
		fc = new ReactionEdge(f,c,new BioChemicalReaction("fc"));g.addEdge(f, c, fc);g.setEdgeWeight(fc, 1.0);
		bh = new ReactionEdge(b,h,new BioChemicalReaction("bh"));g.addEdge(b, h, bh);g.setEdgeWeight(bh, 1.0);
		eb = new ReactionEdge(e,b,new BioChemicalReaction("eb"));g.addEdge(e, b, eb);g.setEdgeWeight(eb, 1.0);
		ic = new ReactionEdge(i,c,new BioChemicalReaction("ic"));g.addEdge(i, c, ic);g.setEdgeWeight(ic, 1.0);
		
		
		BioChemicalReaction r1 = new BioChemicalReaction("acyz");
		r1.setReversibility(true);
		ReactionEdge az,za,zc,cz;
		ReactionEdge ay,ya,yc,cy;
		BioPhysicalEntity z = new BioPhysicalEntity("z"); g.addVertex(z);
		BioPhysicalEntity y = new BioPhysicalEntity("y"); g.addVertex(y);
		az = new ReactionEdge(a,z,r1);g.addEdge(a, z, az);g.setEdgeWeight(az, 0.45);
		za = new ReactionEdge(z,a,r1);g.addEdge(z, a, za);g.setEdgeWeight(za, 0.45);
		zc = new ReactionEdge(z,c,r1);g.addEdge(z, c, zc);g.setEdgeWeight(zc, 0.55);
		cz = new ReactionEdge(c,z,r1);g.addEdge(c, z, cz);g.setEdgeWeight(cz, 0.55);
		
		ay = new ReactionEdge(a,y,r1);g.addEdge(a, y, ay);g.setEdgeWeight(ay, 0.55);
		ya = new ReactionEdge(y,a,r1);g.addEdge(y, a, ya);g.setEdgeWeight(ya, 0.55);
		yc = new ReactionEdge(y,c,r1);g.addEdge(y, c, yc);g.setEdgeWeight(yc, 0.45);
		cy = new ReactionEdge(c,y,r1);g.addEdge(c, y, cy);g.setEdgeWeight(cy, 0.45);
		
		BioPhysicalEntity z2 = new BioPhysicalEntity("z2"); g.addVertex(z2);
		BioPhysicalEntity z3 = new BioPhysicalEntity("z3"); g.addVertex(z3);
		ReactionEdge zz2,z2z3,z3y;
		zz2 = new ReactionEdge(z,z2,new BioChemicalReaction("zz2"));g.addEdge(z, z2, zz2);g.setEdgeWeight(zz2, 0.0001);
		z2z3 = new ReactionEdge(z2,z3,new BioChemicalReaction("z2z3"));g.addEdge(z2, z3, z2z3);g.setEdgeWeight(z2z3, 0.0001);
		z3y = new ReactionEdge(z3,y,new BioChemicalReaction("z3y"));g.addEdge(z3, y, z3y);g.setEdgeWeight(z3y, 0.0001);
	
		List<ReactionEdge> pathList = new ArrayList<ReactionEdge>();
		pathList.add(ad);
		pathList.add(de);
		pathList.add(ef);
		try{
			p = new BioPath<BioPhysicalEntity,ReactionEdge>(g, a, f, pathList, 4.0);
		}catch(Exception e){
			fail("error while creating BioPath");
		}
	}
	
	/**
	 * Test the path values.
	 */
	@Test
	public void testPathValues() {
		assertEquals("error computing path length",3.0,p.getLength(),Double.MIN_VALUE);
		assertEquals("error computing path weigth",4.0,p.getWeight(),Double.MIN_VALUE);
	}
	
	/**
	 * Test the append path.
	 */
	@Test
	public void testAppendPath() {
		List<ReactionEdge> pathList = new ArrayList<ReactionEdge>();
		pathList.add(ad);
		pathList.add(de);
		pathList.add(ef);
		pathList.add(fc);
		
		List<ReactionEdge> pathListToAppend = new ArrayList<ReactionEdge>();
		pathListToAppend.add(fc);
		BioPath<BioPhysicalEntity,ReactionEdge> p2 = new BioPath<BioPhysicalEntity,ReactionEdge>(g, f, c, pathListToAppend, 1.0);
		BioPath<BioPhysicalEntity,ReactionEdge> p3 = p.appendPath(p2);
		
		assertEquals("error in edge list after appendPath",pathList,p3.getEdgeList());
		assertEquals("error in path length after appendPath",4.0,p3.getLength(),Double.MIN_VALUE);
		assertEquals("error in path weight after appendPath",5.0,p3.getWeight(),Double.MIN_VALUE);
		assertEquals("error in path starting node after appendPath",a,p3.getStartVertex());
		assertEquals("error in path ending node after appendPath",c,p3.getEndVertex());	
	}
	
	/**
	 * Test the sub path.
	 */
	@Test
	public void testSubPath() {
		BioPath<BioPhysicalEntity,ReactionEdge> p4 = p.getSubPath(d, f);
		List<ReactionEdge> pathList = new ArrayList<ReactionEdge>();
		pathList.add(de);
		pathList.add(ef);
		
		assertEquals("error in edge list after subPath",pathList,p4.getEdgeList());
		assertEquals("error in path length after subPath",2.0,p4.getLength(),Double.MIN_VALUE);
		assertEquals("error in path weight after subPath",3.0,p4.getWeight(),Double.MIN_VALUE);
		assertEquals("error in path starting node after subPath",d,p4.getStartVertex());
		assertEquals("error in path ending node after subPath",f,p4.getEndVertex());	
	}
	
	@Test
	public void testIterator() {
		Iterator<ReactionEdge> iterator = p.iterator();
		assertTrue(iterator.hasNext());
		assertEquals(ad,iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(de,iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(ef,iterator.next());
		assertFalse(iterator.hasNext());
	}

}
