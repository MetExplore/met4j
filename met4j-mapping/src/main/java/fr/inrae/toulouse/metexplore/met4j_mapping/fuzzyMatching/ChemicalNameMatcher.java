package fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching;

import java.util.*;

/**
 * Class to perform fuzzy mapping on chemical names, add default preprocessing suited for compound matching
 */
public class ChemicalNameMatcher extends BioEntityNameMatcher {


    protected ChemicalNameMatcher(Map<String, String> referenceAlias, NamePreProcessing processing, StringSimilarity algorithm) {
        super(referenceAlias, processing, algorithm);
    }

    /**
     * Class builder
     */
    public static class Builder extends BioEntityNameMatcher.Builder {

        /**
         * set builder to create chemical name matcher
         *
         * @param referenceSet the collection of names to search in
         */
        public Builder(Collection<String> referenceSet) {
            super(referenceSet);
        }

        /**
         * set builder to create chemical name matcher, using defaul preprocessing
         * for names harmonization (See ChemicalAliasCreator methods)
         * @return a builder instance
         */
        public Builder DefaultProcessing(){
            this.processing=new ChemicalAliasCreator(ChemicalAliasCreator.all());
            return this;
        }

        @Override
        public ChemicalNameMatcher build(){
            Map<String,String> referenceAlias = new HashMap<>();
            for(String name : referenceSet){
                String alias = (processing==null) ? name : processing.createAlias(name);
                referenceAlias.put(name,alias);
            }
            return new ChemicalNameMatcher(referenceAlias,processing,algorithm);
        }

    }
}
