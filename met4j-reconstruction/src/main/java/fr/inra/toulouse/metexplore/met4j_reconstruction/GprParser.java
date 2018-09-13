package fr.inra.toulouse.metexplore.met4j_reconstruction;

//import java.util.ArrayList;
//
//import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
//import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
//import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
//import fr.inra.toulouse.metexplore.met4j_core.biodata.BioParticipant;
//import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
//import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

public class GprParser {
	
//	/**
//	 * Reads a gene association written in a Palsson way. Creates genes if they
//	 * don't exist. Create proteins and enzymes from genes.
//	 * 
//	 * @param reactionId
//	 * @param gpr
//	 * @return true if no error, false otherwise TODO : test the gpr
//	 */
//	public static Boolean setGeneAssociationFromString(BioNetwork bn, String reactionId, String gpr) {
//
//		Boolean flag = true;
//
//		BioReaction rxn = bn.getBiochemicalReactionList().get(
//				reactionId);
//
//		rxn.getEnzList().clear();
//		rxn.getEnzrxnsList().clear();
//
//		String[] tab;
//
//		ArrayList<String[]> genesAssociated = new ArrayList<String[]>();
//
//		if (!gpr.equals("") && !gpr.equals("NA")) {
//
//			if (gpr.contains(" or ")) {
//				tab = gpr.split(" or ");
//			} else {
//				tab = new String[1];
//				tab[0] = gpr;
//			}
//
//			for (String genesAssociatedStr : tab) {
//
//				genesAssociatedStr = genesAssociatedStr.replaceAll("[\\(\\)]",
//						"");
//
//				String[] tab2;
//
//				if (genesAssociatedStr.contains(" and ")) {
//					tab2 = genesAssociatedStr.split(" and ");
//				} else {
//					tab2 = new String[1];
//					tab2[0] = genesAssociatedStr;
//				}
//
//				int n = tab2.length;
//
//				for (int k = 0; k < n; k++) {
//					tab2[k] = tab2[k].replaceAll(" ", "");
//				}
//
//				genesAssociated.add(tab2);
//
//			}
//		}
//
//		for (int k = 0; k < genesAssociated.size(); k++) {
//			String[] tabGenes = genesAssociated.get(k);
//			String enzymeId = StringUtils.implode(tabGenes, "_and_");
//
//			BioComplex enzyme;
//
//			if (!bn.getComplexList().containsKey(enzymeId)) {
//				enzyme = new BioComplex(enzymeId, enzymeId);
//
//				bn.addComplex(enzyme);
//			}
//
//			enzyme = bn.getComplexList().get(enzymeId);
//
//			rxn.addEnz(enzyme);
//
//			BioProtein protein;
//
//			if (!bn.getProteinList().containsKey(enzymeId)) {
//				protein = new BioProtein(enzymeId, enzymeId);
//				bn.addProtein(protein);
//			}
//			protein = bn.getProteinList().get(enzymeId);
//			enzyme.addComponent(new BioParticipant(protein));
//
//			for (int u = 0; u < tabGenes.length; u++) {
//				String geneId = tabGenes[u];
//				BioGene gene;
//				if (!bn.getGeneList().containsKey(geneId)) {
//					gene = new BioGene(geneId, geneId);
//					bn.addGene(gene);
//				}
//
//				gene = bn.getGeneList().get(geneId);
//
//				protein.addGene(gene);
//			}
//		}
//
//		return flag;
//
//	}

}
