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
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * @author Ludovic COTTRET
 * 
 */

public class BioNetwork {


	private HashMap<String, BioPathway> pathwayList = new HashMap<String, BioPathway>();

	private HashMap<String, BioPhysicalEntity> physicalEntityList = new HashMap<String, BioPhysicalEntity>();
	
	private HashMap<String, BioComplex> complexList = new HashMap<String, BioComplex>();

	private HashMap<String, BioProtein> proteinList = new HashMap<String, BioProtein>();

	private HashMap<String, BioGene> geneList = new HashMap<String, BioGene>();

	private HashMap<String, BioReaction> biochemicalReactionList = new HashMap<String, BioReaction>();

	private HashMap<String, BioCatalysis> catalysisList = new HashMap<String, BioCatalysis>();

	private HashMap<String, BioTransport> transportList = new HashMap<String, BioTransport>();

	private HashMap<String, BioCompartment> compartments = new HashMap<String, BioCompartment>();

	private HashMap<String, BioCompartmentType> listOfCompartmentType= new HashMap<String, BioCompartmentType>();

	private HashMap<String, BioPhysicalEntity> enzymes = null;

	private String id = "NA";

	private String name = "NA";

	private String type = "custom";

	private String sboterm;


	private HashMap<String, BioUnitDefinition> unitDefinitions = new HashMap<String, BioUnitDefinition>();


	/**
	 * Constructor from scratch
	 */
	public BioNetwork() {
		super();
	}

	/**
	 * Constructor from a list of {@link BioReaction} Duplicate
	 * reactions
	 */
	public BioNetwork(HashMap<String, BioReaction> listOfReactions) {

		this.setId("BioNetwork" + (new Date()).getTime());

		for (BioReaction rxn : listOfReactions.values()) {

			BioReaction newRxn = new BioReaction(rxn);
			this.addBiochemicalReaction(newRxn);

			this.addUnitDefinition(newRxn.getLowerBound().unitDefinition);
			this.addUnitDefinition(newRxn.getUpperBound().unitDefinition);

		}
	}
	
	/**
	 * Filter the holes, i.e. the reactions not spontaneous and not associated
	 * with an enzyme
	 * 
	 */

	public void removeInfeasibleReactions() {

		HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>(
				this.getBiochemicalReactionList());

		for (BioReaction reaction : reactions.values()) {

			if (reaction.isPossible() == false) {
				this.removeBioChemicalReaction(reaction.getId());
			}
		}
	}

	/**
	 * @return Returns the pathwayList
	 */
	public HashMap<String, BioPathway> getPathwayList() {
		return pathwayList;
	}

	/**
	 * Add a pathway in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addPathway(BioPathway o) {
		this.pathwayList.put(o.getId(), o);
	}

	/**
	 * @return Returns the complexList.
	 */
	public HashMap<String, BioComplex> getComplexList() {
		return complexList;
	}

	/**
	 * Add a complex
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addComplex(BioComplex o) {
		this.complexList.put(o.getId(), o);
	}

	/**
	 * @return Returns the physicalEntityList.
	 */
	public HashMap<String, BioPhysicalEntity> getPhysicalEntityList() {
		return physicalEntityList;
	}

	/**
	 * Add a physical entity in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addPhysicalEntity(BioPhysicalEntity o) {

		this.physicalEntityList.put(o.getId(), o);
		this.compartments.put(o.getCompartment().getId(), o.getCompartment());

	}
	
	/**
	 * Add a physical entity in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addPhysicalEntities(Collection<BioPhysicalEntity> compounds) {
		for(BioPhysicalEntity e : compounds){
			this.addPhysicalEntity(e);
		}
	}

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
	 * Remove several compounds
	 * 
	 * @param compounds
	 */
	public void removeCompoundsFromIds(Set<String> compounds) {

		for (String cpd : compounds) {
			this.removeCompound(cpd);
		}

		return;

	}
	
	/**
	 * Remove several compounds
	 * 
	 * @param compounds
	 */
	public void removeCompounds(Set<BioPhysicalEntity> compounds) {

		for (BioPhysicalEntity cpd : compounds) {
			this.removeCompound(cpd.getId());
		}

		return;

	}

