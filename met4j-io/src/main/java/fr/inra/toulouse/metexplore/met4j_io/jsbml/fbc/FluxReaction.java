package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;


import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioEnzymeUtils;


/**
 * This Class represents a new type of reactions that handles the SBML fbc
 * package. It uses the classical {@link BioReaction} to handle reaction
 * participants and the new {@link GeneAssociation} to handle complex gene
 * associations
 * 
 * @author Benjamin
 * @since 3.0
 */
public class FluxReaction extends BioEntity {

	/**
	 * The actual {@link BioReaction} that is behind this
	 * {@link FluxReaction}
	 */
	private BioReaction underlyingReaction;
	/**
	 * The object holding the Gene Association
	 * 
	 * @see GeneAssociation
	 */
	private GeneAssociation reactionGeneAssociation;

	/**
	 * Constructor using a {@link BioReaction}
	 * 
	 * @param reaction2
	 *            the {@link #underlyingReaction}
	 */
	public FluxReaction(BioReaction reaction) {
		super(reaction.getId());
		this.underlyingReaction = reaction;
	}

	/**
	 * Convert the reaction's {@link GeneAssociation} to a set of
	 * {@link BioComplex} and add them to the {@link BioNetwork} given in
	 * parameter.</br>It also add them to the reaction as enzymes
	 * 
	 * @param bn
	 *            The {@link BioNetwork} where the {@link BioComplex} will be
	 *            added
	 */
	public void convertGeneAssociationstoComplexes(BioNetwork bn) {

		BioReaction rxn = this.getUnderlyingReaction();
		
		if(! bn.contains(rxn)) {
			throw new IllegalArgumentException(rxn+" not present in the network");
		}
		
		// BioNetwork bn=net.getUnderlyingBionet();

//		BioCompartment defaultCmp;
//		if ((defaultCmp = bn.findbioCompartmentInList("fake_compartment")) == null) {
//			defaultCmp = new BioCompartment();
//			defaultCmp.setAsFakeCompartment();
//			bn.addCompartment(defaultCmp);
//		}

		for (GeneSet sga : this.getReactionGeneAssociation()) {
			BioCollection<BioProtein> protlist = new BioCollection<BioProtein>();

			// for each gene get the corresponding protein from the network or
			// create it if it doesn't exists
			for (BioGene gene : sga) {
				
				if(! bn.contains(gene))
				{
					bn.add(gene);
				}
				
				BioProtein prot;

				if (bn.getProteinsView().containsId(gene.getId())) {
					prot = bn.getProteinsView().get(gene.getId());
				} else {
					prot = new BioProtein(gene.getId(), gene.getName());
					bn.add(prot);
				}
				
				bn.affectGeneProduct(prot, gene);

				protlist.add(prot);
			}

			if (protlist.size() == 1) {
				
				BioProtein prot = protlist.iterator().next();
				
				BioEnzyme enz = new BioEnzyme(prot.getId(), prot.getName());
				enz.setName(prot.getName());
				
				if(! bn.contains(enz)) {
					bn.add(enz);
				}
				bn.affectSubUnit(prot, 1.0, enz);
				
				bn.affectEnzyme(enz, rxn);

			} else {
				
				String id = BioEnzymeUtils.createIdFromProteins(protlist);
				
				BioEnzyme enz = new BioEnzyme(id, id);
				
				if(! bn.contains(enz)) {
					bn.add(enz);
				}
				
				for(BioProtein prot : protlist)
				{
					bn.affectSubUnit(prot, 1.0, enz);
				}
				
				bn.affectEnzyme(enz, rxn);

			}

		}

	}

	/**
	 * Get the {@link #underlyingReaction}
	 * 
	 * @return the {@link #underlyingReaction}
	 */
	public BioReaction getUnderlyingReaction() {
		return underlyingReaction;
	}

	/**
	 * Get the {@link GeneAssociation} of the reaction
	 * 
	 * @return {@link #reactionGeneAssociation}
	 */
	public GeneAssociation getReactionGeneAssociation() {
		return reactionGeneAssociation;
	}

	/**
	 * Set {@link #reactionGeneAssociation} to a new value
	 * 
	 * @param reactionGAs
	 *            the new value of {@link #reactionGeneAssociation}
	 */
	public void setReactionGeneAssociation(GeneAssociation reactionGAs) {
		this.reactionGeneAssociation = reactionGAs;
	}

}