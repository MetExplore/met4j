package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

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
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxCollection;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
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
	protected BioNetwork bionet;

	protected Model model;

	/**
	 * The ordered list of {@link PackageParser} activated for this parser
	 */
	public ArrayList<PackageParser> setOfPackage = new ArrayList<PackageParser>();

	/**
	 * Main method of the parser. It should call the different list parser
	 * defined in the inheriting classes
	 * 
	 * @param model
	 *            the jsbml model
	 */
	protected void parseModel(Model model) {
		this.model = model;

		this.getModelData(model);
		this.parseListOfUnitDefinitions(model);
		this.parseListOfCompartments(model);
		this.parseListOfReactions(model);

		this.parsePackageAdditionalData(model);
	}

	/**
	 * Parse the jsbml Model object and retrieves basic information from it
	 * 
	 * @param model
	 *            the jsbml model
	 */
	protected void getModelData(Model model) {

		BioNetwork bionet = new BioNetwork(model.getId());

		bionet.setName(model.getName());

		// Remove this in parsebionet -> met4j
		// bionet.setType("sbml" + model.getLevel() + "." + model.getVersion());
		this.setBionet(bionet);
	}

	/**
	 * Default way of parsing sbml UnitDefinition. Needs to be overridden to
	 * modify behavior
	 * 
	 * @param model
	 *            the jsbml model
	 */
	protected void parseListOfUnitDefinitions(Model model) {

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

					UnitSbml bionetUnit = new UnitSbml(kind.getName().toUpperCase(), Exp, Scale, Multiplier);
					bionetUD.addUnit(bionetUnit);
				}
			}

			NetworkAttributes.addUnitDefinition(this.getBionet(), bionetUD);
		}

	}

	/**
	 * Default way of parsing sbml compartements. Needs to be overridden to
	 * modify behavior
	 * 
	 * @param model
	 *            the jsbml model
	 */
	protected void parseListOfCompartments(Model model) {

		for (Compartment jSBMLCompart : model.getListOfCompartments()) {

			String compartId = jSBMLCompart.getId();
			String compartName = jSBMLCompart.getName();

			if (compartName.isEmpty()) {
				compartName = compartId;
			}

			BioCompartment bionetCompart = this.getBionet().getCompartmentsView().getEntityFromId(compartId);
			if (bionetCompart == null) {
				bionetCompart = new BioCompartment(compartName, compartId);
				this.getBionet().add(bionetCompart);
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
			 * If the compartment has an outside attribute, we need to look if
			 * the outside compartment exists in the bioNetwork
			 */
			if (jSBMLCompart.isSetOutside()) {

				Compartment outsideJSBMLComp = model.getCompartment(jSBMLCompart.getOutside());

				BioCompartment outsideCompart = this.getBionet().getCompartmentsView()
						.getEntityFromId(outsideJSBMLComp.getId());

				// if it's null, we create it and add it to the bionetwork
				if (outsideCompart == null) {
					outsideCompart = new BioCompartment(outsideJSBMLComp.getName(), outsideJSBMLComp.getId());
					this.getBionet().add(outsideCompart);
				}

				// We can add it as outside compartment of the current
				// compartment
				CompartmentAttributes.setOutsideCompartment(bionetCompart, outsideCompart);
			}

			if (jSBMLCompart.isSetUnits()) {

				UnitDefinition JsbmlUnitDef = model.getUnitDefinition(jSBMLCompart.getUnits());
				BioUnitDefinition bionetUnitDef = NetworkAttributes.getUnitDefinition(this.getBionet(),
						JsbmlUnitDef.getId());
				CompartmentAttributes.setUnitDefinition(bionetCompart, bionetUnitDef);
			}

			CompartmentAttributes.setSboTerm(bionetCompart, jSBMLCompart.getSBOTermID());

			CompartmentAttributes.setConstant(bionetCompart, jSBMLCompart.getConstant());

			if (jSBMLCompart.isSetSize()) {
				CompartmentAttributes.setSize(bionetCompart, jSBMLCompart.getSize());
			} else {
				CompartmentAttributes.setSize(bionetCompart, jSBMLCompart.getSize());
			}
			if (jSBMLCompart.isSetSpatialDimensions()) {
				CompartmentAttributes.setSpatialDimensions(bionetCompart, (int)jSBMLCompart.getSpatialDimensions());
			} else {
				CompartmentAttributes.setSpatialDimensions(bionetCompart, 3);
			}

		}

	}

	/**
	 * Method to parse the list of reaction of the jsbml model
	 * 
	 * @param model
	 *            the jsbml model
	 */
	protected void parseListOfReactions(Model model) {
		for (Reaction jSBMLReaction : model.getListOfReactions()) {
			String reactionId = jSBMLReaction.getId();

			String reactionName = jSBMLReaction.getName();
			if (reactionName.isEmpty()) {
				reactionName = reactionId;
			}
			BioReaction bionetReaction = new BioReaction(reactionId, reactionName);

			if (jSBMLReaction.isSetSBOTerm()) {
				ReactionAttributes.setSboTerm(bionetReaction, jSBMLReaction.getSBOTermID());
			}

			if (jSBMLReaction.isSetFast()) {
				ReactionAttributes.setFast(bionetReaction, jSBMLReaction.getFast());
			}

			// if reversible attribute is present and is set to false
			if (jSBMLReaction.isSetReversible() && !jSBMLReaction.getReversible()) {
				bionetReaction.setReversible(false);
			} else {
				bionetReaction.setReversible(true);
			}

			this.parseReactionListOf(bionetReaction, jSBMLReaction.getListOfReactants(), "left");
			this.parseReactionListOf(bionetReaction, jSBMLReaction.getListOfProducts(), "right");

			boolean hasBounds = false;

			BioUnitDefinitionCollection udList = NetworkAttributes.getUnitDefinitions(this.getBionet());

			KineticLaw kine = jSBMLReaction.getKineticLaw();
			if (kine != null) {

				ReactionAttributes.setKineticFormula(bionetReaction, kine.getMathMLString());

				if (model.getVersion() < 3) {
					for (int n = 0; n < kine.getNumParameters(); n++) {

						UnitDefinition jsbmlUnit = model.getUnitDefinition(kine.getParameter(n).getUnits());

						if (jsbmlUnit != null) {

							BioUnitDefinition UD = udList.getEntityFromId(jsbmlUnit.getId());

							if (kine.getParameter(n).getId().equalsIgnoreCase("UPPER_BOUND")
									|| kine.getParameter(n).getName().equalsIgnoreCase("UPPER_BOUND")) {

								/**
								 * This is to make sure that the unit definition
								 * associated with the fluxes is not null
								 */
								if (UD == null && (udList.containsId("mmol_per_gDW_per_hr"))) {
									UD = udList.getEntityFromId("mmol_per_gDW_per_hr");
								} else if (UD == null && udList.containsId("FLUX_UNIT")) {
									UD = udList.getEntityFromId("FLUX_UNIT");
								}
								if (UD == null) {
									UD = new BioUnitDefinition();
									udList.add(UD);
								}

								Flux newflux = new Flux(kine.getParameter(n).getValue(), UD);
								ReactionAttributes.setUpperBound(bionetReaction, newflux);

								hasBounds = true;
							} else if (kine.getParameter(n).getId().equalsIgnoreCase("LOWER_BOUND")
									|| kine.getParameter(n).getName().equalsIgnoreCase("LOWER_BOUND")) {

								/**
								 * This is to make sure that the unit definition
								 * associated with the fluxes is not null
								 */
								if (UD == null && udList.containsId("mmol_per_gDW_per_hr")) {
									UD = udList.getEntityFromId("mmol_per_gDW_per_hr");
								} else if (UD == null && udList.containsId("FLUX_UNIT")) {
									UD = udList.getEntityFromId("FLUX_UNIT");
								}
								if (UD == null) {
									UD = new BioUnitDefinition();
									udList.add(UD);

								}

								Flux newflux = new Flux(kine.getParameter(n).getValue(), UD);

								ReactionAttributes.setLowerBound(bionetReaction, newflux);

								hasBounds = true;
							} else if (UD != null) {
								Flux newflux = new Flux(kine.getParameter(n).getValue(), UD);

								ReactionAttributes.addFlux(bionetReaction, newflux);

							} else if (UD == null
									&& kine.getParameter(n).getUnits().equalsIgnoreCase("dimensionless")) {
								UD = new BioUnitDefinition("dimensionless", "dimensionless");
								Flux newflux = new Flux(kine.getParameter(n).getValue(), UD);

								ReactionAttributes.addFlux(bionetReaction, newflux);
							}
						} else {

							BioUnitDefinition UD = new BioUnitDefinition("dimensionless", "dimensionless");
							Flux newflux = new Flux(kine.getParameter(n).getValue(), UD);

							ReactionAttributes.addFlux(bionetReaction, newflux);
						}
					}
				}
			} else { // SBML V3.0
				for (LocalParameter param : kine.getListOfLocalParameters()) {

					UnitDefinition jsbmlUnit = model.getUnitDefinition(param.getUnits());
					if (jsbmlUnit != null) {

						BioUnitDefinition UD = udList.getEntityFromId(jsbmlUnit.getId());

						if (param.getId().equalsIgnoreCase("UPPER_BOUND")
								|| param.getName().equalsIgnoreCase("UPPER_BOUND")) {

							/**
							 * This is to make sure that the unit definition
							 * associated with the fluxes is not null
							 */
							if (UD == null && udList.containsId("mmol_per_gDW_per_hr")) {
								UD = udList.getEntityFromId("mmol_per_gDW_per_hr");
							} else if (UD == null && udList.containsId("FLUX_UNIT")) {
								UD = udList.getEntityFromId("FLUX_UNIT");
							}
							if (UD == null) {
								UD = new BioUnitDefinition(null, null);
								udList.add(UD);
							}

							Flux newflux = new Flux(param.getValue(), UD);
							ReactionAttributes.setUpperBound(bionetReaction, newflux);

							hasBounds = true;
						} else if (param.getId().equalsIgnoreCase("LOWER_BOUND")
								|| param.getName().equalsIgnoreCase("LOWER_BOUND")) {

							/**
							 * This is to make sure that the unit definition
							 * associated with the fluxes is not null
							 */
							if (UD == null && udList.containsId("mmol_per_gDW_per_hr")) {
								UD = udList.getEntityFromId("mmol_per_gDW_per_hr");
							} else if (UD == null && udList.containsId("FLUX_UNIT")) {
								UD = udList.getEntityFromId("FLUX_UNIT");
							}
							if (UD == null) {
								UD = new BioUnitDefinition();
								udList.add(UD);
							}

							Flux newflux = new Flux(param.getValue(), UD);
							ReactionAttributes.setLowerBound(bionetReaction, newflux);

							hasBounds = true;
						} else if (UD != null) {
							Flux newflux = new Flux(param.getValue(), UD);

							ReactionAttributes.addFlux(bionetReaction, newflux);

						} else if (UD == null && param.getUnits().equalsIgnoreCase("dimensionless")) {
							UD = new BioUnitDefinition("dimensionless", "dimensionless");
							Flux newflux = new Flux(param.getValue(), UD);
							ReactionAttributes.addFlux(bionetReaction, newflux);
						}
					} else {

						BioUnitDefinition UD = new BioUnitDefinition("dimensionless", "dimensionless");
						Flux newflux = new Flux(param.getValue(), UD);
						ReactionAttributes.addFlux(bionetReaction, newflux);
					}
				}

			}

			this.getBionet().add(bionetReaction);
		}
	}

	/**
	 * Abstract class to parse the list of SpeciesReferences of a given Reaction
	 * 
	 * @param bionetReaction
	 *            the {@link BioChemicalReaction}
	 * @param listOf
	 *            the jsbml list of SpeciesReferences
	 * @param side
	 *            The side of the SpeciesReferences
	 *            <ul>
	 *            <li>left = reactants
	 *            <li>right = products
	 *            </ul>
	 */
	protected void parseReactionListOf(BioReaction bionetReaction, ListOf<SpeciesReference> listOf, String side) {
		for (SpeciesReference specieRef : listOf) {
			BioReactant reactant = this.parseParticipantSpecies(specieRef);

			if (specieRef.isSetConstant()) {
				ReactantAttributes.setConstant(reactant, specieRef.getConstant());
			}

			if (side.equals("left")) {
				this.getBionet().affectLeft(reactant, bionetReaction);
			} else if (side.equals("right")) {
				this.getBionet().affectRight(reactant, bionetReaction);
			}

		}
	}

	/**
	 * parse the jsbml species participating in a reaction and create the
	 * corresponding metabolite as {@link BioPhysicalEntity} object
	 * 
	 * @param specie
	 *            the Jsbml Species object
	 * @return BioPhysicalEntity
	 */
	protected BioReactant parseParticipantSpecies(SpeciesReference specieRef) {
		Species specie = this.getModel().getSpecies(specieRef.getSpecies());
		String specieId = specie.getId();

		String Stoechio = String.valueOf(specieRef.getStoichiometry());

		BioMetabolite bionetSpecies = this.getBionet().getMetabolitesView().getEntityFromId(specieId);

		if (bionetSpecies == null) {
			String specieName = specie.getName();
			if (specieName.isEmpty()) {
				specieName = specieId;
			}
			bionetSpecies = new BioMetabolite(specieId, specieName);

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

			this.getBionet().add(bionetSpecies);

			this.getBionet().affectToCompartment(bionetSpecies,
					this.getBionet().getCompartmentsView().getEntityFromId(specie.getCompartment()));

		}

		BioReactant reactant = new BioReactant(bionetSpecies, Double.parseDouble(Stoechio),
				this.getBionet().getCompartmentsView().getEntityFromId(specie.getCompartment()));

		return reactant;
	}

	/**
	 * @return the bionet
	 */
	public BioNetwork getBionet() {
		return bionet;
	}

	/**
	 * @param bionet
	 *            the bionet to set
	 */
	public void setBionet(BioNetwork bionet) {
		this.bionet = bionet;
	}

	/**
	 * @return the setOfPackage
	 */
	public ArrayList<PackageParser> getSetOfPackage() {
		return setOfPackage;
	}

	/**
	 * Add a package to this parser
	 * 
	 * @param pkg
	 *            the package to add
	 * @throws JSBMLPackageReaderException
	 *             if the package is not compatible with the the current SBML
	 *             level
	 */
	public void addPackage(PackageParser pkg) throws JSBMLPackageReaderException {

		if (pkg instanceof ReaderSBML2Compatible) {
			this.getSetOfPackage().add(pkg);
		} else {
			throw new JSBMLPackageReaderException("Invalid SBML level and package Reader combination");
		}

	}

	/**
	 * Set the {@link #setOfPackage} to a new list
	 * 
	 * @param packages
	 *            the ordered list of packages to set
	 * @throws JSBMLPackageReaderException
	 *             if one of the package in the list is not compatible with the
	 *             current SBML level
	 */
	public void setPackages(ArrayList<PackageParser> packages) throws JSBMLPackageReaderException {
		for (PackageParser pkg : packages) {
			this.addPackage(pkg);
		}
	}

	/**
	 * Launch the parsing of the jsbml model by the different packages
	 * 
	 * @param model
	 *            the jsbml model
	 */
	public void parsePackageAdditionalData(Model model) {
		for (PackageParser parser : this.getSetOfPackage()) {
			parser.parseModel(model, this.getBionet());
		}
	}

	public Model getModel() {
		return model;
	}

}