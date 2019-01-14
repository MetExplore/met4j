package fr.inra.toulouse.metexplore.met4j_io.annotations.compartment;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;

public class CompartmentAttributesTest {
	
	BioNetwork network;
	BioCompartment compartment;
	
	@Before
	public void init() {
		network = new BioNetwork();
		compartment = new BioCompartment("c");
	}

	@Test
	public void testGetConstant() {
		
		assertTrue(CompartmentAttributes.getConstant(compartment));
		
		compartment.addAttribute(GenericAttributes.CONSTANT, false);
		
		assertFalse(CompartmentAttributes.getConstant(compartment));
		
		compartment.addAttribute(GenericAttributes.CONSTANT, true);
		
		assertTrue(CompartmentAttributes.getConstant(compartment));
		
	}
	
	@Test
	public void testSetConstant() {
		
		CompartmentAttributes.setConstant(compartment, false);
		assertFalse((Boolean)compartment.getAttribute(GenericAttributes.CONSTANT));
		
		CompartmentAttributes.setConstant(compartment, true);
		assertTrue((Boolean)compartment.getAttribute(GenericAttributes.CONSTANT));
		
	}


}
