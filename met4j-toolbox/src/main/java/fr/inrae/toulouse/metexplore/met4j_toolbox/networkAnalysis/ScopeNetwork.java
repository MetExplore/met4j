package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.ScopeCompounds;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.GraphOutPut;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Txt;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class ScopeNetwork extends AbstractMet4jApplication implements GraphOutPut {

    //arguments
    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file: path to network used for computing scope, in sbml format.", required = true)
    public String sbmlFilePath;

    @ParameterType(name = InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-s", aliases = {"--seeds"}, usage = "input seeds file: tabulated file containing node of interest ids", required = true)
    public String seedsFilePath;

    //options
    @ParameterType(name = InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-sc", aliases = {"--sides"}, usage = "an optional file containing list of ubiquitous side compounds to be considered available by default but ignored during expansion")
    public String sideCompoundFile = null;

    @Option(name = "-ssc", aliases = {"--showsides"}, usage = "show side compounds in output network", depends = {"-sc"})
    public boolean includeSides = false;

    @ParameterType(name = InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-ir", aliases = {"--ignore"}, usage = "an optional file containing list of reaction to ignore (forbid inclusion in scope")
    public String reactionToIgnoreFile = null;

    @Option(name = "-t", aliases = {"--trace"}, usage = "trace inclusion step index for each node in output")
    public boolean trace = false;

    @Option(name = "-f", aliases = {"--format"}, usage = "Format of the exported graph" +
            "Tabulated edge list by default (source id \t edge type \t target id). Other options include GML, JsonGraph, and tabulated node list (label \t node id \t node type).")
    public GraphOutPut.formatEnum format = GraphOutPut.formatEnum.tab;

    @Format(name = Txt)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output file: path to the tabulated file where the resulting network will be exported", required = true)
    public String output;



    public static void main(String[] args) {
        ScopeNetwork app = new ScopeNetwork();
        app.parseArguments(args);
        app.run();
    }


    public void run() {

        System.out.println("reading SBML...");
        BioNetwork network = IOUtils.readSbml(sbmlFilePath);
        BipartiteGraph graph = (new Bionetwork2BioGraph(network)).getBipartiteGraph();

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
            bootstraps = (sideCompoundFile == null) ? new BioCollection<>() : mapper.map(sideCompoundFile).stream()
                    .map(BioMetabolite.class::cast)
                    .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);

            for(BioMetabolite bootstrap : bootstraps){
                if(seeds.contains(bootstrap)) System.err.println("Warning: bootstrap compound "+bootstrap.getId()+" is also listed as a seed." +
                        " It will be ignored from the bootstrap list.");
            }
            bootstraps.removeAll(seeds);

        } catch (IOException e) {
            System.err.println("Error while reading the side compound file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        BioCollection<BioReaction> forbidden = null;
        try {
            forbidden = (reactionToIgnoreFile == null) ? new BioCollection<>() : mapper.map(reactionToIgnoreFile).stream()
                    .map(BioReaction.class::cast)
                    .collect(BioCollection::new, BioCollection::add, BioCollection::addAll);
        } catch (IOException e) {
            System.err.println("Error while reading the reactions-to-ignore file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        if (seeds.isEmpty()) {
            System.err.println("no seed available, computation aborted");
        } else {
            ScopeCompounds scopeComp = new ScopeCompounds(graph, seeds, bootstraps, forbidden);
            if (includeSides) scopeComp.includeBootstrapsInScope();
            if (trace) scopeComp.trace();
            BipartiteGraph scope = scopeComp.getScopeNetwork();
            //export sub-network
            this.exportGraph(scope, network, format, output, trace, "step");
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
                "are either a seed or a product of a previously added reaction";
    }

    @Override
    public String getShortDescription() {
        return "Perform a network expansion from a set of compound seeds to create a scope network";
    }

    @Override
    public Set<Doi> getDois() {
        Set<Doi> dois = new HashSet<>();
        dois.add(new Doi("https://doi.org/10.1007/s00239-005-0027-1"));
        return dois;
    }
}
