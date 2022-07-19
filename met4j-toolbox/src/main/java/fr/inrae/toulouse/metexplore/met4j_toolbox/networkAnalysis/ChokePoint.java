package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DefaultWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class ChokePoint extends AbstractMet4jApplication {

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


    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        ChokePoint app = new ChokePoint();

        app.parseArguments(args);

        app.run();

    }

    public void run()  {

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
        System.err.println("reading SBML...");
        JsbmlReader reader = new JsbmlReader(this.inputPath);

        BioNetwork network = null;
        try {
            network = reader.read();
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //Create compound graph
        System.err.println("Creating network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        //Graph processing: side compound removal [optional]
        if (sideCompoundFile != null) {
            System.err.println("removing side compounds...");
            BioCollection<BioMetabolite> sideCpds = new BioCollection<>();

            try {
                BufferedReader fr = new BufferedReader(new FileReader(sideCompoundFile));
                String line;
                while ((line = fr.readLine()) != null) {
                    String sId = line.trim().split("\t")[0];
                    BioMetabolite s = network.getMetabolite(sId);
                    if (s != null) {
                        sideCpds.add(s);
                    } else {
                        System.err.println(sId + " side compound not found in network.");
                    }
                }
                fr.close();
                boolean removed = graph.removeAllVertices(sideCpds);
                if (removed) System.err.println(sideCpds.size() + " compounds removed.");
            } catch(IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

        //Graph processing: set weights
        WeightingPolicy wp = new DefaultWeightPolicy();
        wp.setWeight(graph);

        //compute loads
        System.err.println("Computing load points...");
        HashSet<BioReaction> choke = fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.ChokePoint.getChokePoint(graph);

        //export results
        System.err.println("Export results...");

        try {
            for (BioReaction r : choke) {
                fw.write(r.getId() + "\t" + r.getName() + "\t" + BioReactionUtils.getEquation(r, false, false) + "\n");
            }
        } catch(IOException e) {
            System.err.println("Error while writing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.err.println("done.");

        try {
            fw.close();
        } catch (IOException e) {
            System.err.println("Error while closing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\nLoad points constitute an indicator of lethality and can help identifying drug target " +
                "Choke points are reactions that are required to consume or produce one compound. Targeting of choke point can lead to the accumulation or the loss of some metabolites, thus choke points constitute an indicator of lethality and can help identifying drug target \n" +
                "See : Syed Asad Rahman, Dietmar Schomburg; Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinformatics 2006; 22 (14): 1767-1774. doi: 10.1093/bioinformatics/btl181";
    }

    @Override
    public String getShortDescription() {
        return "Compute the Choke points of a metabolic network.";
    }
}
