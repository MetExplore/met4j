package parsebionet.io.jsbml.reader;

import java.util.ArrayList;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.SpeciesType;
import org.sbml.jsbml.UnitDefinition;

import parsebionet.biodata.BioAnnotation;
import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioCompartment;
import parsebionet.biodata.BioCompartmentType;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioPhysicalEntityParticipant;
import parsebionet.biodata.BioUnitDefinition;
import parsebionet.biodata.Flux;
import parsebionet.biodata.Notes;
import parsebionet.io.jsbml.errors.JSBMLPackageReaderException;
import parsebionet.io.jsbml.reader.plugin.PackageParser;
import parsebionet.io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;

/**
 * This class is used to parse SBML level 1 and 2 models. It defines the abstract
 * methods declared in {@link JsbmlToBioNetwork} to met the SBML level 2
 * specifications </br>This class uses deprecated methods of JSBML to stick to
 * the SBML level 2 specifications.
 * 
 * @author bmerlet
 * @since 3.0
 */
public class Jsbml2ToBioNetwork extends JsbmlToBioNetwork {

	@Override
	protected void parseModel(Model model) {

		this.getModelData(model);
		this.parseListOfUnitDefinitions(model);
		this.parseListOfCompartments(model);
		this.parseListOfReactions(model);

		this.parsePackageAdditionalData(model);

	}

	/**
	 * Overrides JsbmlToBioNetwork.parseListOfCompartments to use
	 * CompartementTypes only used in SBML level 2 version 2 through 4
	 */
	protected void parseListOfCompartments(Model model) {

		for (Compartment jSBMLCompart : model.getListOfCompartments()) {

			String compartId = jSBMLCompart.getId();
			String compartName = jSBMLCompart.getName();

			if (compartName.isEmpty()) {
				compartName = compartId;
			}

			BioCompartment bionetCompart = this.getBionet().getCompartments()
					.get(compartId);
			if (bionetCompart == null) {
				bionetCompart = new BioCompartment(compartName, compartId);
				this.getBionet().addCompartment(bionetCompart);
			}

			/*
			 * If the compartment has an outside attribute, we need to look if
			 * the outside compartment exists in the bioNetwork
			 */
			if (jSBMLCompart.isSetOutside()) {

				Compartment outsideJSBMLComp = model
						.getCompartment(jSBMLCompart.getOutside());

				BioCompartment outsideCompart = this.getBionet()
						.getCompartments().get(outsideJSBMLComp.getId());

				// if it's null, we create it and add it to the bionetwork
				if (outsideCompart == null) {
					outsideCompart = new BioCompartment(
							outsideJSBMLComp.getName(),
							outsideJSBMLComp.getId());
					this.getBionet().addCompartment(outsideCompart);
				}

				// We can add it as outside compartment of the current
				// compartment
				bionetCompart.setOutsideCompartment(outsideCompart);
			}
			if (jSBMLCompart.isSetCompartmentType()) {
				BioCompartmentType bionetCompartType = this.getBionet()
						.findbioCompartmentTypeInList(
								jSBMLCompart.getCompartmentType());

				if (bionetCompartType == null) {
					bionetCompartType = new BioCompartmentType(
							jSBMLCompart.getCompartmentType(), model
									.getCompartmentType(
											jSBMLCompart.getCompartmentType())
									.getName());

					this.getBionet().addCompartmentType(bionetCompartType);
				}
				bionetCompart.setCompartmentType(bionetCompartType);
			}

			if (jSBMLCompart.isSetUnits()) {

				UnitDefinition JsbmlUnitDef = model
						.getUnitDefinition(jSBMLCompart.getUnits());
				BioUnitDefinition bionetUnitDef = this.getBionet()
						.getUnitDefinitions().get(JsbmlUnitDef.getId());

				bionetCompart.setUnit(bionetUnitDef);
			}

			bionetCompart.setSboterm(jSBMLCompart.getSBOTermID());
			bionetCompart.setConstant(jSBMLCompart.getConstant());
			if (jSBMLCompart.isSetSize()) {
				bionetCompart.setSize(jSBMLCompart.getSize());
			} else {
				bionetCompart.setSize(1);
			}
			bionetCompart.setSpatialDimensions((int) jSBMLCompart
					.getSpatialDimensions());

		}

	}

