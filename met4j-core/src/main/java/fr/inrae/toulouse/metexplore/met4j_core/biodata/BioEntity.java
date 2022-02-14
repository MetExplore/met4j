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
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * The root class
 *
 * @author lcottret
 * @version $Id: $Id
 */
public abstract class BioEntity {

    @Getter
    final String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private ArrayList<String> synonyms = new ArrayList<>();

    @Getter
    @Setter
    private String comment;

    @Getter
    @Setter
    private HashMap<String, Set<BioRef>> refs;

    @Getter
    @Setter
    private HashMap<String, Object> attributes;

    /**
     * Constructor from an id and a name
     *
     * @param id   String not null
     * @param name String
     */
    public BioEntity(String id, String name) {

        if (StringUtils.isVoid(id)) {
            String newId = UUID.randomUUID().toString().replaceAll("-", "_");
            this.id = newId;
            System.err.println("Invalid id for building a BioEntity: " + id);
            System.err.println("Creates a random unique id : " + newId);
        } else {
            this.id = id;
        }
        this.setName(name);
        this.setRefs(new HashMap<>());

        attributes = new HashMap<>();
    }

    /**
     * Constructor from an id
     *
     * @param id String not null
     */
    public BioEntity(String id) {
        this(id, id);
    }

    /**
     * Deep copy
     * <p>
     * The refs and attributes are not deeply copied
     *
     * @param e the original bioentity
     */
    public BioEntity(@NonNull BioEntity e) {
        this.id = e.getId();
        this.name = e.getName();
        this.setSynonyms(new ArrayList<>(e.getSynonyms()));
        this.setComment(e.getComment());
        this.setRefs(new HashMap<>(e.getRefs()));
        this.setAttributes(new HashMap<>(e.getAttributes()));
    }

    /**
     * TODO : voir la coherence du code entre les deux methodes addRef. Celle ci
     * devrait se terminer par un this.addRef(ref)
     *
     * @param dbName          name of the database
     * @param dbId            id of the database
     * @param confidenceLevel confidence level
     * @param relation        Type of relation
     * @param origin          Origin database
     */
    public void addRef(@NonNull String dbName, @NonNull String dbId, int confidenceLevel, @NonNull String relation, @NonNull String origin) {
        BioRef ref = new BioRef(origin, dbName, dbId, confidenceLevel);
        ref.logicallink = relation;
        this.addRef(ref);
    }

    /**
     * Add a reference
     *
     * @param ref a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef}
     */
    public void addRef(@NonNull BioRef ref) {
        String dbName = ref.dbName;
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
     * Get all refs associated to a database
     *
     * @param dbName the database name
     * @return a {@link java.util.Set} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef}
     */
    public Set<BioRef> getRefs(@NonNull String dbName) {
        return this.refs.getOrDefault(dbName, null);
    }

    /**
     * Check if the entity has a reference whose the database name is dbName and that contains a refence
     * whose the id is refId
     *
     * @param dbName the database name
     * @param refId  the reference id
     * @return true if the entity has the reference
     */
    public boolean hasRef(@NonNull String dbName, @NonNull String refId) {
        if (this.refs == null || !this.refs.containsKey(dbName)) {
            return false;
        }
        for (BioRef ref : this.refs.get(dbName)) {
            if (ref.id.equals(refId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the entity contains a reference
     *
     * @param unkRef a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioRef}
     * @return true if the entity has the reference
     */
    public boolean hasRef(@NonNull BioRef unkRef) {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.getId();
    }

    /**
     * <p>setAttribute.</p>
     *
     * @param key   a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object setAttribute(@NonNull String key, Object value) {
        return attributes.put(key, value);
    }

    /**
     * <p>getAttribute.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     */
    public Object getAttribute(@NonNull String key) {
        return attributes.get(key);
    }

}
