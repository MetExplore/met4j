package fr.inra.toulouse.metexplore.met4j_core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;

public class BioNetworkUtils {
	
	/**
	 * Extract a subNetwork from the network with a list of compartments A
	 * reaction will be added to the sub-network if at least one substrate or
	 * product is in the reaction
	 * 
	 * @param compartments
	 *            : the list of compartment ids
	 * @param withTransports
	 *            : true if we want to add the transports
	 * 
	 */
	public static BioNetwork getSubNetwork(BioNetwork originalNetwork, Set<String> compartments,
			boolean withTransports) {
		// HashSet<String> reactions=new HashSet<String>();
		// HashSet<String> compounds=new HashSet<String>();

		HashMap<String, BioChemicalReaction> listOfReactions = new HashMap<String, BioChemicalReaction>();

		// GET ALL THE REACTIONS
		for (BioChemicalReaction reaction : originalNetwork.getBiochemicalReactionList()
				.values()) {
			// System.out.println(reaction.getEnzList().keySet());
			if (reaction.getCompartment() != null) {
				if (compartments.contains(reaction.getCompartment().getId())) {
					/*
					 * reactions.add(reaction.getId());
					 * for(BioPhysicalEntityParticipant cpdParticipant :
					 * reaction.getLeftParticipantList().values()) {
					 * compounds.add
					 * (cpdParticipant.getPhysicalEntity().getId()); }
					 * for(BioPhysicalEntityParticipant cpdParticipant :
					 * reaction.getRightParticipantList().values()) {
					 * compounds.add
					 * (cpdParticipant.getPhysicalEntity().getId()); }
					 */
					listOfReactions.put(reaction.getId(), reaction);
				}
			} else {
				if (withTransports) {
					boolean transportInvolved = false;
					for (BioPhysicalEntityParticipant cpdParticipant : reaction
							.getLeftParticipantList().values()) {
						if (compartments.contains(cpdParticipant
								.getPhysicalEntity().getCompartment().getId()))
							transportInvolved = true;
					}
					for (BioPhysicalEntityParticipant cpdParticipant : reaction
							.getRightParticipantList().values()) {
						if (compartments.contains(cpdParticipant
								.getPhysicalEntity().getCompartment().getId()))
							transportInvolved = true;
					}

					if (transportInvolved) {

						/*
						 * reactions.add(reaction.getId());
						 * for(BioPhysicalEntityParticipant cpdParticipant :
						 * reaction.getLeftParticipantList().values()) {
						 * compounds
						 * .add(cpdParticipant.getPhysicalEntity().getId()); }
						 * for(BioPhysicalEntityParticipant cpdParticipant :
						 * reaction.getRightParticipantList().values()) {
						 * compounds
						 * .add(cpdParticipant.getPhysicalEntity().getId()); }
						 */
						listOfReactions.put(reaction.getId(), reaction);
					}

				}
			}
		}
		// CREATE THE NETWORK WITH THE REACTIONS
		// BioNetwork subNet=new BioNetwork(this,reactions,compounds);

		BioNetwork subNet = new BioNetwork(listOfReactions);
		/*
		 * for(BioChemicalReaction reaction:
		 * subNet.getBiochemicalReactionList().values()) {
		 * System.out.println(reaction.getEnzList().keySet()); }
		 */
		// ADD THE COMPARTMENTS TO THE NETWORK
		/*
		 * for(String compId : this.getCompartments().keySet()) {
		 * if(compartments.contains(compId)) {
		 * subNet.addCompartment(this.getCompartments().get(compId)); } }
		 */

		return subNet;
	}
	

