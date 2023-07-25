package fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.EigenVectorCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.PathBasedCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class to aggregate metabolites' centrality into pathway score for Pathway Topology Analysis
 */
public class PathwayTopologyAnalysis {

    private Map<BioPathway,Collection<BioMetabolite>> kb;
    private CompoundGraph g;
    private Set<BioMetabolite> data;

    private boolean normalize;

    /**
     * Instantiate class to perform Pathway Topology Analysis
     * @param knowledgeBase Map affiliating metabolites to pathways
     * @param topology CompoundGraph storing metabolites' relationships
     * @param compoundOfInterest Set containing input data (significantly overrepresented metabolites for example)
     */
    public PathwayTopologyAnalysis(Map<BioPathway,Collection<BioMetabolite>> knowledgeBase, CompoundGraph topology, Set<BioMetabolite> compoundOfInterest){
        this.kb=knowledgeBase;
        this.g = topology;
        this.data=compoundOfInterest;
    }

    /**
     * Instantiate class to perform Pathway Topology Analysis
     * @param knowledgeBase BioNetwork affiliating metabolites to pathways
     * @param topology CompoundGraph storing metabolites' relationships
     * @param compoundOfInterest Set containing input data (significantly overrepresented metabolites for example)
     */
    public PathwayTopologyAnalysis(BioNetwork knowledgeBase, CompoundGraph topology, Set<BioMetabolite> compoundOfInterest){
        this.kb=new HashMap<>();
        for(BioPathway p : knowledgeBase.getPathwaysView()){
            kb.put(p,knowledgeBase.getMetabolitesFromPathway(p));
        }
        this.g = topology;
        this.data=compoundOfInterest;
    }

    /**
     * Use normalized score, using the ratio between the raw pathway score and the maximum score by pathway
     * @return a PathwayTopologyAnalysis object
     */
    public PathwayTopologyAnalysis useNormalization(){
        this.normalize=true;
        return this;
    }

