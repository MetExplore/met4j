/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.core.compressed;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class PathEdge<V extends BioEntity,E extends Edge<V>> extends Edge<V>{

	private BioPath<V, E> path;
	/** if reversible. */
	public boolean reversible;
	private static final long serialVersionUID = 1L;
	
	public PathEdge(V v1, V v2) {
		super(v1,v2);
		this.path=null;
	}
	
	public PathEdge(V v1, V v2, BioPath<V, E> path) {
		super(v1,v2);
		this.path=path;
	}
	
	/**
	 * Sets the path edges.
	 *
	 * @param l the new path edges
	 */
	public void setPath(BioPath<V, E> sp){
		path=sp;
	}
	
	/**
	 * Gets the path edges.
	 *
	 * @return the path edges
	 */
	public BioPath<V, E> getPath(){
		return path;
	}
	
	@Override
	public String toString(){
		return this.getPath().toString();
	}
	

}
