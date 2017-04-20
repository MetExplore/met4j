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
/*
 * Created on 1 juil. 2005
 * L.C
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils;
import fr.inra.toulouse.metexplore.met4j_core.io.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * 
 */

public class BioNetwork {


	private HashMap<String, BioPathway> pathways = new HashMap<String, BioPathway>();

	private HashMap<String, BioMetabolite> metabolites = new HashMap<String, BioMetabolite>();
	
	private HashMap<String, BioProtein> proteins = new HashMap<String, BioProtein>();

	private HashMap<String, BioGene> genes = new HashMap<String, BioGene>();

	private HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>();

	private HashMap<String, BioCompartment> compartments = new HashMap<String, BioCompartment>();

	private HashMap<String, BioEnzyme> enzymes = new HashMap<String, BioEnzyme>();


	/**
	 * Removes a compound from a network.
	 */
	public void removeCompound(String id) {

		if (this.getPhysicalEntityList().containsKey(id) == true) {

			HashMap<String, BioReaction> RP = this
					.getListOfReactionsAsProduct(id);
			HashMap<String, BioReaction> RC = this
					.getListOfReactionsAsSubstrate(id);

			HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>();
			reactions.putAll(RP);
			reactions.putAll(RC);

			this.getPhysicalEntityList().remove(id);

			for (BioReaction rxn : reactions.values()) {

				Set<String> left = rxn.getLeftList().keySet();
				Set<String> right = rxn.getRightList().keySet();

				if (left.contains(id) == true) {
					rxn.removeLeftCpd(rxn.getLeftList().get(id));
				}

				if (right.contains(id) == true) {
					rxn.removeRightCpd(rxn.getRightList().get(id));
				}

				if (rxn.getLeftList().size() == 0
						|| rxn.getRightList().size() == 0) {
					this.removeBioChemicalReaction(rxn.getId());
				}
			}
		}
	}

	



	/**
	 * Add a biochemical reaction in the list
	 * 
	 * TODO : Maybe add parameters left right
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addBiochemicalReaction(BioReaction o) {
		
		//add biochemical reaction to model.
		this.reactions.put(o.getId(), o);

	}


	/**
	 * @param id
	 *            in the encoded mode
	 * @return physical entity with the same id
	 */
	public BioPhysicalEntity getBioPhysicalEntityById(String id) {
		BioPhysicalEntity entity = null;
		for (BioPhysicalEntity metabolite : getPhysicalEntityList().values()) {
			if (StringUtils.sbmlEncode(metabolite.getId()).equals(id)) {
				entity = metabolite;
				break;
			}
		}
		return entity;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param cpd
	 * @return the list of reactionNodes which involves the compound cpd as
	 *         substrate
	 */
	public HashMap<String, BioReaction> getListOfReactionsAsSubstrate(
			String cpd) {

		HashMap<String, BioReaction> reactionsAsSubstrate = new HashMap<String, BioReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsSubstrate;
		}

		for (BioReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			HashMap<String, BioPhysicalEntity> listOfSubstrates = rxn
					.getListOfSubstrates();

			if (listOfSubstrates.containsKey(cpd)) {
				reactionsAsSubstrate.put(rxn.getId(), rxn);
			}
		}

		return reactionsAsSubstrate;

	}


	/**
	 * @param cpd
	 * @return the list of reactionNodes which involves the compound cpd as
	 *         product
	 */

	public HashMap<String, BioReaction> getListOfReactionsAsProduct(
			String cpd) {

		HashMap<String, BioReaction> reactionsAsProduct = new HashMap<String, BioReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsProduct;
		}

		for (BioReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			HashMap<String, BioPhysicalEntity> listOfProducts = rxn
					.getListOfProducts();

			if (listOfProducts.containsKey(cpd)) {
				reactionsAsProduct.put(rxn.getId(), rxn);
			}
		}

		return reactionsAsProduct;

	}



	/**
	 * @param left
	 * @param right
	 * @return the list of the reactionNodes which have these left and right
	 *         compounds
	 */
	public HashMap<String, BioReaction> reactionsWith(Set<String> left,
			Set<String> right) {

		HashMap<String, BioReaction> listOfReactions = new HashMap<String, BioReaction>();

		for (BioReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			Set<String> l = rxn.getLeftList().keySet();
			Set<String> r = rxn.getRightList().keySet();

			if (rxn.getReversiblity().compareToIgnoreCase(
					"irreversible-left-to-right") == 0) {
				if (l.equals(left) && r.equals(right))
					listOfReactions.put(rxn.getId(), rxn);
			} else if (rxn.getReversiblity().compareToIgnoreCase(
					"irreversible-right-to-left") == 0) {
				if (l.equals(right) && r.equals(left))
					listOfReactions.put(rxn.getId(), rxn);
			} else {
				if ((l.equals(right) && r.equals(left))
						|| (l.equals(left) && r.equals(right)))
					listOfReactions.put(rxn.getId(), rxn);
			}
		}

		return listOfReactions;

	}

	/**
	 * Returns the reactions that contains the metabolites in the set1 and the
	 * metabolites in the set2 in each side. For instance : if set1 = A and set2
	 * = B Returns : R1 : A +C -> B + D R2 : B + E -> A +F
	 * 
	 */
	public HashMap<String, BioReaction> reactionsThatInvolvesAtLeast(
			Set<String> set1, Set<String> set2) {

		HashMap<String, BioReaction> listOfReactions = new HashMap<String, BioReaction>();

		for (BioReaction reaction : this.getBiochemicalReactionList()
				.values()) {

			Set<String> lefts = reaction.getLeftList().keySet();
			Set<String> rights = reaction.getRightList().keySet();

			if ((lefts.containsAll(set1) && rights.containsAll(set2))
					|| (lefts.containsAll(set2) && rights.containsAll(set1))) {
				listOfReactions.put(reaction.getId(), reaction);
			}
		}

		return listOfReactions;

	}





	/**
	 * Returns the list of pathways where a compound is involved
	 * 
	 * @param cpdId
	 * @return a HashMap<String, BioPathway>
	 */
	public HashMap<String, BioPathway> getPathwaysOfCompound(String cpdId) {

		HashMap<String, BioPathway> pathways = new HashMap<String, BioPathway>();

		if (this.getPhysicalEntityList().containsKey(cpdId)) {

			HashMap<String, BioReaction> rs = this
					.getListOfReactionsAsSubstrate(cpdId);
			HashMap<String, BioReaction> rp = this
					.getListOfReactionsAsProduct(cpdId);

			HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>(
					rs);
			reactions.putAll(rp);

			for (BioReaction r : reactions.values()) {
				pathways.putAll(r.getPathwayList());
			}
		}

		return pathways;
	}

	/**
	 * Returns the list of exchange reactions for a metabolite
	 * 
	 * @param cpdId
	 * @return
	 */
	public HashMap<String, BioReaction> getExchangeReactionsOfMetabolite(
			String cpdId) {

		HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>();
		HashMap<String, BioReaction> ex_reactions = new HashMap<String, BioReaction>();

		if (!this.getPhysicalEntityList().containsKey(cpdId)) {
			System.err.println("[Warning] getExchangeReactionsOfMetabolite : "
					+ cpdId + " is not in the network !");
			return ex_reactions;
		}

		reactions.putAll(this.getListOfReactionsAsProduct(cpdId));
		reactions.putAll(this.getListOfReactionsAsSubstrate(cpdId));

		for (BioReaction reaction : reactions.values()) {

			if (reaction.isExchangeReaction()) {
				ex_reactions.put(reaction.getId(), reaction);
			}

		}

		return ex_reactions;

	}



}
