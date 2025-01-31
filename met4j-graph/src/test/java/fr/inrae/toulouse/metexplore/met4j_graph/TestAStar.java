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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.AStar;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.heuristic.ChemicalSimilarityHeuristic;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.SimilarityWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test {@link AStar} algorithm
 * @author clement
 */
public class TestAStar {
	
	public static CompoundGraph g;
	
	public static BioMetabolite a,b,c,d,e,f,x,y;
	
	public static ReactionEdge ab,bc,ad,de,ef,fc,bx,eb,yc;
	 

	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		a = new BioMetabolite("a"); a.setName("glucose"); a.setInchi("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
		g.addVertex(a);
		b = new BioMetabolite("b"); b.setName("atp"); b.setInchi("InChI=1S/C10H16N5O13P3/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(26-10)1-25-30(21,22)28-31(23,24)27-29(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H,23,24)(H2,11,12,13)(H2,18,19,20)/p-4/t4-,6-,7-,10-/m1/s1");
		g.addVertex(b);
		c = new BioMetabolite("c"); c.setName("Dihydroxyacetone phosphate"); c.setInchi("InChI=1S/C3H7O6P/c4-1-3(5)2-9-10(6,7)8/h1,3,5H,2H2,(H2,6,7,8)/p-2");
		g.addVertex(c);
		d = new BioMetabolite("d"); d.setName("glucose-6-P"); d.setInchi("InChI=1S/C6H13O9P/c7-3-2(1-14-16(11,12)13)15-6(10)5(9)4(3)8/h2-10H,1H2,(H2,11,12,13)/t2-,3-,4+,5-,6?/m1/s1");
		g.addVertex(d);
		e = new BioMetabolite("e"); e.setName("fructose-6-P"); e.setInchi("InChI=1S/C6H13O9P/c7-1-3(8)5(10)6(11)4(9)2-15-16(12,13)14/h4-7,9-11H,1-2H2,(H2,12,13,14)/p-2/t4-,5-,6-/m1/s1");
		g.addVertex(e);
		f = new BioMetabolite("f"); f.setName("fructose 1,6-bisphosphate"); f.setInchi("InChI=1S/C6H14O12P2/c7-3(1-17-19(11,12)13)5(9)6(10)4(8)2-18-20(14,15)16/h3,5-7,9-10H,1-2H2,(H2,11,12,13)(H2,14,15,16)/t3-,5-,6-/m1/s1");
		g.addVertex(f);
		x = new BioMetabolite("x"); x.setName("lipoate"); x.setInchi("InChI=1S/C8H14O2S2/c9-8(10)4-2-1-3-7-5-6-11-12-7/h7H,1-6H2,(H,9,10)");
		g.addVertex(x);
		y = new BioMetabolite("y"); y.setName("glycerol 3-phosphate"); y.setInchi("InChI=1S/C3H9O6P/c4-1-3(5)2-9-10(6,7)8/h3-5H,1-2H2,(H2,6,7,8)/p-2/t3-/m1/s1");
		g.addVertex(y);
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);
		bc = new ReactionEdge(b,c,new BioReaction("bc"));g.addEdge(b, c, bc);
		ad = new ReactionEdge(a,d,new BioReaction("ad"));g.addEdge(a, d, ad);
		de = new ReactionEdge(d,e,new BioReaction("de"));g.addEdge(d, e, de);
		ef = new ReactionEdge(e,f,new BioReaction("ef"));g.addEdge(e, f, ef);
		fc = new ReactionEdge(f,c,new BioReaction("fc"));g.addEdge(f, c, fc);
		bx = new ReactionEdge(b,x,new BioReaction("bx"));g.addEdge(b, x, bx);
		eb = new ReactionEdge(e,b,new BioReaction("eb"));g.addEdge(e, b, eb);
		yc = new ReactionEdge(y,c,new BioReaction("yc"));g.addEdge(y, c, yc);
		SimilarityWeightPolicy wp = new SimilarityWeightPolicy();
		wp.useDistance(true);
		wp.setWeight(g);
	}
	
	/**
	 * Test the get best path.
	 */
	@Test
	public void testGetBestPath() {
		
		ReactionEdge[] expectedPath = {ad,de,ef,fc};
		List<ReactionEdge> bestPath = (new AStar<>(g, new ChemicalSimilarityHeuristic())).findBestPath( g.getVertex("a"), g.getVertex("c"));
		
		assertNotNull("No path found", bestPath);
		assertTrue("wrong path",Arrays.asList(expectedPath).containsAll(bestPath));
	}
	
	@Test
	public void testGetBestPathUnion() {
		
		ReactionEdge[] expectedPath = {ad,de,ef,fc};
		HashSet<BioMetabolite> sourceSet = new HashSet<>();
		sourceSet.add(a);
		sourceSet.add(c);
		List<ReactionEdge> bestPath = (new AStar<>(g, new ChemicalSimilarityHeuristic())).getBestPathUnionList(g, sourceSet);
		
		assertNotNull("No path found", bestPath);
		assertTrue("wrong path",Arrays.asList(expectedPath).containsAll(bestPath));
		
		HashSet<BioMetabolite> targetSet = new HashSet<>();
		sourceSet.remove(c);
		targetSet.add(c);
		
		bestPath = (new AStar<>(g, new ChemicalSimilarityHeuristic())).getBestPathUnionList(g, sourceSet, targetSet);
		
		assertNotNull("No path found", bestPath);
		assertTrue("wrong path",Arrays.asList(expectedPath).containsAll(bestPath));
		
		
	}
	
}
