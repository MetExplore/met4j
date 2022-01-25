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

public class SetRefsFromFileTest {


    private BioNetwork network;
    private BioReaction reaction;
    private BioMetabolite metabolite;
    private BioProtein protein;
    private BioPathway pathway;
    private BioGene gene;

    @Before
    public void init() {
        network = new BioNetwork();
        reaction = new BioReaction("r");
        metabolite = new BioMetabolite("m");
        protein = new BioProtein("p");
        pathway = new BioPathway("pathway");
        gene = new BioGene("g");

        network.add(reaction, metabolite, protein, pathway, gene);
    }
    
    @Test
    public void testAttribute() {
        SetRefsFromFile setRefsFromFile = new SetRefsFromFile(0, 1, network, "", "", 0, false, false, "refTest", EntityType.REACTION);
        assertTrue(setRefsFromFile.testAttribute("anything"));
    }

    @Test
    public void setAttributesReaction() throws IOException {
        SetRefsFromFile setRefsFromFile = Mockito.spy(new SetRefsFromFile(0, 1, network, "", "", 0, false, false, "refTest", EntityType.REACTION));
        Mockito.doReturn(true).when(setRefsFromFile).parseAttributeFile();

        String line = "r\trefValue";
        Boolean flag = setRefsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setRefsFromFile.setAttributes();
        System.err.println(reaction.getRefs());
        BioRef refRef = new BioRef("attributeTable", "refTest", "refValue", 1);

        assertTrue(reaction.getRefs().get("refTest").contains(refRef));

    }

    @Test
    public void setAttributesMetabolite() throws IOException {
        SetRefsFromFile setRefsFromFile = Mockito.spy(new SetRefsFromFile(0, 1, network, "", "", 0, false, false, "refTest", EntityType.METABOLITE));
        Mockito.doReturn(true).when(setRefsFromFile).parseAttributeFile();

        String line = "m\trefValue";
        Boolean flag = setRefsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setRefsFromFile.setAttributes();
        assertTrue(metabolite.getRefs().containsKey("refTest"));
        BioRef refRef = new BioRef("attributeTable", "refTest", "refValue", 1);
        assertTrue(metabolite.getRefs().get("refTest").contains(refRef));
    }

    @Test
    public void setAttributesGene() throws IOException {
        SetRefsFromFile setRefsFromFile = Mockito.spy(new SetRefsFromFile(0, 1, network, "", "", 0, false, false, "refTest", EntityType.GENE));
        Mockito.doReturn(true).when(setRefsFromFile).parseAttributeFile();

        String line = "g\trefValue";
        Boolean flag = setRefsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setRefsFromFile.setAttributes();
        assertTrue(gene.getRefs().containsKey("refTest"));
        BioRef refRef = new BioRef("attributeTable", "refTest", "refValue", 1);
        assertTrue(gene.getRefs().get("refTest").contains(refRef));
    }

    @Test
    public void setAttributesProtein() throws IOException {
        SetRefsFromFile setRefsFromFile = Mockito.spy(new SetRefsFromFile(0, 1, network, "", "", 0, false, false, "refTest", EntityType.PROTEIN));
        Mockito.doReturn(true).when(setRefsFromFile).parseAttributeFile();

        String line = "p\trefValue";
        Boolean flag = setRefsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setRefsFromFile.setAttributes();
        System.err.println(protein.getRefs());
        assertTrue(protein.getRefs().containsKey("refTest"));
        BioRef refRef = new BioRef("attributeTable", "refTest", "refValue", 1);
        assertTrue(protein.getRefs().get("refTest").contains(refRef));
    }

    @Test
    public void setAttributesPathway() throws IOException {
        SetRefsFromFile setRefsFromFile = Mockito.spy(new SetRefsFromFile(0, 1, network, "", "", 0, false, false, "refTest", EntityType.PATHWAY));
        Mockito.doReturn(true).when(setRefsFromFile).parseAttributeFile();

        String line = "pathway\trefValue";
        Boolean flag = setRefsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setRefsFromFile.setAttributes();
        assertTrue(pathway.getRefs().containsKey("refTest"));
        BioRef refRef = new BioRef("attributeTable", "refTest", "refValue", 1);
        assertTrue(pathway.getRefs().get("refTest").contains(refRef));
    }

}