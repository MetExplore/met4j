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

package fr.inrae.toulouse.metexplore.met4j_flux.analyses;

import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.RSAAnalysisResult;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inrae.toulouse.metexplore.met4j_flux.interaction.Interaction;
import fr.inrae.toulouse.metexplore.met4j_flux.interaction.InteractionNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;

import java.util.*;

public class RSAAnalysis extends Analysis {

	private InteractionNetwork intNet;

	private List<Map<BioEntity, Integer>> statesList = new ArrayList<Map<BioEntity, Integer>>();

	private List<Map<BioEntity, Integer>> attractorStatesList = new ArrayList<Map<BioEntity, Integer>>();

	private List<Constraint> finalConstraints = new ArrayList<Constraint>();

	private Map<BioEntity, Constraint> simpleConstraints = new HashMap<BioEntity, Constraint>();

	/**
	 * Maximal number of iterations to find a steady state in the regulatory
	 * network.
	 */
	private int steadyStatesIterations = 10000;

	public RSAAnalysis(InteractionNetwork intNetwork, Map<BioEntity, Constraint> simpleConstraints) {
		super(null);
		this.intNet = intNetwork;
		this.simpleConstraints = simpleConstraints;

	}

	@Override
	public RSAAnalysisResult runAnalysis() {

		RSAAnalysisResult res = new RSAAnalysisResult();

		if (intNet.getTargetToInteractions().isEmpty() && intNet.getInitialConstraints().isEmpty()
				&& intNet.getInitialStates().isEmpty()) {
			return res;
		}

		List<BioEntity> entitiesToCheck = new ArrayList<BioEntity>();
		entitiesToCheck.addAll(intNet.getTargetToInteractions().keySet());

		for (BioEntity ent : intNet.getInitialStates().keySet()) {
			res.addResultEntity(ent);
		}

		// we set the values for the variables in the first state
		Map<BioEntity, Integer> thisState = new HashMap<BioEntity, Integer>(intNet.getInitialStates());

		for (BioEntity b : intNet.getInitialConstraints().keySet()) {

			// If the entity is in the regulatory network
			if (intNet.getInteractionNetworkEntities().containsKey(b.getId())) {

				// TRANSLATION
				if (intNet.canTranslate(b)) {
					thisState.put(b, intNet.getStateFromValue(b, intNet.getInitialConstraints().get(b).getLb()));
				} else {

					int stateMin = intNet.getInteractionNetworkEntitiesStates().get(b)[0];

					int stateMax = intNet.getInteractionNetworkEntitiesStates().get(b)[1];

					double value = intNet.getInitialConstraints().get(b).getLb();

					// If the value is an integer AND is between min and max
					// states
					if (value <= stateMax && value >= stateMin && value == Math.floor(value)) {

						thisState.put(b, (int) value);

					} else {
						System.err.println(
								"Error : no translation available for variable " + b.getId() + " and value " + value);
						System.exit(0);
					}

				}

				// if this entity had a simple constraint, but not fix (ub!=lb)
				// we
				// overwrite it

				if (simpleConstraints.containsKey(b)) {
					if (simpleConstraints.get(b).getLb() != simpleConstraints.get(b).getUb()) {

						if (intNet.canTranslate(b)) {
							thisState.put(b,
									intNet.getStateFromValue(b, intNet.getInitialConstraints().get(b).getLb()));
						} else {
							int stateMin = intNet.getInteractionNetworkEntitiesStates().get(b)[0];

							int stateMax = intNet.getInteractionNetworkEntitiesStates().get(b)[1];

							double value = simpleConstraints.get(b).getLb();

							// If the value is an integer AND is between min and
							// max
							// states
							if (value <= stateMax && value >= stateMin && value == Math.floor(value)) {

								thisState.put(b, (int) value);

							} else {
								System.err.println("Error : no translation available for variable " + b.getId()
										+ " and value " + value);
								System.exit(0);
							}
						}
					}
				}
			}
		}

		for (BioEntity b : simpleConstraints.keySet()) {

			// If the entity is in the regulatory network
			if (intNet.getInteractionNetworkEntities().containsKey(b.getId())) {

				// if the entity is already set by a constraint, we remove the
				// interactions that have this entity as a target
				if (simpleConstraints.get(b).getLb() == simpleConstraints.get(b).getUb()) {

					// TRANSLATION
					if (intNet.canTranslate(b)) {

						thisState.put(b, intNet.getStateFromValue(b, simpleConstraints.get(b).getLb()));
					} else {
						int stateMin = intNet.getInteractionNetworkEntitiesStates().get(b)[0];

						int stateMax = intNet.getInteractionNetworkEntitiesStates().get(b)[1];

						double value = simpleConstraints.get(b).getLb();

						// If the value is an integer AND is between min and
						// max
						// states
						if (value <= stateMax && value >= stateMin && value == Math.floor(value)) {

							thisState.put(b, (int) value);

						} else {
							System.err.println("Error : no translation available for variable " + b.getId()
									+ " and value " + value);
							System.exit(0);
						}
					}

					if (intNet.getTargetToInteractions().containsKey(b)) {
						entitiesToCheck.remove(b);
					}
				}
			}
		}
		//

		int attractorSize = 0;

		for (int it = 1; it < steadyStatesIterations; it++) {
			statesList.add(thisState);

			// we copy the previous state
			Map<BioEntity, Integer> newState = new HashMap<BioEntity, Integer>();
			for (BioEntity b : thisState.keySet()) {
				newState.put(b, thisState.get(b));
			}

			Map<Constraint, double[]> newtStepConstraints = goToNextInteractionNetworkState(thisState, entitiesToCheck);

			// we update the values
			for (Constraint c : newtStepConstraints.keySet()) {
				newState.put((BioEntity) c.getEntities().keySet().toArray()[0], (int) Math.round(c.getLb()));
			}

			thisState = newState;

			// /////We check that this step has not already been achieved
			boolean areTheSame = false;
			for (Map<BioEntity, Integer> previousStep : statesList) {
				areTheSame = true;
				// compare "thisStepSimpleConstraints" with "previousStep"
				// They have to be exactly the same

				// the same size
				if (thisState.size() == previousStep.size()) {

					for (BioEntity b : thisState.keySet()) {
						if (previousStep.containsKey(b)) {

							if (thisState.get(b) != previousStep.get(b)) {
								areTheSame = false;
							}

						} else {
							areTheSame = false;
						}
					}
				} else {
					areTheSame = false;
				}

				if (areTheSame) {
					attractorSize = it - statesList.indexOf(previousStep);

					for (int index = statesList.indexOf(previousStep); index < it; index++) {
						attractorStatesList.add(statesList.get(index));
					}

					break;
				}

			}

			if (areTheSame) {
				// if (Vars.verbose) {
				// System.err.println("Steady state found in " + (it)
				// + " iterations.");
				// System.err.println("Attractor size : " + attractorSize);
				// }
				break;
			}

		}

		statesList.add(thisState);

		Map<BioEntity, Double> meanAttractorStates = new HashMap<BioEntity, Double>();

		if (attractorStatesList.size() != 0) {

			for (BioEntity b : attractorStatesList.get(0).keySet()) {

				// If it is an external metab, we set a constraint
				boolean isExtMetab = false;

				if (b.getClass().equals(BioMetabolite.class)) {
					BioMetabolite metab = (BioMetabolite) b;
					// If it is external
					if (MetaboliteAttributes.getBoundaryCondition(metab)) {
						isExtMetab = true;
					}
				}

				// if (intNet.getTargetToInteractions().containsKey(b)
				// || isExtMetab) {

				// We make the average of the values of all states of the
				// attractor
				double lb = 0;
				double ub = 0;

				/**
				 * Mean state value
				 */
				double meanStateValue = 0;

				for (int nb = 0; nb < attractorStatesList.size(); nb++) {

					meanStateValue += attractorStatesList.get(nb).get(b);

					if (intNet.canTranslate(b)) {
						lb += intNet.getConstraintFromState(b, attractorStatesList.get(nb).get(b)).getLb();
						ub += intNet.getConstraintFromState(b, attractorStatesList.get(nb).get(b)).getUb();
					} else {
						lb += attractorStatesList.get(nb).get(b);
						ub += attractorStatesList.get(nb).get(b);
					}

				}

				meanStateValue = meanStateValue / attractorStatesList.size();

				meanAttractorStates.put(b, meanStateValue);

				lb = lb / attractorStatesList.size();
				ub = ub / attractorStatesList.size();

				Map<BioEntity, Double> constMap = new HashMap<BioEntity, Double>();
				constMap.put(b, 1.0);
				finalConstraints.add(new Constraint(constMap, lb, ub));
				// }

			}

		} else {
			System.err.println(
					"Warning, no regulatory network attractor was found in " + steadyStatesIterations + " iterations.");

		}
		res.setStatesList(statesList);
		res.setAttractorStatesList(attractorStatesList);

		res.setMeanAttractorStates(meanAttractorStates);

		// ////TRANSLATION

		res.setSteadyStateConstraints(finalConstraints);

		// System.out.println("Attractor size : "+attractorSize);

		return res;

	}

