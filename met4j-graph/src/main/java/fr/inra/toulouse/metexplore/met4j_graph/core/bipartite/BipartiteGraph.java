/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.core.bipartite;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.EdgeFactory;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;

public class BipartiteGraph extends BioGraph<BioEntity, BipartiteEdge> {

	private static final long serialVersionUID = -8285420874595144901L;

	public BipartiteGraph() {
		super(BipartiteEdge.class);
	}

	public static GraphFactory<BioEntity, BipartiteEdge, BipartiteGraph> getFactory(){
		return new GraphFactory<BioEntity, BipartiteEdge, BipartiteGraph>() {
			@Override
			public BipartiteGraph createGraph() {
				return new BipartiteGraph();
			}
		};
		
	}
	
	public Set<BioPhysicalEntity> compoundVertexSet(){
		HashSet<BioPhysicalEntity> vertexSet = new HashSet<BioPhysicalEntity>();
		for(BioEntity v : this.vertexSet()){
			if(v instanceof BioPhysicalEntity) vertexSet.add((BioPhysicalEntity) v);
		}
		return vertexSet;
	}
	
	public Set<BioChemicalReaction> reactionVertexSet(){
		HashSet<BioChemicalReaction> vertexSet = new HashSet<BioChemicalReaction>();
		for(BioEntity v : this.vertexSet()){
			if(v instanceof BioChemicalReaction) vertexSet.add((BioChemicalReaction) v);
		}
		return vertexSet;
	}

	@Override
	public EdgeFactory<BioEntity, BipartiteEdge> getEdgeFactory() {
		return new EdgeFactory<BioEntity, BipartiteEdge>() {
			@Override
			public BipartiteEdge createEdge(BioEntity arg0, BioEntity arg1) {
				return new BipartiteEdge(arg0, arg1,false);
			}
		};
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
	public void mergeReversibleEdges(){

		HashSet<BipartiteEdge> toRemove = new HashSet<BipartiteEdge>();
		for(BipartiteEdge e : this.edgeSet()){
			BioEntity v1 = this.getEdgeSource(e);
			BioEntity v2 = this.getEdgeTarget(e);
			
			if(v1 instanceof BioPhysicalEntity){
				BioChemicalReaction reaction = (BioChemicalReaction) v2;
				if(reaction.isReversible()){
					e.setReversible(true);
					if(reaction.getRightList().containsKey(v1.getId())) toRemove.add(e);
				}
			}else if(v1 instanceof BioChemicalReaction){
				BioChemicalReaction reaction = (BioChemicalReaction) v1;
				if(reaction.isReversible()){
					e.setReversible(true);
					if(reaction.getLeftList().containsKey(v2.getId())) toRemove.add(e);
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
	public void addMissingCompoundAsSide(){
		//add side compounds
		for(BioEntity v : this.vertexSet()){
			
			//take only reaction vertices
			if(v instanceof BioChemicalReaction){
				BioChemicalReaction r = (BioChemicalReaction) v;
				//check all substrates
				for(BioPhysicalEntity s : r.getListOfSubstrates().values()){
					//add compound if missing
					if(!this.containsVertex(s)){
						s.setIsSide(true);
						this.addVertex(s);
						BipartiteEdge edge = new BipartiteEdge(s,r);
						edge.setSide(true);
						this.addEdge(s, r, edge);
					//else add edge if missing
					}else if(!this.containsEdge(s, r)){
						BipartiteEdge edge = new BipartiteEdge(s,r);
						if(this.containsEdge(r, s)){
							edge.setSide(this.getEdge(r, s).isSide());
						}else{
							edge.setSide(true);
						}
						this.addEdge(s, r, edge);
					}
				}
				//check all products
				for(BioPhysicalEntity p : r.getListOfProducts().values()){
					//add compound if missing
					if(!this.containsVertex(p)){
						p.setIsSide(true);
						this.addVertex(p);
						BipartiteEdge edge = new BipartiteEdge(r,p);
						edge.setSide(true);
						this.addEdge(r, p, edge);
					//else add edge if missing	
					}else if(!this.containsEdge(r, p)){
						BipartiteEdge edge = new BipartiteEdge(r,p);
						if(this.containsEdge(p, r)){
							edge.setSide(this.getEdge(p, r).isSide());
						}else{
							edge.setSide(true);
						}
						this.addEdge(r, p, edge);
					}
				}
			}
		}
	}
	
	/**
	 * Duplicate side compounds
	 *
	 */
	public void duplicateSideCompounds(){
		HashSet<BioEntity> sideCompoundList = new HashSet<BioEntity>();
		HashSet<BipartiteEdge> edgesToAdd = new HashSet<BipartiteEdge>();
		for(BioEntity v : this.vertexSet()){
			if(v instanceof BioPhysicalEntity){
				BioPhysicalEntity c = (BioPhysicalEntity) v;
				if(c.getIsSide()){
					sideCompoundList.add(c);
					
					for(BipartiteEdge e : this.incomingEdgesOf(c)){
						BioChemicalReaction r = (BioChemicalReaction) e.getV1();
						BioPhysicalEntity cCopy = new BioPhysicalEntity(c);
						cCopy.setIsSide(true);
						cCopy.setId(c.getId()+"_"+r.getId());
						BipartiteEdge e2 = new BipartiteEdge(r, cCopy);
						e2.setReversible(e.isReversible());
						e2.setSide(true);
						edgesToAdd.add(e2);
					}
					
					for(BipartiteEdge e : this.outgoingEdgesOf(c)){
						BioChemicalReaction r = (BioChemicalReaction) e.getV2();
						BioPhysicalEntity cCopy = new BioPhysicalEntity(c);
						cCopy.setIsSide(true);
						cCopy.setId(c.getId()+"_"+r.getId());
						BipartiteEdge e2 = new BipartiteEdge(cCopy,r);
						e2.setReversible(e.isReversible());
						e2.setSide(true);
						edgesToAdd.add(e2);
					}
				}
			}
		}
		this.removeAllVertices(sideCompoundList);
		for(BipartiteEdge e : edgesToAdd){
			if(e.getV1() instanceof BioPhysicalEntity){
				this.addVertex(e.getV1());
			}else{
				this.addVertex(e.getV2());
			}
			this.addEdge(e.getV1(), e.getV2(), e);
		}
	}

	@Override
	public BipartiteEdge reverseEdge(BipartiteEdge edge) {
		BipartiteEdge reverse = new BipartiteEdge(edge.getV2(), edge.getV1(), edge.isReversible());
		return reverse;
	}
}
