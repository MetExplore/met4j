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
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
 
/**
 * The Class BioGraph2XGMML.
 * @author clement
 */
public class BioGraph2XGMML{
	
	/** The graph name. */
	private final String graphName;
	
	/** The XGMML doc. */
	private Document doc;
	
	/** The bipartite graph. */
	private final BipartiteGraph g;
	
	/** The optional mapping **/
	private HashMap<String, Double> mapping;
	
	/** The coordinate map **/
	private HashMap<String,Double[]> coord;
	
	/**
	 * Instantiates a new xgmml exporter.
	 *
	 * @param g the graph
	 */
	public BioGraph2XGMML(BipartiteGraph g){
		this.g=g;
		DateFormat df = new SimpleDateFormat("dd/MM/yy_HH:mm:ss");
		this.graphName ="Parsebionet_Network_"+df.format(new Date());
	}
	
	/**
	 * Instantiates a new xgmml exporter.
	 *
	 * @param g the graph
	 * @param coord vertex coordinates map
	 */
	public BioGraph2XGMML(BipartiteGraph g, HashMap<String,Double[]> coord){
		this.g=g;
		this.coord=coord;
		DateFormat df = new SimpleDateFormat("dd/MM/yy_HH:mm:ss");
		this.graphName ="Parsebionet_Network_"+df.format(new Date());
	}
	
	/**
	 * Instantiates a new xgmml exporter.
	 *
	 * @param g the graph
	 * @param name the name of the graph
	 */
	public BioGraph2XGMML(BipartiteGraph g, String name){
		this.g=g;
		this.graphName =name;
	}
	
	/**
	 * Write xgmml to output.
	 *
	 * @param outputPath the output path
	 * @throws Exception if xml configuration is faulty
	 */
	public void writeXGMML(String outputPath) throws Exception{
		if(doc ==null){
			createDoc();
		}
		
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        
        DOMSource source = new DOMSource(doc);
        StreamResult output = new StreamResult(new File(outputPath));
        
        transformer.transform(source, output);
        System.out.println("\nGraph exported successfully..");
	}
	
	/**
	 * Add mapping
	 *
	 * @param mapping the map containing compounds id as key and double score as value
	 */
	public void addMapping(HashMap<String, Double> mapping){
		this.mapping = mapping;
	}
	
	/**
	 * Creates the doc.
	 *
	 * @throws ParserConfigurationException the parser configuration exception
	 */
	private void createDoc() throws ParserConfigurationException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		this.doc = docBuilder.newDocument();
		
		Element graph = doc.createElement("graph");
		doc.appendChild(graph);
		graph.setAttribute("label", this.graphName);
		graph.setAttribute("xmlns:dc","http://purl.org/dc/elements/1.1/");
		graph.setAttribute("xmlns:xlink","http://www.w3.org/1999/xlink");
		graph.setAttribute("xmlns:rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		graph.setAttribute("xmlns:cy","http://www.cytoscape.org");
		graph.setAttribute("xmlns", "http://www.cs.rpi.edu/XGMML");
		graph.setAttribute("directed", "1");
		graph.appendChild(createAttribute("documentVersion","1.1"));
		graph.appendChild(createAttribute("backgroundColor","#ffffff"));
		
		for(BioEntity v : g.vertexSet()){
			Element node = (v instanceof BioReaction) ? createReactionNode((BioReaction)v) : createCompoundNode((BioMetabolite)v);
			graph.appendChild(node);
		}
		
		for(BipartiteEdge e : g.edgeSet()){
			Element node = createEdge(e);
			graph.appendChild(node);
		}
	}
	
	/**
	 * Creates a compound node.
	 *
	 * @param v the vertex
	 * @return the xml element
	 */
	private Element createCompoundNode(BioMetabolite v){
		Element node = doc.createElement("node");
//        node.setAttribute("label", v.getName());
//        node.setAttribute("id", v.getId());
//        node.appendChild(createAttribute("Class","compound"));
//        node.appendChild(createAttribute("Compartment",v.getCompartment().getName()));
//        node.appendChild(createAttribute("Pathway","null"));
//        node.appendChild(createAttribute("Side",v.getIsSide()));
//        node.appendChild(createCompoundNodeGraphics(v));
		node.setAttribute("label", v.getName());
		node.setAttribute("id", v.getId());
		node.appendChild(createAttribute("canonicalName",v.getId()));
        node.appendChild(createAttribute("chemicalFormula",v.getChemicalFormula()));
		node.appendChild(createAttribute("dbIdentifier",v.getName()));
		node.appendChild(createAttribute("sbml name",v.getName()));
		node.appendChild(createAttribute("sbml type","species"));
		if(mapping !=null && mapping.containsKey(v.getId())){
			node.appendChild(createAttribute("identified","identified"));
			node.appendChild(createAttribute("value", mapping.get(v.getId()).toString()));
			node.appendChild(createMappedCompoundNodeGraphics(v));
		}else{
			node.appendChild(createCompoundNodeGraphics(v));
		}
        return node;
	}
	
