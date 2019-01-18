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
}
