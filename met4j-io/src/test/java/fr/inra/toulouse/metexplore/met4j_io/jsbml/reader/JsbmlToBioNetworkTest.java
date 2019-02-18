package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.AnnotationParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;

public class JsbmlToBioNetworkTest {

	public SBMLDocument doc;
	Model model;
	JsbmlToBioNetwork parser;
	Compartment c1, c2;
	Species m1, m2;
	Reaction r1, r2;

	@Before
	public void init() throws XMLStreamException, JSBMLPackageReaderException {

		doc = new SBMLDocument(3, 1);

		model = doc.createModel();
		model.setId("modelId");
		model.setName("modelName");

		c1 = model.createCompartment("c1");
		c2 = model.createCompartment("c2");

		m1 = model.createSpecies("m1", "name1", c1);
		m2 = model.createSpecies("m2", "name2", c2);

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new FBCParser(), new AnnotationParser(true)));

		parser.setPackages(pkgs);

	}

	@Test
	public void testParseNetworkData() throws JSBMLPackageReaderException {

		parser.parseModel();

		assertEquals(model.getId(), parser.getNetwork().getId());

	}

	@Test
	public void testParseListOfUnitDefinitions() {

		UnitDefinition testUd1 = new UnitDefinition("testUd1");
		testUd1.addUnit(Kind.SECOND);
		testUd1.addUnit(Kind.AVOGADRO);

		UnitDefinition testUd2 = new UnitDefinition("testUd2");
		testUd2.addUnit(Kind.DIMENSIONLESS);

		model.addUnitDefinition(testUd1);
		model.addUnitDefinition(testUd2);

		parser.parseModel();

		BioUnitDefinitionCollection unitDefs = NetworkAttributes.getUnitDefinitions(parser.getNetwork());

		assertNotNull(unitDefs);

		assertEquals(2, unitDefs.size());

		BioUnitDefinition unitDef = unitDefs.getEntityFromId("testUd1");

		assertNotNull(unitDef);

		assertEquals(2, unitDef.getUnits().size());

		assertNotNull(unitDef.getUnits().get(Kind.AVOGADRO.getName()));

	}

	@Test
	public void testParseListOfCompartments() {

		parser.parseModel();

		Set<String> testIds = new HashSet<String>();
		testIds.add("c1");
		testIds.add("c2");

		assertEquals(testIds, parser.getNetwork().getCompartmentsView().getIds());

	}

	@Test
	public void testParseListOfSpecies() {

		parser.parseModel();

		Set<String> testIds = new HashSet<String>();
		testIds.add("m1");
		testIds.add("m2");

		assertEquals(testIds, parser.getNetwork().getMetabolitesView().getIds());

		Set<String> testNames = new HashSet<String>();
		testNames.add("name1");
		testNames.add("name2");

		assertEquals(testNames,
				parser.getNetwork().getMetabolitesView().stream().map(x -> x.getName()).collect(Collectors.toSet()));

		assertTrue(parser.getNetwork().getCompartmentsView().getEntityFromId("c1").getComponents().containsId("m1"));
		assertTrue(parser.getNetwork().getCompartmentsView().getEntityFromId("c2").getComponents().containsId("m2"));

	}

	@Test
	public void testParseListOfReactions() {

		r1 = model.createReaction("r1");
		r1.setName("name1");
		r1.setReversible(false);

		r2 = model.createReaction("r2");
		r2.setReversible(true);

		SpeciesReference m1Ref = new SpeciesReference(m1);
		m1Ref.setStoichiometry(2.0);

		SpeciesReference m1RefBis = new SpeciesReference(m1);
		m1RefBis.setStoichiometry(3.0);

		SpeciesReference m2Ref = new SpeciesReference(m2);

		r1.addReactant(m1Ref);
		r1.addProduct(m2Ref);
		r1.addProduct(m1RefBis);

		parser.parseModel();

		assertEquals(2, parser.getNetwork().getReactionsView().size());

		Set<String> testIds = new HashSet<String>();
		testIds.add("r1");
		testIds.add("r2");

		assertEquals(testIds, parser.getNetwork().getReactionsView().getIds());

		Set<String> testNames = new HashSet<String>();
		testNames.add("name1");
		testNames.add("r2");

		assertEquals(testNames,
				parser.getNetwork().getReactionsView().stream().map(x -> x.getName()).collect(Collectors.toSet()));

		BioReaction reaction1 = parser.getNetwork().getReactionsView().getEntityFromId("r1");

		assertNotNull(reaction1);

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
		
		assertEquals(testCoeffs, parser.getNetwork().getRightReactants(reaction1).stream()
				.map(x -> x.getQuantity()).collect(Collectors.toSet()));
		
		// TODO test  flux bounds sans le package FBC et avec model version = 2

	}

}
