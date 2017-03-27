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
package fr.inra.toulouse.metexplore.met4j_toolbox_test.cytoscape;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_toolbox.cytoscape.SbmlToCytoscapeAttributes;

public class SbmlToCytoscapeAttributesTest {

	@Test
	public void testMain() throws IOException {

		File testFile = File.createTempFile("testBioNetworkToAttributeTable", "txt");
		// testFile.deleteOnExit();

		// Classical sbml, normal ids

		String[] args = { "-s", "test1.sbml", "-out", testFile.getAbsolutePath() };

		SbmlToCytoscapeAttributes.main(args);

		String ref = "id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n"

				+ "A-cpd\t" + "species\t" + "A'\t" + "NA\t" + "formulaA\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "B-cpd\t" + "species\t" + "B\t" + "NA\t" + "formulaB\t" + "(pathway3)\t" + "NA\t" + "NA\t"
				+ "periplasm\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "C-cpd\t" + "species\t" + "C\t" + "NA\t" + "formulaC\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "D-cpd\t" + "species\t" + "D\t" + "NA\t" + "NA\t" + "(pathway3)\t" + "NA\t" + "NA\t" + "cytosol\t"
				+ "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "r1-rxn\t" + "reaction\t" + "R1\t" + "NA\t" + "A'[c] -> C[c]\t" + "(pathway)\t" + "1.1.1.1\t"
				+ "false\t" + "(cytosol)\t" + "( gene1 )\t" + "( gene1 (TH) )\t" + "-1000.0\t" + "2.0\n"

				+ "r2-rxn\t" + "reaction\t" + "R2ase\t" + "NA\t" + "D[c] -> B[p]\t" + "(pathway3)\t" + "NA\t"
				+ "false\t" + "(cytosol::periplasm)\t" + "\t" + "\t" + "0\t" + "99999" + "\n";

		String test = IOUtils.toString(new FileReader(testFile));

		assertEquals("[Classical SBML with normal ids] Test and reference files are not equal", ref, test);

		// Classical sbml, uncoded ids

		String[] args2 = { "-s", "test1.sbml", "-out", testFile.getAbsolutePath(), "-decode" };

		SbmlToCytoscapeAttributes.main(args2);

		ref = "id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n"

				+ "A__45__cpd\t" + "species\t" + "A'\t" + "NA\t" + "formulaA\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "B__45__cpd\t" + "species\t" + "B\t" + "NA\t" + "formulaB\t" + "(pathway3)\t" + "NA\t" + "NA\t"
				+ "periplasm\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "C__45__cpd\t" + "species\t" + "C\t" + "NA\t" + "formulaC\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "D__45__cpd\t" + "species\t" + "D\t" + "NA\t" + "NA\t" + "(pathway3)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "r1__45__rxn\t" + "reaction\t" + "R1\t" + "NA\t" + "A'[c] -> C[c]\t" + "(pathway)\t" + "1.1.1.1\t"
				+ "false\t" + "(cytosol)\t" + "( gene1 )\t" + "( gene1 (TH) )\t" + "-1000.0\t" + "2.0\n"

				+ "r2__45__rxn\t" + "reaction\t" + "R2ase\t" + "NA\t" + "D[c] -> B[p]\t" + "(pathway3)\t" + "NA\t"
				+ "false\t" + "(cytosol::periplasm)\t" + "\t" + "\t" + "0\t" + "99999" + "\n";

		test = IOUtils.toString(new FileReader(testFile));

		assertEquals("[Classical SBML with uncoded ids] Test and reference files are not equal", ref, test);

		// Extended sbml, normal ids

		String[] args3 = { "-s", "test1.xml", "-out", testFile.getAbsolutePath(), "-ext" };

		SbmlToCytoscapeAttributes.main(args3);

		ref = "id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n"

				+ "A-cpd\t" + "species\t" + "A'\t" + "10\t" + "formulaA\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "B-cpd\t" + "species\t" + "B\t" + "10d\t" + "formulaB\t" + "(pathway3)\t" + "NA\t" + "NA\t"
				+ "periplasm\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "C-cpd\t" + "species\t" + "C\t" + "12.5\t" + "formulaC\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "D-cpd\t" + "species\t" + "D\t" + "NA\t" + "NA\t" + "(pathway3)\t" + "NA\t" + "NA\t" + "cytosol\t"
				+ "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "r1-rxn\t" + "reaction\t" + "R1\t" + "NA\t" + "A'[c] -> C[c]\t" + "(pathway)\t" + "1.1.1.1\t"
				+ "false\t" + "(cytosol)\t" + "( gene1 )\t" + "( gene1 (TH) )\t" + "-1000.0\t" + "2.0\n"

				+ "r2-rxn\t" + "reaction\t" + "R2ase\t" + "NA\t" + "D[c] -> B[p]\t" + "(pathway3)\t" + "NA\t"
				+ "false\t" + "(cytosol::periplasm)\t" + "\t" + "\t" + "0\t" + "99999" + "\n";

		test = IOUtils.toString(new FileReader(testFile));

		assertEquals("[Extended SBML with normal ids] Test and reference files are not equal", ref, test);

		// Extended sbml, uncoded ids

		String[] args4 = { "-s", "test1.xml", "-out", testFile.getAbsolutePath(), "-ext", "-decode" };

		SbmlToCytoscapeAttributes.main(args4);

		ref = "id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n"

				+ "A__45__cpd\t" + "species\t" + "A'\t" + "10\t" + "formulaA\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "B__45__cpd\t" + "species\t" + "B\t" + "10d\t" + "formulaB\t" + "(pathway3)\t" + "NA\t" + "NA\t"
				+ "periplasm\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "C__45__cpd\t" + "species\t" + "C\t" + "12.5\t" + "formulaC\t" + "(pathway)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "D__45__cpd\t" + "species\t" + "D\t" + "NA\t" + "NA\t" + "(pathway3)\t" + "NA\t" + "NA\t" + "cytosol\t"
				+ "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "r1__45__rxn\t" + "reaction\t" + "R1\t" + "NA\t" + "A'[c] -> C[c]\t" + "(pathway)\t" + "1.1.1.1\t"
				+ "false\t" + "(cytosol)\t" + "( gene1 )\t" + "( gene1 (TH) )\t" + "-1000.0\t" + "2.0\n"

				+ "r2__45__rxn\t" + "reaction\t" + "R2ase\t" + "NA\t" + "D[c] -> B[p]\t" + "(pathway3)\t" + "NA\t"
				+ "false\t" + "(cytosol::periplasm)\t" + "\t" + "\t" + "0\t" + "99999" + "\n";

		test = IOUtils.toString(new FileReader(testFile));

		assertEquals("[Extended SBML with normal ids] Test and reference files are not equal", ref, test);

	}

}
