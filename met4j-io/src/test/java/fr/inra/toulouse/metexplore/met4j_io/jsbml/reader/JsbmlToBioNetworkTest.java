package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

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
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.ASTNode.Type;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.annotations.compartment.BioCompartmentType;
import fr.inra.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.Flux;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.AnnotationParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;

public class JsbmlToBioNetworkTest {

	public SBMLDocument doc;
	Model model;
	JsbmlToBioNetwork parser;
	Compartment c1, c2, c3;
	Species m1, m2, m3;
	Reaction r1, r2, r3;

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

		System.err.println("model :" + model.getLevel() + " - " + model.getVersion());

		c1 = model.createCompartment("c1");
		c2 = model.createCompartment("c2");
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
		m3 = model.createSpecies("m3", "name3", c2);
		
		m1.setConstant(true);
		m2.setConstant(false);

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

		BioUnitDefinition unitDef = unitDefs.getEntityFromId("testUd1");

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

		BioCompartment c1 = parser.getNetwork().getCompartmentsView().getEntityFromId("c1");
		BioCompartment c2 = parser.getNetwork().getCompartmentsView().getEntityFromId("c2");
		BioCompartment c3 = parser.getNetwork().getCompartmentsView().getEntityFromId("c3");

		assertNotNull(c1);
		assertNotNull(c2);
		assertNotNull(c3);

		// test outside compartment

		BioCompartment cOutside = CompartmentAttributes.getOutsideCompartment(c1);
		assertEquals(c2, cOutside);

		cOutside = CompartmentAttributes.getOutsideCompartment(c2);
		assertEquals(c1, cOutside);

		// test units
		BioUnitDefinition u = CompartmentAttributes.getUnitDefinition(c1);

		assertNotNull(u);

		assertTrue(u.getUnits().containsKey(Kind.METRE.getName()));

		// test size
		Double size = CompartmentAttributes.getSize(c1);
		assertEquals(2.0, size, 0.0);

		// test spatial dimensions
		int dims = CompartmentAttributes.getSpatialDimensions(c1);
		assertEquals(4, dims);

	}

	@Test
	public void testCompartmentsLevel2() throws JSBMLPackageReaderException, Met4jSbmlReaderException {
		doc.setLevel(2);

		initModel();

		parser = new JsbmlToBioNetwork(model);

		ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new AnnotationParser(true)));

		parser.setPackages(pkgs);

		parser.parseModel();

		// Test compartment type
		BioCompartment compartment1 = parser.getNetwork().getCompartmentsView().getEntityFromId("c1");

		BioCompartmentType type = CompartmentAttributes.getType(compartment1);

		assertNotNull(type);

		assertEquals("cType", type.getId());

		compartment1 = parser.getNetwork().getCompartmentsView().getEntityFromId("c2");

		type = CompartmentAttributes.getType(compartment1);

		assertNull(type);

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
		testNames.add("name3");

		assertEquals(testNames,
				parser.getNetwork().getMetabolitesView().stream().map(x -> x.getName()).collect(Collectors.toSet()));

		BioMetabolite m1 = parser.getNetwork().getMetabolitesView().getEntityFromId("m1");
		BioMetabolite m2 = parser.getNetwork().getMetabolitesView().getEntityFromId("m2");
		BioMetabolite m3 = parser.getNetwork().getMetabolitesView().getEntityFromId("m3");

		assertNotNull(m1);
		assertNotNull(m2);
		assertNotNull(m3);
		
		assertTrue(MetaboliteAttributes.getConstant(m1));
		assertFalse(MetaboliteAttributes.getConstant(m2));
		assertFalse(MetaboliteAttributes.getConstant(m3));
		
		assertTrue(parser.getNetwork().getCompartmentsView().getEntityFromId("c1").getComponents().containsId("m1"));
		assertTrue(parser.getNetwork().getCompartmentsView().getEntityFromId("c2").getComponents().containsId("m2"));
		
		
		
		
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

		BioReaction reaction1 = parser.getNetwork().getReactionsView().getEntityFromId("r1");
		BioReaction reaction2 = parser.getNetwork().getReactionsView().getEntityFromId("r2");
		BioReaction reaction3 = parser.getNetwork().getReactionsView().getEntityFromId("r3");


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

		BioReaction reaction1 = parser.getNetwork().getReactionsView().getEntityFromId("r1");

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

		BioReaction reaction1 = parser.getNetwork().getReactionsView().getEntityFromId("r1");

		Flux lb = ReactionAttributes.getLowerBound(reaction1);

		assertNotNull(lb);

		assertEquals(0.1, lb.value, 0.0);

		Flux ub = ReactionAttributes.getUpperBound(reaction1);

		assertNotNull(ub);

		assertEquals(150.0, ub.value, 0.0);

		BioReaction reaction2 = parser.getNetwork().getReactionsView().getEntityFromId("r2");

		Flux f = ReactionAttributes.getFlux(reaction2, "FLUX");

		assertNotNull(f);
		assertEquals(flux.getValue(), f.value, 0.0);

		// We test that the flux in the notes are not read when the FBC parser is used
		pkgs = new ArrayList<PackageParser>(
				Arrays.asList(new NotesParser(true), new FBCParser(), new AnnotationParser(true)));

		parser.setPackages(pkgs);

		parser.parseModel();

		reaction1 = parser.getNetwork().getReactionsView().getEntityFromId("r1");

		lb = ReactionAttributes.getLowerBound(reaction1);

		assertNull(lb);

	}

}
