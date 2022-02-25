/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.GeneSetException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.*;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
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

import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;

import java.util.ArrayList;

/**
 * This class is used to parse SBML level 3 FBC version 2 package.
 *
 * @author lcottret from bmerlet
 * @since 3.0
 * @version $Id: $Id
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

	/** {@inheritDoc} */
	@Override
	public String getAssociatedPackageName() {
		return "fbc";
	}

	/** {@inheritDoc} */
	@Override
	public boolean isPackageUseableOnModel(Model model) {
		return model.isPackageURIEnabled(PackageNamespace);
	}

	/**
	 * {@inheritDoc}
	 *
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
	public void parseModel(Model model, BioNetwork bionetwork) throws Met4jSbmlReaderException {

		this.setFlxNet(new FluxNetwork(bionetwork));
		this.setFbcModel((FBCModelPlugin) model.getPlugin("fbc"));
		System.err.println("Starting " + this.getAssociatedPackageName() + " version "
				+ this.getFbcModel().getPackageVersion() + " plugin...");

		this.setStrictFromFbcModel();

		this.parseParameters();
		this.parseListOfGeneProducts();
		try {
			this.parseFluxReactions();
		} catch (Exception e) {
			throw new Met4jSbmlReaderException(e.getMessage());
		}

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

		if(this.getFbcModel().getParent().getListOfParameters().size() > 0) {
			BioUnitDefinitionCollection udList = NetworkAttributes.getUnitDefinitions(this.flxNet.getUnderlyingBionet());
			if(udList == null) {
				System.err.println("[Warning] No unit definition in the SBML file, default one selected");
				udList = new BioUnitDefinitionCollection();
				this.flxNet.getUnderlyingBionet().setAttribute(NetworkAttributes.UNIT_DEFINITIONS, udList);
			}

			for (Parameter gParam : this.getFbcModel().getParent().getListOfParameters()) {
				Flux bioParam = new Flux(gParam.getId());
				bioParam.setConstant(gParam.getConstant());
				bioParam.value = gParam.getValue();

				String unit = gParam.getUnits();

				if (unit != "") {
					BioUnitDefinition unitDefinition = udList.get(gParam.getUnits());
					if(unitDefinition == null) {
						System.err.println("[Warning] Unit definition "+gParam.getUnits()+" not defined in the SBML : we add it");
						unitDefinition = new BioUnitDefinition(gParam.getUnits(), gParam.getUnits());
						udList.add(unitDefinition);
					}
					bioParam.unitDefinition = unitDefinition;
				}

				this.flxNet.addFluxBound(bioParam);
			}
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

			gene.setName(geneProd.getLabel());

			this.getFlxNet().getUnderlyingBionet().add(gene);

			BioProtein protein = new BioProtein(geneId, geneName);

			this.getFlxNet().getUnderlyingBionet().add(protein);

			this.getFlxNet().getUnderlyingBionet().affectGeneProduct(protein, gene);


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
	private void parseFluxReactions() throws Exception {

		for (Reaction rxn : this.getFbcModel().getParent().getListOfReactions()) {

			BioReaction reaction = this.flxNet.getUnderlyingBionet().getReaction(rxn.getId());

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
	private GeneAssociation computeGeneAssocations(Association block) throws GeneSetException, Met4jSbmlReaderException {

		GeneAssociation geneAssociation = new GeneAssociation();

		if (block != null) {

			if (block.getClass().getSimpleName().equals("And")) {
				And andBlock = (And) block;

				ArrayList<GeneAssociation> geneAssociations = new ArrayList<GeneAssociation>();

				for (Association andEl : andBlock.getListOfAssociations()) {

					geneAssociations.add(this.computeGeneAssocations(andEl));

//					for (GeneSet x : this.computeGeneAssocations(andEl)) {
//
//						if (geneAssociation.isEmpty()) {
//							geneAssociation.add(x);
//						} else {
//
//							for (GeneSet y : geneAssociation) {
//								y.addAll(x);
//							}
//						}
//
//					}

				}

				// Cross the geneAssociations
				geneAssociation = GeneAssociations.merge(geneAssociations.stream().toArray(GeneAssociation[]::new));

			} else if (block.getClass().getSimpleName().equals("Or")) {
				Or orBlock = (Or) block;

				for (Association orEl : orBlock.getListOfAssociations()) {
					geneAssociation.addAll(this.computeGeneAssocations(orEl));
				}

			} else  {
				
				// The association is composed of a GeneProductRef.

				GeneProductRef geneRef = (GeneProductRef) block;
				GeneSet geneSet = new GeneSet();

				geneSet.setId(geneRef.getId());

				BioGene g = this.flxNet.getUnderlyingBionet().getGene(geneRef.getGeneProduct());

				if(g== null) {
					throw new Met4jSbmlReaderException("Gene "+geneRef.getGeneProduct() + " not present in the list of genes");
				}

				geneSet.add(g.getId());

				geneAssociation.add(geneSet);
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

			BioMetabolite metabolite = net.getMetabolite(specie.getId());

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

			String type = "maximize";

			if(fbcObj.getType() != null)
			{
				type = fbcObj.getType().toString();
			}

			objective.setType(fbcObj.getType().toString());
			
			objective.active = this.getFbcModel().getActiveObjective().equals(fbcObj.getId()) ? true : false;

			for (FluxObjective fbcFluxObj : fbcObj.getListOfFluxObjectives()) {
				
				String id, name;
				
				BioReaction r = this.flxNet.getUnderlyingBionet().getReaction(fbcFluxObj.getReaction());

				if(r != null) {

					if (!StringUtils.isVoid(fbcFluxObj.getId())) {
						id = fbcFluxObj.getId();
					} else {
						id = fbcFluxObj.getReaction();
					}

					if (!StringUtils.isVoid(fbcFluxObj.getName())) {
						name = fbcFluxObj.getName();
					} else {
						name = id;
					}

					ReactionObjective biodataFluxObj = new ReactionObjective(id, name);

					biodataFluxObj.setCoefficient(fbcFluxObj.getCoefficient());

					biodataFluxObj.setFlxReaction(new FluxReaction(r));

					objective.getListOfReactionObjectives().add(biodataFluxObj);
				}
			}
			
			objectives.add(objective);
			
			// TODO : est ce qu'on s'en sert encore ?
			this.flxNet.getListOfObjectives().put(objective.getId(), objective);
			
		}
		
		NetworkAttributes.setObjectives(this.flxNet.getUnderlyingBionet(), objectives);


		this.flxNet.setActiveObjective(this.getFbcModel().getActiveObjective());

	}

	/**
	 * <p>Getter for the field <code>flxNet</code>.</p>
	 *
	 * @return the flxNet
	 */
	public FluxNetwork getFlxNet() {
		return flxNet;
	}

	/**
	 * <p>Setter for the field <code>flxNet</code>.</p>
	 *
	 * @param flxNet
	 *            the flxNet to set
	 */
	public void setFlxNet(FluxNetwork flxNet) {
		this.flxNet = flxNet;
	}

	/**
	 * <p>Getter for the field <code>fbcModel</code>.</p>
	 *
	 * @return the fbcModel
	 */
	public FBCModelPlugin getFbcModel() {
		return fbcModel;
	}

	/**
	 * <p>Setter for the field <code>fbcModel</code>.</p>
	 *
	 * @param fbcModel
	 *            the fbcModel to set
	 */
	public void setFbcModel(FBCModelPlugin fbcModel) {
		this.fbcModel = fbcModel;
	}

}
