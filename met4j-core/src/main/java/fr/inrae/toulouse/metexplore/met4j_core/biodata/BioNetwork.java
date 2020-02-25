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

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */

public class BioNetwork extends BioEntity {

    private BioCollection<BioPathway> pathways = new BioCollection<>();

    private BioCollection<BioMetabolite> metabolites = new BioCollection<>();

    private BioCollection<BioProtein> proteins = new BioCollection<>();

    private BioCollection<BioGene> genes = new BioCollection<>();

    private BioCollection<BioReaction> reactions = new BioCollection<>();

    private BioCollection<BioCompartment> compartments = new BioCollection<>();

    private BioCollection<BioEnzyme> enzymes = new BioCollection<>();

    public BioNetwork(String id) {
        super(id);
    }

    public BioNetwork() {
        super("NA");
    }

    /**
     * Add several entities
     *
     * @param bioEntities
     */
    public void add(BioEntity... bioEntities) {

        for (BioEntity e : bioEntities) {
            this.add(e);
        }
    }

    public void add(BioCollection<?> bioEntities) {

        for (BioEntity e : bioEntities) {
            this.add(e);
        }
    }

    /**
     * Add one entity
     *
     * @param e
     */
    public void add(BioEntity e) {
        if (e instanceof BioPathway) {
            this.pathways.add((BioPathway) e);
        } else if (e instanceof BioMetabolite) {
            this.metabolites.add((BioMetabolite) e);
        } else if (e instanceof BioProtein) {
            this.proteins.add((BioProtein) e);
        } else if (e instanceof BioGene) {
            this.genes.add((BioGene) e);
        } else if (e instanceof BioReaction) {
            this.reactions.add((BioReaction) e);
        } else if (e instanceof BioCompartment) {
            this.compartments.add((BioCompartment) e);
        } else if (e instanceof BioEnzyme) {
            this.enzymes.add((BioEnzyme) e);
        } else {
            throw new IllegalArgumentException(
                    "BioEntity \"" + e.getClass().getSimpleName() + "\" not supported by BioNetwork");
        }
    }

    /**
     * Remove on cascade a BioEntity
     *
     * @param e
     */
    private void removeOnCascade(BioEntity e) {

        if(e == null)
        {
            throw new NullPointerException();
        }
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
            this.enzymes.remove(e);
        } else {

            throw new IllegalArgumentException(
                    "BioEntity \"" + e.getClass().getSimpleName() + "\" not supported by BioNetwork");
        }

