package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

/**
 * Simple Enumeration to fix the possible values for the types of {@link Objectives} present in the model
 * @author Benjamin
 * @since 3.0
 */
public enum FbcType {
	/**
	 * The Maximize FBC type. This means that the fluxes of the set of reactions present in the {@link Objectives} needs to be maximized
	 */
	maximize,
	/**
	 * The minimize FBC type. This means that the fluxes of the set of reactions present in the {@link Objectives} needs to be minimized
	 */
	minimize
}