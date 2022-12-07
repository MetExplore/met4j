package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.*;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.GroupPathwayParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CompoundNet extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-s", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-sc", usage = "input Side compound file", required = false)
    public String inputSide = null;

    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Option(name = "-o", usage = "output Graph file", required = true)
    public String outputPath = null;

    enum strategy {no, by_name,by_id}
    @Option(name = "-mc", aliases = {"--mergecomp"}, usage = "merge compartments. " +
            "Use names if consistent and unambiguous across compartments, or identifiers if compartment suffix is present (id in form \"xxx_y\" with xxx as base identifier and y as compartment label).")
    public strategy mergingStrat = strategy.no;
    public String idRegex = "^(\\w+)_\\w$";

    @Option(name = "-me", aliases = {"--simple"}, usage = "merge parallel edges to produce a simple graph", required = false)
    public boolean mergeEdges = false;

    @Option(name = "-ri", aliases = {"--removeIsolatedNodes"}, usage = "remove isolated nodes", required = false)
    public boolean removeIsolated = false;

    @Option(name = "-dw", aliases = {"--degreeWeights"}, usage = "penalize traversal of hubs by using degree square weighting", forbids = {"-cw"})
    public Boolean degree = false;

    @ParameterType(name=EnumParameterTypes.InputFile)
    @Format(name=EnumFormats.Tsv)
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for compound pairs", forbids = {"-dw"})
    public String weightFile = null;

    @Option(name = "-un", aliases = {"--undirected"}, usage = "create as undirected", required = false)
    public boolean undirected = false;

    @Option(name = "-tp", aliases = {"--transitionproba"}, usage = "set weight as random walk transition probability, normalized by reaction", required = false)
    public boolean computeWeight = false;

    @Option(name = "-am", aliases = {"--asmatrix"}, usage = "export as matrix (implies simple graph conversion). Default export as GML file", required = false)
    public boolean asMatrix = false;

    public static void main(String[] args)  {

        CompoundNet app = new CompoundNet();

        app.parseArguments(args);

        app.run();

    }


    public void run() {
        System.out.print("Reading SBML...");
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(
                new NotesParser(false), new FBCParser(), new GroupPathwayParser()));

        BioNetwork network = null;

        try {
            network = reader.read(pkgs);
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println(" Done.");


        System.out.print("Buildinig Network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        System.out.println(" Done.");

        //Graph processing: side compound removal [optional]
        if (inputSide != null) {
            System.err.println("removing side compounds...");
            NodeMapping<BioMetabolite, ReactionEdge, CompoundGraph> mapper = new NodeMapping<>(graph).skipIfNotFound();
            BioCollection<BioMetabolite> sideCpds = null;
            try {
                sideCpds = mapper.map(inputSide);
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.err.println(sideCpds.size() + " compounds removed.");
        }

        //Graph processing: set weights [optional]
        WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            System.err.println("Setting edge weights...");
            wp = new WeightsFromFile(weightFile);
        } else if (degree) {
            System.err.println("Setting edge weights...");
            int pow = 2;
            wp = new DegreeWeightPolicy(pow);
        }
        wp.setWeight(graph);

        //invert graph as undirected (copy edge weight to reversed edge)
       if(undirected){
           System.out.print("Create Undirected...");
           graph.asUndirected();
           System.out.println(" Done.");
       }

        //merge compartment
        if(mergingStrat!=strategy.no){
            System.out.print("Merging compartments...");
            VertexContraction vc = new VertexContraction();
            VertexContraction.Mapper merger = mergingStrat.equals(strategy.by_name) ? new VertexContraction.MapByName() : new VertexContraction.MapByIdSubString(idRegex);
            graph = vc.decompartmentalize(graph, merger);
            System.out.println(" Done.");
        }

        //remove isolated nodes
        if(removeIsolated){
            System.out.println("Remove isolated nodes...");
            HashSet<BioMetabolite> nodes = new HashSet<>(graph.vertexSet());
            graph.removeIsolatedNodes();
            nodes.removeAll(graph.vertexSet());
            for(BioMetabolite n : nodes){
                System.out.println("\tremoving " + n.getName());
            }
            System.out.println(" Done.");
        }

        //compute transitions probability from weights
        if(computeWeight) {
            System.out.print("Compute transition matrix...");
            ReactionProbabilityWeight wp2 = new ReactionProbabilityWeight();
            wp2.setWeight(graph);
            System.out.println(" Done.");
        }

        //merge parallel edges
        if(mergeEdges){
            System.out.print("Merging edges...");
            EdgeMerger.mergeEdgesWithOverride(graph);
            System.out.println(" Done.");
        }

        //export graph
        System.out.print("Exporting...");
        if(asMatrix){
            ComputeAdjacencyMatrix adjBuilder = new ComputeAdjacencyMatrix(graph);
            if(!computeWeight) adjBuilder.parallelEdgeWeightsHandling((u, v) -> Math.max(u,v));
            ExportMatrix.toCSV(this.outputPath,adjBuilder.getadjacencyMatrix());
        }else{
            ExportGraph.toGmlWithAttributes(graph, this.outputPath, true);
        }
        System.out.println(" Done.");
        return;
    }

    @Override
    public String getLabel() {return this.getClass().getSimpleName();}

    @Override
    public String getLongDescription() {
        return "Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes.\n" +
                "While Carbon Skeleton Graph offer a relevant alternative topology for graph-based analysis, it requires compounds' structure information, usually not provided in model, and difficult to retrieve for model with sparse cross-reference annotations.\n" +
                "In contrary to the SBML2Graph app that performs a raw conversion of the SBML content, the present app propose a fine-tuned creation of compound graph from predefined list of side compounds and degree² weighting to get relevant structure without structural data."+
                "This app also enable Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the network.";
    }

    @Override
    public String getShortDescription() {return "Advanced creation of a compound graph representation of a SBML file content";}
}

