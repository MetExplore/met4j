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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.BionetworkToJsbml;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;

public class AnnotationWriterTest {

	public SBMLDocument doc;
	Model model;
	BioNetwork network;
	BioReaction r1, r2;
	Reaction rSbml1, rSbml2;

	BioMetabolite m1, m2;
	BioCompartment c1, c2;

	Species s1, s3;
	Compartment compartSbml1, compartSbml2;

	AnnotationWriter writer;

	@Before
	public void init() throws Met4jSbmlWriterException {

		doc = new SBMLDocument(3, 1);
		model = doc.createModel();

		network = new BioNetwork();

		r1 = new BioReaction("r1");

		BioRef refR1 = new BioRef("origin1", "kegg.reaction", "id1", 1);
		r1.addRef(refR1);
		
		r1.setEcNumber("1.2.3.4");

		r2 = new BioReaction("r2");

		network.add(r1);
		network.add(r2);

		m1 = new BioMetabolite("m1");

		BioRef refR2 = new BioRef("origin2", "kegg.reaction", "id2", 2);
		m1.addRef(refR2);

		m1.setInchi("inchi1");
		
		MetaboliteAttributes.setPubchem(m1, "puchem123");
		
		Set<Integer> pmids = new HashSet<Integer>();
		
		pmids.add(12345);
		pmids.add(23456);
		
		MetaboliteAttributes.setPmids(m1, pmids);
		
		ReactionAttributes.setPmids(r1, pmids);
		
		m2 = new BioMetabolite("m2");

		network.add(m1);
		network.add(m2);

		c1 = new BioCompartment("c1");
		BioRef refR3 = new BioRef("origin3", "bigg.compartment", "id3", 3);
		c1.addRef(refR3);

		c2 = new BioCompartment("c2");

		network.add(c1);
		network.add(c2);

		network.affectToCompartment(c1, m1);
		network.affectToCompartment(c2, m2);

		// Model annotation
		String annotationStr = "<annotation>\n"
				+ "  <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:vCard=\"http://www.w3.org/2001/vcard-rdf/3.0#\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:bqmodel=\"http://biomodels.net/model-qualifiers/\" xmlns:bqbiol=\"http://biomodels.net/biology-qualifiers/\">\n"
				+ "        <rdf:Description rdf:about=\"#_metarecon2\">\n" + "        <dc:creator>\n"
				+ "        <rdf:Bag>\n" + "        <rdf:li rdf:parseType=\"Resource\">\n"
				+ "        <vCard:N rdf:parseType=\"Resource\">\n" + "        <vCard:Family>Thiele</vCard:Family>\n"
				+ "        <vCard:Given>Ines</vCard:Given>\n" + "        </vCard:N>\n"
				+ "        <vCard:EMAIL>ines.thiele@gmail.com</vCard:EMAIL>\n"
				+ "        <vCard:ORG rdf:parseType=\"Resource\">\n"
				+ "        <vCard:Orgname>University of Iceland</vCard:Orgname>\n" + "        </vCard:ORG>\n"
				+ "        </rdf:li>\n" + "        <rdf:li rdf:parseType=\"Resource\">\n"
				+ "        <vCard:N rdf:parseType=\"Resource\">\n" + "        <vCard:Family>Swainston</vCard:Family>\n"
				+ "        <vCard:Given>Neil</vCard:Given>\n" + "        </vCard:N>\n"
				+ "        <vCard:EMAIL>neil.swainston@manchester.ac.uk</vCard:EMAIL>\n"
				+ "        <vCard:ORG rdf:parseType=\"Resource\">\n"
				+ "        <vCard:Orgname>University of Manchester</vCard:Orgname>\n" + "        </vCard:ORG>\n"
				+ "        </rdf:li>\n" + "        </rdf:Bag>\n" + "        </dc:creator>\n"
				+ "        <dcterms:created rdf:parseType=\"Resource\">\n"
				+ "        <dcterms:W3CDTF>2013-02-22T00:00:00Z</dcterms:W3CDTF>\n" + "        </dcterms:created>\n"
				+ "        <dcterms:modified rdf:parseType=\"Resource\">\n"
				+ "        <dcterms:W3CDTF>2013-08-06T07:32:47Z</dcterms:W3CDTF>\n" + "        </dcterms:modified>\n"
				+ "        <bqmodel:is>\n" + "        <rdf:Bag>\n"
				+ "        <rdf:li rdf:resource=\"http://identifiers.org/biomodels.db/MODEL1109130000\"/>\n"
				+ "        </rdf:Bag>\n" + "        </bqmodel:is>\n" + "        <bqbiol:occursIn>\n"
				+ "        <rdf:Bag>\n" + "        <rdf:li rdf:resource=\"http://identifiers.org/taxonomy/9606\"/>\n"
				+ "        </rdf:Bag>\n" + "        </bqbiol:occursIn>\n" + "        </rdf:Description>\n"
				+ "    </rdf:RDF>\n" + "</annotation>";
		SbmlAnnotation val = new SbmlAnnotation("val", annotationStr);
		network.setAttribute(GenericAttributes.SBML_ANNOTATION, val);

		BionetworkToJsbml converter = new BionetworkToJsbml();
		model = converter.parseBioNetwork(network);

		writer = new AnnotationWriter();
		writer.parseBionetwork(model, network);

	}

