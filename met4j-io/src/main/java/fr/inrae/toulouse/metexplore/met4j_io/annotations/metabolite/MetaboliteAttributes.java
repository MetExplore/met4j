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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

/**
 * <p>MetaboliteAttributes class.</p>
 *
 * @author lcottret
 */
public class MetaboliteAttributes extends GenericAttributes {

	/** Constant <code>NOTES="notes"</code> */
	public static final String NOTES = "notes";
	/** Constant <code>COMMENT="comment"</code> */
	public static final String COMMENT = "comment";
	/** Constant <code>CHARGE="charge"</code> */
	public static final String CHARGE = "charge";
	/** Constant <code>FORMULA="formula"</code> */
	public static final String FORMULA = "formula";
	/** Constant <code>INCHIKEY="inchikey"</code> */
	public static final String INCHIKEY = "inchikey";
	/** Constant <code>INCHI="inchi"</code> */
	public static final String INCHI = "inchi";
	/** Constant <code>BOUNDARY_CONDITION="boundary_condition"</code> */
	public static final String BOUNDARY_CONDITION = "boundary_condition";
	/** Constant <code>SUBSTANCE_UNITS="substance_units"</code> */
	public static final String SUBSTANCE_UNITS = "substance_units";
	/** Constant <code>INITIAL_AMOUNT="initial_quantity_amount"</code> */
	public static final String INITIAL_AMOUNT = "initial_quantity_amount";
	/** Constant <code>INITIAL_CONCENTRATION="initial_quantity_concentration"</code> */
	public static final String INITIAL_CONCENTRATION = "initial_quantity_concentration";
	/** Constant <code>PUBCHEM="pubchem.compound"</code> */
	public static final String PUBCHEM = "pubchem.compound";
	/** Constant <code>HAS_ONLY_SUBSTANCE_UNITS="has_only_substance_units"</code> */
	public static final String HAS_ONLY_SUBSTANCE_UNITS = "has_only_substance_units";


	/** Constant <code>IS_COFACTOR="is_cofactor"</code> */
	public static final String IS_COFACTOR = "is_cofactor";

	/**
	 * get Boundary condition of a metabolite
	 *
	 * @param m a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @return a {@link java.lang.Boolean} object.
	 */
	public static Boolean getBoundaryCondition(BioMetabolite m) {

		Boolean flag = false;

		if (m.getAttribute(BOUNDARY_CONDITION) != null) {
			flag = (Boolean) m.getAttribute(BOUNDARY_CONDITION);
		}

		return flag;
	}

	/**
	 * Set boundary condition
	 *
	 * @param m a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param flag a {@link java.lang.Boolean} object.
	 */
	public static void setBoundaryCondition(BioMetabolite m, Boolean flag) {
		m.setAttribute(BOUNDARY_CONDITION, flag);
	}

	/**
	 * Get Metabolite units
	 *
	 * @param m a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getSubtanceUnits(BioMetabolite m) {

		return (String) m.getAttribute(SUBSTANCE_UNITS);
	}

	/**
	 * Set subtance units
	 *
	 * @param u a {@link java.lang.String} object.
	 * @param m a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 */
	public static void setSubstanceUnits(BioMetabolite m, String u) {

		m.setAttribute(SUBSTANCE_UNITS, u);

	}

	/**
	 * get Initial Amount
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @return a {@link java.lang.Double} object.
	 */
	public static Double getInitialAmount(BioMetabolite metabolite) {

		if(metabolite.getAttribute(INITIAL_AMOUNT) == null)
		{
			return null;
		}

		return (Double) metabolite.getAttribute(INITIAL_AMOUNT);

	}

	/**
	 * Set initial amount
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param val a {@link java.lang.Double} object.
	 */
	public static void setInitialAmount(BioMetabolite metabolite, Double val) {
		metabolite.setAttribute(INITIAL_AMOUNT, val);

	}

	/**
	 * Get initial concentration
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @return a {@link java.lang.Double} object.
	 */
	public static Double getInitialConcentration(BioMetabolite metabolite) {
		Double val = (Double) metabolite.getAttribute(INITIAL_CONCENTRATION);

		return val;
	}

	/**
	 * Set initial concentration
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param val a {@link java.lang.Double} object.
	 */
	public static void setInitialConcentration(BioMetabolite metabolite, Double val) {
		metabolite.setAttribute(INITIAL_CONCENTRATION, val);

	}

	/**
	 * Get pubchem
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getPubchem(BioMetabolite metabolite) {
		return (String)metabolite.getAttribute(PUBCHEM);
	}

	/**
	 * Set pubchem
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param val a {@link java.lang.String} object.
	 */
	public static void setPubchem(BioMetabolite metabolite, String val) {
		metabolite.setAttribute(PUBCHEM, val);
	}

	/**
	 * Get hasOnlySubstanceUnit value
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @return a {@link java.lang.Boolean} object.
	 */
	public static Boolean getHasOnlySubstanceUnits(BioMetabolite metabolite) {
		return (Boolean) metabolite.getAttribute(HAS_ONLY_SUBSTANCE_UNITS);
	}

	/**
	 * Set hasOnlySubstanceUnit value
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param b a boolean.
	 */
	public static void setHasOnlySubstanceUnits(BioMetabolite metabolite, boolean b) {
		metabolite.setAttribute(HAS_ONLY_SUBSTANCE_UNITS, b);
	}

	/**
	 * get is cofactor attribute
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @return a {@link java.lang.Boolean} object.
	 */
	public static Boolean getIsCofactor(BioMetabolite metabolite) {
		if(metabolite.getAttribute(IS_COFACTOR) == null) {
			return false;
		}
		return (Boolean) metabolite.getAttribute(IS_COFACTOR);

	}

	/**
	 * Set IsCofactor value
	 *
	 * @param metabolite a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param b a boolean.
	 */
	public static void setIsCofactor(BioMetabolite metabolite, boolean b) {
		metabolite.setAttribute(IS_COFACTOR, b);
	}

}
