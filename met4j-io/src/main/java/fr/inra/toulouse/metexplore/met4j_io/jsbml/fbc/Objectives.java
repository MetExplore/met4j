package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import java.util.HashSet;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * This represents a complete flux objectives present in the model. A Flux
 * obective is a linear combination of reactions of the model that need their fluxes to be
 * maximized or minimised to attain a particular biological objective.</br> e.g.
 * <ul>
 * <li>Maximize the biomass
 * <li>Minimize the virulence
 * </ul>
 * </br></br> Each Flux objective has an id and a name.
 * 
 * @author Benjamin
 * @since 3.0
 */
public class Objectives extends BioEntity {
	/**
	 * The type of the objective. can be {@link FbcType#maximize} or
	 * {@link FbcType#minimize}
	 */
	private FbcType type;

	/**
	 * The list of {@link ReactionObjective} that are part of this objective
	 */
	private HashSet<ReactionObjective> listOfReactionObjectives = new HashSet<ReactionObjective>();

	/**
	 * Constructor with id and name parameter
	 * 
	 * @param id
	 *            the sbml id
	 * @param name
	 *            the name of the objective
	 */
	public Objectives(String id, String name) {
		super(id, name);
	}

	/**
	 * Retrieves the {@link #type} of this objective
	 * 
	 * @return the {@link #type} as a string
	 */
	public String getType() {
		return type.toString();
	}

	/**
	 * Set the {@link #type} of this objective
	 * 
	 * @param type
	 *            the new {@link #type} of this objective
	 */
	public void setType(String type) {
		this.type = FbcType.valueOf(type);
	}

	/**
	 * Retrieves the {@link #listOfReactionObjectives} of this objective
	 * 
	 * @return the complete list of {link ReactionObjective}
	 */
	public HashSet<ReactionObjective> getListOfReactionObjectives() {
		return this.listOfReactionObjectives;
	}

	/**
	 * Set the {@link #listOfReactionObjectives}
	 * 
	 * @param listOfReactionObjectives
	 *            the new {@link #listOfReactionObjectives}
	 */
	public void setListOfReactionObjectives(
			HashSet<ReactionObjective> listOfReactionObjectives) {
		this.listOfReactionObjectives = listOfReactionObjectives;
	}

}