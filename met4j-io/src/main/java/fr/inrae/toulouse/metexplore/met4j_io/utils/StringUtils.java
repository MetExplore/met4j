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
package fr.inrae.toulouse.metexplore.met4j_io.utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.util.XMLChar;



public class StringUtils {
	
	public static String htmlEncode(String in) {
		String out;
		
		out = in;
		
		out = forXML(out);
		
		out = out.replaceAll("<[^>]*>", "");
		
		out = out.replaceAll("&lt;", "less_than");
		
		out = out.replaceAll("&gt;", "greater_than");
		
		out = out.replaceAll("&", "&amp;");
		
		out = out.replaceAll("\"", "&quot;");
		
		out = out.replaceAll("'", "&apos;");
		
		return out;
		
	}
	
	public static  String finalDecode(String in)
	{
		String out;
		
		out = in;
		
		out = out.replaceAll("&lt;/SUB&gt;", "");
		
		out = out.replaceAll("&lt;sup&gt;", "");
		
		out = out.replaceAll("&lt;/sup&gt;", "");
		
		out = out.replaceAll("&lt;SUB&gt;", "");
		
		out = out.replaceAll("&amp;beta;", "beta");
		
		out = out.replaceAll("&amp;delta;", "delta");
		
		out = out.replaceAll("&amp;alpha;", "alpha");
		
		out = out.replaceAll("&lt;SUP&gt;", "");
		
		out = out.replaceAll("&lt;/SUP&gt;", "");
		
		
		return out;
	}
	
	public static String htmlDecode(String in) {
		String out;
		
		out = in;
		
		out = out.replaceAll("less_than", "&lt;");
		
		out = out.replaceAll("greater_than", "&gt;");
		
		out = out.replaceAll("&amp;", "&");
		
		out = out.replaceAll("&quot;", "\"");
		
		out = out.replaceAll("&apos;", "'");
		
		return out;
		
	}
	
	public static String forXML(String aText){
	    final StringBuilder result = new StringBuilder();
	    final StringCharacterIterator iterator = new StringCharacterIterator(aText);
	    char character =  iterator.current();
	    while (character != CharacterIterator.DONE ){
	    	if(!XMLChar.isValid(character)) {
	    		result.append("_");
	    	}
	    	
	      if (character == '<') {
	        result.append("&lt;");
	      }
	      else if (character == '>') {
	        result.append("&gt;");
	      }
	      else if (character == '\"') {
	        result.append("&quot;");
	      }
	      else if (character == '\'') {
	        result.append("&#039;");
	      }
	      else if (character == '&') {
	         result.append("&amp;");
	      }
	      else {
	        //the char is not a special one
	        //add it to the result as is
	        result.append(character);
	      }
	      character = iterator.next();
	    }
	    return result.toString();
	  }

	
	public static String sbmlEncode(String in) {
		
		String out;
		
		out = htmlEncode(in);
		
		String REGEX = "^\\d";
		
		Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(out);
        
        if(matcher.find()) {
			out = "_".concat(out);
		}
        
        REGEX = "[^0-9A-Za-z_]";
        
        pattern = Pattern.compile(REGEX);
        matcher = pattern.matcher(out);
        
        while(matcher.find()) {
        	String specialCharacter = matcher.group(0);
        	Integer value = specialCharacter.codePointAt(0);
        	String code = "__"+value+"__";
        	
        	out = out.replace(specialCharacter,code);
        }
		
		return out;
	}
	
	public static String sbmlDecode(String in) {
		
		in =  htmlDecode(in);
		
		String out=in;
		
		String REGEX = "__(\\d+)__";
		
		Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(in);
        
        while(matcher.find()) {
        	
        	String str = matcher.group(1);
        	
//        	ArrayList<Integer> codes = new ArrayList<Integer>();
//        	
//        	codes.add(new Integer(str));
        	
        	int[] codesInt = new int []{new Integer(str)};
        	
        	String specialCharacter = new String(codesInt,0, codesInt.length);
        	
        	if(validString(specialCharacter)) {
        		out = out.replace("__"+str+"__", specialCharacter);
        	}
//        	else {
//        		out = out.replace("__"+str+"__", specialCharacter);
//        	}
        	
//        	matcher = pattern.matcher(out);
        	
        }
        
        REGEX = "^_(\\d*).*";
        
        pattern = Pattern.compile(REGEX);
        matcher = pattern.matcher(out);
        
        if(matcher.find()) {
        	String str = matcher.group(1);   	
        	out = out.replaceFirst("^_"+str, str);
        }
        
        return out;
	}
	
	
	/**
	 * Transforms a stoechiometric coefficient to be compatible with the
	 * SBML annotations
	 */
	
