/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: metexplore@toulouse.inra.fr
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
 * @author Fabien
 *	Definition from SBML v2 Version4 release1
 *	A compartment type in SBML is a grouping construct used to establish a relationship between multiple compartments.
 *	A compartment type is represented by the CompartmentType object class.
 *	In SBML Level 2 Version 3 and beyond, a compartment type only has an identity, and this identity can only be used to indicate that particular compartments belong to this type.
 *	This may be	useful for conveying a modeling intention, such as when a model contains many similar compartments, either by their biological function or the reactions they carry; without a compartment type construct, it would be impossible in the language of SBML to indicate that all of the compartments share an underlying conceptual relationship because each SBML compartment must be given a unique and separate identity.
 *	Compartment types have no mathematical meaning in SBML Level 2 Version 4. they have no effect on a models mathematical interpretation.
 *	Simulators and other numerical analysis software may ignore Compartment-Type objects and references to them in a model.
 *	There is no mechanism in SBML for representing hierarchies of compartment types.
 *	One CompartmentType	object instance cannot be the subtype of another CompartmentType object; SBML provides no means of defining such relationships.
 */
public class BioCompartmentType {
	private String id;
	private String name;
	public BioCompartmentType(String i, String n)
	{
		id=i;
		name=n;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
