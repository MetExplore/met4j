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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPathUtils;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

/**
 * test class for {@link BioPathUtils} methods
 * @author clement
 *
 */
public class TestBioPathUtils {
	
	/** The graph. */
	public static CompoundGraph g;
	
	/** The nodes. */
	public static BioPhysicalEntity a,b,c,d,e,f,h;
	
	/** The edges. */
	public static ReactionEdge ab,bc,ad,de,ef,fc,bh,hc;
	
	/** The paths. */
	public static BioPath<BioPhysicalEntity, ReactionEdge> abc, abhc, adefc;
	
	/** The paths. */
	public static Collection<BioPath<BioPhysicalEntity, ReactionEdge>> testSet;
	
	
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
		
		ab = new ReactionEdge(a,b,new BioChemicalReaction("ab"));g.addEdge(a, b, ab);g.setEdgeWeight(ab, 0);
		bc = new ReactionEdge(b,c,new BioChemicalReaction("bc"));g.addEdge(b, c, bc);g.setEdgeWeight(bc, 100);
		ad = new ReactionEdge(a,d,new BioChemicalReaction("ad"));g.addEdge(a, d, ad);g.setEdgeWeight(ad, 0);
		de = new ReactionEdge(d,e,new BioChemicalReaction("de"));g.addEdge(d, e, de);g.setEdgeWeight(de, 0);
		ef = new ReactionEdge(e,f,new BioChemicalReaction("ef"));g.addEdge(e, f, ef);g.setEdgeWeight(ef, 0);
		fc = new ReactionEdge(f,c,new BioChemicalReaction("fc"));g.addEdge(f, c, fc);g.setEdgeWeight(fc, 1);
		bh = new ReactionEdge(b,h,new BioChemicalReaction("bh"));g.addEdge(b, h, bh);g.setEdgeWeight(bh, 0);
		hc = new ReactionEdge(h,c,new BioChemicalReaction("hc"));g.addEdge(h, c, hc);g.setEdgeWeight(hc, 10);
		
		List<ReactionEdge> abcList = new ArrayList<ReactionEdge>();
			abcList.add(ab);abcList.add(bc);
		List<ReactionEdge> abhcList = new ArrayList<ReactionEdge>();
			abhcList.add(ab);abhcList.add(bh);abhcList.add(hc);
		List<ReactionEdge> adefcList = new ArrayList<ReactionEdge>();
			adefcList.add(ad);adefcList.add(de);adefcList.add(ef);adefcList.add(fc);
			
		abc = new BioPath<BioPhysicalEntity, ReactionEdge>(g, a, c, abcList, 100);
		abhc = new BioPath<BioPhysicalEntity, ReactionEdge>(g, a, c, abhcList, 10);
		adefc = new BioPath<BioPhysicalEntity, ReactionEdge>(g, a, c, adefcList, 1);
		
		testSet = new HashSet<BioPath<BioPhysicalEntity, ReactionEdge>>();
		testSet.add(abc);
		testSet.add(abhc);
		testSet.add(adefc);
	}
	
	@Test
	public void getPathsContainingAllEdges() {
		Collection<ReactionEdge> edges = new HashSet<ReactionEdge>();
		edges.add(ab);
		edges.add(hc);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.getPathsContainingAllEdges(testSet, edges));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void getPathsContainingAllNodes() {
		Collection<BioPhysicalEntity> nodes = new HashSet<BioPhysicalEntity>();
		nodes.add(a);
		nodes.add(b);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		expectedSet.add(abhc);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.getPathsContainingAllNodes(testSet, nodes));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void getPathsContainingEdges() {
		Collection<ReactionEdge> edges = new HashSet<ReactionEdge>();
		edges.add(ab);
		edges.add(hc);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.getPathsContainingEdges(testSet, edges));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void getPathsContainingNodes() {
		Collection<BioPhysicalEntity> nodes = new HashSet<BioPhysicalEntity>();
		nodes.add(h);
		nodes.add(f);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(adefc);
		expectedSet.add(abhc);
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.getPathsContainingNodes(testSet, nodes));
		
		Collections.sort(expectedSet);
		Collections.sort(filteredSet);
		
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void lengthFilter() {
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet,filteredSet;
		
		double threshold = 3.0;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void lengthPercentileFilter() {
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet,filteredSet;
		
		double threshold = 50;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthPercentileFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void lengthRankFilter() {
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet,filteredSet;
		
		int threshold = 2;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.lengthRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void weightFilter() {
		
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet,filteredSet;
		
		double threshold = 10.0;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void weightPercentileFilter() {
	ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet,filteredSet;
			
			double threshold = 50.0;
			String operator;
			
			operator = BioPathUtils.EQUALITY;
			expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
			expectedSet.add(abhc);
			filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.GREATER;
			expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
			expectedSet.add(abc);
			filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.GREATEROREQUAL;
			expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
			expectedSet.add(abhc);
			expectedSet.add(abc);
			filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.INEQUALITY;
			expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
			expectedSet.add(abc);
			expectedSet.add(adefc);
			filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.LESS;
			expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
			expectedSet.add(adefc);
			filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
			
			operator = BioPathUtils.LESSOREQUAL;
			expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
			expectedSet.add(abhc);
			expectedSet.add(adefc);
			filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightPercentileFilter(testSet, threshold, operator));
			Collections.sort(expectedSet);Collections.sort(filteredSet);
			assertEquals(expectedSet, filteredSet);
	}
	
	@Test
	public void weightRankFilter() {
		ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>> expectedSet,filteredSet;
		
		int threshold = 2;
		String operator;
		
		operator = BioPathUtils.EQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATER;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.GREATEROREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(abc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.INEQUALITY;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESS;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
		
		operator = BioPathUtils.LESSOREQUAL;
		expectedSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>();
		expectedSet.add(abhc);
		expectedSet.add(adefc);
		filteredSet = new ArrayList<BioPath<BioPhysicalEntity, ReactionEdge>>(BioPathUtils.weightRankFilter(testSet, threshold, operator));
		Collections.sort(expectedSet);Collections.sort(filteredSet);
		assertEquals(expectedSet, filteredSet);
	}

}
