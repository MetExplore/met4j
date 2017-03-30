/**
 * 3 déc. 2013 
 */
package fr.inra.toulouse.metexplore.met4j_core.mock;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.UnitSbml;

/**
 * @author lcottret 3 déc. 2013
 * 
 */
public class TestEqualityNetworks {

	BioNetwork networkRef;
	BioNetwork networkTest;

	public TestEqualityNetworks(BioNetwork networkRef, BioNetwork networkTest) {

		this.networkRef = networkRef;
		this.networkTest = networkTest;

	}

	public void testAll() {

		this.testNumberUnitDefinitions();
		this.testNumberCompartments();
		this.testNumberReactions();
		this.testNumberMetabolites();
		this.testNumberGenes();
		this.testNumberPathways();
		this.testNumberProteins();
		this.testNumberEnzymes();
		this.testAttributesUnitDefinitions();
		this.testAttributesMetabolites();
		this.testAttributesReactions();
		this.testAttributesGenes();
		this.testAttributesProteins();
		this.testAttributeEnzymes();

	}

	public void testNumberUnitDefinitions() {
		int nUnitDefinitionsRef = networkRef.getUnitDefinitions().size();
		int nUnitDefinitionsTest = networkTest.getUnitDefinitions().size();
		assertEquals("Test number of unit definitions", nUnitDefinitionsRef,
				nUnitDefinitionsTest, 0);
		return;
	}

	public void testNumberCompartments() {
		int nCompartmentsRef = networkRef.getCompartments().size();
		int nCompartmentsTest = networkTest.getCompartments().size();
		assertEquals("Test number of compartments", nCompartmentsRef,
				nCompartmentsTest, 0);

		return;
	}

	public void testNumberReactions() {
		int nReactionsRef = networkRef.getBiochemicalReactionList().size();
		int nReactionsTest = networkTest.getBiochemicalReactionList().size();
		assertEquals("Test number of reactions", nReactionsRef, nReactionsTest,
				0);

		return;
	}

	public void testNumberMetabolites() {
		int nMetabolitesRef = networkRef.getPhysicalEntityList().size();
		int nMetabolitesTest = networkTest.getPhysicalEntityList().size();
		assertEquals("Test number of metabolites", nMetabolitesRef,
				nMetabolitesTest, 0);

		return;
	}

	public void testNumberGenes() {
		int nGenesRef = networkRef.getGeneList().size();
		int nGenesTest = networkTest.getGeneList().size();
		assertEquals("Test number of genes", nGenesRef, nGenesTest, 0);

		return;
	}

	public void testNumberPathways() {
		int nPathwaysRef = networkRef.getPathwayList().size();
		int nPathwaysTest = networkTest.getPathwayList().size();
		assertEquals("Test number of pathways", nPathwaysRef, nPathwaysTest, 0);

		return;
	}

	public void testNumberProteins() {
		int nProteinsRef = networkRef.getProteinList().size();
		int nProteinsTest = networkTest.getProteinList().size();
		assertEquals("Test number of proteins", nProteinsRef, nProteinsTest, 0);

		return;
	}

	public void testNumberEnzymes() {
		int nRef = networkRef.getEnzymeList().size();
		int n = networkTest.getEnzymeList().size();
		assertEquals("Test number of enzymes", nRef, n, 0);

		return;
	}

	public void testAttributesUnitDefinitions() {

		Set<String> unitsRef = networkRef.getUnitDefinitions().keySet();
		Set<String> unitsTest = networkTest.getUnitDefinitions().keySet();
		assertEquals("Test ids of unitDefinitions", unitsRef, unitsTest);

		for (String unitId : unitsRef) {
			BioUnitDefinition unitRef = networkRef.getUnitDefinitions().get(
					unitId);
			BioUnitDefinition unitTest = networkRef.getUnitDefinitions().get(
					unitId);

			String unitNameRef = unitRef.getName();
			String unitNameTest = unitTest.getName();

			assertEquals("Test name of the unitDefinition " + unitId,
					unitNameRef, unitNameTest);

			Set<String> unitsSbmlRef = unitRef.getUnits().keySet();
			Set<String> unitsSbmlTest = unitTest.getUnits().keySet();

			assertEquals("Test unitSbmls of unitDefinition " + unitId,
					unitsSbmlRef, unitsSbmlTest);

			for (String unitSbmlId : unitsSbmlRef) {
				UnitSbml unitSbmlRef = unitRef.getUnits().get(unitSbmlId);
				UnitSbml unitSbmlTest = unitTest.getUnits().get(unitSbmlId);

				String unitSbmlKindRef = unitSbmlRef.getKind();
				String unitSbmlKindTest = unitSbmlTest.getKind();

				assertEquals("Test kind of the unitSbml " + unitSbmlId,
						unitSbmlKindRef, unitSbmlKindTest);

				String unitSbmlExpRef = unitSbmlRef.getExponent();
				String unitSbmlExpTest = unitSbmlTest.getExponent();

				assertEquals("Test exponent of the unitSbml " + unitSbmlId,
						unitSbmlExpRef, unitSbmlExpTest);

				String unitSbmlMultRef = unitSbmlRef.getMultiplier();
				String unitSbmlMultTest = unitSbmlTest.getMultiplier();

				assertEquals("Test multiplier of the unitSbml " + unitSbmlId,
						unitSbmlMultRef, unitSbmlMultTest);

				String unitSbmlScaleRef = unitSbmlRef.getScale();
				String unitSbmlScaleTest = unitSbmlTest.getScale();

				assertEquals("Test scale of the unitSbml " + unitSbmlId,
						unitSbmlScaleRef, unitSbmlScaleTest);

			}
		}
	}

