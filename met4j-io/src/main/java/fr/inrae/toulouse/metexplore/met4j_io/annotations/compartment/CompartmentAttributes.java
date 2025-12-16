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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;

/**
 * <p>CompartmentAttributes class.</p>
 *
 * @author lcottret
 */
public class CompartmentAttributes extends GenericAttributes {

	/** Constant <code>OUTSIDE_COMPARTMENT="outside_compartment"</code> */
	public static final String OUTSIDE_COMPARTMENT = "outside_compartment";
	/** Constant <code>TYPE="type"</code> */
	public static final String TYPE = "type";
	/** Constant <code>SIZE="size"</code> */
	public static final String SIZE = "size";
	/** Constant <code>SPATIAL_DIMENSIONS="spatial_dimensions"</code> */
	public static final String SPATIAL_DIMENSIONS = "spatial_dimensions";
	/** Constant <code>UNIT_DEFINITION="unit_definition"</code> */
	public static final String UNIT_DEFINITION = "unit_definition";

	/**
	 * Set outside compartment
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @param outside a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 */
	public static void setOutsideCompartment(BioCompartment c, BioCompartment outside) {

		if (c.getId().equals(outside.getId())) {
			throw new IllegalArgumentException("The compartment and the outside compartment have a different id");
		}

		c.setAttribute(OUTSIDE_COMPARTMENT, outside);
	}

	/**
	 * get outside compartment
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 */
	public static BioCompartment getOutsideCompartment(BioCompartment c) {
		return (BioCompartment) c.getAttribute(OUTSIDE_COMPARTMENT);
	}

	/**
	 * Set type
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @param type a {@link fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.BioCompartmentType} object.
	 */
	public static void setType(BioCompartment c, BioCompartmentType type) {

		c.setAttribute(TYPE, type);

	}

	/**
	 * Get Type
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.BioCompartmentType} object.
	 */
	public static BioCompartmentType getType(BioCompartment c) {
		return (BioCompartmentType) c.getAttribute(TYPE);
	}

	/**
	 * Add a unit definition
	 *
	 * @param unitDefinition a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition} object.
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 */
	public static void setUnitDefinition(BioCompartment c, BioUnitDefinition unitDefinition) {

		c.setAttribute(UNIT_DEFINITION, unitDefinition);

	}

	/**
	 * Get a Unit definition from its id
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition} object.
	 */
	public static BioUnitDefinition getUnitDefinition(BioCompartment c) {

		return ((BioUnitDefinition) c.getAttribute(UNIT_DEFINITION));

	}

	/**
	 * get size
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @return a {@link java.lang.Double} object.
	 */
	public static Double getSize(BioCompartment c) {

		if (c.getAttribute(SIZE) == null) {
			return null;
		}
		return (Double) c.getAttribute(SIZE);
	}

	/**
	 * Set size
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @param s a {@link java.lang.Double} object.
	 */
	public static void setSize(BioCompartment c, Double s) {

		c.setAttribute(SIZE, s);

	}

	/**
	 * get spatial dimensions
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @return a {@link java.lang.Integer} object.
	 */
	public static Integer getSpatialDimensions(BioCompartment c) {
		return (Integer) c.getAttribute(SPATIAL_DIMENSIONS);
	}

	/**
	 * Set spatial dimensions
	 *
	 * @param c a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment} object.
	 * @param s a {@link java.lang.Integer} object.
	 */
	public static void setSpatialDimensions(BioCompartment c, Integer s) {

		c.setAttribute(SPATIAL_DIMENSIONS, s);

	}

}
