package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.KShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.SteinerTreeApprox;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.*;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class ExtractSubReactionNetwork extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-s", usage = "input sources txt file", required = true)
    public String sourcePath = null;

    @Option(name = "-u", aliases = {"--undirected"}, usage = "Ignore reaction direction")
    public Boolean undirected = false;

    @Option(name = "-tab", aliases = {"--asTable"}, usage = "Export in tabulated file instead of .GML")
    public Boolean asTable = false;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-t", usage = "input targets txt file", required = true)
    public String targetPath = null;

    @Format(name = Gml)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output gml file", required = true)
    public String outputPath = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-sc", aliases = {"--side"}, usage = "a file containing list of side compounds to ignore", required = true)
    public String sideCompoundFile = null;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-re", aliases = {"--rExclude"}, usage = "an optional file containing list of reactions to ignore")
    public String rExclude = null;

    @Format(name = Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for reactions pairs")
    public String weightFile = null;

    @Option(name = "-k", usage = "Extract k-shortest paths", forbids = {"-st"})
    public int k = 1;

    @Option(name = "-st", aliases = {"--steinertree"}, usage = "Extract Steiner Tree", forbids = {"-k"})
    public boolean st = false;


    public void run() {
        //import network
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        BioNetwork network = null;
        try {
            network = reader.read();
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //Instantiate nodes to remove and mappers
        Set<BioEntity> removedNodes = new HashSet<>();
        Mapper<BioMetabolite> metMapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();
        Mapper<BioReaction> rxnMapper = new Mapper<>(network, BioNetwork::getReactionsView).skipIfNotFound();

        //Graph processing: import side compounds
        System.err.println("importing side compounds...");
        BioCollection<BioMetabolite> sideCpds = null;
        try {
            sideCpds = metMapper.map(sideCompoundFile);
        } catch (IOException e) {
            System.err.println("Error while reading the side compound file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (metMapper.getNumberOfSkippedEntries() > 0)
            System.err.println(metMapper.getNumberOfSkippedEntries() + " side compounds not found in network.");
        System.err.println(sideCpds.size() + " side compounds ignored during graph build.");

        //get sources and targets
        System.err.println("extracting sources and targets");
        HashSet<BioReaction> sources = null;
        try {
            sources = new HashSet<>(rxnMapper.map(sourcePath));
        } catch (IOException e) {
            System.err.println("Error while reading the source metabolite file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (rxnMapper.getNumberOfSkippedEntries() > 0)
            System.err.println(rxnMapper.getNumberOfSkippedEntries() + " source not found in network.");
        HashSet<BioReaction> targets = null;
        try {
            targets = new HashSet<>(rxnMapper.map(targetPath));
        } catch (IOException e) {
            System.err.println("Error while reading the target metabolite file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (rxnMapper.getNumberOfSkippedEntries() > 0)
            System.err.println(rxnMapper.getNumberOfSkippedEntries() + " target not found in network.");

        //Create reaction graph
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        ReactionGraph graph = builder.getReactionGraph(sideCpds);

        //Graph processing: reactions removal [optional]
        if (rExclude != null) {
            System.err.println("removing reactions to exclude...");
            BioCollection<BioReaction> rList = null;
            try {
                rList = rxnMapper.map(rExclude);
            } catch (IOException e) {
                System.err.println("Error while reading the reaction to ignore file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            boolean removed = graph.removeAllVertices(rList);
            if(removed) removedNodes.addAll(rList);
            if (rxnMapper.getNumberOfSkippedEntries() > 0)
            System.err.println(rxnMapper.getNumberOfSkippedEntries() + " reactions to exclude not found in network.");
            System.err.println(rList.size() + " reactions ignored during graph build.");
        }

        //Graph processing: set weights [optional]
        WeightingPolicy<BioReaction, CompoundEdge, ReactionGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            wp = new WeightsFromFile(weightFile, true);
        }
        wp.setWeight(graph);

        //extract sub-network
        GraphFactory<BioReaction, CompoundEdge, ReactionGraph> factory = new GraphFactory<>() {
            @Override
            public ReactionGraph createGraph() {
                return new ReactionGraph();
            }
        };
        ReactionGraph subnet;
        if (st) {
            SteinerTreeApprox<BioReaction, CompoundEdge, ReactionGraph> stComp = new SteinerTreeApprox<>(graph, (weightFile != null), !undirected, false);
            List<CompoundEdge> stEdges = stComp.getLightestUnionOfShortestPaths(sources, targets);
            subnet = factory.createGraphFromEdgeList(stEdges);
        } else if (k > 1) {
            KShortestPath<BioReaction, CompoundEdge, ReactionGraph> kspComp = new KShortestPath<>(graph, !undirected);
            List<BioPath<BioReaction, CompoundEdge>> kspPath = kspComp.getKShortestPathsUnionList(sources, targets, k);
            subnet = factory.createGraphFromPathList(kspPath);
        } else {
            ShortestPath<BioReaction, CompoundEdge, ReactionGraph> spComp = new ShortestPath<>(graph, !undirected);
            List<BioPath<BioReaction, CompoundEdge>> spPath = spComp.getShortestPathsUnionList(sources, targets);
            subnet = factory.createGraphFromPathList(spPath);
        }

        //export sub-network
        if(asTable){
            ExportGraph.toTab(subnet, outputPath);
        }else{
            ExportGraph.toGmlWithAttributes(subnet, outputPath);
        }

    }

    public static void main(String[] args)  {
        ExtractSubReactionNetwork app = new ExtractSubReactionNetwork();
        app.parseArguments(args);
        app.run();
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "The subnetwork corresponds to part of the network that connects reactions from the first list to reactions from the second list.\n" +
                "Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, " +
                "or the Steiner tree connecting them. Contrary to compound graph, reaction graph often lacks weighting policy for edge relevance. In order to ensure appropriate " +
                "network density, a list of side compounds to ignore for linking reactions must be provided. An optional edge weight file, if available, can also be used.";
    }

    @Override
    public String getShortDescription() {
        return "Create a subnetwork from a GSMN in SBML format, and two files containing lists of reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
