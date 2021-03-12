package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.weighting.DefaultWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class ChokePoint  extends AbstractMet4jApplication {

    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Option(name = "-o", usage = "output results file", required = true)
    public String outputPath = null;

    @Option(name = "-s", aliases = {"--side"}, usage = "an optional file containing list of side compounds to ignore")
    public String sideCompoundFile = null;


    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        ChokePoint app = new ChokePoint();

        app.parseArguments(args);

        app.run();

    }

    public void run() throws IOException, Met4jSbmlReaderException {
        //open file
        FileWriter fw = new FileWriter(outputPath);

        //import network
        System.err.println("reading SBML...");
        JsbmlReader reader = new JsbmlReader(this.inputPath, false);
        BioNetwork network = reader.read();

        //Create compound graph
        System.err.println("Creating network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        network = null;

        //Graph processing: side compound removal [optional]
        if (sideCompoundFile != null) {
            System.err.println("Remove side compounds...");
            BioCollection<BioMetabolite> sideCpds = new BioCollection<>();
            BufferedReader fr = new BufferedReader(new FileReader(sideCompoundFile));
            String line;
            while ((line = fr.readLine()) != null) {
                BioMetabolite s = network.getMetabolitesView().get(line);
                sideCpds.add(s);
            }
            fr.close();
            graph.removeAllVertices(sideCpds);
        }

        //Graph processing: set weights
        WeightingPolicy wp = new DefaultWeightPolicy();
        wp.setWeight(graph);

        //compute loads
        System.err.println("Computing load points...");
        HashSet<BioReaction> choke = fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.ChokePoint.getChokePoint(graph);

        //export results
        System.err.println("Export results...");
        for(BioReaction r : choke){
            fw.write(r.getId()+"\t"+r.getName()+"\t"+r.toString()+"\n");
        }
        System.err.println("done.");
        fw.close();

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Compute the Choke points of a metabolic network. Load points constitute an indicator of lethality and can help identifying drug target " +
                "Choke points are reactions that are required to consume or produce one compound. Targeting of choke point can lead to the accumulation or the loss of some metabolites, thus choke points constitute an indicator of lethality and can help identifying drug target \n" +
                "See : Syed Asad Rahman, Dietmar Schomburg; Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinformatics 2006; 22 (14): 1767-1774. doi: 10.1093/bioinformatics/btl181";
    }
}
