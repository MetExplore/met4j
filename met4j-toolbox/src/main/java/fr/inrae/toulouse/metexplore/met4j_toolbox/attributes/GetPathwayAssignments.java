package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
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
import java.util.HashSet;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.*;

public class GetPathwayAssignments extends AbstractMet4jApplication {


    @ParameterType(name= EnumParameterTypes.InputFile)
    @Format(name= EnumFormats.Sbml)
    @Option(name = "-i", usage = "Input SBML file", required = true)
    public String sbml;

    public enum entity {METABOLITE, REACTION, GENE}
    @Option(name = "-entity", usage = "the type of entity assigned to pathway, either metabolite, reaction, or gene", required = false)
    public GetPathwayAssignments.entity export = entity.REACTION;

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
        GetPathwayAssignments app = new GetPathwayAssignments();
        app.parseArguments(args);
        app.run();
    }

    public record assignment(BioEntity e, BioPathway p) {}

    /**
     * <p>run.</p>
     */
    public void run() {

        //read SBML, create bionetwork
        String fileIn = this.sbml;
        BioNetwork network = IOUtils.readSbml(fileIn, NOTES, GROUPS);

        Set<assignment> assignments = new HashSet<>();
        for(BioPathway p : network.getPathwaysView()){
            BioCollection<BioPathway> collection = new BioCollection<>();
            collection.add(p);
            if(export.equals(entity.REACTION)){
                for(BioReaction r : network.getReactionsFromPathways(collection)){
                    assignments.add(new assignment(r, p));
                }
            }else if(export.equals(entity.GENE)){
                for(BioGene g : network.getGenesFromPathways(collection)){
                    assignments.add(new assignment(g, p));
                }
            } else if(export.equals(entity.METABOLITE)){
                for(BioMetabolite m : network.getMetabolitesFromPathway(p)){
                    assignments.add(new assignment(m, p));
                }
            }
        }

        //Print output
        try (PrintWriter writer = new PrintWriter(new FileWriter(this.outputFile, false))) {
            writer.println(export+"\tPATHWAY");
            for (assignment a : assignments){
                writer.println(a.e.getId()+"\t"+a.p.getName());
            }
        } catch (IOException e) {
            System.err.println("Error while writing Pathway assignments");
            System.exit(1);
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Get Pathway assignments of reactions, genes or metabolites in a metabolic network. The output is a tsv file with a first column with the reaction/gene/metabolite identifier in the network," +
                "and a second column with the pathway identifier. In a SBML model, reactions are assigned to pathways. In this app, a metabolite is assigned to a pathway if it is a reactant (substrate or product) of a reaction assigned to the pathway," +
                "and a gene is assigned to a pathway if it belongs to the GPR annotation of a reaction assigned to the pathway. In case of an entity being assigned to multiple pathways, each assignment will be outputed as a separated line in the file ";
    }

    @Override
    public String getShortDescription() {
        return "Get Pathway assignments of entities in a metabolic network";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
