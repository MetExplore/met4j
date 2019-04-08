package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader;

public class Met4jSbmlReaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
	
	public Met4jSbmlReaderException(String m) {
		this.message = m;
	}
	
	public String toString() {
	      return this.getClass().getSimpleName()+ "[" + message + "]";
	   }
	
}
