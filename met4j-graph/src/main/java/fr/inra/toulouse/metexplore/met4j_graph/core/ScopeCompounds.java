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
package fr.inra.toulouse.metexplore.met4j_graph.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.text.parser.ParseException;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.io.BioNetworkToJSBML;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;



public class ScopeCompounds {
	
	private BioNetwork originalBioNetwork;
	private BioNetwork scopeNetwork;
	private BioNetwork inverseScopeNetwork;
	private Set <String> inCpds;
	private Set<String> bootstrapCpds;
	private Integer stepNo; 					// Current numero of the iteration
	private HashMap<String, BioChemicalReaction> availableReactions; // Reactions still available
	
	//	Indicates the ids of the added elements and the numero of the step when they have appeared 
	private HashMap<String, Integer> currentCpdsSteps = new  HashMap<String, Integer>();
	
	private HashMap<String, Integer> currentReactionsSteps = new  HashMap<String, Integer>();
	
	private Set<String> cpdToReach;
	public Boolean useReversibleReactionsOnlyOnce;
	

	public Boolean forward;
	
	
	
	
	/*
	 * Constructor
	 */
	
	public ScopeCompounds(BioNetwork bioNetwork, Set <String> in, Set<String> bs, String cpdToReach, Set<String> reactionsToAvoid, Boolean useReversibleReactionsOnlyOnce, Boolean forward) {
		this.setOriginalBioNetwork(bioNetwork);
		this.setInCpds(in);
		this.setBootstrapCpds(bs);
		this.setStepNo(0);
		this.setCpdToReach(new HashSet<String>());
		this.getCpdToReach().add(cpdToReach);		
		
		this.setScopeNetwork(new BioNetwork());
		
		this.useReversibleReactionsOnlyOnce = useReversibleReactionsOnlyOnce;
		this.forward = forward;
		
		HashMap <String, BioChemicalReaction> listOfReactions = new HashMap <String, BioChemicalReaction>(bioNetwork.getBiochemicalReactionList());
		this.setAvailableReactions(listOfReactions);
		
		for(String reactionToAvoid : reactionsToAvoid) {
			this.getAvailableReactions().remove(reactionToAvoid);
		}
		
		for(Iterator<String> iter=in.iterator(); iter.hasNext();) {
			String cpdId = iter.next();
			
			if(this.getOriginalBioNetwork().getPhysicalEntityList().containsKey(cpdId) == true) {
				this.putCpdStep(cpdId, 0);
			}
		}
		
	}	
	
	
	public ScopeCompounds(BioNetwork bioNetwork, Set <String> in, Set<String> bs, Set<String> cpdsToReach, Set<String> reactionsToAvoid, Boolean useReversibleReactionsOnlyOnce, Boolean forward) {
		this.setOriginalBioNetwork(bioNetwork);
		this.setInCpds(in);
		this.setBootstrapCpds(bs);
		this.setStepNo(0);
		if(cpdsToReach != null) {
			this.setCpdToReach(cpdsToReach);
		}
		else {
			this.setCpdToReach(new HashSet<String>());
		}
		this.setScopeNetwork(new BioNetwork());
		
		this.useReversibleReactionsOnlyOnce = useReversibleReactionsOnlyOnce;
		this.forward = forward;
		
		HashMap <String, BioChemicalReaction> listOfReactions = new HashMap <String, BioChemicalReaction>(bioNetwork.getBiochemicalReactionList());
		this.setAvailableReactions(listOfReactions);
		
		for(String reactionToAvoid : reactionsToAvoid) {
			this.getAvailableReactions().remove(reactionToAvoid);
		}
		
		for(Iterator<String> iter=in.iterator(); iter.hasNext();) {
			String cpdId = iter.next();
			
			if(this.getOriginalBioNetwork().getPhysicalEntityList().containsKey(cpdId) == true) {
				this.putCpdStep(cpdId, 0);
			}
		}
		
	}	
	
	
	
	
	/*
	 ** Iteration of the process
	 * returns the number of compounds added in the step
	 */
	
