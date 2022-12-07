package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
// import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.FloydWarshall;
// import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DegreeWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.*;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Csv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.*;

public class DistanceMatrix extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = Csv)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output Matrix file", required = true)
    public String outputPath = null;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-sc", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore")
    public String sideCompoundFile = null;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-s", aliases = {"--seed"}, usage = "an optional file containing list of compounds of interest. The returned distance matrix contains only the corresponding rows and columns")
    public String seedFile = null;

    @Option(name = "-dw", aliases = {"--degree"}, usage = "penalize traversal of hubs by using degree square weighting (-w must not be set)", forbids = {"-w"})
    public Boolean degree = false;

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-w", aliases = {"--weights"}, usage = "an optional file containing weights for compound pairs", forbids = {"-d"})
    public String weightFile = null;

    @Option(name = "-u", aliases = {"--undirected"}, usage = "Ignore reaction direction")
    public Boolean undirected = false;


    public static void main(String[] args) {

        DistanceMatrix app = new DistanceMatrix();

        app.parseArguments(args);

        app.run();

    }


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
            Mapper<BioMetabolite> mapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();
            BioCollection<BioMetabolite> sideCpds = null;
            try {
                sideCpds = mapper.map(sideCompoundFile);
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (mapper.getNumberOfSkippedEntries() > 0)
                System.err.println(mapper.getNumberOfSkippedEntries() + " side compounds not found in network.");
            boolean removed = graph.removeAllVertices(sideCpds);
            System.err.println(sideCpds.size() + " side compounds ignored during graph build.");
        }

        //Graph processing: set weights [optional]
        WeightingPolicy wp = new UnweightedPolicy();
        if (weightFile != null) {
            wp = new WeightsFromFile(weightFile, true);
        } else if (degree) {
            int pow = 2;
            wp = new DegreeWeightPolicy(pow);
        }
        wp.setWeight(graph);

        //init BioMatrix
        BioMatrix distM = null;
        if(seedFile == null){
            //compute distance matrix
            ShortestPath matrixComputor = new ShortestPath<>(graph, !undirected);
            //get All SPs
            distM = matrixComputor.getShortestPathDistanceMatrix();
        }else{
            System.err.println("filtering matrix...");
            Mapper<BioMetabolite> mapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();
            Set<BioMetabolite> seeds = null;
            try {
                seeds = new LinkedHashSet<BioMetabolite>(mapper.map(seedFile));
            } catch (IOException e) {
                System.err.println("Error while reading the seed file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            //compute distance matrix
            ShortestPath<BioMetabolite, ReactionEdge, CompoundGraph> matrixComputor = new ShortestPath<>(graph, !undirected);
            //get SPs
            List<BioPath<BioMetabolite, ReactionEdge>> sps = matrixComputor.getShortestPathsUnionList(seeds);
            distM = matrixComputor.getShortestPathDistanceMatrix(seeds,seeds);
        }
        //export results
        ExportMatrix.toCSV(outputPath, distM);

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "The distance between two compounds is computed as the length of the shortest path connecting the two in the compound graph, " +
                "where two compounds are linked if they are respectively substrate and product of the same reaction.\n" +
                "An optional edge weighting can be used, turning the distances into the sum of edge weights in the lightest path, rather than the length of the shortest path." +
                "The default weighting use target's degree squared. Alternatively, custom weighting can be provided in a file. In that case, edges without weight are ignored during path search.\n" +
                "If no edge weighting is set, it is recommended to provide a list of side compounds to ignore during network traversal.";
    }

    @Override
    public String getShortDescription() {
        return "Create a compound to compound distance matrix.";
    }
}
