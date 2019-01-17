package fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin;

import java.util.ArrayList;

import org.sbml.jsbml.Model;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;

/**
 * An interface that declares common methods for the writer plugins
 * 
 * @author Benjamin
 * @since 3.0
 */
public interface PackageWriter {

	/**
	 * The list of errors and/or warnings raised by the Plugin
	 */
	public ArrayList<String> errorsAndWarnings = new ArrayList<String>();

	/**
	 * If this package plugin uses an official SBML package, this method must
	 * return the key name of your package in the HashMap returned by the model
	 * getExtensionPackages() method, a custom name otherwise
	 * 
	 * @return the name of the plugin
	 */
	String getAssociatedPackageName();

	/**
	 * This method tests if the package can be used on the specified SBML Level.
	 * </br> </br>Custom testing can be performed or this method can also always
	 * return true if you want it to be used in every model conversion
	 * 
	 * @param lvl
	 *            the level of the SBML that is going to be written
	 * @return true if the package is compatible with the level, false otherwise
	 */
	boolean isPackageUseableOnLvl(int lvl);

	/**
	 * Parse the bionetwork and add the converted data to the SBML model using
	 * jsbml
	 * 
	 * @param model
	 *            The SBML model
	 * @param bionetwork
	 *            The BioNetwork
	 */
	void parseBionetwork(Model model, BioNetwork bionetwork);

}
