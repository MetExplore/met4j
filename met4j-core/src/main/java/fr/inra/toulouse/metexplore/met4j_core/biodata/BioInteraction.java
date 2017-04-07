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
import java.util.Iterator;

/**
 * An entity that defines a single biochemical interaction between two or more
 * entities. An interaction cannot be defined without the entities it relates.
 * Since it is a highly abstract class in the ontology, instances of the
 * interaction class should be created rarely.
 */

public abstract class BioInteraction extends BioEntity {
	
	private HashMap<String, BioParticipant> leftParticipantList = new HashMap<String, BioParticipant>();
	private HashMap<String, BioParticipant> rightParticipantList = new HashMap<String, BioParticipant>();

	public BioInteraction(BioInteraction in) {
		super(in);
		this.leftParticipantList = new HashMap<String, BioParticipant>();
		this.copyLeftParticipantList(in.getLeftParticipantList());
		this.rightParticipantList = new HashMap<String, BioParticipant>();
		this.copyRightParticipantList(in.getRightParticipantList());
	}


	/**
	 * @return the leftList
	 */
	public HashMap<String, BioPhysicalEntity> getLeftList() {
		HashMap<String, BioPhysicalEntity> list = new HashMap<String, BioPhysicalEntity>();

		for (BioParticipant bpe : this.getLeftParticipantList()
				.values()) {
			list.put(bpe.getPhysicalEntity().getId(), bpe.getPhysicalEntity());
		}

		return list;
	}

	/**
	 * @return the leftParticipantList
	 */
	public HashMap<String, BioParticipant> getLeftParticipantList() {
		return leftParticipantList;
	}


	/**
	 * @param leftParticipantList the leftParticipantList to set
	 */
	public void setLeftParticipantList(HashMap<String, BioParticipant> leftParticipantList) {
		this.leftParticipantList = leftParticipantList;
	}


	/**
	 * @return the rightParticipantList
	 */
	public HashMap<String, BioParticipant> getRightParticipantList() {
		return rightParticipantList;
	}


	/**
	 * @param rightParticipantList the rightParticipantList to set
	 */
	public void setRightParticipantList(HashMap<String, BioParticipant> rightParticipantList) {
		this.rightParticipantList = rightParticipantList;
	}


	/**
	 * Remove a cpd from the list of left compounds
	 * 
	 * @param cpd
	 */
	public void removeLeftCpd(BioPhysicalEntity cpd) {
		this.leftParticipantList.remove(cpd.getId());
	}

	/**
	 * Remove a cpd from the list of right compounds
	 * 
	 * @param cpd
	 */
	public void removeRightCpd(BioPhysicalEntity cpd) {
		this.rightParticipantList.remove(cpd.getId());
	}

	/**
	 * @return the rightList
	 */
	public HashMap<String, BioPhysicalEntity> getRightList() {
		HashMap<String, BioPhysicalEntity> list = new HashMap<String, BioPhysicalEntity>();

		for (BioParticipant bpe : this.getRightParticipantList()
				.values()) {
			list.put(bpe.getPhysicalEntity().getId(), bpe.getPhysicalEntity());
		}

		return list;
	}
	
	
}
