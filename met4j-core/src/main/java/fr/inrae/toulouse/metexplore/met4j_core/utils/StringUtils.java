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

package fr.inrae.toulouse.metexplore.met4j_core.utils;

import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>StringUtils class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class StringUtils {

	final private static Pattern patternEC = Pattern.compile("(EC\\s*)*\\d(\\.(\\d{0,3}|-)){0,3}");
	final private static Pattern patternFormula = Pattern.compile("^([\\*\\(\\)A-Z][a-z]*\\d*)+$");

	/**
	 * <p>isVoid.</p>
	 *
	 * @param in a String
	 * @return true if in is null or empty or equals to multiple spaces, NULL, or null
	 */
	public static boolean isVoid (String in) {
		if(in == null) return true;
		if(in.isEmpty()) return true;

		if(in.matches("^\\s*$")) return true;
		return in.equalsIgnoreCase("null");
	}

	/**
	 * <p>isNa</p>
	 *
	 * @param in a String
	 * @return true if in is null or empty or equals to multiple spaces, NULL, or null
	 */
	public static boolean isNa (String in) {
		if(in == null) return false;
		if(in.isEmpty()) return false;

		return in.equalsIgnoreCase("na");
	}

	/**
	 * From https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java/5439632#5439632
	 *
	 * @param s a String
	 * @return true if s corresponds to an integer
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException | NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	/**
	 * From https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java/5439632#5439632
	 *
	 * @param s a String
	 * @return true if s corresponds to a double
	 */
	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
		} catch(NumberFormatException | NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	/**
	 * Check EC number
	 * @param ec a string to test as ec number
	 * @return true if it looks like an ec number (e.g 1.2.3.4, 1.2.3, 1.2, 1)
	 */
	public static  boolean checkEcNumber(@NonNull String ec) {
		Matcher m = patternEC.matcher(ec);
		return m.matches();
	}

	/**
	 * Checks if a metabolite formula is well formatted
	 * @param formula a String to check
	 * @return true if it looks like a chemical formula (e.g. CH3, C, (n)CH2O6)
	 */
	public static boolean checkMetaboliteFormula(String formula) {

		if(formula == null) {
			return false;
		}
		Matcher m = patternFormula.matcher(formula);
		return m.matches();
	}

}
