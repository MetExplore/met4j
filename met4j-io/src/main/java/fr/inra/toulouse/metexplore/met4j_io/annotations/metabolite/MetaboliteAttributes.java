package fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;

public class MetaboliteAttributes {

	public static final String NOTES = "notes";
	public static final String COMMENT = "comment";
	public static final String CHARGE = "charge";
	public static final String INCHIKEY = "inchikey";
	public static final String BOUNDARY_CONDITION = "boundary_condition";

	/**
	 * get Boundary condition of a metabolite
	 * 
	 * @param m
	 * @return
	 */
	public static Boolean getBoundaryCondition(BioMetabolite m) {

		Boolean flag = false;

		if (m.getAttribute(BOUNDARY_CONDITION) != null) {
			flag = (Boolean) m.getAttribute(BOUNDARY_CONDITION);
		}

		return flag;
	}

	/**
	 * Set boundary condition
	 * 
	 * @param m
	 */
	public static void setBoundaryCondition(BioMetabolite m, Boolean flag) {
		m.setAttribute(BOUNDARY_CONDITION, flag);
	}

	/**
	 * get Constant condition of a metabolite
	 * 
	 * @param m
	 * @return
	 */
	public static Boolean getConstant(BioMetabolite m) {

		return GenericAttributes.getConstant(m);
	}

	/**
	 * Set constant condition
	 * 
	 * @param m
	 */
	public static void setConstant(BioMetabolite m, Boolean flag) {

		GenericAttributes.setConstant(m, flag);

	}

	/**
	 * 
	 * @param metabolite
	 * @param notes
	 */
	public static void setNotes(BioMetabolite m, Notes notes) {

		GenericAttributes.setNotes(m, notes);

	}

	/**
	 * 
	 * @param metabolite
	 * @param notes
	 */
	public static Notes getNotes(BioMetabolite m) {

		return GenericAttributes.getNotes(m);

	}

	/**
	 * get charge
	 * 
	 * @param m
	 * @return
	 */
	public static Double getCharge(BioMetabolite m) {

		Double charge = (Double) m.getAttribute(CHARGE);

		if (charge == null) {
			charge = 0.0;
		}

		return charge;

	}

	/**
	 * set charge
	 * 
	 * @param m
	 * @param charge
	 */
	public static void setCharge(BioMetabolite m, Double charge) {

		m.setAttribute(CHARGE, charge);

	}

}
