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

import java.util.HashMap;
import java.util.HashSet;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.centrality.EigenVectorCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;


/**
 * Test {@link EigenVectorCentrality}
 * @author clement
 */
public class TestEigenVectorCentrality {

	public static CompoundGraph graph;

	@BeforeClass
	public static void init(){
//		{1,1,0,0,1,0,0},
//		{1,1,0,0,1,0,0},
//		{0,0,1,1,1,0,0},
//		{0,0,1,1,1,0,0},
//		{1,1,1,1,1,1,1},
//		{0,0,0,0,1,1,1},
//		{0,0,0,0,1,1,1}
		graph = new CompoundGraph();
		BioMetabolite a = new BioMetabolite("a");graph.addVertex(a);
		BioMetabolite b = new BioMetabolite("b");graph.addVertex(b);
		BioMetabolite c = new BioMetabolite("c");graph.addVertex(c);
		BioMetabolite d = new BioMetabolite("d");graph.addVertex(d);
		BioMetabolite e = new BioMetabolite("e");graph.addVertex(e);
		BioMetabolite f = new BioMetabolite("f");graph.addVertex(f);
		BioMetabolite g = new BioMetabolite("g");graph.addVertex(g);
//		ReactionEdge aa = new ReactionEdge(a, a, new BioReaction("aa")); graph.addEdge(a, a, aa);
		ReactionEdge ab = new ReactionEdge(a, b, new BioReaction("ab")); graph.addEdge(a, b, ab);
		ReactionEdge ae = new ReactionEdge(a, e, new BioReaction("ae")); graph.addEdge(a, e, ae);
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction("ba")); graph.addEdge(b, a, ba);
//		ReactionEdge bb = new ReactionEdge(b, b, new BioReaction("bb")); graph.addEdge(b, b, bb);
		ReactionEdge be = new ReactionEdge(b, e, new BioReaction("be")); graph.addEdge(b, e, be);
//		ReactionEdge cc = new ReactionEdge(c, c, new BioReaction("cc")); graph.addEdge(c, c, cc);
		ReactionEdge cd = new ReactionEdge(c, d, new BioReaction("cd")); graph.addEdge(c, d, cd);
		ReactionEdge ce = new ReactionEdge(c, e, new BioReaction("ce")); graph.addEdge(c, e, ce);
		ReactionEdge dc = new ReactionEdge(d, c, new BioReaction("dc")); graph.addEdge(d, c, dc);
//		ReactionEdge dd = new ReactionEdge(d, d, new BioReaction("dd")); graph.addEdge(d, d, dd);
		ReactionEdge de = new ReactionEdge(d, e, new BioReaction("de")); graph.addEdge(d, e, de);
		ReactionEdge ea = new ReactionEdge(e, a, new BioReaction("ea")); graph.addEdge(e, a, ea);
		ReactionEdge eb = new ReactionEdge(e, b, new BioReaction("eb")); graph.addEdge(e, b, eb);
		ReactionEdge ec = new ReactionEdge(e, c, new BioReaction("ec")); graph.addEdge(e, c, ec);
		ReactionEdge ed = new ReactionEdge(e, d, new BioReaction("ed")); graph.addEdge(e, d, ed);
//		ReactionEdge ee = new ReactionEdge(e, e, new BioReaction("ee")); graph.addEdge(e, e, ee);
		ReactionEdge ef = new ReactionEdge(e, f, new BioReaction("ef")); graph.addEdge(e, f, ef);
		ReactionEdge eg = new ReactionEdge(e, g, new BioReaction("eg")); graph.addEdge(e, g, eg);
		ReactionEdge fe = new ReactionEdge(f, e, new BioReaction("fe")); graph.addEdge(f, e, fe);
//		ReactionEdge ff = new ReactionEdge(f, f, new BioReaction("ff")); graph.addEdge(f, f, ff);
		ReactionEdge fg = new ReactionEdge(f, g, new BioReaction("fg")); graph.addEdge(f, g, fg);
		ReactionEdge ge = new ReactionEdge(g, e, new BioReaction("ge")); graph.addEdge(g, e, ge);
		ReactionEdge gf = new ReactionEdge(g, f, new BioReaction("gf")); graph.addEdge(g, f, gf);	
//		ReactionEdge gg = new ReactionEdge(g, g, new BioReaction("gg")); graph.addEdge(g, g, gg);
	}
	
	/**
	 * Test the golbal eigen vector centrality.
	 */
	@Test
	public void testGolbalEigenVectorCentrality() {
		EigenVectorCentrality<BioMetabolite,ReactionEdge,CompoundGraph> pg = new EigenVectorCentrality<>(graph);
		for(int i=0; i<pg.adjacencyMatrix.numRows(); i++){
			pg.adjacencyMatrix.set(i, i, 1.0);
		}
		HashMap<String, Double> res = pg.computeEigenVectorCentrality();
		double[] centrality = new double[graph.vertexSet().size()];
		for(int i2=0; i2<centrality.length; i2++){
			centrality[i2] = res.get(pg.adjacencyMatrix.getRowIndexMap().get(i2));
		}
		
		double[] expectedEC = {0.12500000000000006, 0.12499999999999988, 0.12500000000000003, 0.12500000000000003, 0.25, 0.12500000000000003, 0.12500000000000003};
		assertArrayEquals(expectedEC, centrality, 0.00000000000001);
	}
	
	/**
	 * Test the page rank with prior.
	 */
	@Test
	public void testPageRankWithPrior() {
		CompoundGraph graph = new CompoundGraph();
		BioMetabolite a = new BioMetabolite("a");graph.addVertex(a);
		BioMetabolite e = new BioMetabolite("e");graph.addVertex(e);
		BioMetabolite d = new BioMetabolite("d");graph.addVertex(d);
		BioMetabolite b = new BioMetabolite("b");graph.addVertex(b);
		BioMetabolite c = new BioMetabolite("c");graph.addVertex(c);
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction("ba")); graph.addEdge(b, a, ba); graph.setEdgeWeight(ba, 1.0);
		ReactionEdge ae = new ReactionEdge(a, e, new BioReaction("ae")); graph.addEdge(a, e, ae); graph.setEdgeWeight(ae, 1.0);
		ReactionEdge ca = new ReactionEdge(c, a, new BioReaction("ca")); graph.addEdge(c, a, ca); graph.setEdgeWeight(ca, 0.5);
		ReactionEdge cd = new ReactionEdge(c, d, new BioReaction("cd")); graph.addEdge(c, d, cd); graph.setEdgeWeight(cd, 0.5);
		ReactionEdge db = new ReactionEdge(d, b, new BioReaction("db")); graph.addEdge(d, b, db); graph.setEdgeWeight(db, 1.0);
		ReactionEdge ec = new ReactionEdge(e, c, new BioReaction("ec")); graph.addEdge(e, c, ec); graph.setEdgeWeight(ec, 0.2);
		ReactionEdge eb = new ReactionEdge(e, b, new BioReaction("eb")); graph.addEdge(e, b, eb); graph.setEdgeWeight(eb, 0.4);
		ReactionEdge ed = new ReactionEdge(e, d, new BioReaction("ed")); graph.addEdge(e, d, ed); graph.setEdgeWeight(ed, 0.4);
		EigenVectorCentrality<BioMetabolite,ReactionEdge,CompoundGraph> pg = new EigenVectorCentrality<>(graph);
		HashMap<String, Double> seeds = new HashMap<>();
