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
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;


/**
 * @author ludo 10 juin 2011
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

		fw.write("id\tsbml type\tsbml name\tmass\tformula\tpathways\tec\trev\tcompartment\tgeneRules\tprotRules\tlb\tub\n");

		/**
		 * For ordering compounds in the file and thus making easy the tests
		 */
		TreeMap<String, BioPhysicalEntity> cpds = new TreeMap<String, BioPhysicalEntity>(
				this.network.getPhysicalEntityList());

		for (String cpdId : cpds.keySet()) {

			BioPhysicalEntity cpd = cpds.get(cpdId);

			String cpdName = StringUtils.getNotFormattedString(cpd.getName());

			String mass = cpd.getMolecularWeight();

			String formula = cpd.getChemicalFormula();

			TreeSet<String> pathways = new TreeSet<String>();

			/**
			 * For ordering reactions in the file and thus making easy the tests
			 */
			TreeMap<String, BioChemicalReaction> reactionsAsSubstrate = new TreeMap<String, BioChemicalReaction>(
					cpd.getReactionsAsSubstrate());

			for (String rxnId : reactionsAsSubstrate.keySet()) {

				BioChemicalReaction rxn = reactionsAsSubstrate.get(rxnId);

				TreeMap<String, BioPathway> rxnPathways = new TreeMap<String, BioPathway>(
						rxn.getPathwayList());

				for (String pathwayId : rxnPathways.keySet()) {

					BioPathway pathway = rxnPathways.get(pathwayId);

					String pathwayName = StringUtils
							.getNotFormattedString(pathway.getName());

					pathways.add(pathwayName);

				}

			}

			/**
			 * For ordering reactions in the file and thus making easy the tests
			 */
			TreeMap<String, BioChemicalReaction> reactionsAsProduct = new TreeMap<String, BioChemicalReaction>(
					cpd.getReactionsAsProduct());

			for (String rxnId : reactionsAsProduct.keySet()) {

				BioChemicalReaction rxn = reactionsAsProduct.get(rxnId);

				TreeMap<String, BioPathway> rxnPathways = new TreeMap<String, BioPathway>(
						rxn.getPathwayList());

				for (String pathwayId : rxnPathways.keySet()) {

					BioPathway pathway = rxnPathways.get(pathwayId);

					String pathwayName = StringUtils
							.getNotFormattedString(pathway.getName());

					pathways.add(pathwayName);

				}
			}

			String pathwaysStr = "";

			int i = 0;
			for (String pathwayStr : pathways) {
				i++;

				if (i == 1) {
					pathwaysStr = pathwayStr;
				} else {
					pathwaysStr = pathwaysStr + " _+_ " + pathwayStr;
				}
			}

			String compartmentName = "NA";

			if (cpd.getCompartment() != null) {
				compartmentName = cpd.getCompartment().getName();
			}

			if (sbmlCoded) {
				cpdId = StringUtils.sbmlEncode(cpdId);
			}

			fw.write(cpdId + "\tspecies\t" + cpdName + "\t" + mass + "\t"
					+ formula + "\t" + pathwaysStr + "\tNA\t" + compartmentName
					+ "\tNA\tNA\tNA\tNA\n");

		}

		/**
		 * For ordering reactions in the file and thus making easy the tests
		 */
		TreeMap<String, BioChemicalReaction> reactions = new TreeMap<String, BioChemicalReaction>(
				this.network.getBiochemicalReactionList());

		for (String rxnId : reactions.keySet()) {

			BioChemicalReaction rxn = reactions.get(rxnId);
			String rxnName = StringUtils.getNotFormattedString(rxn.getName());
			String rxnFormula = StringUtils.getNotFormattedString(rxn
					.getEquation());

			String pathwaysStr = "";

			int i = 0;

			
			/**
			 * For ordering pathways in the file and thus making easy the tests
			 */
			TreeMap<String, BioPathway> pathways = new TreeMap<String, BioPathway>(
					rxn.getPathwayList());
			
			for (String pathwayId: pathways.keySet()) {
				
				BioPathway pathway = pathways.get(pathwayId);

				i++;

				String pathwayName = StringUtils.getNotFormattedString(pathway
						.getName());

				if (i == 1) {
					pathwaysStr = pathwayName;
				} else {
					pathwaysStr = pathwaysStr + " _+_ " + pathwayName;
				}

			}

			String ec = rxn.getEcNumber();

			String rev = "false";

			if (rxn.isReversible()) {
				rev = "true";
			}

			TreeSet<String> compartmentIds = new TreeSet<String>();

			for (BioPhysicalEntityParticipant bpe : rxn
					.getLeftParticipantList().values()) {

				BioPhysicalEntity cpd = bpe.getPhysicalEntity();

				String compartmentId = "NA";

				if (cpd.getCompartment() != null) {

					compartmentId = cpd.getCompartment().getId();
				}

				compartmentIds.add(compartmentId);

			}

			String compartmentsStr = "";

			i = 0;

			for (String compartmentId : compartmentIds) {
				i++;

				if (i == 1) {
					compartmentsStr = compartmentId;
				} else {
					compartmentsStr = compartmentsStr + " _+_ " + compartmentId;
				}
			}

			String lb = rxn.getLowerBound().value;
			String ub = rxn.getUpperBound().value;

			String geneRules = rxn.getGPR().get(0);
			String proteinRules = rxn.getGPR().get(1);

			if (sbmlCoded) {
				rxnId = StringUtils.sbmlEncode(rxnId);
			}

			fw.write(rxnId + "\treaction\t" + rxnName + "\tNA\t" + rxnFormula
					+ "\t" + pathwaysStr + "\t" + ec + "\t" + rev + "\t"
					+ compartmentsStr + "\t" + geneRules + "\t" + proteinRules
					+ "\t" + lb + "\t" + ub + "\n");

		}

		fw.close();

	}

}
