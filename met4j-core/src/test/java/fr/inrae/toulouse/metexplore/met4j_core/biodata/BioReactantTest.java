/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import org.junit.Test;


public class BioReactantTest {
	

	@Test
	public void testToString() {
		
		// Test with an integer
		BioReactant reactant = new BioReactant(new BioMetabolite("cpdId"), 1.0, new BioCompartment("cptId"));
		assertEquals("reactant badly formatted (integer coefficient)", "1 cpdId[cptId]", reactant.toString());
		
		// Test if a double is well rounded
		reactant = new BioReactant(new BioMetabolite("cpdId"), 1.26666, new BioCompartment("cptId"));
		assertEquals("reactant badly formatted (integer coefficient)", "1.27 cpdId[cptId]", reactant.toString());
		
		// Test when the biocompartment is null
		reactant = new BioReactant(new BioMetabolite("cpdId"), 1.0, null);
		assertEquals("reactant badly formatted (integer coefficient)", "1 cpdId", reactant.toString());
		
	}


	@Test
	public void testEquality() {
	
		BioMetabolite c1 = new BioMetabolite("c1");
		BioCompartment cpt1 = new BioCompartment("cpt1");
		BioMetabolite c2 = new BioMetabolite("c2");
		BioCompartment cpt2 = new BioCompartment("cpt2");
		
		BioReactant r1 = new BioReactant(c1, 1.0, cpt1);
		BioReactant r2 = new BioReactant(c1, 2.0, cpt1);
		BioReactant r3 = new BioReactant(c1, 1.0, cpt2);
		BioReactant r4 = new BioReactant(c2, 1.0, cpt1);
		BioReactant r5 = new BioReactant(c1, 1.0, cpt1);
		
		assertFalse("r1 and r2 must not be equal", r1.equals(r2));
		assertFalse("r1 and r3 must not be equal", r1.equals(r3));
		assertFalse("r1 and r4 must not be equal", r1.equals(r4));
		assertTrue("r1 and r5 must be equal", r1.equals(r5));
		
	}


}
