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

package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils;
import fr.inra.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;

public class NotesParserTest {

	public SBMLDocument doc;
	Model model;
	BioNetwork network;
	NotesParser parser;

	@Before
	public void init() throws XMLStreamException {

		doc = new SBMLDocument(3, 1);

		model = doc.createModel();

		model.setNotes("<body xmlns=\"http://www.w3.org/1999/xhtml\"><p>this a note</p></body>");

		network = new BioNetwork();

	}

	@Test
	public void testAddNetworkNotes() {

		parser = new NotesParser(true);

		parser.parseModel(model, network);

		Notes notes = NetworkAttributes.getNotes(network);

		assertEquals(
				"<notes>\n  <body xmlns=\"http://www.w3.org/1999/xhtml\">\n    <p>this a note</p></body>\n</notes>",
				notes.getXHTMLasString());

	}

	@Test
	public void testAddCompartmentNotes() throws XMLStreamException {

		Compartment c1 = model.createCompartment("c1");

		c1.setNotes(
				"<body xmlns=\"http://www.w3.org/1999/xhtml\"><p>Attribut1 : value1</p><p>Attribut2:value2</p></body>");

		BioCompartment comp1 = new BioCompartment("c1");

		network.add(comp1);

		parser = new NotesParser(true);

		parser.othersAsRefs = true;

		parser.parseModel(model, network);

		Notes notes = CompartmentAttributes.getNotes(comp1);

		assertEquals(
				"<notes>\n  <body xmlns=\"http://www.w3.org/1999/xhtml\">\n    <p>Attribut1 : value1</p><p>Attribut2:value2</p></body>\n</notes>",
				notes.getXHTMLasString());

		assertEquals(2, comp1.getRefs().size());

		Set<BioRef> refs = comp1.getRefs().get("attribut1");
		assertNotNull(refs);
		assertEquals(1, refs.size());
		BioRef ref = refs.iterator().next();
		assertEquals(ref.dbName, "attribut1");
		assertEquals(ref.id, "value1");

	}

	@Test
	public void testAddReactionNotes() throws XMLStreamException {

		Reaction r1 = model.createReaction("r1");

		String notesStr = "<body xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+ "    <p>Attribut1 : value1</p><p>EC-NUMBER: 1.1.1.1</p>" + "<p>PMID: 10000,12323,PMID: 12</p>"
				+ "<p>SUBSYSTEM: Pathway1 || Pathway2</p>" + "<p>SCORE: 1</p>" + "<p>STATUS: Not defined</p>"
				+ "<p>COMMENTS: comment</p>" + "<p>GENE ASSOCIATION: (G1 and G2 or G2 and G3) and (G4 AND G5 or (G6) or (G4 AND G7))</p>\n" + "  </body>";

		r1.setNotes(notesStr);

		BioReaction reaction1 = new BioReaction("r1");
		network.add(reaction1);

		parser = new NotesParser(true);

		parser.othersAsRefs = true;

		parser.parseModel(model, network);

		Notes notes = ReactionAttributes.getNotes(reaction1);

		assertNotNull(notes);

		assertEquals("<notes>\n  " + notesStr + "\n</notes>", notes.getXHTMLasString());

		assertEquals("1.1.1.1", reaction1.getEcNumber());

		assertEquals(2, network.getPathwaysView().size());

		BioPathway p1 = network.getPathwaysView().get("Pathway1");

		assertNotNull(p1);

		assertEquals(1, network.getReactionsFromPathway(p1).size());

		assertEquals("r1", network.getReactionsFromPathway(p1).iterator().next().getId());

		assertEquals(7, reaction1.getRefs().size());

		assertNotNull(ReactionAttributes.getStatus(reaction1));

		assertEquals("Not defined", ReactionAttributes.getStatus(reaction1));

		System.err.println(" ReactionAttributes.getScore(reaction1)" + ReactionAttributes.getScore(reaction1));

		assertNotNull(ReactionAttributes.getScore(reaction1));

		assertEquals(1, ReactionAttributes.getScore(reaction1), 0.0);

		assertNotNull(ReactionAttributes.getPmids(reaction1));

		assertEquals(3, ReactionAttributes.getPmids(reaction1).size());

		assertNotNull(ReactionAttributes.getComment(reaction1));

		assertEquals("comment", ReactionAttributes.getComment(reaction1));

		assertEquals("( G1 AND G2 AND G4 AND G5 ) OR ( G1 AND G2 AND G4 AND G7 ) OR ( G1 AND G2 AND G6 ) OR " +
				"( G2 AND G3 AND G4 AND G5 ) OR ( G2 AND G3 AND G4 AND G7 ) OR ( G2 AND G3 AND G6 )",
				BioReactionUtils.getGPR(network, reaction1, false));

		assertNotNull(reaction1.getRefs().get("attribut1"));

	}

