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
package fr.inra.toulouse.metexplore.met4j_core.biodata.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class BioCollection<E extends BioEntity> implements Collection<E> {

	private Map<String, E> entities;

	private BioCollection(Map<String, E> entities) {
		this.entities = entities;
	}

	public BioCollection() {
		entities = new HashMap<String, E>();
	}

	public BioCollection(Collection<E> set) {
		entities = new HashMap<String, E>();
		this.addAll(set);
	}

	@Override
	public String toString() {
		String str = "BioCollection\n[\n";
		for (E e : this.entities.values()) {
			str += e.toString();
			str += "\n";
		}

		return str + "]";
	}
	

	/**
	 * Check if the collection contains a BioEntity which has a given id
	 * 
	 * @param id
	 * @return {@link Boolean}
	 */
	public Boolean containsId(String id) {
		return entities.containsKey(id);
	}

	/**
	 * Check if the collection contains a BioEntity which has a given name
	 * 
	 * @param id
	 * @return {@link Boolean}
	 */
	public Boolean containsName(String name) {
		return entities.values().stream().filter(o -> o.getId().equals(name)).findFirst().isPresent();
	}

	/**
	 * Get entity with a specific id
	 * 
	 * @param id
	 * @return
	 */
	public E getEntityFromId(String id) {

		E entity = entities.get(id);

		return entity;
	}

	/**
	 * Get the set of the ids of the entities in the collection
	 */
	public Set<String> getIds() {
		return this.entities.keySet();
	}

	/**
	 * Get entities with a specific name
	 * 
	 * @param id
	 * @return
	 */
	public BioCollection<E> getEntitiesFromName(String name) {

		HashSet<E> e = new HashSet<E>(
				entities.values().stream().filter(o -> o.getName().equals(name)).collect(Collectors.toSet()));

		return new BioCollection<E>(e);
	}

	@Override
	public int size() {
		return this.entities.size();
	}

	@Override
	public boolean isEmpty() {
		return this.entities.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return this.entities.values().iterator();
	}

	@Override
	public Object[] toArray() {
		return this.entities.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.entities.values().toArray(a);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.entities.values().retainAll(c);
	}

	@Override
	public void clear() {
		this.entities.clear();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return entities.values().removeAll(c);
	}

	@Override
	public boolean contains(Object o) {
		return entities.values().contains(o);
	}

	@Override
	public boolean add(E e) {
		String id = e.getId();
		if (entities.containsKey(id))
			throw new IllegalArgumentException("Duplicated identifier in BioCollection. Identifiers must be unique");
		entities.put(e.getId(), e);
		return true;
	}

	@Override
	public boolean remove(Object o) {
		Boolean flag = entities.values().remove(o);

		if (!flag) {
			throw new IllegalArgumentException("Impossible to remove an object absent from a collection");
		}

		return flag;

	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return entities.values().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			this.add(e);
		}
		return true;
	}

	public BioCollection<E> getView() {
		return new BioCollection<E>(Collections.unmodifiableMap(this.entities));
	}

	@Override
	public boolean equals(Object obj) {

		// checking if both the object references are
		// referring to the same object.
		if (this == obj)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;
		
		BioCollection<?> c = (BioCollection<?>) obj;
		
		if(c.size() != this.size())
			return false;
		
		for(BioEntity e : c)
		{
			if(! this.contains(e))
				return false;
		}
			
		return true;
		
	}
	
	@Override
	  public int hashCode() {
	    
		int h=0;
		
		for(BioEntity e : this.entities.values()) {
			h += e.hashCode();
		}
		return h;
		
	  }  

}
