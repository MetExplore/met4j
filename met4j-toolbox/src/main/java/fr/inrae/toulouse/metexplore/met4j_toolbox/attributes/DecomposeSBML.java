package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
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
import java.io.PrintWriter;

public class DecomposeSBML extends AbstractMet4jApplication {


    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Sbml)
    @Option(name = "-i", usage = "Input SBML file", required = true)
    public String sbml;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-m", aliases = {"--metabolites"}, usage = "Extract Metabolites", required = false)
    public Boolean printMetabolites = false;
    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-r", aliases = {"--reactions"}, usage = "Extract Reactions", required = false)
    public Boolean printReactions = false;
    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-c", aliases = {"--compartments"}, usage = "Extract Compartments", required = false)
    public Boolean printCompartments = false;
    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-p", aliases = {"--pathways"}, usage = "Extract Pathways", required = false)
    public Boolean printPathways = false;
    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-g", aliases = {"--genes"}, usage = "Extract Genes", required = false)
    public Boolean printGenes = false;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-nt", aliases = {"--noTypeCol"}, usage = "Do not output type column", required = false)
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
        DecomposeSBML app = new DecomposeSBML();
        app.parseArguments(args);
        app.run();
    }

    /**
     * <p>run.</p>
     */
    public void run() {

        String fileIn = this.sbml;
        JsbmlReader reader = new JsbmlReader(fileIn);
        BioNetwork network = null;
        try {
            network = reader.read();
        } catch (Met4jSbmlReaderException e) {
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

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
        return "Parse SBML to render list of composing entities: metabolites, reactions, genes, pathways and compartments. " +
                "The output file is a tsv with two columns, one with entities identifiers, and one with the entity type. " +
                "If no entity type is selected, by default all of them are taken into account. " +
                "Only identifiers are written, attributes can be extracted from dedicated apps or from the SBML2Tab.";
    }

    @Override
    public String getShortDescription() {
        return "Parse SBML to render list of composing entities: metabolites, reactions, genes and others.";
    }
}
