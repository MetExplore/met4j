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
package fr.inra.toulouse.metexplore.met4j_graph.io;

import java.util.Collection;
import java.util.HashSet;

import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;


/**
 * The Class to convert Bionetwork to BioGraph.
 * @author clement
 */
public class Bionetwork2BioGraph {

	private BioNetwork bn;
	
	/**
	 * Instantiates a new bionetwork 2 bio graph converter.
	 *
	 * @param bn the bioNetwork
	 */
	public Bionetwork2BioGraph(BioNetwork bn) {
		this.bn=bn;
	}
	
	/**
	 * Builds the graph.
	 */
	public CompoundGraph getCompoundGraph(){
		CompoundGraph g = new CompoundGraph();
		
		for(BioPhysicalEntity v : bn.getPhysicalEntityList().values()){
			g.addVertex(v);
		}
		
		for(BioReaction r : bn.getBiochemicalReactionList().values()){
			boolean reversible = r.isReversible();
			Collection<BioPhysicalEntity> left = r.getLeftList().values();
			Collection<BioPhysicalEntity> right = r.getRightList().values();
			if(!left.isEmpty() && !right.isEmpty()){
				for(BioPhysicalEntity v1 : left){
					for(BioPhysicalEntity v2 : right){
						if(v1!=v2){
							g.addEdge(v1, v2, new ReactionEdge(v1,v2,r));
							if(reversible){
								g.addEdge(v2, v1, new ReactionEdge(v2,v1,r));
							}
						}else{
							System.err.println("skip loop on "+v1.getId()+" -> "+v2.getId()+" ("+r.getId()+")");
						}
					}
				}
			}
		}
		
		return g;
	}
	
	
	
	/**
	 * Builds the graph.
	 */
	public ReactionGraph getReactionGraph(){
		ReactionGraph g = new ReactionGraph();
		
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			if(!r.getLeftParticipantList().isEmpty() && !r.getRightParticipantList().isEmpty()){
				g.addVertex(r);
			}
		}
		
		for(BioPhysicalEntity c : bn.getPhysicalEntityList().values()){
			if(!c.getIsSide()){
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
		}
		return g;
	}
	
	
	
	public BipartiteGraph getBipartiteGraph(){
		BipartiteGraph g = new BipartiteGraph(); 
		for(BioPhysicalEntity v : bn.getPhysicalEntityList().values()){
			g.addVertex(v);
		}
		for(BioReaction r : bn.getBiochemicalReactionList().values()){
			
			Collection<BioPhysicalEntity> left = r.getLeftList().values();
			Collection<BioPhysicalEntity> right = r.getRightList().values();
			if(!left.isEmpty() && !right.isEmpty()){
				
				g.addVertex(r);
				boolean reversible = r.isReversible();
				
				for(BioPhysicalEntity v1 : left){
					g.addEdge(v1, r, new BipartiteEdge(v1, r, false));
					if(reversible){
						g.addEdge(r, v1, new BipartiteEdge(r, v1, true));
					}
				}
				for(BioPhysicalEntity v2 : right){	
					g.addEdge(r, v2, new BipartiteEdge(r, v2, false));	
					if(reversible){
						g.addEdge(v2, r, new BipartiteEdge(v2, r, true));
					}
				}
			}
		}
		return g;
	}
	
	/**
	 * Gets the pathways as source.
	 *
	 * @param e the biophysical entity
	 * @return the pathways as source
	 */
	public static Collection<BioPathway> getPathwaysAsSource(BioPhysicalEntity e){
		HashSet<BioPathway> pathwaysIn = new HashSet<BioPathway>();
		HashSet<BioPathway> pathwaysAsSource = new HashSet<BioPathway>();
		for(BioReaction r : e.getReactionsAsProduct().values()){
			if(!r.isReversible()) pathwaysIn.addAll(r.getPathwayList().values());
		}
		for(BioReaction r : e.getReactionsAsSubstrate().values()){
			for(BioPathway p : r.getPathwayList().values()){
				if(!pathwaysIn.contains(p)) pathwaysAsSource.add(p);
			}
		}
		return pathwaysAsSource;
	}
	
	/**
	 * Gets the pathways as target.
	 *
	 * @param e the biophysical entity
	 * @return the pathways as target
	 */
	public static Collection<BioPathway> getPathwaysAsTarget(BioPhysicalEntity e){
		HashSet<BioPathway> pathwaysIn = new HashSet<BioPathway>();
		HashSet<BioPathway> pathwaysAsTarget = new HashSet<BioPathway>();
		for(BioReaction r : e.getReactionsAsSubstrate().values()){
			if(!r.isReversible()) pathwaysIn.addAll(r.getPathwayList().values());
		}
		for(BioReaction r : e.getReactionsAsProduct().values()){
			for(BioPathway p : r.getPathwayList().values()){
				if(!pathwaysIn.contains(p)) pathwaysAsTarget.add(p);
			}
		}
		return pathwaysAsTarget;
	}
	
	/**
	 * Builds the graph.
	 *
	 * @param sbmlPath the sbml path
	 * @return the pathway graph
	 */
	public PathwayGraph getPathwayGraph(){
		
		PathwayGraph g = new PathwayGraph();
		
		for(BioPathway v : bn.getPathwayList().values()){
			g.addVertex(v);
		}
		
		for(BioPhysicalEntity e : bn.getPhysicalEntityList().values()){
			for(BioPathway p1 : getPathwaysAsTarget(e)){
				for(BioPathway p2 : getPathwaysAsSource(e)){
					if(p1!=p2){
						if(!g.containsEdge(p1, p2)){
							PathwayGraphEdge edge = new PathwayGraphEdge(p1, p2, e);
							g.addEdge(p1, p2, edge);
						}else{
							PathwayGraphEdge edge = g.getEdge(p1, p2);
							edge.addConnectingCompounds(e);
						}
					}
				}
			}
		}
		return g;
	}
	
}
