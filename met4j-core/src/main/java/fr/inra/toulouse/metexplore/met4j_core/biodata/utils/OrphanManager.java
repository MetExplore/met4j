package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;

public class OrphanManager {
	
	private BioNetwork bn;
	
	public OrphanManager(BioNetwork bn){
		this.bn = bn;
	}
	
	/**
	 * 
	 * Iteratively removes the dead reactions
	 * 
	 * @return
	 */
	public Collection<BioChemicalReaction> trim() {
		HashSet<BioChemicalReaction> allRemovedReactions = new HashSet<BioChemicalReaction>();
		Collection<BioChemicalReaction> removed = removeOrphanReactions();
		while (!removed.isEmpty()) {
			allRemovedReactions.addAll(removed);
			removed = removeOrphanReactions();
		}
		return allRemovedReactions;
	}

	/**
	 * 
	 * @return
	 */
	private Collection<BioChemicalReaction> removeOrphanReactions() {
		HashSet<BioChemicalReaction> removedReactions = new HashSet<BioChemicalReaction>();

		HashMap<String, BioPhysicalEntity> orphans = getOrphanMetabolites(bn);

		for (BioPhysicalEntity metabolite : orphans.values()) {

			HashMap<String, BioChemicalReaction> reactionsP = new HashMap<String, BioChemicalReaction>(
					metabolite.getReactionsAsProduct());
			for (BioChemicalReaction reaction : reactionsP.values()) {
				if (bn.getBiochemicalReactionList().containsKey(
						reaction.getId())) {
					removedReactions.add(reaction);
					bn.removeBioChemicalReaction(reaction.getId());
				}
			}

			HashMap<String, BioChemicalReaction> reactionsS = new HashMap<String, BioChemicalReaction>(
					metabolite.getReactionsAsSubstrate());

			for (BioChemicalReaction reaction : reactionsS.values()) {
				if (bn.getBiochemicalReactionList().containsKey(
						reaction.getId())) {
					removedReactions.add(reaction);
					bn.removeBioChemicalReaction(reaction.getId());
				}
			}
		}

		return removedReactions;
	}

	/**
	 * Methods inspired from Surrey FBA, the aim is to get the orphan
	 * metabolites. An orphan is an internal metabolite
	 * (boundaryCondition==false) and not produced or not consumed
	 * 
	 * @return
	 */
	public static HashMap<String, BioPhysicalEntity> getOrphanMetabolites(BioNetwork bn) {
		HashMap<String, BioPhysicalEntity> orphanMetabolites = new HashMap<String, BioPhysicalEntity>();

		for (BioPhysicalEntity cpd : bn.getPhysicalEntityList().values()) {
			if (!cpd.getBoundaryCondition()) {
				HashMap<String, BioChemicalReaction> reactions = new HashMap<String, BioChemicalReaction>();

				// HashMap<String, BioChemicalReaction> reactionsP =
				// this.getListOfReactionsAsProduct(cpd.getId());
				HashMap<String, BioChemicalReaction> reactionsP = cpd
						.getReactionsAsProduct();
				// HashMap<String, BioChemicalReaction> reactionsS =
				// this.getListOfReactionsAsSubstrate(cpd.getId());
				HashMap<String, BioChemicalReaction> reactionsS = cpd
						.getReactionsAsSubstrate();

				reactions.putAll(reactionsP);
				reactions.putAll(reactionsS);

				Set<String> rp = reactionsP.keySet();
				rp.retainAll(bn.getBiochemicalReactionList().keySet());
				Set<String> rs = reactionsS.keySet();
				rs.retainAll(bn.getBiochemicalReactionList().keySet());

				Set<String> rxns = reactions.keySet();

				rxns.retainAll(bn.getBiochemicalReactionList().keySet());

				if (rxns.size() < 2) {
					orphanMetabolites.put(cpd.getId(), cpd);
				} else {
					if (rp.size() == 0 || rs.size() == 0) {
						orphanMetabolites.put(cpd.getId(), cpd);
					}
				}
			}
		}

		return orphanMetabolites;
	}

	/**
	 * Create exchange reactions for each orphan metabolite
	 * 
	 * @param withExternal
	 *            . Boolean. if true, create an external metabolite
	 * @param suffix
	 *            to add at the end of the external metabolite
	 * @compartmentId : the id of the compartment in which the external
	 *                metabolites will be added
	 */
	public void addExchangeReactionsToOrphans(Boolean withExternal,
			String externalCompoundSuffix, String exchangeReactionSuffix, String ExternalCompartmentId) {
	

		HashMap<String, BioPhysicalEntity> orphans = getOrphanMetabolites(bn);

		for (BioPhysicalEntity orphan : orphans.values()) {

			bn.addExchangeReactionToMetabolite(orphan.getId(), withExternal, 
					orphan.getId()+externalCompoundSuffix,
					orphan.getId()+exchangeReactionSuffix,
					ExternalCompartmentId);
		}
	}
}
