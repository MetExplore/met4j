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
package fr.inrae.toulouse.metexplore.met4j_graph.core.reaction;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;

import java.util.Objects;

/**
 * The edge Class linking two {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}, associated with a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
 *
 * @author clement
 */
public class CompoundEdge extends Edge<BioReaction>{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The compound. */
    private final BioMetabolite c;
    

    /**
     * Instantiates a new reaction edge.
     *
     * @param v1 the source vertex
     * @param v2 the target vertex
     * @param c the compound
     */
    public CompoundEdge(BioReaction v1, BioReaction v2, BioMetabolite c) {
        super(v1,v2);
        this.c=c;
    }

    /* (non-Javadoc)
     * @see org.jgrapht.graph.DefaultEdge#toString()
     */
    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String toString() {
        return c.getId();
    }
    
    /**
     * Gets the reaction.
     *
     * @return the reaction
     */
    public BioMetabolite getCompound(){
    	return c;
    }
    
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
    public boolean equals(Object obj) {
        // Vérification de l'égalité des références
        if (obj==this) {
            return true;
        }
        // Vérification du type du paramètre
        if (obj instanceof CompoundEdge) {
            // Vérification des valeurs des attributs
            CompoundEdge e = ((CompoundEdge) obj);
            if(e.getV1()== this.getV1() && e.getV2()== this.getV2() && e.c == this.c){
            	return true;
            }else return e.getV1().getId().equals(this.getV1().getId()) && e.getV2().getId().equals(this.getV2().getId())
                    && e.toString().equals(this.toString());
        }
        return false;
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode(java.lang.Object)
	 */
	/** {@inheritDoc} */
	@Override
    public int hashCode() {
		return Objects.hash(getV1().getId(), this.getV2().getId(), this.c.getId());
	}

}
