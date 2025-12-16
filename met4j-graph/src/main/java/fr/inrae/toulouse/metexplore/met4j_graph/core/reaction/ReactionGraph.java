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
package fr.inrae.toulouse.metexplore.met4j_graph.core.reaction;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * The Class CompoundGraph.
 *
 * @author clement
 */
public class ReactionGraph extends BioGraph<BioReaction, CompoundEdge> {


	private static final long serialVersionUID = -929423100467552635L;

//	/**the graph name**/
//	private String name = "ParseBioNet-Graph";
	
	/** The edge factory. */
	private static Class<? extends CompoundEdge> edgeFactory;

	/**
	 * Instantiates a new bio graph.
	 */
	public ReactionGraph() {
		super();
	}
	
	/**
	 * create a new CompoundGraph from a pre-existing one (more well-suited than override clone() I suppose)
	 *
	 * @param g the graph
	 */
	public ReactionGraph(ReactionGraph g) {
		super();
		for(BioReaction vertex : g.vertexSet()){
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
	 * @param compoundId the compound id
	 * @return the edges from reaction
	 */
	public HashSet<CompoundEdge> getEdgesFromCompound(String compoundId){
		HashSet<CompoundEdge> edgeList = new HashSet<>();
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
	public HashMap<String, BioMetabolite> getCompoundList(){
		HashMap<String, BioMetabolite> compoundMap = new HashMap<>();
		for(CompoundEdge e: this.edgeSet()){
			if(!compoundMap.containsKey(e.toString())){
				compoundMap.put(e.toString(), e.getCompound());
			}
		}
		return compoundMap;
	}
	
	/**
	 * Adds the edges from compound. Reactions not in graph will be ignored
	 *
	 * @param model the bionetwork
	 * @param c the connecting metabolite
	 */
	public void addEdgesFromCompound(BioNetwork model, BioMetabolite c){
		for(BioReaction in : model.getReactionsFromProduct(c)){
			if(this.hasVertex(in.getId())){
				for(BioReaction out : model.getReactionsFromSubstrate(c)){
					if(this.hasVertex(out.getId())){
						if(in!=out){
							CompoundEdge edge = new CompoundEdge(in, out, c);
                            this.addEdge(in, out, edge);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 *
	 * Gets edge from source, target and associated reaction.
	 */
	public CompoundEdge getEdge(String sourceVertex, String targetVertex, String reaction) {
		for(CompoundEdge e : this.edgeSet()){
			if(e.getV1().getId().equals(sourceVertex) && e.getV2().getId().equals(targetVertex) && e.toString().equals(reaction)){
				return e;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public CompoundEdge copyEdge(CompoundEdge edge) {
		return new CompoundEdge(edge.getV1(), edge.getV2(), edge.getCompound());
	}

	/** {@inheritDoc} */
	@Override
	public BioReaction createVertex(String id) {
		return new BioReaction(id);
	}

	@Override
	public CompoundEdge createEdge(BioReaction v1, BioReaction v2) {
		return new CompoundEdge(v1,v2,new BioMetabolite(UUID.randomUUID().toString()));
	}

	@Override
	public CompoundEdge createEdgeFromModel(BioReaction v1, BioReaction v2, CompoundEdge edge) {
		return new CompoundEdge(v1, v2, edge.getCompound());
	}
	
	/**
	 * <p>getFactory.</p>
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.GraphFactory} object.
	 */
	public static GraphFactory<BioReaction, CompoundEdge, ReactionGraph> getFactory(){
		return new GraphFactory<>() {
            @Override
            public ReactionGraph createGraph() {
                return new ReactionGraph();
            }
        };
	}

	/** {@inheritDoc} */
	@Override
	public CompoundEdge reverseEdge(CompoundEdge edge) {
		CompoundEdge reversed = new CompoundEdge(edge.getV2(), edge.getV1(), edge.getCompound());
		return reversed;
	}
}
