package fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching;

import java.util.*;

/**
 * Class to perform fuzzy mapping on bioentity names
 */
public class BioEntityNameMatcher {

    private Map<String,String> referenceAlias;
    private NamePreProcessing processing;

    private StringSimilarity algorithm;

    protected BioEntityNameMatcher(Map<String,String> referenceAlias,
                                   NamePreProcessing processing,
                                   StringSimilarity algorithm){
        this.referenceAlias=referenceAlias;
        this.processing=processing;
        this.algorithm=algorithm;
    }

    /**
     * Class builder
     */
    public static class Builder {
        NamePreProcessing processing;
        Collection<String> referenceSet;
        StringSimilarity algorithm=new EditDistance();

        /**
         * set builder to create bioentity name matcher
         * @param referenceSet the collection of names to search in
         */
        public Builder (Collection<String> referenceSet){
            this.referenceSet=referenceSet;
        }

        /**
         * set builder to create bioentity name matcher, using preprocessing
         * @param regexProcessing a NamePreProcessing for names harmonization
         * @return a builder instance
         */
        public Builder customProcessing(NamePreProcessing regexProcessing){
            this.processing=regexProcessing;
            return this;
        }

        /**
         * set builder to create bioentity name matcher, using a custom String similarity algorithm
         * @param algorithm a StringSimilarity object to compute names similarity/distance
         * @return a builder instance
         */
        public Builder customSimilarity(StringSimilarity algorithm){
            this.algorithm=algorithm;
            return this;
        }

        /**
         * create a bioentity name matcher instance
         * @return a bioentity name matcher
         */
        public BioEntityNameMatcher build(){
            Map<String,String> referenceAlias = new HashMap<>();
            for(String name : referenceSet){
                String alias = (processing==null) ? name : processing.createAlias(name);
                referenceAlias.put(name,alias);
            }
            return new BioEntityNameMatcher(referenceAlias,processing,algorithm);
        }
    }

    /**
     * Search for a given name within the set of names given at construction
     * @param query a name to search
     * @return a map with matching names and their score
     */
    public Map<String,Double> search(String query){
        String q = (processing==null) ? query : processing.createAlias(query);
        Map<String,Double> res = new HashMap<>();
        for(Map.Entry<String,String> entry : referenceAlias.entrySet()){
            Double score = algorithm.getSimilarity(q,entry.getValue());
            res.put(entry.getKey(),score);
        }
        return res;
    }

    /**
     * Find names from the set given at construction, that match the queried name
     * @param query the bioentity name to find
     * @param threshold the score threshold to define a hit
     * @param maxRes the maximum number of desired match
     * @return the list of bioentity names that match the query
     */
    public List<String> getMatches(String query, Double threshold, int maxRes){
        Map<String,Double> scores = search(query);
        List<String> matches = new ArrayList<>();
        if(!algorithm.asDistance()){
            scores.entrySet().stream()
                    .filter(e -> e.getValue()>=threshold)
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(e -> matches.add(e.getKey()));
        }else{
            scores.entrySet().stream()
                    .filter(e -> e.getValue()<threshold)
                    .sorted(Map.Entry.comparingByValue())
                    .forEachOrdered(e -> matches.add(e.getKey()));
        }
        if(maxRes>matches.size()) maxRes=matches.size();
        return matches.subList(0,maxRes);
    }

    /**
     * Find the name from the set given at construction, that best match the queried name
     * @param query the bioentity name to find
     * @param threshold the score threshold to define a hit
     * @return
     */
    public String getBestHit(String query, Double threshold){
        List<String> res= getMatches(query,threshold,1);
        if(res.isEmpty()) return null;
        return res.get(0);
    }
}
