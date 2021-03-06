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
/**
 * 1 juin 2011 
 */
package fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;

/**
 * <p>Flux class.</p>
 *
 * @author ludo 1 juin 2011
 * @version $Id: $Id
 */
public class Flux extends BioEntity {

	public Double value;

	public BioUnitDefinition unitDefinition = null;
	private boolean constant = false;

	/** Constant <code>FLUXMAX</code> */
	public static Double FLUXMAX = 99999.0;
	/** Constant <code>FLUXMIN</code> */
	public static Double FLUXMIN = -99999.0;

	/**
	 * <p>Constructor for Flux.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 * @param value a {@link java.lang.Double} object.
	 * @param unitDefinition a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition} object.
	 */
	public Flux(String id, Double value, BioUnitDefinition unitDefinition) {

		super(id);

		this.value = value;
		this.unitDefinition = unitDefinition;

	}

	/**
	 * <p>Constructor for Flux.</p>
	 *
	 * @param value a {@link java.lang.Double} object.
	 */
	public Flux(Double value) {

		super(value + "_" + BioUnitDefinition.DEFAULT_UNIT);

		BioUnitDefinition unitDefinition = new BioUnitDefinition();

		this.value = value;
		this.unitDefinition = unitDefinition;

	}

	/**
	 * <p>Constructor for Flux.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 */
	public Flux(String id) {
		super(id);
	}

	/**
	 * <p>Getter for the field <code>constant</code>.</p>
	 *
	 * @return a boolean.
	 */
	public boolean getConstant() {
		return this.constant;
	}

	/**
	 * <p>Setter for the field <code>constant</code>.</p>
	 *
	 * @param constant a boolean.
	 */
	public void setConstant(boolean constant) {
		this.constant = constant;
	}

}
