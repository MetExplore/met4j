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
 * The BioNetwork class is the essential class of met4j.
 *
 * It contains and links all the entities composing a metabolic network (metabolites, reactions,
 * pathways, genes, proteins, enzymes, compartments)
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
     * Copy only the id, the refs and the attributes
     * @param network
     */
    public BioNetwork(BioNetwork network) {
        super(network);
    }

    /**
     * Add several entities
     *
     * @param bioEntities 0 to several {@link BioEntity} instances
     */
    public void add(BioEntity... bioEntities) {

        for (BioEntity e : bioEntities) {
            this.add(e);
        }
    }

    /**
     * Add several entities
     *
     * @param bioEntities a {@link BioCollection} of {@link BioEntity}
     */
    public void add(BioCollection<?> bioEntities) {

        for (BioEntity e : bioEntities) {
            this.add(e);
        }
    }

    /**
     * Add one entity
     *
     * @param e a {@link BioEntity}
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
     * @param entities 0 or several {@link BioEntity}
     */
    public void removeOnCascade(BioEntity... entities) {
        for (BioEntity e : entities) {
            removeOnCascade(e);
        }
    }

    /**
     * Remove on cascade several entities stored in a BioCollection
     *
     * @param entities a {@link BioCollection} of {@link BioEntity}
     */
    public void removeOnCascade(BioCollection<?> entities) {
        for (BioEntity e : entities) {
            removeOnCascade(e);
        }
    }


    /**
     * Remove protein from the network and from the enzymes and the compartments
     * where it is involved
     *
     * @param protein a {@link BioProtein} instance
     *
     */
    private void removeProtein(BioProtein protein) {

        this.proteins.remove(protein);

        this.getEnzymesView().forEach(e -> {
            BioCollection<BioEnzymeParticipant> participants = new BioCollection<>(
                    e.getParticipants());

            boolean remove = false;
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
     *
     * @param m a {@link BioMetabolite}
     *
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
     * proteins
     *
     * @param g a {@link BioGene}
     *
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
     *
     * @param r a {@link BioReaction}
     *
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
     *
     * @param c a BioCompartment
     *
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
     *
     */
    private void affectLeft(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite substrate) {

        affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.LEFT, substrate);

    }

    /**
     *
     * Add several left metabolites to a reaction
     *
     * @param reaction a {@link BioReaction}
     * @param stoichiometry the stroichiometric coefficient
     * @param localisation the compartment of the substrates
     * @param substrates 0 or several {@link BioMetabolite}
     */
    public void affectLeft(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite... substrates) {

        for (BioMetabolite s : substrates)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.LEFT, s);

    }

    /**
     *
     * Add several left metabolites to a reaction
     *
     * @param reaction a {@link BioReaction}
     * @param stoichiometry the stroichiometric coefficient
     * @param localisation the compartment of the substrates
     * @param substrates a {@link BioCollection} of {@link BioMetabolite}
     */
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

    /**
     *
     * Add several left reactants to a reaction
     *
     * @param reaction a {@link BioReaction}
     * @param reactants 0 or several {@link BioReactant}
     */
    public void affectLeft(BioReaction reaction, BioReactant... reactants) {
        for (BioReactant reactant : reactants) {
            this.affectLeft(reaction, reactant);
        }
    }

    /**
     *
     * Add several left reactants to a reaction
     *
     * @param reaction a {@link BioReaction}
     * @param reactants a {@link BioCollection} {@link BioReactant}
     */
    public void affectLeft(BioReaction reaction, BioCollection<BioReactant> reactants) {
        this.affectLeft(reaction, reactants.toArray(new BioReactant[0]));
    }

    /**
     * Remove a left reactant
     * @param e a {@link BioMetabolite} to remove from the left side of the reaction
     * @param localisation a {@link BioCompartment}
     * @param reaction a {@link BioReaction}
     */
    public void removeLeft(BioMetabolite e, BioCompartment localisation, BioReaction reaction) {

        removeSideReaction(e, localisation, reaction, BioReaction.Side.LEFT);
    }

    /**
     * Add a relation product-reaction
     *
     * @param reaction a {@link BioReaction}
     * @param stoichiometry the stoichiometric coefficient
     * @param localisation  a {@link BioCompartment}
     * @param product a {@link BioMetabolite} to add to the right side of the reaction
     */
    public void affectRight(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite product) {

        affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    /**
     * Add several relation product-reaction
     *
     * @param reaction a {@link BioReaction}
     * @param stoichiometry the stoichiometric coefficient
     * @param localisation  a {@link BioCompartment}
     * @param products 0 or several {@link BioMetabolite} to add to the right side of the reaction
     */
    public void affectRight(BioReaction reaction, Double stoichiometry, BioCompartment localisation, BioMetabolite... products) {
        for (BioMetabolite product : products)
            affectSideReaction(reaction, stoichiometry, localisation, BioReaction.Side.RIGHT, product);
    }

    /**
     * Add several relation product-reaction
     *
     * @param reaction a {@link BioReaction}
     * @param stoichiometry the stoichiometric coefficient
     * @param localisation  a {@link BioCompartment}
     * @param products a {@link BioCollection} of {@link BioMetabolite} to add to the right side of the reaction
     */
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

    /**
     *
     * affect several reactant to the right side of a reaction
     *
     * @param reaction a {@link BioReaction}
     * @param reactants 0 or several {@link BioReactant}
     */
    public void affectRight(BioReaction reaction, BioReactant... reactants) {
        for (BioReactant reactant : reactants) {
            this.affectRight(reaction, reactant);
        }
    }

    /**
     *
     * affect several reactant to the right side of a reaction
     *
     * @param reaction a {@link BioReaction}
     * @param reactants a {@link BioCollection} {@link BioReactant} of {@link BioReactant}
     */
    public void affectRight(BioReaction reaction, BioCollection<BioReactant> reactants) {
        this.affectRight(reaction, reactants.toArray(new BioReactant[0]));
    }

    /**
     * Remove a right reactant
     *
     * @param e a {@link BioMetabolite} to remove from the right side of the reaction
     * @param localisation a {@link BioCompartment}
     * @param reaction a {@link BioReaction}
     */
    public void removeRight(BioMetabolite e, BioCompartment localisation, BioReaction reaction) {

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
            throw new IllegalArgumentException("Metabolite " + e.getId() + " not in the compartment "+localisation.getId());
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
    private void removeSideReaction(BioMetabolite e, BioCompartment localisation, BioReaction reaction, BioReaction.Side side) {

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
     * @param reaction a {@link BioReaction}
     * @param enzymes 0 or several enzymes
     */
    public void affectEnzyme(BioReaction reaction, BioEnzyme... enzymes) {

        for (BioEnzyme e : enzymes) {
            affectEnzyme(reaction, e);
        }

    }

    /**
     * Affect several enzymes to a reaction
     *
     * @param reaction a {@link BioReaction}
     * @param enzymes a {@link BioCollection of enzymes}
     */
    public void affectEnzyme(BioReaction reaction, BioCollection<BioEnzyme> enzymes) {

        for (BioEnzyme e : enzymes) {
            affectEnzyme(reaction, e);
        }

    }


    /**
     * Remove the link between enzyme from a reaction
     *
     * @param enzyme a {@link BioEnzyme}
     * @param reaction a {@link BioReaction}
     *
     * @throws IllegalArgumentException if the enzyme or the reaction are not present in the network
     *
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

    /**
     * Add a subunit to an enzymes
     * @param enzyme a {@link BioEnzyme}
     * @param quantity number of units
     * @param unit a {@link BioPhysicalEntity}
     *
     * @throws IllegalArgumentException of the enzyme of the unit is not present in the network
     */
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

    /**
     * Add a subunit to an enzyme
     * @param enzyme a {@link BioEnzyme}
     * @param unit a {@link BioEnzymeParticipant}
     *
     * @throws IllegalArgumentException of the enzyme of the unit is not present in the network
     */
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
     * Add a subunit to an enzyme
     * @param enzyme a {@link BioEnzyme}
     * @param unit a {@link BioEnzymeParticipant}
     *
     * @throws IllegalArgumentException of the enzyme of the unit is not present in the network
     */
    public void affectSubUnit(BioEnzyme enzyme, BioEnzymeParticipant... units) {

       for(BioEnzymeParticipant u : units)
       {
           this.affectSubUnit(enzyme, u);
       }

    }

    /**
     * Adds several subunits to an enzyme with the same stoichiometric coefficient
     *
     * @param enzyme a {@link BioEnzyme}
     * @param quantity the number of units of each subunit to add
     * @param units 0 or several {@link BioPhysicalEntity}
     */
    public void affectSubUnit(BioEnzyme enzyme, Double quantity, BioPhysicalEntity... units) {

        for (BioPhysicalEntity unit : units) {
            affectSubUnit(enzyme, quantity, unit);
        }
    }

    /**
     * Adds several subunits to an enzyme with the same stoichiometric coefficient
     *
     * @param enzyme a {@link BioEnzyme}
     * @param quantity the number of units of each subunit to add
     * @param units a {@link BioCollection} of {@link BioPhysicalEntity}
     *
     *
     */
    public void affectSubUnit(BioEnzyme enzyme, Double quantity, BioCollection<?> units) {

        for (BioEntity unit : units) {
                affectSubUnit(enzyme, quantity, (BioPhysicalEntity)unit);

        }
    }

    /**
     * Adds several subunits to an enzyme
     *
     * @param enzyme a {@link BioEnzyme}
     * @param units a {@link BioCollection} of {@link BioEnzymeParticipant}
     */
    public void affectSubUnit(BioEnzyme enzyme, BioCollection<BioEnzymeParticipant> units) {

        for (BioEnzymeParticipant unit : units) {
            affectSubUnit(enzyme, unit);
        }
    }


    /**
     * Remove a subunit from an enzyme
     * @param unit a {@link BioPhysicalEntity} to remove from the enzyme
     * @param enzyme a {@link BioEnzyme}
     *
     * @throws IllegalArgumentException if the enzyme or the entity is not present in the network
     */
    public void removeSubUnit(BioPhysicalEntity unit, BioEnzyme enzyme) {

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
     * @param protein a {@link BioProtein} instance
     * @param gene a {@link BioGene}
     *
     * @throws IllegalArgumentException if the gene or the protein is not present in the network
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

    /**
     * Remove a relation between gene and product
     *
     * @param protein a {@link BioProtein} instance
     * @param gene a {@link BioGene}
     * @throws IllegalArgumentException if the gene or the protein is not present in the network
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

    /**
     * Add a reaction to a pathway
     *
     * @param pathway a {@link BioPathway}
     * @param reaction a {@link BioReaction}
     *
     * @throws IllegalArgumentException if the pathway or the reaction is not present
     *
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


    /**
     * Add several reactions to a pathway
     *
     * @param pathway a {@link BioPathway}
     * @param reactions 0 or several {@link BioReaction}
     *
     */
    public void affectToPathway(BioPathway pathway, BioReaction... reactions) {

        for (BioReaction reaction : reactions) {
            this.affectToPathway(pathway, reaction);
        }
    }

    /**
     * Add several reactions to a pathway
     *
     * @param pathway a {@link BioPathway}
     * @param reactions a {@link BioCollection} of {@link BioReaction}
     *
     */
    public void affectToPathway(BioPathway pathway, BioCollection<BioReaction> reactions) {

        for (BioReaction reaction : reactions) {
            this.affectToPathway(pathway, reaction);
        }
    }


    /**
     * Remove a reaction from a pathway
     *
     * @param r a {@link BioReaction}
     * @param p a {@link BioPathway}
     *
     * @throws IllegalArgumentException if the pathway or the reaction is not present
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
     *
     * @param p a {@link BioPathway}
     * @return a {@link BioCollection} of {@link BioMetabolite}
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

    /**
     * Affect several metabolites to a compartment
     *
     * @param compartment a {@link BioCompartment}
     * @param entities one or several {@link BioEntity}
     */
    public void affectToCompartment(BioCompartment compartment, BioEntity... entities) {

        for (BioEntity ent : entities) {
            this.affectToCompartment(compartment, ent);
        }

    }

    /**
     * Affect several metabolites from a collection to a compartment
     *
     * @param compartment a {@link BioCompartment}
     * @param entities a BioCollection of {@link BioEntity}
     */
    public void affectToCompartment(BioCompartment compartment, BioCollection<?> entities) {
        for (BioEntity e : entities) {
            this.affectToCompartment(compartment, e);
        }
    }

    /**
     * Return true if the entity is in the list of metabolites, reactions, genes,
     * pathways, proteins, etc...
     *
     * @param e  a {@link BioEntity}
     *
     * @throws NullPointerException if e is null
     * @throws IllegalArgumentException if e is not an instance of :
     * {@link BioProtein}, {@link BioMetabolite}, {@link BioGene}, {@link BioEnzyme},
     * {@link BioReaction}, {@link BioPathway}, or {@link BioCompartment}
     *
     * @return true if the network contains the entity
     */
    public Boolean contains(BioEntity e) {

        if (e == null) {
            System.err.println("Entity is null");
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
     * @param substrates a {@link BioCollection} of {@link BioMetabolite}
     * @param exact if true, the match must be exact, if false, the reactions
     *              returned can have a superset of the specified substrates
     *
     * @return  a {@link BioCollection} of {@link BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromSubstrates(BioCollection<BioMetabolite> substrates, Boolean exact) {

        return this.getReactionsFromSubstratesOrProducts(substrates, exact, true);

    }

    /**
     * returns the list of reactions that can produce a list of metabolites
     *
     * @param products a {@link BioCollection} of {@link BioMetabolite}
     * @param exact if true, the match must be exact, if false, the reactions
     *              returned can have a superset of the specified products
     *
     * @return  a {@link BioCollection} of {@link BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromProducts(BioCollection<BioMetabolite> products, Boolean exact) {

        return this.getReactionsFromSubstratesOrProducts(products, exact, false);

    }

    /**
     * Get reactions from a list of of substrates (or products)
     * @return  a {@link BioCollection} of {@link BioReaction}
     */
    private BioCollection<BioReaction> getReactionsFromSubstratesOrProducts(BioCollection<BioMetabolite> metabolites,
                                                                            Boolean exact, Boolean areSubstrates) {

        for (BioMetabolite m : metabolites) {
            if (!this.metabolites.contains(m)) {
                throw new IllegalArgumentException("Metabolite " + m + " not present in the network");
            }
        }

        HashSet<BioReaction> reactionSet = this.getReactionsView().stream().filter(o -> {
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
        }).collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(reactionSet);
    }

    /**
     * @param m
     * @param isSubstrate
     * @return  a {@link BioCollection} of {@link BioReaction}
     */
    private BioCollection<BioReaction> getReactionsFromSubstrateOrProduct(BioMetabolite m, Boolean isSubstrate) {

        if (m == null || !this.contains(m)) {
            throw new IllegalArgumentException("Metabolite not present in the network");
        }

        BioCollection<BioReaction> reactions = new BioCollection<>();

        this.getReactionsView().forEach(r -> {
            boolean flag = false;

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
     * @param m a {@link BioMetabolite}
     * @return a {@link BioCollection} of {@link BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromSubstrate(BioMetabolite m) {
        return this.getReactionsFromSubstrateOrProduct(m, true);
    }

    /**
     * Return the reactions involving m as product
     *
     * @param m a {@link BioMetabolite}
     * @return a {@link BioCollection} of {@link BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromProduct(BioMetabolite m) {
        return this.getReactionsFromSubstrateOrProduct(m, false);
    }

    /**
     *  Get pathways where a metabolite is involved
     *
     * @param metabolites a {@link BioCollection} of {@link BioMetabolite}
     * @param all if true : a pathway must contain all the metabolites, if false, a pathway must contain at least
     *            one of the metabolites
     * @return a {@link BioCollection} of {@link BioPathway}
     */
    public BioCollection<BioPathway> getPathwaysFromMetabolites(BioCollection<BioMetabolite> metabolites, Boolean all) {

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
     * @param genes a {@link BioCollection} of {@link BioGene}
     * @param all if true, a reaction must be coded by all the genes, if false a reaction must be coded by
     *            at least one of the genes
     * @return a {@link BioCollection} of {@link BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromGenes(BioCollection<BioGene> genes, Boolean all) {

        for (BioGene g : genes) {
            if (!this.genes.contains(g)) {
                throw new IllegalArgumentException("Gene " + g + " not present in the network");
            }
        }

        HashSet<BioReaction> reactionSet = this.getReactionsView().stream().
                filter(o -> all ? o.getGenes().containsAll(genes) : !Collections.disjoint(o.getGenes(), genes))
                .collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(reactionSet);

    }

    /**
     * Get reactions from a gene
     * @param gene a {@link BioGene}
     * @return a {@link BioCollection} of {@link BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromGene(BioGene gene) {

        if (!this.genes.contains(gene)) {
            throw new IllegalArgumentException("Gene " + gene + " not present in the network");
        }

        HashSet<BioReaction> reactionSet = this.getReactionsView().stream().
                filter(o -> o.getGenes().contains(gene))
                .collect(Collectors.toCollection(HashSet::new));

        return new BioCollection<>(reactionSet);

    }

    /**
     * Get genes involved in a set of reactions
     *
     * @param reactions one or several {@link BioReaction}
     *
     * @return a {@link BioCollection} of {@link BioGene}
     *
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
     *
     * @param reactions a {@link BioCollection} of {@link BioReaction} {@link BioReaction}
     *
     * @return a {@link BioCollection} of {@link BioGene}
     *
     */
    public BioCollection<BioGene> getGenesFromReactions(BioCollection<BioReaction> reactions) {

        return getGenesFromReactions(reactions.toArray(new BioReaction[0]));
    }

    /**
     * @param reaction a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioGene}
     *
     * @throws IllegalArgumentException if reaction is not present in the network
     * @return a {@link BioCollection} of {@link BioGene}
     */
    private BioCollection<BioGene> getGenesFromReaction(BioReaction reaction) {
        if (!this.contains(reaction)) {
            throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
        }

        return reaction.getGenes();
    }

    /**
     * Get genes from pathways
     *
     * @param pathways a {@link BioCollection} of {@link BioPathway}
     * @return a {@link BioCollection} of {@link BioGene}
     */
    public BioCollection<BioGene> getGenesFromPathways(BioCollection<BioPathway> pathways) {
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
     * @param pathways 0 or several {@link BioPathway}
     *
     * @return a {@link BioCollection} of {@link BioGene}
     */
    public BioCollection<BioGene> getGenesFromPathways(BioPathway... pathways) {
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
     * @param enzymes a {@link BioCollection of enzymes} of {@link BioEnzyme}
     * @return a {@link BioCollection} of {@link BioGene}
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
     * @param e a {@link BioEnzyme}
     * @return a {@link BioCollection} of {@link BioGene}
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
     * @param genes a {@link BioCollection} of {@link BioGene}
     * @param all if true, the pathway must contain all the genes
     *
     * @return a {@link BioCollection} of {@link BioPathway}
     */
    public BioCollection<BioPathway> getPathwaysFromGenes(BioCollection<BioGene> genes, Boolean all) {
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
     * @param reactions a {@link BioCollection} of {@link BioReaction}
     * @param all if true, the pathway must contain all the reactions
     *
     * @return a {@link BioCollection} of {@link BioPathway}
     *
     * @throws IllegalArgumentException if a reaction is missing in the network
     *
     */
    public BioCollection<BioPathway> getPathwaysFromReactions(BioCollection<BioReaction> reactions, Boolean all) {
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
     * @param r a {@link BioReaction}
     *
     * @return a {@link BioCollection} of {@link BioPathway}
     *
     * throws {@link IllegalArgumentException} if r is not in the network
     *
     */
    public BioCollection<BioPathway> getPathwaysFromReaction(BioReaction r) {
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
     * @param pathways a {@link BioCollection} of {@link BioPathway}
     * @return a {@link BioCollection} of {@link BioReaction}
     *
     * @throws IllegalArgumentException if one of the pathways is not in the network
     *
     */
    public BioCollection<BioReaction> getReactionsFromPathways(BioCollection<BioPathway> pathways) {

        BioCollection<BioReaction> reactions = new BioCollection<>();
        for (BioPathway p : pathways) {
            reactions.addAll(this.getReactionsFromPathway(p));
        }

        return reactions;

    }

    /**
     * get reactions from pathways
     * @param pathways one or several {@link BioPathway}
     * @return a {@link BioCollection} of {@link BioReaction}
     *
     * @throws IllegalArgumentException if one of the pathways is not in the network
     *
     */
    public BioCollection<BioReaction> getReactionsFromPathways(BioPathway... pathways) {

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
     *
     * @throws IllegalArgumentException if the pathway is not in the network
     *
     */
    private BioCollection<BioReaction> getReactionsFromPathway(BioPathway p) {

        if (!this.pathways.contains(p)) {
            throw new IllegalArgumentException("Pathway " + p + " not present in the network");
        }
        return p.getReactions().getView();
    }


    /**
     * Get left reactants of a reaction
     * @param r a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioReactant}
     */
    public BioCollection<BioReactant> getLeftReactants(BioReaction r) {

        return r.getLeftReactants().getView();

    }

    /**
     * Get right reactants of a reaction
     * @param r a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioReactant}
     */
    public BioCollection<BioReactant> getRightReactants(BioReaction r) {

        return r.getRightReactants().getView();

    }

    /**
     * Get left or right reactants of a reaction
     * @param r a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioReactant}
     */
    private BioCollection<BioMetabolite> getLeftsOrRights(BioReaction r, Boolean left) {

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
     * @param r a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioMetabolite}
     */
    public BioCollection<BioMetabolite> getLefts(BioReaction r) {
        return this.getLeftsOrRights(r, true);
    }

    /**
     * Get right metabolites of a reaction
     * @param r a {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioMetabolite}
     */
    public BioCollection<BioMetabolite> getRights(BioReaction r) {
        return this.getLeftsOrRights(r, false);
    }

    /**
     * Return all the metabolites involved in a set of reactions
     *
     * @param reactions a {@link BioCollection} of {@link BioReaction}
     * @return a {@link BioCollection} of {@link BioMetabolite}
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
     * @param m a {@link BioMetabolite}
     * @return a {@link BioCollection} of {@link BioReaction}
     */
    public BioCollection<BioReaction> getReactionsFromMetabolite(BioMetabolite m) {

        BioCollection<BioReaction> reactions = new BioCollection<>();

        reactions.addAll(this.getReactionsFromSubstrate(m));
        reactions.addAll(this.getReactionsFromProduct(m));

        return reactions;
    }

    /**
     *
     * Get compartments where is involved an entity
     *
     * @param e a {@link BioEntity}
     * @return a {@link BioCollection} of {@link BioCompartment}
     *
     * @throws IllegalArgumentException if the entity is not present in the network
     *
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

    /**
     * @return an unmodifiable {@link BioCollection} copy of the pathways
     */
    public BioCollection<BioPathway> getPathwaysView() {
        return pathways.getView();
    }

    /**
     * @return an unmodifiable {@link BioCollection} copy of the metabolites
     */
    public BioCollection<BioMetabolite> getMetabolitesView() {
        return metabolites.getView();
    }

    /**
     * @return an unmodifiable {@link BioCollection} copy of the proteins
     */
    public BioCollection<BioProtein> getProteinsView() {
        return proteins.getView();
    }

    /**
     * @return an unmodifiable {@link BioCollection} copy of the genes
     */
    public BioCollection<BioGene> getGenesView() {
        return genes.getView();
    }

    /**
     * @return an unmodifiable {@link BioCollection} copy of the reactions
     */
    public BioCollection<BioReaction> getReactionsView() {
        return reactions.getView();
    }

    /**
     * @return an unmodifiable {@link BioCollection} copy of the compartments
     */
    public BioCollection<BioCompartment> getCompartmentsView() {
        return compartments.getView();
    }

    /**
     * @return an unmodifiable {@link BioCollection} copy of the enzymes
     */
    public BioCollection<BioEnzyme> getEnzymesView() {
        return enzymes.getView();
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
