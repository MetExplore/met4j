package fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching;

/**
 * Interface for objetcs computing string similarity/distance
 */
public interface StringSimilarity {

    /**
     * return a similarity score from two strings
     * @param s1
     * @param s2
     * @return
     */
    public
    Double getSimilarity(String s1, String s2);

    /**
     * Method to decide ranking order according to the provided score
     * @return whether the method getSimilarity returns a distance or a similarity
     */
    public
    Boolean asDistance();

}
