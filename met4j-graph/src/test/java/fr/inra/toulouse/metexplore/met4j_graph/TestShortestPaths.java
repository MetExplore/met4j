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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.computation.algo.FloydWarshall;
import fr.inra.toulouse.metexplore.met4j_graph.computation.algo.KShortestPath;
import fr.inra.toulouse.metexplore.met4j_graph.computation.algo.ShortestPath;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.GraphCentralityMeasure;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.CompressedGraph;
import fr.inra.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;

/**
 * Test {@link ShortestPath}
 * @author clement
 */
public class TestShortestPaths {
	
	public static CompoundGraph g;
	
	public static BioPhysicalEntity a,b,c,d,e,f,h,i;
	
	public static ReactionEdge ab,bc,ad,de,ef,fc,bh,eb,ic;
	 
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
		de = new ReactionEdge(d,e,new BioChemicalReaction("de"));g.addEdge(d, e, de);g.setEdgeWeight(de, 1.0);
		ef = new ReactionEdge(e,f,new BioChemicalReaction("ef"));g.addEdge(e, f, ef);g.setEdgeWeight(ef, 1.0);
		fc = new ReactionEdge(f,c,new BioChemicalReaction("fc"));g.addEdge(f, c, fc);g.setEdgeWeight(fc, 1.0);
		bh = new ReactionEdge(b,h,new BioChemicalReaction("bh"));g.addEdge(b, h, bh);g.setEdgeWeight(bh, 1.0);
		eb = new ReactionEdge(e,b,new BioChemicalReaction("eb"));g.addEdge(e, b, eb);g.setEdgeWeight(eb, 1.0);
		ic = new ReactionEdge(i,c,new BioChemicalReaction("ic"));g.addEdge(i, c, ic);g.setEdgeWeight(ic, 1.0);
		
		
//		BioChemicalReaction r1 = new BioChemicalReaction("acyz");
//		r1.setReversibility(true);
//		ReactionEdge az,za,zc,cz;
//		ReactionEdge ay,ya,yc,cy;
//		BioPhysicalEntity z = new BioPhysicalEntity("z"); g.addVertex(z);
//		BioPhysicalEntity y = new BioPhysicalEntity("y"); g.addVertex(y);
//		az = new ReactionEdge(a,z,r1);g.addEdge(a, z, az);g.setEdgeWeight(az, 0.45);
//		za = new ReactionEdge(z,a,r1);g.addEdge(z, a, za);g.setEdgeWeight(za, 0.45);
//		zc = new ReactionEdge(z,c,r1);g.addEdge(z, c, zc);g.setEdgeWeight(zc, 0.55);
//		cz = new ReactionEdge(c,z,r1);g.addEdge(c, z, cz);g.setEdgeWeight(cz, 0.55);
//		
//		ay = new ReactionEdge(a,y,r1);g.addEdge(a, y, ay);g.setEdgeWeight(ay, 0.55);
//		ya = new ReactionEdge(y,a,r1);g.addEdge(y, a, ya);g.setEdgeWeight(ya, 0.55);
//		yc = new ReactionEdge(y,c,r1);g.addEdge(y, c, yc);g.setEdgeWeight(yc, 0.45);
//		cy = new ReactionEdge(c,y,r1);g.addEdge(c, y, cy);g.setEdgeWeight(cy, 0.45);
//		
//		BioPhysicalEntity z2 = new BioPhysicalEntity("z2"); g.addVertex(z2);
//		BioPhysicalEntity z3 = new BioPhysicalEntity("z3"); g.addVertex(z3);
//		ReactionEdge zz2,z2z3,z3y;
//		zz2 = new ReactionEdge(z,z2,new BioChemicalReaction("zz2"));g.addEdge(z, z2, zz2);g.setEdgeWeight(zz2, 0.0001);
//		z2z3 = new ReactionEdge(z2,z3,new BioChemicalReaction("z2z3"));g.addEdge(z2, z3, z2z3);g.setEdgeWeight(z2z3, 0.0001);
//		z3y = new ReactionEdge(z3,y,new BioChemicalReaction("z3y"));g.addEdge(z3, y, z3y);g.setEdgeWeight(z3y, 0.0001);
		
