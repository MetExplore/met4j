package fr.inra.toulouse.metexplore.met4j_io.annotations.reaction;

import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.Flux;

public class ReactionAttributes {

	public static final String LOWER_BOUND = "flux_lower_bound";
	public static final String UPPER_BOUND = "flux_upper_bound";
	public static final String SCORE = "score";
	public static final String STATUS = "status";



	public static final double MIN_LOWER_BOUND = Flux.FLUXMIN;
	public static final double MAX_UPPER_BOUND = Flux.FLUXMAX;

	/**
	 * get lower bound
	 * 
	 * @param r
	 * @return
	 */
	public static Flux getLowerBound(BioReaction r) {

		return (Flux) r.getAttribute(LOWER_BOUND);

	}

	/**
	 * get upper bound
	 * 
	 * @param r
	 * @return
	 */
	public static Flux getUpperBound(BioReaction r) {

		return (Flux) r.getAttribute(UPPER_BOUND);
	}

	/**
	 * 
	 * set lower bound
	 * 
	 * @param r
	 * @param val
	 */
	public static void setLowerBound(BioReaction r, Flux val) {

		if (!r.isReversible() && val.value < 0) {
			throw new IllegalArgumentException(
					"The flux value must be greater or equal to 0 since the reaction is irreversible");
		}
		r.setAttribute(LOWER_BOUND, val);
	}

	/**
	 * 
	 * set upper bound
	 * 
	 * @param r
	 * @param val
	 */
	public static void setUpperBound(BioReaction r, Flux val) {

		if (!r.isReversible() && val.value < 0) {
			throw new IllegalArgumentException(
					"The flux value must be greater or equal to 0 since the reaction is irreversible");
		}
		r.setAttribute(UPPER_BOUND, val);
	}

	
	/**
	 * 
	 * @param network
	 * @param notes
	 */
	public static void setNotes(BioReaction r, Notes notes) {
		
		GenericAttributes.setNotes(r, notes);
		
	}
	
	/**
	 * 
	 * @param network
	 * @param notes
	 */
	public static Notes getNotes(BioReaction r) {
		
		return GenericAttributes.getNotes(r);
		
	}
	
	/**
	 * Get Score
	 * @param r
	 * @return
	 */
	public static Double getScore(BioReaction r) {
		
		return (Double)r.getAttribute(SCORE);
	}
	
	/**
	 * Set score
	 * @param r
	 * @param score
	 */
	public static void setScore(BioReaction r, Double score) {
		
		r.setAttribute(SCORE, score);
	}
	
	/**
	 * Get status
	 * @param r
	 * @return
	 */
	public static String getStatus(BioReaction r) {
		
		return (String)r.getAttribute(STATUS);
	}
	
	/**
	 * Set status
	 * @param r
	 * @param status
	 */
	public static void setStatus(BioReaction r, String status) {
		
		r.setAttribute(STATUS, status);
	}
	
	/**
	 * get pmids
	 * @param r
	 * @return
	 */
	public static Set<Integer> getPmids(BioReaction r) {
		
		return GenericAttributes.getPmids(r);
		
	}
	
	/**
	 * 
	 * set pmids
	 * 
	 * @param r
	 * @param pmids
	 */
	public static void setPmids(BioReaction r, Set<Integer> pmids) {
		
		GenericAttributes.setPmids(r, pmids);
		
	}
	
	/**
	 * Set comment
	 * @param r
	 * @param comment
	 */
	public static void setComment(BioReaction r, String comment) {
		
		GenericAttributes.setComment(r, comment);
		
	}
	
	/**
	 * Get comment
	 * @param r
	 * @return
	 */
	public static String getComment(BioReaction r) {
		
		return GenericAttributes.getComment(r);
		
		
	}
	
	
	
	
}
