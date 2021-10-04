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

package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>BioNetworkUtils class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioNetworkUtils {

    /**
     * Return choke reactions
     * A choke reaction involves metabolites
     * that are consumed or produced only by this reaction
     *
     * @param network a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public static BioCollection<BioReaction> getChokeReactions(BioNetwork network) {

        BioCollection<BioReaction> chokeReactions = new BioCollection<>();

        for (BioReaction r : network.getReactionsView()) {

            BioCollection<BioMetabolite> metabolites = network.getLefts(r);
            metabolites.addAll(network.getRights(r));

            for (BioMetabolite m : metabolites) {

                BioCollection<BioReaction> reactionsAsSubstrate = network.getReactionsFromSubstrate(m);
                BioCollection<BioReaction> reactionsAsProduct = network.getReactionsFromProduct(m);

                reactionsAsSubstrate.remove(r);
                reactionsAsProduct.remove(r);

                if (reactionsAsSubstrate.size() == 0 || reactionsAsProduct.size() == 0) {
                    chokeReactions.add(r);
                    break;
                }

            }
        }

        return chokeReactions;

    }

    /**
     * Remove from a network all the metabolites not connected to any reaction
     *
     * @param network a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork}
     */
    public static void removeNotConnectedMetabolites(BioNetwork network) {
        BioCollection<BioMetabolite> metabolites = network.getMetabolitesView();
        for (BioMetabolite m : metabolites) {
            if (network.getReactionsFromMetabolite(m).size() == 0) {
                network.removeOnCascade(m);
            }
        }
    }

    /**
     * <p>deepCopy.</p>
     *
     * @param network a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     */
    public static BioNetwork deepCopy(BioNetwork network) {

        BioNetwork newNetwork = new BioNetwork(network);

        // Copy metabolites
        for (BioMetabolite metabolite : network.getMetabolitesView()) {
            BioMetabolite newMetabolite = new BioMetabolite(metabolite);
            newNetwork.add(newMetabolite);
        }

        // Copy compartments (empty for the moment)
        for (BioCompartment cpt : network.getCompartmentsView()) {
            BioCompartment newCpt = new BioCompartment(cpt);
            newNetwork.add(newCpt);

            // Copy metabolites in the compartments
            cpt.getComponentsView().stream().
                    filter((c) -> c.getClass().equals(BioMetabolite.class)).
                    map(BioEntity::getId).
                    forEach((id) -> {
                        BioMetabolite newMetabolite = newNetwork.getMetabolitesView().get(id);
                        newNetwork.affectToCompartment(newCpt, newMetabolite);
                    });
        }

        // Copy genes
        for (BioGene gene : network.getGenesView()) {
            BioGene newGene = new BioGene(gene);
            newNetwork.add(newGene);
        }

        // Copy proteins
        for (BioProtein protein : network.getProteinsView()) {
            BioProtein newProtein = new BioProtein(protein);
            newNetwork.add(newProtein);
            if (protein.getGene() != null) {
                String geneId = protein.getGene().getId();
                BioGene newGene = newNetwork.getGenesView().get(geneId);
                newNetwork.affectGeneProduct(newProtein, newGene);
            }
        }

        // Copy enzymes
        for (BioEnzyme enzyme : network.getEnzymesView()) {
            BioEnzyme newEnzyme = new BioEnzyme(enzyme);

            newNetwork.add(newEnzyme);

            BioCollection<BioEnzymeParticipant> participants = enzyme.getParticipantsView();

            for (BioEnzymeParticipant participant : participants) {
                Double quantity = participant.getQuantity();

                BioEnzymeParticipant newParticipant = null;
                if (participant.getPhysicalEntity().getClass().equals(BioMetabolite.class)) {
                    BioMetabolite metabolite = (BioMetabolite) participant.getPhysicalEntity();
                    BioMetabolite newMetabolite = newNetwork.getMetabolitesView().get(metabolite.getId());
                    newParticipant = new BioEnzymeParticipant(newMetabolite, quantity);
                } else if (participant.getPhysicalEntity().getClass().equals(BioProtein.class)) {
                    BioProtein protein = (BioProtein) participant.getPhysicalEntity();
                    BioProtein newProtein = newNetwork.getProteinsView().get(protein.getId());
                    newParticipant = new BioEnzymeParticipant(newProtein, quantity);
                } else {
                    System.err.println("BioPhysical entity not recognized as enzyme participant : "
                            + participant.getPhysicalEntity().getId()
                            + "(" + participant.getPhysicalEntity().getClass() + ")");
                }

                if(newParticipant != null)
                {
                    newNetwork.affectSubUnit(newEnzyme, newParticipant);
                }

            }
        }


        // Copy reactions
        for (BioReaction r : network.getReactionsView()) {
            BioReaction newReaction = new BioReaction(r);
            newReaction.setSpontaneous(r.isSpontaneous());
            newReaction.setReversible(r.isReversible());
            newReaction.setEcNumber(r.getEcNumber());

            newNetwork.add(newReaction);

            // Copy lefts
            for (BioReactant reactant : r.getLeftReactantsView()) {
                BioMetabolite newMetabolite = newNetwork.getMetabolitesView().get(reactant.getMetabolite().getId());
                BioCompartment newCpt = newNetwork.getCompartmentsView().get(reactant.getLocation().getId());
                Double sto = reactant.getQuantity();
                BioReactant newReactant = new BioReactant(newMetabolite, sto, newCpt);

                newNetwork.affectLeft(newReaction, newReactant);
            }

            // Copy rights
            for (BioReactant reactant : r.getRightReactantsView()) {
                BioMetabolite newMetabolite = newNetwork.getMetabolitesView().get(reactant.getMetabolite().getId());
                BioCompartment newCpt = newNetwork.getCompartmentsView().get(reactant.getLocation().getId());
                Double sto = reactant.getQuantity();
                BioReactant newReactant = new BioReactant(newMetabolite, sto, newCpt);

                newNetwork.affectRight(newReaction, newReactant);
            }

            // Copy enzymes
            for(BioEnzyme enzyme: r.getEnzymesView())
            {
                BioEnzyme newEnzyme = newNetwork.getEnzymesView().get(enzyme.getId());
                newNetwork.affectEnzyme(newReaction, newEnzyme);
            }
        }

        // Copy pathways
        for(BioPathway pathway : network.getPathwaysView())
        {
            BioPathway newPathway = new BioPathway(pathway);
            newNetwork.add(newPathway);

            // Add reactions into pathway
            BioCollection<BioReaction> reactions = network.getReactionsFromPathways(pathway);

            for(BioReaction reaction : reactions)
            {
                BioReaction newReaction = newNetwork.getReactionsView().get(reaction.getId());
                newNetwork.affectToPathway(newPathway, newReaction);
            }
        }


        return newNetwork;

    }
}
