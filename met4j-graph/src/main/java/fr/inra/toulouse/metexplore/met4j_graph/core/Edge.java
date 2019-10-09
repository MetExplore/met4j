/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.core;

import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

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
     **/
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
     **/
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
	 * @return the attributs
	 */
	public HashMap<String,Object> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributs the attributs to set
	 */
	public void setAttributes(HashMap<String,Object> attributs) {
        this.attributes = attributs;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
}
