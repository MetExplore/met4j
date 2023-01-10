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
package fr.inrae.toulouse.metexplore.met4j_graph.core;

import java.util.*;

import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.EjmlMatrix;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * A utility class used to filter list of paths, according to various criterion such as length, weight, presence/absence of a given vertex or edges ...
 *
 * @author clement
 * @version $Id: $Id
 */
public class BioPathUtils {
	/** The "EQUALITY" operator. */
	public static final String EQUALITY = "=";
	
	/** The "INEQUALITY" operator. */
	public static final String INEQUALITY = "<>";
	
	/** The "GREATER" operator. */
	public static final String GREATER = ">";
	
	/** The "LESS" operator. */
	public static final String LESS = "<";
	
	/** The "GREATER OR EQUAL" operator. */
	public static final String GREATEROREQUAL = ">=";
	
	/** The "LESS OR EQUAL" operator. */
	public static final String LESSOREQUAL = "<=";
	
	/**
	 * <p>Constructor for BioPathUtils.</p>
	 */
	public BioPathUtils() {
	}
	
	/**
	 * Return paths from the input path set that contains all nodes contained in the input node set.
	 * Returned path can contains nodes not present in the input node set.
	 *
	 * @param paths the input path set
	 * @param nodes the input node set
	 * @return paths that contains all nodes given as parameter
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> getPathsContainingAllNodes(Collection<BioPath<V,E>> paths, Collection<V> nodes){
		List<BioPath<V,E>> filtered = new ArrayList<>();
		for(BioPath<V, E> path : paths){
			if(path.getVertexList().containsAll(nodes)){
				filtered.add(path);
			}
		}
		return filtered;
	}
	
	/**
	 * Return paths from the input path set that contains at least one node contained in the input node set.
	 *
	 * @param paths the input path set
	 * @param nodes the input node set
	 * @return paths that contains at least one of the nodes given as parameter
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> getPathsContainingNodes(Collection<BioPath<V,E>> paths, Collection<V> nodes){
		List<BioPath<V,E>> filtered = new ArrayList<>();
		for(BioPath<V, E> path : paths){
			Iterator<V> iterator = nodes.iterator();
			while(!filtered.contains(path) && iterator.hasNext()){
				if(path.getVertexList().contains(iterator.next())) filtered.add(path);
			}
		}
		return filtered;
	}
	
	/**
	 * Return paths from the input path set that contains all edges contained in the input edge set.
	 * Returned path can contains edges not present in the input edge set.
	 *
	 * @param paths the input path set
	 * @param edges the input edge set
	 * @return paths that contains all edges given as parameter
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> getPathsContainingAllEdges(Collection<BioPath<V,E>> paths, Collection<E> edges){
		List<BioPath<V,E>> filtered = new ArrayList<>();
		for(BioPath<V, E> path : paths){
			if(path.getEdgeList().containsAll(edges)){
				filtered.add(path);
			}
		}
		return filtered;
	}
	
	/**
	 * Return paths from the input path set that contains at least one edge contained in the input edge set.
	 *
	 * @param paths the input path set
	 * @param edges the input edge set
	 * @return paths that contains at least one of the edge given as parameter
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> getPathsContainingEdges(Collection<BioPath<V,E>> paths, Collection<E> edges){
		List<BioPath<V,E>> filtered = new ArrayList<>();
		for(BioPath<V, E> path : paths){
			Iterator<E> iterator = edges.iterator();
			while(!filtered.contains(path) && iterator.hasNext()){
				if(path.getEdgeList().contains(iterator.next())) filtered.add(path);
			}
		}
		return filtered;
	}		
	
	/**
	 * Filter path set according to a weight rank (in decreasing order) and a given operator.
	 *
	 * @param paths the path set
	 * @param n the rank used for filtering
	 * @param operator the operator
	 * @return the filtered list of paths
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> weightRankFilter(Collection<BioPath<V,E>> paths, int n, String operator){
		if(n<1) throw new IllegalArgumentException();
		List<Double> weights = new ArrayList<>(new HashSet<>(getWeightMap(paths).values()));
		Collections.sort(weights);
		Collections.reverse(weights);
		return weightFilter(paths, weights.get(n-1), operator);
	}

	/**
	 *  Filter path set according to a weight percentile and a given operator.
	 *
	 * @param paths the path set
	 * @param p the percentile used for filtering
	 * @param operator the operator
	 * @return the filtered list of paths
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> weightPercentileFilter(Collection<BioPath<V,E>> paths, double p, String operator){
		DescriptiveStatistics stat = new DescriptiveStatistics();
		for(BioPath<V, E> path : paths){
			stat.addValue(path.getWeight());
		}
		
		double t = stat.getPercentile(p);
		return weightFilter(paths, t, operator);
	}
	
	/**
	 * Filter path set according to a length rank (in decreasing order) and a given operator.
	 *
	 * @param paths the path set
	 * @param n the rank used for filtering
	 * @param operator the operator
	 * @return the filtered list of paths
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> lengthRankFilter(Collection<BioPath<V,E>> paths, int n, String operator){
		List<Double> lengths = new ArrayList<>(new HashSet<>(getLengthMap(paths).values()));
		Collections.sort(lengths);
		Collections.reverse(lengths);
		return lengthFilter(paths, lengths.get(n-1), operator);
	}
	
	/**
	 *  Filter path set according to a length percentile and a given operator.
	 *
	 * @param paths the path set
	 * @param p the percentile used for filtering
	 * @param operator the operator
	 * @return the filtered list of paths
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> lengthPercentileFilter(Collection<BioPath<V,E>> paths, double p, String operator){
		DescriptiveStatistics stat = new DescriptiveStatistics();
		for(BioPath<V, E> path : paths){
			stat.addValue(path.getLength());
		}
		
		double t = stat.getPercentile(p);
		return lengthFilter(paths, t, operator);
	}
	
	/*
	 * return a map with path as key and weight as value
	 */
	private static <V extends BioEntity, E extends Edge<V>> Map<BioPath<V, E>, Double> getWeightMap(Collection<BioPath<V, E>> paths){
		Map<BioPath<V,E>,Double> weights = new HashMap<>();
		for(BioPath<V, E> path : paths){
			weights.put(path, path.getWeight());
		}
		return weights;
	}
	
