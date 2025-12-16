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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * This class represents a part of one of the flux objectives present in the model
 *
 * @author Benjamin
 * @since 3.0
 */
public class ReactionObjective extends BioEntity {

	
	
	
	/**
	 * <p>Constructor for ReactionObjective.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 * @param name a {@link java.lang.String} object.
	 */
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
	 * Retrieves the {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction}
	 *
	 * @return the {@link #flxReaction}
	 */
	public FluxReaction getFlxReaction() {
		return this.flxReaction;
	}

	/**
	 * Set the {@link #flxReaction}
	 *
	 * @param reaction
	 * 		the new {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction}
	 */
	public void setFlxReaction(FluxReaction reaction) {
		this.flxReaction=reaction;
	}

	/**
	 * Retrieves the {@link #coefficient}
	 *
	 * @return the {@link #coefficient}
	 */
	public double getCoefficient() {
		return this.coefficient;
	}

	/**
	 * Set the {@link #coefficient}
	 *
	 * @param coefficient
	 * 		the new {@link #coefficient}
	 */
	public void setCoefficient(double coefficient) {
		this.coefficient=coefficient;
	}

}
