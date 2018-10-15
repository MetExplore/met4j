package parsebionet.io.jsbml.writer.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.ext.fbc.And;
import org.sbml.jsbml.ext.fbc.Association;
import org.sbml.jsbml.ext.fbc.FBCModelPlugin;
import org.sbml.jsbml.ext.fbc.FBCReactionPlugin;
import org.sbml.jsbml.ext.fbc.FBCSpeciesPlugin;
import org.sbml.jsbml.ext.fbc.FluxObjective;
import org.sbml.jsbml.ext.fbc.GeneProduct;
import org.sbml.jsbml.ext.fbc.GeneProductAssociation;
import org.sbml.jsbml.ext.fbc.GeneProductRef;
import org.sbml.jsbml.ext.fbc.Objective;
import org.sbml.jsbml.ext.fbc.Or;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioComplex;
import parsebionet.biodata.BioGene;
import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioProtein;
import parsebionet.biodata.Flux;
import parsebionet.biodata.fbc.FluxNetwork;
import parsebionet.io.jsbml.dataTags.PrimaryDataTag;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML3Compatible;
import parsebionet.utils.StringUtils;

/**
 * This class is used to extend the SBML model by adding the SBML FBC version 2
 * package to it.
 * 
 * @author Benjamin
 * @since 3.0
 */
public class FBCWriter implements PackageWriter, WriterSBML3Compatible, PrimaryDataTag {

	/**
	 * The XML namespace of the FBC version 2 SBML package
	 */
	public static final String PackageNamespace = "http://www.sbml.org/sbml/level3/version1/fbc/version2";

	/**
	 * The FBC model plugin from jsbml
	 */
	public FBCModelPlugin fbcModel;
	/**
	 * The Flux network
	 */
	public FluxNetwork flxNet;

	@Override
	public String getAssociatedPackageName() {
		return "fbc";
	}

