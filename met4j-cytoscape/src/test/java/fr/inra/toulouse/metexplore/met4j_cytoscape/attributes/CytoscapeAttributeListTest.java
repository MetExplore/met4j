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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.IOUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class CytoscapeAttributeListTest {
	
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	

	@Test
	public void testCytoscapeAttributeList() {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("1", "a");

		CytoscapeAttributeList c = new CytoscapeAttributeList("toto", map);

		System.err.println(c.getName());

		assertNotNull("Map attributes must be not null ", c.getMap_attributes());
		assertNotNull("Name must be not null ", c.getName());

		c = new CytoscapeAttributeList("toto;", map);

		assertNull("Map attributes must be null when the name contains a special character ", c.getMap_attributes());
		assertNull("Name must be null when the name contains a special character ", c.getName());

		c = new CytoscapeAttributeList("to to", map);

		assertNull("Map attributes must be null when the name contains a space character ", c.getMap_attributes());
		assertNull("Name must be null when the name contains a space character ", c.getName());

		c = new CytoscapeAttributeList("toto", null);

		assertNull("Map attributes must be null when map equals to null ", c.getMap_attributes());
		assertNull("Name must be null when when map equals to null  ", c.getName());

		c = new CytoscapeAttributeList("toto", new HashMap<String, String>());

		assertNull("Map attributes must be null when map is empty", c.getMap_attributes());
		assertNull("Name must be null when when map is empty", c.getName());

	}

	@Test
	public void testWriteAsAttributeFile() throws IOException {

		HashMap<String, String> map = new HashMap<String, String>();

		map.put("1", "a");
		map.put("2", "b");

		CytoscapeAttributeList c = new CytoscapeAttributeList("toto", map);
		
		File testFile = File.createTempFile( "testCompoundsAttributesForReactions", "txt");
		testFile.deleteOnExit();
		
		c.writeAsAttributeFile(testFile.getAbsolutePath(), false);
		
		String test = IOUtil.toString(new FileReader(testFile));
		
		String ref="toto\n"
				+ "1 = a\n"
				+ "2 = b\n";
		
		assertEquals("Test and reference files are not equal", ref, test);
		
		// Test for attributes as lists
		map.clear();
		map.put("1", "(a)");
		map.put("2", "(a::b::c)");
	
		c = new CytoscapeAttributeList("toto", map);
		
		testFile = File.createTempFile( "testCompoundsAttributesForReactions", "txt");
		testFile.deleteOnExit();
		
		c.writeAsAttributeFile(testFile.getAbsolutePath(), true);
		
		ref="toto (class=java.lang.String)\n"
				+ "1 = (a)\n"
				+ "2 = (a::b::c)\n";
		
		test = IOUtil.toString(new FileReader(testFile));
		
		assertEquals("List attributes : Test and reference files are not equal", ref, test);
		

	}

}
