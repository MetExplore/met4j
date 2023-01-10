package fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching;

import java.util.regex.Pattern;

/**
 * A class that runs a harmonization processing on chemical names with substitutions of common patterns among synonyms,
 * in order to create aliases on which classical fuzzy matching can be run efficiently.
 */
public class ChemicalAliasCreator extends NamePreProcessing{


    /**
     * Create a ChemicalAliasCreator from an array of substitution regex
     * @param pattern
     */
    public ChemicalAliasCreator(SubstitutionPattern... pattern){
        super(true);
        for(SubstitutionPattern regex : pattern){
            addSubstitution(regex.find,regex.replace);
        }
    }

    /**
     * List of default substitution patterns, frequently found among chemical name synonyms
     */
    public enum SubstitutionPattern {
        cisabbrv("cis[^a-z]", "z"), //cis abbreviation
        transabbrv("trans[^a-z]", "e"), //trans abbreviation
        neutral("ic[_\\-\\s]*acid","ate"), //acid form
        ion("inium","ine"), //ion form
        coenzabbrv("coenzyme", "co"), //coenzyme abreviation
        charge("\\(\\d[+-]\\)", ""), //charge
        inbrackets("\\s\\([^\\)]+\\)$", ""), //info
        saccharidescyclic1("ofuranose", "ose"), //Monosaccharides cyclic form
        saccharidescyclic2("opyranose", "ose"), //Monosaccharides cyclic form
        greek1("alpha","α"), //normalize greek letters
        greek2("beta","β"),
        greek3("gamma","γ"),
        greek4("delta","δ"),
        greek5("epsilon","ε"),
        greek6("zeta","ζ"),
        greek7("theta","θ"),
        greek8("iota","ι"),
        greek9("kappa","κ"),
        greek10("lambda","λ"),
        greek11("ksi","ξ"),
        greek12("omicron","ο"),
        greek13("rho","ρ"),
        greek14("sigma","σ"),
        greek15("upsilon","υ"),
        greek16("khi","χ"),
        greek17("omega","ω"),
        specialchar("[^\\da-zA-Zα-ω]", ""); //remove special char

        private final String find;
        private final String replace;

        SubstitutionPattern(String find, String replace) {
            this.find = find;
            this.replace = replace;
        }
    }

    /**
     * substitution patterns for fully spelled greek letter as single letter
     * @return array of SubstitutionPattern
     */
    public static SubstitutionPattern[] greek(){
        SubstitutionPattern[] patternArray = {SubstitutionPattern.greek1,
                SubstitutionPattern.greek2,
                SubstitutionPattern.greek3,
                SubstitutionPattern.greek4,
                SubstitutionPattern.greek5,
                SubstitutionPattern.greek6,
                SubstitutionPattern.greek7,
                SubstitutionPattern.greek8,
                SubstitutionPattern.greek9,
                SubstitutionPattern.greek10,
                SubstitutionPattern.greek11,
                SubstitutionPattern.greek12,
                SubstitutionPattern.greek13,
                SubstitutionPattern.greek14,
                SubstitutionPattern.greek15,
                SubstitutionPattern.greek16,
                SubstitutionPattern.greek17
        };
        return patternArray;
    }

    /**
     * Provide all the defaults substitution patterns for fully spelled greek letter as single letter
     * @return array of SubstitutionPattern
     */
    public static SubstitutionPattern[] all(){
        return SubstitutionPattern.class.getEnumConstants();
    }

}
