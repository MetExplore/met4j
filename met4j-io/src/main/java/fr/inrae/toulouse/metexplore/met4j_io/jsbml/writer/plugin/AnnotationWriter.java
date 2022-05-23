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

import java.util.HashMap;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.refs.IdentifiersOrg;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.CVTerm.Qualifier;
import org.sbml.jsbml.CVTerm.Type;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;

import static fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

/**
 * Creates MIRIAM annotation for the SBML entities of the model created by
 *
 * @author Benjamin
 * @since 3.0
 * @version $Id: $Id
 */
public class AnnotationWriter implements PackageWriter, AdditionalDataTag {

    /**
     * The SBML model
     */
    public Model model;
    /**
     * the {@link BioNetwork}
     */
    public BioNetwork bionetwork;

    /**
     * The default URL pattern for the annotations
     */
    public static final String DEFAULT_URL_BASE = "https://identifiers.org/";

    /**
     * The user defined URL pattern for the annotations
     */
    public String usedPattern;
    /**
     * The separator between database and identifier in the annotation URL
     */
    public char separator;

    /**
     * Instantiate this Annotation Writer with {@link #DEFAULT_URL_BASE}:
     * <ul>
     * <li>{@value #DEFAULT_URL_BASE}
     * </ul>
     */
    public AnnotationWriter() {
        this.setSeparator('/');
        this.usedPattern = DEFAULT_URL_BASE;
    }

    /**
     * Constructor
     *
     * @param pattern the user defined url pattern
     */
    public AnnotationWriter(String pattern) {
        this.setUsedPattern(pattern);
        this.setSeparator('/');
    }

    /**
     * Constructor
     *
     * @param pattern   the user defined url pattern
     * @param separator the user defined separator
     */
    public AnnotationWriter(String pattern, char separator) {
        this.setUsedPattern(pattern);
        this.setSeparator(separator);
    }

