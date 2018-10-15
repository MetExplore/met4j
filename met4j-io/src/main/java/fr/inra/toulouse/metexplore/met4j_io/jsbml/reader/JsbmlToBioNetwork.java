package parsebionet.io.jsbml.reader;

import java.util.ArrayList;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.Unit.Kind;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioCompartment;
import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioUnitDefinition;
import parsebionet.biodata.UnitSbml;
import parsebionet.io.jsbml.errors.JSBMLPackageReaderException;
import parsebionet.io.jsbml.reader.plugin.PackageParser;

/**
 * Abstract class that defines the different 'ListOf' parsing method.
 * @author bmerlet
 * @since 3.0
 */
public abstract class JsbmlToBioNetwork {

	/**
	 * The {@link BioNetwork} created by this class
	 */
	protected BioNetwork bionet;

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
	protected abstract void parseModel(Model model);

	/**
	 * Parse the jsbml Model object and retrieves basic information from it
	 * 
	 * @param model
	 *            the jsbml model
	 */
	protected void getModelData(Model model) {

		BioNetwork bionet = new BioNetwork();

		bionet.setId(model.getId());
		bionet.setName(model.getName());
		bionet.setType("sbml" + model.getLevel() + "." + model.getVersion());
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

			BioUnitDefinition bionetUD = new BioUnitDefinition(jSBMLUD.getId(),
					jSBMLUD.getName());

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
					String Multiplier = String.valueOf(jSBMLUnit
							.getMultiplier());

					UnitSbml bionetUnit = new UnitSbml(kind.getName()
							.toUpperCase(), Exp, Scale, Multiplier);
					bionetUD.addUnit(bionetUnit);
				}
			}
			this.getBionet().addUnitDefinition(bionetUD);
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
			if (jSBMLCompart.isSetSpatialDimensions()) {
				bionetCompart.setSpatialDimensions((int) jSBMLCompart
						.getSpatialDimensions());
			} else {
				bionetCompart.setSpatialDimensions(3);
			}

		}

	}

	/**
	 * Method to parse the list of reaction of the jsbml model
	 * 
	 * @param model
	 *            the jsbml model
	 */
	protected abstract void parseListOfReactions(Model model);

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
	protected abstract void parseReactionListOf(
			BioChemicalReaction bionetReaction,
			ListOf<SpeciesReference> listOf, String side);

	/**
	 * parse the jsbml species participating in a reaction and create the
	 * corresponding metabolite as {@link BioPhysicalEntity} object
	 * 
	 * @param specie
	 *            the Jsbml Species object
	 * @return BioPhysicalEntity
	 */
	protected abstract BioPhysicalEntity parseParticipantSpecies(Species specie);

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
	public abstract void addPackage(PackageParser pkg)
			throws JSBMLPackageReaderException;

	/**
	 * Set the {@link #setOfPackage} to a new list
	 * 
	 * @param packages
	 *            the ordered list of packages to set
	 * @throws JSBMLPackageReaderException
	 *             if one of the package in the list is not compatible with the
	 *             current SBML level
	 */
	public abstract void setPackages(ArrayList<PackageParser> packages)
			throws JSBMLPackageReaderException;

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

}