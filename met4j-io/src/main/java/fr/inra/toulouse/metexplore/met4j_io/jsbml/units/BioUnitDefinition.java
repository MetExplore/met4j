/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
/**
 * 
 */
package fr.inra.toulouse.metexplore.met4j_io.jsbml.units;

import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

//import net.sf.saxon.functions.Unordered;

/**
 * @author ludo
 *
 */
public class BioUnitDefinition extends BioEntity {
	
	public static String DEFAULT_UNIT = "mmol_per_gDW_per_hr";
	
	HashMap<String, UnitSbml> units;
	
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
	
	public HashMap<String, UnitSbml> getUnits() {
		return units;
	}

	public void setUnits(HashMap<String, UnitSbml> units) {
		this.units = units;
	}
	
	public void addUnit(UnitSbml unit) {
		this.units.put(unit.getKind(), unit);
	}

}