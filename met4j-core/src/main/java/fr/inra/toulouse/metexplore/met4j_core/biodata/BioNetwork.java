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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.sbml.jsbml.Compartment;

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioChemicalReactionUtils;
import fr.inra.toulouse.metexplore.met4j_core.io.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * 
 */

public class BioNetwork {

	private BioCollection<BioPathway> pathways = new BioCollection<BioPathway>();

	private BioCollection<BioMetabolite> metabolites = new BioCollection<BioMetabolite>();

	private BioCollection<BioProtein> proteins = new BioCollection<BioProtein>();

	private BioCollection<BioGene> genes = new BioCollection<BioGene>();

	private BioCollection<BioReaction> reactions = new BioCollection<BioReaction>();

	private BioCollection<BioCompartment> compartments = new BioCollection<BioCompartment>();

	private BioCollection<BioEnzyme> enzymes = new BioCollection<BioEnzyme>();
	
	
	
	public void add(BioEntity e){
		if(e instanceof BioPathway){
			this.pathways.add((BioPathway)e);
		}
		else if(e instanceof BioMetabolite){
			this.metabolites.add((BioMetabolite)e);
		}
		else if(e instanceof BioProtein){
			this.proteins.add((BioProtein)e);
		}
		else if(e instanceof BioGene){
			this.genes.add((BioGene)e);
		}
		else if(e instanceof BioReaction){
			this.reactions.add((BioReaction)e);
		}
		else if(e instanceof BioCompartment){
			this.compartments.add((BioCompartment)e);
		}
		else if(e instanceof BioEnzyme){
			this.enzymes.add((BioEnzyme)e);
		}else {
			throw new IllegalArgumentException("BioEntity \""+e.getClass().getSimpleName()+"\" not supported by BioNetwork");
		}
	}
	
	
	public void remove(BioEntity e){
		if(e instanceof BioPathway){
			this.pathways.remove((BioPathway)e);
		}
		else if(e instanceof BioMetabolite){
			this.metabolites.remove((BioMetabolite)e);
			//remove des reactants
			//remove des compartiments
		}
		else if(e instanceof BioProtein){
			this.proteins.remove((BioProtein)e);
			//remove des genes
			//remove des reactants
			//remove des compartmnt
		}
		else if(e instanceof BioGene){
			this.genes.remove((BioGene)e);
		}
		else if(e instanceof BioReaction){
			this.reactions.remove((BioReaction)e);
			//remove des pathway
			//remove des enzyme?
		}
		else if(e instanceof BioCompartment){
			this.compartments.remove((BioCompartment)e);
			//remove reactants des reactions
		}
		else if(e instanceof BioEnzyme){
			this.enzymes.remove((BioEnzyme)e);
			//remove from reaction?
		}else {
			
			throw new IllegalArgumentException("BioEntity \""+e.getClass().getSimpleName()+"\" not supported by BioNetwork");
		}
	}
	
	//relation reactant-reaction
	public void affectSubstrate(BioPhysicalEntity substrate, Double stoichiometry, BioCompartment localisation, BioReaction reaction){}
	public void affectProduct(BioPhysicalEntity product, Double stoichiometry, BioCompartment localisation, BioReaction reaction){}
	
	//relation enzyme -reaction
	public void affectEnzyme(BioEnzyme enzyme, BioReaction reaction){};
	public void affectEnzyme(BioProtein protein, BioReaction reaction){};
	
	//relation enzyme -constituant
	public void affectSubUnit(BioPhysicalEntity unit, Double quantity, BioEnzyme enzyme){};
	
	//relations proteine-gène
	public void affectGeneProduct(BioProtein protein, BioGene gene){};
	
