package fr.inra.toulouse.metexplore.met4j_io.annotations.gene;

import java.util.Set;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;

public class GeneAttributes {

	/**
	 * get pmids
	 * 
	 * @param r
	 * @return
	 */
	public static Set<Integer> getPmids(BioGene g) {

		return GenericAttributes.getPmids(g);

	}

	/**
	 * 
	 * set pmids
	 * 
	 * @param r
	 * @param pmids
	 */
	public static void setPmids(BioGene g, Set<Integer> pmids) {

		GenericAttributes.setPmids(g, pmids);

	}
	
}
