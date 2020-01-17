package fr.inra.toulouse.metexplore.met4j_io.jsbml.errors;

/**
 * This error is thrown by
 * {@link parsebionet.io.jsbml.reader.plugin.NotesParser} class when the gene
 * association read in the reaction notes does not have a correct syntax. In
 * most case it is a missing parenthesis.
 * 
 * @author Benjamin
 * @since 3.0
 */
public class MalformedGeneAssociationStringException extends Exception {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3827619509014098132L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            the error message
	 */
	public MalformedGeneAssociationStringException(String message) {
		super(message);
	}
}
