package fr.inra.toulouse.metexplore.met4j_io.annotations.compartment;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;

public class CompartmentAttributes {

	public static final String OUTSIDE_COMPARTMENT = "outside_compartment";
	public static final String TYPE = "type";
	public static final String SIZE = "size";
	public static final String SPATIAL_DIMENSIONS = "spatial_dimensions";
	public static final String UNIT_DEFINITION = "unit_definition";

	/**
	 * get Constant condition of a compartment
	 * 
	 * @param c
	 * @return
	 */
	public static Boolean getConstant(BioCompartment c) {

		return GenericAttributes.getConstant(c);
	}

	/**
	 * Set constant condition
	 * 
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
	 * 
	 * @param c
	 * @param outside
	 */
	public static void setOutsideCompartment(BioCompartment c, BioCompartment outside) {

		if (c.equals(outside)) {
			throw new IllegalArgumentException("The compartment and the outside compartment must be different");
		}

		c.setAttribute(OUTSIDE_COMPARTMENT, outside);
	}

	/**
	 * get outside compartment
	 * 
	 * @param c
	 * @return
	 */
	public static BioCompartment getOutsideCompartment(BioCompartment c) {
		return (BioCompartment) c.getAttribute(OUTSIDE_COMPARTMENT);
	}

	/**
	 * Set type
	 * 
	 * @param c
	 * @param type
	 */
	public static void setType(BioCompartment c, BioCompartmentType type) {

		c.setAttribute(TYPE, type);

	}

	/**
	 * Get Type
	 * 
	 * @param c
	 * @return
	 */
	public static BioCompartmentType getType(BioCompartment c) {
		return (BioCompartmentType) c.getAttribute(TYPE);
	}

	/**
	 * Add a unit definition
	 * 
	 * @param e
	 * @param unitDefinition
	 */
	public static void setUnitDefinition(BioCompartment c, BioUnitDefinition unitDefinition) {

		c.setAttribute(UNIT_DEFINITION, unitDefinition);

	}

	/**
	 * Get a Unit definition from its id
	 * 
	 * @param e
	 * @param id
	 * @return
	 */
	public static BioUnitDefinition getUnitDefinition(BioCompartment c) {

		return ((BioUnitDefinition) c.getAttribute(UNIT_DEFINITION));

	}

	/**
	 * Get Sbo term
	 * 
	 * @param c
	 * @return
	 */
	public static String getSboTerm(BioCompartment c) {
		return GenericAttributes.getSboTerm(c);
	}

	/**
	 * Set Sbo term
	 * 
	 * @param c
	 * @param sboTerm
	 */
	public static void setSboTerm(BioCompartment c, String sboTerm) {
		GenericAttributes.setSboTerm(c, sboTerm);
	}

	/**
	 * get size
	 * 
	 * @param c
	 * @return
	 */
	public static double getSize(BioCompartment c) {
		return (double) c.getAttribute(SIZE);
	}
	
	/**
	 * Set size
	 * 
	 * @param c
	 * @param s
	 */
	public static void setSize(BioCompartment c, Double s) {
		
		c.setAttribute(SIZE, s);
		
	}
	
	/**
	 * get spatial dimensions
	 * 
	 * @param c
	 * @return
	 */
	public static Integer getSpatialDimensions(BioCompartment c) {
		return (Integer) c.getAttribute(SPATIAL_DIMENSIONS);
	}
	
	/**
	 * Set spatial dimensions
	 * 
	 * @param c
	 * @param s
	 */
	public static void setSpatialDimensions(BioCompartment c, Integer s) {
		
		c.setAttribute(SPATIAL_DIMENSIONS, s);
		
	}
	
	

}
