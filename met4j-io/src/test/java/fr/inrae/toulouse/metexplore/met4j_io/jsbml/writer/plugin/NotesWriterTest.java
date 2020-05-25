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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.BionetworkToJsbml;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.*;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageWriterException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;

public class NotesWriterTest {

    public SBMLDocument doc;
    Model model;
    BioNetwork network;
    BioReaction r1, r2;
    Reaction rSbml1, rSbml2;

    BioMetabolite m1, m2;
    BioCompartment c1, c2;

    Species s1, s3;
    Compartment compartSbml1, compartSbml2;

    NotesWriter writer;

    @Before
    public void init() throws JSBMLPackageWriterException, Met4jSbmlWriterException {

        doc = new SBMLDocument(3, 2);
        model = doc.createModel();

        network = new BioNetwork();

        NetworkAttributes.setNotes(network,
                new Notes("<body xmlns=\"http://www.w3.org/1999/xhtml\"><p>this a note</p></body>"));

        BioRef refR1 = new BioRef("origin1", "dbName1", "id1", 1);

        r1 = new BioReaction("r1");
        r2 = new BioReaction("r2");

        r1.addRef(refR1);

        r1.setEcNumber("1.2.3.4");

        r2 = new BioReaction("r2");

        Set<Integer> pmids = new HashSet<Integer>();

        pmids.add(12345);
        pmids.add(23456);

        ReactionAttributes.setPmids(r1, pmids);

        ReactionAttributes.setLowerBound(r1, new Flux(15.0));
        ReactionAttributes.setUpperBound(r1, new Flux(20.0));

        network.add(r1);
        network.add(r2);

        BioPathway pathway1 = new BioPathway("p1", "pathway 1");
        BioPathway pathway2 = new BioPathway("p2", "pathway 2");

        network.add(pathway1);
        network.add(pathway2);

        ReactionAttributes.setScore(r1, 2.0);

        network.affectToPathway(pathway1, r1);
        network.affectToPathway(pathway2, r1);

        m1 = new BioMetabolite("m1");
        m2 = new BioMetabolite("m2");

        MetaboliteAttributes.setNotes(m1,
                new Notes("<body xmlns=\"http://www.w3.org/1999/xhtml\"><p>this a metabolite note</p></body>"));

        m1.setCharge(3);
        m1.setChemicalFormula("CH6");
        m1.setSmiles("sMILE");

        BioRef refR2 = new BioRef("origin2", "dbName2", "id2", 2);
        m1.addRef(refR2);

        m1.setInchi("inchi1");

        MetaboliteAttributes.setPubchem(m1, "puchem123");

        MetaboliteAttributes.setPmids(m1, pmids);

        network.add(m1);
        network.add(m2);

        NetworkAttributes.addUnitDefinition(network, new BioUnitDefinition());

        c1 = new BioCompartment("c1");
        c2 = new BioCompartment("c2");

        c1.addRef(refR1);

        network.add(c1);
        network.add(c2);

        network.affectToCompartment(c1, m1);
        network.affectToCompartment(c2, m2);

        BioGene g1 = new BioGene("g1");
        BioGene g2 = new BioGene("g2");
        BioGene g3 = new BioGene("g3");

        network.add(g1);
        network.add(g2);
        network.add(g3);

        BioProtein p1 = new BioProtein("p1");
        BioProtein p2 = new BioProtein("p2");
        BioProtein p3 = new BioProtein("p3");

        network.add(p1);
        network.add(p2);
        network.add(p3);

        network.affectGeneProduct(p1, g1);
        network.affectGeneProduct(p2, g2);
        network.affectGeneProduct(p3, g3);

        BioEnzyme e1 = new BioEnzyme("e1");
        BioEnzyme e2 = new BioEnzyme("e2");

        network.add(e1);
        network.add(e2);

        network.affectSubUnit(e1, 1.0, p1);
        network.affectSubUnit(e1, 1.0, p2);
        network.affectSubUnit(e2, 1.0, p3);

        network.affectEnzyme(r1, e1);
        network.affectEnzyme(r1, e2);
        network.affectEnzyme(r2, e2);

        BionetworkToJsbml converter = new BionetworkToJsbml();
        writer = new NotesWriter(true);
        converter.addPackage(writer);

        model = converter.parseBioNetwork(network);

        doc.setModel(model);

        SBMLWriter writer = new SBMLWriter();
        writer.setIndentationChar('\t');
        writer.setIndentationCount((short) 1);

        System.err.println("Writing file...");

        try {
            System.err.println(writer.writeSBMLToString(doc));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testNotesModel() throws XMLStreamException {

        assertEquals(
                "<notes>" + NetworkAttributes.getNotes(network).getXHTMLasString().replaceAll("\\s", "") + "</notes>",
                model.getNotesString().replaceAll("\\s", ""));

    }

    @Test
    public void testMetaboliteNotes() throws XMLStreamException {

        Species s1 = model.getSpecies("m1");

        assertNotNull(s1);

        assertTrue(s1.getNotesString().contains("<p>this a metabolite note</p>"));
        assertTrue(s1.getNotesString().contains("<p>formula: CH6</p>"));
        assertTrue(s1.getNotesString().contains("<p>charge: 3</p>"));
        assertTrue(s1.getNotesString().contains("<p>inchi: inchi1</p>"));
        assertTrue(s1.getNotesString().contains("<p>pubchem.compound: puchem123</p>"));
        assertTrue(s1.getNotesString().contains("<p>smiles: sMILE</p>"));
        assertTrue(s1.getNotesString().contains("<p>dbName2: id2</p>"));

    }

    @Test
    public void testReactionNotes() throws XMLStreamException {

        Reaction reaction1 = model.getReaction("r1");

        assertNotNull(reaction1);

        assertTrue(reaction1.getNotesString().contains("<p>EC_NUMBER: 1.2.3.4</p>"));
        assertTrue(reaction1.getNotesString().contains("<p>pmids: 12345,23456</p>"));
        assertTrue(reaction1.getNotesString().contains("GENE_ASSOCIATION: ( g1 AND g2 ) OR ( g3 )"));
        assertTrue(reaction1.getNotesString().contains("<p>dbName1: id1</p>"));
        assertTrue(reaction1.getNotesString().contains("<p>SUBSYSTEM: pathway 1 || pathway 2</p>"));
        assertTrue(reaction1.getNotesString().contains("<p>score: 2.0</p>"));

    }

    @Test
    public void testCompartmentNotes() throws XMLStreamException {

        Compartment c1 = model.getCompartment("c1");

        assertNotNull(c1);

        System.err.println(c1.getNotesString());

        assertTrue(c1.getNotesString().contains("<p>dbName1: id1</p>"));


    }


}
