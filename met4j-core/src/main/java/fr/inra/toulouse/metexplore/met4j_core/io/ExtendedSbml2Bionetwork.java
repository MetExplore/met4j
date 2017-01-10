/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.io.IOException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Comment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Flux;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.UnitSbml;
import fr.inra.toulouse.metexplore.met4j_core.io.XMLUtils;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

public class ExtendedSbml2Bionetwork {

	BioNetwork bioNetwork;
	String inputFile = null;

	BioCompartment fakeCompartment;

	/**
	 * Constructor from a file
	 * 
	 * @param inputFile
	 */
	public ExtendedSbml2Bionetwork(String inputFile) {

		bioNetwork = new BioNetwork();

		fakeCompartment = new BioCompartment();
		fakeCompartment.setAsFakeCompartment();

		this.inputFile = inputFile;

		this.convert();
	}

	/**
	 * Constructor from a file and a default compartment
	 * 
	 * The compartments for the enzymes/proteins/genes will be the
	 * defaultCompartment put as parameter
	 * 
	 * @param inputFile
	 * @param defaultCompartment
	 */
	public ExtendedSbml2Bionetwork(String inputFile, BioCompartment defaultCompartment) {

		bioNetwork = new BioNetwork();

		fakeCompartment = defaultCompartment;

		this.inputFile = inputFile;

		this.convert();
	}

	public void convert() {
		try {

			bioNetwork.addCompartment(this.fakeCompartment);

			long startTime = System.nanoTime();
			Document document = XMLUtils.open(inputFile);

			NodeList tmp = document.getElementsByTagName("model");
			Element model = (Element) tmp.item(0);
			long elapsedTime = System.nanoTime() - startTime;

			if (elapsedTime / 1000000000 == 0) {
				System.err.println("Time to read the xml nodes in miliseconde :" + elapsedTime / 1000000);
			} else {
				System.err.println("Time to read the xml nodes object in seconde :" + elapsedTime / 1000000000);
			}

			startTime = System.nanoTime();

			parseSbmlListOfUnitDefinitions(document);
			parseSbmlListOfCompartments(document);
			parseSbmlListOfCompounds(document);
			parseSbmlListOfReactions(document);

			bioNetwork.setId(model.getAttribute("id"));
			bioNetwork.setName(model.getAttribute("name"));
			bioNetwork.setType("sbml");

			elapsedTime = System.nanoTime() - startTime;

			if (elapsedTime / 1000000000 == 0) {
				System.err.println("Time to generate the bionetwork object in miliseconde :" + elapsedTime / 1000000);
			} else {
				System.err.println("Time to generate the bionetwork object in seconde :" + elapsedTime / 1000000000);
			}

		} catch (IOException e) {
			System.err.println("Error while reading sbml file :" + e.getMessage());
			bioNetwork = null;
		} catch (SAXException e) {
			System.err.println("Error while reading sbml file :" + e.getMessage());
			bioNetwork = null;
		}

	}

	public void parseSbmlListOfUnitDefinitions(Document document) {
		NodeList listOfUnitDefinitions = document.getElementsByTagName("listOfUnitDefinitions");

		if (listOfUnitDefinitions != null) {

			if (listOfUnitDefinitions.getLength() > 0) {
				NodeList unitDefinitions = ((Element) (listOfUnitDefinitions.item(0)))
						.getElementsByTagName("unitDefinition");

				int nbUnitDefinitions = unitDefinitions.getLength();

				// IMPORTANT: The loop adds 2 at each iteration... I don't know
				// why, but there are

				for (int i = 0; i < nbUnitDefinitions; i++) {
					Element unitDefinitionElt = (Element) unitDefinitions.item(i);
					String unitDefinitionId = unitDefinitionElt.getAttribute("id");
					String unitDefinitionName = unitDefinitionElt.getAttribute("name");

					if (unitDefinitionName == null) {
						unitDefinitionName = "";
					}

					BioUnitDefinition unitDefinition = new BioUnitDefinition(unitDefinitionId, unitDefinitionName);

					NodeList listOfUnits = unitDefinitionElt.getElementsByTagName("listOfUnits");

					if (listOfUnits != null && listOfUnits.getLength() > 0) {

						NodeList units = listOfUnits.item(0).getChildNodes();
						int nbUnits = units.getLength();

						for (int j = 1; j < nbUnits; j = j + 2) {
							Element unitElt = (Element) units.item(j);

							String kind = unitElt.getAttribute("kind");
							if (kind == null) {
								kind = "";
							}
							String multiplier = unitElt.getAttribute("multiplier");
							if (multiplier == null) {
								multiplier = "";
							}
							String scale = unitElt.getAttribute("scale");
							if (scale == null) {
								scale = "";
							}
							String exponent = unitElt.getAttribute("exponent");
							if (exponent == null) {
								exponent = "";
							}

							UnitSbml unit = new UnitSbml(kind, exponent, scale, multiplier);

							unitDefinition.addUnit(unit);

						}

					}
					this.getBioNetwork().addUnitDefinition(unitDefinition);
				}
			}
		} else {
			throw new RuntimeException("Incorrect Format: Can't find node [listOfUnidefinitions]");
		}
	}

