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
import java.util.HashMap;

/**
 * A set or series of interactions, often forming a network, which biologists
 * have found useful to group together for organizational, historic, biophysical
 * or other reasons.
 */

public class BioPathway extends BioEntity {
	
	private HashMap<String, BioPathway> superPathways = new HashMap<String, BioPathway> ();
	private HashMap<String, BioPathway>  subPathways = new HashMap<String, BioPathway> ();
	
	private ArrayList<String> primaryCompounds = new ArrayList<String>();
		
	private HashMap<String, BioInteraction> listOfInteractions = new HashMap<String, BioInteraction>();
	
	private HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>();
	
	public BioPathway(String id) {
		super(id);
	}
	
	public BioPathway(String id, String name) {
		super(id, name);
	}
	
	public BioPathway(BioPathway in) {
		super(in);
		this.copySuperPathways(in.getSuperPathways());
		this.copySubPathways(in.getSubPathways());
		this.setPrimaryCompounds(new ArrayList<String>());
		this.getPrimaryCompounds().addAll(in.getPrimaryCompounds());
	}
	

	/**
	 * @return Returns the subPathways.
	 */
	public HashMap<String, BioPathway> getSubPathways() {
		return subPathways;
	}

	/**
	 * @param subPathways The subPathways to set.
	 */
	public void setSubPathways(HashMap<String, BioPathway>  subPathways) {
		this.subPathways = subPathways;
	}
	
	public void copySubPathways(HashMap<String, BioPathway> subPathways) {
		
		this.setSubPathways(new HashMap<String, BioPathway>());
		
		for(BioPathway subPathway : subPathways.values()) {
			BioPathway newSubPathway = new BioPathway(subPathway);
			this.addSubPathway(newSubPathway);
		}
		
	}
	
	
	/**
	 * Adds a sub Pathway in the list
	 */
	public void addSubPathway(BioPathway pathway) {
		this.subPathways.put(pathway.getId(), pathway);
	}

	/**
	 * @return Returns the superPathways.
	 */
	public HashMap<String, BioPathway> getSuperPathways() {
		return superPathways;
	}

	/**
	 * @param superPathways The superPathways to set.
	 */
	public void setSuperPathways(HashMap<String, BioPathway>  superPathways) {
		this.superPathways = superPathways;
	}
	
	public void copySuperPathways(HashMap<String, BioPathway> superPathways) {
		
		this.setSuperPathways(new HashMap<String, BioPathway>());
		
		for(BioPathway superPathway : superPathways.values()) {
			BioPathway newSuperPathway = new BioPathway(superPathway);
			this.addSuperPathway(newSuperPathway);
		}
		
	}
	
	
	/**
	 * Adds a super Pathway in the list
	 */
	public void addSuperPathway(BioPathway pathway) {
		this.superPathways.put(pathway.getId(), pathway);
	}

	/**
	 * @return Returns the primaryCompounds.
	 */
	public ArrayList<String> getPrimaryCompounds() {
		return primaryCompounds;
	}

	/**
	 * @param primaryCompounds The primaryCompounds to set.
	 */
	public void setPrimaryCompounds(ArrayList<String> primaryCompounds) {
		this.primaryCompounds = primaryCompounds;
	}
	
	/**
	 * 
	 */
	public void addPrimaryCompound(String primaryCompound) {
		if(! primaryCompounds.contains(primaryCompound)) {
			primaryCompounds.add(primaryCompound);
		}
	}

	public HashMap<String, BioInteraction> getListOfInteractions() {
		return listOfInteractions;
	}

	public void setListOfInteractions(
			HashMap<String, BioInteraction> listOfInteractions) {
		this.listOfInteractions = listOfInteractions;
	}

	public HashMap<String, BioReaction> getReactions() {
		return reactions;
	}

	public void setReactions(HashMap<String, BioReaction> reactions) {
		this.reactions = reactions;
	}
	
	public void addReaction(BioReaction reaction) {
		
		this.reactions.put(reaction.getId(), reaction);
		
	}
	
	


}
