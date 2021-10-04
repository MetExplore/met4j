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

/**
 * 
 */
package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.classesForTests.BioEntityFake;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author lcottret
 *
 */



public class BioEntityTest {

	public static BioRef ref;
	public static BioEntity bioEntityTest;

	public static void init() {
		ref = new BioRef("origin", "dbName", "dbId", 1);
		ref.setLogicallink("link");
		bioEntityTest = new BioEntityFake("entityTest");

	}

	@BeforeClass
	public static void beforeClass() {
		init();
	}

	@After
	public void after() {
		init();
	}

	@Test
	public void testConstructorWithNullId() {
		bioEntityTest = new BioEntityFake(null);
		assertNotNull(bioEntityTest.getId());
	}
	
	/**
	 * Test method for
	 * {@link BioEntity#addRef(BioRef)}.
	 */
	@Test
	public void testAddRefFromBioRef() {

		// Test add one ref
		bioEntityTest.addRef(ref);

		assertTrue("No key dbname in the refs", bioEntityTest.getRefs().containsKey(ref.dbName));

		// Test if the ref is well loaded
		assertEquals("The ref is not well loaded", bioEntityTest.getRefs().get(ref.dbName).iterator().next(), ref);

		// Test if we load a bioref with the same parameters, there is no
		// redundance
		BioRef refRedundant = new BioRef(ref.origin, ref.dbName, ref.id, ref.confidenceLevel);
		refRedundant.setLogicallink(ref.getLogicallink());
		bioEntityTest.addRef(refRedundant);

		assertEquals("The same ref has been added", 1, bioEntityTest.getRefs().get(ref.dbName).size());

		// Test if we load a bioref with an other logical link, it's considered
		// as a new Bioref
		refRedundant.setLogicallink("linkRedundant");
		bioEntityTest.addRef(refRedundant);
		assertEquals("The same ref (except logical link) has not been added", 2,
				bioEntityTest.getRefs().get(ref.dbName).size());

	}

	/**
	 * Test method for
	 * {@link BioEntity#addRef(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddRefFromParameters() {

		// Test add one ref
		bioEntityTest.addRef(ref.dbName, ref.getId(), ref.confidenceLevel, ref.getLogicallink(), ref.getOrigin());
		assertTrue("No key dbname in the refs", bioEntityTest.getRefs().containsKey(ref.dbName));

		// Test if the ref is well loaded
		assertEquals("The ref is not well loaded", bioEntityTest.getRefs().get(ref.dbName).iterator().next(), ref);

		// Test if we load a bioref with the same parameters, there is no
		// redundance
		bioEntityTest.addRef(ref.dbName, ref.getId(), ref.confidenceLevel, ref.getLogicallink(), ref.getOrigin());

		assertEquals("The same ref has been added", 1, bioEntityTest.getRefs().get(ref.dbName).size());

		// Test if we load a bioref with an other logical link, it's considered
		// as a new Bioref
		bioEntityTest.addRef(ref.dbName, ref.getId(), ref.confidenceLevel, "linkRedundant", ref.getOrigin());
		assertEquals("The same ref (except logical link) has not been added", 2,
				bioEntityTest.getRefs().get(ref.dbName).size());

	}

	/**
	 * Test method for
	 * {@link BioEntity#getRefs(java.lang.String)}.
	 */
	@Test
	public void testGetRefsFromDbName() {

		bioEntityTest.addRef(ref);
		Set<BioRef> refs = new HashSet<BioRef>();
		refs.add(ref);

		assertEquals("The set of refs is not the one that is expected", refs, bioEntityTest.getRefs(ref.dbName));

	}

	/**
	 * Test method for
	 * {@link BioEntity#hasRef(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testHasRefFromParameters() {

		// Test if HasRef returns false if the set of refs is empty
		assertFalse("Must return false when the set of refs is empty", bioEntityTest.hasRef("test", "test"));

		// Test if HasREf returns false if the parameters does not correspond to
		// any ref
		bioEntityTest.addRef(ref);
		assertFalse("Must return false when the ref does not correspond to any ref",
				bioEntityTest.hasRef("test", "test"));

		// Test if hasRef returns true if the parameters corresponds to a ref
		assertTrue("Must return true if the parameters corresponds to a ref", bioEntityTest.hasRef(ref.dbName, ref.id));

	}

	/**
	 * Test method for
	 * {@link BioEntity#hasRef(BioRef)}.
	 */
	@Test
	public void testHasRefBioRef() {

		// Test if HasRef returns false if the set of refs is empty
		assertFalse("Must return false when the set of refs is empty", bioEntityTest.hasRef(ref));

		// Test if HasREf returns false if the ref does not correspond to
		// any ref
		BioRef ref2 = new BioRef("test", "test", "test", 1);

		bioEntityTest.addRef(ref);
		assertFalse("Must return false when the ref does not correspond to any ref", bioEntityTest.hasRef(ref2));

		// Test if hasRef returns true if the ref is in a set of refs
		assertTrue("Must return true if the ref is in a set of refs", bioEntityTest.hasRef(ref));

		// Test if it returns true if the ref is similar to a ref in the set of
		// refs
		ref2 = new BioRef(ref.origin, ref.dbName, ref.id, ref.confidenceLevel);
		ref2.setLogicallink(ref.logicallink);
		assertTrue("Must return true if the ref is similar to a ref in the set of refs", bioEntityTest.hasRef(ref2));

		// Test if it returns false if the ref is similar (except the logical
		// link) to a ref in the set of refs
		ref2.setLogicallink("test");
		assertFalse("Must return false if the ref is similar (except the logical link) to a ref in the set of refs",
				bioEntityTest.hasRef(ref2));

	}

}
