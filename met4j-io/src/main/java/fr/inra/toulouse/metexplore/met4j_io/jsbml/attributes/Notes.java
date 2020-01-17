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

package fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;

/**
 * This Class is used to keep track of the notes present in the imported file in
 * the BioNetwork object
 * 
 * @author Benjamin
 * @since 2.0
 */
public class Notes {

	/**
	 * The default String used for an empty notes element
	 */
	private static final String emptyNoteString = "<notes>\n<body xmlns=\"http://www.w3.org/1999/xhtml\">\n"
			+ "</body>\n</notes>";

	/**
	 * The notes are successive xhtml elements used to for user specific data.
	 */
	private String XHTMLasString;

	/**
	 * Default Constructor
	 */
	public Notes() {
		XHTMLasString = emptyNoteString;
	}

	/**
	 * Constructor
	 * 
	 * @param xhtmlasString
	 *            the XML of the Notes
	 */
	public Notes(String xhtmlasString) {
		XHTMLasString = xhtmlasString;
	}

	/**
	 * Add an attribute to the notes. If the key already exists and updateValue
	 * is set to true, updates its value
	 * 
	 * @param key
	 *            the key to add or update
	 * @param value
	 *            the value of the key attribute
	 * @param updateValue
	 *            true to update the value if key already exists
	 * 
	 */
	public void addAttributeToNotes(String key, String value,
			boolean updateValue) {

		String formattedKey = StringUtils.forXML(key.trim());
		String formattedValue = StringUtils.forXML(value.trim());
		
		String existingNotes = this.getXHTMLasString();
		String newNote = existingNotes;

		Pattern p = Pattern.compile(formattedKey, Pattern.CASE_INSENSITIVE
				| Pattern.UNICODE_CASE);

		if (p.matcher(existingNotes).find() && updateValue) {

			String valueRegex = formattedKey + ": ([^<]*)</p>";
			Matcher regexMatcher = Pattern.compile(valueRegex,
					Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(
					existingNotes);
			if (regexMatcher.find())
				newNote = regexMatcher.replaceFirst(formattedKey + ": "
						+ formattedValue + "</p>");

		} else if (!p.matcher(existingNotes).find()) {

			String replacementStrg = "<p>" + formattedKey + ": " + formattedValue
					+ "</p>\n</body>";

			newNote = existingNotes.replace("</body>", replacementStrg);
		}

		this.setXHTMLasString(newNote);

	}

	/**
	 * test if the notes are empty or not
	 * 
	 * @return true if the note are empty or equal to {@link #emptyNoteString}
	 */
	public boolean isEmpty() {
		if (this.getXHTMLasString().replaceAll("\\s", "")
				.equalsIgnoreCase(emptyNoteString.replaceAll("\\s", ""))) {
			return true;
		}
		return false;
	}

	/**
	 * @return the xHTMLasString
	 */
	public String getXHTMLasString() {
		return XHTMLasString;
	}

	/**
	 * @param xHTMLasString
	 *            the xHTMLasString to set
	 */
	private void setXHTMLasString(String xHTMLasString) {
		XHTMLasString = xHTMLasString;
	}

}
