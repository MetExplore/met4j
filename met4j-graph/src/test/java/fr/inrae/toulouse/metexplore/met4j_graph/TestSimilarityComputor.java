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

import java.util.BitSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_mathUtils.similarity.SimilarityComputor;

/**
 * Test {@link SimilarityComputor}
 * @author clement
 */
public class TestSimilarityComputor {
	

	public static BitSet set1;
	

	public static BitSet set2;
	
	/** The expected cosine. */
	public static double expectedCosine;
	
	/** The expected dice. */
	public static double expectedDice;
	
	/** The expected euclidean. */
	public static double expectedEuclidean;
	
	/** The expected manhattan. */
	public static double expectedManhattan;
	
	/** The expected soergel. */
	public static double expectedSoergel;
	
	/** The expected tanimoto. */
	public static double expectedTanimoto;
	
	/** The expected xor. */
	public static double expectedXor;
	

	@BeforeClass
	public static void init(){
		set1 = fromString("0101000111");
		set2 = fromString("0100001110");
		expectedCosine=1-0.32918;
		expectedDice=1-0.333333;
		expectedEuclidean=1.732051;
		expectedManhattan=3.000000;
		expectedSoergel=0.500000;
		expectedTanimoto=1-0.500000;
		expectedXor=3.000000;
	}
	
	/**
	 * Test the cosine coeff.
	 */
	@Test
	public void testCosineCoeff() {
		assertEquals("error compting Cosine", expectedCosine, SimilarityComputor.getCosineCoeff(set1, set2),0.000001);
	}
	
	/**
	 * Test the dice coeff.
	 */
	@Test
	public void testDiceCoeff() {
		assertEquals("error compting Dice", expectedDice, SimilarityComputor.getDiceCoeff(set1, set2),0.000001);
	}	
	
	/**
	 * Test the euclidean dist.
	 */
	@Test
	public void testEuclideanDist() {
		assertEquals("error compting Euclidean", expectedEuclidean, SimilarityComputor.getEuclideanDist(set1, set2),0.000001);
	}	
	
	/**
	 * Test the manhattan dist.
	 */
	@Test
	public void testManhattanDist() {
		assertEquals("error compting Manhattan", expectedManhattan, SimilarityComputor.getManhattanDist(set1, set2),0.000001);
	}	
	
	/**
	 * Test the soergel dist.
	 */
	@Test
	public void testSoergelDist() {
		assertEquals("error compting Soergel", expectedSoergel, SimilarityComputor.getSoergelDist(set1, set2),0.000001);
	}	
	
	/**
	 * Test the tanimoto.
	 */
	@Test
	public void testTanimoto() {
		assertEquals("error compting Tanimoto", expectedTanimoto, SimilarityComputor.getTanimoto(set1, set2),0.000001);
	}
	
	/**
	 * Test the xor.
	 */
	@Test
	public void testXor() {
		assertEquals("error compting Xor dist", expectedXor, SimilarityComputor.getXorDist(set1, set2),0.000001);
	}
	
	
	/**
	 * From string.
	 *
	 * @param s the string
	 * @return the bit set
	 */
	private static BitSet fromString(final String s) {
        return BitSet.valueOf(new long[] { Long.parseLong(s, 2) });
    }
}
