package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.ext.fbc.And;
import org.sbml.jsbml.ext.fbc.Association;
import org.sbml.jsbml.ext.fbc.FBCModelPlugin;
import org.sbml.jsbml.ext.fbc.FBCReactionPlugin;
import org.sbml.jsbml.ext.fbc.FBCSpeciesPlugin;
import org.sbml.jsbml.ext.fbc.FluxObjective;
import org.sbml.jsbml.ext.fbc.GeneProduct;
import org.sbml.jsbml.ext.fbc.GeneProductRef;
import org.sbml.jsbml.ext.fbc.Objective;
import org.sbml.jsbml.ext.fbc.Or;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.Flux;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxNetwork;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.GeneSet;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjective;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjectiveCollection;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.ReactionObjective;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;

/**
 * This class is used to parse SBML level 3 FBC version 2 package. It extends
 * the {@link FBC1Parser} class because the SBML packages share some common
 * objects.</br>
 * </br>
 * To incorporate the additional data, this class uses objects from the
 * {@link parsebionet.biodata.fbc} package
 * 
 * @author Benjamin
 * @since 3.0
 * 
 */
public class FBCParser implements PackageParser, PrimaryDataTag, ReaderSBML3Compatible {

	/**
	 * The sbml namespace of the FBC version 2 package
	 */
	private String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/fbc/version2";

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

	@Override
	public String getAssociatedPackageName() {
		return "fbc";
	}

	@Override
	public boolean isPackageUseableOnModel(Model model) {
		return model.isPackageURIEnabled(PackageNamespace);
	}

	/**
	 * Parse the new object introduced by the SBML FBC version 2 package and
	 * some of the objects introduced by the FBC version 1 package:
	 * <ul>
	 * <li>The list of flux bounds, (introduced in fbc v1 but now present in the
	 * list of Global Parameters)
	 * <li>The list of flux Objectives (introduced in fbc v1)
	 * <li>The list of Gene Product (introduced in fbc v2)
	 * <li>The additional attributes present in the Species elements (introduced
	 * in fbc v1)
	 * <li>The additional attributes present in the Reaction elements
	 * (introduced in fbc v2) including the GeneAssociation Objects
	 * </ul>
	 */
	public void parseModel(Model model, BioNetwork bionetwork) {

		this.setFlxNet(new FluxNetwork(bionetwork));
		this.setFbcModel((FBCModelPlugin) model.getPlugin("fbc"));
		System.err.println("Starting " + this.getAssociatedPackageName() + " version "
				+ this.getFbcModel().getPackageVersion() + " plugin...");

		this.setStrictFromFbcModel();

		this.parseParameters();
		this.parseListOfGeneProducts();
		this.parseFluxReactions();

		/**
		 * Same Methods as FBC1 parser.
		 */
		this.parseFluxSpecies();
		this.parseListOfFluxObjectives();

	}

	/**
	 * Get FBC data only present in the SBML Model object, ie the fbc:strict
	 * attribute
	 * 
	 * @see FluxNetwork#setFbcStrict(boolean)
	 */
	private void setStrictFromFbcModel() {
		
		this.getFlxNet().setFbcStrict(this.getFbcModel().isSetStrict() ? this.getFbcModel().getStrict() : false);
	}

	/**
	 * Retrieves the list of global parameters of this model. This list includes
	 * all the possible flux bounds values of the reactions in this model.
	 */
	private void parseParameters() {

		BioUnitDefinitionCollection udList = NetworkAttributes.getUnitDefinitions(this.flxNet.getUnderlyingBionet());

		for (Parameter gParam : this.getFbcModel().getParent().getListOfParameters()) {
			Flux bioParam = new Flux(gParam.getId());
			bioParam.setConstant(gParam.getConstant());
			bioParam.value = gParam.getValue();

			bioParam.unitDefinition = (BioUnitDefinition) (udList.getEntityFromId(gParam.getUnits()));

			this.flxNet.addFluxBound(bioParam);
		}
	}

	/**
	 * Parse the list of GeneProduct to create the corresponding genes. Only
	 * introduced in package fbc version 2
	 */
	private void parseListOfGeneProducts() {
		for (GeneProduct geneProd : this.getFbcModel().getListOfGeneProducts()) {
			String geneId = geneProd.getId();
			String geneName = geneProd.getName();

			BioGene gene = new BioGene(geneId, geneName);

			// Note LC : before, it was "setLabel"...
			gene.setName(geneProd.getLabel());

			this.getFlxNet().getUnderlyingBionet().add(gene);
		}
	}

