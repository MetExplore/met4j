package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.PathwayTopologyAnalysis;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.*;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.GroupPathwayParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TopologicalPathwayAnalysis extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-s", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Txt)
    @Option(name = "-sc", usage = "input Side compound file (recommended)", required = false)
    public String inputSide = null;

    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Format(name= EnumFormats.Tsv)
    @Option(name = "-o", usage = "output result file (tsv format)", required = true)
    public String outputPath = null;

    enum strategy {no, by_name,by_id}
    @Option(name = "-mc", aliases = {"--mergecomp"}, usage = "merge compartments. " +
            "Use names if consistent and unambiguous across compartments, or identifiers if compartment suffix is present (id in form \"xxx_y\" with xxx as base identifier and y as compartment label).")
    public strategy mergingStrat = strategy.no;
    public String idRegex = "^(\\w+)_\\w$";


    @Option(name = "-ri", aliases = {"--removeIsolatedNodes"}, usage = "remove isolated nodes", required = false)
    public boolean removeIsolated = false;

    @ParameterType(name=EnumParameterTypes.InputFile)
    @Format(name=EnumFormats.Tsv)
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for compound pairs, taken into account for betweenness computation. Edges not found in file will be removed", forbids = {"-dw"})
    public String weightFile = null;

    @Option(name = "-un", aliases = {"--undirected"}, usage = "the compound graph built from the metabolic network and used for computations will undirected, i.e. the reaction directions won't be taken into account", required = false)
    public boolean undirected = false;


    @Format(name= EnumFormats.Txt)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-noi", usage = "file containing the list of metabolites of interests (one per line)", required = true)
    public String dataPath = null;

    @Option(name = "-out", aliases = {"--outDegree"}, usage = "use out-degree as scoring function instead of betweenness (faster computation)", required = false)
    public boolean out = false;


    public static void main(String[] args)  {

        TopologicalPathwayAnalysis app = new TopologicalPathwayAnalysis();

        app.parseArguments(args);

        app.run();

    }


    public void run() {
        //open file
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputPath);
        } catch (IOException e) {
            System.err.println("Error while opening the output file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.print("Reading SBML...");
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(
                new NotesParser(false), new GroupPathwayParser()));

        BioNetwork network = null;

        try {
            network = reader.read(pkgs);
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println(" Done.\n\n");


        System.out.print("Building Network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        //Graph processing: side compound removal [optional]
        if (inputSide != null) {
            System.out.println("Removing side compounds...");
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
            if (removed) System.out.println(sideCpds.size() + " compounds removed.");
        }

        //Graph processing: set weights [optional]
        WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            System.out.println("Setting edge weights...");
            wp = new WeightsFromFile(weightFile).removeEdgeNotInFile();
            wp.setWeight(graph);
            System.out.println(" Done.");
        }else{
            wp.setWeight(graph);
        }

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

        System.out.println(" Network successfully created.\n\n");

        System.out.println("Importing nodes of interest");
        NodeMapping<BioMetabolite, ReactionEdge, CompoundGraph> mapper = new NodeMapping<>(graph).throwErrorIfNotFound();
        HashSet<BioMetabolite> data = null;
        try {
            data = new HashSet<>(mapper.map(dataPath));
        } catch (IOException e) {
            System.err.println("Error while reading the source metabolite file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done.");


        System.out.println("Computing Pathway topology Analysis... (may take a while)");
        PathwayTopologyAnalysis computor = new PathwayTopologyAnalysis(network,graph,data).useNormalization();
        PathwayTopologyAnalysis.IndividualScoringStrategy strat = out ? PathwayTopologyAnalysis.IndividualScoringStrategy.outDegree() : PathwayTopologyAnalysis.IndividualScoringStrategy.betweenness() ;
        Map<BioPathway, Double> res = computor.run(strat,PathwayTopologyAnalysis.AggregationStrategy.rawSum());
        System.out.println("Done.");

        //export results
        System.out.print("Exporting...");
        try {
            for (Map.Entry<BioPathway, Double> e : res.entrySet()) {
                BioPathway p = e.getKey();
                fw.write(p.getId() + "\t" + p.getName() + "\t" + e.getValue() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("Error while writing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Done.");
        return;
    }

    @Override
    public String getLabel() {return this.getClass().getSimpleName();}

    @Override
    public String getLongDescription() {
        return "Run a Topological Pathway Analysis (TPA) to identify key pathways based on topological properties of its mapped compounds." +
                " From a list of compounds of interest, the app compute their betweenness centrality (which quantifies how often a compound acts as a intermediary along the shortest paths between pairs of other compounds in the network," +
                " which, if high, suggest a critical role in the overall flow within the network). Each pathway is scored according to the summed centrality of its metabolites found in the dataset." +
                " Alternatively to the betweenness, one can make use of the out-degree (the number of outgoing link, i.e. number of direct metabolic product) as a criterion of importance." +
                " TPA is complementary to statistical enrichment analysis to ensures a more meaningful interpretation of the data, by taking into account the influence of identified compounds on the structure of the pathways.";
    }

    @Override
    public String getShortDescription() {return "Run a Topological Pathway Analysis to identify key pathways based on topological properties of its constituting compounds.";}

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}

