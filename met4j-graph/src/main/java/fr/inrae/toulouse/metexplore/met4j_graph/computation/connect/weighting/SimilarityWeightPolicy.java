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

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollections;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.similarity.SimilarityComputor;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The Chemical Similarity weighting policy.
 *
 * @author clement
 * @version $Id: $Id
 */
public class SimilarityWeightPolicy extends WeightingPolicy<BioMetabolite,ReactionEdge,CompoundGraph> {
	
	private int fingerprintType;
	private boolean weightByMassContribution;
	private boolean useDist;
	private BioMetabolite global;
	/** Constant <code>DEFAULT_FINGERPRINT=FingerprintBuilder.EXTENDED</code> */
	public static final int DEFAULT_FINGERPRINT = FingerprintBuilder.EXTENDED;
	
	/**
	 * Instantiates a new similarity weight policy using default fingerprint type
	 */
	public SimilarityWeightPolicy() {
        this.fingerprintType = DEFAULT_FINGERPRINT;
	}
	
	/**
	 * Instantiates a new similarity weight policy using specified fingerprint type
	 *
	 * @see FingerprintBuilder
	 * @param fingerprintType a type of fingerprint.
	 */
	public SimilarityWeightPolicy(int fingerprintType) {
		this.fingerprintType=fingerprintType;
	}
	
	/**
	 * <p>Constructor for SimilarityWeightPolicy.</p>
	 *
	 * @param fingerprintType a int.
	 * @param weightByMassContribution a boolean.
	 * @param useDist a boolean.
	 */
	public SimilarityWeightPolicy(int fingerprintType, boolean weightByMassContribution, boolean useDist) {
		this.fingerprintType=fingerprintType;
		this.weightByMassContribution=weightByMassContribution;
		this.useDist=useDist;
	}
	
	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.WeightingPolicy#setWeight(parsebionet.applications.graphe.BioGraph)
	 */
	/** {@inheritDoc} */
	@Override
	public void setWeight(CompoundGraph g) {
//		noStructFilter(g);
		FingerprintBuilder fingerprinter = new FingerprintBuilder(fingerprintType);
		HashMap<String, BitSet> fingerPrintMap = new HashMap<>();
		for(ReactionEdge e:g.edgeSet()){
			
			//extract compound
			BioMetabolite cpd1 = e.getV1();
			BioMetabolite cpd2 = e.getV2();
			
			//computing finger-print
			if (!fingerPrintMap.containsKey(cpd1.getId())){
				BitSet fingerprint=fingerprinter.getFingerprint(cpd1);
				fingerPrintMap.put(cpd1.getId(), fingerprint);
			}
			if (!fingerPrintMap.containsKey(cpd2.getId())){
				BitSet fingerprint=fingerprinter.getFingerprint(cpd2);
				fingerPrintMap.put(cpd2.getId(), fingerprint);
			}
	
			BitSet fingerprint1 = fingerPrintMap.get(cpd1.getId());
			BitSet fingerprint2 = fingerPrintMap.get(cpd2.getId());
	
			//computing similarity
			if((fingerprint1 != null) && (fingerprint2 != null)){
	
				try {
					double sim = SimilarityComputor.getTanimoto(fingerprint1, fingerprint2);
					if(weightByMassContribution){
						double pamc = getMassContribution(e);
						if(!Double.isNaN(pamc)){
							sim*=pamc;
						}else{
							System.err.println("Error computing mass contribution for "+cpd1+" and "+cpd2);
							sim = -1.0;
						}
					}
//					
					if(global !=null){
						double sim2 = SimilarityComputor.getTanimoto(fingerPrintMap.get(global.getId()), fingerprint2);
						sim=(sim+sim2)/2;
					}
//					
					if(useDist && sim != -1.0){
						sim=1-sim;
					}
					g.setEdgeWeight(e,sim);
						
				} catch (IllegalArgumentException exc) {
					System.err.println("Error computing similarity between "+cpd1+" and "+cpd2);
					exc.printStackTrace();
					g.setEdgeWeight(e,-1.0);
				} 
			}else{
				g.setEdgeWeight(e,-1.0);
			}
		}
		
	}

	
	/**
	 * Filter the nodes without structural information
	 *
	 * @param g the graph
	 */
	public void noStructFilter(CompoundGraph g){
		Set<ReactionEdge> edgesToRemove = new HashSet<>();
		for (ReactionEdge e:g.edgeSet()){
			if(g.getEdgeWeight(e)==0.0){
				edgesToRemove.add(e);
			}else if(g.getEdgeWeight(e)==-1.0){
				for (ReactionEdge e2:g.edgeSet()){
					if(e.toString().equals(e2.toString())){
						edgesToRemove.add(e2);
					}
				}
				edgesToRemove.add(e);
			}
		}
		System.err.println(edgesToRemove.size()+" edges removed due to missing similarity");
		g.removeAllEdges(edgesToRemove);
		g.removeIsolatedNodes();
	}
	
	/**
	 * get the percentage atomic mass contribution (PAMC) defined as hundred times the sum of mass for source and target
	 * divided by the total mass of the metabolites in that reaction.
	 * 
	 * @param e the edge
	 * @return the percentage atom mass contribution
	 */
	private double getMassContribution(ReactionEdge e){
		try{
			if(e.getV1().getMolecularWeight() != null && e.getV2().getMolecularWeight() != null){
				double massSum = e.getV1().getMolecularWeight()+e.getV2().getMolecularWeight();
				double massReactionSum = 0.0;
				for(BioMetabolite p : BioCollections.union(e.getReaction().getLeftsView(),e.getReaction().getRightsView())){
					if(p.getMolecularWeight()==null) return Double.NaN;
					massReactionSum+=p.getMolecularWeight();
				}
				return (100*massSum)/massReactionSum;
			}else{
				return Double.NaN;
			}
		}catch(NumberFormatException ex){
			ex.printStackTrace();
			return Double.NaN;
		}
	}
	
	/**
	 * set weight as the product of chemical similarity and percentage atomic mass contribution (PAMC)
	 *
	 * @param weightByMassContribution a boolean.
	 */
	public void weightByMassContribution(boolean weightByMassContribution) {
		this.weightByMassContribution = weightByMassContribution;
	}
	
	/**
	 * Convert similarity as a distance (invert of the similarity)
	 *
	 * @param useDist a boolean.
	 */
	public void useDistance(boolean useDist) {
		this.useDist = useDist;
	}
	
	/**
	 * <p>useGlobalSimilarity.</p>
	 *
	 * @param start a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite} object.
	 */
	public void useGlobalSimilarity(BioMetabolite start) {
        this.global = start;
	}

	/**
	 * <p>Setter for the field <code>fingerprintType</code>.</p>
	 *
	 * @param fingerprintType a int.
	 */
	public void setFingerprintType(int fingerprintType) {
		this.fingerprintType = fingerprintType;
	}

	/**
	 * <p>Getter for the field <code>fingerprintType</code>.</p>
	 *
	 * @return a int.
	 */
	public int getFingerprintType() {
		return fingerprintType;
	}

	/**
	 * <p>isWeightByMassContribution.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isWeightByMassContribution() {
		return weightByMassContribution;
	}

	/**
	 * <p>isUseDist.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isUseDist() {
		return useDist;
	}

}
