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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 */
public class BioMetabolite extends BioPhysicalEntity {

    private Double molecularWeight;
    private String chemicalFormula;
    private String inchi;
    private String smiles;
    private Integer charge = 0;

    /**
     * Constructor
     *
     * @param id the id of the BioMetabolite
     */
    public BioMetabolite(String id) {
        super(id);
    }

    /**
     * Constructor
     *
     * @param id   the id of the BioMetabolite
     * @param name the name of the BioMetabolite
     */
    public BioMetabolite(String id, String name) {
        super(id, name);
    }

    /**
     * Copy a BioMetabolite.
     * The refs and attributes are not deeply copied
     * @param metabolite the original metabolite
     */
    public BioMetabolite(BioMetabolite metabolite) {
        super(metabolite);

        this.inchi = metabolite.inchi;
        this.smiles = metabolite.smiles;
        this.charge = metabolite.charge;
        this.molecularWeight = metabolite.molecularWeight;
        this.chemicalFormula = metabolite.chemicalFormula;

    }


    /**
     * @return the inchi
     */
    public String getInchi() {
        return inchi;
    }

    /**
     * @param inchi the inchi to set
     */
    public void setInchi(String inchi) {
        this.inchi = inchi;
    }


    /**
     * @return the smile
     */
    public String getSmiles() {
        return smiles;
    }


    /**
     * @param smile the smile to set
     */
    public void setSmiles(String smile) {
        this.smiles = smile;
    }


    /**
     * @param molecularWeight the molecularWeight to set
     */
    public void setMolecularWeight(double molecularWeight) {
        this.molecularWeight = molecularWeight;
    }


    /**
     * @return the chemical formula of the metabolite
     */
    public String getChemicalFormula() {
        return chemicalFormula;
    }


    /**
     * @param chemicalFormula : a String like C6H12O6
     */
    public void setChemicalFormula(String chemicalFormula) {
        this.chemicalFormula = chemicalFormula;
    }


    /**
     * @return the molecularWeight
     */
    public Double getMolecularWeight() {
        return molecularWeight;
    }


    /**
     * @return the charge
     */
    public Integer getCharge() {
        return charge;
    }


    /**
     * @param charge the charge to set
     */
    public void setCharge(Integer charge) {
        this.charge = charge;
    }
}
