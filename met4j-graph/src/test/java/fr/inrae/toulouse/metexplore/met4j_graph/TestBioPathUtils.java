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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPathUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

/**
 * test class for {@link BioPathUtils} methods
 * @author clement
 *
 */
public class TestBioPathUtils {
	
	/** The graph. */
	public static CompoundGraph g;
	
	/** The nodes. */
	public static BioMetabolite a,b,c,d,e,f,h;
	
	/** The edges. */
	public static ReactionEdge ab,bc,ad,de,ef,fc,bh,hc;
	
	/** The paths. */
	public static BioPath<BioMetabolite, ReactionEdge> abc, abhc, adefc;
	
	/** The paths. */
	public static Collection<BioPath<BioMetabolite, ReactionEdge>> testSet;
	
	
	@BeforeClass
	public static void init(){
		
		g = new CompoundGraph();
		a = new BioMetabolite("a"); g.addVertex(a);
		b = new BioMetabolite("b"); g.addVertex(b);
		c = new BioMetabolite("c"); g.addVertex(c);
		d = new BioMetabolite("d"); g.addVertex(d);
		e = new BioMetabolite("e"); g.addVertex(e);
		f = new BioMetabolite("f"); g.addVertex(f);
		h = new BioMetabolite("h"); g.addVertex(h);
		
		ab = new ReactionEdge(a,b,new BioReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 0);
		bc = new ReactionEdge(b,c,new BioReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 100);
		ad = new ReactionEdge(a,d,new BioReaction("ad"));g.addEdge(a, d, ad);g.setEdgeWeight(ad, 0);
		de = new ReactionEdge(d,e,new BioReaction("de"));g.addEdge(d, e, de);g.setEdgeWeight(de, 0);
		ef = new ReactionEdge(e,f,new BioReaction("ef"));g.addEdge(e, f, ef);g.setEdgeWeight(ef, 0);
		fc = new ReactionEdge(f,c,new BioReaction("fc"));g.addEdge(f, c, fc);g.setEdgeWeight(fc, 1);
		bh = new ReactionEdge(b,h,new BioReaction("bh"));g.addEdge(b, h, bh);g.setEdgeWeight(bh, 0);
		hc = new ReactionEdge(h,c,new BioReaction("hc"));g.addEdge(h, c, hc);g.setEdgeWeight(hc, 10);
		
		List<ReactionEdge> abcList = new ArrayList<>();
			abcList.add(ab);abcList.add(bc);
		List<ReactionEdge> abhcList = new ArrayList<>();
			abhcList.add(ab);abhcList.add(bh);abhcList.add(hc);
		List<ReactionEdge> adefcList = new ArrayList<>();
			adefcList.add(ad);adefcList.add(de);adefcList.add(ef);adefcList.add(fc);
			
		abc = new BioPath<>(g, a, c, abcList, 100);
		abhc = new BioPath<>(g, a, c, abhcList, 10);
		adefc = new BioPath<>(g, a, c, adefcList, 1);
		
		testSet = new HashSet<>();
		testSet.add(abc);
		testSet.add(abhc);
		testSet.add(adefc);
	}
	
	@Test
	public void getPathsContainingAllEdges() {
		Collection<ReactionEdge> edges = new HashSet<>();
		edges.add(ab);
		edges.add(hc);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> filteredSet = new ArrayList<>(BioPathUtils.getPathsContainingAllEdges(testSet, edges));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void getPathsContainingAllNodes() {
		Collection<BioMetabolite> nodes = new HashSet<>();
		nodes.add(a);
		nodes.add(b);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		expectedSet.add(abhc);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> filteredSet = new ArrayList<>(BioPathUtils.getPathsContainingAllNodes(testSet, nodes));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void getPathsContainingEdges() {
		Collection<ReactionEdge> edges = new HashSet<>();
		edges.add(ab);
		edges.add(hc);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> filteredSet = new ArrayList<>(BioPathUtils.getPathsContainingEdges(testSet, edges));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void getPathsContainingNodes() {
		Collection<BioMetabolite> nodes = new HashSet<>();
		nodes.add(h);
		nodes.add(f);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet = new ArrayList<>();
		expectedSet.add(adefc);
		expectedSet.add(abhc);
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> filteredSet = new ArrayList<>(BioPathUtils.getPathsContainingNodes(testSet, nodes));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void lengthFilter() {
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet,filteredSet;
		
		double threshold = 3.0;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void lengthPercentileFilter() {
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet,filteredSet;
		
		double threshold = 50;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void lengthRankFilter() {
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet,filteredSet;
		
		int threshold = 2;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void weightFilter() {
		
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet,filteredSet;
		
		double threshold = 10.0;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void weightPercentileFilter() {
	ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet,filteredSet;
			
			double threshold = 50.0;
			String operator;
			
			operator = BioPathUtils.EQUALITY;
			expectedSet = new ArrayList<>();
			expectedSet.add(abhc);
			filteredSet = new ArrayList<>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.GREATER;
			expectedSet = new ArrayList<>();
			expectedSet.add(abc);
			filteredSet = new ArrayList<>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.GREATEROREQUAL;
			expectedSet = new ArrayList<>();
			expectedSet.add(abhc);
			expectedSet.add(abc);
			filteredSet = new ArrayList<>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.INEQUALITY;
			expectedSet = new ArrayList<>();
			expectedSet.add(abc);
			expectedSet.add(adefc);
			filteredSet = new ArrayList<>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.LESS;
			expectedSet = new ArrayList<>();
			expectedSet.add(adefc);
			filteredSet = new ArrayList<>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.LESSOREQUAL;
			expectedSet = new ArrayList<>();
			expectedSet.add(abhc);
			expectedSet.add(adefc);
			filteredSet = new ArrayList<>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void weightRankFilter() {
		ArrayList<BioPath<BioMetabolite, ReactionEdge>> expectedSet,filteredSet;
		
		int threshold = 2;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}

}
