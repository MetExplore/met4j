/*
 * Copyright INRAE (2026)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.utils.MetabolitesCoOccurrence;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Tsv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

/**
 * Export co-occurring metabolite sets observed on opposite sides of reactions.
 */
public class MetaboliteSetCooccurrence extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = Tsv)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output TSV file", required = true)
    public String outputPath = null;

    @Option(name = "-min", aliases = {"--minOccurrences"}, usage = "minimum number of co-occurrences to report (>=1)", required = true)
    public int minOccurrences = 1;

    @Option(name = "-max", aliases = {"--maxSubsetSize"}, usage = "maximum subset size for each side (default: 2)")
    public int maxSubsetSize = 2;

    public static void main(String[] args) {
        MetaboliteSetCooccurrence app = new MetaboliteSetCooccurrence();
        app.parseArguments(args);
        app.run();
    }

    public void run() {
        if (minOccurrences < 1) {
            System.err.println("Error: -min must be >= 1");
            System.exit(1);
        }
        if (maxSubsetSize < 1) {
            System.err.println("Error: -max must be >= 1");
            System.exit(1);
        }

        System.out.println("reading SBML...");
        BioNetwork network = IOUtils.readSbml(this.inputPath);

        System.out.println("computing metabolite set co-occurrences...");
        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> coOccurrences =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, minOccurrences, maxSubsetSize);

        // Stabilize output order to keep deterministic files.
        ArrayList<Map.Entry<MetabolitesCoOccurrence.ReactantPattern, Integer>> entries = new ArrayList<>(coOccurrences.entrySet());
        entries.sort(
                Comparator.<Map.Entry<MetabolitesCoOccurrence.ReactantPattern, Integer>>comparingInt(Map.Entry::getValue)
                        .reversed()
                        // For equal co-occurrences, list larger patterns first (supersets before subsets).
                        .thenComparing(Comparator.comparingInt((Map.Entry<MetabolitesCoOccurrence.ReactantPattern, Integer> e) ->
                                getTotalSetSize(e.getKey())).reversed())
                        .thenComparing(Comparator.comparingInt((Map.Entry<MetabolitesCoOccurrence.ReactantPattern, Integer> e) ->
                                getLargestSideSize(e.getKey())).reversed())
                        .thenComparing(e -> toCsv(e.getKey().left()))
                        .thenComparing(e -> toCsv(e.getKey().right()))
        );

        System.out.println("writing output...");
        try (FileWriter fw = new FileWriter(outputPath)) {
            for (Map.Entry<MetabolitesCoOccurrence.ReactantPattern, Integer> entry : entries) {
                MetabolitesCoOccurrence.ReactantPattern pattern = entry.getKey();
                fw.write(toCsv(pattern.left()) + "\t" + toCsv(pattern.right()) + "\t" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error while writing output file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("done.");
    }

    private static String toCsv(BioCollection<BioMetabolite> set) {
        List<String> sorted = new ArrayList<>(set.getIds());
        Collections.sort(sorted);
        return String.join(",", sorted);
    }

    private static int getTotalSetSize(MetabolitesCoOccurrence.ReactantPattern pattern) {
        return pattern.left().size() + pattern.right().size();
    }

    private static int getLargestSideSize(MetabolitesCoOccurrence.ReactantPattern pattern) {
        return Math.max(pattern.left().size(), pattern.right().size());
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return getShortDescription() + "\n" +
                "Each output row contains two metabolite sets observed on opposite sides of reactions " +
                "and the number of reactions where this pattern is found.";
    }

    @Override
    public String getShortDescription() {
        return "Detect co-occurring metabolite sets across reaction sides and export a tabulated file.";
    }

    @Override
    public Set<Doi> getDois() {
        return new HashSet<>();
    }
}
