package fr.inra.toulouse.metexplore.met4j_io.annotations.network;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.BioAnnotation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

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

		assertEquals(notes, network.getAttribute(GenericAttributes.NOTES));

	}

	@Test
	public void testGetNotes() {

		Notes notes = new Notes("<p>toto</p>");

		network.setAttribute(GenericAttributes.NOTES, notes);

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
		
		BioAnnotation val = new BioAnnotation("val", "<p>");
		
		NetworkAttributes.setAnnotation(network, val);
		
		assertEquals(val, network.getAttribute(GenericAttributes.SBML_ANNOTATION));
		
	}
	
	@Test
	public void testGetAnnotation() {
		
		BioAnnotation val = new BioAnnotation("val", "<p>");
		
		network.setAttribute(GenericAttributes.SBML_ANNOTATION, val);
		
		assertEquals(val, NetworkAttributes.getAnnotation(network));
		
	}


}
