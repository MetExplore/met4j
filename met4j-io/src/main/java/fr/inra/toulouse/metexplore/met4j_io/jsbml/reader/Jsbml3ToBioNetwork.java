package parsebionet.io.jsbml.reader;

import java.util.ArrayList;

import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.UnitDefinition;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioPhysicalEntityParticipant;
import parsebionet.biodata.BioProtein;
import parsebionet.biodata.BioUnitDefinition;
import parsebionet.biodata.Flux;
import parsebionet.io.jsbml.errors.JSBMLPackageReaderException;
import parsebionet.io.jsbml.reader.plugin.PackageParser;
import parsebionet.io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;

/**
 * This class is used to parse SBML level 3 models. It defines the abstract
 * methods declared in {@link JsbmlToBioNetwork} to met the SBML level 3
 * specifications
 * 
 * @author Benjamin
 * @since 3.0
 */
public class Jsbml3ToBioNetwork extends JsbmlToBioNetwork {

	@Override
	protected void parseModel(Model model) {
		this.getModelData(model);
		this.parseListOfUnitDefinitions(model);
		this.parseListOfCompartments(model);
		this.parseListOfReactions(model);

		this.parsePackageAdditionalData(model);

	}

	@Override
	protected void parseListOfReactions(Model model) {

		for (Reaction jSBMLReaction : model.getListOfReactions()) {

			String reactionId = jSBMLReaction.getId();

			String reactionName = jSBMLReaction.getName();
			if (reactionName.isEmpty()) {
				reactionName = reactionId;
			}

			BioChemicalReaction bionetReaction = new BioChemicalReaction(
					reactionId, reactionName);

			bionetReaction.setSboterm(jSBMLReaction.getSBOTermID());

			if (jSBMLReaction.isSetFast()) {
				bionetReaction.setSpontaneous(String.valueOf(jSBMLReaction
						.getFast()));
			} else {
				bionetReaction.setSpontaneous("false");
			}

			// if reversible attribute is present and is set to false
			if (jSBMLReaction.isSetReversible()
					&& !jSBMLReaction.getReversible()) {
				bionetReaction.setReversibility(false);
			} else {
				bionetReaction.setReversibility(true);
			}

			this.parseReactionListOf(bionetReaction,
					jSBMLReaction.getListOfReactants(), "left");
			this.parseReactionListOf(bionetReaction,
					jSBMLReaction.getListOfProducts(), "right");

			bionetReaction.setListOfSubstrates();
			bionetReaction.setListOfProducts();

			// this.parseReactionListOf(bionetReaction,jSBMLReaction.getListOfModifiers(),
			// "modifiers");

			boolean hasBounds = false;

			KineticLaw kine = jSBMLReaction.getKineticLaw();
			if (kine != null) {

				bionetReaction.setKineticFormula(kine.getMathMLString());

				for (LocalParameter param : kine.getListOfLocalParameters()) {

					UnitDefinition jsbmlUnit = model.getUnitDefinition(param
							.getUnits());
					if (jsbmlUnit != null) {

						BioUnitDefinition UD = this.getBionet()
								.getUnitDefinitions().get(jsbmlUnit.getId());
						if (param.getId().equalsIgnoreCase("UPPER_BOUND")
								|| param.getName().equalsIgnoreCase(
										"UPPER_BOUND")) {

							/**
							 * This is to make sure that the unit definition
							 * associated with the fluxes is not null
							 */
							if (UD == null
									&& this.getBionet().getUnitDefinitions()
											.containsKey("mmol_per_gDW_per_hr")) {
								UD = this.getBionet().getUnitDefinitions()
										.get("mmol_per_gDW_per_hr");
							} else if (UD == null
									&& this.getBionet().getUnitDefinitions()
											.containsKey("FLUX_UNIT")) {
								UD = this.getBionet().getUnitDefinitions()
										.get("FLUX_UNIT");
							}
							if (UD == null) {
								UD = new BioUnitDefinition();
								UD.setDefault();
								this.getBionet().getUnitDefinitions()
										.put(UD.getId(), UD);
							}

							Flux newflux = new Flux(String.valueOf(param
									.getValue()), UD);
							bionetReaction.setUpperBound(newflux);
							hasBounds = true;
						} else if (param.getId()
								.equalsIgnoreCase("LOWER_BOUND")
								|| param.getName().equalsIgnoreCase(
										"LOWER_BOUND")) {

							/**
							 * This is to make sure that the unit definition
							 * associated with the fluxes is not null
							 */
							if (UD == null
									&& this.getBionet().getUnitDefinitions()
											.containsKey("mmol_per_gDW_per_hr")) {
								UD = this.getBionet().getUnitDefinitions()
										.get("mmol_per_gDW_per_hr");
							} else if (UD == null
									&& this.getBionet().getUnitDefinitions()
											.containsKey("FLUX_UNIT")) {
								UD = this.getBionet().getUnitDefinitions()
										.get("FLUX_UNIT");
							}
							if (UD == null) {
								UD = new BioUnitDefinition();
								UD.setDefault();
								this.getBionet().getUnitDefinitions()
										.put(UD.getId(), UD);
							}

							Flux newflux = new Flux(String.valueOf(param
									.getValue()), UD);
							bionetReaction.setLowerBound(newflux);
							hasBounds = true;
						} else if (UD != null) {
							Flux newflux = new Flux(String.valueOf(param
									.getValue()), UD);
							bionetReaction.addFluxParam(param.getId(), newflux);
						} else if (UD == null
								&& param.getUnits().equalsIgnoreCase(
										"dimensionless")) {
							UD = new BioUnitDefinition("dimensionless",
									"dimensionless");
							Flux newflux = new Flux(String.valueOf(param
									.getValue()), UD);
							bionetReaction.addFluxParam(param.getId(), newflux);
						}
					} else {

						BioUnitDefinition UD = new BioUnitDefinition(
								"dimensionless", "dimensionless");
						Flux newflux = new Flux(
								String.valueOf(param.getValue()), UD);

						bionetReaction.addFluxParam(param.getId(), newflux);
					}
				}

			}
			/*
			 * This allow to override the automatic instantiation of this two
			 * fluxes in the reaction instantiation.
			 */
			if (!hasBounds) {
				bionetReaction.setUpperBound(null);
				bionetReaction.setLowerBound(null);
			}

			this.getBionet().addBiochemicalReaction(bionetReaction);
		}

	}

