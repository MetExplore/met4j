package fr.inra.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;




public class BioReactantTest {
	

	@Test
	public void testToString() {
		
		// Test with an integer
		BioReactant reactant = new BioReactant(new BioMetabolite("cpdId"), 1.0, new BioCompartment("cptId"));
		assertEquals("reactant badly formatted (integer coefficient)", "1 cpdId[cptId]", reactant.toString());
		
		// Test if a double is well rounded
		reactant = new BioReactant(new BioMetabolite("cpdId"), 1.26666, new BioCompartment("cptId"));
		assertEquals("reactant badly formatted (integer coefficient)", "1.27 cpdId[cptId]", reactant.toString());
		
		// Test when the biocompartment is null
		reactant = new BioReactant(new BioMetabolite("cpdId"), 1.0, null);
		assertEquals("reactant badly formatted (integer coefficient)", "1 cpdId", reactant.toString());
		
	}


	@Test
	public void testEquality() {
	
		BioMetabolite c1 = new BioMetabolite("c1");
		BioCompartment cpt1 = new BioCompartment("cpt1");
		BioMetabolite c2 = new BioMetabolite("c2");
		BioCompartment cpt2 = new BioCompartment("cpt2");
		
		BioReactant r1 = new BioReactant(c1, 1.0, cpt1);
		BioReactant r2 = new BioReactant(c1, 2.0, cpt1);
		BioReactant r3 = new BioReactant(c1, 1.0, cpt2);
		BioReactant r4 = new BioReactant(c2, 1.0, cpt1);
		BioReactant r5 = new BioReactant(c1, 1.0, cpt1);
		
		assertFalse("r1 and r2 must not be equal", r1.equals(r2));
		assertFalse("r1 and r3 must not be equal", r1.equals(r3));
		assertFalse("r1 and r4 must not be equal", r1.equals(r4));
		assertTrue("r1 and r5 must be equal", r1.equals(r5));
		
	}


}
