package fr.inra.toulouse.metexplore.met4j_io.annotations.compartment;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;

public class CompartmentAttributes {
	
	public static final String OUTSIDE_COMPARTMENT = "outside_compartment";
	public static final String TYPE = "type";
	public static final String SIZE = "size";
	public static final String SPATIAL_DIMENSIONS = "spatial_dimensions";



	
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
	
	/**
	 * Set outside compartment
	 * @param c
	 * @param outside
	 */
	public static void setOutsideCompartment(BioCompartment c, BioCompartment outside) {
		
		if(c.equals(outside)) {
			throw new IllegalArgumentException("The compartment and the outside compartment must be different");
		}
		
		c.setAttribute(OUTSIDE_COMPARTMENT, outside);
	}
	
	/**
	 * get outside compartment
	 * @param c
	 * @return
	 */
	public static BioCompartment getOutsideCompartment(BioCompartment c) {
		return (BioCompartment) c.getAttribute(OUTSIDE_COMPARTMENT);
	}
	
	
	/**
	 * Set type
	 * @param c
	 * @param type
	 */
	public static void setType(BioCompartment c, String type) {
		
		c.setAttribute(TYPE, type);
		
	}
	
	/**
	 * Get Type
	 * @param c
	 * @return
	 */
	public static String getType(BioCompartment c) {
		return (String) c.getAttribute(TYPE);
	}
	
	



}
