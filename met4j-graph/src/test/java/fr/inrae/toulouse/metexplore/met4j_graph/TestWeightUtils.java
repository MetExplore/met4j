
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

import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.weighting.WeightUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.weighting.WeightsFromFile;


public class TestWeightUtils {
	
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
	 * Test the weights inversion
	 */
	@Test
	public void testInvert(){
		
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
		WeightUtils.invert(g);
		
		assertEquals("wrong weight after inversion", 0.9, g.getEdgeWeight(ab),1E-16);
		assertEquals("wrong weight after inversion", 0.7, g.getEdgeWeight(ad),1E-16);
		assertEquals("wrong weight after inversion", 0.8, g.getEdgeWeight(bc),1E-16);
		assertEquals("wrong weight after inversion", 0.5, g.getEdgeWeight(bx),1E-16);
		assertEquals("wrong weight after inversion", 0.3, g.getEdgeWeight(de),1E-16);//=0.30000000000000004 -> fail with Double.MIN_VALUE...
		assertEquals("wrong weight after inversion", 0.4, g.getEdgeWeight(eb),1E-16);
		assertEquals("wrong weight after inversion", 0.6, g.getEdgeWeight(ef),1E-16);
		assertEquals("wrong weight after inversion", 0.2, g.getEdgeWeight(fc),1E-16);
		assertEquals("wrong weight after inversion", 0.1, g.getEdgeWeight(yc),1E-16);
	}
	
	/**
	 * test rising weight to the power of n
	 */
	@Test
	public void testPow(){
	
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		abWeight=1;g.setEdgeWeight(ab, abWeight);
		bcWeight=2;g.setEdgeWeight(bc, bcWeight);
		adWeight=3;g.setEdgeWeight(ad, adWeight);
		efWeight=4;g.setEdgeWeight(ef, efWeight);
		bxWeight=5;g.setEdgeWeight(bx, bxWeight);
		ebWeight=6;g.setEdgeWeight(eb, ebWeight);
		deWeight=7;g.setEdgeWeight(de, deWeight);
		fcWeight=8;g.setEdgeWeight(fc, fcWeight);
		ycWeight=9;g.setEdgeWeight(yc, ycWeight);
		WeightUtils.pow(g, 2);
		
		assertEquals("wrong weight after pow", 1, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 9, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 4, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 25, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 49, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 36, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 16, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 64, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight after pow", 81, g.getEdgeWeight(yc),Double.MIN_VALUE);
	}
	
	/**
	 * Test weight scaling
	 */
	@Test
	public void testScale(){
		
		double abWeight,bcWeight,adWeight,efWeight,bxWeight,ebWeight,deWeight,fcWeight,ycWeight;
		abWeight=500;g.setEdgeWeight(ab, abWeight);
		bcWeight=500;g.setEdgeWeight(bc, bcWeight);
		adWeight=750;g.setEdgeWeight(ad, adWeight);
		efWeight=750;g.setEdgeWeight(ef, efWeight);
		bxWeight=1000;g.setEdgeWeight(bx, bxWeight);
		ebWeight=1000;g.setEdgeWeight(eb, ebWeight);
		deWeight=1250;g.setEdgeWeight(de, deWeight);
		fcWeight=1250;g.setEdgeWeight(fc, fcWeight);
		ycWeight=1500;g.setEdgeWeight(yc, ycWeight);
		WeightUtils.scale(g);
		
		assertEquals("wrong weight after scaling", 0.0, g.getEdgeWeight(ab),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 0.25, g.getEdgeWeight(ad),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 0.0, g.getEdgeWeight(bc),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 0.5, g.getEdgeWeight(bx),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 0.75, g.getEdgeWeight(de),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 0.5, g.getEdgeWeight(eb),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 0.25, g.getEdgeWeight(ef),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 0.75, g.getEdgeWeight(fc),Double.MIN_VALUE);
		assertEquals("wrong weight after scaling", 1.0, g.getEdgeWeight(yc),Double.MIN_VALUE);
	}
	
	/**
	 * Test the weights export
	 */
	@Test
	public void testWeightsExport() {
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
		WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph> wp = new WeightsFromFile<BioMetabolite,ReactionEdge,CompoundGraph>(tmpPath.toString());
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
}
