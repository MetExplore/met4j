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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.text.parser.ParseException;

import fr.inra.toulouse.metexplore.met4j_graph.core.ScopeCompounds;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;
import fr.inra.toulouse.metexplore.met4j_core.io.BioNetworkToJSBML;
import fr.inra.toulouse.metexplore.met4j_core.io.InputPrecursorReader;
import fr.inra.toulouse.metexplore.met4j_core.io.JSBMLToBionetwork;


// TODO: Auto-generated Javadoc
/**
 * The Class CompressNetworkByPrecursorsAndTargets.
 */
public class CompressNetworkByPrecursorsAndTargets {

	
	/** The network. */
	BioNetwork network;
	
	/** The inputs. */
	Set<String> inputs;
	
	/** The targets. */
	Set<String> targets;
	
	/** The bootstraps. */
	Set<String> bootstraps;
	
	/** The user defined precursors. */
	Set<String> userDefinedPrecursors;
	
	/** The compressed network. */
	BioNetwork compressedNetwork;
	
	/**
	 * Instantiates a new compress network by precursors and targets.
	 *
	 * @param network the network
	 * @param inputs the inputs
	 * @param targets the targets
	 * @param bootstraps the bootstraps
	 * @param userDefinedPrecursors the user defined precursors
	 */
	public CompressNetworkByPrecursorsAndTargets (BioNetwork network, Set<String> inputs, Set<String> targets, Set<String> bootstraps, Set<String> userDefinedPrecursors) {
		this.network = network;
		this.inputs = inputs;
		this.targets = targets;
		this.bootstraps = bootstraps;
		this.userDefinedPrecursors = userDefinedPrecursors;
		this.compressedNetwork = new BioNetwork();

	}
	