	public static String transformStoi (String st) {
		
		if(st == null) {
			return "1";
		}
		
		
		if(st.matches("^\\d$"))
				return st;
		
		String REGEX = "[^\\d]*(\\d*\\.\\d+)[^\\d]*.*";
		
		Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(st);
		
        if(matcher.find()) {
        	String out = matcher.group(1);
        	return out;
        }
        else {
        	return "1";
        }
	}
	
	
	/**
	 * 
	 * Join array elements with a string
	 * 
	 * @param ary : the array of strings  to implode 
	 * @param delim : 
	 * @return  Returns a string containing a string representation
	 *  of all the array elements in the same order, with the delim string between each element. 
	 */
	public static String implode(String[] ary, String delim) {
	    String out = "";
	    for(int i=0; i<ary.length; i++) {
	        if(i!=0) { out += delim; }
	        out += ary[i];
	    }
	    return out;
	}
	
	
	/**
	 * Return true if str does not contain any illegal character
	 * false otherwise
	 * @param str
	 * @return
	 */
	public static Boolean validString(String str) {
		boolean valid = true;  
		
		char strArray[] = str.toCharArray();
		
		for (char e : strArray)  
		{  
		   if (!XMLChar.isValid(e))  
		   {  
		      valid = false;  
		   }  
		}  
		   
		return valid;
	}
	
	/**
	 * remove _IN_NIL
	 * @param id
	 * @return
	 */
	public static String removeDefaultBioCycSuffix(String id)
	{
		if(id.contains("_IN_NIL"))
			id=id.replace("_IN_NIL", "");
		return id;
	}
	
	/**
	 * Remove
	 * - every thing between &lt; and &gt; (included) 
	 * - &something; 
	 * - #something;
	 * - amp;
	 * - prime;
	 * - mdash;
	 * - ;
	 * @param id
	 * @return
	 */
	public static String removeHtmlMarks(String id)
	{
		
		String id2 = id;
		
		String REGEX = "&lt;[^\"&gt;\"]*&gt;";
//		String REGEX = "&[^;]*;[^&]*&[^;]*;";
		
		Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(id2);
        
        while(matcher.find()) {
        	
        	String str = matcher.group();
        	
        	id2 = id2.replace(str, "");
        	
        	matcher = pattern.matcher(id2);
        	
        }
        
        
        REGEX = "&[^;]*;";
        
        pattern = Pattern.compile(REGEX);
        matcher = pattern.matcher(id2);
        
        while(matcher.find()) {
        	
        	String str = matcher.group();
        	
        	id2 = id2.replace(str, "");
        	
        	matcher = pattern.matcher(id2);
        	
        }
        
        
        REGEX = "#[^;]*;";
        
        pattern = Pattern.compile(REGEX);
        matcher = pattern.matcher(id2);
        
        while(matcher.find()) {
        	
        	String str = matcher.group();
        	
        	id2 = id2.replace(str, "");
        	
        	matcher = pattern.matcher(id2);
        	
        }
        
        id2 = id2.replaceAll("amp;", "");
        id2 = id2.replaceAll("prime;", "");
        id2 = id2.replaceAll("mdash;", "");
        id2 = id2.replaceAll(";", "");
        
        
        
		return id2;
	}
	
	/**
	 * Remove any SBML or HTML marks or transformation
	 * @param id
	 * @return
	 */
	
	public static String getNotFormattedString(String id) {
		
		String id2 = id;
		
		id2 = sbmlDecode(id2);
		id2 = removeHtmlMarks(id2);
		
		return id2;
		
	}
	
	/**
	 * Returns the length of the longest substring of the string f in the string se
	 * @param first
	 * @param second
	 * @return
	 */
	public static int longestSubstr(String f, String s) {
		
		String first = f.toLowerCase();
		String second = s.toLowerCase();
		
	    if (first == null || second == null || first.length() == 0 || second.length() == 0) {
	        return 0;
	    }
	 
	    int maxLen = 0;
	    int fl = first.length();
	    int sl = second.length();
	    int[][] table = new int[fl][sl];
	 
	    for (int i = 0; i < fl; i++) {
	        for (int j = 0; j < sl; j++) {
	            if (first.charAt(i) == second.charAt(j)) {
	                if (i == 0 || j == 0) {
	                    table[i][j] = 1;
	                }
	                else {
	                    table[i][j] = table[i - 1][j - 1] + 1;
	                }
	                if (table[i][j] > maxLen) {
	                    maxLen = table[i][j];
	                }
	            }
	        }
	    }
	    return maxLen;
	}
	