	/**
	 * Build a subNetwork from an original network, a list of reactions and a
	 * list of compounds
	 * 
	 * @param originalNetwork
	 * @param reactions
	 * @param compounds
	 * 
	 *            Takes into account only the reactions and not the informations
	 *            associated
	 * 
	 */
	public static BioNetwork getSubNetwork(BioNetwork originalNetwork, Set<String> reactions,
			Set<String> compounds) {
		
		BioNetwork subNet = new BioNetwork();
		
		for (String cpdId : compounds) {

			String cpdName = originalNetwork.getPhysicalEntityList().get(cpdId)
					.getName();

			BioPhysicalEntity cpd = new BioPhysicalEntity(cpdId, cpdName);

			// BioCompartment cpt = cpd.getCompartment();
			BioCompartment cpt = originalNetwork.getPhysicalEntityList()
					.get(cpdId).getCompartment();

			cpd.setCompartment(cpt);

			subNet.addCompartment(cpt);

			subNet.addPhysicalEntity(cpd);
		}

		for (String reacId : reactions) {
			BioChemicalReaction reaction = originalNetwork
					.getBiochemicalReactionList().get(reacId);

			BioChemicalReaction newReaction = new BioChemicalReaction(
					reaction.getId(), reaction.getName());

			newReaction.setReversibility(reaction.getReversiblity());
			newReaction.setFlag(reaction.getFlag());

			subNet.addBiochemicalReaction(newReaction);

			for (BioPhysicalEntityParticipant cpdParticipant : reaction
					.getLeftParticipantList().values()) {

				String cpdId = cpdParticipant.getPhysicalEntity().getId();
				String cpdName = cpdParticipant.getPhysicalEntity().getName();
				BioPhysicalEntity newCpd;
				if (!subNet.getPhysicalEntityList().containsKey(cpdId)) {
					newCpd = new BioPhysicalEntity(cpdId, cpdName);
					subNet.addPhysicalEntity(newCpd);
				} else {
					newCpd = subNet.getPhysicalEntityList().get(
							cpdParticipant.getPhysicalEntity().getId());
				}

				BioPhysicalEntityParticipant newCpdParticipant = new BioPhysicalEntityParticipant(
						cpdParticipant.getId(), newCpd,
						cpdParticipant.getStoichiometricCoefficient(),
						cpdParticipant.getLocation());

				newReaction.addLeftParticipant(newCpdParticipant);

				if (newReaction.getReversiblity().compareToIgnoreCase(
						"irreversible-left-to-right") == 0) {
					newCpd.addReactionAsSubstrate(newReaction);
				} else if (newReaction.getReversiblity().compareToIgnoreCase(
						"irreversible-right-to-left") == 0) {
					newCpd.addReactionAsProduct(newReaction);
				} else {
					newCpd.addReactionAsSubstrate(newReaction);
					newCpd.addReactionAsProduct(newReaction);
				}
			}

			for (BioPhysicalEntityParticipant cpdParticipant : reaction
					.getRightParticipantList().values()) {

				String cpdId = cpdParticipant.getPhysicalEntity().getId();
				String cpdName = cpdParticipant.getPhysicalEntity().getName();
				BioPhysicalEntity newCpd;
				if (!subNet.getPhysicalEntityList().containsKey(cpdId)) {
					newCpd = new BioPhysicalEntity(cpdId, cpdName);
					subNet.addPhysicalEntity(newCpd);
				} else {
					newCpd = subNet.getPhysicalEntityList().get(
							cpdParticipant.getPhysicalEntity().getId());
				}

				BioPhysicalEntityParticipant newCpdParticipant = new BioPhysicalEntityParticipant(
						cpdParticipant.getId(), newCpd,
						cpdParticipant.getStoichiometricCoefficient(),
						cpdParticipant.getLocation());

				newReaction.addRightParticipant(newCpdParticipant);

				if (newReaction.getReversiblity().compareToIgnoreCase(
						"irreversible-left-to-right") == 0) {
					newCpd.addReactionAsProduct(newReaction);
				} else if (newReaction.getReversiblity().compareToIgnoreCase(
						"irreversible-right-to-left") == 0) {
					newCpd.addReactionAsSubstrate(newReaction);
				} else {
					newCpd.addReactionAsSubstrate(newReaction);
					newCpd.addReactionAsProduct(newReaction);
				}
			}
		}
		
		return subNet;

	}
	
	
	/**
	 * Double the reversible reactions
	 */
	public static BioNetwork doubleReversibleReactions(BioNetwork bn) {

		BioNetwork newNetwork = getSubNetwork(bn, bn
				.getBiochemicalReactionList().keySet(), bn
				.getPhysicalEntityList().keySet());

		Set<BioChemicalReaction> reactions = new HashSet<BioChemicalReaction>(
				bn.getBiochemicalReactionList().values());

		for (BioChemicalReaction rxn : reactions) {

			if (rxn.getReversiblity().compareToIgnoreCase("reversible") == 0) {

				String originalId = rxn.getId();
				String originalName = rxn.getName();

				rxn.setId(originalId + "__F");
				rxn.setName(originalName + "__F");

				rxn.setReversibility("irreversible-left-to-right");

				BioChemicalReaction rxn2 = new BioChemicalReaction();

				rxn2.setId(originalId + "__B");
				rxn2.setName(originalName + "__B");
				rxn2.setLeftParticipantList(rxn.getRightParticipantList());
				rxn2.setRightParticipantList(rxn.getLeftParticipantList());

				rxn2.setReversibility("irreversible-left-to-right");

				newNetwork.addBiochemicalReaction(rxn2);
				newNetwork.getBiochemicalReactionList().remove(originalId);
				newNetwork.addBiochemicalReaction(rxn);

				for (BioPhysicalEntity cpd : rxn.getLeftList().values()) {
					cpd.removeReactionAsSubstrate(originalId);
					cpd.removeReactionAsProduct(originalId);
				}

				for (BioPhysicalEntity cpd : rxn.getRightList().values()) {
					cpd.removeReactionAsSubstrate(originalId);
					cpd.removeReactionAsProduct(originalId);
				}

				for (BioPhysicalEntity cpd : rxn.getLeftList().values()) {
					cpd.addReactionAsProduct(rxn2);
					cpd.addReactionAsSubstrate(rxn);
				}

				for (BioPhysicalEntity cpd : rxn.getRightList().values()) {
					cpd.addReactionAsProduct(rxn);
					cpd.addReactionAsSubstrate(rxn2);
				}

			}

		}

		return newNetwork;

	}
	
