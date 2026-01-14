package fr.inrae.toulouse.metexplore.met4j_toolbox.mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching.ChemicalNameMatcher;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.*;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Tsv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;

public class NameMatcher extends AbstractMet4jApplication {


    @Option(name = "-nMatch", usage = "[1] Number of matchs to return per name")
    public int n = 1;

    @Option(name = "-skip", usage = "[0] Number of lines to skip at the beginning of the compound file")
    public int nSkip = 0;

    @Option(name = "-col", usage = "[1] column containing compounds' names")
    public int col = 1;

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "Original sbml file", required = true)
    public String sbml;

    @ParameterType(name = InputFile)
    @Format(name = Tsv)
    @Option(name = "-compound", usage = "Compound file containing one column with compound names to search among the SBML entries", required = true)
    public String input;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Tsv)
    @Option(name = "-o", usage = "Output tabulated file", required = true)
    public String outputFile;

    @ParameterType(name = EnumParameterTypes.Text)
    @Option(name = "-c", usage = "[#] Comment String in the compound file. The lines beginning by this string won't be read")
    public String comment = "#";

    @ParameterType(name = EnumParameterTypes.Text)
    @Option(name = "-sep", usage = "[\\t] separator in the compound file to split the colmumns.")
    public String sep = "\t";

    public static void main(String[] args) {
        NameMatcher app = new NameMatcher();

        app.parseArguments(args);

        app.run();
    }

    /**
     * <p>readSbml.</p>
     *
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     */
    protected Map<String, List<String>> readSbml() {

        BioNetwork bn = IOUtils.readSbml(this.sbml);
        Map<String, List<String>> names = new HashMap<>();

        for (BioMetabolite m : bn.getMetabolitesView()) {
            names.computeIfAbsent(m.getName(), k -> new ArrayList<>()).add(m.getId());
        }
        return names;

    }

    protected List<String> readFile() {
        List<String> names = new ArrayList<>();
        try {
            BufferedReader fr = new BufferedReader(new FileReader(input));
            String line;
            int i = 0;
            while ((line = fr.readLine()) != null) {
                i++;
                if (i > nSkip && !line.startsWith(comment)) {
                    String name = line.trim().split(sep)[col - 1];
                    //TODO add check
                    names.add(name);
                }
            }
            fr.close();
        } catch (IOException e) {
            System.err.println("Error while reading the side compound file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return names;

    }

    private void run() {
        Map<String, List<String>> modelNames = this.readSbml();
        List<String> queries = this.readFile();
        ChemicalNameMatcher matcher = new ChemicalNameMatcher.Builder(modelNames.keySet())
                .DefaultProcessing()
                .build();

        try (PrintWriter writer = new PrintWriter(new FileWriter(this.outputFile, false))) {

            writer.println("input Name\tSBML Name\tSBML ID");

            for (String query : queries) {
                List<String> resList = matcher.getMatches(query, Double.valueOf(query.length()), n);
                if (resList.isEmpty()) {
                    writer.println(query + "\tNA\tNA");
                } else {
                    for (String res : resList) {
                        String sb = query + "\t" +
                                res +
                                "\t" +
                                String.join(",", modelNames.get(res));
                        writer.println(sb);
                    }
                }
            }
            System.err.println("Done.");
        } catch (IOException e) {
            System.err.println("Error while printing metabolites");
            System.exit(1);
        }

    }


    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Metabolic models and Metabolomics Data often refer compounds only by using their common names, " +
                "which vary greatly according to the source, thus impeding interoperability between models, databases " +
                "and experimental data. This requires a tedious step of manual mapping. Fuzzy matching is a range" +
                " of methods which can potentially helps fasten this process, by allowing the search for near-similar " +
                "names. Fuzzy matching is primarily designed for common language search engines and is frequently " +
                "based on edit distance, i.e. the number of edits to transform a character string into another, " +
                "effectively managing typo, case and special character variations, and allowing auto-completion. " +
                "However, edit-distance based search fall short when mapping chemical names: " +
                "As an example, alpha-D-Glucose et Glucose would require more edits than between Fructose and Glucose.\n\n" +
                "This tool runs edit-distance based fuzzy matching to perform near-similar name matching between a metabolic model " +
                "and a list of chemical names in a dataset. A harmonization processing is performed on chemical names with " +
                "substitutions of common patterns among synonyms, in order to create aliases on which classical fuzzy " +
                "matching can be run efficiently.";
    }

    @Override
    public String getShortDescription() {
        return "From a list of compound names, find the best matching metabolites in a SBML model using fuzzy name matching on harmonized aliases.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}
