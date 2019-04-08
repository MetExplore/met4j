package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader;

import java.lang.instrument.IllegalClassFormatException;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

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

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.annotations.compartment.BioCompartmentType;
import fr.inra.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reactant.ReactantAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.Flux;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.UnitSbml;

import org.sbml.jsbml.Unit.Kind;

/**
 * Abstract class that defines the different 'ListOf' parsing method.
 * 
 * @author bmerlet
 * @since 3.0
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

	public JsbmlToBioNetwork(Model model) {
		this.model = model;
	}

	/**
	 * Main method of the parser. It should call the different list parser defined
	 * in the inheriting classes
	 * 
	 * @param model the jsbml model
	 * @throws Met4jSbmlReaderException 
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
	 * @param model the jsbml model
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
	 * @param model the jsbml model
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
					String Exp = String.valueOf(jSBMLUnit.getExponent());
					String Scale = String.valueOf(jSBMLUnit.getScale());
					String Multiplier = String.valueOf(jSBMLUnit.getMultiplier());

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
	 * @param model the jsbml model
	 */
	private void parseListOfCompartments() {

		for (Compartment jSBMLCompart : model.getListOfCompartments()) {

			String compartId = jSBMLCompart.getId();
			String compartName = jSBMLCompart.getName().trim();

			if (StringUtils.isEmpty(compartName)) {
				compartName = compartId;
			}

			BioCompartment bionetCompart = this.getNetwork().getCompartmentsView().getEntityFromId(compartId);
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
						.getEntityFromId(outsideJSBMLComp.getId());

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

			CompartmentAttributes.setSboTerm(bionetCompart, jSBMLCompart.getSBOTermID());

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
	 * @param model the jsbml model
	 * @throws Met4jSbmlReaderException 
	 */
	private void parseListOfReactions() throws Met4jSbmlReaderException {
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

			if (jSBMLReaction.isSetFast()) {
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

						if(jsbmlUnit == null) {
							throw new Met4jSbmlReaderException("Problem with the reaction "+bionetReaction.getId()+" : the flux unit "+kine.getParameter(n).getUnits()+" does not exist in the model");
						}
						else {

							BioUnitDefinition UD = udList.getEntityFromId(jsbmlUnit.getId());

							/**
							 * This is to make sure that the unit definition associated with the fluxes is
							 * not null
							 */
							if (UD == null) {
								throw new Met4jSbmlReaderException("Problem with the reaction "+bionetReaction.getId()+" : the flux unit "+jsbmlUnit.getId()+" does not exist in the bioNetwork");
							}

							if (kine.getParameter(n).getId().equalsIgnoreCase("UPPER_BOUND")
									|| kine.getParameter(n).getName().equalsIgnoreCase("UPPER_BOUND")) {

								Flux newflux = new Flux(kine.getParameter(n).getId(), kine.getParameter(n).getValue(),
										UD);
								ReactionAttributes.setUpperBound(bionetReaction, newflux);

							} else if (kine.getParameter(n).getId().equalsIgnoreCase("LOWER_BOUND")
									|| kine.getParameter(n).getName().equalsIgnoreCase("LOWER_BOUND")) {

								Flux newflux = new Flux(kine.getParameter(n).getId(), kine.getParameter(n).getValue(),
										UD);

								ReactionAttributes.setLowerBound(bionetReaction, newflux);

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
							throw new Met4jSbmlReaderException(
									"Problem with the reaction " + bionetReaction.getId() + " : the flux unit "
											+ param.getUnits() + " does not exist in the model");
						}
						else {

							BioUnitDefinition UD = udList.getEntityFromId(jsbmlUnit.getId());

							/**
							 * This is to make sure that the unit definition associated with the fluxes is
							 * not null
							 */
							if (UD == null) {
								throw new Met4jSbmlReaderException(
										"Problem with the reaction " + bionetReaction.getId() + " : the flux unit "
												+ param.getUnits() + " does not exist in the bioNetwork");
							}

							if (param.getId().equalsIgnoreCase("UPPER_BOUND")
									|| param.getName().equalsIgnoreCase("UPPER_BOUND")) {

								Flux newflux = new Flux(param.getName(), param.getValue(), UD);
								ReactionAttributes.setUpperBound(bionetReaction, newflux);

							} else if (param.getId().equalsIgnoreCase("LOWER_BOUND")
									|| param.getName().equalsIgnoreCase("LOWER_BOUND")) {

								Flux newflux = new Flux(param.getName(), param.getValue(), UD);
								ReactionAttributes.setLowerBound(bionetReaction, newflux);

							} else {
								Flux newflux = new Flux(param.getId(), param.getValue(), UD);

								ReactionAttributes.addFlux(bionetReaction, newflux);

							}
						} 
					}

				}
			}
		}
	}

	/**
	 * Abstract class to parse the list of SpeciesReferences of a given Reaction
	 * 
	 * @param bionetReaction the {@link BioChemicalReaction}
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

			if (specieRef.isSetConstant()) {
				ReactantAttributes.setConstant(reactant, specieRef.getConstant());
			}

			if (side.equals("left")) {
				this.getNetwork().affectLeft(reactant, bionetReaction);
			} else if (side.equals("right")) {
				this.getNetwork().affectRight(reactant, bionetReaction);
			}

		}
	}

	private void parseListOfSpecies() {
		for (Species specie : this.getModel().getListOfSpecies()) {
			String specieId = specie.getId();

			String specieName = specie.getName();
			if (specieName.isEmpty()) {
				specieName = specieId;
			}

			BioMetabolite bionetSpecies = new BioMetabolite(specieId, specieName);

			MetaboliteAttributes.setBoundaryCondition(bionetSpecies, specie.getBoundaryCondition());
			MetaboliteAttributes.setConstant(bionetSpecies, specie.getConstant());

			MetaboliteAttributes.setSubstanceUnits(bionetSpecies, specie.getSubstanceUnits());
			MetaboliteAttributes.setSboTerm(bionetSpecies, specie.getSBOTermID());

			if (specie.isSetInitialAmount()) {
				MetaboliteAttributes.setInitialAmount(bionetSpecies, specie.getInitialAmount());
			} else if (specie.isSetInitialConcentration()) {
				MetaboliteAttributes.setInitialConcentration(bionetSpecies, specie.getInitialConcentration());
			}

			if (specie.isSetCharge()) {
				bionetSpecies.setCharge(specie.getCharge());
			}

			if (model.getVersion() < 3) {
				if (specie.isSetSpeciesType()) {

					SpeciesType stype = specie.getSpeciesTypeInstance();
					if (stype.isSetSBOTerm()) {

						MetaboliteAttributes.setSboTerm(bionetSpecies, stype.getSBOTermID());

					}
					try {
						if (stype.isSetAnnotation()) {

							MetaboliteAttributes.setAnnotation(bionetSpecies,
									new SbmlAnnotation(stype.getMetaId(), stype.getAnnotationString()));
						}
						if (stype.isSetNotes()) {
							MetaboliteAttributes.setNotes(bionetSpecies, new Notes(stype.getNotesString()));
						}
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}
				}
			} else {

				MetaboliteAttributes.setSubstanceUnits(bionetSpecies, specie.getSubstanceUnits());
				MetaboliteAttributes.setSboTerm(bionetSpecies, specie.getSBOTermID());

				if (specie.isSetInitialAmount()) {
					MetaboliteAttributes.setInitialAmount(bionetSpecies, specie.getInitialAmount());
				} else if (specie.isSetInitialConcentration()) {
					MetaboliteAttributes.setInitialConcentration(bionetSpecies, specie.getInitialConcentration());
				}
			}

			this.getNetwork().add(bionetSpecies);

			this.getNetwork().affectToCompartment(bionetSpecies,
					this.getNetwork().getCompartmentsView().getEntityFromId(specie.getCompartment()));
		}
	}

	/**
	 * parse the jsbml species participating in a reaction and create the
	 * corresponding metabolite as {@link BioPhysicalEntity} object
	 * 
	 * @param specie the Jsbml Species object
	 * @return BioPhysicalEntity
	 */
	private BioReactant parseParticipantSpecies(SpeciesReference specieRef) {
		Species specie = this.getModel().getSpecies(specieRef.getSpecies());
		String specieId = specie.getId();

		String sto = String.valueOf(specieRef.getStoichiometry());

		Double stoDbl = 1.0;

		try {
			stoDbl = Double.parseDouble(sto);
		} catch (NumberFormatException e) {
			System.err.println("Warning : invalid coefficient : " + sto + " for " + specieId);
			stoDbl = 1.0;
		}

		if (Double.isNaN(stoDbl)) {
			System.err.println("Warning : invalid coefficient : " + sto + " for " + specieId);
			stoDbl = 1.0;
		}

		BioMetabolite bionetSpecies = this.getNetwork().getMetabolitesView().getEntityFromId(specieId);

		BioReactant reactant = new BioReactant(bionetSpecies, stoDbl,
				this.getNetwork().getCompartmentsView().getEntityFromId(specie.getCompartment()));

		return reactant;
	}

	/**
	 * @return the bionet
	 */
	public BioNetwork getNetwork() {
		return network;
	}

	/**
	 * @param bionet the bionet to set
	 */
	public void setNetwork(BioNetwork bionet) {
		this.network = bionet;
	}

	/**
	 * @return the setOfPackage
	 */
	public ArrayList<PackageParser> getSetOfPackage() {
		return packages;
	}

	/**
	 * Add a package to this parser
	 * 
	 * @param pkg the package to add
	 * @throws JSBMLPackageReaderException if the package is not compatible with the
	 *                                     the current SBML level
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
	 * @throws JSBMLPackageReaderException if one of the package in the list is not
	 *                                     compatible with the current SBML level
	 */
	public void setPackages(ArrayList<PackageParser> packages) throws JSBMLPackageReaderException {
		for (PackageParser pkg : packages) {
			this.addPackage(pkg);
		}
	}

	/**
	 * Launch the parsing of the jsbml model by the different packages
	 * 
	 * @param model the jsbml model
	 */
	public void parsePackageAdditionalData() {
		for (PackageParser parser : this.getSetOfPackage()) {
			parser.parseModel(model, this.getNetwork());
		}
	}

	public Model getModel() {
		return model;
	}

}