    /** {@inheritDoc} */
    @Override
    public String getAssociatedPackageName() {
        return "annot";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPackageUseableOnLvl(int lvl) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * Parse the different HashMaps present in the BioNetwork and create annotations
     */
    @Override
    public void parseBionetwork(Model model, BioNetwork bionetwork) {

        this.setBionetwork(bionetwork);
        this.setModel(model);

        this.createModelAnnotation();
        try {
            this.createAnnotationFromBioEntities(this.getBionetwork().getCompartmentsView());
            this.createAnnotationFromBioEntities(this.getBionetwork().getMetabolitesView());
            this.createAnnotationFromBioEntities(this.getBionetwork().getReactionsView());
            this.createAnnotationFromBioEntities(this.getBionetwork().getProteinsView());
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the model's annotation from the saved annotation if it exists
     */
    private void createModelAnnotation() {

        System.err.println("Generating Model Annotations...");

        String id = "NA";
        if(! isVoid(this.getBionetwork().getId())) {
            id = this.getBionetwork().getId();
        }

        this.getModel().setMetaId(id);

        HashMap<String, Set<BioRef>> refs = this.getBionetwork().getRefs();

        Annotation annot = this.createAnnotationsFromRefs(refs);

        this.getModel().setAnnotation(annot);

        SbmlAnnotation modelAnnot = NetworkAttributes.getAnnotation(this.getBionetwork());

        if (modelAnnot != null) {

            try {
                this.getModel().setAnnotation(new Annotation(modelAnnot.getXMLasString()));
            } catch (XMLStreamException e) {

                errorsAndWarnings.add("Network annotations mal formatted");
            }
        }

    }

    /**
     * Loop through the list of entities and creates annotations from their set of
     * references and, depending on the type of the entity, from several other
     * attributes
     *
     * @param entityList the list of {@link BioEntity}
     * @throws XMLStreamException
     */
    private void createAnnotationFromBioEntities(BioCollection<? extends BioEntity> entityList)
            throws XMLStreamException {
        for (BioEntity ent : entityList) {

            UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(StringUtils.convertToSID(ent.getId()));

            if (sbase != null) {

                Annotation annot = createAnnotationsFromRefs(ent.getRefs());

                String metaId = model.getSBMLDocument().nextMetaId();
                sbase.setMetaId(metaId);
                annot.setAbout(metaId);

                if (ent instanceof BioMetabolite) {
                    this.addInchiAnnotation((BioMetabolite) ent, annot, metaId);
                    this.getAdditionnalAnnotation((BioMetabolite) ent, annot);
                } else if (ent instanceof BioReaction) {
                    this.getAdditionnalAnnotation((BioReaction) ent, annot);
                }

                if (!annot.isEmpty()) {

                    sbase.setAnnotation(annot);
                }

            }

        }

    }

    /**
     * Creates the appropriate biological annotations from an entity's external
     * references.
     *
     * @param setOfRef a set of extenal references coming from an object of the
     *                 {@link BioNetwork}
     * @return The SBML Annotation object
     */
    private Annotation createAnnotationsFromRefs(HashMap<String, Set<BioRef>> setOfRef) {
        Annotation annot = new Annotation();

        for (Set<BioRef> refs : setOfRef.values()) {
            addingLoop:
            for (BioRef r : refs) {

                if (IdentifiersOrg.validIdentifiers.contains(r.getDbName().toLowerCase()) && ! r.getDbName().equalsIgnoreCase("inchi")){

                    Qualifier qual;

                    try {
                        qual = Qualifier.valueOf("BQB_" + r.logicallink.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Bad property in annotations: "+ r.logicallink+", set to HAS_PROPERTY");
                        qual = Qualifier.BQB_HAS_PROPERTY;
                    }

                    for (CVTerm innerCV : annot.getListOfCVTerms()) {
                        if (innerCV.getBiologicalQualifierType()
                                .compareTo(qual) == 0) {
                            innerCV.addResource(usedPattern + r.getDbName() + separator + r.id);
                            continue addingLoop;
                        }
                    }

                    CVTerm cvTerm = new CVTerm();
                    cvTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);

                    cvTerm.setBiologicalQualifierType(qual);
                    cvTerm.addResource(usedPattern + r.getDbName() + separator + r.id);
                    annot.addCVTerm(cvTerm);
                }
            }
        }

        return annot;
    }

    /**
     * Cf
     * http://sbml.org/Community/Wiki/About_annotations_in_Level_2#How_do_I_put_InChI_strings_in_annotations.3F
     *
     * @param ent
     * @param annot
     * @param metaId
     * @throws XMLStreamException
     */
    private void addInchiAnnotation(BioMetabolite ent, Annotation annot, String metaId) throws XMLStreamException {

        if (ent.getInchi() != null && !ent.getInchi().isEmpty() && !ent.getInchi().equals("NA")) {

            annot.appendNonRDFAnnotation("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  "
                    + "xmlns:bqbiol=\"http://biomodels.net/biology-qualifiers/\"  >" + "<rdf:Description rdf:about=\"#"
                    + metaId + "\"> <in:inchi xmlns:in=\"http://biomodels.net/inchi\">" + "InChI=" + ent.getInchi()
                    + "</in:inchi></rdf:Description>" + "</rdf:RDF>");

        }
    }

    /**
     * Add additional annotation CV Terms from attributes of the input
     * {@link BioPhysicalEntity}
     *
     * @param ent   the entity
     * @param annot The SBMl annotation object
     */
    private void getAdditionnalAnnotation(BioMetabolite ent, Annotation annot) {

        CVTerm cvIsTerm = new CVTerm();
        boolean newCV = false;

        if (annot.filterCVTerms(Qualifier.BQB_IS).isEmpty()) {
            cvIsTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
            cvIsTerm.setBiologicalQualifierType(Qualifier.BQB_IS);
            newCV = true;
        } else {
            cvIsTerm = annot.filterCVTerms(Qualifier.BQB_IS).get(0);
        }

        String pubChemCid = MetaboliteAttributes.getPubchem(ent);

        if (pubChemCid != null && !pubChemCid.isEmpty() && !pubChemCid.equals("NA")) {
            if (annot.filterCVTerms(Qualifier.BQB_IS, MetaboliteAttributes.PUBCHEM).isEmpty()) {
                cvIsTerm.addResource(usedPattern + MetaboliteAttributes.PUBCHEM + separator + pubChemCid);
            }
        }

        if (newCV && cvIsTerm.getNumResources() > 0) {
            annot.addCVTerm(cvIsTerm);
        }

        /**
         * Same method for the "isDecribedBy" term
         */
        CVTerm cvIsDescByTerm = new CVTerm();
        newCV = false;
        if (annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).isEmpty()) {
            cvIsDescByTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
            cvIsDescByTerm.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
            newCV = true;
        } else {
            cvIsDescByTerm = annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).get(0);
        }
        if (MetaboliteAttributes.getPmids(ent) != null) {

            Set<Integer> pmids = MetaboliteAttributes.getPmids(ent);

            for (Integer pmid : pmids) {
                cvIsDescByTerm.addResource(usedPattern + "pubmed" + separator + pmid);
            }
        }
        if (newCV && cvIsDescByTerm.getNumResources() > 0) {
            annot.addCVTerm(cvIsDescByTerm);
        }
    }

    /**
     * Add additional annotation CV Terms from attributes of the input
     * {@link BioReaction}
     *
     * @param rxn   the reaction
     * @param annot The SBMl annotation object
     */
    private void getAdditionnalAnnotation(BioReaction rxn, Annotation annot) {

        CVTerm cvIsTerm = new CVTerm();
        boolean newCV = false;

        if (annot.filterCVTerms(Qualifier.BQB_IS).isEmpty()) {
            cvIsTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
            cvIsTerm.setBiologicalQualifierType(Qualifier.BQB_IS);
            newCV = true;
        } else {
            cvIsTerm = annot.filterCVTerms(Qualifier.BQB_IS).get(0);
        }

        if (rxn.getEcNumber() != null && !rxn.getEcNumber().isEmpty()) {

            if (annot.filterCVTerms(Qualifier.BQB_IS, "ec-code").isEmpty()) {
                cvIsTerm.addResource(usedPattern + "ec-code" + separator + rxn.getEcNumber());
            }
        }
        if (newCV && cvIsTerm.getNumResources() > 0) {
            annot.addCVTerm(cvIsTerm);
        }

        /**
         * Same method for the "isDecribedBy" term
         */
        CVTerm cvIsDescByTerm = new CVTerm();
        newCV = false;
        if (annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).isEmpty()) {
            cvIsDescByTerm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
            cvIsDescByTerm.setBiologicalQualifierType(Qualifier.BQB_IS_DESCRIBED_BY);
            newCV = true;
        } else {
            cvIsDescByTerm = annot.filterCVTerms(Qualifier.BQB_IS_DESCRIBED_BY).get(0);
        }

        if (ReactionAttributes.getPmids(rxn) != null) {

            Set<Integer> pmids = ReactionAttributes.getPmids(rxn);

            for (Integer pmid : pmids) {
                cvIsDescByTerm.addResource(usedPattern + "pubmed" + separator + pmid);
            }
        }
        if (newCV && cvIsDescByTerm.getNumResources() > 0) {
            annot.addCVTerm(cvIsDescByTerm);
        }
    }

    /**
     * <p>Getter for the field <code>model</code>.</p>
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * <p>Setter for the field <code>model</code>.</p>
     *
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * <p>Getter for the field <code>bionetwork</code>.</p>
     *
     * @return the bionetwork
     */
    public BioNetwork getBionetwork() {
        return bionetwork;
    }

    /**
     * <p>Setter for the field <code>bionetwork</code>.</p>
     *
     * @param bionetwork the bionetwork to set
     */
    public void setBionetwork(BioNetwork bionetwork) {
        this.bionetwork = bionetwork;
    }

    /**
     * <p>Getter for the field <code>usedPattern</code>.</p>
     *
     * @return the usedPattern
     */
    public String getUsedPattern() {
        return usedPattern;
    }

    /**
     * <p>Setter for the field <code>usedPattern</code>.</p>
     *
     * @param usedPattern the usedPattern to set
     */
    public void setUsedPattern(String usedPattern) {
        this.usedPattern = usedPattern;
    }

    /**
     * <p>Getter for the field <code>separator</code>.</p>
     *
     * @return the separator
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * <p>Setter for the field <code>separator</code>.</p>
     *
     * @param separator the separator to set
     */
    public void setSeparator(char separator) {
        this.separator = separator;
    }

}
