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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.network;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjective;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjectiveCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

public class NetworkAttributesTest {

	BioNetwork network;
	BioUnitDefinitionCollection unitDefinitions;
	BioUnitDefinition u1, u2;

	@Before
	public void init() {
		network = new BioNetwork();

		u1 = new BioUnitDefinition("u1", "U1");
		u2 = new BioUnitDefinition("u2", "U2");

		unitDefinitions = new BioUnitDefinitionCollection();
		unitDefinitions.add(u1);
		unitDefinitions.add(u2);
	}

	@Test
	public void testAddUnitDefinitions() {
		NetworkAttributes.addUnitDefinitions(network, unitDefinitions);

		Set<String> cTest = ((BioUnitDefinitionCollection) network.getAttribute(NetworkAttributes.UNIT_DEFINITIONS))
				.getIds();

		assertEquals("Test add unit definition collection", unitDefinitions.getIds(), cTest);

	}

	@Test
	public void testGetUnitDefinitions() {
		NetworkAttributes.addUnitDefinitions(network, unitDefinitions);
		Set<String> cTest = NetworkAttributes.getUnitDefinitions(network).getIds();

		assertEquals("Test get unit definition collection", unitDefinitions.getIds(), cTest);
	}

	@Test
	public void testGetUnitDefinition() {
		NetworkAttributes.addUnitDefinitions(network, unitDefinitions);

		BioUnitDefinition test = NetworkAttributes.getUnitDefinition(network, "u1");

		assertEquals("Test get unit definition", u1, test);

	}

	@Test
	public void testGetNotExistingUnitDefinition() {
		NetworkAttributes.addUnitDefinitions(network, unitDefinitions);

		BioUnitDefinition test = NetworkAttributes.getUnitDefinition(network, "toto");
		assertNull("Test get not existing unit definition", test);
	}

	@Test(expected = NullPointerException.class)
	public void testGetUnitDefinitionWithoutUnitDefinitions() {
		NetworkAttributes.getUnitDefinition(network, "toto");
	}

	@Test
	public void testContainsUnitDefinition() {

		assertFalse("Test if no unitDefinitions", NetworkAttributes.containsUnitDefinitions(network));

		NetworkAttributes.addUnitDefinitions(network, unitDefinitions);
		assertTrue("Test if unitDefinitions", NetworkAttributes.containsUnitDefinitions(network));

	}

	@Test
	public void testAddUnitDefinition() {

		NetworkAttributes.addUnitDefinition(network, u1);
		NetworkAttributes.addUnitDefinition(network, u2);

		Set<String> cTest = ((BioUnitDefinitionCollection) network.getAttribute(NetworkAttributes.UNIT_DEFINITIONS))
				.getIds();

		assertEquals("Test add unit definition", unitDefinitions.getIds(), cTest);

	}

	@Test
	public void testSetNotes() {

		Notes notes = new Notes("<p>toto</p>");

		NetworkAttributes.setNotes(network, notes);

		assertEquals(notes, network.getAttribute(GenericAttributes.SBML_NOTES));

	}

	@Test
	public void testGetNotes() {

		Notes notes = new Notes("<p>toto</p>");

		network.setAttribute(GenericAttributes.SBML_NOTES, notes);

		assertEquals(notes, NetworkAttributes.getNotes(network));

	}
	
	@Test
	public void testSetPmids() {

		Set<Integer> pmids = new HashSet<Integer>();

		pmids.add(1235);
		pmids.add(111);

		NetworkAttributes.setPmids(network, pmids);

		assertEquals(pmids, network.getAttribute(GenericAttributes.PMIDS));

	}

	@Test
	public void testGetPmids() {

		Set<Integer> pmids = new HashSet<Integer>();

		pmids.add(1235);
		pmids.add(111);

		network.setAttribute(GenericAttributes.PMIDS, pmids);

		assertEquals(pmids, NetworkAttributes.getPmids(network));

	}
	
	@Test 
	public void testSetAnnotation() {
		
		SbmlAnnotation val = new SbmlAnnotation("val", "<annotation>annot</annotation>");
		
		NetworkAttributes.setAnnotation(network, val);
		
		assertEquals(val, network.getAttribute(GenericAttributes.SBML_ANNOTATION));
		
	}
	
	@Test
	public void testGetAnnotation() {
		
		SbmlAnnotation val = new SbmlAnnotation("val", "<annotation>annot</annotation>");
		
		network.setAttribute(GenericAttributes.SBML_ANNOTATION, val);
		
		assertEquals(val, NetworkAttributes.getAnnotation(network));
		
	}
	
	
	@Test
	public void testGetObjectives() {
		
		BioObjective obj1 = new BioObjective("obj1", "obj1");
		BioObjective obj2 = new BioObjective("obj2", "obj2");
		
		BioObjectiveCollection objs = new BioObjectiveCollection();
		objs.add(obj1);
		objs.add(obj2);
		
		network.setAttribute(NetworkAttributes.OBJECTIVES, objs);
		
		assertEquals(objs, NetworkAttributes.getObjectives(network));
		
	}
	
	@Test
	public void testSetObjectives() {
		
		BioObjective obj1 = new BioObjective("obj1", "obj1");
		BioObjective obj2 = new BioObjective("obj2", "obj2");
		
		BioObjectiveCollection objs = new BioObjectiveCollection();
		objs.add(obj1);
		objs.add(obj2);
		
		NetworkAttributes.setObjectives(network, objs);
		
		assertEquals(objs, network.getAttribute(NetworkAttributes.OBJECTIVES));
		
	}


}
