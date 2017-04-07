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
package fr.inra.toulouse.metexplore.met4j_core.mock;

import fr.inra.toulouse.metexplore.met4j_core.TestJSBMLToBionetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.io.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.io.UnitSbml;

/**
 * interface for collecting information and build test-case based on a sbml file
 * Class that implements this interface are used for testing import in {@link TestJSBMLToBionetwork}
 * After importing the sbml file, the Bionetwork's field are compared with information from the dummy.
 * The original sbml file have to be added in the project folder (parsebionet/data/Tests/)
 * 
 * @author clement
 */
public interface DummySbml {
		
	public String getSbmlPath();
	public String getNotesValueSeparator();
	public int getNumberOfReaction();
	public int getNumberOfMetabolite();
	public int getNumberOfCompartments();
	public int getNumberOfUnits();
	public int getNumberOfGenes();
	public int getNumberOfEnzyme();
	public int getNumberOfComplex();
	public int getNumberOfProtein();
	public int getNumberOfRefs();
	public BioPhysicalEntity getTestCompound();
	public BioReaction getTestReaction();
	public BioProtein getTestProtein();
	public BioGene getTestGene();
	public BioComplex getTestComplex();
	public UnitSbml getTestUnit();
	public BioUnitDefinition getTestUnitDef();
	public BioCompartment getTestComp();
}
