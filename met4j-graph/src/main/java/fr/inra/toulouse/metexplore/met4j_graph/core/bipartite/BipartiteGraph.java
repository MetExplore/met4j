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
package fr.inra.toulouse.metexplore.met4j_graph.core.bipartite;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.EdgeFactory;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

public class BipartiteGraph extends BioGraph<BioEntity, BipartiteEdge> {

	private static final long serialVersionUID = -8285420874595144901L;

	public BipartiteGraph() {
		super(new BipartiteEdgeFactory());
	}

	public static GraphFactory<BioEntity, BipartiteEdge, BipartiteGraph> getFactory(){
		return new GraphFactory<BioEntity, BipartiteEdge, BipartiteGraph>() {
			@Override
			public BipartiteGraph createGraph() {
				return new BipartiteGraph();
			}
		};
		
	}
	
	public Set<BioMetabolite> compoundVertexSet(){
		HashSet<BioMetabolite> vertexSet = new HashSet<BioMetabolite>();
		for(BioEntity v : this.vertexSet()){
			if(v instanceof BioMetabolite) vertexSet.add((BioMetabolite) v);
		}
		return vertexSet;
	}
	
	public Set<BioReaction> reactionVertexSet(){
		HashSet<BioReaction> vertexSet = new HashSet<BioReaction>();
		for(BioEntity v : this.vertexSet()){
			if(v instanceof BioReaction) vertexSet.add((BioReaction) v);
		}
		return vertexSet;
	}

	@Override
	public EdgeFactory<BioEntity, BipartiteEdge> getEdgeFactory() {
		return new BipartiteEdgeFactory();
	}

	@Override
	public BipartiteEdge copyEdge(BipartiteEdge edge) {
		return new BipartiteEdge(edge.getV1(),edge.getV2(),edge.isReversible());
	}
	
	/**
	 * Keep only unique edge for reversible reaction in the bipartite graph 
	 *
	 * @param bip the bipartite graph
	 */
	public void mergeReversibleEdges(BioNetwork bn){

		HashSet<BipartiteEdge> toRemove = new HashSet<BipartiteEdge>();
		for(BipartiteEdge e : this.edgeSet()){
			BioEntity v1 = this.getEdgeSource(e);
			BioEntity v2 = this.getEdgeTarget(e);
			
			if(v1 instanceof BioMetabolite){
				BioReaction reaction = (BioReaction) v2;
				if(reaction.isReversible()){
					e.setReversible(true);
					if(bn.getRights(reaction).contains(v1)) toRemove.add(e);
				}
			}else if(v1 instanceof BioReaction){
				BioReaction reaction = (BioReaction) v1;
				if(reaction.isReversible()){
					e.setReversible(true);
					if(bn.getLefts(reaction).contains(v2)) toRemove.add(e);
				}
			}
		}
		this.removeAllEdges(toRemove);
	}
	
	/**
	 *  Add compound that are referred in reaction's reactant list but missing in the reaction graph.
	 *  Add edge if a compound is referred in reaction's reactant list but the edge between this compound and the reaction is missing
	 *  Newly added elements will be flagged as "side" compounds.
	 */
	public void addMissingCompoundAsSide(BioNetwork bn){
		HashSet<BioMetabolite> sideCompoundToAdd = new HashSet<BioMetabolite>();
		HashSet<BipartiteEdge> edgesToAdd = new HashSet<BipartiteEdge>();
		
		
		//add side compounds
		for(BioEntity v : this.vertexSet()){
			
			//take only reaction vertices
			if(v instanceof BioReaction){
				BioReaction r = (BioReaction) v;
				//check all substrates
				for(BioMetabolite s : bn.getLefts(r)){
					//add compound if missing
					if(!this.containsVertex(s) && !sideCompoundToAdd.contains(s)){
						sideCompoundToAdd.add(s);
						BipartiteEdge edge = new BipartiteEdge(s,r);
						edge.setSide(true);
						edgesToAdd.add(edge);
					//else add edge if missing
					}else if(!this.containsEdge(s, r)){
						BipartiteEdge edge = new BipartiteEdge(s,r);
						if(this.containsEdge(r, s)){
							edge.setSide(this.getEdge(r, s).isSide());
						}else{
							edge.setSide(true);
						}
						edgesToAdd.add(edge);
					}
				}
				//check all products
				for(BioMetabolite p : bn.getRights(r)){
					//add compound if missing
					if(!this.containsVertex(p) && !sideCompoundToAdd.contains(p)){
						sideCompoundToAdd.add(p);
						BipartiteEdge edge = new BipartiteEdge(r,p);
						edge.setSide(true);
						edgesToAdd.add(edge);
					//else add edge if missing	
					}else if(!this.containsEdge(r, p)){
						BipartiteEdge edge = new BipartiteEdge(r,p);
						if(this.containsEdge(p, r)){
							edge.setSide(this.getEdge(p, r).isSide());
						}else{
							edge.setSide(true);
						}
						edgesToAdd.add(edge);
					}
				}
			}
		}
		
		for(BioMetabolite sideCompounds : sideCompoundToAdd){
			this.addVertex(sideCompounds);
		}
		for(BipartiteEdge e : edgesToAdd){
			this.addEdge(e.getV1(), e.getV2(), e);
		}
	}

	@Override
	public BipartiteEdge reverseEdge(BipartiteEdge edge) {
		BipartiteEdge reverse = new BipartiteEdge(edge.getV2(), edge.getV1(), edge.isReversible());
		return reverse;
	}
}
