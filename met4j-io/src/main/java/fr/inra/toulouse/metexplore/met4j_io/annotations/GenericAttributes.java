package fr.inra.toulouse.metexplore.met4j_io.annotations;

import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.BioAnnotation;

public class GenericAttributes {

	public static final String SBML_UNIT_DEFINITIONS = "unit_definitions";
	public static final String NOTES = "notes";
	public static final String PMIDS = "pmids";
	public static final String COMMENT = "comment";
	public static final String SBO_TERM = "sbo_term";
	public static final String CONSTANT = "constant";

	public static final String ANNOTATION = "annotation";

	/**
	 * get Constant condition of a compartment
	 * 
	 * @param e
	 * @return
	 */
	public static Boolean getConstant(BioEntity e) {

		if (!(e.getClass().equals(BioCompartment.class) || e.getClass().equals(BioReactant.class)
				|| e.getClass().equals(BioMetabolite.class))) {
			throw new IllegalArgumentException("The entity must be a BioMetabolite, a BioCompartment or a BioReactant");
		}

		Boolean flag = true;

		if (e.getAttribute(CONSTANT) != null) {
			flag = (Boolean) e.getAttribute(CONSTANT);
		}

		return flag;

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

		return (Notes) e.getAttribute(NOTES);

	}

	/**
	 * set notes
	 * 
	 * @param e
	 * @return
	 */
	public static void setNotes(BioEntity e, Notes notes) {

		e.setAttribute(NOTES, notes);

	}

	/**
	 * set pmids
	 * 
	 * @param e
	 * @param pmids
	 */
	public static void setPmids(BioEntity e, Set<Integer> pmids) {

		e.setAttribute(PMIDS, pmids);

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
	 * 
	 * get comment
	 * 
	 * @param e
	 * @return
	 */
	public static String getComment(BioEntity e) {
		return (String) e.getAttribute(COMMENT);
	}

	/**
	 * 
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
		e.setAttribute(SBO_TERM, sboTerm);

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
	public static BioAnnotation getAnnotation(BioEntity e) {
		return (BioAnnotation) e.getAttribute(ANNOTATION);
	}

	/**
	 * set annotation
	 * 
	 * @param e
	 * @param sboTerm
	 */
	public static void setAnnotation(BioEntity e, BioAnnotation val) {
		e.setAttribute(ANNOTATION, val);

	}

}
