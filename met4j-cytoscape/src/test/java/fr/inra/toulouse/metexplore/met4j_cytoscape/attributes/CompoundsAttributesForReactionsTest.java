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

import java.io.IOException;
import java.util.HashMap;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;

/**
 * @author lcottret
 *
 */
public class CompoundsAttributesForReactionsTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_cytoscape.attributes.CompoundsAttributesForReactions#CompoundsAttributesForReactions(BioNetwork, String, Boolean, Boolean)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGetAttributes() throws IOException {

		BioNetwork network = new BioNetwork();

		BioChemicalReaction r1 = new BioChemicalReaction("r1-rxn");

		BioPhysicalEntity a = new BioPhysicalEntity("A-cpd");
		BioPhysicalEntity b = new BioPhysicalEntity("B-cpd");
		BioPhysicalEntity c = new BioPhysicalEntity("C-cpd");
		BioPhysicalEntity d = new BioPhysicalEntity("D-cpd");

		r1.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(b));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(c));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(d));

		BioChemicalReaction r2 = new BioChemicalReaction("r2-rxn");

		r2.addRightParticipant(new BioPhysicalEntityParticipant(a));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(c));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(d));

		network.addBiochemicalReaction(r1);
		network.addBiochemicalReaction(r2);

		HashMap<String, String> ref_sub = new HashMap<String, String>();
		ref_sub.put("r1-rxn", "(A-cpd::B-cpd)");
		ref_sub.put("r2-rxn", "(C-cpd::D-cpd)");

		HashMap<String, String> ref_prod = new HashMap<String, String>();
		ref_prod.put("r2-rxn", "(A-cpd::B-cpd)");
		ref_prod.put("r1-rxn", "(C-cpd::D-cpd)");

		// test substrates
		CompoundsAttributesForReactions converter = new CompoundsAttributesForReactions(network, true, false);

		HashMap<String, String> map = converter.getAttributes();

		assertEquals("substrate maps are not equal", ref_sub, map);

		// test products
		converter = new CompoundsAttributesForReactions(network, false, false);

		map = converter.getAttributes();

		assertEquals("product maps are not equal", ref_prod, map);
		
		// test sbml coding
		ref_prod.clear();
		ref_prod.put("r2__45__rxn", "(A-cpd::B-cpd)");
		ref_prod.put("r1__45__rxn", "(C-cpd::D-cpd)");
		
		converter = new CompoundsAttributesForReactions(network, false, true);

		map = converter.getAttributes();
		
		assertEquals("product maps are not equal (sbml coding)", ref_prod, map);

	}

}