    /**
     * Method to compute Pathway Impact from data, according to a given strategy for individual scoring and aggregation
     * @param scoring an IndividualScoringStrategy for scoring metabolites (centrality measure)
     * @param aggregation an AggregationStrategy for aggregating component scores into pathway score.
     * @return a Map of Pathways and their score
     */
    public Map<BioPathway,Double> run(IndividualScoringStrategy scoring, AggregationStrategy aggregation){
        Map<BioPathway,Double> pathwayFinalScore = computePathwayScore(data, g, scoring, aggregation);
        if(normalize){
            //create background data (i.e dataset with all compounds in network)
            Set<BioMetabolite> background = kb.values().stream().flatMap(Collection::stream).filter(v -> g.vertexSet().contains(v))
                    .collect(Collectors.toSet());

            //compute pathway score as if all their compounds were in dataset (recompute individual score for all compounds)
            Map<BioPathway,Double> byPathwayBackgroundScore = computePathwayScore(background, g, scoring, aggregation);
            //set final pathway score as ratio between pathway score computed from data and theoretical maximal pathway score
            pathwayFinalScore = Stream.concat(pathwayFinalScore.entrySet().stream(), byPathwayBackgroundScore.entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (value1, value2) -> value1/value2));
        }
        return pathwayFinalScore;
    }

    /*
    Compute topology pathway analysis
    */
    private Map<BioPathway,Double> computePathwayScore(Set<BioMetabolite> data, CompoundGraph g, IndividualScoringStrategy scoring, AggregationStrategy aggregation){
        //filter kb to keep only mapped pathways
        kb = kb.entrySet().stream().filter(e -> e.getValue().stream().anyMatch(data::contains)).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));
        //From compounds and given interaction network, compute topology score for each compound, using scoring strategy.
        Map<BioMetabolite,Double> individualScore = scoring.apply(data,g);
        //From knowledge base, get the pathway memberships and collect component's scores.
        Map<BioPathway,Collection<Double>> pathwayScores = individualScoresByPathway(individualScore);
        //Using aggregation strategy, compute for each pathway its final score from its constituents ones.
        Map<BioPathway,Double> pathwayFinalScore = aggregation.apply(pathwayScores);
        return pathwayFinalScore;
    }

    /*
    From associated compound given by knowledge base, retrieve list of components' scores for each pathway
     */
    private Map<BioPathway,Collection<Double>> individualScoresByPathway(Map<BioMetabolite,Double> individualScore){
        HashMap<BioPathway,Collection<Double>> pathwayScores = new HashMap<>();
        for(Map.Entry<BioPathway,Collection<BioMetabolite>> pathwayEntry : kb.entrySet()){
            List<Double> componentsScore = pathwayEntry.getValue().stream()
                    .filter(individualScore::containsKey)
                    .map(individualScore::get)
                    .collect(Collectors.toList());
            pathwayScores.put(pathwayEntry.getKey(),componentsScore);
        }
        return pathwayScores;
    }

    /**
     * Interface for individual scoring strategy, computing metabolites impact
     */
    public interface IndividualScoringStrategy extends BiFunction<Set<BioMetabolite>,CompoundGraph,Map<BioMetabolite,Double>> {

        /**
         * Use betweenness as scoring function, i.e. the proportion of shortest paths passing through a given node (excluding paths where it is the starting or ending node).
         * @return a map of compounds and their respective impact score.
         */
        static IndividualScoringStrategy betweenness(){
            return  (Set<BioMetabolite> data,CompoundGraph graph) -> {
            PathBasedCentrality<BioMetabolite, ReactionEdge,CompoundGraph> centralityAnalyser = new PathBasedCentrality<>(graph);
            Map<BioMetabolite, Integer> betweenness = centralityAnalyser.getGeodesicBetweenness();
            return betweenness.entrySet().stream()
                    .filter(e -> data.contains(e.getKey()))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> Double.valueOf(betweenness.get(e.getKey()))));
            };
        }

        /**
         * Use PageRank as scoring function, a centrality measure that represents the likelihood that a random walk reach a particular node.
         * This is run with default damping factor 0.85, using power iteration approximation with 15000 max iterations and 0.001 tolerance for convergence
         * @return a map of compounds and their respective impact score.
         */
        static IndividualScoringStrategy pageRank(){
            return IndividualScoringStrategy.pageRank(0.85,15000,0.001);
        }

        /**
         * Use PageRank as scoring function, a centrality measure that represents the likelihood that a random walk reach a particular node
         * @param dampingFactor damping factor
         * @param maxIter maximal number of iteration of the power method
         * @param tolerance convergence tolerance
         * @return a map of compounds and their respective impact score.
         */
        static IndividualScoringStrategy pageRank(Double dampingFactor, int maxIter, double tolerance){
            return  (Set<BioMetabolite> data,CompoundGraph graph) -> {
                EigenVectorCentrality<BioMetabolite, ReactionEdge,CompoundGraph> centralityAnalyser = new EigenVectorCentrality<>(graph);
                centralityAnalyser.addJumpProb(graph.vertexSet().stream()
                        .map(BioMetabolite::getId).collect(Collectors.toSet()), dampingFactor);
                Map<String, Double> pageRank = centralityAnalyser.computePowerMethodPageRank(dampingFactor,maxIter,tolerance);
                return pageRank.entrySet().stream()
                        .filter(e -> data.stream().map(BioMetabolite::getId).collect(Collectors.toSet()).contains(e.getKey()))
                        .collect(Collectors.toMap(e -> graph.getVertex(e.getKey()), e -> e.getValue()));
            };
        }

        /**
         * Use out degree as scoring function, i.e. the number of outgoing edges of a node.
         * @return a map of compounds and their respective impact score.
         */
        static IndividualScoringStrategy outDegree(){
            return  (Set<BioMetabolite> data,CompoundGraph graph) -> data.stream()
                    .collect(Collectors.toMap(v -> v, v -> Double.valueOf(graph.outDegreeOf(v))));
        }

        /**
         * Use number of neighbors as scoring function. Contrary to degree, this is not impacted by parallel edges
         * (same pairs of nodes connected by different edges corresponding to different reactions)
         * @return a map of compounds and their respective impact score.
         */
        static IndividualScoringStrategy neighbors(){
            return  (Set<BioMetabolite> data,CompoundGraph graph) -> data.stream()
                    .collect(Collectors.toMap(v -> v, v -> Double.valueOf(graph.neighborListOf(v).size())));
        }

    }

    /**
     * Interface for aggregation strategy, computing pathway impact from constituting compounds' impact
     */
    public interface AggregationStrategy extends Function<Map<BioPathway,Collection<Double>>,Map<BioPathway,Double>> {

        /**
         * Simply count the sum of compounds of interest scores as the final pathway score
         * @return
         */
        static AggregationStrategy rawSum(){
            return (Map<BioPathway,Collection<Double>> pathwayScores) ->
            {
                Map<BioPathway,Double> pathwayFinalScore = new HashMap<>();
                for(Map.Entry<BioPathway,Collection<Double>> e : pathwayScores.entrySet()){
                    Double finalScoring = 0.0;
                    for(Double score : e.getValue()){
                        finalScoring+=score;
                    }
                    pathwayFinalScore.put(e.getKey(),finalScoring);
                }
                return pathwayFinalScore;
            };
        }
    }
}
