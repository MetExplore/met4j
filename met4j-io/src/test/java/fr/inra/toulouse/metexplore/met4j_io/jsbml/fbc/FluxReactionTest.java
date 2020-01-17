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

package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;

public class FluxReactionTest {

	@Test
	public void testConvertGeneAssociationstoComplexes() {

		BioNetwork network = new BioNetwork();

		BioGene g1 = new BioGene("g1");

		BioReaction r1 = new BioReaction("r1");
		
		network.add(r1);
		
		GeneSet set1 = new GeneSet();
		set1.add(g1);
		
		GeneAssociation a1 = new GeneAssociation();
		a1.add(set1);
		
		FluxReaction f1 = new FluxReaction(r1);
		
		f1.setReactionGeneAssociation(a1);
		f1.convertGeneAssociationstoComplexes(network);
		
		Set<String> geneIds = new HashSet<String>();
		geneIds.add("g1");
		
		assertTrue(network.getProteinsView().getIds().contains("g1"));
		assertTrue(network.getEnzymesView().getIds().contains("g1"));
		assertTrue(network.getReactionsFromGenes(geneIds, true).contains(r1));
		
		BioGene g2 = new BioGene("g2");
		set1.add(g2);
		
		f1.convertGeneAssociationstoComplexes(network);
		geneIds.add("g2");
		
		assertTrue(network.getProteinsView().getIds().contains("g2"));
		assertTrue(network.getEnzymesView().getIds().contains("g1_AND_g2"));
		assertTrue(network.getReactionsFromGenes(geneIds, true).contains(r1));
		
		
		
		

	}

}
