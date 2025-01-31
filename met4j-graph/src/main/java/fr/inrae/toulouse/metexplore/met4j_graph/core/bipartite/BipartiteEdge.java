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
package fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;

/**
 * The Class BipartiteEdge.
 *
 * @author clement
 * @version $Id: $Id
 */
public class BipartiteEdge extends Edge<BioEntity> {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2619530622614208364L;
	
	/**  is reversible. */
	public boolean reversible;
	
	/**  link side compound. */
	public boolean side;

	/**
	 * Instantiates a new bipartite edge.
	 *
	 * @param v1 the source vertex
	 * @param v2 the target vertex
	 */
	public BipartiteEdge(BioMetabolite v1, BioReaction v2){
		super(v1,v2);
        this.reversible =v2.isReversible();
	}
	/**
	 * <p>Constructor for BipartiteEdge.</p>
	 *
	 * @param v1 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 * @param v2 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 */
	public BipartiteEdge(BioReaction v1, BioMetabolite v2){
		super(v1,v2);
        this.reversible =v1.isReversible();
	}
	/**
	 * <p>Constructor for BipartiteEdge.</p>
	 *
	 * @param v1 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param v2 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 * @param reversible a boolean.
	 */
	public BipartiteEdge(BioMetabolite v1, BioReaction v2, boolean reversible){
		super(v1,v2);
		this.reversible=reversible;
		
	}
	/**
	 * <p>Constructor for BipartiteEdge.</p>
	 *
	 * @param v1 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} object.
	 * @param v2 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 * @param reversible a boolean.
	 */
	public BipartiteEdge(BioReaction v1, BioMetabolite v2, boolean reversible){
		super(v1,v2);
		this.reversible=reversible;
	}
	/**
	 * <p>Constructor for BipartiteEdge.</p>
	 *
	 * @param v1 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
	 * @param v2 a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} object.
	 * @param reversible a boolean.
	 */
	public BipartiteEdge(BioEntity v1, BioEntity v2, boolean reversible){
		super(v1,v2);
		if(!(v1 instanceof BioMetabolite && v2 instanceof BioReaction)
		 && !(v1 instanceof BioReaction && v2 instanceof BioMetabolite)){
			throw new IllegalArgumentException("Edges in bipartite graph can only connect a compound to a reaction");
		}
		this.reversible=reversible;
	}
	
	/**
	 * Sets the reversibility.
	 *
	 * @param reversible if the edge belong to a reversible reaction
	 */
	public void setReversible(boolean reversible) {
		this.reversible = reversible;
	}
	
	/**
	 * Checks if is reversible.
	 *
	 * @return true, if is reversible
	 */
	public boolean isReversible(){
		return reversible;
	}
	
	/**
	 * Sets if involve side compound.
	 *
	 * @param side if the edge involve a side transition (involving a side compound for example)
	 */
	public void setSide(boolean side) {
		this.side = side;
	}
	
	/**
	 * Checks if involve side compound.
	 *
	 * @return true, if involve side compound.
	 */
	public boolean isSide(){
		return side;
	}
	
}
