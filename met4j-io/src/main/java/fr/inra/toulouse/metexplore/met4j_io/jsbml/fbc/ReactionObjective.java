package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * This class represents a part of one of the flux objectives present in the model
 * @author Benjamin
 * @since 3.0
 */
public class ReactionObjective extends BioEntity {

	
	
	
	public ReactionObjective(String id, String name) {
		super(id, name);
	}

	/**
	 * the {@link FluxReaction} of this {@link ReactionObjective}
	 */
	private FluxReaction flxReaction;
	/**
	 * The coefficient affected to the {@link FluxReaction}
	 */
	private double coefficient;

	/**
	 * Retrieves the {@link FluxReaction}
	 * @return the {@link #flxReaction}
	 */
	public FluxReaction getFlxReaction() {
		return this.flxReaction;
	}

	/**
	 * Set the {@link #flxReaction}
	 * @param reaction
	 * 		the new {@link FluxReaction}
	 */
	public void setFlxReaction(FluxReaction reaction) {
		this.flxReaction=reaction;
	}

	/**
	 * Retrieves the {@link #coefficient}
	 * @return the {@link #coefficient}
	 */
	public double getCoefficient() {
		return this.coefficient;
	}

	/**
	 * Set the {@link #coefficient}
	 * @param coefficient
	 * 		the new {@link #coefficient}
	 */
	public void setCoefficient(double coefficient) {
		this.coefficient=coefficient;
	}

}