	public int run () {
		
		int nbCpdsSupp = 0;
		
		HashMap<String, Integer> currentListOfcurrentSteps = new HashMap<String, Integer>();
		
		currentListOfcurrentSteps.putAll(this.getCurrentCpdsSteps());
		
		this.setStepNo(this.getStepNo() + 1);
		
		for(Iterator<String> iterCpd = currentListOfcurrentSteps.keySet().iterator(); iterCpd.hasNext();) {
			String cpdId = iterCpd.next();
			
			BioPhysicalEntity cpd = this.getOriginalBioNetwork().getPhysicalEntityList().get(cpdId);
			
			this.getScopeNetwork().addPhysicalEntity(this.getOriginalBioNetwork().getPhysicalEntityList().get(cpdId));
			HashMap<String, BioChemicalReaction> listOfReactions;
			
			if(this.forward == true) {
//				listOfReactions = this.getOriginalBioNetwork().getListOfReactionsAsSubstrate(cpdId);
				listOfReactions = cpd.getReactionsAsSubstrate();
			}
			else {
//				listOfReactions = this.getOriginalBioNetwork().getListOfReactionsAsProduct(cpdId);
				listOfReactions = cpd.getReactionsAsProduct();
			}
			
			for(BioChemicalReaction reaction : listOfReactions.values()) {
				
//				System.out.println("Reaction : "+reaction);
				
				if(this.getAvailableReactions().containsKey(reaction.getId())) {
					
//					System.out.println("Available");
					
					HashMap<String, BioPhysicalEntity> lefts = reaction.getListOfSubstrates();
					HashMap<String, BioPhysicalEntity> rights = reaction.getListOfProducts();
					
					if(reaction.getReversiblity().compareToIgnoreCase("reversible") == 0) {

						HashMap<String, BioPhysicalEntity> tmpLeft;
						HashMap<String, BioPhysicalEntity> tmpRight;
						
							tmpLeft = reaction.getLeftList();
							tmpRight = reaction.getRightList();
						
							if(this.forward == true) {
								
								rights = tmpRight;
								lefts = tmpLeft;
								
								if(tmpRight.containsKey(cpdId))  {
									lefts = tmpRight;
									rights = tmpLeft;
									// To indicate the direction used in the reaction
									reaction.setFlag(false);
								}
								if(tmpLeft.containsKey(cpdId))  { 
									rights = tmpRight;
									lefts = tmpLeft;
									// To indicate the direction used in the reaction
									reaction.setFlag(true);
								}
							}
							else {
								
								rights = tmpRight;
								lefts = tmpLeft;
								
								if(tmpLeft.containsKey(cpdId)) {
									lefts = tmpRight;
									rights = tmpLeft;
									// To indicate the direction used in the reaction
									reaction.setFlag(false);
								}
								if(tmpRight.containsKey(cpdId))  {
									rights = tmpRight;
									lefts = tmpLeft;
									// To indicate the direction used in the reaction
									reaction.setFlag(true);
								}
							}
					}
					
					HashMap<String, BioPhysicalEntity> toAdd;
					
					if(this.forward==true) {
						toAdd = rights;
					}
					else {
						toAdd = lefts;
					}
					
					Boolean success = true;
					
					int nBoot = 0;
					
					if(this.forward==true) {
						for(Iterator<String> iterLeft = lefts.keySet().iterator(); iterLeft.hasNext(); ){
							String cpdLeft = lefts.get(iterLeft.next()).getId();

//							System.out.println("Cpd Left:"+cpdLeft);
							
							if(! currentListOfcurrentSteps.containsKey(cpdLeft) && this.getBootstrapCpds().contains(cpdLeft) == false) {
								success = false;
							}
							
//							if(currentListOfcurrentSteps.containsKey(cpdLeft)) {
//								System.out.println(cpdLeft+" contained in current steps");
//							}
//							
//							if( this.getBootstrapCpds().contains(cpdLeft) ) {
//								System.out.println(cpdLeft+" is a bootstrap");
//							}
//							
//							if(success == true) {
//								System.out.println("Success cpd !");
//							}
//							else {
//								System.out.println("Failed cpd !");
//							}
								
							// We check if all the substrates are not bootstrap compounds
							if(this.getBootstrapCpds().contains(cpdLeft) == true && currentListOfcurrentSteps.containsKey(cpdLeft) == false) {
								nBoot++;
							}
						}
					}
					
					if(nBoot==lefts.size()) {
						success = false;
					}
					
//					if(success == true) {
//						System.out.println("Success !");
//					}
//					else {
//						System.out.println("Failed !");
//					}

					if(success == true) { // All the substrates are present in the current cpds.
						
						for(String cpdToAdd : toAdd.keySet()) {
							
//							System.out.println("On ajoute "+cpdToAdd);

							if(this.getCurrentCpdsSteps().containsKey(cpdToAdd) == false) {
								// The product is not yet in the current compounds
								this.putCpdStep(cpdToAdd, this.getStepNo());
								nbCpdsSupp++;
							}
							else {
								if(this.getCurrentCpdsSteps().get(cpdToAdd) == 0) {
									// The product is an input compound
									this.putCpdStep(cpdToAdd, this.getStepNo());
								}
							}
						}
						
						if(this.useReversibleReactionsOnlyOnce) {
							this.getAvailableReactions().remove(reaction.getId());
						}
						else {
							if(reaction.getReversiblity().equalsIgnoreCase("reversible")) {
								if(this.getCurrentCpdsSteps().keySet().containsAll(reaction.getListOfSubstrates().keySet())) {
									this.getAvailableReactions().remove(reaction.getId());
								}
							}
							else {
								this.getAvailableReactions().remove(reaction.getId());
							}
						}						
					
						
						this.putReactionStep(reaction.getId(), this.getStepNo());
						this.getScopeNetwork().addBiochemicalReaction(reaction);
						
					}
				}
			}
		}
		
		return nbCpdsSupp;
	}
	
