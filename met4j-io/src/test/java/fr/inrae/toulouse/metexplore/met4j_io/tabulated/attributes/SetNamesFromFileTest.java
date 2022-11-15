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

public class SetNamesFromFileTest {

    private BioNetwork network;
    private BioReaction reaction;
    private BioMetabolite metabolite;
    private BioProtein protein;
    private BioGene gene;
    private BioPathway pathway;

    private BioCompartment compartment;

    @Before
    public void init() {
        network = new BioNetwork();
        reaction = new BioReaction("r");
        metabolite = new BioMetabolite("m");
        protein = new BioProtein("p");
        gene = new BioGene("g");
        pathway = new BioPathway("pathway");
        compartment = new BioCompartment("cpt");

        network.add(reaction, metabolite, protein, gene, pathway, compartment);


    }


    @Test
    public void testAttribute() {
        SetNamesFromFile setNamesFromFile = new SetNamesFromFile(0, 1, network, "", "", 0, false, false, EntityType.REACTION);

        assertTrue(setNamesFromFile.testAttribute("anything"));
    }

    @Test
    public void setAttributesReaction() throws IOException {

        SetNamesFromFile setNamesFromFile = Mockito.spy(new SetNamesFromFile(0, 1, network, "", "", 0, false, false, EntityType.REACTION));
        Mockito.doReturn(true).when(setNamesFromFile).parseAttributeFile();

        String line = "r\tmyreaction\n";

        Boolean flag = setNamesFromFile.parseLine(line, 1);

        assertTrue(flag);

        setNamesFromFile.setAttributes();

        assertEquals("myreaction", reaction.getName());

    }

    @Test
    public void setAttributesMetabolite() throws IOException {

        SetNamesFromFile setNamesFromFile = Mockito.spy(new SetNamesFromFile(0, 1, network, "", "", 0, false, false, EntityType.METABOLITE));
        Mockito.doReturn(true).when(setNamesFromFile).parseAttributeFile();

        String line = "m\tmymetabolite\n";

        Boolean flag = setNamesFromFile.parseLine(line, 1);

        assertTrue(flag);

        setNamesFromFile.setAttributes();

        assertEquals("mymetabolite", metabolite.getName());

    }

    @Test
    public void setAttributesProtein() throws IOException {

        SetNamesFromFile setNamesFromFile = Mockito.spy(new SetNamesFromFile(0, 1, network, "", "", 0, false, false, EntityType.PROTEIN));
        Mockito.doReturn(true).when(setNamesFromFile).parseAttributeFile();

        String line = "p\tmyprotein\n";

        Boolean flag = setNamesFromFile.parseLine(line, 1);

        assertTrue(flag);

        setNamesFromFile.setAttributes();

        assertEquals("myprotein", protein.getName());

    }

    @Test
    public void setAttributesGene() throws IOException {

        SetNamesFromFile setNamesFromFile = Mockito.spy(new SetNamesFromFile(0, 1, network, "", "", 0, false, false, EntityType.GENE));
        Mockito.doReturn(true).when(setNamesFromFile).parseAttributeFile();

        String line = "g\tmygene\n";

        Boolean flag = setNamesFromFile.parseLine(line, 1);

        assertTrue(flag);

        setNamesFromFile.setAttributes();

        assertEquals("mygene", gene.getName());

    }

    @Test
    public void setAttributesPathway() throws IOException {

        SetNamesFromFile setNamesFromFile = Mockito.spy(new SetNamesFromFile(0, 1, network, "", "", 0, false, false, EntityType.PATHWAY));
        Mockito.doReturn(true).when(setNamesFromFile).parseAttributeFile();

        String line = "pathway\tmypathway\n";

        Boolean flag = setNamesFromFile.parseLine(line, 1);

        assertTrue(flag);

        setNamesFromFile.setAttributes();

        assertEquals("mypathway", pathway.getName());

    }

    @Test
    public void setAttributesCompartment() throws IOException {

        SetNamesFromFile setNamesFromFile = Mockito.spy(new SetNamesFromFile(0, 1, network, "", "", 0, false, false, EntityType.COMPARTMENT));
        Mockito.doReturn(true).when(setNamesFromFile).parseAttributeFile();

        String line = "cpt\tcompartment\n";

        Boolean flag = setNamesFromFile.parseLine(line, 1);

        assertTrue(flag);

        setNamesFromFile.setAttributes();

        assertEquals("compartment", compartment.getName());

    }



}