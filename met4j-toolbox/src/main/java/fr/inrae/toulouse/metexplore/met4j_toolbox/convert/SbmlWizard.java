package fr.inrae.toulouse.metexplore.met4j_toolbox.convert;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.utils.BioNetworkUtils;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.utils.CompartmentMerger;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.ALL;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.getMetabolitesFromFile;

public class SbmlWizard extends AbstractMet4jApplication {

    @Format(name = EnumFormats.Sbml)
    @ParameterType(name = EnumParameterTypes.InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-rc", aliases = {"--removeC"}, usage = "file containing identifiers of compounds to remove from the metabolic network")
    public String inputSide = null;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-kc", aliases = {"--retainC"}, usage = "file containing identifiers of compounds to keep from the metabolic network")
    public String toKeepC = null;

    @Option(name = "-ric", aliases = {"--noIsolated"}, usage = "remove isolated compounds (not involved in any reaction)")
    public boolean removeIsolated;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-rr",aliases = {"--removeR"}, usage = "file containing identifiers of reactions to remove from the metabolic network")
    public String inputReactions = null;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-kr", aliases = {"--retainR"}, usage = "file containing identifiers of reactions to keep from the metabolic network")
    public String toKeepR = null;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Sbml)
    @Option(name = "-o", usage = "output SBML file", required = true)
    public String outputPath = null;

    @Option(name = "-r0", aliases = {"--noFlux"}, usage = "remove reactions with lower and upper flux bounds both set to 0.0")
    public boolean removeNoFlux;

    enum strategy {no, by_name, by_id}

    @Option(name = "-mc", aliases = {"--mergecomp"}, usage = "merge compartments using the provided strategy. " +
            "No merge by default. \"by_name\" can be used if names are consistent and unambiguous across compartments, \"by_id\" can be used if compartment suffix is present in compounds identifiers (id in form \"xxx_y\" with xxx as base identifier and y as compartment label).")
    public strategy mergingStrat = strategy.no;

    @Option(name = "-rdr", aliases = {"--noDuplicated"}, usage = "remove duplicated reactions (same reactants, same GPR)")
    public boolean removeDuplicated;


    @Option(name = "-rEX", aliases = {"--removeExchange"}, usage = "remove exchange reactions and species from given exchange compartment identifier")
    public String exchangeCompToRemove;

    public static void main(String[] args) throws Met4jSbmlWriterException, IOException {

        SbmlWizard app = new SbmlWizard();

        app.parseArguments(args);

        app.run();

    }


