package fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;

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
		
		metabolite.addAttribute(MetaboliteAttributes.BOUNDARY_CONDITION, true);
		
		assertTrue(MetaboliteAttributes.getBoundaryCondition(metabolite));
		
		metabolite.addAttribute(MetaboliteAttributes.BOUNDARY_CONDITION, false);
		
		assertFalse(MetaboliteAttributes.getBoundaryCondition(metabolite));
		
	}

}
