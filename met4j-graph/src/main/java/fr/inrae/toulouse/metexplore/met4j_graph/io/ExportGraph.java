/*
 * Copyright INRAE (2020)
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
package fr.inrae.toulouse.metexplore.met4j_graph.io;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.gml.GmlExporter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Export information from graphs generated from {@link Bionetwork2BioGraph} into Cytoscape-readable files
 * @author clement
 */
public class ExportGraph<V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> {

	private final G graph;
	private final AttributeExporter att;

	/**
	 * Instantiates a new export graph.
	 * @param graph the graph to export
	 */
	public ExportGraph(G graph) {
		this.graph=graph;
		this.att = AttributeExporter.minimal();
	}

	/**
	 * Instantiates a new export graph.
	 * @param graph the graph to export
	 * @param att the object that will provide the attributes to export
	 */
	public ExportGraph(G graph, AttributeExporter att) {
		this.graph=graph;
		this.att = att;
	}

	/**
	 * Export node in tabular format. Each line is a node, with its identifier as first column,
	 * and its attributes (if set) as following columns
	 *
	 * @param output the output writer
	 */
	public void toNodeTab(Writer output){
		if(graph instanceof CompoundGraph){
			toNodeTab((CompoundGraph) graph, output, att.compoundAttProvider);
		} else if (graph instanceof ReactionGraph){
			toNodeTab((ReactionGraph) graph, output, att.reactionAttProvider);
		} else if (graph instanceof BipartiteGraph){
			toNodeTab((BipartiteGraph) graph, output, att.bipNodeAttProvider);
		} else if (graph != null){
			toNodeTab((BioGraph<BioEntity, Edge<BioEntity>>) graph, output, att.defaultNodeAttProvider);
		}
	}

	/**
	 * export graph in GML format
	 *
	 * @param output the output writer
	 */
	public void toGml(Writer output){
		if(graph instanceof CompoundGraph){
			toGml((CompoundGraph) graph, output, att.compoundAttProvider, att.reactionEdgeAttProvider);
		} else if (graph instanceof ReactionGraph){
			toGml((ReactionGraph) graph, output, att.reactionAttProvider, att.compoundEdgeAttProvider);
		} else if (graph instanceof BipartiteGraph){
			toGml((BipartiteGraph) graph, output, att.bipNodeAttProvider, att.bipEdgeAttProvider);
		} else if (graph != null){
			toGml((BioGraph<BioEntity, Edge<BioEntity>>) graph, output, (Function<BioEntity, Map<String, Attribute>>) att.defaultNodeAttProvider, att.defaultEdgeAttProvider);
		}
	}

	/**
	 * export graph in tabulated format : [source_label]\t[edge_label]\t[target_label]
	 * extra columns are added for edge attributes if set.
	 * @param output the output writer
	 */
	public void toTab(Writer output){
//		//TODO switch java 21
//		switch (graph) {
//			case CompoundGraph cg -> toTab(cg, outputPath,att.reactionEdgeLabelProvider);
//			case ReactionGraph rg -> toTab(rg, outputPath,att.compoundEdgeLabelProvider);
//			case BipartiteGraph bg -> toTab(bg, outputPath,att.bipEdgeLabelProvider);
//			default -> toTab(graph, outputPath, Edge::toString);
//		}
		if(graph instanceof CompoundGraph){
			toTab((CompoundGraph) graph, output, att.reactionEdgeLabelProvider, att.reactionEdgeAttProvider, att.getEdgeAttributeLabels());
		}
		else if(graph instanceof ReactionGraph){
			toTab((ReactionGraph) graph, output, att.compoundEdgeLabelProvider, att.compoundEdgeAttProvider, att.getEdgeAttributeLabels());
		}
		else if(graph instanceof BipartiteGraph){
			toTab((BipartiteGraph) graph, output, att.bipEdgeLabelProvider, att.bipEdgeAttProvider, att.getEdgeAttributeLabels());
		}
		else{
			toTab(graph, output, Edge::toString);
		}

	}

