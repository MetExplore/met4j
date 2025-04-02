package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.FormulaParser;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Tsv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.FBC;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.NOTES;

/**
 *
 */
public class SideCompoundsScan extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = Tsv)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output file containing the side compounds", required = true)
    public String outputPath = null;

    @Option(name = "-s", aliases = {"--onlySides"}, usage = "output compounds flagged as side compounds only")
    public boolean sideOnly = false;

    @Option(name = "-id", aliases = {"--onlyIds"}, usage = "do not report values in output, export ids of compounds flagged as side compounds, allowing piping results")
    public boolean noReportValue = false;

    @Option(name = "-d", aliases = {"--degree"}, usage = "flag as side compounds any compound with degree above threshold", forbids = {"-dp"})
    public int degree = 400;

    @Option(name = "-dp", aliases = {"--degreep"}, usage = "flag as side compounds the top x% of compounds according to their degree", forbids = {"-d"})
    public double degreePrecentile = Double.NaN;

    @Option(name = "-cc", aliases = {"--noCarbonSkeleton"}, usage = "flag as side compound any compound with less than 2 carbons in formula")
    public Boolean flagInorganic = false;

    @Option(name = "-uf", aliases = {"--undefinedFormula"}, usage = "flag as side compound any compound with no valid chemical formula")
    public Boolean flagNoFormula = false;

    @Option(name = "-nc", aliases = {"--neighborCoupling"}, usage = "flag as side compound any compound with a number of parallel edges shared with a neighbor above the given threshold")
    public double parallelEdge = Double.NaN;
    @Option(name = "-m", aliases = {"--merge"}, usage = "degree is shared between compounds in different compartments. " +
            "Use names if consistent and unambiguous across compartments, or identifiers if compartment suffix is present (id in form \"xxx_y\" with xxx as base identifier and y as compartment label).")
    public strategy mergingStrat = strategy.no;

    public static void main(String[] args) {

        SideCompoundsScan app = new SideCompoundsScan();

        app.parseArguments(args);

        app.run();

    }

    public void run() {


        //import network
        System.out.println("reading SBML...");
        BioNetwork network = IOUtils.readSbml(this.inputPath, FBC, NOTES);

        //Create compound graph
        System.out.println("Creating network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();

        //Graph processing: set weights
        WeightingPolicy wp = new UnweightedPolicy();
        wp.setWeight(graph);


        //perform scan
        //------------
        System.out.println("Scaning...");

        //if merging compartment
        Map<String, Integer> mergedDegree = new HashMap<>();
        Boolean merge = (mergingStrat != strategy.no);
        Function<BioMetabolite, String> getSharedId = BioMetabolite::getName;
        if (merge) {
            if (mergingStrat.equals(strategy.by_id))
                getSharedId = (new VertexContraction.MapByIdSubString("^(\\w+)_\\w$"))::commonField;

            mergedDegree = graph.vertexSet().stream().collect(
                    Collectors.groupingBy(
                            getSharedId,
                            Collectors.summingInt(v -> graph.degreeOf(v))
                    )
            );
        }

        //degree statistics
        DescriptiveStatistics degreeStats = new DescriptiveStatistics();
        double dt = degree;
        if (!Double.isNaN(degreePrecentile)) {
            for (BioMetabolite v : graph.vertexSet()) {
                if (merge) {
                    degreeStats.addValue(mergedDegree.get(getSharedId.apply(v)));
                } else {
                    degreeStats.addValue(graph.degreeOf(v));
                }
            }
            dt = degreeStats.getPercentile(degreePrecentile);
        }

        //open file
        try {
            FileWriter fw = new FileWriter(outputPath);
            //header
            Boolean reportValue = (!noReportValue);
            if (reportValue) {
                StringBuffer l = new StringBuffer("ID\tNAME");
                l.append("\tDEGREE");
                if (!Double.isNaN(parallelEdge)) l.append("\tMAX_PARALLEL_EDGES");
                if (flagInorganic) l.append("\tNO_CARBON_BOND");
                if (flagNoFormula) l.append("\tVALID_CHEMICAL");
                l.append("\tIS_SIDE\n");
                fw.write(l.toString());
            }

            //if ids only, report side only
            if (noReportValue) sideOnly = true;

            int count = 0;
            for (BioMetabolite v : graph.vertexSet()) {

                boolean side = false;

                //check degree
                StringBuffer l = new StringBuffer(v.getId());
                if (reportValue) l.append("\t" + v.getName());

                int d = merge ? mergedDegree.get(getSharedId.apply(v)) : graph.degreeOf(v);
                boolean sideFromDegree = (d >= dt);
                if (sideFromDegree) side = true;
                if (reportValue) l.append("\t" + d);

                //check parallel edges
                if (!Double.isNaN(parallelEdge)) {
                    int maxIpe = 0;
                    for (BioMetabolite n : graph.neighborListOf(v)) {
                        int e = graph.getAllEdges(v, n).size() + graph.getAllEdges(n, v).size();
                        if (e > maxIpe) maxIpe = e;
                    }
                    boolean sideFromParallel = (maxIpe > parallelEdge);
                    if (sideFromParallel) side = true;
                    if (reportValue) l.append("\t" + maxIpe);
                }

                //check formula
                if (flagInorganic || flagNoFormula) {
                    String formula = v.getChemicalFormula();
                    String inorganic = "?";
                    String validFormula = "true";
                    try {
                        FormulaParser fp = new FormulaParser(formula);
                        if (flagInorganic) {
                            if (fp.isExpectedInorganic()) {
                                inorganic = "true";
                                side = true;
                            } else {
                                inorganic = "false";
                            }
                        }

                    } catch (IllegalArgumentException e) {
                        if (flagNoFormula) {
                            validFormula = "false";
                            side = true;
                        }

                    }
                    if (reportValue) {
                        if (flagInorganic) l.append("\t" + inorganic);
                        if (flagNoFormula) l.append("\t" + validFormula);
                    }
                }

                if (reportValue) l.append("\t" + side);

                if (!sideOnly || side) {
                    fw.write(l.toString());
                    fw.write("\n");
                }
                if (side) count++;
            }

            fw.close();
            System.out.println("done");
            System.out.println("found " + count + " side compound among " + graph.vertexSet().size() + " compounds");
        } catch (IOException e) {
            System.err.println("Error while writing the result file");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "Side compounds are metabolites of small relevance for topological analysis. Their definition can be quite subjective and varies between sources.\n" +
                "Side compounds tend to be ubiquitous and not specific to a particular biochemical or physiological process." +
                "Compounds usually considered as side compounds include water, atp or carbon dioxide. By being involved in many reactions and thus connected to many compounds, " +
                "they tend to significantly lower the average shortest path distances beyond expected metabolic relatedness.\n" +
                "This tool attempts to propose a list of side compounds according to specific criteria:  \n" +
                "- *Degree*: Compounds with an uncommonly high number of neighbors can betray a lack of process specificity.  \n" +
                "High degree compounds typically include water and most main cofactors (CoA, ATP, NADPH...) but can also include central compounds such as pyruvate or acetyl-CoA  \n" +
                "- *Neighbor Coupling*: Similar to degree, this criteria assume that side compounds are involved in many reactions, but in pairs with other side compounds.\n" +
                "Therefore, the transition from ATP to ADP will appear multiple times in the network, creating redundant 'parallel edges' between these two neighbors.\n" +
                "Being tightly coupled to another compound through a high number of redundant edges, can point out cofactors while keeping converging pathways' products with high degree like pyruvate aside.  \n" +
                "- *Carbon Count*: Metabolic \"waste\", or degradation end-product such as ammonia or carbon dioxide are usually considered as side compounds.\n" +
                "Most of them are inorganic compound, another ill-defined concept, sometimes defined as compound lacking C-C or C-H bonds. Since chemical structure is rarely available " +
                "in SBML model beyond chemical formula, we use a less restrictive criterion by flagging compound with one or no carbons. This cover most inorganic compounds, but include few compounds" +
                " such as methane usually considered as organic.  " +
                "- *Chemical Formula*: Metabolic network often contains 'artifacts' that serve modelling purpose (to define a composite objective function for example). " +
                "Such entities can be considered as 'side entities'. Since they are not actual chemical compounds, they can be detected by their lack of valid chemical formula. " +
                "However, this can also flag main compounds with erroneous or missing annotation.";
    }

    @Override
    public String getShortDescription() {
        return "Scan a network to identify side compounds.";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }

    enum strategy {no, by_name, by_id}
}
