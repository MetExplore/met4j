package fr.inra.toulouse.metexplore.met4j_io.jsbml.errors;

/**
 * This Error is thrown by the
 * {@link parsebionet.io.jsbml.reader.JsbmlToBioNetwork} class when it checks
 * the compatibility between the SBML level and the packages used for the
 * parsing
 * 
 * @author Benjamin
 * @since 3.0
 */
public class JSBMLPackageReaderException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            the error message
	 */
	public JSBMLPackageReaderException(String message) {
		super(message);
	}

}
