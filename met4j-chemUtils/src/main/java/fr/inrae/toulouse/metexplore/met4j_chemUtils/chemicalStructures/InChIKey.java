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

package fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures;

/**
 * This class converts an InchiKey Strings into objects that can be manipulated
 * and compared between them with a custom logic
 *
 * @author Benjamin
 * @since 2.0
 */
public class InChIKey extends ChemicalStructure {
	/**
	 * The original InchiKey String
	 */
	public String inchikeyString;

	/**
	 * The first block of the InChIkey
	 */
	public String firstBlock;
	/**
	 * The second block of the InChIkey
	 */
	public String secondBlock;

	/**
	 * Character Flag that tell if th InChI behind this InchiKey was Standard or
	 * not
	 */
	public char standardFlag;
	/**
	 * Character Flag for the version of the InChI program behind this InchiKey
	 */
	public char versionFlag;
	/**
	 * Character
	 */
	public char protonationFlag;

	/**
	 * Empty constructor. Set the Validity to false
	 */
	public InChIKey() {
		this.validity = false;
	}

	/**
	 * Constructor. Uses {@link #setBlocks()} to create the different blocks and
	 * character flags
	 *
	 * @param keyString
	 *            the inchiKey string
	 */
	public InChIKey(String keyString) {
		this.setInchikeyString(keyString);
		this.setBlocks();
	}

	/**
	 * set the different blocks and flags frome {@link #inchikeyString}
	 */
	public void setBlocks() {
		String ks = this.getInchikeyString();

		String[] tmp = ks.split("-");

		if (ks.length() == 27 && tmp.length == 3) {
			if ((tmp[2]).length() == 1) {

				this.setProtonationFlag((tmp[2]).charAt(0));

				this.setFirstBlock(tmp[0]);

				this.setVersionFlag(tmp[1].charAt(tmp[1].length() - 1));
				this.setStandardFlag(tmp[1].charAt(tmp[1].length() - 2));

				this.setSecondBlock(tmp[1].substring(0, tmp[1].length() - 2));

				this.setValidity(true);
			} else {
				this.setValidity(false);
			}
		} else {
			this.setValidity(false);
		}
	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String toString() {
		if (this.isValid()) {
			return this.getFirstBlock() + "-" + this.getSecondBlock()
					+ this.getStandardFlag() + this.getVersionFlag() + "-"
					+ this.getProtonationFlag();
		}
		return null;

	}

	/**
	 * Default InChI comparison. By default this compares:
	 * <ul>
	 * <li>{@link #firstBlock}
	 * </ul>
	 *
	 * @param compared
	 *            the compared {@link fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures.InChIKey}
	 * @return true if the first block of the two InChIKeys are equals, false
	 *         otherwise
	 */
	public boolean equals(InChIKey compared) {
		return this.equals(compared, "1");
	}

	/**
	 * Test if two {@link fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures.InChIKey} are equals, considering the passed blocks
	 * identifier
	 *
	 * @param compared
	 *            the compared {@link fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalStructures.InChIKey}
	 * @param comparedBlocks
	 *            the blocks to compare. Can be a combination of these block
	 *            identifiers:
	 *            <ul>
	 *            <li>1 : compare the {@link #firstBlock}
	 *            <li>2 : compare the {@link #secondBlock}
	 *            <li>3 : compare the {@link #protonationFlag}
	 *            </ul>
	 * @return true if the compared layers are the same, false otherwise
	 */
	public boolean equals(InChIKey compared, String comparedBlocks) {

		boolean result = false, second = true, third = true;

		if (comparedBlocks.contains("1")) {

			if (this.getFirstBlock().equalsIgnoreCase(compared.getFirstBlock())) {
				result = true;
			}
		} else {
			return false;
		}

		if (comparedBlocks.contains("2")
				&& !this.getSecondBlock().equalsIgnoreCase(
						compared.getSecondBlock())) {

			second = false;
		}

		if (comparedBlocks.contains("3")
				&& this.getProtonationFlag() != compared.getProtonationFlag()) {

			third = false;
		}

		return (result & second & third);
	}

	/**
	 * <p>isValid.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isValid() {
		return validity;
	}

	/** {@inheritDoc} */
	public void setValidity(boolean validity) {
		this.validity = validity;
	}

	/**
	 * <p>Getter for the field <code>inchikeyString</code>.</p>
	 *
	 * @return the inchikeyString
	 */
	public String getInchikeyString() {
		return inchikeyString;
	}

	/**
	 * <p>Setter for the field <code>inchikeyString</code>.</p>
	 *
	 * @param inchikeyString
	 *            the inchikeyString to set
	 */
	public void setInchikeyString(String inchikeyString) {
		this.inchikeyString = inchikeyString;
	}

	/**
	 * <p>Getter for the field <code>firstBlock</code>.</p>
	 *
	 * @return the firstBlock
	 */
	public String getFirstBlock() {
		return firstBlock;
	}

	/**
	 * <p>Setter for the field <code>firstBlock</code>.</p>
	 *
	 * @param firstBlock
	 *            the firstBlock to set
	 */
	public void setFirstBlock(String firstBlock) {
		this.firstBlock = firstBlock;
	}

	/**
	 * <p>Getter for the field <code>secondBlock</code>.</p>
	 *
	 * @return the secondBlock
	 */
	public String getSecondBlock() {
		return secondBlock;
	}

	/**
	 * <p>Setter for the field <code>secondBlock</code>.</p>
	 *
	 * @param secondBlock
	 *            the secondBlock to set
	 */
	public void setSecondBlock(String secondBlock) {
		this.secondBlock = secondBlock;
	}

	/**
	 * <p>Getter for the field <code>standardFlag</code>.</p>
	 *
	 * @return the standardFlag
	 */
	public char getStandardFlag() {
		return standardFlag;
	}

	/**
	 * <p>Setter for the field <code>standardFlag</code>.</p>
	 *
	 * @param standardFlag
	 *            the standardFlag to set
	 */
	public void setStandardFlag(char standardFlag) {
		this.standardFlag = standardFlag;
	}

	/**
	 * <p>Getter for the field <code>versionFlag</code>.</p>
	 *
	 * @return the versionFlag
	 */
	public char getVersionFlag() {
		return versionFlag;
	}

	/**
	 * <p>Setter for the field <code>versionFlag</code>.</p>
	 *
	 * @param versionFlag
	 *            the versionFlag to set
	 */
	public void setVersionFlag(char versionFlag) {
		this.versionFlag = versionFlag;
	}

	/**
	 * <p>Getter for the field <code>protonationFlag</code>.</p>
	 *
	 * @return the protonationFlag
	 */
	public char getProtonationFlag() {
		return protonationFlag;
	}

	/**
	 * <p>Setter for the field <code>protonationFlag</code>.</p>
	 *
	 * @param protonationFlag
	 *            the protonationFlag to set
	 */
	public void setProtonationFlag(char protonationFlag) {
		this.protonationFlag = protonationFlag;
	}

}
