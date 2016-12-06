/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.computation.analysis;

import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;

public class ChokePoint {

	public ChokePoint() {
		// TODO Auto-generated constructor stub
	}
	
	public static HashSet<BioChemicalReaction> getChokePoint(CompoundGraph g){
		HashSet<BioChemicalReaction> chokePoints = new HashSet<BioChemicalReaction>();
		for(BioPhysicalEntity v : g.vertexSet()){
			Set<ReactionEdge> in = g.incomingEdgesOf(v);
			Set<ReactionEdge> out = g.incomingEdgesOf(v);
			if(in.size() == 1 ){
				ReactionEdge choke = in.iterator().next();
				chokePoints.add(choke.getReaction());
			}else if(out.size() == 1 ){
				ReactionEdge choke = out.iterator().next();
				chokePoints.add(choke.getReaction());
			}
		}
		
		return chokePoints;
	}
	
	public static HashSet<BioChemicalReaction> getChokePoint(BipartiteGraph g){
		HashSet<BioChemicalReaction> chokePoints = new HashSet<BioChemicalReaction>();
		for(BioEntity v : g.vertexSet()){
			if(v instanceof BioPhysicalEntity){
				Set<BioEntity> in = g.predecessorListOf(v);
				Set<BioEntity> out = g.successorListOf(v);
				if(in.size() == 1 ){
					BioChemicalReaction choke = (BioChemicalReaction) in.iterator().next();
					chokePoints.add(choke);
				}else if(out.size() == 1 ){
					BioChemicalReaction choke = (BioChemicalReaction) out.iterator().next();
					chokePoints.add(choke);
				}
			}
		}
		return chokePoints;
	}

}
