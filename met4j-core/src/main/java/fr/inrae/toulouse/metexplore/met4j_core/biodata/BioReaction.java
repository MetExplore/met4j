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
package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import java.util.HashSet;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import lombok.NonNull;

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
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioReaction extends BioEntity {

	private boolean spontaneous = false;
	private String ecNumber;
	private boolean reversible=true;

	final private BioCollection<BioReactant> left;
	final private BioCollection<BioReactant> right;

	final private BioCollection<BioEnzyme> enzymes;

	public enum Side {
		LEFT, RIGHT
	}

	/**
	 * <p>Constructor for BioReaction.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 */
	public BioReaction(String id) {
		this(id, id);
	}

	/**
	 * <p>Constructor for BioReaction.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public BioReaction(String id, String name) {
		super(id, name);
		left = new BioCollection<>();
		right = new BioCollection<>();
		enzymes = new BioCollection<>();
	}

	/**
	 * <p>Copy of a BioReaction (only atomic attributes, not left nor right).</p>
	 *
	 * @param reaction a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 */
	public BioReaction(@NonNull BioReaction reaction) {
		super(reaction);
		left = new BioCollection<>();
		right = new BioCollection<>();
		enzymes = new BioCollection<>();
		this.spontaneous = reaction.spontaneous;
		this.ecNumber = reaction.ecNumber;
		this.reversible = reaction.reversible;
	}

	/**
	 * <p>Copy of a BioReaction (only atomic attributes, not left nor right) with new id</p>
	 *
	 * @param reaction a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 */
	public BioReaction(@NonNull BioReaction reaction, String id) {
		super(reaction, id);
		left = new BioCollection<>();
		right = new BioCollection<>();
		enzymes = new BioCollection<>();
		this.spontaneous = reaction.spontaneous;
		this.ecNumber = reaction.ecNumber;
		this.reversible = reaction.reversible;
	}


	/**
	 * <p>Getter for the field <code>ecNumber</code>.</p>
	 *
	 * @return Returns the ecNumber.
	 */
	public String getEcNumber() {
		return ecNumber;
	}

	/**
	 * <p>Setter for the field <code>ecNumber</code>.</p>
	 *
	 * @param ecNumber The ecNumber to set.
	 */
	public void setEcNumber(String ecNumber) {
		this.ecNumber = ecNumber;
	}

	/**
	 * <p>isReversible.</p>
	 *
	 * @return true if the reaction is reversible
	 */
	public Boolean isReversible() {
		return reversible;
	}

	/**
	 * <p>isSpontaneous.</p>
	 *
	 * @return true if the reaction is spontaneous
	 */
	public boolean isSpontaneous() {
		return spontaneous;
	}

	/**
	 * <p>Setter for the field <code>spontaneous</code>.</p>
	 *
	 * @param spontaneous the spontaneous to set
	 */
	public void setSpontaneous(boolean spontaneous) {
		this.spontaneous = spontaneous;
	}

	/**
	 * <p>Setter for the field <code>reversible</code>.</p>
	 *
	 * @param reversible the reversible to set
	 */
	public void setReversible(boolean reversible) {
		this.reversible = reversible;
	}

	/**
	 * <p>isTransportReaction.</p>
	 *
	 * @return true if the reaction is a transport reaction, i.e a reaction that
	 *         involves several compartmens Limitation : this reaction is considered
	 *         as a transport reaction Ex : A_a + C_b -&gt; D_a + B_b
	 */
	public Boolean isTransportReaction() {

		HashSet<BioCompartment> compartments = new HashSet<>();

		for (BioReactant s : this.getReactantsView()) {
			compartments.add(s.getLocation());
		}

		return compartments.size() > 1;

	}

	/**
	 * <p>getLeftsView.</p>
	 *
	 * @return unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of entities involved in left
	 *         {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
	 */
	public BioCollection<BioMetabolite> getLeftsView() {
		return getSideEntities(Side.LEFT);
	}

	/**
	 * <p>getRightsView.</p>
	 *
	 * @return unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of entities involved in right
	 *         {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
	 */
	public BioCollection<BioMetabolite> getRightsView() {
		return getSideEntities(Side.RIGHT);
	}

	/**
	 * <p>getLeftReactants.</p>
	 *
	 * @return {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of left {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
	 */
	protected BioCollection<BioReactant> getLeftReactants() {
		return getSideReactants(Side.LEFT);
	}

	/**
	 * <p>getRightReactants.</p>
	 *
	 * @return {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection}of right {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
	 */
	protected BioCollection<BioReactant> getRightReactants() {

		return getSideReactants(Side.RIGHT);
	}
	
	
	/**
	 * <p>getLeftReactantsView.</p>
	 *
	 * @return {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of left {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
	 */
	public BioCollection<BioReactant> getLeftReactantsView() {

		return left.getView();
	}

	/**
	 * <p>getRightReactantsView.</p>
	 *
	 * @return {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection}of right {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
	 */
	public BioCollection<BioReactant> getRightReactantsView() {

		return right.getView();
	}
	

	/**
	 * <p>getEntities.</p>
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity}
	 */
	protected BioCollection<BioMetabolite> getEntities() {

		BioCollection<BioReactant> reactants = this.getReactantsView();

		BioCollection<BioMetabolite> entities = new BioCollection<>();

		for (BioReactant reactant : reactants) {
			BioMetabolite entity = reactant.getMetabolite();

			if (!entities.contains(entity)) {
				entities.add(entity);
			}
		}

		return entities;
	}

	/**
	 * <p>getReactantsView.</p>
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
	 */
	public BioCollection<BioReactant> getReactantsView() {

		BioCollection<BioReactant> reactants = new BioCollection<>();

		reactants.addAll(this.getLeftReactants());
		reactants.addAll(this.getRightReactants());

		return reactants;
	}

	/**
	 * @param side : {@link Side} the side of the reaction
	 * @return {@link BioCollection} of {@link BioReactant}
	 */
	private BioCollection<BioReactant> getSideReactants(@NonNull Side side) {

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

		BioCollection<BioMetabolite> entityCollection = new BioCollection<>();

		for (BioReactant reactant : reactantCollection) {
			entityCollection.add(reactant.getMetabolite());
		}

		return entityCollection;

	}

	/**
	 * Get the list of enzymes
	 *
	 * @return  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
	 */
	protected BioCollection<BioEnzyme> getEnzymes() {

		return enzymes;

	}
	
	/**
	 * Get an unmodifiable copy of the collection of enzymes
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
	 */
	public BioCollection<BioEnzyme> getEnzymesView() {

		return enzymes.getView();

	}

	/**
	 * Add an enzyme that catalyses the reaction
	 *
	 * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
	 */
	protected void addEnzyme(@NonNull BioEnzyme e) {
		this.enzymes.add(e);
	}

	/**
	 * Removes an enzyme frome a reaction
	 *
	 * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
	 */
	protected void removeEnzyme(@NonNull BioEnzyme e) {
		this.enzymes.remove(e);
	}

	//Remove a physical entity from a side of a reaction
	/**
	 * <p>removeSide.</p>
	 *
	 * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity} object.
	 * @param localisation a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @param side a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction.Side} object.
	 */
	protected void removeSide(@NonNull BioPhysicalEntity e, @NonNull BioCompartment localisation, @NonNull Side side) {

		BioCollection<BioReactant> reactants;
		if (side.equals(Side.LEFT)) {
			reactants = new BioCollection<>(this.getLeftReactants());
		} else {
			reactants = new BioCollection<>(this.getRightReactants());
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

	//Get list of genes
	/**
	 * <p>getGenes.</p>
	 *
	 * Get the list of the genes associated to the reactions.
	 * Be careful, modifying the BioCollection returned by this method won't affect the reaction
	 *
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} object.
	 */
	protected BioCollection<BioGene> getGenes() {

		HashSet<BioGene> genes = new HashSet<>();
		this.getEnzymes().forEach(e -> e.getParticipants().getView().forEach(p -> {
			if (p.getPhysicalEntity() instanceof BioProtein) {
				BioGene gene = ((BioProtein) p.getPhysicalEntity()).getGene();

				if (gene != null) {
					genes.add(((BioProtein) p.getPhysicalEntity()).getGene());
				}
			}
		}));

		return new BioCollection<>(genes);
	}

	/**
	 * <p>getMetabolitesView</p>
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection}
	 * of the {@link BioMetabolite} involved in the reaction
	 */
	public BioCollection<BioMetabolite> getMetabolitesView() {
		return this.getReactantsView().stream()
				.map(BioReactant::getMetabolite)
				.collect(BioCollection::new, BioCollection::add, BioCollection::addAll);
	}

}