	/**
	 * Computes the.
	 */
	public void compute() {
		
		System.err.println("User defined precursors : "+this.userDefinedPrecursors);
		
		// First, we compress from the user defined precursors
		int n = compress(this.userDefinedPrecursors);
		
		// Secondly, we compress from the metabolites that are not user defined precursors
		// nor targets in the compressed network, i.e those that are flagged in the compress
		// function
				
		
		while(n != 0) {
			System.err.println("\n####\nSecond compression\n####\n");
			Set<String> flaggedCpds = new HashSet<String>();
		
			for(BioPhysicalEntity cpd : compressedNetwork.getPhysicalEntityList().values()) {
				if(cpd.getFlag()) {
					flaggedCpds.add(cpd.getId());
				}
			}
			
			System.err.println("Flagged cpds :"+flaggedCpds);
			
			n = compress(flaggedCpds);
		}
		
		return;
		
	}
	
	
	/**
	 * Compress.
	 *
	 * @param cpdIds the cpd ids
	 * @return the int
	 */
	public int compress(Set<String> cpdIds) {
		
		int n=0;
		
		for(String cpdId : cpdIds) {
			
			if(this.compressedNetwork.getPhysicalEntityList().containsKey(cpdId)) {
				this.compressedNetwork.getPhysicalEntityList().get(cpdId).setFlag(false);
			}
			
			System.err.println("\n-----------\ncpdId : "+cpdId+"\n-----------\n");
						
			HashMap<String, BioReaction> reactionsUsingCpd = this.network.getListOfReactionsAsSubstrate(cpdId);
			
			for(BioReaction rxn : reactionsUsingCpd.values()) {
				
				System.err.println("\n-----------\nReaction "+rxn+"\n----------\n");
				
				HashMap<String, BioPhysicalEntity> tmpSubs;
				HashMap<String, BioPhysicalEntity> tmpProds;
				
				if(rxn.getReversiblity().compareToIgnoreCase("irreversible")==0) {
					tmpSubs = rxn.getLeftList();
					tmpProds = rxn.getRightList();
				}
				else {
					HashMap<String, BioPhysicalEntity> lefts = rxn.getLeftList();
					HashMap<String, BioPhysicalEntity> rights = rxn.getRightList();
					if(lefts.containsKey(cpdId)) {
						tmpSubs = lefts;
						tmpProds = rights;
					}
					else {
						tmpSubs = rights;
						tmpProds = lefts;
					}
				}
				
				System.err.println("tmpSubs : "+tmpSubs);
				
				HashMap<String, BioPhysicalEntity> subs = new HashMap<String, BioPhysicalEntity>(tmpSubs);
				HashMap<String, BioPhysicalEntity> prods = new HashMap<String, BioPhysicalEntity>(tmpProds);

				for(String subId : tmpSubs.keySet()) {
					if(this.bootstraps.contains(subId)) {
						subs.remove(subId);
					}
				}
				
				for(String prodId : tmpProds.keySet()) {
					if(this.bootstraps.contains(prodId)) {
						prods.remove(prodId);
					}
				}
				
				BioReaction newReaction = new BioReaction();
				newReaction.setReversibility(false);
				for(BioPhysicalEntity sub : subs.values()) {
					BioPhysicalEntity newSub = new BioPhysicalEntity(sub);
					if(sub.getFlag()) {
						newSub.setFlag(true);
					}
					newReaction.addLeftParticipant(new BioParticipant(newSub));
				}
				
				if(rxn.getReversiblity().compareToIgnoreCase("reversible")==0) {
					for(BioPhysicalEntity prod : prods.values()) {
						BioPhysicalEntity newProd = new BioPhysicalEntity(prod);
						if(prod.getFlag()) {
							newProd.setFlag(true);
						}
						newReaction.addRightParticipant(new BioParticipant(newProd));
					}

					newReaction.setReversibility(true);

				}
				else {

					ScopeCompounds scope = new ScopeCompounds(this.network, subs.keySet(), this.bootstraps, "", new HashSet<String>(), true, true);

					// We make the other reactions using the substrates not available
					for(BioPhysicalEntity sub : subs.values()) {
						HashMap<String, BioReaction> rxns = this.network.getListOfReactionsAsSubstrate(sub.getId());
						for(BioReaction reactionTest : rxns.values()) {
							if(! reactionTest.equals(rxn)) {
								scope.getAvailableReactions().remove(reactionTest.getId());
							}
						}
					}

					scope.compute();
					scope.createScopeNetwork();

					BioNetwork scopeNetwork = scope.getScopeNetwork();

//					System.err.println("Scope of "+subs);
//					System.err.println(scopeNetwork.networkAsString());

					for(BioPhysicalEntity scopeCpd : scopeNetwork.getPhysicalEntityList().values()) {

						System.err.println("Scope cpd : "+scopeCpd);

						Boolean flag = false;

						if(! subs.containsKey(scopeCpd.getId())) {
							if(this.targets.contains(scopeCpd.getId())) {
								flag = true;
							}
							else {
								if(! this.bootstraps.contains(scopeCpd.getId())) {
									Set<String> reactionsUsingScopeCpd = this.network.getListOfReactionsAsSubstrate(scopeCpd.getId()).keySet();
									System.err.println("Reactions using scope cpd : "+reactionsUsingScopeCpd);

									for(String reactionUsingScopeCpd : reactionsUsingScopeCpd) {
										if(! scopeNetwork.getBiochemicalReactionList().containsKey(reactionUsingScopeCpd)) {
											flag = true;
											if(!cpdIds.contains(scopeCpd.getId())) {
												scopeCpd.setFlag(true);
											}
											break;
										}
									}
								}
							}
						}

						if(flag) {

							BioPhysicalEntity newProd = new BioPhysicalEntity(scopeCpd);
							if(scopeCpd.getFlag()) {
								newProd.setFlag(true);
							}
							newReaction.addRightParticipant(new BioParticipant(newProd));

							System.err.println("new Reaction : "+newReaction);

						}
					}
				}
				
				if(newReaction.getRightParticipantList().size()>0 && this.compressedNetwork.reactionsWith(newReaction.getLeftList().keySet(), newReaction.getRightList().keySet()).size() == 0) {
					
					HashMap<String, BioReaction> reactionsWithSameSubstrates = this.compressedNetwork.reactionsWithTheseSubstrates(newReaction.getLeftList().keySet());
					
					if(reactionsWithSameSubstrates.size() > 0 ) {
						for(BioReaction reaction : reactionsWithSameSubstrates.values()) {
							for(BioParticipant bpe : newReaction.getRightParticipantList().values()) {
								reaction.addRightParticipant(bpe);
								this.compressedNetwork.addPhysicalEntity(bpe.getPhysicalEntity());
							}
							System.err.println("Complete "+reaction);
						}
					} 
					else {

						String id = this.compressedNetwork.getNewReactionId("R");
						newReaction.setId(id);
						newReaction.setName(id);
						this.compressedNetwork.addBiochemicalReaction(newReaction);
						System.err.println("Add "+newReaction);
					}
					n++;
				}
			}
		}
		
		return n;
		
	}
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		String sbmlFile = args[0];
		String inputFile = args[1];
		String outFile = args[2];
		
		JSBMLToBionetwork reader = new JSBMLToBionetwork(sbmlFile);
		BioNetwork bn = reader.getBioNetwork();
		
		InputPrecursorReader inputReader = new InputPrecursorReader(inputFile);
		
		Set<String> inputs = inputReader.getInputCompounds();
		Set<String> bootstraps = inputReader.getBootstrapCompounds();
		Set<String> targets = inputReader.getTargetCompounds();
		Set<String> userDefinedPrecursors = inputReader.getPrecursorCompounds();
		
		CompressNetworkByPrecursorsAndTargets compressor = new CompressNetworkByPrecursorsAndTargets(bn, inputs, targets, bootstraps, userDefinedPrecursors);
		compressor.compute();
		
		BioNetworkToJSBML fw = new BioNetworkToJSBML(compressor.compressedNetwork, outFile);
		
		try {
			fw.write();
		} catch (SBMLException | XMLStreamException | ParseException e) {
			e.printStackTrace();
		}
		
	}

}