	/*
	 * return a map with path as key and length as value
	 */
	private static <V extends BioEntity, E extends Edge<V>> Map<BioPath<V, E>, Double> getLengthMap(Collection<BioPath<V, E>> paths){
		Map<BioPath<V,E>,Double> length = new HashMap<>();
		for(BioPath<V, E> path : paths){
			Double l = Integer.valueOf(path.getLength()).doubleValue();
			length.put(path, l);
		}
		return length;
	}

	/**
	 * Create a square distance matrix, where rows and columns correspond to the ends of paths, and elements correspond
	 * to path length or weight
	 * @param shortestsPaths the set of paths
	 * @return a square distance matrix
	 */
	public static <V extends BioEntity, E extends Edge<V>> BioMatrix getDistanceMatrixFromPaths(List<BioPath<V,E>> shortestsPaths){
		//get labels in a defined order
		TreeSet<V> ends = new TreeSet<>(Comparator.comparing(V::getId));
		for(int i = 0; i<shortestsPaths.size(); i++){
			ends.add(shortestsPaths.get(i).getStartVertex());
			ends.add(shortestsPaths.get(i).getEndVertex());
		}
		return(getDistanceMatrixFromPaths(ends,ends,shortestsPaths));
	}

	/**
	 * Create a distance matrix, where each cell corresponds to paths length or weight
	 * @param rows set of starting nodes corresponding to matrix rows
	 * @param cols set of ending nodes corresponding to matrix columns
	 * @param shortestsPaths the set of paths
	 * @throws IllegalArgumentException if paths contains source not listed as row or ends not listed as column
	 * @return a square distance matrix
	 */
	public static <V extends BioEntity, E extends Edge<V>> BioMatrix getDistanceMatrixFromPaths(Set<V> rows, Set<V>  cols, List<BioPath<V,E>> shortestsPaths) throws IllegalArgumentException{

		//instanciate distanceMatrix
		BioMatrix distMatrix = new EjmlMatrix(rows.size(),cols.size());
		int i=0;
		for(V r1 : rows){
			distMatrix.setRowLabel(i,r1.getId());
			int j=0;
			for(V r2 : cols){
				if(i==0) distMatrix.setColumnLabel(j,r2.getId());;
				if(r1!=r2){
					distMatrix.set(i, j, Double.POSITIVE_INFINITY);
				}else{
					distMatrix.set(i, j,0.0);
				}
				j++;
			}
			i++;
		}
		//get the label-index matching
		HashMap<String,Integer> rowLabelMap = distMatrix.getRowLabelMap();
		HashMap<String,Integer> columnLabelMap = distMatrix.getColumnLabelMap();
		//fill the biomatrix
		for(BioPath<V,E> bp:shortestsPaths) {
			if(bp!=null && !bp.isEmpty()){
				Double dist = bp.getWeight();
				Integer i2 = rowLabelMap.get(bp.getStartVertex().getId());
				Integer j2 = columnLabelMap.get(bp.getEndVertex().getId());
				if(i2==null || j2==null) throw new IllegalArgumentException("path starting/ending node not found in row/columns list");
				distMatrix.set(i2,j2, dist);
			}
		}

		return distMatrix;
	}
	
