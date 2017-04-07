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
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

public class BioEntityCollection<E extends BioEntity> implements Collection<E> {
	
	
	private HashSet<E> entities;
	
	
	public BioEntityCollection() {
		entities = new HashSet<E>();
	}
	
	
	public BioEntityCollection(HashSet<E> e) {
		entities = e;
	}
	
	
	/**
	 * Check if the collection contains a BioEntity which has a given id
	 * @param id
	 * @return {@link Boolean}
	 */
	public Boolean containsId(String id) {
		return entities.stream().filter(o -> o.getId().equals(id)).findFirst().isPresent();
	}
	
	/**
	 * Check if the collection contains a BioEntity which has a given name
	 * @param id
	 * @return {@link Boolean}
	 */
	public Boolean containsName(String name) {
		return entities.stream().filter(o -> o.getId().equals(name)).findFirst().isPresent();
	}
	
	
	/**
	 * Get entities with a specific id
	 * @param id
	 * @return
	 */
	public BioEntityCollection<E> getEntitiesWithId(String id) {
		
		HashSet<E> e = new HashSet<E> (entities.stream().filter
				(o -> o.getId().equals(id)).collect(Collectors.toSet()));
			
		return new BioEntityCollection<E>(e);
	}
	
	/**
	 * Get entities with a specific name
	 * @param id
	 * @return
	 */
	public BioEntityCollection<E> getEntitiesWithName(String name) {
		
		HashSet<E> e = new HashSet<E> (entities.stream().filter
				(o -> o.getName().equals(name)).collect(Collectors.toSet()));
			
		return new BioEntityCollection<E>(e);
	}
	
	
	public HashSet<E> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(HashSet<E> entities) {
		this.entities = entities;
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
		return this.entities.iterator();
	}


	@Override
	public Object[] toArray() {
		return this.entities.toArray();
	}


	@Override
	public <T> T[] toArray(T[] a) {
		return this.entities.toArray(a);
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		return this.entities.retainAll(c);
	}


	@Override
	public void clear() {
		this.entities.clear();
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
