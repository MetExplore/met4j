package fr.inra.toulouse.metexplore.met4j_io.annotations.network;

import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjective;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.BioObjectiveCollection;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;

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

		return getUnitDefinitions(network).getEntityFromId(unitId);

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
	public static void setObjectives(BioNetwork network,BioObjectiveCollection objectives) {
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
