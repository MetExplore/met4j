/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_graph.core.reaction;

import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.EdgeFactory;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;

/**
 * The Class CompoundGraph.
 * @author clement
 */
public class ReactionGraph extends BioGraph<BioChemicalReaction, CompoundEdge> {


	private static final long serialVersionUID = -929423100467552635L;

//	/**the graph name**/
//	private String name = "ParseBioNet-Graph";
	
	/** The edge factory. */
	private static Class<? extends CompoundEdge> edgeFactory;

	/**
	 * Instantiates a new bio graph.
	 */
	public ReactionGraph() {
		super(edgeFactory);
	}
	
	/**
	 * create a new CompoundGraph from a pre-existing one (more well-suited than override clone() I suppose)
	 *
	 * @param g the graph
	 */
	public ReactionGraph(ReactionGraph g) {
		super(edgeFactory);
		for(BioChemicalReaction vertex : g.vertexSet()){
			this.addVertex(vertex);
		}
		for(CompoundEdge edge : g.edgeSet()){
			CompoundEdge newEdge = new CompoundEdge(edge.getV1(), edge.getV2(), edge.getCompound());
			this.addEdge(newEdge.getV1(), newEdge.getV2(), newEdge);
			this.setEdgeWeight(newEdge, g.getEdgeWeight(edge));
			this.setEdgeScore(newEdge, g.getEdgeScore(edge));
		}
	}

	
	/**
	 * Gets the edges from reaction.
	 *
	 * @param reactionId the reaction id
	 * @return the edges from reaction
	 */
	public HashSet<CompoundEdge> getEdgesFromCompound(String compoundId){
		HashSet<CompoundEdge> edgeList = new HashSet<CompoundEdge>();
		for(CompoundEdge edge : this.edgeSet()){
			if(edge.toString().equalsIgnoreCase(compoundId)){
				edgeList.add(edge);
			}
		}
		return edgeList;
	}
	
	/**
	 * Gets the biochemical reaction list.
	 *
	 * @return the biochemical reaction list
	 */
	public HashMap<String, BioPhysicalEntity> getCompoundList(){
		HashMap<String, BioPhysicalEntity> compoundMap = new HashMap<String, BioPhysicalEntity>();
		for(CompoundEdge e: this.edgeSet()){
			if(!compoundMap.containsKey(e.toString())){
				compoundMap.put(e.toString(), e.getCompound());
			}
		}
		return compoundMap;
	}
	
	/**
	 * Adds the edges from reaction.
	 *
	 * @param r the reaction
	 */
	public void addEdgesFromCompound(BioPhysicalEntity c){
		for(BioChemicalReaction sub : c.getReactionsAsSubstrate().values()){
			if(this.hasVertex(sub.getId())){
				for(BioChemicalReaction prd : c.getReactionsAsProduct().values()){
					if(this.hasVertex(prd.getId())){
						if(sub!=prd){
							CompoundEdge edge = new CompoundEdge(sub, prd, c);
							this.addEdge(sub, prd, edge);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Gets edge from source, target and associated reaction.
	 *
	 * @param sourceVertex the source vertex
	 * @param targetVertex the target vertex
	 * @param reaction the reaction
	 * @return the edge
	 */
	public CompoundEdge getEdge(String sourceVertex, String targetVertex, String reaction) {
		for(CompoundEdge e : this.edgeSet()){
			if(e.getV1().getId().equals(sourceVertex) && e.getV2().getId().equals(targetVertex) && e.toString().equals(reaction)){
				return e;
			}
		}
		return null;
	}

	@Override
	public EdgeFactory<BioChemicalReaction, CompoundEdge> getEdgeFactory() {
		return new EdgeFactory<BioChemicalReaction, CompoundEdge>() {

			@Override
			public CompoundEdge createEdge(BioChemicalReaction arg0,
					BioChemicalReaction arg1) {
				return new CompoundEdge(arg0, arg1, new BioPhysicalEntity(""));
			}
			
		};
	}

	@Override
	public CompoundEdge copyEdge(CompoundEdge edge) {
		return new CompoundEdge(edge.getV1(), edge.getV2(), edge.getCompound());
	}
	
	public static GraphFactory<BioChemicalReaction, CompoundEdge, ReactionGraph> getFactory(){
		return new GraphFactory<BioChemicalReaction, CompoundEdge, ReactionGraph>(){
			@Override
			public ReactionGraph createGraph() {
				return new ReactionGraph();
			}
		};
	}

	@Override
	public CompoundEdge reverseEdge(CompoundEdge edge) {
		CompoundEdge reversed = new CompoundEdge(edge.getV2(), edge.getV1(), edge.getCompound());
		return reversed;
	}
}
