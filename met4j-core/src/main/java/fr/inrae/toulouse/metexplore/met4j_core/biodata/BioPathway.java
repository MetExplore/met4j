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
package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.util.Objects;

/**
 * A set or series of interactions, often forming a network, which biologists
 * have found useful to group together for organizational, historic, biophysical
 * or other reasons.
 */

public class BioPathway extends BioEntity {

	private BioCollection<BioReaction> reactions = new BioCollection<>();

	public BioPathway(String id) {
		super(id);
	}
	
	public BioPathway(String id, String name) {
		super(id, name);
	}

	protected BioCollection<BioReaction> getReactions() {
		return reactions;
	}

	protected void setReactions(BioCollection<BioReaction> reactions) {
		this.reactions = reactions;
	}

	protected void addReaction(BioReaction reaction) {
		this.reactions.add(reaction);

	}

	protected void removeReaction(BioReaction reaction) {
		this.reactions.remove(reaction);
	}

	/**
	 * Get metabolites involved in pathways
	 */
	protected BioCollection<BioMetabolite> getMetabolites() {
		BioCollection<BioMetabolite> metaboliteSet = new BioCollection<>();

		this.getReactions().forEach(r -> metaboliteSet.addAll(r.getEntities()));

		return metaboliteSet;
	}

	/**
	 * Get genes involved in pathways
	 */

	protected BioCollection<BioGene> getGenes() {
		BioCollection<BioGene> geneSet = new BioCollection<>();

		this.getReactions().forEach(r -> geneSet.addAll(r.getGenes()));

		return geneSet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		BioPathway that = (BioPathway) o;
		return Objects.equals(reactions, that.reactions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), reactions);
	}
}
