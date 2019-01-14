package fr.inra.toulouse.metexplore.met4j_io.annotations.compartment;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;

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
	
	/**
	 * 
	 * @param compartment
	 * @param notes
	 */
	public static void setNotes(BioCompartment c, Notes notes) {
	
		GenericAttributes.setNotes(c, notes);
		
	}
	
	/**
	 * 
	 * @param compartment
	 * @param notes
	 */
	public static Notes getNotes(BioCompartment c) {
		
		return GenericAttributes.getNotes(c);
		
	}



}
