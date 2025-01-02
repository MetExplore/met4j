package fr.inrae.toulouse.metexplore.met4j_toolbox.reconstruction;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork.CommunityNetworkBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork.PrefixedMetaEntityFactory;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.*;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.util.*;
import java.util.function.Function;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.reconstruction.CreateMetaNetwork.strategy.by_name;

public class CreateMetaNetwork extends AbstractMet4jApplication {

    //arguments
    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-n1", aliases = {"--network1"},  usage = "input SBML file: path to first network, in sbml format.", required = true)
    public String sbml1FilePath;
    @ParameterType(name = InputFile)
    @Option(name = "-n2", aliases = {"--network2"},  usage = "input SBML file: path to second network, in sbml format.", required = true)
    public String sbml2FilePath;

    @Option(name = "-n1ex", aliases = {"--external1"},  usage = "external compartment identifier in first network.", required = true)
    public String external1;
    @Option(name = "-n2ex", aliases = {"--external2"},  usage = "external compartment identifier in second network.", required = true)
    public String external2;

    @Option(name = "-n1px", aliases = {"--n1prefix"},  usage = "prefix that will be added to first network's entities identifiers", required = false)
    public String n1prefix = "Net1_";
    @Option(name = "-n2px", aliases = {"--n2prefix"},   usage = "prefix that will be added to second network's entities identifiers", required = false)
    public String n2prefix = "Net2_";

    @Option(name = "-k", aliases = {"--keepCompartment"}, usage = "keep the original external compartments in the meta-network, otherwise, they will be fused into the new shared external compartment", required = false)
    public boolean keepCompartment = false;

    @Option(name = "-n1meta", aliases = {"--firstAsMeta"}, usage = "Treat first network as meta-network, allowing more than two sub-models with iterative fusions. This will overwrite shared compartment and pool compounds (which must follow the \"pool_\" prefix convention) and will ignore --n1prefix argument", required = false)
    public boolean firstIsMeta = false;

    enum strategy {by_metanetx, by_name, by_id}
    @Option(name = "-mc", aliases = {"--mergingCriterion"}, usage = "field used to identify the same metabolites across the two different networks. " +
            "\"by_name\"/\"by_id\" can be used if names/identifiers are consistent and unambiguous across source models, \"by_metanetx\" can be used if models contains MetaNetX identifiers in annotation field using standard miriam format.")
    public CreateMetaNetwork.strategy mergingCriterion = by_name;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Sbml)
    @Option(name = "-o", usage = "output meta-network SBML file", required = true)
    public String outputPath = null;

    public static void main(String[] args) throws Met4jSbmlWriterException {
        CreateMetaNetwork app = new CreateMetaNetwork();
        app.parseArguments(args);
        app.run();
    }

    public void run() throws Met4jSbmlWriterException {

        if(Objects.equals(this.n1prefix, this.n2prefix)){
            System.err.println("Error: prefixes must be different");
            System.exit(1);
        }

        //import networks
        System.out.print("Importing network 1...");
        JsbmlReader reader = new JsbmlReader(this.sbml1FilePath);
        ArrayList<PackageParser> pkgs = new ArrayList<>(Arrays.asList(
                new NotesParser(false), new AnnotationParser(true), new FBCParser(), new GroupPathwayParser()));

        BioNetwork network1 = null;
        try {
            network1 = reader.read(pkgs);
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the first SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        BioCompartment co1ex = network1.getCompartment(external1);
        if(co1ex==null){
            System.err.println("Error: external compartment " + external1 + " not found in network 1");
            System.exit(1);
        }
        if(firstIsMeta){
            n1prefix = "";
            BioMetabolite[] pool1 = network1.getCompartment(external1)
                    .getComponentsView().stream()
                    .filter(e -> e instanceof BioMetabolite)
                    .filter(m -> m.getId().startsWith("pool_"))
                    .toArray(BioMetabolite[]::new);
            for(BioMetabolite pool : pool1){
                network1.removeOnCascade(network1.getReactionsFromMetabolite(pool));
            }
            network1.removeOnCascade(pool1);
        }
        System.out.println(" Done.");


        System.out.print("Importing network 2...");
        reader = new JsbmlReader(this.sbml2FilePath);
        BioNetwork network2 = null;
        try {
            network2 = reader.read(pkgs);
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the second SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        BioCompartment co2ex = network2.getCompartment(external2);
        if(co2ex==null){
            System.err.println("Error: external compartment " + external2 + " not found in network 2");
            System.exit(1);
        }
        System.out.println(" Done.");

        System.out.print("Creating meta-network...");
        //setup
        BioCompartment medium=new BioCompartment("medium"); medium.setName("medium");
        HashMap<BioNetwork,String> alias = new HashMap<>();
        alias.put(network1,n1prefix);alias.put(network2,n2prefix);
        CommunityNetworkBuilder builder = initMetaNetworkBuilder(medium, alias);
        builder.add(network1);
        builder.add(network2);

        //build meta-network
        if(!keepCompartment){
            builder.fuseCompartmentIntoSharedCompartment(network1,co1ex,medium);
            builder.fuseCompartmentIntoSharedCompartment(network2,co2ex,medium);
        }else{
            if(firstIsMeta) {
                builder.fuseCompartmentIntoSharedCompartment(network1, co1ex, medium);
            }else{
                builder.bumpCompartmentIntoSharedCompartment(network1, co1ex, medium);
            }
            builder.bumpCompartmentIntoSharedCompartment(network2,co2ex,medium);
        }
        BioNetwork metaNetwork = builder.build();
        System.out.println(" Done.");

        //export the meta-network
        System.out.print("Exporting MetaNetwork...");
        NetworkAttributes.addUnitDefinition(metaNetwork, new BioUnitDefinition());
        new JsbmlWriter(outputPath,metaNetwork).write();
        System.out.println(" Done.");

    }

    private CommunityNetworkBuilder initMetaNetworkBuilder(BioCompartment medium, HashMap<BioNetwork, String> alias) {
        CommunityNetworkBuilder builder = new CommunityNetworkBuilder(medium);
        PrefixedMetaEntityFactory factory = new PrefixedMetaEntityFactory(alias,"pool_");
        factory.addCompSuffix = (id, comp) -> id+"_"+comp.getId();
        builder.setEntityFactory(factory);
        Function<BioMetabolite, String> getSharedIdFunction;
        switch (mergingCriterion) {
            case by_metanetx:
                getSharedIdFunction = x -> {
                    try {
                        BioRef r = x.getRefs("metanetx.chemical").iterator().next();
                        return r.getId();
                    } catch (NoSuchElementException | NullPointerException e) {
                        return "unknown_"+UUID.randomUUID();
                    }
                };
                break;
            case by_id:
                getSharedIdFunction = BioEntity::getId;
                break;
            case by_name:
                getSharedIdFunction = BioEntity::getName;
                break;
            default:
                throw new IllegalArgumentException("Invalid merging criterion: " + mergingCriterion);
        }
        builder.setGetSharedIdFunction(getSharedIdFunction);
        return builder;
    }

    @Override
    public String getLabel() {return this.getClass().getSimpleName();}

    @Override
    public String getShortDescription() {
        return "Create a Meta-Network from two sub-networks in SBML format.";
    }

    @Override
    public String getLongDescription() {
        return "Create a Meta-Network from two sub-networks in SBML format. A meta-network is a single model which contains several sub-networks that remains individualized within" +
                "the meta-network (as opposed to models fusion), but which can share some of their components with " +
                "other sub-networks through a shared \"medium\" compartment.";
    }
}