//		double p = 1.0/graph.vertexSet().size();
		for(BioMetabolite entity: graph.vertexSet()){
			if(entity==a){
				seeds.put(entity.getId(), 1.0);
			}else{
				seeds.put(entity.getId(), 0.0);
			}
//			seeds.put(entity.getId(), p);
		}
		pg.adjacencyMatrix.print();
//		pg.addScalingFactor(0.9);
		pg.addJumpProb(seeds.keySet(), 0.1);
		pg.adjacencyMatrix.print();
		HashMap<String, Double> result = pg.powerIteration(seeds, 20, 0.001);
		
		double[] expectedEC = {0.27,0.26,0.15,0.25,0.07};
		double[] observedEC = new double[result.keySet().size()];
		
		for(int i : pg.adjacencyMatrix.getRowIndexMap().keySet()){
			observedEC[i] = result.get(pg.adjacencyMatrix.getRowIndexMap().get(i));
		}
		assertArrayEquals(expectedEC, observedEC, 0.01);
	}

	
	@Test
	public void testPageRankWithPrior2() {
		
		//test case from : https://sites.google.com/site/nirajatweb/home/technical_and_coding_stuff/page-rank-with-prior
		//see : 1.        White, S., Smyth, P., (2003). Algorithms for estimating relative importance in networks. KDD 2003: 266-275.	
		
		CompoundGraph graph = new CompoundGraph();
		BioMetabolite a = new BioMetabolite("a");graph.addVertex(a);
		BioMetabolite b = new BioMetabolite("b");graph.addVertex(b);
		BioMetabolite c = new BioMetabolite("c");graph.addVertex(c);
		BioMetabolite d = new BioMetabolite("d");graph.addVertex(d);
		BioMetabolite e = new BioMetabolite("e");graph.addVertex(e);
		BioMetabolite f = new BioMetabolite("f");graph.addVertex(f);
		BioMetabolite g = new BioMetabolite("g");graph.addVertex(g);
		BioMetabolite h = new BioMetabolite("h");graph.addVertex(h);
		BioMetabolite i = new BioMetabolite("i");graph.addVertex(i);
		BioMetabolite j = new BioMetabolite("j");graph.addVertex(j);
		ReactionEdge ab = new ReactionEdge(a, b, new BioReaction("ab")); graph.addEdge(a, b, ab); graph.setEdgeWeight(ab, 1.0/3.0);
		ReactionEdge bc = new ReactionEdge(b, c, new BioReaction("bc")); graph.addEdge(b, c, bc); graph.setEdgeWeight(bc, 1.0/3.0);
		ReactionEdge ca = new ReactionEdge(c, a, new BioReaction("ca")); graph.addEdge(c, a, ca); graph.setEdgeWeight(ca, 1.0/3.0);
		ReactionEdge hi = new ReactionEdge(h, i, new BioReaction("hi")); graph.addEdge(h, i, hi); graph.setEdgeWeight(hi, 1.0/3.0);
		ReactionEdge ig = new ReactionEdge(i, g, new BioReaction("ig")); graph.addEdge(i, g, ig); graph.setEdgeWeight(ig, 1.0/3.0);
		ReactionEdge gh = new ReactionEdge(g, h, new BioReaction("gh")); graph.addEdge(g, h, gh); graph.setEdgeWeight(gh, 1.0/3.0);
		ReactionEdge de = new ReactionEdge(d, e, new BioReaction("de")); graph.addEdge(d, e, de); graph.setEdgeWeight(de, 1.0/3.0);
		ReactionEdge ef = new ReactionEdge(e, f, new BioReaction("ef")); graph.addEdge(e, f, ef); graph.setEdgeWeight(ef, 1.0/3.0);
		ReactionEdge fd = new ReactionEdge(f, d, new BioReaction("fd")); graph.addEdge(f, d, fd); graph.setEdgeWeight(fd, 1.0/3.0);
		ReactionEdge ej = new ReactionEdge(e, j, new BioReaction("ej")); graph.addEdge(e, j, ej); graph.setEdgeWeight(ej, 1.0/3.0);
		ReactionEdge cj = new ReactionEdge(c, j, new BioReaction("cj")); graph.addEdge(c, j, cj); graph.setEdgeWeight(cj, 1.0/3.0);
		ReactionEdge hj = new ReactionEdge(h, j, new BioReaction("hj")); graph.addEdge(h, j, hj); graph.setEdgeWeight(hj, 1.0/3.0);
		ReactionEdge ad = new ReactionEdge(a, d, new BioReaction("ad")); graph.addEdge(a, d, ad); graph.setEdgeWeight(ad, 1.0/3.0);	
		ReactionEdge fg = new ReactionEdge(f, g, new BioReaction("fg")); graph.addEdge(f, g, fg); graph.setEdgeWeight(fg, 1.0/3.0);
		ReactionEdge bi = new ReactionEdge(b, i, new BioReaction("bi")); graph.addEdge(b, i, bi); graph.setEdgeWeight(bi, 1.0/3.0);
		
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction("ba")); graph.addEdge(b, a, ba); graph.setEdgeWeight(ba, 1.0/3.0);
		ReactionEdge cb = new ReactionEdge(c, b, new BioReaction("cb")); graph.addEdge(c, b, cb); graph.setEdgeWeight(cb, 1.0/3.0);
		ReactionEdge ac = new ReactionEdge(a, c, new BioReaction("ac")); graph.addEdge(a, c, ac); graph.setEdgeWeight(ac, 1.0/3.0);
		ReactionEdge ih = new ReactionEdge(i, h, new BioReaction("ih")); graph.addEdge(i, h, ih); graph.setEdgeWeight(ih, 1.0/3.0);
		ReactionEdge gi = new ReactionEdge(g, i, new BioReaction("gi")); graph.addEdge(g, i, gi); graph.setEdgeWeight(gi, 1.0/3.0);
		ReactionEdge hg = new ReactionEdge(h, g, new BioReaction("hg")); graph.addEdge(h, g, hg); graph.setEdgeWeight(hg, 1.0/3.0);
		ReactionEdge ed = new ReactionEdge(e, d, new BioReaction("ed")); graph.addEdge(e, d, ed); graph.setEdgeWeight(ed, 1.0/3.0);
		ReactionEdge fe = new ReactionEdge(f, e, new BioReaction("fe")); graph.addEdge(f, e, fe); graph.setEdgeWeight(fe, 1.0/3.0);
		ReactionEdge df = new ReactionEdge(d, f, new BioReaction("df")); graph.addEdge(d, f, df); graph.setEdgeWeight(df, 1.0/3.0);
		ReactionEdge je = new ReactionEdge(j, e, new BioReaction("je")); graph.addEdge(j, e, je); graph.setEdgeWeight(je, 1.0/3.0);
		ReactionEdge jc = new ReactionEdge(j, c, new BioReaction("jc")); graph.addEdge(j, c, jc); graph.setEdgeWeight(jc, 1.0/3.0);
		ReactionEdge jh = new ReactionEdge(j, h, new BioReaction("jh")); graph.addEdge(j, h, jh); graph.setEdgeWeight(jh, 1.0/3.0);
		ReactionEdge da = new ReactionEdge(d, a, new BioReaction("da")); graph.addEdge(d, a, da); graph.setEdgeWeight(da, 1.0/3.0);
		ReactionEdge gf = new ReactionEdge(g, f, new BioReaction("gf")); graph.addEdge(g, f, gf); graph.setEdgeWeight(gf, 1.0/3.0);
		ReactionEdge ib = new ReactionEdge(i, b, new BioReaction("ib")); graph.addEdge(i, b, ib); graph.setEdgeWeight(ib, 1.0/3.0);

		EigenVectorCentrality<BioMetabolite,ReactionEdge,CompoundGraph> pg = new EigenVectorCentrality<>(graph);
		HashMap<String, Double> seeds = new HashMap<>();
		double p = 1.0/graph.vertexSet().size();
		
		HashSet<String>  roots = new HashSet<>();
		roots.add(a.getId()); roots.add(f.getId());
		for(BioMetabolite entity: graph.vertexSet()){
//			if(roots.contains(entity.getId())){
//				seeds.put(entity.getId(), 0.5);
//			}else{
//				seeds.put(entity.getId(), 0.0);
//			}
			seeds.put(entity.getId(), p);
		}
		pg.adjacencyMatrix.print();
