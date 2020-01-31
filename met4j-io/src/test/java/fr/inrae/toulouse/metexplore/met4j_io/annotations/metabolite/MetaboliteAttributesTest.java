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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;

public class MetaboliteAttributesTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
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
		
		assertFalse(MetaboliteAttributes.getConstant(metabolite));
		
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

		String sbo = "SBO:1234567";
		MetaboliteAttributes.setSboTerm(metabolite, sbo);

		assertEquals((String) metabolite.getAttribute(GenericAttributes.SBO_TERM), sbo);
	}
	
	
	@Test
	public void testSetSboTermBadlyFormatted() {

		String sbo = "SBO:134567";
		MetaboliteAttributes.setSboTerm(metabolite, sbo);
		
		assertNull(MetaboliteAttributes.getSboTerm(metabolite));

	}
	
	@Test
	public void testSetSboTermBadlyFormatted2() {

		String sbo = "SB:1234567";
		MetaboliteAttributes.setSboTerm(metabolite, sbo);

		assertNull(MetaboliteAttributes.getSboTerm(metabolite));

	}

	@Test
	public void testGetInitialAmount() {
		
		Double val = 1.0;
		
		assertNull(MetaboliteAttributes.getInitialAmount(metabolite));
		
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
		
		assertNull(MetaboliteAttributes.getInitialConcentration(metabolite));
		
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
		
		SbmlAnnotation val = new SbmlAnnotation("val", "<annotation>annot</annotation>");
		
		MetaboliteAttributes.setAnnotation(metabolite, val);
		
		assertEquals(val, metabolite.getAttribute(GenericAttributes.SBML_ANNOTATION));
		
	}
	
	@Test
	public void testGetAnnotation() {
		
		SbmlAnnotation val = new SbmlAnnotation("val", "<annotation>annot</annotation>");
		
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
	
	@Test
	public void testGetIsCofactor() {
		
		assertFalse(MetaboliteAttributes.getIsCofactor(metabolite));

		metabolite.setAttribute(MetaboliteAttributes.IS_COFACTOR, true);
		
		assertTrue(MetaboliteAttributes.getIsCofactor(metabolite));
		
		metabolite.setAttribute(MetaboliteAttributes.IS_COFACTOR, false);
		
		assertFalse(MetaboliteAttributes.getIsCofactor(metabolite));
		
	}
	
	@Test
	public void testSetIsCofactor() {
		
		MetaboliteAttributes.setIsCofactor(metabolite, true);
		assertTrue((Boolean)metabolite.getAttribute(MetaboliteAttributes.IS_COFACTOR));
		
		MetaboliteAttributes.setIsCofactor(metabolite, false);
		assertFalse((Boolean)metabolite.getAttribute(MetaboliteAttributes.IS_COFACTOR));
		
	}

}
