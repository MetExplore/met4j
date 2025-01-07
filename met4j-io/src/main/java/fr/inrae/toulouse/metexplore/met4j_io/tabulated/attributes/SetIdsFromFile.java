/*
 * Copyright INRAE (2022)
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
package fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;

import java.io.IOException;

public class SetIdsFromFile extends AbstractSetAttributesFromFile {


    /**
     * <p>Constructor for AbstractSetAttributesFromFile.</p>
     *
     * @param colId      an int.
     * @param colAttr    : number of the attribute column
     * @param bn         : {@link BioNetwork}
     * @param fileIn     : tabulated file containing the ids and the attributes
     * @param c          : comment character
     * @param nSkip      an int. Number of lines to skip
     * @param entityType a {@link EntityType}
     * @param p          a {@link Boolean} object : To match the objects in the sbml file, adds the prefix R_ to reactions and M_ to metabolites
     * @param s          a {@link Boolean} object : To match the objects in the sbml file, adds the suffix _compartmentID to metabolite
     */
    public SetIdsFromFile(int colId, int colAttr, BioNetwork bn, String fileIn, String c, int nSkip, EntityType entityType, Boolean p, Boolean s) {
        super(colId, colAttr, bn, fileIn, c, nSkip, entityType, p, s);
    }

    @Override
    public Boolean testAttribute(String attribute) {
        return !StringUtils.isVoid(attribute);
    }

    @Override
    public Boolean setAttributes() throws IOException {
        Boolean flag;

        try {
            flag = this.parseAttributeFile();
        } catch (IOException e) {
            return false;
        }

        if (!flag) {
            return false;
        }

        int n = 0;

        for (String id : this.getIdAttributeMap().keySet()) {

            String newId = this.getIdAttributeMap().get(id);

            if (!id.equals(newId)) {

                n++;

                switch (entityType) {
                    case GENE: {
                        this.setGeneId(id, newId);
                        break;
                    }
                    case PATHWAY: {
                        this.setPathwayId(id, newId);
                        break;
                    }
                    case METABOLITE: {
                        this.setMetaboliteId(id, newId);
                        break;
                    }
                    case REACTION: {
                        this.setReactionId(id, newId);
                        break;
                    }
                    case COMPARTMENT: {
                        this.setCompartmentId(id, newId);
                        break;
                    }
                    default: {
                        throw new EntityTypeException("Entity type " + this.entityType + " not recognized");
                    }
                }
            }
        }

        System.out.println(n + " entities processed");

        return true;
    }

    /**
     * Replace a compartment with the same compartment with another id
     *
     * @param id    : the original id
     * @param newId : the new id
     */
    private void setCompartmentId(String id, String newId) {
        BioCompartment compartment = this.bn.getCompartment(id);

        if (compartment != null) {
            BioCompartment newCompartment;

            if (!this.bn.containsCompartment(newId)) {
                newCompartment = new BioCompartment(compartment, newId);
                this.bn.add(newCompartment);
            } else {
                System.err.println("[WARNING] The compartment "+newId+" already exists");
                newCompartment = this.bn.getCompartment(newId);
            }

            // Add metabolites
            this.bn.affectToCompartment(newCompartment, compartment.getComponentsView());

            // Change compartment in each left reactant
            this.bn.getReactionsView().forEach(r -> r.getLeftReactantsView().forEach(reactant -> {
                if (reactant.getLocation().equals(compartment)) {
                    BioMetabolite metabolite = reactant.getMetabolite();
                    Double sto = reactant.getQuantity();
                    this.bn.affectLeft(r, sto, newCompartment, metabolite);
                    this.bn.removeLeft(metabolite, compartment, r);
                }
            }));

            // Change compartment in each right reactant
            this.bn.getReactionsView().forEach(r -> r.getRightReactantsView().forEach(reactant -> {
                if (reactant.getLocation().equals(compartment)) {
                    BioMetabolite metabolite = reactant.getMetabolite();
                    Double sto = reactant.getQuantity();
                    this.bn.affectRight(r, sto, newCompartment, metabolite);
                    this.bn.removeRight(metabolite, compartment, r);
                }
            }));

            this.bn.removeOnCascade(compartment);

        }
    }

    /**
     * Replace a reaction with the same reaction with another id
     *
     * @param id    : the original id
     * @param newId : the new id
     */
    private void setReactionId(String id, String newId) {
        BioReaction reaction = this.bn.getReaction(id);

        if (reaction != null) {
            BioReaction newReaction;

            if (!this.bn.containsReaction(newId)) {
                newReaction = new BioReaction(reaction, newId);
                this.bn.add(newReaction);
                // Add lefts and rights
                this.bn.affectLeft(newReaction, reaction.getLeftReactantsView());
                this.bn.affectRight(newReaction, reaction.getRightReactantsView());
            } else {
                newReaction = this.bn.getReaction(newId);
                System.err.println("[WARNING] The reaction "+newId+" already exists");
            }

            // Add enzymes
            this.bn.affectEnzyme(newReaction, reaction.getEnzymesView());

            // Change reaction in the pathways
            this.bn.getPathwaysFromReaction(reaction).forEach(p -> this.bn.affectToPathway(p, newReaction));

            this.bn.removeOnCascade(reaction);
        }
    }

    /**
     * Replace a metabolite with the same metabolite with another id
     *
     * @param id    : the original id
     * @param newId : the new id
     */
    private void setMetaboliteId(String id, String newId) {
        BioMetabolite metabolite = this.bn.getMetabolite(id);

        if (metabolite != null) {
            BioMetabolite newMetabolite;

            if (!this.bn.containsMetabolite(newId)) {
                newMetabolite = new BioMetabolite(metabolite, newId);
                this.bn.add(newMetabolite);
                // Change the metabolite in the compartments
                this.bn.getCompartmentsView().stream().filter(c -> c.getComponentsView().contains(metabolite)).forEach(c -> {
                    this.bn.removeFromCompartment(c, metabolite);
                    this.bn.affectToCompartment(c, newMetabolite);
                });
            } else {
                newMetabolite = this.bn.getMetabolite(newId);
                System.err.println("[WARNING] The metabolite "+newId+" already exists");
            }

            // Change the metabolite in the left reactants
            this.bn.getReactionsView().forEach(r -> r.getLeftReactantsView().stream().filter(reactant -> reactant.getMetabolite().equals(metabolite))
                    .forEach(reactant -> {
                        Double sto = reactant.getQuantity();
                        BioCompartment cpt = reactant.getLocation();
                        this.bn.affectLeft(r, sto, cpt, newMetabolite);
                    }));

            // Change the metabolite in the right reactants
            this.bn.getReactionsView().forEach(r ->
                    r.getRightReactantsView()
                            .stream().filter(reactant -> reactant.getMetabolite().equals(metabolite))
                            .forEach(reactant -> {
                                Double sto = reactant.getQuantity();
                                BioCompartment cpt = reactant.getLocation();
                                this.bn.affectRight(r, sto, cpt, newMetabolite);
                            }));

            // Change the metabolite in the enzymes
            this.bn.getEnzymesView().
                    forEach(e -> e.getParticipantsView()
                            .stream().filter(p -> p.getPhysicalEntity().equals(metabolite))
                            .forEach(participant -> {
                                Double sto = participant.getQuantity();
                                this.bn.affectSubUnit(e, sto, newMetabolite);
                            }));

            this.bn.removeOnCascade(metabolite);
        }
    }

    /**
     * Replace a pathway with the same pathway with another id
     *
     * @param id    : the original id
     * @param newId : the new id
     */
    private void setPathwayId(String id, String newId) {
        BioPathway originalPathway = this.bn.getPathway(id);

        if (originalPathway != null) {
            BioPathway newPathway;

            if (!this.bn.containsPathway(newId)) {
                newPathway = new BioPathway(originalPathway, newId);
                this.bn.add(newPathway);
            } else {
                newPathway = this.bn.getPathway(newId);
                System.err.println("[WARNING] The pathway "+newId+" already exists");
            }

            this.bn.affectToPathway(newPathway, this.bn.getReactionsFromPathways(originalPathway));
            this.bn.removeOnCascade(originalPathway);
        }
    }

    /**
     * Replace a gene with the same gene with another id
     *
     * @param id    : the original id
     * @param newId : the new id
     */
    private void setGeneId(String id, String newId) {
        BioGene originalGene = this.bn.getGene(id);

        if (originalGene != null) {
            BioGene newGene;
            if (!this.bn.containsGene(newId)) {
                newGene = new BioGene(originalGene, newId);
                this.bn.add(newGene);
            } else {
                newGene = this.bn.getGene(id);
                System.err.println("[WARNING] The gene "+newId+" already exists");
            }

            this.bn.getProteinsView().stream().filter(p -> p.getGene().equals(originalGene)).forEach(p -> this.bn.affectGeneProduct(p, newGene));

            this.bn.removeOnCascade(originalGene);
        }
    }
}
