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
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;

/**
 * @author lcottret
 *
 */
public class GPRAttributesTest {

	/**
	 * Test method for {@link fr.inra.toulouse.metexplore.met4j_cytoscape.attributes.GPRAttributes#getAttributes()}.
	 */
	@Test
	public void testGetAttributes() {
		
		BioNetwork network = new BioNetwork();

		
		BioChemicalReaction r1 = new BioChemicalReaction("r1-rxn", "R1");
		
		// We don't test here complex gpr, it will be done in unit test of BioChemicalReactions
		BioProtein p = new BioProtein("p1", "P1");
		BioGene g = new BioGene("g1", "gene1");
		p.addGene(g);
		

		BioPhysicalEntity a = new BioPhysicalEntity("A-cpd", "A';");
		BioPhysicalEntity b = new BioPhysicalEntity("B-cpd", "B");
		BioPhysicalEntity c = new BioPhysicalEntity("C-cpd", "C");
		BioPhysicalEntity d = new BioPhysicalEntity("D-cpd", "D");
		
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(c));

		r1.addEnz(p);
		
		BioChemicalReaction r2 = new BioChemicalReaction("r2-rxn", "R2&lt;i&gt;ase&lt;/i&gt;");

		r2.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(d));

		network.addBiochemicalReaction(r1);
		network.addBiochemicalReaction(r2);
		
		HashMap<String, String> ref = new HashMap<String, String>();
		ref.put("r1-rxn", "( gene1 )");
		ref.put("r2-rxn", "");
		
		GPRAttributes converter = new GPRAttributes(network, false, true);
		HashMap<String, String> map = converter.getAttributes();
		
		assertEquals("gene association maps are not equal", ref, map);
		
		ref.clear();
		
		ref.put("r1-rxn", "( P1 )");
		ref.put("r2-rxn", "");
		
		converter = new GPRAttributes(network, false, false);
		map = converter.getAttributes();
		
		assertEquals("protein association maps are not equal", ref, map);
		
		ref.clear();
		ref.put("r1__45__rxn", "( P1 )");
		ref.put("r2__45__rxn", "");
		
		converter = new GPRAttributes(network, true, false);
		map = converter.getAttributes();
		
		assertEquals("protein association maps are not equal (SBML coded)", ref, map);
		
		
		
		
	}

}