	//relation pathway -reaction
	public void affectToPathway(BioReaction reaction, BioPathway pathway){};
	//relations compartiment - contenu
	public void affectToCompartment(BioPhysicalEntity entity, BioCompartment compartment){};

	
	//__________________________________
//	obtenir les reactions en fonction d'une liste de left et de right metabolites (match exact ou partiel)
	public BioCollection<BioReaction> getReactionsFromSubstrates(Collection<BioPhysicalEntity> substrate){};
	public BioCollection<BioReaction> getReactionsFromProducts(Collection<BioPhysicalEntity> substrate){};
	public BioCollection<BioReaction> getReactionsFromSubstrate(BioPhysicalEntity substrate){};
	public BioCollection<BioReaction> getReactionsFromProduct(BioPhysicalEntity substrate){};
	public BioCollection<BioReaction> getReactionsFromEnzyme(BioPhysicalEntity substrate){};
	public BioCollection<BioReaction> getReactionsFromPathway(BioPhysicalEntity substrate){};
//	obtenir les reactions en fonction d'un substrat ou d'un produit
//	obtenir les reactions en fonction d'un pathway
	
//	obtenir les metabolites en fonction d'un pathway
//	obtenir les pathways depuis un metabolite
//	obtenir les pathways depuis une reaction

	

//	/**
//	 * Add a pathway in the network Each reaction involved in a pathway must be
//	 * present in the reaction list
//	 * 
//	 * @param {@link BioPathway} pathway
//	 */
//	public void addPathway(BioPathway pathway) {
//
//		// We check that each reaction is already in the network
//		for (BioReaction reaction : pathway.getReactions()) {
//			if (!this.reactions.contains(reaction)) {
//				throw new UnsupportedOperationException(
//						"Reaction "
//								+ reaction.getId()
//								+ " not present in the BioNetwork. You have to add the reaction before adding the pathway");
//			}
//		}
//		
//		this.pathways.add(pathway);
//
//	}
//
//	/**
//	 * Add a metabolite in the network
//	 * 
//	 * @param {@link BioMetabolite} metabolite
//	 */
//	public void addMetabolite(BioMetabolite metabolite) {
//		this.metabolites.add(metabolite);
//	}
//
//	/**
//	 * Add a protein in the network
//	 * 
//	 * @param {@link BioProtein} protein
//	 */
//	public void addProtein(BioProtein protein) {
//		this.proteins.add(protein);
//	}
//
//	/**
//	 * Add a gene in the network Each protein must be present in the protein
//	 * list
//	 * 
//	 * @param {@link BioGene} gene
//	 */
//	public void addGene(BioGene gene) {
//
//		// We check that each protein is already in the network
//		for (BioProtein protein : gene.getProteinList()) {
//			if (!this.proteins.contains(protein)) {
//				throw new UnsupportedOperationException(
//						"Protein "
//								+ protein.getId()
//								+ " not present in the BioNetwork. You have to add the protein before adding the gene");
//			}
//		}
//		
//		this.genes.add(gene);
//
//	}
//
//	/**
//	 * Add a reaction in the network Each reactant must be present in the
//	 * protein list
//	 * 
//	 * @param {@link BioReaction} reaction
//	 */
//	public void addReaction(BioReaction reaction) {
//
//		// We check that each reactant is already in the network
//		for (BioPhysicalEntity entity : reaction.getEntities()) {
//			if (entity.getClass() == BioProtein.class) {
//				if (!this.proteins.contains(entity)) {
//					throw new UnsupportedOperationException(
//							"Protein "
//									+ entity.getId()
//									+ " not present in the BioNetwork. You have to add the protein before adding the reaction");
//				}
//			} else {
//				if (!this.metabolites.contains(entity)) {
//					throw new UnsupportedOperationException(
//							"Metabolite "
//									+ entity.getId()
//									+ " not present in the BioNetwork. You have to add the metabolite before adding the reaction");
//				}
//			}
//
//		}
//		
//		this.reactions.add(reaction);
//		
//	}
//
//	public void addCompartment(BioCompartment compartment) {
//		
//		// We check that each participant is already in the network
//				for (BioPhysicalEntity entity : compartment.getEntities()) {
//					if (entity.getClass() == BioProtein.class) {
//						if (!this.proteins.contains(entity)) {
//							throw new UnsupportedOperationException(
//									"Protein "
//											+ entity.getId()
//											+ " not present in the BioNetwork. You have to add the protein before adding the reaction");
//						}
//					} else {
//						if (!this.metabolites.contains(entity)) {
//							throw new UnsupportedOperationException(
//									"Metabolite "
//											+ entity.getId()
//											+ " not present in the BioNetwork. You have to add the metabolite before adding the reaction");
//						}
//					}
//
//				}
//		
//		
//		this.compartments.add(compartment);
//	}
//
//	public void addEnzyme(BioEnzyme enzyme) {
//		// verifier que ses participants sont bien dans metabolites and proteins
//	}
//
//	/**
//	 * Removes a compound from a network.
//	 */
//	public void removeMetabolite(String id) {
//
//		if (this.getPhysicalEntityList().containsKey(id) == true) {
//
//			HashMap<String, BioReaction> RP = this
//					.getListOfReactionsAsProduct(id);
//			HashMap<String, BioReaction> RC = this
//					.getListOfReactionsAsSubstrate(id);
//
//			HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>();
//			reactions.putAll(RP);
//			reactions.putAll(RC);
//
//			this.getPhysicalEntityList().remove(id);
//
//			for (BioReaction rxn : reactions.values()) {
//
//				Set<String> left = rxn.getLeftList().keySet();
//				Set<String> right = rxn.getRightList().keySet();
//
//				if (left.contains(id) == true) {
//					rxn.removeLeftCpd(rxn.getLeftList().get(id));
//				}
//
//				if (right.contains(id) == true) {
//					rxn.removeRightCpd(rxn.getRightList().get(id));
//				}
//
//				if (rxn.getLeftList().size() == 0
//						|| rxn.getRightList().size() == 0) {
//					this.removeBioChemicalReaction(rxn.getId());
//				}
//			}
//		}
//	}


