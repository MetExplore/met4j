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
 * 13 mars 2013 
 */
package fr.inrae.toulouse.metexplore.met4j_flux.thread;

import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.FVAResult;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Vars;
import fr.inrae.toulouse.metexplore.met4j_flux.objective.Objective;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * Thread to perform an FVA analysis.
 * 
 * @author lmarmiesse 13 mars 2013
 * 
 */
public class ThreadFVA extends ResolveThread {

	/**
	 * Number of entities to treat.
	 */
	private double todo;

	/**
	 * Contains all entities to treat.
	 */
	private Queue<BioEntity> entities;
	private Queue<BioEntity> entitiesCopy = new LinkedBlockingQueue<BioEntity>();

	/**
	 * The FVA result.
	 */
	private FVAResult result;

	/**
	 * 
	 * For the output Thread safe integer
	 * 
	 */
	private static AtomicInteger nbPrintedStars = new AtomicInteger(0);

	public ThreadFVA(Bind b, Queue<BioEntity> ents, Queue<BioEntity> entsCopy, FVAResult result) {
		super(b);
		this.todo = ents.size();
		this.entities = ents;
		this.entitiesCopy = entsCopy;
		this.result = result;
	}

	/**
	 * Starts the thread.
	 */
	public void run() {
		bind.setObjective(new Objective());
		bind.makeSolverObjective();

		// we do all the minimize
		bind.setObjSense(false);

		BioEntity entity;

		while ((entity = entities.poll()) != null) {

			bind.changeObjVarValue(entity, 1.0);
			result.setMin(entity, bind.FBA(new ArrayList<Constraint>(), false, false).result);
			bind.changeObjVarValue(entity, 0.0);

			if (Vars.verbose) {

				int percent = (int) Math.round(((todo - entities.size()) / todo) * 50);

				while (nbPrintedStars.intValue() < (percent / 2)) {
					System.err.print("*");
					nbPrintedStars.incrementAndGet();
				}
			}

		}
		// and all the maximize
		bind.setObjSense(true);

		while ((entity = entitiesCopy.poll()) != null) {
			bind.changeObjVarValue(entity, 1.0);
			result.setMax(entity, bind.FBA(new ArrayList<Constraint>(), false, false).result);
			bind.changeObjVarValue(entity, 0.0);

			if (Vars.verbose) {

				int percent = (int) Math.round(((todo - entitiesCopy.size()) / todo) * 50) + 50;

				while (nbPrintedStars.intValue() < (percent / 2)) {
					System.err.print("*");
					nbPrintedStars.incrementAndGet();
				}

			}

		}

		bind.clearAll();
		bind.end();

		nbPrintedStars = new AtomicInteger(0);
	}

}
