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

import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.DRResult;
import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.RSAAnalysisResult;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Vars;
import fr.inrae.toulouse.metexplore.met4j_flux.thread.ResolveThread;
import fr.inrae.toulouse.metexplore.met4j_flux.thread.ThreadFVA;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 
 * This class performs a dead reactions analysis.
 * 
 * @author lmarmiesse 5 avr. 2013
 * 
 */
public class DRAnalysis extends Analysis {

	protected List<ResolveThread> threads = new ArrayList<ResolveThread>();

	/**
	 * 
	 * Maximal flux value to consider a reaction dead.
	 * 
	 */
	protected double minValue = 0.000001;

	public DRAnalysis(Bind b, double d) {
		super(b);
		minValue = d;
	}

	public DRResult runAnalysis() {

		double startTime = System.currentTimeMillis();

		List<Constraint> constraintsToAdd = new ArrayList<Constraint>();
		// we add the constraints corresponding to the interactions


		RSAAnalysis ssa = new RSAAnalysis(b.getInteractionNetwork(),b.getSimpleConstraints());
		RSAAnalysisResult res = ssa.runAnalysis();
		
		
		for (Constraint c : res.getSteadyStateConstraints()) {
			constraintsToAdd.add(c);
		}

		b.getConstraints().addAll(constraintsToAdd);

		DRResult drResult = new DRResult(0.0, b);
		
		Map<String, BioEntity> FVAMap = new HashMap<String, BioEntity>();

		for (String reactionId : b.getBioNetwork()
				.getReactionsView().getIds()) {
			FVAMap.put(reactionId, b.getBioNetwork()
					.getReactionsView().get(reactionId));
		}

		// one queue to minimize and the other to maximize
		Queue<BioEntity> entQueue = new LinkedBlockingQueue<BioEntity>();
		Queue<BioEntity> entQueueCopy = new LinkedBlockingQueue<BioEntity>();

		for (String entName : FVAMap.keySet()) {
			entQueue.add(FVAMap.get(entName));
			entQueueCopy.add(FVAMap.get(entName));
		}

		for (int j = 0; j < Vars.maxThread; j++) {
			
			Bind newBind = null;
			
			try {
				newBind = b.copy();
			} catch (ClassNotFoundException | NoSuchMethodException
					| SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			ThreadFVA threadFva = new ThreadFVA(newBind, entQueue, entQueueCopy, drResult);
			
			threads.add(threadFva);
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

		// we remove the threads to permit another analysis
		while (threads.size() > 0) {
			threads.remove(0);
		}

		// we remove the constraints that sets the objective and interactions
		// to permit other analysis
		b.getConstraints().removeAll(constraintsToAdd);

		if (Vars.verbose) {
			System.err.println("DR over "
					+ ((System.currentTimeMillis() - startTime) / 1000) + "s "
					+ Vars.maxThread + " threads");
		}
		drResult.clean(minValue);
		
		drResult.addPrunedReactions();

		return drResult;

	}

}