	/**
	 * export graph in JSONgraph format
	 *
	 * @param output the output writer
	 */
	public void toJSONgraph(Writer output) throws IOException {
		if(graph instanceof CompoundGraph){
			toJSONgraph((CompoundGraph) graph, output, att.compoundAttProvider, att.reactionEdgeAttProvider);
		} else if (graph instanceof ReactionGraph){
			toJSONgraph((ReactionGraph) graph, output, att.reactionAttProvider, att.compoundEdgeAttProvider);
		} else if (graph instanceof BipartiteGraph){
			toJSONgraph((BipartiteGraph) graph, output, att.bipNodeAttProvider, att.bipEdgeAttProvider);
		} else if (graph != null){
			toJSONgraph((BioGraph<BioEntity, Edge<BioEntity>>) graph, output, att.defaultNodeAttProvider, att.defaultEdgeAttProvider);
		}
	}



	/**
	 * Export node in tabular format. Each line is a node, with its identifier as first column,
	 * and its attributes as following columns
	 *
	 * @param <V> node type
	 * @param <E> edge type
	 * @param graph the graph
	 * @param output the output writer
	 * @param vertexAttProvider a function that takes a node and return a map with each attribute name as key and the attribute value as value.
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> void toNodeTab(G graph, Writer output, Function<V, Map<String, Attribute>> vertexAttProvider){
		String sep = "\t";
		TreeSet<String> attSet = new TreeSet<>();
		Map<String, Map<String,Attribute>> attMap = new HashMap<>();
		graph.vertexSet().forEach(v -> attMap.put(v.getId(),vertexAttProvider.apply(v)));
		attMap.values().forEach(m -> attSet.addAll(m.keySet()));
		StringBuilder header = new StringBuilder("Node_id").append(sep);
		int count = 0;
		for(String att:attSet){
			header.append(att);
			count++;
			if(count!=attSet.size()) header.append(sep);
		}
		header.append("\n");
		try {
			output.write(header.toString());
			for(Map.Entry<String,Map<String,Attribute>> v : attMap.entrySet()){
				StringBuilder line = new StringBuilder(v.getKey()).append(sep);
				for(String att:attSet){
					Attribute a = v.getValue().get(att);
					line.append(a==null ? "NA" : a.getValue()).append(sep);
				}
				line.append("\n");
				output.write(line.toString());
			}
			output.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Export node in gml format.
	 *
	 * @param <V> node type
	 * @param <E> edge type
	 * @param graph the graph
	 * @param output the output writer
	 * @param vertexAttProvider a function that takes a node and return a map with each attribute name as key and the attribute value as value.
	 * @param edgeAttProvider a function that takes an edge and return a map with each attribute name as key and the attribute value as value.
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> void toGml(G graph, Writer output,  Function<V, Map<String, Attribute>> vertexAttProvider,  Function<E, Map<String, Attribute>> edgeAttProvider){
        GmlExporter<V, E> gml
                = new GmlExporter<>();
        gml.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
        gml.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
        if(vertexAttProvider!=null){
            gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_VERTEX_ATTRIBUTES, true);
            gml.setVertexAttributeProvider(vertexAttProvider);
        }
        if(edgeAttProvider!=null) {
            gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_EDGE_ATTRIBUTES, true);
            gml.setEdgeAttributeProvider(edgeAttProvider);
        }
        gml.exportGraph(graph, output);
        System.out.println("GML created.");
    }


	/**
	 * export graph in tabulated format : [source_label]\t[edgeLabel]\t[target_label]
	 *
	 * @param <V> node type
	 * @param <E> edge type
	 * @param graph the compound graph
	 * @param output the output writer
	 * @param interactionProvider a function that takes an edge and return a string characterizing the interaction
	 */
	public static <V extends BioEntity,E extends Edge<V>> void toTab(BioGraph<V,E> graph, Writer output, Function<E,String> interactionProvider){
		ExportGraph.toTab(graph, output, interactionProvider, null, null);
	}

