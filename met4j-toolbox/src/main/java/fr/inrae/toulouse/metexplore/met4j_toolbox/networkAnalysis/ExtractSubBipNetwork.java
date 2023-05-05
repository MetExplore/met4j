package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.KShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.SteinerTreeApprox;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class ExtractSubBipNetwork extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-s", usage = "input sources txt file", required = true)
    public String sourcePath = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-t", usage = "input targets txt file", required = true)
    public String targetPath = null;

    @Option(name = "-u", aliases = {"--undirected"}, usage = "Ignore reaction direction")
    public Boolean undirected = false;

    @Option(name = "-tab", aliases = {"--asTable"}, usage = "Export in tabulated file instead of .GML")
    public Boolean asTable = false;

    @Format(name = Gml)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output gml file", required = true)
    public String outputPath = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-sc", aliases = {"--side"}, usage = "a file containing list of side compounds to ignore", required = true)
    public String sideCompoundFile = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-br", aliases = {"--blokedReactions"}, usage = "a file containing list of blocked reactions to ignore")
    public String blkdReactionFile = null;

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

        //Graph processing: import side compounds
        System.err.println("importing side compounds...");
        Mapper<BioMetabolite> cmapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();
        BioCollection<BioMetabolite> sideCpds = null;

        try {
            sideCpds = cmapper.map(sideCompoundFile);
        } catch (IOException e) {
            System.err.println("Error while reading the side compound file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (cmapper.getNumberOfSkippedEntries() > 0)
            System.err.println(cmapper.getNumberOfSkippedEntries() + " side compounds not found in network.");

        //Graph processing: import blocked reactions
        BioCollection<BioReaction> blkdReactions = null;
        if(blkdReactionFile!=null){
            System.err.println("importing blocked reactions...");
            Mapper<BioReaction> rmapper = new Mapper<>(network, BioNetwork::getReactionsView).skipIfNotFound();

            try {
                blkdReactions = rmapper.map(blkdReactionFile);
            } catch (IOException e) {
                System.err.println("Error while reading the blocked reaction file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (rmapper.getNumberOfSkippedEntries() > 0)
                System.err.println(rmapper.getNumberOfSkippedEntries() + " blocked reactions not found in network.");
        }

        //get sources and targets
        System.err.println("extracting sources and targets");
        BioCollection<BioEntity> entities = new BioCollection<>();
        entities.addAll(network.getReactionsView());
        entities.addAll(network.getMetabolitesView());
        Mapper<BioEntity> mapper = new Mapper<>(network, (n -> entities)).skipIfNotFound();
        HashSet<BioEntity> sources = null;
        try {
            sources = new HashSet<>(mapper.map(sourcePath));
        } catch (IOException e) {
            System.err.println("Error while reading the source metabolite file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (mapper.getNumberOfSkippedEntries() > 0)
            System.err.println(mapper.getNumberOfSkippedEntries() + " source not found in network.");

        HashSet<BioEntity> targets = null;
        try {
            targets = new HashSet<>(mapper.map(targetPath));
        } catch (IOException e) {
            System.err.println("Error while reading the target metabolite file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (mapper.getNumberOfSkippedEntries() > 0)
            System.err.println(mapper.getNumberOfSkippedEntries() + " target not found in network.");

        //Create reaction graph
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        BipartiteGraph graph = builder.getBipartiteGraph();
        boolean removed = graph.removeAllVertices(sideCpds);
        if (removed) System.err.println(sideCpds.size() + " side compounds removed.");
        if(blkdReactionFile!=null){
            removed = graph.removeAllVertices(blkdReactions);
            if (removed) System.err.println(blkdReactions.size() + " blocked reaction removed.");
        }
        //Graph processing: set weights [optional]
        WeightingPolicy<BioEntity, BipartiteEdge, BipartiteGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            wp = new WeightsFromFile(weightFile, true);
        }
        wp.setWeight(graph);

        //extract sub-network
        GraphFactory<BioEntity, BipartiteEdge, BipartiteGraph> factory = new GraphFactory<>() {
            @Override
            public BipartiteGraph createGraph() {
                return new BipartiteGraph();
            }
        };
        BipartiteGraph subnet;
        if (st) {
            SteinerTreeApprox<BioEntity, BipartiteEdge, BipartiteGraph> stComp = new SteinerTreeApprox<>(graph, (weightFile != null), !undirected);
            List<BipartiteEdge> stEdges = stComp.getMetricClosureGraphMST(sources, targets, (weightFile != null));
            subnet = factory.createGraphFromEdgeList(stEdges);
        } else if (k > 1) {
            KShortestPath<BioEntity, BipartiteEdge, BipartiteGraph> kspComp = new KShortestPath<>(graph, !undirected);
            List<BioPath<BioEntity, BipartiteEdge>> kspPath = kspComp.getKShortestPathsUnionList(sources, targets, k);
            subnet = factory.createGraphFromPathList(kspPath);
        } else {
            ShortestPath<BioEntity, BipartiteEdge, BipartiteGraph> spComp = new ShortestPath<>(graph, !undirected);
            List<BioPath<BioEntity, BipartiteEdge>> spPath = spComp.getShortestPathsUnionList(sources, targets);
            subnet = factory.createGraphFromPathList(spPath);
        }

        //export sub-network
        if(asTable){
            ExportGraph.toTab(subnet, outputPath);
        }else{
            ExportGraph.toGml(subnet, outputPath);
        }

    }

    public static void main(String[] args) {
        ExtractSubBipNetwork app = new ExtractSubBipNetwork();
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
                "The subnetwork corresponds to part of the network that connects reactions and compounds from the first list to reactions and compounds from the second list.\n" +
                "Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, " +
                "or the Steiner tree connecting them. Contrary to compound graph, bipartite graph often lacks weighting policy for edge relevance. In order to ensure appropriate " +
                "network density, a list of side compounds and blocked reactions to ignore during path build must be provided. An optional edge weight file, if available, can also be used.";
    }

    @Override
    public String getShortDescription() {
        return "Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds and/or reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.";
    }
}