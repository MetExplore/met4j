/**
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author lcottret
 *
 */



public class BioEntityTest {

	public static BioRef ref;
	public static BioEntityFake bioEntityTest;

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

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity#addRef(fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef)}.
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
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity#addRef(java.lang.String, java.lang.String, int, java.lang.String, java.lang.String)}.
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
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity#getRefs(java.lang.String)}.
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
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity#hasRef(java.lang.String, java.lang.String)}.
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
	 * {@link fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity#hasRef(fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef)}.
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