	/**
	 * Returns the score of the longest common subsequence between two strings
	 * i.e the max between the length(lcs)/length(first) and length(lcs)/length(first)
	 * @param first
	 * @param second
	 * @return
	 */
	public static double scoreLcs(String first, String second) {
		
		double score = 0.0;
		
		double lengthLCS = longestSubstr(first, second);
		double lengthFirst = first.length();
		double lengthSecond = second.length();
		
		double s1 = lengthLCS/lengthFirst*100;
		double s2 = lengthLCS/lengthSecond*100;
		
		if(s1>s2) {
			score = s1;
		}
		else {
			score = s2;
		}
		
		return score;
		
	}
	
	
	/**
	 * Returns the longest common substring between s1 and s2
	 * @param S1
	 * @param S2
	 * @return
	 */
	public static String longestCommonSubstring(String S1, String S2)
	{
	    int Start = 0;
	    int Max = 0;
	    for (int i = 0; i < S1.length(); i++)
	    {
	        for (int j = 0; j < S2.length(); j++)
	        {
	            int x = 0;
	            while (S1.charAt(i + x) == S2.charAt(j + x))
	            {
	                x++;
	                if (((i + x) >= S1.length()) || ((j + x) >= S2.length())) break;
	            }
	            if (x > Max)
	            {
	                Max = x;
	                Start = i;
	            }
	         }
	    }
	    return S1.substring(Start, (Start + Max));
	}
	
	
	
	/**
	 * Escaping special characters in text for regular expresions
	 */
	static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");


	public static String escapeSpecialRegexChars(String str) {

		return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
	}
	
	/**
	 * Find the closing parenthesis in a text given the position of the matching
	 * opening one
	 * 
	 * @param text
	 *            The text
	 * @param openPos
	 *            the position of the opening parenthesis
	 * @return the position of the matching closing parenthesis
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the matching closing parenthesis is not found
	 */
	public static int findClosingParen(char[] text, int openPos)
			throws ArrayIndexOutOfBoundsException {
		int closePos = openPos;
		int counter = 1;
		while (counter > 0) {
			char c = text[++closePos];
			if (c == '(') {
				counter++;
			} else if (c == ')') {
				counter--;
			}
		}
		return closePos;
	}

	/**
	 * Find the opening parenthesis in a text given the position of the matching
	 * closing one
	 * 
	 * @param text
	 *            The text
	 * @param closePos
	 *            the position of the closing parenthesis
	 * @return the position of the matching opening parenthesis
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the matching opening parenthesis is not found
	 */
	public static int findOpenParen(char[] text, int closePos) {
		int openPos = closePos;
		int counter = 1;
		while (counter > 0) {
			char c = text[--openPos];
			if (c == '(') {
				counter--;
			} else if (c == ')') {
				counter++;
			}
		}
		return openPos;
	}
	

	/**
	 * Add to list1 all elements of list2 that are not empty or only space
	 * characters
	 * 
	 * @param list1
	 * @param list2
	 */
	public static void addAllNonEmpty(ArrayList<String> list1,
			List<String> list2) {
		for (String s : list2) {
			if (!s.replaceAll(" ", "").isEmpty()) {
				list1.add(s);
			}
		}

	}
	
	/**
	 * Convert an ID to a valid SBML SID
	 * 
	 * @param id
	 *            the Id to convert to SID
	 * @return the converted SID
	 */
	public static String convertToSID(String id) {

		Matcher matcher = Pattern.compile("[^0-9A-Za-z_]").matcher(id);

		while (matcher.find()) {
			id = id.replaceAll(
					StringUtils.escapeSpecialRegexChars(matcher.group(0)), "_");
		}

		if (Pattern.compile("^[0-9].*").matcher(id).matches()) {
			id = "_" + id;
		}

		return id;

	}
	
	/**
	 * return true if a sboTerm is valid
	 * sbo:1234567
	 * SBO : 1234567
	 * 1234567
	 * @param sboTerm
	 * @return
	 */
	public static Boolean isValidSboTerm(String sboTerm) {
		Pattern p = Pattern.compile("^SBO\\s*:\\s*\\d{7}$", Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile("^\\d{7}$");

		Matcher matcher = p.matcher(sboTerm);
		Matcher matcher2 = p2.matcher(sboTerm);

		return matcher.find() || matcher2.find();
	}

	/**
	 * Format a reaction id to the format R_reactionId (Cobra format)
	 * @param id the original id
	 * @return the id cobra formatted
	 */
	public static String formatReactionIdCobra(String id) {

		String out=id;

		if (!id.startsWith("R_")) {
			out = "R_" + id;
		}

		return out;

	}

	/**
	 * Format a metabolite id in the cobra way (M_metaboliteId_compartmentId)
	 * @param metaboliteId the original metabolite id
	 * @param compartmentId  the compartment id
	 * @return the metaboliteId formatted
	 */
	public static String formatMetaboliteIdCobra(String metaboliteId, String compartmentId) {

		String out = metaboliteId;

		if(! metaboliteId.startsWith("M_")) {
			out = "M_"+metaboliteId;
		}

		if(! metaboliteId.endsWith("_"+compartmentId)) {
			out = out+"_"+compartmentId;
		}

		return out;


	}

	
}
