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
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

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
    public static BioCollection<BioReaction> getChokeReactions(@NonNull BioNetwork network) {

        BioCollection<BioReaction> chokeReactions = new BioCollection<>();

        BioCollection<BioReaction> reactions = network.getReactionsView();

        for (BioReaction r : reactions) {

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
    public static void removeNotConnectedMetabolites(@NonNull BioNetwork network) {
        BioCollection<BioMetabolite> metabolites = network.getMetabolitesView();
        for (BioMetabolite m : metabolites) {
            if (network.getReactionsFromMetabolite(m).size() == 0) {
                network.removeOnCascade(m);
            }
        }
    }

    public static void deepCopy(BioNetwork networkIn, BioNetwork networkOut) {
        deepCopy(networkIn, networkOut, true, false);
    }

    /**
     * <p>deepCopy.</p>
     *
     * @param networkIn    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object. The source network
     * @param networkOut   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object. The target network
     * @param keepGPR      keep the associations between reactions and genes (proteins and enzymes)
     * @param keepPrevious do not replace the entities in networkOut but complete them
     *                     For instance :
     *                     - add metabolites to a compartment
     *                     - add components to an enzyme
     *                     - add reactions to a pathway
     *                     - add enzymes to a reaction
     *                     But, it does not change the reactants of a reaction, ie the formula of the reaction.
     */
    public static void deepCopy(@NonNull BioNetwork networkIn, @NonNull BioNetwork networkOut, Boolean keepGPR, Boolean keepPrevious) {


        networkOut.setSynonyms(new ArrayList<>(networkIn.getSynonyms()));
        networkOut.setComment(networkIn.getComment());
        networkOut.setRefs(new HashMap<>(networkIn.getRefs()));
        networkOut.setAttributes(new HashMap<>(networkIn.getAttributes()));

        // Copy metabolites
        BioCollection<BioMetabolite> metabolitesToCopy = networkIn.getMetabolitesView();
        if (keepPrevious) {
            metabolitesToCopy.removeAll(networkOut.getMetabolitesView());
        }

        for (BioMetabolite metabolite : metabolitesToCopy) {
            BioMetabolite newMetabolite;
            if (!networkOut.containsEntityWithSameId(metabolite)) {
                newMetabolite = new BioMetabolite(metabolite);
                networkOut.add(newMetabolite);
            }
        }

        // Copy compartments
        BioCollection<BioCompartment> compartmentsToCopy = networkIn.getCompartmentsView();
        if (keepPrevious) {
            compartmentsToCopy.removeAll(networkOut.getCompartmentsView());
        }

        for (BioCompartment cpt : networkIn.getCompartmentsView()) {

            BioCompartment newCpt;

            if (compartmentsToCopy.contains(cpt) && (!networkOut.containsEntityWithSameId(cpt))) {
                newCpt = new BioCompartment(cpt);
                networkOut.add(newCpt);
            } else {
                newCpt = networkOut.getCompartment(cpt.getId());
            }

            // Copy metabolites in the compartments
            cpt.getComponentsView().stream().
                    filter((c) -> c.getClass().equals(BioMetabolite.class)).
                    map(BioEntity::getId).
                    forEach((id) -> {
                        BioMetabolite newMetabolite = networkOut.getMetabolite(id);
                        networkOut.affectToCompartment(newCpt, newMetabolite);
                    });
        }

        // Copy genes
        if (keepGPR) {
            BioCollection<BioGene> genesToCopy = networkIn.getGenesView();
            if (keepPrevious) {
                genesToCopy.removeAll(networkOut.getGenesView());
            }

            for (BioGene gene : genesToCopy) {
                if (!networkOut.containsEntityWithSameId(gene)) {
                    BioGene newGene = new BioGene(gene);
                    networkOut.add(newGene);
                }
            }
        }

        // Copy proteins
        if (keepGPR) {
            BioCollection<BioProtein> proteinsToCopy = networkIn.getProteinsView();
            if (keepPrevious) {
                proteinsToCopy.removeAll(networkOut.getProteinsView());
            }
            for (BioProtein protein : networkIn.getProteinsView()) {

                BioProtein newProtein;

                if (proteinsToCopy.contains(protein) && (!networkOut.containsEntityWithSameId(protein))) {
                    newProtein = new BioProtein(protein);
                    networkOut.add(newProtein);
                } else {
                    newProtein = networkOut.getProtein(protein.getId());
                }

                if (protein.getGene() != null) {
                    String geneId = protein.getGene().getId();
                    BioGene newGene = networkOut.getGene(geneId);
                    networkOut.affectGeneProduct(newProtein, newGene);
                }
            }
        }

        // Copy enzymes
        if (keepGPR) {

            BioCollection<BioEnzyme> enzymesToCopy = networkIn.getEnzymesView();
            if (keepPrevious) {
                enzymesToCopy.removeAll(networkOut.getEnzymesView());
            }

            for (BioEnzyme enzyme : networkIn.getEnzymesView()) {

                BioEnzyme newEnzyme;

                if (enzymesToCopy.contains(enzyme) && !networkOut.containsEntityWithSameId(enzyme)) {
                    newEnzyme = new BioEnzyme(enzyme);
                    networkOut.add(newEnzyme);
                } else {
                    newEnzyme = networkOut.getEnzyme(enzyme.getId());
                }

                BioCollection<BioEnzymeParticipant> participants = enzyme.getParticipantsView();

                for (BioEnzymeParticipant participant : participants) {
                    Double quantity = participant.getQuantity();

                    if (participant.getPhysicalEntity().getClass().equals(BioMetabolite.class)) {
                        BioMetabolite metabolite = (BioMetabolite) participant.getPhysicalEntity();
                        BioMetabolite newMetabolite = networkOut.getMetabolite(metabolite.getId());
                        networkOut.affectSubUnit(newEnzyme, quantity, newMetabolite);
                    } else if (participant.getPhysicalEntity().getClass().equals(BioProtein.class)) {
                        BioProtein protein = (BioProtein) participant.getPhysicalEntity();
                        BioProtein newProtein = networkOut.getProtein(protein.getId());
                        networkOut.affectSubUnit(newEnzyme, quantity, newProtein);
                    }
                }
            }
        }

        // Copy reactions
        BioCollection<BioReaction> reactionsIn = networkIn.getReactionsView();
        BioCollection<BioReaction> reactionsOut = networkOut.getReactionsView();

        BioCollection<BioReaction> reactionsToCopy = networkIn.getReactionsView();
        if (keepPrevious) {
            reactionsToCopy.removeAll(reactionsOut);
        }

        for (BioReaction r : reactionsIn) {

            BioReaction newReaction;

            if (reactionsToCopy.contains(r) && !networkOut.containsReaction(r.getId())) {

                newReaction = new BioReaction(r);
                newReaction.setSpontaneous(r.isSpontaneous());
                newReaction.setReversible(r.isReversible());
                newReaction.setEcNumber(r.getEcNumber());

                networkOut.add(newReaction);

                // Copy lefts
                for (BioReactant reactant : r.getLeftReactantsView()) {
                    BioMetabolite newMetabolite = networkOut.getMetabolite(reactant.getMetabolite().getId());
                    BioCompartment newCpt = networkOut.getCompartment(reactant.getLocation().getId());
                    Double sto = reactant.getQuantity();
                    networkOut.affectLeft(newReaction, sto, newCpt, newMetabolite);
                }

                // Copy rights
                for (BioReactant reactant : r.getRightReactantsView()) {
                    BioMetabolite newMetabolite = networkOut.getMetabolite(reactant.getMetabolite().getId());
                    BioCompartment newCpt = networkOut.getCompartment(reactant.getLocation().getId());
                    Double sto = reactant.getQuantity();
                    networkOut.affectRight(newReaction, sto, newCpt, newMetabolite);
                }
            } else {
                newReaction = networkOut.getReaction(r.getId());
            }

            // Copy enzymes
            if (keepGPR) {
                for (BioEnzyme enzyme : r.getEnzymesView()) {
                    BioEnzyme newEnzyme = networkOut.getEnzyme(enzyme.getId());
                    networkOut.affectEnzyme(newReaction, newEnzyme);
                }
            }
        }

        // Copy pathways
        BioCollection<BioPathway> pathwaysToCopy = networkIn.getPathwaysView();
        if (keepPrevious) {
            pathwaysToCopy.removeAll(networkOut.getPathwaysView());
        }

        for (BioPathway pathway : networkIn.getPathwaysView()) {

            if (pathwaysToCopy.contains(pathway) && !networkOut.containsEntityWithSameId(pathway)) {
                BioPathway newPathway = new BioPathway(pathway);
                networkOut.add(newPathway);
            }

            BioPathway newPathway = networkOut.getPathway(pathway.getId());
            // Add reactions into pathway
            BioCollection<BioReaction> reactions = networkIn.getReactionsFromPathways(pathway);

            for (BioReaction reaction : reactions) {
                BioReaction newReaction = networkOut.getReaction(reaction.getId());
                networkOut.affectToPathway(newPathway, newReaction);
            }
        }
    }
}
