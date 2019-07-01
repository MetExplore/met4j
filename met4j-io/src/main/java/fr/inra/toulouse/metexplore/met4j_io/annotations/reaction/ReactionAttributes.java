package fr.inra.toulouse.metexplore.met4j_io.annotations.reaction;

import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;


public class ReactionAttributes {

	public static final String LOWER_BOUND = "flux_lower_bound";
	public static final String UPPER_BOUND = "flux_upper_bound";
	public static final String SCORE = "score";
	public static final String STATUS = "status";
	public static final String FAST = "fast";
	public static final String KINETIC_FORMULA = "kinetic_formula";
	public static final String FLUX_PARAMS = "flux_params";
	public static final String ADDITIONAL_FLUX_PARAMS = "additional_flux_params";
	public static final String SUBSYSTEM = "SUBSYSTEM";
	public static final String EC_NUMBER = "EC_NUMBER";
	public static final String GENE_ASSOCIATION = "GENE_ASSOCIATION";
	public static final String HOLE = "hole";
	public static final String SIDE_COMPOUNDS = "side_compounds";

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

		if (!r.isReversible() && (val != null && val.value < 0)) {
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

		if (!r.isReversible() && (val != null && val.value < 0)) {
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
	 * Add pmid
	 * @param r
	 * @param pmid
	 */
	public static void addPmid(BioReaction r, Integer pmid) {
		GenericAttributes.addPmid(r, pmid);
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
	
	/**
	 * Get Sbo term
	 * 
	 * @param c
	 * @return
	 */
	public static String getSboTerm(BioReaction r) {
		return GenericAttributes.getSboTerm(r);
	}

	/**
	 * Set Sbo term
	 * 
	 * @param c
	 * @param sboTerm
	 */
	public static void setSboTerm(BioReaction r, String sboTerm) {
		GenericAttributes.setSboTerm(r, sboTerm);
	}

	
	/**
	 * get fast attribute
	 * @param r
	 * @return
	 */
	public static boolean getFast(BioReaction r) {
		return r.getAttribute(FAST)!=null ? (Boolean) r.getAttribute(FAST) : false;
	}
	
	/**
	 * Set fast attribute
	 * @param r
	 * @param flag
	 */
	public static void setFast(BioReaction r, boolean flag) {
		r.setAttribute(FAST, flag);
	}
	
	/**
	 * get kinetic formula
	 * @param r
	 * @return
	 */
	public static String getKineticFormula(BioReaction r) {
		return (String)r.getAttribute(KINETIC_FORMULA);
	}
	
	/**
	 * Set fast attribute
	 * @param r
	 * @param flag
	 */
	public static void setKineticFormula(BioReaction r, String k) {
		r.setAttribute(KINETIC_FORMULA, k);
	}

	/**
	 * Get flux params
	 * @param r
	 * @return
	 */
	public static FluxCollection getFluxParams(BioReaction r) {
		return (FluxCollection)r.getAttribute(FLUX_PARAMS);
	}

	/**
	 * Set Flux params
	 * @param r
	 * @param c
	 */
	public static void setFluxParams(BioReaction r, FluxCollection c) {
		r.setAttribute(FLUX_PARAMS, c);
	}
	
	
	/**
	 * Get addtional flux params
	 * @param r
	 * @return
	 */
	public static FluxCollection getAdditionalFluxParams(BioReaction r) {
		return (FluxCollection)r.getAttribute(ADDITIONAL_FLUX_PARAMS);
	}

	/**
	 * Set additional Flux params
	 * @param r
	 * @param c
	 */
	public static void setAdditionalFluxParams(BioReaction r, FluxCollection c) {
		r.setAttribute(ADDITIONAL_FLUX_PARAMS, c);
	}
	
	
	
	/**
	 * Add a flux
	 * @param r
	 * @param f
	 */
	public static void addFlux(BioReaction r, Flux f) {
		
		if(getFluxParams(r) == null)
		{
			setFluxParams(r, new FluxCollection());
		}
		
		getFluxParams(r).add(f);
		
	}

	/**
	 * get flux from its id
	 * @param r
	 * @param id
	 * @return
	 */
	public static Flux getFlux(BioReaction r, String id) {
		
		if(getFluxParams(r) == null)
		{
			return null;
		}
		
		return getFluxParams(r).getEntityFromId(id);
		
		
	}
	/**
	 * get the hole attribute of a metabolite
	 * 
	 * @param r
	 * @return
	 */
	public static Boolean getHole(BioReaction r) {

		Boolean flag = false;

		if (r.getAttribute(HOLE) != null) {
			flag = (Boolean) r.getAttribute(HOLE);
		}

		return flag;
	}

	/**
	 * Set hole attribute
	 * 
	 * @param m
	 */
	public static void setHole(BioReaction r, Boolean flag) {
		r.setAttribute(HOLE, flag);
	}

	/**
	 * set side compounds
	 * 
	 * @param e
	 * @param side compounds
	 */
	public static void setSideCompounds(BioReaction e, Set<String> sideCompounds) {

		e.setAttribute(SIDE_COMPOUNDS, sideCompounds);

	}

	/**
	 * add side compound
	 * 
	 * @param e
	 * @param side compound
	 */
	public static void addSideCompound(BioReaction e, String id) {

		if (getSideCompounds(e) == null) {
			setSideCompounds(e, new HashSet<String>());
		}

		getSideCompounds(e).add(id);

	}

	/**
	 * get side compounds
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<String> getSideCompounds(BioReaction e) {

		return (Set<String>) e.getAttribute(SIDE_COMPOUNDS);

	}
	
}