	/**
	 * Parse the list of reaction and uses the data provided by the fbc package
	 * to fill the missing data for the reactions presents in the bionetwork.
	 * Example are:
	 * <ul>
	 * <li>fbc:upperbound
	 * <li>fbc:lowerbound
	 * <li>fbc:GeneProductAssociation
	 * </ul>
	 */
	private void parseFluxReactions() {

		for (Reaction rxn : this.getFbcModel().getParent().getListOfReactions()) {

			BioReaction reaction = this.flxNet.getUnderlyingBionet().getReactionsView().getEntityFromId(rxn.getId());

			FBCReactionPlugin rxnPlugin = (FBCReactionPlugin) rxn.getPlugin("fbc");
			FluxReaction flxReaction = new FluxReaction(reaction);

			GeneAssociation geneAssociation = new GeneAssociation();

			// System.err.println(rxn.getId());
			if (rxnPlugin.isSetGeneProductAssociation()) {
				geneAssociation = this.computeGeneAssocations(rxnPlugin.getGeneProductAssociation().getAssociation());
			}
			// System.err.println("out of recursion");
			flxReaction.setReactionGeneAssociation(geneAssociation);
			flxReaction.convertGeneAssociationstoComplexes(flxNet.getUnderlyingBionet());

			ReactionAttributes.setLowerBound(reaction,
					this.flxNet.getListOfFluxBounds().get(rxnPlugin.getLowerFluxBound()));

			ReactionAttributes.setLowerBound(reaction,
					this.flxNet.getListOfFluxBounds().get(rxnPlugin.getLowerFluxBound()));

			ReactionAttributes.setUpperBound(reaction,
					this.flxNet.getListOfFluxBounds().get(rxnPlugin.getUpperFluxBound()));

			this.flxNet.getListOfFluxReactions().put(flxReaction.getId(), flxReaction);

		}

	}

	/**
	 * Recursively parse Association blocks to retrieve all possible combination
	 * of Gene associations
	 * 
	 * @param block
	 *            the current Association block
	 * @return an ArrayList of {@link GeneSet}
	 */
	private GeneAssociation computeGeneAssocations(Association block) {

		GeneAssociation geneAssociation = new GeneAssociation();

		if (block != null) {

			if (block.getClass().getSimpleName().equals("And")) {
				And andBlock = (And) block;

				for (Association andEl : andBlock.getListOfAssociations()) {
					for (GeneSet x : this.computeGeneAssocations(andEl)) {

						if (geneAssociation.isEmpty()) {
							geneAssociation.add(x);
						} else {

							for (GeneSet y : geneAssociation) {
								y.addAll(x);
							}
						}

					}

				}

			} else if (block.getClass().getSimpleName().equals("Or")) {
				Or orBlock = (Or) block;

				for (Association orEl : orBlock.getListOfAssociations()) {
					geneAssociation.addAll(this.computeGeneAssocations(orEl));
				}

			} else  {
				
				// The association is composed of a GeneProductRef.

				GeneProductRef geneRef = (GeneProductRef) block;
				GeneSet GA = new GeneSet();

				GA.setId(geneRef.getId());

				BioGene g = this.flxNet.getUnderlyingBionet().getGenesView().getEntityFromId(geneRef.getGeneProduct());
				GA.add(g);

				geneAssociation.add(GA);
			}
		}

		return geneAssociation;
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
	private void parseFluxSpecies() {
		for (Species specie : this.getFbcModel().getParent().getListOfSpecies()) {
			FBCSpeciesPlugin speciePlugin = (FBCSpeciesPlugin) specie.getPlugin("fbc");

			BioNetwork net = this.flxNet.getUnderlyingBionet();

			BioMetabolite metabolite = net.getMetabolitesView().getEntityFromId(specie.getId());

			if (speciePlugin.isSetCharge())
				metabolite.setCharge(speciePlugin.getCharge());
			if (speciePlugin.isSetChemicalFormula())
				metabolite.setChemicalFormula(speciePlugin.getChemicalFormula());
		}

	}

	/**
	 * Parse the list of Flux objective of this FBC model
	 * 
	 * @see BioObjective
	 */
	private void parseListOfFluxObjectives() {

		
		BioObjectiveCollection objectives = new BioObjectiveCollection();

		for (Objective fbcObj : this.getFbcModel().getListOfObjectives()) {

			BioObjective objective = new BioObjective(fbcObj.getId(), fbcObj.getName());
			
			objective.setType(fbcObj.getType().toString());
			
			objective.active = this.getFbcModel().getActiveObjective().equals(fbcObj.getId()) ? true : false;

			for (FluxObjective fbcFluxObj : fbcObj.getListOfFluxObjectives()) {
				
				String id, name;
				
				BioReaction r = this.flxNet.getUnderlyingBionet().getReactionsView()
						.getEntityFromId(fbcFluxObj.getReaction());
				
				if(! StringUtils.isVoid(fbcFluxObj.getId()))
				{
					id = fbcFluxObj.getId();
				}
				else {
					id = fbcFluxObj.getReaction();
				}
				
				if(! StringUtils.isVoid(fbcFluxObj.getName()))
				{
					name = fbcFluxObj.getName();
				}
				else {
					name = id;
				}
				
				ReactionObjective biodataFluxObj = new ReactionObjective(id, name);

				biodataFluxObj.setCoefficient(fbcFluxObj.getCoefficient());

				biodataFluxObj.setFlxReaction(new FluxReaction(r));

				objective.getListOfReactionObjectives().add(biodataFluxObj);
			}
			
			objectives.add(objective);
			
			// TODO : est ce qu'on s'en sert encore ?
			this.flxNet.getListOfObjectives().put(objective.getId(), objective);
			
		}
		
		NetworkAttributes.setObjectives(this.flxNet.getUnderlyingBionet(), objectives);


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