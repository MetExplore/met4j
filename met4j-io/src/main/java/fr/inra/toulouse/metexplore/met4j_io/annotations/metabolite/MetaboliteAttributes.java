package fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

public class MetaboliteAttributes {
	

	public static final String NOTES = "notes";
	public static final String COMMENT = "comment";
	public static final String CHARGE = "charge";
	public static final String SMILE = "smile";
	public static final String INCHIKEY = "inchikey";
	public static final String OUTSIDE_COMPARTMENT = "outside_compartment";
	public static final String SBO_TERM = "sbo_term";
	public static final String CONSTANT = "constant";
	public static final String BOUNDARY_CONDITION = "boundary_condition";
	public static final String SUBSTANCE_UNITS = "substance_units";
	public static final String INITIAL_AMOUNT = "initial_quantity_amount";
	public static final String INITIAL_CONCENTRATION = "initial_quantity_concentration";
	public static final String ANNOTATION = "annotation";
	public static final String PUBCHEM_CID = "pubchem.compound";
	
	
	/**
	 * get Boundary condition of a metabolite
	 * @param m
	 * @return
	 */
	public static Boolean getBoundaryCondition(BioMetabolite m) {
		
		Boolean flag = false;
		
		if(m.getAttribute(BOUNDARY_CONDITION) != null)
		{
			flag = (Boolean)m.getAttribute(BOUNDARY_CONDITION);
		}
		
		return flag;
	}


}
