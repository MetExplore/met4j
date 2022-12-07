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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.*;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.similarity.Tanimoto;

/**
 * Test {@link fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph} with {@link WeightingPolicy <BioMetabolite, ReactionEdge , CompoundGraph >}
 * @author clement
 */
public class TestWeightingPolicy {
	
	public static CompoundGraph g;
	
	public static BioMetabolite a,b,c,d,e,f,x,y;
	
	public static ReactionEdge ab,bc,ad,de,ef,fc,bx,eb,yc;

	public static double abSim,bcSim,adSim,efSim,bxSim,ebSim,deSim,fcSim,ycSim;
	 
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
		BioReaction abd = new BioReaction("abd");
		BioReaction efb = new BioReaction("efb");
		BioReaction bxc = new BioReaction("bxc");
		BioReaction fyc = new BioReaction("fyc");
		ab = new ReactionEdge(a,b,abd);g.addEdge(a, b, ab);
		bc = new ReactionEdge(b,c,bxc);g.addEdge(b, c, bc);
		ad = new ReactionEdge(a,d,abd);g.addEdge(a, d, ad);
		de = new ReactionEdge(d,e,new BioReaction("de"));g.addEdge(d, e, de);
		ef = new ReactionEdge(e,f,efb);g.addEdge(e, f, ef);
		fc = new ReactionEdge(f,c,new BioReaction("fc"));g.addEdge(f, c, fc);
		bx = new ReactionEdge(b,x,bxc);g.addEdge(b, x, bx);
		eb = new ReactionEdge(e,b,efb);g.addEdge(e, b, eb);
		yc = new ReactionEdge(y,c,fyc);g.addEdge(y, c, yc);

