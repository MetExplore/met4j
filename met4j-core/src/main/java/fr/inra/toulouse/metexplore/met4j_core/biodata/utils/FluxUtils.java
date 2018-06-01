package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;

public class FluxUtils {
	
	/**
	 * Compute the atom balances for all the reactions
	 * 
	 * @return
	 */
	public static HashMap<String, HashMap<String, Double>> computeBalanceAllReactions(BioNetwork bn) {

		HashMap<String, HashMap<String, Double>> balances = new HashMap<String, HashMap<String, Double>>();

		for (BioReaction rxn : bn.getBiochemicalReactionList()
				.values()) {

			String id = rxn.getId();

			HashMap<String, Double> balance = BioChemicalReactionUtils.computeAtomBalances(rxn);

			balances.put(id, balance);
		}

		return balances;

	}
	
	/**
	 * Tests an objective function
	 * 
	 * @param obj
	 * @return
	 */
	public static Boolean testObjectiveFunction(BioNetwork bn, String obj) {

		Boolean flag = true;

		// The objective function has the following format : 1 R1 + 2.5 R3

		String tab[] = obj.split(" \\+ ");

		for (String member : tab) {

			String tab2[] = member.split(" ");

			String reactionId;

			if (tab2.length == 1) {
				reactionId = tab2[0];
			} else if (tab2.length == 2) {
				reactionId = tab2[1];
				String coeff = tab2[0];

				try {
					Double.parseDouble(coeff);
				} catch (NumberFormatException e) {
					System.err.println(coeff + " is not a double");
					return false;
				}

			} else {
				System.err.println("Objective function badly formatted");
				return false;
			}

			if (!bn.getBiochemicalReactionList().containsKey(reactionId)) {
				System.err.println("The reaction " + reactionId
						+ " is not in the network");
				return false;
			}

		}

		return flag;

	}

}
