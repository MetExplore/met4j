package fr.inrae.toulouse.metexplore.met4j_toolbox.convert;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.*;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.*;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class Sbml2Graph extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = EnumFormats.Txt)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output Graph file", required = true)
    public String outputPath = null;

    @Option(name = "-b", aliases = {"--bipartite"}, usage = "create bipartite graph", forbids = {"-c", "-r"})
    public Boolean bipartite = false;

    @Option(name = "-c", aliases = {"--compound"}, usage = "create compound graph", forbids = {"-b", "-r"})
    public Boolean compound = true;

    @Option(name = "-r", aliases = {"--reaction"}, usage = "create reaction graph", forbids = {"-c", "-b"})
    public Boolean reaction = false;

    @Option(name = "-tab", usage = "export in tabulated file", forbids = {"-gml"})
    public Boolean tab = false;

    @Option(name = "-gml", usage = "export in GML file", forbids = {"-tab"})
    public Boolean gml = true;

    public static void main(String[] args) {

        Sbml2Graph app = new Sbml2Graph();

        app.parseArguments(args);

        app.run();

    }


    public void run() {
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(
                new NotesParser(false), new FBCParser(), new GroupPathwayParser()));

        BioNetwork network = null;

        try {
            network = reader.read(pkgs);
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);

        if (bipartite || reaction) {
            compound = false;
        }
        if (tab) gml = false;

        if (compound) {
            CompoundGraph graph = builder.getCompoundGraph();
            if (gml) {
                ExportGraph.toGml(graph, this.outputPath);
            } else {
                ExportGraph.toTab(graph, this.outputPath);
            }
        } else if (bipartite) {
            BipartiteGraph graph = builder.getBipartiteGraph();
            if (gml) {
                ExportGraph.toGml(graph, this.outputPath);
            } else {
                ExportGraph.toTab(graph, this.outputPath);
            }
        } else {
            ReactionGraph graph = builder.getReactionGraph();
            if (gml) {
                ExportGraph.toGml(graph, this.outputPath);
            } else {
                ExportGraph.toTab(graph, this.outputPath);
            }
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "The graph can be either a compound graph, a reaction graph or a bipartite graph, and can be exported in gml or tabulated file format.";
    }

    @Override
    public String getShortDescription() {
        return "Create a graph representation of a SBML file content, and export it in graph file format.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of(new Doi("https://doi.org/10.1109/tcbb.2008.79"));
    }

}
