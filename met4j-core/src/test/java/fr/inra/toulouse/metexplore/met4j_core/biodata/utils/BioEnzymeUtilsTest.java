package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioEnzymeUtilsTest {

	@Test
	public void testCreateIdFromProteins() {
		
		BioProtein p1 = new BioProtein("test1", "NA");
		
		BioProtein p2 = new BioProtein("test2", "NA");
		
		BioProtein p3 = new BioProtein("test3", "NA");
		
		BioCollection<BioProtein> c = new BioCollection<BioProtein>();
		
		c.add(p1);
		c.add(p2);
		c.add(p3);
		
		String id = BioEnzymeUtils.createIdFromProteins(c);
		
		assertEquals("id badly formatted", "test1_AND_test2_AND_test3", id);
		
	}

}