	/**
	 * Creates a reaction node.
	 *
	 * @param v the vertex
	 * @return the xml element
	 */
	private Element createReactionNode(BioReaction v){
		Element node = doc.createElement("node");
//        node.setAttribute("label", v.getName());
//        node.setAttribute("id", v.getId());
//        node.appendChild(createAttribute("Class","reaction"));
//        node.appendChild(createAttribute("Compartment","null"));
//        node.appendChild(createAttribute("Pathway",StringUtils.join(v.getPathwayList().values(),",")));
//        node.appendChild(createAttribute("Side",false));
//        node.appendChild(createReactionNodeGraphics(v));
		node.setAttribute("label", v.getName());
        node.setAttribute("id", v.getId());
        node.appendChild(createAttribute("canonicalName",v.getId()));
        node.appendChild(createAttribute("dbIdentifier",v.getName()));
        node.appendChild(createAttribute("ec",v.getEcNumber()));
        node.appendChild(createReactionNodeGraphics(v));
        node.appendChild(createAttribute("reversibility",v.isReversible()));
        node.appendChild(createAttribute("sbml name",v.getName()));
        node.appendChild(createAttribute("sbml type","reaction"));
        node.appendChild(createReactionNodeGraphics(v));
        return node;
	}
	
	/**
	 * Creates an edge.
	 *
	 * @param e the edge
	 * @return the xml element
	 */
	private Element createEdge(BipartiteEdge e){
		Element edge = doc.createElement("edge");
		BioEntity src = e.getV1();
		BioEntity trg = e.getV2();
//		String interaction = (src instanceof BioReaction) ? "product" : "substrate of";
		String interaction = (src instanceof BioReaction) ? "reaction-product" : "reaction-reactant";
		String label = src.getId()+" ("+interaction+") "+trg.getId();
		edge.setAttribute("label", label);
		edge.setAttribute("source", src.getId());
		edge.setAttribute("target", trg.getId());
		edge.appendChild(createAttribute("reversibility",e.isReversible()));
		edge.appendChild(createAttribute("side",e.isSide()));
		edge.appendChild(createEdgeGraphics(e));
        return edge;
	}
	
	/**
	 * Creates an attribute from string.
	 *
	 * @param name the attribute's name
	 * @param value the attribute's value
	 * @return the xml element
	 */
	private Element createAttribute(String name, String value){
		Element att = doc.createElement("att");
		att.setAttribute("type", "string");
		att.setAttribute("name", name);
		att.setAttribute("value", value);
        return att;
	}
	
	/**
	 * Creates an attribute from boolean.
	 *
	 * @param name the attribute's name
	 * @param value the attribute's value
	 * @return the xml element
	 */
	private Element createAttribute(String name, boolean value){
		Element att = doc.createElement("att");
		att.setAttribute("type", "boolean");
		att.setAttribute("name", name);
		String valueAsString = value ? "true" : "false";
		att.setAttribute("value", valueAsString);
        return att;
	}
	
	/**
	 * Creates an attribute from string list.
	 *
	 * @param name the attribute's name
	 * @param valueList the attribute's values
	 * @return the xml element
	 */
	private Element createAttribute(String name, Collection<String> valueList){
		Element att = doc.createElement("att");
		att.setAttribute("type", "list");
		att.setAttribute("name", name);
		for(String value : valueList){
			att.appendChild(createAttribute(name,value));
		}
        return att;
	}
	
	/**
	 * Creates the edge graphics.
	 *
	 * @param e the edge
	 * @return the element
	 */
	private Element createEdgeGraphics(BipartiteEdge e){
		Element graphics = doc.createElement("graphics");
		String width = "8";
		graphics.setAttribute("width",width);
		String fill = (e.getV1() instanceof BioReaction) ? "#339900" : "#cc3300";
		graphics.setAttribute("fill",fill);
		String sourceArrow = e.isReversible() ?  "6" : "0";
		graphics.setAttribute("cy:sourceArrow",sourceArrow);
		String targetArrow = "6";
		graphics.setAttribute("cy:targetArrow",targetArrow);
		String sourceArrowColor = fill;
		graphics.setAttribute("cy:sourceArrowColor",sourceArrowColor);
		String targetArrowColor = fill;
		graphics.setAttribute("cy:targetArrowColor",targetArrowColor);
		String edgeLabelFont = "SanSerif-0-10";
		graphics.setAttribute("cy:edgeLabelFont",edgeLabelFont);
		String edgeLabel = "";
		graphics.setAttribute("cy:edgeLabel",edgeLabel);
		String edgeLineType = e.isSide() ? "DOT" : "SOLID";
		graphics.setAttribute("cy:edgeLineType",edgeLineType);
		String curved = "STRAIGHT_LINES";
		graphics.setAttribute("cy:curved",curved);
		String edgeTransparancy = "0.47058823529411764";
		graphics.setAttribute("cy:edgeTransparency",edgeTransparancy);
		return graphics;
	}
	
