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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc;

import static org.junit.Assert.*;

import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.GeneSetException;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;

public class GeneSetTest {

	@Test
	public void testToString() throws GeneSetException {

		BioGene g1 = new BioGene("g1");
		BioGene g2 = new BioGene("g2");
		BioGene g3 = new BioGene("g3");
		
		GeneSet set = new GeneSet();
		
		set.add(g3.getId());
		
		assertEquals("g3", set.toString());
		
		set.add(g1.getId());
		
		assertEquals("g1 AND g3", set.toString());
		
		set.add(g2.getId());
		
		assertEquals("g1 AND g2 AND g3", set.toString());

	}
	
	@Test
	public void testEquals() throws GeneSetException {
		
		BioGene g1 = new BioGene("g1");
		BioGene g2 = new BioGene("g2");
		
		BioGene g1Bis = new BioGene("g1");
		BioGene g2Bis = new BioGene("g2");
		
		GeneSet set = new GeneSet();
		set.add(g1.getId());
		set.add(g2.getId());
		
		GeneSet setBis = new GeneSet();
		setBis.add(g1Bis.getId());
		setBis.add(g2Bis.getId());
		
		//assertEquals(set.hashCode(), setBis.hashCode());
		
		assertEquals(set, setBis);
		
		
		
		
	}

}
