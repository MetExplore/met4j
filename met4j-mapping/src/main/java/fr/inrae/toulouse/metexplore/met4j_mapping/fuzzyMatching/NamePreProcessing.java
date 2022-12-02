package fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A class that runs a harmonization processing on bioentity names with substitutions of common patterns among synonyms,
 * in order to create aliases on which classical fuzzy matching can be run efficiently.
 */
public class NamePreProcessing {

    protected LinkedHashMap<Pattern,String> replacements=new LinkedHashMap<>();
    protected boolean lowercase = true;

    public NamePreProcessing(Boolean lowercase){
        this.lowercase=lowercase;
    }

    /**
     * Create a simple NamePreProcessing from a substitution regex
     * @param find the pattern to substitute in names
     * @param replace the string to replace with (can be empty)
     */
    public NamePreProcessing(String find, String replace) {
        replacements.put(Pattern.compile(find), replace);
    }

    /**
     * Create fuzzy-matching-friendly alias
     * @param name
     * @return an alias
     */
    public String createAlias(String name){
        String str = name;
        if(lowercase) str = str.toLowerCase();
        for(Map.Entry<Pattern,String> entry : replacements.entrySet()){
            str = entry.getKey().matcher(str).replaceAll(entry.getValue());
        }
        return str;
    }

    /**
     * add a substitution regex to the NamePreProcessing processing
     * @param find the pattern to substitute in names
     * @param replace  the string to replace with (can be empty)
     */
    public void addSubstitution(String find, String replace){
        replacements.put(Pattern.compile(find),replace);
    }

}
