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

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import org.apache.commons.lang3.StringUtils;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.SpeciesType;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.BioCompartmentType;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reactant.ReactantAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.UnitSbml;

import org.sbml.jsbml.Unit.Kind;

/**
 * Abstract class that defines the different 'ListOf' parsing method.
 *
 * @author bmerlet
 * @since 3.0
 * @version $Id: $Id
 */
public class JsbmlToBioNetwork {

	/**
	 * The {@link BioNetwork} created by this class
	 */
	private BioNetwork network;

	private Model model;

	/**
	 * The ordered list of {@link PackageParser} activated for this parser
	 */
	public ArrayList<PackageParser> packages = new ArrayList<PackageParser>();

	/**
	 * <p>Constructor for JsbmlToBioNetwork.</p>
	 *
	 * @param model a {@link org.sbml.jsbml.Model} object.
	 */
	public JsbmlToBioNetwork(Model model) {
		this.model = model;
	}

	/**
	 * Main method of the parser. It should call the different list parser defined
	 * in the inheriting classes
	 *
	 * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
	 */
	protected void parseModel() throws Met4jSbmlReaderException {

		this.parseNetworkData();
		this.parseListOfUnitDefinitions();
		this.parseListOfCompartments();

		this.parseListOfSpecies();

		this.parseListOfReactions();

		this.parsePackageAdditionalData();
	}

	/**
	 * Parse the jsbml Model object and retrieves basic information from it
	 * 
	 */
	private void parseNetworkData() {

		BioNetwork bionet = new BioNetwork(model.getId());

		bionet.setName(model.getName());

		this.setNetwork(bionet);
	}

	/**
	 * Default way of parsing sbml UnitDefinition. Needs to be overridden to modify
	 * behavior
	 * 
	 */
	private void parseListOfUnitDefinitions() {

		for (UnitDefinition jSBMLUD : model.getListOfUnitDefinitions()) {

			BioUnitDefinition bionetUD = new BioUnitDefinition(jSBMLUD.getId(), jSBMLUD.getName());

			if (jSBMLUD.getName().isEmpty()) {
				bionetUD.setName(jSBMLUD.getId());
			}
			ListOf<Unit> listofunits = jSBMLUD.getListOfUnits();

			if (listofunits.size() != 0) {

				for (int n = 0; n < listofunits.size(); n++) {

					Unit jSBMLUnit = listofunits.get(n);

					Kind kind = jSBMLUnit.getKind();
					Double Exp = jSBMLUnit.getExponent();
					Integer Scale = jSBMLUnit.getScale();
					Double Multiplier = jSBMLUnit.getMultiplier();

					UnitSbml bionetUnit = new UnitSbml(kind.getName(), Exp, Scale, Multiplier);
					bionetUD.addUnit(bionetUnit);
				}
			}

			NetworkAttributes.addUnitDefinition(this.getNetwork(), bionetUD);
		}

	}

