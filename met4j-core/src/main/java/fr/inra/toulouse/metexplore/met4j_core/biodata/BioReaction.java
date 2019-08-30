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
	private boolean reversible=true;

	private BioCollection<BioReactant> left = new BioCollection<BioReactant>();
	private BioCollection<BioReactant> right = new BioCollection<BioReactant>();

	private BioCollection<BioEnzyme> enzymes = new BioCollection<BioEnzyme>();

	public enum Side {
		LEFT, RIGHT
	};

	public BioReaction(String id) {
		super(id);
	}

	public BioReaction(String id, String name) {
		super(id, name);
	}

	/**
	 * 
	 */
	@Override
	public String toString() {

		String str = new String();

		String direction = " -> ";

		str = str.concat(this.getId() + ": ");

		ArrayList<String> lefts = new ArrayList<String>();

		for (BioReactant cpd : this.getLeftReactants()) {

			lefts.add(cpd.toString());
		}

		Collections.sort(lefts);

		String leftStr = String.join(" + ", lefts);

		if (this.isReversible()) {

			direction = " <-> ";
		}

		ArrayList<String> rights = new ArrayList<String>();

		for (BioReactant cpd : this.getRightReactants()) {

			rights.add(cpd.toString());
		}

		Collections.sort(rights);

		String rightStr = String.join(" + ", rights);

		str = str + leftStr + direction + rightStr;

		return str;

	}

	/**
	 * @return Returns the ecNumber.
	 */
	public String getEcNumber() {
		return ecNumber;
	}

	/**
	 * @param ecNumber The ecNumber to set.
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
	 * @return true if the reaction is a transport reaction, i.e a reaction that
	 *         involves several compartmens Limitation : this reaction is considered
	 *         as a transport reaction Ex : A_a + C_b -> D_a + B_b
	 * 
	 */
	public Boolean isTransportReaction() {

		HashSet<BioCompartment> compartments = new HashSet<BioCompartment>();

		for (BioReactant s : this.getReactants()) {
			compartments.add(s.getLocation());
		}

		if (compartments.size() > 1) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @param side : {@link Side}
	 * @return unmodifiable {@link BioCollection} of entities involved in left
	 *         {@link BioReactant}
	 */
	protected BioCollection<BioMetabolite> getLefts() {
		return getSideEntities(Side.LEFT);
	}

	/**
	 * @param side : {@link Side}
	 * @return unmodifiable {@link BioCollection} of entities involved in right
	 *         {@link BioReactant}
	 */
	protected BioCollection<BioMetabolite> getRights() {
		return getSideEntities(Side.RIGHT);
	}

	/**
	 * 
	 * @param side : {@link Side}
	 * @return {@link BioCollection} of left {@link BioReactant}
	 */
	protected BioCollection<BioReactant> getLeftReactants() {

		return getSideReactants(Side.LEFT);
	}

	/**
	 * 
	 * @param side : {@link Side}
	 * @return {@link BioCollection}of right {@link BioReactant}
	 */
	protected BioCollection<BioReactant> getRightReactants() {

		return getSideReactants(Side.RIGHT);
	}
	
	
	/**
	 * 
	 * @param side : {@link Side}
	 * @return {@link BioCollection} of left {@link BioReactant}
	 */
	public BioCollection<BioReactant> getLeftReactantsView() {

		return left.getView();
	}

	/**
	 * 
	 * @param side : {@link Side}
	 * @return {@link BioCollection}of right {@link BioReactant}
	 */
	public BioCollection<BioReactant> getRightReactantsView() {

		return right.getView();
	}
	

	/**
	 * 
	 * @return a {@link BioCollection} of {@link BioPhysicalEntity}
	 */
	protected BioCollection<BioMetabolite> getEntities() {

		BioCollection<BioReactant> reactants = this.getReactants();

		BioCollection<BioMetabolite> entities = new BioCollection<BioMetabolite>();

		for (BioReactant reactant : reactants) {
			BioMetabolite entity = reactant.getMetabolite();

			if (!entities.contains(entity)) {
				entities.add(entity);
			}
		}

		return entities;
	}

	/**
	 * @return a {@link BioCollection} of {@link BioReactant}
	 */
	protected BioCollection<BioReactant> getReactants() {

		BioCollection<BioReactant> reactants = new BioCollection<BioReactant>();
		try {
			reactants.addAll(this.getLeftReactants());
			reactants.addAll(this.getRightReactants());
		} catch (IllegalArgumentException e) {
		}

		return reactants;
	}

	/**
	 * @param side : {@link Side}
	 * @return {@link BioCollection} of {@link BioReactant}
	 */
	private BioCollection<BioReactant> getSideReactants(Side side) {

		BioCollection<BioReactant> reactantCollection;

		if (side == Side.LEFT) {
			reactantCollection = this.left;
		} else {
			reactantCollection = this.right;
		}

		return reactantCollection;

	}

	/**
	 * @param side : {@link Side}
	 * @return unmodifiable {@link BioCollection} of {@link BioPhysicalEntity}
	 *         involved in reactants
	 */
	private BioCollection<BioMetabolite> getSideEntities(Side side) {

		BioCollection<BioReactant> reactantCollection;

		if (side == Side.LEFT) {
			reactantCollection = this.left;
		} else {
			reactantCollection = this.right;
		}

		BioCollection<BioMetabolite> entityCollection = new BioCollection<BioMetabolite>();

		for (BioReactant reactant : reactantCollection) {
			entityCollection.add(reactant.getMetabolite());
		}

		return entityCollection;

	}

	/**
	 * Get the list of enzymes
	 */
	protected BioCollection<BioEnzyme> getEnzymes() {

		return enzymes;

	}
	
	/**
	 * Get the list of enzymes
	 */
	public BioCollection<BioEnzyme> getEnzymesView() {

		return enzymes.getView();

	}

	/**
	 * Add an enzyme that catalyses the reaction
	 */
	protected void addEnzyme(BioEnzyme e) {
		this.enzymes.add(e);
	}

	/**
	 * Removes an enzyme frome a reaction
	 */
	protected void removeEnzyme(BioEnzyme e) {
		this.enzymes.remove(e);
	}

	/**
	 * Remove a physical entity from a side of a reaction
	 */
	protected void removeSide(BioPhysicalEntity e, BioCompartment localisation, Side side) {

		BioCollection<BioReactant> reactants;
		if (side.equals(Side.LEFT)) {
			reactants = new BioCollection<BioReactant>(this.getLeftReactants());
		} else {
			reactants = new BioCollection<BioReactant>(this.getRightReactants());
		}

		for (BioReactant p : reactants) {
			if (p.getMetabolite().equals(e) && p.getLocation().equals(localisation)) {
				if (side.equals(Side.LEFT)) {
					this.getLeftReactants().remove(p);
				} else {
					this.getRightReactants().remove(p);
				}
			}
		}
	}

	/**
	 * Get list of genes
	 */
	protected BioCollection<BioGene> getGenes() {

		HashSet<BioGene> genes = new HashSet<BioGene>();
		this.getEnzymes().forEach(e -> {

			e.getParticipants().getView().forEach(p -> {
				if (p.getPhysicalEntity() instanceof BioProtein) {
					BioGene gene = ((BioProtein) p.getPhysicalEntity()).getGene();

					if (gene != null) {
						genes.add(((BioProtein) p.getPhysicalEntity()).getGene());
					}
				}
			});
		});

		return new BioCollection<BioGene>(genes);
	}

}