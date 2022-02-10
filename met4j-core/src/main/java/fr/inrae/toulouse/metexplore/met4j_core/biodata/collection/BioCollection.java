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
package fr.inrae.toulouse.metexplore.met4j_core.biodata.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * <p>BioCollection class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioCollection<E extends BioEntity> implements Collection<E> {

	private Map<String, E> entities;

	private BioCollection(Map<String, E> entities) {
		this.entities = entities;
	}

	/**
	 * <p>Constructor for BioCollection.</p>
	 */
	public BioCollection() {
		entities = new HashMap<>();
	}

	/**
	 * <p>Constructor for BioCollection.</p>
	 *
	 * @param set a {@link java.util.Collection} object.
	 */
	public BioCollection(Collection<E> set) {
		entities = new HashMap<>();
		this.addAll(set);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder(this.getClass().getSimpleName()+"(");

		str.append(entities.values().stream()
				.map(BioEntity::getId)
				.collect(Collectors.joining(", ")));

		return str + ")";
	}

	/**
	 * Check if the collection contains a BioEntity which has a given id
	 *
	 * @param id id
	 * @return {@link java.lang.Boolean}
	 */
	public Boolean containsId(String id) {
		return entities.containsKey(id);
	}

	/**
	 * Check if the collection contains a BioEntity which has a given name
	 *
	 * @param name name
	 * @return {@link java.lang.Boolean}
	 */
	public Boolean containsName(String name) {
		return entities.values().stream().anyMatch(o -> o.getName().equals(name));
	}

	/**
	 * Get entity with a specific id
	 *
	 * @param id id
	 * @return the entity or null
	 */
	public E get(String id) {

		return entities.get(id);
	}

	/**
	 * Get the set of the ids of the entities in the collection
	 *
	 * @return the {@link java.util.Set} of the entities' ids
	 */
	public Set<String> getIds() {
		return this.entities.keySet();
	}

	/**
	 * Get entities with a specific name
	 *
	 * @param name name of the entity
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection}
	 */
	public BioCollection<E> getEntitiesFromName(String name) {

		return entities.values().stream().filter(o -> o.getName().equals(name)).collect(Collectors.toCollection( BioCollection<E>::new));

	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		return this.entities.size();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isEmpty() {
		return this.entities.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public Iterator<E> iterator() {
		return this.entities.values().iterator();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] toArray() {
		return this.entities.values().toArray();
	}

	/** {@inheritDoc} */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.entities.values().toArray(a);
	}

	/** {@inheritDoc} */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.entities.values().retainAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public void clear() {
		this.entities.clear();
	}

	/** {@inheritDoc} */
	@Override
	public boolean removeAll(Collection<?> c) {
		return entities.values().removeAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(Object o) {
		return entities.containsValue(o);
	}

	/** {@inheritDoc} */
	@Override
	public boolean add(E e) {
		if(entities.containsValue(e)) {
			return false;
		}
		else {
			if (entities.keySet().contains(e.getId())) {
				throw new IllegalArgumentException("An entity with the same id (" + e.getId() + ") is already present in the BioCollection");
			}
			entities.put(e.getId(), e);
		}
		return true;
	}

	/**
	 * add several entities to the collection
	 *
	 * @param newEntities : 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
	 * @return always true
	 */
	public boolean add(E... newEntities) {
		for(E e : newEntities) {
			entities.put(e.getId(), e);
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean remove(Object o) {
		Object removed =  entities.remove(((BioEntity) o).getId());

		return removed == null ? false : true;

	}

	/** {@inheritDoc} */
	@Override
	public boolean containsAll(Collection<?> c) {
		return entities.values().containsAll(c);
	}

	/** {@inheritDoc} */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c) {
			this.add(e);
		}
		return true;
	}

	/**
	 * <p>getView.</p>
	 *
	 * @return an unmodifiable copy of the {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection}
	 */
	public BioCollection<E> getView() {
		return new BioCollection<>(new HashMap<>(this.entities));
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {

		// checking if both the object references are
		// referring to the same object.
		if (this == obj)
			return true;

		if (obj == null || obj.getClass() != this.getClass())
			return false;

		BioCollection<?> c = (BioCollection<?>) obj;

		if (c.size() != this.size())
			return false;

		for (BioEntity e : c) {
			if (!this.contains(e))
				return false;
		}

		return true;

	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {

		StringBuilder idsString = new StringBuilder();

		ArrayList<String> ids = new ArrayList<>(this.getIds());

		Collections.sort(ids);

		for (String id : ids) {
			idsString.append(id);
		}

		return idsString.toString().hashCode();

	}

	/**
	 * <p>getMapView.</p>
	 *
	 * @return the map of entities
	 */
	public HashMap<String, E> getMapView() {
		return new HashMap<>(this.entities);
	}

}
