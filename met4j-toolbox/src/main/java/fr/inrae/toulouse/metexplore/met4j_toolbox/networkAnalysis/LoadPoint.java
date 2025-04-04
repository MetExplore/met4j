package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.getMetabolitesFromFile;

public class LoadPoint extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = Tsv)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output results file", required = true)
    public String outputPath = null;

    @Format(name = Txt)
    @ParameterType(name = InputFile)
    @Option(name = "-s", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore")
    public String sideCompoundFile = null;

    @Option(name = "-k", aliases = {"--npath"}, usage = "Number of alternative paths to consider between a pair of connected metabolites")
    public int k = 1;

    public static void main(String[] args) {

        LoadPoint app = new LoadPoint();

        app.parseArguments(args);

        app.run();

    }

    public void run() {
        //open file
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputPath);
        } catch (IOException e) {
            System.err.println("Error while opening the output file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //import network
        System.out.println("reading SBML...");
        BioNetwork network = IOUtils.readSbml(inputPath);

        //Create compound graph
        System.out.println("Creating network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        //Graph processing: side compound removal [optional]
        if (sideCompoundFile != null) {
            System.out.println("removing side compounds...");
            BioCollection<BioMetabolite> sideCpds = getMetabolitesFromFile(sideCompoundFile, network, "side compounds");
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.out.println(sideCpds.size() + " compounds removed.");
        }

        //Graph processing: set weights
        WeightingPolicy wp = new UnweightedPolicy();
        wp.setWeight(graph);

        //compute loads
        System.out.println("Computing load points...");
        fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.LoadPoint computor = new fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.LoadPoint<BioMetabolite, ReactionEdge, CompoundGraph>(graph);
        HashMap<BioMetabolite, Double> loads = computor.getLoads(k);

        //export results
        System.out.println("Export results...");

        try {
            for (Map.Entry<BioMetabolite, Double> e : loads.entrySet()) {
                BioMetabolite m = e.getKey();
                fw.write(m.getId() + "\t" + m.getName() + "\t" + e.getValue() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("Error while writing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("done.");

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() +
                "\nFrom Rahman et al. Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinf. (2006):\n" +
                "For a given metabolic network, the load L on metabolite m can be defined as :\n" +
                "ln [(pm/km)/(∑Mi=1Pi)/(∑Mi=1Ki)]\n" +
                "p is the number of shortest paths passing through a metabolite m;\n" +
                "k is the number of nearest neighbour links for m in the network;\n" +
                "P is the total number of shortest paths;\n" +
                "K is the sum of links in the metabolic network of M metabolites (where M is the number of metabolites in the network).\n" +
                "Use of the logarithm makes the relevant values more distinguishable.";
    }

    @Override
    public String getShortDescription() {
        return "Compute the Load points of a metabolic network. Load points constitute an indicator of lethality and can help identifying drug targets.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of(new Doi("https://doi.org/10.1093/bioinformatics/btl181"));
    }
}
