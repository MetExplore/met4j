package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.FormulaParser;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.AtomMappingWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.ReactionProbabilityWeight;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.GroupPathwayParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CarbonSkeletonNet  extends AbstractMet4jApplication {

    @Option(name = "-s", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Option(name = "-g", usage = "input GSAM file", required = true)
    public String inputAAM = null;

    @Option(name = "-o", usage = "output Graph file", required = true)
    public String outputPath = null;

    @Option(name = "-ks", aliases = {"--keepSingleCarbon"}, usage = "keep edges involving single-carbon compounds, such as CO2 (requires formulas in SBML)", required = false)
    public boolean keepSingleCarbon = false;

    @Option(name = "-mc", aliases = {"--nocomp"}, usage = "merge compartments (requires unique compound names that are consistent across compartments)", required = false)
    public boolean mergeComp = false;

    @Option(name = "-me", aliases = {"--simple"}, usage = "merge parallel edges to produce a simple graph", required = false)
    public boolean mergeEdges = false;

    @Option(name = "-ri", aliases = {"--removeIsolatedNodes"}, usage = "remove isolated nodes", required = false)
    public boolean removeIsolated = false;

    @Option(name = "-un", aliases = {"--undirected"}, usage = "create as undirected", required = false)
    public boolean undirected = false;

    @Option(name = "-tp", aliases = {"--transitionproba"}, usage = "set transition probability as weight", required = false)
    public boolean computeWeight = false;

    @Option(name = "-am", aliases = {"--asmatrix"}, usage = "export as matrix (implies simple graph conversion). Default export as GML file", required = false)
    public boolean asMatrix = false;

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        CarbonSkeletonNet app = new CarbonSkeletonNet();

        app.parseArguments(args);

        app.run();

    }


    public void run() throws IOException, Met4jSbmlReaderException {
        System.out.print("Reading SBML...");
        JsbmlReader reader = new JsbmlReader(this.inputPath, false);
        ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(
                new NotesParser(false), new FBCParser(), new GroupPathwayParser()));
        BioNetwork network = reader.read(pkgs);
        System.out.println(" Done.");


        System.out.print("Buildinig Network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        System.out.println(" Done.");

        System.out.print("Processing atom mappings...");
        AtomMappingWeightPolicy wp = new AtomMappingWeightPolicy()
                .fromNumberOfConservedCarbons(inputAAM)
                .binarize()
                .removeEdgeWithoutMapping()
                .removeEdgesWithoutConservedCarbon();

        wp.setWeight(graph);
        System.out.println(" Done.");

        //invert graph as undirected (copy edge weight to reversed edge)
       if(undirected){
           System.out.print("Create Undirected...");
           graph.asUndirected();
           System.out.println(" Done.");
       }

        //merge compartment
        if(mergeComp){
            System.out.print("Merging compartments...");
            VertexContraction vc = new VertexContraction();
            graph = vc.decompartmentalize(graph, new VertexContraction.MapByName());
            System.out.println(" Done.");
        }

        //remove single-carbon compounds
        if(!keepSingleCarbon){
            System.out.println("Skip compounds with less than two carbons detected...");
            HashSet<BioMetabolite> toRemove = new HashSet<>();
            for(BioMetabolite n : graph.vertexSet()) {
                if (!graph.edgesOf(n).isEmpty()) {
                    String formula = n.getChemicalFormula();
                    try {
                        FormulaParser fp = new FormulaParser(formula);
                        if (fp.isExpectedInorganic()) {
                            graph.removeAllEdges(graph.edgesOf(n));
                            System.out.println("\tdisconnecting " + n.getName());
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("\tcan't define structure of " + n.getName());
                    }
                }
            }
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
        return "Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes." +
                " Focusing on links between compounds that share parts of their carbon skeleton allows to avoid many transitions involving side compounds, and removes entities without defined chemical structure. " +
                "This app produce a Carbon Skeleton Network relevant for graph-based analysis of metabolism, in GML or matrix format, from a SBML and an GSAM atom mapping file. " +
                "GSAM (see https://forgemia.inra.fr/metexplore/gsam) perform atom mapping at genome-scale level using the Reaction Decoder Tool (https://github.com/asad/ReactionDecoder) and allows to compute the number of conserved atoms of a given type between reactants." +
                "This app also enable Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the Carbon Skeleton Network.";
    }

    @Override
    public String getShortDescription() {return "Create a carbon skeleton graph representation of a SBML file content, using GSAM atom-mapping file (see https://forgemia.inra.fr/metexplore/gsam)";}
}

