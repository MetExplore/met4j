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

package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;

public class GeneAssociationTest {

	BioGene g1, g2, g3, g4;
	GeneAssociation a;

	@Before
	public void init() {

		a = new GeneAssociation();

		g1 = new BioGene("g1");
		g2 = new BioGene("g2");
		g3 = new BioGene("g3");
		g4 = new BioGene("g4");

	}

	@Test
	public void testToString() {

		GeneSet set1 = new GeneSet();
		set1.add(g1);

		a.add(set1);

		assertEquals("g1", a.toString());

		set1.add(g2);

		assertEquals("g1 AND g2", a.toString());

		GeneSet set2 = new GeneSet();
		set2.add(g3);

		a.add(set2);

		assertEquals("( g1 AND g2 ) OR g3", a.toString());

		set2.add(g4);

		assertEquals("( g1 AND g2 ) OR ( g3 AND g4 )", a.toString());

		BioGene g1Bis = new BioGene("g1");
		BioGene g2Bis = new BioGene("g2");
		GeneSet set1Bis = new GeneSet();
		set1Bis.add(g1Bis);
		set1Bis.add(g2Bis);

		assertEquals(set1Bis.hashCode(), set1.hashCode());
		assertEquals(set1Bis, set1);

		a.add(set1Bis);

		assertEquals("( g1 AND g2 ) OR ( g3 AND g4 )", a.toString());

	}

	@Test
	public void testContains() {

		GeneSet set1 = new GeneSet();
		set1.add(g1);

		a.add(set1);

		assertTrue(a.contains(set1));

		assertEquals("g1", a.toString());

		set1.add(g2);

		assertTrue(a.contains(set1));

	}

	@Test
	public void testRemove() {

		GeneSet set1 = new GeneSet();
		
		assertFalse(a.remove(set1));
		
		set1.add(g1);

		a.add(set1);

		assertTrue(a.contains(set1));

		assertEquals("g1", a.toString());

		set1.add(g2);

		a.remove(set1);
		
		assertTrue(!a.contains(set1));

		a.add(set1);
		set1.remove(g2);

		GeneSet set1bis = new GeneSet();
		set1bis.add(g1);

		a.remove(set1bis);

		assertEquals(0, a.size());

	}

	@Test
	public void testAdd() {

		GeneSet set1 = new GeneSet();
		set1.add(g1);
		a.add(set1);
		set1.add(g2);

		GeneSet set1bis = new GeneSet();
		set1bis.add(g1);
		set1bis.add(g2);

		a.add(set1bis);

		assertEquals(1, a.size());

	}

	@Test
	public void testAddAll() {

		GeneAssociation a2 = new GeneAssociation();

		GeneSet set1 = new GeneSet();
		set1.add(g1);
		a.add(set1);
		set1.add(g2);

		GeneSet set2 = new GeneSet();
		set2.add(g2);
		a2.add(set2);
		set1.add(g3);

		a.addAll(a2);

		assertEquals(2, a.size());

		a2.add(set1);

		a.addAll(a2);

		assertEquals(2, a.size());

	}

	@Test
	public void testRemoveAll() {

		GeneAssociation a2 = new GeneAssociation();

		GeneSet set1 = new GeneSet();
		set1.add(g1);
		a.add(set1);
		set1.add(g2);

		GeneSet set2 = new GeneSet();
		set2.add(g2);
		set1.add(g3);

		a.add(set2);
		a2.add(set1);

		a.removeAll(a2);

		assertEquals(1, a.size());
		
		assertEquals(set2, a.iterator().next());

	}
	
	@Test
	public void testRetainAll() {
		
		GeneAssociation a2 = new GeneAssociation();

		GeneSet set1 = new GeneSet();
		set1.add(g1);
		a.add(set1);
		set1.add(g2);

		GeneSet set2 = new GeneSet();
		set2.add(g2);
		set1.add(g3);

		a.add(set2);
		a2.add(set1);

		a.retainAll(a2);

		assertEquals(1, a.size());
		
		assertEquals(set1, a.iterator().next());
		
	}
	
	@Test
	public void testContainsAll() {
		
		GeneAssociation a2 = new GeneAssociation();

		GeneSet set1 = new GeneSet();
		set1.add(g1);
		set1.add(g2);

		GeneSet set2 = new GeneSet();
		set2.add(g2);
		set1.add(g3);

		a.add(set2);
		a2.add(set1);
		
		assertFalse(a.containsAll(a2));
		
		a.add(set1);
		
		assertTrue(a.containsAll(a2));
		
	}
	

}