	@Test
	public void testModelAnnotation() throws XMLStreamException {

		assertEquals(NetworkAttributes.getAnnotation(network).getXMLasString(), model.getAnnotationString());

	}

	@Test
	public void testMetaboliteAnnotation() throws XMLStreamException {

		Species s1 = model.getSpecies("m1");

		assertNotNull(s1);

		assertTrue(s1.getAnnotationString().contains("http://identifiers.org/kegg.reaction/id2"));

	}

	@Test
	public void testMetaboliteInchi() throws XMLStreamException {

		Species s1 = model.getSpecies("m1");

		assertNotNull(s1);

		assertTrue(s1.getAnnotationString()
				.contains("<in:inchi xmlns:in=\"http://biomodels.net/inchi\">InChI=inchi1</in:inchi>"));
		
	}
	
	@Test
	public void testMetabolitePubchem() throws XMLStreamException {
		
		Species s1 = model.getSpecies("m1");

		assertNotNull(s1);
		
		assertTrue(s1.getAnnotationString()
				.contains("<rdf:li rdf:resource=\"http://identifiers.org/pubchem.compound/puchem123\"/>"));
		
	}
	
	@Test
	public void testMetabolitePmids() throws XMLStreamException {
		
		Species s1 = model.getSpecies("m1");

		assertNotNull(s1);
		
		assertTrue(s1.getAnnotationString()
				.contains("<rdf:li rdf:resource=\"http://identifiers.org/pubmed/23456\"/>"));
		
		assertTrue(s1.getAnnotationString()
				.contains("<rdf:li rdf:resource=\"http://identifiers.org/pubmed/12345\"/>"));
		
	}
	
	@Test
	public void testReactionAnnotation() throws XMLStreamException {

		Reaction r1 = model.getReaction("r1");

		assertNotNull(r1);

		assertTrue(r1.getAnnotationString().contains("http://identifiers.org/kegg.reaction/id1"));

	}
	
	@Test
	public void testReactionEc() throws XMLStreamException {

		Reaction r1 = model.getReaction("r1");

		assertNotNull(r1);

		assertTrue(r1.getAnnotationString().contains("http://identifiers.org/ec-code/1.2.3.4"));

	}
	
	@Test
	public void testReactionPmids() throws XMLStreamException {

		Reaction r1 = model.getReaction("r1");

		assertNotNull(r1);

		assertTrue(r1.getAnnotationString()
				.contains("<rdf:li rdf:resource=\"http://identifiers.org/pubmed/23456\"/>"));
		
		assertTrue(r1.getAnnotationString()
				.contains("<rdf:li rdf:resource=\"http://identifiers.org/pubmed/12345\"/>"));
	}
	

}