	/**
	 * Creates the compound node graphics.
	 *
	 * @param v the vertex
	 * @return the element
	 */
	private Element createCompoundNodeGraphics(BioMetabolite v){
		Element graphics = doc.createElement("graphics");
		String type="ELLIPSE";
		graphics.setAttribute("type",type);
		String h="35.0";
		graphics.setAttribute("h",h);
		String w="35.0";
		graphics.setAttribute("w",w);
		String fill="#3b79a1";
		graphics.setAttribute("fill",fill);
		String width="3";
		graphics.setAttribute("width",width);
		String outline="#000000";
		graphics.setAttribute("outline",outline);
		String nodeTransparency="0.47058823529411764";
		graphics.setAttribute("cy:nodeTransparency",nodeTransparency);
		String nodeLabelFont="Times New Roman Bold-0-15";
		graphics.setAttribute("cy:nodeLabelFont",nodeLabelFont);
		String nodeLabel=v.getName();
		graphics.setAttribute("cy:nodeLabel",nodeLabel);
		String borderLineType="solid";
		graphics.setAttribute("cy:borderLineType",borderLineType);
		if(coord !=null && coord.containsKey(v.getId())){
			Double[] xy = coord.get(v.getId());
			String x = xy[0].toString();
			String y = xy[1].toString();
			graphics.setAttribute("x",x);
			graphics.setAttribute("y",y);
		}
		return graphics;
	}
	
	/**
	 * Creates the compound node graphics.
	 *
	 * @param v the vertex
	 * @return the element
	 */
	private Element createMappedCompoundNodeGraphics(BioMetabolite v){
		Element graphics = doc.createElement("graphics");
		String type="ELLIPSE";
		graphics.setAttribute("type",type);
		String h="80.0";
		graphics.setAttribute("h",h);
		String w="80.0";
		graphics.setAttribute("w",w);
		String fill="#3b79a1";
		graphics.setAttribute("fill",fill);
		String width="3";
		graphics.setAttribute("width",width);
		String outline="#000000";
		graphics.setAttribute("outline",outline);
		String nodeTransparency="1.0";
		graphics.setAttribute("cy:nodeTransparency",nodeTransparency);
		String nodeLabelFont="Times New Roman Bold-0-15";
		graphics.setAttribute("cy:nodeLabelFont",nodeLabelFont);
		String nodeLabel=v.getName();
		graphics.setAttribute("cy:nodeLabel",nodeLabel);
		String borderLineType="solid";
		graphics.setAttribute("cy:borderLineType",borderLineType);
		if(coord !=null && coord.containsKey(v.getId())){
			Double[] xy = coord.get(v.getId());
			String x = xy[0].toString();
			String y = xy[1].toString();
			graphics.setAttribute("x",x);
			graphics.setAttribute("y",y);
		}
		return graphics;
	}
	
	/**
	 * Creates the reaction node graphics.
	 *
	 * @param v the vertex
	 * @return the element
	 */
	private Element createReactionNodeGraphics(BioReaction v){
		Element graphics = doc.createElement("graphics");
		String type="ROUNDED_RECTANGLE";
		graphics.setAttribute("type",type);
		String h="35.0";
		graphics.setAttribute("h",h);
		String w="35.0";
		graphics.setAttribute("w",w);
		String fill="#bedff4";
		graphics.setAttribute("fill",fill);
		String width="3";
		graphics.setAttribute("width",width);
		String outline="#000000";
		graphics.setAttribute("outline",outline);
		String nodeTransparency="0.47058823529411764";
		graphics.setAttribute("cy:nodeTransparency",nodeTransparency);
		String nodeLabelFont="Times New Roman Bold-0-15";
		graphics.setAttribute("cy:nodeLabelFont",nodeLabelFont);
		String nodeLabel=v.getName();
		graphics.setAttribute("cy:nodeLabel",nodeLabel);
		String borderLineType="solid";
		graphics.setAttribute("cy:borderLineType",borderLineType);
		if(coord !=null && coord.containsKey(v.getId())){
			Double[] xy = coord.get(v.getId());
			String x = xy[0].toString();
			String y = xy[1].toString();
			graphics.setAttribute("x",x);
			graphics.setAttribute("y",y);
		}
		return graphics;
	}

}