package fr.inra.toulouse.metexplore.met4j_io.annotations.gene;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

public class GeneAttributesTest {
	
	BioGene g;
	
	@Before
	public void init() {
		g = new BioGene("g");
	}

	@Test
	public void testGetPmids() {
		Set<Integer> pmids = new HashSet<Integer>();

		pmids.add(1235);
		pmids.add(111);

		g.setAttribute(GenericAttributes.PMIDS, pmids);

		assertEquals(pmids, GeneAttributes.getPmids(g));
	}

	@Test
	public void testSetPmids() {
		Set<Integer> pmids = new HashSet<Integer>();

		pmids.add(1235);
		pmids.add(111);

		GeneAttributes.setPmids(g, pmids);

		assertEquals(pmids, g.getAttribute(GenericAttributes.PMIDS));
	}

}
