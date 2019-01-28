package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;

public class GeneSetTest {

	@Test
	public void testToString() {

		BioGene g1 = new BioGene("g1");
		BioGene g2 = new BioGene("g2");
		BioGene g3 = new BioGene("g3");
		
		GeneSet set = new GeneSet();
		
		set.add(g3);
		
		assertEquals("g3", set.toString());
		
		set.add(g1);
		
		assertEquals("g1 AND g3", set.toString());
		
		set.add(g2);
		
		assertEquals("g1 AND g2 AND g3", set.toString());

	}
	
	@Test
	public void testEquals() {
		
		BioGene g1 = new BioGene("g1");
		BioGene g2 = new BioGene("g2");
		
		BioGene g1Bis = new BioGene("g1");
		BioGene g2Bis = new BioGene("g2");
		
		GeneSet set = new GeneSet();
		set.add(g1);
		set.add(g2);
		
		GeneSet setBis = new GeneSet();
		setBis.add(g1Bis);
		setBis.add(g2Bis);
		
		assertEquals(set.hashCode(), setBis.hashCode());
		
		assertEquals(set, setBis);
		
		
		
		
	}

}
