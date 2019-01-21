package fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;

public class MetaboliteAttributesTest {
	
	
	BioMetabolite metabolite;
	
	@Before
	public void init() {
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
		
		assertEquals(notes, metabolite.getAttribute(GenericAttributes.SBML_NOTES));
		
	}
	
	@Test 
	public void testGetNotes() {
		
		Notes notes = new Notes("<p>toto</p>");
		
		metabolite.setAttribute(GenericAttributes.SBML_NOTES, notes);
		
		assertEquals(notes, MetaboliteAttributes.getNotes(metabolite));
		
	}
	
	@Test 
	public void testGetSubstanceUnits() {
		
		String u = "u";
		
		metabolite.setAttribute(MetaboliteAttributes.SUBSTANCE_UNITS, u);
		
		assertEquals(u, MetaboliteAttributes.getSubtanceUnits(metabolite));
		
	}
	
	@Test
	public void testSetSubstanceUnits() {
		
		String u = "u";
		
		MetaboliteAttributes.setSubstanceUnits(metabolite, u);
		
		assertEquals(u, metabolite.getAttribute(MetaboliteAttributes.SUBSTANCE_UNITS));
		
		
	}
	
	@Test
	public void testGetSboTerm() {

		String sbo = "sbo";

		metabolite.setAttribute(GenericAttributes.SBO_TERM, sbo);

		assertEquals(sbo, MetaboliteAttributes.getSboTerm(metabolite));

	}

	@Test
	public void testSetSboTerm() {

		String sbo = "sbo";
		MetaboliteAttributes.setSboTerm(metabolite, sbo);

		assertEquals((String) metabolite.getAttribute(GenericAttributes.SBO_TERM), sbo);
	}

	@Test
	public void testGetInitialAmount() {
		
		Double val = 1.0;
		
		assertEquals(0.0, MetaboliteAttributes.getInitialAmount(metabolite), 0.0);
		
		metabolite.setAttribute(MetaboliteAttributes.INITIAL_AMOUNT, val);
		
		assertEquals(val, MetaboliteAttributes.getInitialAmount(metabolite), 0.0);
		
	}
	
	@Test
	public void testSetInitialAmount() {
		
		Double val = 1.0;
		
		MetaboliteAttributes.setInitialAmount(metabolite, val);
		
		assertEquals(val, metabolite.getAttribute(MetaboliteAttributes.INITIAL_AMOUNT));
		
	}
	
	@Test
	public void testGetInitialConcentration() {
		
		Double val = 1.0;
		
		assertEquals(0.0, MetaboliteAttributes.getInitialConcentration(metabolite), 0.0);
		
		metabolite.setAttribute(MetaboliteAttributes.INITIAL_CONCENTRATION, val);
		
		assertEquals(val, MetaboliteAttributes.getInitialConcentration(metabolite), 0.0);
		
	}
	
	@Test
	public void testSetInitialConcentration() {
		
		Double val = 1.0;
		
		MetaboliteAttributes.setInitialConcentration(metabolite, val);
		
		assertEquals(val, metabolite.getAttribute(MetaboliteAttributes.INITIAL_CONCENTRATION));
		
	}
	
	@Test 
	public void testSetAnnotation() {
		
		SbmlAnnotation val = new SbmlAnnotation("val", "<p>");
		
		MetaboliteAttributes.setAnnotation(metabolite, val);
		
		assertEquals(val, metabolite.getAttribute(GenericAttributes.SBML_ANNOTATION));
		
	}
	
	@Test
	public void testGetAnnotation() {
		
		SbmlAnnotation val = new SbmlAnnotation("val", "<p>");
		
		metabolite.setAttribute(GenericAttributes.SBML_ANNOTATION, val);
		
		assertEquals(val, MetaboliteAttributes.getAnnotation(metabolite));
		
	}
	
	
	@Test
	public void testGetPubchem() {
		
		String val = "id";
		
		metabolite.setAttribute(MetaboliteAttributes.PUBCHEM, val);
		
		assertEquals(val, MetaboliteAttributes.getPubchem(metabolite));
		
	}
	
	@Test 
	public void testSetPubchem() {
		
		String val = "id";
		
		MetaboliteAttributes.setPubchem(metabolite, val);
		
		assertEquals(val, metabolite.getAttribute(MetaboliteAttributes.PUBCHEM));
		
	}
	
	@Test
	public void testSetPmids() {

		Set<Integer> pmids = new HashSet<Integer>();

		pmids.add(1235);
		pmids.add(111);

		MetaboliteAttributes.setPmids(metabolite, pmids);

		assertEquals(pmids, metabolite.getAttribute(GenericAttributes.PMIDS));

	}

	@Test
	public void testGetPmids() {

		Set<Integer> pmids = new HashSet<Integer>();

		pmids.add(1235);
		pmids.add(111);

		metabolite.setAttribute(GenericAttributes.PMIDS, pmids);

		assertEquals(pmids, MetaboliteAttributes.getPmids(metabolite));

	}
	
	@Test
	public void testGetHasOnlySubstanceUnits() {
		
		
		metabolite.setAttribute(MetaboliteAttributes.HAS_ONLY_SUBSTANCE_UNITS, true);
		
		assertTrue(MetaboliteAttributes.getHasOnlySubstanceUnits(metabolite));
		
		metabolite.setAttribute(MetaboliteAttributes.HAS_ONLY_SUBSTANCE_UNITS, false);
		
		assertFalse(MetaboliteAttributes.getHasOnlySubstanceUnits(metabolite));
		
	}
	
	@Test
	public void testSetHasOnlySubstanceUnits() {
		
		MetaboliteAttributes.setHasOnlySubstanceUnits(metabolite, true);
		assertTrue((Boolean)metabolite.getAttribute(MetaboliteAttributes.HAS_ONLY_SUBSTANCE_UNITS));
		
		MetaboliteAttributes.setHasOnlySubstanceUnits(metabolite, false);
		assertFalse((Boolean)metabolite.getAttribute(MetaboliteAttributes.HAS_ONLY_SUBSTANCE_UNITS));
		
	}

}
