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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;

/**
 * The Class DegreeWeightPolicy which set as edge weight the target node's degree to the power of n.
 *
 * @author clement
 * @version $Id: $Id
 */
public class DegreeWeightPolicy extends WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> {
	
	/** The exponent. */
	double pow = 2;
	
	/**
	 * Instantiates a new degree weight policy, which set as edge weight the square of the target node's degree
	 */
	public DegreeWeightPolicy() {}
	
	/**
	 * Instantiates a new degree weight policy, which set as edge weight the target node's degree to the power of n
	 *
	 * @param n the exponent
	 */
	public DegreeWeightPolicy(double n) {
        this.pow =n;
	}

	/* (non-Javadoc)
	 * @see parsebionet.computation.graphe.WeightingPolicy#setWeight(parsebionet.computation.graphe.CompoundGraph)
	 */
	/** {@inheritDoc} */
	@Override
	public void setWeight(CompoundGraph g) {
		for(ReactionEdge e : g.edgeSet()){
			double weight = g.outDegreeOf(e.getV2());
			weight += g.inDegreeOf(e.getV2());
			g.setEdgeWeight(e, StrictMath.pow(weight, this.pow));
		}
	}

}