	/**
	 * @author LC
	 * @param document
	 */
	public void parseSbmlListOfCompartments(Document document) {
		NodeList listOfCompartments = document.getElementsByTagName("listOfCompartments");

		// Check if everything is OK.
		if (listOfCompartments == null)
			throw new RuntimeException("Incorrect Format: Can't find node [listOfCompartments]");

		if (listOfCompartments.getLength() > 1)
			throw new RuntimeException("Incorrect Format: More than one node [listOfCompartments]");

		NodeList compartments = listOfCompartments.item(0).getChildNodes();

		int compartmentCount = compartments.getLength();

		// IMPORTANT: The loop adds 2 at each iteration... I don't know why, but
		// there are
		// some
		for (int i = 1; i < compartmentCount; i = i + 2) {
			Element compartment = (Element) compartments.item(i);
			String compartmentId = StringUtils.sbmlDecode(compartment.getAttribute("id"));
			String compartmentName = StringUtils.sbmlDecode(compartment.getAttribute("name"));

			if (compartmentId == null) {
				compartmentId = "NA";
			}
			if (compartmentName == null) {
				compartmentName = compartmentId;
			}

			String outside = StringUtils.sbmlDecode(compartment.getAttribute("outside"));

			BioCompartment comp = new BioCompartment(compartmentName, compartmentId);

			this.bioNetwork.addCompartment(comp);

			if (outside != null && !outside.contentEquals("NIL") && !outside.contentEquals("")) {

				BioCompartment outsideCompartment = this.bioNetwork.getCompartments().get(outside);

				if (outsideCompartment == null) {
					outsideCompartment = new BioCompartment(outside, outside);

					this.bioNetwork.addCompartment(outsideCompartment);
				}
				comp.setOutsideCompartment(outsideCompartment);
			}
		}
	}

