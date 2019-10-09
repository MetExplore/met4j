package fr.inra.toulouse.metexplore.met4j_flux.utils;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;

public class BioNetworkUtils {

	/**
	 * Iteratively emove all the dead reactions
	 * 
	 * Returns all the removed reactions
	 * 
	 * @param network
	 */
	public static BioCollection<BioReaction> trim(BioNetwork network) {

		BioCollection<BioReaction> allRemoved = new BioCollection<BioReaction>();

		BioCollection<BioReaction> deadReactions = getDeadReactions(network);

		while (!deadReactions.isEmpty()) {

			deadReactions.forEach(r -> {
				network.removeOnCascade(r);
				allRemoved.add(r);
			});

			deadReactions = getDeadReactions(network);

		}

		return allRemoved;
	}

	/**
	 * Get dead reactions, i.e reactions involving orphan metabolites
	 * 
	 * @param network
	 * @return
	 */
	private static BioCollection<BioReaction> getDeadReactions(BioNetwork network) {

		BioCollection<BioReaction> deadReactions = new BioCollection<BioReaction>();

		BioCollection<BioMetabolite> orphans = getOrphanMetabolites(network);

		orphans.forEach(m -> {

			deadReactions.addAll(network.getReactionsFromSubstrate(m));
			deadReactions.addAll(network.getReactionsFromProduct(m));

		});

		return deadReactions;

	}

	/**
	 * Get orphan metabolites, i.e. internal metabolites (boundaryCondition==false)
	 * not produced or not consumed
	 * 
	 * @param network
	 * @return
	 */
	public static BioCollection<BioMetabolite> getOrphanMetabolites(BioNetwork network) {

		BioCollection<BioMetabolite> orphanMetabolites = new BioCollection<BioMetabolite>();

		for (BioMetabolite met : network.getMetabolitesView()) {
			if (!MetaboliteAttributes.getBoundaryCondition(met)) {

				BioCollection<BioReaction> reactionsAsSubstrate = network.getReactionsFromSubstrate(met);
				BioCollection<BioReaction> reactionsAsProduct = network.getReactionsFromProduct(met);

				BioCollection<BioReaction> allReactions = new BioCollection<BioReaction>(reactionsAsSubstrate);
				allReactions.addAll(reactionsAsProduct);
				if (allReactions.size() < 2) {
					orphanMetabolites.add(met);
				} else {

					if (reactionsAsSubstrate.size() == 0 || reactionsAsProduct.size() == 0) {
						orphanMetabolites.add(met);
					}
				}
			}
		}

		return orphanMetabolites;

	}

}
