package fr.inra.toulouse.metexplore.met4j_io.annotations.compartment;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;
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
		
		compartment.setAttribute(GenericAttributes.CONSTANT, false);
		
		assertFalse(CompartmentAttributes.getConstant(compartment));
		
		compartment.setAttribute(GenericAttributes.CONSTANT, true);
		
		assertTrue(CompartmentAttributes.getConstant(compartment));
		
	}
	
	@Test
	public void testSetConstant() {
		
		CompartmentAttributes.setConstant(compartment, false);
		assertFalse((Boolean)compartment.getAttribute(GenericAttributes.CONSTANT));
		
		CompartmentAttributes.setConstant(compartment, true);
		assertTrue((Boolean)compartment.getAttribute(GenericAttributes.CONSTANT));
		
	}

	@Test 
	public void testSetNotes() {
		
		Notes notes = new Notes("<p>toto</p>");
		
		CompartmentAttributes.setNotes(compartment, notes);
		
		assertEquals(notes, compartment.getAttribute(GenericAttributes.NOTES));
		
	}
	
	@Test 
	public void testGetNotes() {
		
		Notes notes = new Notes("<p>toto</p>");
		
		compartment.setAttribute(GenericAttributes.NOTES, notes);
		
		assertEquals(notes, CompartmentAttributes.getNotes(compartment));
		
	}
	
	@Test
	public void testSetOutsideCompartment() {
		
		BioCompartment outside = new BioCompartment("o");
		
		CompartmentAttributes.setOutsideCompartment(compartment, outside);
		
		assertEquals((BioCompartment)compartment.getAttribute(CompartmentAttributes.OUTSIDE_COMPARTMENT), outside);
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetEqualOutsideCompartment() {
		CompartmentAttributes.setOutsideCompartment(compartment, compartment);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetIdenticalOutsideCompartment() {
		
		BioCompartment outside = new BioCompartment("c");
		CompartmentAttributes.setOutsideCompartment(compartment, outside);
	}
	
	@Test
	public void testGetOutsideCompartment() {
		BioCompartment outside = new BioCompartment("o");
		
		compartment.setAttribute(CompartmentAttributes.OUTSIDE_COMPARTMENT, outside);
		
		assertEquals(outside, CompartmentAttributes.getOutsideCompartment(compartment));
		
	}
	
	@Test
	public void testGetNullOutsideCompartment() {
		
		assertNull(CompartmentAttributes.getOutsideCompartment(compartment));
		
	}
	
	@Test 
	public void testGetType() {
		
		String type = "type";
		
		compartment.setAttribute(CompartmentAttributes.TYPE, type);
		
		assertEquals(type, CompartmentAttributes.getType(compartment));
		
	}
	
	@Test
	public void testSetType() {
		
		String type = "type";
		
		CompartmentAttributes.setType(compartment, type); 		
		
		assertEquals((String) compartment.getAttribute(CompartmentAttributes.TYPE), type);
		
	}
	


}
