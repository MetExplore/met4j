package fr.inrae.toulouse.metexplore.met4j_chemUtils;

import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utility methods to analyse formula's content
 */
public class FormulaParser {

    String formula;
    Pattern inorgaRegex;
    Pattern groupMatch;
    Pattern genericFormula = Pattern.compile("^([\\*\\(\\)A-Z][a-z]*\\d*)+$");;
    List<String> atoms = Arrays.asList("H","B","C","N","O","F","Na","Mg","Al","Si","P","S","Cl","K","Ca","Cr","Mn","Fe","Co","Cu","Zn","Se","Mo","Cd","Sn","I");


    public FormulaParser(String formula){
        if(StringUtils.isVoid(formula) || !genericFormula.matcher(formula).find()) throw new IllegalArgumentException("Unable to parse formula");
        this.formula=formula;
    }

    /**
     * @return true if the formula string contains only known atom type found in living organism.
     */
    public boolean hasValidAtomSymbols(){
        if(hasUndefinedPart()) return false;
        Matcher atomMatch = Pattern.compile("([A-Z][a-z]*)").matcher(formula);
        while (atomMatch.find()){
            String atom = atomMatch.group(1);
            if(!atoms.contains(atom)) return false;
        }
        return true;
    }

    /**
     * @return false if the formula contains R- or *.
     */
    public boolean hasUndefinedPart(){
        if(groupMatch==null) groupMatch = Pattern.compile(".*([RX\\*]-?).*");
        return (groupMatch.matcher(formula).matches());
    }

    /**
     * Detect inorganic compounds. Inorganic compound is an ill-defined concept, sometimes defined as compound lacking C-C or C-H bonds. Since chemical structure is rarely available " +
     * in SBML model beyond chemical formula, we use a less restrictive criterion by flagging compound with one or no carbons. This cover most inorganic compounds, but include few compounds" +
     * such as methane usually considered as organic.
     * @return false if the formula contains a radical or more than one carbon atom.
     */
    public boolean isExpectedInorganic(){
        if(inorgaRegex==null) inorgaRegex = Pattern.compile(".*(R\\-?|\\*|C\\d).*");
        return (!inorgaRegex.matcher(formula).matches());
    }

}