	public void testAttributesMetabolites() {
		Set<String> idMetabolitesRef = networkRef.getPhysicalEntityList()
				.keySet();
		Set<String> idMetabolitesTest = networkTest.getPhysicalEntityList()
				.keySet();
		assertEquals("Test ids of metabolites", idMetabolitesRef,
				idMetabolitesTest);

		for (String cpdId : idMetabolitesRef) {
			BioPhysicalEntity cpdRef = networkRef.getPhysicalEntityList().get(
					cpdId);
			String cpdNameRef = cpdRef.getName();

			BioPhysicalEntity cpdTest = networkTest.getPhysicalEntityList()
					.get(cpdId);
			String cpdNameTest = cpdTest.getName();
			assertEquals("Test name of the metabolite " + cpdId, cpdNameRef,
					cpdNameTest);

			String formulaRef = cpdRef.getChemicalFormula();
			String formulaTest = cpdTest.getChemicalFormula();
			assertEquals("Test formula of the metabolite " + cpdId, formulaRef,
					formulaTest);

			String weightRef = cpdRef.getMolecularWeight();
			String weightTest = cpdTest.getMolecularWeight();

			weightRef = weightRef.replaceAll("[^0-9\\.]", "");
			weightTest = weightRef.replaceAll("[^0-9\\.]", "");

			Float weightFloatRef = null;
			Float weightFloatTest = null;
			try {
				weightFloatRef = Float.parseFloat(weightRef);
				weightFloatTest = Float.parseFloat(weightTest);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				System.err
						.println("[Test Warning] The weight for the metabolite "
								+ cpdId
								+ "can't be transformed in float (ref : "
								+ weightRef + ", test : " + weightTest);
			}

			if (weightFloatRef != null && weightFloatTest != null) {
				assertEquals("Test weight (float) of the metabolite " + cpdId,
						weightFloatRef, weightFloatTest);
			}

			Boolean isGenericRef = cpdRef.getIsHolderClass();
			Boolean isGenericTest = cpdTest.getIsHolderClass();
			assertEquals("Test isGeneric of the metabolite " + cpdId,
					isGenericRef, isGenericTest);

			String compartmentRef = cpdRef.getCompartment().getId();
			String compartmentTest = cpdTest.getCompartment().getId();
			assertEquals("Test compartment of the metabolite " + cpdId,
					compartmentRef, compartmentTest);

			String chargeRef = cpdRef.getCharge();
			String chargeTest = cpdTest.getCharge();
			assertEquals("Test charge of the metabolite " + cpdId, chargeRef,
					chargeTest);

		}

		return;
	}

