/*
 * Copyright INRAE (2025)
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

package fr.inrae.toulouse.metexplore.met4j_toolbox.convert;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.*;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.GraphOutPut;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.util.HashSet;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Tsv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Txt;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.FBC;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.NOTES;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.getMetabolitesFromFile;

public class Sbml2CompoundGraph extends AbstractMet4jApplication implements GraphOutPut {

    @Format(name = EnumFormats.Sbml)
    @ParameterType(name = EnumParameterTypes.InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-sc", usage = "input Side compound file")
    public String inputSide = null;

    @Option(name = "-mc", aliases = {"--mergecomp"}, usage = "merge compartments. " +
            "Use names if consistent and unambiguous across compartments, or identifiers if compartment suffix is present (id in form \"xxx_y\" with xxx as base identifier and y as compartment label).")
    public strategy mergingStrat = strategy.no;
    public String idRegex = "^(\\w+)_\\w$";

    @Option(name = "-me", aliases = {"--simple"}, usage = "merge parallel edges to produce a simple graph")
    public boolean mergeEdges = false;

    @Option(name = "-ri", aliases = {"--removeIsolatedNodes"}, usage = "remove isolated nodes")
    public boolean removeIsolated = false;

    @Option(name = "-dw", aliases = {"--degreeWeights"}, usage = "penalize traversal of hubs by using degree square weighting", forbids = {"-cw"})
    public Boolean degree = false;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Tsv)
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for compound pairs", forbids = {"-dw"})
    public String weightFile = null;

    @Option(name = "-un", aliases = {"--undirected"}, usage = "create as undirected")
    public boolean undirected = false;

    @Option(name = "-tp", aliases = {"--transitionproba"}, usage = "set weight as random walk transition probability, normalized by reaction")
    public boolean computeWeight = false;

    @Option(name = "-f", aliases = {"--format"}, usage = "Format of the exported graph" +
            "Tabulated edge list by default (source id \t edge type \t target id). Other options include GML, JsonGraph, and tabulated node list (label \t node id \t node type).")
    public GraphOutPut.formatEnum format = GraphOutPut.formatEnum.tab;

    @Format(name = Txt)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output file: path to the tabulated file where the resulting network will be exported", required = true)
    public String output;

    public static void main(String[] args) {

        Sbml2CompoundGraph app = new Sbml2CompoundGraph();

        app.parseArguments(args);

        app.run();

    }

    public void run() {
        System.out.print("Reading SBML...");
        BioNetwork network = IOUtils.readSbml(inputPath, FBC, NOTES );
        System.out.println(" Done.");

        System.out.print("Building Network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        System.out.println(" Done.");

        //Graph processing: side compound removal [optional]
        if (inputSide != null) {
            System.out.println("removing side compounds...");
            BioCollection<BioMetabolite> sideCpds = getMetabolitesFromFile(inputSide, network, "side compounds");
            boolean removed = graph.removeAllVertices(sideCpds);
            if (removed) System.out.println(sideCpds.size() + " compounds removed.");
        }

        //Graph processing: set weights [optional]
        WeightingPolicy<BioMetabolite, ReactionEdge, CompoundGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            System.out.println("Setting edge weights...");
            wp = new WeightsFromFile(weightFile);
        } else if (degree && !undirected) {
            System.out.println("Setting edge weights...");
            int pow = 2;
            wp = new DegreeWeightPolicy(pow);
        }
        wp.setWeight(graph);
        System.out.println(" Done.");

        //invert graph as undirected (copy edge weight to reversed edge)
        if (undirected) {
            System.out.print("Create Undirected...");
            graph.asUndirected();
            System.out.println(" Done.");
            if (degree) {
                //since degree weighting policy is not symmetric, for undirected case we create reversed edges, apply
                //a corrected degree computation for each edge, and treat the graph as normal
                System.out.println("Setting edge weights (target degree)...");
                int pow = 2;
                wp = new DegreeWeightPolicy(1);
                wp.setWeight(graph);
                //adjust degree to ignore edges added for undirected case support
                WeightUtils.process(graph, x -> StrictMath.pow((x / 2), pow));
                System.out.println(" Done.");
            }
        }

        //merge compartment
        if (mergingStrat != strategy.no) {
            System.out.print("Merging compartments...");
            VertexContraction vc = new VertexContraction();
            VertexContraction.Mapper merger = mergingStrat.equals(strategy.by_name) ? new VertexContraction.MapByName() : new VertexContraction.MapByIdSubString(idRegex);
            graph = vc.decompartmentalize(graph, merger);
            System.out.println(" Done.");
        }

        //remove isolated nodes
        if (removeIsolated) {
            System.out.println("Remove isolated nodes...");
            HashSet<BioMetabolite> nodes = new HashSet<>(graph.vertexSet());
            graph.removeIsolatedNodes();
            nodes.removeAll(graph.vertexSet());
            for (BioMetabolite n : nodes) {
                System.out.println("\tremoving " + n.getName());
            }
            System.out.println(" Done.");
        }

        //compute transitions probability from weights
        if (computeWeight) {
            System.out.print("Compute transition matrix...");
            ReactionProbabilityWeight wp2 = new ReactionProbabilityWeight();
            wp2.setWeight(graph);
            System.out.println(" Done.");
        }

        //merge parallel edges
        if (mergeEdges) {
            System.out.print("Merging edges...");
            EdgeMerger.mergeEdgesWithOverride(graph);
            System.out.println(" Done.");
        }

        //export graph
        System.out.print("Exporting...");
        this.exportGraph(graph, format, output, computeWeight, "weight");
        System.out.println(" Done.");
    }

    @Override
    public void exportToMatrix(BioGraph graph, String outputPath){
        ComputeAdjacencyMatrix<BioMetabolite, ReactionEdge, CompoundGraph> adjBuilder = new ComputeAdjacencyMatrix<>(graph);
        if(!computeWeight) adjBuilder.parallelEdgeWeightsHandling(Math::max);
        ExportMatrix.toCSV(outputPath,adjBuilder.getadjacencyMatrix());
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes.\n" +
                "While Carbon Skeleton Graph offer a relevant alternative topology for graph-based analysis, it requires compounds' structure information, usually not provided in model, and difficult to retrieve for model with sparse cross-reference annotations.\n" +
                "In contrary to the Sbml2Graph app that performs a raw conversion of the SBML content, the present app propose a fine-tuned creation of compound graph from predefined list of side compounds and degree weighting to get relevant structure without structural data." +
                "This app also enables Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the network.";
    }

    @Override
    public String getShortDescription() {
        return "Advanced creation of a compound graph representation of a SBML file content";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }

    enum strategy {no, by_name, by_id}


}

