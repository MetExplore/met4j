package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.IOException;

public class ExtractPathways extends AbstractMet4jApplication {

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name= EnumFormats.Sbml)
    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Option(name = "-o", usage = "output SBML file", required = true)
    public String outputPath = null;

    @Option(name = "-p", usage = "pathway identifiers, separated by \"+\" sign if more than one", required = true)
    public String pathwayId = null;

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException, Met4jSbmlWriterException {

        ExtractPathways app = new ExtractPathways();

        app.parseArguments(args);

        app.run();

    }


    public void run() throws IOException, Met4jSbmlReaderException, Met4jSbmlWriterException {
        //read smbl
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        BioNetwork network = reader.read();
        System.out.println("Number of reactions in original network: "+network.getReactionsView().size());
        System.out.println("Number of species in original network: "+network.getMetabolitesView().size());
        System.out.println("Number of genes in original network: "+network.getGenesView().size());


        //get all reactions & metabolites
        BioCollection<BioReaction> reactions = new BioCollection<>(network.getReactionsView());
        BioCollection<BioMetabolite> metabolites = new BioCollection<>(network.getMetabolitesView());
        BioCollection<BioGene> genes = new BioCollection<>(network.getGenesView());

        //get pathways
        BioCollection<BioPathway> pathways = new BioCollection<>();
        for(String id : pathwayId.split("\\+")){
            BioPathway pathway = network.getPathwaysView().get(id);
            if(pathway!=null){
                pathways.add(pathway);
                System.out.println("Number of reactions in pathway "+pathway.getName()+" ("+id+"): "+network.getReactionsFromPathways(pathway).size());
                System.out.println("Number of species in pathway "+pathway.getName()+" ("+id+"): "+network.getMetabolitesFromPathway(pathway).size());
                System.out.println("Number of genes in pathway "+pathway.getName()+" ("+id+"): "+network.getGenesFromPathways(pathway).size());
            }else{
                System.out.println("Error: Pathway "+id+" not found in network, please check sbml file.");
            }
        }

        //remove pathway's reactions and metabolites from list
        BioCollection<BioReaction> pathwaysReactions = network.getReactionsFromPathways(pathways);
        reactions.removeAll(pathwaysReactions);
        metabolites.removeAll(network.getMetabolitesFromReactions(pathwaysReactions));
        genes.removeAll(network.getGenesFromReactions(pathwaysReactions));

        //remove remaining reactions
        network.removeOnCascade(reactions);
        network.removeOnCascade(metabolites);
        network.removeOnCascade(genes);
        System.out.println("Number of reactions in network: "+network.getReactionsView().size());
        System.out.println("Number of species in network: "+network.getMetabolitesView().size());
        System.out.println("Number of genes in network: "+network.getGenesView().size());

        //export network
        JsbmlWriter w = new JsbmlWriter(outputPath, network);
        w.write();
        System.err.println("network exported.");
        return;
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "\"Extract pathway(s) from GSMN: From a SBML file, Create a sub-network SBML file including only a selection of pathways";
    }

    @Override
    public String getShortDescription() {
        return "Extract pathway(s) from GSMN";
    }
}