	public void testAttributesReactions() {
		Set<String> idReactionsRef = networkRef.getBiochemicalReactionList()
				.keySet();
		Set<String> idReactionsTest = networkTest.getBiochemicalReactionList()
				.keySet();
		assertEquals("Test ids of reactions", idReactionsRef, idReactionsTest);

		for (String reactionId : idReactionsRef) {
			BioReaction reactionRef = networkRef
					.getBiochemicalReactionList().get(reactionId);
			BioReaction reactionTest = networkTest
					.getBiochemicalReactionList().get(reactionId);

			String reactionNameRef = reactionRef.getName();
			String reactionNameTest = reactionTest.getName();
			assertEquals("Test name of the reaction " + reactionId,
					reactionNameRef, reactionNameTest);

			Boolean revRef = reactionRef.isReversible();
			Boolean revTest = reactionTest.isReversible();
			assertEquals("Test reversibility of the reaction " + reactionId,
					revRef, revTest);

			String ecRef = reactionRef.getEcNumber();
			String ecTest = reactionTest.getEcNumber();
			assertEquals("Test EC of the reaction " + reactionId, ecRef, ecTest);

			Boolean isHoleRef = reactionRef.getHole();
			Boolean isHoleTest = reactionTest.getHole();
			assertEquals("Test isHole of the reaction " + reactionId,
					isHoleRef, isHoleTest);

			Boolean isGenericRef = reactionRef.getIsGenericReaction();
			Boolean isGenericTest = reactionTest.getIsGenericReaction();
			assertEquals("Test isGeneric of the reaction " + reactionId,
					isGenericRef, isGenericTest);


			Set<String> leftsRef = reactionRef.getLeftList().keySet();
			Set<String> leftsTest = reactionTest.getLeftList().keySet();
			assertEquals(
					"Test left participants of the reaction " + reactionId,
					leftsRef, leftsTest);

			Set<String> rightsRef = reactionRef.getRightList().keySet();
			Set<String> rightsTest = reactionTest.getRightList().keySet();
			assertEquals("Test right participants of the reaction "
					+ reactionId, rightsRef, rightsTest);

			Set<String> cofactorsRef = reactionRef.getCofactors();
			Set<String> cofactorsTest = reactionTest.getCofactors();
			assertEquals("Test cofactors of the reaction " + reactionId,
					cofactorsRef, cofactorsTest);

			Set<String> sidesRef = reactionRef.getSideCompounds();
			Set<String> sidesTest = reactionTest.getSideCompounds();
			assertEquals("Test side compounds of the reaction " + reactionId,
					sidesRef, sidesTest);

			Set<String> pathwaysRef = reactionRef.getPathwayList().keySet();
			Set<String> pathwaysTest = reactionTest.getPathwayList().keySet();
			assertEquals("Test pathways of the reaction " + reactionId,
					pathwaysRef, pathwaysTest);
			
			Set<String> enzymesRef = reactionRef.getEnzList().keySet();
			Set<String> enzymesTest = reactionTest.getEnzList().keySet();
			
			assertEquals("Test enzymes of the reaction " + reactionId,
					enzymesRef, enzymesTest);

		}
	}

	public void testAttributesGenes() {
		Set<String> genesRef = networkRef.getGeneList().keySet();
		Set<String> genesTest = networkTest.getGeneList().keySet();
		assertEquals("Test ids of genes ", genesRef, genesTest);

		for (String geneId : genesRef) {
			BioGene geneRef = networkRef.getGeneList().get(geneId);
			BioGene geneTest = networkTest.getGeneList().get(geneId);

			String geneNameRef = geneRef.getName();
			String geneNameTest = geneTest.getName();
			assertEquals("Test name of the gene " + geneId, geneNameRef,
					geneNameTest);

			Set<String> proteinsRef = networkRef.getProteinList().keySet();
			Set<String> proteinsTest = networkTest.getProteinList().keySet();
			assertEquals("Test proteins of the gene " + geneId, proteinsRef,
					proteinsTest);

		}
	}

	public void testAttributesProteins() {

		Set<String> proteinsRef = networkRef.getProteinList().keySet();
		Set<String> proteinsTest = networkTest.getProteinList().keySet();
		assertEquals("Test ids of proteins ", proteinsRef, proteinsTest);

		for (String proteinId : proteinsRef) {
			BioProtein proteinRef = networkRef.getProteinList().get(proteinId);
			BioProtein proteinTest = networkTest.getProteinList()
					.get(proteinId);

			String proteinNameRef = proteinRef.getName();
			String proteinNameTest = proteinTest.getName();
			assertEquals("Test name of the protein " + proteinId,
					proteinNameRef, proteinNameTest);

			Set<String> genesRef = proteinRef.getGeneList().keySet();
			Set<String> genesTest = proteinTest.getGeneList().keySet();
			assertEquals("Test set of genes of protein " + proteinId, genesRef,
					genesTest);
		}
	}

	public void testAttributeEnzymes() {
		Set<String> enzymesRef = networkRef.getEnzymeList().keySet();
		Set<String> enzymesTest = networkTest.getEnzymeList().keySet();
		assertEquals("Test ids of proteins ", enzymesRef, enzymesTest);

		for (String id : enzymesRef) {
			BioPhysicalEntity enzymeRef = networkRef.getEnzymeList().get(id);
			BioPhysicalEntity enzymeTest = networkTest.getEnzymeList().get(id);

			String nameRef = enzymeRef.getName();
			String nameTest = enzymeTest.getName();
			assertEquals("Test name of the enzyme " + id, nameRef, nameTest);

			Set<String> componentRefs = new HashSet<String>();
			if (enzymeRef.getClass() == BioComplex.class) {
				componentRefs.addAll(((BioComplex) enzymeRef)
						.getAllComponentList().keySet());
			} else {
				componentRefs.add(enzymeRef.getId());
			}

			Set<String> componentTests = new HashSet<String>();
			if (enzymeTest.getClass() == BioComplex.class) {
				componentTests.addAll(((BioComplex) enzymeTest)
						.getAllComponentList().keySet());
			} else {
				componentTests.add(enzymeTest.getId());
			}

			
			assertEquals("Test number of components of the enzyme " + id,
					componentRefs.size(), componentTests.size());

			assertEquals("Test ids of the components of the enzyme " + id,
					componentRefs, componentTests);

		}
	}

}
