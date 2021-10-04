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

package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import static org.junit.Assert.*;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioNetworkUtilsTest {

    @Test
    public void testGetChokeReactions() {

        BioNetwork network = new BioNetwork("model");

        BioReaction r1 = new BioReaction("R1");
        BioReaction r2 = new BioReaction("R2");

        BioMetabolite m1 = new BioMetabolite("m1");
        BioMetabolite m2 = new BioMetabolite("m2");
        BioMetabolite m3 = new BioMetabolite("m3");

        BioCompartment c = new BioCompartment("c");

        network.add(r1, r2, m1, m2, m3, c);

        network.affectToCompartment(c, m1, m2, m3);

        network.affectLeft(r1, 1.0, c, m1);
        network.affectLeft(r1, 1.0, c, m2);

        network.affectLeft(r2, 1.0, c, m1);

        network.affectRight(r1, 1.0, c, m3);

        network.affectRight(r2, 1.0, c, m3);

        BioCollection<BioReaction> chokeReactions = BioNetworkUtils.getChokeReactions(network);

        assertEquals(1, chokeReactions.size());

        assertTrue(chokeReactions.contains(r1));


    }

    @Test
    public void removeNotConnectedMetabolites() {

        BioNetwork network = new BioNetwork();

        BioReaction r1 = new BioReaction("R1");

        BioMetabolite m1 = new BioMetabolite("m1");
        BioMetabolite m2 = new BioMetabolite("m2");
        BioMetabolite m3 = new BioMetabolite("m3");

        BioCompartment c = new BioCompartment("c");

        network.add(r1, m1, m2, m3, c);

        network.affectToCompartment(c, m1, m2, m3);

        network.affectLeft(r1, 1.0, c, m1);
        network.affectLeft(r1, 1.0, c, m2);

        BioNetworkUtils.removeNotConnectedMetabolites(network);

        assertEquals(2, network.getMetabolitesView().size());

        assertFalse(network.contains(m3));
        assertTrue(network.contains(m2));
        assertTrue(network.contains(m1));


    }

    @Test
    public void deepCopy() {

        BioNetwork originalNetwork = new BioNetwork("ori");

        BioReaction r1 = new BioReaction("R1");
        BioReaction r2 = new BioReaction("R2");
        BioMetabolite m1 = new BioMetabolite("M1");
        BioMetabolite m2 = new BioMetabolite("M2");
        BioMetabolite m3 = new BioMetabolite("M3");
        BioMetabolite m4 = new BioMetabolite("M4");
        BioCompartment c1 = new BioCompartment("c1");
        BioPathway pathway1 = new BioPathway("pathway1");
        BioEnzyme enzyme1 = new BioEnzyme("enz1");
        BioProtein protein1 = new BioProtein("protein1");
        BioGene gene1 = new BioGene("gene1");

        originalNetwork.add(r1, r2, m1, m2, m3, m4, c1, pathway1, protein1, gene1, enzyme1);
        originalNetwork.affectToPathway(pathway1, r1, r2);
        originalNetwork.affectToCompartment(c1, m1, m2, m3, m4);
        originalNetwork.affectGeneProduct(protein1, gene1);
        originalNetwork.affectSubUnit(enzyme1, 1.0, protein1);
        originalNetwork.affectLeft(r1, 2.0, c1, m1);
        originalNetwork.affectRight(r1, 1.0, c1, m2);
        originalNetwork.affectLeft(r2, 1.0, c1, m3);
        originalNetwork.affectRight(r2, 2.0, c1, m4);

        BioNetwork newNetwork = BioNetworkUtils.deepCopy(originalNetwork);

        originalNetwork.removeOnCascade(r1, r2, m1, m2, m3, m4, c1, pathway1, protein1, gene1, enzyme1);

        assertEquals(2, newNetwork.getReactionsView().size());
        assertEquals(4, newNetwork.getMetabolitesView().size());
        assertEquals(1, newNetwork.getCompartmentsView().size());
        assertEquals(1, newNetwork.getPathwaysView().size());
        assertEquals(1, newNetwork.getEnzymesView().size());
        assertEquals(1, newNetwork.getProteinsView().size());
        assertEquals(1, newNetwork.getGenesView().size());

        r1.setName("newName");
        assertNotEquals(r1.getName(), newNetwork.getReactionsView().get("R1").getName());

        m1.setName("newName");
        assertNotEquals(m1.getName(), newNetwork.getMetabolitesView().get("M1").getName());

        c1.setName("newName");
        assertNotEquals(c1.getName(), newNetwork.getCompartmentsView().get("c1").getName());

        pathway1.setName("newName");
        assertNotEquals(pathway1.getName(), newNetwork.getPathwaysView().get("pathway1").getName());

        enzyme1.setName("newName");
        assertNotEquals(enzyme1.getName(), newNetwork.getEnzymesView().get("enz1").getName());

        protein1.setName("newName");
        assertNotEquals(protein1.getName(), newNetwork.getProteinsView().get("protein1").getName());

        gene1.setName("newName");
        assertNotEquals(gene1.getName(), newNetwork.getGenesView().get("gene1").getName());

        assertEquals(r1.getLeftsView().size(),
                newNetwork.getReactionsView().get("R1").getLeftsView().size());

        assertEquals(r1.getRightsView().size(),
                newNetwork.getReactionsView().get("R1").getLeftsView().size());

        assertEquals(2, newNetwork.getReactionsFromPathways(newNetwork.getPathwaysView().get("pathway1")).size());

        assertEquals(enzyme1.getParticipantsView().size(),
                newNetwork.getEnzymesView().get("enz1").getParticipantsView().size());


    }

}
