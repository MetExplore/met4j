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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.transform;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.Edge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.parallel.MetaEdge;

import java.util.*;

/**
 * Class used to merges parallel edges.
 * @author clement
 */
public class EdgeMerger {
	
	/**
	 * Merge edges sharing same source and target, create new one with concatenated labels and summed weights
	 *
	 * @param g a graph.
	 * @param <V> vertex class
	 */
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> void mergeEdgesWithOverride(G g){
		
		//init source target map
		HashMap<V,HashMap<V,ArrayList<E>>> sourceTargetMap = new HashMap<>();
		for(E edge : g.edgeSet()){
			V source = edge.getV1();
			V target = edge.getV2();
			if(!sourceTargetMap.containsKey(source)){
				sourceTargetMap.put(source, new HashMap<>());
			}
			if(!sourceTargetMap.get(source).containsKey(target)){
				sourceTargetMap.get(source).put(target, new ArrayList<>());
			}
			sourceTargetMap.get(source).get(target).add(edge);
		}
		
		//create new edge as merging of all edges sharing same source and target
		for(Map.Entry<V, HashMap<V, ArrayList<E>>> entry : sourceTargetMap.entrySet()){
			V source = entry.getKey();
			for(V target: entry.getValue().keySet()){
				ArrayList<E> edgeList= entry.getValue().get(target);
				if(edgeList.size()>1){
					
					//compute new label and new weight
					double mergedWeight=0.0;
					double mergedScore=0.0;
					StringBuilder label= new StringBuilder();
					for(E edge : edgeList){
						mergedWeight+=g.getEdgeWeight(edge);
						mergedScore+=g.getEdgeScore(edge);
						if(label.length() == 0){
							label = new StringBuilder(edge.toString());
						}else{
							label.append("_").append(edge);
						}
					}
							
					//create new edge
					E newEdge = g.createEdge(source, target);
					g.addEdge(source, target, newEdge);
					g.setEdgeWeight(newEdge, mergedWeight);
					g.setEdgeScore(newEdge, mergedScore);
					g.removeAllEdges(edgeList);				
				}
			}
		}
	}

