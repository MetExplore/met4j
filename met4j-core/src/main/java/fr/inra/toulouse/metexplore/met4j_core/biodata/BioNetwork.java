/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
/*
 * Created on 1 juil. 2005
 * L.C
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction.Side;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

/**
 * 
 */

public class BioNetwork extends BioEntity {


	private BioCollection<BioPathway> pathways = new BioCollection<BioPathway>();

	private BioCollection<BioMetabolite> metabolites = new BioCollection<BioMetabolite>();

	private BioCollection<BioProtein> proteins = new BioCollection<BioProtein>();

	private BioCollection<BioGene> genes = new BioCollection<BioGene>();

	private BioCollection<BioReaction> reactions = new BioCollection<BioReaction>();

	private BioCollection<BioCompartment> compartments = new BioCollection<BioCompartment>();

	private BioCollection<BioEnzyme> enzymes = new BioCollection<BioEnzyme>();

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

	public void remove(BioEntity e) throws IllegalArgumentException {

		if (e instanceof BioPathway) {
			this.pathways.remove((BioPathway) e);
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
			this.enzymes.remove((BioEnzyme) e);
		} else {

			throw new IllegalArgumentException(
					"BioEntity \"" + e.getClass().getSimpleName() + "\" not supported by BioNetwork");
		}

		e = null;

	}

	/**
	 * Remove protein from the network and from the enzymes and the compartments
	 * where it is involved
	 */
	private void removeProtein(BioProtein protein) {

		this.proteins.remove(protein);

		this.enzymes.forEach(e -> {
			BioCollection<BioEnzymeParticipant> participants = new BioCollection<BioEnzymeParticipant>(
					e.getParticipants());

					Boolean remove = false;
					for(BioEnzymeParticipant p : participants) {
						if (p.getPhysicalEntity().equals(protein)) {
							remove = true;
							break;
						}
					}

					if(remove)
					{
						this.remove(e);
					}
		});

		this.compartments.forEach(c -> {
			BioCollection<BioPhysicalEntity> components = new BioCollection<BioPhysicalEntity>(c.getComponents());

			components.forEach(p -> {
				if (p.equals(protein)) {
					c.getComponents().remove(p);
				}
			});
		});

	}

	/**
	 * Remove a metabolite from the network and from the reactions and compartments
	 * where it is involved and from the c
	 */
	private void removeMetabolite(BioMetabolite m) {
		this.metabolites.remove(m);

		BioCollection<BioReaction> reactionsCopy = new BioCollection<BioReaction>(reactions);

		reactionsCopy.forEach(r -> {
			BioCollection<BioReactant> lefts = new BioCollection<BioReactant>(r.getLeftReactants());

			Boolean remove = false;

			for (BioReactant participant : lefts) {
				if (participant.getPhysicalEntity().equals(m)) {
					remove = true;
					break;
				}
			}

			if (remove == false) {
				BioCollection<BioReactant> rights = new BioCollection<BioReactant>(r.getRightReactants());
				for (BioReactant participant : rights) {
					if (participant.getPhysicalEntity().equals(m)) {
						remove = true;
						break;
					}
				}
			}

			if(remove == true)
			{
				this.remove(r);
			}

		});

		this.compartments.forEach(c -> {
			BioCollection<BioPhysicalEntity> components = new BioCollection<BioPhysicalEntity>(c.getComponents());

			components.forEach(p -> {
				if (p.equals(m)) {
					c.getComponents().remove(p);
				}
			});
		});

		BioCollection<BioEnzyme> enzymesCopy = new BioCollection<BioEnzyme>(this.enzymes);

		enzymesCopy.forEach(e -> {
			BioCollection<BioEnzymeParticipant> participants = new BioCollection<BioEnzymeParticipant>(
					e.getParticipants());

					Boolean remove = false;
					for(BioEnzymeParticipant p : participants) {
						if (p.getPhysicalEntity().equals(m)) {
							remove = true;
							break;
						}
					}

					if(remove)
					{
						this.remove(e);
					}
			
		});
	}

	/**
	 * Remove a gene from the network and remove the link between the gene and
	 * proteins
	 */
	private void removeGene(BioGene g) {

		this.genes.remove(g);

		BioCollection<BioProtein> proteinsCopy = new BioCollection<BioProtein>(proteins);
		proteinsCopy.forEach(p -> {
			if (p.getGene().equals(g)) {
				this.remove(p);
			}
		});

	}