	/**
	 * @param cpd
	 * @return the list of reactionNodes which involves the compound cpd as
	 *         substrate
	 */
	public HashMap<String, BioReaction> getListOfReactionsAsSubstrate(String cpd) {

		HashMap<String, BioReaction> reactionsAsSubstrate = new HashMap<String, BioReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsSubstrate;
		}

		for (BioReaction rxn : this.getBiochemicalReactionList().values()) {

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

	public HashMap<String, BioReaction> getListOfReactionsAsProduct(String cpd) {

		HashMap<String, BioReaction> reactionsAsProduct = new HashMap<String, BioReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsProduct;
		}

		for (BioReaction rxn : this.getBiochemicalReactionList().values()) {

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

		for (BioReaction rxn : this.getBiochemicalReactionList().values()) {

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

		for (BioReaction reaction : this.getBiochemicalReactionList().values()) {

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
	 * @return the pathways
	 */
	public BioCollection<BioPathway> getPathwaysView() {
		return pathways.getView();
	}


	/**
	 * @return the metabolites
	 */
	public BioCollection<BioMetabolite> getMetabolitesView() {
		return metabolites.getView();
	}


	/**
	 * @return the proteins
	 */
	public BioCollection<BioProtein> getProteinsView() {
		return proteins.getView();
	}


	/**
	 * @return the genes
	 */
	public BioCollection<BioGene> getGenesView() {
		return genes.getView();
	}


	/**
	 * @return the reactions
	 */
	public BioCollection<BioReaction> getReactionsView() {
		return reactions.getView();
	}


	/**
	 * @return the compartments
	 */
	public BioCollection<BioCompartment> getCompartmentsView() {
		return compartments.getView();
	}


	/**
	 * @return the enzymes
	 */
	public BioCollection<BioEnzyme> getEnzymesView() {
		return enzymes.getView();
	}

}