	/**
	 * Find all possible compartment-unannotated reactions in the subnetwork of
	 * the compound in question.
	 * 
	 * ATTENTION: COMPARTMENT MATCHING ONLY POSSIBLE IF PALSON OR BIOCYC FORMAT
	 * OF COMPARTMENT TAGGING IN ID IS FOLLOWED.
	 * 
	 * @param HashMap
	 *            <String,BioPhysicalEntity> ... list of compounds for which
	 *            reactions need to be looked up.
	 * @param String
	 *            ... chose PALSSON or BIOCYC
	 * @return HashMap<String,HashMap<String,BioChemicalReaction>> ...
	 *         String:Compund_name &
	 *         HashMap<String,BioChemicalReaction>:Reaction_name
	 *         :BioChemicalReaction created: 18-07-13
	 */
	public static HashMap<String, HashMap<String, BioChemicalReaction>> getListOfPossibleReactionsinSubnetwork(
			BioNetwork bn, HashMap<String, BioPhysicalEntity> list, String IDtype) {

		HashMap<String, HashMap<String, BioChemicalReaction>> out = new HashMap<String, HashMap<String, BioChemicalReaction>>(); // declare
		// output
		// variable

		for (String s : list.keySet()) {
			HashMap<String, BioPhysicalEntity> cpds = new HashMap<String, BioPhysicalEntity>();
			HashMap<String, BioChemicalReaction> rxns = new HashMap<String, BioChemicalReaction>();
			HashMap<String, BioChemicalReaction> okrxns = new HashMap<String, BioChemicalReaction>();

			rxns = list.get(s).getReactionsAsProduct(); // get list of reactions
			// in which the compound
			// of interest is
			// product
			BioCompartment compart = list.get(s).getCompartment();

			for (String n : rxns.keySet()) {

				cpds = rxns.get(n).getListOfProducts(); // get all products from
				// reaction
				Boolean flag = true;

				for (String m : cpds.keySet()) { // check if all products are in
					// the same compartment

					BioCompartment c1 = cpds.get(m).getCompartment();

					if (IDtype.equals("BIOCYC")) { // if BIOCYC ID
						if (c1.getName().equalsIgnoreCase("CCO-CYTOSOL")) { // IN
							// BIOCYC,
							// compounds
							// in
							// cytosol
							// dont
							// have
							// their
							// IDs
							// tagged

							m = m + "_CCO-GLYCO-LUM"; // * ---> *_CCO-GLYCO-LUM

						} else if (!compart.equals(c1)) { // *_CCO-***-*** --->
							// *_CCO-GLYCO-LUM

							m = m.subSequence(0, m.indexOf("CCO"))
									+ "CCO-GLYCO-LUM";

						}
					} else if (IDtype.equals("PALSSON")) { // if PALSSON ID

						if (!compart.equals(c1)) { // M_***_* ----> M_***_x

							m = m.subSequence(0, m.indexOf("_.$")) + "_x";

						}
					} else { // if ID type is not PALSSON or BIOCYC, terminate
						System.err
						.println(IDtype
								+ " FORMAT OF COMPARTMENT TAGGING IN ID IS NOT SUPPORTED");
						System.err
						.println(IDtype
								+ " FORMAT OF COMPARTMENT TAGGING IN ID IS NOT SUPPORTED");
						System.exit(0);
					}

					if (bn.getPhysicalEntityList().containsKey(m)) { // check if
						// the
						// modified
						// exits....if
						// not
						// switch of
						// the
						// trigger
						// flag
						flag = false;
					}
				}

				if (flag) { // Add reaction to 'okrxns' if all products are in
					// the sam compartment
					okrxns.put(n, rxns.get(n));
				}
			}

			rxns.putAll(list.get(s).getReactionsAsSubstrate()); // get list of
			// reactions in
			// which the
			// compound of
			// interest is
			// product

			for (String n : rxns.keySet()) {

				cpds = rxns.get(n).getListOfSubstrates(); // get all reactants
				// from reaction

				Boolean flag = true;

				for (String m : cpds.keySet()) { // check if all products are in
					// the same compartment

					BioCompartment c1 = cpds.get(m).getCompartment();

					if (IDtype.equals("BIOCYC")) {
						if (c1.getName().equalsIgnoreCase("CCO-CYTOSOL")) {

							m = m + "_CCO-GLYCO-LUM";

						} else if (!compart.equals(c1)) {

							m = m.subSequence(0, m.indexOf("CCO"))
									+ "CCO-GLYCO-LUM";

						} else {

						}
					} else if (IDtype.equals("PALSSON")) {

						if (!compart.equals(c1)) {

							m = m.subSequence(0, m.indexOf("_.$")) + "_x";

						} else {

						}
					} else {
						System.err
						.println(IDtype
								+ " FORMAT OF COMPARTMENT TAGGING IN ID IS NOT SUPPORTED");
						System.err
						.println(IDtype
								+ " FORMAT OF COMPARTMENT TAGGING IN ID IS NOT SUPPORTED");
						System.exit(0);
					}
				}

				if (flag) {
					System.err.println(n);
					okrxns.put(n, rxns.get(n)); // Add reaction to 'okrxns' if
					// all substrates are in the sam
					// compartment
				}
			}

			out.put(s, okrxns);

		}

		return out;
	}
	