	/**
	 * Remove a reaction from the network and from the pathways where it is involved
	 */
	private void removeReaction(BioReaction r) {

		this.reactions.remove(r);

		this.pathways.forEach(p -> {
			try {
				p.removeReaction(r);
			} catch (IllegalArgumentException e) {
			}
			;
		});

	}

	/**
	 * Remove a compartment and all the reactions that involve reactants in this
	 * compartment
	 */
	private void removeCompartment(BioCompartment c) {
		this.compartments.remove(c);

		this.reactions.forEach(r -> {

			BioCollection<BioReactant> reactants = r.getReactants();
			for (BioReactant reactant : reactants) {
				if (reactant.getLocation().equals(c)) {
					this.removeReaction(r);
					break;
				}
			}
		});

	}

	/**
	 * add a relation reactant-reaction
	 */
	public void affectLeft(BioMetabolite substrate, Double stoichiometry, BioCompartment localisation,
			BioReaction reaction) {

		affectSideReaction(substrate, stoichiometry, localisation, reaction, Side.LEFT);

	}

	/**
	 * Remove a left reactant
	 */
	public void removeLeft(BioPhysicalEntity e, BioCompartment localisation, BioReaction reaction) {

		removeSideReaction(e, localisation, reaction, Side.LEFT);
	}

	/**
	 * Add a relation product-reaction
	 */
	public void affectRight(BioMetabolite product, Double stoichiometry, BioCompartment localisation,
			BioReaction reaction) {

		affectSideReaction(product, stoichiometry, localisation, reaction, Side.RIGHT);
	}

	/**
	 * Remove a right reactant
	 */
	public void removeRight(BioPhysicalEntity e, BioCompartment localisation, BioReaction reaction) {

		removeSideReaction(e, localisation, reaction, Side.RIGHT);
	}

	private void affectSideReaction(BioMetabolite e, Double stoichiometry, BioCompartment localisation,
			BioReaction reaction, Side side) {
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

		if (side.equals(Side.LEFT)) {
			reaction.getLeftReactants().add(reactant);
		} else {
			reaction.getRightReactants().add(reactant);
		}
	}

	/**
	 * Remove an entity from a side of reaction
	 */
	private void removeSideReaction(BioPhysicalEntity e, BioCompartment localisation, BioReaction reaction, Side side) {

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
	public void affectEnzyme(BioEnzyme enzyme, BioReaction reaction) {

		if (!this.contains(enzyme)) {
			throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
		}

		if (!this.contains(reaction)) {
			throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
		}

		reaction.addEnzyme(enzyme);

	};

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

	};

	// relation enzyme -constituant
	public void affectSubUnit(BioPhysicalEntity unit, Double quantity, BioEnzyme enzyme) {

		BioEnzymeParticipant p = new BioEnzymeParticipant(unit, quantity);

		if (!this.contains(enzyme)) {
			throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
		}

		if (!this.contains(unit)) {
			throw new IllegalArgumentException("Physical entity " + unit.getId() + " not present in the network");
		}

		enzyme.addParticipant(p);

	};

