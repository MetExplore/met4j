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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis;

import java.util.Iterator; 
import java.util.LinkedList; 
import java.util.Queue;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
 
 
/** 
 * The Class used to compute the scope network of a given set of compounds
 * See DOI:10.1007/s00239-005-0027-1 for more information.
 *
 * The class use a bipartite graph traversal that use reaction informations to ensure reactions availability.
 * Consequently, the bipartite graph structure should not conflict with reactions informations.
 */ 
public class ScopeCompounds{ 
   
  /** The graph */ 
  private final BipartiteGraph g;
   
  /** The available compounds. */ 
  private final BioCollection <BioMetabolite> inCpds;
   
  /** The bootstrap compounds, i.e. side compounds. */ 
  private final BioCollection<BioMetabolite> bootstrapCpds;
   
  /** The compounds to reach (optional) */ 
  private final BioCollection<BioMetabolite> cpdToReach;
   
  /** The reactions to avoid. */ 
  private final BioCollection<BioReaction> reactionToAvoid;
   
  /** 
   * Instantiates a new scope class 
   * 
   * @param bipartiteGraph the bipartite graph 
   * @param inCpds the available compounds. 
   * @param bootstrapCpds The bootstrap compounds, i.e. side compounds. 
   * @param cpdToReach The compounds to reach (optional) 
   * @param reactionToAvoid the reactions to avoid 
   */ 
  public ScopeCompounds(BipartiteGraph bipartiteGraph, BioCollection<BioMetabolite> inCpds, BioCollection<BioMetabolite> bootstrapCpds, BioCollection<BioMetabolite> cpdToReach, BioCollection<BioReaction> reactionToAvoid){
      this.g =bipartiteGraph;
    this.inCpds=inCpds; 
    this.bootstrapCpds=bootstrapCpds; 
    this.cpdToReach=cpdToReach; 
    this.reactionToAvoid=reactionToAvoid; 
  } 
   
  /** 
   * Instantiates a new scope class. 
   * 
   * @param bipartiteGraph the bipartite graph 
   * @param inCpds the available compounds. 
   * @param bootstrapCpds The bootstrap compounds, i.e. side compounds. 
   * @param reactionToAvoid the reactions to avoid 
   */ 
  public ScopeCompounds(BipartiteGraph bipartiteGraph, BioCollection <BioMetabolite> inCpds, BioCollection<BioMetabolite> bootstrapCpds, BioCollection<BioReaction> reactionToAvoid){
      this.g =bipartiteGraph;
    this.inCpds=inCpds; 
    this.bootstrapCpds=bootstrapCpds;
      this.cpdToReach = new BioCollection<>();
    this.reactionToAvoid=reactionToAvoid; 
  } 
   
  /** 
   * Gets the scope network. 
   * 
   * @return the scope network 
   */ 
  public BipartiteGraph getScopeNetwork() throws IllegalArgumentException{

    //check network consistency
    if(!this.g.isConsistent()) throw new IllegalArgumentException("The network structure must be consistent with reaction reactant lists") ;

    BipartiteGraph scopeNetwork = new BipartiteGraph();
    //Instantiate a custom iterator for the bipartite graph, similar to Breath first search, but a reaction can be visited only if all predecessors have already been visited 
    Traversal traversal = new Traversal();
     
    //Go until their is no reaction with all its substrate available. 
    while(traversal.hasNext()){   
      BioReaction r = traversal.next();
       
      //if a reaction is visited, all its reactants are available, consequently we add to the scope network the reaction and its neighborhood from the original graph 
      for(BipartiteEdge e : g.edgesOf(r)){
        if(!bootstrapCpds.contains(e.getV1()) && !bootstrapCpds.contains(e.getV2())){ //bootstrap compounds are not added to the scope network
          if(!scopeNetwork.containsVertex(e.getV1())){ 
            scopeNetwork.addVertex(e.getV1()); 
          } 
          if(!scopeNetwork.containsVertex(e.getV2())){ 
            scopeNetwork.addVertex(e.getV2()); 
          } 
          scopeNetwork.addEdge(e.getV1(), e.getV2(), e); 
        } 
      } 
    } 
     
    //If there is some targets compounds, a pruning step is necessary to iteratively remove reactions available but that do not lead to any target compoudns 
    if(!cpdToReach.isEmpty()) pruning(scopeNetwork);
     
    return scopeNetwork; 
  } 
   
