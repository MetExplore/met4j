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

/*
 * 
 */
package fr.inrae.toulouse.metexplore.met4j_graph.core;

import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * <p>Abstract Edge class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public abstract class Edge<T extends BioEntity> extends DefaultWeightedEdge{
	
	private static final long serialVersionUID = -8321862744429021818L;
    private double score;
	private final T v1;
	private final T v2;
	private String label;
	
    /** The attributes map. */
    private HashMap<String,Object> attributes;
	
	/**
	 * Instantiates a new edge.
	 *
	 * @param v1 the source vertex
	 * @param v2 the target vertex
	 */
	public Edge(T v1, T v2) {
	       this.v1 = v1;
	       this.v2 = v2;
        this.attributes = new HashMap<>();
	}
	
	/**
	 * Instantiates a new edge.
	 *
	 * @param v1 the source vertex
	 * @param v2 the target vertex
	 * @param l the edge label
	 */
	public Edge(T v1, T v2, String l) {
	       this.v1 = v1;
	       this.v2 = v2;
        this.label =l;
        this.attributes = new HashMap<>();
	}
	
    /**
     * Gets the source vertex.
     *
     * @return the source vertex
     */
    public T getV1() {
        return v1;
    }

    /**
     * Gets the target vertex.
     *
     * @return the target vertex
     */
    public T getV2() {
        return v2;
    }
    
	/**
	 * Gets the score.
	 *
	 * @return the score
	 */
	public double getScore() {
		return score;
	}

	/**
	 * Sets the score.
	 *
	 * @param score the new score
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * <p>Getter for the field <code>attributes</code>.</p>
	 *
	 * @return the attributs
	 */
	public HashMap<String,Object> getAttributes() {
		return attributes;
	}

	/**
	 * <p>Setter for the field <code>attributes</code>.</p>
	 *
	 * @param attributs the attributs to set
	 */
	public void setAttributes(HashMap<String,Object> attributs) {
        this.attributes = attributs;
	}

	/**
	 * <p>Getter for the field <code>label</code>.</p>
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * <p>Setter for the field <code>label</code>.</p>
	 *
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
}