		for(ReactionEdge e : g.edgeSet()){
			System.out.println(e.getV1().getId()+"-["+e+"]->"+e.getV2().getId());
		}
	}
	
	/**
	 * Reset weight.
	 */
	@After
	public void resetWeight(){
		for(ReactionEdge e : g.edgeSet()){
			g.setEdgeWeight(e, 1.0);
		}
	}
	
	/**
	 * Test the get shortest.
	 */
	@Test
	public void testGetShortest() {
		ReactionEdge[] expectedPath = {ab, bc};
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		BioPath<BioPhysicalEntity,ReactionEdge> path = pathSearch.getShortest(a, c);
		assertNotNull(path);
		List<ReactionEdge> sp = path.getEdgeList();
		assertTrue("wrong path", Arrays.asList(expectedPath).containsAll(sp));
		assertTrue("wrong path", sp.containsAll(Arrays.asList(expectedPath)));
		
		g.setEdgeWeight(bc, 1000.0);
		ReactionEdge[] expectedLightestPath = {ad, de, ef, fc};
		BioPath<BioPhysicalEntity,ReactionEdge> path2 =pathSearch.getShortest(a, c);
		assertNotNull(path2);
		List<ReactionEdge> res = path2.getEdgeList();
		assertTrue("wrong weighted path", Arrays.asList(expectedLightestPath).containsAll(res));
		assertTrue("wrong weighted path", res.containsAll(Arrays.asList(expectedLightestPath)));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetShortestNoStartException() {
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		BioPath<BioPhysicalEntity,ReactionEdge> path = pathSearch.getShortest(new BioPhysicalEntity("u"), c);
		System.out.println(path);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetShortestNoTargetException() {
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		BioPath<BioPhysicalEntity,ReactionEdge> path = pathSearch.getShortest(a, new BioPhysicalEntity("u"));
		System.out.println(path);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetUndirectedShortestNoStartException() {
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		BioPath<BioPhysicalEntity,ReactionEdge> path = pathSearch.getShortestAsUndirected(new BioPhysicalEntity("u"), c);
		System.out.println(path);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetUndirectedShortestNoTargetException() {
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		BioPath<BioPhysicalEntity,ReactionEdge> path = pathSearch.getShortestAsUndirected(a, new BioPhysicalEntity("u"));
		System.out.println(path);
	}
	
	@Test
	public void testGetShortestundirected() {
		ReactionEdge[] expectedPath = {bc, ab};
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		BioPath<BioPhysicalEntity,ReactionEdge> path = pathSearch.getShortestAsUndirected(c, a);
		assertNotNull(path);
		List<ReactionEdge> sp = path.getEdgeList();
		assertTrue("wrong path", Arrays.asList(expectedPath).containsAll(sp));
		assertTrue("wrong path", sp.containsAll(Arrays.asList(expectedPath)));
		
		g.setEdgeWeight(bc, 1000.0);
		g.setEdgeWeight(ab, 1000.0);
		ReactionEdge[] expectedLightestPath = {fc, ef, de, ad};
		BioPath<BioPhysicalEntity,ReactionEdge> path2 =pathSearch.getShortestAsUndirected(c, a);
		assertNotNull(path2);
		List<ReactionEdge> res = path2.getEdgeList();
		System.out.println(path2.toString());
		assertTrue("wrong weighted path", Arrays.asList(expectedLightestPath).containsAll(res));
		assertTrue("wrong weighted path", res.containsAll(Arrays.asList(expectedLightestPath)));
	}
	
//	@Test
//	public void testReversibility() {
//		CompoundGraph g2 = new CompoundGraph();
//		BioPhysicalEntity x = new BioPhysicalEntity("x"); g2.addVertex(x);
//		BioPhysicalEntity a = new BioPhysicalEntity("a"); g2.addVertex(a);
//		BioPhysicalEntity b = new BioPhysicalEntity("b"); g2.addVertex(b);
//		BioPhysicalEntity c = new BioPhysicalEntity("c"); g2.addVertex(c);
//		BioPhysicalEntity d = new BioPhysicalEntity("d"); g2.addVertex(d);
//		BioPhysicalEntity e = new BioPhysicalEntity("e"); g2.addVertex(e);
//		BioPhysicalEntity f = new BioPhysicalEntity("f"); g2.addVertex(f);
//		BioPhysicalEntity g = new BioPhysicalEntity("g"); g2.addVertex(g);
//		BioPhysicalEntity y = new BioPhysicalEntity("y"); g2.addVertex(y);
//		
//		BioChemicalReaction r1 = new BioChemicalReaction("1");
//		BioChemicalReaction r2 = new BioChemicalReaction("2");
//		BioChemicalReaction r3 = new BioChemicalReaction("3");
//		BioChemicalReaction r4 = new BioChemicalReaction("4");
//		BioChemicalReaction r5 = new BioChemicalReaction("5");
//		BioChemicalReaction r6 = new BioChemicalReaction("6");
//		BioChemicalReaction r7 = new BioChemicalReaction("7");
//		BioChemicalReaction r8 = new BioChemicalReaction("8");
//		BioChemicalReaction r9 = new BioChemicalReaction("9");r9.setReversibility(true);
//		
//		ReactionEdge xa = new ReactionEdge(x,a,r1);g2.addEdge(x, a, xa);g2.setEdgeWeight(xa, 1.0);
//		ReactionEdge xb = new ReactionEdge(x,b,r2);g2.addEdge(x, b, xb);g2.setEdgeWeight(xb, 1.0);
//		ReactionEdge ad = new ReactionEdge(a,d,r4);g2.addEdge(a, d, ad);g2.setEdgeWeight(ad, 1.0);
//		ReactionEdge de = new ReactionEdge(d,e,r6);g2.addEdge(d, e, de);g2.setEdgeWeight(de, 1.0);
//		ReactionEdge be = new ReactionEdge(b,e,r9);g2.addEdge(b, e, be);g2.setEdgeWeight(be, 1.0);
//		ReactionEdge eb = new ReactionEdge(e,b,r9);g2.addEdge(e, b, eb);g2.setEdgeWeight(eb, 1.0);
//		ReactionEdge ec = new ReactionEdge(e,c,r9);g2.addEdge(e, c, ec);g2.setEdgeWeight(ec, 1.0);
//		ReactionEdge ef = new ReactionEdge(e,f,r7);g2.addEdge(e, f, ef);g2.setEdgeWeight(ef, 1.0);
//		ReactionEdge fg = new ReactionEdge(f,g,r8);g2.addEdge(f, g, fg);g2.setEdgeWeight(fg, 1.0);
//		ReactionEdge gc = new ReactionEdge(g,c,r5);g2.addEdge(g, c, gc);g2.setEdgeWeight(gc, 1.0);
//		ReactionEdge cy = new ReactionEdge(c,y,r3);g2.addEdge(c, y, cy);g2.setEdgeWeight(cy, 1.0);
//
//		ReactionEdge[] expectedPath = {xa,ad,de,ec,cy};
//		ValidShortestPath vsp = new ValidShortestPath(g2);
//		BioPath<BioPhysicalEntity, ReactionEdge> path = vsp.getShortest(x,y);
//		assertNotNull(path);
//		List<ReactionEdge> sp = path.getEdgeList();
//		for(ReactionEdge edge : sp){
//			System.out.println(edge.getV1().getId()+" -> "+edge.getV2().getId()+" ["+edge.getReaction().getId()+"]");
//		}
//		
//		
//		assertTrue("wrong path", Arrays.asList(expectedPath).containsAll(sp));
//	}


	
	/**
	 * Test the get k shortest.
	 */
	@Test
	public void testGetKShortest() {
		ReactionEdge[] expectedPath = {ab, bc, ad, de, ef, fc, ad, de, eb, bc};
		
//		long start = System.nanoTime();
		KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		List<BioPath<BioPhysicalEntity,ReactionEdge>> kshort = pathSearch.getKShortest(g.getVertex("a"), g.getVertex("c"),3);
		List<ReactionEdge> res = new ArrayList<ReactionEdge>();
		for(BioPath<BioPhysicalEntity,ReactionEdge> p : kshort){
			res.addAll(p.getEdgeList());
		}
//		long end = System.nanoTime();
//		long start2 = System.nanoTime();
//		List<ReactionEdge> res2 = ShortestPaths.getKShortestII(g, g.getVertex("a"), g.getVertex("c"),3);
//		long end2 = System.nanoTime();
//		System.out.println("new : "+(end-start));
//		System.out.println("old : "+(end2-start2));
		
		assertTrue("wrong path", Arrays.asList(expectedPath).containsAll(res));
		assertTrue("wrong path", res.containsAll(Arrays.asList(expectedPath)));
	}
	
	/**
	 * Test the get k shortest union list.
	 */
	@Test
	public void testGetKShortestUnionList() {
		g.setEdgeWeight(ef,500.0);
		ReactionEdge[] expectedPath = {ic, ab, bc, ad, de, eb, bc};
		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
		noi.add(a);noi.add(c);noi.add(i);
		KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		List<BioPath<BioPhysicalEntity, ReactionEdge>> kshort = pathSearch.getKShortestPathsUnionList(noi, 2);
		List<ReactionEdge> res = new ArrayList<ReactionEdge>();
		for(BioPath<BioPhysicalEntity, ReactionEdge> p : kshort){
			res.addAll(p.getEdgeList());
		}
		assertTrue("wrong path", Arrays.asList(expectedPath).containsAll(res));
		assertTrue("wrong path", res.containsAll(Arrays.asList(expectedPath)));
	}
	
	/**
	 * Test the get k shortest iterative union
	 */
	@Test
	public void testGetKShortestIterativeUnion() {
		g.setEdgeWeight(ef,500.0);
		KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		ReactionEdge[] expectedPath = {ic, ab, bc};
		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
		noi.add(a);noi.add(c);noi.add(i);
		List<BioPath<BioPhysicalEntity, ReactionEdge>> kshort = pathSearch.getKShortestPathsUnionList(noi, 2);
		CompoundGraph subNet =  CompoundGraph.getFactory().createGraphFromPathList(kshort, noi);
		List<ReactionEdge> res = new ArrayList<ReactionEdge>(subNet.edgeSet());
		assertTrue("wrong path", Arrays.asList(expectedPath).containsAll(res));
		assertTrue("wrong path", res.containsAll(Arrays.asList(expectedPath)));
	}
	
	/**
	 * Test the get min distance computation
	 */
	@Test	
	public void testGetMinSpDistance(){
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> sp = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		HashSet<BioPhysicalEntity> sources = new HashSet<BioPhysicalEntity>();
		HashSet<BioPhysicalEntity> targets = new HashSet<BioPhysicalEntity>();
		sources.add(c);
		targets.add(a);
		targets.add(d);
		HashMap<BioPhysicalEntity, Double> min = sp.getMinSpDistance(sources, targets, false);
		
		assertNotNull(min);
		assertTrue(min.containsKey(c));
		assertEquals(2.0, min.get(c), Double.MIN_VALUE);
		
		HashMap<BioPhysicalEntity, Double> min2 = sp.getMinSpDistance(sources, targets, true);
		
		assertNotNull(min2);
		assertTrue(min2.containsKey(c));
		assertEquals(2.0, min2.get(c), Double.MIN_VALUE);
	}
	
	/**
	 * Test the get average distance computation
	 */
	@Test	
	public void testGetAverageSpDistance(){
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> sp = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		HashSet<BioPhysicalEntity> sources = new HashSet<BioPhysicalEntity>();
		HashSet<BioPhysicalEntity> targets = new HashSet<BioPhysicalEntity>();
		sources.add(c);
		targets.add(a);
		targets.add(d);
		HashMap<BioPhysicalEntity, Double> avg = sp.getAverageSpDistance(sources, targets, false);
		
		assertNotNull(avg);
		assertTrue(avg.containsKey(c));
		assertEquals(2.5, avg.get(c), Double.MIN_VALUE);
		
		HashMap<BioPhysicalEntity, Double> avg2 = sp.getAverageSpDistance(sources, targets, true);
		
		assertNotNull(avg2);
		assertTrue(avg2.containsKey(c));
		assertEquals(2.5, avg2.get(c), Double.MIN_VALUE);
	}
	
	/**
	 * Test the get k shortest union.
	 */
//	@Test
//	public void testGetKShortestUnion() {
//		g.setEdgeWeight(ef,500.0);
//		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
//		noi.add(a);noi.add(c);noi.add(i);
//		KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new KShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
//
//		CompoundGraph subGraph = KShortestPath.getKShortestPathsUnion(g, noi, 2);
//		String list = "edges : ";
//		for(ReactionEdge e : subGraph.edgeSet()){
//			list+=(e.getV1().getId()+e.getV2().getId()+";");
//		}System.out.println(list);
//		list = "vertex : ";
//		for(BioPhysicalEntity v : subGraph.vertexSet()){
//			list+=(v.getId()+";");
//		}System.out.println(list);
//		assertEquals(6, subGraph.edgeSet().size());
//		assertEquals(6, subGraph.vertexSet().size());
//		g.setEdgeWeight(ef,1.0);
//	}
	
	/**
	 * Test the get shortest union list.
	 */
	@Test
	public void testGetShortestUnionList() {
		g.setEdgeWeight(ef,500.0);
		HashSet<ReactionEdge> expectedPath = new HashSet<ReactionEdge>();
		expectedPath.add(ic);expectedPath.add(ab);expectedPath.add(bc);
		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
		noi.add(a);noi.add(c);noi.add(i);
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		CompoundGraph g2 = new CompoundGraph();
		for(BioPath<BioPhysicalEntity, ReactionEdge> p : pathSearch.getShortestPathsUnionList(noi)){
//			System.out.println(p.toString());
			g2.addPath(p);
		}
		assertEquals("wrong path",expectedPath,(g2.edgeSet()));
	}
	
	/**
	 * Test the get shortest union.
	 */
//	@Test
//	public void testGetShortestUnion() {
//		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
//		noi.add(a);noi.add(c);noi.add(i);
//		CompoundGraph subGraph = ShortestPath.getShortestPathsUnion(g, noi);
//		for(ReactionEdge e : subGraph.edgeSet()){
//			System.out.println(e.getV1().getId()+" -> "+e.getV2().getId());
//		}
//		assertEquals("wrong graph size", 3, subGraph.edgeSet().size());
//		assertEquals("wrong graph order", 4, subGraph.vertexSet().size());
//	}
	
	/**
	 * Test the metric closure graph.
	 */
	@Test
	public void testMetricClosureGraph(){
		HashSet<BioPhysicalEntity> noi = new HashSet<BioPhysicalEntity>();
		noi.add(a);noi.add(b);noi.add(c);noi.add(d);noi.add(e);
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> pathSearch = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);

		CompressedGraph<BioPhysicalEntity, ReactionEdge, CompoundGraph> cg = pathSearch.getMetricClosureGraph(noi,noi,false);
		
		for(BioPhysicalEntity e1 : noi){
			for(BioPhysicalEntity e2 : noi){
				if(e1!=e2){
					BioPath<BioPhysicalEntity,ReactionEdge> path = pathSearch.getShortest(e1, e2);
					if(path!=null){
//						double weightSum=0;
//						for(ReactionEdge e : path){
//							weightSum+=g.getEdgeWeight(e);
//						}
						assertTrue("no link between two connected terminal node", cg.containsEdge(e1, e2));
						assertEquals("wrong edge weight in Metric closure graph",path.getLength() , cg.getEdgeWeight(cg.getEdge(e1, e2)), Double.MIN_VALUE);
						
						assertTrue("wrong path",path.getEdgeList().containsAll(cg.getEdge(e1, e2).getPath().getEdgeList()));

					}
				}
			}
		}
	}
	
	@Test
	public void testFloydWarshallDist(){
		
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> spComputor = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		
		FloydWarshall<BioPhysicalEntity,ReactionEdge,CompoundGraph> computor = new FloydWarshall<BioPhysicalEntity,ReactionEdge,CompoundGraph>(g);
		HashMap<String, HashMap<String, Double>> res = ExportMatrix.matrixToMap(computor.getDistances());
		for(String a : res.keySet()){
			for(String b : res.get(a).keySet()){
				if(a.equals(b)){
					assertEquals(0.0, res.get(a).get(b), Double.MIN_VALUE);
				}else{
					BioPath<BioPhysicalEntity, ReactionEdge> sp = spComputor.getShortest(g.getVertex(a), g.getVertex(b));
					double weight = (sp==null) ? Double.POSITIVE_INFINITY : sp.getWeight();
					assertEquals(weight, res.get(a).get(b), Double.MIN_VALUE);
				}
			}
		}
	}
	
	@Test
	public void testFloydWarshallPath(){
		
		ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph> spComputor = new ShortestPath<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		
		FloydWarshall<BioPhysicalEntity,ReactionEdge,CompoundGraph> computor = new FloydWarshall<BioPhysicalEntity,ReactionEdge,CompoundGraph>(g);
		HashMap<String, HashMap<String, BioPath<BioPhysicalEntity, ReactionEdge>>> res = computor.getPaths();
		for(String a : res.keySet()){
			for(String b : res.get(a).keySet()){
				if(!a.equals(b)){
					BioPath<BioPhysicalEntity, ReactionEdge> sp = spComputor.getShortest(g.getVertex(a), g.getVertex(b));
					if(sp==null){
						assertTrue(!res.get(a).containsKey(b));
					}else{
						assertEquals(sp.getWeight(), res.get(a).get(b).getWeight(), Double.MIN_VALUE);
					}
				}
			}
		}
	}
	
	@Test
	public void testNeighboorhoodCentrality(){
		GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph> measure =
				new GraphCentralityMeasure<BioPhysicalEntity, ReactionEdge, CompoundGraph>(g);
		Map<BioPhysicalEntity, Integer> nc = measure.getGeodesicNeighborhoodCentrality();
		assertEquals(6, nc.get(a).intValue());
		assertEquals(2, nc.get(b).intValue());
		assertEquals(0, nc.get(c).intValue());
		assertEquals(5, nc.get(d).intValue());
		assertEquals(4, nc.get(e).intValue());
		assertEquals(1, nc.get(f).intValue());
		assertEquals(0, nc.get(h).intValue());
		assertEquals(1, nc.get(i).intValue());
	}
	
}
