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
package fr.inra.toulouse.metexplore.met4j_core.biodata;

/**
 *
 */
public class BioMetabolite extends BioPhysicalEntity {
	
	private double molecularWeight;
	private String chemicalFormula;
	private String inchi;
	private String smile;
	private int charge;
	
	public BioMetabolite(String id) {
		super(id);
	}
	
	public BioMetabolite(String id, String name) {
		super(id, name);
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
	public String getSmile() {
		return smile;
	}


	/**
	 * @param smile the smile to set
	 */
	public void setSmile(String smile) {
		this.smile = smile;
	}


	/**
	 * @param molecularWeight the molecularWeight to set
	 */
	public void setMolecularWeight(double molecularWeight) {
		this.molecularWeight = molecularWeight;
	}



	public String getChemicalFormula() {
		return chemicalFormula;
	}


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
	public int getCharge() {
		return charge;
	}


	/**
	 * @param charge the charge to set
	 */
	public void setCharge(int charge) {
		this.charge = charge;
	}

}