	/**
	 * Merge edges sharing same source and target, keeping only one. Use comparator to select the one to keep  (first once sorted).
	 *
	 * @param g a G object.
	 * @param comparator a {@link java.util.Comparator} object.
	 * @param <V> a V object.
	 * @param <E> a E object.
	 * @param <G> a G object.
	 */
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> void mergeEdgesWithOverride(G g, Comparator<E> comparator){

		//init source target map
		HashMap<V,HashMap<V,ArrayList<E>>> sourceTargetMap = new HashMap<>();
		for(E edge : g.edgeSet()){
			V source = edge.getV1();
			V target = edge.getV2();
			if(!sourceTargetMap.containsKey(source)){
				sourceTargetMap.put(source, new HashMap<>());
			}
			if(!sourceTargetMap.get(source).containsKey(target)){
				sourceTargetMap.get(source).put(target, new ArrayList<>());
			}
			sourceTargetMap.get(source).get(target).add(edge);
		}

		//remove edges sharing same source and target, keeping only one (first according to comparator)
		for(HashMap<V, ArrayList<E>> vArrayListHashMap : sourceTargetMap.values()){
			for(V target: vArrayListHashMap.keySet()){
				ArrayList<E> edgeList= vArrayListHashMap.get(target);
				if(edgeList.size()>1){

					//get 'best' edge
					edgeList.sort(comparator);

					//remove other edges
					edgeList.remove(0);
					g.removeAllEdges(edgeList);
				}
			}
		}
	}

	
	/**
	 * Merge edges sharing same source and target
	 *
	 * @param g a graph
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph} object.
	 * @param <V> the vertex class
	 */
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> MergedGraph<V,E> mergeEdges(G g){
		
		MergedGraph<V,E> mergedG = new MergedGraph<>();
		
		for(V v : g.vertexSet()){
			mergedG.addVertex(v);
		}
		
		//init source target map
		HashMap<V, HashMap<V, HashSet<E>>> sourceTargetMap = new HashMap<>();
		for(E edge : g.edgeSet()){
			V source = edge.getV1();
			V target = edge.getV2();
			if(!sourceTargetMap.containsKey(source)){
				sourceTargetMap.put(source, new HashMap<>());
			}
			if(!sourceTargetMap.get(source).containsKey(target)){
				sourceTargetMap.get(source).put(target, new HashSet<>());
			}
			sourceTargetMap.get(source).get(target).add(edge);
		}
		
		//create new edge as merging of all edges sharing same source and target
		for(Map.Entry<V, HashMap<V, HashSet<E>>> entry : sourceTargetMap.entrySet()){
			V source = entry.getKey();
			for(V target: entry.getValue().keySet()){
				Set<E> edgeList= entry.getValue().get(target);
					
				//compute new label and new weight
				double mergedWeight=0.0;
				double mergedScore=0.0;
				StringBuilder label= new StringBuilder();
				for(E edge : edgeList){
					mergedWeight+=g.getEdgeWeight(edge);
					mergedScore+=g.getEdgeScore(edge);
					if(label.length() == 0){
						label = new StringBuilder(edge.toString());
					}else{
						label.append("_").append(edge);
					}
				}
						
				//create new edge
				MetaEdge<V, E> newEdge = new MetaEdge<>(source, target, edgeList);
				mergedG.addEdge(source, target, newEdge);
				mergedG.setEdgeWeight(newEdge, mergedWeight);
				mergedG.setEdgeScore(newEdge, mergedScore);
			}
		}
		
		return mergedG;
	}


	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> Comparator<E> alphabeticalOrder(){
		return new Comparator<E>() {
			@Override
			public int compare(E e1, E e2) {
				return e1.toString().compareTo(e2.toString());
			}
		};
	}
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> Comparator<E> highWeightFirst(G g){
		return new Comparator<E>() {
			@Override
			public int compare(E e1, E e2) {
				return Double.compare(
						g.getEdgeWeight(e2),
						g.getEdgeWeight(e1)
				);
			}
		};
	}
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> Comparator<E> lowWeightFirst(G g){
		return new Comparator<E>() {
			@Override
			public int compare(E e1, E e2) {
				return Double.compare(
						g.getEdgeWeight(e1),
						g.getEdgeWeight(e2)
				);
			}
		};
	}

	/**
	 * Merge parallel and reversed edges, keep only one. This can be used to avoid edge duplications during undirected graph export
	 *
	 * @param g a G object.
	 * @param comparator a {@link java.util.Comparator} object.
	 * @param <V> a V object.
	 * @param <E> a E object.
	 * @param <G> a G object.
	 */
	public static <V extends BioEntity,E extends Edge<V>, G extends BioGraph<V,E>> void undirectedMergeEdgesWithOverride(G g, Comparator<E> comparator){
		List<List<E>> mergingGroups = new ArrayList<>();
		Set<E> visited = new HashSet<>();
		for(E edge : g.edgeSet()){
			if(!visited.contains(edge)){
				Double w = g.getEdgeWeight(edge);
				List<E> parallelAndReversed=new ArrayList<>();
				//get parallel edges
				parallelAndReversed.addAll(g.getAllEdges(edge.getV1(), edge.getV2()));
				//get reversed edges
				parallelAndReversed.addAll(g.getAllEdges(edge.getV2(), edge.getV1()));
				//set ignore edges in merging group during next iterations.
				visited.addAll(parallelAndReversed);
				mergingGroups.add(parallelAndReversed);
			}
		}
		for(List<E> mergingGroup : mergingGroups){
			//get 'best' edge
			if(comparator!=null) mergingGroup.sort(comparator);
			mergingGroup.remove(0);
			//remove other edges
			g.removeAllEdges(mergingGroup);
		}
	}
}
