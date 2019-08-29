package fr.inra.toulouse.metexplore.met4j_io.annotations.reactant;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

public class ReactantAttributesTest {

	BioReactant r;
	BioCompartment c;
	BioMetabolite m;

	@Before
	public void init() {
		m = new BioMetabolite("m");
		c = new BioCompartment("c");

		r = new BioReactant(m, 1.0, c);
	}

	@Test
	public void testGetConstant() {

		assertFalse(ReactantAttributes.getConstant(r));

		r.setAttribute(GenericAttributes.CONSTANT, false);

		assertFalse(ReactantAttributes.getConstant(r));

		r.setAttribute(GenericAttributes.CONSTANT, true);

		assertTrue(ReactantAttributes.getConstant(r));

	}

	@Test
	public void testSetConstant() {

		ReactantAttributes.setConstant(r, false);
		assertFalse((Boolean) r.getAttribute(GenericAttributes.CONSTANT));

		ReactantAttributes.setConstant(r, true);
		assertTrue((Boolean) r.getAttribute(GenericAttributes.CONSTANT));

	}

}
