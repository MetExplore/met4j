package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.CustomWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.GroupPathwayParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PathwayNet extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-s", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Txt)
    @Option(name = "-sc", usage = "input Side compound file (recommended)", required = false)
    public String inputSide = null;

    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Format(name= EnumFormats.Gml)
    @Option(name = "-o", usage = "output Graph file", required = true)
    public String outputPath = null;

    @Option(name = "-ri", aliases = {"--removeIsolatedNodes"}, usage = "remove isolated nodes", required = false)
    public boolean removeIsolated = false;

    @Option(name = "-oss", aliases = {"--onlySourcesAndSinks"}, usage = "consider only metabolites that are source or sink in the pathway (i.e non-intermediary compounds)", required = false)
    public boolean onlySourcesAndSinks = false;

    @ParameterType(name=EnumParameterTypes.InputFile)
    @Format(name=EnumFormats.Tsv)
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for pathway pairs", forbids = {"-ncw"})
    public String weightFile = null;

    @Option(name = "-ncw", aliases = {"--connectorWeights"}, usage = "set number of connecting compounds as weight", forbids = {"-cw"})
    public Boolean connectors = false;

    @Option(name = "-am", aliases = {"--asmatrix"}, usage = "export as matrix (implies simple graph conversion). Default export as GML file", required = false)
    public boolean asMatrix = false;

    public static void main(String[] args)  {

        PathwayNet app = new PathwayNet();

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

        //Graph processing: side compound removal [optional]
        BioCollection<BioMetabolite> sideCpds = new BioCollection<>();
        if (inputSide != null) {
            System.err.println("importing side compounds...");
            Mapper<BioMetabolite> cmapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();

            try {
                sideCpds = cmapper.map(inputSide);
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (cmapper.getNumberOfSkippedEntries() > 0)
                System.err.println(cmapper.getNumberOfSkippedEntries() + " side compounds not found in network.");
        }

        System.out.print("Buildinig Network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        PathwayGraph graph = onlySourcesAndSinks ? builder.getPathwayGraph(sideCpds) : builder.getPathwayOverlapGraph(sideCpds);
        System.out.println(" Done.");

        //Graph processing: set weights [optional]
        WeightingPolicy<BioPathway, PathwayGraphEdge, PathwayGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            System.err.println("Setting edge weights...");
            wp = new WeightsFromFile(weightFile);
        } else if (connectors) {
            wp = new CustomWeightPolicy<>(e -> Double.valueOf(e.getConnectingCompounds().size()));
        }
        wp.setWeight(graph);
        System.out.println(" Done.");

        //remove isolated nodes
        if(removeIsolated){
            System.out.println("Remove isolated nodes...");
            HashSet<BioPathway> nodes = new HashSet<>(graph.vertexSet());
            graph.removeIsolatedNodes();
            nodes.removeAll(graph.vertexSet());
            for(BioPathway n : nodes){
                System.out.println("\tremoving " + n.getName());
            }
            System.out.println(" Done.");
        }

        //export graph
        System.out.print("Exporting...");
        if(asMatrix){
            ComputeAdjacencyMatrix adjBuilder = new ComputeAdjacencyMatrix(graph);
            ExportMatrix.toCSV(this.outputPath,adjBuilder.getadjacencyMatrix());
        }else{
            if (!onlySourcesAndSinks) EdgeMerger.undirectedMergeEdgesWithOverride(graph,null);
            BioNetwork finalNetwork = network;
            Map<BioPathway,Integer> size = graph.vertexSet().stream()
                    .collect(Collectors.toMap(p -> p, p -> finalNetwork.getMetabolitesFromPathway(p).size()));
            ExportGraph.toGmlWithAttributes(graph, this.outputPath, size, "size",true);
        }
        System.out.println(" Done.");
        return;
    }

    @Override
    public String getLabel() {return this.getClass().getSimpleName();}

    @Override
    public String getLongDescription() {
        return "Genome-scale metabolic networks are often partitioned into metabolic pathways. Pathways are frequently " +
                "considered independently despite frequent coupling in their activity due to shared metabolites. In " +
                "order to decipher the interconnections linking overlapping pathways, this app proposes the creation of " +
                "\"Pathway Network\", where two pathways are linked if they share compounds.";
    }

    @Override
    public String getShortDescription() {return "Creation of a Pathway Network representation of a SBML file content";}
}

