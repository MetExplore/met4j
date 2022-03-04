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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.GeneSetException;

/**
 * This class represents a unique "AND" gene association
 * <p>
 * it is composed of a set of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene} that all need to be active for
 * this association to be active
 *
 * @author Benjamin mainly modified by LC
 * @version $Id: $Id
 * @since 3.0
 */
public class GeneSet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String id;

    private Set<String> geneIds;


    /**
     * This is to prevent the hashcode modification
     */
    public Boolean addedInGeneAssociation = false;


    public GeneSet() {
        geneIds = new HashSet<>();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method outputs the PAlson's representation of an "AND" gene
     * association
     */
    @Override
    public String toString() {
        return this.geneIds.stream().sorted().collect(Collectors.joining(" AND "));
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
        this.id = id;
    }


    public boolean addAll(GeneSet s) throws GeneSetException {
        if (this.addedInGeneAssociation) {
            throw new GeneSetException();
        }
        return geneIds.addAll(s.geneIds);
    }

    public boolean add(String c) throws GeneSetException {
        if (this.addedInGeneAssociation) {
            throw new GeneSetException();
        }
        return geneIds.add(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return this.geneIds.stream().sorted().collect(Collectors.joining(" AND ")).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneSet)) return false;
        GeneSet geneSet = (GeneSet) o;
        return geneIds.equals(geneSet.geneIds);
    }

    public int size() {
        return this.geneIds.size();
    }

    public Iterator<String> iterator() {
        return this.geneIds.iterator();
    }

    public Stream<String> stream() {
        return this.geneIds.stream();
    }

    protected Set<String> getGeneIds() {
        return this.geneIds;
    }

    public void remove(String geneId) {
        this.geneIds.remove(geneId);
    }
}
