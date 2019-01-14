package fr.inra.toulouse.metexplore.met4j_io.annotations;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;

public class GenericAttributes {

	public static final String LOWER_BOUND = "flux_lower_bound";
	public static final String UPPER_BOUND = "flux_upper_bound";
	public static final String UNIT_DEFINITIONS = "unit_definitions";
	public static final String NOTES = "notes";
	public static final String SCORE = "score";
	public static final String STATUS = "status";
	public static final String PMIDS = "pmids";
	public static final String COMMENT = "comment";
	public static final String CHARGE = "charge";
	public static final String SMILE = "smile";
	public static final String INCHIKEY = "inchikey";
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

		e.addAttribute(CONSTANT, flag);
	}

}
