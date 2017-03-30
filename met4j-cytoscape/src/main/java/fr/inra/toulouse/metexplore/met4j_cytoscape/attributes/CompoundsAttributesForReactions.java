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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * @author lcottret
 * 
 *         Creates a hashmap with for each reaction the list of substrates or
 *         products separated by "::"
 * 
 *
 */
public class CompoundsAttributesForReactions extends GenericAttributeHandler {

	private Boolean substrate;

	/**
	 * 
	 * @param network
	 *            : a {@link BioNetwork}
	 * @param substrate
	 *            : if true, get the substrates, if false, get the products
	 * @param sbmlCode
	 *            : if true, transforms the not sbml-compliant characters in the reaction ids
	 * @throws IOException
	 */
	public CompoundsAttributesForReactions(BioNetwork network, Boolean substrate, Boolean sbmlCode) throws IOException {

		super(network, sbmlCode);

		this.substrate = substrate;

	}

	/**
	 * Get substrate or product attributes in a hashmap
	 */
	@Override
	public HashMap<String, String> getAttributes() {

		HashMap<String, String> res = new HashMap<String, String>();

		HashMap<String, BioReaction> reactions = this.getNetwork().getBiochemicalReactionList();

		for (BioReaction reaction : reactions.values()) {
			HashMap<String, BioPhysicalEntity> compounds = new HashMap<String, BioPhysicalEntity>();

			if (substrate) {
				compounds = reaction.getListOfSubstrates();
			} else {
				compounds = reaction.getListOfProducts();
			}

			String attribute = "(";

			int i = 0;
			
			ArrayList<String> keys = 
					new ArrayList<String>(compounds.keySet());
			
			Collections.sort(keys);
			

			for (String cpdId : keys) {

				BioPhysicalEntity cpd = compounds.get(cpdId);
				
				i++;

				if (i != 1) {
					attribute += "::";
				}

				attribute += cpd.getName();

			}
			
			attribute += ")";
			
			String id = this.getSbmlCoded() ? StringUtils.sbmlEncode(reaction.getId()) : reaction.getId();
			
			res.put(id, attribute);
			

		}
		
		return res;

	}

}
