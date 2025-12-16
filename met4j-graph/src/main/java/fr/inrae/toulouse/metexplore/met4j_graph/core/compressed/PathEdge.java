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
package fr.inrae.toulouse.metexplore.met4j_graph.core.compressed;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;

/**
 * <p>PathEdge class.</p>
 *
 * @author lcottret
 */
public class PathEdge<V extends BioEntity,E extends Edge<V>> extends Edge<V>{

	private BioPath<V, E> path;
	/** if reversible. */
	public boolean reversible;
	private static final long serialVersionUID = 1L;
	
	/**
	 * <p>Constructor for PathEdge.</p>
	 *
	 * @param v1 a V object.
	 * @param v2 a V object.
	 */
	public PathEdge(V v1, V v2) {
		super(v1,v2);
        this.path =null;
	}
	
	/**
	 * <p>Constructor for PathEdge.</p>
	 *
	 * @param v1 a V object.
	 * @param v2 a V object.
	 * @param path a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.BioPath} object.
	 */
	public PathEdge(V v1, V v2, BioPath<V, E> path) {
		super(v1,v2);
		this.path=path;
	}
	
	/**
	 * Sets the path edges.
	 *
	 * @param sp the new path edges
	 */
	public void setPath(BioPath<V, E> sp){
        path =sp;
	}
	
	/**
	 * Gets the path edges.
	 *
	 * @return the path edges
	 */
	public BioPath<V, E> getPath(){
		return path;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString(){
		return this.path.toString();
	}
	

}
