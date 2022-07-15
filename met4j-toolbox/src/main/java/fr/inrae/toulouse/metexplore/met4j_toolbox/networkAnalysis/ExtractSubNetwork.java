package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.KShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.SteinerTreeApprox;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DefaultWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DegreeWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.SimilarityWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.*;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

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

        //Create compound graph
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        //Graph processing: side compound removal [optional]
        if (sideCompoundFile != null) {
            System.err.println("removing side compounds...");
            NodeMapping<BioMetabolite, ReactionEdge, CompoundGraph> mapper = new NodeMapping<>(graph).skipIfNotFound();
            BioCollection<BioMetabolite> sideCpds = null;
            try {
                sideCpds = mapper.map(sideCompoundFile);
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.err.println(sideCpds.size() + " compounds removed.");
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
        WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> wp = new DefaultWeightPolicy<>();
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
            SteinerTreeApprox<BioMetabolite, ReactionEdge, CompoundGraph> stComp = new SteinerTreeApprox<>(graph,(degree || weightFile != null), !undirected);
            List<ReactionEdge> stEdges = stComp.getSteinerTreeList(sources, targets, (degree || weightFile != null));
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
        if(asTable){
            ExportGraph.toTab(subnet, outputPath);
        }else{
            ExportGraph.toGmlWithAttributes(subnet, outputPath);
        }

    }

    public static void main(String[] args) {
        ExtractSubNetwork app = new ExtractSubNetwork();
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
                "The subnetwork correspond to part of the network that connects compounds from the first list to compounds from the second list.\n" +
                "Sources and targets list can have elements in common. The connecting part can be defined as the union of shortest or k-shortest paths between sources and targets, " +
                "or the Steiner tree connecting them. The relevance of considered path can be increased by weighting the edges using degree squared, chemical similarity (require InChI or SMILES annotations) or any provided weighting.\n" +
                "\nSee previous works on subnetwork extraction for parameters recommendations:" +
                "Frainay, C., & Jourdan, F. Computational methods to identify metabolic sub-networks based on metabolomic profiles. Bioinformatics 2016;1–14. https://doi.org/10.1093/bib/bbv115\n" +
                "Faust, K., Croes, D., & van Helden, J. Prediction of metabolic pathways from genome-scale metabolic networks. Bio Systems 2011;105(2), 109–121. https://doi.org/10.1016/j.biosystems.2011.05.004\n" +
                "Croes D, Couche F, Wodak SJ, et al. Metabolic PathFinding: inferring relevant pathways in biochemical networks. Nucleic Acids Res 2005;33:W326–30.\n" +
                "Croes D, Couche F, Wodak SJ, et al. Inferring meaningful pathways in weighted metabolic networks. J Mol Biol 2006; 356:222–36.\n" +
                "Rahman SA, Advani P, Schunk R, et al. Metabolic pathway analysis web service (Pathway Hunter Tool at CUBIC). Bioinformatics 2005;21:1189–93.\n" +
                "Pertusi DA, Stine AE, Broadbelt LJ, et al. Efficient searching and annotation of metabolic networks using chemical similarity. Bioinformatics 2014;1–9.\n" +
                "McShan DC, Rao S, Shah I. PathMiner: predicting metabolic pathways by heuristic search. Bioinformatics 2003;19:1692–8.\n";
    }

    @Override
    public String getShortDescription() {
        return "Create a subnetwork from a GSMN in SBML format, and two files containing lists of compounds of interests ids, one per row.";
    }
}
