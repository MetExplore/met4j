package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.SourcesAndSinks;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;

public class SeedsAndTargets extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-i", aliases = {"--inputSBML"}, usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Txt)
    @Option(name = "-sc", aliases = {"--sideFile"}, usage = "input Side compound file", required = false)
    public String inputSide = null;

    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Format(name= EnumFormats.Tsv)
    @Option(name = "-o", aliases = {"--output"}, usage = "output seeds file", required = true)
    public String outputPath = null;

    @ParameterType(name= EnumParameterTypes.Text)
    @Option(name = "-c", aliases = {"--comp"}, usage = "Selected compartment(s), as model identifiers, separated by \"+\" sign if more than one", required = false)
    public String comp = null;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-s", aliases = {"--seeds"}, usage = "export seeds", required = false)
    public boolean source = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-t", aliases = {"--targets"}, usage = "export targets", required = false)
    public boolean sink = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-!s", aliases = {"--notSeed"}, usage = "export nodes that are not seed", required = false)
    public boolean notsource = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-!t", aliases = {"--notTarget"}, usage = "export nodes that are not targets", required = false)
    public boolean notsink = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-is", aliases = {"--keepIsolated"}, usage = "do not ignore isolated nodes, consider isolated both seed and target", required = false)
    public boolean keepIsolated = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-B", aliases = {"--useBorensteinAlg"}, usage = "use Borenstein Algorithm. Please cite Borenstein et al. 2008 Large-scale reconstruction and phylogenetic analysis of metabolic environments https://doi.org/10.1073/pnas.0806162105). ignore internal option", required = false)
    public boolean useBorensteinAlg = false;


    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-in", aliases = {"--internal"}, usage = "if an external compartment is defined, adjust degree by considering internal counterpart", required = false)
    public boolean useInternal = false;





    public static void main(String[] args) {

        SeedsAndTargets app = new SeedsAndTargets();

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
        if (inputSide != null) {
            System.err.println("removing side compounds...");
            NodeMapping<BioMetabolite, ReactionEdge, CompoundGraph> mapper = new NodeMapping<>(graph).skipIfNotFound();
            BioCollection<BioMetabolite> sideCpds = null;
            try {
                sideCpds = mapper.map(inputSide);
            } catch (IOException e) {
                System.err.println("Error while reading the side compound file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.err.println(sideCpds.size() + " compounds removed.");
        }

        //compute seeds and targets
        SourcesAndSinks ss = new SourcesAndSinks(graph)
                .selectNonSinks(notsink)
                .selectSinks(sink)
                .selectSources(source)
                .selectNonSources(notsource)
                .keepIsolated(keepIsolated)
                .useBorensteinAlgorithm(useBorensteinAlg);
        if (comp != null) {
            ss = ss.fromExternalCompartment(getCandidates(network, graph), useInternal);
        }
        BioCollection<BioMetabolite> res = ss.getSelection();

        //export results
        try {
            for (BioMetabolite m : res) {
                fw.write(m.getId() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("Error while writing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.err.println("done.");


    }

    private BioCollection<BioMetabolite> getCandidates(BioNetwork network, CompoundGraph graph){
        //Select Candidates
        BioCollection<BioMetabolite> compoundSet = new BioCollection<>();
        if(comp!=null){
            //for each "external" (available) compartment
            for(String id : comp.split("\\+")){
                BioCompartment c = network.getCompartmentsView().get(id);
                if(c!=null){
                    //add compound graph nodes belonging to external compartment as candidate
                    for(BioEntity e : c.getComponentsView()){
                        if(graph.vertexSet().contains(e)) compoundSet.add((BioMetabolite) e);
                    }
                }else{
                    System.out.println("Error: Compartment "+id+" not found in network, please check sbml file.");
                }
            }
        }else{
            compoundSet.addAll(graph.vertexSet());
        }
        return compoundSet;
    }



    @Override
    public String getLabel() {return this.getClass().getSimpleName();}

    @Override
    public String getLongDescription() {
        return "Identify exogenously acquired compounds, producible compounds exogenously available and/or dead ends metabolites from metabolic network topology. " +
                "Metabolic seeds and targets are useful for identifying medium requirements and metabolic capability, and thus enable analysis of metabolic ties within communities of organisms.\n" +
                "This application can use seed definition and SCC-based detection algorithm by Borenstein et al. or, alternatively, degree-based sink and source detection with compartment adjustment.\n" +
                "The first method (see Borenstein et al. 2008 Large-scale reconstruction and phylogenetic analysis of metabolic environments https://doi.org/10.1073/pnas.0806162105) " +
                "consider strongly connected components rather than individual nodes, thus, members of cycles can be considered as seed. " +
                "A sink from an external compartment can however be connected to a non sink internal counterpart, thus highlighting what could end up in the external compartment rather than what must be exported.\n" +
                "The second approach is neighborhood based and identify sources and sinks. Since \"real\" sinks and sources in intracellular compartment(s) may be involved in transport/exchange reactions " +
                "reversible by default, thus not allowing extracellular source or sink, an option allows to take " +
                "the degree (minus extracellular neighbors) of intracellular counterparts.";
    }

    @Override
    public String getShortDescription() {
        return "Identify exogenously acquired compounds, producible compounds exogenously available and/or dead ends metabolites from metabolic network topology";
    }
}
