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
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class ScopeNetwork extends AbstractMet4jApplication {

    //arguments
    @Option(name = "-i", usage = "input SBML file: path to network used for computing scope, in sbml format.", required = true)
    String sbmlFilePath;
    @Option(name = "-s", aliases = {"--seeds"}, usage = "input seeds file: tabulated file containing node of interest ids", required = true)
    String seedsFilePath;
    @Option(name = "-o", usage="output file: path to the .gml file where the results scope network will be exported", required = true)
    String output;

    //oprtions
    @Option(name = "-sc", aliases = {"--sides"}, usage = "an optional file containing list of side compounds to ignore")
    public String sideCompoundFile = null;
    @Option(name = "-ir", aliases = {"--ignore"}, usage = "an optional file containing list of reaction to ignore (forbid inclusion in scope")
    public String reactionToIgnoreFile = null;

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {
        ScopeNetwork app = new ScopeNetwork();
        app.parseArguments(args);
        app.run();
    }


    public void run() throws IOException, Met4jSbmlReaderException {
        JsbmlReader in = new JsbmlReader(sbmlFilePath);
        BipartiteGraph graph = (new Bionetwork2BioGraph(in.read())).getBipartiteGraph();
        NodeMapping<BioEntity, BipartiteEdge,BipartiteGraph> mapper =  new NodeMapping<>(graph).skipIfNotFound();

        BioCollection<BioMetabolite> seeds = mapper.map(seedsFilePath).stream()
                .map(BioMetabolite.class::cast)
                .collect(BioCollection::new,BioCollection::add,BioCollection::addAll);
        BioCollection<BioMetabolite> bootstraps = mapper.map(sideCompoundFile).stream()
                .map(BioMetabolite.class::cast)
                .collect(BioCollection::new,BioCollection::add,BioCollection::addAll);
        BioCollection<BioReaction> forbidden = mapper.map(reactionToIgnoreFile).stream()
                .map(BioReaction.class::cast)
                .collect(BioCollection::new,BioCollection::add,BioCollection::addAll);

        ScopeCompounds scopeComp = new ScopeCompounds(graph, seeds, bootstraps,forbidden);
        BipartiteGraph scope = scopeComp.getScopeNetwork();
        ExportGraph.toGml(scope,output);
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Perform a network expansion from a set of compound seeds to create a scope network.\n" +
                "The scope of a set of compounds (seed) refer to the maximal metabolic network that can be extended from them," +
                "where the extension process consist of adding a reaction to the network if and only if all of its substrates " +
                "are either a seed or a product of a previously added reaction\n" +
                "For more information, see Handorf, Ebenhöh and Heinrich (2005). *Expanding metabolic networks: scopes of compounds, robustness, and evolution.* Journal of molecular evolution, 61(4), 498-512. (https://doi.org/10.1007/s00239-005-0027-1)";
    }
}