	/**
	 * 
	 *
	 */
	public void compute() {
		while(this.run() != 0 && ! (this.getCpdToReach().size() > 0 && this.getCurrentCpdsSteps().keySet().containsAll(this.getCpdToReach()))) { // While new compounds are added
			;
		}
	}
	
	
	/**
	 * @return the inCpds
	 */
	public Set<String> getInCpds() {
		return inCpds;
	}


	/**
	 * @param inCpds the inCpds to set
	 */
	public void setInCpds(Set<String> inCpds) {
		this.inCpds = inCpds;
	}


	/**
	 * @return the originalBioNetwork
	 */
	public BioNetwork getOriginalBioNetwork() {
		return originalBioNetwork;
	}


	/**
	 * @param originalBioNetwork the originalBioNetwork to set
	 */
	public void setOriginalBioNetwork(BioNetwork originalBioNetwork) {
		this.originalBioNetwork = originalBioNetwork;
	}

	/**
	 * @return the availableReactions
	 */
	public HashMap<String, BioChemicalReaction> getAvailableReactions() {
		return availableReactions;
	}

	/**
	 * @param availableReactions the availableReactions to set
	 */
	public void setAvailableReactions(HashMap<String, BioChemicalReaction> availableReactions) {
		this.availableReactions = availableReactions;
	}

	/**
	 * @return the stepNo
	 */
	public int getStepNo() {
		return stepNo;
	}

