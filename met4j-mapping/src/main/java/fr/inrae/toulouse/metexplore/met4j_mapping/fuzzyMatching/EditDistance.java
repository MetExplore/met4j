package fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching;

import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * Class that compute Edit distance (Levenshtein implementation) between strings, i.e. the number of edits to transform a character string into
 * another, which can be used to manage typo, case and special character variations, and perform auto-completion.
 */
public class EditDistance implements StringSimilarity{

    LevenshteinDistance ld;

    public EditDistance(){
        ld=new LevenshteinDistance();
    }

    /**
     * Provides Levenshtein distance with threshold cap
     * @param threshold
     */
    public EditDistance(int threshold){
        ld=new LevenshteinDistance(threshold);
    }

    @Override
    public Double getSimilarity(String s1, String s2) {
        Integer res = ld.apply(s1,s2);
        return res.doubleValue();
    }

    @Override
    public Boolean asDistance() {
        return true;
    }
}
