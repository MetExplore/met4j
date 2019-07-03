package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioNetworkUtils {

	/**
	 * Return choke reactions
	 * @param network
	 * @return
	 */
	public static BioCollection<BioReaction> getChokeReactions(BioNetwork network) {

		BioCollection<BioReaction> chokeReactions = new BioCollection<BioReaction>();

		for (BioReaction r : network.getReactionsView()) {

			BioCollection<BioMetabolite> metabolites = network.getLefts(r);
			metabolites.addAll(network.getRights(r));

			for(BioMetabolite m : metabolites) {
			
				BioCollection<BioReaction> reactionsAsSubstrate = network.getReactionsFromSubstrate(m);
				BioCollection<BioReaction> reactionsAsProduct = network.getReactionsFromProduct(m);
				
				reactionsAsSubstrate.remove(r);
				reactionsAsProduct.remove(r);
				
				if(reactionsAsSubstrate.size() == 0 || reactionsAsProduct.size() == 0)
				{
					chokeReactions.add(r);
					break;
				}
				
			}
		}

		return chokeReactions;

	}

}
