package fr.inra.toulouse.metexplore.met4j_core.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;

public class ImportCofactorFile {
	
	BioNetwork bn;
	String cofactorFile;
	
	public ImportCofactorFile(BioNetwork bn, String cofactorFile){
		this.bn=bn;
		this.cofactorFile=cofactorFile;
	}
	
	
	/**
	 * @param cofactorFile
	 *            : file where there is a list of cofactor transformation to
	 *            mark Mark in each reaction the compounds corresponding to
	 *            cofactors. If a compound appears always as a cofactor, mark it
	 *            as a cofactor.
	 * @throws IOException
	 */
	public void markCofactors() throws IOException {

		FileInputStream in = new FileInputStream(cofactorFile);
		InputStreamReader ipsr = new InputStreamReader(in);
		BufferedReader br = new BufferedReader(ipsr);
		String ligne;

		Set<String> compartmentIds = new HashSet<String>();

		if (bn.getCompartments().size() > 0) {
			// In the biocyc networks built by MetExplore, the metabolites are
			// duplicated in each compartment
			// The information about the compartment is added as suffix in each
			// metabolite label
			// ex : ATP_IN_cytoplasm
			compartmentIds = bn.getCompartments().keySet();
		}

		Set<ArrayList<ArrayList<String>>> cofactorPairs = new HashSet<ArrayList<ArrayList<String>>>();

		while ((ligne = br.readLine()) != null) {
			if (!ligne.matches("^#.*")) {
				String[] tab = ligne.split("\\t");

				String cof1 = tab[0];
				String[] str = cof1.split("\\+");

				ArrayList<String> cofs1 = new ArrayList<String>();
				for (int i = 0; i < str.length; i++) {
					cofs1.add(str[i]);
				}

				String cof2 = tab[1];
				str = cof2.split("\\+");

				ArrayList<String> cofs2 = new ArrayList<String>();
				for (int i = 0; i < str.length; i++) {
					cofs2.add(str[i]);
				}

				ArrayList<ArrayList<String>> pair = new ArrayList<ArrayList<String>>();
				pair.add(cofs1);
				pair.add(cofs2);

				cofactorPairs.add(pair);

				for (String compartmentId : compartmentIds) {
					// We duplicate the pairs for each compartment
					ArrayList<String> cofs1Compt = new ArrayList<String>();

					for (String x : cofs1) {
						cofs1Compt.add(x + "_IN_" + compartmentId);
					}

					for (String compartmentId2 : compartmentIds) {

						ArrayList<String> cofs2Compt = new ArrayList<String>();

						for (String x : cofs2) {
							cofs2Compt.add(x + "_IN_" + compartmentId2);
						}

						ArrayList<ArrayList<String>> pairCpt = new ArrayList<ArrayList<String>>();

						pairCpt.add(cofs1Compt);
						pairCpt.add(cofs2Compt);

						cofactorPairs.add(pairCpt);
					}
				}
			}
		}

		in.close();

		for (ArrayList<ArrayList<String>> pairs : cofactorPairs) {

			ArrayList<String> cofs1 = pairs.get(0);
			ArrayList<String> cofs2 = pairs.get(1);

			if (bn.getPhysicalEntityList().containsKey(cofs1.get(0))
					&& bn.getPhysicalEntityList().containsKey(cofs2.get(0))) {

				HashMap<String, BioChemicalReaction> listOfReactions = new HashMap<String, BioChemicalReaction>(
						bn.getBiochemicalReactionList());

				for (BioChemicalReaction reaction : listOfReactions.values()) {

					HashMap<String, BioPhysicalEntityParticipant> leftP = reaction
							.getLeftParticipantList();
					HashMap<String, BioPhysicalEntityParticipant> rightP = reaction
							.getRightParticipantList();

					HashMap<String, BioPhysicalEntity> left = reaction
							.getLeftList();
					HashMap<String, BioPhysicalEntity> right = reaction
							.getRightList();

					if (left.containsKey(cofs1.get(0))
							&& right.containsKey(cofs2.get(0))) {

						for (BioPhysicalEntityParticipant bp : leftP.values()) {

							if (cofs1.contains(bp.getPhysicalEntity().getId())) {
								reaction.addCofactor(bp.getPhysicalEntity()
										.getId());
							}

						}

						for (BioPhysicalEntityParticipant bp : rightP.values()) {

							if (cofs2.contains(bp.getPhysicalEntity().getId())) {
								reaction.addCofactor(bp.getPhysicalEntity()
										.getId());
							}

						}

					} else if (left.containsKey(cofs2.get(0))
							&& right.containsKey(cofs1.get(0))) {

						for (BioPhysicalEntityParticipant bp : leftP.values()) {

							if (cofs2.contains(bp.getPhysicalEntity().getId())) {
								reaction.addCofactor(bp.getPhysicalEntity()
										.getId());
							}

						}

						for (BioPhysicalEntityParticipant bp : rightP.values()) {

							if (cofs1.contains(bp.getPhysicalEntity().getId())) {
								reaction.addCofactor(bp.getPhysicalEntity()
										.getId());
							}

						}

					}
				}
			}
		}

		// If a compound is a cofactor in each reaction it occurs, mark it as a
		// cofactor
		for (BioPhysicalEntity cpd : bn.getPhysicalEntityList().values()) {

			ArrayList<BioChemicalReaction> reactions = new ArrayList<BioChemicalReaction>();

			reactions.addAll(cpd.getReactionsAsSubstrate().values());
			reactions.addAll(cpd.getReactionsAsProduct().values());

			Boolean isCof = true;

			int nb = reactions.size();
			int i = nb;

			while (i > 0 && isCof == true) {

				i--;

				BioChemicalReaction rxn = reactions.get(i);

				HashMap<String, BioPhysicalEntityParticipant> participants = new HashMap<String, BioPhysicalEntityParticipant>();

				participants.putAll(rxn.getLeftParticipantList());
				participants.putAll(rxn.getRightParticipantList());

				for (BioPhysicalEntityParticipant bp : participants.values()) {
					if (bp.getId().compareTo(cpd.getId()) == 0) {
						isCof = bp.getIsCofactor();
					}
				}
			}
		}
	}

}
