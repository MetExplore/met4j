package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
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

public class GetReactantsFromReactions extends AbstractMet4jApplication {


    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Sbml)
    @Option(name = "-i", usage = "Input SBML file", required = true)
    public String sbml;

    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Tsv)
    @Option(name = "-r", usage = "Input Reaction file", required = true)
    public String reactionFile;

    @ParameterType(name= EnumParameterTypes.Text)
    @Option(name = "-sep", usage = "Separator in reaction file")
    public String sep = "\t";

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-header", usage = "Skip reaction file header")
    public boolean hasHeader = false;

    @ParameterType(name= EnumParameterTypes.Integer)
    @Option(name = "-col", usage = "Column number in reaction file (first as 1)")
    public int i=1;

    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-s", aliases = {"--substrates"}, usage = "Extract substrates only")
    public Boolean printSubstrates = false;
    @ParameterType(name= EnumParameterTypes.Boolean)
    @Option(name = "-p", aliases = {"--products"}, usage = "Extract products only")
    public Boolean printProducts = false;

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
        GetReactantsFromReactions app = new GetReactantsFromReactions();
        app.parseArguments(args);
        app.run();
    }


    /**
     * <p>run.</p>
     */
    public void run() {

        //read SBML, create bionetwork
        String fileIn = this.sbml;
        BioNetwork network = IOUtils.readSbml(fileIn);

        //Import Reaction File
        BioCollection<BioReaction> input = new BioCollection<>();
        try {
            BioNetwork finalNetwork = network;
            Mapper map = new Mapper(finalNetwork, bioNetwork -> finalNetwork.getReactionsView())
                    .columnSeparator(sep)
                    .idColumn(i)
                    .skipIfNotFound();
            if(hasHeader) map = map.skipHeader();
            input = map.map(reactionFile);
            System.out.println(input.size()+" reactions mapped");
            System.out.println(map.getNumberOfSkippedEntries()+" reactions not found in model");
        } catch (IOException e) {
            System.err.println("Error while reading the Reaction file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //default case: everything printed
        if(!(printSubstrates|printProducts)){
            printSubstrates=printProducts=true;
        }

        //Print output
        try (PrintWriter writer = new PrintWriter(new FileWriter(this.outputFile, false))) {
            for (BioReaction r : input){
                BioCollection<BioMetabolite> metabolites = new BioCollection<>();
                if(printSubstrates || r.isReversible()) metabolites.addAll(network.getLefts(r));
                if(printProducts || r.isReversible()) metabolites.addAll(network.getRights(r));
                for (BioMetabolite m : metabolites) {
                    writer.println(r.getId()+"\t"+m.getId());
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
        return "Get reactant lists from a list of reactions and a Sbml file. Output a tab-separated file " +
                "with one row per reactant, reaction identifiers in first column, reactant identifiers in second column. " +
                "It can provides substrates, products, or both (by default). In the case of reversible reactions, " +
                "all reactants are considered as both substrates and products";
    }

    @Override
    public String getShortDescription() {
        return "Get reactant lists from a list of reactions and a SBML file.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