	/**
	 * export graph in tabulated format : [source_label]\t[edgeLabel]\t[target_label]
	 *
	 * @param <V> node type
	 * @param <E> edge type
	 * @param graph the compound graph
	 * @param output the output writer
	 * @param interactionProvider a function that takes an edge and return a string characterizing the interaction
	 * @param edgeAttProvider a function that takes an edge and return a map with each attribute name as key and the attribute value as value.
	 * @param edgeAttributes the edge attributes to export. Define the order of the columns in the output file.
	 */
	public static <V extends BioEntity,E extends Edge<V>> void toTab(BioGraph<V,E> graph, Writer output, Function<E,String> interactionProvider, Function<E, Map<String, Attribute>> edgeAttProvider, List<String> edgeAttributes){
		try {
			System.err.println("[warning] tabulated format will obfuscate isolated nodes");
			StringBuilder header = new StringBuilder("source\tinteraction\ttarget");
			if(edgeAttProvider!=null){
				for(String att : edgeAttributes){
					header.append("\t").append(att);
				}
			}
			header.append("\n");
			output.write(header.toString());
			for(E e: graph.edgeSet()){
				StringBuilder entry= new StringBuilder(e.getV1().getId() + "\t" + interactionProvider.apply(e) + "\t" + e.getV2().getId());
				if(edgeAttProvider!=null){
					Map<String, Attribute> attMap = edgeAttProvider.apply(e);
					for(String att : edgeAttributes){
						Attribute val = attMap.get(att);
						entry.append("\t").append(val==null ? "NA" : val.getValue());
					}
				}
				entry.append("\n");
				output.write(entry.toString());
			}
			output.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	/**
	 * export graph as JSONgraph
	 *
	 * @param graph the bio-graph
	 * @param output the output writer
	 * @param vertexAttProvider a function that takes a node and return a map with each attribute name as key and the attribute value as value.
	 * @param edgeAttProvider a function that takes an edge and return a map with each attribute name as key and the attribute value as value.
	 * @param <V> node type
	 * @param <E> edge type
	 * @throws IOException if unable to create output file
	 */
	public static <V extends BioEntity, E extends Edge<V>> void toJSONgraph(BioGraph<V,E> graph, Writer output, Function<V, Map<String, Attribute>> vertexAttProvider,  Function<E, Map<String, Attribute>> edgeAttProvider) throws IOException {
		JSONObject jo = new JSONObject();
			JSONObject jgraph = new JSONObject();
			jgraph.put("directed",true);
			jgraph.put("label","Met4J generated "+graph.getClass().getSimpleName());
			JSONObject jnodes = new JSONObject();
				for(V node : graph.vertexSet()){
					JSONObject jnodeAtt = new JSONObject();
						jnodeAtt.put("label", node.getName());
						JSONObject jnodeMetadata = new JSONObject();
							if(vertexAttProvider!=null){
								for(Map.Entry<String,Attribute> entry : vertexAttProvider.apply(node).entrySet()){
									jnodeMetadata.put(entry.getKey(),entry.getValue().getValue());
								}
							}else{
								jnodeMetadata.put("type",node.getClass().getSimpleName());
							}
						jnodeAtt.put("metadata",jnodeMetadata);
					jnodes.put(node.getId(),jnodeAtt);
				}
			jgraph.put("nodes",jnodes);
			JSONArray jedges = new JSONArray();
				for(E edge : graph.edgeSet()){
					JSONObject jedge = new JSONObject();
					jedge.put("source",edge.getV1().getId());
					jedge.put("target",edge.getV2().getId());
					jedge.put("label",edge.toString());
					if(vertexAttProvider!=null){
						JSONObject jnodeMetadata = new JSONObject();
						for(Map.Entry<String,Attribute> entry : edgeAttProvider.apply(edge).entrySet()){
							jnodeMetadata.put(entry.getKey(),entry.getValue().getValue());
						}
						jedge.put("metadata",jnodeMetadata);
					}
					jedges.add(jedge);
				}
			jgraph.put("edges",jedges);
		jo.put("graph", jgraph);

		output.write(jo.toJSONString());
		output.close();
	}
	
}
