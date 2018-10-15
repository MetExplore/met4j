package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import java.util.stream.Collectors;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

/**
 * This class represents a unique "AND" gene association required to activate a
 * given {@link BioChemicalReaction}.</br> </br> it is composed of a set of
 * {@link BioGene} that all need to be active for this association to be active
 * 
 * @author Benjamin
 * @since 3.0
 */
public class SingleGeneAssociation {

	/**
	 * The Set of {@link BioGene} present in this association
	 */
	private BioCollection<BioGene> geneList = new BioCollection<BioGene>();

	/**
	 * Add a gene to the set of gene in this association
	 * 
	 * @param g
	 *            The gene to be added to the association's list
	 */
	public void addGene(BioGene g) {
		this.geneList.add(g);
	}

	/**
	 * Retrieves the complete Set of {@link BioGene} of this association
	 * 
	 * @return the list of genes
	 */
	public BioCollection<BioGene> getGeneList() {
		return this.geneList;
	}

	/**
	 * Replace the list of {@link BioGene} of this association
	 * 
	 * @param geneList
	 *            The new set of gene
	 */
	public void setGeneList(BioCollection<BioGene> geneList) {
		this.geneList = geneList;
	}

	/**
	 * Concatenate two existing {@link SingleGeneAssociation}s to a new
	 * one.</br></br> we cannot use an instance's method that simply does:
	 * 
	 * <pre>
	 * <code>
	 * this.getGeneList().addAll(y.geneList);
	 * </code>
	 * </pre>
	 * 
	 * Because the x {@link SingleGeneAssociation} is used in a recursion loop
	 * and needs to be left unchanged in every recursion
	 * 
	 * @param x
	 *            the first {@link SingleGeneAssociation}
	 * @param y
	 *            the second {@link SingleGeneAssociation}
	 * @return a new {@link SingleGeneAssociation} that contains the genes of x
	 *         and y.
	 */
	public static SingleGeneAssociation concatToNewGA(SingleGeneAssociation x,
			SingleGeneAssociation y) {
		SingleGeneAssociation newGa = new SingleGeneAssociation();

		newGa.getGeneList().addAll(x.geneList);
		newGa.getGeneList().addAll(y.geneList);

		return newGa;
	}

	/**
	 * This method outputs the PAlson's representation of an "AND" gene association
	 * @return the string representation of this {@link SingleGeneAssociation}
	 */
	public String toString() {

		return this.getGeneList().getIds().stream().sorted().collect(Collectors.joining(" AND "));

	}

	/**
	 * Test if this {@link SingleGeneAssociation} has only one gene
	 * @return true if the size of {@link #geneList} is one. False otherwise 
	 */
	public boolean isSingleGene() {
		if (this.getGeneList().size() == 1) {
			return true;
		}
		return false;
	}

}