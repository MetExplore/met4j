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
 * Set the object status from a tabulated file
 * 
 * 6 dec. 2012 
 */
package attributes;

import java.io.IOException;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;


/**
 * @author lcottret
 * 6 dec. 2012
 *
 */
public class SetStatusFromFile extends SetAttributeFromFile {
	
	/**
	 * 
	 * @param colId number of the column where are the reaction ids
	 * @param colAttr number of the attribute column
	 * @param bn BioNetwork
	 * @param fileIn tabulated file
	 * @param c comment string
	 * @param nSkip number of lines to skip at the beginning of the file
	 * @param p if true, to match the reactions in the sbml file, the reaction ids in the tabulated file are formatted in the palsson way
	 */
	public SetStatusFromFile(int colId, int colAttr, BioNetwork bn, String fileIn, String c, int nSkip, Boolean p, String object) {
		
		super(colId, colAttr, bn, fileIn, c, nSkip, object, p);
		
	}
	
	/**
	 * Test the status
	 */
	public Boolean testAttribute(String status) {
		
		if(status.matches("^\\d+$")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Reads the file and sets the attributes
	 * @return
	 * @throws IOException 
	 */
	public Boolean setAttributes() throws IOException {
		
		Boolean flag = true;
		
		try {
			flag = this.test();
		} catch (IOException e) {
			return false;
		}
		
		if(!flag) {
			return false;
		}

		int n = 0;
		
		for(String id : this.getIdAttributeMap().keySet()) {


			BioEntity object;
			n++;

			String status = this.getIdAttributeMap().get(id);

			if(this.getObject().equalsIgnoreCase(REACTION)) {
				object = this.getNetwork().getBiochemicalReactionList().get(id);
				((BioChemicalReaction)object).setStatus(status);
			}
			else {
				System.err.println("Only reaction objects allowed for the moment");
			}

			
		}
		
		System.err.println(n+" attributions");
		
		return flag;
		
	}
	
	
	
	
}