	@Test
	public void testComputeGeneAssociation() throws XMLStreamException {

		Reaction r1 = model.createReaction("r1");

		String notesStr = "<body xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+ "<p>GENE ASSOCIATION: (G1 and G2) OR G4 AND G5</p>\n" + "  </body>";

		r1.setNotes(notesStr);

		BioReaction reaction1 = new BioReaction("r1");
		network.add(reaction1);

		parser = new NotesParser(true);

		parser.othersAsRefs = true;

		parser.parseModel(model, network);

		assertEquals("( G1 AND G2 ) OR ( G4 AND G5 )",
				BioReactionUtils.getGPR(network, reaction1, false));

	}

	@Test
	public void testComputeGeneAssociation2() throws XMLStreamException {
		Reaction r1 = model.createReaction("r1");
		String notesStr = "<body xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+ "<p>GENE ASSOCIATION: G4 AND G5 OR (G6) OR ( G4 AND G7)</p>\n" + "  </body>";

		r1.setNotes(notesStr);

		BioReaction reaction1 = new BioReaction("r1");
		network.add(reaction1);

		parser = new NotesParser(true);

		parser.othersAsRefs = true;

		parser.parseModel(model, network);

		assertEquals("( G4 AND G5 ) OR ( G4 AND G7 ) OR ( G6 )",
				BioReactionUtils.getGPR(network, reaction1, false));
	}

	@Test
	public void testAddMetaboliteNotes() throws XMLStreamException {

		Species m1 = model.createSpecies("m1");

		String notesStr = "<body xmlns=\"http://www.w3.org/1999/xhtml\">\n"
				+ "    <p>Attribut1 : value1</p><p>Formula: C5H403</p>" + "<p>CHARGE: 3</p>"
				+ "<p>INCHI: InChI=inchiCode</p>" + "<p>SMILES: smilesCode</p>\n" + "  </body>";
		
		m1.setNotes(notesStr);
		
		BioMetabolite metabolite1 = new BioMetabolite("m1");
		network.add(metabolite1);
		
		parser = new NotesParser(true);

		parser.othersAsRefs = true;

		parser.parseModel(model, network);
		
		Notes notes = MetaboliteAttributes.getNotes(metabolite1);
		
		assertNotNull(notes);

		assertEquals("<notes>\n  " + notesStr + "\n</notes>", notes.getXHTMLasString());

		assertEquals(5, metabolite1.getRefs().size());

		assertNotNull(metabolite1.getChemicalFormula());
		
		assertEquals("C5H403", metabolite1.getChemicalFormula());
		
		assertNotNull(metabolite1.getCharge());
		
		assertEquals(3, metabolite1.getCharge(), 0);
		
		assertNotNull(metabolite1.getInchi());
		
		assertEquals("inchiCode", metabolite1.getInchi());
		
		assertNotNull(metabolite1.getSmiles());
		
		assertEquals("smilesCode", metabolite1.getSmiles());

		assertNotNull(metabolite1.getRefs().get("attribut1"));

	}

}
