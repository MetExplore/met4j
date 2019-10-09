package fr.inra.toulouse.metexplore.met4j_flux.utils;

import fr.inra.toulouse.metexplore.met4j_core.biodata.*;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;

public class BioReactionUtils {

	/**
	 * Check if a reaction is an exchange reaction, i.e contains
	 * a metabolite with the boundary condition equals to true
	 * @param n
	 * @param r
	 * @return
	 */
	static public Boolean isExchangeReaction(BioNetwork n, BioReaction r) {

		BioCollection<BioReactant> lefts = n.getLeftReactants(r);
		BioCollection<BioReactant> rights = n.getRightReactants(r);

		if (lefts.size() == 0 || rights.size() == 0) {
			return true;
		}

		BioCollection<BioReactant> all = lefts;
		all.addAll(rights);

		for (BioParticipant p : all) {
			BioMetabolite m = (BioMetabolite) p.getPhysicalEntity();

			if (MetaboliteAttributes.getBoundaryCondition(m) != null && MetaboliteAttributes.getBoundaryCondition(m)) {
				return true;
			}

		}

		return false;
	}

}
