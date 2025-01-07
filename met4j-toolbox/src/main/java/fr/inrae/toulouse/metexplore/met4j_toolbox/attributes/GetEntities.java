package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.*;

public class GetEntities extends AbstractMet4jApplication {


    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Sbml)
    @Option(name = "-i", usage = "Input SBML file", required = true)
    public String sbml;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-m", aliases = {"--metabolites"}, usage = "Extract Metabolites")
    public Boolean printMetabolites = false;
    
    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-r", aliases = {"--reactions"}, usage = "Extract Reactions")
    public Boolean printReactions = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-c", aliases = {"--compartments"}, usage = "Extract Compartments")
    public Boolean printCompartments = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-p", aliases = {"--pathways"}, usage = "Extract Pathways")
    public Boolean printPathways = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-g", aliases = {"--genes"}, usage = "Extract Genes")
    public Boolean printGenes = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-nt", aliases = {"--noTypeCol"}, usage = "Do not write type column")
    public Boolean noTypeCol = false;

    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Format(name= EnumFormats.Tsv)
    @Option(name = "-o", usage = "Output file", required=true)
    public String outputFile;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        GetEntities app = new GetEntities();
        app.parseArguments(args);
        app.run();
    }

    /**
     * <p>run.</p>
     */
    public void run() {

        String fileIn = this.sbml;
        BioNetwork network = IOUtils.readSbml(fileIn, FBC, NOTES, GROUPS);

        //default case: everything printed
        if(!(printMetabolites|printReactions|printPathways|printGenes|printCompartments)){
            printMetabolites=printReactions=printPathways=printGenes=printCompartments=true;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(this.outputFile, false))) {

            if(!noTypeCol) writer.println("ID\tEntity.Type");
            if(printMetabolites){
                for (BioMetabolite metabolite : network.getMetabolitesView()) {
                    writer.println(noTypeCol ? metabolite.getId() : metabolite.getId() + "\tMETABOLITE");
                }
            }
            if(printReactions){
                for (BioReaction reaction : network.getReactionsView()) {
                    writer.println(noTypeCol ? reaction.getId() : reaction.getId() + "\tREACTION");
                }
            }
            if(printGenes){
                for (BioGene gene : network.getGenesView()) {
                    writer.println(noTypeCol ? gene.getId() : gene.getId() + "\tGENE");
                }
            }
            if(printPathways){
                for (BioPathway pathway : network.getPathwaysView()) {
                    writer.println(noTypeCol ? pathway.getId() : pathway.getId() + "\tPATHWAY");
                }
            }
            if(printCompartments){
                for (BioCompartment compartment : network.getCompartmentsView()) {
                    writer.println(noTypeCol ? compartment.getId() : compartment.getId() + "\tCOMPARTMENT");
                }
            }

        } catch (IOException e) {
            System.err.println("Error while writing SBML entities");
            System.exit(1);
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() +
                "The output file is a tabulated file with two columns, one with entity identifiers, and one with the entity type. " +
                "If no entity type is selected, all of them are returned by default. " +
                "Only identifiers are written, attributes can be extracted from dedicated apps or from the Sbml2Tab app.";
    }

    @Override
    public String getShortDescription() {
        return "Parse a SBML file to return a list of entities composing the network: metabolites, reactions, genes and others.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }


}