	/**
	 * Filter any may containing Double as values, given a threshold and an operator
	 *
	 * @param map the map
	 * @param value the threshold
	 * @param operator the operator
	 * @return a collection of keys that fit the constraint given by the threshold and the operator
	 * @param <T> a T object.
	 */
	public static <T> Collection<T> filter(Map<T,Double> map, double value, String operator){
		List<T> filtered = new ArrayList<>();
		switch(operator){
		case EQUALITY:
			for(Map.Entry<T, Double> entry : map.entrySet()){
				if(entry.getValue()==value){
					filtered.add(entry.getKey());
				}
			}
			break;
		case INEQUALITY:
			for(Map.Entry<T, Double> entry : map.entrySet()){
				if(entry.getValue()!=value){
					filtered.add(entry.getKey());
				}
			}
			break;
		case GREATER:
			for(Map.Entry<T, Double> entry : map.entrySet()){
				if(entry.getValue()>value){
					filtered.add(entry.getKey());
				}
			}
			break;
		case LESS:
			for(Map.Entry<T, Double> entry : map.entrySet()){
				if(entry.getValue()<value){
					filtered.add(entry.getKey());
				}
			}
			break;
		case GREATEROREQUAL:
			for(Map.Entry<T, Double> entry : map.entrySet()){
				if(entry.getValue()>=value){
					filtered.add(entry.getKey());
				}
			}
			break;
		case LESSOREQUAL:
			for(Map.Entry<T, Double> entry : map.entrySet()){
				if(entry.getValue()<=value){
					filtered.add(entry.getKey());
				}
			}
			break;
		default :
			System.err.println("unrecoginzed filter operator");
			throw (new IllegalArgumentException());
		}
		
		return filtered;
	}
	
	/**
	 * Filter path set according to a length value and a comparison operator.
	 *
	 * @param paths the set of paths
	 * @param value the value used for filtering
	 * @param operator the operator
	 * @return the filtered set
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> lengthFilter(Collection<BioPath<V,E>> paths, double value, String operator){
		return filter(paths,value,operator,false);
	}
	
	/**
	 * Filter path set according to a weight value and a comparison operator.
	 *
	 * @param paths the set of paths
	 * @param value the value used for filtering
	 * @param operator the operator
	 * @return the filtered set
	 * @param <V> a V object.
	 * @param <E> a E object.
	 */
	public static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> weightFilter(Collection<BioPath<V,E>> paths, double value, String operator){
		return filter(paths,value,operator,true);
	}
	
	/*
	 * generic path set filtering. The use of weight or length as filtering criterion is given as boolean value
	 */
	private static <V extends BioEntity, E extends Edge<V>> Collection<BioPath<V,E>> filter(Collection<BioPath<V,E>> paths, double value, String operator, boolean weighted){
		
		List<BioPath<V,E>> filtered = new ArrayList<>();
		switch(operator){
		case EQUALITY:
			for(BioPath<V,E> entry : paths){
				double pathValue = weighted ? entry.getWeight() : entry.getLength();
				if(pathValue==value){
					filtered.add(entry);
				}
			}
			break;
		case INEQUALITY:
			for(BioPath<V,E> entry : paths){
				double pathValue = weighted ? entry.getWeight() : entry.getLength();
				if(pathValue!=value){
					filtered.add(entry);
				}
			}
			break;
		case GREATER:
			for(BioPath<V,E> entry : paths){
				double pathValue = weighted ? entry.getWeight() : entry.getLength();
				if(pathValue>value){
					filtered.add(entry);
				}
			}
			break;
		case LESS:
			for(BioPath<V,E> entry : paths){
				double pathValue = weighted ? entry.getWeight() : entry.getLength();
				if(pathValue<value){
					filtered.add(entry);
				}
			}
			break;
		case GREATEROREQUAL:
			for(BioPath<V,E> entry : paths){
				double pathValue = weighted ? entry.getWeight() : entry.getLength();
				if(pathValue>=value){
					filtered.add(entry);
				}
			}
			break;
		case LESSOREQUAL:
			for(BioPath<V,E> entry : paths){
				double pathValue = weighted ? entry.getWeight() : entry.getLength();
				if(pathValue<=value){
					filtered.add(entry);
				}
			}
			break;
		default :
			System.err.println("unrecoginzed filter operator");
			throw (new IllegalArgumentException());
		}
		
		return filtered;
	}
}
