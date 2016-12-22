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
 * 10 juin 2011 
 */
package fr.inra.toulouse.metexplore.met4j_cytoscape.attributes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * @author ludo 10 juin 2011
 * 
 * Complete refactoring 22/12/2016
 * 
 */
public class BioNetworkToAttributeTable {

	BioNetwork network;
	String outputFile;

	public BioNetworkToAttributeTable(BioNetwork bioNetwork, String outputFile) {

		this.network = bioNetwork;
		this.outputFile = outputFile;

	}

	public void writeAttributes(Boolean sbmlCoded) throws IOException {

		FileWriter fw = new FileWriter(this.outputFile);

		fw.write(
				"id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n");

		/**
		 * For ordering compounds in the file and thus making easy the tests
		 */
		TreeMap<String, BioPhysicalEntity> cpds = new TreeMap<String, BioPhysicalEntity>(
				this.network.getPhysicalEntityList());

		HashMap<String, String> cpdNames = (new NameAttributes(this.network, false, true)).getAttributes();
		HashMap<String, String> cpdMasses = (new MassAttributes(this.network, false)).getAttributes();
		HashMap<String, String> cpdFormulas = (new FormulaAttributes(this.network, false)).getAttributes();
		HashMap<String, String> cpdPathways = (new PathwayAttributes(this.network, false, true)).getAttributes();
		HashMap<String, String> cpdCompartments = (new CompartmentAttributes(this.network, false, true))
				.getAttributes();

		for (String cpdId : cpds.keySet()) {

			String cpdName = cpdNames.get(cpdId);
			String mass = cpdMasses.get(cpdId);
			String formula = cpdFormulas.get(cpdId);
			String pathways = cpdPathways.get(cpdId);
			String compartmentName = cpdCompartments.get(cpdId);

			if (sbmlCoded) {
				cpdId = StringUtils.sbmlEncode(cpdId);
			}
			
			fw.write(cpdId + "\tspecies\t" + cpdName + "\t" + mass + "\t" + formula + "\t" + pathways + "\tNA\tNA\t"
					+ compartmentName + "\tNA\tNA\tNA\tNA\n");
		}

		/**
		 * For ordering reactions in the file and thus making easy the tests
		 */
		TreeMap<String, BioChemicalReaction> reactions = new TreeMap<String, BioChemicalReaction>(
				this.network.getBiochemicalReactionList());

		HashMap<String, String> rxnNames = (new NameAttributes(this.network, false, false)).getAttributes();
		HashMap<String, String> rxnFormulas = (new EquationAttributes(this.network, false)).getAttributes();
		HashMap<String, String> rxnPathways = (new PathwayAttributes(this.network, false, false)).getAttributes();
		HashMap<String, String> rxnEcs = (new ECAttributes(this.network, false)).getAttributes();
		HashMap<String, String> rxnRev = (new ReversibilityAttributes(this.network, false)).getAttributes();
		HashMap<String, String> rxnCpts = (new CompartmentAttributes(this.network, false, false)).getAttributes();
		HashMap<String, String> rxnLb = (new FluxBoundAttributes(network, false, true)).getAttributes();
		HashMap<String, String> rxnUb = (new FluxBoundAttributes(network, false, false)).getAttributes();
		HashMap<String, String> rxnGenes = (new GPRAttributes(network, false, true)).getAttributes();
		HashMap<String, String> rxnProteins = (new GPRAttributes(network, false, false)).getAttributes();
		
		for (String rxnId : reactions.keySet()) {

			String rxnName = rxnNames.get(rxnId);
			String rxnFormula = rxnFormulas.get(rxnId);
			String pathwaysStr = rxnPathways.get(rxnId);
			String ec = rxnEcs.get(rxnId);
			String rev = rxnRev.get(rxnId);
			String compartmentsStr = rxnCpts.get(rxnId);
			String lb = rxnLb.get(rxnId);
			String ub = rxnUb.get(rxnId);
			String geneRules = rxnGenes.get(rxnId);
			String proteinRules = rxnProteins.get(rxnId);
			
			if (sbmlCoded) {
				rxnId = StringUtils.sbmlEncode(rxnId);
			}

			fw.write(rxnId + "\treaction\t" + rxnName + "\tNA\t" + rxnFormula + "\t" + pathwaysStr + "\t" + ec + "\t"
					+ rev + "\t" + compartmentsStr + "\t" + geneRules + "\t" + proteinRules + "\t" + lb + "\t" + ub
					+ "\n");

		}

		fw.close();

	}

}
