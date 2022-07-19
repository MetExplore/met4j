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
package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.EigenVectorCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.*;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.GraphFilter;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.RankUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Tsv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

/**
 * @author clement
 */
public class MetaboRank extends AbstractMet4jApplication {

    //arguments
    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file: path to network used for computing centrality, in sbml format.", required = true)
    public String sbmlFilePath;

    @Format(name = Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-s", usage = "input seeds file: tabulated file containing node of interest ids and weight", required = true)
    public String seedsFilePath;

    @Format(name = Tsv)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output file: path to the file where the results will be exported", required = true)
    public String output;

    //parameters
    @Format(name = Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-w", usage = "input edge weight file: (recommended) path to file containing edges' weights. Will be normalized as transition probabilities")
    public String edgeWeightsFilePaths;

    @Option(name = "-max", usage = "maximal number of iteration")
    public int maxNbOfIter = 15000;

    @Option(name = "-t", usage = "convergence tolerance")
    public double tolerance = 0.001;

    @Option(name = "-d", usage = "damping factor")
    public double dampingFactor = 0.85;

    //variables
    CompoundGraph firstGraph;
    CompoundGraph reverseGraph;
    HashMap<String, Double> seeds;

    //intermediate results holder
    HashMap<String, Double> pageRankScore;
    HashMap<String, Double> cheiRankScore;
    HashMap<String, Integer> pageRank;
    HashMap<String, Integer> cheiRank;

    HashMap<String, Double> globalPageRankScore;
    HashMap<String, Double> globalCheiRankScore;
    HashMap<String, Integer> globalPageRank;
    HashMap<String, Integer> globalCheiRank;

    //results holder
    private HashMap<String, Double> globalVsPersonalizedPageRank;
    private HashMap<String, Double> globalVsPersonalizedCheiRank;

    public static void main(String[] args) {
        MetaboRank app = new MetaboRank();
        app.parseArguments(args);
        app.run();
    }

    public void run() {

        BioNetwork model = null;
        try {
            model = importModel(sbmlFilePath);
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        createCompoundGraph(model);
        setEdgeWeights(firstGraph, edgeWeightsFilePaths);

        createEdgeReversedGraph();

        turnWeightsIntoProba(firstGraph);
        System.err.println("transition probabilities computed");
        turnWeightsIntoProba(reverseGraph);
        System.err.println("transition probabilities computed (reverse graph)");

        importSeeds(seedsFilePath);
        if(seeds.isEmpty()){
            System.err.println("no seed available, computation aborted");
        }else{
            compute();
            printCompoundTable(output);
        }
        System.err.println("done.");

    }

    /*
     * CREATE MODEL FROM SBML FILE
     * use default parameters for attributes value extraction from notes.
     *
     */
    private BioNetwork importModel(String sbmlFilePath) throws Met4jSbmlReaderException {
        JsbmlReader reader = new JsbmlReader(sbmlFilePath);
        BioNetwork model = reader.read();
        System.err.println("model imported.");
        return model;
    }


    /*
     * CREATE COMPOUND GRAPH
     *
     * create a compound-graph object from a model : each reaction is split in transition edges
     *	 example:		reaction in bn:			A + B -> C + D
     *	 edges in cpd-graph:		A -> C ; A -> D ; B -> C ; B -> D
     *
     */
    private void createCompoundGraph(BioNetwork model) {
        firstGraph = new Bionetwork2BioGraph(model).getCompoundGraph();
        System.err.println("compound graph created.");
    }


    /*
     *  CREATE EDGE REVERSED GRAPH
     *
     *  create a graph g' from this graph g where for each edge e(x,y) in g their exist an edge e'(y,x) in g'
     *  This one will be used for chei rank computation.
     *  /!\ Probabilities have to be re-computed otherwise outgoing weights won't sum up to 1
     */
    private void createEdgeReversedGraph() {
        GraphFactory<BioMetabolite, ReactionEdge, CompoundGraph> factory = new GraphFactory<>() {
            @Override
            public CompoundGraph createGraph() {
                return new CompoundGraph();
            }
        };
        reverseGraph = factory.reverse(firstGraph);
        System.err.println("reverse graph created.");
    }


    /*	COMPUTE WEIGHTS
     *
     *  add a weight for each edges, according to a selected criteria (chemical similarity, atom conservation, target's degree ...)
     *	this weight will be used for probability computation
     *
     *  using chemical similarity :
     *
     *		- for each compound convert chemical structure information to bit-vector fingerprint
     *				each bit represent a structural sub-structure, the value if the sub-structure is present in the molecule
     *				fingerprint type available : MACCS, KlekotaRoth, ExtendedFingerprint (default), PubchemFingerprint, EStateFingerprint...
     *		- for each edge compute the fingerprint similarity between source and target (using Tanimoto coef)
     *		- nodes without known chemical structure are removed
     *
     *	using weights from file :
     *		- import weights from tab separated file using the following format:
     *			source-node-id	target-node-id	reaction-id	weight-as-double
     *		- remove edges with weight equals to 0 or NaN
     *		- remove resulting isolated nodes
     *
     */
    public void setSimilarityWeights(CompoundGraph graph) {
        SimilarityWeightPolicy wp = new SimilarityWeightPolicy(FingerprintBuilder.EXTENDED, false, false);
        wp.setWeight(graph);
        WeightUtils.removeEdgeWithNaNWeight(graph);
        wp.noStructFilter(graph);
        System.err.println("weights computed.");
    }

    public void setEdgeWeights(CompoundGraph graph, String localFilePath) {
        Boolean defaultWeight = (localFilePath==null || localFilePath.isEmpty() || localFilePath.isBlank());
        //import weights from file
        WeightingPolicy wp = (defaultWeight) ? new DefaultWeightPolicy() : new WeightsFromFile<>(localFilePath, true);
        //set weights to edges
        wp.setWeight(graph);
        if (!defaultWeight) {
            //remove weights below 0.0
            int nb = GraphFilter.weightFilter(graph, 0.0, "<=");
            System.err.println(nb + " edges removed");
            //remove edges without NaN weight
            WeightUtils.removeEdgeWithNaNWeight(graph);
        }
        //remove disconnected nodes
        graph.removeIsolatedNodes();
        System.err.println("weights computed.");
    }


    /*  COMPUTE TRANSITIONS PROBABILITIES
     *
     * 	turn weights into probabilities (the higher is the weight, the higher is the probability)
     * 		- this probability replace the first weighting
     * 		- for a given vertex, the sum of its outgoing edges' weight is 1
     */
    public void turnWeightsIntoProba(CompoundGraph graph) {
        ReactionProbabilityWeight pp = new ReactionProbabilityWeight();
        pp.computeProba(graph);
    }


    /*  IMPORT SEEDS FROM FILE
     *
     *  create the personalized vector, where each position correspond to a vertex.
     *  Seeds entries haves 1/(nb of seeds) values, other nodes haves 0 values.
     *
     */
    public void importSeeds(String seedsFilePath) {
        seeds = new HashMap<>();
        HashMap<String, Double> tmpSeeds = new HashMap<>();
        Double somme = 0.0;
        try {
            BufferedReader file = new BufferedReader(new FileReader(seedsFilePath));
            String line;

            while ((line = file.readLine()) != null) {
                String[] splitLine = line.split("\t");
                String node = splitLine[0];
                if (!firstGraph.hasVertex(node)) {
                    System.err.println(node + " not found in graph!");
                } else {
                    double weight = 0.0;
                    if (splitLine.length > 1)
                        weight = Double.parseDouble(splitLine[1]);

                    tmpSeeds.put(node, weight);
                    somme += weight;
                }
            }
            file.close();

        } catch (IOException e) {
            System.err.println("Error while importing seeds");
            e.printStackTrace();
            System.exit(1);
        }
        if (somme == 0.0) {
            for (String node : tmpSeeds.keySet()) {
                seeds.put(node, 1.0D / tmpSeeds.size());
            }
        } else {
            for (String node : tmpSeeds.keySet()) {
                seeds.put(node, tmpSeeds.get(node) / somme);
            }
        }

        System.err.println("seeds file imported");
        System.err.println(seeds.size() + " seeds");
    }

    public void compute() {

        globalPageRankScore = computeScore(firstGraph, dampingFactor, maxNbOfIter, tolerance);
        globalPageRank = getRankFromScore(globalPageRankScore, null);
        normalizeScore(globalPageRankScore);
        System.err.println("global pageRank computed");

        globalCheiRankScore = computeScore(reverseGraph, dampingFactor, maxNbOfIter, tolerance);
        globalCheiRank = getRankFromScore(globalCheiRankScore, null);
        normalizeScore(globalCheiRankScore);
        System.err.println("global cheiRank computed");

        pageRankScore = computeScore(firstGraph, dampingFactor, seeds, maxNbOfIter, tolerance);
        pageRank = getRankFromScore(pageRankScore, seeds.keySet());
        normalizeScore(pageRankScore);
        System.err.println("pageRank computed");

        cheiRankScore = computeScore(reverseGraph, dampingFactor, seeds, maxNbOfIter, tolerance);
        cheiRank = getRankFromScore(cheiRankScore, seeds.keySet());
        normalizeScore(cheiRankScore);
        System.err.println("cheiRank computed");

        globalVsPersonalizedPageRank = computeGlobalVsPersonalized(globalPageRankScore, pageRankScore);
        globalVsPersonalizedCheiRank = computeGlobalVsPersonalized(globalCheiRankScore, cheiRankScore);

    }

    public HashMap<String, Double> computeScore(CompoundGraph graph, double dampingFactor, int maxNbOfIter, double tolerance) {

        HashMap<String, Double> allNodes = new HashMap<>();
        double probability = 1.0 / (double) graph.vertexSet().size();
        for (BioMetabolite v : graph.vertexSet()) {
            allNodes.put(v.getId(), probability);
        }

        return computeScore(graph, dampingFactor, allNodes, maxNbOfIter, tolerance);
    }


    public HashMap<String, Double> computeScore(CompoundGraph graph, double dampingFactor, HashMap<String, Double> seeds, int maxNbOfIter, double tolerance) {

        EigenVectorCentrality<BioMetabolite, ReactionEdge, CompoundGraph> scoreComputor
                = new EigenVectorCentrality<>(graph);
        scoreComputor.addJumpProb(seeds, 1 - dampingFactor);
        return scoreComputor.powerIteration(seeds, maxNbOfIter, tolerance);
    }


    public HashMap<String, Integer> getRankFromScore(HashMap<String, Double> score, Set<String> seedsToIgnore) {
        HashMap<String, Double> scoreCopy = new HashMap<>(score);
        if (seedsToIgnore != null) {
            for (String seed : seedsToIgnore) {
                scoreCopy.remove(seed);
            }
        }
        return RankUtils.computeRank(scoreCopy);
    }


    public void normalizeScore(HashMap<String, Double> score) {
        double max = 0.0;
        for (double value : score.values()) {
            if (max < value) max = value;
        }

        for (Map.Entry<String, Double> entry : score.entrySet()) {
            score.put(entry.getKey(), entry.getValue() / max);
        }
    }


    public HashMap<String, Double> computeGlobalVsPersonalized(HashMap<String, Double> globalScore, HashMap<String, Double> score) {

        HashMap<String, Double> globalVsPersoRatio = new HashMap<>();

        for (Map.Entry<String, Double> globalEntry : globalScore.entrySet()) {
            String vertex = globalEntry.getKey();
            double globalValue = globalEntry.getValue();
            double persoValue = score.get(vertex);
            globalVsPersoRatio.put(vertex, persoValue / globalValue);
        }

        return globalVsPersoRatio;
    }

    public void printCompoundTable(String output) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(output, true));
            bw.write("Compound ID\tName\tFormula\tHMDB\tSeed\tglobal PageRank\tglobal CheiRank\tPageRank\tCheiRank\tPageRank*\tCheiRank*");
            bw.newLine();
            for (BioMetabolite compound : firstGraph.vertexSet()) {

                boolean isSeed = seeds.containsKey(compound.getId());


                double globalPageRank = globalPageRankScore.get(compound.getId());
                double globalCheiRank = globalCheiRankScore.get(compound.getId());
                double pageRank = pageRankScore.get(compound.getId());
                double cheiRank = cheiRankScore.get(compound.getId());

                //format data
                DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.CEILING);

                bw.write(compound.getId() + "\t");
                bw.write(compound.getName() + "\t");
                bw.write(compound.getChemicalFormula() + "\t");
                if (isSeed) {
                    bw.write("yes\t");
                } else {
                    bw.write("no\t");
                }

                bw.write(df.format(globalPageRank) + "\t");
                bw.write(df.format(globalCheiRank) + "\t");
                bw.write(df.format(pageRank) + "\t");
                bw.write(df.format(cheiRank) + "\t");

                if (!isSeed) {
                    double globalVsPersoPR = globalVsPersonalizedPageRank.get(compound.getId());
                    double globalVsPersoCR = globalVsPersonalizedCheiRank.get(compound.getId());
                    bw.write(df.format(globalVsPersoPR) + "\t");
                    bw.write(df.format(globalVsPersoCR) + "\t");
                } else {
                    bw.write("NA\t");
                    bw.write("NA\t");
                }

                bw.newLine();

            }
            bw.close();
        } catch (IOException e1) {
            System.err.println("Error while writing compound table");
            System.err.println(e1.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "The MetaboRank takes a metabolic network and a list of compounds of interest, and provide a score of relevance for all of the other compounds in the network.\n" +
                "The MetaboRank can, from metabolomics results, be used to fuel a recommender system highlighting interesting compounds to investigate, retrieve missing identification and drive literature mining.\n" +
                "It is a two dimensional centrality computed from personalized PageRank and CheiRank, with special transition probability and normalization to handle the specificities of metabolic networks.\n" +
                "See publication for more information: Frainay et al. MetaboRank: network-based recommendation system to interpret and enrich metabolomics results, Bioinformatics (35-2), https://doi.org/10.1093/bioinformatics/bty577";
    }

    @Override
    public String getShortDescription() {
        return "Compute the MetaboRank, a custom personalized PageRank for metabolic network.";
    }
}