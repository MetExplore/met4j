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
import java.util.Map.Entry;

/**
 * @author ludo
 *
 */
public class UnitSbml {

	private static final HashMap<String, Integer> SbmlKindConverter = new HashMap<String, Integer>();

	/**
	 * Static initializer for the SbmlKindConverter variable. This code is executed
	 * once the class is first referenced.
	 */
	static {
		SbmlKindConverter.put("AMPERE", 0);
		SbmlKindConverter.put("AVOGADRO", 1);
		SbmlKindConverter.put("BECQUEREL", 2);
		SbmlKindConverter.put("CANDELA", 3);
		SbmlKindConverter.put("CELSIUS", 4);
		SbmlKindConverter.put("COULOMB", 5);
		SbmlKindConverter.put("DIMENSIONLESS", 6);
		SbmlKindConverter.put("FARAD", 7);
		SbmlKindConverter.put("GRAM", 8);
		SbmlKindConverter.put("GRAY", 9);
		SbmlKindConverter.put("HENRY", 10);
		SbmlKindConverter.put("HERTZ", 11);
		SbmlKindConverter.put("INVALID", 36);
		SbmlKindConverter.put("ITEM", 12);
		SbmlKindConverter.put("JOULE", 13);
		SbmlKindConverter.put("KATAL", 14);
		SbmlKindConverter.put("KELVIN", 15);
		SbmlKindConverter.put("KILOGRAM", 16);
		SbmlKindConverter.put("LITER", 17);
		SbmlKindConverter.put("LITRE", 18);
		SbmlKindConverter.put("LUMEN", 19);
		SbmlKindConverter.put("LUX", 20);
		SbmlKindConverter.put("METER", 21);
		SbmlKindConverter.put("METRE", 22);
		SbmlKindConverter.put("MOLE", 23);
		SbmlKindConverter.put("NEWTON", 24);
		SbmlKindConverter.put("OHM", 25);
		SbmlKindConverter.put("PASCAL", 26);
		SbmlKindConverter.put("RADIAN", 27);
		SbmlKindConverter.put("SECOND", 28);
		SbmlKindConverter.put("SIEMENS", 29);
		SbmlKindConverter.put("SIEVERT", 30);
		SbmlKindConverter.put("STERADIAN", 31);
		SbmlKindConverter.put("TESLA", 32);
		SbmlKindConverter.put("VOLT", 33);
		SbmlKindConverter.put("WATT", 34);
		SbmlKindConverter.put("WEBER", 35);

	}

	private String kind;
	private Double exponent = 1.0;
	private Integer scale = 0;
	private Double multiplier = 1.0;

	public UnitSbml(String kind, Double exponent, Integer scale, Double multiplier) {
		super();
		this.kind = kind;

		if (exponent != null)
			this.exponent = exponent;

		if (scale != null)
			this.scale = scale;

		if (multiplier != null)
			this.multiplier = multiplier;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind.toUpperCase();
	}

	public Double getExponent() {
		return exponent;
	}

	public void setExponent(Double exponent) {
		this.exponent = exponent;
	}

	public Integer getScale() {
		return scale;
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public Double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}

	public static HashMap<String, Integer> getSbmlkindconverter() {
		return SbmlKindConverter;
	}

	public void convertKindtoString(int sbmlKindAsInt) {
		for (Entry<String, Integer> kindEntry : UnitSbml.getSbmlkindconverter().entrySet()) {
			if (kindEntry.getValue() == sbmlKindAsInt) {
				this.setKind(kindEntry.getKey());
				break;
			}
		}
	}

	public int getSBMLKindCode() {
		return UnitSbml.getSbmlkindconverter().get(this.kind);
	}

}