	/**
	 * Parse the lists of species reference of a sbml reaction node (ie
	 * listOfReactant and listOfProduct) and add them in the
	 * BioChemicalReaction. The side parameter decides in which side the current
	 * list is going to be passed. Current value accepted for the side parameter
	 * are "left" and "right"
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
	@Override
	protected void parseReactionListOf(BioChemicalReaction bionetReaction,
			ListOf<SpeciesReference> listOf, String side) {

		for (SpeciesReference specieRef : listOf) {

			Species specie = listOf.getModel().getSpecies(
					specieRef.getSpecies());

			BioPhysicalEntity metabolite = this.parseParticipantSpecies(specie);

			String bionetMetabId = metabolite.getId();
			String Stoechio = String.valueOf(specieRef.getStoichiometry());
			String participantId;
			if (specieRef.isSetId()) {
				participantId = specieRef.getId();
			} else {
				participantId = bionetReaction.getId() + "__With__"
						+ bionetMetabId;
			}
			BioPhysicalEntityParticipant bionetSubsPart = new BioPhysicalEntityParticipant(
					participantId, metabolite, Stoechio,
					metabolite.getCompartment());

			if (specieRef.isSetConstant()) {
				bionetSubsPart.setIsConstant(specieRef.getConstant());
			} else {
				bionetSubsPart.setIsConstant(false);
			}

			if (side.equals("left")) {
				bionetReaction.addLeftParticipant(bionetSubsPart);
			} else if (side.equals("right")) {
				bionetReaction.addRightParticipant(bionetSubsPart);
			}

		}

	}

	@Override
	protected BioPhysicalEntity parseParticipantSpecies(Species specie) {

		String specieId = specie.getId();

		BioPhysicalEntity bionetSpecies = this.getBionet()
				.getBioPhysicalEntityById(specieId);
		if (bionetSpecies != null) {
			return bionetSpecies;
		} else {

			String specieName = specie.getName();
			if (specieName.isEmpty()) {
				specieName = specieId;
			}
			bionetSpecies = new BioPhysicalEntity(specieId, specieName);

			bionetSpecies.setBoundaryCondition(specie.getBoundaryCondition());
			bionetSpecies.setConstant(specie.getConstant());
			bionetSpecies.setSubstanceUnits(specie.getSubstanceUnits());
			bionetSpecies.setSboterm(specie.getSBOTermID());

			bionetSpecies.setCompartment(this.getBionet().getCompartments()
					.get(specie.getCompartment()));

			if (specie.isSetInitialAmount()) {
				bionetSpecies.addInitialQuantity("amount",
						specie.getInitialAmount());
			} else if (specie.isSetInitialConcentration()) {
				bionetSpecies.addInitialQuantity("Concentration",
						specie.getInitialConcentration());
			}

			this.getBionet().addPhysicalEntity(bionetSpecies);
		}
		return bionetSpecies;
	}

	/**
	 * @deprecated Modifiers are not used anymore in the parser.</br>To add
	 *             enzyme/gene data, please use the
	 *             {@link parsebionet.io.jsbml.reader.plugin.FBC2Parser} or the
	 *             {@link parsebionet.io.jsbml.reader.plugin.NotesParser}
	 *             plugins
	 * @param modSpecie
	 *            the SMBL Species object corresponding to the modifier
	 * @return the modifier BioPhysicalEntity
	 */
	@Deprecated
	protected BioPhysicalEntity parseModifierSpecies(Species modSpecie) {

		String specieId = modSpecie.getId();

		// we check if the modifier is in any of the list (bioprotein,
		// biocomplex, physicalentity)
		if (this.getBionet().getProteinList().containsKey(specieId)) {
			return this.getBionet().getProteinList().get(specieId);
		} else if (this.getBionet().getComplexList().containsKey(specieId)) {
			return this.getBionet().getComplexList().get(specieId);
		} else if (this.getBionet().getPhysicalEntityList()
				.containsKey(specieId)) {
			return this.getBionet().getPhysicalEntityList().get(specieId);
		}
		// if it doesn't, create it, set its parameter and return it.
		else {

			String specieName = modSpecie.getName();
			if (specieName.isEmpty()) {
				specieName = specieId;
			}

			BioProtein proteinModifier = new BioProtein(specieId, specieName);
			proteinModifier.setId(specieId);
			proteinModifier.setName(specieName);

			proteinModifier.setBoundaryCondition(modSpecie
					.getBoundaryCondition());
			proteinModifier.setConstant(modSpecie.getConstant());
			proteinModifier.setSubstanceUnits(modSpecie.getSubstanceUnits());
			proteinModifier.setSboterm(modSpecie.getSBOTermID());

			// get the compartment
			proteinModifier.setCompartment(this.getBionet().getCompartments()
					.get(modSpecie.getCompartment()));

			// get initial quantity if it is present, normally not for enzymes.
			if (modSpecie.isSetInitialAmount()) {
				proteinModifier.addInitialQuantity("amount",
						modSpecie.getInitialAmount());
			} else if (modSpecie.isSetInitialConcentration()) {
				proteinModifier.addInitialQuantity("concentration",
						modSpecie.getInitialConcentration());
			}

			return proteinModifier;
		}
	}

	@Override
	public void addPackage(PackageParser pkg)
			throws JSBMLPackageReaderException {

		if (pkg instanceof ReaderSBML3Compatible) {
			this.getSetOfPackage().add(pkg);
		} else {
			throw new JSBMLPackageReaderException(
					"Invalid SBML level and package Reader combination");
		}
	}

	@Override
	public void setPackages(ArrayList<PackageParser> packages)
			throws JSBMLPackageReaderException {
		for (PackageParser pkg : packages) {
			this.addPackage(pkg);
		}

	}

}