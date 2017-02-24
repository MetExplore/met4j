/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
/**
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

/**
 * @author ludo & Fabien
 *
 *	Biological cellular compartment, e.g. mitochondria, cytoplasm
 *	SBML v2 Version 4 release 1
 *	"A compartment in SBML represents a bounded space in which species are located.
 *	Compartments do not necessarily have to correspond to actual structures inside or outside of a biological cell, although models are often designed that way."
 */
public class BioCompartment {
	/**
	 * id is the identifier for this compartment, default is NA
	 */
	private String id="NA";
	/**
	 * Name is a user specified name, default is NA (optional)
	 */
	private String name="NA";
	/**
	 * Compartment containing the current compartment type
	 */
	private BioCompartmentType compartmentType=null;
	/**
	 * SBML v2 Version 4 release 1:
	 * "A Compartment object has an optional attribute spatialDimensions, whose value must be a positive integer indicating the number of spatial dimensions possessed by the compartment.
	 * The maximum value is �3�, meaning a three-dimensional structure (a volume). 
	 * Other permissible values are �2� (for a two-dimensional area), �1� (for a one-dimensional curve), and �0� (for a point).
	 * The default value is �3�.
	 * Note that the number of spatial dimensions possessed by a compartment affects certain aspects of the compartment�s size and units-of-size."
	 */
	private int spatialDimensions=3;
	/**
	 * SBML v2 Version 4 release 1:
	 * "Each compartment has an optional floating-point attribute named size, representing the initial total size of the compartment.
	 * The size may be a volume (if the compartment is a three-dimensional one), or it may be 4 an area (if the compartment is two-dimensional), or a length (if the compartment is one-dimensional)."
	 */
	private double size=1;
	/**
	 * SBML v2 Version 4 release 1:
	 * "The units associated with the compartment�s size value may be set using the optional attribute units.
	 * The default units, and the kinds of units allowed as values of the attribute units, interact with the number of spatial dimensions of the compartment."
	 */
	private BioUnitDefinition unit;
	/**
	 * SBML v2 Version 4 release 1:
	 * "The optional attribute outside of type SId can be used to express one type of positioning relationship between compartments.
	 * If present, the value of outside for a given compartment must be the id attribute value of another compartment defined in the model.
	 * Doing so means that the other compartment surrounds it or is �outside� of it.
	 * This enables the representation of simple topological relationships between compartments, for those simulation systems that can make use of the information (e.g., for drawing simple diagrams of compartments)."
	 */
	private BioCompartment outsideCompartment=null;
	/**
	 * SBML v2 Version 4 release 1:
	 * "A Compartment also has an optional boolean attribute called constant that indicates whether the compartment�s size stays constant or can vary during a simulation.
	 * A value of �false� indicates the compartment�s size can be changed by other constructs in SBML.
	 * A value of �true� indicates the compartment�s size cannot be changed by any other construct except InitialAssignment.
	 * In the special case of spatialDimensions=�0�, the value cannot be changed by InitialAssignment either.
	 * The default value for the constant attribute is �true� because in the most common modeling scenarios at the time of this writing, compartment sizes remain constant.
	 * The constant attribute must default to or be set to �true� if the value of the spatialDimensions attribute is �0�, because a zero-dimensional compartment cannot ever have a size."
	 */
	private boolean constant=true;
	
	private String sboterm;
	
	private Boolean flagedAsUpdate=false;
	private Boolean flagedAsInsert=false;
	private Boolean flagedAsConflict=false;
	
	private String fake_id = "fake_compartment";
	private String fake_name = "fake compartment";
	
	
	public BioCompartment() {

	}
	
	/**
	 * Constructor by copy
	 * @param c compartment to copy
	 */
	public BioCompartment(BioCompartment bioCompartment) {
		if(bioCompartment != null) 
		{
			this.name = bioCompartment.getName();
			this.id = bioCompartment.getId();
		}
	}
	
	/**
	 * BioCompartment constructor
	 * @param name Common name of the compartment
	 * @param id Identifier of the compartment
	 */
	public BioCompartment(String name, String id) {
		this.name = name;
		this.id = id;
	}
	/**
	 * 
	 * @return the BioCompartment containing the current one
	 */
	public BioCompartment getOutsideCompartment() {
		return outsideCompartment;
	}
	/**
	 * Set the compartment containing the current one
	 * @param outsideCompartment
	 */
	public void setOutsideCompartment(BioCompartment outsideCompartment) {
		this.outsideCompartment = outsideCompartment;
	}
	
	/**
	 * get the sbo term of the entity
	 * @return sboterm : String
	 */
	public String getSboterm() {
		return sboterm;
	}

	/**
	 * Set the sbo term of the entity
	 * @param sboterm : String
	 */
	public void setSboterm(String sboterm) {
		this.sboterm = sboterm;
	}
	
	/**
	 * Useful to indicate each time the same compartment for proteins, enzymes, etc...
	 */
	public void setAsFakeCompartment () {
		this.setName(fake_name);
		this.setId(fake_id);
	}
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BioCompartmentType getCompartmentType() {
		return compartmentType;
	}
	public void setCompartmentType(BioCompartmentType compartmentType) {
		this.compartmentType = compartmentType;
	}
	public int getSpatialDimensions() {
		return spatialDimensions;
	}
	public void setSpatialDimensions(int spatialDimensions) {
		this.spatialDimensions = spatialDimensions;
	}
	public double getSize() {
		return size;
	}
	public void setSize(double size) {
		this.size = size;
	}
	public BioUnitDefinition getUnit() {
		return unit;
	}
	public void setUnit(BioUnitDefinition unit) {
		this.unit = unit;
	}

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	public Boolean getFlagedAsUpdate() {
		return flagedAsUpdate;
	}

	public void setFlagedAsUpdate(Boolean flagedAsUpdate) {
		this.flagedAsUpdate = flagedAsUpdate;
	}

	public Boolean getFlagedAsInsert() {
		return flagedAsInsert;
	}

	public void setFlagedAsInsert(Boolean flagedAsInsert) {
		this.flagedAsInsert = flagedAsInsert;
	}

	public Boolean getFlagedAsConflict() {
		return flagedAsConflict;
	}

	public void setFlagedAsConflict(Boolean flagedAsConflict) {
		this.flagedAsConflict = flagedAsConflict;
	}
	
}
