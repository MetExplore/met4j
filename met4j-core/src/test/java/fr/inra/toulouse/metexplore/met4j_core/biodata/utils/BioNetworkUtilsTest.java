package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioNetworkUtilsTest {

	@Test
	public void testGetChokeReactions() {

		BioNetwork network = new BioNetwork("model");

		BioReaction r1 = new BioReaction("R1");
		BioReaction r2 = new BioReaction("R2");

		BioMetabolite m1 = new BioMetabolite("m1");
		BioMetabolite m2 = new BioMetabolite("m2");
		BioMetabolite m3 = new BioMetabolite("m3");

		BioCompartment c = new BioCompartment("c");

		network.add(r1, r2, m1, m2, m3, c);
		
		network.affectToCompartment(c, m1, m2, m3);

		network.affectLeft(m1, 1.0, c, r1);
		network.affectLeft(m2, 1.0, c, r1);

		network.affectLeft(m1, 1.0, c, r2);

		network.affectRight(m3, 1.0, c, r1);

		network.affectRight(m3, 1.0, c, r2);

		BioCollection<BioReaction> chokeReactions = BioNetworkUtils.getChokeReactions(network);
		
		assertEquals(1, chokeReactions.size());
		
		assertTrue(chokeReactions.contains(r1));
		

	}

}
