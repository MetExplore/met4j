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
package fr.inra.toulouse.metexplore.met4j_io.annotations;

import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

//import net.sf.saxon.functions.Unordered;

/**
 * @author ludo
 *
 */
public class BioUnitDefinition extends BioEntity {
	
	private String id = "NA";
	private String name = "NA";
	
	private Boolean flagedAsUpdate=false;
	private Boolean flagedAsInsert=false;
	private Boolean flagedAsConflict=false;
	
	HashMap<String, UnitSbml> units;
	
	public BioUnitDefinition(String id, String name) {
		super(id, name);
		this.units = new HashMap<String, UnitSbml>();
	}
	
	/**
	 * default BioUnitDefinition
	 */
	public void setDefault() {
		
		this.id = "mmol_per_gDW_per_hr";
		this.name = this.id;
		this.units = new HashMap<String, UnitSbml>();
		
		this.units.put("mole", new UnitSbml("mole", "", "-3", ""));
		this.units.put("gram", new UnitSbml("gram", "-1", "", ""));
		this.units.put("second", new UnitSbml("second", "-1", "", "0.00027777"));
		
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public Boolean getFlagedAsUpdate() {
		return flagedAsUpdate;
	}

	public void setFlagedAsUpdate(Boolean flagedAsUpdate) {
		this.flagedAsUpdate = flagedAsUpdate;
	}

	public Boolean getFlagedAsInsert() {
		return flagedAsInsert;
	}

	public void setFlagedAsInsert(Boolean flagedAsInsert) {
		this.flagedAsInsert = flagedAsInsert;
	}

	public Boolean getFlagedAsConflict() {
		return flagedAsConflict;
	}

	public void setFlagedAsConflict(Boolean flagedAsConflict) {
		this.flagedAsConflict = flagedAsConflict;
	}

}
