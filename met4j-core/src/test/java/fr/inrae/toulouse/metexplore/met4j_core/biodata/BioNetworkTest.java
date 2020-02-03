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

package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.classesForTests.BioEntityFake;
import org.junit.Before;
import org.junit.Test;

public class BioNetworkTest {

	BioNetwork network;
	BioReaction r;
	BioMetabolite s1, s2, p1, p2;
	BioCompartment cpt;
	BioEnzyme e1, e2;
	BioGene g1, g2;
	BioPathway pathway;

	/**
	 * 
	 * @return a reaction with two substrates, two products, one enzyme linked
	 *         to two genes All objects have been added to the network
	 */
	private BioReaction addTestReactionToNetwork() {
		r = new BioReaction("r1");

		s1 = new BioMetabolite("s1");
		s2 = new BioMetabolite("s2");
		p1 = new BioMetabolite("p1");
		p2 = new BioMetabolite("p2");
		cpt = new BioCompartment("cpt");

		network.add(r);
		network.add(s1);
		network.add(s2);
		network.add(p1);
		network.add(p2);
		network.add(cpt);
		
		network.affectToCompartment(cpt, s1, s2, p1, p2);

		network.affectLeft(r, 2.0, cpt, s1);
		network.affectLeft(r, 2.0, cpt, s2);
		network.affectRight(r, 3.0, cpt, p1);
		network.affectRight(r, 3.0, cpt, p2);

		pathway = new BioPathway("pathway1");
		network.add(pathway);

		network.affectToPathway(pathway, r);

		e1 = new BioEnzyme("e");
		network.add(e1);
		BioProtein prot1 = new BioProtein("p1");
		network.add(prot1);
		BioProtein prot2 = new BioProtein("p2");
		network.add(prot2);
		g1 = new BioGene("g1");
		network.add(g1);
		g2 = new BioGene("g2");
		network.add(g2);
		
		e2 = new BioEnzyme("e2");
		network.add(e2);
		
		network.affectGeneProduct(prot1, g1);
		network.affectGeneProduct(prot2, g2);

		network.affectSubUnit(e1, 1.0, prot1);
		network.affectSubUnit(e1, 1.0, prot2);
		
		network.affectSubUnit(e2, 1.0, prot1);

		network.affectEnzyme(r, e1);
		network.affectEnzyme(r, e2);

		return r;

	}

	@Before
	public void init() {
		network = new BioNetwork();
	}

	@Test
	public void testAddBioPathway() {

		BioPathway pathway = new BioPathway("pathway");
		network.add(pathway);
		BioCollection<BioPathway> pathways = network.getPathwaysView();
		assertEquals("Not good number of pathways after adding a pathway", 1, pathways.size());

	}

	@Test
	public void testAddMetabolite() {
		BioMetabolite metabolite = new BioMetabolite("metabolite");
		network.add(metabolite);
		BioCollection<BioMetabolite> metabolites = network.getMetabolitesView();
		assertEquals("Not good number of metabolites after adding a metabolite", 1, metabolites.size());

	}

	@Test
	public void testAddProtein() {
		BioProtein protein = new BioProtein("protein");
		network.add(protein);
		BioCollection<BioProtein> proteins = network.getProteinsView();
		assertEquals("Not good number of proteins after adding a pathway", 1, proteins.size());

	}

	@Test
	public void testAddGene() {
		BioGene gene = new BioGene("gene");
		network.add(gene);
		BioCollection<BioGene> genes = network.getGenesView();
		assertEquals("Not good number of genes after adding a gene", 1, genes.size());

	}

	@Test
	public void testAddReaction() {
		BioReaction reaction = new BioReaction("reaction");
		network.add(reaction);
		BioCollection<BioReaction> reactions = network.getReactionsView();
		assertEquals("Not good number of reactions after adding a reaction", 1, reactions.size());

	}

	@Test
	public void testAddCompartment() {
		BioCompartment compartment = new BioCompartment("compartment");
		network.add(compartment);
		BioCollection<BioCompartment> compartments = network.getCompartmentsView();
		assertEquals("Not good number of compartments after adding a compartment", 1, compartments.size());

	}

