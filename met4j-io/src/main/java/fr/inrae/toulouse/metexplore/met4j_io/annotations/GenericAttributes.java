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

package fr.inrae.toulouse.metexplore.met4j_io.annotations;

import java.util.HashSet;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;

/**
 * <p>GenericAttributes class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class GenericAttributes {

    /**
     * Constant <code>SBML_UNIT_DEFINITIONS="unit_definitions"</code>
     */
    public static final String SBML_UNIT_DEFINITIONS = "unit_definitions";
    /**
     * Constant <code>SBML_NOTES="notes"</code>
     */
    public static final String SBML_NOTES = "notes";
    /**
     * Constant <code>PMIDS="pmids"</code>
     */
    public static final String PMIDS = "pmids";
    /**
     * Constant <code>COMMENT="comment"</code>
     */
    public static final String COMMENT = "comment";
    /**
     * Constant <code>SBO_TERM="sbo_term"</code>
     */
    public static final String SBO_TERM = "sbo_term";
    /**
     * Constant <code>CONSTANT="constant"</code>
     */
    public static final String CONSTANT = "constant";
    /**
     * Constant <code>SBML_ANNOTATION="annotation"</code>
     */
    public static final String SBML_ANNOTATION = "annotation";
    /**
     * Constant <code>GENERIC="generic"</code>
     */
    public static final String GENERIC = "generic";
    /**
     * Constant <code>TYPE="type"</code>
     */
    public static final String TYPE = "type";
    /**
     * Constant <code>ANNOTATOR_COMMENTS="annotator_comments"</code>
     */
    public static final String ANNOTATOR_COMMENTS = "annotator_comments";
    /**
     * Constant <code>AUTHORS="AUTHORS"</code>
     */
    public static final String AUTHORS = "AUTHORS";


    /**
     * get Constant condition
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public static Boolean getConstant(BioEntity e) {

        if (!(e.getClass().equals(BioCompartment.class) || e.getClass().equals(BioReactant.class)
                || e.getClass().equals(BioMetabolite.class))) {
            throw new IllegalArgumentException("The entity must be a BioMetabolite, a BioCompartment or a BioReactant");
        }

        if (e.getAttribute(CONSTANT) != null) {
            return (Boolean) e.getAttribute(CONSTANT);
        } else {
            return false;
        }

    }

    /**
     * Set constant condition
     *
     * @param e    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param flag a {@link java.lang.Boolean} object.
     */
    public static void setConstant(BioEntity e, Boolean flag) {

        if (!(e.getClass().equals(BioCompartment.class) || e.getClass().equals(BioReactant.class)
                || e.getClass().equals(BioMetabolite.class))) {
            throw new IllegalArgumentException("The entity must be a BioMetabolite, a BioCompartment or a BioReactant");
        }

        e.setAttribute(CONSTANT, flag);
    }

    /**
     * get notes
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes} object.
     */
    public static Notes getNotes(BioEntity e) {

        return (Notes) e.getAttribute(SBML_NOTES);

    }

    /**
     * set notes
     *
     * @param e     a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param notes a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes} object.
     */
    public static void setNotes(BioEntity e, Notes notes) {

        e.setAttribute(SBML_NOTES, notes);

    }

    /**
     * set pmids
     * <p>
     * TODO : see if it useful since there is BioRef...
     *
     * @param e     a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param pmids a {@link java.util.Set} object.
     */
    public static void setPmids(BioEntity e, Set<Integer> pmids) {

        e.setAttribute(PMIDS, pmids);

    }

    /**
     * add pmid
     *
     * @param e    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param pmid a {@link java.lang.Integer} object.
     */
    public static void addPmid(BioEntity e, Integer pmid) {

        if (getPmids(e) == null) {
            setPmids(e, new HashSet<Integer>());
        }

        getPmids(e).add(pmid);

    }

    /**
     * get pmids
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link java.util.Set} object.
     */
    @SuppressWarnings("unchecked")
    public static Set<Integer> getPmids(BioEntity e) {

        return (Set<Integer>) e.getAttribute(PMIDS);

    }

    /**
     * get comment
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getComment(BioEntity e) {
        return (String) e.getAttribute(COMMENT);
    }

    /**
     * set comment
     *
     * @param e       a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param comment a {@link java.lang.String} object.
     */
    public static void setComment(BioEntity e, String comment) {
        e.setAttribute(COMMENT, comment);
    }

    /**
     * set Sbo term
     *
     * @param e       a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param sboTerm a {@link java.lang.String} object.
     */
    public static void setSboTerm(BioEntity e, String sboTerm) {
        if (!StringUtils.isValidSboTerm(sboTerm)) {
            System.err.println("[Warning] SBO term badly formatted for " + e.getId()
                    + " (" + sboTerm + ", must be in the format SBO:1234567). It has been set to null.");
            e.getAttributes().remove(SBO_TERM);
        } else {
            e.setAttribute(SBO_TERM, sboTerm);
        }
    }

    /**
     * Get SBO term
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getSboTerm(BioEntity e) {
        return (String) e.getAttribute(SBO_TERM);
    }

    /**
     * Get annotation
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation} object.
     */
    public static SbmlAnnotation getAnnotation(BioEntity e) {
        return (SbmlAnnotation) e.getAttribute(SBML_ANNOTATION);
    }

    /**
     * set annotation
     *
     * @param e   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param val a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation} object.
     */
    public static void setAnnotation(BioEntity e, SbmlAnnotation val) {
        e.setAttribute(SBML_ANNOTATION, val);

    }

    /**
     * get the generic attribute of an entity
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link java.lang.Boolean} object.
     */
    public static Boolean getGeneric(BioEntity e) {

        Boolean flag = false;

        if (e.getAttribute(GENERIC) != null) {
            flag = (Boolean) e.getAttribute(GENERIC);
        }

        return flag;
    }

    /**
     * Set generic attribute
     *
     * @param e    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param flag a {@link java.lang.Boolean} object.
     */
    public static void setGeneric(BioEntity e, Boolean flag) {
        e.setAttribute(GENERIC, flag);
    }

    /**
     * get type
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getType(BioEntity e) {
        return (String) e.getAttribute(TYPE);
    }

    /**
     * set comment
     *
     * @param e    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param type a {@link java.lang.String} object.
     */
    public static void setType(BioEntity e, String type) {
        e.setAttribute(TYPE, type);
    }

    /**
     * set annotator comments
     *
     * @param e        a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param comments a {@link java.util.Set} object.
     */
    public static void setAnnotatorComments(BioEntity e, Set<AnnotatorComment> comments) {

        e.setAttribute(ANNOTATOR_COMMENTS, comments);

    }

    /**
     * add comment
     *
     * @param e       a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @param comment a {@link fr.inrae.toulouse.metexplore.met4j_io.annotations.AnnotatorComment} object.
     */
    public static void addAnnotatorComment(BioEntity e, AnnotatorComment comment) {

        if (getAnnotatorComments(e) == null) {
            setAnnotatorComments(e, new HashSet<AnnotatorComment>());
        }

        getAnnotatorComments(e).add(comment);

    }

    /**
     * get comments
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
     * @return a {@link java.util.Set} object.
     */
    @SuppressWarnings("unchecked")
    public static Set<AnnotatorComment> getAnnotatorComments(BioEntity e) {

        return (Set<AnnotatorComment>) e.getAttribute(ANNOTATOR_COMMENTS);

    }

}
