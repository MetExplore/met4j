package fr.inra.toulouse.metexplore.met4j_io.jsbml.writer;

public class Met4jSbmlWriterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
	
	public Met4jSbmlWriterException(String m) {
		this.message = m;
	}
	
	public String toString() {
	      return this.getClass().getSimpleName()+ "[" + message + "]";
	   }
	
}