	@Override
	public boolean isPackageUseableOnLvl(int lvl) {
		if (lvl >= 3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Activate the fbc2 package on the SBML model object and create all the fbc
	 * entities from the {@link BioNetwork}
	 */
	@Override
	public void parseBionetwork(Model model, BioNetwork bionetwork) {
		System.err.println("Generating Flux Balance Constraints modules...");
		this.setFlxNet(new FluxNetwork(bionetwork));
		this.setFbcModel((FBCModelPlugin) model.getPlugin(PackageNamespace));

		this.getFbcModel().setStrict(true);

		this.createFluxSpecies();
		this.createGeneProductsInBioNet();
		this.createFluxReactionsAndParameters();

	}

	/**
	 * Creates the FBCSpeciesPlugin associated to each metabolite to add the
	 * attributes fbc:charge and fbc:chemicalFormula to the species nodes in the
	 * sbml model
	 */
	private void createFluxSpecies() {
		for (BioPhysicalEntity bioMetab : this.flxNet.getUnderlyingBionet().getPhysicalEntityList().values()) {
			Species specie = this.fbcModel.getParent().getSpecies(StringUtils.convertToSID(bioMetab.getId()));

			if (specie != null && !bioMetab.getChemicalFormula().isEmpty()) {

				FBCSpeciesPlugin speciePlugin = (FBCSpeciesPlugin) specie.getPlugin("fbc");
				speciePlugin.setCharge(Integer.parseInt(bioMetab.getCharge()));

				try {
					speciePlugin.setChemicalFormula(bioMetab.getChemicalFormula());
				} catch (Exception e) {
					FBCWriter.errorsAndWarnings.add("The species " + specie.getId() + " has no formula.");

				}
			}
		}

	}

	/**
	 * Create the list of gene product from the list of genes in the current
	 * bionetwork.
	 */
	private void createGeneProductsInBioNet() {
		for (BioGene bioGene : this.flxNet.getUnderlyingBionet().getGeneList().values()) {
			GeneProduct geneProd = this.getFbcModel().createGeneProduct();

			geneProd.setId(StringUtils.convertToSID(bioGene.getId()));
			geneProd.setName(bioGene.getName());

			if (bioGene.getLabel() == null || bioGene.getLabel().isEmpty()) {
				geneProd.setLabel(bioGene.getName());
			} else {
				geneProd.setLabel(bioGene.getLabel());
			}

			// if(bioGene.getProteinList().size()==1 ){
			// for(BioProtein prot:bioGene.getProteinList().values()){
			// if(!prot.getId().equals(bioGene.getId())){
			// Species protSpecies=this.getOrCreateProteinSpecies(prot);
			//
			// geneProd.setAssociatedSpecies(protSpecies.getId());
			// }
			// }
			// }
		}
	}

	/**
	 * Create a species from a {@link BioProtein}
	 * 
	 * @param prot
	 *            the protein
	 * @return A SBML Species
	 * @deprecated {@link BioProtein} are now used trough
	 *             {@link #createGeneAssociation(BioPhysicalEntity)} method
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Species getOrCreateProteinSpecies(BioProtein prot) {
		Species protspecies = this.getFbcModel().getParent().getSpecies(StringUtils.convertToSID(prot.getId()));

		if (protspecies == null) {
			protspecies = this.getFbcModel().getParent().createSpecies(prot.getId());
			protspecies.setCompartment(prot.getCompartment().getId());

			protspecies.setName(prot.getName());
			protspecies.setBoundaryCondition(prot.getBoundaryCondition());

			protspecies.setConstant(prot.getConstant());

			if (!StringUtils.isVoid(prot.getSboterm())) {
				protspecies.setSBOTerm(prot.getSboterm());
			} else {
				protspecies.setSBOTerm("SBO:0000252");
			}

			protspecies.setHasOnlySubstanceUnits(prot.getHasOnlySubstanceUnit());

			if (prot.getInitialQuantity().size() == 1) {
				for (Entry<String, Double> quantity : prot.getInitialQuantity().entrySet()) {
					if (quantity.getKey().equals("amount")) {
						protspecies.setInitialAmount(quantity.getValue());
					} else if (quantity.getKey().equals("concentration")) {
						protspecies.setInitialConcentration(quantity.getValue());
					}
				}
			}
		}
		return protspecies;
	}

	/**
	 * Creates the FBCReactionPlugin associated to each reaction to add the fbc
	 * attributes
	 */
	private void createFluxReactionsAndParameters() {

		HashMap<String, Double> rxnInObjective = new HashMap<String, Double>();

		for (BioChemicalReaction bioRxn : this.flxNet.getUnderlyingBionet().getBiochemicalReactionList().values()) {

			Reaction rxn = this.getFbcModel().getParent().getReaction(StringUtils.convertToSID(bioRxn.getId()));
			FBCReactionPlugin rxnPlugin = (FBCReactionPlugin) rxn.getPlugin("fbc");

			/**
			 * updating SpeciesRef to fit fbc package strict attribute
			 */
			if (this.getFbcModel().isStrict()) {
				for (SpeciesReference s : rxn.getListOfReactants()) {
					s.setConstant(true);
				}
				for (SpeciesReference s : rxn.getListOfProducts()) {
					s.setConstant(true);
				}
			}

			/**
			 * Updating fluxes bounds to fit fbc package
			 */
			if (rxn.isSetKineticLaw()) {
				rxn.unsetKineticLaw();
			}

			Parameter up = this.getFbcModel().getParent().getParameter(StringUtils
					.convertToSID(("UPPER_BOUND_" + bioRxn.getUpperBound().value).replaceAll("[\\+\\-]", "")));
			if (up == null) {
				up = this.getFbcModel().getParent().createParameter(StringUtils
						.convertToSID(("UPPER_BOUND_" + bioRxn.getUpperBound().value).replaceAll("[\\+\\-]", "")));
				up.setValue(Double.parseDouble(bioRxn.getUpperBound().value));
				up.setConstant(true);
				up.setUnits(StringUtils.convertToSID(bioRxn.getUpperBound().unitDefinition.getId()));
			}
			rxnPlugin.setUpperFluxBound(up);

			Parameter down = this.getFbcModel().getParent().getParameter(StringUtils
					.convertToSID(("LOWER_BOUND_" + bioRxn.getLowerBound().value).replaceAll("[\\+\\-]", "")));
			if (down == null) {
				down = this.getFbcModel().getParent().createParameter(StringUtils
						.convertToSID(("LOWER_BOUND_" + bioRxn.getLowerBound().value).replaceAll("[\\+\\-]", "")));
				down.setValue(Double.parseDouble(bioRxn.getLowerBound().value));
				down.setConstant(true);
				down.setUnits(StringUtils.convertToSID(bioRxn.getUpperBound().unitDefinition.getId()));
			}
			rxnPlugin.setLowerFluxBound(down);

			for (Entry<String, Flux> param : bioRxn.getListOfAdditionalFluxParam().entrySet()) {
				if (param.getKey().equals("OBJECTIVE_COEFFICIENT") && Double.valueOf(param.getValue().value) != 0) {
					rxnInObjective.put(bioRxn.getId(), Double.valueOf(param.getValue().value));
				}
			}

			/**
			 * update modifiers to geneProduct references
			 */
			if (!bioRxn.getListOfGenes().isEmpty()) {
				GeneProductAssociation GPA = rxnPlugin.createGeneProductAssociation();

				if (bioRxn.getEnzList().size() == 1) {
					for (BioPhysicalEntity enz : bioRxn.getEnzList().values()) {
						Association a = createGeneAssociation(enz);
						if (a != null) {
							GPA.setAssociation(a);
						}
					}
				} else {
					Or orAssoc = new Or();
					for (BioPhysicalEntity enz : bioRxn.getEnzList().values()) {
						Association a = createGeneAssociation(enz);
						if (a != null) {
							orAssoc.addAssociation(a);
						}
					}
					GPA.setAssociation(orAssoc);
				}

			}
		}

		if (!rxnInObjective.isEmpty())
			this.setObjectives(rxnInObjective);
	}

	/**
	 * Create and set the main Objective of the model
	 * 
	 * @param rxnInObjective
	 *            the list of reaction ID present in the Flux Objectives along
	 *            with their coefficient
	 */
	private void setObjectives(HashMap<String, Double> rxnInObjective) {

		Objective obj = this.getFbcModel().createObjective("obj");
		this.getFbcModel().setActiveObjective("obj");
		obj.setType("maximize");

		for (Entry<String, Double> rxnInObj : rxnInObjective.entrySet()) {
			FluxObjective sObj = obj.createFluxObjective();
			sObj.setReaction(StringUtils.convertToSID(rxnInObj.getKey()));
			sObj.setCoefficient(rxnInObj.getValue());
		}
	}

	/**
	 * Recursively loop on Enzyme's constituent and create the corresponding
	 * Gene Association
	 * 
	 * @param enz
	 *            the BioPhysicalEntity
	 * @return an SBML association (an AND/ OR association)
	 */
	private Association createGeneAssociation(BioPhysicalEntity enz) {

		Association assoc;
		switch (enz.getClass().getSimpleName()) {
		case "BioProtein":
			List<Association> assoslist = new ArrayList<Association>();

			BioGene gene = ((BioProtein) enz).getGene();

			if (gene != null) {
				GeneProductRef geneRef = new GeneProductRef();
				geneRef.setGeneProduct(StringUtils.convertToSID(gene.getId()));
				assoslist.add(geneRef);
			}

			if (assoslist.size() > 1) {
				assoc = new Or();
				((Or) assoc).addAllAssociations(assoslist);
			} else if (assoslist.size() == 1) {
				assoc = assoslist.get(0);
			} else {
				PackageWriter.errorsAndWarnings.add("The protein " + enz.getId()
						+ " is not linked to any gene. It will not be added as a fbc:geneProduct instance in the model.");
				assoc = null;
			}

			break;
		case "BioComplex":

			if (((BioComplex) enz).getAllComponentList().size() == 1) {
				assoc = null;
				for (BioPhysicalEntity part : ((BioComplex) enz).getAllComponentList().values()) {
					assoc = createGeneAssociation(part);
				}

			} else if (((BioComplex) enz).getAllComponentList().size() > 1) {
				assoc = new And();
				for (BioPhysicalEntity part : ((BioComplex) enz).getAllComponentList().values()) {
					((And) assoc).addAssociation(createGeneAssociation(part));
				}
			} else {
				assoc = null;
			}

			break;
		default:
			assoc = null;
			break;
		}

		return assoc;

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

}
