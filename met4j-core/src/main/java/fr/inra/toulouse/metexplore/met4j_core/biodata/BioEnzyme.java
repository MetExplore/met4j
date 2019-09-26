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

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

public class BioEnzyme extends BioPhysicalEntity {

	private BioCollection<BioEnzymeParticipant> participants;

	public BioEnzyme(String id) {
		super(id);

		participants = new BioCollection<>();
	}
	
	public BioEnzyme(String id, String name) {
		super(id, name);

		participants = new BioCollection<>();
	}

	/**
	 * @return the participants
	 */
	protected BioCollection<BioEnzymeParticipant> getParticipants() {
		return participants;
	}
	
	public BioCollection<BioEnzymeParticipant> getParticipantsView() {
		return participants.getView();
	}

	/**
	 * @param participants the participants to set
	 */
	protected void setParticipants(BioCollection<BioEnzymeParticipant> participants) {
		this.participants = participants;
	}

	protected void addParticipant(BioEnzymeParticipant participant)
	{
		this.participants.add(participant);
	}

	/**
	 * Remove a participant from ifs physical entity
	 */
	protected void removeParticipant(BioPhysicalEntity e) 
	{

		BioCollection<BioEnzymeParticipant> tmp = new BioCollection<>(this.participants);

		for(BioEnzymeParticipant p : tmp)
		{
			if(p.getPhysicalEntity().equals(e)) 
			{
				this.participants.remove(p);
				return;
			}
		}
	}
	




	
	
	

}