	/**
	 * @author LC from Paulo Milreu
	 * @param document
	 */
	public void parseSbmlListOfCompounds(Document document) {
		// get the element "listOfSpecies"
		NodeList listOfSpecies = document.getElementsByTagName("listOfSpecies");

		// Check if everything is OK.
		if (listOfSpecies == null)
			throw new RuntimeException("Incorrect Format: Can't find node [listOfSpecies]");

		if (listOfSpecies.getLength() > 1)
			throw new RuntimeException("Incorrect Format: More than one node [listOfSpecies]");

		// print all child elements from listOfSpecies
		NodeList listOfCompounds = listOfSpecies.item(0).getChildNodes();
		int compoundCount = listOfCompounds.getLength();
		// IMPORTANT: The loop adds 2 at each iteration... I don't know why, but
		// there are
		// some
		for (int i = 1; i < compoundCount; i = i + 2) {
			Element compound = (Element) listOfCompounds.item(i);
			String cpdId = StringUtils.sbmlDecode(compound.getAttribute("id"));
			String cpdName = StringUtils.sbmlDecode(compound.getAttribute("name"));
			String compartment = StringUtils.sbmlDecode(compound.getAttribute("compartment"));

			String charge;
			if (compound.hasAttribute("charge")) {
				charge = StringUtils.sbmlDecode(compound.getAttribute("charge"));
			} else {
				charge = "0";
			}

			if (charge == null) {
				charge = "0";
			}

			String boundaryConditionStr = compound.getAttribute("boundaryCondition");

			Boolean boundaryCondition = false;

			if (boundaryConditionStr != null && boundaryConditionStr.compareToIgnoreCase("true") == 0) {
				boundaryCondition = true;
			}

			String formula = compound.getAttribute("formula");
			
			System.err.println(cpdId+" Formula : "+formula);

			if (formula == null || formula.equals("")) {
				formula = "NA";
			}

			String mass = compound.getAttribute("mass");

			if (mass == null || mass.equals("")) {
				mass = "NA";
			}

			Boolean generic = false;

			String genericStr = compound.getAttribute("generic");

			if (genericStr != null && genericStr.compareToIgnoreCase("true") == 0) {
				generic = true;
			}

			BioPhysicalEntity cpd;

			cpd = new BioPhysicalEntity(cpdId, cpdName);

			if (!this.bioNetwork.getCompartments().containsKey(compartment)) {
				BioCompartment bioCompartment = new BioCompartment(compartment, compartment);
				this.bioNetwork.addCompartment(bioCompartment);
			}

			BioCompartment bioCompartment = this.bioNetwork.getCompartments().get(compartment);

			if (bioCompartment == null) {
				throw new RuntimeException(
						"Incorrect Format: Compartment " + compartment + " does not exist in the list of compartments");
			}

			cpd.setCharge(charge);

			cpd.setCompartment(bioCompartment);

			cpd.setBoundaryCondition(boundaryCondition);
			cpd.setChemicalFormula(formula);
			cpd.setIsHolderClass(generic);
			cpd.setMolecularWeight(mass);

			bioNetwork.addPhysicalEntity(cpd);
			// compounds.put(compound.getAttribute("id"), new
			// Compound(compound.getAttribute("id"),
			// compound.getAttribute("name"),
			// compound.getAttribute("compartment")));
			// System.out.println(compound.getAttribute("name"));

		}
	}

	public void parseSbmlListOfReactions(Document document) {
		// get the element "listOfReactions"
		NodeList listOfReactions = document.getElementsByTagName("listOfReactions");

		// Check if everything is OK.
		if (listOfReactions == null)
			throw new RuntimeException("Incorrect Format: Can't find node [listOfReactions]");

		if (listOfReactions.getLength() > 1)
			throw new RuntimeException("Incorrect Format: More than one node [listOfReactions]");

		// print all child elements from listOfReactions
		NodeList reactionsList = listOfReactions.item(0).getChildNodes();
		int reactionCount = reactionsList.getLength();
		// IMPORTANT: The loop adds 2 at each iteration... I don't know why, but
		// there are
		// some
		for (int i = 1; i < reactionCount; i = i + 2) {
			Element reaction = (Element) reactionsList.item(i);

			BioChemicalReaction rxn = new BioChemicalReaction(StringUtils.sbmlDecode(reaction.getAttribute("id")),
					StringUtils.sbmlDecode(reaction.getAttribute("name")));

			String rev = reaction.getAttribute("reversible");

			if (rev.equals("")) {
				rev = "true";
			}

			rxn.setReversibility("true".equals(rev));

			String ec = reaction.getAttribute("ec");

			if (ec == null) {
				ec = "";
			}

			rxn.setEcNumber(ec);

			parseSbmlReaction(reaction, rxn);

			bioNetwork.addBiochemicalReaction(rxn);
		}
	}

