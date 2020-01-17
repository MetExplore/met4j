package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import java.util.HashSet;
import java.util.stream.Collectors;


import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;

/**
 * This class represents a unique "AND" gene association required to activate a
 * given {@link BioReaction}.</br>
 * </br>
 * it is composed of a set of {@link BioGene} that all need to be active for
 * this association to be active
 * 
 * @author Benjamin mainly modified by LC
 * @since 3.0
 */
public class GeneSet extends HashSet<BioGene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;

	/**
	 * This method outputs the PAlson's representation of an "AND" gene
	 * association
	 * 
	 * @return the string representation of this {@link GeneSet}
	 */
	@Override
	public String toString() {

		return this.stream().map(x -> x.getId()).sorted().collect(Collectors.joining(" AND "));

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {

		String str = toString();

		final int prime = 31;
		
		// TODO : not very satisfying since we use a mutable property...
		int result = prime * ((str == null) ? 0 : str.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		String str = this.toString();

		if (this == obj)
			return true;
		
		if (getClass() != obj.getClass())
			return false;
		GeneSet other = (GeneSet) obj;
		if (str == null) {
			if (other.toString() != null)
				return false;
		} else if (!str.equals(other.toString()))
			return false;

		return true;
	}


}