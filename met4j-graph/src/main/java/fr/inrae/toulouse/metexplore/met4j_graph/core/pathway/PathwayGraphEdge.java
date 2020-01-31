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
package fr.inrae.toulouse.metexplore.met4j_graph.core.pathway;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

/**
 * The edge Class linking two {@link BioPathway}, linked by {@link BioMetabolite}
 * @author clement
 */
public class PathwayGraphEdge extends Edge<BioPathway>{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The reaction. */
    private final BioCollection<BioMetabolite> connectingCompounds;

    /**
     * Instantiates a new reaction edge.
     *
     * @param v1 the source vertex
     * @param v2 the target vertex
     * @param connectingCompounds the connecting compounds
     */
    public PathwayGraphEdge(BioPathway v1, BioPathway v2, BioCollection<BioMetabolite> connectingCompounds) {
        super(v1,v2);
        this.connectingCompounds=connectingCompounds;
    }
    
    /**
     * Instantiates a new reaction edge.
     *
     * @param v1 the source vertex
     * @param v2 the target vertex
     * @param connectingCompound the connecting compound
     */
    public PathwayGraphEdge(BioPathway v1, BioPathway v2, BioMetabolite connectingCompound) {
        super(v1,v2);
        this.connectingCompounds = new BioCollection<>();
        addConnectingCompounds(connectingCompound);
    }

    
    /**
     * Gets the reaction.
     *
     * @return the reaction
     */
    public BioCollection<BioMetabolite> getConnectingCompounds(){
    	return connectingCompounds;
    }
    
    /**
     * Gets the reaction.
     *
     * @return the reaction
     */
    public void addConnectingCompounds(BioMetabolite compound){
        connectingCompounds.add(compound);
    }

    public void addConnectingCompounds(BioCollection<BioMetabolite> compounds){
        connectingCompounds.addAll(compounds);
    }
    
    /**
     * Gets the reaction.
     *
     * @return the reaction
     */
    public void removeConnectingCompounds(BioMetabolite e){
        connectingCompounds.remove(e);
    }

}
