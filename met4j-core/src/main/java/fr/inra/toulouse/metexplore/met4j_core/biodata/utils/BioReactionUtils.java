/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inra.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEnzymeParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;

public class BioReactionUtils {
	
	/**
	 * Comparison with another reaction : if the substrates and the products
	 * have the same id, return true
	 */
	public static Boolean areRedundant(BioNetwork network, BioReaction r1, BioReaction r2) {

		
		if(! network.contains(r1))
		{
			throw new IllegalArgumentException(r1.getId()+" is not present in the network");
		}
		
		if(! network.contains(r2))
		{
			throw new IllegalArgumentException(r2.getId()+" is not present in the network");
		}
		
		
		if(r1.isReversible() != r2.isReversible())
		{
			return false;
		}
		
		BioCollection<BioReactant> leftR1 = network.getLeftReactants(r1);
		BioCollection<BioReactant> leftR2 = network.getLeftReactants(r2);
		BioCollection<BioReactant> rightR1 = network.getRightReactants(r1);
		BioCollection<BioReactant> rightR2 = network.getRightReactants(r2);
		
		Boolean flag1 = leftR1.containsAll(leftR2) &&
				leftR2.containsAll(leftR1) && 
				rightR1.containsAll(rightR2) &&
				rightR2.containsAll(rightR1);
		
		if(! r1.isReversible())
		{
			return flag1;
		}
		else 
		{
			Boolean flag2 = rightR1.containsAll(leftR2) &&
					leftR2.containsAll(rightR1) && 
					leftR1.containsAll(rightR2) &&
					rightR2.containsAll(leftR1);
			
			return flag1 || flag2;
		}
		

	}
	
	
	