	/**
	 * Default way of parsing sbml compartements. Needs to be overridden to modify
	 * behavior
	 * 
	 */
	private void parseListOfCompartments() {

		if(model.getListOfCompartments().size()==0)
		{
			System.err.println("[WARNING] No compartment in the model !");
		}

		for (Compartment jSBMLCompart : model.getListOfCompartments()) {

			String compartId = jSBMLCompart.getId();
			String compartName = jSBMLCompart.getName().trim();

			if (StringUtils.isEmpty(compartName)) {
				compartName = compartId;
			}

			BioCompartment bionetCompart = this.getNetwork().getCompartmentsView().get(compartId);
			if (bionetCompart == null) {
				bionetCompart = new BioCompartment(compartId, compartName);
				this.getNetwork().add(bionetCompart);
			}

			/**
			 * SBML 2.0
			 */
			if (jSBMLCompart.isSetCompartmentType()) {
				BioCompartmentType type = new BioCompartmentType(
						model.getCompartmentType(jSBMLCompart.getCompartmentType()).getId(),
						model.getCompartmentType(jSBMLCompart.getCompartmentType()).getName());
				CompartmentAttributes.setType(bionetCompart, type);

			}

			/*
			 * If the compartment has an outside attribute, we need to look if the outside
			 * compartment exists in the bioNetwork
			 */
			if (jSBMLCompart.isSetOutside()) {

				Compartment outsideJSBMLComp = model.getCompartment(jSBMLCompart.getOutside());

				BioCompartment outsideCompart = this.getNetwork().getCompartmentsView()
						.get(outsideJSBMLComp.getId());

				// if it's null, we create it and add it to the bionetwork
				if (outsideCompart == null) {
					outsideCompart = new BioCompartment(outsideJSBMLComp.getId(), outsideJSBMLComp.getName());
					this.getNetwork().add(outsideCompart);
				}

				// We can add it as outside compartment of the current
				// compartment
				CompartmentAttributes.setOutsideCompartment(bionetCompart, outsideCompart);
			}

			if (jSBMLCompart.isSetUnits()) {

				UnitDefinition JsbmlUnitDef = model.getUnitDefinition(jSBMLCompart.getUnits());
				BioUnitDefinition bionetUnitDef = NetworkAttributes.getUnitDefinition(this.getNetwork(),
						JsbmlUnitDef.getId());
				CompartmentAttributes.setUnitDefinition(bionetCompart, bionetUnitDef);
			}

			if (jSBMLCompart.getSBOTerm() != -1) {
				CompartmentAttributes.setSboTerm(bionetCompart, jSBMLCompart.getSBOTermID());
			}

			CompartmentAttributes.setConstant(bionetCompart, jSBMLCompart.getConstant());

			if (jSBMLCompart.isSetSize()) {
				CompartmentAttributes.setSize(bionetCompart, jSBMLCompart.getSize());
			}

			if (jSBMLCompart.isSetSpatialDimensions()) {
				CompartmentAttributes.setSpatialDimensions(bionetCompart, (int) jSBMLCompart.getSpatialDimensions());
			} else {
				CompartmentAttributes.setSpatialDimensions(bionetCompart, 3);
			}

		}

	}

	/**
	 * 
	 * @return true if the parse contains the FBC package
	 */
	private boolean containsFbcPackage() {

		for (PackageParser p : this.packages) {
			if (p instanceof FBCParser) {
				return true;
			}
		}

		return false;

	}

