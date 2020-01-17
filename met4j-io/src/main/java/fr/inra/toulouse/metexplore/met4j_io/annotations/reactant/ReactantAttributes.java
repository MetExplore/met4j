package fr.inra.toulouse.metexplore.met4j_io.annotations.reactant;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

public class ReactantAttributes extends GenericAttributes {
	public static final String IS_COFACTOR = "is_cofactor";
	
	/**
	 * get is cofactor attribute
	 * @param reactant
	 * @return
	 */
	public static Boolean getIsCofactor(BioReactant reactant) {
		if(reactant.getAttribute(IS_COFACTOR) == null) {
			return false;
		}
		return (Boolean) reactant.getAttribute(IS_COFACTOR);

	}
	
	/**
	 * Set IsCofactor value
	 * @param reactant
	 * @param b
	 */
	public static void setIsCofactor(BioReactant reactant, boolean b) {
		reactant.setAttribute(IS_COFACTOR, b);
	}
}
