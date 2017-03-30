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
 * An interaction in which one or more entities is physically transformed into
 * one or more other entities. This class is designed to represent a simple,
 * single-step transformation. Multi-step transformations, such as the
 * conversion of glucose to pyruvate in the glycolysis pathway, should be
 * represented as pathways, if known. Since it is a highly abstract class in the
 * ontology, instances of the conversion class should be created rarely, if
 * ever.
 */

public class BioConversion extends BioInteraction {

	public HashMap<String, BioParticipant> leftParticipantList = new HashMap<String, BioParticipant>();
	public HashMap<String, BioParticipant> rightParticipantList = new HashMap<String, BioParticipant>();

	private String spontaneous = null;

	public HashMap<String, BioParticipant> primaryLeftParticipantList = new HashMap<String, BioParticipant>();
	public HashMap<String, BioParticipant> primaryRightParticipantList = new HashMap<String, BioParticipant>();

	public HashMap<String, BioPhysicalEntity> primaryLeftList = new HashMap<String, BioPhysicalEntity>();
	public HashMap<String, BioPhysicalEntity> primaryRightList = new HashMap<String, BioPhysicalEntity>();

	private Boolean doesItContainClassCpd = null;
	private Boolean doesItContainClassPrimaryCpd = null;

	public BioConversion(String id) {
		super(id);
	}

	public BioConversion(String id, String name) {
		super(id, name);
	}

	public BioConversion(BioConversion in) {
		super(in);
		this.leftParticipantList = new HashMap<String, BioParticipant>();
		this.copyLeftParticipantList(in.getLeftParticipantList());
		this.rightParticipantList = new HashMap<String, BioParticipant>();
		this.copyRightParticipantList(in.getRightParticipantList());
		this.setSpontaneous(in.getSpontaneous());
		this.primaryLeftParticipantList = new HashMap<String, BioParticipant>();
		this.copyPrimaryLeftParticipantList(in.getLeftParticipantList());
		this.primaryRightParticipantList = new HashMap<String, BioParticipant>();
		this.copyPrimaryRightParticipantList(in.getLeftParticipantList());
		this.setDoesItContainClassCpd(in.getDoesItContainClassCpd());
		this.setDoesItContainClassPrimaryCpd(in
				.getDoesItContainClassPrimaryCpd());

	}

	/**
	 * @return Returns the spontaneous.
	 */
	public String getSpontaneous() {
		return spontaneous;
	}

	/**
	 * @return Returns the leftList.
	 */
	public HashMap<String, BioParticipant> getLeftParticipantList() {
		return leftParticipantList;
	}

	/**
	 * @param leftList
	 *            The leftList to set.
	 */
	public void setLeftParticipantList(
			HashMap<String, BioParticipant> leftList) {

		this.leftParticipantList = new HashMap<String, BioParticipant>();

		for (Iterator<BioParticipant> iter = leftList.values()
				.iterator(); iter.hasNext();) {
			this.addLeftParticipant(iter.next());
		}

	}

	public void copyLeftParticipantList(
			HashMap<String, BioParticipant> leftList) {

		for (BioParticipant bpe : leftList.values()) {
			BioParticipant newBpe = new BioParticipant(
					bpe);
			this.addLeftParticipant(newBpe);
		}
	}

	/**
	 * @return Returns the rightList.
	 */
	public HashMap<String, BioParticipant> getRightParticipantList() {
		return rightParticipantList;
	}

	/**
	 * @param rightList
	 *            The rightList to set.
	 */
	public void setRightParticipantList(
			HashMap<String, BioParticipant> rightList) {

		this.rightParticipantList = new HashMap<String, BioParticipant>();

		for (Iterator<BioParticipant> iter = rightList.values()
				.iterator(); iter.hasNext();) {
			this.addRightParticipant(iter.next());
		}
	}

	public void copyRightParticipantList(
			HashMap<String, BioParticipant> rightList) {

		for (BioParticipant bpe : rightList.values()) {
			BioParticipant newBpe = new BioParticipant(
					bpe);
			this.addRightParticipant(newBpe);
		}
	}

	/**
	 * @param spontaneous
	 *            The spontaneous to set.
	 */
	public void setSpontaneous(String spontaneous) {
		this.spontaneous = spontaneous;
	}

	/**
	 * @param p
	 *            The participant to add in the list of the participants
	 */
	public void addLeftParticipant(BioParticipant p) {
		this.leftParticipantList.put(p.getId(), p);
		this.getParticipantList().put(p.getId(), p);
	}

	/**
	 * @param p
	 *            The participant to add in the list of the participants
	 */
	public void addRightParticipant(BioParticipant p) {
		this.rightParticipantList.put(p.getId(), p);
		this.getParticipantList().put(p.getId(), p);
	}

	/**
	 * @return Returns the primaryLeftList.
	 */
	public HashMap<String, BioParticipant> getPrimaryLeftParticipantList() {
		return primaryLeftParticipantList;
	}

	public void copyPrimaryLeftParticipantList(
			HashMap<String, BioParticipant> leftList) {

		for (BioParticipant bpe : leftList.values()) {
			BioParticipant newBpe = new BioParticipant(
					bpe);
			this.getPrimaryLeftParticipantList().put(newBpe.getId(), newBpe);
		}
	}

	/**
	 * @return Returns the primaryRightList.
	 */
	public HashMap<String, BioParticipant> getPrimaryRightParticipantList() {
		return primaryRightParticipantList;
	}

	public void copyPrimaryRightParticipantList(
			HashMap<String, BioParticipant> rightList) {

		for (BioParticipant bpe : rightList.values()) {
			BioParticipant newBpe = new BioParticipant(
					bpe);
			this.getPrimaryRightParticipantList().put(newBpe.getId(), newBpe);
		}
	}