	/**
	 * Method to parse the list of reaction of the jsbml model
	 * 
	 * @throws Met4jSbmlReaderException
	 */
	private void parseListOfReactions() throws Met4jSbmlReaderException {

		Boolean hasModifiers = false;

		for (Reaction jSBMLReaction : model.getListOfReactions()) {
			String reactionId = jSBMLReaction.getId();

			String reactionName = jSBMLReaction.getName();
			if (reactionName.isEmpty()) {
				reactionName = reactionId;
			}
			BioReaction bionetReaction = new BioReaction(reactionId, reactionName);

			this.getNetwork().add(bionetReaction);

			if (jSBMLReaction.isSetSBOTerm()) {
				ReactionAttributes.setSboTerm(bionetReaction, jSBMLReaction.getSBOTermID());
			}

			if (jSBMLReaction.isSetFast()
					&& ((model.getLevel() < 3) || (model.getLevel() == 3 && model.getVersion() == 1))) {
				ReactionAttributes.setFast(bionetReaction, jSBMLReaction.getFast());
			}

			// if reversible attribute is present and is set to false
			if (jSBMLReaction.isSetReversible()) {
				bionetReaction.setReversible(jSBMLReaction.getReversible());
			} else {
				bionetReaction.setReversible(true);
			}

			this.parseReactionListOf(bionetReaction, jSBMLReaction.getListOfReactants(), "left");
			this.parseReactionListOf(bionetReaction, jSBMLReaction.getListOfProducts(), "right");

			BioUnitDefinitionCollection udList = NetworkAttributes.getUnitDefinitions(this.getNetwork());

			KineticLaw kine = jSBMLReaction.getKineticLaw();
			if (kine != null) {

				ReactionAttributes.setKineticFormula(bionetReaction, kine.getMathMLString());

				if (model.getLevel() < 3) {
					for (int n = 0; n < kine.getNumParameters(); n++) {

						UnitDefinition jsbmlUnit = model.getUnitDefinition(kine.getParameter(n).getUnits());

						// This means that <parameter id="REDUCED_COST" value="0.000000"/> won't be taken into account
						if (jsbmlUnit != null) {

							BioUnitDefinition UD = udList.get(jsbmlUnit.getId());

							/**
							 * This is to make sure that the unit definition associated with the fluxes is
							 * not null, e.g dimensionless
							 */
							if (UD == null) {
								UD = new BioUnitDefinition(jsbmlUnit.getId(), jsbmlUnit.getName());
								udList.add(UD);
							}

							if (kine.getParameter(n).getId().equalsIgnoreCase("UPPER_BOUND")
									|| kine.getParameter(n).getName().equalsIgnoreCase("UPPER_BOUND")) {

								Flux newflux = new Flux(kine.getParameter(n).getId(), kine.getParameter(n).getValue(),
										UD);

								try {
									ReactionAttributes.setUpperBound(bionetReaction, newflux);
								} catch (IllegalArgumentException e) {
									System.err.println("[Warning] Upper bound of reaction "+bionetReaction.getId()+ " badly formatted : put to 0");
									e.printStackTrace();
									newflux.value = 0.0;
									ReactionAttributes.setUpperBound(bionetReaction, newflux);
								}

							} else if (kine.getParameter(n).getId().equalsIgnoreCase("LOWER_BOUND")
									|| kine.getParameter(n).getName().equalsIgnoreCase("LOWER_BOUND")) {

								Flux newflux = new Flux(kine.getParameter(n).getId(), kine.getParameter(n).getValue(),
										UD);


								try {
									ReactionAttributes.setLowerBound(bionetReaction, newflux);
								} catch (IllegalArgumentException e) {
									System.err.println("[Warning] Lower bound of reaction "+bionetReaction.getId()+ " badly formatted : put to 0");
									e.printStackTrace();
									newflux.value = 0.0;
									ReactionAttributes.setLowerBound(bionetReaction, newflux);
								}

							} else {
								Flux newflux = new Flux(kine.getParameter(n).getId(), kine.getParameter(n).getValue(),
										UD);

								ReactionAttributes.addFlux(bionetReaction, newflux);

							}
						}

					}
				} else if (!this.containsFbcPackage()) { // SBML V3.0 and not
															// fbc package

					for (LocalParameter param : kine.getListOfLocalParameters()) {

						UnitDefinition jsbmlUnit = model.getUnitDefinition(param.getUnits());
						if (jsbmlUnit == null) {
							throw new Met4jSbmlReaderException("Problem with the reaction " + bionetReaction.getId()
									+ " : the flux unit " + param.getUnits() + " does not exist in the model");
						} else {

							BioUnitDefinition UD = udList.get(jsbmlUnit.getId());

							/**
							 * This is to make sure that the unit definition associated with the fluxes is
							 * not null
							 */
							if (UD == null) {
								UD = new BioUnitDefinition(jsbmlUnit.getId(), jsbmlUnit.getName());
								udList.add(UD);
							}

							if (param.getId().equalsIgnoreCase("UPPER_BOUND")
									|| param.getName().equalsIgnoreCase("UPPER_BOUND")) {

								String name = "UPPER_BOUND";
								Flux newflux = new Flux(name, param.getValue(), UD);
								ReactionAttributes.setUpperBound(bionetReaction, newflux);

							} else if (param.getId().equalsIgnoreCase("LOWER_BOUND")
									|| param.getName().equalsIgnoreCase("LOWER_BOUND")) {

								String name = "LOWER_BOUND";
								Flux newflux = new Flux(name, param.getValue(), UD);
								ReactionAttributes.setLowerBound(bionetReaction, newflux);

							} else {
								Flux newflux = new Flux(param.getId(), param.getValue(), UD);

								ReactionAttributes.addFlux(bionetReaction, newflux);

							}
						}
					}

				}
			}

			if(jSBMLReaction.getModifierCount() > 0)
			{
				hasModifiers = true;
			}

		}

		if(hasModifiers)
		{
			System.err.println("[warning] Some reactions have list of modifiers. Be careful, Met4j-io does not import list of modifiers");
		}
	}

