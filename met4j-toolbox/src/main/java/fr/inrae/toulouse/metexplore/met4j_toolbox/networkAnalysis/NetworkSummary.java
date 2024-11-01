package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.FloydWarshall;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.PathBasedCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.GraphLocalMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.GraphMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.RankUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;


public class NetworkSummary extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name = OutputFile)
    @Format(name= EnumFormats.Txt)
    @Option(name = "-o", usage = "output report file", required = true)
    public String outputPath = null;

    @ParameterType(name = InputFile)
    @Format(name= EnumFormats.Txt)
    @Option(name = "-s", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore (recommended)")
    public String sideCompoundFile = null;

    @Option(name = "-sd", aliases = {"--skipdist"}, usage = "skip full distance matrix computation (quick summary)")
    public Boolean skipdist = false;

    @Option(name = "-d", aliases = {"--directed"}, usage = "use reaction direction for distances")
    public Boolean directed = false;


    public static void main(String[] args)  {
        NetworkSummary app = new NetworkSummary();
        app.parseArguments(args);
        app.run();
    }


    public void run() {


        //import network
        System.err.println("reading SBML...");
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
        System.err.println("Creating network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        WeightingPolicy wp = new UnweightedPolicy();
        wp.setWeight(graph);

        //Graph processing: side compound removal
        if (sideCompoundFile != null) {
            try {
                System.err.println("removing side compounds...");
                BioCollection<BioMetabolite> sideCpds = new BioCollection<>();
                BufferedReader fr = new BufferedReader(new FileReader(sideCompoundFile));
                String line;
                while ((line = fr.readLine()) != null) {
                    String sId = line.trim().split("\t")[0];
                    BioMetabolite s = network.getMetabolite(sId);
                    if (s != null) {
                        sideCpds.add(s);
                    } else {
                        System.err.println(sId + " side compound not found in network.");
                    }
                }
                fr.close();
                boolean removed = graph.removeAllVertices(sideCpds);
                if (removed) System.err.println(sideCpds.size() + " compounds removed.");
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        //Start Anlysis
        System.err.println("perform basic analysis...");
        GraphMeasure analyzer = new GraphMeasure(graph);
        analyzer.adjustEdgeCountForMultiGraph();

        //Start output
        try {
            FileWriter fw = new FileWriter(outputPath);
            fw.write("#\tMET4J NETWORK SUMMARY\n");
            fw.write("#\tNetwork: " + this.inputPath + "\n");
            if (sideCompoundFile != null) fw.write("#\tSide compounds: " + this.sideCompoundFile + "\n");
            Date currentDate = new Date();
            fw.write("#\t" + currentDate.toString() + "\n");
            fw.write("#" + "-".repeat(60) + "\n");
            //basic stats
            fw.write("Number of nodes:\t" + graph.vertexSet().size() + "\n");
            fw.write("Number of edges:\t" + graph.edgeSet().size() + "\n");
            fw.write("Number of neighbor pairs (ignore parallel edges):\t" + (int) analyzer.getNumberOfEdges() + "\n");

            //connectivity
            System.err.println("extract connected component...");
            List<Set<BioMetabolite>> cc = GraphMeasure.getConnectedComponents(graph);
            fw.write("Number of connected component:\t" + cc.size() + "\n");
            Map<Integer, Integer> ccSizes = cc.stream().collect(Collectors.groupingBy(Set::size))
                    .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e -> e.getValue().size())));
            for (Map.Entry e : ccSizes.entrySet()) {
                fw.write("\t" + e.getKey() + " x" + e.getValue() + "\n");
            }

            //density
            System.err.println("Compute density...");
            fw.write("Density (gamma index):\t" + analyzer.getGamma() + "\n");
            DescriptiveStatistics clusterStats = new DescriptiveStatistics();
            System.err.println("Compute local clustering coefficients...");
            GraphLocalMeasure<BioMetabolite, ReactionEdge, CompoundGraph> analyzer2 = new GraphLocalMeasure(graph);
            for (BioMetabolite v : graph.vertexSet()) {
                clusterStats.addValue(analyzer2.getLocalClusteringCoeff(v));
            }
            fw.write("Average local clustering coefficient:\t" + clusterStats.getMean() + "\n");


            //degree statistics
            System.err.println("Compute degree statistics...");
            DescriptiveStatistics degreeStats = new DescriptiveStatistics();
            for (BioMetabolite v : graph.vertexSet()) {
                degreeStats.addValue(graph.degreeOf(v));
            }
            fw.write("Max degree:\t" + degreeStats.getMax() + "\n");
            fw.write("Average degree:\t" + degreeStats.getMean() + "\n");

            //distances statistics
            if (!skipdist) {
                System.err.println("Compute distances...");
                //  compute distance matrix
                ComputeAdjacencyMatrix adjBuilder = new ComputeAdjacencyMatrix(graph);
                if (!directed) adjBuilder.asUndirected();
                adjBuilder.parallelEdgeWeightsHandling((a, b) -> Math.min(a, b)); //keep lowest weight if parallel edges
                FloydWarshall distComputor = new FloydWarshall<>(graph, adjBuilder);
                BioMatrix distM = distComputor.getDistances();

                //  compute distance stats
                System.err.println("Compute distances statistics...");
                DescriptiveStatistics distStats = new DescriptiveStatistics();
                //  gather all elements in matrix, remove infinity
                for (int i = 0; i < distM.numRows(); i++) {
                    for (int j = 0; j < distM.numCols(); j++) {
                        if (i != j) {
                            Double d = distM.get(i, j);
                            if (!d.equals(Double.POSITIVE_INFINITY)) {
                                distStats.addValue(d);
                            }
                        }
                    }
                }

                int diameter = (int) distStats.getMax();
                fw.write("Diameter:\t" + diameter + "\n");
                fw.write("Average shortest path length:\t" + distStats.getMean() + "\n");

                //Centrality analysis
                System.err.println("Compute centrality...");
                PathBasedCentrality<BioMetabolite, ReactionEdge, CompoundGraph> cm = new PathBasedCentrality(graph);

//            Set<BioPath<BioMetabolite,ReactionEdge>> sp = new HashSet<>(distComputor.getPaths().values());
//            LinkedHashMap<BioMetabolite, Integer> betweenness = RankUtils.computeRank(cm.getBetweenness(sp).entrySet().stream().collect(
//                    Collectors.toMap(Map.Entry::getKey, e -> e.getValue().doubleValue())
//            ));
//            System.err.println("\tBetweenness done.");

                Map<BioMetabolite, Double> closenessRaw = cm.getCloseness(distM);
                LinkedHashMap<BioMetabolite, Integer> closeness = RankUtils.computeRank(closenessRaw);
                System.err.println("\tCloseness done.");

                Iterator<Map.Entry<BioMetabolite, Integer>> it = closeness.entrySet().iterator();
                fw.write("Top Closeness:\n");
                int top = 20;
                if (closeness.size() < top) top = closeness.size();
                for (int i = 0; i < top; i++) {
                    Map.Entry<BioMetabolite, Integer> e = it.next();
                    fw.write("\t" + (e.getValue() + 1) + "\t" + e.getKey().getName() + "\t" + (closenessRaw.get(e.getKey()) * graph.vertexSet().size()) + "\n");
                }

//            it = betweenness.entrySet().iterator();
//            fw.write("Top Betweenness:");
//            for (int i = 0; i < 5; i++) {
//                Map.Entry<BioMetabolite, Integer> e = it.next();
//                fw.write("\t"+e.getValue()+"\t"+e.getKey().getName());
//            }
            }
            System.err.println("Done.");
            fw.close();
        }
        catch (IOException e) {
            System.err.println("Error while writing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Use a metabolic network in SBML file and an optional list of side compounds, " +
                "and produce a report summarizing several graph measures characterising the structure of the network." +
                "This includes (non-exhaustive list): size and order, connectivity, density, degree distribution, shortest paths length, top centrality nodes...";
    }

    @Override
    public String getShortDescription() {
        return "Create a report summarizing several graph measures characterising the structure of the network.";
    }

}
