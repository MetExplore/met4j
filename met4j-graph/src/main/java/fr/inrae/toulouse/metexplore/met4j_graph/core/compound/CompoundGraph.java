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
package fr.inrae.toulouse.metexplore.met4j_graph.core.compound;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The Class CompoundGraph.
 *
 * @author clement
 * @version $Id: $Id
 */
public class CompoundGraph extends BioGraph<BioMetabolite, ReactionEdge> {


	private static final long serialVersionUID = -929423100467552635L;

//	/**the graph name**/
//	private String name = "ParseBioNet-Graph";
	
	/** The edge factory. */
	private static Class<? extends ReactionEdge> edgeFactory;

	/**
	 * Instantiates a new bio graph.
	 */
	public CompoundGraph() {
		super();
	}
	
	/**
	 * create a new CompoundGraph from a pre-existing one (more well-suited than override clone() I suppose)
	 *
	 * @param g the graph
	 */
	public CompoundGraph(CompoundGraph g) {
		super();
		for(BioMetabolite vertex : g.vertexSet()){
			this.addVertex(vertex);
		}
		for(ReactionEdge edge : g.edgeSet()){
			ReactionEdge newEdge = new ReactionEdge(edge.getV1(), edge.getV2(), edge.getReaction());
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
	public HashSet<ReactionEdge> getEdgesFromReaction(String reactionId){
		HashSet<ReactionEdge> edgeList = new HashSet<>();
		for(ReactionEdge edge : this.edgeSet()){
			if(edge.toString().equalsIgnoreCase(reactionId)){
				edgeList.add(edge);
			}
		}
		return edgeList;
	}

	/**
	 * Compute the reaction subgraph
	 *
	 * @param reaction the reaction to evaluate
	 * @return the reaction subgraph
	 */
	public CompoundGraph getReactionSubGraph(BioReaction reaction) {
		//Create graph for the reaction
		CompoundGraph rSubGraph = new CompoundGraph();
		//Create all possible edges between left and right reactants
		for(ReactionEdge e : this.getEdgesFromReaction(reaction.getId())){
			rSubGraph.addVertex(e.getV1());
			rSubGraph.addVertex(e.getV2());
			rSubGraph.addEdge(e);
		}
		return rSubGraph;
	}
	
	/**
	 * Gets the biochemical reaction list.
	 *
	 * @return the biochemical reaction list
	 */
	public HashMap<String, BioReaction> getBiochemicalReactionList(){
		HashMap<String, BioReaction> reactionMap = new HashMap<>();
		for(ReactionEdge e: this.edgeSet()){
			if(!reactionMap.containsKey(e.toString())){
				reactionMap.put(e.toString(), e.getReaction());
			}
		}
		return reactionMap;
	}
	
	/**
	 * Adds the edges from reaction. If a substrate or a product is not present in the network it will be ignored
	 *
	 * @param r the reaction
	 * @param model a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
	 */
	public void addEdgesFromReaction(BioNetwork model, BioReaction r){
		for(BioMetabolite sub : model.getLefts(r)){
			if(this.hasVertex(sub.getId())){
				for(BioMetabolite prd : model.getRights(r)){
					if(this.hasVertex(prd.getId())){
						ReactionEdge edge = new ReactionEdge(sub, prd, r);
						this.addEdge(sub, prd, edge);
						if(r.isReversible()){
							ReactionEdge edgeBack = new ReactionEdge(prd, sub, r);
							this.addEdge(prd, sub, edgeBack);
						}
					}
				}
			}
		}
	}

	/**
	 * <p>getEdgesFromCompartment.</p>
	 *
	 * @param bn a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
	 * @param comp a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @return a {@link java.util.HashSet} object.
	 */
	public HashSet<ReactionEdge> getEdgesFromCompartment(BioNetwork bn, BioCompartment comp){
		return this.edgeSet().stream()
			.filter(e -> e.getReaction().getReactantsView()
				.stream().allMatch(r -> r.getLocation().equals(comp))
			).collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * Adds the edges from reaction. If a substrate or a product is not present in the network it will be ignored
	 *
	 * @param rCollection the reactions
	 * @param model a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
	 */
	public void addEdgesFromReactions(BioNetwork model, BioCollection<BioReaction> rCollection){
		for(BioReaction r : rCollection){
			this.addEdgesFromReaction(model, r);
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * Gets edge from source, target and associated reaction.
	 */
	public ReactionEdge getEdge(String sourceVertex, String targetVertex, String reaction) {
		for(ReactionEdge e : this.edgeSet()){
			if(e.getV1().getId().equals(sourceVertex) && e.getV2().getId().equals(targetVertex) && e.toString().equals(reaction)){
				return e;
			}
		}
		return null;
	}

	/**
	 * Gets edge from source, target and associated reaction.
	 *
	 * @param sourceVertex the source vertex
	 * @param targetVertex the target vertex
	 * @param reaction the reaction
	 * @return the edge
	 */
	public ReactionEdge getEdge(BioMetabolite sourceVertex, BioMetabolite targetVertex, BioReaction reaction) {
		for(ReactionEdge e : this.edgeSet()){
			if(e.getV1().equals(sourceVertex) && e.getV2().equals(targetVertex) && e.getReaction().equals(reaction)){
				return e;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public ReactionEdge copyEdge(ReactionEdge edge) {
		return new ReactionEdge(edge.getV1(), edge.getV2(), edge.getReaction());
	}

	@Override
	public BioMetabolite createVertex(String id) {
		return new BioMetabolite(id);
	}

	@Override
	public ReactionEdge createEdge(BioMetabolite v1, BioMetabolite v2) {
		return new ReactionEdge(v1,v2,new BioReaction(UUID.randomUUID().toString()));
	}

	@Override
	public ReactionEdge createEdgeFromModel(BioMetabolite v1, BioMetabolite v2, ReactionEdge edge){
		return new ReactionEdge(v1, v2, edge.getReaction());
	}
	
	/**
	 * <p>getFactory.</p>
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory} object.
	 */
	public static GraphFactory<BioMetabolite, ReactionEdge, CompoundGraph> getFactory(){
		return new GraphFactory<>() {
            @Override
            public CompoundGraph createGraph() {
                return new CompoundGraph();
            }
        };
	}

	/** {@inheritDoc} */
	@Override
	public ReactionEdge reverseEdge(ReactionEdge edge) {
		ReactionEdge reverse = new ReactionEdge(edge.getV2(), edge.getV1(), edge.getReaction());
		return reverse;
	}
}
