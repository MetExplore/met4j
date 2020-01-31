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
import java.util.stream.Collectors;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjective;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjectiveCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.ReactionObjective;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.BionetworkToJsbml;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.fbc.And;
import org.sbml.jsbml.ext.fbc.Association;
import org.sbml.jsbml.ext.fbc.FBCReactionPlugin;
import org.sbml.jsbml.ext.fbc.FBCSpeciesPlugin;
import org.sbml.jsbml.ext.fbc.GeneProductRef;
import org.sbml.jsbml.ext.fbc.ListOfObjectives;
import org.sbml.jsbml.ext.fbc.Objective;
import org.sbml.jsbml.ext.fbc.Or;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageWriterException;

public class FBCWriterTest {

	public SBMLDocument doc;
	Model model;
	BioNetwork network;
	BioReaction r1, r2;
	Reaction rSbml1, rSbml2;

	BioMetabolite m1, m2;
	BioCompartment c1, c2;

	Species s1, s3;
	Compartment compartSbml1, compartSbml2;

	FBCWriter writer;

	@Before
	public void init() throws JSBMLPackageWriterException {

		doc = new SBMLDocument(3, 1);
		model = doc.createModel();

		network = new BioNetwork();

		r1 = new BioReaction("r1");
		r2 = new BioReaction("r2");

		ReactionAttributes.setLowerBound(r1, new Flux(15.0));
		ReactionAttributes.setUpperBound(r1, new Flux(20.0));

		network.add(r1);
		network.add(r2);

		m1 = new BioMetabolite("m1");
		m2 = new BioMetabolite("m2");

		m1.setCharge(3);
		m1.setChemicalFormula("CH6");

		network.add(m1);
		network.add(m2);

		NetworkAttributes.addUnitDefinition(network, new BioUnitDefinition());

		c1 = new BioCompartment("c1");
		c2 = new BioCompartment("c2");

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

		network.affectSubUnit(p1, 1.0, e1);
		network.affectSubUnit(p2, 1.0, e1);
		network.affectSubUnit(p3, 1.0, e2);

		network.affectEnzyme(e1, r1);
		network.affectEnzyme(e2, r1);
		network.affectEnzyme(e2, r2);

		BioObjectiveCollection objectives = new BioObjectiveCollection();

		BioObjective obj1 = new BioObjective("obj1", "obj1");
		ReactionObjective robj1 = new ReactionObjective("robj1", "robj1");
		robj1.setCoefficient(2.0);
		robj1.setFlxReaction(new FluxReaction(r1));
		ReactionObjective robj2 = new ReactionObjective("robj2", "robj2");
		robj2.setFlxReaction(new FluxReaction(r2));
		robj1.setCoefficient(2.0);

		BioCollection<ReactionObjective> setReactions1 = new BioCollection<ReactionObjective>();
		setReactions1.add(robj2);
		setReactions1.add(robj1);
		obj1.setListOfReactionObjectives(setReactions1);

		BioObjective obj2 = new BioObjective("obj2", "obj2");
		BioCollection<ReactionObjective> setReactions2 = new BioCollection<ReactionObjective>();
		setReactions2.add(robj1);
		obj2.setListOfReactionObjectives(setReactions2);
		obj2.active = true;

		objectives.add(obj1);
		objectives.add(obj2);

		NetworkAttributes.setObjectives(network, objectives);

		BionetworkToJsbml converter = new BionetworkToJsbml();
		writer = new FBCWriter();
		converter.addPackage(writer);

		model = converter.parseBioNetwork(network);

	}

	@Test
	public void testMetaboliteCharge() {

		Species s1 = model.getSpecies("m1");

		assertNotNull(s1);

		FBCSpeciesPlugin speciePlugin1 = (FBCSpeciesPlugin) s1.getPlugin("fbc");

		assertEquals(m1.getCharge(), speciePlugin1.getCharge(), 0);

	}

	@Test
	public void testMetaboliteFormula() {

		Species s1 = model.getSpecies("m1");

		assertNotNull(s1);

		FBCSpeciesPlugin speciePlugin1 = (FBCSpeciesPlugin) s1.getPlugin("fbc");

		assertEquals(m1.getChemicalFormula(), speciePlugin1.getChemicalFormula());

	}

