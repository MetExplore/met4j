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
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BioRefTest {
	
	
	public BioRef ref;
	
	@Before
	public void init() {
		
	}

	@Test(expected = Exception.class)
	public void testBioRefWithNullDbName() {
		ref = new BioRef("ori", null, "id", 1);
	}
	
	@Test(expected = Exception.class)
	public void testBioRefWithNullId() {
		ref = new BioRef("ori", "dbName", null, 1);
	}
		
	@Test
	public void testEquals() {
		
		BioRef ref = new BioRef("origin", "db1", "id1",1);
		BioRef ref2 = new BioRef("origin2", "db1", "id1", 2);
		BioRef ref3 = new BioRef("origin2", "db2", "id1", 1);
		BioRef ref4 = new BioRef("origin2", "db1", "id2", 1);
		
		assertTrue("Two refs with same db and id must be equal", ref.equals(ref2));
		assertFalse("Two refs with different db must not be equal", ref.equals(ref3));
		assertFalse("Two refs with different id must not be equal", ref.equals(ref4));
		
		ref.setLogicallink("otherLink");
		assertFalse("Two refs with different logical link must not be equal", ref.equals(ref2));
		
	}
	
	@Test
	public void testCompare() {
		BioRef ref = new BioRef("origin", "db1", "id1",1);
		BioRef ref2 = new BioRef("origin2", "db1", "id1", 2);
		
		assertEquals("Test comparison BioRefs", 1, ref2.compare(ref2, ref));
		assertEquals("Test comparison BioRefs", 1, ref2.compareTo(ref));
		
	}
	

}
