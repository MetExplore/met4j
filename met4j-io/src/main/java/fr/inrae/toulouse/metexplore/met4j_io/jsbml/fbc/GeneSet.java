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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc;

import java.util.HashSet;
import java.util.stream.Collectors;


import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;

/**
 * This class represents a unique "AND" gene association required to activate a
 * given {@link BioReaction}.</br>
 * </br>
 * it is composed of a set of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene} that all need to be active for
 * this association to be active
 *
 * @author Benjamin mainly modified by LC
 * @since 3.0
 * @version $Id: $Id
 */
public class GeneSet extends HashSet<BioGene> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;

	/**
	 * {@inheritDoc}
	 *
	 * This method outputs the PAlson's representation of an "AND" gene
	 * association
	 */
	@Override
	public String toString() {

		return this.stream().map(x -> x.getId()).sorted().collect(Collectors.joining(" AND "));

	}

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getId() {
		return id;
	}

	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id a {@link java.lang.String} object.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {

		String str = toString();

		final int prime = 31;
		
		// TODO : not very satisfying since we use a mutable property...
		int result = prime * ((str == null) ? 0 : str.hashCode());
		return result;
	}

	/** {@inheritDoc} */
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
