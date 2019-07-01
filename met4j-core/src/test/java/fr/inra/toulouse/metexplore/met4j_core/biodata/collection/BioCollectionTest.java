package fr.inra.toulouse.metexplore.met4j_core.biodata.collection;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

public class BioCollectionTest {

	public BioCollection<BioMetabolite> collec;
	public BioMetabolite m1;
	public BioMetabolite m2;

	@Before
	public void init() {
		collec = new BioCollection<BioMetabolite>();

		m1 = new BioMetabolite("m1", "metabolite1");
		m2 = new BioMetabolite("m2", "metabolite2");

		collec.add(m1);
		collec.add(m2);

	}

	@Test
	public void testContainsId() {

		assertTrue(collec.containsId("m1"));
		assertTrue(collec.containsId("m2"));
		assertFalse(collec.containsId("m3"));

	}

	@Test
	public void testContainsName() {
		assertTrue(collec.containsName("metabolite1"));
		assertFalse(collec.containsId("m3"));
	}

	@Test
	public void testGetEntityFromId() {

		assertEquals(collec.getEntityFromId("m1"), m1);

	}

	@Test
	public void testGetIds() {

		Set<String> refs = new HashSet<String>();
		refs.add("m1");
		refs.add("m2");

		assertEquals(collec.getIds(), refs);

	}

	@Test
	public void testGetEntitiesFromName() {

		BioCollection<BioMetabolite> c2 = new BioCollection<BioMetabolite>();
		c2.add(m1);
		assertEquals(collec.getEntitiesFromName("metabolite1"), c2);

	}

	@Test
	public void testSize() {

		assertEquals(2, collec.size());

	}

	@Test
	public void testIsEmpty() {

		assertFalse(collec.isEmpty());

		BioCollection<BioEntity> emp = new BioCollection<BioEntity>();

		assertTrue(emp.isEmpty());

	}

	@Test
	public void testRetainAll() {

		BioCollection<BioMetabolite> c2 = new BioCollection<BioMetabolite>();
		c2.add(m1);

		collec.retainAll(c2);

		assertEquals(1, collec.size());

		assertTrue(collec.containsId("m1"));

	}

	@Test
	public void testClear() {

		collec.clear();

		assertTrue(collec.isEmpty());

	}

	@Test
	public void testRemoveAll() {

		BioCollection<BioMetabolite> c2 = new BioCollection<BioMetabolite>();
		c2.add(m1);

		collec.removeAll(c2);

		assertEquals(1, collec.size());

		assertTrue(collec.containsId("m2"));

	}

	@Test
	public void testContains() {

		assertTrue(collec.contains(m1));
		assertTrue(collec.contains(m2));

		BioMetabolite m3 = new BioMetabolite("m3");

		assertFalse(collec.contains(m3));

	}

	@Test
	public void testAdd() {

		BioMetabolite m3 = new BioMetabolite("m3");

		collec.add(m3);

		assertTrue(collec.contains(m3));

	}

	@Test
	public void testRemove() {

		collec.remove(m1);

		assertEquals(1, collec.size());

		assertTrue(collec.containsId("m2"));

	}

	@Test
	public void testContainsAll() {

		BioCollection<BioMetabolite> collec2 = new BioCollection<BioMetabolite>();

		BioMetabolite m3 = new BioMetabolite("m3");
		
		collec2.add(m1);

		assertTrue(collec.containsAll(collec2));

		collec2.add(m2);
		
		assertTrue(collec.containsAll(collec2));

		collec2.add(m3);

		assertFalse(collec.containsAll(collec2));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddAll() {

		BioMetabolite m3 = new BioMetabolite("m3");
		BioMetabolite m4 = new BioMetabolite("m4");

		BioCollection<BioMetabolite> collec2 = new BioCollection<BioMetabolite>();

		collec2.add(m3);
		collec2.add(m4);

		collec.addAll(collec2);

		assertEquals(4, collec.size());

		collec.addAll(collec2);

		assertEquals(4, collec.size());

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetView() {

		BioCollection<BioMetabolite> c2 = collec.getView();

		assertEquals(2, c2.size());

		c2.remove(m1);

	}

	@Test
	public void testEqualsObject() {

		BioCollection<BioMetabolite> c2 = new BioCollection<BioMetabolite>();

		BioMetabolite m1Bis = new BioMetabolite("m1", "metabolite1");
		BioMetabolite m2Bis = new BioMetabolite("m2", "metabolite1");

		c2.add(m1Bis);

		assertFalse(collec.equals(c2));

		c2.add(m2Bis);

		assertTrue(collec.equals(c2));

	}

}
