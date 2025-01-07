package fr.inrae.toulouse.metexplore.met4j_toolbox.mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_mapping.enrichment.PathwayEnrichment;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Tsv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.GROUPS;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.NOTES;

public class ORApathwayEnrichment extends AbstractMet4jApplication {


    @Option(name = "-th", aliases = {"--threshold"}, usage = "threshold to select significant pathways. No filtering if <=0")
    public double th = 0.0;

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", aliases = {"--sbml"}, usage = "Input model : SBML file with pathway annotation", required = true)
    public String sbml;

    @ParameterType(name = InputFile)
    @Format(name = Tsv)
    @Option(name = "-d", aliases = {"--data"}, usage = "Input data : Compounds of interest file, as one SBML specie identifier per line", required = true)
    public String input;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Tsv)
    @Option(name = "-o", aliases = {"--output"}, usage = "Output file : tabulated file with pathway identifier, pathway name, adjusted p-value.", required = true)
    public String outputFile;
    @Option(name = "-c", aliases = {"--correction"}, usage = "Method for multiple testing p-value adjustment.")
    public correction corr = correction.BenjaminiHochberg;

    public static void main(String[] args) {
        ORApathwayEnrichment app = new ORApathwayEnrichment();

        app.parseArguments(args);

        app.run();
    }

    private void run() {
        //open file
        FileWriter fw = null;
        try {
            fw = new FileWriter(outputFile);
        } catch (IOException e) {
            System.err.println("Error while opening the output file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        //import network
        System.out.print("Reading SBML...");
        BioNetwork network = IOUtils.readSbml(this.sbml, NOTES, GROUPS);
        System.out.println(" Done.");

        //import data
        System.out.println("Import data...");
        Mapper<BioMetabolite> metMapper = new Mapper<>(network, BioNetwork::getMetabolitesView).skipIfNotFound();
        HashSet<BioMetabolite> noi = null;
        try {
            noi = new HashSet<>(metMapper.map(input));
        } catch (IOException e) {
            System.err.println("Error while reading the compound of interest file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (metMapper.getNumberOfSkippedEntries() > 0)
            System.err.println(metMapper.getNumberOfSkippedEntries() + " compounds not found in network.");

        System.out.println(noi.size() + " compounds imported.");
        System.out.println(" Done.");

        //Run analysis
        System.out.print("Perform Pathway Enrichment...");
        int corrInt;
        switch (corr) {
            case BenjaminiHochberg:
                corrInt = PathwayEnrichment.BENJAMINIHOCHBERG;
                break;
            case HolmBonferroni:
                corrInt = PathwayEnrichment.HOLMBONFERRONI;
                break;
            case Bonferroni:
                corrInt = PathwayEnrichment.BONFERRONI;
                break;
            default:
                corrInt = PathwayEnrichment.BENJAMINIHOCHBERG;
                break;
        }
        PathwayEnrichment pe = new PathwayEnrichment(network, noi);
        Map<BioPathway, Double> pathwayScore = pe.computeEnrichment(corrInt);
        System.out.println(" Done.");

        //filter
        if (th > 0.0) {
            System.out.println("Select significant pathways...");
            pathwayScore = pathwayScore.entrySet().stream().filter(e -> e.getValue() <= th).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
            System.out.println("\t" + pathwayScore.size() + " significant pathways found.");
            System.out.println(" Done.");
        }

        //export
        System.out.println("Export results...");
        try {
            for (Map.Entry<BioPathway, Double> e : pathwayScore.entrySet()) {
                BioPathway m = e.getKey();
                fw.write(m.getId() + "\t" + m.getName() + "\t" + e.getValue() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("Error while writing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("done.");


    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getShortDescription() {
        return "Perform Over Representation Analysis for Pathway Enrichment, using one-tailed exact Fisher Test.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }

    @Override
    public String getLongDescription() {
        return "Perform Over Representation Analysis for Pathway Enrichment, using one-tailed exact Fisher Test.\n" +
                "The fisher exact test computes the probability p to randomly get the given set of values. \n" +
                "This version computes the probability to get at least the given overlap between the given set and the given modality :\n" +
                "Sum the hypergeometric probability with increasing target/query intersection cardinality.\n\n" +
                "The hypergeometric probability is computed from the following contingency table entries.\n" +
                "(values in cells correspond to the marginal totals of each intersection groups)\n" +
                "\t\t\t\tQuery\t!Query\n" +
                "\tTarget\t\ta\t\tb\n" +
                "\t!Target\t\tc\t\td\n" +
                "\n" +
                "The probability of obtaining the set of value is computed as following:\n" +
                "p = ((a+b)!(c+d)!(a+c)!(b+d)!)/(a!b!c!d!(a+b+c+d)!)\n" +
                "\n" +
                "The obtained p-value is then adjusted for multiple testing using one of the following methods:\n" +
                " - Bonferroni: adjusted p-value = p*n\n" +
                " - Benjamini-Hochberg: adjusted p-value = p*n/k\n" +
                " - Holm-Bonferroni: adjusted p-value = p*(n+1-k)\n" +
                "n : number of tests; k : pvalue rank";
    }

    enum correction {Bonferroni, BenjaminiHochberg, HolmBonferroni}
}
