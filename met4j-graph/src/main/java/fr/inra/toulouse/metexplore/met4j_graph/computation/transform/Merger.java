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
package fr.inra.toulouse.metexplore.met4j_graph.computation.transform;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.text.parser.ParseException;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MetaEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.io.BioNetworkToJSBML;
import fr.inra.toulouse.metexplore.met4j_core.io.JSBMLToBionetwork;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * Class used to merges nodes or edges
 * @author clement
 */
public class Merger {
	
	/**
	 * Merge edges sharing same source and target
	 * @param <V>
	 */
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> void mergeEdges(G g){
		
		//init source target map
		HashMap<V,HashMap<V,ArrayList<E>>> sourceTargetMap = new HashMap<V, HashMap<V,ArrayList<E>>>();
		for(E edge : g.edgeSet()){
			V source = edge.getV1();
			V target = edge.getV2();
			if(!sourceTargetMap.containsKey(source)){
				sourceTargetMap.put(source, new HashMap<V, ArrayList<E>>());
			}
			if(!sourceTargetMap.get(source).containsKey(target)){
				sourceTargetMap.get(source).put(target, new ArrayList<E>());
			}
			sourceTargetMap.get(source).get(target).add(edge);
		}
		
		//create new edge as merging of all edges sharing same source and target
		for(V source:sourceTargetMap.keySet()){
			for(V target:sourceTargetMap.get(source).keySet()){
				ArrayList<E> edgeList=sourceTargetMap.get(source).get(target);
				if(edgeList.size()>1){
					
					//compute new label and new weight
					double mergedWeight=0.0;
					double mergedScore=0.0;
					String label="";
					for(E edge : edgeList){
						mergedWeight+=g.getEdgeWeight(edge);
						mergedScore+=g.getEdgeScore(edge);
						if(label.equals("")){
							label=edge.toString();
						}else{
							label=label+"_"+edge.toString();
						}
					}
							
					//create new edge
					E newEdge = g.getEdgeFactory().createEdge(source, target);
					g.addEdge(source, target, newEdge);
					g.setEdgeWeight(newEdge, mergedWeight);
					g.setEdgeScore(newEdge, mergedScore);
					g.removeAllEdges(edgeList);				
				}
			}
		}
	}
	
	/**
	 * Merge edges sharing same source and target
	 * @param <V>
	 */
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> MergedGraph<V,E> mergeEdgesII(G g){
		
		MergedGraph<V,E> mergedG = new MergedGraph<V,E>();
		
		for(V v : g.vertexSet()){
			mergedG.addVertex(v);
		}
		
		//init source target map
		HashMap<V, HashMap<V, HashSet<E>>> sourceTargetMap = new HashMap<V, HashMap<V,HashSet<E>>>();
		for(E edge : g.edgeSet()){
			V source = edge.getV1();
			V target = edge.getV2();
			if(!sourceTargetMap.containsKey(source)){
				sourceTargetMap.put(source, new HashMap<V, HashSet<E>>());
			}
			if(!sourceTargetMap.get(source).containsKey(target)){
				sourceTargetMap.get(source).put(target, new HashSet<E>());
			}
			sourceTargetMap.get(source).get(target).add(edge);
		}
		
		//create new edge as merging of all edges sharing same source and target
		for(V source:sourceTargetMap.keySet()){
			for(V target:sourceTargetMap.get(source).keySet()){
				Set<E> edgeList=sourceTargetMap.get(source).get(target);
					
				//compute new label and new weight
				double mergedWeight=0.0;
				double mergedScore=0.0;
				String label="";
				for(E edge : edgeList){
					mergedWeight+=g.getEdgeWeight(edge);
					mergedScore+=g.getEdgeScore(edge);
					if(label.equals("")){
						label=edge.toString();
					}else{
						label=label+"_"+edge.toString();
					}
				}
						
				//create new edge
				MetaEdge<V, E> newEdge = new MetaEdge<V, E>(source, target,edgeList);
				mergedG.addEdge(source, target, newEdge);
				mergedG.setEdgeWeight(newEdge, mergedWeight);
				mergedG.setEdgeScore(newEdge, mergedScore);
			}
		}
		
		return mergedG;
	}
	
