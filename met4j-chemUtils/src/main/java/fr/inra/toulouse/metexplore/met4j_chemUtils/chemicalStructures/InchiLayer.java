package fr.inra.toulouse.metexplore.met4j_chemUtils.chemicalStructures;

import java.util.HashMap;

/**
 * This class represents the different inchi layers that can contain a unique
 * InChI object
 * 
 * @author Benjamin
 * @since 2.0
 */
public class InchiLayer {

	/**
	 * name of the layer
	 */
	public char name;
	/**
	 * Value of the layer as a string
	 */
	public String value;

	/**
	 * The map of sub-layers contained in this one with their character
	 * identifier
	 */
	public HashMap<Character, InchiLayer> subLayer;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            {@link #name}
	 * @param value
	 *            {@link #value}
	 */
	public InchiLayer(char name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 *            {@link #name}
	 * @param value
	 *            {@link #value}
	 * @param subLayer
	 *            the map of the sub layer
	 */
	public InchiLayer(char name, String value,
			HashMap<Character, InchiLayer> subLayer) {
		this.name = name;
		this.value = value;
		this.subLayer = subLayer;
	}

	public String toString() {

		String output = this.name + ": " + this.value;

		return output;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof InchiLayer) {

			InchiLayer layB = (InchiLayer) obj;

			if (this.getName() != layB.getName()) {
				return false;
			}

			if (!this.getValue().equals(layB.getValue())) {
				return false;
			}

			return true;

		} else {
			return false;
		}

	}

	/**
	 * @return the name
	 */
	public char getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(char name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the subLayer
	 */
	public HashMap<Character, InchiLayer> getSubLayer() {
		return subLayer;
	}

	/**
	 * @param subLayer
	 *            the subLayer to set
	 */
	public void setSubLayer(HashMap<Character, InchiLayer> subLayer) {
		this.subLayer = subLayer;
	}

}
