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
/**
 * 15 mars 2013 
 */
package fr.inrae.toulouse.metexplore.met4j_flux.thread;

import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.KOResult;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inrae.toulouse.metexplore.met4j_flux.general.DoubleResult;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Vars;
import fr.inrae.toulouse.metexplore.met4j_flux.objective.Objective;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread to perform an FVA analysis.
 *
 * @author lmarmiesse 15 mars 2013
 *
 */
public class ThreadKO extends ResolveThread {

    /**
     *
     * For the output Thread safe integer
     *
     */
    private static AtomicInteger nbPrintedStars = new AtomicInteger(0);
    protected BioCollection<BioEntity> entitiesInInteractionNetwork = new BioCollection<BioEntity>();
    protected List<Constraint> interactionNetworkConstraints = new ArrayList<Constraint>();
    /**
     * Number of entities to treat.
     */
    private double todo;
    /**
     * Contains all entities to treat.
     */
    private Queue<BioEntity> entities;
    /**
     * The KO result.
     */
    private KOResult result;

    public ThreadKO(Bind b, Queue<BioEntity> entities, KOResult result, Objective obj,
                    BioCollection<BioEntity> entitiesInInteractionNetwork, List<Constraint> interactionNetwotkConstraints) {
        super(b, obj);
        this.todo = entities.size();
        this.entities = entities;
        this.result = result;
        this.entitiesInInteractionNetwork = entitiesInInteractionNetwork;
        this.interactionNetworkConstraints = interactionNetwotkConstraints;

        nbPrintedStars = new AtomicInteger(0);

    }

    public void run() {

        BioEntity entity;

        while ((entity = entities.poll()) != null) {

            Map<BioEntity, Double> entityMap = new HashMap<BioEntity, Double>();
            entityMap.put(entity, 1.0);

            boolean removedConst = false;
            Constraint toRemove = null;
            // we remove a constraint that is already present on this entity
            if (bind.getSimpleConstraints().containsKey(entity)) {
                removedConst = true;

                toRemove = bind.getSimpleConstraints().get(entity);
                bind.getSimpleConstraints().remove(entity);
                bind.getConstraints().remove(toRemove);
//				bind.prepareSolver();
            }

            //

            List<Constraint> constraintsToAdd = new ArrayList<Constraint>();

            Constraint newConstraint = new Constraint(entityMap, 0.0, 0.0);

            constraintsToAdd.add(newConstraint);

            // if the entity is not in the interaction network, we
            // don't recompute the attractors
            if (!entitiesInInteractionNetwork.contains(entity)) {
                constraintsToAdd.addAll(interactionNetworkConstraints);
                bind.checkInteractionNetwork = false;
            } else {
                bind.checkInteractionNetwork = true;

            }


            List<Constraint> tmpConstraints = new ArrayList<Constraint>(constraintsToAdd);

            for (Constraint c : tmpConstraints) {
                if (c.getEntityNames().containsKey(entity.getId())) {
                    constraintsToAdd.remove(c);
                }
            }

            constraintsToAdd.add(newConstraint);

            DoubleResult value = bind.FBA(constraintsToAdd, false, true);

            if (Double.isNaN(value.result)) {
                value.result = 0.0;
            }
            result.addLine(entity, value.result);

            if (Vars.verbose) {
                int percent = (int) Math.round((todo - entities.size()) / todo * 100);

                while (nbPrintedStars.intValue() < (percent / 2)) {
                    System.err.print("*");
                    nbPrintedStars.incrementAndGet();
                }
            }

            if (removedConst) {
                bind.getConstraints().add(toRemove);
                bind.getSimpleConstraints().put(entity, toRemove);
            }

        }

//		bind.end();

    }
}
