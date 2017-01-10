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
/**
 * 9 febr. 2012 
 */
package fr.inra.toulouse.metexplore.met4j_toolbox.cytoscape;

import java.io.IOException;

import org.kohsuke.args4j.Option;

import fr.inra.toulouse.metexplore.met4j_toolbox.AbstractSbmlApplication;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_cytoscape.attributes.BioNetworkToAttributeTable;


/**
 * @author lcottret
 * Created 9 febr. 2012
 * 
 * Jan 2017 : refactoring for met4j
 * 
 *
 */
public class SbmlToCytoscapeAttributes extends AbstractSbmlApplication {

	String description = "Writes a tabulated file with network attributes";
	
	@Option(name="-out", usage="[network_attributes.tab] Cytoscape attribute file (tabulated format)")
	private String out = "network_attributes.tab";
	
	@Option(name="-decode", usage="[deactivated] If activated, decodes the sbml ids)")
	private Boolean decode = false;
	
	
	public SbmlToCytoscapeAttributes() {
		super();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
		SbmlToCytoscapeAttributes s = new SbmlToCytoscapeAttributes();
		
		s.parseArguments(args);
		
		String out = s.getOut();
		Boolean encoded = s.getDecode();
		
		
		BioNetwork bn = s.getNetwork();
		BioNetworkToAttributeTable bna = new BioNetworkToAttributeTable(bn, out);
		
		bna.writeAttributes(encoded);

	}

	/**
	 * @return the out
	 */
	public String getOut() {
		return out;
	}

	/**
	 * @return the decode
	 */
	public Boolean getDecode() {
		return decode;
	}

	@Override
	public String getDescription() {
		return description;
	}

	
	
}
