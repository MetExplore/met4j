package fr.inrae.toulouse.metexplore.met4j_toolbox.generic;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.AttributeExporter;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix.toCSV;

/**
 * Interface for apps that output files in graph formats
 */
public interface GraphOutPut {

    /**
     * Enum for the different formats
     */
    enum formatEnum {gml, tab, nodeList, json, matrix}

    /**
     * Export a graph to a file
     * @param graph the graph to export
     * @param format the format to export to
     * @param outputPath the path to the output file
     */
    default void exportGraph(BioGraph graph, formatEnum format, String outputPath){
        exportGraph(graph, null, format, outputPath, false, null);
    }

    /**
     * Export a graph to a file
     * @param graph the graph to export
     * @param format the format to export to
     * @param outputPath the path to the output file
     * @param weighted whether to export edge weights
     * @param edgeWeightName the name of the edge weight attribute
     */
    default void exportGraph(BioGraph graph, BioNetwork network, formatEnum format, String outputPath, Boolean weighted, String edgeWeightName){
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            AttributeExporter att = getAttributeExporter(network);
            if(weighted) att.exportEdgeAttribute(edgeWeightName, e-> graph.getEdgeWeight(e));
            ExportGraph export = new ExportGraph<>(graph,att);

            switch (format) {
                case tab -> export.toTab(writer);
                case nodeList -> export.toNodeTab(writer);
                case gml -> export.toGml(writer);
                case json -> export.toJSONgraph(writer);
                case matrix -> this.exportToMatrix(graph, outputPath);
                default -> throw new IllegalArgumentException("Unexpected value: " + format);
            }
        }catch (IOException e){
            System.err.println("Error while exporting graph");
            e.printStackTrace();
        }
    }

    /**
     * Get the default attribute exporter
     * @return the attribute exporter
     */
    default AttributeExporter getAttributeExporter(BioNetwork network){
        return new AttributeExporter(network)
                .exportName().exportType().exportReversible().exportTransportFlag();
    }

    /**
     * Export the graph to a matrix
     * @param graph the graph to export
     * @param outputPath the path to the output file
     */
    default void exportToMatrix(BioGraph graph, String outputPath){
        ComputeAdjacencyMatrix adjBuilder = new ComputeAdjacencyMatrix<>(graph);
        toCSV(outputPath,adjBuilder.getadjacencyMatrix());
    }

}
