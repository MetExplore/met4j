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
 * 
 */
package fr.inrae.toulouse.metexplore.met4j_io.jsbml.units;

import java.util.HashMap;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

//import net.sf.saxon.functions.Unordered;

/**
 * <p>BioUnitDefinition class.</p>
 *
 * @author ludo
 * @version $Id: $Id
 */
public class BioUnitDefinition extends BioEntity {
	
	/** Constant <code>DEFAULT_UNIT="mmol_per_gDW_per_hr"</code> */
	public static String DEFAULT_UNIT = "mmol_per_gDW_per_hr";
	
	HashMap<String, UnitSbml> units;
	
	/**
	 * <p>Constructor for BioUnitDefinition.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 * @param name a {@link java.lang.String} object.
	 */
	public BioUnitDefinition(String id, String name) {
		super(id, name);
		
		this.units = new HashMap<String, UnitSbml>();
	}
	
	/**
	 * default BioUnitDefinition
	 */
	public BioUnitDefinition() {
		
		super(DEFAULT_UNIT, DEFAULT_UNIT);
		
		this.units = new HashMap<String, UnitSbml>();
		
		this.units.put("mole", new UnitSbml("mole", null, -3, null));
		this.units.put("gram", new UnitSbml("gram", -1.0, null, null));
		this.units.put("second", new UnitSbml("second", -1.0, null, 0.00027777));
		
	}
	
	/**
	 * <p>Getter for the field <code>units</code>.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, UnitSbml> getUnits() {
		return units;
	}

	/**
	 * <p>Setter for the field <code>units</code>.</p>
	 *
	 * @param units a {@link java.util.HashMap} object.
	 */
	public void setUnits(HashMap<String, UnitSbml> units) {
		this.units = units;
	}
	
	/**
	 * <p>addUnit.</p>
	 *
	 * @param unit a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.UnitSbml} object.
	 */
	public void addUnit(UnitSbml unit) {
		this.units.put(unit.getKind(), unit);
	}

}
