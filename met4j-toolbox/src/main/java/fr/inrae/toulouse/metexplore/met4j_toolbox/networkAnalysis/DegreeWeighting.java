package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DegreeWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.EdgeWeighting;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * App that provides tabulated compound graph edge list, with one column with target's degree.
 */
public class DegreeWeighting extends EdgeWeighting {

    @Option(name = "-pow", aliases = {"--power"}, usage = "set weights as the degree raised to the power of number in parameter.", required = false)
    public int pow = 1;

    @Override
    public WeightingPolicy setWeightingPolicy() {
        DegreeWeightPolicy wp = new DegreeWeightPolicy(pow);
        return wp;
    }

    public static void main(String[] args) throws Met4jSbmlReaderException, IOException {

        DegreeWeighting app = new DegreeWeighting();

        app.parseArguments(args);

        app.run();

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() +
                "Degree has been proposed as edge weight for finding meaningful paths in metabolic networks," +
                " using shortest (lightest) path search.";
    }

    @Override
    public String getShortDescription() {
        return "Provides tabulated compound graph edge list, with one column with target's degree.";
    }

    @Override
    public Set<Doi> getDois()  {
        Set<Doi> dois = new HashSet<>();
        dois.add(new Doi("https://doi.org/10.1016/j.jmb.2005.09.079"));
        return dois;
    }
}