//		pg.addScalingFactor(0.9);
		pg.addJumpProb(roots, 0.7);
		pg.adjacencyMatrix.print();
		HashMap<String, Double> result = pg.powerIteration(seeds, 30, 0.001);
		
		double[] expectedEC = {0.36610862668654204,
				0.04163322248977564,
				0.0417001108435577,
				0.0777529335320873,
				0.045312081947788865,
				0.36610862668654204,
				0.03802125138554447,
				0.0055803998012460354,
				0.008523487367656617,
				0.00925925925925926};

		double[] observedEC = new double[result.keySet().size()];
		
		for(int k : pg.adjacencyMatrix.getRowIndexMap().keySet()){
			observedEC[k] = result.get(pg.adjacencyMatrix.getRowIndexMap().get(k));
		}
		assertArrayEquals(expectedEC, observedEC, 0.001);
	}
	
	/**
	 * Test the page rank with prior.
	 */
	@Test
	public void testPageRankWithPrior3() {
		CompoundGraph graph = new CompoundGraph();
		BioMetabolite a = new BioMetabolite("a");graph.addVertex(a);
		BioMetabolite e = new BioMetabolite("e");graph.addVertex(e);
		BioMetabolite d = new BioMetabolite("d");graph.addVertex(d);
		BioMetabolite b = new BioMetabolite("b");graph.addVertex(b);
		BioMetabolite c = new BioMetabolite("c");graph.addVertex(c);
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction("ba")); graph.addEdge(b, a, ba); graph.setEdgeWeight(ba, 1.0);
		ReactionEdge ae = new ReactionEdge(a, e, new BioReaction("ae")); graph.addEdge(a, e, ae); graph.setEdgeWeight(ae, 1.0);
		ReactionEdge ca = new ReactionEdge(c, a, new BioReaction("ca")); graph.addEdge(c, a, ca); graph.setEdgeWeight(ca, 0.5);
		ReactionEdge cd = new ReactionEdge(c, d, new BioReaction("cd")); graph.addEdge(c, d, cd); graph.setEdgeWeight(cd, 0.5);
		ReactionEdge db = new ReactionEdge(d, b, new BioReaction("db")); graph.addEdge(d, b, db); graph.setEdgeWeight(db, 1.0);
		ReactionEdge ec = new ReactionEdge(e, c, new BioReaction("ec")); graph.addEdge(e, c, ec); graph.setEdgeWeight(ec, 0.2);
		ReactionEdge eb = new ReactionEdge(e, b, new BioReaction("eb")); graph.addEdge(e, b, eb); graph.setEdgeWeight(eb, 0.4);
		ReactionEdge ed = new ReactionEdge(e, d, new BioReaction("ed")); graph.addEdge(e, d, ed); graph.setEdgeWeight(ed, 0.4);
		
		BioMatrix adj = new ComputeAdjacencyMatrix<>(graph).getadjacencyMatrix();
		
		EigenVectorCentrality<BioMetabolite, ReactionEdge, CompoundGraph> pg;
		try {
			pg = new EigenVectorCentrality<>(adj);
			HashMap<String, Double> seeds = new HashMap<>();
			HashMap<String, Double> weights = new HashMap<>();
//			double p = 1.0/graph.vertexSet().size();
			for(BioMetabolite entity: graph.vertexSet()){
				if(entity==a){
					seeds.put(entity.getId(), 1.0);
				}else{
					seeds.put(entity.getId(), 0.0);
				}
				weights.put(entity.getId(), 1.0/graph.vertexSet().size());
//				seeds.put(entity.getId(), p);
			}
			pg.adjacencyMatrix.print();
//			pg.addScalingFactor(0.9);
			pg.addJumpProb(weights, 0.1);
			pg.adjacencyMatrix.print();
			HashMap<String, Double> result = pg.powerIteration(seeds, 20, 0.001);
			
			double[] expectedEC = {0.27,0.26,0.15,0.25,0.07};
			double[] observedEC = new double[result.keySet().size()];
			
			for(int i : pg.adjacencyMatrix.getRowIndexMap().keySet()){
				observedEC[i] = result.get(pg.adjacencyMatrix.getRowIndexMap().get(i));
			}
			assertArrayEquals(expectedEC, observedEC, 0.01);
		
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
}