	/**
	 * 
	 * @param network
	 * @param r
	 * @return
	 */
	public static String getGPR(BioNetwork network, BioReaction r, Boolean getGeneNames) {
		
		if(! network.contains(r))
		{
			throw new IllegalArgumentException(r.getId()+" is not present in the network");
		}
		
		BioCollection<BioEnzyme> enzymes = r.getEnzymesView();
		
		TreeSet<String> geneIdSets  = new TreeSet<>();
		
		for(BioEnzyme enz : enzymes)
		{
			BioCollection<BioEnzymeParticipant> participants = enz.getParticipantsView();
			
			TreeSet<String> geneIds = new TreeSet<>();
			
			for(BioEnzymeParticipant p : participants) {
				BioPhysicalEntity ent = p.getPhysicalEntity();
				
				if(ent.getClass().equals(BioProtein.class))
				{
					BioProtein prot = (BioProtein) ent;
					BioGene g = prot.getGene();
					
					String id = getGeneNames ? g.getName() : g.getId();
					
					geneIds.add(id);
					
				}
			}
			
			if(geneIds.size() > 0)
			{
				String geneIdString = StringUtils.join(geneIds.toArray(new String[0]), " AND ");
				geneIdSets.add(geneIdString);
			}
			
		}
		
		TreeSet<String> finalSet;
		
		if(geneIdSets.size() > 1 ){
			
			finalSet = geneIdSets.stream().map(s -> "( " + s + " )").collect(Collectors.toCollection(TreeSet::new));
			
		}
		else {
			finalSet = geneIdSets;
		}
		
		
		String gpr = StringUtils.join(finalSet.toArray(new String[0]), " OR ");
		
		
		return gpr;
		
	}
	
	
//	/**
//	 * 
//	 * @return a ArrayList<String> corresponding to the association between
//	 *         genes and between proteins that enable the catalysis of the
//	 *         reaction Ex : res.get(0) g1 and ( g2 or g3 ) res.get(1) p1 and p2
//	 */
//	public static ArrayList<String> getGPR(BioReaction r) {
//
//		String geneStr = "";
//		String protStr = "";
//		ArrayList<String> res = new ArrayList<String>();
//
//		int j = 0;
//
//		for (Iterator<String> iterEnz = r.getEnzList().keySet().iterator(); iterEnz
//				.hasNext();) {
//			j++;
//
//			if (j > 1) {
//				protStr = protStr + " or ";
//				geneStr = geneStr + " or ";
//			}
//
//			BioPhysicalEntity enzyme = r.getEnzList().get(iterEnz.next());
//
//			String classe = enzyme.getClass().getSimpleName();
//
//			HashMap<String, BioGene> listOfGenes = new HashMap<String, BioGene>();
//
//			HashMap<String, BioProtein> listOfProteins = new HashMap<String, BioProtein>();
//
//			if (classe.compareTo("BioProtein") == 0) {
//				listOfProteins.put(enzyme.getId(), (BioProtein) enzyme);
//
//				listOfGenes = ((BioProtein) enzyme).getGeneList();
//
//			} else if (classe.compareTo("BioComplex") == 0) {
//
//				listOfGenes = ((BioComplex) enzyme).getGeneList();
//
//				HashMap<String, BioPhysicalEntity> componentList = ((BioComplex) enzyme)
//						.getAllComponentList();
//
//				for (Iterator<String> iterComponent = componentList.keySet()
//						.iterator(); iterComponent.hasNext();) {
//
//					BioPhysicalEntity component = componentList
//							.get(iterComponent.next());
//
//					if (component.getClass().getSimpleName()
//							.compareTo("BioProtein") == 0) {
//						listOfProteins.put(component.getId(),
//								(BioProtein) component);
//					}
//
//				}
//			}
//			int k = 0;
//
//			geneStr = geneStr + "( ";
//
//			for (Iterator<String> iterGene = listOfGenes.keySet().iterator(); iterGene
//					.hasNext();) {
//				k++;
//
//				if (k > 1) {
//					geneStr = geneStr + " and ";
//				}
//
//				BioGene gene = listOfGenes.get(iterGene.next());
//
//				geneStr = geneStr + StringUtils.htmlEncode(gene.getName());
//			}
//
//			geneStr = geneStr + " )";
//
//			protStr = protStr + "( ";
//
//			k = 0;
//
//			for (Iterator<String> iterProt = listOfProteins.keySet().iterator(); iterProt
//					.hasNext();) {
//				k++;
//				if (k > 1) {
//					protStr = protStr + " and ";
//				}
//
//				BioProtein prot = listOfProteins.get(iterProt.next());
//				protStr = protStr + StringUtils.htmlEncode(prot.getName());
//			}
//
//			protStr = protStr + " )";
//
//		}
//
//		res.add(geneStr);
//		res.add(protStr);
//
//		return res;
//
//	}
//	
//	/**
//	 * Compute the atom balances
//	 * 
//	 * @return
//	 */
//	public static HashMap<String, Double> computeAtomBalances(BioReaction r) {
//
//		HashMap<String, Double> balances = new HashMap<String, Double>();
//
//		for (BioParticipant bpe : r.getLeftParticipantList()
//				.values()) {
//
//			String stoStr = bpe.getStoichiometricCoefficient();
//
//			Double sto = 0.0;
//
//			try {
//				sto = Double.parseDouble(stoStr);
//			} catch (NumberFormatException e) {
//				System.err.println("Stoichiometry not valid in the reaction "
//						+ r.getId());
//				return new HashMap<String, Double>();
//			}
//
//			String formula = bpe.getPhysicalEntity().getChemicalFormula();
//
//			if (formula.equals("NA")) {
//				System.err.println("No formula for "
//						+ bpe.getPhysicalEntity().getId() + " in "
//						+ r.getId());
//				return new HashMap<String, Double>();
//			}
//
//			String REGEX = "[A-Z]{1}[a-z]*[0-9]*";
//
//			Pattern pattern = Pattern.compile(REGEX);
//			Matcher matcher = pattern.matcher(formula);
//
//			while (matcher.find()) {
//				String group = matcher.group(0);
//
//				String REGEX2 = "([A-Z]{1}[a-z]*)([0-9]*)";
//
//				Pattern pattern2 = Pattern.compile(REGEX2);
//				Matcher matcher2 = pattern2.matcher(group);
//
//				matcher2.find();
//
//				String atom = matcher2.group(1);
//
//				String numStr = matcher2.group(2);
//
//				if (numStr.equals("")) {
//					numStr = "1.0";
//				}
//
//				Double number = Double.parseDouble(numStr);
//
//				if (!balances.containsKey(atom)) {
//					balances.put(atom, sto * number);
//				} else {
//					balances.put(atom, balances.get(atom) + sto * number);
//				}
//
//			}
//
//		}
//
//		for (BioParticipant bpe : r.getRightParticipantList()
//				.values()) {
//
//			String stoStr = bpe.getStoichiometricCoefficient();
//
//			Double sto = 0.0;
//
//			try {
//				sto = Double.parseDouble(stoStr);
//			} catch (NumberFormatException e) {
//				System.err.println("Stoichiometry not valid in the reaction "
//						+ r.getId());
//				return new HashMap<String, Double>();
//			}
//
//			String formula = bpe.getPhysicalEntity().getChemicalFormula();
//
//			if (formula.equals("NA")) {
//				System.err.println("No formula for "
//						+ bpe.getPhysicalEntity().getId() + " in "
//						+ r.getId());
//				return new HashMap<String, Double>();
//			}
//
//			String REGEX = "[A-Z]{1}[a-z]*[0-9]*";
//
//			Pattern pattern = Pattern.compile(REGEX);
//			Matcher matcher = pattern.matcher(formula);
//
//			while (matcher.find()) {
//				String group = matcher.group(0);
//
//				String REGEX2 = "([A-Z]{1}[a-z]*)([0-9]*)";
//
//				Pattern pattern2 = Pattern.compile(REGEX2);
//				Matcher matcher2 = pattern2.matcher(group);
//
//				matcher2.find();
//
//				String atom = matcher2.group(1);
//
//				String numStr = matcher2.group(2);
//
//				if (numStr.equals("")) {
//					numStr = "1.0";
//				}
//
//				Double number = Double.parseDouble(numStr);
//
//				if (!balances.containsKey(atom)) {
//					balances.put(atom, -sto * number);
//				} else {
//					balances.put(atom, balances.get(atom) + -sto * number);
//				}
//
//			}
//
//		}
//
//		System.err.println(balances);
//
//		return balances;
//
//	}
//
//	/**
//	 * Checks if a reaction is balanced
//	 * 
//	 * @return
//	 */
//	public static Boolean isBalanced(BioReaction r) {
//
//		Double sum = 0.0;
//
//		HashMap<String, Double> balances = computeAtomBalances(r);
//
//		if (balances.size() == 0) {
//			return false;
//		}
//
//		for (Double value : balances.values()) {
//			sum = sum + value;
//		}
//
//		if (sum != 0.0) {
//			return false;
//		} else {
//			return true;
//		}
//	}
	
}
