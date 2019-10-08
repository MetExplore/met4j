package fr.inra.toulouse.metexplore.met4j_chemUtils.chemicalStructures;

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
	 *            the compared {@link InChIKey}
	 * @return true if the first block of the two InChIKeys are equals, false
	 *         otherwise
	 */
	public boolean equals(InChIKey compared) {
		return this.equals(compared, "1");
	}

	/**
	 * Test if two {@link InChIKey} are equals, considering the passed blocks
	 * identifier
	 * 
	 * @param compared
	 *            the compared {@link InChIKey}
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

	public boolean isValid() {
		return validity;
	}

	public void setValidity(boolean validity) {
		this.validity = validity;
	}

	/**
	 * @return the inchikeyString
	 */
	public String getInchikeyString() {
		return inchikeyString;
	}

	/**
	 * @param inchikeyString
	 *            the inchikeyString to set
	 */
	public void setInchikeyString(String inchikeyString) {
		this.inchikeyString = inchikeyString;
	}

	/**
	 * @return the firstBlock
	 */
	public String getFirstBlock() {
		return firstBlock;
	}

	/**
	 * @param firstBlock
	 *            the firstBlock to set
	 */
	public void setFirstBlock(String firstBlock) {
		this.firstBlock = firstBlock;
	}

	/**
	 * @return the secondBlock
	 */
	public String getSecondBlock() {
		return secondBlock;
	}

	/**
	 * @param secondBlock
	 *            the secondBlock to set
	 */
	public void setSecondBlock(String secondBlock) {
		this.secondBlock = secondBlock;
	}

	/**
	 * @return the standardFlag
	 */
	public char getStandardFlag() {
		return standardFlag;
	}

	/**
	 * @param standardFlag
	 *            the standardFlag to set
	 */
	public void setStandardFlag(char standardFlag) {
		this.standardFlag = standardFlag;
	}

	/**
	 * @return the versionFlag
	 */
	public char getVersionFlag() {
		return versionFlag;
	}

	/**
	 * @param versionFlag
	 *            the versionFlag to set
	 */
	public void setVersionFlag(char versionFlag) {
		this.versionFlag = versionFlag;
	}

	/**
	 * @return the protonationFlag
	 */
	public char getProtonationFlag() {
		return protonationFlag;
	}

	/**
	 * @param protonationFlag
	 *            the protonationFlag to set
	 */
	public void setProtonationFlag(char protonationFlag) {
		this.protonationFlag = protonationFlag;
	}

}
