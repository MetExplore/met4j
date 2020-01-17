package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;

public class FluxReactionTest {

	@Test
	public void testConvertGeneAssociationstoComplexes() {

		BioNetwork network = new BioNetwork();

		BioGene g1 = new BioGene("g1");

		BioReaction r1 = new BioReaction("r1");
		
		network.add(r1);
		
		GeneSet set1 = new GeneSet();
		set1.add(g1);
		
		GeneAssociation a1 = new GeneAssociation();
		a1.add(set1);
		
		FluxReaction f1 = new FluxReaction(r1);
		
		f1.setReactionGeneAssociation(a1);
		f1.convertGeneAssociationstoComplexes(network);
		
		Set<String> geneIds = new HashSet<String>();
		geneIds.add("g1");
		
		assertTrue(network.getProteinsView().getIds().contains("g1"));
		assertTrue(network.getEnzymesView().getIds().contains("g1"));
		assertTrue(network.getReactionsFromGenes(geneIds, true).contains(r1));
		
		BioGene g2 = new BioGene("g2");
		set1.add(g2);
		
		f1.convertGeneAssociationstoComplexes(network);
		geneIds.add("g2");
		
		assertTrue(network.getProteinsView().getIds().contains("g2"));
		assertTrue(network.getEnzymesView().getIds().contains("g1_AND_g2"));
		assertTrue(network.getReactionsFromGenes(geneIds, true).contains(r1));
		
		
		
		

	}

}
