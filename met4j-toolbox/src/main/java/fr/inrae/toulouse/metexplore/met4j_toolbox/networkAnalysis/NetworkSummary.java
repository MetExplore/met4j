package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.algo.FloydWarshall;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.GraphCentralityMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.GraphMeasure;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.RankUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.weighting.DefaultWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class NetworkSummary extends AbstractMet4jApplication {

    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Option(name = "-o", usage = "output Matrix file", required = true)
    public String outputPath = null;

    @Option(name = "-s", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore (recommended)")
    public String sideCompoundFile = null;

    @Option(name = "-sd", aliases = {"--skipdist"}, usage = "skip full distance matrix computation (quick summary)", forbids={"-w"})
    public Boolean skipdist = false;

    @Option(name = "-u", aliases = {"--undirected"}, usage = "Ignore reaction direction")
    public Boolean undirected = false;



    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {
        NetworkSummary app = new NetworkSummary();
        app.parseArguments(args);
        app.run();
    }


    public void run() throws IOException, Met4jSbmlReaderException {
        //import network
        System.err.println("reading SBML...");
        JsbmlReader reader = new JsbmlReader(this.inputPath, false);
        BioNetwork network = reader.read();

        //Create compound graph
        System.err.println("Creating network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        network=null;

        WeightingPolicy wp = new DefaultWeightPolicy();
        wp.setWeight(graph);

        //Graph processing: side compound removal
        if(sideCompoundFile!=null){
            System.err.println("Remove side compounds...");
            BioCollection<BioMetabolite> sideCpds=new BioCollection<>();
            BufferedReader fr = new BufferedReader(new FileReader(sideCompoundFile));
            String line;
            while ((line = fr.readLine()) != null) {
                BioMetabolite s = network.getMetabolitesView().get(line);
                sideCpds.add(s);
            }
            fr.close();
            graph.removeAllVertices(sideCpds);
        }

        //Start Anlysis
        GraphMeasure analyzer = new GraphMeasure(graph);
        analyzer.adjustEdgeCountForMultiGraph();

        //basic stats
        graph.vertexSet().size();
        graph.edgeSet().size();
        analyzer.getNumberOfEdges();

        //connectivity
        List<Set<BioMetabolite>> cc = GraphMeasure.getConnectedCompenent(graph);
        cc.size();
        Map<Integer, Integer> ccSizes = cc.stream().collect(Collectors.groupingBy(Set::size))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (e -> e.getValue().size())));
        for(Map.Entry e : ccSizes.entrySet()){
            System.out.println("\t"+e.getKey()+" x"+e.getValue());
        }
        //density
        analyzer.getGamma();

        //degree statistics
        DescriptiveStatistics degreeStats = new DescriptiveStatistics();
        for(BioMetabolite v : graph.vertexSet()){
            degreeStats.addValue(graph.degreeOf(v));
        }
        degreeStats.getMax();
        degreeStats.getMean();

        //distances statistics
        if(!skipdist) {
            //  compute distance matrix
            ComputeAdjacencyMatrix adjBuilder = new ComputeAdjacencyMatrix(graph);
            if (undirected) adjBuilder.asUndirected();
            adjBuilder.parallelEdgeWeightsHandling((a, b) -> Math.min(a, b)); //keep lowest weight if parallel edges
            FloydWarshall distComputor = new FloydWarshall<>(graph, adjBuilder);
            BioMatrix distM = distComputor.getDistances();

            //  gather all elements in matrix, remove infinity
            ArrayList<Double> dist = new ArrayList<Double>(Arrays.asList(
                    Stream.of(distM.toDoubleArray())
                            .toArray(Double[]::new)));
            dist.removeIf(x -> x.equals(Double.POSITIVE_INFINITY));

            //  compute distance stats
            DescriptiveStatistics distStats = new DescriptiveStatistics();
            for (Double d : dist) distStats.addValue(d);

            int Diameter = (int) distStats.getMax();
            distStats.getMean();

            //Centrality analysis
            GraphCentralityMeasure<BioMetabolite, ReactionEdge, CompoundGraph> cm = new GraphCentralityMeasure(graph);
            Set<BioPath<BioMetabolite,ReactionEdge>> sp = (Set<BioPath<BioMetabolite, ReactionEdge>>) distComputor.getPaths().values().stream().collect(Collectors.toSet());
            LinkedHashMap<BioMetabolite, Integer> betweenness = RankUtils.computeRank(cm.getBetweenness(sp).entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getKey, e -> e.getValue().doubleValue())
            ));
            LinkedHashMap<BioMetabolite, Integer> closeness = RankUtils.computeRank(cm.getCloseness(sp));

            Iterator<Map.Entry<BioMetabolite, Integer>> it = closeness.entrySet().iterator();
            for (int i = 0; i < 3; i++) {
                Map.Entry<BioMetabolite, Integer> e = it.next();
                String s = e.getValue()+"\t"+e.getKey().getName();
            }

            it = betweenness.entrySet().iterator();
            for (int i = 0; i < 3; i++) {
                Map.Entry<BioMetabolite, Integer> e = it.next();
                String s = e.getValue()+"\t"+e.getKey().getName();
            }
        }

    }
    @Override
    public String getLabel() {return this.getClass().getSimpleName();}

    @Override
    public String getDescription() {return "";}

}