  /** 
   * Prunning step, iteratively remove reactions available but that do not lead to any target compoudns 
   * 
   * @param scopeNetwork the scope network 
   */ 
  private void pruning(BipartiteGraph scopeNetwork){
    BioCollection<BioEntity> vertexToRemove = new BioCollection<>();
     
    //retreive sinks that are not in the set of target 
    for(BioEntity e : scopeNetwork.vertexSet()){
      if(scopeNetwork.outDegreeOf(e)==0 && !cpdToReach.contains(e)) vertexToRemove.add(e);

    } 
     
    //go until their is no sink that do not belong to the target set 
    while(!vertexToRemove.isEmpty()){
      scopeNetwork.removeAllVertices(vertexToRemove); //remove sinks, potentially creating new sinks. 
      //reset sink list 
      vertexToRemove = new BioCollection<>();
      //retreive sinks that are not in the set of target 
      for(BioEntity e : scopeNetwork.vertexSet()){
        if(scopeNetwork.outDegreeOf(e)==0 && !cpdToReach.contains(e)) vertexToRemove.add(e);
      } 
    } 
 
  } 
   
   
  /** 
   * A custom iterator for the bipartite graph, similar to Breath first search, but a reaction can be visited only if all predecessors have already been visited 
   */ 
  private class Traversal implements Iterator<BioReaction>{ 
     
    /** The visited compounds. */ 
    private final BioCollection<BioMetabolite> visitedCompounds = new BioCollection<>();
     
    /** The visited reactions. */ 
    private final BioCollection<BioReaction> visitedReactions = new BioCollection<>();
       
      /** The reaction queue. */ 
      private final Queue<BioReaction> reactionQueue = new LinkedList<>();
       
      /** 
       * Instantiates a new traversal. 
       */
      Traversal() {
          this.visitedCompounds.addAll(inCpds);
          this.visitedCompounds.addAll(bootstrapCpds);
          for(BioMetabolite cpd : inCpds){
              visit(cpd);
          } 
      } 
 
      /* (non-Javadoc) 
       * @see java.util.Iterator#hasNext() 
       */ 
      @Override 
      public boolean hasNext() { 
        if(reactionQueue.isEmpty()) return false; //no remaining reaction
        while(!reactionQueue.isEmpty()){
          BioReaction r = reactionQueue.peek();
          if(this.visitedCompounds.containsAll(r.getLeftsView())){ //check susbtrate availability
            return true; 
          }else if(r.isReversible() && this.visitedCompounds.containsAll(r.getRightsView())){ //check product availability if reversible
            return true; 
          }
            reactionQueue.remove(); //remove unavailable reaction from the queue
        } 
        return false; //all reaction from the original queue are unavailable 
      } 
       
      /** 
       * Add consuming reaction of visited compound to the queue of reactions 
       * 
       * @param entity the entity 
       */ 
      private void visit(BioMetabolite entity){
        if(!bootstrapCpds.contains(entity)){ //skip bootstrap compounds
          Set<BioEntity> successor = g.successorListOf(entity);
          for (BioEntity neighbor : g.neighborListOf(entity)) {
              BioReaction r = null; 
              if(neighbor instanceof BioReaction){ 
                r = (BioReaction)neighbor; 
              } 
              if(successor.contains(r) || r.isReversible()){ //if irreversible reaction, only consider reaction successor in the graph 
                  if (!reactionToAvoid.contains(r) && !this.visitedReactions.contains(r) && !this.reactionQueue.contains(r)) {
                      this.reactionQueue.add(r);
                  } 
              } 
            } 
        }

          this.visitedCompounds.add(entity);
      } 
 
      /* (non-Javadoc) 
       * @see java.util.Iterator#next() 
       */ 
      @Override 
      public BioReaction next() { 
           
          //removes from front of queue 
        BioReaction nextReaction = reactionQueue.remove();
        boolean available = false; 
        boolean reverse = false; 
         
        //check if all substrate have already been visited 
        if(this.visitedCompounds.containsAll(nextReaction.getLeftsView())){
          available=true; 
          reverse=false; 
        }else if(nextReaction.isReversible() && this.visitedCompounds.containsAll(nextReaction.getRightsView())){ //check product availability if reversible
          available=true; 
          reverse=true; 
        } 
         
        //iteratively remove reaction until one available one is found. If the method hasNext() has been called before, and an available reaction exist, head of queue should be available, so the loop is skipped 
        while(!available && !reactionQueue.isEmpty()){
          nextReaction = reactionQueue.remove();
          if(this.visitedCompounds.containsAll(nextReaction.getLeftsView())){
            available=true; 
            reverse=false; 
          }else if(nextReaction.isReversible() && this.visitedCompounds.containsAll(nextReaction.getRightsView())){
            available=true; 
            reverse=true; 
          } 
        } 
        if(!available && reactionQueue.isEmpty()) return null; //no available reaction

        visitedReactions.add(nextReaction); //mark visited reaction
         
        //mark products as visited and add products' consuming reactions to the queue 
        if(!reverse){ 
          for(BioMetabolite neighbor : nextReaction.getRightsView()){
            if(!visitedCompounds.contains(neighbor)){
                visit(neighbor);
            } 
          } 
        }else{ 
          for(BioMetabolite neighbor : nextReaction.getLeftsView()){
              this.visitedCompounds.add(neighbor);
              visit(neighbor);
          } 
        } 
         
        //return the available reaction 
        return nextReaction; 
      } 
  } 
}