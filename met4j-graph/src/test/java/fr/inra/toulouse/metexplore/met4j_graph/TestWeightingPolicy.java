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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.DefaultWeightPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.DegreeWeightPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.ProbabilityWeightPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.RPairsTagPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.RPairsWeightPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.ReactionProbabilityWeight;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.SimilarityWeightPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.StochasticWeightPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.WeightUtils;
import fr.inra.toulouse.metexplore.met4j_graph.computation.weighting.WeightsFromFile;

/**
 * Test {@link Bionetwork2CompoundGraph} with {@link WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph>}
 * @author clement
 */
public class TestWeightingPolicy {
	
	public static CompoundGraph g;
	
	public static BioPhysicalEntity a,b,c,d,e,f,x,y;
	
	public static ReactionEdge ab,bc,ad,de,ef,fc,bx,eb,yc;
	 
	@BeforeClass
	public static void init(){
		g = new CompoundGraph();
		a = new BioPhysicalEntity("a"); a.setName("glucose"); a.setInchi("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
		g.addVertex(a);
		b = new BioPhysicalEntity("b"); b.setName("atp"); b.setInchi("InChI=1S/C10H16N5O13P3/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(26-10)1-25-30(21,22)28-31(23,24)27-29(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H,23,24)(H2,11,12,13)(H2,18,19,20)/p-4/t4-,6-,7-,10-/m1/s1");
		g.addVertex(b);
		c = new BioPhysicalEntity("c"); c.setName("Dihydroxyacetone phosphate"); c.setInchi("InChI=1S/C3H7O6P/c4-1-3(5)2-9-10(6,7)8/h1,3,5H,2H2,(H2,6,7,8)/p-2");
		g.addVertex(c);
		d = new BioPhysicalEntity("d"); d.setName("glucose-6-P"); d.setInchi("InChI=1S/C6H13O9P/c7-3-2(1-14-16(11,12)13)15-6(10)5(9)4(3)8/h2-10H,1H2,(H2,11,12,13)/t2-,3-,4+,5-,6?/m1/s1");
		g.addVertex(d);
		e = new BioPhysicalEntity("e"); e.setName("fructose-6-P"); e.setInchi("InChI=1S/C6H13O9P/c7-1-3(8)5(10)6(11)4(9)2-15-16(12,13)14/h4-7,9-11H,1-2H2,(H2,12,13,14)/p-2/t4-,5-,6-/m1/s1");
		g.addVertex(e);
		f = new BioPhysicalEntity("f"); f.setName("fructose 1,6-bisphosphate"); f.setInchi("InChI=1S/C6H14O12P2/c7-3(1-17-19(11,12)13)5(9)6(10)4(8)2-18-20(14,15)16/h3,5-7,9-10H,1-2H2,(H2,11,12,13)(H2,14,15,16)/t3-,5-,6-/m1/s1");
		g.addVertex(f);
		x = new BioPhysicalEntity("x"); x.setName("lipoate"); x.setInchi("InChI=1S/C8H14O2S2/c9-8(10)4-2-1-3-7-5-6-11-12-7/h7H,1-6H2,(H,9,10)");
		g.addVertex(x);
		y = new BioPhysicalEntity("y"); y.setName("glycerol 3-phosphate"); y.setInchi("InChI=1S/C3H9O6P/c4-1-3(5)2-9-10(6,7)8/h3-5H,1-2H2,(H2,6,7,8)/p-2/t3-/m1/s1");
		g.addVertex(y);
		BioChemicalReaction abd = new BioChemicalReaction("abd");
		BioChemicalReaction efb = new BioChemicalReaction("efb");
		BioChemicalReaction bxc = new BioChemicalReaction("bxc");
		BioChemicalReaction fyc = new BioChemicalReaction("fyc");
		ab = new ReactionEdge(a,b,abd);g.addEdge(a, b, ab);
		bc = new ReactionEdge(b,c,bxc);g.addEdge(b, c, bc);
		ad = new ReactionEdge(a,d,abd);g.addEdge(a, d, ad);
		de = new ReactionEdge(d,e,new BioChemicalReaction("de"));g.addEdge(d, e, de);
		ef = new ReactionEdge(e,f,efb);g.addEdge(e, f, ef);
		fc = new ReactionEdge(f,c,new BioChemicalReaction("fc"));g.addEdge(f, c, fc);
		bx = new ReactionEdge(b,x,bxc);g.addEdge(b, x, bx);
		eb = new ReactionEdge(e,b,efb);g.addEdge(e, b, eb);
		yc = new ReactionEdge(y,c,fyc);g.addEdge(y, c, yc);
	}
	
	/**
	 * Reset weight.
	 */
	@After
	public void resetWeight(){
		for(ReactionEdge e : g.edgeSet()){
			g.setEdgeWeight(e, 0.0);
		}
	}
	
	/**
	 * Test the default weight policy.
	 */
	@Test
	public void testDefaultWeightPolicy() {
		WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new DefaultWeightPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph>();
		wp.setWeight(g);
		double defautValue = 1.0;
		for(ReactionEdge e : g.edgeSet()){
			assertEquals("wrong weight with default weighting policy", defautValue, g.getEdgeWeight(e),Double.MIN_VALUE);
		}
	}
	
	/**
	 * Test the probability weight policy.
	 */
	@Test
	public void testProbabilityWeightPolicy() {
		WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new ProbabilityWeightPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph>();
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		abWeight=bcWeight=adWeight=efWeight=bxWeight=ebWeight = 0.5;
		deWeight=fcWeight=ycWeight = 1.0;
		wp.setWeight(g);
		assertEquals("wrong weight with probability weighting policy", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);
	}
	
	/**
	 * Test the degree weight policy.
	 */
	@Test
	public void testDegreeWeightPolicy() {
		WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new DegreeWeightPolicy(2);
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		abWeight=ebWeight=16;
		bcWeight=fcWeight=ycWeight=deWeight=9;
		adWeight=efWeight=4;
		bxWeight=1;
		wp.setWeight(g);
		assertEquals("wrong weight with probability weighting policy", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight with probability weighting policy", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);
	}
	
	/**
	 * Test the similarity weight policy.
	 */
	@Test
	public void testSimilarityWeightPolicy() {
		WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new SimilarityWeightPolicy();
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		wp.setWeight(g);
//		System.out.println("abWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(ab.getV1()),FingerprintBuilder.getExtendedFingerprint(ab.getV2())));
//		System.out.println("bcWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(bc.getV1()),FingerprintBuilder.getExtendedFingerprint(bc.getV2())));
//		System.out.println("adWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(ad.getV1()),FingerprintBuilder.getExtendedFingerprint(ad.getV2())));
//		System.out.println("efWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(ef.getV1()),FingerprintBuilder.getExtendedFingerprint(ef.getV2())));
//		System.out.println("bxWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(bx.getV1()),FingerprintBuilder.getExtendedFingerprint(bx.getV2())));
//		System.out.println("ebWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(eb.getV1()),FingerprintBuilder.getExtendedFingerprint(eb.getV2())));
//		System.out.println("deWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(de.getV1()),FingerprintBuilder.getExtendedFingerprint(de.getV2())));
//		System.out.println("fcWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(fc.getV1()),FingerprintBuilder.getExtendedFingerprint(fc.getV2())));
//		System.out.println("ycWeight="+SimilarityComputor.getTanimoto(FingerprintBuilder.getExtendedFingerprint(yc.getV1()),FingerprintBuilder.getExtendedFingerprint(yc.getV2())));
		abWeight=0.15566037735849056;
		bcWeight=0.0661764705882353;
		adWeight=0.5751633986928104;
		efWeight=0.9347826086956522;
		bxWeight=0.07192575406032482;
		ebWeight=0.18159806295399517;
		deWeight=0.4397590361445783;
		fcWeight=0.3157894736842105;
		ycWeight=0.5555555555555556;
		assertEquals("wrong weight with similarity weighting policy", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight with similarity weighting policy", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);

	}
	
	/**
	 * Test the Stochastic weight policy.
	 */
	@Test
	public void testStochasticWeightPolicy() {
//		WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new ProbabilityWeightPolicy(new SimilarityWeightPolicy());
		WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new StochasticWeightPolicy();
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		wp.setWeight(g);
		abWeight=0.15566037735849056/(0.15566037735849056+0.5751633986928104);
		bcWeight=0.0661764705882353/(0.0661764705882353+0.07192575406032482);
		adWeight=0.5751633986928104/(0.15566037735849056+0.5751633986928104);
		efWeight=0.9347826086956522/(0.9347826086956522+0.18159806295399517);
		bxWeight=0.07192575406032482/(0.0661764705882353+0.07192575406032482);
		ebWeight=0.18159806295399517/(0.9347826086956522+0.18159806295399517);
		deWeight=1;
		fcWeight=1;
		ycWeight=1;
		assertEquals("wrong weight with stochastic weighting policy", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);
	}
	
	/**
	 * Test the Stochastic weight policy.
	 */
	@Test
	public void testReactionProbabilityPolicy() {
		WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new ReactionProbabilityWeight(new SimilarityWeightPolicy());
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		wp.setWeight(g);
		abWeight=0.15566037735849056/(0.15566037735849056+0.5751633986928104);
		bcWeight=0.0661764705882353/(0.0661764705882353+0.07192575406032482);
		adWeight=0.5751633986928104/(0.15566037735849056+0.5751633986928104);
		efWeight=0.9347826086956522/(0.9347826086956522+0.18159806295399517);
		bxWeight=0.07192575406032482/(0.0661764705882353+0.07192575406032482);
		ebWeight=0.18159806295399517/(0.9347826086956522+0.18159806295399517);
		deWeight=1;
		fcWeight=1;
		ycWeight=1;
		assertEquals("wrong weight with stochastic weighting policy", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight with stochastic weighting policy", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);
	}
	
	/**
	 * Test the weights export
	 */
	@Test
	public void testWeightsFromFileExport() {
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_edgeWeightExport", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		abWeight=0.1;g.setEdgeWeight(ab, abWeight);
		bcWeight=0.2;g.setEdgeWeight(bc, bcWeight);
		adWeight=0.3;g.setEdgeWeight(ad, adWeight);
		efWeight=0.4;g.setEdgeWeight(ef, efWeight);
		bxWeight=0.5;g.setEdgeWeight(bx, bxWeight);
		ebWeight=0.6;g.setEdgeWeight(eb, ebWeight);
		deWeight=0.7;g.setEdgeWeight(de, deWeight);
		fcWeight=0.8;g.setEdgeWeight(fc, fcWeight);
		ycWeight=0.9;g.setEdgeWeight(yc, ycWeight);
		try {
			WeightUtils.export(g, tmpPath.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Edge weight export failure");
		}
		
		resetWeight();
		WeightsFromFile<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new WeightsFromFile<BioPhysicalEntity,ReactionEdge,CompoundGraph>(tmpPath.toString());
		wp.setWeight(g);
		
		assertEquals("wrong weight after export", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight after export", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight after export", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight after export", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight after export", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight after export", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight after export", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight after export", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight after export", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);
		
	}
	
	/**
	 * Test the weights Import
	 */
	@Test
	public void testWeightsFromFileImport() {
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_edgeWeightmport", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		String filePath = "EdgeWeightTestFile.tab";
		try {
			filePath = TestUtils.copyProjectResource(filePath, tmpPath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
			fail("problem while reading edge weight file");
		}
		
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		abWeight=0.1;
		bcWeight=0.2;
		adWeight=0.3;
		efWeight=0.4;
		bxWeight=0.5;
		ebWeight=0.6;
		deWeight=0.7;
		fcWeight=0.8;
		ycWeight=0.9;
		
		WeightsFromFile<BioPhysicalEntity,ReactionEdge,CompoundGraph> wp = new WeightsFromFile<BioPhysicalEntity,ReactionEdge,CompoundGraph>(filePath);
		wp.setWeight(g);
		
		assertEquals("wrong weight after export", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight after export", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight after export", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight after export", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight after export", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight after export", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight after export", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight after export", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight after export", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);

	}
	
	@Ignore
	@Test
	public void testRPairsWeight(){
		RPairsWeightPolicy wp = new RPairsWeightPolicy(false);		
		
		//Test web service
		assertEquals("Unable to retreive RPairs id","RP00060",wp.getRPairEntry("C00031","C00092"));
		assertEquals("Unable to retreive atom conservation rate",1.0,wp.getAtomConservation("RP00060",false),Double.MIN_VALUE);
		assertEquals("Unable to retreive atom conservation rate",12.0/16.0,wp.getAtomConservation("RP00060",true),Double.MIN_VALUE);
		
		//Test Graph weighting
		CompoundGraph g = new CompoundGraph();
		BioPhysicalEntity glc = new BioPhysicalEntity("M_glc_D_c");g.addVertex(glc);
		glc.addRef("KEGG.COMPOUND", "C00031", 1, "is", "none");
		glc.addRef("KEGG.COMPOUND", "C00221", 1, "is", "none");
		BioPhysicalEntity g6p = new BioPhysicalEntity("M_g6p_c");g.addVertex(g6p);
		g6p.addRef("KEGG.COMPOUND", "C00092", 1, "is", "none");
		BioPhysicalEntity glc_e = new BioPhysicalEntity("M_glc_D_e");g.addVertex(glc_e);
		glc_e.addRef("KEGG.COMPOUND", "C00031", 1, "is", "none");
		BioPhysicalEntity noKegg = new BioPhysicalEntity("M_nokeg_g");g.addVertex(noKegg);
		
		BioChemicalReaction glcToG6p = new BioChemicalReaction("glcToG6p");
		glcToG6p.setReversibility(true);
		
		ReactionEdge e1 = new ReactionEdge(glc,g6p,glcToG6p);g.addEdge(glc, g6p, e1);
		ReactionEdge e2 = new ReactionEdge(g6p,glc,glcToG6p);g.addEdge(g6p, glc, e2);
		ReactionEdge e3 = new ReactionEdge(glc,glc_e,new BioChemicalReaction("transport"));g.addEdge(glc, glc_e, e3);
		ReactionEdge e4 = new ReactionEdge(g6p,glc,new BioChemicalReaction("glcToG6pII"));g.addEdge(g6p, glc, e4);
		
		wp.setWeight(g);
		
		assertEquals("wrong atom conservation ", 1.0,g.getEdgeWeight(e1),Double.MIN_VALUE);
		assertEquals("wrong atom conservation ", 12.0/16.0,g.getEdgeWeight(e2),Double.MIN_VALUE);
		assertEquals("wrong atom conservation ", 1.0,g.getEdgeWeight(e3),Double.MIN_VALUE);
		assertEquals("wrong atom conservation ", 12.0/16.0,g.getEdgeWeight(e4),Double.MIN_VALUE);
		assertFalse("Compound without Kegg in graph ",g.containsVertex(noKegg));
	}
	
	@Ignore
	@Test
	public void testRPairsTagWeight(){
		RPairsTagPolicy wp = new RPairsTagPolicy();
		wp.setLeaveWeight(42.0);
		
		//Test web service
		assertEquals("Unable to retreive RPairs id","RP00060",wp.getRPairEntry("C00031","C00092"));
		assertEquals("Unable to retreive rpair tag","main",wp.getPairType("RP00060"));
		assertEquals("Unable to retreive RPairs id","RP04650",wp.getRPairEntry("C00009","C00092"));
		assertEquals("Unable to retreive rpair tag","leave",wp.getPairType("RP04650"));
		
		//Test Graph weighting
		CompoundGraph g = new CompoundGraph();
		BioPhysicalEntity glc = new BioPhysicalEntity("M_glc_D_c");g.addVertex(glc);
		glc.addRef("KEGG.COMPOUND", "C00031", 1, "is", "none");
		glc.addRef("KEGG.COMPOUND", "C00221", 1, "is", "none");
		BioPhysicalEntity g6p = new BioPhysicalEntity("M_g6p_c");g.addVertex(g6p);
		g6p.addRef("KEGG.COMPOUND", "C00092", 1, "is", "none");
		BioPhysicalEntity glc_e = new BioPhysicalEntity("M_glc_D_e");g.addVertex(glc_e);
		glc_e.addRef("KEGG.COMPOUND", "C00031", 1, "is", "none");
		BioPhysicalEntity pho = new BioPhysicalEntity("M_pho_c");g.addVertex(pho);
		pho.addRef("KEGG.COMPOUND", "C00009", 1, "is", "none");
		BioPhysicalEntity noKegg = new BioPhysicalEntity("M_nokeg_g");g.addVertex(noKegg);
		
		BioChemicalReaction glcToG6p = new BioChemicalReaction("glcToG6p");
		glcToG6p.setReversibility(true);
		
		ReactionEdge e1 = new ReactionEdge(glc,g6p,glcToG6p);g.addEdge(glc, g6p, e1);
		ReactionEdge e2 = new ReactionEdge(g6p,glc,glcToG6p);g.addEdge(g6p, glc, e2);
		ReactionEdge e3 = new ReactionEdge(glc,glc_e,new BioChemicalReaction("transport"));g.addEdge(glc, glc_e, e3);
		ReactionEdge e4 = new ReactionEdge(g6p,glc,new BioChemicalReaction("glcToG6pII"));g.addEdge(g6p, glc, e4);
		ReactionEdge e5 = new ReactionEdge(pho,g6p,new BioChemicalReaction("glcToG6pIII"));g.addEdge(pho, g6p, e5);
		wp.setWeight(g);
		
		assertEquals("wrong atom conservation ", 1.0,g.getEdgeWeight(e1),Double.MIN_VALUE);
		assertEquals("wrong atom conservation ", 1.0,g.getEdgeWeight(e2),Double.MIN_VALUE);
		assertEquals("wrong atom conservation ", Double.NaN,g.getEdgeWeight(e3),Double.MIN_VALUE);
		assertEquals("wrong atom conservation ", 1.0,g.getEdgeWeight(e4),Double.MIN_VALUE);
		assertEquals("wrong atom conservation ", 42.0,g.getEdgeWeight(e5),Double.MIN_VALUE);
		assertFalse("Compound without Kegg in graph ",g.containsVertex(noKegg));
	}
	
//	@Test
//	public void testAtomConservationWeight(){
//		AtomConservationWeightPolicy wp = new AtomConservationWeightPolicy();
//		
//		
//		CompoundGraph g = new CompoundGraph();
//		BioPhysicalEntity glc = new BioPhysicalEntity("M_glc_D_c");g.addVertex(glc);
//		glc.setInchi("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
//		BioPhysicalEntity g6p = new BioPhysicalEntity("M_g6p_c");g.addVertex(g6p);
//		g6p.setInchi("InChI=1S/C6H13O9P/c7-3-2(1-14-16(11,12)13)15-6(10)5(9)4(3)8/h2-10H,1H2,(H2,11,12,13)/t2-,3-,4+,5-,6?/m1/s1");
//		BioPhysicalEntity glc_e = new BioPhysicalEntity("M_glc_D_e");g.addVertex(glc_e);
//		glc_e.setInchi("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
//		BioPhysicalEntity noInChI = new BioPhysicalEntity("M_noInCh_i");g.addVertex(noInChI);
//		
//		BioChemicalReaction glcToG6p = new BioChemicalReaction("glcToG6p");
//		glcToG6p.setReversibility(true);
//		ReactionEdge e1 = new ReactionEdge(glc,g6p,glcToG6p);g.addEdge(glc, g6p, e1);
//		ReactionEdge e2 = new ReactionEdge(g6p,glc,glcToG6p);g.addEdge(g6p, glc, e2);
//		ReactionEdge e3 = new ReactionEdge(glc,glc_e,new BioChemicalReaction("transport"));g.addEdge(glc, glc_e, e3);
//		ReactionEdge e4 = new ReactionEdge(g6p,glc,new BioChemicalReaction("glcToG6pII"));g.addEdge(g6p, glc, e4);
//		long t0=System.nanoTime();
//		wp.setWeight(g);
//		long t1=System.nanoTime();
//		System.out.println("sequential : "+(t1-t0));
//		assertEquals("wrong atom conservation ", 1.0,g.getEdgeWeight(e1),Double.MIN_VALUE);
//		assertEquals("wrong atom conservation ", 12.0/16.0,g.getEdgeWeight(e2),Double.MIN_VALUE);
//		assertEquals("wrong atom conservation ", 1.0,g.getEdgeWeight(e3),Double.MIN_VALUE);
//		assertEquals("wrong atom conservation ", 12.0/16.0,g.getEdgeWeight(e4),Double.MIN_VALUE);
//		assertFalse("Compound without InChI in graph ",g.containsVertex(noInChI));
//	}s
}
