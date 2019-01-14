package fr.inra.toulouse.metexplore.met4j_io.annotations;

import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;

public class GenericAttributes {

	public static final String UNIT_DEFINITIONS = "unit_definitions";
	public static final String NOTES = "notes";
	public static final String PMIDS = "pmids";
	public static final String COMMENT = "comment";
	public static final String OUTSIDE_COMPARTMENT = "outside_compartment";
	public static final String TYPE = "type";
	public static final String UNIT = "unit";
	public static final String SBO_TERM = "sbo_term";
	public static final String CONSTANT = "constant";
	public static final String SIZE = "size";
	public static final String SPATIAL_DIMENSIONS = "spatial_dimensions";
	public static final String FAST = "fast";
	public static final String KINETIC_FORMULA = "kinetic_formula";
	public static final String FLUX_PARAMS = "flux_params";
	public static final String BOUNDARY_CONDITION = "boundary_condition";
	public static final String SUBSTANCE_UNITS = "substance_units";
	public static final String INITIAL_AMOUNT = "initial_quantity_amount";
	public static final String INITIAL_CONCENTRATION = "initial_quantity_concentration";
	public static final String ANNOTATION = "annotation";
	public static final String PUBCHEM_CID = "pubchem.compound";

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

		if (!(e.getClass().equals(BioNetwork.class) || e.getClass().equals(BioReaction.class)
				|| e.getClass().equals(BioMetabolite.class) ||  e.getClass().equals(BioCompartment.class))) {
			throw new IllegalArgumentException("The entity must be a BioCompartment, a BioMetabolite, a BioNetwork or a BioReaction");
		}

		return (Notes) e.getAttribute(NOTES);

	}

	/**
	 * set notes
	 * 
	 * @param e
	 * @return
	 */
	public static void setNotes(BioEntity e, Notes notes) {

		if (!(e.getClass().equals(BioNetwork.class) || e.getClass().equals(BioReaction.class)
				|| e.getClass().equals(BioMetabolite.class) ||  e.getClass().equals(BioCompartment.class))) {
			throw new IllegalArgumentException("The entity must be a BioCompartment, a BioMetabolite, a BioNetwork or a BioReaction");
		}

		e.setAttribute(NOTES, notes);

	}
	
	/**
	 * set pmids
	 * @param e
	 * @param pmids
	 */
	public static void setPmids(BioEntity e, Set<Integer> pmids) {
		
		if (!(e.getClass().equals(BioNetwork.class) || e.getClass().equals(BioReaction.class)
				)) {
			throw new IllegalArgumentException("The entity must be a BioNetwork or a BioReaction");
		}
		
		e.setAttribute(PMIDS, pmids);

	}
	
	/**
	 * get pmids
	 * @param e
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<Integer> getPmids (BioEntity e) {
		
		if (!(e.getClass().equals(BioNetwork.class) || e.getClass().equals(BioReaction.class)
				)) {
			throw new IllegalArgumentException("The entity must be a BioNetwork or a BioReaction");
		}
		
		return (Set<Integer>)e.getAttribute(PMIDS);
		
	}
	
	/**
	 * 
	 * get comment
	 * 
	 * @param e
	 * @return
	 */
	public static String getComment(BioEntity e) {
		return (String)e.getAttribute(COMMENT);
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
	

}