	/**
	 * Merge nodes with same inchi from different compartment.
	 *
	 * @param g the original graph
	 * @return the merged graph
	 */
	public static CompoundGraph mergeCompartment(CompoundGraph g){
		
		//define a new meta-compartment
		BioCompartment comp = new BioCompartment("metaComp", "metaComp");
		CompoundGraph g2 = new CompoundGraph();
		
		//for several vertex with same inchi, add only one in the graph
		HashMap<String, BioPhysicalEntity> uniqVertex = new HashMap<String, BioPhysicalEntity>();
		for(BioPhysicalEntity v : g.vertexSet()){
			String inchi = v.getInchi();
			if(!StringUtils.isVoid(inchi) && (!uniqVertex.containsKey(inchi))){
				BioPhysicalEntity uniq = new BioPhysicalEntity(v);
				uniq.setReactionsAsProduct(new HashMap<String,BioChemicalReaction>());
				uniq.setReactionsAsSubstrate(new HashMap<String,BioChemicalReaction>());
				uniq.setCompartment(comp);
				//keep the choosen one for a given inchi
				uniqVertex.put(inchi, uniq);
				g2.addVertex(uniq);
			}
		}
		
		HashMap<String, BioChemicalReaction> updatedReaction = new HashMap<String, BioChemicalReaction>();
		for(ReactionEdge e : g.edgeSet()){
			//get source and target inchis
			String inchi1 = e.getV1().getInchi();
			String inchi2 = e.getV2().getInchi();
			BioChemicalReaction r = e.getReaction();
			if(!StringUtils.isVoid(inchi1) && !StringUtils.isVoid(inchi2) && !inchi1.equals(inchi2)){
				//retreive the choosen one for each inchi
				BioPhysicalEntity v1 = uniqVertex.get(inchi1);
				BioPhysicalEntity v2 = uniqVertex.get(inchi2);
				
				//copy reaction
				BioChemicalReaction r2 = null;
				if(updatedReaction.containsKey(r.getId())){
					r2 = updatedReaction.get(r.getId());
				}else{
					r2 = new BioChemicalReaction(r);
					updatedReaction.put(r.getId(), r2);
				}
				
				//update reaction participants to fit with the linked nodes
				if(r2.getLeftList().containsValue(e.getV1())){
					r2.removeLeftCpd(e.getV1());
					r2.addLeftParticipant(new BioPhysicalEntityParticipant(v1));
				}else if(r2.getRightList().containsValue(e.getV1())){
					r2.removeRightCpd(e.getV1());
					r2.addRightParticipant(new BioPhysicalEntityParticipant(v1));
				}
				if(r2.getLeftList().containsValue(e.getV2())){
					r2.removeLeftCpd(e.getV2());
					r2.addLeftParticipant(new BioPhysicalEntityParticipant(v2));
				}else if(r2.getRightList().containsValue(e.getV2())){
					r2.removeRightCpd(e.getV2());
					r2.addRightParticipant(new BioPhysicalEntityParticipant(v2));
				}
				
				//create new edge
				ReactionEdge e2 = new ReactionEdge(v1, v2, r2);
				g2.addEdge(v1, v2, e2);
			}
		}	
		return g2;
	}
	
