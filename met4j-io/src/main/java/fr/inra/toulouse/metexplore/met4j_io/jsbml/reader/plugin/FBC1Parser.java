package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.fbc.FBCModelPlugin;
import org.sbml.jsbml.ext.fbc.FBCSpeciesPlugin;
import org.sbml.jsbml.ext.fbc.FluxBound;
import org.sbml.jsbml.ext.fbc.FluxObjective;
import org.sbml.jsbml.ext.fbc.Objective;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.Flux;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxNetwork;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.Objectives;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.ReactionObjective;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;

/**
 * This class is used to parse SBML level 3 FBC version 1 package.</br></br>To
 * incorporate the additional data, this class uses objects from the
 * {@link parsebionet.biodata.fbc} package
 * 
 * @author Benjamin
 * @since 3.0
 * 
 */
public class FBC1Parser implements PackageParser, PrimaryDataTag,
		ReaderSBML3Compatible {

	/**
	 * The sbml namespace of the FBC version 1 package
	 */
	private String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/fbc/version1";

	/**
	 * The Flux Network created by this parser
	 * 
	 * @see FluxNetwork
	 */
	public FluxNetwork flxNet;

	/**
	 * The SBML fbc model plugin
	 */
	public FBCModelPlugin fbcModel;

	/**
	 * Parse the new object introduced by the SBML fbc version 1 package:
	 * <ul>
	 * <li>The list of flux bounds
	 * <li>The list of flux Objectives
	 * <li>The additional attributes present in the Species elements
	 * </ul>
	 */
	@Override
	public void parseModel(Model model, BioNetwork bionetwork) {

		this.setFlxNet(new FluxNetwork(bionetwork));
		this.setFbcModel((FBCModelPlugin) model.getPlugin("fbc"));
		System.err.println("Starting " + this.getAssociatedPackageName()
				+ " version " + this.getFbcModel().getPackageVersion()
				+ " plugin...");

		this.parseListOfBounds();

		this.parseFluxSpecies();
		this.parseListOfFluxObjective();
	}

	@Override
	public String getAssociatedPackageName() {
		return "fbc";
	}

	@Override
	public boolean isPackageUseableOnModel(Model model) {

		return model.isPackageURIEnabled(PackageNamespace);
	}

	/**
	 * This parse the list of flux bounds present only in SBML fbc v1
	 * models.</br> Because this kind of 'ListOf' is only present in fbc v1
	 * models. The corresponding class and methods are deprecated in jsbml but
	 * are kept for backward compatibility
	 */
	private void parseListOfBounds() {
		for (FluxBound bound : this.getFbcModel().getListOfFluxBounds()) {
			BioReaction rxn = this.getFlxNet().getUnderlyingBionet()
					.getReactionsView().getEntityFromId(bound.getReaction());

			Flux flux = new Flux(bound.getValue());

			
			switch (bound.getOperation()) {
			case EQUAL:
				
				ReactionAttributes.setLowerBound(rxn, flux);
				ReactionAttributes.setUpperBound(rxn, flux);
				
				break;
			case GREATER_EQUAL:

				ReactionAttributes.setLowerBound(rxn, flux);

				break;
			case LESS_EQUAL:

				ReactionAttributes.setUpperBound(rxn, flux);

				break;
			}
		}

	}

	/**
	 * Parse the list of species and uses the data provided by the fbc package
	 * to fill the missing data for the metabolites presents in the bionetwork.
	 * Example are:
	 * <ul>
	 * <li>fbc:charge
	 * <li>fbc:chemicalformula
	 * </ul>
	 */
	protected void parseFluxSpecies() {
		for (Species specie : this.getFbcModel().getParent().getListOfSpecies()) {
			FBCSpeciesPlugin speciePlugin = (FBCSpeciesPlugin) specie
					.getPlugin("fbc");

			BioNetwork net = this.flxNet.getUnderlyingBionet();

			BioMetabolite metabolite = net.getMetabolitesView().getEntityFromId(
					specie.getId());

			if (speciePlugin.isSetCharge())
				metabolite.setCharge(speciePlugin.getCharge());
			if (speciePlugin.isSetChemicalFormula())
				metabolite
						.setChemicalFormula(speciePlugin.getChemicalFormula());
		}

	}

	/**
	 * Parse the list of Flux objective of this FBC model
	 * 
	 * @see Objectives
	 */
	protected void parseListOfFluxObjective() {

		for (Objective fbcObj : this.getFbcModel().getListOfObjectives()) {

			Objectives biodatObj = new Objectives(fbcObj.getId(),
					fbcObj.getName());
			biodatObj.setType(fbcObj.getType().toString());

			for (FluxObjective fbcFluxObj : fbcObj.getListOfFluxObjectives()) {
				ReactionObjective biodataFluxObj = new ReactionObjective(fbcFluxObj.getId(), fbcFluxObj.getName());

				biodataFluxObj.setCoefficient(fbcFluxObj.getCoefficient());

				biodataFluxObj.setFlxReaction(new FluxReaction(this.flxNet
						.getUnderlyingBionet().getReactionsView()
						.getEntityFromId(fbcFluxObj.getReaction())));


				biodatObj.getListOfReactionObjectives().add(biodataFluxObj);
			}
			this.flxNet.getListOfObjectives().put(biodatObj.getId(), biodatObj);
		}

		this.flxNet.setActiveObjective(this.getFbcModel().getActiveObjective());

	}

	/**
	 * @return the flxNet
	 */
	public FluxNetwork getFlxNet() {
		return flxNet;
	}

	/**
	 * @param flxNet
	 *            the flxNet to set
	 */
	public void setFlxNet(FluxNetwork flxNet) {
		this.flxNet = flxNet;
	}

	/**
	 * @return the fbcModel
	 */
	public FBCModelPlugin getFbcModel() {
		return fbcModel;
	}

	/**
	 * @param fbcModel
	 *            the fbcModel to set
	 */
	public void setFbcModel(FBCModelPlugin fbcModel) {
		this.fbcModel = fbcModel;
	}

}
