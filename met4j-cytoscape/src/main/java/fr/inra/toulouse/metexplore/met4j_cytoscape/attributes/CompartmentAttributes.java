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
package fr.inra.toulouse.metexplore.met4j_cytoscape.attributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * @author lcottret 22/12/2016
 *
 *         Get compartment attributes for metabolites or reactions
 *
 */
public class CompartmentAttributes extends GenericAttributeHandler {

	private Boolean compounds = true;

	/**
	 * 
	 * @param network
	 * @param sbmlCoded
	 * @param compounds
	 *            : if true, get the compound compartment, if false get the
	 *            reaction compartments
	 * @throws IOException
	 */
	public CompartmentAttributes(BioNetwork network, Boolean sbmlCoded, Boolean compounds) {

		super(network, sbmlCoded);

		this.compounds = compounds;

	}

	/**
	 * Get compound or reaction compartments
	 */
	@Override
	public HashMap<String, String> getAttributes() {

		return compounds ? this.getCompoundCompartment() : this.getReactionCompartments();

	}

	/**
	 * Get compound compartment
	 * 
	 * @return
	 */
	private HashMap<String, String> getCompoundCompartment() {

		HashMap<String, String> res = new HashMap<String, String>();

		HashMap<String, BioPhysicalEntity> cpds = this.getNetwork().getPhysicalEntityList();

		for (BioPhysicalEntity cpd : cpds.values()) {

			String cpt = StringUtils.getNotFormattedString(cpd.getCompartment().getName());

			String id = this.getSbmlCoded() ? StringUtils.sbmlEncode(cpd.getId()) : cpd.getId();

			res.put(id, cpt);

		}

		return res;

	}

	/**
	 * Get reaction compartment
	 * 
	 * @return
	 */
	private HashMap<String, String> getReactionCompartments() {
		HashMap<String, String> res = new HashMap<String, String>();

		HashMap<String, BioChemicalReaction> reactions = this.getNetwork().getBiochemicalReactionList();

		for (BioChemicalReaction rxn : reactions.values()) {

			TreeSet<String> compartmentIds = new TreeSet<String>();

			for (BioPhysicalEntityParticipant bpe : rxn.getLeftParticipantList().values()) {

				BioPhysicalEntity cpd = bpe.getPhysicalEntity();

				String compartmentId = "NA";

				if (cpd.getCompartment() != null) {

					compartmentId = cpd.getCompartment().getName();
				}

				compartmentIds.add(compartmentId);

			}

			for (BioPhysicalEntityParticipant bpe : rxn.getRightParticipantList().values()) {

				BioPhysicalEntity cpd = bpe.getPhysicalEntity();

				String compartmentId = "NA";

				if (cpd.getCompartment() != null) {

					compartmentId = cpd.getCompartment().getName();
				}

				compartmentIds.add(compartmentId);

			}

			String compartmentsStr = "(";

			int i = 0;

			for (String compartmentId : compartmentIds) {
				i++;

				if (i != 1) {
					compartmentsStr += "::";
				}

				compartmentsStr += compartmentId;

			}
			compartmentsStr += ")";

			String id = this.getSbmlCoded() ? StringUtils.sbmlEncode(rxn.getId()) : rxn.getId();

			res.put(id, compartmentsStr);
		}

		return res;
	}

}
