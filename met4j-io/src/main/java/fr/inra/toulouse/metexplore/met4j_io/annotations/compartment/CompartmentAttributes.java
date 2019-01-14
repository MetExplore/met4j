package fr.inra.toulouse.metexplore.met4j_io.annotations.compartment;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

public class CompartmentAttributes {
	
	
	/**
	 * get Constant condition of a compartment
	 * @param c
	 * @return
	 */
	public static Boolean getConstant(BioCompartment c) {
		
		return GenericAttributes.getConstant(c);
	}
	
	/**
	 * Set constant condition
	 * @param m
	 */
	public static void setConstant(BioCompartment c, Boolean flag) {
		
		GenericAttributes.setConstant(c, flag);
	}
	



}
