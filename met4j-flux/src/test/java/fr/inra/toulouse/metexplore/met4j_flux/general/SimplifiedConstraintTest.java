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

package fr.inra.toulouse.metexplore.met4j_flux.general;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimplifiedConstraintTest {

	@Test
	public void testEqualsSimpleConstraint() {
		SimplifiedConstraint c1 = new SimplifiedConstraint("b", 100.0, ConstraintType.BINARY);
		SimplifiedConstraint c2 = new SimplifiedConstraint("b", 100.0, ConstraintType.BINARY);
		
		assertTrue(c1.equals(c2));
		
		SimplifiedConstraint c3 = new SimplifiedConstraint("b", 10.0,  ConstraintType.BINARY);
		assertFalse(c1.equals(c3));
		
		c3 = new SimplifiedConstraint("b1", 100.0, ConstraintType.BINARY);
		assertFalse(c1.equals(c3));
		
		c3 = new SimplifiedConstraint("b", 10.0, ConstraintType.BINARY);
		assertFalse(c1.equals(c3));
		
		c3 = new SimplifiedConstraint("b", 100.0, ConstraintType.DOUBLE);
		assertFalse(c1.equals(c3));
	
		
	}
	
	
	@Test
	public void testSetOfSimpleConstraints() {
		Set<SimplifiedConstraint> set = new HashSet<SimplifiedConstraint>();
		set.add(new SimplifiedConstraint("b",10.0, ConstraintType.BINARY));
		set.add(new SimplifiedConstraint("b",10.0, ConstraintType.BINARY));
		
		Assert.assertEquals("test the duplication of constraints in a set", 1, set.size());
		
	}

}
