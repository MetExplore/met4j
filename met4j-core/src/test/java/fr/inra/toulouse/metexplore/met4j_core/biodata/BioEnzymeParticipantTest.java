package fr.inra.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import org.junit.Test;




public class BioEnzymeParticipantTest {
	

	@Test
	public void testToString() {
		
		BioEnzymeParticipant p = new BioEnzymeParticipant(new BioProtein("p1"), 2.0);
		
		assertEquals("toString does not work", "2 p1", p.toString());
		
		
	}


	@Test
	public void testEquality() {
		
		
		BioEnzymeParticipant p = new BioEnzymeParticipant(new BioProtein("p1"), 2.0);
		BioEnzymeParticipant p2 = new BioEnzymeParticipant(new BioProtein("p2"), 2.0);
		BioEnzymeParticipant pBis = new BioEnzymeParticipant(new BioProtein("p1"), 2.0);
		
		assertNotEquals("p and p2 must not be equal", p, p2);
		
		assertEquals("p and pBis must not be equal", p, pBis);
		
		
	}


}
