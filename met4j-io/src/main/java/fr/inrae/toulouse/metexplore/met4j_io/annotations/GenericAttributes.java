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

public class GenericAttributes {

    public static final String SBML_UNIT_DEFINITIONS = "unit_definitions";
    public static final String SBML_NOTES = "notes";
    public static final String PMIDS = "pmids";
    public static final String COMMENT = "comment";
    public static final String SBO_TERM = "sbo_term";
    public static final String CONSTANT = "constant";
    public static final String SBML_ANNOTATION = "annotation";
    public static final String GENERIC = "generic";
    public static final String TYPE = "type";
    public static final String ANNOTATOR_COMMENTS = "annotator_comments";
    public static final String AUTHORS = "AUTHORS";


    /**
     * get Constant condition
     *
     * @param e
     * @return
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
     * @param m
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
     * @param e
     * @return
     */
    public static Notes getNotes(BioEntity e) {

        return (Notes) e.getAttribute(SBML_NOTES);

    }

    /**
     * set notes
     *
     * @param e
     * @return
     */
    public static void setNotes(BioEntity e, Notes notes) {

        e.setAttribute(SBML_NOTES, notes);

    }

    /**
     * set pmids
     * <p>
     * TODO : see if it useful since there is BioRef...
     *
     * @param e
     * @param pmids
     */
    public static void setPmids(BioEntity e, Set<Integer> pmids) {

        e.setAttribute(PMIDS, pmids);

    }

    /**
     * add pmid
     *
     * @param e
     * @param pmids
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
     * @param e
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set<Integer> getPmids(BioEntity e) {

        return (Set<Integer>) e.getAttribute(PMIDS);

    }

    /**
     * get comment
     *
     * @param e
     * @return
     */
    public static String getComment(BioEntity e) {
        return (String) e.getAttribute(COMMENT);
    }

    /**
     * set comment
     *
     * @param e
     * @return
     */
    public static void setComment(BioEntity e, String comment) {
        e.setAttribute(COMMENT, comment);
    }

    /**
     * set Sbo term
     *
     * @param e
     * @param sboTerm
     */
    public static void setSboTerm(BioEntity e, String sboTerm) {
        if (!StringUtils.isValidSboTerm(sboTerm)) {
            System.err.println("[Warning] SBO term badly formatted for " + e.getId()
                    + " ("+sboTerm+", must be in the format SBO:1234567). It has not been set.");
        } else {
            e.setAttribute(SBO_TERM, sboTerm);
        }
    }

    /**
     * Get SBO term
     *
     * @param e
     * @return
     */
    public static String getSboTerm(BioEntity e) {
        return (String) e.getAttribute(SBO_TERM);
    }

    /**
     * Get annotation
     *
     * @param e
     * @return
     */
    public static SbmlAnnotation getAnnotation(BioEntity e) {
        return (SbmlAnnotation) e.getAttribute(SBML_ANNOTATION);
    }

    /**
     * set annotation
     *
     * @param e
     * @param sboTerm
     */
    public static void setAnnotation(BioEntity e, SbmlAnnotation val) {
        e.setAttribute(SBML_ANNOTATION, val);

    }

    /**
     * get the generic attribute of an entity
     *
     * @param m
     * @return
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
     * @param m
     */
    public static void setGeneric(BioEntity e, Boolean flag) {
        e.setAttribute(GENERIC, flag);
    }

    /**
     * get type
     *
     * @param e
     * @return
     */
    public static String getType(BioEntity e) {
        return (String) e.getAttribute(TYPE);
    }

    /**
     * set comment
     *
     * @param e
     * @return
     */
    public static void setType(BioEntity e, String type) {
        e.setAttribute(TYPE, type);
    }

    /**
     * set annotator comments
     *
     * @param e
     * @param comments
     */
    public static void setAnnotatorComments(BioEntity e, Set<AnnotatorComment> comments) {

        e.setAttribute(ANNOTATOR_COMMENTS, comments);

    }

    /**
     * add comment
     *
     * @param e
     * @param comment
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
     * @param e
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set<AnnotatorComment> getAnnotatorComments(BioEntity e) {

        return (Set<AnnotatorComment>) e.getAttribute(ANNOTATOR_COMMENTS);

    }

}
