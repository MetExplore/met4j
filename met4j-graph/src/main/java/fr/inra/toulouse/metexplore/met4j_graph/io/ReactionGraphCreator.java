package fr.inra.toulouse.metexplore.met4j_graph.io;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;

public class ReactionGraphCreator {
	
	private BioNetwork bn;
	
	/**
	 * Instantiates a new bionetwork 2 bio graph converter.
	 *
	 * @param bn the bioNetwork
	 */
	public ReactionGraphCreator(BioNetwork bn) {
		this.bn=bn;
	}
	
	/**
	 * Builds the graph.
	 */
	public ReactionGraph getReactionGraph0(){
		ReactionGraph g = new ReactionGraph();
		HashSet<BioChemicalReaction> exchange = new HashSet<BioChemicalReaction>();
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			if(!r.isExchangeReaction()){
				g.addVertex(r);
			}else{
				exchange.add(r);
			}
		}
		
		for(BioPhysicalEntity c : bn.getPhysicalEntityList().values()){
			
			Collection<BioChemicalReaction> left = c.getReactionsAsSubstrate().values();
			Collection<BioChemicalReaction> right = c.getReactionsAsProduct().values();
			left.removeAll(exchange);
			right.removeAll(exchange);
			
			if(!left.isEmpty() && !right.isEmpty()){
				for(BioChemicalReaction v1 : right){
					for(BioChemicalReaction v2 : left){
						if(v1!=v2){
							g.addEdge(v1, v2, new CompoundEdge(v1,v2,c));
						}
					}
				}
			}
		}
		return g;
	}
	
	
	public ReactionGraph getReactionGraph1(){
		ReactionGraph g = new ReactionGraph();
		HashMap<BioPhysicalEntity, HashSet<BioChemicalReaction>> incoming = new HashMap<BioPhysicalEntity, HashSet<BioChemicalReaction>>();
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			g.addVertex(r);
			HashSet<BioPhysicalEntityParticipant> left = new HashSet<BioPhysicalEntityParticipant>(r.getLeftParticipantList().values());
			if(r.isReversible()) left.addAll(r.getRightParticipantList().values());
			for(BioPhysicalEntityParticipant p : left){
				BioPhysicalEntity e = p.getPhysicalEntity();
				if(!e.getIsSide()){
					HashSet<BioChemicalReaction> relation = incoming.get(e);
					if(relation==null){
						relation=new HashSet<BioChemicalReaction>();
						relation.add(r);
						incoming.put(e, relation);
					}else{
						relation.add(r);
					}
				}
			}
		}

		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			HashSet<BioPhysicalEntityParticipant> right = new HashSet<BioPhysicalEntityParticipant>(r.getRightParticipantList().values());
			if(r.isReversible()) right.addAll(r.getLeftParticipantList().values());
			for(BioPhysicalEntityParticipant p : right){
				BioPhysicalEntity e = p.getPhysicalEntity();
				if(!e.getIsSide()){
					HashSet<BioChemicalReaction> relation = incoming.get(e);
					if(relation != null){
						for(BioChemicalReaction r2 : relation){
							if(!r.equals(r2)){
								g.addEdge(r, r2, new CompoundEdge(r, r2, e));
							}
						}
					}
				}
			}	
		}
		return g;
	}
	
	public ReactionGraph getReactionGraph2(){
		ReactionGraph g = new ReactionGraph();
		
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			if(!r.getLeftParticipantList().isEmpty() && !r.getRightParticipantList().isEmpty()){
				g.addVertex(r);
			}
		}
		
		for(BioPhysicalEntity c : bn.getPhysicalEntityList().values()){
			
			Collection<BioChemicalReaction> left = c.getReactionsAsSubstrate().values();
			Collection<BioChemicalReaction> right = c.getReactionsAsProduct().values();
			
			if(!left.isEmpty() && !right.isEmpty()){
				for(BioChemicalReaction v1 : left){
					if(!v1.getLeftParticipantList().isEmpty() && !v1.getRightParticipantList().isEmpty()){
						for(BioChemicalReaction v2 : right){
							if(v1 != v2 && !v2.getLeftParticipantList().isEmpty() && !v2.getRightParticipantList().isEmpty()){
								g.addEdge(v2, v1, new CompoundEdge(v2,v1,c));
							}
						}
					}
				}
			}
		}
		return g;
	}
	
	public ReactionGraph getReactionGraph3(){
		ReactionGraph g = new ReactionGraph();
		HashMap<BioPhysicalEntity, HashSet<BioChemicalReaction>> incoming = new HashMap<BioPhysicalEntity, HashSet<BioChemicalReaction>>();
		HashMap<BioPhysicalEntity, HashSet<BioChemicalReaction>> outgoing = new HashMap<BioPhysicalEntity, HashSet<BioChemicalReaction>>();

		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			g.addVertex(r);
			HashSet<BioPhysicalEntityParticipant> left = new HashSet<BioPhysicalEntityParticipant>(r.getLeftParticipantList().values());
			if(r.isReversible()) left.addAll(r.getRightParticipantList().values());
			for(BioPhysicalEntityParticipant p : left){
				BioPhysicalEntity e = p.getPhysicalEntity();
				HashSet<BioChemicalReaction> relation = incoming.get(e);
				if(relation==null){
					relation=new HashSet<BioChemicalReaction>();
					relation.add(r);
					incoming.put(e, relation);
				}else{
					relation.add(r);
				}
			}
			HashSet<BioPhysicalEntityParticipant> right = new HashSet<BioPhysicalEntityParticipant>(r.getRightParticipantList().values());
			if(r.isReversible()) right.addAll(r.getLeftParticipantList().values());
			for(BioPhysicalEntityParticipant p : right){
				BioPhysicalEntity e = p.getPhysicalEntity();
				HashSet<BioChemicalReaction> relation = outgoing.get(e);
				if(relation==null){
					relation=new HashSet<BioChemicalReaction>();
					relation.add(r);
					outgoing.put(e, relation);
				}else{
					relation.add(r);
				}
			}
		}
		for(Entry<BioPhysicalEntity,HashSet<BioChemicalReaction>> entry : outgoing.entrySet()){
			BioPhysicalEntity e = entry.getKey();
			for(BioChemicalReaction r1 : entry.getValue()){
				HashSet<BioChemicalReaction> connected = incoming.get(e);
				if(connected!=null){
					for(BioChemicalReaction r2 : connected){
						if(r1!=r2){
							g.addEdge(r1, r2, new CompoundEdge(r1,r2,e));
						}
					}
				}
			}
		}
		
		return g;
	}
	
	public ReactionGraph getReactionGraph4(){
		ReactionGraph g = new ReactionGraph();
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			g.addVertex(r);
		}
		HashSet<BioChemicalReaction> reactionStack1 = new HashSet<BioChemicalReaction>(bn.getBiochemicalReactionList().values());
		HashSet<BioChemicalReaction> reactionStack2 = new HashSet<BioChemicalReaction>(bn.getBiochemicalReactionList().values());
		
		for(BioChemicalReaction r1 : reactionStack1){
			reactionStack2.remove(r1);
			for(BioChemicalReaction r2 : reactionStack2){
				
				HashSet<BioPhysicalEntity> right1 = new HashSet<BioPhysicalEntity>(r1.getRightList().values());
				HashSet<BioPhysicalEntity> right2 = new HashSet<BioPhysicalEntity>(r2.getRightList().values());
				HashSet<BioPhysicalEntity> left1 = new HashSet<BioPhysicalEntity>(r1.getLeftList().values());
				HashSet<BioPhysicalEntity> left2 = new HashSet<BioPhysicalEntity>(r2.getLeftList().values());
				
				if(r1.isReversible()){
					left1.addAll(right1);
					right1=left1;
				}
				if(r2.isReversible()){
					left2.addAll(right2);
					right2=left2;
				}
				
				HashSet<BioPhysicalEntity> commonRight2Left1 = new HashSet<BioPhysicalEntity>(right2);
				commonRight2Left1.retainAll(left1);
				
				HashSet<BioPhysicalEntity> commonRight1Left2 = new HashSet<BioPhysicalEntity>(right1);
				if(r1.isReversible()) commonRight1Left2.addAll(r1.getLeftList().values());
				commonRight1Left2.retainAll(left2);
				
				for(BioPhysicalEntity c : commonRight2Left1){
					g.addEdge(r2, r1, new CompoundEdge(r2,r1,c));
				}
				for(BioPhysicalEntity c : commonRight1Left2){
					g.addEdge(r1, r2, new CompoundEdge(r1,r2,c));
				}
			}
		}
		
		return g;
	}
	
	public ReactionGraph getReactionGraph5(){
		ReactionGraph reactionGraph = new ReactionGraph();
		HashSet<BioChemicalReaction> exchange = new HashSet<BioChemicalReaction>();
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			if(!r.isExchangeReaction()){
				reactionGraph.addVertex(r);
			}else{
				exchange.add(r);
			}
		}
		int cpt=0;
		//for each reaction r1 in N
		for(BioChemicalReaction r1 : bn.getBiochemicalReactionList().values())
		{
			//for each reaction r2 in N
			for(BioChemicalReaction r2 : bn.getBiochemicalReactionList().values())
			{
				//if the two reactions are different and not exchange ones
				if((r1!=r2)&&(!exchange.contains(r1))&&(!exchange.contains(r2))){
					//for each mR1 in right participant of r1
					for(BioPhysicalEntityParticipant mR1 : r1.rightParticipantList.values()){
						//for each mL2 in left participant of r2
						for(BioPhysicalEntityParticipant mL2 : r2.leftParticipantList.values()){
							if(mR1.getPhysicalEntity().getId().equals(mL2.getPhysicalEntity().getId())){
								reactionGraph.addVertex(r1);
								reactionGraph.addVertex(r2);
								reactionGraph.addEdge(r1, r2, new CompoundEdge(r1,r2,mR1.getPhysicalEntity()));
							}
						}
						//if r2 is reversible
						if(r2.isReversible())
						{
							//for each mR2 in right participant of r2
							for(BioPhysicalEntityParticipant mR2 : r2.rightParticipantList.values()){
								if(mR1.getPhysicalEntity().getId().equals(mR2.getPhysicalEntity().getId())){
									reactionGraph.addVertex(r1);
									reactionGraph.addVertex(r2);
									reactionGraph.addEdge(r1, r2, new CompoundEdge(r1,r2,mR1.getPhysicalEntity()));
								}
							}
						}						
					}
					if(r1.isReversible())
					{
						for(BioPhysicalEntityParticipant mL1 : r1.leftParticipantList.values()){
							//for each mL2 in left participant of r2
							for(BioPhysicalEntityParticipant mL2 : r2.leftParticipantList.values()){
								if(mL1.getPhysicalEntity().getId().equals(mL2.getPhysicalEntity().getId())){
									reactionGraph.addVertex(r1);
									reactionGraph.addVertex(r2);
									reactionGraph.addEdge(r1, r2, new CompoundEdge(r1,r2,mL1.getPhysicalEntity()));
								}
							}
							//if r2 is reversible
							if(r2.isReversible())
							{
								//for each mR2 in right participant of r2
								for(BioPhysicalEntityParticipant mR2 : r2.rightParticipantList.values()){
									if(mL1.getPhysicalEntity().getId().equals(mR2.getPhysicalEntity().getId())){
										reactionGraph.addVertex(r1);
										reactionGraph.addVertex(r2);
										reactionGraph.addEdge(r1, r2, new CompoundEdge(r1,r2,mL1.getPhysicalEntity()));
									}
								}
							}						
						}						
					}
				}
			}
		}
		
		return reactionGraph;
	}

}
