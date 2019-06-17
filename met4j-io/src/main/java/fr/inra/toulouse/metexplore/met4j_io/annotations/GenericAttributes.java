package fr.inra.toulouse.metexplore.met4j_io.annotations;

import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;

public class GenericAttributes {

	public static final String SBML_UNIT_DEFINITIONS = "unit_definitions";
	public static final String SBML_NOTES = "notes";
	public static final String PMIDS = "pmids";
	public static final String COMMENT = "comment";
	public static final String SBO_TERM = "sbo_term";
	public static final String CONSTANT = "constant";

	public static final String SBML_ANNOTATION = "annotation";

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

		if (e.getAttribute(CONSTANT) != null) {
			return (Boolean) e.getAttribute(CONSTANT);
		}
		else {
			return null;
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
	 * 
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
	public static void addPmid(BioEntity e,Integer pmid) {

		if(getPmids(e) == null) {
			setPmids(e,	 new HashSet<Integer>());
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
		if(! sboTerm.matches("SBO:\\d{7}")) {
			throw new IllegalArgumentException("SBO term badly formatted (must be SBO:1234567");
		}
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

}
