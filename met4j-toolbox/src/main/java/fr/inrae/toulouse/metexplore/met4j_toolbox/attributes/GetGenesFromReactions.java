package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
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

import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.FBC;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.NOTES;

public class GetGenesFromReactions extends AbstractMet4jApplication {


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

    @ParameterType(name= EnumParameterTypes.OutputFile)
    @Format(name= EnumFormats.Tsv)
    @Option(name = "-o", usage = "Output file", required=true)
    public String outputFile;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link String} objects.
     */
    public static void main(String[] args) {
        GetGenesFromReactions app = new GetGenesFromReactions();
        app.parseArguments(args);
        app.run();
    }


    /**
     * <p>run.</p>
     */
    public void run() {

        //read SBML, create bionetwork
        String fileIn = this.sbml;
        BioNetwork network = IOUtils.readSbml(fileIn, FBC, NOTES);

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

        //Print output
        try (PrintWriter writer = new PrintWriter(new FileWriter(this.outputFile, false))) {
            for (BioReaction r : input){
                BioCollection<BioGene> genes = network.getGenesFromReactions(r);
                for (BioGene g : genes) {
                    writer.println(r.getId()+"\t"+g.getId());
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
        return "Get associated gene list from a list of reactions and a SBML file. Parse SBML GPR annotations and output a tab-separated file " +
                "with one row per gene, associated reaction identifiers from input file in first column, gene identifiers in second column.";
    }

    @Override
    public String getShortDescription() {
        return "Get gene lists from a list of reactions and a SBML file.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
