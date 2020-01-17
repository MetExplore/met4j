/**
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.core;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * @author clement
 *
 */
public class BioEdge<T extends BioEntity,M  extends BioEntity> extends Edge<T> {

private static final long serialVersionUID = 4129743061750652400L;

	public BioEdge(T v1, T v2, M modifier) {
		super(v1, v2);
	}

}
