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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;


/**
 * The Class to convert Bionetwork to BioGraph.
 *
 * @author clement
 * @version $Id: $Id
 */
public class Bionetwork2BioGraph {

	private final BioNetwork bn;
	
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
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph} object.
	 */
	public CompoundGraph getCompoundGraph(){
		CompoundGraph g = new CompoundGraph();
		
		for(BioMetabolite v : bn.getMetabolitesView()){
			g.addVertex(v);
		}
		
		for(BioReaction r : bn.getReactionsView()){
			boolean reversible = r.isReversible();
			Collection<BioMetabolite> left = bn.getLefts(r);
			Collection<BioMetabolite> right = bn.getRights(r);
			if(!left.isEmpty() && !right.isEmpty()){
				for(BioMetabolite v1 : left){
					for(BioMetabolite v2 : right){
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
	 * <p>getReactionGraph.</p>
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph} object.
	 */
	public ReactionGraph getReactionGraph(){
		return getReactionGraph(new BioCollection<>());
	}

	/**
	 * Builds the graph.
	 *
	 * @param cofactors a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} object.
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph} object.
	 */
	public ReactionGraph getReactionGraph(BioCollection<BioMetabolite> cofactors){

		ReactionGraph g = new ReactionGraph();
		HashMap<BioMetabolite,BioCollection<BioReaction>> consumingReaction = new HashMap<>();
		HashMap<BioMetabolite,BioCollection<BioReaction>> productingReaction = new HashMap<>();

		for(BioReaction r : bn.getReactionsView()){
			if(!r.getLeftsView().isEmpty() && !r.getRightsView().isEmpty()) {
				g.addVertex(r);
				r.getLeftsView()
						.forEach(s -> {
							consumingReaction.computeIfAbsent(s, k -> new BioCollection<>()).add(r);
							if (r.isReversible()) {
								productingReaction.computeIfAbsent(s, k -> new BioCollection<>()).add(r);
							}
						});
				r.getRightsView()
						.forEach(p -> {
							productingReaction.computeIfAbsent(p, k -> new BioCollection<>()).add(r);
							if (r.isReversible()) {
								consumingReaction.computeIfAbsent(p, k -> new BioCollection<>()).add(r);
							}
						});
			}
		}

		consumingReaction.keySet().removeAll(cofactors);
		productingReaction.keySet().removeAll(cofactors);

		consumingReaction.keySet().retainAll(productingReaction.keySet());

		consumingReaction.forEach((c, sources) -> sources.forEach((r1) -> {
			productingReaction.get(c).forEach(r2 -> {

				if(r1!=r2) g.addEdge(r2,r1,new CompoundEdge(r2,r1,c));

			});
		}));
		return g;
	}
	/**
	 * Builds the graph.
	 *
	 * @param cofactors a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} object.
	 * @param rExclude a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} object.
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph} object.
	 */
	public ReactionGraph getReactionGraph(BioCollection<BioMetabolite> cofactors, BioCollection<BioReaction> rExclude){

		ReactionGraph g = new ReactionGraph();
		HashMap<BioMetabolite,BioCollection<BioReaction>> consumingReaction = new HashMap<>();
		HashMap<BioMetabolite,BioCollection<BioReaction>> productingReaction = new HashMap<>();
		BioCollection<BioReaction> bionetReactions = bn.getReactionsView();
		bionetReactions.removeAll(rExclude);

		for(BioReaction r : bionetReactions){
			if(!r.getLeftsView().isEmpty() && !r.getRightsView().isEmpty()) {
				g.addVertex(r);
				r.getLeftsView()
						.forEach(s -> {
							consumingReaction.computeIfAbsent(s, k -> new BioCollection<>()).add(r);
							if (r.isReversible()) {
								productingReaction.computeIfAbsent(s, k -> new BioCollection<>()).add(r);
							}
						});
				r.getRightsView()
						.forEach(p -> {
							productingReaction.computeIfAbsent(p, k -> new BioCollection<>()).add(r);
							if (r.isReversible()) {
								consumingReaction.computeIfAbsent(p, k -> new BioCollection<>()).add(r);
							}
						});
			}
		}
		
		consumingReaction.keySet().removeAll(cofactors);
		productingReaction.keySet().removeAll(cofactors);

		consumingReaction.keySet().retainAll(productingReaction.keySet());

		consumingReaction.forEach((c, sources) -> sources.forEach((r1) -> {
			productingReaction.get(c).forEach(r2 -> {

				if(r1!=r2) g.addEdge(r2,r1,new CompoundEdge(r2,r1,c));

			});
		}));
		return g;
	}
	/**
	 * <p>getBipartiteGraph.</p>
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph} object.
	 */
	public BipartiteGraph getBipartiteGraph(){
		BipartiteGraph g = new BipartiteGraph();
		for(BioMetabolite v : bn.getMetabolitesView()){
			g.addVertex(v);
		}
		for(BioReaction r : bn.getReactionsView()){
			
			Collection<BioMetabolite> left = bn.getLefts(r);
			Collection<BioMetabolite> right = bn.getRights(r);
			//if(!left.isEmpty() && !right.isEmpty()){
				
			g.addVertex(r);
			boolean reversible = r.isReversible();

			for(BioMetabolite v1 : left){
				g.addEdge(v1, r, new BipartiteEdge(v1, r, false));
				if(reversible){
					g.addEdge(r, v1, new BipartiteEdge(r, v1, true));
				}
			}
			for(BioMetabolite v2 : right){
				g.addEdge(r, v2, new BipartiteEdge(r, v2, false));
				if(reversible){
					g.addEdge(v2, r, new BipartiteEdge(v2, r, true));
				}
			}
		}
		return g;
	}

	/**
	 * Builds a pathway "seed" graph, where a directed edge exists between two pathways if one produces a sink that is a source of the other.
	 * i.e. it constructs an overlap graph that is ignoring intermediary compounds and consider only inputs and outputs of pathways
	 *
	 * @return the pathway graph
	 */
	public PathwayGraph getPathwayGraph(){
		return getPathwayGraph(new BioCollection<BioMetabolite>());
	}

	/**
	 * Builds a pathway "seed" graph, where a directed edge exists between two pathways if one produces a sink that is a source of the other.
	 * i.e. it constructs an overlap graph that is ignoring intermediary compounds and consider only inputs and outputs of pathways
	 * @param cofactors a list of compounds to ignore for connecting pathways
	 * @return
	 */
	public PathwayGraph getPathwayGraph(BioCollection<BioMetabolite> cofactors){

		PathwayGraph g = new PathwayGraph();
		HashMap<BioPathway,BioCollection<BioMetabolite>> pathwaysSources = new HashMap<>();
		HashMap<BioPathway,BioCollection<BioMetabolite>> pathwaysProducts = new HashMap<>();

		//init
		for(BioPathway v : bn.getPathwaysView()){
			//add to graph
			g.addVertex(v);

			BioCollection<BioMetabolite> sources = new BioCollection<>();
			BioCollection<BioMetabolite> products = new BioCollection<>();
			//get reactions
			for(BioReaction r : bn.getReactionsFromPathways(v)) {

				//get sources and products
				if(!r.isReversible()) {
					sources.addAll(r.getLeftsView());
					products.addAll(r.getRightsView());
				}else {
					products.addAll(r.getLeftsView());
					products.addAll(r.getRightsView());
				}

			}
			sources.removeAll(products);

			pathwaysSources.put(v, sources);
			pathwaysProducts.put(v, products);
		}

		//create connections
		for(BioPathway p1 : bn.getPathwaysView()){
			for(BioPathway p2 : bn.getPathwaysView()){
				if(p1!=p2){
					BioCollection<BioMetabolite> connectors =
							pathwaysSources.get(p2).stream()
							.filter(m -> pathwaysProducts.get(p1).contains(m))
							.collect(Collectors.toCollection(BioCollection::new));
					connectors.removeAll(cofactors);
					if(!connectors.isEmpty()) {
						PathwayGraphEdge edge = new PathwayGraphEdge(p1, p2, connectors);
						g.addEdge(p1, p2, edge);
					}
				}
			}
		}
		return g;
	}

	/**
	 * Builds a pathway "overlap" graph, where an undirected edge exists between two pathways they share a common compound.
	 *
	 * @param cofactors a list of compounds to ignore for connecting pathways
	 * @return the pathway graph
	 */
	public PathwayGraph getPathwayOverlapGraph(BioCollection<BioMetabolite> cofactors){
		PathwayGraph g = new PathwayGraph();
		//add nodes
		for(BioPathway p : bn.getPathwaysView()){
			g.addVertex(p);
		}
		//create connections
		ArrayList<BioPathway> plist = new ArrayList<>(bn.getPathwaysView());
		for (int i = 0; i < plist.size()-1; i++){
			for (int j = i+1; j < plist.size(); j++){
				BioPathway p1 = plist.get(i);
				BioPathway p2 = plist.get(j);
				BioCollection<BioMetabolite> connectors = new BioCollection<>(bn.getMetabolitesFromPathway(p1));
				connectors.retainAll(bn.getMetabolitesFromPathway(p2));
				connectors.removeAll(cofactors);
				if(!connectors.isEmpty()) {
					PathwayGraphEdge edge = new PathwayGraphEdge(p1, p2, connectors);
					g.addEdge(p1, p2, edge);
					PathwayGraphEdge edger = new PathwayGraphEdge(p2, p1, connectors);
					g.addEdge(p2, p1, edger);
				}
			}
		}
		return g;
	}

}