	@Test
	public void testGeneProducts() {

		assertEquals(network.getGenesView().size(), writer.getFbcModel().getGeneProductCount());

		assertNotNull(writer.getFbcModel().getGeneProduct("g1"));
		assertNotNull(writer.getFbcModel().getGeneProduct("g2"));
		assertNotNull(writer.getFbcModel().getGeneProduct("g3"));

	}

	@Test
	public void testReactionBounds() {

		Reaction reaction1 = model.getReaction("r1");

		assertNotNull(reaction1);

		FBCReactionPlugin rxn1Plugin = (FBCReactionPlugin) reaction1.getPlugin("fbc");

		assertEquals(ReactionAttributes.getLowerBound(r1).value.toString(),
				Double.toString(rxn1Plugin.getLowerFluxBoundInstance().getValue()));

		assertEquals(ReactionAttributes.getUpperBound(r1).value.toString(),
				Double.toString(rxn1Plugin.getUpperFluxBoundInstance().getValue()));

		Reaction reaction2 = model.getReaction("r2");

		assertNotNull(reaction2);

		FBCReactionPlugin rxn2Plugin = (FBCReactionPlugin) reaction2.getPlugin("fbc");

		assertNull(rxn2Plugin.getUpperFluxBoundInstance());

		assertNull(rxn2Plugin.getLowerFluxBoundInstance());

	}

	@Test
	public void testObjectives() {

		BioObjectiveCollection refObjectives = NetworkAttributes.getObjectives(network);
		ListOfObjectives testObjectives = writer.getFbcModel().getListOfObjectives();

		assertEquals(refObjectives.size(), testObjectives.size());

		assertEquals(refObjectives.stream().map(x -> x.getId()).collect(Collectors.toSet()),
				testObjectives.stream().map(x -> x.getId()).collect(Collectors.toSet()));

		BioObjective refObj1 = refObjectives.get("obj1");
		Objective testObj1 = testObjectives.get("obj1");

		assertEquals(
				refObj1.getListOfReactionObjectives().stream()
						.map(x -> x.getFlxReaction().getUnderlyingReaction().getId()).collect(Collectors.toSet()),
				testObj1.getListOfFluxObjectives().stream().map(x -> x.getReaction()).collect(Collectors.toSet()));

		assertEquals(
				refObj1.getListOfReactionObjectives().stream().map(x -> x.getCoefficient()).collect(Collectors.toSet()),
				testObj1.getListOfFluxObjectives().stream().map(x -> x.getCoefficient()).collect(Collectors.toSet()));

	}

	@Test
	public void testGpr() {

		Reaction reaction1 = model.getReaction("r1");

		assertNotNull(reaction1);

		FBCReactionPlugin rxn1Plugin = (FBCReactionPlugin) reaction1.getPlugin("fbc");

		assertEquals(r1.getEnzymesView().size(),
				rxn1Plugin.getGeneProductAssociation().getAssociation().getChildCount());

		assertEquals(rxn1Plugin.getGeneProductAssociation().getAssociation().getClass(), Or.class);

		for (int i = 0; i < r1.getEnzymesView().size(); i++) {
			Association a = (Association) rxn1Plugin.getGeneProductAssociation().getAssociation().getChildAt(i);

			if (a.getClass().equals(And.class)) {

				Set<String> refs = new HashSet<String>();
				refs.add("g1");
				refs.add("g2");

				assertEquals(refs.size(), a.getChildCount());

				Set<String> tests = new HashSet<String>();

				for (int j = 0; j < refs.size(); j++) {
					GeneProductRef geneProductRef = (GeneProductRef) a.getChildAt(j);
					tests.add(geneProductRef.getGeneProductInstance().getId());
				}

				assertEquals(refs, tests);

			} else if (a.getClass().equals(GeneProductRef.class)) {

				assertEquals("g3", ((GeneProductRef) a).getGeneProductInstance().getId());

			} else {

				fail("The gene association is bad");

			}

			Reaction reaction2 = model.getReaction("r2");

			assertNotNull(reaction2);

			FBCReactionPlugin rxn2Plugin = (FBCReactionPlugin) reaction2.getPlugin("fbc");

			assertEquals(0, rxn2Plugin.getGeneProductAssociation().getAssociation().getChildCount());

			assertEquals(GeneProductRef.class, rxn2Plugin.getGeneProductAssociation().getAssociation().getClass());

			assertEquals("g3", ((GeneProductRef) rxn2Plugin.getGeneProductAssociation().getAssociation())
					.getGeneProductInstance().getId());

		}

	}

}
