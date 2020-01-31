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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer;

import java.util.ArrayList;
import java.util.List;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.FBCWriter;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.ASTNode.Type;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioParticipant;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import static fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.FluxCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageWriterException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.UnitSbml;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.PackageWriter;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;

import org.sbml.jsbml.Unit.Kind;

/**
 * Abstract class that defines all the methods used to parse the bioNetwork
 * regardless of the requested output SBML level
 *
 * @author Benjamin
 * @since 3.0
 */
public class BionetworkToJsbml {

    /**
     * The output SBML level
     */
    private final int level;
    /**
     * The output SBML level version
     */
    private final int vs;

    /**
     * the SBML {@link Model}
     */
    public Model model;

    /**
     * The SBML document that will contain the {@link #model} object
     */
    public SBMLDocument doc;

    /**
     * The ordered list of {@link PackageWriter} activated for this parser
     */
    public List<PackageWriter> setOfPackage = new ArrayList<PackageWriter>();

    public BionetworkToJsbml(SBMLDocument doc) {
        this.doc = doc;
        this.level = doc.getLevel();
        this.vs = doc.getVersion();
    }

    public BionetworkToJsbml(int level, int vs) {
        this.vs = vs;
        this.level = level;
        this.doc = new SBMLDocument(level, vs);
    }

    public BionetworkToJsbml() {
        this.level = 3;
        this.vs = 2;
        this.doc = new SBMLDocument(this.level, this.vs);
    }

    /**
     * Launches the parsing of the BioNetwork and create all the SBML components
     * from it
     *
     * @param net the input {@link BioNetwork}
     * @return The completed SBML Model
     */
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
     * Abstract method to create the top {@link Model} object that will contain all
     * the other SBMl components
     *
     * @param net the input {@link BioNetwork}
     * @return a simple model created from the attribute of the Bionetwork
     */
    protected Model createModel(BioNetwork net) {
        this.getDoc().setLevelAndVersion(level, vs);
        Model model = this.getDoc().createModel();

        model.setId(StringUtils.convertToSID(net.getId()));
        model.setName(net.getName());

        return model;
    }

    /**
     * Default way of creating SBML Unit definitions. This method should be
     * overridden to change its behavior.
     *
     * @param net The bionetwork
     */
    protected void createUnits(BioNetwork net) {

        BioUnitDefinitionCollection unitDefinitions = NetworkAttributes.getUnitDefinitions(net);

        if (unitDefinitions != null) {

            for (BioUnitDefinition bioUD : unitDefinitions) {
                UnitDefinition ud = model.createUnitDefinition();

                ud.setId(StringUtils.convertToSID(bioUD.getId()));
                ud.setName(bioUD.getName());

                for (UnitSbml bioUnits : bioUD.getUnits().values()) {
                    Unit libSBMLUnit = ud.createUnit();
                    libSBMLUnit.setExponent(bioUnits.getExponent());
                    libSBMLUnit.setMultiplier(bioUnits.getMultiplier());
                    libSBMLUnit.setScale(bioUnits.getScale());

                    libSBMLUnit.setKind(Kind.valueOf(bioUnits.getKind().toUpperCase()));
                }
            }
        }
    }

    /**
     * @return true if the parse contains the FBC package
     */
    private boolean containsFbcPackage() {

        for (PackageWriter p : this.setOfPackage) {
            if (p instanceof FBCWriter) {
                return true;
            }
        }

        return false;

    }

