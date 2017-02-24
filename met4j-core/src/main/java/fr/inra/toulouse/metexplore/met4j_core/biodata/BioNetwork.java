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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.utils.BioChemicalReactionUtils;
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

	private HashMap<String, BioChemicalReaction> biochemicalReactionList = new HashMap<String, BioChemicalReaction>();

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
	 * Constructor from a list of {@link BioChemicalReaction} Duplicate
	 * reactions
	 */
	public BioNetwork(HashMap<String, BioChemicalReaction> listOfReactions) {

		this.setId("BioNetwork" + (new Date()).getTime());

		for (BioChemicalReaction rxn : listOfReactions.values()) {

			BioChemicalReaction newRxn = new BioChemicalReaction(rxn);
			this.addBiochemicalReaction(newRxn);

			this.addUnitDefinition(newRxn.getLowerBound().unitDefinition);
			this.addUnitDefinition(newRxn.getUpperBound().unitDefinition);

		}
	}

	public void printBioNetworkSizeToErr(){

		System.err.println("Size of "+this.getId()+" :");

		System.err.println(this.getUnitDefinitions().size()+" unit def");
		System.err.println(this.getCompartments().size()+" Comparts");
		System.err.println(this.getGeneList().size()+" genes");
		System.err.println(this.getProteinList().size()+" proteins");
		System.err.println(this.getEnzList().size()+" enzymes");
		System.err.println(this.getPathwayList().size()+" pathways");
		System.err.println(this.getBiochemicalReactionList().size()+" reactions");
		System.err.println(this.getPhysicalEntityList().size()+" metabolites");

	}



	public void printBioNetworkSizeToOut(){

		System.out.println("Size of "+this.getId()+" :");

		System.out.println(this.getUnitDefinitions().size()+" unit def");
		System.out.println(this.getCompartments().size()+" Comparts");
		System.out.println(this.getGeneList().size()+" genes");
		System.out.println(this.getProteinList().size()+" proteins");
		System.out.println(this.getEnzList().size()+" enzymes");
		System.out.println(this.getPathwayList().size()+" pathways");
		System.out.println(this.getBiochemicalReactionList().size()+" reactions");
		System.out.println(this.getPhysicalEntityList().size()+" metabolites");

	}





	/**
	 * Filter the holes, i.e. the reactions not spontaneous and not associated
	 * with an enzyme
	 * 
	 */

	public void removeInfeasibleReactions() {

		HashMap<String, BioChemicalReaction> reactions = new HashMap<String, BioChemicalReaction>(
				this.getBiochemicalReactionList());

		for (BioChemicalReaction reaction : reactions.values()) {

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

			HashMap<String, BioChemicalReaction> RP = this
					.getListOfReactionsAsProduct(id);
			HashMap<String, BioChemicalReaction> RC = this
					.getListOfReactionsAsSubstrate(id);

			HashMap<String, BioChemicalReaction> reactions = new HashMap<String, BioChemicalReaction>();
			reactions.putAll(RP);
			reactions.putAll(RC);

			this.getPhysicalEntityList().remove(id);

			for (BioChemicalReaction rxn : reactions.values()) {

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
			BioChemicalReaction rxn = this.getBiochemicalReactionList().get(id);

			HashMap<String, BioPhysicalEntity> left = rxn.getLeftList();
			HashMap<String, BioPhysicalEntity> right = rxn.getRightList();
			HashMap<String, BioPhysicalEntityParticipant> leftP = rxn
					.getLeftParticipantList();
			HashMap<String, BioPhysicalEntityParticipant> rightP = rxn
					.getRightParticipantList();

			this.getBiochemicalReactionList().remove(id);

			HashMap<String, BioPhysicalEntity> leftAndRight = new HashMap<String, BioPhysicalEntity>();
			leftAndRight.putAll(left);
			leftAndRight.putAll(right);

			HashMap<String, BioPhysicalEntityParticipant> leftAndRightP = new HashMap<String, BioPhysicalEntityParticipant>();
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
	public HashMap<String, BioChemicalReaction> getBiochemicalReactionList() {
		return biochemicalReactionList;
	}

	/**
	 * Add a biochemical reaction in the list
	 * 
	 * @param o
	 *            the object to add
	 */
	public void addBiochemicalReaction(BioChemicalReaction o) {
		
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
			// System.err.println("ID "+metabolite.getId()+" -- "+metabolite.getName());
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
	public HashMap<String, BioChemicalReaction> getListOfReactionsAsSubstrate(
			String cpd) {

		HashMap<String, BioChemicalReaction> reactionsAsSubstrate = new HashMap<String, BioChemicalReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsSubstrate;
		}

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			HashMap<String, BioPhysicalEntity> listOfSubstrates = rxn
					.getListOfSubstrates();

			if (listOfSubstrates.containsKey(cpd)) {
				reactionsAsSubstrate.put(rxn.getId(), rxn);
			}
		}

		return reactionsAsSubstrate;

	}

	public String getNewReactionId(String prefix) {

		String id;

		int n = 1;

		id = prefix + n;

		while (this.getBiochemicalReactionList().containsKey(id)) {
			n++;
			id = prefix + n;
		}

		return id;

	}

	public String getNewMetaboliteId(String prefix, String suffix) {

		String id;

		int n = 1;

		id = prefix + n + suffix;

		while (this.getPhysicalEntityList().containsKey(id)) {
			n++;
			id = prefix + n + suffix;
		}

		return id;

	}

	/**
	 * @param cpd
	 * @return the list of reactionNodes which involves the compound cpd as
	 *         substrate
	 */

	public HashMap<String, BioChemicalReaction> getListOfReactionsAsPrimarySubstrate(
			String cpd) {

		HashMap<String, BioChemicalReaction> reactionsAsSubstrate = new HashMap<String, BioChemicalReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsSubstrate;
		}

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
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

	public HashMap<String, BioChemicalReaction> getListOfReactionsAsProduct(
			String cpd) {

		HashMap<String, BioChemicalReaction> reactionsAsProduct = new HashMap<String, BioChemicalReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsProduct;
		}

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
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

	public HashMap<String, BioChemicalReaction> getListOfReactionsAsPrimaryProduct(
			String cpd) {

		HashMap<String, BioChemicalReaction> reactionsAsProduct = new HashMap<String, BioChemicalReaction>();

		if (this.getPhysicalEntityList().containsKey(cpd) == false) {
			return reactionsAsProduct;
		}

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
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
	public HashMap<String, BioChemicalReaction> reactionsWith(Set<String> left,
			Set<String> right) {

		HashMap<String, BioChemicalReaction> listOfReactions = new HashMap<String, BioChemicalReaction>();

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
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
	public HashMap<String, BioChemicalReaction> reactionsThatInvolvesAtLeast(
			Set<String> set1, Set<String> set2) {

		HashMap<String, BioChemicalReaction> listOfReactions = new HashMap<String, BioChemicalReaction>();

		for (BioChemicalReaction reaction : this.getBiochemicalReactionList()
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
	public HashMap<String, BioChemicalReaction> reactionsWithTheseSubstrates(
			Set<String> left) {

		HashMap<String, BioChemicalReaction> listOfReactions = new HashMap<String, BioChemicalReaction>();

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			Set<String> substrates = rxn.getListOfSubstrates().keySet();

			if (substrates.equals(left))
				listOfReactions.put(rxn.getId(), rxn);

		}

		return listOfReactions;

	}

	/**
	 * Write the network as a list of reactions If a compound doesn't occur in
	 * any reaction, it will be indicated at the end.
	 */
	public String networkAsString(Boolean encodeSbml) {

		String out = "";

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			String id = rxn.getId();
			if(encodeSbml)
			{
				id = StringUtils.sbmlEncode(id);
			}

			out = out.concat(id+" = "+rxn.getEquation() + "\n");
		}

		for (BioPhysicalEntity cpd : this.getPhysicalEntityList().values()) {
			if (cpd.getReactionsAsSubstrate().size() == 0
					&& cpd.getReactionsAsProduct().size() == 0) {

				if (encodeSbml) {
					out = out
							.concat(StringUtils.sbmlEncode(cpd.getId()) + "\n");
				} else {
					out = out.concat(cpd.getId() + "\n");
				}
			}
		}

		return out;
	}

	public String networkAsString() {
		return this.networkAsString(false);
	}

	public String printNetworkForHuman() {

		String out = "";

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
				.values()) {
			out = out.concat(rxn.getEquationForHuman() + "\n");
		}

		for (BioPhysicalEntity cpd : this.getPhysicalEntityList().values()) {
			if (cpd.getReactionsAsSubstrate().size() == 0
					&& cpd.getReactionsAsProduct().size() == 0) {

				out = out.concat(StringUtils.htmlEncode(cpd.getName()) + "\n");
			}
		}

		return out;
	}

	public void setBiochemicalReactionList(
			HashMap<String, BioChemicalReaction> biochemicalReactionList) {
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


	public Boolean isEmpty() {

		Boolean flag = false;

		if (this.getPhysicalEntityList().size() == 0)
			flag = true;

		return flag;
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

			BioChemicalReaction reaction = this.getBiochemicalReactionList()
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

			BioChemicalReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			Boolean flag = false;

			for (BioPhysicalEntityParticipant leftP : reaction
					.getLeftParticipantList().values()) {
				if (leftP.getPhysicalEntity().getIsHolderClass()) {
					flag = true;
					break;
				}
			}

			if (!flag) {
				for (BioPhysicalEntityParticipant rightP : reaction
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

			BioChemicalReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			HashMap<String, BioPhysicalEntityParticipant> leftParticipants = new HashMap<String, BioPhysicalEntityParticipant>(
					reaction.getLeftParticipantList());

			for (BioPhysicalEntityParticipant bpe : leftParticipants.values()) {

				if (bpe.getIsPrimaryCompound() == false) {
					reaction.removeLeft(bpe.getPhysicalEntity().getId());
				}

			}

			HashMap<String, BioPhysicalEntityParticipant> rightParticipants = new HashMap<String, BioPhysicalEntityParticipant>(
					reaction.getRightParticipantList());

			for (BioPhysicalEntityParticipant bpe : rightParticipants.values()) {

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

			BioChemicalReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			HashMap<String, BioPhysicalEntityParticipant> leftParticipants = new HashMap<String, BioPhysicalEntityParticipant>(
					reaction.getLeftParticipantList());

			for (BioPhysicalEntityParticipant bpe : leftParticipants.values()) {

				if (bpe.getIsCofactor() == true) {
					reaction.removeLeft(bpe.getPhysicalEntity().getId());
				}

			}

			HashMap<String, BioPhysicalEntityParticipant> rightParticipants = new HashMap<String, BioPhysicalEntityParticipant>(
					reaction.getRightParticipantList());

			for (BioPhysicalEntityParticipant bpe : rightParticipants.values()) {

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

			BioChemicalReaction reaction = this.getBiochemicalReactionList()
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
				BioChemicalReaction reaction = this
						.getBiochemicalReactionList().get(reactionId);

				HashMap<String, BioPhysicalEntityParticipant> leftParticipants = new HashMap<String, BioPhysicalEntityParticipant>(
						reaction.getLeftParticipantList());

				for (BioPhysicalEntityParticipant bpe : leftParticipants
						.values()) {

					if (!metaboliteIds
							.contains(bpe.getPhysicalEntity().getId())) {
						reaction.removeLeft(bpe.getPhysicalEntity().getId());
						this.removeCompound(bpe.getPhysicalEntity().getId());
					}

				}

				HashMap<String, BioPhysicalEntityParticipant> rightParticipants = new HashMap<String, BioPhysicalEntityParticipant>(
						reaction.getRightParticipantList());

				for (BioPhysicalEntityParticipant bpe : rightParticipants
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

			BioChemicalReaction reaction = this.getBiochemicalReactionList()
					.get(reactionId);

			if (reaction.getPathwayList().size() == 0) {
				this.removeBioChemicalReaction(reactionId);
			}
		}
	}

	/**
	 * Returns the set of reactions catalysed by a gene
	 * 
	 * @param geneId
	 *            : String
	 * @return a Set of Strings TODO : test it
	 */
	public Set<String> getReactionsFromGene(String geneId) {

		Set<String> reactions = new HashSet<String>();

		for (BioChemicalReaction reaction : this.getBiochemicalReactionList()
				.values()) {

			HashMap<String, BioGene> genes = BioChemicalReactionUtils.getListOfGenesFromReaction(reaction);

			if (genes.containsKey(geneId)) {
				reactions.add(reaction.getId());
			}
		}

		return reactions;

	}

	/**
	 * Reads a gene association written in a Palsson way. Creates genes if they
	 * don't exist. Create proteins and enzymes from genes.
	 * 
	 * @param reactionId
	 * @param gpr
	 * @return true if no error, false otherwise TODO : test the gpr
	 */
	public Boolean setGeneAssociationFromString(String reactionId, String gpr) {

		Boolean flag = true;

		BioChemicalReaction rxn = this.getBiochemicalReactionList().get(
				reactionId);

		rxn.getEnzList().clear();
		rxn.getEnzrxnsList().clear();

		String[] tab;

		ArrayList<String[]> genesAssociated = new ArrayList<String[]>();

		if (!gpr.equals("") && !gpr.equals("NA")) {

			if (gpr.contains(" or ")) {
				tab = gpr.split(" or ");
			} else {
				tab = new String[1];
				tab[0] = gpr;
			}

			for (String genesAssociatedStr : tab) {

				genesAssociatedStr = genesAssociatedStr.replaceAll("[\\(\\)]",
						"");

				String[] tab2;

				if (genesAssociatedStr.contains(" and ")) {
					tab2 = genesAssociatedStr.split(" and ");
				} else {
					tab2 = new String[1];
					tab2[0] = genesAssociatedStr;
				}

				int n = tab2.length;

				for (int k = 0; k < n; k++) {
					tab2[k] = tab2[k].replaceAll(" ", "");
				}

				genesAssociated.add(tab2);

			}
		}

		for (int k = 0; k < genesAssociated.size(); k++) {
			String[] tabGenes = genesAssociated.get(k);
			String enzymeId = StringUtils.implode(tabGenes, "_and_");

			BioComplex enzyme;

			if (!this.getComplexList().containsKey(enzymeId)) {
				enzyme = new BioComplex(enzymeId, enzymeId);

				this.addComplex(enzyme);
			}

			enzyme = this.getComplexList().get(enzymeId);

			rxn.addEnz(enzyme);

			BioProtein protein;

			if (!this.getProteinList().containsKey(enzymeId)) {
				protein = new BioProtein(enzymeId, enzymeId);
				this.addProtein(protein);
			}
			protein = this.getProteinList().get(enzymeId);
			enzyme.addComponent(new BioPhysicalEntityParticipant(protein));

			for (int u = 0; u < tabGenes.length; u++) {
				String geneId = tabGenes[u];
				BioGene gene;
				if (!this.getGeneList().containsKey(geneId)) {
					gene = new BioGene(geneId, geneId);
					this.addGene(gene);
				}

				gene = this.getGeneList().get(geneId);

				protein.addGene(gene);
			}
		}

		return flag;

	}



	/**
	 * Adds an exchange reaction to a metabolite
	 * 
	 * @param cpdId
	 * @param withExternal
	 * @param suffix
	 * @param compartmentId
	 */
	public String addExchangeReactionToMetabolite(String cpdId,
			Boolean withExternal, String suffix, String compartmentId) {

		if (!this.getPhysicalEntityList().containsKey(cpdId)) {
			return null;
		}

		BioChemicalReaction rxn = new BioChemicalReaction(
				this.getNewReactionId("R_EX_"));

		if (withExternal) {
			BioPhysicalEntity cpd = new BioPhysicalEntity(
					this.getNewMetaboliteId("M_", suffix));

			BioCompartment compartment;

			if (this.getCompartments().containsKey(compartmentId)) {
				compartment = this.getCompartments().get(compartmentId);
			} else {
				if (compartmentId == null || compartmentId.equals("")) {
					compartmentId = "NA";
				}
				compartment = new BioCompartment(compartmentId, compartmentId);

				this.addCompartment(compartment);

			}

			cpd.setBoundaryCondition(true);
			cpd.setCompartment(compartment);

			rxn.addLeftParticipant(new BioPhysicalEntityParticipant(cpd));
		}
		rxn.addRightParticipant(new BioPhysicalEntityParticipant(this
				.getPhysicalEntityList().get(cpdId)));
		rxn.setReversibility(true);

		this.addBiochemicalReaction(rxn);

		return rxn.getId();

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

			HashMap<String, BioChemicalReaction> rs = this
					.getListOfReactionsAsSubstrate(cpdId);
			HashMap<String, BioChemicalReaction> rp = this
					.getListOfReactionsAsProduct(cpdId);

			HashMap<String, BioChemicalReaction> reactions = new HashMap<String, BioChemicalReaction>(
					rs);
			reactions.putAll(rp);

			for (BioChemicalReaction r : reactions.values()) {
				pathways.putAll(r.getPathwayList());
			}
		}

		return pathways;
	}

	/**
	 * Replace all the compartments by one compartment "NA" If the suffix is
	 * "_IN_*", it is removed
	 * 
	 */
	public void removeCompartments() {

		// We replace all the compartments by one not specified
		BioCompartment cptNA = new BioCompartment("NA", "NA");
		this.setCompartments(new HashMap<String, BioCompartment>());
		this.addCompartment(cptNA);

		HashMap<String, BioPhysicalEntity> cpds = new HashMap<String, BioPhysicalEntity>(
				this.getPhysicalEntityList());

		for (BioPhysicalEntity cpd : cpds.values()) {

			this.getPhysicalEntityList().remove(cpd.getId());

			String newId = cpd.getId().replaceAll("_IN_.*", "");

			cpd.setId(newId);

			cpd.setCompartment(cptNA);

			this.addPhysicalEntity(cpd);

		}

	}

	/**
	 * Tests an objective function
	 * 
	 * @param obj
	 * @return
	 */
	public Boolean testObjectiveFunction(String obj) {

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

			if (!this.getBiochemicalReactionList().containsKey(reactionId)) {
				System.err.println("The reaction " + reactionId
						+ " is not in the network");
				return false;
			}

		}

		return flag;

	}

	/**
	 * Returns the list of exchange reactions for a metabolite
	 * 
	 * @param cpdId
	 * @return
	 */
	public HashMap<String, BioChemicalReaction> getExchangeReactionsOfMetabolite(
			String cpdId) {

		HashMap<String, BioChemicalReaction> reactions = new HashMap<String, BioChemicalReaction>();
		HashMap<String, BioChemicalReaction> ex_reactions = new HashMap<String, BioChemicalReaction>();

		if (!this.getPhysicalEntityList().containsKey(cpdId)) {
			System.err.println("[Warning] getExchangeReactionsOfMetabolite : "
					+ cpdId + " is not in the network !");
			return ex_reactions;
		}

		reactions.putAll(this.getListOfReactionsAsProduct(cpdId));
		reactions.putAll(this.getListOfReactionsAsSubstrate(cpdId));

		for (BioChemicalReaction reaction : reactions.values()) {

			if (reaction.isExchangeReaction()) {
				ex_reactions.put(reaction.getId(), reaction);
			}

		}

		return ex_reactions;

	}

	/**
	 * Compute the atom balances for all the reactions
	 * 
	 * @return
	 */
	public HashMap<String, HashMap<String, Double>> computeBalanceAllReactions() {

		HashMap<String, HashMap<String, Double>> balances = new HashMap<String, HashMap<String, Double>>();

		for (BioChemicalReaction rxn : this.getBiochemicalReactionList()
				.values()) {

			String id = rxn.getId();

			HashMap<String, Double> balance = BioChemicalReactionUtils.computeAtomBalances(rxn);

			balances.put(id, balance);
		}

		return balances;

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

			for (BioChemicalReaction reaction : this
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


	public void resetEnzyme(){
		this.enzymes = null;
	}

	/**
	 * Finds a commpartement by its ID in the compartements lists
	 * @param Sid : String, Identifier of the compartment
	 * @return cmp : BioCompartment
	 */
	public BioCompartment findbioCompartmentInList(String Sid){

		if( this.compartments == null){
			return null;
		}
		else{
			for (BioCompartment cmp: this.getCompartments().values()){
				if(cmp.getId().equals(Sid)){
					return cmp;
				}
			}
			return null;
		}
	}

	public BioCompartmentType findbioCompartmentTypeInList(String Sid){

		if( this.listOfCompartmentType == null){
			return null;
		}
		else{
			for (BioCompartmentType compartType: this.getListOfCompartmentType().values()){
				if(compartType.getId().equals(Sid)){
					return compartType;
				}
			}
			return null;
		}
	}

	public BioUnitDefinition findUnitInUnitDefinituin(String UnitSid){
		if( this.unitDefinitions == null){
			return null;
		}
		else{
			for (BioUnitDefinition unitdef: this.getUnitDefinitions().values()){
				if(unitdef.getId().equals(UnitSid)){
					return unitdef;
				}
			}
			return null;
		}
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
