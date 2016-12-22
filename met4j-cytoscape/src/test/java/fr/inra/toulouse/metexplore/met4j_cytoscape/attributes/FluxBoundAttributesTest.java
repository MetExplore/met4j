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
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Flux;

/**
 * @author lcottret
 *
 */
public class FluxBoundAttributesTest {

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_cytoscape.attributes.FluxBoundAttributes#getAttributes()}.
	 */
	@Test
	public void testGetAttributes() {

		BioNetwork network = new BioNetwork();

		BioUnitDefinition ud = new BioUnitDefinition();

		BioChemicalReaction r1 = new BioChemicalReaction("r1-rxn");
		r1.setLowerBound(new Flux("-10", ud));
		r1.setUpperBound(new Flux("10", ud));
		BioChemicalReaction r2 = new BioChemicalReaction("r2-rxn");
		r2.setLowerBound(new Flux("-20", ud));
		r2.setUpperBound(new Flux("20", ud));

		network.addBiochemicalReaction(r1);
		network.addBiochemicalReaction(r2);

		HashMap<String, String> ref = new HashMap<String, String>();

		ref.put("r1-rxn", "-10");
		ref.put("r2-rxn", "-20");

		FluxBoundAttributes converter = new FluxBoundAttributes(network, false, true);

		HashMap<String, String> map = converter.getAttributes();

		assertEquals("lb maps are not equal", ref, map);

		ref.clear();
		ref.put("r1-rxn", "10");
		ref.put("r2-rxn", "20");

		converter = new FluxBoundAttributes(network, false, false);

		map = converter.getAttributes();

		assertEquals("ub maps are not equal", ref, map);
		
		ref.clear();
		ref.put("r1__45__rxn", "10");
		ref.put("r2__45__rxn", "20");
		
		
		converter = new FluxBoundAttributes(network, true, false);

		map = converter.getAttributes();

		assertEquals("ub maps are not equal (sbml coded", ref, map);
		

	}

}
