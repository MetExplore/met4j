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
/**
 * 
 */
package fr.inra.toulouse.metexplore.met4j_cytoscape.attributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;

/**
 * @author ludo
 *
 */
public class PathwayAttributes extends GenericAttributeHandler {

	private Boolean compounds = true;

	/**
	 * 
	 * @param network
	 * @param sbmlCoded
	 * @param compounds : if true, get the compound pathways, if false get the reaction pathways
	 * @throws IOException
	 */
	public PathwayAttributes(BioNetwork network, Boolean sbmlCoded, Boolean compounds) {
		super(network, sbmlCoded);

		this.compounds = compounds;
	}

	/**
	 * Get compound or reaction pathways
	 */
	@Override
	public HashMap<String, String> getAttributes() {

		return compounds ? this.getCompoundPathways() : this.getReactionPathways();

	}

	/**
	 * Get compound pathway names
	 * 
	 * @return
	 */
	private HashMap<String, String> getCompoundPathways() {

		HashMap<String, String> res = new HashMap<String, String>();

		for (BioPhysicalEntity cpd : this.getNetwork().getPhysicalEntityList().values()) {

			HashMap<String, BioReaction> reactionsP = cpd.getReactionsAsProduct();
			HashMap<String, BioReaction> reactionsS = cpd.getReactionsAsSubstrate();

			HashMap<String, BioReaction> reactionsT = reactionsP;
			reactionsT.putAll(reactionsS);

			Set<String> pathways = new HashSet<String>();

			for (BioReaction reaction : reactionsT.values()) {

				HashMap<String, BioPathway> BioPathways = reaction.getPathwayList();

				for (BioPathway pathway : BioPathways.values()) {
					pathways.add(StringUtils.getNotFormattedString(pathway.getName()));
				}
			}

			ArrayList<String> sortedPathways = new ArrayList<String>(pathways);

			Collections.sort(sortedPathways);

			String attribute = "(";

			int i = 0;

			for (String key : sortedPathways) {

				i++;

				if (i != 1) {
					attribute += "::";
				}

				attribute += key;

			}

			attribute += ")";

			String id = this.getSbmlCoded() ? StringUtils.sbmlEncode(cpd.getId()) : cpd.getId();

			res.put(id, attribute);
		}

		return res;

	}

	/**
	 * 
	 * Get reaction pathways
	 * 
	 * @return
	 */
	private HashMap<String, String> getReactionPathways() {

		HashMap<String, String> res = new HashMap<String, String>();

		HashMap<String, BioReaction> reactions = this.getNetwork().getBiochemicalReactionList();

		for (BioReaction rxn : reactions.values()) {

			HashMap<String, BioPathway> pathways = rxn.getPathwayList();

			Set<String> pathwayNames = new HashSet<String>();

			for (BioPathway pathway : pathways.values()) {

				pathwayNames.add(StringUtils.getNotFormattedString(pathway.getName()));

			}

			ArrayList<String> sortedPathways = new ArrayList<String>(pathwayNames);

			Collections.sort(sortedPathways);

			String attribute = "(";

			int i = 0;

			for (String key : sortedPathways) {

				i++;

				if (i != 1) {
					attribute += "::";
				}

				attribute += key;

			}

			attribute += ")";

			String id = this.getSbmlCoded() ? StringUtils.sbmlEncode(rxn.getId()) : rxn.getId();

			res.put(id, attribute);

		}

		return res;

	}

}