    public void run() throws Met4jSbmlWriterException, IOException {
        System.out.print("Reading SBML...");

        BioNetwork network = IOUtils.readSbml(inputPath, ALL);
        System.out.println(" Done.");

        //print info
        System.out.println("\n\n\tcompartments:\t"+network.getCompartmentsView().size());
        System.out.println("\tmetabolites:\t"+network.getMetabolitesView().size());
        System.out.println("\treactions:\t"+network.getReactionsView().size());
        System.out.println("\tenzymes:\t"+network.getEnzymesView().size());
        System.out.println("\tgenes:\t"+network.getGenesView().size());
        System.out.println("\tprotein:\t"+network.getProteinsView().size());
        System.out.println("\tpathway:\t"+network.getPathwaysView().size()+"\n\n");


        //retain compounds
        if (toKeepC != null) {
            BioCollection<BioMetabolite> toKeep = new BioCollection<>();
            System.out.println("retaining compounds...");
            Mapper<BioMetabolite> cmapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();

            try {
                toKeep = cmapper.map(toKeepC);
            } catch (IOException e) {
                System.err.println("Error while reading the compounds to keep file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (cmapper.getNumberOfSkippedEntries() > 0)
                System.out.println(cmapper.getNumberOfSkippedEntries() + " compounds not found in network.");

            for(BioMetabolite c : network.getMetabolitesView()){
                if(!toKeep.contains(c)){
                    network.removeOnCascade(c);
                }
            }
            System.out.println(toKeep.size() + " compounds retained in network.");
        }

        //side compound removal [optional]
        if (inputSide != null) {
            System.out.println("removing side compounds...");

            BioCollection<BioMetabolite> sideCpds = getMetabolitesFromFile(inputSide, network, "side compounds");

            for(BioMetabolite sc : sideCpds){
                network.removeOnCascade(sc);
            }
            System.out.println(sideCpds.size() + " side compounds removed from network.");
        }

        //retain reactions
        if(toKeepR != null){
            BioCollection<BioReaction> toKeep = new BioCollection<>();
            System.out.println("retaining reactions...");
            Mapper<BioReaction> rmapper = new Mapper<>(network, BioNetwork::getReactionsView).skipIfNotFound();

            try {
                toKeep = rmapper.map(toKeepR);
            } catch (IOException e) {
                System.err.println("Error while reading the reactions to keep file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (rmapper.getNumberOfSkippedEntries() > 0)
                System.out.println(rmapper.getNumberOfSkippedEntries() + " reactions not found in network.");

            for(BioReaction r : network.getReactionsView()){
                if(!toKeep.contains(r)){
                    network.removeOnCascade(r);
                }
            }
            System.out.println(toKeep.size() + " reactions retained in network.");
        }

        //irrelevant reaction removal [optional]
        if (inputReactions != null) {
            BioCollection<BioReaction> sideRxns = new BioCollection<>();
            System.out.println("removing side reaction...");
            Mapper<BioReaction> rmapper = new Mapper<>(network, BioNetwork::getReactionsView).skipIfNotFound();

            try {
                sideRxns = rmapper.map(inputReactions);
            } catch (IOException e) {
                System.err.println("Error while reading the irrelevant reactions file");
                System.err.println(e.getMessage());
                System.exit(1);
            }
            if (rmapper.getNumberOfSkippedEntries() > 0)
                System.out.println(rmapper.getNumberOfSkippedEntries() + " reactions not found in network.");

            for(BioReaction r : sideRxns){
                network.removeOnCascade(r);
            }
            System.out.println(sideRxns.size() + " irrelevant reactions removed from network.");
        }

        //removal of reactions that cannot hold flux in any condition
        if(removeNoFlux){
            System.out.println("removing reaction with null flux bounds...");
            BioCollection<BioReaction> toRemove = new BioCollection<>();
            for(BioReaction r : network.getReactionsView()){
                if(ReactionAttributes.getLowerBound(r).value==0.0 &&
                ReactionAttributes.getUpperBound(r).value==0.0){
                    toRemove.add(r);
                }
            }

            network.removeOnCascade(toRemove);
            System.out.println(toRemove.size() + " \"closed\" reactions removed from network.");
        }

        //exchange reaction removal
        if(exchangeCompToRemove!=null){
            System.out.println("removing external compartment...");
            BioCompartment exchange = network.getCompartment(exchangeCompToRemove);
            if(exchange==null){
                System.err.println("Exchange compartment not found, please check provided identifier");
            }else{
                int n = 0;
                for (BioEntity e : exchange.getComponentsView()){
                    network.removeOnCascade(e);
                    n++;
                }
                System.out.println(n + " external species removed from network.");
            }
        }


        //remove compounds not in any reactions
        if(removeIsolated){
            System.out.println("removing isolated compounds...");
            int n = network.getMetabolitesView().size();
            BioNetworkUtils.removeNotConnectedMetabolites(network);
            System.out.println((n-network.getMetabolitesView().size())+" isolated compounds removed from network.");
        }

        //merge compartment
        BioNetwork newNetwork;
        if (mergingStrat == strategy.by_id) {
            System.out.print("Merging compartments...");
            CompartmentMerger merger = new CompartmentMerger()
                    .usePalssonIdentifierConvention();
            newNetwork = merger.merge(network);
            System.out.println(" Done.");
        }else if (mergingStrat == strategy.by_name) {
            System.out.print("Merging compartments...");
            CompartmentMerger merger = new CompartmentMerger()
                    .setGetUniqIdFunction(BioMetabolite::getName);
            newNetwork = merger.merge(network);
            System.out.println(" Done.");
        }else{
            newNetwork = network;
        }

        //remove duplicated reactions
        if(removeDuplicated){
            System.out.println("removing duplicated reactions...");
            int n = newNetwork.getReactionsView().size();
            BioNetworkUtils.removeDuplicatedReactions(newNetwork,true);
            System.out.println((n-newNetwork.getReactionsView().size())+" duplicated reactions removed from network.");
        }

        //print info
        System.out.println("\n\n\tcompartments:\t"+newNetwork.getCompartmentsView().size());
        System.out.println("\tmetabolites:\t"+newNetwork.getMetabolitesView().size());
        System.out.println("\treactions:\t"+newNetwork.getReactionsView().size());
        System.out.println("\tenzymes:\t"+newNetwork.getEnzymesView().size());
        System.out.println("\tgenes:\t"+newNetwork.getGenesView().size());
        System.out.println("\tprotein:\t"+newNetwork.getProteinsView().size());
        System.out.println("\tpathway:\t"+newNetwork.getPathwaysView().size()+"\n\n");

        //export network
        System.out.print("Exporting...");
        IOUtils.writeSbml(newNetwork, outputPath);
        System.out.println(" Done.");
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "General SBML model processing including compound removal (such as side compounds or isolated compounds), reaction removal (ex. blocked or exchange reaction), and compartment merging";
    }

    @Override
    public String getShortDescription() {
        return "General SBML model processing";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
