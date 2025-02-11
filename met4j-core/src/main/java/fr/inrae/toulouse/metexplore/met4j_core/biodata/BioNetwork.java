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
/*
 * Created on 1 juil. 2005
 * L.C
 */
package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The BioNetwork class is the essential class of met4j.
 * <p>
 * It contains and links all the entities composing a metabolic network (metabolites, reactions,
 * pathways, genes, proteins, enzymes, compartments)
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioNetwork extends BioEntity {

    private final BioCollection<BioPathway> pathways = new BioCollection<>();

    private final BioCollection<BioMetabolite> metabolites = new BioCollection<>();

    private final BioCollection<BioProtein> proteins = new BioCollection<>();

    private final BioCollection<BioGene> genes = new BioCollection<>();

    private final BioCollection<BioReaction> reactions = new BioCollection<>();

    private final BioCollection<BioCompartment> compartments = new BioCollection<>();

    private final BioCollection<BioEnzyme> enzymes = new BioCollection<>();

    private final BioCollection<BioReactant> reactants = new BioCollection<>();

    private final BioCollection<BioEnzymeParticipant> enzymeParticipants = new BioCollection<>();

    /**
     * <p>Constructor for BioNetwork.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public BioNetwork(@NonNull String id) {
        super(id);
    }

    /**
     * <p>Constructor for BioNetwork.</p>
     */
    public BioNetwork() {
        super("NA");
    }


    /**
     * Get a reaction by its id
     *
     * @param id a String
     * @return a {@link BioReaction}
     */
    public BioReaction getReaction(@NonNull String id) {
        return this.reactions.get(id);
    }

    /**
     * Check if a reaction with a specific id already exists in the network
     *
     * @param id a String
     * @return true if there is a {@link BioReaction} with this id exists in the BioNetwork
     */
    public boolean containsReaction(@NonNull String id) {
        return this.reactions.containsId(id);
    }


    /**
     * Get a metabolite by its id
     * @param id a String
     * @return a {@link BioMetabolite}
     */
    public BioMetabolite getMetabolite(@NonNull String id) {
        return this.metabolites.get(id);
    }

    /**
     * Check if a metabolite with a specific id already exists in the network
     *
     * @param id a String
     * @return true if there is a {@link BioMetabolite} with this id exists in the BioNetwork
     */
    public boolean containsMetabolite(@NonNull String id) {
        return this.metabolites.containsId(id);
    }

    /**
     * Get a pathway by its id
     * @param id a String
     * @return a {@link BioPathway}
     */
    public BioPathway getPathway(@NonNull String id) {
        return this.pathways.get(id);
    }

    /**
     * Check if a pathway with a specific id already exists in the network
     *
     * @param id a String
     * @return true if there is a {@link BioPathway} with this id exists in the BioNetwork
     */
    public boolean containsPathway(@NonNull String id) {
        return this.pathways.containsId(id);
    }

    /**
     * Get a protein by its id
     * @param id a String
     * @return a {@link BioProtein}
     */
    public BioProtein getProtein(@NonNull String id) {
        return this.proteins.get(id);
    }

    /**
     * Check if a pathway with a specific id already exists in the network
     *
     * @param id a String
     * @return true if there is a {@link BioPathway} with this id exists in the BioNetwork
     */
    public boolean containsProtein(@NonNull String id) {
        return this.proteins.containsId(id);
    }


    /**
     * Get a gene by its id
     * @param id a String
     * @return a {@link BioGene}
     */
    public BioGene getGene(String id) {
        return this.genes.get(id);
    }

    /**
     * Check if a gene with a specific id already exists in the network
     *
     * @param id a String
     * @return true if there is a {@link BioGene} with this id exists in the BioNetwork
     */
    public boolean containsGene(String id) {
        return this.genes.containsId(id);
    }

    /**
     * Get a compartment by its id
     * @param id a String
     * @return a {@link BioCompartment}
     */
    public BioCompartment getCompartment(String id) {
        return this.compartments.get(id);
    }

    /**
     * Check if a compartment with a specific id already exists in the network
     *
     * @param id a String
     * @return true if there is a {@link BioCompartment} with this id exists in the BioNetwork
     */
    public boolean containsCompartment(String id) {
        return this.compartments.containsId(id);
    }

    /**
     * Get an enzyme by its id
     * @param id a String
     * @return a {@link BioEnzyme}
     */
    public BioEnzyme getEnzyme(String id) {
        return this.enzymes.get(id);
    }

    /**
     * Check if an enzyme with a specific id already exists in the network
     *
     * @param id a String
     * @return true if there is a {@link BioEnzyme} with this id exists in the BioNetwork
     */
    public boolean containsEnzyme(String id) {
        return this.enzymes.containsId(id);
    }


    /**
     * Add several entities
     *
     * @param bioEntities 0 to several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity} instances
     */
    public void add(BioEntity... bioEntities) {

        for (BioEntity e : bioEntities) {
            this.add(e);
        }
    }

    /**
     * Add several entities
     *
     * @param bioEntities a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     */
    public void add(@NonNull BioCollection<?> bioEntities) {

        for (BioEntity e : bioEntities) {
            this.add(e);
        }
    }

    /**
     * Add one entity
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     */
    public void add(@NonNull BioEntity e) {
        if (e instanceof BioPathway) {
            this.addPathway((BioPathway) e);
        } else if (e instanceof BioMetabolite) {
            this.addMetabolite((BioMetabolite) e);
        } else if (e instanceof BioProtein) {
            this.addProtein((BioProtein) e);
        } else if (e instanceof BioGene) {
            this.addGene((BioGene) e);
        } else if (e instanceof BioReaction) {
            this.addReaction((BioReaction) e);
        } else if (e instanceof BioCompartment) {
            this.addCompartment((BioCompartment) e);
        } else if (e instanceof BioEnzyme) {
            this.addEnzyme((BioEnzyme) e);
        } else {
            throw new IllegalArgumentException(
                    "BioEntity \"" + e.getClass().getSimpleName() + "\" not supported by BioNetwork");
        }
    }

    /**
     * Adds a pathway in the network
     * The pathway must not contain reactions
     *
     * @param pathway : the {@link BioPathway} to add
     */
    public void addPathway(@NonNull BioPathway pathway) {
        if (pathway.getReactions().size() > 0) {
            throw new IllegalArgumentException("[addPathway] The pathway must be empty before adding it to a BioNetwork");
        }
        this.pathways.add(pathway);
    }

    /**
     * Add a Metabolite in the network
     *
     * @param metabolite : the {@link BioMetabolite} to add
     */
    public void addMetabolite(@NonNull BioMetabolite metabolite) {
        this.metabolites.add(metabolite);
    }

    /**
     * Add a protein in the network
     * The protein must not be affected to a gene before adding it to a BioNetwork.
     *
     * @param protein : the {@link BioProtein} to add
     */
    public void addProtein(@NonNull BioProtein protein) {
        if (protein.getGene() != null) {
            throw new IllegalArgumentException("[addProtein] The protein must not be affected to a gene before adding it to a BioNetwork");
        }
        this.proteins.add(protein);
    }

    /**
     * Add a gene in the BioNetwork
     *
     * @param gene : the {@link BioGene} to add
     */
    public void addGene(@NonNull BioGene gene) {
        this.genes.add(gene);
    }

    /**
     * Add a reaction in a BioNetwork
     * The reaction must not be linked to reactants nor to enzymes
     *
     * @param reaction : the {@link BioReaction} to add
     */
    public void addReaction(@NonNull BioReaction reaction) {

        if (reaction.getLeftReactants().size() > 0 || reaction.getRightReactants().size() > 0) {
            throw new IllegalArgumentException("[addReaction] The reaction must not contain substrates before adding it to a BioNetwork");
        }

        if (reaction.getEnzymes().size() > 0) {
            throw new IllegalArgumentException("[addReaction] The reaction must not be affected to a reaction before adding it to a BioNetwork");
        }

        this.reactions.add(reaction);

    }

    /**
     * Add a compartment in a BioNetwork
     * The compartment must be empty
     *
     * @param compartment : the {@link BioCompartment} to add
     */
    public void addCompartment(@NonNull BioCompartment compartment) {

        if (compartment.getComponents().size() > 0) {
            throw new IllegalArgumentException("[addCompartment] The compartment must be empty before adding it to a BioNetwork");
        }

        this.compartments.add(compartment);
    }

    /**
     * Add an enzyme in a BioNetwork
     * The enzyme must not contain participants
     *
     * @param enzyme : the {@link BioEnzyme} to add
     */
    public void addEnzyme(@NonNull BioEnzyme enzyme) {

        if (enzyme.getParticipants().size() > 0) {
            throw new IllegalArgumentException("[addEnzyme] The enzyme must not contain participants before adding it to a BioNetwork");
        }

        this.enzymes.add(enzyme);
    }


    /**
     * Remove on cascade a BioEntity
     *
     * @param e the {@link BioEntity} to remove
     */
    private void removeOnCascade(@NonNull BioEntity e) {

        if (e instanceof BioPathway) {
            this.pathways.remove(e);
        } else if (e instanceof BioMetabolite) {
            this.removeMetabolite((BioMetabolite) e);
        } else if (e instanceof BioProtein) {
            this.removeProtein((BioProtein) e);
        } else if (e instanceof BioGene) {
            this.removeGene((BioGene) e);
        } else if (e instanceof BioReaction) {
            this.removeReaction((BioReaction) e);
        } else if (e instanceof BioCompartment) {
            this.removeCompartment((BioCompartment) e);
        } else if (e instanceof BioEnzyme) {
            this.removeEnzyme((BioEnzyme) e);
        } else {

            throw new IllegalArgumentException(
                    "BioEntity \"" + e.getClass().getSimpleName() + "\" not supported by BioNetwork");
        }

    }

    /**
     * Remove on cascade an enzyme : remove also the enzymes in the reactions
     *
     * @param e enzyme
     */
    public void removeEnzyme(@NonNull BioEnzyme e) {

        BioCollection<BioReaction> reactions = this.reactions;

        for (BioReaction r : reactions) {
            this.removeEnzymeFromReaction(e, r);
        }

        this.enzymes.remove(e);

    }

    /**
     * Remove on cascade several entities
     *
     * @param entities 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     */
    public void removeOnCascade(@NonNull BioEntity... entities) {
        for (BioEntity e : entities) {
            removeOnCascade(e);
        }
    }

    /**
     * Remove on cascade several entities stored in a BioCollection
     *
     * @param entities a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     */
    public void removeOnCascade(@NonNull BioCollection<?> entities) {
        for (BioEntity e : entities) {
            removeOnCascade(e);
        }
    }


    /**
     * Remove protein from the network and from the enzymes and the compartments
     * where it is involved
     *
     * @param protein a {@link BioProtein} instance
     */
    private void removeProtein(@NonNull BioProtein protein) {

        this.proteins.remove(protein);

        this.getEnzymesView().forEach(e -> {
            BioCollection<BioEnzymeParticipant> participants = new BioCollection<>(
                    e.getParticipants());

            boolean remove = participants.stream().anyMatch(p -> p.getPhysicalEntity().equals(protein));

            if (remove) {
                this.removeOnCascade(e);
            }
        });

        this.compartments.forEach(c -> {
            BioCollection<BioEntity> components = new BioCollection<>(c.getComponents());

            components.forEach(p -> {
                if (p.equals(protein)) {
                    c.getComponents().remove(p);
                    if (c.getComponents().size() == 0) {
                        this.removeOnCascade(c);
                    }
                }
            });
        });

    }

    /**
     * Remove a metabolite from the network and from the reactions and compartments
     * where it is involved and from the c
     *
     * @param m a {@link BioMetabolite}
     */
    private void removeMetabolite(@NonNull BioMetabolite m) {

        BioCollection<BioReaction> reactions = this.getReactionsFromMetabolite(m);

        reactions.forEach(r -> {
            BioCollection<BioReactant> lefts = new BioCollection<>(r.getLeftReactants());

            for (BioReactant participant : lefts) {
                if (participant.getPhysicalEntity().equals(m)) {
                    r.getLeftReactants().remove(participant);
                }
            }

            BioCollection<BioReactant> rights = new BioCollection<>(r.getRightReactants());
            for (BioReactant participant : rights) {
                if (participant.getPhysicalEntity().equals(m)) {
                    r.getRightReactants().remove(participant);
                }
            }

            if (r.getLeftReactantsView().size() == 0 && r.getRightReactantsView().size() == 0) {
                this.removeReaction(r);
            }

        });

        BioCollection<BioCompartment> cpts = this.getCompartmentsOf(m);

        cpts.forEach(c -> {
            c.getComponents().remove(m);
            if (c.getComponents().size() == 0) {
                this.removeOnCascade(c);
            }
        });

        BioCollection<BioEnzyme> enzymesCopy = this.getEnzymesView();

        enzymesCopy.forEach(e -> {
            BioCollection<BioEnzymeParticipant> participants = new BioCollection<>(
                    e.getParticipants());

            boolean contains = false;

            for (BioEnzymeParticipant p : participants) {
                if (p.getPhysicalEntity().equals(m)) {
                    contains = true;
                    e.getParticipants().remove(p);
                }
            }

            if (e.getParticipantsView().size() == 0 && contains) {
                this.removeOnCascade(e);
            }
        });

        this.metabolites.remove(m);

    }

    /**
     * Remove a gene from the network and remove the link between the gene and
     * proteins.
     * If the protein is not linked to a gene anymore, remove the protein
     *
     * @param g a {@link BioGene}
     */
    private void removeGene(@NonNull BioGene g) {

        this.genes.remove(g);

        BioCollection<BioProtein> proteins = this.getProteinsView();
        proteins.forEach(p -> {
            if (p.getGene() != null && p.getGene().equals(g)) {
                this.removeOnCascade(p);
            }
        });

    }

    /**
     * Remove a reaction from the network and from the pathways where it is involved
     *
     * @param r a {@link BioReaction}
     */
    private void removeReaction(@NonNull BioReaction r) {

        BioCollection<BioPathway> pathways = this.getPathwaysFromReaction(r);

        pathways.forEach(p -> {
            p.removeReaction(r);
            if (p.getReactions().size() == 0) {
                this.removeOnCascade(p);
            }

        });
        this.reactions.remove(r);
    }

    /**
     * Remove a compartment and all the reactions that involve reactants in this
     * compartment
     *
     * @param c a BioCompartment
     */
    private void removeCompartment(@NonNull BioCompartment c) {

        this.getReactionsView().forEach(r -> {
            BioCollection<BioReactant> reactants = r.getReactantsView();
            for (BioReactant reactant : reactants) {
                if (reactant.getLocation().equals(c)) {
                    this.removeReaction(r);
                    break;
                }
            }
        });

        this.compartments.remove(c);

    }

    /**
     * add a relation reactant-reaction
     */
    private void affectLeft(@NonNull BioReaction reaction, @NonNull Double stoichiometry, @NonNull BioCompartment localisation, @NonNull BioMetabolite substrate) {

        affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.LEFT, substrate);

    }

    /**
     * Add several left metabolites to a reaction
     *
     * @param reaction      a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param stoichiometry the stroichiometric coefficient
     * @param localisation  the compartment of the substrates
     * @param substrates    0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     */
    public void affectLeft(@NonNull BioReaction reaction, @NonNull Double stoichiometry, @NonNull BioCompartment localisation, BioMetabolite... substrates) {

        for (BioMetabolite s : substrates)
            affectLeft(reaction, stoichiometry, localisation, s);

    }

    /**
     * Add several left metabolites to a reaction
     *
     * @param reaction      a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param stoichiometry the stroichiometric coefficient
     * @param localisation  the compartment of the substrates
     * @param substrates    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     */
    public void affectLeft(@NonNull BioReaction reaction, @NonNull Double stoichiometry, @NonNull BioCompartment localisation, @NonNull BioCollection<BioMetabolite> substrates) {

        for (BioMetabolite s : substrates)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.LEFT, s);

    }

    /**
     * add a relation reactant-reaction
     */
    private void affectLeft(@NonNull BioReaction reaction, @NonNull BioReactant reactant) {

        affectSideReaction(reactant, reaction, BioReaction.Side.LEFT);

    }

    /**
     * Add several left reactants to a reaction
     *
     * @param reaction  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param reactants 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
     */
    public void affectLeft(@NonNull BioReaction reaction, @NonNull BioReactant... reactants) {
        for (BioReactant reactant : reactants) {
            this.affectLeft(reaction, reactant);
        }
    }

    /**
     * Add several left reactants to a reaction
     *
     * @param reaction  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param reactants a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
     */
    public void affectLeft(@NonNull BioReaction reaction, @NonNull BioCollection<BioReactant> reactants) {
        this.affectLeft(reaction, reactants.toArray(new BioReactant[0]));
    }

    /**
     * Remove a left reactant
     *
     * @param e            a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} to remove from the left side of the reaction
     * @param localisation a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @param reaction     a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public void removeLeft(@NonNull BioMetabolite e, @NonNull BioCompartment localisation, @NonNull BioReaction reaction) {

        removeSideReaction(e, localisation, reaction, BioReaction.Side.LEFT);
    }

    /**
     * Add a relation product-reaction
     *
     * @param reaction      a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param stoichiometry the stoichiometric coefficient
     * @param localisation  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @param product       a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} to add to the right side of the reaction
     */
    public void affectRight(@NonNull BioReaction reaction, @NonNull Double stoichiometry, @NonNull BioCompartment localisation, @NonNull BioMetabolite product) {

        affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    /**
     * Add several relation product-reaction
     *
     * @param reaction      a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param stoichiometry the stoichiometric coefficient
     * @param localisation  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @param products      0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} to add to the right side of the reaction
     */
    public void affectRight(@NonNull BioReaction reaction, @NonNull Double stoichiometry, @NonNull BioCompartment localisation, @NonNull BioMetabolite... products) {
        for (BioMetabolite product : products)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    /**
     * Add several relation product-reaction
     *
     * @param reaction      a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param stoichiometry the stoichiometric coefficient
     * @param localisation  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @param products      a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} to add to the right side of the reaction
     */
    public void affectRight(@NonNull BioReaction reaction, @NonNull Double stoichiometry, @NonNull BioCompartment localisation, @NonNull BioCollection<BioMetabolite> products) {
        for (BioMetabolite product : products)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    /**
     * Add a relation product-reaction
     */
    private void affectRight(@NonNull BioReaction reaction, @NonNull BioReactant reactant) {
        affectSideReaction(reactant, reaction, BioReaction.Side.RIGHT);
    }

    /**
     * affect several reactant to the right side of a reaction
     *
     * @param reaction  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param reactants 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
     */
    public void affectRight(@NonNull BioReaction reaction, @NonNull BioReactant... reactants) {
        for (BioReactant reactant : reactants) {
            this.affectRight(reaction, reactant);
        }
    }

    /**
     * affect several reactant to the right side of a reaction
     *
     * @param reaction  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param reactants a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
     */
    public void affectRight(@NonNull BioReaction reaction, @NonNull BioCollection<BioReactant> reactants) {
        this.affectRight(reaction, reactants.toArray(new BioReactant[0]));
    }

    /**
     * Remove a right reactant
     *
     * @param e            a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} to remove from the right side of the reaction
     * @param localisation a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @param reaction     a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public void removeRight(@NonNull BioMetabolite e, @NonNull BioCompartment localisation, @NonNull BioReaction reaction) {

        removeSideReaction(e, localisation, reaction, BioReaction.Side.RIGHT);
    }

    /**
     * get a reactant with the same metabolite, stoichiometry and compartment
     *
     * @param metabolite    a {@link BioMetabolite}
     * @param stoichiometry a {@link Double}
     * @param compartment   a {@link BioCompartment}
     * @return a {@link BioReactant}
     */
    protected BioReactant getReactant(@NonNull BioMetabolite metabolite, @NonNull Double stoichiometry, @NonNull BioCompartment compartment) {
        Optional<BioReactant> any = this.reactants.stream()
                .filter(r -> r.getMetabolite().equals(metabolite) &&
                        r.getQuantity().equals(stoichiometry)
                        && r.getLocation().equals(compartment)).findAny();
        return any.orElse(null);
    }

    /**
     * @param reaction      a {@link BioReaction}
     * @param stoichiometry a {@link Double}
     * @param localisation  a {@link BioCompartment}
     * @param side          a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction.Side}
     * @param metabolite    a {@link BioMetabolite}
     */
    private void affectSideReaction(@NonNull BioReaction reaction, @NonNull Double stoichiometry, @NonNull BioCompartment localisation, @NonNull BioReaction.Side side, @NonNull BioMetabolite metabolite) {

        if(Double.isNaN(stoichiometry) || stoichiometry < 0.0) {
            throw new IllegalArgumentException("Error in stoichiometry for the metabolite "+metabolite.getId()
                    + " and the reaction "+reaction.getId()+ ", it should be strictly positive");
        }

        if(stoichiometry == 0.0) {
            System.err.println("[WARNING] The stoichiometry for the metabolite "+metabolite.getId()
                    + " and the reaction "+reaction.getId()+ "is null, it won't be added to the reaction");
            return;
        }


        BioReactant reactant = this.getReactant(metabolite, stoichiometry, localisation);

        if (reactant == null) {
            reactant = new BioReactant(metabolite, stoichiometry, localisation);
            this.reactants.add(reactant);
        }

        this.affectSideReaction(reactant, reaction, side);

    }

    private void addReactant(BioReactant reactant) {
        this.reactants.add(reactant);
    }

    /**
     * Add reactants in the list of reactants
     *
     * @param reactants a list of {@link BioReactant}
     */
    protected void addReactants(@NonNull BioReactant... reactants) {
        for (BioReactant r : reactants) {
            this.addReactant(r);
        }
    }


    private void affectSideReaction(@NonNull BioReactant reactant, @NonNull BioReaction reaction, @NonNull BioReaction.Side side) {

        BioCompartment localisation = reactant.getLocation();

        if (!this.reactants.contains(reactant)) {
            throw new IllegalArgumentException("Reactant " + reactant.getId() + " not in the network");
        }

        // The network must contain the compartment
        if (!this.compartments.contains(localisation)) {
            throw new IllegalArgumentException("Compartment " + localisation.getId() + " not in the network");
        }

        if (!this.metabolites.contains(reactant.getMetabolite())) {
            throw new IllegalArgumentException(
                    "Metabolite " + reactant.getMetabolite().getId() + " not in the network");
        }

        // The metabolite must be connected to the compartment
        if (!localisation.getComponents().contains(reactant.getMetabolite())) {
            throw new IllegalArgumentException(
                    "Metabolite " + reactant.getMetabolite().getId() + " not in the compartment");
        }

        // The network must contain the reaction
        if (!this.reactions.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not in the network");
        }

        if (side.equals(BioReaction.Side.LEFT)) {
            if (!reaction.getLeftReactants().containsId(reactant.getId())) {
                reaction.getLeftReactants().add(reactant);
            }
        } else {
            if (!reaction.getRightReactants().containsId(reactant.getId())) {
                reaction.getRightReactants().add(reactant);
            }
        }
    }

    /**
     * Remove an entity from a side of reaction
     */
    private void removeSideReaction(@NonNull BioMetabolite e, @NonNull BioCompartment localisation, @NonNull BioReaction reaction, @NonNull BioReaction.Side side) {

        // The network must contain the compartment
        if (!this.compartments.contains(localisation)) {
            throw new IllegalArgumentException("Compartment " + localisation.getId() + " not in the network");
        }

        if (!this.metabolites.contains(e)) {
            throw new IllegalArgumentException("Metabolite " + e.getId() + " not in the network");
        }

        // The metabolite must be connected to the compartment
        if (!localisation.getComponents().contains(e)) {
            throw new IllegalArgumentException("Metabolite " + e.getId() + " not in the compartment");
        }

        // The network must contain the reaction
        if (!this.reactions.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not in the network");
        }

        reaction.removeSide(e, localisation, side);

    }

    /**
     * Affects an enzyme to a reaction
     */
    private void affectEnzyme(@NonNull BioReaction reaction, @NonNull BioEnzyme enzyme) {

        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        reaction.addEnzyme(enzyme);

    }

    /**
     * Affect several enzymes to a reaction
     *
     * @param reaction a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param enzymes  0 or several enzymes
     */
    public void affectEnzyme(@NonNull BioReaction reaction, @NonNull BioEnzyme... enzymes) {

        for (BioEnzyme e : enzymes) {
            affectEnzyme(reaction, e);
        }

    }

    /**
     * Affect several enzymes to a reaction
     *
     * @param reaction a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param enzymes  a {@link BioCollection of enzymes}
     */
    public void affectEnzyme(@NonNull BioReaction reaction, @NonNull BioCollection<BioEnzyme> enzymes) {

        for (BioEnzyme e : enzymes) {
            affectEnzyme(reaction, e);
        }

    }


    /**
     * Remove the link between enzyme from a reaction
     *
     * @param enzyme   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @param reaction a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @throws java.lang.IllegalArgumentException if the enzyme or the reaction are not present in the network
     */
    public void removeEnzymeFromReaction(@NonNull BioEnzyme enzyme, @NonNull BioReaction reaction) {

        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        reaction.removeEnzyme(enzyme);

    }

    /**
     * Get enzyme participant with the same quantity and the same entity
     *
     * @param quantity a {@link Double}
     * @param unit     a {@link BioPhysicalEntity}
     * @return a {@link BioEnzymeParticipant}
     */
    protected BioEnzymeParticipant getEnzymeParticipant(@NonNull BioPhysicalEntity unit, @NonNull Double quantity) {
        Optional<BioEnzymeParticipant> any = this.enzymeParticipants.stream()
                .filter(e -> e.getPhysicalEntity().equals(unit) && e.getQuantity().equals(quantity))
                .findAny();

        return any.orElse(null);
    }

    /**
     * Add a subunit to an enzymes
     *
     * @param enzyme   a {@link BioEnzyme}
     * @param quantity number of units
     * @param unit     a {@link BioPhysicalEntity}
     * @throws IllegalArgumentException of the enzyme of the unit is not present in the network
     */
    private void affectSubUnit(@NonNull BioEnzyme enzyme, @NonNull Double quantity, @NonNull BioPhysicalEntity unit) {

        if (!(unit instanceof BioMetabolite) && !(unit instanceof BioProtein)) {
            throw new IllegalArgumentException("A subunit of a BioEnzyme must be a BioProtein or a BioMetabolite");
        }
        BioEnzymeParticipant p = this.getEnzymeParticipant(unit, quantity);
        if (p == null) {
            p = new BioEnzymeParticipant(unit, quantity);
            this.enzymeParticipants.add(p);
        }

        this.affectSubUnit(enzyme, p);


    }

    /**
     * Add a subunit to an enzyme
     *
     * @param enzyme a {@link BioEnzyme}
     * @param unit   a {@link BioEnzymeParticipant}
     * @throws IllegalArgumentException of the enzyme of the unit is not present in the network
     */
    private void affectSubUnit(@NonNull BioEnzyme enzyme, @NonNull BioEnzymeParticipant unit) {

        if (!this.enzymeParticipants.contains(unit)) {
            throw new IllegalArgumentException("Enzyme participant " + unit.getId() + " not present in the network");
        }

        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(unit.getPhysicalEntity())) {
            throw new IllegalArgumentException("Physical entity " + unit.getPhysicalEntity().getId() + " not present in the network");
        }

        if (!enzyme.getParticipants().containsId(unit.getId())) {
            enzyme.addParticipant(unit);
        }

    }

    /**
     * Add a subunit to an enzyme
     *
     * @param enzyme a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @param units  several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzymeParticipant}
     * @throws java.lang.IllegalArgumentException of the enzyme of the unit is not present in the network
     */
    public void affectSubUnit(@NonNull BioEnzyme enzyme, @NonNull BioEnzymeParticipant... units) {

        for (BioEnzymeParticipant u : units) {
            this.affectSubUnit(enzyme, u);
        }

    }

    /**
     * Adds several subunits to an enzyme with the same stoichiometric coefficient
     *
     * @param enzyme   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @param quantity the number of units of each subunit to add
     * @param units    0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity}
     */
    public void affectSubUnit(@NonNull BioEnzyme enzyme, @NonNull Double quantity, @NonNull BioPhysicalEntity... units) {

        for (BioPhysicalEntity unit : units) {
            affectSubUnit(enzyme, quantity, unit);
        }
    }

    /**
     * Adds several subunits to an enzyme with the same stoichiometric coefficient
     *
     * @param enzyme   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @param quantity the number of units of each subunit to add
     * @param units    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity}
     */
    public void affectSubUnit(@NonNull BioEnzyme enzyme, @NonNull Double quantity, @NonNull BioCollection<?> units) {

        for (BioEntity unit : units) {
            affectSubUnit(enzyme, quantity, (BioPhysicalEntity) unit);

        }
    }

    /**
     * Adds several subunits to an enzyme
     *
     * @param enzyme a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @param units  a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzymeParticipant}
     */
    protected void affectSubUnit(@NonNull BioEnzyme enzyme, @NonNull BioCollection<BioEnzymeParticipant> units) {

        for (BioEnzymeParticipant unit : units) {
            affectSubUnit(enzyme, unit);
        }
    }


    /**
     * Remove a subunit from an enzyme
     *
     * @param unit   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity} to remove from the enzyme
     * @param enzyme a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @throws java.lang.IllegalArgumentException if the enzyme or the entity is not present in the network
     */
    public void removeSubUnit(@NonNull BioPhysicalEntity unit, @NonNull BioEnzyme enzyme) {

        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(unit)) {
            throw new IllegalArgumentException("Physical entity " + unit.getId() + " not present in the network");
        }

        enzyme.removeParticipant(unit);

    }

    /**
     * Add a relation protein gene
     *
     * @param protein a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein} instance
     * @param gene    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     * @throws java.lang.IllegalArgumentException if the gene or the protein is not present in the network
     */
    public void affectGeneProduct(@NonNull BioProtein protein, @NonNull BioGene gene) {

        if (!this.contains(protein)) {
            throw new IllegalArgumentException("Protein " + protein.getId() + " not present in the network");
        }

        if (!this.contains(gene)) {
            throw new IllegalArgumentException("Gene " + gene.getId() + " not present in the network");
        }

        protein.setGene(gene);

    }

    /**
     * Remove a relation between gene and product
     *
     * @param protein a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein} instance
     * @param gene    a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     * @throws java.lang.IllegalArgumentException if the gene or the protein is not present in the network
     */
    public void removeGeneProduct(@NonNull BioProtein protein, @NonNull BioGene gene) {

        if (!this.contains(protein)) {
            throw new IllegalArgumentException("Protein " + protein.getId() + " not present in the network");
        }

        if (!this.contains(gene)) {
            throw new IllegalArgumentException("Gene " + gene.getId() + " not present in the network");
        }

        protein.removeGene();

    }

    /**
     * Add a reaction to a pathway
     *
     * @param pathway  a {@link BioPathway}
     * @param reaction a {@link BioReaction}
     * @throws IllegalArgumentException if the pathway or the reaction is not present
     */
    private void affectToPathway(@NonNull BioPathway pathway, @NonNull BioReaction reaction) {

        if (!this.contains(pathway)) {
            throw new IllegalArgumentException("Pathway " + pathway.getId() + " not present in the network");
        }

        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        pathway.addReaction(reaction);

    }


    /**
     * Add several reactions to a pathway
     *
     * @param pathway   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @param reactions 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public void affectToPathway(@NonNull BioPathway pathway, @NonNull BioReaction... reactions) {

        for (BioReaction reaction : reactions) {
            this.affectToPathway(pathway, reaction);
        }
    }

    /**
     * Add several reactions to a pathway
     *
     * @param pathway   a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @param reactions a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public void affectToPathway(@NonNull BioPathway pathway, @NonNull BioCollection<BioReaction> reactions) {

        for (BioReaction reaction : reactions) {
            this.affectToPathway(pathway, reaction);
        }
    }


    /**
     * Remove a reaction from a pathway
     *
     * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param p a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @throws java.lang.IllegalArgumentException if the pathway or the reaction is not present
     */
    public void removeReactionFromPathway(@NonNull BioReaction r, @NonNull BioPathway p) {
        if (!this.contains(p)) {
            throw new IllegalArgumentException("Pathway " + p.getId() + " not present in the network");
        }

        if (!this.contains(r)) {
            throw new IllegalArgumentException("Reaction " + r.getId() + " not present in the network");
        }

        p.removeReaction(r);
    }

    /**
     * Get metabolites involved in a pathway
     *
     * @param p a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     */
    public BioCollection<BioMetabolite> getMetabolitesFromPathway(@NonNull BioPathway p) {

        if (!this.contains(p)) {
            throw new IllegalArgumentException("Pathway " + p.getId() + " not present in the network");
        }
        return p.getMetabolites();

    }

    // relations compartiment - contenu
    private void affectToCompartment(@NonNull BioCompartment compartment, @NonNull BioEntity entity) {

        if (!contains(compartment)) {
            throw new IllegalArgumentException("Compartment " + compartment.getId() + " not in the network");
        }

        if (!contains(entity)) {
            throw new IllegalArgumentException("Physical entity " + entity.getId() + " not in the network");
        }

        compartment.addComponent(entity);

    }

    /**
     * Affect several metabolites to a compartment
     *
     * @param compartment a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @param entities    one or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     */
    public void affectToCompartment(@NonNull BioCompartment compartment, @NonNull BioEntity... entities) {

        for (BioEntity ent : entities) {
            this.affectToCompartment(compartment, ent);
        }

    }

    /**
     * Affect several metabolites from a collection to a compartment
     *
     * @param compartment a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @param entities    a BioCollection of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     */
    public void affectToCompartment(@NonNull BioCompartment compartment, @NonNull BioCollection<?> entities) {
        for (BioEntity e : entities) {
            this.affectToCompartment(compartment, e);
        }
    }

    /**
     * Remove a component from a compartment
     * @param compartment
     * @param entities
     */
    public void removeFromCompartment(@NonNull BioCompartment compartment, @NonNull BioEntity... entities) {
        for(BioEntity ent : entities) {
            compartment.getComponents().remove(ent);
        }
    }

    /**
     * Return true if the entity is in the list of metabolites, reactions, genes,
     * pathways, proteins, etc...
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     * @return true if the network contains the entity
     * @throws java.lang.NullPointerException     if e is null
     * @throws java.lang.IllegalArgumentException if e is not an instance of :
     *                                            {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein}, {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}, {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}, {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme},
     *                                            {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}, {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}, or {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     */
    public Boolean contains(@NonNull BioEntity e) {

        if (e instanceof BioProtein) {
            return this.proteins.contains(e);
        } else if (e instanceof BioMetabolite) {
            return this.metabolites.contains(e);
        } else if (e instanceof BioGene) {
            return this.genes.contains(e);
        } else if (e instanceof BioEnzyme) {
            return this.enzymes.contains(e);
        } else if (e instanceof BioReaction) {
            return this.reactions.contains(e);
        } else if (e instanceof BioPathway) {
            return this.pathways.contains(e);
        } else if (e instanceof BioCompartment) {
            return this.compartments.contains(e);
        }

        throw new IllegalArgumentException("BioEntity " + e + " not handled by BioNetwork");
    }

    /**
     * returns the list of reactions that can use as substrates a list of
     * metabolites
     *
     * @param substrates a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     * @param exact      if true, the match must be exact, if false, the reactions
     *                   returned can have a superset of the specified substrates
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromSubstrates(@NonNull BioCollection<BioMetabolite> substrates, @NonNull Boolean exact) {

        return this.getReactionsFromSubstratesOrProducts(substrates, exact, true);

    }

    /**
     * returns the list of reactions that can produce a list of metabolites
     *
     * @param products a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     * @param exact    if true, the match must be exact, if false, the reactions
     *                 returned can have a superset of the specified products
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromProducts(@NonNull BioCollection<BioMetabolite> products, @NonNull Boolean exact) {

        return this.getReactionsFromSubstratesOrProducts(products, exact, false);

    }

    /**
     * Get reactions from a list of of substrates (or products)
     *
     * @return a {@link BioCollection} of {@link BioReaction}
     */
    private BioCollection<BioReaction> getReactionsFromSubstratesOrProducts(@NonNull BioCollection<BioMetabolite> metabolites,
                                                                            @NonNull Boolean exact, @NonNull Boolean areSubstrates) {

        for (BioMetabolite m : metabolites) {
            if (!this.metabolites.contains(m)) {
                throw new IllegalArgumentException("Metabolite " + m + " not present in the network");
            }
        }

        HashSet<BioReaction> reactionSet = this.reactions.stream().filter(r -> {

            if (!r.isReversible()) {

                BioCollection<BioMetabolite> refs = areSubstrates ? r.getLeftsView() : r.getRightsView();

                return exact ? refs.equals(metabolites) : refs.containsAll(metabolites);

            } else {
                return exact
                        ? r.getRightsView().equals(metabolites) || r.getLeftsView().equals(metabolites)
                        : r.getRightsView().containsAll(metabolites)
                        || r.getLeftsView().containsAll(metabolites);
            }
        }).collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(reactionSet);
    }

    /**
     * @param m           a {@link BioMetabolite}
     * @param isSubstrate a {@link Boolean} indicating if the metabolite is a substrate or a product
     * @return a {@link BioCollection} of {@link BioReaction}
     */
    private BioCollection<BioReaction> getReactionsFromSubstrateOrProduct(@NonNull BioMetabolite m, @NonNull Boolean isSubstrate) {

        BioCollection<BioReaction> reactions = new BioCollection<>();

        this.reactions.forEach(r -> {
            boolean flag;

            BioCollection<BioMetabolite> lefts = r.getLeftsView();
            BioCollection<BioMetabolite> rights = r.getRightsView();

            if (!r.isReversible()) {
                flag = isSubstrate ? lefts.contains(m) : rights.contains(m);
            } else {
                flag = lefts.contains(m) || rights.contains(m);
            }

            if (flag) {
                reactions.add(r);
            }

        });

        return reactions;

    }

    /**
     * Return the reactions involving m as substrate
     *
     * @param m a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromSubstrate(@NonNull BioMetabolite m) {
        return this.getReactionsFromSubstrateOrProduct(m, true);
    }

    /**
     * Return the reactions involving m as product
     *
     * @param m a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromProduct(@NonNull BioMetabolite m) {
        return this.getReactionsFromSubstrateOrProduct(m, false);
    }

    /**
     * Get pathways where a metabolite is involved
     *
     * @param metabolites a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     * @param all         if true : a pathway must contain all the metabolites, if false, a pathway must contain at least
     *                    one of the metabolites
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     */
    public BioCollection<BioPathway> getPathwaysFromMetabolites(@NonNull BioCollection<BioMetabolite> metabolites, @NonNull Boolean all) {

        for (BioMetabolite m : metabolites) {
            if (!this.metabolites.contains(m)) {
                throw new IllegalArgumentException("Metabolite " + m + " not present in the network");
            }
        }

        HashSet<BioPathway> pathwaySet = this.getPathwaysView().stream().
                filter(p -> all ? p.getMetabolites().containsAll(metabolites)
                        : !Collections.disjoint(metabolites, p.getMetabolites()))
                .collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(pathwaySet);

    }


    /**
     * Get reactions from genes
     *
     * @param genes a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     * @param all   if true, a reaction must be coded by all the genes, if false a reaction must be coded by
     *              at least one of the genes
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromGenes(@NonNull BioCollection<BioGene> genes, @NonNull Boolean all) {

        for (BioGene g : genes) {
            if (!this.genes.contains(g)) {
                throw new IllegalArgumentException("Gene " + g + " not present in the network");
            }
        }

        HashSet<BioReaction> reactionSet = this.reactions.stream().
                filter(o -> all ? o.getGenes().containsAll(genes) : !Collections.disjoint(o.getGenes(), genes))
                .collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(reactionSet);

    }

    /**
     * Get reactions from a gene
     *
     * @param gene a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromGene(@NonNull BioGene gene) {

        if (!this.genes.contains(gene)) {
            throw new IllegalArgumentException("Gene " + gene + " not present in the network");
        }

        HashSet<BioReaction> reactionSet = this.reactions.stream().
                filter(o -> o.getGenes().contains(gene))
                .collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(reactionSet);

    }

    /**
     * Get genes involved in a set of reactions
     *
     * @param reactions one or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     */
    public BioCollection<BioGene> getGenesFromReactions(@NonNull BioReaction... reactions) {

        BioCollection<BioGene> genes = new BioCollection<>();

        for (BioReaction r : reactions) {
            genes.addAll(this.getGenesFromReaction(r));
        }

        return genes;
    }

    /**
     * Get genes involved in a set of reactions
     *
     * @param reactions a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction} {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     */
    public BioCollection<BioGene> getGenesFromReactions(@NonNull BioCollection<BioReaction> reactions) {

        return getGenesFromReactions(reactions.toArray(new BioReaction[0]));
    }

    /**
     * @param reaction a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioGene}
     * @throws IllegalArgumentException if reaction is not present in the network
     */
    private BioCollection<BioGene> getGenesFromReaction(@NonNull BioReaction reaction) {
        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        return reaction.getGenes();
    }

    /**
     * Get genes from pathways
     *
     * @param pathways a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     */
    public BioCollection<BioGene> getGenesFromPathways(@NonNull BioCollection<BioPathway> pathways) {
        BioCollection<BioGene> genes = new BioCollection<>();
        for (BioPathway p : pathways) {
            if (!this.pathways.contains(p)) {
                throw new IllegalArgumentException("Pathway " + p + " not present in the network");
            }
            p.getReactions().forEach(r -> genes.addAll(r.getGenes()));
        }

        return genes;

    }

    /**
     * Get genes from pathways
     *
     * @param pathways 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     */
    public BioCollection<BioGene> getGenesFromPathways(@NonNull BioPathway... pathways) {
        BioCollection<BioGene> genes = new BioCollection<>();
        for (BioPathway p : pathways) {
            if (!this.pathways.contains(p)) {
                throw new IllegalArgumentException("Pathway " + p + " not present in the network");
            }
            p.getReactions().forEach(r -> genes.addAll(r.getGenes()));
        }

        return genes;

    }

    /**
     * Return genes involved in enzymes
     *
     * @param enzymes a {@link BioCollection of enzymes} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     */
    public BioCollection<BioGene> getGenesFromEnzymes(@NonNull BioCollection<BioEnzyme> enzymes) {

        BioCollection<BioGene> genes = new BioCollection<>();

        for (BioEnzyme e : enzymes) {
            if (e != null) {
                genes.addAll(this.getGenesFromEnzyme(e));
            }
        }

        return genes;

    }

    /**
     * Return the collection of genes coding for an enzyme
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     */
    public BioCollection<BioGene> getGenesFromEnzyme(@NonNull BioEnzyme e) {

        if (!this.getEnzymesView().contains(e)) {
            throw new IllegalArgumentException("Enzyme " + e.getId() + " not present in the network");
        }

        BioCollection<BioGene> genes = new BioCollection<>();

        BioCollection<BioEnzymeParticipant> participants = e.getParticipantsView();

        for (BioEnzymeParticipant ep : participants) {

            BioPhysicalEntity p = ep.getPhysicalEntity();

            if (p.getClass().equals(BioProtein.class)) {
                BioProtein prot = (BioProtein) p;

                BioGene g = prot.getGene();

                if (g != null) {
                    genes.add(g);
                }
            }
        }

        return genes;

    }

    /**
     * Get pathways from gene ids
     *
     * @param genes a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene}
     * @param all   if true, the pathway must contain all the genes
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     */
    public BioCollection<BioPathway> getPathwaysFromGenes(@NonNull BioCollection<BioGene> genes, Boolean all) {
        for (BioGene g : genes) {
            if (!this.genes.contains(g)) {
                throw new IllegalArgumentException("Gene " + g + " not present in the network");
            }
        }

        HashSet<BioPathway> pathwaySet = this.getPathwaysView().stream().
                filter(p -> all ? p.getGenes().containsAll(genes) : !Collections.disjoint(p.getGenes(), genes))
                .collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(pathwaySet);

    }

    /**
     * get pathways from reactions
     *
     * @param reactions a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @param all       if true, the pathway must contain all the reactions
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @throws java.lang.IllegalArgumentException if a reaction is missing in the network
     */
    public BioCollection<BioPathway> getPathwaysFromReactions(@NonNull BioCollection<BioReaction> reactions, @NonNull Boolean all) {
        for (BioReaction r : reactions) {
            if (!this.reactions.contains(r)) {
                throw new IllegalArgumentException("Reaction " + r + " not present in the network");
            }
        }

        HashSet<BioPathway> pathwaySet = this.getPathwaysView().stream().
                filter(p -> all
                        ? p.getReactions().containsAll(reactions)
                        : !Collections.disjoint(p.getReactions(), reactions))
                .collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(pathwaySet);

    }

    /**
     * get pathways from reaction
     *
     * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * <p>
     * throws {@link java.lang.IllegalArgumentException} if r is not in the network
     */
    public BioCollection<BioPathway> getPathwaysFromReaction(@NonNull BioReaction r) {
        if (!this.reactions.contains(r)) {
            throw new IllegalArgumentException("Reaction " + r + " not present in the network");
        }

        HashSet<BioPathway> pathwaySet = this.getPathwaysView().stream().filter(p -> {

            Set<String> reactionRefIds = p.getReactions().getIds();

            return reactionRefIds.contains(r.getId());

        }).collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(pathwaySet);

    }

    /**
     * get reactions from pathways
     *
     * @param pathways a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @throws java.lang.IllegalArgumentException if one of the pathways is not in the network
     */
    public BioCollection<BioReaction> getReactionsFromPathways(@NonNull BioCollection<BioPathway> pathways) {

        BioCollection<BioReaction> reactions = new BioCollection<>();
        for (BioPathway p : pathways) {
            reactions.addAll(this.getReactionsFromPathway(p));
        }

        return reactions;

    }

    /**
     * get reactions from pathways
     *
     * @param pathways one or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @throws java.lang.IllegalArgumentException if one of the pathways is not in the network
     */
    public BioCollection<BioReaction> getReactionsFromPathways(@NonNull BioPathway... pathways) {

        BioCollection<BioReaction> reactions = new BioCollection<>();
        for (BioPathway p : pathways) {
            reactions.addAll(this.getReactionsFromPathway(p));
        }

        return reactions;

    }


    /**
     * Get reactions involved in a pathway
     *
     * @param p a {@link BioPathway}
     * @return a {@link BioCollection} of {@link BioReaction}
     * @throws IllegalArgumentException if the pathway is not in the network
     */
    private BioCollection<BioReaction> getReactionsFromPathway(@NonNull BioPathway p) {

        if (!this.pathways.contains(p)) {
            throw new IllegalArgumentException("Pathway " + p + " not present in the network");
        }
        return p.getReactions().getView();
    }


    /**
     * Get left reactants of a reaction
     *
     * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
     */
    public BioCollection<BioReactant> getLeftReactants(@NonNull BioReaction r) {

        return r.getLeftReactants().getView();

    }

    /**
     * Get right reactants of a reaction
     *
     * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant}
     */
    public BioCollection<BioReactant> getRightReactants(@NonNull BioReaction r) {

        return r.getRightReactants().getView();

    }

    /**
     * Get left or right reactants of a reaction
     *
     * @param r a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioReactant}
     */
    private BioCollection<BioMetabolite> getLeftsOrRights(@NonNull BioReaction r, @NonNull Boolean left) {

        if (!this.contains(r)) {
            throw new IllegalArgumentException("Reaction " + r.getId() + " not present in the network");
        }

        BioCollection<BioReactant> reactants = left ? r.getLeftReactants() : r.getRightReactants();

        BioCollection<BioMetabolite> metabolites = new BioCollection<>();

        reactants.forEach(m -> metabolites.add(m.getMetabolite()));

        return metabolites;

    }

    /**
     * Get left metabolites of a reaction
     *
     * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     */
    public BioCollection<BioMetabolite> getLefts(@NonNull BioReaction r) {
        return this.getLeftsOrRights(r, true);
    }

    /**
     * Get right metabolites of a reaction
     *
     * @param r a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     */
    public BioCollection<BioMetabolite> getRights(@NonNull BioReaction r) {
        return this.getLeftsOrRights(r, false);
    }

    /**
     * Return all the metabolites involved in a set of reactions
     *
     * @param reactions a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     */
    public BioCollection<BioMetabolite> getMetabolitesFromReactions(@NonNull BioCollection<@NonNull BioReaction> reactions) {

        BioCollection<BioMetabolite> allMetabolites = new BioCollection<>();

        for (BioReaction reaction : reactions) {
            if (!this.contains(reaction)) {
                throw new IllegalArgumentException("Reaction " + reaction + " not present in the network");
            }
            if (this.contains(reaction)) {
                allMetabolites.addAll(this.getLefts(reaction));
                allMetabolites.addAll(this.getRights(reaction));
            }
        }
        return allMetabolites;

    }

    /**
     * Get all the reactions where the metabolite m is involved
     *
     * @param m a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromMetabolite(@NonNull BioMetabolite m) {

        BioCollection<BioReaction> reactions = new BioCollection<>();

        reactions.addAll(this.getReactionsFromSubstrate(m));
        reactions.addAll(this.getReactionsFromProduct(m));

        return reactions;
    }

    /**
     * Get compartments where is involved an entity
     *
     * @param e a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity}
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} of {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment}
     * @throws java.lang.IllegalArgumentException if the entity is not present in the network
     */
    public BioCollection<BioCompartment> getCompartmentsOf(@NonNull BioEntity e) {

        BioCollection<BioCompartment> cpts = new BioCollection<>();

        if (!this.contains(e)) {
            throw new IllegalArgumentException("Entity " + e + " not present in the network");
        }

        for (BioCompartment c : this.compartments) {
            if (c.getComponents().contains(e)) {
                cpts.add(c);
            }
        }

        return cpts;

    }

    /**
     * <p>getPathwaysView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} copy of the pathways
     */
    public BioCollection<BioPathway> getPathwaysView() {
        return pathways.getView();
    }

    /**
     * <p>getMetabolitesView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} copy of the metabolites
     */
    public BioCollection<BioMetabolite> getMetabolitesView() {
        return metabolites.getView();
    }

    /**
     * <p>getProteinsView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} copy of the proteins
     */
    public BioCollection<BioProtein> getProteinsView() {
        return proteins.getView();
    }

    /**
     * <p>getGenesView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} copy of the genes
     */
    public BioCollection<BioGene> getGenesView() {
        return genes.getView();
    }

    /**
     * <p>getReactionsView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} copy of the reactions
     */
    public BioCollection<BioReaction> getReactionsView() {
        return reactions.getView();
    }

    /**
     * <p>getCompartmentsView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} copy of the compartments
     */
    public BioCollection<BioCompartment> getCompartmentsView() {
        return compartments.getView();
    }

    /**
     * <p>getEnzymesView.</p>
     *
     * @return an unmodifiable {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} copy of the enzymes
     */
    public BioCollection<BioEnzyme> getEnzymesView() {
        return enzymes.getView();
    }

    /**
     * @param e a {@link BioEntity}
     * @return true if the network contains an entity with the same id
     */
    public Boolean containsEntityWithSameId(@NonNull BioEntity e) {

        String id = e.getId();

        Boolean flag;

        if (e instanceof BioPathway) {
            flag = this.pathways.containsId(id);
        } else if (e instanceof BioMetabolite) {
            flag = this.metabolites.containsId(id);
        } else if (e instanceof BioProtein) {
            flag = this.proteins.containsId(id);
        } else if (e instanceof BioGene) {
            flag = this.genes.containsId(id);
        } else if (e instanceof BioReaction) {
            flag = this.reactions.containsId(id);
        } else if (e instanceof BioCompartment) {
            flag = this.compartments.containsId(id);
        } else if (e instanceof BioEnzyme) {
            flag = this.enzymes.containsId(id);
        } else {

            throw new IllegalArgumentException(
                    "BioEntity \"" + e.getClass().getSimpleName() + "\" not supported by BioNetwork");
        }
        return flag;

    }

    /**
     * Adds enzymes participants in the network
     *
     * @param enzymeParticipants a list of {@link BioEnzymeParticipant}
     */
    protected void addEnzymeParticipants(@NonNull BioEnzymeParticipant... enzymeParticipants) {
        for (BioEnzymeParticipant ep : enzymeParticipants) {
            this.addEnzymeParticipant(ep);
        }

    }

    /**
     * @param ep a {@link BioEnzymeParticipant}
     */
    private void addEnzymeParticipant(@NonNull BioEnzymeParticipant ep) {
        this.enzymeParticipants.add(ep);
    }
}
