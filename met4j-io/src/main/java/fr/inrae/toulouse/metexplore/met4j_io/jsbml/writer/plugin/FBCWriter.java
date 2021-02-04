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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin;

import java.util.ArrayList;
import java.util.List;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import org.sbml.jsbml.*;
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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzymeParticipant;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjective;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjectiveCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.FluxNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.ReactionObjective;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;

/**
 * This class is used to extend the SBML model by adding the SBML FBC version 2
 * package to it.
 *
 * @author Benjamin
 * @since 3.0
 */
public class FBCWriter implements PackageWriter, PrimaryDataTag {

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
        this.createObjectiveFunctions();

    }

    /**
     * Creates the FBCSpeciesPlugin associated to each metabolite to add the
     * attributes fbc:charge and fbc:chemicalFormula to the species nodes in the
     * sbml model
     */
    private void createFluxSpecies() {
        for (BioMetabolite bioMetab : this.flxNet.getUnderlyingBionet().getMetabolitesView()) {
            Species specie = this.fbcModel.getParent().getSpecies(StringUtils.convertToSID(bioMetab.getId()));

            if (specie != null) {

                FBCSpeciesPlugin speciePlugin = (FBCSpeciesPlugin) specie.getPlugin("fbc");
                if (bioMetab.getCharge() != null) {
                    try {
                        speciePlugin.setCharge(bioMetab.getCharge());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Charge not in good format (" + bioMetab.getCharge() + ") for " + bioMetab.getId());
                    }

                }

                if (!fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils
                        .isVoid(bioMetab.getChemicalFormula()))
                    try {
                        speciePlugin.setChemicalFormula(bioMetab.getChemicalFormula());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Chemical formula not in good format (" + bioMetab.getChemicalFormula() + ") for " + bioMetab.getId());
                    }
            }
        }

    }

    /**
     * Create the list of gene product from the list of genes in the current
     * bionetwork.
     */
    private void createGeneProductsInBioNet() {
        for (BioGene bioGene : this.flxNet.getUnderlyingBionet().getGenesView()) {
            GeneProduct geneProd;

            geneProd = this.getFbcModel().getGeneProduct(StringUtils.convertToSID(bioGene.getId()));

            if (geneProd == null) {
                geneProd = this.getFbcModel().createGeneProduct();
                geneProd.setId(StringUtils.convertToSID(bioGene.getId()));
                geneProd.setName(bioGene.getName());

                geneProd.setLabel(bioGene.getName());
            }
        }
    }

    /**
     * Creates the FBCReactionPlugin associated to each reaction to add the fbc
     * attributes
     */
    private void createFluxReactionsAndParameters() {

        for (BioReaction bioRxn : this.flxNet.getUnderlyingBionet().getReactionsView()) {

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

            Flux ub = ReactionAttributes.getUpperBound(bioRxn);

            if (ub == null) {
                ub = new Flux(Flux.FLUXMAX);
                NetworkAttributes.addUnitDefinition(this.getFlxNet().getUnderlyingBionet(), ub.unitDefinition);
            }


            Parameter up = this.getFbcModel().getParent()
                    .getParameter(StringUtils.convertToSID(("UPPER_BOUND_" + ub.value).replaceAll("[\\+\\-]", "")));
            if (up == null) {
                up = this.getFbcModel().getParent().createParameter(
                        StringUtils.convertToSID(("UPPER_BOUND_" + ub.value).replaceAll("[\\+\\-]", "")));
                up.setValue(ub.value);
                up.setConstant(true);
                up.setUnits(StringUtils.convertToSID(ub.unitDefinition.getId()));
            }
            rxnPlugin.setUpperFluxBound(up);

            Flux lb = ReactionAttributes.getLowerBound(bioRxn);

            if (lb == null) {
                lb = new Flux(bioRxn.isReversible() ? Flux.FLUXMIN : 0.0);
                NetworkAttributes.addUnitDefinition(this.getFlxNet().getUnderlyingBionet(), lb.unitDefinition);
            }

            Parameter down = this.getFbcModel().getParent()
                    .getParameter(StringUtils.convertToSID(("LOWER_BOUND_" + lb.value).replaceAll("[\\+\\-]", "")));

            if (down == null) {
                down = this.getFbcModel().getParent().createParameter(
                        StringUtils.convertToSID(("LOWER_BOUND_" + lb.value).replaceAll("[\\+\\-]", "")));
                down.setValue(lb.value);
                down.setConstant(true);
                down.setUnits(StringUtils.convertToSID(lb.unitDefinition.getId()));
            }
            rxnPlugin.setLowerFluxBound(down);

            /**
             * update modifiers to geneProduct references
             */

            BioCollection<BioGene> genes = this.flxNet.getUnderlyingBionet().getGenesFromReactions(bioRxn);
            BioCollection<BioEnzyme> enzymes = bioRxn.getEnzymesView();

            if (!genes.isEmpty()) {
                GeneProductAssociation GPA = rxnPlugin.createGeneProductAssociation();

                if (enzymes.size() == 1) {
                    for (BioEnzyme enz : enzymes) {
                        Association a = createGeneAssociation(enz);
                        if (a != null) {
                            GPA.setAssociation(a);
                        }
                    }
                } else {
                    Or orAssoc = new Or();
                    for (BioEnzyme enz : enzymes) {
                        Association a = createGeneAssociation(enz);

                        if (a != null) {
                            orAssoc.addAssociation(a);
                        }
                    }
                    GPA.setAssociation(orAssoc);
                }

            }
        }

    }

    /**
     * get Objective functions
     */
    private void createObjectiveFunctions() {

        BioObjectiveCollection objectives = NetworkAttributes.getObjectives(this.flxNet.getUnderlyingBionet());

        if (objectives != null) {
            for (BioObjective objective : objectives) {
                Objective obj = this.getFbcModel().createObjective(objective.getId());
                if (objective.active) {
                    this.getFbcModel().setActiveObjective(objective.getId());
                }

                if (objective.getType() != null) {
                    obj.setType(objective.getType().toString());
                }

                for (ReactionObjective rObj : objective.getListOfReactionObjectives()) {
                    FluxObjective sObj = obj.createFluxObjective();
                    sObj.setReaction(StringUtils.convertToSID(rObj.getFlxReaction().getUnderlyingReaction().getId()));
                    sObj.setCoefficient(rObj.getCoefficient());
                }
            }
        }
    }

    /**
     * Recursively loop on Enzyme's constituent and create the corresponding Gene
     * Association
     *
     * @param enz the BioPhysicalEntity
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
                    assoc = assoslist.get(0);
                } else {
                    errorsAndWarnings.add("The protein " + enz.getId()
                            + " is not linked to any gene. It will not be added as a fbc:geneProduct instance in the model.");
                    assoc = null;
                }

                break;
            case "BioEnzyme":

                if (((BioEnzyme) enz).getParticipantsView().size() == 1) {
                    assoc = null;
                    for (BioEnzymeParticipant part : ((BioEnzyme) enz).getParticipantsView()) {
                        assoc = createGeneAssociation(part.getPhysicalEntity());
                    }

                } else if (((BioEnzyme) enz).getParticipantsView().size() > 1) {
                    assoc = new And();
                    for (BioEnzymeParticipant part : ((BioEnzyme) enz).getParticipantsView()) {
                        ((And) assoc).addAssociation(createGeneAssociation(part.getPhysicalEntity()));
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
     * @param fbcModel the fbcModel to set
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
     * @param flxNet the flxNet to set
     */
    public void setFlxNet(FluxNetwork flxNet) {
        this.flxNet = flxNet;
    }

}
