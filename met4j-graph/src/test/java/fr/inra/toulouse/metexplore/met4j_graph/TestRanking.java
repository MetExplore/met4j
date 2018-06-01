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

import java.util.HashMap;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.computation.analysis.RankUtils;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

/**
 * Test {@link RankUtils}
 * @author clement
 */
public class TestRanking {
	
	
	/**
	 * Test the kendall.
	 */
	@Test
	public void testKendall() {
		int[] rank1 = {1,2,3,4,5};
		int[] rank2 = {3,4,1,2,5};
		int[] rank3 = {5,4,3,2,1};
		double kendall = RankUtils.kendallTau(rank1, rank2);
		assertTrue("kendall coefficient should fit between 0 and 1", 0<=kendall && kendall<=1);
		assertEquals("wrong kendall coefficient", 0.4, kendall, Double.MIN_VALUE);
		assertEquals("same ranking should have kendall coeff = 0", 0.0, RankUtils.kendallTau(rank1, rank1), Double.MIN_VALUE);
		assertEquals("invert ranking should have kendall coeff = 1", 1.0, RankUtils.kendallTau(rank1, rank3), Double.MIN_VALUE);
	}
	
	/**
	 * Test the spearman.
	 */
	@Test
	public void testSpearman() {
		int[] rank1 ={1,2,3,4,5,6,7,8,9,10};
		int[] rank2 ={1,6,8,7,10,9,3,5,2,4};
		double spearman = RankUtils.SpearmanRankCoeff(rank1, rank2);
		assertEquals("wrong Spearman correlation coefficient", -0.175757575, spearman, 0.000000001);
	}
	
	/**
	 * Test the compute rank.
	 */
	@Test
	public void testComputeRank(){
		CompoundGraph g1 = new CompoundGraph();CompoundGraph g2 = new CompoundGraph();
		HashMap<String, Integer> expected1 = new HashMap<String, Integer>();
		HashMap<String, Integer> expected2 = new HashMap<String, Integer>();
		BioPhysicalEntity a = new BioPhysicalEntity("a");g1.addVertex(a);g2.addVertex(a);
		BioPhysicalEntity e = new BioPhysicalEntity("e");g1.addVertex(e);g2.addVertex(e);
		BioPhysicalEntity d = new BioPhysicalEntity("d");g1.addVertex(d);g2.addVertex(d);
		BioPhysicalEntity b = new BioPhysicalEntity("b");g1.addVertex(b);g2.addVertex(b);
		BioPhysicalEntity c = new BioPhysicalEntity("c");g1.addVertex(c);g2.addVertex(c);
		
		ReactionEdge ba = new ReactionEdge(b, a, new BioReaction("ba"));
		g1.addEdge(b, a, ba); g1.setEdgeScore(ba, 8.0); expected1.put("ba", 0);
		ReactionEdge ae = new ReactionEdge(a, e, new BioReaction("ae"));
		g1.addEdge(a, e, ae); g1.setEdgeScore(ae, 7.0); expected1.put("ae", 2);
		ReactionEdge ca = new ReactionEdge(c, a, new BioReaction("ca"));
		g1.addEdge(c, a, ca); g1.setEdgeScore(ca, 6.0); expected1.put("ca", 3);
		ReactionEdge cd = new ReactionEdge(c, d, new BioReaction("cd"));
		g1.addEdge(c, d, cd); g1.setEdgeScore(cd, 5.0); expected1.put("cd", 4);
		ReactionEdge db = new ReactionEdge(d, b, new BioReaction("db"));
		g1.addEdge(d, b, db); g1.setEdgeScore(db, 4.0); expected1.put("db", 5);
		ReactionEdge ec = new ReactionEdge(e, c, new BioReaction("ec"));
		g1.addEdge(e, c, ec); g1.setEdgeScore(ec, 3.0); expected1.put("ec", 6);
		ReactionEdge eb = new ReactionEdge(e, b, new BioReaction("eb"));
		g1.addEdge(e, b, eb); g1.setEdgeScore(eb, 2.0); expected1.put("eb", 7);
		BioReaction r = new BioReaction("dea");
		ReactionEdge de = new ReactionEdge(d, e, r);
		ReactionEdge ea = new ReactionEdge(e, a, r);
		g1.addEdge(d, e, de); g1.setEdgeScore(de, 3.7);
		g1.addEdge(e, a, ea); g1.setEdgeScore(ea, 3.8);expected1.put("dea", 1);
		
		HashMap<String, Integer> r1 = RankUtils.computeRank(g1);
		
		g2.addEdge(d, e, de); g2.setEdgeScore(de, 0.7);
		g2.addEdge(e, a, ea); g2.setEdgeScore(ea, 0.8);expected2.put("dea", 6);
		g2.addEdge(b, a, ba); g2.setEdgeScore(ba, 1.0); expected2.put("ba", 7);
		g2.addEdge(a, e, ae); g2.setEdgeScore(ae, 2.0); expected2.put("ae", 5);
		g2.addEdge(c, a, ca); g2.setEdgeScore(ca, 3.0); expected2.put("ca", 4);
		g2.addEdge(c, d, cd); g2.setEdgeScore(cd, 4.0); expected2.put("cd", 3);
		g2.addEdge(d, b, db); g2.setEdgeScore(db, 5.0); expected2.put("db", 2);
		g2.addEdge(e, c, ec); g2.setEdgeScore(ec, 6.0); expected2.put("ec", 1);
		g2.addEdge(e, b, eb); g2.setEdgeScore(eb, 7.0); expected2.put("eb", 0);

		HashMap<String, Integer> r2 = RankUtils.computeRank(g1);
		
		assertEquals("wrong rank size", 8, r1.size());
		assertEquals("wrong rank size", 8, r2.size());
		for(String rId : r1.keySet()){
			assertEquals("wrong rank", expected1.get(rId), r1.get(rId));
			assertEquals("wrong rank", expected2.get(rId), r2.get(rId));
		}
		
		assertEquals("wrong kendall coeff after array conversion", 1.0, RankUtils.kendallTau(r1, r2), Double.MIN_VALUE);

	}
}
