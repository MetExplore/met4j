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

package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioNetworkUtils {

	/**
	 * Return choke reactions
	 * 
	 * @param network
	 * @return
	 */
	public static BioCollection<BioReaction> getChokeReactions(BioNetwork network) {

		BioCollection<BioReaction> chokeReactions = new BioCollection<BioReaction>();

		for (BioReaction r : network.getReactionsView()) {

			BioCollection<BioMetabolite> metabolites = network.getLefts(r);
			metabolites.addAll(network.getRights(r));

			for (BioMetabolite m : metabolites) {

				BioCollection<BioReaction> reactionsAsSubstrate = network.getReactionsFromSubstrate(m);
				BioCollection<BioReaction> reactionsAsProduct = network.getReactionsFromProduct(m);

				reactionsAsSubstrate.remove(r);
				reactionsAsProduct.remove(r);

				if (reactionsAsSubstrate.size() == 0 || reactionsAsProduct.size() == 0) {
					chokeReactions.add(r);
					break;
				}

			}
		}

		return chokeReactions;

	}

	/**
	 *
	 * @param network
	 */
	public static void removeNotConnectedMetabolites(BioNetwork network)
	{
		BioCollection<BioMetabolite> metabolites = network.getMetabolitesView();
		for(BioMetabolite m : metabolites)
		{
			if(network.getReactionsFromMetabolite(m).size() == 0)
			{
				network.removeOnCascade(m);
			}
		}
	}

//	public static BioNetwork getSubNetwork(BioNetwork originalNetwork, BioCollection<BioReaction> reactions,
//			BioCollection<BioMetabolite> additionalMetabolites) {
//
//		BioNetwork network = new BioNetwork();
//
//		BioCollection<BioMetabolite> metabolitesToAdd = originalNetwork.getMetabolitesFromReactions(reactions);
//
//		return network;
//
//	}

}
