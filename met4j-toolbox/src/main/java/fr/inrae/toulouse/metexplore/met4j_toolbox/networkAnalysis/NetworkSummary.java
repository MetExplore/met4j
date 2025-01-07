package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.GraphLocalMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.GraphMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.PathBasedCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.FloydWarshall;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.RankUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.getMetabolitesFromFile;


public class NetworkSummary extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name = OutputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-o", usage = "output report file", required = true)
    public String outputPath = null;

    @ParameterType(name = InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-s", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore (recommended)")
    public String sideCompoundFile = null;

    @Option(name = "-sd", aliases = {"--skipdist"}, usage = "skip full distance matrix computation (quick summary)")
    public Boolean skipdist = false;

    @Option(name = "-d", aliases = {"--directed"}, usage = "use reaction direction for distances")
    public Boolean directed = false;


    public static void main(String[] args) {
        NetworkSummary app = new NetworkSummary();
        app.parseArguments(args);
        app.run();
    }


    public void run() {


        //import network
        System.out.println("reading SBML...");
        BioNetwork network = IOUtils.readSbml(this.inputPath);

        //Create compound graph
        System.out.println("Creating graph...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        WeightingPolicy wp = new UnweightedPolicy();
        wp.setWeight(graph);

        //Graph processing: side compound removal
        if (sideCompoundFile != null) {
            System.out.println("removing side compounds...");
            BioCollection<BioMetabolite> sideCpds = getMetabolitesFromFile(sideCompoundFile, network, "side compounds");
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.out.println(sideCpds.size() + " compounds removed.");
        }

        //Start Anlysis
        System.out.println("perform basic analysis...");
        GraphMeasure analyzer = new GraphMeasure(graph);
        analyzer.adjustEdgeCountForMultiGraph();

        //Start output
        try {
            FileWriter fw = new FileWriter(outputPath);
            fw.write("#\tMET4J NETWORK SUMMARY\n");
            fw.write("#\tNetwork: " + this.inputPath + "\n");
            if (sideCompoundFile != null) fw.write("#\tSide compounds: " + this.sideCompoundFile + "\n");
            Date currentDate = new Date();
            fw.write("#\t" + currentDate + "\n");
            fw.write("#" + "-".repeat(60) + "\n");
            //basic stats
            fw.write("Number of nodes:\t" + graph.vertexSet().size() + "\n");
            fw.write("Number of edges:\t" + graph.edgeSet().size() + "\n");
            fw.write("Number of neighbor pairs (ignore parallel edges):\t" + (int) analyzer.getNumberOfEdges() + "\n");

            //connectivity
            System.out.println("extract connected component...");
            List<Set<BioMetabolite>> cc = GraphMeasure.getConnectedComponents(graph);
            fw.write("Number of connected component:\t" + cc.size() + "\n");
            Map<Integer, Integer> ccSizes = cc.stream().collect(Collectors.groupingBy(Set::size))
                    .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e -> e.getValue().size())));
            for (Map.Entry e : ccSizes.entrySet()) {
                fw.write("\t" + e.getKey() + " x" + e.getValue() + "\n");
            }

            //density
            System.out.println("Compute density...");
            fw.write("Density (gamma index):\t" + analyzer.getGamma() + "\n");
            DescriptiveStatistics clusterStats = new DescriptiveStatistics();
            System.out.println("Compute local clustering coefficients...");
            GraphLocalMeasure<BioMetabolite, ReactionEdge, CompoundGraph> analyzer2 = new GraphLocalMeasure(graph);
            for (BioMetabolite v : graph.vertexSet()) {
                clusterStats.addValue(analyzer2.getLocalClusteringCoeff(v));
            }
            fw.write("Average local clustering coefficient:\t" + clusterStats.getMean() + "\n");


            //degree statistics
            System.out.println("Compute degree statistics...");
            DescriptiveStatistics degreeStats = new DescriptiveStatistics();
            for (BioMetabolite v : graph.vertexSet()) {
                degreeStats.addValue(graph.degreeOf(v));
            }
            fw.write("Max degree:\t" + degreeStats.getMax() + "\n");
            fw.write("Average degree:\t" + degreeStats.getMean() + "\n");

            //distances statistics
            if (!skipdist) {
                System.out.println("Compute distances...");
                //  compute distance matrix
                ComputeAdjacencyMatrix adjBuilder = new ComputeAdjacencyMatrix(graph);
                if (!directed) adjBuilder.asUndirected();
                adjBuilder.parallelEdgeWeightsHandling((a, b) -> Math.min(a, b)); //keep lowest weight if parallel edges
                FloydWarshall distComputor = new FloydWarshall<>(graph, adjBuilder);
                BioMatrix distM = distComputor.getDistances();

                //  compute distance stats
                System.out.println("Compute distances statistics...");
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
                System.out.println("Compute centrality...");
                PathBasedCentrality<BioMetabolite, ReactionEdge, CompoundGraph> cm = new PathBasedCentrality(graph);

//            Set<BioPath<BioMetabolite,ReactionEdge>> sp = new HashSet<>(distComputor.getPaths().values());
//            LinkedHashMap<BioMetabolite, Integer> betweenness = RankUtils.computeRank(cm.getBetweenness(sp).entrySet().stream().collect(
//                    Collectors.toMap(Map.Entry::getKey, e -> e.getValue().doubleValue())
//            ));
//            System.out.println("\tBetweenness done.");

                Map<BioMetabolite, Double> closenessRaw = cm.getCloseness(distM);
                LinkedHashMap<BioMetabolite, Integer> closeness = RankUtils.computeRank(closenessRaw);
                System.out.println("\tCloseness done.");

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
            System.out.println("Done.");
            fw.close();
        } catch (IOException e) {
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
        return this.getShortDescription() + "\nUse a metabolic network in SBML file and an optional list of side compounds, " +
                "and produce a report summarizing several graph measures characterising the structure of the network." +
                "This includes (non-exhaustive list): size and order, connectivity, density, degree distribution, shortest paths length, top centrality nodes...";
    }

    @Override
    public String getShortDescription() {
        return "Create a report summarizing several graph measures characterising the structure of a metabolic network.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }

}