    /**
     * Abstract method to create SBML Comparments in {@link #model} from the
     * {@link BioNetwork}
     *
     * @param net The Bionetwork
     */
    protected void createCompartments(BioNetwork net) {
        for (BioCompartment compart : net.getCompartmentsView()) {
            Compartment sbmlCompart = model.createCompartment();

            sbmlCompart.setName(compart.getName());
            sbmlCompart.setId(StringUtils.convertToSID(compart.getId()));

            Boolean constant = CompartmentAttributes.getConstant(compart);
            if (constant != null) {
                sbmlCompart.setConstant(CompartmentAttributes.getConstant(compart));
            }

            if (!isVoid(CompartmentAttributes.getSboTerm(compart))) {
                sbmlCompart.setSBOTerm(CompartmentAttributes.getSboTerm(compart));
            }

            Integer sd = CompartmentAttributes.getSpatialDimensions(compart);
            if (sd != null) {
                sbmlCompart.setSpatialDimensions(CompartmentAttributes.getSpatialDimensions(compart));
            }

            Double size = CompartmentAttributes.getSize(compart);
            if (size != null) {
                CompartmentAttributes.setSize(compart, sbmlCompart.getSize());
            }

            if (CompartmentAttributes.getOutsideCompartment(compart) != null && model.getLevel() < 3) {
                sbmlCompart.setOutside(
                        StringUtils.convertToSID(CompartmentAttributes.getOutsideCompartment(compart).getId()));
            }
            if (CompartmentAttributes.getType(compart) != null
                    && (model.getLevel() == 2 && model.getVersion() >= 2 && model.getVersion() <= 4)) {
                sbmlCompart.setCompartmentType(CompartmentAttributes.getType(compart).getId());
            }
        }
    }

    /**
     * Common Method to create SBML Species in {@link #model} from the
     * {@link BioNetwork}
     *
     * @param net The Bionetwork
     */
    protected void createSpecies(BioNetwork net) {

        for (BioMetabolite bioMetab : net.getMetabolitesView()) {

            BioCollection<BioCompartment> cpts = net.getCompartmentsOf(bioMetab);

            // TODO : to check : if the metabolite has several compartments, it will fail
            for (BioCompartment cpt : cpts) {

                Compartment comp = model.getCompartment(StringUtils.convertToSID(cpt.getId()));

                Species metab = model.createSpecies(StringUtils.convertToSID(bioMetab.getId()), comp);

                metab.setName(bioMetab.getName());
                metab.setBoundaryCondition(MetaboliteAttributes.getBoundaryCondition(bioMetab));

                Boolean constant = MetaboliteAttributes.getConstant(bioMetab);
                if (constant != null) {
                    metab.setConstant(MetaboliteAttributes.getConstant(bioMetab));
                }

                if (!isVoid(MetaboliteAttributes.getSboTerm(bioMetab))) {
                    metab.setSBOTerm(MetaboliteAttributes.getSboTerm(bioMetab));
                } else {
                    metab.setSBOTerm("SBO:0000299");
                }

                // TODO Ne semble pas exister du côté reader...
                Boolean hasOnlySubstanceUnits = MetaboliteAttributes.getHasOnlySubstanceUnits(bioMetab);
                if (hasOnlySubstanceUnits != null) {
                    metab.setHasOnlySubstanceUnits(MetaboliteAttributes.getHasOnlySubstanceUnits(bioMetab));
                }

                if (MetaboliteAttributes.getInitialAmount(bioMetab) != null) {
                    metab.setInitialAmount(MetaboliteAttributes.getInitialAmount(bioMetab));
                } else if (MetaboliteAttributes.getInitialConcentration(bioMetab) != null) {
                    metab.setInitialConcentration(MetaboliteAttributes.getInitialConcentration(bioMetab));
                }
            }
        }

    }

