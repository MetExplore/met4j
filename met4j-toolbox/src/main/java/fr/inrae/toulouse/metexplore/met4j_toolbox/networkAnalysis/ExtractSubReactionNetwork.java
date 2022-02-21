package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.KShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.SteinerTreeApprox;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DefaultWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DegreeWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class ExtractSubReactionNetwork extends AbstractMet4jApplication {

    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;
    @Option(name = "-s", usage = "input sources txt file", required = true)
    public String sourcePath = null;
    @Option(name = "-t", usage = "input targets txt file", required = true)
    public String targetPath = null;
    @Option(name = "-o", usage = "output gml file", required = true)
    public String outputPath = null;


    @Option(name = "-sc", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore")
    public String sideCompoundFile = null;
    
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for reactions pairs")
    public String weightFile = null;

    @Option(name = "-k", usage = "Extract k-shortest paths", forbids = {"-st"})
    public int k = 1;
    @Option(name = "-st", aliases = {"--steinertree"}, usage = "Extract Steiner Tree", forbids = {"-k"})
    public boolean st = false;


    public void run() throws IOException, Met4jSbmlReaderException {
        //import network
        JsbmlReader reader = new JsbmlReader(this.inputPath, false);
        BioNetwork network = reader.read();

        //Graph processing: import side compounds
        System.err.println("importing side compounds...");
        Mapper<BioMetabolite> mapper = new Mapper<>(network,BioNetwork::getMetabolitesView).skipIfNotFound();
        BioCollection<BioMetabolite> sideCpds = mapper.map(sideCompoundFile);
        if(mapper.getNumberOfSkippedEntries()>0) System.err.println(mapper.getNumberOfSkippedEntries() + " side compounds not found in network.");
        System.err.println(sideCpds.size() + " side compounds ignored during graph build.");

        //get sources and targets
        System.err.println("extracting sources and targets");
        Mapper<BioReaction> rmapper = new Mapper<>(network,BioNetwork::getReactionsView).skipIfNotFound();
        HashSet<BioReaction> sources = new HashSet<>(rmapper.map(sourcePath));
        if(rmapper.getNumberOfSkippedEntries()>0) System.err.println(rmapper.getNumberOfSkippedEntries() + " source not found in network.");
        HashSet<BioReaction> targets = new HashSet<>(rmapper.map(targetPath));
        if(rmapper.getNumberOfSkippedEntries()>0) System.err.println(rmapper.getNumberOfSkippedEntries() + " target not found in network.");

        //Create reaction graph
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        ReactionGraph graph = builder.getReactionGraph(sideCpds);

        //Graph processing: set weights [optional]
        WeightingPolicy<BioReaction, CompoundEdge, ReactionGraph> wp = new DefaultWeightPolicy<>();
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
            SteinerTreeApprox<BioReaction, CompoundEdge, ReactionGraph> stComp = new SteinerTreeApprox<>(graph);
            List<CompoundEdge> stEdges = stComp.getSteinerTreeList(sources, targets, (weightFile != null));
            subnet = factory.createGraphFromEdgeList(stEdges);
        } else if (k > 1) {
            KShortestPath<BioReaction, CompoundEdge, ReactionGraph> kspComp = new KShortestPath<>(graph);
            List<BioPath<BioReaction, CompoundEdge>> kspPath = kspComp.getKShortestPathsUnionList(sources, targets, k);
            subnet = factory.createGraphFromPathList(kspPath);
        } else {
            ShortestPath<BioReaction, CompoundEdge, ReactionGraph> spComp = new ShortestPath<>(graph);
            List<BioPath<BioReaction, CompoundEdge>> spPath = spComp.getShortestPathsUnionList(sources, targets);
            subnet = factory.createGraphFromPathList(spPath);
        }

        //export sub-network
        ExportGraph.toGmlWithAttributes(subnet, outputPath);

    }

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {
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
                "network density, a list of side compounds to ignore for linking reactions must be provided";
    }

    @Override
    public String getShortDescription() {
        return "Create a subnetwork from a GSMN in SBML format, and two files containing lists of reactions of interests ids, one per row, plus one file of the same format containing side compounds ids.";
    }
}
