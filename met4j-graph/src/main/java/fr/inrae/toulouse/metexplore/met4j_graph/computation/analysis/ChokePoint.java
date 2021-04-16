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
package fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis;

import java.util.HashSet;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

/**
 * Class to compute choke points, i.e. reaction that are required to consume or produce one compound.
 * Targeting of choke point can lead to the accumulation or the loss of some metabolites, thus choke points constitute an indicator of lethality and can help identifying drug target
 * See : Syed Asad Rahman, Dietmar Schomburg; Observing local and global properties of metabolic pathways: ‘load points’ and ‘choke points’ in the metabolic networks. Bioinformatics 2006; 22 (14): 1767-1774. doi: 10.1093/bioinformatics/btl181
 *
 * @author clement
 * @version $Id: $Id
 */
public class ChokePoint {

	/**
	 * Compute choke points from compound graph
	 *
	 * @param g the graph
	 * @return set of choke points
	 */
	public static HashSet<BioReaction> getChokePoint(CompoundGraph g){
		HashSet<BioReaction> chokePoints = new HashSet<>();
		for(BioMetabolite v : g.vertexSet()){
			Set<ReactionEdge> in = g.incomingEdgesOf(v);
			Set<ReactionEdge> out = g.outgoingEdgesOf(v);
			if(in.size() == 1 ){
				ReactionEdge choke = in.iterator().next();
				chokePoints.add(choke.getReaction());
			}else if(out.size() == 1 ){
				ReactionEdge choke = out.iterator().next();
				chokePoints.add(choke.getReaction());
			}
		}
		
		return chokePoints;
	}
	
	/**
	 * Compute choke points from bipartite graph
	 *
	 * @param g the graph
	 * @return set of choke points
	 */
	public static HashSet<BioReaction> getChokePoint(BipartiteGraph g){
		HashSet<BioReaction> chokePoints = new HashSet<>();
		for(BioEntity v : g.vertexSet()){
			if(v instanceof BioMetabolite){
				Set<BioEntity> in = g.predecessorListOf(v);
				Set<BioEntity> out = g.successorListOf(v);
				if(in.size() == 1 ){
					BioReaction choke = (BioReaction) in.iterator().next();
					chokePoints.add(choke);
				}else if(out.size() == 1 ){
					BioReaction choke = (BioReaction) out.iterator().next();
					chokePoints.add(choke);
				}
			}
		}
		return chokePoints;
	}

}
