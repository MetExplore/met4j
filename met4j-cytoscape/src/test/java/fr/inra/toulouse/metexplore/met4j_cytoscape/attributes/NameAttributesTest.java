/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_cytoscape.attributes;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;

public class NameAttributesTest {
	
	
	

	@Test 
	public void testGetAttributes() {
		BioNetwork network = new BioNetwork();

		BioReaction r1 = new BioReaction("r1-rxn", "R1");

		BioPhysicalEntity a = new BioPhysicalEntity("A-cpd", "A';");
		BioPhysicalEntity b = new BioPhysicalEntity("B-cpd", "B");
		BioPhysicalEntity c = new BioPhysicalEntity("C-cpd", "C");
		BioPhysicalEntity d = new BioPhysicalEntity("D-cpd", "D");

		r1.addLeftParticipant(new BioParticipant(a));
		r1.addLeftParticipant(new BioParticipant(b));
		r1.addRightParticipant(new BioParticipant(c));
		r1.addRightParticipant(new BioParticipant(d));

		BioReaction r2 = new BioReaction("r2-rxn", "R2&lt;i&gt;ase&lt;/i&gt;");

		r2.addRightParticipant(new BioParticipant(a));
		r2.addRightParticipant(new BioParticipant(b));
		r2.addLeftParticipant(new BioParticipant(c));
		r2.addLeftParticipant(new BioParticipant(d));

		network.addBiochemicalReaction(r1);
		network.addBiochemicalReaction(r2);
		
		// test compounds names
		HashMap<String, String> ref = new HashMap<String, String>();
		ref.put("A-cpd", "A'");
		ref.put("B-cpd", "B");
		ref.put("C-cpd", "C");
		ref.put("D-cpd", "D");
		
		NameAttributes converter = new NameAttributes(network, false, true);
		
		HashMap<String, String> map = converter.getAttributes();

		assertEquals("compound name maps are not equal", ref, map);
		
		// test compounds names with sbml coded
		ref.clear();
		ref.put("A__45__cpd", "A'");
		ref.put("B__45__cpd", "B");
		ref.put("C__45__cpd", "C");
		ref.put("D__45__cpd", "D");
		
		
		converter = new NameAttributes(network, true, true);
		
		map = converter.getAttributes();

		assertEquals("compound name maps (sbml coded) are not equal", ref, map);
		
		// test reaction names
		ref.clear();
		ref.put("r1-rxn", "R1");
		ref.put("r2-rxn", "R2ase");
		
		converter = new NameAttributes(network, false, false);
		
		map = converter.getAttributes();
		
		assertEquals("reaction name maps are not equal", ref, map);
		
		// test reaction names with sbml coded
		ref.clear();
		ref.put("r1__45__rxn", "R1");
		ref.put("r2__45__rxn", "R2ase");
		
		converter = new NameAttributes(network, true, false);
		
		map = converter.getAttributes();
		
		assertEquals("reaction name maps are not equal", ref, map);
		
		
		
	}


}