        e = null;

    }

    /**
     * Remove on cascade several entities
     *
     * @param entities
     */
    public void removeOnCascade(BioEntity... entities) {
        for (BioEntity e : entities) {
            removeOnCascade(e);
        }
    }

    /**
     * Remove on cascade several entities stored in a BioCollection
     *
     * @param entities
     */
    public void removeOnCascade(BioCollection<?> entities) {
        for (BioEntity e : entities) {
            removeOnCascade(e);
        }
    }


    /**
     * Remove protein from the network and from the enzymes and the compartments
     * where it is involved
     */
    private void removeProtein(BioProtein protein) {

        this.proteins.remove(protein);

        this.getEnzymesView().forEach(e -> {
            BioCollection<BioEnzymeParticipant> participants = new BioCollection<>(
                    e.getParticipants());

            Boolean remove = false;
            for (BioEnzymeParticipant p : participants) {
                if (p.getPhysicalEntity().equals(protein)) {
                    remove = true;
                    break;
                }
            }

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
     */
    private void removeMetabolite(BioMetabolite m) {

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

            for (BioEnzymeParticipant p : participants) {
                if (p.getPhysicalEntity().equals(m)) {
                    e.getParticipants().remove(p);
                }
            }

            if (e.getParticipantsView().size() == 0) {
                this.removeOnCascade(e);
            }
        });

        this.metabolites.remove(m);

    }

    /**
     * Remove a gene from the network and remove the link between the gene and
     * proteins
     */
    private void removeGene(BioGene g) {

        this.genes.remove(g);

        BioCollection<BioProtein> proteinsCopy = new BioCollection<>(proteins);
        proteinsCopy.forEach(p -> {
            if (p.getGene().equals(g)) {
                this.removeOnCascade(p);
            }
        });

    }

    /**
     * Remove a reaction from the network and from the pathways where it is involved
     */
    private void removeReaction(BioReaction r) {

        this.reactions.remove(r);

        this.getPathwaysView().forEach(p -> {
            p.removeReaction(r);
            if (p.getReactions().size() == 0) {
                this.removeOnCascade(p);
            }

        });

    }

    /**
     * Remove a compartment and all the reactions that involve reactants in this
     * compartment
     */
    private void removeCompartment(BioCompartment c) {

        this.reactions.forEach(r -> {

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
    private void affectLeft(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite substrate) {

        affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.LEFT, substrate);

    }

    public void affectLeft(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite... substrates) {

        for (BioMetabolite s : substrates)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.LEFT, s);

    }

    public void affectLeft(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioCollection<BioMetabolite> substrates) {

        for (BioMetabolite s : substrates)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.LEFT, s);

    }

    /**
     * add a relation reactant-reaction
     */
    private void affectLeft(BioReaction reaction, BioReactant reactant) {

        affectSideReaction(reactant, reaction, BioReaction.Side.LEFT);

    }

    public void affectLeft(BioReaction reaction, BioReactant... reactants) {
        for (BioReactant reactant : reactants) {
            this.affectLeft(reaction, reactant);
        }
    }

    public void affectLeft(BioReaction reaction, BioCollection<BioReactant> reactants) {
        this.affectLeft(reaction, reactants.toArray(new BioReactant[0]));
    }

    /**
     * Remove a left reactant
     */
    public void removeLeft(BioPhysicalEntity e, BioCompartment localisation, BioReaction reaction) {

        removeSideReaction(e, localisation, reaction, BioReaction.Side.LEFT);
    }

    /**
     * Add a relation product-reaction
     */
    public void affectRight(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite product) {

        affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    public void affectRight(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite... products) {
        for (BioMetabolite product : products)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    public void affectRight(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioCollection<BioMetabolite> products) {
        for (BioMetabolite product : products)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    /**
     * Add a relation product-reaction
     */
    private void affectRight(BioReaction reaction, BioReactant reactant) {
        affectSideReaction(reactant, reaction, BioReaction.Side.RIGHT);
    }

    public void affectRight(BioReaction reaction, BioReactant... reactants) {
        for (BioReactant reactant : reactants) {
            this.affectRight(reaction, reactant);
        }
    }

    public void affectRight(BioReaction reaction, BioCollection<BioReactant> reactants) {
        this.affectRight(reaction, reactants.toArray(new BioReactant[0]));
    }

    /**
     * Remove a right reactant
     */
    public void removeRight(BioPhysicalEntity e, BioCompartment localisation, BioReaction reaction) {

        removeSideReaction(e, localisation, reaction, BioReaction.Side.RIGHT);
    }

    private void affectSideReaction(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioReaction.Side side, BioMetabolite e) {
        BioReactant reactant = new BioReactant(e, stoichiometry, localisation);

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

        if (side.equals(BioReaction.Side.LEFT)) {
            reaction.getLeftReactants().add(reactant);
        } else {
            reaction.getRightReactants().add(reactant);
        }
    }

    private void affectSideReaction(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioReaction.Side side, BioMetabolite... metabolites) {
        for (BioMetabolite m : metabolites) {
            this.affectSideReaction(reaction, stoichiometry, localisation, side, m);
        }
    }

    private void affectSideReaction(BioReactant reactant, BioReaction reaction, BioReaction.Side side) {

        BioCompartment localisation = reactant.getLocation();

        // The network must contain the compartment
        if (!this.compartments.contains(localisation)) {
            throw new IllegalArgumentException("Compartment " + localisation.getId() + " not in the network");
        }

        if (!this.metabolites.contains(reactant.getPhysicalEntity())) {
            throw new IllegalArgumentException(
                    "Metabolite " + reactant.getPhysicalEntity().getId() + " not in the network");
        }

        // The metabolite must be connected to the compartment
        if (!localisation.getComponents().contains(reactant.getPhysicalEntity())) {
            throw new IllegalArgumentException(
                    "Metabolite " + reactant.getPhysicalEntity().getId() + " not in the compartment");
        }

        // The network must contain the reaction
        if (!this.reactions.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not in the network");
        }

        if (side.equals(BioReaction.Side.LEFT)) {
            reaction.getLeftReactants().add(reactant);
        } else {
            reaction.getRightReactants().add(reactant);
        }
    }

    /**
     * Remove an entity from a side of reaction
     */
    private void removeSideReaction(BioPhysicalEntity e, BioCompartment localisation, BioReaction reaction, BioReaction.Side side) {

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
    private void affectEnzyme(BioReaction reaction, BioEnzyme enzyme) {

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
     * @param reaction
     * @param enzymes
     */
    public void affectEnzyme(BioReaction reaction, BioEnzyme... enzymes) {

        for (BioEnzyme e : enzymes) {
            affectEnzyme(reaction, e);
        }

    }

    /**
     * Affect several enzymes stored in a BioCollection to a reaction
     *
     * @param reaction
     * @param enzymes
     */
    public void affectEnzyme(BioReaction reaction, BioCollection<BioEnzyme> enzymes) {

        for (BioEnzyme e : enzymes) {
            affectEnzyme(reaction, e);
        }

    }


    /**
     * Remove the link between enzyme from a reaction
     */
    public void removeEnzymeFromReaction(BioEnzyme enzyme, BioReaction reaction) {

        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        reaction.removeEnzyme(enzyme);

    }

    ;

    // relation enzyme -constituant
    private void affectSubUnit(BioEnzyme enzyme, Double quantity, BioPhysicalEntity unit) {

        BioEnzymeParticipant p = new BioEnzymeParticipant(unit, quantity);

        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(unit)) {
            throw new IllegalArgumentException("Physical entity " + unit.getId() + " not present in the network");
        }

        enzyme.addParticipant(p);

    }

    // relation enzyme -constituant
    private void affectSubUnit(BioEnzyme enzyme, BioEnzymeParticipant unit) {


        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(unit.getPhysicalEntity())) {
            throw new IllegalArgumentException("Physical entity " + unit.getPhysicalEntity().getId() + " not present in the network");
        }

        enzyme.addParticipant(unit);

    }

    /**
     * Adds several subunits to an enzyme with the same stoichiometric coefficient
     *
     * @param enzyme
     * @param quantity
     * @param units
     */
    public void affectSubUnit(BioEnzyme enzyme, Double quantity, BioPhysicalEntity... units) {

        for (BioPhysicalEntity unit : units) {
            affectSubUnit(enzyme, quantity, unit);
        }
    }

    /**
     * Adds several subunits stored in a BioCollection to an enzyme with the same stoichiometric coefficient
     *
     * @param enzyme
     * @param quantity
     * @param units
     */
    public void affectSubUnit(BioEnzyme enzyme, Double quantity, BioCollection<?> units) {

        for (BioEntity unit : units) {
            if (BioPhysicalEntity.class.isInstance(unit)) {
                affectSubUnit(enzyme, quantity, (BioPhysicalEntity) unit);
            } else {
                throw new IllegalArgumentException("Units of an enzyme must be BioPhysicalEntity instances");
            }
        }
    }

    /**
     * Adds several subunits stored in a BioCollection to an enzyme with the same stoichiometric coefficient
     *
     * @param enzyme
     * @param units
     */
    public void affectSubUnit(BioEnzyme enzyme, BioCollection<BioEnzymeParticipant> units) {

        for (BioEnzymeParticipant unit : units) {
            affectSubUnit(enzyme, unit);
        }
    }


    public void removeSubUnit(BioPhysicalEntity unit, BioEnzyme enzyme) {

        if (!this.contains(enzyme)) {
            throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
        }

        if (!this.contains(unit)) {
            throw new IllegalArgumentException("Physical entity " + unit.getId() + " not present in the network");
        }

        enzyme.removeParticipant(unit);

    }

    ;

    /**
     * Add a relation protein gene
     */
    public void affectGeneProduct(BioProtein protein, BioGene gene) {

        if (!this.contains(protein)) {
            throw new IllegalArgumentException("Protein " + protein.getId() + " not present in the network");
        }

        if (!this.contains(gene)) {
            throw new IllegalArgumentException("Gene " + gene.getId() + " not present in the network");
        }

        protein.setGene(gene);

    }

    ;

    /**
     * Remove a relation between gene and product
     */
    public void removeGeneProduct(BioProtein protein, BioGene gene) {

        if (!this.contains(protein)) {
            throw new IllegalArgumentException("Protein " + protein.getId() + " not present in the network");
        }

        if (!this.contains(gene)) {
            throw new IllegalArgumentException("Gene " + gene.getId() + " not present in the network");
        }

        protein.removeGene();

    }

    ;

    /**
     * Add a pathway affected to a reaction
     */
    private void affectToPathway(BioPathway pathway, BioReaction reaction) {

        if (!this.contains(pathway)) {
            throw new IllegalArgumentException("Pathway " + pathway.getId() + " not present in the network");
        }

        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        pathway.addReaction(reaction);

    }


    public void affectToPathway(BioPathway pathway, BioReaction... reactions) {

        for (BioReaction reaction : reactions) {
            this.affectToPathway(pathway, reaction);
        }
    }

    public void affectToPathway(BioPathway pathway, BioCollection<BioReaction> reactions) {

        for (BioReaction reaction : reactions) {
            this.affectToPathway(pathway, reaction);
        }
    }


    /**
     * Remove a reaction from a pathway
     */
    public void removeReactionFromPathway(BioReaction r, BioPathway p) {
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
     */
    public BioCollection<BioMetabolite> getMetabolitesFromPathway(BioPathway p) {

        if (!this.contains(p)) {
            throw new IllegalArgumentException("Pathway " + p.getId() + " not present in the network");
        }
        return p.getMetabolites();

    }

    // relations compartiment - contenu
    private void affectToCompartment(BioCompartment compartment, BioEntity entity) {

        if (!contains(compartment)) {
            throw new IllegalArgumentException("Compartment " + compartment.getId() + " not in the network");
        }

        if (!contains(entity)) {
            throw new IllegalArgumentException("Physical entity " + entity.getId() + " not in the network");
        }

        compartment.addComponent(entity);

    }

    ;

    /**
     * Affect several metabolites to a compartment
     *
     * @param compartment
     * @param entities
     */
    public void affectToCompartment(BioCompartment compartment, BioEntity... entities) {

        for (BioEntity ent : entities) {
            this.affectToCompartment(compartment, ent);
        }

    }

    /**
     * Affect several metabolites from a collection to a compartment
     *
     * @param compartment
     * @param entities
     */
    public void affectToCompartment(BioCompartment compartment, BioCollection<?> entities) {
        for (BioEntity e : entities) {
            this.affectToCompartment(compartment, e);
        }
    }

    ;

    /**
     * Return true if the entity is in the list of metabolites, reactions, genes,
     * pathways, proteins, etc...
     */
    public Boolean contains(BioEntity e) {

        if (e == null) {
            throw new NullPointerException();
        }

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
     * @param exact if true, the match must be exact, if false, the reactions
     *              returned can have a superset of the specified substrates
     */
    public BioCollection<BioReaction> getReactionsFromSubstrates(BioCollection<BioMetabolite> substrates, Boolean exact) {

        return this.getReactionsFromSubstratesOrProducts(substrates, exact, true);

    }

    /**
     * returns the list of reactions that can produce a list of metabolites
     *
     * @param exact if true, the match must be exact, if false, the reactions
     *              returned can have a superset of the specified products
     */
    public BioCollection<BioReaction> getReactionsFromProducts(BioCollection<BioMetabolite> substrates, Boolean exact) {

        return this.getReactionsFromSubstratesOrProducts(substrates, exact, false);

    }

    /**
     * Get reactions from a list of ids of substrates (or products)
     */
    private BioCollection<BioReaction> getReactionsFromSubstratesOrProducts(BioCollection<BioMetabolite> metabolites,
                                                                            Boolean exact, Boolean areSubstrates) {

        for (BioMetabolite m : metabolites) {
            if (!this.metabolites.contains(m)) {
                throw new IllegalArgumentException("Metabolite " + m + " not present in the network");
            }
        }

        HashSet<BioReaction> reactionSet = new HashSet<>(this.getReactionsView().stream().filter(o -> {
            BioReaction r = o;

            if (!r.isReversible()) {

                BioCollection<BioMetabolite> refs = areSubstrates ? r.getLeftsView() : r.getRightsView();

                return exact ? refs.equals(metabolites) : refs.containsAll(metabolites);

            } else {
                return exact
                        ? r.getRightsView().equals(metabolites) || r.getLeftsView().equals(metabolites)
                        : r.getRightsView().containsAll(metabolites)
                        || r.getLeftsView().containsAll(metabolites);
            }
        }).collect(Collectors.toSet()));

        return new BioCollection<>(reactionSet);
    }

    /**
     * @param m
     * @param isSubstrate
     * @return
     */
    private BioCollection<BioReaction> getReactionsFromSubstrateOrProduct(BioMetabolite m, Boolean isSubstrate) {

        if (m == null || !this.contains(m)) {
            throw new IllegalArgumentException("Metabolite not present in the network");
        }

        BioCollection<BioReaction> reactions = new BioCollection<>();

        this.getReactionsView().forEach(r -> {
            Boolean flag = false;

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
     * @param m
     * @return
     */
    public BioCollection<BioReaction> getReactionsFromSubstrate(BioMetabolite m) {
        return this.getReactionsFromSubstrateOrProduct(m, true);
    }

    /**
     * Return the reactions involving m as product
     *
     * @param m
     * @return
     */
    public BioCollection<BioReaction> getReactionsFromProduct(BioMetabolite m) {
        return this.getReactionsFromSubstrateOrProduct(m, false);
    }

    /**
     * Get pathways where a metabolite is involved
     */
    public BioCollection<BioPathway> getPathwaysFromMetabolites(BioCollection<BioMetabolite> metabolites, Boolean all) {

        for (BioMetabolite m : metabolites) {
            if (!this.metabolites.contains(m)) {
                throw new IllegalArgumentException("Metabolite " + m + " not present in the network");
            }
        }

        HashSet<BioPathway> pathwaySet = new HashSet<>(this.getPathwaysView().stream().
                filter(p -> all ? p.getMetabolites().containsAll(metabolites)
                        : !Collections.disjoint(metabolites, p.getMetabolites())).
                collect(Collectors.toSet()));

        return new BioCollection<>(pathwaySet);

    }


    public BioCollection<BioReaction> getReactionsFromGenes(BioCollection<BioGene> genes, Boolean all) {

        for (BioGene g : genes) {
            if (!this.genes.contains(g)) {
                throw new IllegalArgumentException("Gene " + g + " not present in the network");
            }
        }

        HashSet<BioReaction> reactionSet = new HashSet<>(this.getReactionsView().stream().
                filter(o -> all ? o.getGenes().containsAll(genes) : !Collections.disjoint(o.getGenes(), genes)).
                collect(Collectors.toSet()));

        return new BioCollection<>(reactionSet);

    }

    public BioCollection<BioReaction> getReactionsFromGene(BioGene gene) {

        if (!this.genes.contains(gene)) {
            throw new IllegalArgumentException("Gene " + gene + " not present in the network");
        }

        HashSet<BioReaction> reactionSet = new HashSet<>(this.getReactionsView().stream().
                filter(o -> o.getGenes().contains(gene)).
                collect(Collectors.toSet()));

        return new BioCollection<>(reactionSet);

    }

    /**
     * Get genes involved in a set of reactions
     */
    public BioCollection<BioGene> getGenesFromReactions(BioReaction... reactions) {

        BioCollection<BioGene> genes = new BioCollection<>();

        for (BioReaction r : reactions) {
            genes.addAll(this.getGenesFromReaction(r));
        }

        return genes;
    }

    /**
     * Get genes involved in a set of reactions
     */
    public BioCollection<BioGene> getGenesFromReactions(BioCollection<BioReaction> reactions) {

        return getGenesFromReactions(reactions.toArray(new BioReaction[0]));
    }

    /**
     * @param reaction
     * @return
     */
    private BioCollection<BioGene> getGenesFromReaction(BioReaction reaction) {
        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        return reaction.getGenes();
    }

    /**
     * Get genes from pathways
     */
    public BioCollection<BioGene> getGenesFromPathways(BioCollection<BioPathway> pathways) {
        BioCollection<BioGene> genes = new BioCollection<>();
        for (BioPathway p : pathways) {
            if (!this.pathways.contains(p)) {
                throw new IllegalArgumentException("Pathway " + p + " not present in the network");
            }
            p.getReactions().forEach(r -> {
                genes.addAll(r.getGenes());
            });
        }

        return genes;

    }

    public BioCollection<BioGene> getGenesFromPathways(BioPathway... pathways) {
        BioCollection<BioGene> genes = new BioCollection<>();
        for (BioPathway p : pathways) {
            if (!this.pathways.contains(p)) {
                throw new IllegalArgumentException("Pathway " + p + " not present in the network");
            }
            p.getReactions().forEach(r -> {
                genes.addAll(r.getGenes());
            });
        }

        return genes;

    }

    /**
     * Return genes involved in enzymes
     *
     * @param enzymes
     * @return
     */
    public BioCollection<BioGene> getGenesFromEnzymes(BioCollection<BioEnzyme> enzymes) {

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
     * @param e
     * @return
     */
    public BioCollection<BioGene> getGenesFromEnzyme(BioEnzyme e) {

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
     * @param all if true, the pathway must contain all the genes
     */
    public BioCollection<BioPathway> getPathwaysFromGenes(BioCollection<BioGene> genes, Boolean all) {
        for (BioGene g : genes) {
            if (!this.genes.contains(g)) {
                throw new IllegalArgumentException("Gene " + g + " not present in the network");
            }
        }

        HashSet<BioPathway> pathwaySet = new HashSet<>(this.getPathwaysView().stream().
                filter(p -> all ? p.getGenes().containsAll(genes) : !Collections.disjoint(p.getGenes(), genes)).
                collect(Collectors.toSet()));

        return new BioCollection<>(pathwaySet);

    }

    /**
     * get pathways from reactions
     */
    public BioCollection<BioPathway> getPathwaysFromReactions(BioCollection<BioReaction> reactions, Boolean all) {
        for (BioReaction r : reactions) {
            if (!this.reactions.contains(r)) {
                throw new IllegalArgumentException("Reaction " + r + " not present in the network");
            }
        }

        HashSet<BioPathway> pathwaySet = new HashSet<>(this.getPathwaysView().stream().
                filter(p -> all ? p.getReactions().containsAll(reactions) : !Collections.disjoint(p.getReactions(), reactions)).
                collect(Collectors.toSet()));

        return new BioCollection<>(pathwaySet);

    }

    /**
     * get pathways from reaction
     */
    public BioCollection<BioPathway> getPathwaysFromReaction(BioReaction r) {
        if (!this.reactions.contains(r)) {
            throw new IllegalArgumentException("Reaction " + r + " not present in the network");
        }

        HashSet<BioPathway> pathwaySet = new HashSet<>(this.getPathwaysView().stream().filter(p -> {

            Set<String> reactionRefIds = p.getReactions().getIds();

            return reactionRefIds.contains(r.getId());

        }).collect(Collectors.toSet()));

        return new BioCollection<>(pathwaySet);

    }

    public BioCollection<BioReaction> getReactionsFromPathways(BioCollection<BioPathway> pathways) {

        BioCollection<BioReaction> reactions = new BioCollection<>();
        for (BioPathway p : pathways) {
            if (!this.pathways.contains(p)) {
                throw new IllegalArgumentException("Reaction " + p + " not present in the network");
            }
            reactions.addAll(p.getReactions());
        }

        return reactions;

    }


    /**
     * Get reactions involved in a pathway
     *
     * @param p
     * @return
     */
    public BioCollection<BioReaction> getReactionsFromPathway(BioPathway p) {

        return p.getReactions().getView();
    }

    /**
     * @param cpd
     * @return the list of reactionNodes which involves the compound cpd as
     *         substrate
     */

    /**
     * @return the pathways
     */
    public BioCollection<BioPathway> getPathwaysView() {
        return pathways.getView();
    }

    /**
     * @return the metabolites
     */
    public BioCollection<BioMetabolite> getMetabolitesView() {
        return metabolites.getView();
    }

    /**
     * @return the proteins
     */
    public BioCollection<BioProtein> getProteinsView() {
        return proteins.getView();
    }

    /**
     * @return the genes
     */
    public BioCollection<BioGene> getGenesView() {
        return genes.getView();
    }

    /**
     * @return the reactions
     */
    public BioCollection<BioReaction> getReactionsView() {
        return reactions.getView();
    }

    /**
     * @return the compartments
     */
    public BioCollection<BioCompartment> getCompartmentsView() {
        return compartments.getView();
    }

    /**
     * @return the enzymes
     */
    public BioCollection<BioEnzyme> getEnzymesView() {
        return enzymes.getView();
    }

    public BioCollection<BioReactant> getLeftReactants(BioReaction r) {

        return r.getLeftReactants().getView();

    }

    public BioCollection<BioReactant> getRightReactants(BioReaction r) {

        return r.getRightReactants().getView();

    }

    /**
     * @param r
     * @param left
     * @return
     */
    private BioCollection<BioMetabolite> getLeftsOrRights(BioReaction r, Boolean left) {

        if (!this.contains(r)) {
            throw new IllegalArgumentException("Reaction " + r.getId() + " not present in the network");
        }

        BioCollection<BioReactant> reactants = left ? r.getLeftReactants() : r.getRightReactants();

        BioCollection<BioMetabolite> metabolites = new BioCollection<>();

        reactants.forEach(m -> {

            metabolites.add(m.getMetabolite());

        });

        return metabolites;

    }

    /**
     * Return the left metabolites of a reaction
     *
     * @param r
     * @return
     */
    public BioCollection<BioMetabolite> getLefts(BioReaction r) {
        return this.getLeftsOrRights(r, true);
    }

    /**
     * Return the right metabolites of a reaction
     *
     * @param r
     * @return
     */
    public BioCollection<BioMetabolite> getRights(BioReaction r) {
        return this.getLeftsOrRights(r, false);
    }

    /**
     * Return all the metabolites involved in a set of reactions
     *
     * @param reactions
     * @return
     */
    public BioCollection<BioMetabolite> getMetabolitesFromReactions(BioCollection<BioReaction> reactions) {

        BioCollection<BioMetabolite> allMetabolites = new BioCollection<>();

        for (BioReaction reaction : reactions) {
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
     * @param m
     * @return
     */
    public BioCollection<BioReaction> getReactionsFromMetabolite(BioMetabolite m) {

        BioCollection<BioReaction> reactions = new BioCollection<>();

        reactions.addAll(this.getReactionsFromSubstrate(m));
        reactions.addAll(this.getReactionsFromProduct(m));

        return reactions;
    }

    /**
     * @param e
     * @return
     */
    public BioCollection<BioCompartment> getCompartmentsOf(BioEntity e) {

        BioCollection<BioCompartment> cpts = new BioCollection<>();

        if (!this.contains(e)) {
            throw new IllegalArgumentException("Entity " + e + " not present in the network");
        }

        for (BioCompartment c : this.getCompartmentsView()) {
            if (c.getComponents().contains(e)) {
                cpts.add(c);
            }
        }

        return cpts;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BioNetwork that = (BioNetwork) o;
        return Objects.equals(pathways, that.pathways) &&
                Objects.equals(metabolites, that.metabolites) &&
                Objects.equals(proteins, that.proteins) &&
                Objects.equals(genes, that.genes) &&
                Objects.equals(reactions, that.reactions) &&
                Objects.equals(compartments, that.compartments) &&
                Objects.equals(enzymes, that.enzymes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pathways, metabolites, proteins, genes, reactions, compartments, enzymes);
    }
}