	public void parseSbmlReaction(Element reaction, BioChemicalReaction rxn) {

		NodeList enzymeNodes = reaction.getElementsByTagName("enzyme");

		int nbEnzymes = enzymeNodes.getLength();

		for (int i = 0; i < nbEnzymes; i++) {

			Element enzymeNode = (Element) enzymeNodes.item(i);
			String enzymeId = StringUtils.sbmlDecode(enzymeNode.getAttribute("id"));
			String enzymeName = StringUtils.sbmlDecode(enzymeNode.getAttribute("name"));

			BioComplex enzyme;

			if (!bioNetwork.getComplexList().containsKey(enzymeId)) {
				enzyme = new BioComplex(enzymeId, enzymeName);

				enzyme.setCompartment(fakeCompartment);

				bioNetwork.addComplex(enzyme);
			}

			enzyme = bioNetwork.getComplexList().get(enzymeId);

			rxn.addEnz(enzyme);

			NodeList proteinNodes = enzymeNode.getElementsByTagName("protein");

			for (int j = 0; j < proteinNodes.getLength(); j++) {

				Element proteinNode = (Element) proteinNodes.item(j);
				String proteinId = StringUtils.sbmlDecode(proteinNode.getAttribute("id"));
				String proteinName = StringUtils.sbmlDecode(proteinNode.getAttribute("name"));

				BioProtein protein;

				if (!bioNetwork.getProteinList().containsKey(proteinId)) {
					protein = new BioProtein(proteinId, proteinName);

					protein.setCompartment(fakeCompartment);

					bioNetwork.addProtein(protein);
				}

				protein = bioNetwork.getProteinList().get(proteinId);
				enzyme.addComponent(new BioPhysicalEntityParticipant(protein));

				NodeList listOfGenes = proteinNode.getElementsByTagName("gene");

				for (int k = 0; k < listOfGenes.getLength(); k++) {

					Element geneNode = (Element) listOfGenes.item(k);
					String geneId = StringUtils.sbmlDecode(geneNode.getAttribute("id"));
					String geneName = StringUtils.sbmlDecode(geneNode.getAttribute("name"));

					BioGene gene;
					if (!bioNetwork.getGeneList().containsKey(geneId)) {
						gene = new BioGene(geneId, geneName);

						gene.setCompartment(fakeCompartment);

						bioNetwork.addGene(gene);
					}

					gene = bioNetwork.getGeneList().get(geneId);

					protein.addGene(gene);
				}
			}
		}

		// Get score
		NodeList scoreNodes = reaction.getElementsByTagName("score");
		if (scoreNodes.getLength() > 1) {
			throw new RuntimeException("More than one SCORE tag in the reaction " + rxn.getId());
		}
		if (scoreNodes.getLength() == 1) {
			Element scoreNode = (Element) scoreNodes.item(0);
			rxn.setScore(scoreNode.getTextContent());
		}
		// Get status
		NodeList statusNodes = reaction.getElementsByTagName("status");
		if (statusNodes.getLength() > 1) {
			throw new RuntimeException("More than one STATUS tag in the reaction " + rxn.getId());
		}
		if (statusNodes.getLength() == 1) {
			Element statusNode = (Element) statusNodes.item(0);
			rxn.setStatus(statusNode.getTextContent());
		}

		// Get pmid
		NodeList pmidNodes = reaction.getElementsByTagName("pmid");
		if (statusNodes.getLength() > 0) {
			for (int i = 0; i < pmidNodes.getLength(); i++) {
				Element pmidNode = (Element) pmidNodes.item(i);
				rxn.addPmid(pmidNode.getTextContent());
			}
		}

		// Get pathways
		NodeList pathwayNodes = reaction.getElementsByTagName("pathway");

		for (int i = 0; i < pathwayNodes.getLength(); i++) {

			Element pathwayNode = (Element) pathwayNodes.item(i);

			String pathwayId = StringUtils.sbmlDecode(pathwayNode.getAttribute("id"));
			String pathwayName = StringUtils.sbmlDecode(pathwayNode.getAttribute("name"));

			BioPathway pathway;

			if (!bioNetwork.getPathwayList().containsKey(pathwayId)) {

				pathway = new BioPathway(pathwayId, pathwayName);

				bioNetwork.addPathway(pathway);

			}

			pathway = bioNetwork.getPathwayList().get(pathwayId);

			rxn.addPathway(pathway);

		}

		// Get comments
		NodeList commentNodes = reaction.getElementsByTagName("comment");

		for (int i = 0; i < commentNodes.getLength(); i++) {

			Element commentNode = (Element) commentNodes.item(i);

			String annotator = "NA";

			NodeList annotatorNodes = commentNode.getElementsByTagName("annotator");

			if (annotatorNodes.getLength() > 1) {
				throw new RuntimeException(
						"More than one annotator tag in the comment " + i + " in the reaction " + rxn.getId());
			}
			if (annotatorNodes.getLength() == 1) {
				Element annotatorNode = (Element) annotatorNodes.item(0);
				annotator = annotatorNode.getTextContent();
			}

			NodeList textNodes = commentNode.getElementsByTagName("text");

			if (textNodes.getLength() > 1) {
				throw new RuntimeException(
						"More than one text tag in the comment " + i + " in the reaction " + rxn.getId());
			}
			if (textNodes.getLength() == 0) {
				throw new RuntimeException("No text tag in the comment " + i + " in the reaction " + rxn.getId());
			}

			Element textNode = (Element) textNodes.item(0);
			String text = textNode.getTextContent();

			text = text.replaceAll("\\s+", " ");

			Comment comment = new Comment(text, annotator);

			rxn.addComment(comment);

		}

		// print all child elements from listOfReactions
		NodeList listsFromReaction = reaction.getChildNodes();

		int listsCount = listsFromReaction.getLength();
		for (int j = 0; j < listsCount; j++) {

			NodeList listOfReactants, listOfProducts;

			if ("listOfReactants".equals(listsFromReaction.item(j).getNodeName())) {
				listOfReactants = listsFromReaction.item(j).getChildNodes();
				int reactantsCount = listOfReactants.getLength();

				for (int i = 1; i < reactantsCount; i = i + 2) {
					Element reactant = (Element) listOfReactants.item(i);

					// Finds the compound
					BioPhysicalEntity c = bioNetwork.getPhysicalEntityList()
							.get(StringUtils.sbmlDecode(reactant.getAttribute("species")));

					if (c != null) {
						String coeff = reactant.getAttribute("stoichiometry");
						BioCompartment compartment = c.getCompartment();

						BioPhysicalEntityParticipant cpdParticipant = new BioPhysicalEntityParticipant(
								rxn.getId() + "__With__" + c.getId(), c, coeff, compartment);

						rxn.addLeftParticipant(cpdParticipant);

						if (rxn.getReversiblity().compareToIgnoreCase("irreversible-left-to-right") == 0) {
							c.addReactionAsSubstrate(rxn);
						} else if (rxn.getReversiblity().compareToIgnoreCase("irreversible-right-to-left") == 0) {
							c.addReactionAsProduct(rxn);
						} else {
							c.addReactionAsProduct(rxn);
							c.addReactionAsSubstrate(rxn);
						}

						// rxn.addLeftParticipant(p)(c);
						// c.addSubstrateOf(r);
						// if(r.reversible) {
						// c.addProducedBy(r);
						// }
					}
				}
			}

			if ("listOfProducts".equals(listsFromReaction.item(j).getNodeName())) {
				listOfProducts = listsFromReaction.item(j).getChildNodes();
				int productsCount = listOfProducts.getLength();

				for (int i = 1; i < productsCount; i = i + 2) {
					Element product = (Element) listOfProducts.item(i);

					// Finds the compound
					BioPhysicalEntity c = bioNetwork.getPhysicalEntityList()
							.get(StringUtils.sbmlDecode(product.getAttribute("species")));

					if (c != null) {
						String coeff = product.getAttribute("stoichiometry");
						BioCompartment compartment = c.getCompartment();

						BioPhysicalEntityParticipant cpdParticipant = new BioPhysicalEntityParticipant(
								rxn.getId() + "__With__" + c.getId(), c, coeff, compartment);

						rxn.addRightParticipant(cpdParticipant);

						if (rxn.getReversiblity().compareToIgnoreCase("irreversible-left-to-right") == 0) {
							c.addReactionAsProduct(rxn);
						} else if (rxn.getReversiblity().compareToIgnoreCase("irreversible-right-to-left") == 0) {
							c.addReactionAsSubstrate(rxn);
						} else {
							c.addReactionAsProduct(rxn);
							c.addReactionAsSubstrate(rxn);
						}

						// rxn.addLeftParticipant(p)(c);
						// c.addSubstrateOf(r);
						// if(r.reversible) {
						// c.addProducedBy(r);
						// }
					}
				}
			}


			if ("kineticLaw".equals(listsFromReaction.item(j).getNodeName())) {

				Node kineticLaw = listsFromReaction.item(j);

				NodeList kineticLawNodes = kineticLaw.getChildNodes();

				for (int i = 0; i < kineticLawNodes.getLength(); i++) {

					Node x = kineticLawNodes.item(i);

					if ("listOfParameters".equals(x.getNodeName())) {

						NodeList parameters = x.getChildNodes();

						for (int k = 1; k < parameters.getLength(); k = k + 2) {

							Element parameter = (Element) parameters.item(k);

							String parameterId = parameter.getAttribute("id");

							if (parameterId.compareToIgnoreCase("LOWER_BOUND") == 0) {
								String value = parameter.getAttribute("value");

								String units = parameter.getAttribute("units");
								if (units == null || units.equals("")) {
									units = parameter.getAttribute("name");
								}

								BioUnitDefinition ud;
								if (units != null && !units.equals("")) {
									ud = this.getBioNetwork().getUnitDefinitions().get(units);
								} else {
									// We take the first unit definition
									ud = this.getBioNetwork().getUnitDefinitions().values().iterator().next();
								}

								if (ud == null) {
									System.err.println(
											"Error in the flux unit definition in the reaction " + rxn.getId());
									System.err.println("We take the default unit definition value");
									ud = new BioUnitDefinition("mmol_per_gDW_per_hr", "mmol_per_gDW_per_hr");
								}

								Flux lb = new Flux(value, ud);

								rxn.setLowerBound(lb);
							}

							if (parameterId.compareToIgnoreCase("UPPER_BOUND") == 0) {

								String value = parameter.getAttribute("value");

								String units = parameter.getAttribute("units");
								if (units == null || units.equals("")) {
									units = parameter.getAttribute("name");
								}

								BioUnitDefinition ud;
								if (units != null && !units.equals("")) {
									ud = this.getBioNetwork().getUnitDefinitions().get(units);
								} else {
									// We take the first unit definition
									ud = this.getBioNetwork().getUnitDefinitions().values().iterator().next();
								}

								if (ud == null) {
									System.err.println(
											"Error in the flux unit definition in the reaction " + rxn.getId());
									System.err.println("We take the default unit definition value");
									ud = new BioUnitDefinition("mmol_per_gDW_per_hr", "mmol_per_gDW_per_hr");
								}

								Flux ub = new Flux(value, ud);

								rxn.setUpperBound(ub);
							}

						}

					}

				}

			}

		}

		// It's important to do it after indicating the left and right
		// participants !
		// Get side-compounds
		NodeList sideCompoundNodes = reaction.getElementsByTagName("side-compounds");

		for (int i = 0; i < sideCompoundNodes.getLength(); i++) {

			Element sideCompoundNode = (Element) sideCompoundNodes.item(i);

			NodeList speciesReferences = sideCompoundNode.getElementsByTagName("speciesReference");

			for (int j = 0; j < speciesReferences.getLength(); j++) {
				Element speciesReference = (Element) speciesReferences.item(j);

				String idSpecies = speciesReference.getAttribute("species");
				rxn.addSideCompound(StringUtils.sbmlDecode(idSpecies));

			}
		}

		// Get Cofactors
		sideCompoundNodes = reaction.getElementsByTagName("cofactors");

		for (int i = 0; i < sideCompoundNodes.getLength(); i++) {

			Element sideCompoundNode = (Element) sideCompoundNodes.item(i);

			NodeList speciesReferences = sideCompoundNode.getElementsByTagName("speciesReference");

			for (int j = 0; j < speciesReferences.getLength(); j++) {
				Element speciesReference = (Element) speciesReferences.item(j);

				String idSpecies = speciesReference.getAttribute("species");

				rxn.addCofactor(StringUtils.sbmlDecode(idSpecies));
			}
		}

	}

	/**
	 * Get character data (CDATA) from xml document From
	 * http://www.java2s.com/Code
	 * /Java/XML/GetcharacterdataCDATAfromxmldocument.htm
	 * 
	 * @param e
	 * @return
	 */
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getLastChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	/**
	 * @return the bioNetwork
	 */
	public BioNetwork getBioNetwork() {
		return bioNetwork;
	}

	/**
	 * @param bioNetwork
	 *            the bioNetwork to set
	 */
	public void setBioNetwork(BioNetwork bioNetwork) {
		this.bioNetwork = bioNetwork;
	}

}
