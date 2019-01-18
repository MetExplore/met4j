package fr.inra.toulouse.metexplore.met4j_io.annotations;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;

public class GenericAttributesTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testGetConstant() {
		exception.expect(IllegalArgumentException.class);

		GenericAttributes.getConstant(new BioProtein("p"));

	}

	@Test
	public void testSetConstant() {
		exception.expect(IllegalArgumentException.class);

		GenericAttributes.setConstant(new BioProtein("p"), true);
	}

}
