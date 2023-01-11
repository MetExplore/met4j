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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;

public class SetIdsFromFileTest {


    private BioNetwork network;

    private SetIdsFromFile setIdsFromFile;

    @Before
    public void init()  {
        network = new BioNetwork();
    }

    @Test
    public void setGeneIds() throws IOException {
        BioGene gene = new BioGene("g1");
        BioProtein protein = new BioProtein("p1");

        network.add(gene, protein);

        network.affectGeneProduct(protein, gene);

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.GENE,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        String line = "g1\tgene1\n";

        Boolean flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getGenesView().size());
        assertNotNull(network.getGene("gene1"));

        assertEquals(1, network.getProteinsView().size());

        // Test when the gene is already present
        network.add(new BioGene("g1"));

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.GENE,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getGenesView().size());


    }

    @Test
    public void setMetaboliteIds() throws IOException {
        BioMetabolite metabolite = new BioMetabolite("m1");
        BioCompartment cpt = new BioCompartment("cpt");
        BioReaction r1 = new BioReaction("r1");
        BioReaction r2 = new BioReaction("r2");
        BioEnzyme e = new BioEnzyme("e");

        network.add(metabolite, cpt, r1, r2, e);

        network.affectToCompartment(cpt, metabolite);
        network.affectLeft(r1, 1.0, cpt, metabolite);
        network.affectRight(r2, 1.0, cpt, metabolite);
        network.affectSubUnit(e, 1.0, metabolite);

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.METABOLITE,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        String line = "m1\tmetabolite1\n";

        Boolean flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getMetabolitesView().size());

        BioMetabolite newMetabolite = network.getMetabolite("metabolite1");

        assertNotNull(newMetabolite);

        BioCompartment newCpt = network.getCompartment("cpt");

        assertNotNull(newCpt);

        assertNotNull(newCpt.getComponentsView().get("metabolite1"));

        assertEquals(2, network.getReactionsView().size());

        assertEquals(1, r1.getLeftsView().size());
        assertTrue(r1.getLeftsView().contains(newMetabolite));

        assertEquals(1, r2.getRightsView().size());
        assertTrue(r2.getRightsView().contains(newMetabolite));

        assertEquals(1, network.getEnzymesView().size());
        assertTrue(e.getParticipantsView().stream().anyMatch(p -> p.getPhysicalEntity().equals(newMetabolite)));
        assertFalse(e.getParticipantsView().stream().anyMatch(p -> p.getPhysicalEntity().equals(metabolite)));

        // Test when the metabolite is already present
        network.add(new BioMetabolite("m1"));

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.METABOLITE,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getMetabolitesView().size());

    }

    @Test
    public void setPathwayIds() throws IOException {
        BioPathway pathway = new BioPathway("p1");
        BioReaction r1 = new BioReaction("r1");
        BioReaction r2 = new BioReaction("r2");

        network.add(pathway, r1, r2);
        network.affectToPathway(pathway, r1, r2);

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.PATHWAY,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        String line = "p1\tpathway1\n";

        Boolean flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getPathwaysView().size());

        BioPathway newPathway = network.getPathway("pathway1");
        assertNotNull(newPathway);
        assertEquals(2, network.getReactionsFromPathways(newPathway).size());

        // test when the pathway is already present
        network.add(new BioPathway("p1"));

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.PATHWAY,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getPathwaysView().size());

    }

    @Test
    public void setReactionIds() throws IOException {
        BioPathway pathway = new BioPathway("p1");
        BioReaction r1 = new BioReaction("r1");
        BioMetabolite metabolite = new BioMetabolite("m1");
        BioMetabolite metabolite2 = new BioMetabolite("m2");
        BioCompartment cpt = new BioCompartment("cpt");
        BioEnzyme e = new BioEnzyme("e");

        this.network.add(pathway, r1, metabolite, metabolite2, cpt, e);

        this.network.affectToPathway(pathway, r1);
        this.network.affectToCompartment(cpt, metabolite2, metabolite);
        this.network.affectLeft(r1, 1.0, cpt, metabolite);
        this.network.affectRight(r1, 1.0, cpt, metabolite2);
        this.network.affectEnzyme(r1, e);

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.REACTION,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        String line = "r1\treaction1\n";

        Boolean flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getReactionsView().size());

        BioReaction newReaction = network.getReaction("reaction1");

        assertNotNull(newReaction);

        assertEquals(1, network.getPathwaysView().size());

        assertEquals(1, network.getReactionsFromPathways(pathway).size());

        assertTrue(network.getReactionsFromPathways(pathway).contains(newReaction));

        assertTrue(r1.getEnzymesView().contains(e));

        assertEquals(1, newReaction.getLeftsView().size());
        assertEquals(1, newReaction.getRightsView().size());

        // Check if the reaction is already in the network
        this.network.add(new BioReaction("r1"));

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.REACTION,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, network.getReactionsView().size());

    }

    @Test
    public void setCompartmentIds() throws IOException {

        BioReaction r1 = new BioReaction("r1");
        BioMetabolite metabolite = new BioMetabolite("m1");
        BioMetabolite metabolite2 = new BioMetabolite("m2");
        BioCompartment cpt = new BioCompartment("cpt");

        this.network.add(r1, metabolite, metabolite2, cpt);

        this.network.affectToCompartment(cpt, metabolite2, metabolite);
        this.network.affectLeft(r1, 1.0, cpt, metabolite);
        this.network.affectRight(r1, 1.0, cpt, metabolite2);

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.COMPARTMENT,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        String line = "cpt\tcompartment\n";

        Boolean flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, this.network.getCompartmentsView().size());

        BioCompartment newCpt = this.network.getCompartment("compartment");

        assertNotNull(newCpt);

        assertEquals(2, newCpt.getComponentsView().size());

        assertEquals(1, this.network.getReactionsView().size());

        assertEquals(1, r1.getLeftsView().size());
        assertEquals(1, r1.getRightsView().size());

        assertTrue(r1.getLeftReactantsView().stream().allMatch(reactant -> reactant.getLocation().equals(newCpt)));
        assertTrue(r1.getRightReactantsView().stream().allMatch(reactant -> reactant.getLocation().equals(newCpt)));

        // Check if the compartment is already in the network
        this.network.add(new BioCompartment("cpt"));

        setIdsFromFile = Mockito.spy(new SetIdsFromFile(0, 1, network, "", "", 0, EntityType.COMPARTMENT,false, false));

        Mockito.doReturn(true).when(setIdsFromFile).parseAttributeFile();

        flag = setIdsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setIdsFromFile.setAttributes();

        assertEquals(1, this.network.getCompartmentsView().size());

    }
}