	/**
	 * This class overrides the
	 * {@link JsbmlToBioNetwork#parseListOfReactions(Model)} in order to use the
	 * SBMl Parameter object only used in SBML level 2
	 */
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

			boolean hasBounds = false;

			KineticLaw kine = jSBMLReaction.getKineticLaw();
			if (kine != null) {

				bionetReaction.setKineticFormula(kine.getMathMLString());

				for (int n = 0; n < kine.getNumParameters(); n++) {

					UnitDefinition jsbmlUnit = model.getUnitDefinition(kine
							.getParameter(n).getUnits());

					if (jsbmlUnit != null) {

						BioUnitDefinition UD = this.getBionet()
								.getUnitDefinitions().get(jsbmlUnit.getId());

						if (kine.getParameter(n).getId()
								.equalsIgnoreCase("UPPER_BOUND")
								|| kine.getParameter(n).getName()
										.equalsIgnoreCase("UPPER_BOUND")) {

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

							Flux newflux = new Flux(""
									+ kine.getParameter(n).getValue(), UD);
							bionetReaction.setUpperBound(newflux);
							hasBounds = true;
						} else if (kine.getParameter(n).getId()
								.equalsIgnoreCase("LOWER_BOUND")
								|| kine.getParameter(n).getName()
										.equalsIgnoreCase("LOWER_BOUND")) {

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

							Flux newflux = new Flux(""
									+ kine.getParameter(n).getValue(), UD);
							bionetReaction.setLowerBound(newflux);
							hasBounds = true;
						} else if (UD != null) {
							Flux newflux = new Flux(""
									+ kine.getParameter(n).getValue(), UD);
							bionetReaction.addFluxParam(kine.getParameter(n)
									.getId(), newflux);
						} else if (UD == null
								&& kine.getParameter(n).getUnits()
										.equalsIgnoreCase("dimensionless")) {
							UD = new BioUnitDefinition("dimensionless",
									"dimensionless");
							Flux newflux = new Flux(""
									+ kine.getParameter(n).getValue(), UD);
							bionetReaction.addFluxParam(kine.getParameter(n)
									.getId(), newflux);
						}
					} else {

						BioUnitDefinition UD = new BioUnitDefinition(
								"dimensionless", "dimensionless");
						Flux newflux = new Flux(""
								+ kine.getParameter(n).getValue(), UD);
						bionetReaction.addFluxParam(kine.getParameter(n)
								.getId(), newflux);
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

			if (specie.isSetCharge()) {
				bionetSpecies.setCharge(specie.getCharge() + "");
			}

			if (specie.isSetSpeciesType()) {

				SpeciesType stype = specie.getSpeciesTypeInstance();

				if (stype.isSetSBOTerm()) {
					bionetSpecies.setSboterm(stype.getSBOTermID());
				}
				try {
					if (stype.isSetAnnotation()) {
						bionetSpecies.setEntityAnnot(new BioAnnotation(stype
								.getMetaId(), stype.getAnnotationString()));
					}
					if (stype.isSetNotes()) {
						bionetSpecies.setEntityNotes(new Notes(stype
								.getNotesString()));
					}
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
			}

			this.getBionet().addPhysicalEntity(bionetSpecies);
		}
		return bionetSpecies;
	}

	public void setPackages(ArrayList<PackageParser> packages)
			throws JSBMLPackageReaderException {

		for (PackageParser pkg : packages) {
			this.addPackage(pkg);
		}

	}

	@Override
	public void addPackage(PackageParser pkg)
			throws JSBMLPackageReaderException {
		if (pkg instanceof ReaderSBML2Compatible) {
			this.getSetOfPackage().add(pkg);
		} else {
			throw new JSBMLPackageReaderException(
					"Invalid SBML level and package Reader combination");
		}
	}

}
