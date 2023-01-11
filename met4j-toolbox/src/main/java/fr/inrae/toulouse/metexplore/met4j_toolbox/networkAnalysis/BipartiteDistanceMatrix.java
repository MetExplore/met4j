package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.*;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Csv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.*;

public class BipartiteDistanceMatrix extends AbstractMet4jApplication {

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
    @Option(name = "-re", aliases = {"--rExclude"}, usage = "an optional file containing list of reactions to ignore")
    public String rExclude = null;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-m", aliases = {"--mets"}, usage = "an optional file containing list of compounds of interest.")
    public String metFile = null;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-r", aliases = {"--rxns"}, usage = "an optional file containing list of reactions of interest.")
    public String rxnFile = null;

    @Option(name = "-dw", aliases = {"--degree"}, usage = "penalize traversal of hubs by using degree square weighting (-w must not be set)", forbids = {"-w"})
    public Boolean degree = false;

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-w", aliases = {"--weights"}, usage = "an optional file containing weights for compound pairs", forbids = {"-d"})
    public String weightFile = null;

    @Option(name = "-u", aliases = {"--undirected"}, usage = "Ignore reaction direction")
    public Boolean undirected = false;

    @Option(name = "-f", aliases = {"--full"}, usage = "compute full pairwise matrix from both reactions and compounds lists")
    public Boolean full = false;


    public static void main(String[] args) {

        BipartiteDistanceMatrix app = new BipartiteDistanceMatrix();

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

        //Create bipartite graph
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        BipartiteGraph graph = builder.getBipartiteGraph();

        //Graph processing: side compound removal [optional]
        if (sideCompoundFile != null) {
            System.err.println("removing side compounds...");
            NodeMapping mapper = new NodeMapping<>(graph);
            BioCollection<BioMetabolite> sideCpds = null;
            try {
                sideCpds = mapper.map(sideCompoundFile);
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            graph.removeAllVertices(sideCpds);
            System.err.println(sideCpds.size() + " side compounds ignored during graph build.");
        }

        //Graph processing: reactions removal [optional]
        if (rExclude != null) {
          System.err.println("removing reactions to exclude...");
          NodeMapping mapper = new NodeMapping<>(graph).skipIfNotFound();
          BioCollection<BioReaction> rList = null;
          try {
            rList = mapper.map(rExclude);
          } catch (IOException e) {
              System.err.println("Error while reading the reaction to ignore file");
              System.err.println(e.getMessage());
              System.exit(1);
          }
          graph.removeAllVertices(rList);
          System.err.println(rList.size() + " reactions ignored during graph build.");
      }

        //Graph processing: set weights [optional]
        WeightingPolicy wp = new UnweightedPolicy();
        if (weightFile != null) {
            wp = new WeightsFromFile(weightFile, true);
        }
        wp.setWeight(graph);

        //init BioMatrix
        BioMatrix distM = null;
        Set<BioMetabolite> metSeeds = null;
        Set<BioReaction> rxnSeeds = null;
        NodeMapping mapper = new NodeMapping<>(graph).skipIfNotFound();
        //If both seed files are missing, then compute on complete graphs
        //If metabolite's seed file is missing, use the list of all metabolites nodes
        // If metabolites's reaction file is missing, use the list of all reactions nodes

        //Note cannot use mapper because bionetwork is not updated
        if(metFile == null){
            metSeeds = graph.compoundVertexSet();
        }else{
            try {
                metSeeds = new HashSet<>(mapper.map(metFile));
            } catch (IOException e) {
                System.err.println("Error while reading the metabolite seeds file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        if(rxnFile == null){
            rxnSeeds = graph.reactionVertexSet();
        }else{ 
            try {
                rxnSeeds = new HashSet<>(mapper.map(rxnFile));
            } catch (IOException e) {
                System.err.println("Error while reading the metabolite seeds file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        //full == square distance matrix of all pairwise comparisons between seeds
        if(full){
            Set<BioEntity> seeds = new HashSet<BioEntity>();
            //compute distance matrix
            ShortestPath matrixComputor = new ShortestPath<>(graph, !undirected);
            Stream.of(metSeeds,rxnSeeds).forEach(seeds::addAll);
            distM = matrixComputor.getShortestPathDistanceMatrix(seeds,seeds);
        }else{
            //compute distance matrix
            ShortestPath matrixComputor = new ShortestPath<>(graph, !undirected);
            distM = matrixComputor.getShortestPathDistanceMatrix(metSeeds,rxnSeeds);
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
