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

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;

/**
 * @author lcottret
 *
 */
public class PathwayAttributesTest {

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_cytoscape.attributes.PathwayAttributes#getAttributes()}.
	 */
	@Test
	public void testGetAttributes() {

		BioNetwork network = new BioNetwork();

		BioChemicalReaction r1 = new BioChemicalReaction("r1-rxn", "R1");

		BioPhysicalEntity a = new BioPhysicalEntity("A-cpd", "A';");
		BioPhysicalEntity b = new BioPhysicalEntity("B-cpd", "B");
		BioPhysicalEntity c = new BioPhysicalEntity("C-cpd", "C");
		BioPhysicalEntity d = new BioPhysicalEntity("D-cpd", "D");

		r1.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(c));

		BioChemicalReaction r2 = new BioChemicalReaction("r2-rxn", "R2&lt;i&gt;ase&lt;/i&gt;");

		r2.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(d));

		BioPathway p1 = new BioPathway("P-1", "pathway;");
		BioPathway p2 = new BioPathway("P-2", "pathway--2;");
		BioPathway p3 = new BioPathway("P-3", "pathway3;");
		BioPathway p4 = new BioPathway("P-4", "pathway--4;");

		r1.addPathway(p1);
		r1.addPathway(p2);
		r2.addPathway(p3);
		r2.addPathway(p4);

		network.addBiochemicalReaction(r1);
		network.addBiochemicalReaction(r2);

		// test compound pathways
		HashMap<String, String> ref = new HashMap<String, String>();
		ref.put("A-cpd", "(pathway::pathway--2)");
		ref.put("B-cpd", "(pathway--4::pathway3)");
		ref.put("C-cpd", "(pathway::pathway--2)");
		ref.put("D-cpd", "(pathway--4::pathway3)");

		PathwayAttributes converter = new PathwayAttributes(network, false, true);

		HashMap<String, String> map = converter.getAttributes();

		assertEquals("compound pathway maps are not equal", ref, map);

		// test compound pathways (sbml coded)
		ref.clear();
		ref.put("A__45__cpd", "(pathway::pathway--2)");
		ref.put("B__45__cpd", "(pathway--4::pathway3)");
		ref.put("C__45__cpd", "(pathway::pathway--2)");
		ref.put("D__45__cpd", "(pathway--4::pathway3)");

		converter = new PathwayAttributes(network, true, true);

		map = converter.getAttributes();

		assertEquals("compound pathway maps are not equal (sbml coded)", ref, map);

		// test reaction pathways

		ref.clear();
		ref.put("r1-rxn", "(pathway::pathway--2)");
		ref.put("r2-rxn", "(pathway--4::pathway3)");

		converter = new PathwayAttributes(network, false, false);

		map = converter.getAttributes();

		assertEquals("reaction pathway maps are not equal (sbml coded)", ref, map);
		
		// test reaction pathways (sbml coded)
		
		ref.clear();
		ref.put("r1__45__rxn", "(pathway::pathway--2)");
		ref.put("r2__45__rxn", "(pathway--4::pathway3)");

		converter = new PathwayAttributes(network, true, false);

		map = converter.getAttributes();

		assertEquals("reaction pathway maps are not equal (sbml coded)", ref, map);

	}

}
