package fr.inrae.toulouse.metexplore.met4j_toolbox.networkAnalysis;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.FormulaParser;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DefaultWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public class SideCompoundsScan extends AbstractMet4jApplication {

    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Option(name = "-o", usage = "output Side-Compounds file", required = true)
    public String outputPath = null;

    @Option(name = "-s", aliases = {"--onlySides"}, usage = "output compounds flagged as side-Compounds only")
    public boolean sideOnly = false;

    @Option(name = "-id", aliases = {"--onlyIds"}, usage = "do not report values in output, export ids list of compounds flagged as side-Compounds, allowing piping results")
    public boolean noReportValue = false;

    @Option(name = "-d", aliases = {"--degree"}, usage = "flag as side compounds any compounds with degree above threshold", forbids = {"-dp"})
    public int degree = 400;

    @Option(name = "-dp", aliases = {"--degreep"}, usage = "flag as side compounds the top x% of compounds according to their degree", forbids = {"-d"})
    public double degreePrecentile = Double.NaN;

    @Option(name = "-cc", aliases = {"--noCarbonSkeleton"}, usage = "flag as side compound any compounds with less than 2 carbons in formula")
    public Boolean flagInorganic = false;

    @Option(name = "-uf", aliases = {"--undefinedFormula"}, usage = "flag as side compound any compounds with no valid chemical formula")
    public Boolean flagNoFormula = false;

    @Option(name = "-er", aliases = {"--edgeRedundancy"}, usage = "flag as side compound any compound with a number of redundancy in incident edges (parallel edges connecting to the same neighbor) above the given threshold")
    public double parallelEdge = Double.NaN;

    enum strategy {by_name,by_id}
    @Option(name = "-m", aliases = {"--merge"}, usage = "Degree is shared between compounds in different compartments. " +
            "Use names if consistent and unambiguous across compartments, or identifiers if compartment suffix is present (id in form \"xxx_y\" with xxx as base identifier and y as compartment label).")
    public strategy mergingStrat = null;


    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        SideCompoundsScan app = new SideCompoundsScan();

        app.parseArguments(args);

        app.run();

    }


    public void run() throws IOException, Met4jSbmlReaderException {
        //open file
        FileWriter fw = new FileWriter(outputPath);

        //import network
        System.err.println("reading SBML...");
        System.err.println(inputPath);
        JsbmlReader reader = new JsbmlReader(this.inputPath);
        BioNetwork network = reader.read();

        //Create compound graph
        System.err.println("Creating network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        network = null;

        //Graph processing: set weights
        WeightingPolicy wp = new DefaultWeightPolicy();
        wp.setWeight(graph);


        //perform scan
        //------------
        System.err.println("Scaning...");

        //if merging compartment
        Map<String, Integer> mergedDegree = new HashMap<>();
        Boolean merge = (mergingStrat!=null);
        Function<BioMetabolite,String> getSharedId = BioMetabolite::getName;
        if(merge){
            if(mergingStrat.equals(strategy.by_id)) getSharedId = (new VertexContraction.MapByIdSubString("^(\\w+)_\\w$"))::commonField;

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
                if (merge){
                    degreeStats.addValue(mergedDegree.get(getSharedId.apply(v)));
                }else{
                    degreeStats.addValue(graph.degreeOf(v));
                }
            }
            dt = degreeStats.getPercentile(degreePrecentile);
        }

        //header
        Boolean reportValue = (!noReportValue);
        if (reportValue) {
            StringBuffer l = new StringBuffer("ID\tNAME");
            l.append("\tDEGREE");
            if (!Double.isNaN(parallelEdge)) l.append("\tINCIDENT_PARALLEL_EDGES");
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
            boolean sideFromDegree = (d >= degree);
            if (sideFromDegree) side = true;
            if (reportValue) l.append("\t" + d);

            //check parallel edge ratio
            if (!Double.isNaN(parallelEdge)) {
                int ipe = graph.edgesOf(v).size() - graph.neighborListOf(v).size();
                boolean sideFromParallel = (ipe > parallelEdge);
                if (sideFromParallel) side = true;
                if (reportValue) l.append("\t" + ipe);
            }

            //check formula
            if (flagInorganic || flagNoFormula) {
                String formula = v.getChemicalFormula();
                String inorganic = "?";
                String validFormula = "true";
                try{
                    FormulaParser fp = new FormulaParser(formula);
                    if(flagInorganic){
                        if(fp.isExpectedInorganic()){
                            inorganic = "true";
                            side = true;
                        }else{
                            inorganic = "false";
                        }
                    }
                }catch(IllegalArgumentException e){
                    if(flagNoFormula){
                        validFormula = "false";
                        side = true;
                    }

                }
                if (reportValue){
                    if(flagInorganic) l.append("\t" + inorganic);
                    if(flagNoFormula) l.append("\t" + validFormula);
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
        System.err.println("done");
        System.err.println("found " + count + " side compound among " + graph.vertexSet().size() + " compounds");
        System.err.println(outputPath);
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
                "- *Edge Redundancy*: Similar to degree, this criteria assume that side compounds are involved in many reactions, but in pairs with other side compounds.\n" +
                "Therefore, the transition from ATP to ADP will appear multiple time in the network, creating redundant 'parallel edges' between these two neighbors.\n" +
                "Having a high number of redundancy, i.e. edges that don't extends one's neighborhood, can point out cofactors while keeping converging pathways' products like pyruvate aside.  \n" +
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
        return "Scan a network to identify side-compounds.";
    }
}