	/**
	 * Compare the network with another network by comparing the ids of their
	 * reactionNodes and of their compounds
	 * 
	 * @return
	 */
	public static Boolean haveTheSameReactions(BioNetwork network1 ,BioNetwork network2) {

		Set<String> rxnIds = network1.getBiochemicalReactionList().keySet();
		Set<String> otherRxnIds = network2.getBiochemicalReactionList()
				.keySet();

		Set<String> cpdIds = network1.getPhysicalEntityList().keySet();
		Set<String> otherCpdIds = network2.getPhysicalEntityList().keySet();

		if (rxnIds.equals(otherRxnIds) && cpdIds.equals(otherCpdIds)) {
			return true;
		}

		return false;

	}
	
	/**
	 * 
	 */
	public static void compressIdenticalReactions(BioNetwork bn) {

		ArrayList<BioChemicalReaction> reactions = new ArrayList<BioChemicalReaction>(
				bn.getBiochemicalReactionList().values());

		int l = reactions.size();

		for (int i = 0; i < l; i++) {

			BioChemicalReaction rxn1 = reactions.get(i);

			Boolean identical = false;

			String id = rxn1.getId();
			String name = rxn1.getName();

			if (bn.getBiochemicalReactionList().containsKey(rxn1.getId())) {

				for (int j = i + 1; j < l; j++) {
					BioChemicalReaction rxn2 = reactions.get(j);
					if (bn.getBiochemicalReactionList().containsKey(
							rxn2.getId())) {
						if (BioChemicalReactionUtils.areRedundant(rxn2,rxn1)) {
							identical = true;
							bn.removeBioChemicalReaction(rxn2.getId());
							id = id + "__or__" + rxn2.getId();
							name = name + "__or__" + rxn2.getName();
						}
					}

				}

				if (identical) {
					bn.getBiochemicalReactionList().remove(rxn1.getId());
					rxn1.setId(id);
					rxn1.setName(name);
					bn.addBiochemicalReaction(rxn1);
				}
			}
		}
	}
	
	/**
	 * Mark the compounds as side if they occur as side compound in each
	 * reaction of the network
	 */
	public static void markSides(BioNetwork bn) {

		// If a compound is a cofactor in each reaction it occurs, mark it as a
		// cofactor
		for (BioPhysicalEntity cpd : bn.getPhysicalEntityList().values()) {

			ArrayList<BioChemicalReaction> reactions = new ArrayList<BioChemicalReaction>();

			reactions.addAll(cpd.getReactionsAsSubstrate().values());
			reactions.addAll(cpd.getReactionsAsProduct().values());

			Boolean isSide = true;

			int nb = reactions.size();
			int i = nb;

			while (i > 0 && isSide == true) {

				i--;

				BioChemicalReaction rxn = reactions.get(i);

				HashMap<String, BioPhysicalEntity> primaries = new HashMap<String, BioPhysicalEntity>();

				primaries.putAll(rxn.getPrimaryLeftList());
				primaries.putAll(rxn.getPrimaryRightList());

				if (primaries.containsKey(cpd.getId())) {
					isSide = false;
				}

			}

			cpd.setIsSide(isSide);

		}

	}
}
