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
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import fr.inra.toulouse.metexplore.met4j_core.biodata.classesForTests.BioMetaboliteFake;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

/**
 * @author lcottret
 *
 */
public class BioReactionTest {

	public static BioCompartment cpt1;
	public static BioCompartment cpt2;

	public static BioReactant l1;
	public static BioReactant l2;
	public static BioReactant r1;
	public static BioReactant r2;

	public static BioReaction reaction;

	public static Field leftField;
	public static Field rightField;

	@Before
	public void init()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		cpt1 = new BioCompartment("cpt1");
		cpt2 = new BioCompartment("cpt2");

		l1 = new BioReactant(new BioMetaboliteFake("l1"), 1.0, cpt1);
		l2 = new BioReactant(new BioMetaboliteFake("l2"), 1.0, cpt1);
		r1 = new BioReactant(new BioMetaboliteFake("r1"), 1.0, cpt2);
		r2 = new BioReactant(new BioMetaboliteFake("r2"), 1.0, cpt2);

		reaction = new BioReaction("testreaction");

		leftField = BioReaction.class.getDeclaredField("left");
		leftField.setAccessible(true);

		rightField = BioReaction.class.getDeclaredField("right");
		rightField.setAccessible(true);
		BioCollection<BioReactant> left = new BioCollection<BioReactant>();
		left.add(l1);
		left.add(l2);
		BioCollection<BioReactant> right = new BioCollection<BioReactant>();
		right.add(r1);
		right.add(r2);

		leftField.set(reaction, left);
		rightField.set(reaction, right);
	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction#toString()}.
	 * 
	 */
	@Test
	public void testToString() {

		assertEquals("Formula badly formatted", "testreaction: 1 l1[cpt1] + 1 l2[cpt1] -> 1 r1[cpt2] + 1 r2[cpt2]",
				reaction.toString());

		reaction.setReversible(true);

		assertEquals("Formula badly formatted", "testreaction: 1 l1[cpt1] + 1 l2[cpt1] <-> 1 r1[cpt2] + 1 r2[cpt2]",
				reaction.toString());

	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction#isTransportReaction()}.
	 */
	@Test
	public void testIsTransportReaction() {

		// Positive test
		assertTrue("Must be a transport reaction", reaction.isTransportReaction());

		// Negative Test
		r1.setLocation(cpt1);
		r2.setLocation(cpt1);

		assertFalse("Must not be a transport reaction", reaction.isTransportReaction());
	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction#getLeft()}.
	 */
	@Test
	public void testGetLeft() {
		BioCollection<BioPhysicalEntity> leftCpds = new BioCollection<BioPhysicalEntity>();
		leftCpds.add(l1.getPhysicalEntity());
		leftCpds.add(l2.getPhysicalEntity());

		assertArrayEquals("getLeft does not function well", leftCpds.toArray(), reaction.getLeft().toArray());

	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction#getRight()}.
	 */
	@Test
	public void testGetRight() {
		BioCollection<BioPhysicalEntity> rightCpds = new BioCollection<BioPhysicalEntity>();
		rightCpds.add(r1.getPhysicalEntity());
		rightCpds.add(r2.getPhysicalEntity());

		assertArrayEquals("getLeft does not function well", rightCpds.toArray(), reaction.getRight().toArray());
	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction#getEntities()}.
	 */
	@Test
	public void testGetEntities() {

		BioCollection<BioPhysicalEntity> entities = new BioCollection<BioPhysicalEntity>();
		entities.add(l1.getPhysicalEntity());
		entities.add(l2.getPhysicalEntity());
		entities.add(r1.getPhysicalEntity());
		entities.add(r2.getPhysicalEntity());

		// With distinct metabolites
		assertArrayEquals("getEntities does not return the good entities", entities.toArray(),
				reaction.getEntities().toArray());

	}

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction#getLeftReactants()}.
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws ArrayComparisonFailure
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLeftReactants() throws ArrayComparisonFailure, IllegalArgumentException, IllegalAccessException {

		assertArrayEquals("getLeftReactants does not function well",
				((BioCollection<BioPhysicalEntity>) leftField.get(reaction)).toArray(), reaction.getLeftReactants().toArray());

	}
	
	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction#getRightReactants()}.
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws ArrayComparisonFailure
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetRightReactants() throws ArrayComparisonFailure, IllegalArgumentException, IllegalAccessException {

		assertArrayEquals("getRightReactants does not function well",
				((BioCollection<BioPhysicalEntity>) rightField.get(reaction)).toArray(), reaction.getRightReactants().toArray());

	}

	public void testGetProteins() {
		fail("not implemented");
	}

	public void testGetGenes() {
		fail("not implemented");
	}

}
