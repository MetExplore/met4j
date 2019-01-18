package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import java.util.ArrayList;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.ext.fbc.And;
import org.sbml.jsbml.ext.fbc.Association;
import org.sbml.jsbml.ext.fbc.FBCModelPlugin;
import org.sbml.jsbml.ext.fbc.FBCReactionPlugin;
import org.sbml.jsbml.ext.fbc.GeneProduct;
import org.sbml.jsbml.ext.fbc.GeneProductRef;
import org.sbml.jsbml.ext.fbc.Or;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.Flux;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxNetwork;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociations;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.SingleGeneAssociation;
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
public class FBC2Parser extends FBC1Parser implements PackageParser, PrimaryDataTag, ReaderSBML3Compatible {

	/**
	 * The sbml namespace of the FBC version 2 package
	 */
	private String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/fbc/version2";

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

		this.getModelData();

		this.parseParameters();
		this.parseListOfGeneProducts();
		this.parseFluxReactions();

		/**
		 * Same Methods as FBC1 parser.
		 */
		this.parseFluxSpecies();
		this.parseListOfFluxObjective();

	}

	/**
	 * Get FBC data only present in the SBML Model object, ie the fbc:strict
	 * attribute
	 * 
	 * @see FluxNetwork#setFbcStrict(boolean)
	 */
	private void getModelData() {
		this.getFlxNet().setFbcStrict(this.getFbcModel().getStrict());
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
	protected void parseFluxReactions() {

		BioUnitDefinitionCollection udList = NetworkAttributes.getUnitDefinitions(this.flxNet.getUnderlyingBionet());

		for (Reaction rxn : this.getFbcModel().getParent().getListOfReactions()) {

			BioReaction reaction = this.flxNet.getUnderlyingBionet().getReactionsView().getEntityFromId(rxn.getId());

			FBCReactionPlugin rxnPlugin = (FBCReactionPlugin) rxn.getPlugin("fbc");
			FluxReaction flxReaction = new FluxReaction(reaction);

			GeneAssociations GA = new GeneAssociations();

			// System.err.println(rxn.getId());
			if (rxnPlugin.isSetGeneProductAssociation()) {
				GA.setListOfUniqueGA(this.getGA(rxnPlugin.getGeneProductAssociation().getAssociation()));
			}
			// System.err.println("out of recursion");
			flxReaction.setReactionGAs(GA);
			flxReaction.convertGAtoComplexes(flxNet.getUnderlyingBionet());

			if (!this.flxNet.getListOfFluxBounds().containsKey(rxnPlugin.getLowerFluxBound())) {
				Parameter fbcParam = rxnPlugin.getLowerFluxBoundInstance();
				Flux param = new Flux(fbcParam.getId());
				param.setConstant(fbcParam.getConstant());
				param.value = fbcParam.getValue();
				param.unitDefinition = (BioUnitDefinition) (udList.getEntityFromId(fbcParam.getUnits()));
				this.flxNet.addFluxBound(param);
			}

			ReactionAttributes.setLowerBound(reaction,
					this.flxNet.getListOfFluxBounds().get(rxnPlugin.getLowerFluxBound()));

			ReactionAttributes.setLowerBound(reaction,
					this.flxNet.getListOfFluxBounds().get(rxnPlugin.getLowerFluxBound()));

			if (!this.flxNet.getListOfFluxBounds().containsKey(rxnPlugin.getUpperFluxBound())) {
				Parameter fbcParam = rxnPlugin.getUpperFluxBoundInstance();
				Flux param = new Flux(fbcParam.getId());
				param.setConstant(fbcParam.getConstant());
				param.value = fbcParam.getValue();
				param.unitDefinition = (BioUnitDefinition) (udList.getEntityFromId(fbcParam.getUnits()));
				this.flxNet.addFluxBound(param);
			}

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
	 * @return an ArrayList of {@link SingleGeneAssociation}
	 */
	public ArrayList<SingleGeneAssociation> getGA(Association block) {

		ArrayList<SingleGeneAssociation> list = new ArrayList<SingleGeneAssociation>();

		if (block.getClass().getSimpleName().equals("And")) {
			And andBlock = (And) block;

			for (Association andEl : andBlock.getListOfAssociations()) {
				ArrayList<SingleGeneAssociation> tmplist = new ArrayList<SingleGeneAssociation>();

				for (SingleGeneAssociation x : this.getGA(andEl)) {

					if (list.isEmpty()) {
						tmplist.add(x);
					} else {

						for (SingleGeneAssociation y : list) {
							tmplist.add(SingleGeneAssociation.concatToNewGA(x, y));
						}
					}

				}

				list = tmplist;
			}

		} else if (block.getClass().getSimpleName().equals("Or")) {
			Or orBlock = (Or) block;

			for (Association orEl : orBlock.getListOfAssociations()) {
				list.addAll(this.getGA(orEl));
			}

		} else if (block.getClass().getSimpleName().equals("GeneProductRef")) {

			GeneProductRef geneRef = (GeneProductRef) block;
			SingleGeneAssociation GA = new SingleGeneAssociation();

			GA.setId(geneRef.getId());

			BioGene g = this.flxNet.getUnderlyingBionet().getGenesView().getEntityFromId(geneRef.getGeneProduct());
			GA.addGene(g);

			list.add(GA);
		}

		return list;
	}

}