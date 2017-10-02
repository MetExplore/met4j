package fr.inra.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils;

public class BioNetworkTest {

	BioNetwork network;

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

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveProtein() {

		// Simple test of removing one protein
		BioProtein protein = new BioProtein("protein");
		network.add(protein);
		BioProtein protein2 = new BioProtein("protein2");
		network.add(protein2);
		network.remove(protein2);
		assertEquals("not the good number of proteins after removal", 1, network.getProteinsView().size());

		// If a protein is linked to a gene, its removal does not affect the
		// gene, this one can be used later
		BioGene gene = new BioGene("gene");
		network.add(gene);
		network.affectGeneProduct(protein, gene);
		network.remove(protein);
		assertEquals("not the good number of genes after removal a protein linked to a gene", 1,
				network.getGenesView().size());

		// Test if the enzyme still contains the protein
		BioEnzyme enzyme = new BioEnzyme("enzyme");
		network.add(enzyme);
		network.add(protein);
		network.affectSubUnit(protein, 1.0, enzyme);
		network.remove(protein);
		assertEquals("Protein not removed from enzyme", 0, enzyme.getSubUnits().size());

		// Test if the compartment still contains the protein
		BioCompartment compartment = new BioCompartment("cpt");
		network.add(compartment);
		network.add(protein);
		network.affectToCompartment(protein, compartment);
		network.remove(protein);
		assertEquals("Protein not removed from compartment", 0, compartment.getComponents().size());

		// Remove a protein that is not present in the network
		// Must return an exception
		network.remove(protein2);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemovePathway() {

		// remove one pathway
		BioPathway pathway = new BioPathway("pathwayId");
		network.add(pathway);

		BioReaction reaction = new BioReaction("reactionId");
		network.add(reaction);
		network.affectToPathway(reaction, pathway);

		network.remove(pathway);
		assertEquals("Pathway not removed from the network", 0, network.getPathwaysView().size());

		assertEquals("Reactions of a pathway must not be removed from the network after a pathway is removed", 1,
				network.getReactionsView().size());

		// Must return an exception
		network.remove(pathway);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveMetabolite() {

		// Remove one metabolite
		BioMetabolite metabolite = new BioMetabolite("metaboliteId");
		network.add(metabolite);
		network.remove(metabolite);
		assertEquals("Metabolite not removed from the network", 0, network.getMetabolitesView().size());

		// Remove metabolite from reaction interactions
		BioReaction reaction = new BioReaction("reactionId");
		BioCompartment cpt = new BioCompartment("cpt");
		BioEnzyme enz = new BioEnzyme("enz");
		network.add(reaction);
		network.add(metabolite);
		network.add(cpt);
		network.affectToCompartment(metabolite, cpt);
		network.affectSubstrate(metabolite, 1.0, cpt, reaction);
		network.add(enz);
		network.affectSubUnit(metabolite, 1.0, enz);
		network.remove(metabolite);
		assertEquals("Metabolite not removed from the reaction", 0, reaction.getLeft().size());

		// Remove metabolite from compartments
		assertEquals("Metabolite not removed from the compartment", 0, cpt.getComponents().size());

		// Remove metabolite from enzymes
		assertEquals("Protein not removed from enzyme", 0, enz.getSubUnits().size());

		// Must return an exception
		network.remove(metabolite);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveGene() {

		// Remove one gene
		BioGene gene = new BioGene("geneId");
		network.add(gene);
		network.remove(gene);
		assertEquals("Gene not removed from the network", 0, network.getGenesView().size());

		// Test if the protein still contains the gene
		BioProtein protein = new BioProtein("prot");
		network.add(protein);
		network.affectGeneProduct(protein, gene);
		network.remove(gene);
		assertEquals("Gene not removed from the protein", 0, gene.getProteinList());

		// Must return an exception
		network.remove(gene);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveReaction() {

		// remove one reaction alone
		BioReaction reaction = new BioReaction("reactionId");
		network.add(reaction);
		network.remove(reaction);

		assertEquals("Reaction not removed from the network", 0, network.getReactionsView().size());

		// Test if the reaction is removed from a pathway
		BioPathway pathway = new BioPathway("pathway");
		network.add(pathway);
		network.add(reaction);
		network.affectToPathway(reaction, pathway);
		network.remove(reaction);
		assertEquals("Reaction not removed from pathway", 0, pathway.getReactions().size());

		// Must return an exception
		network.remove(reaction);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveCompartment() {

		// Remove one compartment alone
		BioCompartment cpt = new BioCompartment("cpt");
		network.add(cpt);
		network.remove(cpt);

		assertEquals("Compartment not removed from the network", 0, network.getCompartmentsView().size());

		// Test if reactants in reactions are removed
		BioReaction reaction = new BioReaction("reac");
		BioMetabolite met = new BioMetabolite("metId");
		network.add(met);
		network.add(cpt);
		network.add(reaction);
		network.affectToCompartment(met, cpt);
		network.affectSubstrate(met, 1.0, cpt, reaction);
		network.affectProduct(met, 1.0, cpt, reaction);

		network.remove(cpt);

		assertEquals("Substrate not removed from reaction when the compartment is removed", 0,
				reaction.getLeft().size());
		assertEquals("Product not removed from reaction when the compartment is removed", 0,
				reaction.getRight().size());

		// Must return an exception
		network.remove(cpt);

	}

	@Test
	public void testAffectSubstrate() {

		BioReaction reaction = new BioReaction("r1");
		network.add(reaction);
		BioMetabolite s1 = new BioMetabolite("s1");
		network.add(s1);
		BioProtein s2 = new BioProtein("s2");
		network.add(s2);
		BioCompartment cpt = new BioCompartment("cpt");

		network.affectSubstrate(s1, 1.0, cpt, reaction);
		network.affectSubstrate(s2, 1.0, cpt, reaction);

		assertEquals("Substrate not well added", 2, reaction.getLeftReactants().size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubstrateInNoCompartment() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);
		network.add(cpt);
		network.add(reaction);
		// The compartment has not been affected to the metabolite
		network.affectSubstrate(metabolite, 1.0, cpt, reaction);
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
		network.affectToCompartment(metabolite, cpt2);
		// The metabolite has been affected to an other compartment
		network.affectSubstrate(metabolite, 1.0, cpt, reaction);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubstrateReactionNotInTheNetwork() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);
		network.add(cpt);
		network.affectToCompartment(metabolite, cpt);
		network.affectSubstrate(metabolite, 1.0, cpt, reaction);
	}

	@Test
	public void testAffectProduct() {
		BioReaction reaction = new BioReaction("r1");
		network.add(reaction);
		BioMetabolite s1 = new BioMetabolite("s1");
		network.add(s1);
		BioProtein s2 = new BioProtein("s2");
		network.add(s2);
		BioCompartment cpt = new BioCompartment("cpt");

		network.affectProduct(s1, 1.0, cpt, reaction);
		network.affectProduct(s2, 1.0, cpt, reaction);

		assertEquals("Product not well added", 2, reaction.getRightReactants().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectProductInNoCompartment() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);
		network.add(cpt);
		network.add(reaction);
		network.affectProduct(metabolite, 1.0, cpt, reaction);
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
		network.affectToCompartment(metabolite, cpt2);
		network.affectProduct(metabolite, 1.0, cpt, reaction);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectProductReactionNotInTheNetwork() {
		BioReaction reaction = new BioReaction("reactionId");
		BioMetabolite metabolite = new BioMetabolite("metId");
		BioCompartment cpt = new BioCompartment("cptId");

		network.add(metabolite);
		network.add(cpt);
		network.affectToCompartment(metabolite, cpt);
		network.affectProduct(metabolite, 1.0, cpt, reaction);
	}

	@Test
	public void testAffectEnzyme() {

		BioReaction reaction = new BioReaction("reactionId");
		BioEnzyme enzyme = new BioEnzyme("enzymeId");
		network.add(reaction);
		network.add(enzyme);
		network.affectEnzyme(enzyme, reaction);

		assertEquals("Enzyme not affected to reaction", 1, enzyme.getReactions().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectEnzymeReactionNotPresent() {
		BioReaction reaction = new BioReaction("reactionId");
		BioEnzyme enzyme = new BioEnzyme("enzymeId");
		network.add(enzyme);
		network.affectEnzyme(enzyme, reaction);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectEnzymeNotPresent() {
		BioReaction reaction = new BioReaction("reactionId");
		BioEnzyme enzyme = new BioEnzyme("enzymeId");
		network.add(reaction);
		network.affectEnzyme(enzyme, reaction);

	}

	@Test
	public void testAffectSubUnit() {

		BioMetabolite unitMetabolite = new BioMetabolite("met");
		BioProtein unitProtein = new BioProtein("prot");
		BioEnzyme enz = new BioEnzyme("enz");

		network.add(unitMetabolite);
		network.add(unitProtein);
		network.add(enz);

		network.affectSubUnit(unitMetabolite, 1.0, enz);

		assertEquals("subunit not added to enzyme", 1, enz.getSubUnits().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubUnitEnzymeNotPresent() {

		BioProtein unit = new BioProtein("fakeId");
		BioEnzyme enz = new BioEnzyme("enz");

		network.add(unit);

		network.affectSubUnit(unit, 1.0, enz);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectSubUnitNotPresent() {

		BioProtein unit = new BioProtein("fakeId");
		BioEnzyme enz = new BioEnzyme("enz");

		network.add(enz);

		network.affectSubUnit(unit, 1.0, enz);

	}

	@Test
	public void testAffectGeneProduct() {

		BioProtein prot = new BioProtein("protId");
		BioGene gene = new BioGene("geneId");

		network.add(prot);
		network.add(gene);

		network.affectGeneProduct(prot, gene);

		assertEquals("Bad number of proteins coded by the gene", 1, gene.getProteinList());

		assertEquals("Gene not affected to protein", prot, gene.getProteinList().iterator().next());

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
	public void testAffectToPathway() {

		BioReaction reaction = new BioReaction("reacId");
		BioPathway pathway = new BioPathway("pathwayId");

		network.add(reaction);
		network.add(pathway);

		network.affectToPathway(reaction, pathway);

		assertEquals("Reaction not added to pathway", 1, pathway.getReactions().size());

		assertEquals("Bad reaction added to pathway", reaction, pathway.getReactions().iterator().next());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToPathwayNotPresent() {
		BioReaction reaction = new BioReaction("reacId");
		BioPathway pathway = new BioPathway("pathwayId");

		network.add(reaction);

		network.affectToPathway(reaction, pathway);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToPathwayReactionNotPresent() {

		BioReaction reaction = new BioReaction("reacId");
		BioPathway pathway = new BioPathway("pathwayId");

		network.add(pathway);

		network.affectToPathway(reaction, pathway);

	}

	@Test
	public void testAffectToCompartment() {

		BioMetabolite ent = new BioMetabolite("id");
		BioCompartment cpt = new BioCompartment("cpt");

		network.add(cpt);
		network.add(ent);

		network.affectToCompartment(ent, cpt);

		assertEquals("Compound not added to compartment", 1, cpt.getComponents().size());
		assertEquals("Compound badly added to the compartment", ent, cpt.getComponents().iterator().next());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToCompartmentNotPresent() {

		BioMetabolite ent = new BioMetabolite("id");
		BioCompartment cpt = new BioCompartment("cpt");

		network.add(ent);

		network.affectToCompartment(ent, cpt);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAffectToCompartmentEntityNotPresent() {

		BioMetabolite ent = new BioMetabolite("id");
		BioCompartment cpt = new BioCompartment("cpt");

		network.add(cpt);

		network.affectToCompartment(ent, cpt);

	}

	@Test
	public void testGetReactionsFromSubstrates() {
		BioReaction r1 = addTestReactionToNetwork();

		BioReaction r2 = new BioReaction("id2");
		network.add(r2);

		Set<BioMetabolite> substrates = new HashSet<BioMetabolite>();
		substrates.add(network.getMetabolitesView().getEntityFromId("s1"));
		substrates.add(network.getMetabolitesView().getEntityFromId("s2"));

		BioCollection<BioReaction> reactions = network.getReactionsFromSubstrates(substrates);

		assertEquals("Get the bad number of reactions with these substrates", 1, reactions.size());

		assertEquals("Get the bad reaction with these substrates", r1, reactions.iterator().next());

		BioMetabolite s3 = new BioMetabolite("id3");
		network.add(s3);

		substrates.add(s3);

		reactions = network.getReactionsFromSubstrates(substrates);

		assertEquals("No reaction exists with these substrates", 0, reactions.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReactionsFromSubstratesWithSubstrateAbsent() {

		BioMetabolite s1 = new BioMetabolite("id1");
		BioMetabolite s2 = new BioMetabolite("id2");
		network.add(s1);

		Set<BioMetabolite> substrates = new HashSet<BioMetabolite>();
		substrates.add(s1);
		substrates.add(s2);

		network.getReactionsFromSubstrates(substrates);

	}

	@Test
	public void testGetReactionsFromProducts() {
		BioReaction r1 = addTestReactionToNetwork();

		BioReaction r2 = new BioReaction("id2");
		network.add(r2);

		Set<BioMetabolite> products = new HashSet<BioMetabolite>();
		products.add(network.getMetabolitesView().getEntityFromId("p1"));
		products.add(network.getMetabolitesView().getEntityFromId("p2"));

		BioCollection<BioReaction> reactions = network.getReactionsFromProducts(products);

		assertEquals("Get the bad number of reactions with these products", 1, reactions.size());

		assertEquals("Get the bad reaction with these products", r1, reactions.iterator().next());

		BioMetabolite s3 = new BioMetabolite("id3");

		products.add(s3);
		network.add(s3);

		reactions = network.getReactionsFromProducts(products);

		assertEquals("No reaction exists with these products", 0, reactions.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReactionsFromProductsWithProductAbsent() {

		BioMetabolite s1 = new BioMetabolite("id1");
		BioMetabolite s2 = new BioMetabolite("id2");
		network.add(s1);

		Set<BioMetabolite> substrates = new HashSet<BioMetabolite>();
		substrates.add(s1);
		substrates.add(s2);

		network.getReactionsFromProducts(substrates);

	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetPhysicalEntitiesFromPathway() {

		BioPathway p = new BioPathway("id");
		network.add(p);

		BioReaction r = addTestReactionToNetwork();

		network.affectToPathway(r, p);

		BioCollection<BioMetabolite> subs = network.getPhysicalEntitiesFromPathway(p);

		assertEquals("Bad number of metabolites in pathway", 4, subs.size());

		network.remove(p);
		// Must return an exception
		network.getPhysicalEntitiesFromPathway(p);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPathwaysFromPhysicalEntities() {

		BioPathway p2 = new BioPathway("id2");
		network.add(p2);

		BioReaction r = addTestReactionToNetwork();

		BioMetabolite cpd = (BioMetabolite) r.getLeft().iterator().next();
		
		BioCollection<BioMetabolite> cpds = new BioCollection<BioMetabolite>();
		cpds.add(cpd);

		BioCollection<BioPathway> pathways = network.getPathwaysFromPhysicalEntities(cpds);

		assertEquals("Bad number of pathways containing this compound", 1, pathways.size());

		assertTrue("Bad pathway containing this compound",
				pathways.contains(network.getPathwaysView().getEntitiesFromName("pathway1")));

		BioMetabolite cpd2 = new BioMetabolite("id3");
		network.add(cpd2);

		cpds.remove(cpd);
		cpds.add(cpd2);

		pathways = network.getPathwaysFromPhysicalEntities(cpds);

		assertEquals("No pathway contains this compound", 0, pathways.size());

		network.remove(cpd2);
		// Must return an exception
		pathways = network.getPathwaysFromPhysicalEntities(cpds);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetReactionsFromGenes() {

		BioReaction r = addTestReactionToNetwork();
		BioReaction r2 = new BioReaction("id2");
		network.add(r2);

		Set<BioGene> genes = new HashSet<BioGene>();
		genes.add(network.getGenesView().getEntityFromId("g1"));
		genes.add(network.getGenesView().getEntityFromId("g2"));

		BioCollection<BioReaction> reactions = network.getReactionsFromGenes(genes);

		assertEquals("Bad number of reactions with these genes", 1, reactions.size());

		assertEquals("Bad reaction with these genes", r, reactions.iterator().next());

		BioGene g3 = new BioGene("g3");
		genes.add(g3);
		// Must return an exception
		network.getReactionsFromGenes(genes);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesFromReactions() {

		BioReaction r = addTestReactionToNetwork();
		BioReaction r2 = new BioReaction("id2");
		network.add(r2);

		BioGene g3 = new BioGene("g3");
		network.add(g3);

		Set<BioReaction> reactions = new HashSet<BioReaction>();
		reactions.add(r);
		reactions.add(r2);

		BioCollection<BioGene> genes = network.getGenesFromReactions(reactions);

		assertEquals("Bad number of genes with these reactions", 2, genes.size());

		BioReaction r3 = new BioReaction("id3");
		reactions.add(r3);
		// Must return an exception
		network.getGenesFromReactions(reactions);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetGenesFromPathways() {

		BioReaction r = addTestReactionToNetwork();

		BioPathway p = network.getPathwaysView().getEntityFromId("pathway1");

		Set<BioPathway> pathways = new HashSet<BioPathway>();
		pathways.add(p);

		BioGene g3 = new BioGene("g3");
		network.add(g3);

		BioCollection<BioGene> genes = network.getGenesFromPathways(pathways);

		assertEquals("Bad number of genes in this pathway", 2, genes.size());

		BioCollection<BioGene> genesTest = new BioCollection<BioGene>();
		genesTest.add(network.getGenesView().getEntityFromId("g1"));
		genesTest.add(network.getGenesView().getEntityFromId("g2"));

		assertEquals("Bad genes in this pathway", genesTest, genes);

		BioPathway p2 = new BioPathway("pathway2");

		pathways.add(p2);
		// Must return an exception
		network.getGenesFromPathways(pathways);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPathwaysFromGenes() {
		BioReaction r = addTestReactionToNetwork();
		Set<BioGene> genes = new HashSet<BioGene>();
		genes.add(network.getGenesView().getEntityFromId("g1"));

		BioPathway p2 = new BioPathway("pathway2");
		network.add(p2);

		BioCollection<BioPathway> pathways = network.getPathwaysFromGenes(genes);

		assertEquals("Bad number of pathways with these genes", 1, pathways.size());

		assertTrue("Bad pathway with these genes",
				pathways.contains(network.getPathwaysView().getEntityFromId("pathway1")));

		BioGene gene3 = new BioGene("gene3");

		genes.add(gene3);
		// Must return an exception
		network.getPathwaysFromGenes(genes);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPathwaysFromReactions() {

		BioReaction r = addTestReactionToNetwork();

		BioPathway p2 = new BioPathway("pathway2");
		network.add(p2);

		Set<BioReaction> reactions = new HashSet<BioReaction>();
		reactions.add(r);

		BioCollection<BioPathway> pathways = network.getPathwaysFromReactions(reactions);

		assertEquals("Bad number of pathways with this reaction", 1, pathways.size());

		assertTrue("Bad pathway with these reactions",
				pathways.contains(network.getPathwaysView().getEntityFromId("pathway1")));

		BioReaction r2 = new BioReaction("r2");
		reactions.add(r2);
		// Must return an exception
		network.getPathwaysFromReactions(reactions);

	}

	/**
	 * 
	 * @return a reaction with two substrates, two products, one enzyme linked
	 *         to two genes All objects have been added to the network
	 */
	public BioReaction addTestReactionToNetwork() {
		BioReaction r = new BioReaction("id");

		BioMetabolite s1 = new BioMetabolite("s1");
		BioMetabolite s2 = new BioMetabolite("s2");
		BioMetabolite p1 = new BioMetabolite("p1");
		BioMetabolite p2 = new BioMetabolite("p2");
		BioCompartment cpt = new BioCompartment("cpt");

		network.add(r);
		network.add(s1);
		network.add(s2);
		network.add(cpt);

		network.affectSubstrate(s1, 1.0, cpt, r);
		network.affectSubstrate(s2, 1.0, cpt, r);
		network.affectProduct(p1, 1.0, cpt, r);
		network.affectProduct(p2, 1.0, cpt, r);

		BioPathway p = new BioPathway("pathway1");
		network.add(p);

		network.affectToPathway(r, p);

		BioEnzyme e = new BioEnzyme("e");
		network.add(e);
		BioProtein prot1 = new BioProtein("p1");
		network.add(p1);
		BioProtein prot2 = new BioProtein("p2");
		network.add(p2);
		BioGene g1 = new BioGene("g1");
		network.add(g1);
		BioGene g2 = new BioGene("g2");
		network.add(g2);

		network.affectGeneProduct(prot1, g1);
		network.affectGeneProduct(prot2, g2);

		network.affectSubUnit(p1, 1.0, e);
		network.affectSubUnit(p2, 1.0, e);

		network.affectEnzyme(e, r);

		return r;

	}

}
