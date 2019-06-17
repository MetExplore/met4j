package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.ext.fbc.And;
import org.sbml.jsbml.ext.fbc.Association;
import org.sbml.jsbml.ext.fbc.FBCModelPlugin;
import org.sbml.jsbml.ext.fbc.FBCReactionPlugin;
import org.sbml.jsbml.ext.fbc.FBCSpeciesPlugin;
import org.sbml.jsbml.ext.fbc.FluxObjective;
import org.sbml.jsbml.ext.fbc.GeneProduct;
import org.sbml.jsbml.ext.fbc.GeneProductAssociation;
import org.sbml.jsbml.ext.fbc.GeneProductRef;
import org.sbml.jsbml.ext.fbc.Objective;
import org.sbml.jsbml.ext.fbc.Or;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;

public class FBCParserTest {

	/**
	 * The XML namespace of the FBC version 2 SBML package
	 */
	public static final String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/fbc/version2";

	public SBMLDocument doc;
	Model model;
	FBCParser parser;
	BioNetwork network;
	BioReaction r1, r2, r3;
	Reaction rSbml1, rSbml2, rSbml3;
	FBCReactionPlugin rxn1Plugin, rxn2Plugin, rxn3Plugin;
	FBCModelPlugin plugin;

	@Before
	public void init() {

		doc = new SBMLDocument(3, 1);
		model = doc.createModel();
		parser = new FBCParser();
		network = new BioNetwork();

		r1 = new BioReaction("r1");
		r2 = new BioReaction("r2");
		r3 = new BioReaction("r3");

		network.add(r1);
		network.add(r2);
		network.add(r3);

		BioUnitDefinition def = new BioUnitDefinition();
		NetworkAttributes.addUnitDefinition(network, def);

		rSbml1 = new Reaction("r1");
		rSbml2 = new Reaction("r2");
		rSbml3 = new Reaction("r3");
		model.addReaction(rSbml1);
		model.addReaction(rSbml2);
		model.addReaction(rSbml3);

	}

	@Test
	public void testIsPackageUseableOnModel() {

		assertFalse(parser.isPackageUseableOnModel(model));

		model.getPlugin("fbc");

		assertTrue(parser.isPackageUseableOnModel(model));

	}

	@Test
	public void testParseFluxReactions() throws SBMLException, XMLStreamException {

		plugin = (FBCModelPlugin) model.getPlugin(PackageNamespace);
		plugin.setStrict(true);

		rxn1Plugin = (FBCReactionPlugin) rSbml1.getPlugin("fbc");
		rxn2Plugin = (FBCReactionPlugin) rSbml2.getPlugin("fbc");

		UnitDefinition ud = model.createUnitDefinition();

		ud.setId(BioUnitDefinition.DEFAULT_UNIT);
		ud.setName(BioUnitDefinition.DEFAULT_UNIT);

		Parameter up = model.createParameter(StringUtils.convertToSID(("UPPER_BOUND_1000")));
		up.setValue(1000);
		up.setConstant(true);
		up.setUnits(BioUnitDefinition.DEFAULT_UNIT);

		rxn1Plugin.setUpperFluxBound(up);

		Parameter low = model.createParameter(StringUtils.convertToSID("LOWER_BOUND_MINUS_1000"));
		low.setValue(-1000);
		low.setConstant(true);
		low.setUnits(BioUnitDefinition.DEFAULT_UNIT);

		rxn1Plugin.setLowerFluxBound(low);

		Parameter uniq = model.createParameter(StringUtils.convertToSID("BOUND_EQUAL_1000"));
		uniq.setValue(1000);
		uniq.setConstant(true);
		uniq.setUnits(BioUnitDefinition.DEFAULT_UNIT);

		rxn2Plugin.setLowerFluxBound(uniq);
		rxn2Plugin.setUpperFluxBound(uniq);

		parser.setFbcModel(plugin);

		parser.parseModel(model, network);

		Flux fluxTestUpper = ReactionAttributes.getUpperBound(r1);

		assertNotNull(fluxTestUpper);

		assertEquals(1000.0, fluxTestUpper.value, 0.0);

		Flux fluxTestLower = ReactionAttributes.getLowerBound(r1);

		assertNotNull(fluxTestLower);

		assertEquals(-1000.0, fluxTestLower.value, 0.0);

		fluxTestLower = ReactionAttributes.getLowerBound(r2);

		assertNotNull(fluxTestLower);

		assertEquals(1000.0, fluxTestLower.value, 0.0);

		fluxTestUpper = ReactionAttributes.getUpperBound(r2);

		assertNotNull(fluxTestUpper);

		assertEquals(1000.0, fluxTestUpper.value, 0.0);

	}

