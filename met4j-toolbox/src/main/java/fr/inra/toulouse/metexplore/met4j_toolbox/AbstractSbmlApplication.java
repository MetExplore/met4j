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
package fr.inra.toulouse.metexplore.met4j_toolbox;

import java.io.File;

import org.kohsuke.args4j.Option;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.io.ExtendedSbml2Bionetwork;
import fr.inra.toulouse.metexplore.met4j_core.io.JSBMLToBionetwork;

/**
 * 
 * Met4j abstract class for application using SBML as input
 * 
 * @author lcottret
 *
 */
public abstract class AbstractSbmlApplication extends AbstractApplication {

	private BioNetwork network;
	
	
	/**
	 * @return the network
	 */
	public BioNetwork getNetwork() {
		return network;
	}

	@Option(name = "-s", usage = "[] Sbml file")
	private String sbml = "";
	
	@Option(name="-ext", usage="[deactivated] Read Sbml file in extended format")
	private Boolean ext=false;
	

	/**
	 * Constructor
	 * @param args
	 */
	public AbstractSbmlApplication() {
		super();
		
	}
	
	/**
	 * Parse arguments
	 */
	public void parseArguments(String[] args) {
		super.parseArguments(args);
		
		if(! (new File(this.getSbml())).exists()) {
			System.err.println("Please indicate a valid sbml file\n");
			this.getParser().printUsage(System.err);
			System.exit(0);
		}
		
		if(this.getExt()) {
			ExtendedSbml2Bionetwork reader = new ExtendedSbml2Bionetwork(this.getSbml());
			this.network = reader.getBioNetwork();
		}
		else {
			JSBMLToBionetwork reader = new JSBMLToBionetwork(this.getSbml());
			this.network = reader.getBioNetwork();
		}
		
		System.err.println(this.network.networkAsString());
		
		
	}
	
	
	/**
	 * @return the sbml
	 */
	public String getSbml() {
		return sbml;
	}
	
	/**
	 * @return the ext
	 */
	public Boolean getExt() {
		return ext;
	}
	
}