		try {
			abSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(a),FingerprintBuilder.getExtendedFingerprint(b));
			bcSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(b),FingerprintBuilder.getExtendedFingerprint(c));
			adSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(a),FingerprintBuilder.getExtendedFingerprint(d));
			efSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(e),FingerprintBuilder.getExtendedFingerprint(f));
			bxSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(b),FingerprintBuilder.getExtendedFingerprint(x));
			ebSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(e),FingerprintBuilder.getExtendedFingerprint(b));
			deSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(d),FingerprintBuilder.getExtendedFingerprint(e));
			fcSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(f),FingerprintBuilder.getExtendedFingerprint(c));
			ycSim=Tanimoto.calculate(FingerprintBuilder.getExtendedFingerprint(y),FingerprintBuilder.getExtendedFingerprint(c));
		} catch (CDKException ex) {
			ex.printStackTrace();
		}
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
		WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> wp = new UnweightedPolicy<>();
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
		WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> wp = new ProbabilityWeightPolicy<>();
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
		WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> wp = new DegreeWeightPolicy(2);
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
		WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> wp = new SimilarityWeightPolicy();
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		wp.setWeight(g);
		abWeight=abSim;
		bcWeight=bcSim;
		adWeight=adSim;
		efWeight=efSim;
		bxWeight=bxSim;
		ebWeight=ebSim;
		deWeight=deSim;
		fcWeight=fcSim;
		ycWeight=ycSim;
		assertEquals("wrong weight with similarity weighting policy", abWeight, g.getEdgeWeight(ab),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", adWeight, g.getEdgeWeight(ad),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", bcWeight, g.getEdgeWeight(bc),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", bxWeight, g.getEdgeWeight(bx),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", deWeight, g.getEdgeWeight(de),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", ebWeight, g.getEdgeWeight(eb),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", efWeight, g.getEdgeWeight(ef),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", fcWeight, g.getEdgeWeight(fc),1.0e-6);
		assertEquals("wrong weight with similarity weighting policy", ycWeight, g.getEdgeWeight(yc),1.0e-6);

	}
	
	/**
	 * Test the Stochastic weight policy.
	 */
	@Test
	public void testStochasticWeightPolicy() {
//		WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> wp = new ProbabilityWeightPolicy(new SimilarityWeightPolicy());
		WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> wp = new StochasticWeightPolicy();
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		wp.setWeight(g);
		abWeight=abSim/(abSim+adSim);
		bcWeight=bcSim/(bcSim+bxSim);
		adWeight=adSim/(abSim+adSim);
		efWeight=efSim/(efSim+ebSim);
		bxWeight=bxSim/(bcSim+bxSim);
		ebWeight=ebSim/(efSim+ebSim);
		deWeight=1;
		fcWeight=1;
		ycWeight=1;
		assertEquals("wrong weight with stochastic weighting policy", abWeight, g.getEdgeWeight(ab),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", adWeight, g.getEdgeWeight(ad),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", bcWeight, g.getEdgeWeight(bc),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", bxWeight, g.getEdgeWeight(bx),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", deWeight, g.getEdgeWeight(de),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", ebWeight, g.getEdgeWeight(eb),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", efWeight, g.getEdgeWeight(ef),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", fcWeight, g.getEdgeWeight(fc),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", ycWeight, g.getEdgeWeight(yc),1.0e-6);
	}
	
	/**
	 * Test the Stochastic weight policy.
	 */
	@Test
	public void testReactionProbabilityPolicy() {
		WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> wp = new ReactionProbabilityWeight(new SimilarityWeightPolicy());
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		wp.setWeight(g);
		abWeight=abSim/(abSim+adSim);
		bcWeight=bcSim/(bcSim+bxSim);
		adWeight=adSim/(abSim+adSim);
		efWeight=efSim/(efSim+ebSim);
		bxWeight=bxSim/(bcSim+bxSim);
		ebWeight=ebSim/(efSim+ebSim);
		deWeight=1;
		fcWeight=1;
		ycWeight=1;
		assertEquals("wrong weight with stochastic weighting policy", abWeight, g.getEdgeWeight(ab),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", adWeight, g.getEdgeWeight(ad),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", bcWeight, g.getEdgeWeight(bc),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", bxWeight, g.getEdgeWeight(bx),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", deWeight, g.getEdgeWeight(de),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", ebWeight, g.getEdgeWeight(eb),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", efWeight, g.getEdgeWeight(ef),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", fcWeight, g.getEdgeWeight(fc),1.0e-6);
		assertEquals("wrong weight with stochastic weighting policy", ycWeight, g.getEdgeWeight(yc),1.0e-6);
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
		WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph> wp = new WeightsFromFile<>(tmpPath.toString());
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
		
		WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph> wp = new WeightsFromFile<>(filePath);
		wp.setWeight(g);
		
		assertEquals("wrong weight after import", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight after import", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight after import", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight after import", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight after import", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight after import", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight after import", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight after import", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight after import", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);

	}

	/**
	 * Test the weights Import
	 */
	@Test
	public void testWeightsFromFileImportII() {
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_edgeWeightmport", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		String filePath = "EdgeWeightTestFileII.tab";
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

		WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph> wp =
				new WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph>(filePath)
						.sep(",")
						.sourceCol(0)
						.targetCol(1)
						.edgeLabelCol(2)
						.weightCol(4);
        wp.setWeight(g);

		assertEquals("wrong weight after import", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight after import", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight after import", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight after import", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight after import", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight after import", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight after import", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight after import", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight after import", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);

	}

	/**
	 * Test the weights Import
	 */
	@Test
	public void testWeightsFromFileImportIII() {
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_edgeWeightmport", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		String filePath = "EdgeWeightTestFileII.tab";
		try {
			filePath = TestUtils.copyProjectResource(filePath, tmpPath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
			fail("problem while reading edge weight file");
		}

		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		abWeight=0.2;
		bcWeight=0.4;
		adWeight=0.6;
		efWeight=0.8;
		bxWeight=1.0;
		ebWeight=1.2;
		deWeight=1.4;
		fcWeight=1.6;
		ycWeight=1.8;

		WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph> wp =
				new WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph>(filePath)
						.sep(",")
						.sourceCol(0)
						.targetCol(1)
						.edgeLabelCol(2)
						.weightCol(4)
						.processWeigthCol(s -> {return Double.parseDouble(s)*2;});
		wp.setWeight(g);

		assertEquals("wrong weight after import", abWeight, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight after import", adWeight, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight after import", bcWeight, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight after import", bxWeight, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight after import", deWeight, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight after import", ebWeight, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight after import", efWeight, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight after import", fcWeight, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight after import", ycWeight, g.getEdgeWeight(yc),Double.MIN_VALUE);

	}

	@Test
	public void testAtomMappingWeightPolicy() {
		Path tmpPath = null;
		try {
			tmpPath = Files.createTempFile("test_edgeWeightmport", ".tmp");
		} catch (IOException e1) {
			e1.printStackTrace();
			Assert.fail("Creation of the temporary directory");
		}
		String filePath = "AAMTestFile.tab";
		try {
			filePath = TestUtils.copyProjectResource(filePath, tmpPath.toFile());
		} catch (IOException e) {
			e.printStackTrace();
			fail("problem while reading edge weight file");
		}

		int CCnb1 =21;
		int CCnb5 =5;
		int CCnb6 =1;



		AtomMappingWeightPolicy wp1 = new AtomMappingWeightPolicy().fromConservedCarbonIndexes(filePath);
		wp1.setWeight(g);
		assertEquals("wrong weight for AAM weight", CCnb1, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(ab)));
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(bc)));
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(bx)));
		assertEquals("wrong weight for AAM weight", CCnb5, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb6, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(yc)));

	}

	@Test
	public void testAtomMappingWeightPolicyI() {

		String RxnSmiles1 = "[O:1]=[C:2]([NH2:3])[C:4]1=[CH:5][CH:6]=[CH:7][N:8](=[CH:9]1)[CH:10]2[O:11][CH:12]([CH2:13][O:14][P:15](=[O:16])([OH:17])[O:18][P:19](=[O:20])([OH:21])[O:22][CH2:23][CH:24]3[O:25][CH:26]([N:27]4[CH:28]=[N:29][C:30]=5[C:31](=[N:32][CH:33]=[N:34][C:35]45)[NH2:36])[CH:37]([O:38][P:39](=[O:40])([OH:41])[OH:42])[CH:43]3[OH:44])[CH:45]([OH:46])[CH:47]2[OH:48]>>[O:1]=[C:2]([NH2:3])[C:4]=1[CH2:5][CH:6]=[CH:7][N:8]([CH:9]1)[CH:10]2[O:11][CH:12]([CH2:13][O:14][P:15](=[O:16])([OH:17])[O:18][P:19](=[O:20])([OH:21])[O:22][CH2:23][CH:24]3[O:25][CH:26]([N:27]4[CH:28]=[N:29][C:30]=5[C:31](=[N:32][CH:33]=[N:34][C:35]45)[NH2:36])[CH:37]([O:38][P:39](=[O:40])([OH:41])[OH:42])[CH:43]3[OH:44])[CH:45]([OH:46])[CH:47]2[OH:48]";
		String RxnSmiles2 = "[O:1]=[C:2]([NH2:3])[C:4]1=[CH:5][CH:6]=[CH:7][N:8](=[CH:9]1)[CH:10]2[O:11][CH:12]([CH2:13][O:14][P:15](=[O:16])([OH:17])[O:18][P:19](=[O:20])([OH:21])[O:22][CH2:23][CH:24]3[O:25][CH:26]([N:27]4[CH:28]=[N:29][C:30]=5[C:31](=[N:32][CH:33]=[N:34][C:35]45)[NH2:36])[CH:37]([O:38][P:39](=[O:40])([OH:41])[OH:42])[CH:43]3[OH:44])[CH:45]([OH:46])[CH:47]2[OH:48]>>[O:49]=[C:50]([OH:51])[CH2:52][CH2:53][C:57](=[O:58])[C:59](=[O:60])[OH:61]";
		String RxnSmiles3 = "[O:1]=[C:2]([NH2:3])[C:4]1=[CH:5][CH:6]=[CH:7][N:8](=[CH:9]1)[CH:10]2[O:11][CH:12]([CH2:13][O:14][P:15](=[O:16])([OH:17])[O:18][P:19](=[O:20])([OH:21])[O:22][CH2:23][CH:24]3[O:25][CH:26]([N:27]4[CH:28]=[N:29][C:30]=5[C:31](=[N:32][CH:33]=[N:34][C:35]45)[NH2:36])[CH:37]([O:38][P:39](=[O:40])([OH:41])[OH:42])[CH:43]3[OH:44])[CH:45]([OH:46])[CH:47]2[OH:48]>>[C:54](=[O:55])=[O:56]";
		String RxnSmiles4 = "[O:49]=[C:50]([OH:51])[CH2:52][CH:53]([C:54](=[O:55])[OH:56])[CH:57]([OH:58])[C:59](=[O:60])[OH:61]>>[O:1]=[C:2]([NH2:3])[C:4]=1[CH2:5][CH:6]=[CH:7][N:8]([CH:9]1)[CH:10]2[O:11][CH:12]([CH2:13][O:14][P:15](=[O:16])([OH:17])[O:18][P:19](=[O:20])([OH:21])[O:22][CH2:23][CH:24]3[O:25][CH:26]([N:27]4[CH:28]=[N:29][C:30]=5[C:31](=[N:32][CH:33]=[N:34][C:35]45)[NH2:36])[CH:37]([O:38][P:39](=[O:40])([OH:41])[OH:42])[CH:43]3[OH:44])[CH:45]([OH:46])[CH:47]2[OH:48]";
		String RxnSmiles5 = "[O:49]=[C:50]([OH:51])[CH2:52][CH:53]([C:54](=[O:55])[OH:56])[CH:57]([OH:58])[C:59](=[O:60])[OH:61]>>[O:49]=[C:50]([OH:51])[CH2:52][CH2:53][C:57](=[O:58])[C:59](=[O:60])[OH:61]";
		String RxnSmiles6 = "[O:49]=[C:50]([OH:51])[CH2:52][CH:53]([C:54](=[O:55])[OH:56])[CH:57]([OH:58])[C:59](=[O:60])[OH:61]>>[C:54](=[O:55])=[O:56]";
		int CCnb1 =21;
		int CCnb2 =0;
		int CCnb3 =0;
		int CCnb4 =0;
		int CCnb5 =5;
		int CCnb6 =1;

		HashMap<BioMetabolite, Map<BioMetabolite,String>> RxnMap = new HashMap<>();
		HashMap<BioMetabolite,String> ar = new HashMap<>();
		ar.put(d,RxnSmiles1);
		ar.put(b,RxnSmiles2);
		RxnMap.put(a,ar);
		HashMap<BioMetabolite,String> br = new HashMap<>();
		br.put(c,RxnSmiles3);
		br.put(x,RxnSmiles4);
		RxnMap.put(b,br);
		HashMap<BioMetabolite,String> er = new HashMap<>();
		er.put(b,RxnSmiles5);
		er.put(f,RxnSmiles6);
		RxnMap.put(e,er);


		AtomMappingWeightPolicy wp1 = new AtomMappingWeightPolicy().fromAAMRxnSmiles(RxnMap);
		wp1.setWeight(g);
		assertEquals("wrong weight for AAM weight", CCnb1, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb2, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb3, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb4, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb5, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb6, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(yc)));

	}

	@Test
	public void testAtomMappingWeightPolicyII() {
		ArrayList<Integer> CCList1 = new ArrayList<Integer>(Arrays.asList(2,4,5,6,7,9,10,12,13,23,24,26,28,30,31,33,35,37,43,45,47));
		ArrayList<Integer> CCList2 = new ArrayList<Integer>();
		ArrayList<Integer> CCList3 = new ArrayList<Integer>();
		ArrayList<Integer> CCList4 = new ArrayList<Integer>();
		ArrayList<Integer> CCList5 = new ArrayList<Integer>(Arrays.asList(50,52,53,57,59));
		ArrayList<Integer> CCList6 = new ArrayList<Integer>(Arrays.asList(54));
		int CCnb1 =21;
		int CCnb2 =0;
		int CCnb3 =0;
		int CCnb4 =0;
		int CCnb5 =5;
		int CCnb6 =1;

		HashMap<BioMetabolite, Map<BioMetabolite, Collection<Integer>>> labelMap = new HashMap<>();
		HashMap<BioMetabolite,Collection<Integer>> al = new HashMap<>();
		al.put(d,CCList1);
		al.put(b,CCList2);
		labelMap.put(a,al);
		HashMap<BioMetabolite,Collection<Integer>> bl = new HashMap<>();
		bl.put(c,CCList3);
		bl.put(x,CCList4);
		labelMap.put(b,bl);
		HashMap<BioMetabolite,Collection<Integer>> el = new HashMap<>();
		el.put(b,CCList5);
		el.put(f,CCList6);
		labelMap.put(e,el);


		AtomMappingWeightPolicy wp2 = new AtomMappingWeightPolicy().fromConservedCarbonIndexes(labelMap);
		wp2.setWeight(g);
		assertEquals("wrong weight for AAM weight", CCnb1, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb2, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb3, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb4, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb5, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb6, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(yc)));

	}

	@Test
	public void testAtomMappingWeightPolicyIII() {
		int CCnb1 =21;
		int CCnb2 =0;
		int CCnb3 =0;
		int CCnb4 =0;
		int CCnb5 =5;
		int CCnb6 =1;


		HashMap<BioMetabolite, Map<BioMetabolite,Integer>> nMap = new HashMap<>();
		HashMap<BioMetabolite,Integer> an = new HashMap<>();
		an.put(d,CCnb1);
		an.put(b,CCnb2);
		nMap.put(a,an);
		HashMap<BioMetabolite,Integer> bn = new HashMap<>();
		bn.put(c,CCnb3);
		bn.put(x,CCnb4);
		nMap.put(b,bn);
		HashMap<BioMetabolite,Integer> en = new HashMap<>();
		en.put(b,CCnb5);
		en.put(f,CCnb6);
		nMap.put(e,en);

		AtomMappingWeightPolicy wp3 = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(nMap);
		wp3.setWeight(g);
		assertEquals("wrong weight for AAM weight", CCnb1, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb2, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb3, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb4, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb5, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", CCnb6, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(yc)));

		AtomMappingWeightPolicy wp4 = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(nMap).binarize();
		CompoundGraph g4 = new CompoundGraph(g);
		wp4.setWeight(g4);
		assertEquals("wrong weight for AAM weight", 1, g4.getEdgeWeight(g4.getEdge(a,d)),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", 0, g4.getEdgeWeight(g4.getEdge(a,b)),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", 0, g4.getEdgeWeight(g4.getEdge(b,c)),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", 0, g4.getEdgeWeight(g4.getEdge(b,x)),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", 1, g4.getEdgeWeight(g4.getEdge(e,b)),Double.MIN_VALUE);
		assertEquals("wrong weight for AAM weight", 1, g4.getEdgeWeight(g4.getEdge(e,f)),Double.MIN_VALUE);
		assertTrue("wrong weight for AAM weight", Double.isNaN(g.getEdgeWeight(yc)));

		AtomMappingWeightPolicy wp5 = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(nMap).removeEdgesWithoutConservedCarbon();
		CompoundGraph g5 = new CompoundGraph(g);
		wp5.setWeight(g5);
		assertEquals("wrong filtering for AAM weight",6, g5.edgeSet().size());
		assertTrue("wrong filtering for AAM weight",g5.areConnected(y,c));
		assertFalse("wrong filtering for AAM weight",g5.areConnected(a,b));

		AtomMappingWeightPolicy wp6 = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(nMap).removeEdgeWithoutMapping();
		CompoundGraph g6 = new CompoundGraph(g);
		wp6.setWeight(g6);
		assertEquals("wrong filtering for AAM weight",6, g6.edgeSet().size());
		assertFalse("wrong filtering for AAM weight",g6.areConnected(y,c));
		assertTrue("wrong filtering for AAM weight",g6.areConnected(a,b));


	}
	
	
//	@Test
//	public void testAtomConservationWeight(){
//		AtomConservationWeightPolicy wp = new AtomConservationWeightPolicy();
//		
//		
//		CompoundGraph g = new CompoundGraph();
//		BioMetabolite glc = new BioMetabolite("M_glc_D_c");g.addVertex(glc);
//		glc.setInchi("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
//		BioMetabolite g6p = new BioMetabolite("M_g6p_c");g.addVertex(g6p);
//		g6p.setInchi("InChI=1S/C6H13O9P/c7-3-2(1-14-16(11,12)13)15-6(10)5(9)4(3)8/h2-10H,1H2,(H2,11,12,13)/t2-,3-,4+,5-,6?/m1/s1");
//		BioMetabolite glc_e = new BioMetabolite("M_glc_D_e");g.addVertex(glc_e);
//		glc_e.setInchi("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
//		BioMetabolite noInChI = new BioMetabolite("M_noInCh_i");g.addVertex(noInChI);
//		
//		BioReaction glcToG6p = new BioReaction("glcToG6p");
//		glcToG6p.setReversible(true);
//		ReactionEdge e1 = new ReactionEdge(glc,g6p,glcToG6p);g.addEdge(glc, g6p, e1);
//		ReactionEdge e2 = new ReactionEdge(g6p,glc,glcToG6p);g.addEdge(g6p, glc, e2);
//		ReactionEdge e3 = new ReactionEdge(glc,glc_e,new BioReaction("transport"));g.addEdge(glc, glc_e, e3);
//		ReactionEdge e4 = new ReactionEdge(g6p,glc,new BioReaction("glcToG6pII"));g.addEdge(g6p, glc, e4);
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
