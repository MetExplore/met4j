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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollections;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.gml.GmlExporter;

/**
 * Export informations from graphs generated from {@link Bionetwork2BioGraph} into Cytoscape-readable files
 * @author clement
 * @version $Id: $Id
 */
public class ExportGraph {

	/**
	 * Instantiates a new export graph.
	 */
	private ExportGraph() {}
	
	/**
	 * Export edge weight in Cytoscape Edge Attribute File
	 * WARNING: equals sign aren't allowed in node name space
	 * Use in Cytoscape2:
	 * File>import>Edge_Attributes...
	 * @param graph the graph
	 * @param attName the Name of the attribute represented by weight
	 * @param outputPath the Path to the output file. Note that standard extension for Cytoscape edge attributes file is .eda
	 */
	public static void exportEdgeWeight(CompoundGraph graph, String attName, String outputPath){
		attName=attName.replaceAll("\\s+", "_");
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write(attName+" (class=Double)");
    		bw.newLine();
    		for(ReactionEdge e:graph.edgeSet()){
    			String edgeID = e.getV1().getId()+" ("+ e +") "+e.getV2().getId();
    			if(edgeID.contains("=")){
    				bw.close();
    				throw new IOException("input badly formated : equal sign in namespace");
    			}
				String entry=edgeID+" = "+graph.getEdgeWeight(e);
				bw.write(entry);
	    		bw.newLine();
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export edge score.
	 *
	 * @param graph the graph
	 * @param attName the attribute name
	 * @param outputPath the output path
	 */
	public static void exportEdgeScore(CompoundGraph graph, String attName, String outputPath){
		attName=attName.replaceAll("\\s+", "_");
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write(attName+" (class=Double)");
    		bw.newLine();
    		for(ReactionEdge e:graph.edgeSet()){
    			String edgeID = e.getV1().getId()+" ("+ e +") "+e.getV2().getId();
    			if(edgeID.contains("=")){
    				bw.close();
    				throw new IOException("input badly formated : equal sign in namespace");
    			}
    			
				String entry=edgeID+" = "+graph.getEdgeScore(e);
				bw.write(entry);
				bw.newLine();
				
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export edge attribute.
	 *
	 * @param graph the graph
	 * @param attName the attribute name
	 * @param outputPath the output path
	 */
	public static void exportEdgeAtt(CompoundGraph graph, String attName, String outputPath){
		attName=attName.replaceAll("\\s+", "_");
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write(attName);
    		bw.newLine();
    		for(ReactionEdge e:graph.edgeSet()){
    			String edgeID = e.getV1().getId()+" ("+ e +") "+e.getV2().getId();
    			if(edgeID.contains("=")){
    				bw.close();
    				throw new IOException("input badly formated : equal sign in namespace");
    			}
    			
				String entry=edgeID+" = "+ e.getAttributes().get(attName);
				bw.write(entry);
				bw.newLine();
				
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export nodes name in Cytoscape Node Attribute File
	 * WARNING: equals sign aren't allowed in node name space or id
	 * Use in Cytoscape2:
	 * File>import>Node_Attributes...
	 * @param graph the graph
	 * @param outputPath Path to the output file. Note that standard extension for Cytoscape node attributes file is .noa
	 */
	public static void exportNodeName(CompoundGraph graph, String outputPath){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("Name (class=String)");
    		bw.newLine();
    		for(BioMetabolite e:graph.vertexSet()){
    			String nodeID = e.getId();
    			String nodeName = e.getName();
    			if(nodeID.contains("=") || nodeName.contains("=")){
    				bw.close();
    				throw new IOException("input badly formated : equal sign in namespace");
    			}
				String entry=nodeID+" = "+nodeName;
				bw.write(entry);
	    		bw.newLine();
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export node name.
	 *
	 * @param bn the bioNetwork
	 * @param outputPath the output path
	 */
	public static void exportNodeName(BioNetwork bn, String outputPath){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("Name (class=String)");
    		bw.newLine();
    		for(BioMetabolite e:bn.getMetabolitesView()){
    			String nodeID = e.getId();
    			String nodeName = e.getName();
    			if(nodeID.contains("=") || nodeName.contains("=")){
    				bw.close();
    				throw new IOException("input badly formated : equal sign in namespace");
    			}
				String entry=nodeID+" = "+nodeName;
				bw.write(entry);
	    		bw.newLine();
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export some edges attributes in tabular format, with Cytoscape-readable ids for mapping
	 * Format:
	 * EdgeId	substrateId	substrateName	substrateCompartmnt	reactionId	reactionName	productId	productName	productCompartmnt	EdgeWeight
	 * Use in Cytoscape2:
	 * File>import>Attribute_from_Table
	 * 	Mapping column and attributes' names have to been set manually
	 * @param graph the graph
	 * @param outputPath the output path
	 */
	public static void exportEdgeTabular(CompoundGraph graph, String outputPath){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("CanonicalName\tSubstrateId\tSubstrateName\tSubstrateComp\tReactionId\tReactionName\tProductId\tProductName\tProductComp\tWeight\tScore");
    		bw.newLine();
    		for(ReactionEdge e:graph.edgeSet()){
    			BioReaction r = e.getReaction();
    			BioCollection<BioReactant> substrates = r.isReversible() ? BioCollections.union(r.getLeftReactantsView(), r.getRightReactantsView()) : r.getLeftReactantsView();
    			BioCollection<BioReactant> products = r.isReversible() ? BioCollections.union(r.getLeftReactantsView(), r.getRightReactantsView()) : r.getRightReactantsView();
    			BioMetabolite v1 = e.getV1();
    			BioMetabolite v2 = e.getV2();
				String entry=v1.getId()+" ("+ e +") "+v2.getId()+"\t"
						+v1.getId()+"\t"+v1.getName()+"\t"+substrates.get(v1.getId()).getLocation().getId()+"\t"+ e +"\t"+e.getReaction().getName()+"\t"+e.getV2().getId()+"\t"+e.getV2().getName()+"\t"+products.get(v2.getId()).getLocation().getId()+"\t"+graph.getEdgeWeight(e)+"\t"+graph.getEdgeScore(e);
				bw.write(entry);
	    		bw.newLine();
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export node in tabular format.
	 *
	 * @param graph the graph
	 * @param outputPath the output path
	 */
	public static void exportNodeTabular(CompoundGraph graph, String outputPath){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("CanonicalName\tName");
    		bw.newLine();
    		for(BioMetabolite v : graph.vertexSet()){
				String entry = v.getId()+"\t"+v.getName();
				bw.write(entry);
	    		bw.newLine();
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export node in tabular format.
	 *
	 * @param graph the graph
	 * @param outputPath the output path
	 * @param weights the weights
	 */
	public static void exportNodeTabular(CompoundGraph graph, String outputPath, HashMap<String, Double> weights){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("CanonicalName\tName\tWeight");
    		bw.newLine();
    		for(BioMetabolite v : graph.vertexSet()){
    			double weight = (weights.get(v.getId())==null) ? 0 : weights.get(v.getId());
				String entry = v.getId()+"\t"+v.getName()+"\t"+weight;
				bw.write(entry);
	    		bw.newLine();
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Export jgrapht graph into gml format
	 *
	 * @param graph the graph
	 * @param outputPath the output path
	 * @param <V> a V object.
	 * @param <E> a E object.
	 * @param <G> a G object.
	 */
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>>void toGml(G graph, String outputPath){

		try {
			GmlExporter<V, E> gml
				= new GmlExporter<>();
			gml.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
			FileWriter fw = new FileWriter(new File(outputPath).getAbsoluteFile());
			PrintWriter pw = new PrintWriter(fw);
			gml.exportGraph(graph, pw);
			System.out.println(outputPath+" created.");
		} catch (IOException e) {
			System.err.println("Error in file export!");
			e.printStackTrace();
		}
	}
	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> void toGmlWithAttributes(G graph, String outputPath, Map<V,?> att, String attName){
		toGmlWithAttributes(graph, outputPath, att, attName,false);
	}

	public static <V extends BioEntity, E extends Edge<V>, G extends BioGraph<V,E>> void toGmlWithAttributes(G graph, String outputPath, Map<V,?> att, String attName, boolean weight){
		try {
			GmlExporter<V, E> gml
					= new GmlExporter<>();
			gml.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_VERTEX_ATTRIBUTES, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_EDGE_ATTRIBUTES, true);
			gml.setVertexAttributeProvider(v -> {
				Map<String, Attribute> att2 = new HashMap<>();
				att2.put(attName, DefaultAttribute.createAttribute(att.get(v).toString()));
				att2.put("Name",DefaultAttribute.createAttribute(v.getName()));
				return att2;
			});
			if(weight){
				gml.setEdgeAttributeProvider(e -> {
					Map<String, Attribute> att3 = new HashMap<>();
					att3.put("Weight", DefaultAttribute.createAttribute(graph.getEdgeWeight(e)));
					return att3;
				});
			}

			FileWriter fw = new FileWriter(new File(outputPath).getAbsoluteFile());
			PrintWriter pw = new PrintWriter(fw);
			gml.exportGraph(graph, pw);
			System.out.println(outputPath+" created.");
		} catch (IOException e) {
			System.err.println("Error in file export!");
			e.printStackTrace();
		}
	}

	/**
	 * Export jgrapht graph into gml format
	 *
	 * @param graph the graph
	 * @param outputPath the output path
	 */
	public static <EdgeNameProvider> void toGmlWithAttributes(CompoundGraph graph, String outputPath){
		ExportGraph.toGmlWithAttributes(graph, outputPath,false);
	}

	public static <EdgeNameProvider> void toGmlWithAttributes(CompoundGraph graph, String outputPath, Boolean weight){
		try {
			GmlExporter<BioMetabolite, ReactionEdge> gml
					= new GmlExporter<>();
			gml.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_EDGE_ATTRIBUTES, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_VERTEX_ATTRIBUTES, true);
			gml.setVertexAttributeProvider(v -> {
				Map<String, Attribute> att = new HashMap<>();
				if(v.getName()!=null) att.put("Name", DefaultAttribute.createAttribute(v.getName()));
				if(v.getChemicalFormula()!=null) att.put("Formula", DefaultAttribute.createAttribute(v.getChemicalFormula()));
				if(v.getMolecularWeight()!=null) att.put("Mass", DefaultAttribute.createAttribute(v.getMolecularWeight()));
				return att;
			});
			gml.setEdgeAttributeProvider(e -> {
				Map<String, Attribute> att = new HashMap<>();
				if(e.getReaction()!=null && e.getReaction().getName()!=null) att.put("Name", DefaultAttribute.createAttribute(e.getReaction().getName()));
				if(e.getReaction()!=null && e.getReaction().isReversible()!=null) att.put("Reversible", DefaultAttribute.createAttribute(e.getReaction().isReversible()));
				if(e.getReaction()!=null && e.getReaction().getEcNumber()!=null) att.put("EC", DefaultAttribute.createAttribute(e.getReaction().getEcNumber()));
				if(weight) att.put("Weight", DefaultAttribute.createAttribute(graph.getEdgeWeight(e)));
				return att;
			});
			FileWriter fw = new FileWriter(new File(outputPath).getAbsoluteFile());
			PrintWriter pw = new PrintWriter(fw);
			gml.exportGraph(graph, pw);
			System.out.println(outputPath+" created.");
		} catch (IOException e) {
			System.err.println("Error in file export!");
			e.printStackTrace();
		}
	}

	public static <EdgeNameProvider> void toGmlWithAttributes(ReactionGraph graph, String outputPath){
	ExportGraph.toGmlWithAttributes(graph, outputPath,false);
	}

	public static <EdgeNameProvider> void toGmlWithAttributes(ReactionGraph graph, String outputPath, Boolean weight){
		try {
			GmlExporter<BioReaction, CompoundEdge> gml
					= new GmlExporter<>();
			gml.setParameter(GmlExporter.Parameter.EXPORT_EDGE_LABELS, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_VERTEX_LABELS, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_EDGE_ATTRIBUTES, true);
			gml.setParameter(GmlExporter.Parameter.EXPORT_CUSTOM_VERTEX_ATTRIBUTES, true);
			gml.setVertexAttributeProvider(v -> {
				Map<String, Attribute> att = new HashMap<>();
				if(v.getName()!=null) att.put("Name", DefaultAttribute.createAttribute(v.getName()));
				if(v.isReversible()!=null) att.put("Reversible", DefaultAttribute.createAttribute(v.isReversible()));
				if(v.getEcNumber()!=null) att.put("EC", DefaultAttribute.createAttribute(v.getEcNumber()));
				return att;
			});
			gml.setEdgeAttributeProvider(e -> {
				Map<String, Attribute> att = new HashMap<>();
				if(e.getCompound()!=null && e.getCompound().getName()!=null) att.put("Name", DefaultAttribute.createAttribute(e.getCompound().getName()));
				if(e.getCompound()!=null && e.getCompound().getChemicalFormula()!=null) att.put("Formula", DefaultAttribute.createAttribute(e.getCompound().getChemicalFormula()));
				if(e.getCompound()!=null && e.getCompound().getMolecularWeight()!=null) att.put("Mass", DefaultAttribute.createAttribute(e.getCompound().getMolecularWeight()));
				if(weight) att.put("Weight", DefaultAttribute.createAttribute(graph.getEdgeWeight(e)));
				return att;
			});
			FileWriter fw = new FileWriter(new File(outputPath).getAbsoluteFile());
			PrintWriter pw = new PrintWriter(fw);
			gml.exportGraph(graph, pw);
			System.out.println(outputPath+" created.");
		} catch (IOException e) {
			System.err.println("Error in file export!");
			e.printStackTrace();
		}
	}

	/**
	 * Export bipartite edge.
	 *
	 * @param bip the bipartite graph
	 * @param outputPath the output path
	 */
	public static void exportBipartiteEdge(BipartiteGraph bip, String outputPath){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
//    		bw.write("Reversible");
    		bw.write("edgeId\tReversible\tSide");
    		bw.newLine();
    		for(BipartiteEdge e:bip.edgeSet()){
    			BioEntity src = e.getV1();
    			BioEntity trg = e.getV2();
    			if(!(src==null || trg== null)){
	    			String interaction = (src instanceof BioReaction) ? "product" : "substrate of";
	    			String edgeID = src.getId()+" ("+interaction+") "+trg.getId();
	    			if(edgeID.contains("=")){
	    				bw.close();
	    				throw new IOException("input badly formated : equal sign in namespace");
	    			}
	    			
//					String entry=edgeID+" = "+e.isReversible();
	    			String entry=edgeID+"\t"+e.isReversible()+"\t"+e.isSide();
					bw.write(entry);
					bw.newLine();
    			}
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	
	/**
	 * Export bipartite graph node in tabular format.
	 *
	 * @param bip the bipartite graph
	 * @param outputPath the output path
	 */
	public static void exportBipNodeTabular(BipartiteGraph bip, String outputPath ){
		
		BufferedWriter bw = null;
		try {	    	
    		bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("CanonicalName\tName\tClass");
    		bw.newLine();
    		for(BioEntity v : bip.vertexSet()){
    			String entry=null;
    			if(v instanceof BioMetabolite){
    				BioMetabolite e = (BioMetabolite) v;
//    				boolean side = (graph.hasVertex(v.getId())) ? false : true ;
    				entry=e.getId()+"\t"+e.getName()+"\tcompound";
    			}
    			else if(v instanceof BioReaction){
    				BioReaction r = (BioReaction) v;
    				entry=r.getId()+"\t"+r.getName()+"\treaction";
    			}
    			if(entry!=null){
	    			bw.write(entry);
		    		bw.newLine();
    			}
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * export graph in tabulated format : [source-label]\t[edge-label]\t[target_label]
	 *
	 * @param cmpdGraph the compound graph
	 * @param outputPath the output path
	 */
	public static void toTab(CompoundGraph cmpdGraph, String outputPath){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("source\tinteraction\ttarget");
    		bw.newLine();
    		for(ReactionEdge e:cmpdGraph.edgeSet()){
				String entry=e.getV1().getId()+"\t"+e.getReaction().getId()+"\t"+e.getV2().getId();
				bw.write(entry);
	    		bw.newLine();
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * export graph in tabulated format : [source-label]\t[edge-label]\t[target_label]
	 *
	 * @param rxnGraph the reaction graph
	 * @param outputPath the output path
	 */
	public static void toTab(ReactionGraph rxnGraph, String outputPath){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
			bw.write("source\tinteraction\ttarget");
			bw.newLine();
			for(CompoundEdge e:rxnGraph.edgeSet()){
				String entry=e.getV1().getId()+"\t"+e.getCompound().getId()+"\t"+e.getV2().getId();
				bw.write(entry);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * export bipartite graph in tabulated format : [source-label]\t[edge-label]\t[target_label]
	 *
	 * @param bip the bipartite graph
	 * @param outputPath the output path
	 */
	public static void toTab(BipartiteGraph bip, String outputPath){
		try {	    	
    		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath, true));
    		bw.write("source\tinteraction\ttarget");
    		bw.newLine();
    		for(BipartiteEdge e:bip.edgeSet()){
    			BioEntity src = e.getV1();
    			BioEntity trg = e.getV2();
    			if(!(src==null || trg== null)){
	    			String interaction = (src instanceof BioReaction) ? "product" : "substrate of";
					String entry=src.getId()+"\t"+interaction+"\t"+trg.getId();
					bw.write(entry);
					bw.newLine();
    			}
	    	}
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
