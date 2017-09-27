package fr.inra.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import org.junit.Test;




public class BioReactantTest {
	

	@Test
	public void testToString() {
		
		// Test with an integer
		BioReactant reactant = new BioReactant(new BioPhysicalEntityFake("cpdId"), 1.0, new BioCompartment("cptId"));
		assertEquals("reactant badly formatted (integer coefficient)", "1 cpdId[cptId]", reactant.toString());
		
		// Test if a double is well rounded
		reactant = new BioReactant(new BioPhysicalEntityFake("cpdId"), 1.26666, new BioCompartment("cptId"));
		assertEquals("reactant badly formatted (integer coefficient)", "1.27 cpdId[cptId]", reactant.toString());
		
		// Test when the biocompartment is null
		reactant = new BioReactant(new BioPhysicalEntityFake("cpdId"), 1.0, null);
		assertEquals("reactant badly formatted (integer coefficient)", "1 cpdId", reactant.toString());
		
	}

}
