package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;


import java.util.*;


/**
 * This class represents the full gene association as they are described in SBML
 * files. </br></br> This class consists of a list of
 * {@link SingleGeneAssociation}s. Each of them being one possible "AND" gene
 * association that can activate a given {@link BioChemicalReaction}.
 * 
 * 
 * @author Benjamin
 * @since 3.0
 * 
 */
public class GeneAssociations{


	/**
	 * The list of {@link SingleGeneAssociation}s
	 */
	private ArrayList<SingleGeneAssociation> listOfUniqueGA = new ArrayList<SingleGeneAssociation>();

	/**
	 * Return the complete list of {@link SingleGeneAssociation}s of this
	 * association
	 * 
	 * @return the list of {@link SingleGeneAssociation}s
	 */
	public ArrayList<SingleGeneAssociation> getListOfUniqueGA() {
		return this.listOfUniqueGA;
	}

	/**
	 * Replace the list of {@link SingleGeneAssociation} of this association
	 * 
	 * @param listOfGA
	 *            the new list of {@link SingleGeneAssociation}s
	 */
	public void setListOfUniqueGA(ArrayList<SingleGeneAssociation> listOfGA) {
		this.listOfUniqueGA = listOfGA;
	}

	/**
	 * Add a {@link SingleGeneAssociation} to the list
	 * 
	 * @param ga
	 *            The {@link SingleGeneAssociation} to add
	 */
	public void addUniqueGA(SingleGeneAssociation ga) {
		this.listOfUniqueGA.add(ga);
	}

	/**
	 * Returns the string representation of this GPR as a fully developed
	 * AND/OR logical expression.
	 */
	public String toString() {

		StringBuilder sb = new StringBuilder();
		Object[] tmp = this.getListOfUniqueGA().toArray();
		for (int i = 0; i < tmp.length - 1; i++) {

			sb.append(tmp[i].toString());
			sb.append(" OR ");

		}
		sb.append(tmp[tmp.length - 1].toString().trim());
		return sb.toString();

	}

}