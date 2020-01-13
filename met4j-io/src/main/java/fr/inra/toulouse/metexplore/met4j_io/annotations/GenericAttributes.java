package fr.inra.toulouse.metexplore.met4j_io.annotations;

import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;

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
