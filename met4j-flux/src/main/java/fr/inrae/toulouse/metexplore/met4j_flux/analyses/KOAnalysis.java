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
 * 5 avr. 2013 
 */
package fr.inrae.toulouse.metexplore.met4j_flux.analyses;

import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.KOResult;
import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.RSAAnalysisResult;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Vars;
import fr.inrae.toulouse.metexplore.met4j_flux.interaction.Interaction;
import fr.inrae.toulouse.metexplore.met4j_flux.thread.ResolveThread;
import fr.inrae.toulouse.metexplore.met4j_flux.thread.ThreadKO;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * Class to run a KO analysis.
 * 
 * @author lmarmiesse 5 avr. 2013
 * 
 */
public class KOAnalysis extends Analysis {

	/**
	 * 
	 * 0 : the KO is performed on reactions. 1 : the KO is performed on genes.
	 */
	protected int mode;

	/**
	 * 
	 * Entities to run the KO analysis on.
	 * 
	 */
	protected BioCollection<?> entities;

	protected BioCollection<BioEntity> entitiesInInteractionNetwork = new BioCollection<BioEntity>();

	/**
	 * List containing the threads that will knock out each entity.
	 * 
	 */
	protected List<ResolveThread> threads = new ArrayList<ResolveThread>();

	/**
	 * Constructor
	 * 
	 * @param b        : Bind
	 * @param mode     O:reactions, 1:genes
	 * @param entities : list of entities to take into account
	 */
	public KOAnalysis(Bind b, int mode, BioCollection<?> entities) {
		super(b);
		this.mode = mode;
		this.entities = entities;
	}

	public KOResult runAnalysis() {

		double startTime = System.currentTimeMillis();

		KOResult koResult = new KOResult();

		BioCollection<?> entitiesMap;

		if (entities == null) {

			if (mode == 0) {
				entitiesMap = b.getBioNetwork().getReactionsView();
			} else {
				entitiesMap = b.getBioNetwork().getGenesView();
			}

		} else {
			entitiesMap = entities;
		}

		// ///////this part is to optimize a ko analysis, not to look for the
		// steady states of
		// the interaction network when the entity is not in it

		for (BioEntity targetEnt : b.getInteractionNetwork().getTargetToInteractions().keySet()) {

			for (Interaction i : b.getInteractionNetwork().getTargetToInteractions().get(targetEnt)
					.getConditionalInteractions()) {

				for (BioEntity ent : i.getCondition().getInvolvedEntities()) {
					if (entitiesMap.containsId(ent.getId())) {
						entitiesInInteractionNetwork.add(ent);
					}
				}
				for (BioEntity ent : i.getConsequence().getInvolvedEntities()) {
					if (entitiesMap.containsId(ent.getId())) {
						entitiesInInteractionNetwork.add(ent);
					}
				}
			}
		}

		RSAAnalysis ssa = new RSAAnalysis(b.getInteractionNetwork(), b.getSimpleConstraints());

		RSAAnalysisResult res = ssa.runAnalysis();

		List<Constraint> interactionNetworkConstraints = res.getSteadyStateConstraints();
		// ////////////////

		Queue<BioEntity> tasks = new LinkedBlockingQueue<BioEntity>();

		for (BioEntity e : entitiesMap) {
			tasks.add(e);
		}

		for (int j = 0; j < Vars.maxThread; j++) {

			Bind newBind = null;

			try {
				newBind = b.copy();

			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				System.exit(1);
			}

			ThreadKO threadKo = new ThreadKO(newBind, tasks, koResult, b.getObjective(), entitiesInInteractionNetwork,
					interactionNetworkConstraints);

			threads.add(threadKo);
		}

		if (Vars.verbose) {
			System.err.println("Progress : ");

			System.err.print("[");
			for (int i = 0; i < 50; i++) {
				System.err.print(" ");
			}
			System.err.print("]\n");
			System.err.print("[");
		}

		for (ResolveThread thread : threads) {
			thread.start();
		}

		for (ResolveThread thread : threads) {

			// permits to wait for the threads to end
			try {
				thread.join();
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}

		}
		if (Vars.verbose) {
			System.err.print("]\n");
		}

		while (threads.size() > 0) {
			threads.remove(0);
		}

		if (Vars.verbose) {
			System.err.println("KO over " + ((System.currentTimeMillis() - startTime) / 1000) + "s " + Vars.maxThread
					+ " threads");
		}
		return koResult;
	}
}