	public void removeSubUnit(BioPhysicalEntity unit, BioEnzyme enzyme) {

		if (!this.contains(enzyme)) {
			throw new IllegalArgumentException("Enzyme " + enzyme.getId() + " not present in the network");
		}

		if (!this.contains(unit)) {
			throw new IllegalArgumentException("Physical entity " + unit.getId() + " not present in the network");
		}

		enzyme.removeParticipant(unit);

	};

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

	};

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

	};

	/**
	 * Add a pathway affected to a reaction
	 */
	public void affectToPathway(BioReaction reaction, BioPathway pathway) {

		if (!this.contains(pathway)) {
			throw new IllegalArgumentException("Pathway " + pathway.getId() + " not present in the network");
		}

		if (!this.contains(reaction)) {
			throw new IllegalArgumentException("Reaction " + reaction.getId() + " not present in the network");
		}

		pathway.addReaction(reaction);

	};

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
	public void affectToCompartment(BioPhysicalEntity entity, BioCompartment compartment) {

		if (!contains(compartment)) {
			throw new IllegalArgumentException("Compartment " + compartment.getId() + " not in the network");
		}

		if (!contains(entity)) {
			throw new IllegalArgumentException("Physical entity " + entity.getId() + " not in the network");
		}

		compartment.addComponent(entity);

	};

	/**
	 * Return true if the entity is in the list of metabolites, reactions, genes,
	 * pathways, proteins, etc...
	 */
	public Boolean contains(BioEntity e) {

		if (e instanceof BioProtein) {
			if (this.proteins.contains(e)) {
				return true;
			} else {
				return false;
			}
		}
		if (e instanceof BioMetabolite) {
			if (this.metabolites.contains(e)) {
				return true;
			} else {
				return false;
			}
		}
		if (e instanceof BioGene) {
			if (this.genes.contains(e)) {
				return true;
			} else {
				return false;
			}
		}
		if (e instanceof BioEnzyme) {
			if (this.enzymes.contains(e)) {
				return true;
			} else {
				return false;
			}
		}
		if (e instanceof BioReaction) {
			if (this.reactions.contains(e)) {
				return true;
			} else {
				return false;
			}
		}
		if (e instanceof BioPathway) {
			if (this.pathways.contains(e)) {
				return true;
			} else {
				return false;
			}
		}
		if (e instanceof BioCompartment) {
			if (this.compartments.contains(e)) {
				return true;
			} else {
				return false;
			}
		}

		throw new IllegalArgumentException("BioEntity " + e.getClass().getName() + " not handled by BioNetwork");
	}

	/**
	 * returns the list of reactions that can use as substrates a list of
	 * metabolites
	 * 
	 * @param exact if true, the match must be exact, if false, the reactions
	 *              returned can have a superset of the specified substrates
	 */
	public BioCollection<BioReaction> getReactionsFromSubstrates(Collection<String> substrates, Boolean exact) {

		return this.getReactionsFromSubstratesOrProducts(substrates, exact, true);

	}

	/**
	 * returns the list of reactions that can prodice a list of metabolites
	 * 
	 * @param exact if true, the match must be exact, if false, the reactions
	 *              returned can have a superset of the specified products
	 */
	public BioCollection<BioReaction> getReactionsFromProducts(Collection<String> substrates, Boolean exact) {

		return this.getReactionsFromSubstratesOrProducts(substrates, exact, false);

	}

	/**
	 * Get reactions from a list of ids of substrates (or products)
	 */
	private BioCollection<BioReaction> getReactionsFromSubstratesOrProducts(Collection<String> substrates,
			Boolean exact, Boolean areSubstrates) {

		for (String s : substrates) {
			if (!this.metabolites.containsId(s)) {
				throw new IllegalArgumentException("Metabolite " + s + " not present in the network");
			}
		}

		HashSet<BioReaction> reactionSet = new HashSet<BioReaction>(this.getReactionsView().stream().filter(o -> {
			BioReaction r = (BioReaction) o;

			if (!r.isReversible()) {

				Set<String> refIds = areSubstrates ? r.getLeft().getIds() : r.getRight().getIds();

				return exact ? refIds.equals(substrates) : refIds.containsAll(substrates);

			} else {
				return exact ? r.getRight().getIds().equals(substrates) || r.getLeft().getIds().equals(substrates)
						: r.getRight().getIds().containsAll(substrates) || r.getLeft().getIds().containsAll(substrates);
			}
		}).collect(Collectors.toSet()));

		return new BioCollection<BioReaction>(reactionSet);
	}

	/**
	 * Get pathways where a metabolite is involved
	 */
	public BioCollection<BioPathway> getPathwaysFromMetabolites(Collection<String> metaboliteIds, Boolean all) {

		for (String s : metaboliteIds) {
			if (!this.metabolites.containsId(s)) {
				throw new IllegalArgumentException("Metabolite " + s + " not present in the network");
			}
		}

		HashSet<BioPathway> pathwaySet = new HashSet<BioPathway>(this.getPathwaysView().stream().filter(o -> {

			BioPathway p = (BioPathway) o;

			Set<String> metaboliteIdRefs = p.getMetabolites().getIds();

			return all ? metaboliteIdRefs.containsAll(metaboliteIds)
					: !Collections.disjoint(metaboliteIds, metaboliteIdRefs);

		}).collect(Collectors.toSet()));

		return new BioCollection<BioPathway>(pathwaySet);

	}

	/**
	 * Get reactions from genes
	 */
	public BioCollection<BioReaction> getReactionsFromGenes(Collection<String> geneIds, Boolean all) {

		for (String s : geneIds) {
			if (!this.genes.containsId(s)) {
				throw new IllegalArgumentException("Gene " + s + " not present in the network");
			}
		}

		HashSet<BioReaction> reactionSet = new HashSet<BioReaction>(this.getReactionsView().stream().filter(o -> {
			BioReaction r = (BioReaction) o;
			Set<String> geneRefIds = r.getGenes().getIds();

			return all ? geneRefIds.containsAll(geneIds) : !Collections.disjoint(geneRefIds, geneIds);

		}).collect(Collectors.toSet()));

		return new BioCollection<BioReaction>(reactionSet);

	}

	/**
	 * Get genes involved in a set of reactions
	 */
	public BioCollection<BioGene> getGenesFromReactions(Collection<String> reactionIds) {

		BioCollection<BioReaction> reactionsToTest = new BioCollection<BioReaction>();
		for (String s : reactionIds) {
			if (!this.reactions.containsId(s)) {
				throw new IllegalArgumentException("Reaction " + s + " not present in the network");
			}
			reactionsToTest.add(reactions.getEntityFromId(s));
		}

		BioCollection<BioGene> genes = new BioCollection<BioGene>();

		reactionsToTest.forEach(r -> {
			try {
				genes.addAll(r.getGenes());
			} catch (IllegalArgumentException e) {
			}
		});

		return genes;
	}

	/**
	 * Get genes from pathways
	 */
	public BioCollection<BioGene> getGenesFromPathways(Collection<String> pathwayIds) {
		BioCollection<BioPathway> pathwaysToTest = new BioCollection<BioPathway>();
		for (String s : pathwayIds) {
			if (!this.pathways.containsId(s)) {
				throw new IllegalArgumentException("Reaction " + s + " not present in the network");
			}
			pathwaysToTest.add(pathways.getEntityFromId(s));
		}

		BioCollection<BioGene> genes = new BioCollection<BioGene>();

		pathwaysToTest.forEach(p -> {

			p.getReactions().forEach(r -> {
				try {
					genes.addAll(r.getGenes());
				} catch (IllegalArgumentException e) {
				}
			});

		});

		return genes;

	}

	/**
	 * Get pathways from genes
	 */
	public BioCollection<BioPathway> getPathwaysFromGenes(Collection<String> geneIds, Boolean all) {
		for (String s : geneIds) {
			if (!this.genes.containsId(s)) {
				throw new IllegalArgumentException("Gene " + s + " not present in the network");
			}
		}

		HashSet<BioPathway> pathwaySet = new HashSet<BioPathway>(this.getPathwaysView().stream().filter(p -> {

			Set<String> geneRefIds = p.getGenes().getIds();

			return all ? geneRefIds.containsAll(geneIds) : !Collections.disjoint(geneRefIds, geneIds);

		}).collect(Collectors.toSet()));

		return new BioCollection<BioPathway>(pathwaySet);

	}

	/**
	 * get pathways from reactions
	 */
	public BioCollection<BioPathway> getPathwaysFromReactions(Collection<String> reactionIds, Boolean all) {
		for (String s : reactionIds) {
			if (!this.reactions.containsId(s)) {
				throw new IllegalArgumentException("Reaction " + s + " not present in the network");
			}
		}

		HashSet<BioPathway> pathwaySet = new HashSet<BioPathway>(this.getPathwaysView().stream().filter(p -> {

			Set<String> reactionRefIds = p.getReactions().getIds();

			return all ? reactionRefIds.containsAll(reactionIds) : !Collections.disjoint(reactionRefIds, reactionIds);

		}).collect(Collectors.toSet()));

		return new BioCollection<BioPathway>(pathwaySet);

	}

	/**
	 * Get reactions involved in pathways
	 */
	public BioCollection<BioReaction> getReactionsFromPathways(Collection<String> pathwayIds) {
		BioCollection<BioPathway> pathwaysToTest = new BioCollection<BioPathway>();
		for (String s : pathwayIds) {
			if (!this.pathways.containsId(s)) {
				throw new IllegalArgumentException("Reaction " + s + " not present in the network");
			}
			pathwaysToTest.add(pathways.getEntityFromId(s));
		}

		BioCollection<BioReaction> reactions = new BioCollection<BioReaction>();

		pathwaysToTest.forEach(p -> {

			reactions.addAll(p.getReactions());

		});

		return reactions;

	};
	// obtenir les reactions en fonction d'un substrat ou d'un produit
	// obtenir les reactions en fonction d'un pathway

	// obtenir les metabolites en fonction d'un pathway
	// obtenir les pathways depuis un metabolite
	// obtenir les pathways depuis une reaction

	// /**
	// * Add a pathway in the network Each reaction involved in a pathway must be
	// * present in the reaction list
	// *
	// * @param {@link BioPathway} pathway
	// */
	// public void addPathway(BioPathway pathway) {
	//
	// // We check that each reaction is already in the network
	// for (BioReaction reaction : pathway.getReactions()) {
	// if (!this.reactions.contains(reaction)) {
	// throw new UnsupportedOperationException(
	// "Reaction "
	// + reaction.getId()
	// + " not present in the BioNetwork. You have to add the reaction before adding
	// the pathway");
	// }
	// }
	//
	// this.pathways.add(pathway);
	//
	// }
	//
	// /**
	// * Add a metabolite in the network
	// *
	// * @param {@link BioMetabolite} metabolite
	// */
	// public void addMetabolite(BioMetabolite metabolite) {
	// this.metabolites.add(metabolite);
	// }
	//
	// /**
	// * Add a protein in the network
	// *
	// * @param {@link BioProtein} protein
	// */
	// public void addProtein(BioProtein protein) {
	// this.proteins.add(protein);
	// }
	//
	// /**
	// * Add a gene in the network Each protein must be present in the protein
	// * list
	// *
	// * @param {@link BioGene} gene
	// */
	// public void addGene(BioGene gene) {
	//
	// // We check that each protein is already in the network
	// for (BioProtein protein : gene.getProteinList()) {
	// if (!this.proteins.contains(protein)) {
	// throw new UnsupportedOperationException(
	// "Protein "
	// + protein.getId()
	// + " not present in the BioNetwork. You have to add the protein before adding
	// the gene");
	// }
	// }
	//
	// this.genes.add(gene);
	//
	// }
	//
	// /**
	// * Add a reaction in the network Each reactant must be present in the
	// * protein list
	// *
	// * @param {@link BioReaction} reaction
	// */
	// public void addReaction(BioReaction reaction) {
	//
	// // We check that each reactant is already in the network
	// for (BioPhysicalEntity entity : reaction.getEntities()) {
	// if (entity.getClass() == BioProtein.class) {
	// if (!this.proteins.contains(entity)) {
	// throw new UnsupportedOperationException(
	// "Protein "
	// + entity.getId()
	// + " not present in the BioNetwork. You have to add the protein before adding
	// the reaction");
	// }
	// } else {
	// if (!this.metabolites.contains(entity)) {
	// throw new UnsupportedOperationException(
	// "Metabolite "
	// + entity.getId()
	// + " not present in the BioNetwork. You have to add the metabolite before
	// adding the reaction");
	// }
	// }
	//
	// }
	//
	// this.reactions.add(reaction);
	//
	// }
	//
	// public void addCompartment(BioCompartment compartment) {
	//
	// // We check that each participant is already in the network
	// for (BioPhysicalEntity entity : compartment.getEntities()) {
	// if (entity.getClass() == BioProtein.class) {
	// if (!this.proteins.contains(entity)) {
	// throw new UnsupportedOperationException(
	// "Protein "
	// + entity.getId()
	// + " not present in the BioNetwork. You have to add the protein before adding
	// the reaction");
	// }
	// } else {
	// if (!this.metabolites.contains(entity)) {
	// throw new UnsupportedOperationException(
	// "Metabolite "
	// + entity.getId()
	// + " not present in the BioNetwork. You have to add the metabolite before
	// adding the reaction");
	// }
	// }
	//
	// }
	//
	//
	// this.compartments.add(compartment);
	// }
	//
	// public void addEnzyme(BioEnzyme enzyme) {
	// // verifier que ses participants sont bien dans metabolites and proteins
	// }
	//
	// /**
	// * Removes a compound from a network.
	// */
	// public void removeMetabolite(String id) {
	//
	// if (this.getPhysicalEntityList().containsKey(id) == true) {
	//
	// HashMap<String, BioReaction> RP = this
	// .getListOfReactionsAsProduct(id);
	// HashMap<String, BioReaction> RC = this
	// .getListOfReactionsAsSubstrate(id);
	//
	// HashMap<String, BioReaction> reactions = new HashMap<String, BioReaction>();
	// reactions.putAll(RP);
	// reactions.putAll(RC);
	//
	// this.getPhysicalEntityList().remove(id);
	//
	// for (BioReaction rxn : reactions.values()) {
	//
	// Set<String> left = rxn.getLeftList().keySet();
	// Set<String> right = rxn.getRightList().keySet();
	//
	// if (left.contains(id) == true) {
	// rxn.removeLeftCpd(rxn.getLeftList().get(id));
	// }
	//
	// if (right.contains(id) == true) {
	// rxn.removeRightCpd(rxn.getRightList().get(id));
	// }
	//
	// if (rxn.getLeftList().size() == 0
	// || rxn.getRightList().size() == 0) {
	// this.removeBioChemicalReaction(rxn.getId());
	// }
	// }
	// }
	// }

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

		return r.getLeftReactants();

	}
	
	public BioCollection<BioReactant> getRightReactants(BioReaction r) {

		return r.getRightReactants();

	}

}
