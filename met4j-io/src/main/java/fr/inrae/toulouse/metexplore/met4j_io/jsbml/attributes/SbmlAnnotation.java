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

/*
 * Created on jan. 30 2014
 * B.M
 */

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This Class is used to keep track of the annotations present in the imported
 * file in the BioNetwork object
 *
 * @author Benjamin
 * @since 2.0
 * @version $Id: $Id
 */
public class SbmlAnnotation extends BioEntity {

	/**
	 * The annotations are successive xml elements used to cross reference
	 * biological data.
	 */
	private String xmlAsString;

	/**
	 * Constructor
	 *
	 * @param metaId      the metaID
	 * @param xmlasString the XML string
	 */
	public SbmlAnnotation(String metaId, String xmlasString) {

		super(metaId);

		Pattern p = Pattern.compile("<annotation>.*</annotation>", Pattern.DOTALL);

		Matcher m = p.matcher(xmlasString);

		if (!m.matches()) {
			throw new IllegalArgumentException(
					"The annotation must fit the pattern <annotation>.*</annotation> (" + xmlAsString + ")");
		}

		this.xmlAsString = xmlasString;

	}

	/**
	 * <p>getXMLasString.</p>
	 *
	 * @return the xMLasString
	 */
	public String getXMLasString() {
		return xmlAsString;
	}

}
