/*
 * Copyright INRAE (2022)
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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.gpr;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.MalformedGeneAssociationStringException;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPRTest {

    @Test
    public void createGPRfromString() throws MalformedGeneAssociationStringException {

        BioNetwork network = new BioNetwork();
        BioEnzyme e1 = new BioEnzyme("e1");
        BioEnzyme e2 = new BioEnzyme("e2");
        BioProtein p1 = new BioProtein("p1");
        BioProtein p2 = new BioProtein("p2");
        BioGene g1 = new BioGene("g1");
        BioGene g2 = new BioGene("g2");
        BioReaction r1 = new BioReaction("r1");

        network.add(e1, e2, p1, p2, g1, g2, r1);

        network.affectGeneProduct(p1, g1);
        network.affectSubUnit(e1, 1.0, p1);
        network.affectEnzyme(r1, e1);

        GPR.createGPRfromString(network, r1, "G3 AND G4");

        assertEquals(1, r1.getEnzymesView().size());

        assertEquals(2, network.getGenesFromReactions(r1).size());

        assertTrue(network.getGenesFromReactions(r1).containsId("G3"));
        assertTrue(network.getGenesFromReactions(r1).containsId("G4"));

    }
}