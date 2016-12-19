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
package fr.inra.toulouse.metexplore.met4j_cytoscape.attributes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.utils.ErrorUtils;

public class CytoscapeAttributeList {

	private String name = null;
	
	private HashMap<String, String> map_attributes = null;
	
	/**
	 * 
	 * @param map : key = network element id, value = attribute id
	 */
	public CytoscapeAttributeList(String name, HashMap<String, String> map) {
	
		if(! name.matches("[A-za-z0-9\\_\\-]*")) {
			ErrorUtils.error("The name of the attribute must be composed by these characters : A-za-z0-9_-];");
			return;
		}
		
		if(map == null || map.isEmpty()) {
			ErrorUtils.error("The map must contain values");
			return;
		}
		
		
		this.name = name;
		this.map_attributes = map;
		
	}
	
	
	
	/**
	 * 
	 * Write the attributes in a Cytoscape attribute file
	 * 
	 * @param fileOut : path of the out file
	 * @param asList : if true indicates that the attribute is considered as a list 
	 * 					In this case, each item must be separated by a "::"
	 * @throws IOException
	 */
	public void writeAsAttributeFile(String fileOut, Boolean asList) throws IOException {
		
		if(name == null || map_attributes == null)
		{
			ErrorUtils.warning("CytoscapeAttributeList object badly instanciated");
			return;
		}
		
		FileWriter fw = new FileWriter(fileOut);
		
		fw.write(this.name);
		
		if(asList)
		{
			fw.write(" (class=java.lang.String)");
		}
		
		fw.write("\n");
		
		ArrayList<String> keys = 
				new ArrayList<String>(this.map_attributes.keySet());
		
		Collections.sort(keys);
		
		for(String key : keys)
		{
			String value = this.map_attributes.get(key);
			
			fw.write(key+" = "+value+"\n");
			
		}
		
		fw.close();
		
		
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the map_attributes
	 */
	public HashMap<String, String> getMap_attributes() {
		return map_attributes;
	}
	
	
	
	
}
