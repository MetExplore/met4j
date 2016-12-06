/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
package fr.inra.toulouse.metexplore.met4j_core.utils;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import java.util.TreeMap;

/**
 * Baobab Team
 * parseBioNet
 * @author ludo
 * 11 d�c. 07
 * 
 * 
 * 
 */
public class Counter {
	
	
	private HashMap<String, Integer> mapIds;
	private TreeMap<Integer, Set<String>> mapCounts; 
	
	/**
	 * Constructor
	 *
	 */
	public Counter() {
		
		this.setMapIds(new HashMap<String, Integer>());
		this.setMapCounts(new TreeMap<Integer, Set<String>>());
		
	}
	
	/**
	 * Add an id and its count
	 * @param id
	 * @param n
	 */
	public void put(String id, Integer n) {
		this.getMapIds().put(id, n);
		
		if(this.getMapCounts().containsKey(n) == false) {
			HashSet<String> tab = new HashSet<String>();
			tab.add(id);
			this.getMapCounts().put(n, tab);
		}
		else {
			this.getMapCounts().get(n).add(id);
		}
	}
	
	/**
	 * @param n
	 * @return
	 */
	public Set<String> get(Integer n) {
		
		return this.getMapCounts().get(n);
		
	}
	
	public Set<Integer> getCounts() {
		
		return this.getMapCounts().keySet();
		
	}
	
	public Set<String> getIds() {
		
		return this.getMapIds().keySet();
		
	}
	
	
	public Integer get(String id) {
		
		return this.getMapIds().get(id);
		
	}   
	
	
	public TreeMap<Integer, Set<String>> getMapCounts() {
		return mapCounts;
	}

	public void setMapCounts(TreeMap<Integer, Set<String>> mapCounts) {
		this.mapCounts = mapCounts;
	}

	public HashMap<String, Integer> getMapIds() {
		return mapIds;
	}

	public void setMapIds(HashMap<String, Integer> mapIds) {
		this.mapIds = mapIds;
	}
}