	/**
	 * Abstract class to parse the list of SpeciesReferences of a given Reaction
	 * 
	 * @param bionetReaction the {@link BioReaction}
	 * @param listOf         the jsbml list of SpeciesReferences
	 * @param side           The side of the SpeciesReferences
	 *                       <ul>
	 *                       <li>left = reactants
	 *                       <li>right = products
	 *                       </ul>
	 */
	private void parseReactionListOf(BioReaction bionetReaction, ListOf<SpeciesReference> listOf, String side) {
		for (SpeciesReference specieRef : listOf) {
			BioReactant reactant = this.parseParticipantSpecies(specieRef);

			System.err.println("reactant : "+reactant);

			if(reactant != null) {
				if (specieRef.isSetConstant()) {
					ReactantAttributes.setConstant(reactant, specieRef.getConstant());
				}

				if (side.equals("left")) {
					this.getNetwork().affectLeft(bionetReaction, reactant);
				} else {
					this.getNetwork().affectRight(bionetReaction, reactant);
				}
			}

		}
	}

	private void parseListOfSpecies() {

		Boolean hasInvalidSboTerms = false;

		for (Species specie : this.getModel().getListOfSpecies()) {
			String specieId = specie.getId();

			String specieName = specie.getName();
			if (specieName.isEmpty()) {
				specieName = specieId;
			}

			// Check if the sbo term is valid for a metabolite
			Boolean validSboTerm = true;
			String sboTerm =  specie.getSBOTermID();

			if(sboTerm != null)
			{
				if(sboTerm.compareToIgnoreCase("SBO:0000252")==0)
				{
					validSboTerm = false;
					hasInvalidSboTerms = true;
					// It's considered as a gene
					// We replace the first "_" by "" if exists

					String oldId = specieId;

					specieId = specieId.replaceFirst("^_", "");
					specieId = specieId.replaceFirst("_.$", "");
					specieName = specieName.replaceFirst("^_", "");
					specieName = specieName.replaceFirst("_.$", "");
					BioGene gene = new BioGene(specieId, specieName);

					gene.setAttribute("oldId", oldId);

					this.getNetwork().add(gene);
				}
				else if(sboTerm.compareToIgnoreCase("SBO:0000014")==0 || sboTerm.compareToIgnoreCase("SBO:0000297")==0)
				{
					validSboTerm = false;
					hasInvalidSboTerms = true;

					String oldId = specieId;
					// It's considered as an enzyme
					specieId = specieId.replaceFirst("^_", "");
					specieId = specieId.replaceFirst("_.$", "");
					specieName = specieName.replaceFirst("^_", "");
					specieName = specieName.replaceFirst("_.$", "");
					BioEnzyme enz = new BioEnzyme(specieId, specieName);

					enz.setAttribute("oldId", oldId);

					this.getNetwork().add(enz);
				}
			}

			if(validSboTerm) {

				BioMetabolite bionetSpecies = new BioMetabolite(specieId, specieName);

				MetaboliteAttributes.setBoundaryCondition(bionetSpecies, specie.getBoundaryCondition());
				MetaboliteAttributes.setConstant(bionetSpecies, specie.getConstant());

				MetaboliteAttributes.setSubstanceUnits(bionetSpecies, specie.getSubstanceUnits());
				if (specie.getSBOTerm() != -1) {
					MetaboliteAttributes.setSboTerm(bionetSpecies, specie.getSBOTermID());
				}

				if (specie.isSetInitialAmount()) {
					MetaboliteAttributes.setInitialAmount(bionetSpecies, specie.getInitialAmount());
				} else if (specie.isSetInitialConcentration()) {
					MetaboliteAttributes.setInitialConcentration(bionetSpecies, specie.getInitialConcentration());
				}

				if (specie.isSetCharge()) {
					bionetSpecies.setCharge(specie.getCharge());
				}

				MetaboliteAttributes.setSubstanceUnits(bionetSpecies, specie.getSubstanceUnits());

				if (model.getLevel() == 2 && model.getVersion() >= 2 && model.getVersion() <= 4) {
					if (specie.isSetSpeciesType()) {

						SpeciesType stype = specie.getSpeciesTypeInstance();
						if (stype.isSetSBOTerm()) {

							MetaboliteAttributes.setSboTerm(bionetSpecies, stype.getSBOTermID());

						}
						try {
							if (stype.isSetAnnotation()) {
								MetaboliteAttributes.setAnnotation(bionetSpecies,
										new SbmlAnnotation(stype.getId(), stype.getAnnotationString()));
							}
							if (stype.isSetNotes()) {
								MetaboliteAttributes.setNotes(bionetSpecies, new Notes(stype.getNotesString()));
							}
						} catch (XMLStreamException e) {
							e.printStackTrace();
						}
					}
				}

				this.getNetwork().add(bionetSpecies);

				this.getNetwork().affectToCompartment(
						this.getNetwork().getCompartmentsView().get(specie.getCompartment()), bionetSpecies);
			}
		}

		if(hasInvalidSboTerms)
		{
			System.err.println("[warning] Sbo term for some species are not metabolite sbo terms, they haven't been imported or have been imported as genes, " +
					"depending on their sbo term (SBO:0000252) or as enzymes (SBO:0000014 or SBO:0000297).");
		}
	}

