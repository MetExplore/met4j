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
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

/**
 * A conversion interaction in which one or more entities (substrates) undergo
 * covalent changes to become one or more other entities (products). The
 * substrates of biochemical reactionNodes are defined in terms of sums of
 * species. This is what is typically done in biochemistry, and, in principle,
 * all of the EC reactionNodes should be biochemical reactionNodes.
 * 
 * Example: ATP + H2O = ADP + Pi.
 * 
 * In this reaction, ATP is considered to be an equilibrium mixture of several
 * species, namely ATP4-, HATP3-, H2ATP2-, MgATP2-, MgHATP-, and Mg2ATP.
 * Additional species may also need to be considered if other ions (e.g. Ca2+)
 * that bind ATP are present. Similar considerations apply to ADP and to
 * inorganic phosphate (Pi). When writing biochemical reactionNodes, it is
 * important not to attach charges to the biochemical reactants and not to
 * include ions such as H+ and Mg2+ in the equation. The reaction is written in
 * the direction specified by the EC nomenclature system, if applicable,
 * regardless of the physiological direction(s) in which the reaction proceeds.
 * (This definition from EcoCyc)
 * 
 * NOTE: Polymerization reactionNodes involving large polymers whose structure
 * is not explicitly captured should generally be represented as unbalanced
 * reactionNodes in which the monomer is consumed but the polymer remains
 * unchanged, e.g. glycogen + glucose = glycogen.
 */

public class BioReaction extends BioEntity {


	private boolean spontaneous = false;
	private String ecNumber;
	private boolean reversible;

	
	private BioCollection<BioReactant> left = new BioCollection<BioReactant>();
	private BioCollection<BioReactant> right = new BioCollection<BioReactant>();
	
	public enum Side {LEFT, RIGHT};
	
	
	public BioReaction(String id) {
		super(id);
	}

	/**
	 * 
	 */
	@Override
	public String toString() {

		String str = new String();
		
		String direction = " -> ";

		str = str.concat(this.getId() + " : ");
		
		ArrayList<String> lefts = new ArrayList<String>();
		

		for (BioReactant cpd : this.getLeftReactants()) {

			lefts.add(cpd.toString());
		}
		
		String leftStr = String.join(" + ", lefts);

		if (this.isReversible()) {
			
			direction = " <-> ";
		}

		ArrayList<String> rights = new ArrayList<String>();
		
		
		for (BioReactant cpd : this.getRightReactants()) {

			rights.add(cpd.toString());
		}
		
		String rightStr = String.join(" + ", rights);
		
		str = str+leftStr+direction+rightStr;

		return str;

	}

	/**
	 * @return Returns the ecNumber.
	 */
	public String getEcNumber() {
		return ecNumber;
	}

	/**
	 * @param ecNumber
	 *            The ecNumber to set.
	 */
	public void setEcNumber(String ecNumber) {
		this.ecNumber = ecNumber;
	}



	/**
	 */
	public Boolean isReversible() {
		return reversible;
	}



	/**
	 * @return the spontaneous
	 */
	public boolean isSpontaneous() {
		return spontaneous;
	}

	/**
	 * @param spontaneous the spontaneous to set
	 */
	public void setSpontaneous(boolean spontaneous) {
		this.spontaneous = spontaneous;
	}

	/**
	 * @param reversible the reversible to set
	 */
	public void setReversible(boolean reversible) {
		this.reversible = reversible;
	}




	/**
	 * @return true if the reaction is a transport reaction,
	 * i.e a reaction that involves several compartmens
	 * Limitation : this reaction is considered as a transport reaction
	 *  Ex : A_a + C_b -> D_a + B_b
	 * 
	 */
	public Boolean isTransportReaction() {
		
		HashSet<BioCompartment> compartments = new HashSet<BioCompartment>();
		
		for(BioReactant s : this.getReactants()){
			compartments.add(s.getLocation());
		}
		
		if(compartments.size()>1){
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * @param side : {@link Side}
	 * @return unmodifiable {@link BioCollection} of entities involved in left {@link BioReactant}
	 */
	protected BioCollection<BioPhysicalEntity> getLeft() {
		return getSideEntities(Side.LEFT);
	}
	
	
	/**
	 * @param side : {@link Side}
	 * @return unmodifiable {@link BioCollection} of entities involved in right {@link BioReactant}
	 */
	protected BioCollection<BioPhysicalEntity> getRight() {
		return getSideEntities(Side.RIGHT);
	}
	
	
	
	/**
	 * @param side : {@link Side}
	 * @return  {@link BioCollection} of left {@link BioReactant}
	 */
	private BioCollection<BioReactant> getLeftReactants() {

		return getSideReactants(Side.LEFT);
	}

	/**
	 * @param side : {@link Side}
	 * @return {@link BioCollection}of right {@link BioReactant}
	 */
	private BioCollection<BioReactant> getRightReactants() {

		return getSideReactants(Side.RIGHT);
	}
	
	
	
	/**
	 * 
	 * @return a {@link BioCollection} of {@link BioPhysicalEntity}
	 */
	protected BioCollection<BioPhysicalEntity> getEntities() {
		
		BioCollection<BioReactant> reactants = this.getReactants();
		
		BioCollection<BioPhysicalEntity> entities = new BioCollection<BioPhysicalEntity>();
		
		for(BioReactant reactant : reactants)
		{
			BioPhysicalEntity entity = reactant.getPhysicalEntity();
			
			if(! entities.contains(entity)) {
				entities.add(entity);
			}
		}
		
		return entities;
	}
	
	
	/**
	 * 
	 * @return a {@link BioCollection} of {@link BioReactant}
	 */
	private BioCollection<BioReactant> getReactants() {

		BioCollection<BioReactant> reactants = new BioCollection<BioReactant>();
		
		reactants.addAll(this.getLeftReactants());
		reactants.addAll(this.getRightReactants());
		
		return reactants;
	}
	
	
	
	/**
	 * @param side : {@link Side}
	 * @return  {@link BioCollection} of {@link BioReactant}
	 */
	private BioCollection<BioReactant> getSideReactants(Side side) {
		
		BioCollection<BioReactant> reactantCollection;
		
		if(side == Side.LEFT) {
			reactantCollection = this.left;
		}
		else {
			reactantCollection = this.right;
		}
		
		return reactantCollection;
		
	}
	
	/**
	 * @param side : {@link Side}
	 * @return unmodifiable {@link BioCollection} of {@link BioPhysicalEntity} involved in reactants
	 */
	private BioCollection<BioPhysicalEntity> getSideEntities(Side side) {
		
		BioCollection<BioReactant> reactantCollection;
		
		if(side == Side.LEFT) {
			reactantCollection = this.left;
		}
		else {
			reactantCollection = this.right;
		}
		
		BioCollection<BioPhysicalEntity> entityCollection = new BioCollection<BioPhysicalEntity>();
		
		for(BioReactant reactant: reactantCollection)
		{
			entityCollection.add(reactant.getPhysicalEntity());
		}
		
		return entityCollection;
		
		
	}
	
	
	
	


}
