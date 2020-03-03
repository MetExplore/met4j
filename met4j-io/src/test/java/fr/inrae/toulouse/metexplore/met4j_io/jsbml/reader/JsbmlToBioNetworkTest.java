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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.CompartmentType;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.SpeciesType;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.ASTNode.Type;
import org.sbml.jsbml.CVTerm.Qualifier;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.BioCompartmentType;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reactant.ReactantAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.AnnotationParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;

public class JsbmlToBioNetworkTest {

	public SBMLDocument doc;
	Model model;
	JsbmlToBioNetwork parser;
	Compartment c1, c2, c3;
	Species m1, m2, m3;
	Reaction r1, r2, r3;
	SpeciesType type1, type2, type3;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void init() throws XMLStreamException, JSBMLPackageReaderException {

		doc = new SBMLDocument(3, 1);

		initModel();

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new FBCParser(), new AnnotationParser(true)));

		parser.setPackages(pkgs);

	}

	private void initModel() {

		model = doc.createModel();

		model.setId("modelId");
		model.setName("modelName");

		c1 = model.createCompartment("c1");
		c1.setName("compartment1");
		c2 = model.createCompartment("c2");
		c2.setName("compartment2");
		c3 = model.createCompartment("c3");

		CompartmentType compartmentType = new CompartmentType("cType");
		model.addCompartmentType(compartmentType);
		c1.setCompartmentType(compartmentType);

		c1.setOutside(c2);
		c2.setOutside(c1);

		c1.setSize(2.0);

		c1.setSpatialDimensions(4.0);

		c1.setName("test");

		m1 = model.createSpecies("m1", "name1", c1);
		m2 = model.createSpecies("m2", "name2", c2);
		m3 = model.createSpecies("m3");
		m3.setCompartment(c1);

		m1.setConstant(true);
		m2.setConstant(false);

		m1.setInitialAmount(2.0);
		m2.setInitialAmount(3.0);

		if (model.getLevel() < 3) {
			m1.setCharge(3);
			m2.setCharge(4);

			type1 = model.createSpeciesType("type1");
			type1.setSBOTerm(1234567);
			
			type2 = model.createSpeciesType("type2");
			Annotation annotation = new Annotation();
			CVTerm cvterm = new CVTerm();
			cvterm.addResource("urn.miriam.obo.go#GO%3A1234567");
			cvterm.setQualifierType(org.sbml.jsbml.CVTerm.Type.BIOLOGICAL_QUALIFIER);
			cvterm.setBiologicalQualifierType(Qualifier.BQB_IS);
			annotation.addCVTerm(cvterm);
			type2.setAnnotation(annotation);

			type3 = model.createSpeciesType("type3");
			try {
				type3.setNotes(
						"<notes>\n" + "<body xmlns=\"http://www.w3.org/1999/xhtml\"><p>Attr:val</p></body></notes>");
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			m1.setSpeciesType(type1);
			m2.setSpeciesType(type2);
			m3.setSpeciesType(type3);

		}

		r1 = model.createReaction("r1");
		r1.setName("name1");
		r1.setReversible(false);

		if (model.getLevel() > 2) {
			r1.setSBOTerm("SBO:0000167");
		}

		r1.setFast(true);

		r2 = model.createReaction("r2");

		SpeciesReference m1Ref = new SpeciesReference(m1);
		m1Ref.setStoichiometry(2.0);

		SpeciesReference m1RefBis = new SpeciesReference(m1);
		m1RefBis.setStoichiometry(3.0);

		SpeciesReference m2Ref = new SpeciesReference(m2);

		if (model.getLevel() > 2) {
			m1Ref.setConstant(true);
			m2Ref.setConstant(false);
		}

		r1.addReactant(m1Ref);
		r1.addProduct(m2Ref);
		r1.addProduct(m1RefBis);

		r3 = model.createReaction("r3");
	}

	@Test
	public void testParseNetworkData() throws JSBMLPackageReaderException, Met4jSbmlReaderException {

		parser.parseModel();

		assertEquals(model.getId(), parser.getNetwork().getId());

	}

	@Test
	public void testParseListOfUnitDefinitions() throws Met4jSbmlReaderException {

		UnitDefinition testUd1 = new UnitDefinition("testUd1");
		testUd1.addUnit(Kind.SECOND);
		testUd1.addUnit(Kind.AVOGADRO);

		UnitDefinition testUd2 = new UnitDefinition("testUd2");
		testUd2.setName("test");
		testUd2.addUnit(Kind.DIMENSIONLESS);

		model.addUnitDefinition(testUd1);
		model.addUnitDefinition(testUd2);

		parser.parseModel();

		BioUnitDefinitionCollection unitDefs = NetworkAttributes.getUnitDefinitions(parser.getNetwork());

		assertNotNull(unitDefs);

		assertEquals(2, unitDefs.size());

		BioUnitDefinition unitDef = unitDefs.get("testUd1");

		assertNotNull(unitDef);

		assertEquals(2, unitDef.getUnits().size());

		assertNotNull(unitDef.getUnits().get(Kind.AVOGADRO.getName()));

	}

	@Test
	public void testParseListOfCompartments() throws Met4jSbmlReaderException {

		UnitDefinition testUd1 = new UnitDefinition("testUd");
		testUd1.addUnit(Kind.METRE);

		model.addUnitDefinition(testUd1);

		c1.setUnits(testUd1);

		parser.parseModel();

		Set<String> testIds = new HashSet<String>();
		testIds.add("c1");
		testIds.add("c2");
		testIds.add("c3");

		assertEquals(testIds, parser.getNetwork().getCompartmentsView().getIds());

		BioCompartment c1Test = parser.getNetwork().getCompartmentsView().get("c1");
		BioCompartment c2Test = parser.getNetwork().getCompartmentsView().get("c2");
		BioCompartment c3Test = parser.getNetwork().getCompartmentsView().get("c3");

		assertNotNull(c1Test);
		assertNotNull(c2Test);
		assertNotNull(c3Test);

		assertEquals(c1.getName(), c1Test.getName() );
		assertEquals(c2.getName(), c2Test.getName() );
		assertEquals(c3.getId(), c3Test.getName() );

		// test outside compartment

		BioCompartment cOutside = CompartmentAttributes.getOutsideCompartment(c1Test);
		assertEquals(c2Test, cOutside);

		cOutside = CompartmentAttributes.getOutsideCompartment(c2Test);
		assertEquals(c1Test, cOutside);

		// test units
		BioUnitDefinition u = CompartmentAttributes.getUnitDefinition(c1Test);

		assertNotNull(u);

		assertTrue(u.getUnits().containsKey(Kind.METRE.getName()));

		// test size
		Double size = CompartmentAttributes.getSize(c1Test);
		assertEquals(2.0, size, 0.0);

		// test spatial dimensions
		int dims = CompartmentAttributes.getSpatialDimensions(c1Test);
		assertEquals(4, dims);

	}

	@Test
	public void testCompartmentsLevel2() throws JSBMLPackageReaderException, Met4jSbmlReaderException {
		doc.setLevel(2);
		doc.setVersion(2);

		initModel();

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new AnnotationParser(true)));

		parser.setPackages(pkgs);

		parser.parseModel();

		// Test compartment type
		BioCompartment compartment1 = parser.getNetwork().getCompartmentsView().get("c1");

		BioCompartmentType type = CompartmentAttributes.getType(compartment1);

		assertNotNull(type);

		assertEquals("cType", type.getId());

		compartment1 = parser.getNetwork().getCompartmentsView().get("c2");

		type = CompartmentAttributes.getType(compartment1);

		assertNull(type);

	}

	@Test
	public void testParseListOfSpeciesLevel2() throws Met4jSbmlReaderException, JSBMLPackageReaderException, XMLStreamException {

		doc.setLevel(2);
		doc.setVersion(2);

		initModel();

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new AnnotationParser(true)));

		parser.setPackages(pkgs);

		parser.parseModel();

		BioMetabolite metabolite1 = parser.getNetwork().getMetabolitesView().get("m1");
		BioMetabolite metabolite2 = parser.getNetwork().getMetabolitesView().get("m2");
		BioMetabolite metabolite3 = parser.getNetwork().getMetabolitesView().get("m3");

		assertNotNull(metabolite1);
		assertNotNull(metabolite2);
		assertNotNull(metabolite3);

		assertEquals(3, metabolite1.getCharge(), 0);
		assertEquals(4, metabolite2.getCharge(), 0);
		assertEquals(0, metabolite3.getCharge(), 0);
		
		String sboTerm = MetaboliteAttributes.getSboTerm(metabolite1);
		assertEquals(type1.getSBOTermID(), sboTerm);
		
		SbmlAnnotation annot = MetaboliteAttributes.getAnnotation(metabolite2);
		assertEquals(type2.getAnnotationString(), annot.getXMLasString());
		
		Notes notes = MetaboliteAttributes.getNotes(metabolite3);
		assertEquals(type3.getNotes().toXMLString(), notes.getXHTMLasString());


	}

	@Test
	public void testParseListOfSpecies() throws Met4jSbmlReaderException {

		parser.parseModel();

		Set<String> testIds = new HashSet<String>();
		testIds.add("m1");
		testIds.add("m2");
		testIds.add("m3");

		assertEquals(testIds, parser.getNetwork().getMetabolitesView().getIds());

		Set<String> testNames = new HashSet<String>();
		testNames.add("name1");
		testNames.add("name2");
		testNames.add("m3");

		assertEquals(testNames,
				parser.getNetwork().getMetabolitesView().stream().map(x -> x.getName()).collect(Collectors.toSet()));

		BioMetabolite metabolite1 = parser.getNetwork().getMetabolitesView().get("m1");
		BioMetabolite metabolite2 = parser.getNetwork().getMetabolitesView().get("m2");
		BioMetabolite metabolite3 = parser.getNetwork().getMetabolitesView().get("m3");

		assertNotNull(metabolite1);
		assertNotNull(metabolite2);
		assertNotNull(metabolite3);

		assertTrue(MetaboliteAttributes.getConstant(metabolite1));
		assertFalse(MetaboliteAttributes.getConstant(metabolite2));
		assertFalse(MetaboliteAttributes.getConstant(metabolite3));

		assertEquals(m1.getInitialAmount(), MetaboliteAttributes.getInitialAmount(metabolite1), 0.0);
		assertEquals(m2.getInitialAmount(), MetaboliteAttributes.getInitialAmount(metabolite2), 0.0);
		assertNull(MetaboliteAttributes.getInitialAmount(metabolite3));

		assertTrue(parser.getNetwork().getCompartmentsView().get("c1").getComponentsView().containsId("m1"));
		assertTrue(parser.getNetwork().getCompartmentsView().get("c2").getComponentsView().containsId("m2"));

		m1.setInitialConcentration(2.0);
		m2.setInitialConcentration(3.0);

		// Level 3 : the charge is not an attribute
		assertEquals(0, metabolite1.getCharge(), 0);
		assertEquals(0, metabolite2.getCharge(), 0);
		assertEquals(0, metabolite3.getCharge(), 0);

		// We reparse the model because a specie can't have initial amount AND initial
		// concentration
		parser.parseModel();

		metabolite1 = parser.getNetwork().getMetabolitesView().get("m1");
		metabolite2 = parser.getNetwork().getMetabolitesView().get("m2");
		metabolite3 = parser.getNetwork().getMetabolitesView().get("m3");

		assertEquals(m1.getInitialConcentration(), MetaboliteAttributes.getInitialConcentration(metabolite1), 0.0);
		assertEquals(m2.getInitialConcentration(), MetaboliteAttributes.getInitialConcentration(metabolite2), 0.0);
		assertNull(MetaboliteAttributes.getInitialConcentration(metabolite3));

	}

	@Test
	public void testParseListOfReactions() throws Met4jSbmlReaderException {

		parser.parseModel();

		assertEquals(3, parser.getNetwork().getReactionsView().size());

		Set<String> testIds = new HashSet<String>();
		testIds.add("r1");
		testIds.add("r2");
		testIds.add("r3");

		assertEquals(testIds, parser.getNetwork().getReactionsView().getIds());

		Set<String> testNames = new HashSet<String>();
		testNames.add("name1");
		testNames.add("r2");
		testNames.add("r3");

		assertEquals(testNames,
				parser.getNetwork().getReactionsView().stream().map(x -> x.getName()).collect(Collectors.toSet()));

		BioReaction reaction1 = parser.getNetwork().getReactionsView().get("r1");
		BioReaction reaction2 = parser.getNetwork().getReactionsView().get("r2");
		BioReaction reaction3 = parser.getNetwork().getReactionsView().get("r3");

		assertNotNull(reaction1);
		assertNotNull(reaction2);
		assertNotNull(reaction3);

		assertFalse(reaction1.isReversible());
		assertTrue(reaction2.isReversible());
		assertTrue(reaction3.isReversible());

		assertEquals(r1.getListOfReactants().size(), parser.getNetwork().getLeftReactants(reaction1).size());
		assertEquals(r1.getListOfProducts().size(), parser.getNetwork().getRightReactants(reaction1).size());

		testIds.clear();
		testIds.add("m1");

		assertEquals(testIds, parser.getNetwork().getLeftReactants(reaction1).stream()
				.map(x -> x.getPhysicalEntity().getId()).collect(Collectors.toSet()));

		testIds.add("m2");

		assertEquals(testIds, parser.getNetwork().getRightReactants(reaction1).stream()
				.map(x -> x.getPhysicalEntity().getId()).collect(Collectors.toSet()));

		Set<Double> testCoeffs = new HashSet<Double>();
		testCoeffs.add(1.0);
		testCoeffs.add(3.0);

		assertEquals(testCoeffs, parser.getNetwork().getRightReactants(reaction1).stream().map(x -> x.getQuantity())
				.collect(Collectors.toSet()));

		BioReactant m1Ref = parser.getNetwork().getLeftReactants(reaction1).stream()
				.filter((x) -> x.getPhysicalEntity().getId().compareTo("m1") == 0).findFirst().orElse(null);

		assertNotNull(m1Ref);

		assertTrue(ReactantAttributes.getConstant(m1Ref));

		BioReactant m2Ref = parser.getNetwork().getRightReactants(reaction1).stream()
				.filter((x) -> x.getPhysicalEntity().getId().compareTo("m2") == 0).findFirst().orElse(null);

		assertNotNull(m2Ref);

		assertFalse(ReactantAttributes.getConstant(m2Ref));

		BioReactant m1RefBis = parser.getNetwork().getRightReactants(reaction1).stream()
				.filter((x) -> x.getPhysicalEntity().getId().compareTo("m1") == 0).findFirst().orElse(null);

		assertNotNull(m1RefBis);

		assertFalse(ReactantAttributes.getConstant(m1RefBis));

		// Test sbo term
		String sboTerm = ReactionAttributes.getSboTerm(reaction1);

		assertNotNull(sboTerm);
		assertEquals("SBO:0000167", sboTerm);

		// Test fast
		Boolean fast = ReactionAttributes.getFast(reaction1);
		assertNotNull(fast);
		assertTrue(fast);

		fast = ReactionAttributes.getFast(reaction2);
		assertFalse(fast);

	}

	@Test
	public void testInvalidPackageLevel2() throws JSBMLPackageReaderException {
		doc.setLevel(2);

		model = doc.createModel();

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new FBCParser(), new AnnotationParser(true)));

		exception.expect(JSBMLPackageReaderException.class);
		parser.setPackages(pkgs);
	}

	@Test
	public void testFluxLevel2() throws JSBMLPackageReaderException, Met4jSbmlReaderException {
		doc.setLevel(2);
		doc.setVersion(2);

		initModel();

		UnitDefinition ud = model.createUnitDefinition();
		ud.setId(BioUnitDefinition.DEFAULT_UNIT);

		KineticLaw law = r1.createKineticLaw();

		ASTNode ciNode = new ASTNode(Type.NAME);
		ciNode.setName("FLUX_VALUE");
		law.setMath(ciNode);

		Parameter lBound = new Parameter();
		lBound.setId("LOWER_BOUND");
		lBound.setValue(0.1);
		lBound.setUnits(StringUtils.convertToSID(BioUnitDefinition.DEFAULT_UNIT));

		law.addParameter(lBound);

		Parameter uBound = new Parameter();
		uBound.setId("UPPER_BOUND");
		uBound.setValue(150.0);
		uBound.setUnits(StringUtils.convertToSID(BioUnitDefinition.DEFAULT_UNIT));

		law.addParameter(uBound);

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new AnnotationParser(true)));

		parser.setPackages(pkgs);

		parser.parseModel();

		BioReaction reaction1 = parser.getNetwork().getReactionsView().get("r1");

		Flux lb = ReactionAttributes.getLowerBound(reaction1);

		assertNotNull(lb);

		assertEquals(0.1, lb.value, 0.0);

		Flux ub = ReactionAttributes.getUpperBound(reaction1);

		assertNotNull(ub);

		assertEquals(150.0, ub.value, 0.0);

	}

	@Test
	public void testFluxLevel3WithoutandWithFbc() throws JSBMLPackageReaderException, Met4jSbmlReaderException {
		doc.setLevel(3);

		initModel();

		UnitDefinition ud = model.createUnitDefinition();
		ud.setId(BioUnitDefinition.DEFAULT_UNIT);

		KineticLaw law = r1.createKineticLaw();

		ASTNode ciNode = new ASTNode(Type.NAME);
		ciNode.setName("FLUX_VALUE");
		law.setMath(ciNode);

		LocalParameter lBound = law.createLocalParameter();
		lBound.setId("LOWER_BOUND");
		lBound.setValue(0.1);
		lBound.setUnits(StringUtils.convertToSID(BioUnitDefinition.DEFAULT_UNIT));

		LocalParameter UBound = law.createLocalParameter();
		UBound.setId("UPPER_BOUND");
		UBound.setValue(150.0);
		UBound.setUnits(StringUtils.convertToSID(BioUnitDefinition.DEFAULT_UNIT));

		KineticLaw law2 = r2.createKineticLaw();

		ASTNode ciNode2 = new ASTNode(Type.NAME);
		ciNode2.setName("FLUX_VALUE");
		law2.setMath(ciNode2);

		LocalParameter flux = law2.createLocalParameter();
		flux.setId("FLUX");
		flux.setName("FLUX");
		flux.setValue(10.0);
		flux.setUnits(StringUtils.convertToSID(BioUnitDefinition.DEFAULT_UNIT));

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new AnnotationParser(true)));

		parser.setPackages(pkgs);

		parser.parseModel();

		BioReaction reaction1 = parser.getNetwork().getReactionsView().get("r1");

		Flux lb = ReactionAttributes.getLowerBound(reaction1);

		assertNotNull(lb);

		assertEquals(0.1, lb.value, 0.0);

		Flux ub = ReactionAttributes.getUpperBound(reaction1);

		assertNotNull(ub);

		assertEquals(150.0, ub.value, 0.0);

		BioReaction reaction2 = parser.getNetwork().getReactionsView().get("r2");

		Flux f = ReactionAttributes.getFlux(reaction2, "FLUX");

		assertNotNull(f);
		assertEquals(flux.getValue(), f.value, 0.0);

		// We test that the flux in the notes are not read when the FBC parser is used
		pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new FBCParser(), new AnnotationParser(true)));

		parser.setPackages(pkgs);

		parser.parseModel();

		reaction1 = parser.getNetwork().getReactionsView().get("r1");

		lb = ReactionAttributes.getLowerBound(reaction1);

		assertNull(lb);

	}

}
