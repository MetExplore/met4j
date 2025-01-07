package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.KShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.SteinerTreeApprox;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DegreeWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.SimilarityWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.getMetabolitesFromFile;

public class ExtractSubNetwork extends AbstractMet4jApplication {

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

    @Format(name = Gml)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output gml file", required = true)
    public String outputPath = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-sc", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore")
    public String sideCompoundFile = null;

    @Option(name = "-dw", aliases = {"--degreeWeights"}, usage = "penalize traversal of hubs by using degree square weighting", forbids = {"-cw", "-sw"})
    public Boolean degree = false;

    @Format(name = Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for compound pairs", forbids = {"-dw", "-sw"})
    public String weightFile = null;

    @Option(name = "-sw", aliases = {"--chemSimWeights"}, usage = "penalize traversal of non-relevant edges by using chemical similarity weighting", forbids = {"-dw", "-cw"})
    public Boolean chemicalSim = false;

    @Option(name = "-u", aliases = {"--undirected"}, usage = "Ignore reaction direction")
    public Boolean undirected = false;

    @Option(name = "-tab", aliases = {"--asTable"}, usage = "Export in tabulated file instead of .GML")
    public Boolean asTable = false;

    @Option(name = "-k", usage = "Extract k-shortest paths", forbids = {"-st"})
    public int k = 1;

    @Option(name = "-st", aliases = {"--steinertree"}, usage = "Extract Steiner Tree", forbids = {"-k"})
    public boolean st = false;

    public static void main(String[] args) {
        ExtractSubNetwork app = new ExtractSubNetwork();
        app.parseArguments(args);
        app.run();
    }

    public void run() {
        //import network
        BioNetwork network = IOUtils.readSbml(this.inputPath);

        //Create compound graph
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        //Graph processing: side compound removal [optional]
        if (sideCompoundFile != null) {
            System.err.println("removing side compounds...");
            BioCollection<BioMetabolite> sideCpds = getMetabolitesFromFile(sideCompoundFile, network, "side compounds");
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.out.println(sideCpds.size() + " compounds removed.");
        }

        //get sources and targets
        System.err.println("extracting sources and targets");
        NodeMapping<BioMetabolite, ReactionEdge, CompoundGraph> mapper = new NodeMapping<>(graph).throwErrorIfNotFound();
        HashSet<BioMetabolite> sources = null;
        try {
            sources = new HashSet<>(mapper.map(sourcePath));
        } catch (IOException e) {
            System.err.println("Error while reading the source metabolite file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        HashSet<BioMetabolite> targets = null;
        try {
            targets = new HashSet<>(mapper.map(targetPath));
        } catch (IOException e) {
            System.err.println("Error while reading the target metabolite file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //Graph processing: set weights [optional]
        WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            wp = new WeightsFromFile(weightFile, true);
        } else if (degree) {
            int pow = 2;
            wp = new DegreeWeightPolicy(pow);
        } else if (chemicalSim) {
            SimilarityWeightPolicy sp = new SimilarityWeightPolicy(FingerprintBuilder.EXTENDED, false, true);
            sp.noStructFilter(graph);
            wp = sp;
        }
        wp.setWeight(graph);

        //extract sub-network
        GraphFactory<BioMetabolite, ReactionEdge, CompoundGraph> factory = new GraphFactory<>() {
            @Override
            public CompoundGraph createGraph() {
                return new CompoundGraph();
            }
        };
        CompoundGraph subnet;
        if (st) {
            SteinerTreeApprox<BioMetabolite, ReactionEdge, CompoundGraph> stComp = new SteinerTreeApprox<>(graph, (degree || weightFile != null), !undirected, false);
            List<ReactionEdge> stEdges = stComp.getLightestUnionOfShortestPaths(sources, targets);
            subnet = factory.createGraphFromEdgeList(stEdges);
        } else if (k > 1) {
            KShortestPath<BioMetabolite, ReactionEdge, CompoundGraph> kspComp = new KShortestPath<>(graph, !undirected);
            List<BioPath<BioMetabolite, ReactionEdge>> kspPath = kspComp.getKShortestPathsUnionList(sources, targets, k);
            subnet = factory.createGraphFromPathList(kspPath);
        } else {
            ShortestPath<BioMetabolite, ReactionEdge, CompoundGraph> spComp = new ShortestPath<>(graph, !undirected);
            List<BioPath<BioMetabolite, ReactionEdge>> spPath = spComp.getShortestPathsUnionList(sources, targets);
            subnet = factory.createGraphFromPathList(spPath);
        }

        //export sub-network
        if (asTable) {
            ExportGraph.toTab(subnet, outputPath);
        } else {
            ExportGraph.toGmlWithAttributes(subnet, outputPath);
        }

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "The subnetwork corresponds to the part of the network that connects compounds from the first list to compounds from the second list.\n" +
                "Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, " +
                "or the Steiner tree connecting them. The relevance of considered path can be increased by weighting the edges using degree squared, chemical similarity (require InChI or SMILES annotations) or any provided weighting.\n" +
                "\nSee previous works on subnetwork extraction for parameters recommendations.";
    }

    @Override
    public String getShortDescription() {
        return "Create a subnetwork from a metabolic network in SBML format, and two files containing lists of compounds of interests ids, one per row.";
    }

    @Override
    public Set<Doi> getDois() {
        Set<Doi> dois = new HashSet<>();
        dois.add(new Doi("https://doi.org/10.1093/bib/bbv115"));
        dois.add(new Doi("https://doi.org/10.1016/j.biosystems.2011.05.004"));
        dois.add(new Doi("https://doi.org/10.1093/nar/gki437"));
        dois.add(new Doi("https://doi.org/10.1093/bioinformatics/bti116"));
        dois.add(new Doi("https://doi.org/10.1016/j.jmb.2005.09.079"));
        dois.add(new Doi("https://doi.org/10.1093/bioinformatics/btg217"));
        dois.add(new Doi("https://doi.org/10.1093/bioinformatics/btu760"));
        return dois;
    }
}
