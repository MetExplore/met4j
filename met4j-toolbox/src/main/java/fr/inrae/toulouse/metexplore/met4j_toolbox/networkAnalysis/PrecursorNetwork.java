package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.IOException;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Gml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class PrecursorNetwork extends AbstractMet4jApplication {

    //arguments
    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file: path to network used for computing scope, in sbml format.", required = true)
    public String sbmlFilePath;

    @ParameterType(name = InputFile)
    @Option(name = "-t", aliases = {"--targets"}, usage = "input target file: tabulated file containing node of interest ids", required = true)
    public String targetsFilePath;

    @Format(name = Gml)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output file: path to the .gml file where the results precursor network will be exported", required = true)
    public String output;

    //options
    @ParameterType(name = InputFile)
    @Option(name = "-sc", aliases = {"--sides"}, usage = "an optional file containing list of ubiquitous compounds to be considered already available")
    public String sideCompoundFile = null;
    @ParameterType(name = InputFile)
    @Option(name = "-ir", aliases = {"--ignore"}, usage = "an optional file containing list of reaction to ignore (forbid inclusion in scope)")
    public String reactionToIgnoreFile = null;

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {
        PrecursorNetwork app = new PrecursorNetwork();
        app.parseArguments(args);
        app.run();
    }


    public void run() throws IOException, Met4jSbmlReaderException {
        JsbmlReader in = new JsbmlReader(sbmlFilePath);
        BipartiteGraph graph = (new Bionetwork2BioGraph(in.read())).getBipartiteGraph();
        NodeMapping<BioEntity, BipartiteEdge, BipartiteGraph> mapper = new NodeMapping<>(graph).skipIfNotFound();

        BioCollection<BioMetabolite> targets = mapper.map(targetsFilePath).stream()
                .map(BioMetabolite.class::cast)
                .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);
        BioCollection<BioMetabolite> bootstraps = (sideCompoundFile==null) ? new BioCollection<>() : mapper.map(sideCompoundFile).stream()
                .map(BioMetabolite.class::cast)
                .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);
        BioCollection<BioReaction> forbidden = (reactionToIgnoreFile==null) ? new BioCollection<>() : mapper.map(reactionToIgnoreFile).stream()
                .map(BioReaction.class::cast)
                .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);

        if(targets.isEmpty()){
            System.err.println("no target available, computation aborted");
        }else {
            fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.PrecursorNetwork precursorComp =
                    new fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.PrecursorNetwork(graph, bootstraps, targets, forbidden);
            BipartiteGraph precursorNet = precursorComp.getPrecursorNetwork();
            ExportGraph.toGml(precursorNet, output);
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "The precursor network of a set of compounds (targets) refer to the sub-part of a metabolic network from which a target can be reached" +
                "The network expansion process consist of adding a reaction to the network if any of its products " +
                "are either a targets or a substrate of a previously added reaction";
    }

    @Override
    public String getShortDescription() {
        return "Perform a network expansion from a set of compound targets to create a precursor network.";
    }
}
