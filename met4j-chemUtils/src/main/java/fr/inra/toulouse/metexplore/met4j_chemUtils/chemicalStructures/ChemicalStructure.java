package fr.inra.toulouse.metexplore.met4j_chemUtils.chemicalStructures;

/**
 * Abstract Class that defines common methods for Chemical Structures
 * @author Benjamin
 * @since 2.0
 */
public abstract class ChemicalStructure {
	
	/**
	 * true if the chemical Structure is valid.
	 * This attribute is set upon construction
	 */
	public boolean validity=true;
	
	/**
	 * Return the validity of the structure
	 * @return {@link #validity}
	 */
	public abstract boolean isValid();
	
	
	/**
	 * @param validity the validity to set
	 */
	public void setValidity(boolean validity) {
		this.validity = validity;
	}


	public abstract String toString();
}