	/**
	 * Removes a reaction from a network. Removes also the compounds which
	 * become orphan
	 */
	public void removeBioChemicalReaction(String id) {

		if (this.getBiochemicalReactionList().containsKey(id) == true) {
			BioReaction rxn = this.getBiochemicalReactionList().get(id);

			HashMap<String, BioPhysicalEntity> left = rxn.getLeftList();
			HashMap<String, BioPhysicalEntity> right = rxn.getRightList();
			HashMap<String, BioParticipant> leftP = rxn
					.getLeftParticipantList();
			HashMap<String, BioParticipant> rightP = rxn
					.getRightParticipantList();

			this.getBiochemicalReactionList().remove(id);

			HashMap<String, BioPhysicalEntity> leftAndRight = new HashMap<String, BioPhysicalEntity>();
			leftAndRight.putAll(left);
			leftAndRight.putAll(right);

			HashMap<String, BioParticipant> leftAndRightP = new HashMap<String, BioParticipant>();
			leftAndRightP.putAll(leftP);
			leftAndRightP.putAll(rightP);

			for (String cpdId : leftAndRight.keySet()) {

				if (this.getPhysicalEntityList().containsKey(cpdId)) {

					this.getPhysicalEntityList().get(cpdId)
					.removeReactionAsProduct(id);
					this.getPhysicalEntityList().get(cpdId)
					.removeReactionAsSubstrate(id);

					if (this.getPhysicalEntityList().get(cpdId)
							.getReactionsAsProduct().size() == 0
							&& this.getPhysicalEntityList().get(cpdId)
							.getReactionsAsSubstrate().size() == 0) {
						// The compound does not occur in any reaction any more
						this.getPhysicalEntityList().remove(cpdId);
					}
				}

			}

		}

	}

	/**
	 * @return Returns the proteinList.
	 */
	public HashMap<String, BioProtein> getProteinList() {
		return proteinList;
	}

	/**
	 * Add a protein in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addProtein(BioProtein o) {
		this.proteinList.put(o.getId(), o);

		for (BioGene gene : o.getGeneList().values()) {
			this.addGene(gene);
		}

	}

	/**
	 * @return Returns the biochemicalReactionList.
	 */
	public HashMap<String, BioReaction> getBiochemicalReactionList() {
		return biochemicalReactionList;
	}

	/**
	 * Add a biochemical reaction in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addBiochemicalReaction(BioReaction o) {
		
		//add reaction's substrate to model if not already set
		for (BioPhysicalEntity cpd : o.getLeftList().values()) {
			if (!this.getPhysicalEntityList().containsKey(cpd.getId())) {
				this.addPhysicalEntity(cpd);
			}
			
			//add reaction to compound's consuming reactions list
			this.getPhysicalEntityList().get(cpd.getId())
			.addReactionAsSubstrate(o);
			if (o.isReversible()) {
				//if reversible, add reaction to compound's producing reactions list
				this.getPhysicalEntityList().get(cpd.getId())
				.addReactionAsProduct(o);
			}

		}
		
		//add reaction's product to model if not already set
		for (BioPhysicalEntity cpd : o.getRightList().values()) {
			if (!this.getPhysicalEntityList().containsKey(cpd.getId())) {
				this.addPhysicalEntity(cpd);
			}
			
			//add reaction to compound's producing reactions list
			this.getPhysicalEntityList().get(cpd.getId())
			.addReactionAsProduct(o);
			if (o.isReversible()) {
				//if reversible, add reaction to compound's consuming reactions list
				this.getPhysicalEntityList().get(cpd.getId())
				.addReactionAsSubstrate(o);
			}
		}
		
		//add reaction flux units (lower and upper bound) to model if not already set.
		BioUnitDefinition lowUnit = o.getLowerBound().getUnitDefinition();
		if(!this.getUnitDefinitions().containsKey(lowUnit.getId())){
			this.addUnitDefinition(lowUnit);
		}
		BioUnitDefinition upUnit = o.getLowerBound().getUnitDefinition();
		if(!upUnit.equals(lowUnit) && !this.getUnitDefinitions().containsKey(upUnit.getId())){
			this.addUnitDefinition(upUnit);
		}

		//add biochemical reaction to model.
		this.biochemicalReactionList.put(o.getId(), o);

	}

	/**
	 * @return Returns the catalysisList.
	 */
	public HashMap<String, BioCatalysis> getCatalysisList() {
		return catalysisList;
	}

	/**
	 * Add a catalysis in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addCatalysis(BioCatalysis o) {
		this.catalysisList.put(o.getId(), o);
	}

	/**
	 * @return Returns the transportList.
	 */
	public HashMap<String, BioTransport> getTransportList() {
		return transportList;
	}

