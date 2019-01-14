package fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;

public class MetaboliteAttributesTest {
	
	
	BioNetwork network;
	BioMetabolite metabolite;
	
	@Before
	public void init() {
		network = new BioNetwork();
		metabolite = new BioMetabolite("m");
	}

	@Test
	public void testGetBoundaryCondition() {
		
		assertFalse(MetaboliteAttributes.getBoundaryCondition(metabolite));
		
		metabolite.setAttribute(MetaboliteAttributes.BOUNDARY_CONDITION, true);
		
		assertTrue(MetaboliteAttributes.getBoundaryCondition(metabolite));
		
		metabolite.setAttribute(MetaboliteAttributes.BOUNDARY_CONDITION, false);
		
		assertFalse(MetaboliteAttributes.getBoundaryCondition(metabolite));
		
	}
	
	@Test
	public void testSetBoundaryCondition() {
		
		MetaboliteAttributes.setBoundaryCondition(metabolite, true);
		assertTrue((Boolean)metabolite.getAttribute(MetaboliteAttributes.BOUNDARY_CONDITION));
		
		MetaboliteAttributes.setBoundaryCondition(metabolite, false);
		assertFalse((Boolean)metabolite.getAttribute(MetaboliteAttributes.BOUNDARY_CONDITION));
		
	}
	
	@Test
	public void testGetConstant() {
		
		assertTrue(MetaboliteAttributes.getConstant(metabolite));
		
		metabolite.setAttribute(GenericAttributes.CONSTANT, false);
		
		assertFalse(MetaboliteAttributes.getConstant(metabolite));
		
		metabolite.setAttribute(GenericAttributes.CONSTANT, true);
		
		assertTrue(MetaboliteAttributes.getConstant(metabolite));
		
	}
	
	@Test
	public void testSetConstant() {
		
		MetaboliteAttributes.setConstant(metabolite, false);
		assertFalse((Boolean)metabolite.getAttribute(GenericAttributes.CONSTANT));
		
		MetaboliteAttributes.setConstant(metabolite, true);
		assertTrue((Boolean)metabolite.getAttribute(GenericAttributes.CONSTANT));
		
	}
	
	@Test 
	public void testSetNotes() {
		
		Notes notes = new Notes("<p>toto</p>");
		
		MetaboliteAttributes.setNotes(metabolite, notes);
		
		assertEquals(notes, metabolite.getAttribute(GenericAttributes.NOTES));
		
	}
	
	@Test 
	public void testGetNotes() {
		
		Notes notes = new Notes("<p>toto</p>");
		
		metabolite.setAttribute(GenericAttributes.NOTES, notes);
		
		assertEquals(notes, MetaboliteAttributes.getNotes(metabolite));
		
	}
	
	@Test
	public void testGetCharge() {
		
		Double charge = 1.0;
		
		metabolite.setAttribute(MetaboliteAttributes.CHARGE, charge);
		
		assertEquals(charge, MetaboliteAttributes.getCharge(metabolite));
		
	}
	
	@Test
	public void testSetCharge() {
		
		Double charge = 1.0;
		
		MetaboliteAttributes.setCharge(metabolite, charge);
		
		assertEquals(charge, metabolite.getAttribute(MetaboliteAttributes.CHARGE));
		
	}
	

}