	/**
	 * @return Returns the doesItContainClassCpd.
	 */
	public Boolean getDoesItContainClassCpd() {
		if (this.doesItContainClassCpd == null)
			setDoesItContainClassCpd();
		return doesItContainClassCpd;
	}

	/**
	 * @param doesItContainClassCpd
	 *            The doesItContainClassCpd to set.
	 */
	public void setDoesItContainClassCpd() {

		ArrayList<BioParticipant> leftBPEP = new ArrayList<BioParticipant>(
				getLeftParticipantList().values());
		for (int i = 0; i < leftBPEP.size(); i++) {
			BioPhysicalEntity cpd = leftBPEP.get(i).getPhysicalEntity();

			if (cpd.getIsHolderClass() == true) {
				this.doesItContainClassCpd = true;
				return;
			}
		}

		ArrayList<BioParticipant> rightBPEP = new ArrayList<BioParticipant>(
				getRightParticipantList().values());
		for (int i = 0; i < rightBPEP.size(); i++) {
			BioPhysicalEntity cpd = rightBPEP.get(i).getPhysicalEntity();

			if (cpd.getIsHolderClass() == true) {
				this.doesItContainClassCpd = true;
				return;
			}
		}

		this.doesItContainClassCpd = false;

	}

	public void setDoesItContainClassCpd(Boolean flag) {
		this.doesItContainClassCpd = flag;
	}

	/**
	 * @return Returns the doesItContainClassPrimaryCpd.
	 */
	public Boolean getDoesItContainClassPrimaryCpd() {
		if (this.doesItContainClassPrimaryCpd == null) {
			setDoesItContainClassPrimaryCpd();
		}
		return doesItContainClassPrimaryCpd;
	}

	/**
	 * @param doesItContainClassPrimaryCpd
	 *            The doesItContainClassPrimaryCpd to set.
	 */
	public void setDoesItContainClassPrimaryCpd() {

		if (doesItContainClassCpd != null && doesItContainClassCpd == false) {
			doesItContainClassPrimaryCpd = false;
			return;
		}

		ArrayList<BioParticipant> listPL = new ArrayList<BioParticipant>(
				primaryLeftParticipantList.values());
		ArrayList<BioParticipant> listPR = new ArrayList<BioParticipant>(
				primaryRightParticipantList.values());

		for (int i = 0; i < listPL.size(); i++) {
			BioPhysicalEntity cpd = listPL.get(i).getPhysicalEntity();
			if (cpd.getIsHolderClass() == true) {
				doesItContainClassPrimaryCpd = true;
				return;
			}
		}

		for (int i = 0; i < listPR.size(); i++) {
			BioPhysicalEntity cpd = listPR.get(i).getPhysicalEntity();
			if (cpd.getIsHolderClass() == true) {
				doesItContainClassPrimaryCpd = true;
				return;
			}
		}

		this.doesItContainClassPrimaryCpd = false;

	}

	public void setDoesItContainClassPrimaryCpd(Boolean flag) {
		this.doesItContainClassPrimaryCpd = flag;
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
	 * Remove a cpd from the list of left compounds
	 * 
	 * @param cpd
	 */
	public void removeLeftCpd(BioPhysicalEntity cpd) {

		this.getPrimaryLeftList().remove(cpd.getId());

		HashMap<String, BioParticipant> tmp = new HashMap<String, BioParticipant>(
				this.getLeftParticipantList());

		for (BioParticipant bpe : tmp.values()) {
			if (bpe.getPhysicalEntity().getId().compareTo(cpd.getId()) == 0) {
				this.getLeftParticipantList().remove(bpe.getId());
				this.getPrimaryLeftParticipantList().remove(bpe.getId());
			}
		}
	}

	/**
	 * Remove a cpd from the list of right compounds
	 * 
	 * @param cpd
	 */
	public void removeRightCpd(BioPhysicalEntity cpd) {

		this.getPrimaryRightList().remove(cpd.getId());

		HashMap<String, BioParticipant> tmp = new HashMap<String, BioParticipant>(
				this.getRightParticipantList());

		for (BioParticipant bpe : tmp.values()) {
			if (bpe.getPhysicalEntity().getId().compareTo(cpd.getId()) == 0) {
				this.getRightParticipantList().remove(bpe.getId());
				this.getPrimaryRightParticipantList().remove(bpe.getId());
			}
		}
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

	/**
	 * @return the primaryLeftList
	 */
	public HashMap<String, BioPhysicalEntity> getPrimaryLeftList() {
		if (primaryLeftList.size() == 0) {
			for (Iterator<String> iter = this.getPrimaryLeftParticipantList().keySet()
					.iterator(); iter.hasNext();) {
				BioParticipant bpe = this
						.getPrimaryLeftParticipantList().get(iter.next());
				primaryLeftList.put(bpe.getPhysicalEntity().getId(),
						bpe.getPhysicalEntity());
			}
		}
		return primaryLeftList;
	}

	/**
	 * @return the primaryRightList
	 */
	public HashMap<String, BioPhysicalEntity> getPrimaryRightList() {
		if (primaryRightList.size() == 0) {
			for (Iterator<String> iter = this.getPrimaryRightParticipantList().keySet()
					.iterator(); iter.hasNext();) {
				BioParticipant bpe = this
						.getPrimaryRightParticipantList().get(iter.next());
				primaryRightList.put(bpe.getPhysicalEntity().getId(),
						bpe.getPhysicalEntity());
			}
		}
		return primaryRightList;
	}

}
