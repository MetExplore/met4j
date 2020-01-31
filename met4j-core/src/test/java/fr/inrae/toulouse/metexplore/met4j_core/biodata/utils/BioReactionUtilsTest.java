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
package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import static org.junit.Assert.*;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import org.junit.Before;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;

/**
 * @author lcottret
 *
 */
public class BioReactionUtilsTest {
	BioNetwork network;
	BioReaction r1;
	BioMetabolite m1, m2;
	BioCompartment c;
	BioProtein p1, p2, p3;
	BioGene g1, g2, g3;
	BioEnzyme e1, e2;

	@Before
	public void init() {
		network = new BioNetwork();
		r1 = new BioReaction("r1");
		network.add(r1);
		m1 = new BioMetabolite("m1");
		network.add(m1);
		m2 = new BioMetabolite("m2");
		network.add(m2);
		c = new BioCompartment("c");
		network.add(c);
		network.affectToCompartment(c, m1, m2);
		network.affectLeft(r1, 1.0, c, m1);
		network.affectRight(r1, 1.0, c, m2);
		r1.setReversible(false);

		e1 = new BioEnzyme("e1");
		e2 = new BioEnzyme("e2");

		network.add(e1);
		network.add(e2);

		p1 = new BioProtein("p1");
		p2 = new BioProtein("p2");
		p3 = new BioProtein("p3");

		network.add(p1);
		network.add(p2);
		network.add(p3);

		g1 = new BioGene("g1", "G1");
		g2 = new BioGene("g2", "G2");
		g3 = new BioGene("g3", "G3");
		
		network.add(g1);
		network.add(g2);
		network.add(g3);
		

	}

	/**
	 * Test method for
	 * {@link BioReactionUtils#areRedundant(BioNetwork, BioReaction, BioReaction)}.
	 */
	@Test
	public void testAreRedundantIrreversible() {

		BioReaction r2 = new BioReaction("r2");
		r2.setReversible(false);
		network.add(r2);

		network.affectLeft(r2, 1.0, c, m1);

		assertFalse("r1 and r2 must be identified as not redundant",
				BioReactionUtils.areRedundant(network, r1, r2));

		network.affectRight(r2, 1.0, c, m2);

		assertTrue("r1 and r2 must be identified as redundant", BioReactionUtils.areRedundant(network, r1, r2));

	}

	@Test
	public void testAreRedundantReversible() {

		r1.setReversible(true);
		BioReaction r2 = new BioReaction("r2");
		r2.setReversible(true);

		network.add(r2);

		network.affectLeft(r2, 1.0, c, m1);

		network.affectRight(r2, 1.0, c, m2);

		assertTrue("r1 and r2 must be identified as redundant", BioReactionUtils.areRedundant(network, r1, r2));

		network.removeLeft(m1, c, r2);
		network.removeRight(m2, c, r2);

		network.affectLeft(r2, 1.0, c, m2);

		network.affectRight(r2, 1.0, c, m1);

		assertTrue("r1 and r2 must be identified as redundant", BioReactionUtils.areRedundant(network, r1, r2));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAreRedundantR1Absent() {

		BioReaction r2 = new BioReaction("r2");
		BioReactionUtils.areRedundant(network, r2, r1);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAreRedundantR2Absent() {

		BioReaction r2 = new BioReaction("r2");
		BioReactionUtils.areRedundant(network, r1, r2);

	}

	@Test(expected = NullPointerException.class)
	public void testAreRedundantNetworkNull() {

		BioReaction r2 = new BioReaction("r2");
		BioReactionUtils.areRedundant(null, r1, r2);
	}

	@Test(expected = NullPointerException.class)
	public void testAreRedundantR1Null() {

		BioReactionUtils.areRedundant(network, null, r1);
	}

	@Test(expected = NullPointerException.class)
	public void testAreRedundantR2Null() {

		BioReactionUtils.areRedundant(network, r1, null);
	}

	@Test
	public void testAreRedundantRev() {
		BioReaction r2 = new BioReaction("r2");
		network.add(r2);

		network.affectLeft(r2, 1.0, c, m1);

		network.affectRight(r2, 1.0, c, m2);

		r2.setReversible(true);

		assertFalse("r1 and r2 must be identified as not redundant",
				BioReactionUtils.areRedundant(network, r1, r2));

	}

	@Test
	public void testGprUnicity() {

		network.affectGeneProduct(p1, g1);
		network.affectGeneProduct(p2, g1);
		network.affectGeneProduct(p3, g1);

		network.affectSubUnit(p1, 1.0, e1);
		network.affectSubUnit(p2, 1.0, e1);
		network.affectSubUnit(p3, 1.0, e2);

		network.affectEnzyme(e1, r1);
		network.affectEnzyme(e2, r1);

		String gprRef = "g1";

		String gprTest = BioReactionUtils.getGPR(network, r1, false);

		assertEquals("Test unicity in GPRs", gprRef, gprTest);

	}

	@Test
	public void testGpr() {

		network.affectGeneProduct(p1, g1);
		network.affectGeneProduct(p2, g2);
		network.affectGeneProduct(p3, g3);

		network.affectSubUnit(p1, 1.0, e1);
		network.affectSubUnit(p2, 1.0, e1);
		network.affectSubUnit(p3, 1.0, e2);

		network.affectEnzyme(e1, r1);
		network.affectEnzyme(e2, r1);

		String gprRef = "( g1 AND g2 ) OR ( g3 )";

		String gprTest = BioReactionUtils.getGPR(network, r1, false);

		assertEquals("Test  GPRs", gprRef, gprTest);
		
		String gprRefWithNames = "( G1 AND G2 ) OR ( G3 )";
		
		gprTest = BioReactionUtils.getGPR(network, r1, true);
		
		assertEquals("Test  GPRs with names", gprRefWithNames, gprTest);
		
	}

	// /**
	// * Test method for
	// * {@link
	// fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils#isGeneticallyPossible(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	// */
	// @Test
	// public void testIsGeneticallyPossible() {
	// fail("Not yet implemented");
	// }
	//
	// /**
	// * Test method for
	// * {@link
	// fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils#getGPR(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	// */
	// @Test
	// public void testGetGPR() {
	// fail("Not yet implemented");
	// }
	//
	// /**
	// * Test method for
	// * {@link
	// fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils#computeAtomBalances(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	// */
	// @Test
	// public void testComputeAtomBalances() {
	// fail("Not yet implemented");
	// }
	//
	// /**
	// * Test method for
	// * {@link
	// fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils#isBalanced(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	// */
	// @Test
	// public void testIsBalanced() {
	// fail("Not yet implemented");
	// }

}
