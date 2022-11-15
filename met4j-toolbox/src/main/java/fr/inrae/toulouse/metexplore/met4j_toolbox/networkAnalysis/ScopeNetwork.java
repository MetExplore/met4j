package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.ScopeCompounds;
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

public class ScopeNetwork extends AbstractMet4jApplication {

    //arguments
    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file: path to network used for computing scope, in sbml format.", required = true)
    public String sbmlFilePath;

    @ParameterType(name = InputFile)
    @Option(name = "-s", aliases = {"--seeds"}, usage = "input seeds file: tabulated file containing node of interest ids", required = true)
    public String seedsFilePath;

    @Format(name = Gml)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output file: path to the .gml file where the results scope network will be exported", required = true)
    public String output;

    //options
    @ParameterType(name = InputFile)
    @Option(name = "-sc", aliases = {"--sides"}, usage = "an optional file containing list of ubiquitous side compounds to be considered available by default but ignored during expansion")
    public String sideCompoundFile = null;

    @Option(name = "-ssc", aliases = {"--showsides"}, usage = "show side compounds in output network", depends = {"-sc"})
    public boolean includeSides = false;

    @ParameterType(name = InputFile)
    @Option(name = "-ir", aliases = {"--ignore"}, usage = "an optional file containing list of reaction to ignore (forbid inclusion in scope")
    public String reactionToIgnoreFile = null;

    @Option(name = "-t", aliases = {"--trace"}, usage = "trace inclusion step index for each node in output")
    public boolean trace = false;

    @Option(name = "-tab", aliases = {"--asTable"}, usage = "Export in tabulated file instead of .GML")
    public Boolean asTable = false;

    public static void main(String[] args)  {
        ScopeNetwork app = new ScopeNetwork();
        app.parseArguments(args);
        app.run();
    }


    public void run() {
        JsbmlReader in = new JsbmlReader(sbmlFilePath);
        BipartiteGraph graph = null;
        try {
            graph = (new Bionetwork2BioGraph(in.read())).getBipartiteGraph();
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        NodeMapping<BioEntity, BipartiteEdge, BipartiteGraph> mapper = new NodeMapping<>(graph).skipIfNotFound();

        BioCollection<BioMetabolite> seeds = null;
        try {
            seeds = mapper.map(seedsFilePath).stream()
                    .map(BioMetabolite.class::cast)
                    .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);
        } catch (IOException e) {
            System.err.println("Error while reading the seed file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        BioCollection<BioMetabolite> bootstraps = null;
        try {
            bootstraps = (sideCompoundFile==null) ? new BioCollection<>() : mapper.map(sideCompoundFile).stream()
                    .map(BioMetabolite.class::cast)
                    .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);
        } catch (IOException e) {
            System.err.println("Error while reading the side compound file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        BioCollection<BioReaction> forbidden = null;
        try {
            forbidden = (reactionToIgnoreFile==null) ? new BioCollection<>() : mapper.map(reactionToIgnoreFile).stream()
                        .map(BioReaction.class::cast)
                        .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);
        } catch (IOException e) {
            System.err.println("Error while reading the reactions-to-ignore file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        if(seeds.isEmpty()){
            System.err.println("no seed available, computation aborted");
        }else {
            ScopeCompounds scopeComp = new ScopeCompounds(graph, seeds, bootstraps, forbidden);
            if (includeSides) scopeComp.includeBootstrapsInScope();
            if (trace) scopeComp.trace();
            BipartiteGraph scope = scopeComp.getScopeNetwork();
            if(asTable){
                ExportGraph.toTab(scope, output);
            }else{
                if (trace) {
                    ExportGraph.toGmlWithAttributes(scope, output, scopeComp.getExpansionSteps(), "step");
                } else {
                    ExportGraph.toGml(scope, output);
                }
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
                "The scope of a set of compounds (seed) refer to the maximal metabolic network that can be extended from them," +
                "where the extension process consist of adding a reaction to the network if and only if all of its substrates " +
                "are either a seed or a product of a previously added reaction\n" +
                "For more information, see Handorf, Ebenhöh and Heinrich (2005). *Expanding metabolic networks: scopes of compounds, robustness, and evolution.* Journal of molecular evolution, 61(4), 498-512. (https://doi.org/10.1007/s00239-005-0027-1)";
    }

    @Override
    public String getShortDescription() {
        return "Perform a network expansion from a set of compound seeds to create a scope network";
    }
}
