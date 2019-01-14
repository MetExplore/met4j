package fr.inra.toulouse.metexplore.met4j_io.annotations.reactant;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

public class ReactantAttributes {
	
	/**
	 * get Constant condition of a compartment
	 * @param c
	 * @return
	 */
	public static Boolean getConstant(BioReactant r) {
		
		return GenericAttributes.getConstant(r);
	}
	
	/**
	 * Set constant condition
	 * @param m
	 */
	public static void setConstant(BioReactant r, Boolean flag) {
		
		GenericAttributes.setConstant(r, flag);
	}

}
