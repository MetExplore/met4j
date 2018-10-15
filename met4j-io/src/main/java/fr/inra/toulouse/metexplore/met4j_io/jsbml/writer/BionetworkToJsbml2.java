package parsebionet.io.jsbml.writer;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.CompartmentType;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ASTNode.Type;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioCompartment;
import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPhysicalEntityParticipant;
import parsebionet.biodata.Flux;
import parsebionet.io.jsbml.errors.JSBMLPackageWriterException;
import parsebionet.io.jsbml.writer.plugin.PackageWriter;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML2Compatible;
import parsebionet.utils.StringUtils;

/**
 * This class is used write SBML level 2 models from a given {@link BioNetwork}.
 * It defines the abstract methods declared in {@link BionetworkToJsbml} to meet
 * the SBML level 2 specifications. </br>
 * This class uses deprecated methods of JSBML to stick to the SBML level 2
 * specifications.
 * 
 * @author Benjamin
 * @since 3.0
 * 
 */
@SuppressWarnings("deprecation")
public class BionetworkToJsbml2 extends BionetworkToJsbml {
	/**
	 * The output SBML level : {@value #level}
	 */
	public static final int level = 2;
	/**
	 * The output SBML level version : {@value #vs}
	 */
	public static final int vs = 5;
	/**
	 * The SBML document that will contain the {@link #model} object
	 */
	public SBMLDocument doc;

	/**
	 * Constructor
	 * 
	 * @param doc
	 *            the SBML document
	 */
	public BionetworkToJsbml2(SBMLDocument doc) {
		this.setDoc(doc);
	}

	@Override
	public Model parseBioNetwork(BioNetwork net) {
		this.setModel(this.createModel(net));

		this.createUnits(net);
		this.createCompartments(net);
		this.createSpecies(net);
		this.createReactions(net);

		this.parsePackages(net);

		return this.getModel();
	}

	/**
	 * Create the {@link #model} with the {@link #level} and {@link #vs}
	 * parameters
	 */
	@Override
	protected Model createModel(BioNetwork net) {
		this.getDoc().setLevelAndVersion(level, vs);
		Model model = new Model(level, vs);
		this.getDoc().setModel(model);

		model.setId(StringUtils.convertToSID(net.getId()));
		model.setName(net.getName());

		return model;
	}

	/**
	 * Create Compartments according to the level 2 specifications.</br>
	 * Because of this, this method use jsbml methods that are considered as
	 * deprecated but are kept for backward compatibility
	 */
	@Override
	protected void createCompartments(BioNetwork net) {

		for (BioCompartment compart : net.getCompartments().values()) {
			Compartment LibSBMLCompart = model.createCompartment();

			LibSBMLCompart.setName(compart.getName());
			LibSBMLCompart.setId(StringUtils.convertToSID(compart.getId()));
			LibSBMLCompart.setConstant(compart.isConstant());
			if (!StringUtils.isVoid(compart.getSboterm())) {
				LibSBMLCompart.setSBOTerm(compart.getSboterm());
			}

			LibSBMLCompart.setSpatialDimensions(compart.getSpatialDimensions());

			if ((Double) LibSBMLCompart.getSize() != null && LibSBMLCompart.getSpatialDimensions() == 0) {
				compart.setSpatialDimensions(3);
				compart.setSize(LibSBMLCompart.getSize());
			} else {
				compart.setSize(LibSBMLCompart.getSize());
			}

			if (compart.getOutsideCompartment() != null && model.getLevel() < 3) {
				LibSBMLCompart.setOutside(StringUtils.convertToSID(compart.getOutsideCompartment().getId()));
			}
			if (compart.getCompartmentType() != null && model.getLevel() != 3) {

				if (model.getCompartmentType(compart.getCompartmentType().getId()) == null) {
					CompartmentType cmpType = model.createCompartmentType();
					cmpType.setId(compart.getCompartmentType().getId());
					cmpType.setName(compart.getCompartmentType().getName());
				}
				LibSBMLCompart.setCompartmentType(compart.getCompartmentType().getId());

			}
		}
	}

