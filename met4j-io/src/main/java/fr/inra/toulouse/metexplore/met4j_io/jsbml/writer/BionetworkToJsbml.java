package parsebionet.io.jsbml.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.Unit.Kind;

import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioUnitDefinition;
import parsebionet.biodata.UnitSbml;
import parsebionet.io.jsbml.errors.JSBMLPackageWriterException;
import parsebionet.io.jsbml.writer.plugin.PackageWriter;
import parsebionet.utils.StringUtils;

/**
 * Abstract class that defines all the methods used to parse the bioNetwork
 * regardless of the requested output SBML level
 * 
 * @author Benjamin
 * @since 3.0
 */
public abstract class BionetworkToJsbml {

	/**
	 * the SBML {@link Model}
	 */
	public Model model;

	/**
	 * The ordered list of {@link PackageWriter} activated for this parser
	 */
	public List<PackageWriter> setOfPackage = new ArrayList<PackageWriter>();

	/**
	 * Launches the parsing of the BioNetwork and create all the SBML components
	 * from it
	 * 
	 * @param net
	 *            the input {@link BioNetwork}
	 * @return The completed SBML Model
	 */
	public abstract Model parseBioNetwork(BioNetwork net);

	/**
	 * Abstract method to create the top {@link Model} object that will contain
	 * all the other SBMl components
	 * 
	 * @param net
	 *            the input {@link BioNetwork}
	 * @return a simple model created from the attribute of the Bionetwork
	 */
	protected abstract Model createModel(BioNetwork net);

	/**
	 * Default way of creating SBML Unit definitions. This method should be
	 * overridden to change its behavior.
	 * 
	 * @param net
	 *            The bionetwork
	 */
	protected void createUnits(BioNetwork net) {

		for (BioUnitDefinition bioUD : net.getUnitDefinitions().values()) {
			UnitDefinition ud = model.createUnitDefinition();

			ud.setId(StringUtils.convertToSID(bioUD.getId()));
			ud.setName(bioUD.getName());

			for (UnitSbml bioUnits : bioUD.getUnits().values()) {
				Unit libSBMLUnit = ud.createUnit();
				libSBMLUnit.setExponent(Double.parseDouble(bioUnits
						.getExponent()));
				libSBMLUnit.setMultiplier(Double.parseDouble(bioUnits
						.getMultiplier()));
				libSBMLUnit.setScale(Integer.parseInt(bioUnits.getScale()));

				libSBMLUnit.setKind(Kind.valueOf(bioUnits.getKind()
						.toUpperCase()));
			}
		}
	}

	/**
	 * Abstract method to create SBML Comparments in {@link #model} from the
	 * {@link BioNetwork}
	 * 
	 * @param net
	 *            The Bionetwork
	 */
	protected abstract void createCompartments(BioNetwork net);

	/**
	 * Common Method to create SBML Species in {@link #model} from the
	 * {@link BioNetwork}
	 * 
	 * @param net
	 *            The Bionetwork
	 */
	protected void createSpecies(BioNetwork net) {

		for (BioPhysicalEntity bioMetab : net.getPhysicalEntityList().values()) {

			Compartment comp = model.getCompartment(StringUtils
					.convertToSID(bioMetab.getCompartment().getId()));

			Species metab = model.createSpecies(
					StringUtils.convertToSID(bioMetab.getId()), comp);

			metab.setName(bioMetab.getName());
			metab.setBoundaryCondition(bioMetab.getBoundaryCondition());

			metab.setConstant(bioMetab.getConstant());

			if (!StringUtils.isVoid(bioMetab.getSboterm())) {
				metab.setSBOTerm(bioMetab.getSboterm());
			} else {
				metab.setSBOTerm("SBO:0000299");
			}

			metab.setHasOnlySubstanceUnits(bioMetab.getHasOnlySubstanceUnit());

			if (bioMetab.getInitialQuantity().size() == 1) {
				for (Entry<String, Double> quantity : bioMetab
						.getInitialQuantity().entrySet()) {
					if (quantity.getKey().equals("amount")) {
						metab.setInitialAmount(quantity.getValue());
					} else if (quantity.getKey().equals("concentration")) {
						metab.setInitialConcentration(quantity.getValue());
					}
				}
			}

		}

	}

	/**
	 * Abstract method to create SBML Reactions in {@link #model} from the
	 * {@link BioNetwork}
	 * 
	 * @param net
	 *            The Bionetwork
	 */
	protected abstract void createReactions(BioNetwork net);

	/**
	 * Launches the set writer Package on the Input {@link BioNetwork}
	 * 
	 * @param net
	 *            The Bionetwork
	 */
	public void parsePackages(BioNetwork net) {
		for (PackageWriter parser : this.getSetOfPackage()) {
			parser.parseBionetwork(model, net);
		}
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the set of package
	 */
	public List<PackageWriter> getSetOfPackage() {
		return setOfPackage;
	}

	/**
	 * add a package to the list
	 * 
	 * @param pkg
	 *            the package to add
	 * @throws JSBMLPackageWriterException
	 *             when the added package is incompatible with the current SBML
	 *             level
	 */
	public abstract void addPackage(PackageWriter pkg)
			throws JSBMLPackageWriterException;

	/**
	 * Set the {@link #setOfPackage} to a new list
	 * 
	 * @param packages
	 *            the ordered list of packages to set
	 * @throws JSBMLPackageWriterException
	 *             if one of the package in the list is not compatible with the
	 *             current SBML level
	 */
	public abstract void setPackages(ArrayList<PackageWriter> packages)
			throws JSBMLPackageWriterException;

}
