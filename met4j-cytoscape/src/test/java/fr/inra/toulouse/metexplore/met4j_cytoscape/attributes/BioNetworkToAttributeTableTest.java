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
package fr.inra.toulouse.metexplore.met4j_cytoscape.attributes;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtil;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Flux;

/**
 * @author lcottret
 *
 */
public class BioNetworkToAttributeTableTest {

	/**
	 * Test method for
	 * {@link fr.inra.toulouse.metexplore.met4j_cytoscape.attributes.BioNetworkToAttributeTable#writeAttributes(java.lang.Boolean)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testWriteAttributes() throws IOException {

		BioNetwork network = new BioNetwork();

		BioCompartment cytosol = new BioCompartment("cytosol", "c");

		BioCompartment periplasm = new BioCompartment("periplasm", "p");

		BioChemicalReaction r1 = new BioChemicalReaction("r1-rxn", "R1");

		BioUnitDefinition ud = new BioUnitDefinition();

		BioProtein p = new BioProtein("p1", "P1");
		BioGene g = new BioGene("g1", "gene1");
		p.addGene(g);

		BioPhysicalEntity a = new BioPhysicalEntity("A-cpd", "A';");
		a.setMolecularWeight("10");
		a.setChemicalFormula("formulaA");
		a.setCompartment(cytosol);
		BioPhysicalEntity b = new BioPhysicalEntity("B-cpd", "B");
		b.setMolecularWeight("10d");
		b.setChemicalFormula("formulaB");
		b.setCompartment(periplasm);
		BioPhysicalEntity c = new BioPhysicalEntity("C-cpd", "C");
		c.setMolecularWeight("12.5");
		c.setChemicalFormula("formulaC");
		c.setCompartment(cytosol);
		BioPhysicalEntity d = new BioPhysicalEntity("D-cpd", "D");

		r1.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r1.setEcNumber("1.1.1.1");
		r1.setLowerBound(new Flux("-10000", ud));
		r1.setUpperBound(new Flux("2", ud));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(c));

		r1.addEnz(p);

		BioChemicalReaction r2 = new BioChemicalReaction("r2-rxn", "R2&lt;i&gt;ase&lt;/i&gt;");

		r2.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(d));

		BioPathway p1 = new BioPathway("P-1", "pathway;");
		BioPathway p2 = new BioPathway("P-2", "pathway--2;");
		BioPathway p3 = new BioPathway("P-3", "pathway3;");
		BioPathway p4 = new BioPathway("P-4", "pathway--4;");

		r1.addPathway(p1);
		r1.addPathway(p2);
		r2.addPathway(p3);
		r2.addPathway(p4);

		network.addBiochemicalReaction(r1);
		network.addBiochemicalReaction(r2);

		String ref = "id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n"

				+ "A-cpd\t" + "species\t" + "A'\t" + "10\t" + "formulaA\t" + "(pathway::pathway--2)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "B-cpd\t" + "species\t" + "B\t" + "10d\t" + "formulaB\t" + "(pathway--4::pathway3)\t" + "NA\t"
				+ "NA\t" + "periplasm\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "C-cpd\t" + "species\t" + "C\t" + "12.5\t" + "formulaC\t" + "(pathway::pathway--2)\t" + "NA\t"
				+ "NA\t" + "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "D-cpd\t" + "species\t" + "D\t" + "NA\t" + "NA\t" + "(pathway--4::pathway3)\t" + "NA\t" + "NA\t"
				+ "NA\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "r1-rxn\t" + "reaction\t" + "R1\t" + "NA\t" + "A'[c] -> C[c]\t" + "(pathway::pathway--2)\t"
				+ "1.1.1.1\t" + "false\t" + "(cytosol)\t" + "( gene1 )\t" + "( P1 )\t" + "-10000\t" + "2\n"

				+ "r2-rxn\t" + "reaction\t" + "R2ase\t" + "NA\t" + "D[NA] -> B[p]\t" + "(pathway--4::pathway3)\t"
				+ "\t" + "false\t" + "(NA::periplasm)\t" + "\t" + "\t" + "-99999\t" + "99999" + "\n";

		
		File testFile = File.createTempFile("testBioNetworkToAttributeTable", "txt");
		testFile.deleteOnExit();

		BioNetworkToAttributeTable converter = new BioNetworkToAttributeTable(network, testFile.getAbsolutePath());

		converter.writeAttributes(false);

		String test = IOUtil.toString(new FileReader(testFile));

		assertEquals("Test and reference files are not equal", ref, test);
		
		ref = "id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n"

				+ "A__45__cpd\t" + "species\t" + "A'\t" + "10\t" + "formulaA\t" + "(pathway::pathway--2)\t" + "NA\t" + "NA\t"
				+ "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "B__45__cpd\t" + "species\t" + "B\t" + "10d\t" + "formulaB\t" + "(pathway--4::pathway3)\t" + "NA\t"
				+ "NA\t" + "periplasm\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "C__45__cpd\t" + "species\t" + "C\t" + "12.5\t" + "formulaC\t" + "(pathway::pathway--2)\t" + "NA\t"
				+ "NA\t" + "cytosol\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "D__45__cpd\t" + "species\t" + "D\t" + "NA\t" + "NA\t" + "(pathway--4::pathway3)\t" + "NA\t" + "NA\t"
				+ "NA\t" + "NA\t" + "NA\t" + "NA\t" + "NA\n"

				+ "r1__45__rxn\t" + "reaction\t" + "R1\t" + "NA\t" + "A'[c] -> C[c]\t" + "(pathway::pathway--2)\t"
				+ "1.1.1.1\t" + "false\t" + "(cytosol)\t" + "( gene1 )\t" + "( P1 )\t" + "-10000\t" + "2\n"

				+ "r2__45__rxn\t" + "reaction\t" + "R2ase\t" + "NA\t" + "D[NA] -> B[p]\t" + "(pathway--4::pathway3)\t"
				+ "\t" + "false\t" + "(NA::periplasm)\t" + "\t" + "\t" + "-99999\t" + "99999" + "\n";

		converter = new BioNetworkToAttributeTable(network, testFile.getAbsolutePath());

		converter.writeAttributes(true);

		test = IOUtil.toString(new FileReader(testFile));

		assertEquals("Test and reference files are not equal (SBML coded)", ref, test);

	}

}