	@Test
	public void testAddEnzyme() {
		BioEnzyme enzyme = new BioEnzyme("enzyme");
		network.add(enzyme);
		BioCollection<BioEnzyme> enzymes = network.getEnzymesView();
		assertEquals("Not good number of enzymes after adding a enzyme", 1, enzymes.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddBioEntityFake() {
		BioEntityFake entityFake = new BioEntityFake("entityFake");

		// Must return an exception
		network.add(entityFake);
	}

	@Test
	public void testRemoveProtein() {
		// Simple test of removing one protein
		BioProtein protein = new BioProtein("protein");
		network.add(protein);
		BioProtein protein2 = new BioProtein("protein2");
		network.add(protein2);
		network.removeOnCascade(protein2);
		assertEquals("not the good number of proteins after removal", 1, network.getProteinsView().size());

		// If a protein is linked to a gene, its removal does not affect the
		// gene, this one can be used later
		BioGene gene = new BioGene("gene");
		network.add(gene);
		network.affectGeneProduct(protein, gene);
		network.removeOnCascade(protein);
		assertEquals("not the good number of genes after removal a protein linked to a gene", 1,
				network.getGenesView().size());

		// Test if the enzyme still contains the protein
		BioEnzyme enzyme = new BioEnzyme("enzyme");
		network.add(enzyme);
		network.add(protein);
		network.affectSubUnit(enzyme, 1.0, protein);
		network.removeOnCascade(protein);
		assertEquals("Enzyme not removed", 0, network.getEnzymesView().size());

		// Test if the compartment still contains the protein
		BioCompartment compartment = new BioCompartment("cpt");
		network.add(compartment);
		network.add(protein);
		network.affectToCompartment(compartment, protein);
		network.removeOnCascade(protein);
		assertEquals("Protein not removed from compartment", 0, compartment.getComponentsView().size());

	}

	@Test()
	public void testRemovePathway() {

		// remove one pathway
		BioPathway pathway = new BioPathway("pathwayId");
		network.add(pathway);

		BioReaction reaction = new BioReaction("reactionId");
		network.add(reaction);
		network.affectToPathway(pathway, reaction);

		network.removeOnCascade(pathway);
		assertEquals("Pathway not removed from the network", 0, network.getPathwaysView().size());

		assertEquals("Reactions of a pathway must not be removed from the network after a pathway is removed", 1,
				network.getReactionsView().size());
		
		assertEquals("Removed pathway can't appear in the list of pathways of a reaction", 0, network.getPathwaysFromReaction(reaction).size());

	}

	@Test
	public void testRemoveMetabolite() {

		// Remove one metabolite
		BioMetabolite metabolite = new BioMetabolite("metaboliteId");
		network.add(metabolite);
		network.removeOnCascade(metabolite);
		assertEquals("Metabolite not removed from the network", 0, network.getMetabolitesView().size());

		// Remove metabolite from reaction interactions
		BioReaction reaction = new BioReaction("reactionId");
		BioCompartment cpt = new BioCompartment("cpt");
		BioEnzyme enz = new BioEnzyme("enz");
		network.add(reaction);
		network.add(metabolite);
		network.add(cpt);
		network.affectToCompartment(cpt, metabolite);
		network.affectLeft(reaction, 1.0, cpt, metabolite);
		network.affectRight(reaction, 1.0, cpt, metabolite);
		network.add(enz);
		network.affectSubUnit(enz, 1.0, metabolite);
		network.removeOnCascade(metabolite);
		
		assertEquals(reaction.getLeftReactants().size(), 0);
		assertEquals(reaction.getRightReactants().size(), 0);
		assertEquals(enz.getParticipants().size(), 0);
		assertEquals(network.getReactionsView().size(), 0);
		assertEquals(network.getEnzymesView().size(), 0);
		assertEquals(network.getCompartmentsView().size(), 0);


	}

	@Test
	public void testRemoveGene() {

		// Remove one gene
		BioGene gene = new BioGene("geneId");
		network.add(gene);
		network.removeOnCascade(gene);
		assertEquals("Gene not removed from the network", 0, network.getGenesView().size());

		// Test if the protein still contains the gene
		BioProtein protein = new BioProtein("prot");
		network.add(gene);
		network.add(protein);
		network.affectGeneProduct(protein, gene);
		network.removeOnCascade(gene);
		assertEquals("Protein not removed", 0, network.getProteinsView().size());

	}

	@Test
	public void testRemoveReaction() {

		// remove one reaction alone
		BioReaction reaction = new BioReaction("reactionId");
		network.add(reaction);
		network.removeOnCascade(reaction);

		assertEquals("Reaction not removed from the network", 0, network.getReactionsView().size());

		// Test if the reaction is removed from a pathway
		BioPathway pathway = new BioPathway("pathway");
		network.add(pathway);
		network.add(reaction);
		network.affectToPathway(pathway, reaction);
		network.removeOnCascade(reaction);
		assertEquals("Reaction not removed from pathway", 0, pathway.getReactions().size());

	}

	@Test
	public void testRemoveCompartment() {

		// Remove one compartment alone
		BioCompartment cpt = new BioCompartment("cpt");
		network.add(cpt);
		network.removeOnCascade(cpt);

		assertEquals("Compartment not removed from the network", 0, network.getCompartmentsView().size());

		// Test if reactants in reactions are removed
		BioReaction reaction = new BioReaction("reac");
		BioMetabolite met = new BioMetabolite("metId");
		network.add(met);
		network.add(cpt);
		network.add(reaction);
		network.affectToCompartment(cpt, met);
		network.affectLeft(reaction, 1.0, cpt, met);
		network.affectRight(reaction, 1.0, cpt, met);

		network.removeOnCascade(cpt);

		assertEquals("Substrate not removed from reaction when the compartment is removed", 0,
				network.getReactionsView().size());

	}

	@Test
	public void testAffectSubstrate() {

		BioReaction reaction = new BioReaction("r1");
		network.add(reaction);
		BioMetabolite s1 = new BioMetabolite("s1");
		network.add(s1);
		BioMetabolite s2 = new BioMetabolite("s2");
		network.add(s2);
		BioCompartment cpt = new BioCompartment("cpt");
		network.add(cpt);

		network.affectToCompartment(cpt, s1, s2);

		network.affectLeft(reaction, 1.0, cpt, s1);

		BioReactant reactant = new BioReactant(s2, 1.0, cpt);
		network.affectLeft(reaction, reactant);

		assertEquals("Substrate not well added", 2, reaction.getLeftReactants().size());

	}

	@Test
	public void testRemoveSubstrate() {

		BioReaction reaction = new BioReaction("r1");
		network.add(reaction);
		BioMetabolite s1 = new BioMetabolite("s1");
		network.add(s1);
		BioMetabolite s2 = new BioMetabolite("s2");
		network.add(s2);
		BioCompartment cpt = new BioCompartment("cpt");
		network.add(cpt);

		network.affectToCompartment(cpt, s1, s2);

		network.affectLeft(reaction, 1.0, cpt, s1);
		network.affectLeft(reaction, 1.0, cpt, s2);

		network.removeLeft(s2, cpt, reaction);

		assertEquals("Substrate not well added", 1, reaction.getLeftReactants().size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubstrateInNoCompartment() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);

		network.add(reaction);
		// The compartment has not been added to the network
		network.affectLeft(reaction, 1.0, cpt, metabolite);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubstrateInBadCompartment() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");
		BioCompartment cpt2 = new BioCompartment("cptId2");

		network.add(metabolite);
		network.add(cpt);
		network.add(reaction);
		network.affectToCompartment(cpt2, metabolite);
		// The metabolite has been affected to an other compartment
		network.affectLeft(reaction, 1.0, cpt, metabolite);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubstrateReactionNotInTheNetwork() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);
		network.add(cpt);
		network.affectToCompartment(cpt, metabolite);
		network.affectLeft(reaction, 1.0, cpt, metabolite);
	}

	@Test
	public void testAffectProduct() {
		BioReaction reaction = new BioReaction("r1");
		network.add(reaction);
		BioMetabolite s1 = new BioMetabolite("s1");
		network.add(s1);
		BioMetabolite s2 = new BioMetabolite("s2");
		network.add(s2);
		BioCompartment cpt = new BioCompartment("cpt");
		network.add(cpt);

		network.affectToCompartment(cpt, s1, s2);

		network.affectRight(reaction, 1.0, cpt, s1);
		BioReactant reactant = new BioReactant(s2, 1.0, cpt);
		network.affectRight(reaction, reactant);

		assertEquals("Product not well added", 2, reaction.getRightReactants().size());
	}

	@Test
	public void testRemoveProduct() {
		BioReaction reaction = new BioReaction("r1");
		network.add(reaction);
		BioMetabolite s1 = new BioMetabolite("s1");
		network.add(s1);
		BioMetabolite s2 = new BioMetabolite("s2");
		network.add(s2);
		BioCompartment cpt = new BioCompartment("cpt");
		network.add(cpt);

		network.affectToCompartment(cpt, s1, s2);

		network.affectRight(reaction, 1.0, cpt, s1);
		network.affectRight(reaction, 1.0, cpt, s2);

		network.removeRight(s1, cpt, reaction);

		assertEquals("Product not well removed", 1, reaction.getRightReactants().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectProductInNoCompartment() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);
		network.add(reaction);
		network.affectRight(reaction, 1.0, cpt, metabolite);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectProductInBadCompartment() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");
		BioCompartment cpt2 = new BioCompartment("cptId2");

		network.add(metabolite);
		network.add(cpt);
		network.add(reaction);
		network.affectToCompartment(cpt2, metabolite);
		network.affectRight(reaction, 1.0, cpt, metabolite);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectProductReactionNotInTheNetwork() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);
		network.add(cpt);
		network.affectToCompartment(cpt, metabolite);
		network.affectRight(reaction, 1.0, cpt, metabolite);
	}

