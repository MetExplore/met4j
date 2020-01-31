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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.CVTerm.Qualifier;
import org.sbml.jsbml.CVTerm.Type;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef;

public class AnnotationParserTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	Model model;
	BioNetwork network;
	BioReaction r1, r2;
	BioMetabolite s1, s2, p1, p2;
	BioProtein prot1, prot2;
	BioCompartment cpt;
	BioEnzyme e1, e2;
	Annotation genericAnnotation, pubmedAnnotation, inchiAnnotation, ecAnnotation, modelAnnotation;

	@Before
	public void init() throws XMLStreamException {

		network = new BioNetwork();

		r1 = new BioReaction("r1");
		r2 = new BioReaction("r2");

		s1 = new BioMetabolite("s1");
		s2 = new BioMetabolite("s2");
		p1 = new BioMetabolite("p1");
		p2 = new BioMetabolite("p2");
		cpt = new BioCompartment("cpt");

		network.add(r1);
		network.add(r2);
		network.add(s1);
		network.add(s2);
		network.add(p1);
		network.add(p2);
		network.add(cpt);

		network.affectToCompartment(cpt, s1, s2, p1, p2);

		network.affectLeft(r1, 2.0, cpt, s1);
		network.affectLeft(r1, 2.0, cpt, s2);
		network.affectRight(r1, 3.0, cpt, p1);
		network.affectRight(r1, 3.0, cpt, p2);

		BioPathway p = new BioPathway("pathway1");
		network.add(p);

		network.affectToPathway(p, r1);

		e1 = new BioEnzyme("e");
		network.add(e1);
		prot1 = new BioProtein("p1");
		network.add(prot1);
		prot2 = new BioProtein("p2");
		network.add(prot2);
		BioGene g1 = new BioGene("g1");
		network.add(g1);
		BioGene g2 = new BioGene("g2");
		network.add(g2);

		e2 = new BioEnzyme("e2");
		network.add(e2);

		network.affectGeneProduct(prot1, g1);
		network.affectGeneProduct(prot2, g2);

		network.affectSubUnit(e1, 1.0, prot1);
		network.affectSubUnit(e1, 1.0, prot2);

		network.affectSubUnit(e2, 1.0, prot1);

		network.affectEnzyme(e1, r1);
		network.affectEnzyme(e2, r1);

		model = new Model();
		model.addReaction(new Reaction("r1"));
		model.getReaction("r1").setMetaId("r1");
		model.addReaction(new Reaction("r2"));
		model.getReaction("r2").setMetaId("r2");
		model.addSpecies(new Species("s1"));
		model.getSpecies("s1").setMetaId("s1");

		genericAnnotation = new Annotation();
		CVTerm cvterm = new CVTerm();
		cvterm.addResource("http://identifiers.org/kegg.reaction/R00001");
		cvterm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
		cvterm.setBiologicalQualifierType(Qualifier.BQB_IS);
		genericAnnotation.addCVTerm(cvterm);

		pubmedAnnotation = new Annotation();
		CVTerm cvtermPubmed = new CVTerm();
		cvtermPubmed.addResource("http://identifiers.org/pubmed/1");
		cvtermPubmed.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
		cvtermPubmed.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
		CVTerm cvtermPubmed2 = new CVTerm();
		cvtermPubmed2.addResource("http://identifiers.org/pubmed/2");
		cvtermPubmed2.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
		cvtermPubmed2.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
		pubmedAnnotation.addCVTerm(cvtermPubmed);
		pubmedAnnotation.addCVTerm(cvtermPubmed2);

		CVTerm cvEc = new CVTerm();
		cvEc.addResource("http://identifiers.org/ec-code/1.1.1.1");
		cvEc.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
		cvEc.setBiologicalQualifierType(Qualifier.BQB_IS);
		genericAnnotation.addCVTerm(cvEc);

		model.getReaction("r1").setAnnotation(genericAnnotation);

		model.getReaction("r2").setAnnotation(pubmedAnnotation);

		model.getSpecies("s1")
				.appendAnnotation("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  "
						+ "xmlns:bqbiol=\"http://biomodels.net/biology-qualifiers/\"  >"
						+ "<rdf:Description rdf:about=\"#s1\"> <in:inchi xmlns:in=\"http://biomodels.net/inchi\">"
						+ "InChI=1/C7H8N2O/c1-9-4-2-3-6(5-9)7(8)10/h2-5H,1H3,(H-,8,10)/p+1</in:inchi></rdf:Description>"
						+ "<rdf:Description rdf:about=\"#s1\"> <in:inchi xmlns:in=\"http://biomodels.net/inchi\">"
						+ "InChI=truc</in:inchi></rdf:Description></rdf:RDF>");

	}

	@Test
	public void testParseModel() throws XMLStreamException {

		model.setMetaId("truc");
		
		modelAnnotation = new Annotation();
		CVTerm cvTermTaxon = new CVTerm();
		cvTermTaxon.addResource("http://identifiers.org/taxonomy/511145");
		cvTermTaxon.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
		cvTermTaxon.setBiologicalQualifierType(Qualifier.BQB_HAS_TAXON);
		CVTerm cvTermModel = new CVTerm();
		cvTermModel.addResource("http://identifiers.org/bigg.model/iJO1366");
		cvTermModel.setQualifierType(Type.MODEL_QUALIFIER);
		cvTermModel.setModelQualifierType(Qualifier.BQM_IS);
		CVTerm cvtermPubmed = new CVTerm();
		cvtermPubmed.addResource("http://identifiers.org/pubmed/1");
		cvtermPubmed.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
		cvtermPubmed.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
		
		modelAnnotation.addCVTerm(cvTermTaxon);
		modelAnnotation.addCVTerm(cvTermModel);
		modelAnnotation.addCVTerm(cvtermPubmed);
		
		model.setAnnotation(modelAnnotation);

		AnnotationParser parser = new AnnotationParser(true);

		parser.parseModel(model, network);

		BioRef refModel = new BioRef(AnnotationParser.ORIGIN, "taxonomy", "511145", 1);
		refModel.setLogicallink("hasTaxon");

		System.err.println(network.getRefs());
		
		assertNotNull(network.getRefs("taxonomy"));
		
		assertEquals(network.getRefs("taxonomy").iterator().next(), refModel);

		BioRef ref = new BioRef(AnnotationParser.ORIGIN, "kegg.reaction", "R00001", 1);

		assertEquals(r1.getRefs("kegg.reaction").iterator().next(), ref);

		assertEquals("1.1.1.1", r1.getEcNumber());

		BioRef ref2 = new BioRef(AnnotationParser.ORIGIN, "pubmed", "1", 1);
		ref2.setLogicallink("isDescribedBy");
		BioRef ref3 = new BioRef(AnnotationParser.ORIGIN, "pubmed", "2", 1);
		ref3.setLogicallink("isDescribedBy");

		Set<BioRef> pubmeds = new HashSet<BioRef>();
		pubmeds.add(ref2);
		pubmeds.add(ref3);

		HashMap<String, Set<BioRef>> refs = new HashMap<String, Set<BioRef>>();
		refs.put("pubmed", pubmeds);
		assertEquals(refs, r2.getRefs());

		BioRef ref4 = new BioRef(AnnotationParser.ORIGIN, "inchi",
				"1/C7H8N2O/c1-9-4-2-3-6(5-9)7(8)10/h2-5H,1H3,(H-,8,10)/p+1", 1);
		BioRef ref5 = new BioRef(AnnotationParser.ORIGIN, "inchi", "truc", 1);
		Set<BioRef> inchis = new HashSet<BioRef>();
		inchis.add(ref4);
		inchis.add(ref5);
		HashMap<String, Set<BioRef>> refInchis = new HashMap<String, Set<BioRef>>();
		refInchis.put("inchi", inchis);

		assertEquals(refInchis, s1.getRefs());

	}

}
