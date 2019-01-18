package fr.inra.toulouse.metexplore.met4j_io.annotations.network;

import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.BioAnnotation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinitionCollection;

public class NetworkAttributes {

	public static final String UNIT_DEFINITIONS = "unit_definitions";

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
	 * 
	 * @param network
	 * @param notes
	 */
	public static void setNotes(BioNetwork network, Notes notes) {

		GenericAttributes.setNotes(network, notes);

	}

	/**
	 * 
	 * @param network
	 * @param notes
	 */
	public static Notes getNotes(BioNetwork network) {

		return GenericAttributes.getNotes(network);

	}

	/**
	 * get pmids
	 * 
	 * @param r
	 * @return
	 */
	public static Set<Integer> getPmids(BioNetwork n) {

		return GenericAttributes.getPmids(n);

	}

	/**
	 * 
	 * set pmids
	 * 
	 * @param r
	 * @param pmids
	 */
	public static void setPmids(BioNetwork n, Set<Integer> pmids) {

		GenericAttributes.setPmids(n, pmids);

	}
	
	/**
	 * Set annotation
	 * 
	 * @param metabolite
	 * @param val
	 */
	public static void setAnnotation(BioNetwork network, BioAnnotation val) {
		GenericAttributes.setAnnotation(network, val);
	}

	/**
	 * Get annotation
	 * 
	 * @param metabolite
	 * @return
	 */
	public static BioAnnotation getAnnotation(BioNetwork network) {
		return GenericAttributes.getAnnotation(network);
	}


}
