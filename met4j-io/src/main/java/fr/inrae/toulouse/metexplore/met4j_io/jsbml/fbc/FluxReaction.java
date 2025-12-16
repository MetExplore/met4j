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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc;


import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.util.stream.Collectors;


/**
 * This Class represents a new type of reactions that handles the SBML fbc
 * package. It uses the classical {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} to handle reaction
 * participants and the new {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} to handle complex gene
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
    private final BioReaction underlyingReaction;
    /**
     * The object holding the Gene Association
     *
     * @see GeneAssociation
     */
    private GeneAssociation reactionGeneAssociation;

    /**
     * Constructor using a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     *
     * @param reaction the {@link #underlyingReaction}
     */
    public FluxReaction(BioReaction reaction) {
        super(reaction.getId());
        this.underlyingReaction = reaction;
    }

    /**
     * Convert the reaction's {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} to a set of
     * enzymes and add them to the {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} given in
     * parameter.It also add them to the reaction as enzymes
     *
     * @param bn The {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} where the enzymes will be
     *           added
     */
    public void convertGeneAssociationstoComplexes(BioNetwork bn) {

        BioReaction rxn = this.getUnderlyingReaction();

        if (!bn.contains(rxn)) {
            throw new IllegalArgumentException(rxn + " not present in the network");
        }

        // BioNetwork bn=net.getUnderlyingBionet();

//		BioCompartment defaultCmp;
//		if ((defaultCmp = bn.findbioCompartmentInList("fake_compartment")) == null) {
//			defaultCmp = new BioCompartment();
//			defaultCmp.setAsFakeCompartment();
//			bn.addCompartment(defaultCmp);
//		}

        for (GeneSet sga : this.getReactionGeneAssociation()) {

            String enzymeId;
            if (sga.size() == 1) {
                enzymeId = sga.iterator().next();
            } else {
                enzymeId = sga.stream().sorted().collect(Collectors.joining("_AND_"));
            }

            affectEnzyme(bn, rxn, sga, enzymeId);

        }

    }

    private void affectEnzyme(BioNetwork bn, BioReaction rxn, GeneSet sga, String enzymeId) {
        BioEnzyme enz = bn.getEnzyme(enzymeId);

        if (enz == null) {
            enz = new BioEnzyme(enzymeId, enzymeId);
            bn.add(enz);
        }

        for (String geneId : sga.getGeneIds()) {
            BioGene gene = bn.getGene(geneId);
            if (gene == null) {
                gene = new BioGene(geneId);
                BioProtein prot = new BioProtein(geneId);
                bn.add(gene, prot);
            }

            BioProtein prot = bn.getProtein(geneId);
            if (prot == null) {
                prot = new BioProtein(geneId);
                bn.add(prot);
            }
            bn.affectGeneProduct(prot, gene);
            bn.affectSubUnit(enz, 1.0, prot);

        }

        bn.affectEnzyme(rxn, enz);
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
     * Get the {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} of the reaction
     *
     * @return {@link #reactionGeneAssociation}
     */
    public GeneAssociation getReactionGeneAssociation() {
        return reactionGeneAssociation;
    }

    /**
     * Set {@link #reactionGeneAssociation} to a new value
     *
     * @param reactionGAs the new value of {@link #reactionGeneAssociation}
     */
    public void setReactionGeneAssociation(GeneAssociation reactionGAs) {
        this.reactionGeneAssociation = reactionGAs;
    }

}
