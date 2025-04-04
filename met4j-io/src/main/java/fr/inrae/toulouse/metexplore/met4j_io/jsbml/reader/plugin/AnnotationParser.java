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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML1Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

/**
 * This class is used to parse the MIRIAM annotation of every SBML element.
 *
 * @author L.Cottret from B. Merlet
 */
public class AnnotationParser implements PackageParser, AdditionalDataTag, ReaderSBML1Compatible, ReaderSBML2Compatible,
        ReaderSBML3Compatible {

    /**
     * Constant <code>ORIGIN="SBML"</code>
     */
    public static final String ORIGIN = "SBML";

    /**
     * The Jsbml Model
     */
    public Model model;
    /**
     * The BioNetwork
     */
    public BioNetwork bionetwork;

    /**
     * The user defined annotation pattern used in the regular expression of
     * this class
     */
    public String annotationPattern;

    /**
     * The default annotation pattern:
     * <ul>
     * <li>http://identifiers.org/([^/]+)[/:](.*)
     * </ul>
     * The first parenthesis group is
     */
    public static final String defaultAnnotationPattern = "https?://identifiers.org/([^/]+)[/:](.*)";

    /**
     * Constructor
     *
     * @param useDefault true to use the {@link #defaultAnnotationPattern}
     */
    public AnnotationParser(boolean useDefault) {
        if (useDefault)
            this.setAnnotationPattern(defaultAnnotationPattern);
    }

    /**
     * Constructor
     *
     * @param pattern the user defined pattern
     */
    public AnnotationParser(String pattern) {
        this.annotationPattern = pattern;
    }

    /**
     * <p>getAssociatedPackageName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAssociatedPackageName() {
        return "annot";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPackageUseableOnModel(Model model) {
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Parse all model entities to retrieve their annotations and extract the
     * desired information
     */
    public void parseModel(Model model, BioNetwork bionetwork) {
        System.err.println("Starting " + this.getAssociatedPackageName() + " plugin...");

        this.setBionetwork(bionetwork);
        this.setModel(model);

        this.parseAnnotation(bionetwork, model.getAnnotation());

        this.parseSbmlAnnotations(bionetwork.getReactionsView());
        this.parseSbmlAnnotations(bionetwork.getMetabolitesView());
        this.parseSbmlAnnotations(bionetwork.getGenesView());
        this.parseSbmlAnnotations(bionetwork.getCompartmentsView());
    }

    /**
     * One of the different lists present in the {@link BioNetwork}
     * class
     */
    private void parseSbmlAnnotations(BioCollection<?> collection) {

        for (BioEntity entry : collection) {

            String id = entry.getId();

            if (entry.getAttribute("oldId") != null) {
                id = (String) entry.getAttribute("oldId");
            }

            UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(id);

            if (sbase != null && !sbase.getAnnotation().isEmpty() && sbase.hasValidAnnotation()) {

                this.parseAnnotation(entry, sbase.getAnnotation());

            }
        }
    }

    /**
     * Parse entity's annotation to extract external identifiers
     *
     * @param annot the SBML annotation element
     */
    private void parseAnnotation(BioEntity ent, Annotation annot) {

        Matcher m;
        for (CVTerm cv : annot.getListOfCVTerms()) {

            String qual;

            if (cv.isBiologicalQualifier()) {

                qual = cv.getBiologicalQualifierType().getElementNameEquivalent();
            } else {
                qual = cv.getModelQualifierType().getElementNameEquivalent();
            }

            for (String ress : cv.getResources()) {
                if (this.getAnnotationPattern() != null
                        && (m = Pattern.compile(this.getAnnotationPattern()).matcher(ress)).matches()) {

                    if (m.group(1).equalsIgnoreCase("ec-code") && ent instanceof BioReaction) {
                        String oldEc = ((BioReaction) ent).getEcNumber();
                        if (isVoid(oldEc)) {
                            ((BioReaction) ent).setEcNumber(m.group(2));
                        } else {
                            ((BioReaction) ent).setEcNumber(oldEc + ";" + m.group(2));
                        }
                    }
                    ent.addRef(m.group(1), m.group(2), 1, qual, ORIGIN);
                }
            }
        }

        String nonrdfAnnot = annot.getNonRDFannotationAsString();
        if (ent instanceof BioMetabolite && nonrdfAnnot != null && !nonrdfAnnot.isEmpty()) {

            String specialInchiPattern = "(?i)InChI=([^<]+)";

            m = Pattern.compile(specialInchiPattern, Pattern.DOTALL).matcher(nonrdfAnnot);

            while (m.find()) {
                String inchi = m.group(1);
                if (!ent.hasRef("inchi", inchi)) {
                    ent.addRef("inchi", inchi, 1, "is", ORIGIN);
                    ((BioMetabolite) ent).setInchi(inchi);
                }
            }
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
     * <p>Getter for the field <code>annotationPattern</code>.</p>
     *
     * @return the annotationPattern
     */
    public String getAnnotationPattern() {
        return annotationPattern;
    }

    /**
     * <p>Setter for the field <code>annotationPattern</code>.</p>
     *
     * @param annotationPattern the annotationPattern to set
     */
    public void setAnnotationPattern(String annotationPattern) {
        this.annotationPattern = annotationPattern;
    }

}