    /**
     * Abstract method to create SBML Reactions in {@link #model} from the
     * {@link BioNetwork}
     *
     * @param net The Bionetwork
     */
    protected void createReactions(BioNetwork net) {
        for (BioReaction bionetReaction : net.getReactionsView()) {
            Reaction reaction = model.createReaction();

            reaction.setId(StringUtils.convertToSID(bionetReaction.getId()));
            reaction.setName(bionetReaction.getName());

            if (model.getLevel() < 3 || (model.getLevel() == 3 && model.getVersion() == 1)) {
                Boolean fast = ReactionAttributes.getFast(bionetReaction);

                if (fast != null) {
                    reaction.setFast(ReactionAttributes.getFast(bionetReaction));
                }

            }

            if (!isVoid(ReactionAttributes.getSboTerm(bionetReaction))) {
                reaction.setSBOTerm(ReactionAttributes.getSboTerm(bionetReaction));
            }

            if (bionetReaction.isReversible()) {
                reaction.setReversible(true);
            } else {
                reaction.setReversible(false);
            }
            // Set the substrates of the reaction
            for (BioParticipant BionetLParticipant : net.getLeftReactants(bionetReaction)) {
                SpeciesReference specieRef = reaction.createReactant();
                specieRef.setSpecies(StringUtils.convertToSID(BionetLParticipant.getPhysicalEntity().getId()));
                specieRef.setStoichiometry(BionetLParticipant.getQuantity());

            }
            // set the products of the reaction
            for (BioParticipant BionetRParticipant : net.getRightReactants(bionetReaction)) {
                SpeciesReference specieRef = reaction.createProduct();
                specieRef.setSpecies(StringUtils.convertToSID(BionetRParticipant.getPhysicalEntity().getId()));
                specieRef.setStoichiometry(BionetRParticipant.getQuantity());

            }

            KineticLaw law = reaction.createKineticLaw();

            ASTNode ciNode = new ASTNode(Type.NAME);
            ciNode.setName("FLUX_VALUE");
            law.setMath(ciNode);

            if (ReactionAttributes.getLowerBound(bionetReaction) != null
                    && ReactionAttributes.getUpperBound(bionetReaction) != null) {

                if (!this.containsFbcPackage()) {
                    if (level < 3) {
                        Parameter LBound = new Parameter();
                        LBound.setId("LOWER_BOUND");
                        LBound.setValue(ReactionAttributes.getLowerBound(bionetReaction).value);
                        LBound.setUnits(StringUtils
                                .convertToSID(ReactionAttributes.getLowerBound(bionetReaction).unitDefinition.getId()));

                        law.addParameter(LBound);

                        Parameter UBound = new Parameter();
                        UBound.setId("UPPER_BOUND");
                        UBound.setValue(ReactionAttributes.getUpperBound(bionetReaction).value);
                        UBound.setUnits(StringUtils
                                .convertToSID(ReactionAttributes.getLowerBound(bionetReaction).unitDefinition.getId()));

                        law.addParameter(UBound);
                    } else {
                        LocalParameter LBound = law.createLocalParameter();
                        LBound.setId("LOWER_BOUND");
                        LBound.setValue(ReactionAttributes.getLowerBound(bionetReaction).value);
                        LBound.setUnits(StringUtils
                                .convertToSID(ReactionAttributes.getLowerBound(bionetReaction).unitDefinition.getId()));

                        LocalParameter UBound = law.createLocalParameter();
                        UBound.setId("UPPER_BOUND");
                        UBound.setValue(ReactionAttributes.getUpperBound(bionetReaction).value);
                        UBound.setUnits(StringUtils
                                .convertToSID(ReactionAttributes.getUpperBound(bionetReaction).unitDefinition.getId()));
                    }

                }

                FluxCollection additionalFluxParams = ReactionAttributes.getAdditionalFluxParams(bionetReaction);
                if (additionalFluxParams != null) {

                    for (Flux flux : additionalFluxParams) {

                        if (level < 3) {
                            Parameter param = new Parameter();
                            param.setId(flux.getId());
                            param.setValue(flux.value);
                            param.setUnits(StringUtils.convertToSID(flux.unitDefinition.getId()));

                            law.addParameter(param);
                        } else {
                            LocalParameter param = law.createLocalParameter();
                            param.setId(flux.getId());
                            param.setValue(flux.value);
                            param.setUnits(StringUtils.convertToSID(flux.unitDefinition.getId()));

                        }
                    }
                }
            }
        }
    }

    /**
     * Launches the set writer Package on the Input {@link BioNetwork}
     *
     * @param net The Bionetwork
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
     * @param model the model to set
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
     * @param pkg the package to add
     * @throws JSBMLPackageWriterException when the added package is incompatible
     *                                     with the current SBML level
     */
    public void addPackage(PackageWriter pkg) throws JSBMLPackageWriterException {
        this.getSetOfPackage().add(pkg);
    }

    /**
     * Set the {@link #setOfPackage} to a new list
     *
     * @param packages the ordered list of packages to set
     * @throws JSBMLPackageWriterException if one of the package in the list is not
     *                                     compatible with the current SBML level
     */
    public void setPackages(ArrayList<PackageWriter> packages) throws JSBMLPackageWriterException {
        for (PackageWriter pkg : packages) {
            this.addPackage(pkg);
        }
    }

    public SBMLDocument getDoc() {
        return doc;
    }

    public void setDoc(SBMLDocument doc) {
        this.doc = doc;
    }

}
