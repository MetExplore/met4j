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

import fr.inrae.toulouse.metexplore.met4j_chemUtils.FormulaParser;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.AtomMappingWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.RPAIRSlikePolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.ReactionProbabilityWeight;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.VertexContraction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.GraphOutPut;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.util.HashSet;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.*;

public class Sbml2CarbonSkeletonNet extends AbstractMet4jApplication implements GraphOutPut{

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name = Gsam)
    @ParameterType(name = InputFile)
    @Option(name = "-g", usage = "input GSAM file", required = true)
    public String inputAAM = null;

    @Option(name = "-ks", aliases = {"--keepSingleCarbon"}, usage = "keep edges involving single-carbon compounds, such as CO2 (requires formulas in SBML)")
    public boolean keepSingleCarbon = false;

    @Option(name = "-mc", aliases = {"--nocomp"}, usage = "merge compartments (requires unique compound names that are consistent across compartments)")
    public boolean mergeComp = false;

    @Option(name = "-me", aliases = {"--simple"}, usage = "merge parallel edges to produce a simple graph")
    public boolean mergeEdges = false;

    @Option(name = "-ri", aliases = {"--removeIsolatedNodes"}, usage = "remove isolated nodes")
    public boolean removeIsolated = false;

    @Option(name = "-un", aliases = {"--undirected"}, usage = "create as undirected")
    public boolean undirected = false;

    @Option(name = "-tp", aliases = {"--transitionproba"}, usage = "set transition probability as weight")
    public boolean computeWeight = false;

    @Option(name = "-f", aliases = {"--format"}, usage = "Format of the exported graph" +
            "Tabulated edge list by default (source id \t edge type \t target id). Other options include GML, JsonGraph, and tabulated node list (label \t node id \t node type).")
    public GraphOutPut.formatEnum format = GraphOutPut.formatEnum.tab;

    @Format(name = Txt)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output file: path to the tabulated file where the resulting network will be exported", required = true)
    public String output;

    @Option(name = "-main", aliases = {"--onlyMainTransition"}, usage = "Compute RPAIRS-like tags and keep only main transitions for each reaction")
    public boolean main = false;

    @Option(name = "-fi", aliases = {"--fromIndexes"}, usage = "Use GSAM output with carbon indexes")
    public boolean fromIndexes = false;

    public static void main(String[] args) {

        Sbml2CarbonSkeletonNet app = new Sbml2CarbonSkeletonNet();

        app.parseArguments(args);

        app.run();

    }


    public void run() {
        System.out.print("Reading SBML...");
        BioNetwork network = IOUtils.readSbml(this.inputPath, FBC, NOTES, ANNOTATIONS);
        System.out.println(network.getReactionsView().size()+ " reactions and "+network.getMetabolitesView().size()+" metabolites read.");
        System.out.println(" Done.");


        System.out.print("Building Network...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        CompoundGraph graph = builder.getCompoundGraph();
        System.out.println(graph.vertexSet().size()+ " nodes and "+graph.edgeSet().size()+" edges created.");
        System.out.println(" Done.");

        System.out.print("Processing atom mappings...");
        AtomMappingWeightPolicy wp = (fromIndexes ?
                new AtomMappingWeightPolicy().fromConservedCarbonIndexes(inputAAM) :
                new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(inputAAM)
        );
        wp = wp.binarize()
                .removeEdgeWithoutMapping()
                .removeEdgesWithoutConservedCarbon();

        if (main) {
            RPAIRSlikePolicy rwp = new RPAIRSlikePolicy(wp)
                    .removeSideTransitions()
                    .removeSpuriousTransitions();
            rwp.setWeight(graph);
        } else {
            wp.setWeight(graph);
            System.out.println("Done.");
        }

        //invert graph as undirected (copy edge weight to reversed edge)
        if (undirected) {
            System.out.print("Create Undirected...");
            graph.asUndirected();
            System.out.println(" Done.");
        }

        //merge compartment
        if (mergeComp) {
            System.out.print("Merging compartments...");
            VertexContraction vc = new VertexContraction();
            graph = vc.decompartmentalize(graph, new VertexContraction.MapByName());
            System.out.println(" Done.");
        }

        //remove single-carbon compounds
        if (!keepSingleCarbon) {
            System.out.println("Skip compounds with less than two carbons detected...");
            HashSet<BioMetabolite> toRemove = new HashSet<>();
            for (BioMetabolite n : graph.vertexSet()) {
                if (!graph.edgesOf(n).isEmpty()) {
                    String formula = n.getChemicalFormula();
                    try {
                        FormulaParser fp = new FormulaParser(formula);
                        if (fp.isExpectedInorganic()) {
                            graph.removeAllEdges(graph.edgesOf(n));
                            System.out.println("\tdisconnecting " + n.getName());
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("\tcan't define structure of " + n.getName());
                    }
                }
            }
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

        System.out.println(graph.vertexSet().size()+ " nodes and "+graph.edgeSet().size()+" edges created.");

        //export graph
        System.out.print("Exporting...");
        this.exportGraph(graph, format, output, computeWeight, "Shared_Carbons");
        System.out.println(" Done.");
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return "Metabolic networks used for quantitative analysis often contain links that are irrelevant for graph-based structural analysis. For example, inclusion of side compounds or modelling artifacts such as 'biomass' nodes." +
                " Focusing on links between compounds that share parts of their carbon skeleton allows to avoid many transitions involving side compounds, and removes entities without defined chemical structure. " +
                "This app produces a Carbon Skeleton Network relevant for graph-based analysis of metabolism, in GML or matrix format, from a SBML and an GSAM atom mapping file. " +
                "GSAM (see https://forgemia.inra.fr/metexplore/gsam) performs atom mapping at genome-scale level using the Reaction Decoder Tool (https://github.com/asad/ReactionDecoder) and allows to compute the number of conserved atoms of a given type between reactants." +
                "This app also enables Markov-chain based analysis of metabolic networks by computing reaction-normalized transition probabilities on the Carbon Skeleton Network.";
    }

    @Override
    public String getShortDescription() {
        return "Create a carbon skeleton graph representation of a SBML file content, using GSAM atom-mapping file (see https://forgemia.inra.fr/metexplore/gsam)";
    }

    public Set<Doi> getDois() {
        return Set.of();
    }
}

