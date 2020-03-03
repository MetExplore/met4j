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
package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import java.util.*;

import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * The root class
 */

public abstract class BioEntity {

	private final String id;
	private String name;
	private ArrayList<String> synonyms = new ArrayList<>();
	private String comment;

	private HashMap<String, Set<BioRef>> refs;

	private HashMap<String, Object> attributes;

	/**
	 * Constructor from an id and a name
	 * @param id String not null
	 * @param name String
	 */
	public BioEntity(String id, String name) {

		if (StringUtils.isVoid(id)) {
			throw new IllegalArgumentException("Invalid id for building a BioEntity");
		}

		this.id = id;

		this.setName(name);
		this.setRefs(new HashMap<>());

		attributes = new HashMap<>();
	}

	/**
	 * Constructor from an id
	 * @param id String not null
	 */
	public BioEntity(String id) {

		if (id == null) {
			throw new NullPointerException();
		}

		this.id = id;
		this.setName(id);
		this.setRefs(new HashMap<>());

		attributes = new HashMap<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BioEntity bioEntity = (BioEntity) o;
		return id.equals(bioEntity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	/**
	 * Set the name of the entity
	 * 
	 * @param n String
	 *
	 */
	public void setName(String n) {
		this.name = n;
	}

	/**
	 * Get the name of the entity
	 * 
	 * @return the name of the entity
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Get the list of the synonyms
	 * 
	 * @return an ArrayList of the synonyms of the entity
	 */
	public ArrayList<String> getSynonyms() {
		return this.synonyms;
	}

	/**
	 * Add a synonym in the list
	 * 
	 * @param s synonym to add
	 */
	public void addSynonym(String s) {
		this.synonyms.add(s);
	}

	/**
	 * Set the comment on the entity
	 * 
	 * @param c String
	 */
	public void setComment(String c) {
		this.comment = c;
	}

	/**
	 * Get the comment on the entity
	 * 
	 * @return the comment on the entity
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}

	/**
	 * TODO : voir la coherence du code entre les deux methodes addRef. Celle ci
	 * devrait se terminer par un this.addRef(ref)
	 * 
	 * @param dbName name of the database
	 * @param dbId id of the database
	 * @param confidenceLevel confidence level
	 * @param relation Type of relation
	 * @param origin Origin database
	 */
	public void addRef(String dbName, String dbId, int confidenceLevel, String relation, String origin) {
		BioRef ref = new BioRef(origin, dbName, dbId, confidenceLevel);
		ref.setLogicallink(relation);
		this.addRef(ref);
	}

	/**
	 *
	 * Add a reference
	 * @param ref a {@link BioRef}
	 */
	public void addRef(BioRef ref) {
		String dbName = ref.getDbName();
		if (!this.hasRef(ref)) {
			if (this.refs.containsKey(dbName)) {
				refs.get(dbName).add(ref);
			} else {
				Set<BioRef> refList = new HashSet<>();
				refList.add(ref);
				this.refs.put(dbName, refList);
			}
		}
	}

	/**
	 * Get all refs
	 * @return a {@link HashMap} for which the key is the database name and the values the set of {@link BioRef}
	 * associated to this database
	 */
	public HashMap<String, Set<BioRef>> getRefs() {
		return this.refs;
	}

	/**
	 * Get all refs associated to a database
	 *
	 * @param dbName  the database name
	 *
	 * @return a {@link Set} of {@link BioRef}
	 */
	public Set<BioRef> getRefs(String dbName) {
		return this.refs.getOrDefault(dbName, null);
	}

	/**
	 * Check if the entity has a reference whose the database name is dbName and that contains a refence
	 * whose the id is refId
	 * @param dbName the database name
	 * @param refId the reference id
	 * @return true if the entity has the reference
	 */
	public boolean hasRef(String dbName, String refId) {
		if (this.refs == null || !this.refs.containsKey(dbName)) {
			return false;
		}
		for (BioRef ref : this.refs.get(dbName)) {
			if (ref.getId().equals(refId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the entity contains a reference
	 * @param unkRef a {@link BioRef}
	 * @return true if the entity has the reference
	 */
	public boolean hasRef(BioRef unkRef) {

		if (this.refs == null || !this.refs.containsKey(unkRef.dbName)) {
			return false;
		}
		for (BioRef ref : this.refs.get(unkRef.dbName)) {
			if (ref.equals(unkRef)) {
				return true;
			}
		}
		return false;
	}

	public void setRefs(HashMap<String, Set<BioRef>> refs) {
		this.refs = refs;
	}

	@Override
	public String toString() {
		return this.getId();
	}

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}

	public Object setAttribute(String key, Object value) {
		return attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

}