	@Test
	public void testAffectEnzyme() {

		BioReaction reaction = new BioReaction("reactionId");
		BioEnzyme enzyme = new BioEnzyme("enzymeId");
		network.add(reaction);
		network.add(enzyme);
		network.affectEnzyme(reaction, enzyme);

		assertEquals("Enzyme not affected to reaction", 1, reaction.getEnzymes().size());
	}

	@Test
	public void testRemoveEnzymeFromReaction() {

		BioReaction reaction = new BioReaction("reactionId");
		BioEnzyme enzyme = new BioEnzyme("enzymeId");
		network.add(reaction);
		network.add(enzyme);
		network.affectEnzyme(reaction, enzyme);

		network.removeEnzymeFromReaction(enzyme, reaction);

		assertEquals("Enzyme not removed from reaction", 0, reaction.getEnzymes().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectEnzymeReactionNotPresent() {
		BioReaction reaction = new BioReaction("reactionId");
		BioEnzyme enzyme = new BioEnzyme("enzymeId");
		network.add(enzyme);
		network.affectEnzyme(reaction, enzyme);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectEnzymeNotPresent() {
		BioReaction reaction = new BioReaction("reactionId");
		BioEnzyme enzyme = new BioEnzyme("enzymeId");
		network.add(reaction);
		network.affectEnzyme(reaction, enzyme);

	}

	@Test
	public void testAffectSubUnit() {

		BioMetabolite unitMetabolite = new BioMetabolite("met");
		BioProtein unitProtein = new BioProtein("prot");
		BioEnzyme enz = new BioEnzyme("enz");

		network.add(unitMetabolite);
		network.add(unitProtein);
		network.add(enz);

		network.affectSubUnit(enz, 1.0, unitMetabolite);

		assertEquals("subunit not added to enzyme", 1, enz.getParticipants().size());
	}

	@Test
	public void testRemoveSubUnit() {

		BioMetabolite unitMetabolite = new BioMetabolite("met");
		BioProtein unitProtein = new BioProtein("prot");
		BioEnzyme enz = new BioEnzyme("enz");

		network.add(unitMetabolite);
		network.add(unitProtein);
		network.add(enz);

		network.affectSubUnit(enz, 1.0, unitMetabolite);
		;
		network.removeSubUnit(unitMetabolite, enz);

		assertEquals("subunit not removed from enzyme", 0, enz.getParticipants().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubUnitEnzymeNotPresent() {

		BioProtein unit = new BioProtein("fakeId");
		BioEnzyme enz = new BioEnzyme("enz");

		network.add(unit);

		network.affectSubUnit(enz, 1.0, unit);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubUnitNotPresent() {

		BioProtein unit = new BioProtein("fakeId");
		BioEnzyme enz = new BioEnzyme("enz");

		network.add(enz);

		network.affectSubUnit(enz, 1.0, unit);

	}

	@Test
	public void testAffectGeneProduct() {

		BioProtein prot = new BioProtein("protId");
		BioGene gene = new BioGene("geneId");

		network.add(prot);
		network.add(gene);
		
		network.affectGeneProduct(prot, gene);
		
		assertEquals("Gene badly affected", prot.getGene(), gene);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectGeneProductProteinNotPresent() {

		BioProtein prot = new BioProtein("protId");
		BioGene gene = new BioGene("geneId");

		network.add(gene);

		network.affectGeneProduct(prot, gene);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectGeneProductGeneNotPresent() {

		BioProtein prot = new BioProtein("protId");
		BioGene gene = new BioGene("geneId");

		network.add(prot);

		network.affectGeneProduct(prot, gene);

	}

	@Test
	public void testRemoveGeneProduct() {

		BioProtein prot = new BioProtein("protId");
		BioGene gene = new BioGene("geneId");

		network.add(prot);
		network.add(gene);

		network.affectGeneProduct(prot, gene);
		network.removeGeneProduct(prot, gene);

		assertEquals("Bad number of proteins coded by the gene", null, prot.getGene());

	}

	@Test
	public void testAffectToPathway() {

		BioReaction reaction = new BioReaction("reacId");
		BioPathway pathway = new BioPathway("pathwayId");

		network.add(reaction);
		network.add(pathway);

		network.affectToPathway(pathway, reaction);

		assertEquals("Reaction not added to pathway", 1, pathway.getReactions().size());

		assertEquals("Bad reaction added to pathway", reaction, pathway.getReactions().iterator().next());
	}

	@Test
	public void testRemoveReactionFromPathway() {

		BioReaction reaction = new BioReaction("reacId");
		BioPathway pathway = new BioPathway("pathwayId");

		network.add(reaction);
		network.add(pathway);

		network.affectToPathway(pathway, reaction);

		network.removeReactionFromPathway(reaction, pathway);

		assertEquals("Reaction not removed from pathway", 0, pathway.getReactions().size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToPathwayNotPresent() {
		BioReaction reaction = new BioReaction("reacId");
		BioPathway pathway = new BioPathway("pathwayId");

		network.add(reaction);

		network.affectToPathway(pathway, reaction);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToPathwayReactionNotPresent() {

		BioReaction reaction = new BioReaction("reacId");
		BioPathway pathway = new BioPathway("pathwayId");

		network.add(pathway);

		network.affectToPathway(pathway, reaction);

	}

	@Test
	public void testAffectToCompartment() {

		BioMetabolite ent = new BioMetabolite("id");
		BioCompartment cpt = new BioCompartment("cpt");

		network.add(cpt);
		network.add(ent);

		network.affectToCompartment(cpt, ent);

		assertEquals("Compound not added to compartment", 1, cpt.getComponentsView().size());
		assertEquals("Compound badly added to the compartment", ent, cpt.getComponentsView().iterator().next());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToCompartmentNotPresent() {

		BioMetabolite ent = new BioMetabolite("id");
		BioCompartment cpt = new BioCompartment("cpt");

		network.add(ent);

		network.affectToCompartment(cpt, ent);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToCompartmentEntityNotPresent() {

		BioMetabolite ent = new BioMetabolite("id");
		BioCompartment cpt = new BioCompartment("cpt");

		network.add(cpt);

		network.affectToCompartment(cpt, ent);

	}

	@Test
	public void testGetReactionsFromSubstrate() {
		BioReaction r1 = addTestReactionToNetwork();
		r1.setReversible(false);

		BioReaction r2 = new BioReaction("id2");
		network.add(r2);
		
		BioCollection<BioReaction> reactions = network.getReactionsFromSubstrate(s1);
		
		assertEquals("Get the bad number of reactions with this substrate", 1, reactions.size());

		assertEquals("Get the bad reaction with this substrate", r1, reactions.iterator().next());

		r1.setReversible(true);
		
		reactions = network.getReactionsFromSubstrate(p1);
		
		assertEquals("Get the bad number of reactions with this substrate", 1, reactions.size());

		assertEquals("Get the bad reaction with this substrate", r1, reactions.iterator().next());

		
	}
	
	@Test
	public void testGetReactionsFromProduct() {
		BioReaction r1 = addTestReactionToNetwork();
		r1.setReversible(false);

		BioReaction r2 = new BioReaction("id2");
		network.add(r2);
		
		BioCollection<BioReaction> reactions = network.getReactionsFromProduct(p1);
		
		assertEquals("Get the bad number of reactions with this substrate", 1, reactions.size());

		assertEquals("Get the bad reaction with this substrate", r1, reactions.iterator().next());

		r1.setReversible(true);
		
		reactions = network.getReactionsFromProduct(s1);
		
		assertEquals("Get the bad number of reactions with this substrate", 1, reactions.size());

		assertEquals("Get the bad reaction with this substrate", r1, reactions.iterator().next());

		
	}
	
	@Test
	public void testGetReactionsFromSubstrates() {
		BioReaction r1 = addTestReactionToNetwork();
		r1.setReversible(false);

		BioReaction r2 = new BioReaction("id2");
		network.add(r2);

		BioCollection<BioMetabolite> substrates = new BioCollection<>();

		substrates.add(s1);
		substrates.add(s2);

		BioCollection<BioReaction> reactions = network.getReactionsFromSubstrates(substrates, true);

		assertEquals("Get the bad number of reactions with exactly these substrates", 1, reactions.size());

		assertEquals("Get the bad reaction with exactly these substrates", r1, reactions.iterator().next());

		BioMetabolite s3 = new BioMetabolite("id3");
		network.add(s3);

		substrates.add(s3);

		reactions = network.getReactionsFromSubstrates(substrates, true);

		assertEquals("No reaction exists with these substrates", 0, reactions.size());

		// test exact = false
		substrates.clear();
		substrates.add(s1);

		reactions = network.getReactionsFromSubstrates(substrates, true);

		assertEquals("No reaction with exactly this substrate", 0, reactions.size());

		reactions = network.getReactionsFromSubstrates(substrates, false);

		assertEquals("Get the bad number of reactions with  this at least substrate", 1, reactions.size());

		// since r1 is irreversible, test the right side
		substrates.clear();
		substrates.add(p1);
		reactions = network.getReactionsFromSubstrates(substrates, false);

		assertEquals("No reaction with this substrate (that is a product in fact)", 0, reactions.size());

		substrates.add(p2);

		reactions = network.getReactionsFromSubstrates(substrates, true);

		assertEquals("No reaction with exactly these substrates (that are products in fact)", 0, reactions.size());

		// set r1 reversible and test the right side
		r1.setReversible(true);

		reactions = network.getReactionsFromSubstrates(substrates, true);

		assertEquals("Get the bad number of reactions with  this all these substrates (reversible reaction)", 1,
				reactions.size());

		substrates.remove(p2);

		reactions = network.getReactionsFromSubstrates(substrates, false);

		assertEquals("Get the bad number of reactions with  this at least substrate (reversible reaction)", 1,
				reactions.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReactionsFromSubstratesWithSubstrateAbsent() {

		BioMetabolite s1 = new BioMetabolite("id1");
		network.add(s1);

		BioMetabolite metaboliteAbsent = new BioMetabolite("absent");
		BioCollection<BioMetabolite> substrates = new BioCollection<>();
		substrates.add(s1, metaboliteAbsent);
		network.getReactionsFromSubstrates(substrates, true);

	}

	@Test
	public void testgetReactionsFromProducts() {
		BioReaction r1 = addTestReactionToNetwork();
		r1.setReversible(false);

		BioReaction r2 = new BioReaction("id2");
		network.add(r2);

		BioCollection<BioMetabolite> products = new BioCollection<>();

		products.add(p1, p2);

		BioCollection<BioReaction> reactions = network.getReactionsFromProducts(products, true);

		assertEquals("Get the bad number of reactions with exactly these products", 1, reactions.size());

		assertEquals("Get the bad reaction with exactly these products", r1, reactions.iterator().next());

		BioMetabolite s3 = new BioMetabolite("id3");
		network.add(s3);

		products.add(s3);

		reactions = network.getReactionsFromProducts(products, true);

		assertEquals("No reaction exists with these products", 0, reactions.size());

		// test exact = false
		products.clear();
		products.add(p1);

		reactions = network.getReactionsFromProducts(products, true);

		assertEquals("No reaction with exactly this substrate", 0, reactions.size());

		reactions = network.getReactionsFromProducts(products, false);

		assertEquals("Get the bad number of reactions with  this at least substrate", 1, reactions.size());

		// since r1 is irreversible, test the right side
		products.clear();
		products.add(s1);
		reactions = network.getReactionsFromProducts(products, false);

		assertEquals("No reaction with this substrate (that is a product in fact)", 0, reactions.size());

		products.add(s2);

		reactions = network.getReactionsFromProducts(products, true);

		assertEquals("No reaction with exactly these products (that are products in fact)", 0, reactions.size());

		// set r1 reversible and test the right side
		r1.setReversible(true);

		reactions = network.getReactionsFromProducts(products, true);

		assertEquals("Get the bad number of reactions with  this all these products (reversible reaction)", 1,
				reactions.size());

		products.remove(s2);

		reactions = network.getReactionsFromProducts(products, false);

		assertEquals("Get the bad number of reactions with  this at least substrate (reversible reaction)", 1,
				reactions.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testgetReactionsFromProductsWithProductAbsent() {

		BioMetabolite s1 = new BioMetabolite("id1");
		network.add(s1);

		BioMetabolite metaboliteAbsent = new BioMetabolite("absent");
		BioCollection<BioMetabolite> substrates = new BioCollection<>();
		substrates.add(s1, metaboliteAbsent);

		network.getReactionsFromProducts(substrates, true);

	}

	@Test
	public void testGetMetabolitesFromPathway() {

		BioPathway p = new BioPathway("id");
		network.add(p);

		BioReaction r = addTestReactionToNetwork();

		network.affectToPathway(p, r);

		BioCollection<BioMetabolite> subs = network.getMetabolitesFromPathway(p);

		assertEquals("Bad number of metabolites in pathway", 4, subs.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMetabolitesFromPathwayAbsent() {

		BioPathway p = new BioPathway("id");
		network.add(p);

		BioReaction r = addTestReactionToNetwork();

		network.affectToPathway(p, r);

		network.removeOnCascade(p);
		// Must return an exception
		network.getMetabolitesFromPathway(p);

	}

	@Test
	public void testGetPathwaysFromMetabolites() {

		BioPathway p2 = new BioPathway("pathway2");
		network.add(p2);

		addTestReactionToNetwork();

		BioCollection<BioMetabolite> cpds = new BioCollection<>();
		cpds.add(s1);

		BioCollection<BioPathway> pathways = network.getPathwaysFromMetabolites(cpds, false);

		assertEquals("Bad number of pathways containing this compound", 1, pathways.size());

		assertEquals("Bad pathway containing this compound", "pathway1", pathways.getIds().iterator().next());

		BioMetabolite cpd2 = new BioMetabolite("id3");
		network.add(cpd2);

		cpds.add(cpd2);

		pathways = network.getPathwaysFromMetabolites(cpds, false);

		assertEquals("Bad number of pathways containing at least one  compound of this list", 1, pathways.size());
		
		cpds.remove(s1);

		pathways = network.getPathwaysFromMetabolites(cpds, false);

		assertEquals("No pathway contains this compound", 0, pathways.size());
		
		cpds.clear();
		cpds.add(s1);
		cpds.add(s2);
		cpds.add(p1);
		
		pathways = network.getPathwaysFromMetabolites(cpds, true);
		
		assertEquals("Bad number of pathways containing all these compounds", 1, pathways.size());
		
		cpds.add(cpd2);
		
		pathways = network.getPathwaysFromMetabolites(cpds, true);
		
		assertEquals("Bad number of pathways containing all these compounds", 0, pathways.size());


	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPathwaysFromMetabolitesMetaboliteAbsent() {
		addTestReactionToNetwork();

		BioCollection<BioMetabolite> cpds = new BioCollection<>();

		cpds.add(s1);
		cpds.add(new BioMetabolite("absent"));

		network.getPathwaysFromMetabolites(cpds, false);

	}

	@Test
	public void testGetReactionsFromGenes() {

		BioReaction r = addTestReactionToNetwork();
		BioReaction r2 = new BioReaction("id2");
		network.add(r2);

		BioCollection<BioGene> genes = new BioCollection<>();
		genes.add(g1, g2);

		BioCollection<BioReaction> reactions = network.getReactionsFromGenes(genes, true);

		assertEquals("Bad number of reactions with these genes", 1, reactions.size());

		assertEquals("Bad reaction with these genes", r, reactions.iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReactionsFromGenesWithGeneAbsent() {

		BioCollection<BioGene> genes = new BioCollection<>();
		genes.add(new BioGene("absent"));

		network.getReactionsFromGenes(genes, true);
	}

	@Test
	public void testGetGenesFromReactions() {

		addTestReactionToNetwork();
		BioReaction r2 = new BioReaction("r2");
		network.add(r2);

		BioGene g3 = new BioGene("g3");
		network.add(g3);

		BioCollection<BioReaction> reactions = new BioCollection<BioReaction>();
		reactions.add(r);
		reactions.add(r2);

		BioCollection<BioGene> genes = network.getGenesFromReactions(reactions);

		assertEquals("Bad number of genes with these reactions", 2, genes.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesFromReactionsabsent() {
		// Must return an exception
		network.getGenesFromReactions(new BioReaction("rnotinthenetwork"));

	}

	@Test
	public void testGetGenesFromPathways() {

		addTestReactionToNetwork();

		BioCollection<BioPathway> pathways = new BioCollection<BioPathway>();
		pathways.add(pathway);

		BioGene g3 = new BioGene("g3");
		network.add(g3);

		BioCollection<BioGene> genes = network.getGenesFromPathways(pathways);

		assertEquals("Bad number of genes in this pathway", 2, genes.size());

		Set<String> geneRefs = new HashSet<String>();
		geneRefs.add("g1");
		geneRefs.add("g2");

		assertEquals("Bad genes in this pathway", geneRefs, genes.getIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesFromPathwaysAbsent() {
		BioCollection<BioPathway> pathways = new BioCollection<BioPathway>();
		pathways.add(new BioPathway("absent"));
		// Must return an exception
		network.getGenesFromPathways(pathways);
	}

	@Test
	public void testGetPathwaysFromGenes() {

		addTestReactionToNetwork();
		BioCollection<BioGene> genes = new BioCollection<>();

		BioGene g3 = new BioGene("g3");
		network.add(g3);

		genes.add(g1);
		genes.add(g2);
		genes.add(g3);

		BioPathway p2 = new BioPathway("pathway2");
		network.add(p2);

		BioCollection<BioPathway> pathways = network.getPathwaysFromGenes(genes, false);

		assertEquals("Bad number of pathways with these genes", 1, pathways.size());

		assertTrue("Bad pathway with these genes",
				pathways.contains(network.getPathwaysView().get("pathway1")));

		pathways = network.getPathwaysFromGenes(genes, true);

		assertEquals("No pathway with all these genes", 0, pathways.size());

		pathways = network.getPathwaysFromGenes(genes, false);

		assertEquals("Bad number of pathways with at east one of these genes", 1, pathways.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPathwaysFromGenesAbsent() {
		BioCollection<BioGene> genes = new BioCollection<BioGene>();

		genes.add(new BioGene("absent"));
		// Must return an exception
		network.getPathwaysFromGenes(genes, true);

	}

	@Test
	public void testGetPathwaysFromReactions() {

		addTestReactionToNetwork();

		BioPathway p2 = new BioPathway("pathway2");
		network.add(p2);

		BioCollection<BioReaction> reactions = new BioCollection<BioReaction>();
		reactions.add(r);

		BioCollection<BioPathway> pathways = network.getPathwaysFromReactions(reactions, true);

		assertEquals("Bad number of pathways with this reaction", 1, pathways.size());

		Set<String> pathwaysRef = new HashSet<String>();
		pathwaysRef.add("pathway1");
		assertEquals("Bad pathway with these reactions", pathwaysRef, pathways.getIds());

		BioReaction r2 = new BioReaction("r2");
		network.add(r2);

		reactions.add(r2);

		pathways = network.getPathwaysFromReactions(reactions, true);

		assertEquals("No pathway with all these reactions", 0, pathways.size());

		pathways = network.getPathwaysFromReactions(reactions, false);

		assertEquals("Bad number of pathways with at least one of the reactions", 1, pathways.size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPathwaysFromReactionsAbsent() {

		BioCollection<BioReaction> reactions = new BioCollection<BioReaction>();
		reactions.add(new BioReaction("absent"));

		// Must return an exception
		network.getPathwaysFromReactions(reactions, false);
	}

	@Test
	public void testGetReactionsFromPathways() {

		addTestReactionToNetwork();

		BioReaction r2 = new BioReaction("r2");
		network.add(r2);

		BioReaction r3 = new BioReaction("r3");
		network.add(r3);

		BioPathway pathway1 = network.getPathwaysView().get("pathway1");

		network.affectToPathway(pathway1, r2);

		BioPathway p2 = new BioPathway("pathway2");
		network.add(p2);

		BioCollection<BioPathway> pathways = new BioCollection<BioPathway>();
		pathways.add(pathway);

		BioCollection<BioReaction> reactions = network.getReactionsFromPathways(pathways);

		assertEquals("Bad number of reactions with this reaction", 2, reactions.size());

		Set<String> reactionsRef = new HashSet<String>();
		reactionsRef.add("r1");
		reactionsRef.add("r2");

		assertEquals("Bad pathway with these reactions", reactionsRef, reactions.getIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReactionsFromPathwaysAbsent() {

		BioCollection<BioPathway> pathways = new BioCollection<BioPathway>();
		pathways.add(new BioPathway("absent"));

		network.getReactionsFromPathways(pathways);

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetMetabolitesView() {

		addTestReactionToNetwork();

		BioCollection<BioMetabolite> metabolites = network.getMetabolitesView();

		assertEquals("Not good number of metabolites in the view", 4, metabolites.size());

		metabolites.clear();

	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetEnzymesView() {
		addTestReactionToNetwork();

		BioCollection<BioEnzyme> enzymes = network.getEnzymesView();

		assertEquals("Not good number of enzymes in the view", 2, enzymes.size());

		enzymes.clear();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetProteinsView() {
		addTestReactionToNetwork();

		BioCollection<BioProtein> proteins = network.getProteinsView();

		assertEquals("Not good number of proteins in the view", 2, proteins.size());

		proteins.clear();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetPathwaysView() {
		addTestReactionToNetwork();

		BioCollection<BioPathway> pathways = network.getPathwaysView();

		assertEquals("Not good number of pathways in the view", 1, pathways.size());

		pathways.clear();
	}

	@Test(expected = UnsupportedOperationException.class)

	public void testGetGenesView() {
		addTestReactionToNetwork();

		BioCollection<BioGene> genes = network.getGenesView();

		assertEquals("Not good number of genes in the view", 2, genes.size());

		genes.clear();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetCompartmentsView() {
		addTestReactionToNetwork();

		BioCollection<BioCompartment> compartments = network.getCompartmentsView();

		assertEquals("Not good number of compartments in the view", 1, compartments.size());

		compartments.clear();
	}

	@Test
	public void testGetLeftReactants() {

		addTestReactionToNetwork();

		BioCollection<BioReactant> leftReactants = network.getLeftReactants(r);

		assertEquals("Bad number of left reactants", 2, leftReactants.size());

		for (BioReactant reactant : leftReactants) {
			assertTrue("Bad content of left reactants",
					reactant.getMetabolite().equals(s1) || reactant.getMetabolite().equals(s2));

			assertTrue("Bad compartment of left reactant", reactant.getLocation().equals(cpt));

			assertTrue("Bad stoichiometry of left reactant", reactant.getQuantity() == 2.0);

		}

	}

	@Test
	public void testGetRightReactants() {
		addTestReactionToNetwork();

		BioCollection<BioReactant> rightReactants = network.getRightReactants(r);

		assertEquals("Bad number of right reactants", 2, rightReactants.size());

		for (BioReactant reactant : rightReactants) {
			assertTrue("Bad content of right reactants",
					reactant.getMetabolite().equals(p1) || reactant.getMetabolite().equals(p2));

			assertTrue("Bad compartment of right reactant", reactant.getLocation().equals(cpt));

			assertTrue("Bad stoichiometry of right reactant", reactant.getQuantity() == 3.0);

		}
	}
	
	@Test
	public void testGetCompartmentsOf() {
		
		BioCompartment c1 = new BioCompartment("cpt1");
		BioCompartment c2 = new BioCompartment("cpt2");
		
		network.add(c1);
		network.add(c2);
		s1 = new BioMetabolite("s1");
		network.add(s1);
		
		network.affectToCompartment(c1, s1);
		network.affectToCompartment(c2, s1);
		
		BioCollection<BioCompartment> ref = new BioCollection<BioCompartment>();
		ref.add(c1);
		ref.add(c2);
		
		BioCollection<BioCompartment> test = network.getCompartmentsOf(s1);
		
		assertEquals("Test getCompartmentsOf", ref, test);
		
	}
	
}
