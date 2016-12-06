/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_graph.core.compressed;

import org.jgrapht.EdgeFactory;

import fr.inra.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inra.toulouse.metexplore.met4j_graph.core.GraphFactory;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class CompressedGraph<V extends BioEntity, E extends Edge<V>> extends BioGraph<V,PathEdge<V,E>> {

	private static final long serialVersionUID = 1L;
	private BioGraph<V,E> originalGraph;
	
	
	public CompressedGraph(BioGraph<V,E> originalGraph) {
		super(new PathEdgeFactory<V,E>(originalGraph));
		this.originalGraph=originalGraph;
	}	

	
	public GraphFactory<V, PathEdge<V,E>, CompressedGraph<V,E>> getFactory(){
		return new GraphFactory<V, PathEdge<V,E>, CompressedGraph<V,E>>() {
			@Override
			public CompressedGraph<V, E> createGraph() {
				return new CompressedGraph<V,E>(originalGraph);
			}
		};
	}

	@Override
	public EdgeFactory<V, PathEdge<V, E>> getEdgeFactory() {
		return new PathEdgeFactory<V, E>(originalGraph);
	}
	@Override
	public PathEdge<V, E> copyEdge(PathEdge<V, E> edge) {
		return new PathEdge<V,E>(edge.getV1(), edge.getV2(), edge.getPath());
	}


	@Override
	public PathEdge<V, E> reverseEdge(PathEdge<V, E> edge) {
		PathEdge<V, E> reversed = new PathEdge<V, E>(edge.getV2(), edge.getV1(), edge.getPath());
		return reversed;
	}
	
//	public EdgeFactory<V, PathEdge<V,E>> getFactory() {
//		EdgeFactory<V, PathEdge<V,E>> factory = new EdgeFactory<V, PathEdge<V,E>>(Class<? extends V> vertexClass,Class<? extends E> edgeClass) {
//			@Override
//			public PathEdge<V, E> createEdge(V arg0, V arg1) {
//				PathEdge<V, E> edge = new PathEdge<V, E>(arg0,arg1);
//				return edge;
//			}
//		};
//		return factory;
//	}
//		
//	
//	public class PathEdgeFactory{
//		public PathEdgeFactory(){
//			
//		}
//		@Override
//		public PathEdge<V, E> createEdge(V arg0, V arg1) {
//			PathEdge<V, E> edge = new PathEdge<V, E>(arg0,arg1);
//			return edge;
//		}
//		
//	}
}
