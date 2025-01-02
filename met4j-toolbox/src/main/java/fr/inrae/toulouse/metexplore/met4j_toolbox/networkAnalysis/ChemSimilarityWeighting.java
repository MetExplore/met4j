package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.SimilarityWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_mapping.Mapper;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.EdgeWeighting;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.kohsuke.args4j.Option;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * App that provides tabulated compound graph edge list, with one column with reactant pair's chemical similarity
 */
public class ChemSimilarityWeighting extends EdgeWeighting {

    enum strategy {EState, Extended, KlekotaRoth, MACCS, PubChem}

    @Option(name = "-f", aliases = {"--fingerprint"}, usage = "The chemical fingerprint to use", required = false)
    public strategy type = strategy.Extended;

    @Option(name = "-sm", aliases = {"--smileFile"}, usage = "If not present in SBML's annotations, get structure from a tabulated file with first column as compound id and second column as SMILE string, no header. Ignored if inchi file is provided", required = false)
    public String smileFile;
    @Option(name = "-in", aliases = {"--inchiFile"}, usage = "If not present in SBML's annotations, get structure from a tabulated file with first column as compound id and second column as InChI string, no header.", required = false)
    public String inchiFile;

    @Option(name = "-d", aliases = {"--asDist"}, usage = "Use distance rather than similarity", required = false)
    public boolean dist = false;

    @Override
    public WeightingPolicy setWeightingPolicy() {
        SimilarityWeightPolicy wp = new SimilarityWeightPolicy();
        switch (type) {
            case EState:
                wp.setFingerprintType(FingerprintBuilder.ESTATE);
            case Extended:
                wp.setFingerprintType(FingerprintBuilder.EXTENDED);
            case KlekotaRoth:
                wp.setFingerprintType(FingerprintBuilder.KLEKOTAROTH);
            case MACCS:
                wp.setFingerprintType(FingerprintBuilder.MACCS);
            case PubChem:
                wp.setFingerprintType(FingerprintBuilder.PUBCHEM);
        }
        wp.useDistance(dist);
        return wp;
    }

    @Override
    public BioNetwork processNetwork(BioNetwork bn) {
        if (inchiFile != null) {
            Mapper m = new Mapper<>(bn, BioNetwork::getMetabolitesView)
                    .columnSeparator("\t")
                    .idColumn(1)
                    .skipIfNotFound();
            try {
                Map<BioMetabolite, List<String>> att = m.mapAttributes(new BufferedReader(new FileReader(inchiFile)));
                for (Map.Entry<BioMetabolite, List<String>> entry : att.entrySet()) {
                    entry.getKey().setInchi(entry.getValue().get(0));
                }
            } catch (IOException e) {
                System.err.println("Error reading InChI file");
                throw new RuntimeException(e);
            }
        } else if (smileFile != null) {
            Mapper m = new Mapper<>(bn, BioNetwork::getMetabolitesView)
                    .columnSeparator("\t")
                    .idColumn(1)
                    .skipIfNotFound();
            try {
                Map<BioMetabolite, List<String>> att = m.mapAttributes(new BufferedReader(new FileReader(smileFile)));
                for (Map.Entry<BioMetabolite, List<String>> entry : att.entrySet()) {
                    entry.getKey().setSmiles(entry.getValue().get(0));
                }
            } catch (IOException e) {
                System.err.println("Error reading InChI file");
                throw new RuntimeException(e);
            }
        }
        int s = 0;
        int i = 0;
        for (BioMetabolite m : bn.getMetabolitesView()) {
            if (!StringUtils.isVoid(m.getSmiles())) s++;
            if (!StringUtils.isVoid(m.getInchi())) i++;
        }
        System.out.println(s + "/" + bn.getMetabolitesView().size() + " metabolites with SMILE");
        System.out.println(i + "/" + bn.getMetabolitesView().size() + " metabolites with InChI");
        if ((i + s) == 0) {
            System.err.println("Error: no chemical structure provided, unable to compute chemical similarity");
            System.exit(1);
        }
        return bn;
    }

    public static void main(String[] args) throws Met4jSbmlReaderException, IOException {

        ChemSimilarityWeighting app = new ChemSimilarityWeighting();

        app.parseArguments(args);

        app.run();

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Provides tabulated compound graph edge list, with one column with reactant pair's chemical similarity." +
                "Chemical similarity has been proposed as edge weight for finding meaningful paths in metabolic networks," +
                " using shortest (lightest) path search.";
    }

    @Override
    public String getShortDescription() {
        return "Provides tabulated compound graph edge list, with one column with reactant pair's chemical similarity.";
    }

    @Override
    public Set<Doi> getDois() {
        Set<Doi> dois = new HashSet<>();
        dois.add(new Doi("https://doi.org/10.1093/bioinformatics/btg217"));
        dois.add(new Doi("https://doi.org/10.1093/bioinformatics/bti116"));
        dois.add(new Doi("https://doi.org/10.1093/bioinformatics/btu760"));
        return dois;
    }
}
