package fr.inrae.toulouse.metexplore.met4j_toolbox.generic;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.utils.ComputeAdjacencyMatrix;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.AttributeExporter;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.ExportMatrix.toCSV;

/**
 * Interface for apps that output files in graph formats
 */
public interface GraphOutPut {

    /**
     * Enum for the different formats
     */
    enum formatEnum {gml, tab, nodeList, json, matrix, jsonviz}

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
            AttributeExporter att = format.equals(formatEnum.jsonviz) ? getVizAttExporter(network) : getAttributeExporter(network);
            if(weighted) att.exportEdgeAttribute(edgeWeightName, e-> graph.getEdgeWeight(e));
            ExportGraph export = new ExportGraph<>(graph,att);

            switch (format) {
                case tab -> export.toTab(writer);
                case nodeList -> export.toNodeTab(writer);
                case gml -> export.toGml(writer);
                case json -> export.toJSONgraph(writer);
                case matrix -> this.exportToMatrix(graph, outputPath);
                case jsonviz -> this.exportToJSONviz(graph, att, writer);
                default -> throw new IllegalArgumentException("Unexpected value: " + format);
            }
        }catch (IOException e){
            System.err.println("Error while exporting graph");
            e.printStackTrace();
        }
    }

    /**
     * Export graph to jsongraph with metadata needed to visualize in MetExploreViz.
     * This will run a classical jsongraph export and then add metadata at the graph level,
     * obtained from the getDefaultVizAttributes method
     * @param graph the graph to export
     * @param att the object that select attributes to export
     * @param output the path to the output file
     * @throws IOException
     */
    default void exportToJSONviz(BioGraph graph, AttributeExporter att,  Writer output) throws IOException {
        JSONObject json = new JSONObject();
        if(graph instanceof CompoundGraph){
            json = ExportGraph.createJSONgraph((CompoundGraph) graph, att.compoundAttProvider, att.reactionEdgeAttProvider);
        } else if (graph instanceof ReactionGraph){
            json = ExportGraph.createJSONgraph((ReactionGraph) graph, att.reactionAttProvider, att.compoundEdgeAttProvider);
        } else if (graph instanceof BipartiteGraph){
            json = ExportGraph.createJSONgraph((BipartiteGraph) graph, att.bipNodeAttProvider, att.bipEdgeAttProvider);
        } else if (graph != null){
            json = ExportGraph.createJSONgraph((BioGraph<BioEntity, Edge<BioEntity>>) graph, att.defaultNodeAttProvider, att.defaultEdgeAttProvider);
        }

        JSONObject jgraph =  (JSONObject) json.get("graph");
        jgraph.put("metadata", getDefaultVizAttributes());
        json = new JSONObject();
        json.put("graph", jgraph);

        output.write(json.toJSONString());
        output.close();
    }

    /**
     * Create a json object containing the default metadata needed for importing a jsongraph in MetExploreViz
     * @return a json object
     */
    default JSONObject getDefaultVizAttributes(){
        JSONObject jmetadata = new JSONObject();
        JSONObject jstyle = new JSONObject();
        JSONObject jnodeStyles = new JSONObject();
        JSONObject jmetabolite = new JSONObject();
        jmetabolite.put("shape","circle");
        JSONObject jreaction = new JSONObject();
        jreaction.put("shape","rectangle");
        jnodeStyles.put("metabolite", jmetabolite);
        jnodeStyles.put("reaction", jreaction);
        jstyle.put("nodeStyles", jnodeStyles);
        jmetadata.put("style", jstyle);
        return jmetadata;
    }

    /**
     * Return an attribute exporter following MetExploreViz specifications
     * @param network
     * @return
     */
    default AttributeExporter getVizAttExporter(BioNetwork network){
        return getAttributeExporter(network)
                .exportNodeAttribute("type", n->{
                    if( n instanceof BioMetabolite) return "metabolite";
                    else if( n instanceof BioReaction) return "reaction";
                    else return n.getClass().getSimpleName();
                })
                .exportNodeAttribute("classes", n->{
                    if( n instanceof BioMetabolite) return "[\"metabolite\"]";
                    else if( n instanceof BioReaction) return "[\"reaction\"]";
                    else return "[\""+n.getClass().getSimpleName().toLowerCase()+"\"]";
                })
                .exportEdgeAttribute("reversible", e->{
                    if(e instanceof fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge){
                        return ((BipartiteEdge) e).isReversible();
                    }else if (e instanceof fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge){
                        return ((ReactionEdge) e).getReaction().isReversible();
                    }else return false;
                });
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
