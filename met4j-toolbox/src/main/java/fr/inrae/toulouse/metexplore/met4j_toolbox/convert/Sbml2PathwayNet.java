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
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.CustomWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.UnweightedPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.WeightsFromFile;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.EdgeMerger;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.AttributeExporter;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
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

import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.GROUPS;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.getMetabolitesFromFile;

public class Sbml2PathwayNet extends AbstractMet4jApplication implements GraphOutPut{

    @Format(name = EnumFormats.Sbml)
    @ParameterType(name = EnumParameterTypes.InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Txt)
    @Option(name = "-sc", usage = "input Side compound file (recommended)")
    public String inputSide = null;

    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Format(name = EnumFormats.Txt) // Txt because it can be a matrix or a GML file
    @Option(name = "-o", usage = "output Graph file", required = true)
    public String outputPath = null;

    @Option(name = "-ri", aliases = {"--removeIsolatedNodes"}, usage = "remove isolated nodes")
    public boolean removeIsolated = false;

    @Option(name = "-oss", aliases = {"--onlySourcesAndSinks"}, usage = "consider only metabolites that are source or sink in the pathway (i.e non-intermediary compounds)")
    public boolean onlySourcesAndSinks = false;

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Tsv)
    @Option(name = "-cw", aliases = {"--customWeights"}, usage = "an optional file containing weights for pathway pairs", forbids = {"-ncw"})
    public String weightFile = null;

    @Option(name = "-ncw", aliases = {"--connectorWeights"}, usage = "set number of connecting compounds as weight", forbids = {"-cw"})
    public Boolean connectors = false;

    @Option(name = "-f", aliases = {"--format"}, usage = "Format of the exported graph" +
            "Tabulated edge list by default (source id \t edge type \t target id). Other options include GML, JsonGraph, and tabulated node list (label \t node id \t node type).")
    public GraphOutPut.formatEnum format = GraphOutPut.formatEnum.tab;

    private BioNetwork finalNetwork;

    public static void main(String[] args) {

        Sbml2PathwayNet app = new Sbml2PathwayNet();

        app.parseArguments(args);

        app.run();

    }


    public void run() {
        System.out.print("Reading SBML...");
        BioNetwork network = IOUtils.readSbml(this.inputPath, GROUPS);
        System.out.println(" Done.");

        //Graph processing: side compound removal [optional]
        BioCollection<BioMetabolite> sideCpds = new BioCollection<>();
        if (inputSide != null) {
            sideCpds = getMetabolitesFromFile(inputSide, network, "side compounds");
        }

        System.out.print("Building Graph...");
        Bionetwork2BioGraph builder = new Bionetwork2BioGraph(network);
        PathwayGraph graph = onlySourcesAndSinks ? builder.getPathwayGraph(sideCpds) : builder.getPathwayOverlapGraph(sideCpds);
        System.out.println(" Done.");

        //Graph processing: set weights [optional]
        WeightingPolicy<BioPathway, PathwayGraphEdge, PathwayGraph> wp = new UnweightedPolicy<>();
        if (weightFile != null) {
            System.out.println("Setting edge weights...");
            wp = new WeightsFromFile(weightFile);
        } else if (connectors) {
            wp = new CustomWeightPolicy<>(e -> Double.valueOf(e.getConnectingCompounds().size()));
        }
        wp.setWeight(graph);
        System.out.println(" Done.");

        //remove isolated nodes
        if (removeIsolated) {
            System.out.println("Remove isolated nodes...");
            HashSet<BioPathway> nodes = new HashSet<>(graph.vertexSet());
            graph.removeIsolatedNodes();
            nodes.removeAll(graph.vertexSet());
            for (BioPathway n : nodes) {
                System.out.println("\tremoving " + n.getName());
            }
            System.out.println(" Done.");
        }

        //export graph
        System.out.print("Exporting...");
        finalNetwork = network;
        if(format != formatEnum.matrix && !onlySourcesAndSinks) EdgeMerger.undirectedMergeEdgesWithOverride(graph,null);
        this.exportGraph(graph, format, outputPath);
        System.out.println(" Done.");
    }

    @Override
    public AttributeExporter getAttributeExporter() {
        return new AttributeExporter()
                .exportName()
                .exportType()
                .exportReversible()
                .exportNodeAttribute("size", p -> finalNetwork.getMetabolitesFromPathway((BioPathway) p).size());
    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\nGenome-scale metabolic networks are often partitioned into metabolic pathways. Pathways are frequently " +
                "considered independently despite frequent coupling in their activity due to shared metabolites. In " +
                "order to decipher the interconnections linking overlapping pathways, this app proposes the creation of " +
                "\"Pathway Network\", where two pathways are linked if they share compounds.";
    }

    @Override
    public String getShortDescription() {
        return "Creation of a Pathway Network representation of a SBML file content";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }
}