	/**
	 * When compartment is specified in compound's id,
	 * Merge nodes with same truncated id from different compartment,
	 * given a biograph and a regex used to retrieve an id without compartment part
	 * Example of regex for Recon model : "^(.+)_\\w$"
	 * 	
	 * 
	 * @param g the original graph
	 * @param regex the regex used to retrieve the constant part of the id
	 * @return the merged graph
	 */
	public static CompoundGraph mergeCompartmentFromId(CompoundGraph g, String regex){
		
		//define a new meta-compartment
		BioCompartment comp = new BioCompartment("metaComp", "metaComp");
		CompoundGraph g2 = new CompoundGraph();
		
		//for several vertex with same truncated id, add only one in the graph
		HashMap<String, BioPhysicalEntity> uniqVertex = new HashMap<String, BioPhysicalEntity>();
		for(BioPhysicalEntity v : g.vertexSet()){
			String id = v.getId();
			Matcher m = Pattern.compile(regex).matcher(id);
			if(m.matches()){
				id=m.group(1);
				if (!uniqVertex.containsKey(id)){
					BioPhysicalEntity uniq = new BioPhysicalEntity(v);
					uniq.setEntityAnnot(v.getEntityAnnot());
					uniq.setId(id);
					uniq.setReactionsAsProduct(new HashMap<String,BioChemicalReaction>());
					uniq.setReactionsAsSubstrate(new HashMap<String,BioChemicalReaction>());
					uniq.setCompartment(comp);
					//keep the choosen one for a given truncated ids
					uniqVertex.put(id, uniq);
					g2.addVertex(uniq);
				}
			}
			
		}
		
		HashMap<String, BioChemicalReaction> updatedReaction = new HashMap<String, BioChemicalReaction>();
		for(ReactionEdge e : g.edgeSet()){
			//get source and target ids
			String id1 = e.getV1().getId();
			String id2 = e.getV2().getId();
			Matcher m1,m2;
			m1 = Pattern.compile(regex).matcher(id1);
			m2 = Pattern.compile(regex).matcher(id2);
			
			if(m1.matches() && m2.matches()){
				id1=m1.group(1);
				id2=m2.group(1);
				
				BioChemicalReaction r = e.getReaction();
				if(!id1.equals(id2)){
					//retreive the choosen one for each inchi
					BioPhysicalEntity v1 = uniqVertex.get(id1);
					BioPhysicalEntity v2 = uniqVertex.get(id2);
					
					//copy reaction
					BioChemicalReaction r2 = null;
					if(updatedReaction.containsKey(r.getId())){
						r2 = updatedReaction.get(r.getId());
					}else{
						r2 = new BioChemicalReaction(r);
						updatedReaction.put(r.getId(), r2);
					}
					
					//update reaction participants to fit with the linked nodes
					if(r2.getLeftList().containsValue(e.getV1())){
						r2.removeLeftCpd(e.getV1());
						r2.addLeftParticipant(new BioPhysicalEntityParticipant(v1));
					}else if(r2.getRightList().containsValue(e.getV1())){
						r2.removeRightCpd(e.getV1());
						r2.addRightParticipant(new BioPhysicalEntityParticipant(v1));
					}
					if(r2.getLeftList().containsValue(e.getV2())){
						r2.removeLeftCpd(e.getV2());
						r2.addLeftParticipant(new BioPhysicalEntityParticipant(v2));
					}else if(r2.getRightList().containsValue(e.getV2())){
						r2.removeRightCpd(e.getV2());
						r2.addRightParticipant(new BioPhysicalEntityParticipant(v2));
					}
					
					//create new edge
					ReactionEdge e2 = new ReactionEdge(v1, v2, r2);
					g2.addEdge(v1, v2, e2);
				}
			}
		}	
		return g2;
	}
	