	/**
	 * Add a transport in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addTransport(BioTransport o) {
		this.transportList.put(o.getId(), o);
	}


	/**
	 * @return Returns the geneList.
	 */
	public HashMap<String, BioGene> getGeneList() {
		return geneList;
	}

	/**
	 * @param a
	 *            gene to add
	 */
	public void addGene(BioGene gene) {
		geneList.put(gene.getId(), gene);
	}



	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
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
	 *         substrate
	 */

	public HashMap<String, BioReaction> getListOfReactionsAsPrimarySubstrate(
			String cpd) {

		HashMap<String, BioReaction> reactionsAsSubstrate = new HashMap<String, BioReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsSubstrate;
		}

		for (BioReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			HashMap<String, BioPhysicalEntity> listOfSubstrates = rxn
					.getListOfPrimarySubstrates();

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
	 * @param cpd
	 * @return the list of reactionNodes which involves the compound cpd as
	 *         primary product
	 */

	public HashMap<String, BioReaction> getListOfReactionsAsPrimaryProduct(
			String cpd) {

		HashMap<String, BioReaction> reactionsAsProduct = new HashMap<String, BioReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsProduct;
		}

		for (BioReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			HashMap<String, BioPhysicalEntity> listOfProducts = rxn
					.getListOfPrimaryProducts();

			if (listOfProducts.containsKey(cpd)) {
				reactionsAsProduct.put(rxn.getId(), rxn);
			}
		}

		return reactionsAsProduct;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	 * @param left
	 * @return the list of the reactionNodes which have these substrates
	 */
	public HashMap<String, BioReaction> reactionsWithTheseSubstrates(
			Set<String> left) {

		HashMap<String, BioReaction> listOfReactions = new HashMap<String, BioReaction>();

		for (BioReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			Set<String> substrates = rxn.getListOfSubstrates().keySet();

			if (substrates.equals(left))
				listOfReactions.put(rxn.getId(), rxn);

		}

		return listOfReactions;

	}



	public void setBiochemicalReactionList(
			HashMap<String, BioReaction> biochemicalReactionList) {
		this.biochemicalReactionList = biochemicalReactionList;
	}

	public void setCatalysisList(HashMap<String, BioCatalysis> catalysisList) {
		this.catalysisList = catalysisList;
	}

	public void setComplexList(HashMap<String, BioComplex> complexList) {
		this.complexList = complexList;
	}

	public void setGeneList(HashMap<String, BioGene> geneList) {
		this.geneList = geneList;
	}

	public void setPathwayList(HashMap<String, BioPathway> pathwayList) {
		this.pathwayList = pathwayList;
	}

	public void setPhysicalEntityList(
			HashMap<String, BioPhysicalEntity> physicalEntityList) {
		this.physicalEntityList = physicalEntityList;
	}

	public void setProteinList(HashMap<String, BioProtein> proteinList) {
		this.proteinList = proteinList;
	}

	public void setTransportList(HashMap<String, BioTransport> transportList) {
		this.transportList = transportList;
	}

	public void addCompartment(BioCompartment compartment) {
		this.compartments.put(compartment.getId(), compartment);
	}

	public void addCompartmentType(BioCompartmentType compartmenttype) {
		this.listOfCompartmentType.put(compartmenttype.getId(), compartmenttype);
	}

	public HashMap<String, BioCompartment> getCompartments() {
		return compartments;
	}

	public void setCompartments(HashMap<String, BioCompartment> compartments) {
		this.compartments = compartments;
	}

	public HashMap<String, BioUnitDefinition> getUnitDefinitions() {
		return unitDefinitions;
	}

	public void setUnitDefinitions(
			HashMap<String, BioUnitDefinition> unitsDefinition) {
		this.unitDefinitions = unitsDefinition;
	}

	public void addUnitDefinition(BioUnitDefinition ud) {
		this.unitDefinitions.put(ud.getId(), ud);
	}

	/**
	 * Remove all the reactions whose the 'hole' flag is set to true
	 */
	public void removeHoles() {

		Set<String> reactionIds = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactionIds) {

			BioReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			if (reaction.getHole()) {
				this.removeBioChemicalReaction(reactionId);
			}
		}
	}

