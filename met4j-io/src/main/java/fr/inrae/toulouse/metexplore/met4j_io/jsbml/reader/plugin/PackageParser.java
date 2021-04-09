/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import java.util.ArrayList;

import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import org.sbml.jsbml.Model;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;

/**
 * An interface that declares common methods for the parser plugins
 *
 * @author Benjamin
 * @since 3.0
 * @version $Id: $Id
 */
public interface PackageParser {
	
	/**
	 * The list of errors and/or warnings raised by the Plugin 
	 */
	public ArrayList<String> errorsAndWarnings=new ArrayList<String>();

	/**
	 * Parse the model and add the converted data to the bionetwork
	 *
	 * @param model
	 * 	The SBML model
	 * @param bionetwork
	 * The BioNetwork
	 * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
	 */
	void parseModel(Model model, BioNetwork bionetwork) throws Met4jSbmlReaderException;
	
	/**
	 * If this package plugin uses an official SBML package, this method must return the key name of your package
	 * in the HashMap returned by the model getExtensionPackages() method, a custom name otherwise
	 *
	 * @return the name of the plugin
	 */
	String getAssociatedPackageName();
	
	
	/**
	 * This method tests if the package can be used on the specified JSBML Model.
	 * If the package is dependent on a SBML package, it should test if this package's namespace is present
	 * in the model element by using the model.isPackageURIEnabled() method.
	 * <br />Otherwise, custom testing can be performed on the model. This method can also always return true if you want it
	 * to be used in every model conversion
	 *
	 * @param model
	 * the SBML model
	 * @return true if this package plugin can be used on the model, false otherwise
	 */
	boolean isPackageUseableOnModel(Model model);

	
}
