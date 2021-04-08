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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.heuristic;

import java.util.BitSet;
import java.util.HashMap;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.similarity.SimilarityComputor;

/**
 * The A* heuristic using chemical similarity
 * @author clement
 */
public class ChemicalSimilarityHeuristic implements AStarHeuristic<BioMetabolite> {
	
	private final HashMap<BioMetabolite, BitSet> fingerpMap;
	private final HashMap<BioMetabolite, HashMap<BioMetabolite, Double>> distMap;
	
	/**
	 * Instantiates a new chemical similarity heuristic.
	 */
	public ChemicalSimilarityHeuristic() {
        fingerpMap = new HashMap<>();
        distMap = new HashMap<>();
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.algo.AStarHeuristic#getHeuristicCost(parsebionet.biodata.BioMetabolite, parsebionet.biodata.BioMetabolite)
	 */
	@Override
	public double getHeuristicCost(BioMetabolite node, BioMetabolite end) {
		
		if(distMap.containsKey(node)){
			if(distMap.get(node).containsKey(end)){
				return distMap.get(node).get(end);
			}
		}else{
            distMap.put(node, new HashMap<>());
		}
		
		BitSet fingerprint1= getFingerprint(node);
		BitSet fingerprint2= getFingerprint(end);
		try {
//			return 1-SimilarityComputor.getCosineCoeff(fingerprint1, fingerprint2);
//			return 1-SimilarityComputor.getDiceCoeff(fingerprint1, fingerprint2);
//			return SimilarityComputor.getEuclideanDist(fingerprint1, fingerprint2);
//			return SimilarityComputor.getManhattanDist(fingerprint1, fingerprint2);
			double dist = SimilarityComputor.getSoergelDist(fingerprint1, fingerprint2);
            distMap.get(node).put(end, dist);
			return dist;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return Double.NaN;
		}
	}
	
	private BitSet getFingerprint(BioMetabolite e){
		if(fingerpMap.containsKey(e))return fingerpMap.get(e);
		//BitSet fingerprint=FingerprintBuilder.getMACCSFingerprint(e);
		//BitSet fingerprint=FingerprintBuilder.getKlekotaRothFingerprint(e);
		//BitSet fingerprint=FingerprintBuilder.getPubchemFingerprint(e);
		//BitSet fingerprint=FingerprintBuilder.getEStateFingerprint(e);
		//BitSet fingerprint=FingerprintBuilder.getSubstructureFingerprint(e);
		BitSet fingerprint=FingerprintBuilder.getExtendedFingerprint(e);
        fingerpMap.put(e, fingerprint);
		return fingerprint;
	}

}