	/**
	 * Remove all the reactions that contain generic metabolites
	 */
	public void removeGeneric() {

		Set<String> reactionIds = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactionIds) {

			BioReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			Boolean flag = false;

			for (BioParticipant leftP : reaction
					.getLeftParticipantList().values()) {
				if (leftP.getPhysicalEntity().getIsHolderClass()) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				for (BioParticipant rightP : reaction
						.getRightParticipantList().values()) {
					if (rightP.getPhysicalEntity().getIsHolderClass()) {
						flag = true;
						break;
					}
				}
			}

			if (flag) {
				this.removeBioChemicalReaction(reactionId);
			}
		}
	}

	/**
	 * Remove in each reaction the metabolites flagged as side compounds
	 */
	public void removeSide() {

		Set<String> reactionIds = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactionIds) {

			BioReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			HashMap<String, BioParticipant> leftParticipants = new HashMap<String, BioParticipant>(
					reaction.getLeftParticipantList());

			for (BioParticipant bpe : leftParticipants.values()) {

				if (bpe.getIsPrimaryCompound() == false) {
					reaction.removeLeft(bpe.getPhysicalEntity().getId());
				}

			}

			HashMap<String, BioParticipant> rightParticipants = new HashMap<String, BioParticipant>(
					reaction.getRightParticipantList());

			for (BioParticipant bpe : rightParticipants.values()) {

				if (bpe.getIsPrimaryCompound() == false) {
					reaction.removeRight(bpe.getPhysicalEntity().getId());
				}

			}

			if (reaction.getLeftList().size() == 0
					|| reaction.getRightList().size() == 0) {
				this.removeBioChemicalReaction(reactionId);
			}

		}
	}

	/**
	 * In each reaction, remove the metabolite considered as cofactor
	 */
	public void removeCofactors() {

		Set<String> reactionIds = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactionIds) {

			BioReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			HashMap<String, BioParticipant> leftParticipants = new HashMap<String, BioParticipant>(
					reaction.getLeftParticipantList());

			for (BioParticipant bpe : leftParticipants.values()) {

				if (bpe.getIsCofactor() == true) {
					reaction.removeLeft(bpe.getPhysicalEntity().getId());
				}

			}

			HashMap<String, BioParticipant> rightParticipants = new HashMap<String, BioParticipant>(
					reaction.getRightParticipantList());

			for (BioParticipant bpe : rightParticipants.values()) {

				if (bpe.getIsCofactor() == true) {
					reaction.removeRight(bpe.getPhysicalEntity().getId());
				}
			}

			if (reaction.getLeftList().size() == 0
					|| reaction.getRightList().size() == 0) {
				this.removeBioChemicalReaction(reactionId);
			}

		}
	}

	/**
	 * Remove the reactions that are not involved in the pathways that are in
	 * the input set Do not remove the reactions that are not involved in any
	 * pathway
	 * 
	 * @param pathwayIds
	 */
	public void filterByPathways(Set<String> pathwayIds) {

		Set<String> reactionIds = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactionIds) {

			BioReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			Set<String> pathwayReactionIds = reaction.getPathwayList().keySet();

			if (pathwayReactionIds.size() > 0) {

				Boolean flag = true;

				for (String pathwayId : pathwayReactionIds) {
					if (pathwayIds.contains(pathwayId)) {
						flag = false;
						break;
					}
				}

				if (flag) {
					this.removeBioChemicalReaction(reactionId);
				}

			}
		}
	}

	/**
	 * 
	 * Remove the compounds that are not in the input set
	 */
	public void filterByMetabolites(Set<String> metaboliteIds) {

		Set<String> reactionIds = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactionIds) {

			if (this.getBiochemicalReactionList().containsKey(reactionId)) {
				BioReaction reaction = this
						.getBiochemicalReactionList().get(reactionId);

				HashMap<String, BioParticipant> leftParticipants = new HashMap<String, BioParticipant>(
						reaction.getLeftParticipantList());

				for (BioParticipant bpe : leftParticipants
						.values()) {

					if (!metaboliteIds
							.contains(bpe.getPhysicalEntity().getId())) {
						reaction.removeLeft(bpe.getPhysicalEntity().getId());
						this.removeCompound(bpe.getPhysicalEntity().getId());
					}

				}

				HashMap<String, BioParticipant> rightParticipants = new HashMap<String, BioParticipant>(
						reaction.getRightParticipantList());

				for (BioParticipant bpe : rightParticipants
						.values()) {

					if (!metaboliteIds
							.contains(bpe.getPhysicalEntity().getId())) {
						reaction.removeRight(bpe.getPhysicalEntity().getId());
						this.removeCompound(bpe.getPhysicalEntity().getId());
					}
				}

				if (reaction.getLeftList().size() == 0
						&& reaction.getRightList().size() == 0) {
					this.removeBioChemicalReaction(reactionId);
				}
			}
		}
	}

	/**
	 * Filter the network by a list of reactions
	 * 
	 * @param reactionIds
	 */
	public void filterByReactions(Set<String> reactionsToKeep) {

		HashSet<String> reactions = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactions) {
			if (!reactionsToKeep.contains(reactionId)) {
				this.removeBioChemicalReaction(reactionId);
			}
		}

		return;
	}

	/**
	 * Remove all the reactions that are not in a pathway
	 */
	public void getOnlyPathwayReactions() {

		Set<String> reactionIds = new HashSet<String>(this
				.getBiochemicalReactionList().keySet());

		for (String reactionId : reactionIds) {

			BioReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			if (reaction.getPathwayList().size() == 0) {
				this.removeBioChemicalReaction(reactionId);
			}
		}
	}

	/**
	 * Add external reaction producing a metabolide
	 * @param cpdId metabolite identifier
	 * @param withExternal if the external compound should be added
	 * @param ExternalCpdId the external compound identifier (ignored if withExternal set to false)
	 * @param ExchangeReactionId the exchange reaction identifier
	 * @param ExternalCompartmentId the external reaction identifier
	 * @return 
	 */
	public void addExchangeReactionToMetabolite (String cpdId,
			Boolean withExternal, String ExternalCpdId, String ExchangeReactionId, String ExternalCompartmentId) 
					throws IllegalArgumentException{

		if (!this.getPhysicalEntityList().containsKey(cpdId)) {
			throw new IllegalArgumentException("compound "+cpdId+" not in network");
		}
		if (this.getBiochemicalReactionList().containsKey(ExchangeReactionId)){
			throw new IllegalArgumentException("reaction id "+ExchangeReactionId+" already used");
		}
		if (this.getBiochemicalReactionList().containsKey(ExternalCpdId)){
			throw new IllegalArgumentException("compound id "+ExternalCpdId+" already used");
		}

		BioReaction rxn = new BioReaction(ExchangeReactionId);

		if (withExternal) {
			BioPhysicalEntity cpd = new BioPhysicalEntity(ExternalCpdId);

			BioCompartment compartment;

			if (this.getCompartments().containsKey(ExternalCompartmentId)) {
				compartment = this.getCompartments().get(ExternalCompartmentId);
			} else {
				if (ExternalCompartmentId == null || ExternalCompartmentId.equals("")) {
					ExternalCompartmentId = "NA";
				}
				compartment = new BioCompartment(ExternalCompartmentId, ExternalCompartmentId);

				this.addCompartment(compartment);

			}

			cpd.setBoundaryCondition(true);
			cpd.setCompartment(compartment);

			rxn.addLeftParticipant(new BioParticipant(cpd));
		}
		rxn.addRightParticipant(new BioParticipant(this
				.getPhysicalEntityList().get(cpdId)));
		rxn.setReversibility(true);

		this.addBiochemicalReaction(rxn);

		return;

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


	/**
	 * Return all existing enzymes, without retrieving them from the reaction.
	 * @return a {@link HashMap} of {@link BioPhysicalEntity}
	 */
	public HashMap<String, BioPhysicalEntity> getEnzList() {

		if (this.enzymes == null) {
			this.enzymes = new HashMap<String, BioPhysicalEntity>();
		}
		return this.enzymes;

	}




	/**
	 * computes the list of the enzymes from the existing reactions in the network
	 * @return a {@link HashMap} of {@link BioPhysicalEntity}
	 */
	public HashMap<String, BioPhysicalEntity> getEnzymeList() {

		if (this.enzymes == null) {
			this.enzymes = new HashMap<String, BioPhysicalEntity>();

			for (BioReaction reaction : this
					.getBiochemicalReactionList().values()) {
				HashMap<String, BioPhysicalEntity> enzymes = reaction
						.getEnzList();
				for (BioPhysicalEntity enzyme : enzymes.values()) {
					this.enzymes.put(enzyme.getId(), enzyme);
				}
			}
		}

		return this.enzymes;

	}

	public HashMap<String, BioCompartmentType> getListOfCompartmentType() {
		return listOfCompartmentType;
	}

	public void setListOfCompartmentType(
			HashMap<String, BioCompartmentType> listOfCompartmentType) {
		this.listOfCompartmentType = listOfCompartmentType;
	}

	public String getSboterm() {
		return sboterm;
	}

	public void setSboterm(String sboterm) {
		this.sboterm = sboterm;
	}

}
