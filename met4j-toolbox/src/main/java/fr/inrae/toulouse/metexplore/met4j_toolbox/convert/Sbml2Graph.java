package fr.inrae.toulouse.metexplore.met4j_toolbox.convert;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.*;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class Sbml2Graph extends AbstractMet4jApplication {

    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Option(name = "-o", usage = "output Graph file", required = true)
    public String outputPath = null;

    @Option(name = "-b", aliases = {"--bipartite"},usage = "create bipartite graph", forbids={"-c"})
    public Boolean bipartite = false;

    @Option(name = "-c", aliases = {"--compound"}, usage = "create compound graph", forbids={"-b"})
    public Boolean compound = true;

    @Option(name = "-tab", usage = "export in tabulated file", forbids={"-gml"})
    public Boolean tab = false;

    @Option(name = "-gml", usage = "export in GML file", forbids={"-tab"})
    public Boolean gml = true;

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        Sbml2Graph app = new Sbml2Graph();

        app.parseArguments(args);

        app.run();

    }


    public void run() throws IOException, Met4jSbmlReaderException {
        JsbmlReader reader = new JsbmlReader(this.inputPath, false);
        HashSet<PackageParser> pkgs = new HashSet<>(Arrays.asList(
                new NotesParser(false), new FBCParser(), new GroupPathwayParser()));
        BioNetwork network = reader.read(pkgs);
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);

        if(bipartite) compound = false;
        if(tab) gml = false;

        if(compound){
            CompoundGraph graph = builder.getCompoundGraph();
            if(gml){
                ExportGraph.toGml(graph, this.outputPath);
            }else{
                ExportGraph.toTab(graph, this.outputPath);
            }
        }else{
            BipartiteGraph graph = builder.getBipartiteGraph();
            if(gml){
                ExportGraph.toGml(graph, this.outputPath);
            }else{
                ExportGraph.toTab(graph, this.outputPath);
            }
        }
        return;
    }
    @Override
    public String getLabel() {return this.getClass().getSimpleName();}

    @Override
    public String getDescription() {return "Create a graph representation of a SBML file content, and export it in graph file format.\n" +
            "The graph can be either a compound graph or a bipartite graph, and can be exported in gml or tabulated file format.";}

}
