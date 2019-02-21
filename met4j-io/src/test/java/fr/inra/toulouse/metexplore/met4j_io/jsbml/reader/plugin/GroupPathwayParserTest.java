package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.ext.groups.Group;
import org.sbml.jsbml.ext.groups.GroupsModelPlugin;
import org.sbml.jsbml.ext.groups.Member;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;

public class GroupPathwayParserTest {

	BioNetwork network;
	BioReaction r1, r2, r3;
	Reaction rSbml1, rSbml2, rSbml3;
	Model model;
	SBMLDocument doc;
	GroupPathwayParser parser;
	String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/groups/version1";
	GroupsModelPlugin plugin;

	@Before
	public void init() {

		doc = new SBMLDocument(3, 1);
		model = doc.createModel();
		parser = new GroupPathwayParser();
		network = new BioNetwork();

		r1 = new BioReaction("r1");
		r2 = new BioReaction("r2");
		r3 = new BioReaction("r3");

		network.add(r1);
		network.add(r2);
		network.add(r3);

		rSbml1 = new Reaction("r1");
		rSbml2 = new Reaction("r2");
		rSbml3 = new Reaction("r3");
		model.addReaction(rSbml1);
		model.addReaction(rSbml2);
		model.addReaction(rSbml3);

	}

	@Test
	public void testParseModel() {

		plugin = (GroupsModelPlugin) model.getPlugin(PackageNamespace);

		Group g1 = new Group();
		g1.setId("g1");
		g1.setName("pathway1");

		plugin.addGroup(g1);

		Group g2 = new Group();
		g2.setId("g2");
		g2.setName("pathway2");

		plugin.addGroup(g2);

		Member m1 = new Member();
		m1.setIdRef("r1");

		Member m2 = new Member();
		m2.setIdRef("r2");

		Member m3 = new Member();
		m3.setIdRef("r3");
		
		Member m2Bis = new Member();
		m2Bis.setIdRef("r2");

		Member m3Bis = new Member();
		m3Bis.setIdRef("r3");

		g1.addMember(m1);
		g1.addMember(m2);
		g1.addMember(m3);

		g2.addMember(m2Bis);
		g2.addMember(m3Bis);

		parser.parseModel(model, network);

		assertEquals(2, network.getPathwaysView().size());

		Set<String> testIds = new HashSet<String>();

		testIds.add("r1");
		testIds.add("r3");
		testIds.add("r2");

		BioPathway p1 = network.getPathwaysView().getEntityFromId("g1");

		assertNotNull(p1);

		assertEquals(testIds, network.getReactionsFromPathway(p1).getIds());

		testIds.remove("r1");

		BioPathway p2 = network.getPathwaysView().getEntityFromId("g2");

		assertNotNull(p2);

		assertEquals(testIds, network.getReactionsFromPathway(p2).getIds());

	}

}
