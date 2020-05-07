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
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

/**
 * This represents a complete flux objectives present in the model. A Flux
 * objective is a linear combination of reactions of the model that need their fluxes to be
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
public class BioObjective extends BioEntity {
	/**
	 * The type of the objective. can be {@link FbcType#maximize} or
	 * {@link FbcType#minimize}
	 */
	private FbcType type;

	/**
	 * The list of {@link ReactionObjective} that are part of this objective
	 */
	private BioCollection<ReactionObjective> listOfReactionObjectives = new BioCollection<ReactionObjective>();
	
	public Boolean active = false;
	

	/**
	 * Constructor with id and name parameter
	 * 
	 * @param id
	 *            the sbml id
	 * @param name
	 *            the name of the objective
	 */
	public BioObjective(String id, String name) {
		super(id, name);
	}

	/**
	 * Retrieves the {@link #type} of this objective
	 * 
	 * @return the {@link #type} as a string
	 */
	public FbcType getType() {
		return type;
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
	public BioCollection<ReactionObjective> getListOfReactionObjectives() {
		return this.listOfReactionObjectives;
	}

	/**
	 * Set the {@link #listOfReactionObjectives}
	 * 
	 * @param listOfReactionObjectives
	 *            the new {@link #listOfReactionObjectives}
	 */
	public void setListOfReactionObjectives(
			BioCollection<ReactionObjective> listOfReactionObjectives) {
		this.listOfReactionObjectives = listOfReactionObjectives;
	}

}