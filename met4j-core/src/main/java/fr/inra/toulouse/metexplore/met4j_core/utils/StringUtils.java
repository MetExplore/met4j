/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_core.utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.util.XMLChar;



public class StringUtils {
	
	public static boolean isVoid (String in) {
		if(in == null) return true;
		if(in.isEmpty()) return true;
		if(in.equalsIgnoreCase("NA")) return true;
		if(in.matches("^\\s*$")) return true;
		if(in.equalsIgnoreCase("null")) return true;
		return false;
	}
	
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
	
	public static void main(String[] args) {
		
//		String in = "<i>cis</i>-zeatin & biosynthesis &lt; 1 &gt; 1; \" ,";
//		
//		System.err.println(htmlEncode(in));
//		
//		String in2 = "2-CYSTEIN rED 4";
//		
//		System.err.println(sbmlEncode(in2));
//		
//		String in = "0.5d0";
//		String in2 = ".5";
//		String in3 = "d1.234f05";
//		
//		System.out.println(transformStoi(in));
//		System.out.println(transformStoi(in2));
//		System.out.println(transformStoi(in3));
		
		String in = "S119__R__4__phosphopantothenoyl__45__L_cysteine";
		System.out.println(in);
		System.out.println(sbmlDecode(in));
		
		in = "ribulose__32__phosphate__32__3__45__epimerase";
		System.out.println(in);
		System.out.println(sbmlDecode(in));
		
		in = "_1__46__2__46__1__46__45__45__RXN";
		System.out.println(in);
		System.out.println(sbmlDecode(in));
		
		in = "&lt;i&gt;N&lt;/i&gt;-acetyl-L-ornithine";
		System.out.println(in);
		System.out.println(removeHtmlMarks(in));
		
		in = "mdash;amp;prime;#039;&lt;i&gt;N&lt;/i&gt;__45__acetyl__45__L__45__&amp;truc;ornithine";
		System.out.println(in);
		System.out.println(getNotFormattedString(in));
		
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
        
        REGEX = "<[^>]*>";
        
        pattern = Pattern.compile(REGEX);
        matcher = pattern.matcher(id2);
        
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
	
}
