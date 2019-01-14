package fr.inra.toulouse.metexplore.met4j_io.annotations.compartment;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;

public class CompartmentAttributes {
	
	public static final String CONSTANT = "constant";
	
	/**
	 * get Constant condition of a compartment
	 * @param c
	 * @return
	 */
	public static Boolean getConstant(BioCompartment c) {
		
		Boolean flag = true;
		
		if(c.getAttribute(CONSTANT) != null)
		{
			flag = (Boolean)c.getAttribute(CONSTANT);
		}
		
		return flag;
	}
	
	/**
	 * Set constant condition
	 * @param m
	 */
	public static void setConstant(BioCompartment c, Boolean flag) {
		c.addAttribute(CONSTANT, flag);
	}
	



}