	/**
	 * parse the jsbml species participating in a reaction and create the
	 * corresponding metabolite
	 */
	private BioReactant parseParticipantSpecies(SpeciesReference specieRef) {
		Species specie = this.getModel().getSpecies(specieRef.getSpecies());
		if(specie == null)
		{
			throw new IllegalArgumentException("The specie "+specieRef.getSpecies()+ " does not exist");
		}
		
		String specieId = specie.getId();

		Double stoDbl = specieRef.getStoichiometry();

		if(Double.isNaN(stoDbl) || Double.isInfinite(stoDbl)) {
			System.err.println("Warning : invalid coefficient : " + stoDbl + " for " + specieId+": set to 1.0");
			stoDbl = 1.0;
		}

		if(stoDbl < 0)
		{
			System.err.println("Warning : negative coefficient : " + stoDbl + " for " + specieId+" : set to positive");
			stoDbl = -stoDbl;
		}

		if(stoDbl == 0)
		{
			System.err.println("Warning : coefficient equals to 0 for " + specieId+" : This reactant won't be added to the reaction");
			stoDbl = -stoDbl;
		}

		BioReactant reactant = null;

		if(stoDbl != 0) {
			BioMetabolite bionetSpecies = this.getNetwork().getMetabolitesView().get(specieId);

			reactant = new BioReactant(bionetSpecies, stoDbl,
					this.getNetwork().getCompartmentsView().get(specie.getCompartment()));
		}

		return reactant;
	}

	/**
	 * <p>Getter for the field <code>network</code>.</p>
	 *
	 * @return the bionet
	 */
	public BioNetwork getNetwork() {
		return network;
	}

	/**
	 * <p>Setter for the field <code>network</code>.</p>
	 *
	 * @param bionet the bionet to set
	 */
	public void setNetwork(BioNetwork bionet) {
		this.network = bionet;
	}

	/**
	 * <p>getSetOfPackage.</p>
	 *
	 * @return the setOfPackage
	 */
	public ArrayList<PackageParser> getSetOfPackage() {
		return packages;
	}

	/**
	 * Add a package to this parser
	 *
	 * @param pkg the package to add
	 * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException if any.
	 */
	public void addPackage(PackageParser pkg) throws JSBMLPackageReaderException {

		if ((this.getModel().getLevel() <= 2 && pkg instanceof ReaderSBML2Compatible)
				|| (this.getModel().getLevel() > 2 && pkg instanceof ReaderSBML3Compatible)) {
			this.getSetOfPackage().add(pkg);
		} else {
			throw new JSBMLPackageReaderException("Invalid SBML level and package Reader combination");
		}

	}

	/**
	 * Set the {@link #packages} to a new list
	 *
	 * @param packages the ordered list of packages to set
	 * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException if any.
	 */
	public void setPackages(ArrayList<PackageParser> packages) throws JSBMLPackageReaderException {
		for (PackageParser pkg : packages) {
			this.addPackage(pkg);
		}
	}

	/**
	 * Launch the parsing of the jsbml model by the different packages
	 *
	 * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
	 */
	public void parsePackageAdditionalData() throws Met4jSbmlReaderException {
		for (PackageParser parser : this.getSetOfPackage()) {
			parser.parseModel(model, this.getNetwork());
		}
	}

	/**
	 * <p>Getter for the field <code>model</code>.</p>
	 *
	 * @return a {@link org.sbml.jsbml.Model} object.
	 */
	public Model getModel() {
		return model;
	}

}