	/**
	 * @param stepNo the stepNo to set
	 */
	public void setStepNo(int stepNo) {
		this.stepNo = stepNo;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public void putCpdStep(String key, Integer value) {
		currentCpdsSteps.put(key, value);
	}

	/**
	 * @return the currentCpdSteps
	 */
	public HashMap<String, Integer> getCurrentCpdsSteps() {
		return currentCpdsSteps;
	}

	/**
	 * @return the currentReactionsSteps
	 */
	public HashMap<String, Integer> getCurrentReactionsSteps() {
		return currentReactionsSteps;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public Integer putReactionStep(String key, Integer value) {
		return currentReactionsSteps.put(key, value);
	}
	
	/**
	 * Write the results in a Cytoscape-Like attribute file
	 * 
	 */
	/**
	 * Write the results in a Cytoscape-Like attribute file
	 * 
	 */
	public void writeAttributeFile(String fileOut, Boolean encodeSbml, Boolean onlyResults) throws IOException {
		FileWriter writer = new FileWriter(fileOut);
		writer.write("Step\n");
		for(Iterator<String> iterCpd = this.getCurrentCpdsSteps().keySet().iterator(); iterCpd.hasNext();) {
			String cpdId = iterCpd.next();
			Integer stepNo = this.getCurrentCpdsSteps().get(cpdId);
			
			Boolean flag = false;
			
			if(onlyResults) {
				
				HashMap<String, BioChemicalReaction> reactions;
				
				if(this.forward == false)
					reactions = this.getOriginalBioNetwork().getListOfReactionsAsProduct(cpdId);
				else
					reactions = this.getOriginalBioNetwork().getListOfReactionsAsProduct(cpdId);

				
				int nbReactions = reactions.size();
				
				if(nbReactions == 1) {
					
					BioChemicalReaction rxn = reactions.values().iterator().next();
					
					if(rxn.getReversiblity().compareToIgnoreCase("reversible") == 0) {
						flag = true;
					}
					
				}
				else if(nbReactions == 0) {
					flag = true;
				}
			}
			
		
			if(!onlyResults || flag == true) {
				if(encodeSbml)
					cpdId = StringUtils.sbmlEncode(cpdId);
				writer.write(cpdId+" = "+stepNo+"\n");
			}
		}
		
		if(! onlyResults) {
			for(Iterator<String> iterReaction= this.getCurrentReactionsSteps().keySet().iterator(); iterReaction.hasNext();) {
				String reacId = iterReaction.next();
				Integer stepNo = this.getCurrentReactionsSteps().get(reacId);
				if(encodeSbml)
					reacId = StringUtils.sbmlEncode(reacId);
				writer.write(reacId+" = "+stepNo+"\n");
			}
		}
		
		writer.close();
		
	}
	/**
	 * Write the scope of compounds as a list of compounds generated by the program
	 * 
	 */
	public void writeListOfCompounds(String fileOut, Boolean encodeSbml) throws IOException {
		FileWriter writer = new FileWriter(fileOut);
		for(Iterator<String> iterCpd = this.getCurrentCpdsSteps().keySet().iterator(); iterCpd.hasNext();) {
			String cpdId = iterCpd.next();
			if(encodeSbml)
				cpdId = StringUtils.sbmlEncode(cpdId);
			writer.write(cpdId+"\n");
		}
		
		writer.close();
	}
	
	/**
	 * Write the used reactionNodes as a list
	 * 
	 */
	public void writeListOfReactions(String fileOut, Boolean encodeSbml) throws IOException {
		FileWriter writer = new FileWriter(fileOut);
		for(Iterator<String> iterCpd = this.getCurrentReactionsSteps().keySet().iterator(); iterCpd.hasNext();) {
			String reacId = iterCpd.next();
			if(encodeSbml)
				reacId = StringUtils.sbmlEncode(reacId);
			writer.write(reacId+"\n");
		}
		
		writer.close();
	}
	
	/**
	 * Write the results in a file which can be used in GenePro as cluster file
	 * 
	 */
	public void writeClustersFiles(String fileOut, Boolean encodeSbml) throws IOException {
		
		String clustersFile = fileOut+".clusters"; 
		
		FileWriter writer = new FileWriter(clustersFile);
		writer.write("CID\tORF\n");
		for(Iterator<String> iterCpd = this.getCurrentCpdsSteps().keySet().iterator(); iterCpd.hasNext();) {
			String cpdId = iterCpd.next();
			Integer stepNo = this.getCurrentCpdsSteps().get(cpdId);
			if(encodeSbml)
				cpdId = StringUtils.sbmlEncode(cpdId);
			writer.write(stepNo+"\t"+cpdId+"\n");
		}

		for(Iterator<String> iterReaction= this.getCurrentReactionsSteps().keySet().iterator(); iterReaction.hasNext();) {
			String reacId = iterReaction.next();
			Integer stepNo = this.getCurrentReactionsSteps().get(reacId);
			if(encodeSbml)
				reacId = StringUtils.sbmlEncode(reacId);
			writer.write(stepNo+"\t"+reacId+"\n");
		}
		
		writer.close();
		
		String interactionsFile = fileOut+".interactions";
		
		writer = new FileWriter(interactionsFile);
		writer.write("\n");
		
		for(Iterator<String> iterReaction= this.getCurrentReactionsSteps().keySet().iterator(); iterReaction.hasNext();) {
			String reacId = iterReaction.next();
			BioChemicalReaction reaction = this.getOriginalBioNetwork().getBiochemicalReactionList().get(reacId);
			
			Set<String> left;
			Set<String> right;
			
			left = reaction.getLeftParticipantList().keySet();
			right =reaction.getRightParticipantList().keySet();		

			for(Iterator<String> iterLeft = left.iterator(); iterLeft.hasNext(); ) {
				writer.write(reacId+"\t"+iterLeft.next()+"\t1.0\n");
			}
			
			for(Iterator<String> iterRight = right.iterator(); iterRight.hasNext(); ) {
				writer.write(reacId+"\t"+iterRight.next()+"\t1.0\n");
			}
		}
		
		writer.close();
		
	}
	
	public void createScopeNetwork() {
		
		Set<String> compounds = this.getCurrentCpdsSteps().keySet();
		Set<String> reactions = this.getCurrentReactionsSteps().keySet();
		
//		System.out.println("Current reactions : "+reactions);
		
		BioNetwork network = new BioNetwork(this.originalBioNetwork, reactions, compounds);
		
		if(this.useReversibleReactionsOnlyOnce) {
			// We keep only the direction used in each reaction
			HashMap<String, BioChemicalReaction> listReactions = new HashMap<String, BioChemicalReaction>(network.getBiochemicalReactionList());
			
			for(BioChemicalReaction rxn : listReactions.values()) {
				if(rxn.getReversiblity().equalsIgnoreCase("reversible")) {
					rxn.setReversibility(false);
					
					if(rxn.getFlag() == false) {
						// The reaction is used in the backtrack direction
						HashMap<String, BioPhysicalEntityParticipant> lefts = new HashMap<String, BioPhysicalEntityParticipant>(rxn.getLeftParticipantList());
						HashMap<String, BioPhysicalEntityParticipant> rights = new HashMap<String, BioPhysicalEntityParticipant>(rxn.getRightParticipantList());
						rxn.setLeftParticipantList(rights);
						rxn.setRightParticipantList(lefts);
						
						for(BioPhysicalEntity cpd : rxn.getLeftList().values()) {
							cpd.removeReactionAsProduct(rxn.getId());
						}
						
						for(BioPhysicalEntity cpd : rxn.getRightList().values()) {
							cpd.removeReactionAsSubstrate(rxn.getId());
						}
					}
					
					for(BioPhysicalEntity cpd : rxn.getLeftList().values()) {
						cpd.removeReactionAsProduct(rxn.getId());
					}
					
					for(BioPhysicalEntity cpd : rxn.getRightList().values()) {
						cpd.removeReactionAsSubstrate(rxn.getId());
					}
						
				}
			}
		}
		
//		System.out.println("Reactions in the scope network : "+network.getBiochemicalReactionList().keySet());

		
		this.setScopeNetwork(network);
	}
	
	public void createInverseScopeNetwork() {
		BioNetwork network = new BioNetwork(this.getAvailableReactions());
		
		this.setInverseScopeNetwork(network);
		
	}
	
	
	
	public BioNetwork writeScopeAsSbml(String fileOut) throws IOException, SBMLException, XMLStreamException, ParseException {
		BioNetwork network = this.getScopeNetwork();
		
		BioNetworkToJSBML fw = new BioNetworkToJSBML(network, fileOut);
		
		fw.write();
		
		return network;
		
	}
	
	public BioNetwork writeInvScopeAsSbml(String fileOut) throws IOException, SBMLException, XMLStreamException, ParseException {
		
		if(this.getInverseScopeNetwork() == null) {
			this.createInverseScopeNetwork();
		}
		
		BioNetwork network = this.getInverseScopeNetwork();
		
		BioNetworkToJSBML fw = new BioNetworkToJSBML(network, fileOut);
		
		fw.write();
		
		return network;
		
	}

	public BioNetwork getScopeNetwork() {
		if(scopeNetwork == null) {
			this.createScopeNetwork();
		}
		return scopeNetwork;
	}

	public void setScopeNetwork(BioNetwork scopeNetwork) {
		this.scopeNetwork = scopeNetwork;
	}

	public Set<String> getBootstrapCpds() {
		return bootstrapCpds;
	}

	public void setBootstrapCpds(Set<String> bootstrapCpds) {
		this.bootstrapCpds = bootstrapCpds;
	}

	public BioNetwork getInverseScopeNetwork() {
		if(inverseScopeNetwork == null) {
			this.createInverseScopeNetwork();
		}
		return inverseScopeNetwork;
	}

	public void setInverseScopeNetwork(BioNetwork inverseScopeNetwork) {
		this.inverseScopeNetwork = inverseScopeNetwork;
	}
	

/**
 * @return the cpdToReach
 */
public Set<String> getCpdToReach() {
	return cpdToReach;
}


/**
 * @param cpdToReach the cpdToReach to set
 */
public void setCpdToReach(Set<String> cpdToReach) {
	this.cpdToReach = cpdToReach;
}
	
	/**
	 * Eliminate all the reactions involved in scopes not containing
	 * interestingCpds Be careful : does not work with reversible reactions
	 * 
	 * @param interestingCpds
	 */
	public static BioNetwork compressAroundMetabolites(Set<String> interestingCpds,
			Boolean useReversibleReactionOnlyOnce, BioNetwork bn) {
	
		Set<String> bs = new HashSet<String>(bn.getPhysicalEntityList()
				.keySet());
	
		// 1. we keep only the network produced by the scope of the interesting
		// cpds
	
		ScopeCompounds sc1 = new ScopeCompounds(bn, interestingCpds, bs, "",
				new HashSet<String>(), useReversibleReactionOnlyOnce, true);
	
		sc1.compute();
	
		sc1.createScopeNetwork();
	
		BioNetwork networkCompressed = sc1.getScopeNetwork();
	
		//
		// try {
		// System.out.println("Ecriture de scope.xml");
		// sc1.writeScopeAsSbml("/home/ludo/work/scope.xml");
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// Set<String> alreadyScoped = new HashSet<String>();
		//
		// Set<BioPhysicalEntity> cpds = new
		// HashSet<BioPhysicalEntity>(networkCompressed.getPhysicalEntityList().values());
		//
		// for(BioPhysicalEntity cpd : cpds) {
		//
		// if(networkCompressed.getPhysicalEntityList().containsKey(cpd.getId())
		// && ! alreadyScoped.contains(cpd.getId())) {
		//
		// alreadyScoped.add(cpd.getId());
		//
		// Set<String> in = new HashSet<String>();
		// in.add(cpd.getId());
		//
		// Set<BioChemicalReaction> RUs = new
		// HashSet<BioChemicalReaction>(cpd.getReactionsAsSubstrate().values());
		//
		// for(BioChemicalReaction RU : RUs) {
		//
		// // System.out.println("RU : "+RU);
		//
		// HashMap<String, BioChemicalReaction> reactionsToAvoid = new
		// HashMap<String, BioChemicalReaction>(cpd.getReactionsAsSubstrate());
		// reactionsToAvoid.remove(RU.getId());
		//
		// HashMap<String, String> linksRemoved = new HashMap<String, String>();
		//
		//
		// // We remove each link between cpd and reactionsToAvoid
		// // and we store them into a HashMap
		//
		// for(BioChemicalReaction rxn : reactionsToAvoid.values()) {
		//
		// if(rxn.getLeftList().containsKey(cpd.getId())) {
		// rxn.removeLeft(cpd.getId());
		// cpd.removeReactionAsSubstrate(rxn.getId());
		// linksRemoved.put(rxn.getId(), "L");
		// }
		//
		// if(rxn.getReversiblity().equalsIgnoreCase("reversible") &&
		// rxn.getRightList().containsKey(cpd.getId())) {
		// rxn.removeRight(cpd.getId());
		// cpd.removeReactionAsSubstrate(rxn.getId());
		// linksRemoved.put(rxn.getId(), "R");
		// }
		//
		// }
		//
		// HashMap<String, BioPhysicalEntity> products = RU.getListOfProducts();
		//
		//
		// for(BioPhysicalEntity product : products.values()) {
		//
		// // We remove the link between the other products and RU
		// Set<BioPhysicalEntity> productsRemoved = new
		// HashSet<BioPhysicalEntity>();
		//
		// HashMap<String, BioPhysicalEntity> newProducts = new HashMap<String,
		// BioPhysicalEntity>(RU.getRightList());
		//
		// for(BioPhysicalEntity otherProduct : newProducts.values()) {
		// if(! otherProduct.equals(product)) {
		// RU.removeRight(otherProduct.getId());
		// otherProduct.removeReactionAsProduct(RU.getId());
		// productsRemoved.add(otherProduct);
		// }
		// }
		// ScopeCompounds sc = new ScopeCompounds(networkCompressed, in, bs, "",
		// new HashSet<String>(), false, true);
		//
		// Boolean stop = false;
		//
		// while(sc.run() != 0) { // While new compounds are added
		//
		//
		// for(String interestingCpd : interestingCpds) {
		// if(cpd.getId().compareTo(interestingCpd) != 0 &&
		// sc.getCurrentCpdsSteps().containsKey(interestingCpd)) {
		// stop = true;
		// }
		// }
		//
		// if(stop) {
		// break;
		// }
		//
		// }
		//
		// if(stop == false) {
		// // We remove the link between the reaction and the product
		// RU.removeRight(product.getId());
		// product.removeReactionAsProduct(RU.getId());
		// }
		//
		// // We reput the products removed
		// for(BioPhysicalEntity otherProduct : productsRemoved) {
		// RU.addRightParticipant(new
		// BioPhysicalEntityParticipant(otherProduct));
		// otherProduct.addReactionAsProduct(RU);
		// }
		// }
		//
		// for(String rxnId : linksRemoved.keySet()) {
		//
		// if(networkCompressed.getBiochemicalReactionList().containsKey(rxnId))
		// {
		//
		// String side = linksRemoved.get(rxnId);
		// BioChemicalReaction rxn =
		// networkCompressed.getBiochemicalReactionList().get(rxnId);
		//
		// if(side.equals("L")) {
		// rxn.addLeftParticipant(new BioPhysicalEntityParticipant(cpd));
		// }
		//
		// if(side.equals("R")) {
		// rxn.addRightParticipant(new BioPhysicalEntityParticipant(cpd));
		// }
		//
		// cpd.addReactionAsSubstrate(rxn);
		//
		// networkCompressed.addPhysicalEntity(cpd);
		//
		// }
		//
		// }
		//
		// if(RU.getRightList().size()==0 || RU.getLeftList().size()==0) {
		// networkCompressed.removeBioChemicalReaction(RU.getId());
		// }
		//
		// }
		// }
		// }
		//
		// // Final step : we recompute the scope from the interesting compounds
		// sc1 = new ScopeCompounds(networkCompressed, interestingCpds, bs, "",
		// new HashSet<String>(), false, true);
		// sc1.compute();
		//
		// sc1.createScopeNetwork();
		//
		// networkCompressed = sc1.getScopeNetwork();
	
		return networkCompressed;
	}
}