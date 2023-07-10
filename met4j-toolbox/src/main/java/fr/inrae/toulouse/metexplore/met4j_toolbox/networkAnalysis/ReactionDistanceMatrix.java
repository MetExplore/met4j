package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.ShortestPath;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.CustomWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DegreeWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
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
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Csv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.*;

public class ReactionDistanceMatrix extends AbstractMet4jApplication {

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

    @Option(name = "-dw", aliases = {"--degree"}, usage = "penalize traversal of hubs by using degree square weighting (-w must not be set)", forbids = {"-w"})
    public Boolean degree = false;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-re", aliases = {"--rExclude"}, usage = "an optional file containing list of reactions to ignore")
    public String rExclude = null;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-s", aliases = {"--seeds"}, usage = "an optional file containing list of reactions of interest.")
    public String rxnFile = null;

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = InputFile)
    @Option(name = "-w", aliases = {"--weights"}, usage = "an optional file containing weights for compound pairs", forbids = {"-d"})
    public String weightFile = null;

    @Option(name = "-u", aliases = {"--undirected"}, usage = "Ignore reaction direction")
    public Boolean undirected = false;


    public static void main(String[] args) {

        ReactionDistanceMatrix app = new ReactionDistanceMatrix();

        app.parseArguments(args);

        app.run();

    }


    public void run() {

        JsbmlReader reader = new JsbmlReader(this.inputPath);
        BioNetwork network = null;
        try {
            network = reader.read();
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        //Instantiate nodes to remove and mappers
        Mapper<BioMetabolite> metMapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();
        Mapper<BioReaction> rxnMapper = new Mapper<>(network, BioNetwork::getReactionsView).skipIfNotFound();

        //Graph processing: side compound removal [optional]
        BioCollection<BioMetabolite> sideCpds = new BioCollection<BioMetabolite>();
        if (sideCompoundFile != null) {
            try {
                System.err.println("removing side compounds...");
                sideCpds = metMapper.map(sideCompoundFile);
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (metMapper.getNumberOfSkippedEntries() > 0)
                System.err.println(metMapper.getNumberOfSkippedEntries() + " side compounds not found in network.");
            System.err.println(sideCpds.size() + " side compounds to ignore during graph build");
        }
        //Graph processing: reactions removal [optional]
        BioCollection<BioReaction> rList = new BioCollection<BioReaction>();
        if (rExclude != null) {
          System.err.println("removing reactions to exclude...");
          try {
            rList = rxnMapper.map(rExclude);
          } catch (IOException e) {
              System.err.println("Error while reading the reaction to ignore file");
              System.err.println(e.getMessage());
              System.exit(1);
          }
          if (rxnMapper.getNumberOfSkippedEntries() > 0)
          System.err.println(rxnMapper.getNumberOfSkippedEntries() + " reactions to exclude not found in network.");
          System.err.println(rList.size() + " reactions ignored during graph build.");
      }
        //Create reaction graph
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        ReactionGraph graph = builder.getReactionGraph(sideCpds, rList);
        //Graph processing: set weights [optional]
        WeightingPolicy wp = new UnweightedPolicy();
        if (weightFile != null) {
            wp = new WeightsFromFile(weightFile, true);
        } else if (degree) {
            if(!undirected){
                int pow = 2;
                wp = new DegreeWeightPolicy(pow);
            }else{
                //since degree weighting policy is not symmetric, for undirected case we create reversed edges, apply
                //a corrected degree computation for each edge, and treat the graph as normal
                graph.asUndirected();
                undirected=false;
                wp = new CustomWeightPolicy<BioReaction,CompoundEdge,ReactionGraph>(
                        e -> {
                            Double w = Double.valueOf(graph.inDegreeOf(e.getV2()));
                            w += Double.valueOf(graph.outDegreeOf(e.getV2()));
                            w = w/2;    //adjust for undirected doubled edges
                            w = StrictMath.pow(w,2);
                            return w;
                        });
            }
        }
        wp.setWeight(graph);

        //init BioMatrix
        BioMatrix distM = null;
        if(rxnFile == null){
            //compute distance matrix
            ShortestPath matrixComputor = new ShortestPath<>(graph, !undirected);
            //get All SPs
            distM = matrixComputor.getShortestPathDistanceMatrix();
        }else{
            System.err.println("filtering matrix...");
            NodeMapping mapper = new NodeMapping<>(graph).skipIfNotFound();
            Set<BioReaction> seeds = null;
            try {
                seeds = new LinkedHashSet<BioReaction>(mapper.map(rxnFile));
            } catch (IOException e) {
                System.err.println("Error while reading the seed file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            //compute distance matrix
            ShortestPath<BioReaction, CompoundEdge, ReactionGraph> matrixComputor = new ShortestPath<>(graph, !undirected);
            //get SPs
            distM = matrixComputor.getShortestPathDistanceMatrix(seeds,seeds);
        }
        //export results
        ExportMatrix.toCSV(outputPath, distM);
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
            "The distance between two reactions is computed as the length of the shortest path connecting the two in the reaction graph, " +
            "where two reactions are linked if they produce a metabolite consumed by the other or the other way around.\n" +
            "An optional edge weighting can be used, turning the distances into the sum of edge weights in the lightest path, rather than the length of the shortest path." +
            "The default weighting use target's degree squared. Alternatively, custom weighting can be provided in a file. In that case, edges without weight are ignored during path search.\n" +
            "If no edge weighting is set, it is recommended to provide a list of side compounds to ignore during network traversal.";
    }

    @Override
    public String getShortDescription() {
        return "Create a reaction to reaction distance matrix.";
    }
}