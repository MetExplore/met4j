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

package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.Flux;


/**
 * This Class represents a new type of network that handles the SBML fbc
 * package. It uses the {@link BioNetwork} class to handle classical network
 * data.</br> It also adds new types of 'ListOf' to store the new flux data.
 * 
 * @author Benjamin
 * @since 3.0
 */
public class FluxNetwork {
	/**
	 * The actual {@link BioNetwork} that is behind this {@link FluxNetwork}
	 */
	private BioNetwork underlyingBionet;

	/**
	 * The list of possible flux objectives defined in this model
	 * 
	 * @see BioObjective
	 */
	private HashMap<String, BioObjective> listOfObjectives = new HashMap<String, BioObjective>();
	/**
	 * This required attribute exists so that when multiple {@link BioObjective}
	 * are included in a single model, the model will always be well described
	 * i.e., there is a single, primary objective function which defines a
	 * single optimum and its associated solution space. </br></br>From the SBML
	 * FBC2 specification
	 */
	private BioObjective activeObjective;
	/**
	 * A strict model is fully described and mathematically consistent, for
	 * example, it ensures that all fluxes have a valid upper or lower bound.
	 * </br></br>From the SBML FBC2 specification
	 */
	private boolean fbcStrict = true;
	/**
	 * This List stores the list of flux bounds defined in the model.
	 */
	private HashMap<String, Flux> listOfFluxBounds = new HashMap<String, Flux>();
	/**
	 * This list stores the list of {@link FluxReaction} present in the network
	 */
	private HashMap<String, FluxReaction> listOfFluxReactions = new HashMap<String, FluxReaction>();

	/**
	 * Constructor using a {@link BioNetwork}
	 * 
	 * @param bionet
	 *            The underlying {@link BioNetwork}
	 */
	public FluxNetwork(BioNetwork bionet) {
		this.underlyingBionet = bionet;
	}

	/**
	 * retrieves the {@link #underlyingBionet}
	 * 
	 * @return the {@link #underlyingBionet}
	 */
	public BioNetwork getUnderlyingBionet() {
		return underlyingBionet;
	}

	/**
	 * Set the {@link #underlyingBionet}
	 * 
	 * @param underlyingBionet
	 *            the new {@link #underlyingBionet}
	 */
	public void setUnderlyingBionet(BioNetwork underlyingBionet) {
		this.underlyingBionet = underlyingBionet;
	}

	/**
	 * Retrieves the {@link #listOfObjectives}
	 * 
	 * @return the {@link #listOfObjectives}
	 * 
	 */
	public HashMap<String, BioObjective> getListOfObjectives() {
		return this.listOfObjectives;
	}

	/**
	 * Set the {@link #listOfObjectives}
	 * 
	 * @param listOfObjectives
	 *            the new {@link #listOfObjectives}
	 */
	public void setListOfObjectives(HashMap<String, BioObjective> listOfObjectives) {
		this.listOfObjectives = listOfObjectives;
	}

	/**
	 * Add an {@link BioObjective} to the list
	 * 
	 * @param objective
	 *            the {@link BioObjective} to add
	 */
	public void addObjective(BioObjective objective) {
		this.listOfObjectives.put(objective.getId(), objective);
	}

	/**
	 * Get the Active objective, {@link #activeObjective}
	 * 
	 * @return the {@link #activeObjective}
	 */
	public BioObjective getActiveObjective() {
		return this.activeObjective;
	}

	/**
	 * Sets the active (ie primary) flux objective of this flux network. if this
	 * objectives is not present in the network's list of objectives, this
	 * method also adds it to the list.
	 * 
	 * @param activeObjective
	 *            the {@link BioObjective} to set as Active
	 */
	public void setActiveObjective(BioObjective activeObjective) {
		if (!this.getListOfObjectives().containsKey(activeObjective.getId())) {
			this.addObjective(activeObjective);
		}
		this.activeObjective = activeObjective;
	}

	/**
	 * Set the {@link #activeObjective} to the {@link BioObjective} which has the
	 * given Id
	 * 
	 * @param objectiveID
	 *            the ID of the {@link BioObjective} to set as active
	 */
	public void setActiveObjective(String objectiveID) {
		if (this.getListOfObjectives().containsKey(objectiveID)) {

			this.setActiveObjective(this.getListOfObjectives().get(objectiveID));
		}

	}

	/**
	 * Get the value of {@link #fbcStrict}
	 * 
	 * @return {@link #fbcStrict}
	 */
	public boolean getFbcStrict() {
		return this.fbcStrict;
	}

	/**
	 * Set the value of {@link #fbcStrict}
	 * 
	 * @param fbcStrict
	 *            The new value
	 */
	public void setFbcStrict(boolean fbcStrict) {
		this.fbcStrict = fbcStrict;
	}

	/**
	 * get the list of flux bounds, {@link #listOfFluxBounds}
	 * 
	 * @return {@link #listOfFluxBounds}
	 */
	public HashMap<String, Flux> getListOfFluxBounds() {
		return this.listOfFluxBounds;
	}

	/**
	 * Set the {@link #listOfFluxBounds} to a new value
	 * 
	 * @param listOfFluxBounds
	 *            the new {@link #listOfFluxBounds}
	 */
	public void setListOfFluxBounds(HashMap<String, Flux> listOfFluxBounds) {
		this.listOfFluxBounds = listOfFluxBounds;
	}

	/**
	 * Add a new flux bound to {@link #listOfFluxBounds}
	 * 
	 * @param flx
	 *            the flux to add
	 */
	public void addFluxBound(Flux flx) {
		this.getListOfFluxBounds().put(flx.getId(), flx);
	}

	/**
	 * Get the {@link #listOfFluxReactions}
	 * 
	 * @return the {@link #listOfFluxReactions}
	 */
	public HashMap<String, FluxReaction> getListOfFluxReactions() {
		return listOfFluxReactions;
	}

	/**
	 * Set the {@link #listOfFluxReactions} to a new value
	 * 
	 * @param listOfFluxReactions
	 *            the new {@link #listOfFluxReactions}
	 */
	public void setListOfFluxReactions(
			HashMap<String, FluxReaction> listOfFluxReactions) {
		this.listOfFluxReactions = listOfFluxReactions;
	}

}