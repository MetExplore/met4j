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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.network;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjectiveCollection;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;

public class NetworkAttributes extends GenericAttributes {

	public static final String UNIT_DEFINITIONS = "unit_definitions";
	public static final String OBJECTIVES = "objectives";

	/**
	 * Get BioUnitDefinitions of a network
	 * 
	 * @param network
	 * @return
	 */
	public static BioUnitDefinitionCollection getUnitDefinitions(BioNetwork network) {

		return ((BioUnitDefinitionCollection) (network.getAttribute(UNIT_DEFINITIONS)));

	}

	/**
	 * Get a unit definition of a network from its id
	 * 
	 * @param network
	 * @param unitId
	 * @return
	 */
	public static BioUnitDefinition getUnitDefinition(BioNetwork network, String unitId) {

		return getUnitDefinitions(network).get(unitId);

	}

	/**
	 * Add unit definitions to a network
	 * 
	 * @param network
	 * @param c
	 */
	public static void addUnitDefinitions(BioNetwork network, BioUnitDefinitionCollection c) {
		network.setAttribute(UNIT_DEFINITIONS, c);
	}

	/**
	 * Test if unit definitions have been set for a network
	 * 
	 * @param network
	 * @return
	 */
	public static Boolean containsUnitDefinitions(BioNetwork network) {
		return network.getAttribute(UNIT_DEFINITIONS) != null;
	}

	/**
	 * Add unit definitions to a network
	 * 
	 * @param network
	 * @param c
	 */
	public static void addUnitDefinition(BioNetwork network, BioUnitDefinition unit) {

		if (!containsUnitDefinitions(network)) {
			addUnitDefinitions(network, new BioUnitDefinitionCollection());
		}

		getUnitDefinitions(network).add(unit);

	}

	/**
	 * Set list of objectives
	 * @param network
	 * @param objectives
	 */
	public static void setObjectives(BioNetwork network, BioObjectiveCollection objectives) {
		network.setAttribute(OBJECTIVES, objectives);
	}

	/**
	 * Get list of objectives
	 * @param network
	 * @return
	 */
	public static BioObjectiveCollection getObjectives(BioNetwork network) {
		return (BioObjectiveCollection) network.getAttribute(OBJECTIVES);
	}

}
