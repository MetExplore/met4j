package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import java.util.ArrayList;

import org.sbml.jsbml.Model;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;

/**
 * An interface that declares common methods for the parser plugins 
 * @author Benjamin
 * @since 3.0
 */
public interface PackageParser {
	
	/**
	 * The list of errors and/or warnings raised by the Plugin 
	 */
	public ArrayList<String> errorsAndWarnings=new ArrayList<String>();

	/**
	 * Parse the model and add the converted data to the bionetwork
	 * @param model
	 * 	The SBML model
	 * @param bionetwork
	 * The BioNetwork
	 */
	void parseModel(Model model, BioNetwork bionetwork);
	
	/**
	 * If this package plugin uses an official SBML package, this method must return the key name of your package 
	 * in the HashMap returned by the model getExtensionPackages() method, a custom name otherwise
	 * @return the name of the plugin
	 */
	String getAssociatedPackageName();
	
	
	/**
	 * This method tests if the package can be used on the specified JSBML Model.
	 * If the package is dependent on a SBML package, it should test if this package's namespace is present
	 * in the model element by using the model.isPackageURIEnabled() method. 
	 * </br>Otherwise, custom testing can be performed on the model. This method can also always return true if you want it 
	 * to be used in every model conversion
	 * @param model
	 * the SBML model
	 * @return true if this package plugin can be used on the model, false otherwise
	 */
	boolean isPackageUseableOnModel(Model model);

	
}