/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_graph.core.bipartite;

import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

/**
 * The Class BipartiteEdge.
 * @author clement
 */
public class BipartiteEdge extends Edge<BioEntity>{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2619530622614208364L;
	
	/**  is reversible. */
	public boolean reversible=false;
	
	/**  link side compound. */
	public boolean side=false;

	/**
	 * Instantiates a new bipartite edge.
	 *
	 * @param v1 the source vertex
	 * @param v2 the target vertex
	 */
	public BipartiteEdge(BioMetabolite v1, BioReaction v2){
		super(v1,v2);
		this.reversible=v2.isReversible();
	}
	public BipartiteEdge(BioReaction v1, BioMetabolite v2){
		super(v1,v2);
		this.reversible=v1.isReversible();
	}
	public BipartiteEdge(BioMetabolite v1, BioReaction v2, boolean reversible){
		super(v1,v2);
		this.reversible=reversible;
		
	}
	public BipartiteEdge(BioReaction v1, BioMetabolite v2, boolean reversible){
		super(v1,v2);
		this.reversible=reversible;
	}
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
	 * @param reversible
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
	 * @param side
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
