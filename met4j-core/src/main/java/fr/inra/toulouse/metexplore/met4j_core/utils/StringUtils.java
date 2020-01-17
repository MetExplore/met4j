package fr.inra.toulouse.metexplore.met4j_core.utils;

public class StringUtils {

	public static boolean isVoid (String in) {
		if(in == null) return true;
		if(in.isEmpty()) return true;
		if(in.equalsIgnoreCase("NA")) return true;
		if(in.matches("^\\s*$")) return true;
		if(in.equalsIgnoreCase("null")) return true;
		return false;
	}

	/**
	 * From https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java/5439632#5439632
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch(NumberFormatException e) {
			return false;
		} catch(NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public static boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
		} catch(NumberFormatException e) {
			return false;
		} catch(NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

}