	/**
	 * Create Reactions according to the level 2 specifications.</br>
	 * Because of this, this method use jsbml methods that are considered as
	 * deprecated but are kept for backward compatibility
	 */
	@Override
	protected void createReactions(BioNetwork net) {

		for (BioChemicalReaction bionetReaction : net.getBiochemicalReactionList().values()) {
			Reaction reaction = model.createReaction();

			reaction.setId(StringUtils.convertToSID(bionetReaction.getId()));
			reaction.setName(bionetReaction.getName());
			reaction.setFast(Boolean.parseBoolean(bionetReaction.getSpontaneous()));

			if (!StringUtils.isVoid(bionetReaction.getSboterm())) {
				reaction.setSBOTerm(bionetReaction.getSboterm());
			}

			if (bionetReaction.getReversiblity().equals("reversible")) {
				reaction.setReversible(true);
			} else {
				reaction.setReversible(false);
			}
			// Set the substrates of the reaction
			for (BioPhysicalEntityParticipant BionetLParticipant : bionetReaction.getLeftParticipantList().values()) {
				SpeciesReference specieRef = reaction.createReactant();
				specieRef.setSpecies(StringUtils.convertToSID(BionetLParticipant.getPhysicalEntity().getId()));
				specieRef.setStoichiometry(Double.parseDouble(BionetLParticipant.getStoichiometricCoefficient()));

			}
			// set the products of the reaction
			for (BioPhysicalEntityParticipant BionetRParticipant : bionetReaction.getRightParticipantList().values()) {
				SpeciesReference specieRef = reaction.createProduct();
				specieRef.setSpecies(StringUtils.convertToSID(BionetRParticipant.getPhysicalEntity().getId()));
				specieRef.setStoichiometry(Double.parseDouble(BionetRParticipant.getStoichiometricCoefficient()));

			}

			KineticLaw law = reaction.createKineticLaw();

			ASTNode ciNode = new ASTNode(Type.NAME);
			ciNode.setName("FLUX_VALUE");
			law.setMath(ciNode);

			if (bionetReaction.getLowerBound() != null && bionetReaction.getUpperBound() != null) {
				Parameter LBound = new Parameter();
				LBound.setId("LOWER_BOUND");
				LBound.setValue(Double.parseDouble(bionetReaction.getLowerBound().value));
				LBound.setUnits(StringUtils.convertToSID(bionetReaction.getLowerBound().unitDefinition.getId()));

				law.addParameter(LBound);

				Parameter UBound = new Parameter();
				UBound.setId("UPPER_BOUND");
				UBound.setValue(Double.parseDouble(bionetReaction.getUpperBound().value));
				UBound.setUnits(StringUtils.convertToSID(bionetReaction.getUpperBound().unitDefinition.getId()));

				law.addParameter(UBound);
			}
			for (Entry<String, Flux> moreParam : bionetReaction.getListOfAdditionalFluxParam().entrySet()) {

				if (law.getLocalParameter(moreParam.getKey()) == null) {
					Parameter param = new Parameter();
					param.setId(moreParam.getKey());
					param.setValue(Double.parseDouble(moreParam.getValue().value));
					param.setUnits(StringUtils.convertToSID(moreParam.getValue().unitDefinition.getId()));

					law.addParameter(param);
				}
			}

			if (law.getLocalParameter("FLUX_VALUE") == null) {
				Parameter flxVal = new Parameter("FLUX_VALUE");
				flxVal.setValue(0);
				flxVal.setUnits(StringUtils.convertToSID(bionetReaction.getLowerBound().unitDefinition.getId()));

				law.addParameter(flxVal);
			}
		}
	}

	@Override
	public void addPackage(PackageWriter pkg) throws JSBMLPackageWriterException {
		if (pkg instanceof WriterSBML2Compatible) {
			this.getSetOfPackage().add(pkg);
		} else {
			throw new JSBMLPackageWriterException("Invalid SBML level and package Writer combination");
		}

	}

	@Override
	public void setPackages(ArrayList<PackageWriter> packages) throws JSBMLPackageWriterException {
		for (PackageWriter pkg : packages) {
			this.addPackage(pkg);
		}

	}

	/**
	 * @return the doc
	 */
	public SBMLDocument getDoc() {
		return doc;
	}

	/**
	 * @param doc
	 *            the doc to set
	 */
	public void setDoc(SBMLDocument doc) {
		this.doc = doc;
	}
}
