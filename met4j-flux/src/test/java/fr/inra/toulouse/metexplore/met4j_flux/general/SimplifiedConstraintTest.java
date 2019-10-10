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
