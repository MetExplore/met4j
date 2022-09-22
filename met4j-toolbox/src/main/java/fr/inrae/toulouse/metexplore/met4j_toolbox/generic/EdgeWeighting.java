package fr.inrae.toulouse.metexplore.met4j_toolbox.generic;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.ReactionProbabilityWeight;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
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
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Abstract class for app that provides tabulated compound graph edge list, with one column with edge weight.
 */
public abstract class EdgeWeighting extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-s", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-sc", usage = "input Side compound file", required = false)
    public String inputSide = null;

    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Option(name = "-o", usage = "output edge weight file", required = true)
    public String outputPath = null;

    enum strategy {no, by_name,by_id}
    @Option(name = "-mc", aliases = {"--mergecomp"}, usage = "merge compartments. " +
            "Use names if consistent and unambiguous across compartments, or identifiers if compartment suffix is present (id in form \"xxx_y\" with xxx as base identifier and y as compartment label).")
    public strategy mergingStrat = strategy.no;
    public String idRegex = "^(\\w+)_\\w$";

    @Option(name = "-me", aliases = {"--simple"}, usage = "merge parallel edges to produce a simple graph", required = false)
    public boolean mergeEdges = false;

    @Option(name = "-un", aliases = {"--undirected"}, usage = "create as undirected", required = false)
    public boolean undirected = false;

    @Option(name = "-tp", aliases = {"--transitionproba"}, usage = "set weight as random walk transition probability, normalized by reaction", required = false)
    public boolean computeWeight = false;

    @Option(name = "-nan", aliases = {"--removeNaN"}, usage = "do not output edges with undefined weight", required = false)
    public boolean removeNaN = false;


    public abstract WeightingPolicy setWeightingPolicy();

    public void run() throws IOException, Met4jSbmlReaderException {

        WeightingPolicy wp = setWeightingPolicy();

        System.out.print("Reading SBML...");
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(
                new NotesParser(false), new FBCParser(), new GroupPathwayParser()));
        BioNetwork network = reader.read(pkgs);
        network = processNetwork(network);
        System.out.println(" Done.");


        System.out.print("Buildinig Network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        graph = processCompoundGraph(graph);
        System.out.println(" Done.");

        //Graph processing: side compound removal [optional]
        if (inputSide != null) {
            System.err.println("removing side compounds...");
            NodeMapping<BioMetabolite, ReactionEdge, CompoundGraph> mapper = new NodeMapping<>(graph).skipIfNotFound();
            BioCollection<BioMetabolite> sideCpds = mapper.map(inputSide);
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.err.println(sideCpds.size() + " compounds removed.");
        }

        //Graph processing: set weights [optional]
        wp.setWeight(graph);
        if(removeNaN) WeightUtils.removeEdgeWithNaNWeight(graph);

        //invert graph as undirected (copy edge weight to reversed edge)
        if(undirected){
            System.out.print("Create Undirected...");
            graph.asUndirected();
            System.out.println(" Done.");
        }

        //merge compartment
        if(mergingStrat!= strategy.no){
            System.out.print("Merging compartments...");
            VertexContraction vc = new VertexContraction();
            VertexContraction.Mapper merger = mergingStrat.equals(strategy.by_name) ? new VertexContraction.MapByName() : new VertexContraction.MapByIdSubString(idRegex);
            graph = vc.decompartmentalize(graph, merger);
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
        WeightUtils.export(graph,outputPath);
        System.out.println(" Done.");
        return;
    }

    /**
     * Methods to add optional preprocessing of the compound graph
     * @param graph the original compound graph
     * @return a preprocessed graph
     */
    public CompoundGraph processCompoundGraph(CompoundGraph graph) {
        return graph;
    }

    /**
     * Methods to add optional preprocessing of SBML parsing output
     * @param network the original network from parsed SBML
     * @return a preprocessed network
     */
    public BioNetwork processNetwork(BioNetwork network) {
        return network;
    }

}