	public Map<Constraint, double[]> goToNextInteractionNetworkState(Map<BioEntity, Integer> networkState,
			List<BioEntity> entitiesToCheck) {
		Map<BioEntity, Constraint> netConstraints = new HashMap<BioEntity, Constraint>();
		for (BioEntity ent : networkState.keySet()) {

			netConstraints.put(ent,
					new Constraint(ent, (double) networkState.get(ent), (double) networkState.get(ent)));
		}

		Map<Constraint, double[]> contToTimeInfos = new HashMap<Constraint, double[]>();

		Map<BioEntity, Constraint> nextStepState = new HashMap<BioEntity, Constraint>();

		Set<BioEntity> setEntities = new HashSet<BioEntity>();

		for (BioEntity entity : entitiesToCheck) {

			for (Interaction i : intNet.getTargetToInteractions().get(entity).getConditionalInteractions()) {

				if (i.getCondition().isTrue(netConstraints)) {

					// we go through all the consequences (there should be only
					// one)
					if (intNet.getInteractionToConstraints().containsKey(i)) {
						for (Constraint consequence : this.intNet.getInteractionToConstraints().get(i)) {

							// we check it's a simple constraint
							if (consequence.getEntities().size() == 1) {
								for (BioEntity ent : consequence.getEntities().keySet()) {
									if (consequence.getEntities().get(ent) == 1.0) {

										contToTimeInfos.put(consequence, i.getTimeInfos());
										nextStepState.put(ent, consequence);

										setEntities.add(ent);
									}
								}
							}
						}
					}
					break;
				}
			}
		}

		for (BioEntity entity : entitiesToCheck) {
			if (!setEntities.contains(entity)) {

				Interaction defaultInt = intNet.getTargetToInteractions().get(entity).getdefaultInteraction();

				// we go through all the consequences (there should be only
				// one)
				for (Constraint consequence : defaultInt.getConsequence().createConstraints()) {

					for (BioEntity ent : consequence.getEntities().keySet()) {
						if (consequence.getEntities().get(ent) == 1.0) {
							contToTimeInfos.put(consequence, defaultInt.getTimeInfos());
							nextStepState.put(ent, consequence);

							setEntities.add(ent);
						}
					}

				}

			}
		}

		Map<Constraint, double[]> steadyStateConstraints = new HashMap<Constraint, double[]>();

		for (BioEntity ent : nextStepState.keySet()) {

			if (intNet.getTargetToInteractions().containsKey(ent)) {
				steadyStateConstraints.put(nextStepState.get(ent), contToTimeInfos.get(nextStepState.get(ent)));
			}
		}

		return steadyStateConstraints;
	}

}