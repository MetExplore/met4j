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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

/**
 * Biological cellular compartment, e.g. mitochondria, cytoplasm
 *
 * @author lcottret
 */
public class BioCompartment extends BioPhysicalEntity {


    final private BioCollection<BioEntity> components;

    /**
     * Constructor from an id
     *
     * @param id : must be not null
     */
    public BioCompartment(String id) {
        this(id, id);
    }

    /**
     * Constructor from an id and a name
     *
     * @param id   must be not null
     * @param name the name of the compartment
     */
    public BioCompartment(String id, String name) {
        super(id, name);

        components = new BioCollection<>();
    }

    /**
     * Copy of a compartment
     * Do not copy the list of components
     *
     * @param compartment the original compartment
     */
    public BioCompartment(BioCompartment compartment) {
        super(compartment);

        components = new BioCollection<>();
    }

    /**
     * Copy of a compartment with a new id
     * Do not copy the list of components
     *
     * @param compartment the original compartment
     * @param id the new id
     */
    public BioCompartment(BioCompartment compartment, String id) {
        super(compartment, id);

        components = new BioCollection<>();
    }

    /**
     * <p>getComponentsView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of the components
     */
    public BioCollection<BioEntity> getComponentsView() {
        return components.getView();
    }

    /**
     * <p>Getter for the field <code>components</code>.</p>
     *
     * @return the components
     */
    protected BioCollection<BioEntity> getComponents() {
        return components;
    }

    /**
     * Add a component
     *
     * @param e : the {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} to add
     */
    protected void addComponent(BioEntity e) {
        this.components.add(e);
    }

}