	@Test
	public void testComputeGeneAssociations() throws SBMLException, XMLStreamException {

		plugin = (FBCModelPlugin) model.getPlugin(PackageNamespace);
		plugin.setStrict(true);

		rxn1Plugin = (FBCReactionPlugin) rSbml1.getPlugin("fbc");
		rxn2Plugin = (FBCReactionPlugin) rSbml2.getPlugin("fbc");
		rxn3Plugin = (FBCReactionPlugin) rSbml3.getPlugin("fbc");

		plugin.addGeneProduct(new GeneProduct("g1"));
		plugin.addGeneProduct(new GeneProduct("g2"));
		plugin.addGeneProduct(new GeneProduct("g3"));

		GeneProductRef p1 = new GeneProductRef("g1ref");
		p1.setGeneProduct("g1");
		GeneProductRef p2 = new GeneProductRef("g2ref");
		p2.setGeneProduct("g2");
		GeneProductRef p3 = new GeneProductRef("g3ref");
		p3.setGeneProduct("g3");

		GeneProductAssociation GPA1 = rxn1Plugin.createGeneProductAssociation();
		Association a1 = p1;
		GPA1.setAssociation(a1);

		List<Association> assosList = new ArrayList<Association>();
		assosList.add(p2);
		assosList.add(p3);

		List<Association> assosList2 = new ArrayList<Association>();
		assosList2.add(p1);

		And a2 = new And();
		a2.addAllAssociations(assosList);

		Or orAssoc = new Or();
		orAssoc.addAssociation(a2);
		orAssoc.addAssociation(a1);

		GeneProductAssociation GPA2 = rxn2Plugin.createGeneProductAssociation();
		GPA2.setAssociation(orAssoc);

		rxn3Plugin.createGeneProductAssociation();

		parser.setFbcModel(plugin);

		parser.parseModel(model, network);

		String ga1 = BioChemicalReactionUtils.getGPR(network, r1, false);

		assertEquals("g1", ga1);

		String ga2 = BioChemicalReactionUtils.getGPR(network, r2, false);

		assertEquals("( g1 ) OR ( g2 AND g3 )", ga2);

		String ga3 = BioChemicalReactionUtils.getGPR(network, r3, false);

		assertEquals("", ga3);

		SBMLWriter s = new SBMLWriter();
		System.err.println(s.writeSBMLToString(doc));

	}

	@Test
	public void testParseFluxSpecies() {

		plugin = (FBCModelPlugin) model.getPlugin(PackageNamespace);
		plugin.setStrict(true);
		parser.setFbcModel(plugin);

		BioMetabolite m1 = new BioMetabolite("m1");
		BioMetabolite m2 = new BioMetabolite("m2");
		BioMetabolite m3 = new BioMetabolite("m3");

		network.add(m1);
		network.add(m2);
		network.add(m3);

		Species specie1 = model.createSpecies(StringUtils.convertToSID("m1"));
		Species specie2 = model.createSpecies(StringUtils.convertToSID("m2"));
		model.createSpecies(StringUtils.convertToSID("m3"));

		FBCSpeciesPlugin speciePlugin1 = (FBCSpeciesPlugin) specie1.getPlugin("fbc");
		FBCSpeciesPlugin speciePlugin2 = (FBCSpeciesPlugin) specie2.getPlugin("fbc");

		speciePlugin1.setCharge(3);
		speciePlugin2.setChemicalFormula("C6H6O2");

		parser.parseModel(model, network);

		assertNull(m1.getChemicalFormula());
		assertNull(m3.getChemicalFormula());
		assertEquals(3, m1.getCharge());
		assertEquals(0, m2.getCharge());
		assertEquals(0, m3.getCharge());
		assertEquals("C6H6O2", m2.getChemicalFormula());

	}

	@Test
	public void testParseListOfFluxObjectives() throws SBMLException, XMLStreamException {

		plugin = (FBCModelPlugin) model.getPlugin(PackageNamespace);
		plugin.setStrict(true);
		parser.setFbcModel(plugin);

		Objective obj = plugin.createObjective("obj1");
		plugin.setActiveObjective("obj1");
		obj.setType("maximize");
		FluxObjective sObj = obj.createFluxObjective();
		sObj.setReaction("r1");
		sObj.setCoefficient(2);
		FluxObjective sObj2 = obj.createFluxObjective("f2");
		sObj2.setName("f2");
		sObj2.setReaction("r2");
		sObj2.setCoefficient(3);

		Objective obj2 = plugin.createObjective("obj2");
		obj2.setType("minimize");
		FluxObjective sObj3 = obj2.createFluxObjective();
		sObj3.setReaction("r3");
		sObj3.setCoefficient(1.5);
		
		SBMLWriter s = new SBMLWriter();
		System.err.println(s.writeSBMLToString(doc));

		parser.parseModel(model, network);

		assertEquals(2, parser.getFlxNet().getListOfObjectives().size());
		
		assertEquals("obj1", parser.getFlxNet().getActiveObjective().getId());

		HashSet<String> testObjIds = new HashSet<String>();
		testObjIds.add("r1");
		testObjIds.add("f2");

		assertEquals(testObjIds, parser.getFlxNet().getListOfObjectives().get("obj1").getListOfReactionObjectives().stream()
				.map(x -> x.getId()).collect(Collectors.toSet()));
		
		assertEquals(testObjIds, NetworkAttributes.getObjectives(parser.getFlxNet().getUnderlyingBionet()).getEntityFromId("obj1").getListOfReactionObjectives().stream()
				.map(x -> x.getId()).collect(Collectors.toSet()));
		
		testObjIds.clear();
		testObjIds.add("r3");
		
		assertEquals(testObjIds, parser.getFlxNet().getListOfObjectives().get("obj2").getListOfReactionObjectives().stream()
				.map(x -> x.getId()).collect(Collectors.toSet()));

		
		HashSet<Double> testCoefficients = new HashSet<Double>();
		testCoefficients.add(2.0);
		testCoefficients.add(3.0);
		
		assertEquals(testCoefficients, parser.getFlxNet().getListOfObjectives().get("obj1").getListOfReactionObjectives().stream()
				.map(x -> x.getCoefficient()).collect(Collectors.toSet()));

		HashSet<BioReaction> testReactions = new HashSet<BioReaction>();
		testReactions.add(r1);
		testReactions.add(r2);
		
		assertEquals(testReactions, parser.getFlxNet().getListOfObjectives().get("obj1").getListOfReactionObjectives().stream()
		.map(x -> x.getFlxReaction().getUnderlyingReaction()).collect(Collectors.toSet()));
		
		
		// test active
		assertTrue(parser.getFlxNet().getListOfObjectives().get("obj1").active);
		assertFalse(parser.getFlxNet().getListOfObjectives().get("obj2").active);

	}

}
