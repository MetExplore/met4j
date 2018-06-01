package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

public class BioChemicalReactionUtils {
	
	/**
	 * Comparison with another reaction : if the substrates and the products
	 * have the same id, return true
	 */
	public static Boolean areRedundant(BioReaction r1, BioReaction r2) {

		Set<String> listOfOtherSubstrates = r2.getLeftList().keySet();
		Set<String> listOfOtherProducts = r2.getRightList().keySet();

		if (r1.getReversiblity().equalsIgnoreCase(r2.getReversiblity())
				&& listOfOtherProducts.equals(r1.getRightList().keySet())
				&& listOfOtherSubstrates.equals(r1.getLeftList().keySet())) {
			return true;
		}

		if (r1.getReversiblity().equalsIgnoreCase("reversible")
				&& r2.getReversiblity().equalsIgnoreCase("reversible")
				&& ((r1.getLeftList().keySet().equals(listOfOtherSubstrates) && r1
						.getRightList().keySet().equals(listOfOtherProducts)) || (r1
						.getLeftList().keySet().equals(listOfOtherProducts) && r1
						.getRightList().keySet().equals(listOfOtherSubstrates)))) {
			return true;
		}

		return false;

	}
	
	/**
	 * Indicate if all the genes coding for at least one enzyme are present
	 */
	public static boolean isGeneticallyPossible(BioReaction r) {
		if (r.getSpontaneous() != null) {
				return true;
		} else {
			ArrayList<BioPhysicalEntity> liste = new ArrayList<BioPhysicalEntity>(
					r.getEnzList().values());
	
			for (int i = 0; i < liste.size(); i++) {
	
				BioPhysicalEntity enzyme = liste.get(i);
				String classe = enzyme.getClass().getSimpleName();
	
				if (classe.compareTo("BioProtein") == 0) {
					if (((BioProtein) enzyme).getGeneList().size() > 0) {
						return true;
					}
				} else if (classe.compareTo("BioComplex") == 0) {
					if (((BioComplex) enzyme).getIsGeneticallyPossible() == true) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static Boolean testReaction(BioReaction r) {
		return testReaction(r, false, true);
	}

	/**
	 * 
	 * @return a ArrayList<String> corresponding to the association between
	 *         genes and between proteins that enable the catalysis of the
	 *         reaction Ex : res.get(0) g1 and ( g2 or g3 ) res.get(1) p1 and p2
	 */
	public static ArrayList<String> getGPR(BioReaction r) {

		String geneStr = "";
		String protStr = "";
		ArrayList<String> res = new ArrayList<String>();

		int j = 0;

		for (Iterator<String> iterEnz = r.getEnzList().keySet().iterator(); iterEnz
				.hasNext();) {
			j++;

			if (j > 1) {
				protStr = protStr + " or ";
				geneStr = geneStr + " or ";
			}

			BioPhysicalEntity enzyme = r.getEnzList().get(iterEnz.next());

			String classe = enzyme.getClass().getSimpleName();

			HashMap<String, BioGene> listOfGenes = new HashMap<String, BioGene>();

			HashMap<String, BioProtein> listOfProteins = new HashMap<String, BioProtein>();

			if (classe.compareTo("BioProtein") == 0) {
				listOfProteins.put(enzyme.getId(), (BioProtein) enzyme);

				listOfGenes = ((BioProtein) enzyme).getGeneList();

			} else if (classe.compareTo("BioComplex") == 0) {

				listOfGenes = ((BioComplex) enzyme).getGeneList();

				HashMap<String, BioPhysicalEntity> componentList = ((BioComplex) enzyme)
						.getAllComponentList();

				for (Iterator<String> iterComponent = componentList.keySet()
						.iterator(); iterComponent.hasNext();) {

					BioPhysicalEntity component = componentList
							.get(iterComponent.next());

					if (component.getClass().getSimpleName()
							.compareTo("BioProtein") == 0) {
						listOfProteins.put(component.getId(),
								(BioProtein) component);
					}

				}
			}
			int k = 0;

			geneStr = geneStr + "( ";

			for (Iterator<String> iterGene = listOfGenes.keySet().iterator(); iterGene
					.hasNext();) {
				k++;

				if (k > 1) {
					geneStr = geneStr + " and ";
				}

				BioGene gene = listOfGenes.get(iterGene.next());

				geneStr = geneStr + StringUtils.htmlEncode(gene.getName());
			}

			geneStr = geneStr + " )";

			protStr = protStr + "( ";

			k = 0;

			for (Iterator<String> iterProt = listOfProteins.keySet().iterator(); iterProt
					.hasNext();) {
				k++;
				if (k > 1) {
					protStr = protStr + " and ";
				}

				BioProtein prot = listOfProteins.get(iterProt.next());
				protStr = protStr + StringUtils.htmlEncode(prot.getName());
			}

			protStr = protStr + " )";

		}

		res.add(geneStr);
		res.add(protStr);

		return res;

	}
	
	/**
	 * Compute the atom balances
	 * 
	 * @return
	 */
	public static HashMap<String, Double> computeAtomBalances(BioReaction r) {

		HashMap<String, Double> balances = new HashMap<String, Double>();

		for (BioParticipant bpe : r.getLeftParticipantList()
				.values()) {

			String stoStr = bpe.getStoichiometricCoefficient();

			Double sto = 0.0;

			try {
				sto = Double.parseDouble(stoStr);
			} catch (NumberFormatException e) {
				System.err.println("Stoichiometry not valid in the reaction "
						+ r.getId());
				return new HashMap<String, Double>();
			}

			String formula = bpe.getPhysicalEntity().getChemicalFormula();

			if (formula.equals("NA")) {
				System.err.println("No formula for "
						+ bpe.getPhysicalEntity().getId() + " in "
						+ r.getId());
				return new HashMap<String, Double>();
			}

			String REGEX = "[A-Z]{1}[a-z]*[0-9]*";

			Pattern pattern = Pattern.compile(REGEX);
			Matcher matcher = pattern.matcher(formula);

			while (matcher.find()) {
				String group = matcher.group(0);

				String REGEX2 = "([A-Z]{1}[a-z]*)([0-9]*)";

				Pattern pattern2 = Pattern.compile(REGEX2);
				Matcher matcher2 = pattern2.matcher(group);

				matcher2.find();

				String atom = matcher2.group(1);

				String numStr = matcher2.group(2);

				if (numStr.equals("")) {
					numStr = "1.0";
				}

				Double number = Double.parseDouble(numStr);

				if (!balances.containsKey(atom)) {
					balances.put(atom, sto * number);
				} else {
					balances.put(atom, balances.get(atom) + sto * number);
				}

			}

		}

		for (BioParticipant bpe : r.getRightParticipantList()
				.values()) {

			String stoStr = bpe.getStoichiometricCoefficient();

			Double sto = 0.0;

			try {
				sto = Double.parseDouble(stoStr);
			} catch (NumberFormatException e) {
				System.err.println("Stoichiometry not valid in the reaction "
						+ r.getId());
				return new HashMap<String, Double>();
			}

			String formula = bpe.getPhysicalEntity().getChemicalFormula();

			if (formula.equals("NA")) {
				System.err.println("No formula for "
						+ bpe.getPhysicalEntity().getId() + " in "
						+ r.getId());
				return new HashMap<String, Double>();
			}

			String REGEX = "[A-Z]{1}[a-z]*[0-9]*";

			Pattern pattern = Pattern.compile(REGEX);
			Matcher matcher = pattern.matcher(formula);

			while (matcher.find()) {
				String group = matcher.group(0);

				String REGEX2 = "([A-Z]{1}[a-z]*)([0-9]*)";

				Pattern pattern2 = Pattern.compile(REGEX2);
				Matcher matcher2 = pattern2.matcher(group);

				matcher2.find();

				String atom = matcher2.group(1);

				String numStr = matcher2.group(2);

				if (numStr.equals("")) {
					numStr = "1.0";
				}

				Double number = Double.parseDouble(numStr);

				if (!balances.containsKey(atom)) {
					balances.put(atom, -sto * number);
				} else {
					balances.put(atom, balances.get(atom) + -sto * number);
				}

			}

		}

		System.err.println(balances);

		return balances;

	}

	/**
	 * Checks if a reaction is balanced
	 * 
	 * @return
	 */
	public static Boolean isBalanced(BioReaction r) {

		Double sum = 0.0;

		HashMap<String, Double> balances = computeAtomBalances(r);

		if (balances.size() == 0) {
			return false;
		}

		for (Double value : balances.values()) {
			sum = sum + value;
		}

		if (sum != 0.0) {
			return false;
		} else {
			return true;
		}
	}
	
}
