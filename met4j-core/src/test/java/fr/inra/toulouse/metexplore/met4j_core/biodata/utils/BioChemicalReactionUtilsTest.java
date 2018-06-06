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
package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;

/**
 * @author lcottret
 *
 */
public class BioChemicalReactionUtilsTest {
	BioNetwork network;
	BioReaction r1;
	BioMetabolite m1, m2;
	BioCompartment c;

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
		network.affectToCompartment(m1, c);
		network.affectToCompartment(m2, c);
		network.affectLeft(m1, 1.0, c, r1);
		network.affectRight(m2, 1.0, c, r1);
		r1.setReversible(false);

	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils#areRedundant(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction, fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	 */
	@Test
	public void testAreRedundantIrreversible() {

		BioReaction r2 = new BioReaction("r2");
		network.add(r2);

		network.affectLeft(m1, 1.0, c, r2);

		assertFalse("r1 and r2 must be identified as not redundant",
				BioChemicalReactionUtils.areRedundant(network, r1, r2));

		network.affectRight(m2, 1.0, c, r2);

		assertTrue("r1 and r2 must be identified as redundant", BioChemicalReactionUtils.areRedundant(network, r1, r2));

	}

	@Test
	public void testAreRedundantReversible() {

		r1.setReversible(true);
		BioReaction r2 = new BioReaction("r2");
		r2.setReversible(true);

		network.add(r2);

		network.affectLeft(m1, 1.0, c, r2);

		network.affectRight(m2, 1.0, c, r2);

		assertTrue("r1 and r2 must be identified as redundant", BioChemicalReactionUtils.areRedundant(network, r1, r2));

		network.removeLeft(m1, c, r2);
		network.removeRight(m2, c, r2);

		network.affectLeft(m2, 1.0, c, r2);

		network.affectRight(m1, 1.0, c, r2);

		assertTrue("r1 and r2 must be identified as redundant", BioChemicalReactionUtils.areRedundant(network, r1, r2));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAreRedundantR1Absent() {

		BioReaction r2 = new BioReaction("r2");
		BioChemicalReactionUtils.areRedundant(network, r2, r1);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAreRedundantR2Absent() {

		BioReaction r2 = new BioReaction("r2");
		BioChemicalReactionUtils.areRedundant(network, r1, r2);

	}

	@Test(expected = NullPointerException.class)
	public void testAreRedundantNetworkNull() {

		BioReaction r2 = new BioReaction("r2");
		BioChemicalReactionUtils.areRedundant(null, r1, r2);
	}

	@Test(expected = NullPointerException.class)
	public void testAreRedundantR1Null() {

		BioChemicalReactionUtils.areRedundant(network, null, r1);
	}

	@Test(expected = NullPointerException.class)
	public void testAreRedundantR2Null() {

		BioChemicalReactionUtils.areRedundant(network, r1, null);
	}

	@Test
	public void testAreRedundantRev() {
		BioReaction r2 = new BioReaction("r2");
		network.add(r2);

		network.affectLeft(m1, 1.0, c, r2);

		network.affectRight(m2, 1.0, c, r2);

		r2.setReversible(true);

		assertFalse("r1 and r2 must be identified as not redundant",
				BioChemicalReactionUtils.areRedundant(network, r1, r2));

	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils#isGeneticallyPossible(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	 */
	@Test
	public void testIsGeneticallyPossible() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils#getGPR(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	 */
	@Test
	public void testGetGPR() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils#computeAtomBalances(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	 */
	@Test
	public void testComputeAtomBalances() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils#isBalanced(fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction)}.
	 */
	@Test
	public void testIsBalanced() {
		fail("Not yet implemented");
	}

}