	/**
	 * When compartment is specified in compound's id,
	 * Merge nodes with same truncated id from different compartment,
	 * given a BioNetwork and a regex used to retrieve an id without compartment part
	 * Example of regex for Recon model : "^(.+)_\\w$"
	 * 	
	 * 
	 * @param bn the original BioNetwork
	 * @param regex the regex used to retrieve the constant part of the id
	 * @return the merged BioNetwork
	 */
	public static BioNetwork mergeCompartmentFromId(BioNetwork bn, String regex){
		
		BioNetwork bn2 = new BioNetwork();
		
		//define a new meta-compartment
		BioCompartment comp = new BioCompartment("metaComp", "metaComp");
		bn2.addCompartment(comp);
		for(BioUnitDefinition unit : bn.getUnitDefinitions().values()){
			bn2.addUnitDefinition(unit);
		}
		
		
		//for several vertex with same truncated id, add only one in the network
		HashMap<String, BioPhysicalEntity> uniqCompound = new HashMap<String, BioPhysicalEntity>();
		for(BioPhysicalEntity v : bn.getPhysicalEntityList().values()){
			String id = v.getId();
			Matcher m = Pattern.compile(regex).matcher(id);
			if(m.matches()){
				id=m.group(1);
				if (!uniqCompound.containsKey(id)){
					BioPhysicalEntity uniq = new BioPhysicalEntity(v);
					uniq.setEntityAnnot(v.getEntityAnnot());
					uniq.setId(id);
					uniq.setReactionsAsProduct(new HashMap<String,BioChemicalReaction>());
					uniq.setReactionsAsSubstrate(new HashMap<String,BioChemicalReaction>());
					uniq.setCompartment(comp);
					//keep the choosen one for a given truncated ids
					uniqCompound.put(id, uniq);
					bn2.addPhysicalEntity(uniq); 
				}
			}
			
		}
		for(BioProtein prot : bn.getProteinList().values()){
			String id = prot.getId();
			Matcher m = Pattern.compile(regex).matcher(id);
			if(m.matches()){
				id=m.group(1);
				if (!uniqCompound.containsKey(id)){
					BioProtein uniq = new BioProtein(prot);
					uniq.setId(id);
					uniq.setCompartment(comp);
					uniqCompound.put(id, uniq);
					bn2.addProtein(uniq);
				}
			}
		}
		
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			BioChemicalReaction r2 = new BioChemicalReaction(r);
			for(BioPhysicalEntity e : r.getLeftList().values()){
				Matcher m = Pattern.compile(regex).matcher(e.getId());
				if(m.matches()){
					BioPhysicalEntity e2 =uniqCompound.get(m.group(1));
					r2.removeLeftCpd(e);
					String stochio = r.getLeftParticipantList().get(e.getId()).getStoichiometricCoefficient();
					r2.addLeftParticipant(new BioPhysicalEntityParticipant(e2,stochio));
				}
			}
			for(BioPhysicalEntity e : r.getRightList().values()){
				Matcher m = Pattern.compile(regex).matcher(e.getId());
				if(m.matches()){
					BioPhysicalEntity e2 =uniqCompound.get(m.group(1));
					r2.removeRightCpd(e);
					String stochio = r.getRightParticipantList().get(e.getId()).getStoichiometricCoefficient();
					r2.addRightParticipant(new BioPhysicalEntityParticipant(e2,stochio));
				}
			}
			
			r2.setEnzList(new HashMap<String, BioPhysicalEntity>());
			for(BioPhysicalEntity enz : r.getEnzList().values()){
				if(enz instanceof BioComplex){
					BioComplex cplx = (BioComplex) enz;
					String newId = cplx.getId();
					HashMap<String,BioPhysicalEntityParticipant> newComponentList = new HashMap<String,BioPhysicalEntityParticipant>();
					for(BioPhysicalEntity participant : cplx.getAllComponentList().values()){
						String id = participant.getId();
						Matcher m = Pattern.compile(regex).matcher(id);
						if(m.matches()){
							String trunkId=m.group(1);
							newId=newId.replace(id, trunkId);
							BioPhysicalEntityParticipant uniqParticipant = new BioPhysicalEntityParticipant(uniqCompound.get(trunkId));
							newComponentList.put(trunkId, uniqParticipant);
						}
					}
					
					if(!uniqCompound.containsKey(newId)){
						BioComplex uniq = new BioComplex(cplx);
						uniq.setId(newId);
						uniq.setCompartment(comp);
						uniq.setComponentList(newComponentList);
						
						bn2.addComplex(uniq);
						r2.addEnz(uniq);
						uniqCompound.put(newId, uniq);
					}
				}else{
					Matcher m = Pattern.compile(regex).matcher(enz.getId());
					if(m.matches()){
						BioPhysicalEntity enz2 =uniqCompound.get(m.group(1));
						r2.addEnz(enz2);
					}
				}
			}
			bn2.addBiochemicalReaction(r2);
			
		}	
		return bn2;
	}
	
	/**
	 * remove reaction where substrates and products are the same compounds
	 * @param bioNetwork
	 * @return the number of deleted reactions
	 */
	public static int removeTransport(BioNetwork bn){
		Collection<String> reactionToRemove = new HashSet<String>();
		for(String rId : bn.getBiochemicalReactionList().keySet()){
			BioChemicalReaction r = bn.getBiochemicalReactionList().get(rId);
			
			//remove Exchange reaction
			if(r.getRightList().isEmpty() || r.getLeftList().isEmpty()){
				reactionToRemove.add(rId);
			}
			else{
//				//FOLLOWING DOESN'T CONSIDER ATP DEPENDANT TRANSPORT 
//				if(r.getLeftList().equals(r.getRightList())){
//					reactionToRemove.add(rId);
//				}
				
				for(BioPhysicalEntity e : r.getLeftList().values()){
					if(r.getRightList().containsValue(e)){
						reactionToRemove.add(rId);
//						//output for manual checking
//						Pattern regex = Pattern.compile(".*transport.*", Pattern.CASE_INSENSITIVE);
//						System.err.println(rId+"\t"
//								+r.getName()+"\t"
//								+org.apache.commons.lang.StringUtils.join(r.getPathwayList().keySet(),"; ")+"\t"
//								+org.apache.commons.lang.StringUtils.join(r.getLeftList().keySet(),"+")+"\t"
//								+org.apache.commons.lang.StringUtils.join(r.getRightList().keySet(),"+")+"\t"
//								+(r.getLeftList().equals(r.getRightList()))+"\t"
//								+(regex.matcher(r.getName()).matches() || regex.matcher(org.apache.commons.lang.StringUtils.join(r.getPathwayList().keySet(),"; ")).matches()));
						break;
					}
				}
			}
			
		}
		
		for(String rId : reactionToRemove){
			bn.removeBioChemicalReaction(rId);
		}
		
		return reactionToRemove.size();
	}
	
	/**
	 * merge reaction sharing reactions and products
	 */
	public static int mergeReactions(BioNetwork bn){
		//for several reactions with same substrates and products, add only one in the network
		Collection<String> reactionToRemove = new HashSet<String>();
		HashMap<Integer, BioChemicalReaction> uniqReaction = new HashMap<Integer, BioChemicalReaction>();
		for(BioChemicalReaction r : bn.getBiochemicalReactionList().values()){
			int hash = Objects.hash(r.getListOfSubstrates(),r.getListOfProducts());
			System.out.println(r.getId());
			if (!uniqReaction.containsKey(hash)){
				uniqReaction.put(hash, r);
			}else{
				//add reaction to the to-be-removed list
				reactionToRemove.add(r.getId());
				//update compounds links to reactions
				BioChemicalReaction choosenOne = uniqReaction.get(hash);
				for(BioPhysicalEntity e : r.getListOfSubstrates().values()){
					e.getReactionsAsSubstrate().remove(r.getId());
					e.getReactionsAsSubstrate().put(choosenOne.getId(), choosenOne);
				}
				for(BioPhysicalEntity e : r.getListOfProducts().values()){
					e.getReactionsAsProduct().remove(r.getId());
					e.getReactionsAsProduct().put(choosenOne.getId(), choosenOne);
				}
			}
		}
		
		for(String rId :reactionToRemove){
			bn.removeBioChemicalReaction(rId);
		}
		
		return reactionToRemove.size();
	}
	

}
