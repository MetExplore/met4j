/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
/**
 * Baobab Team
 * 6 sept. 07
 * Project : parseBioNet
 * Package : baobab.parseBioNet.applications
 * File : ConnectedComponents.java
 * 
 * Input : a sbml file
 * Output : the distributions of the nodes (reactionNodes and compounds) 
 * in the connected components
 */
package fr.inra.toulouse.metexplore.met4j_graph.computation.analysis;

import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;


// TODO: Auto-generated Javadoc
/**
 * The Class ConnectedComponents.
 */
public class ConnectedComponents {
	
	/** The network. */
	BioNetwork network;
	
	/** The distrib. */
	HashMap<String, Integer> distrib;
	
	/** The number. */
	int number;
	
	/**
	 * Instantiates a new connected components.
	 *
	 * @param network the network
	 */
	public ConnectedComponents(BioNetwork network) {
		this.setNetwork(network);
		
	}
	
	
	/**
	 * Computes the.
	 */
	public void compute() {
		this.setDistrib(this.getNetwork().clusterDistribution());
		this.setNumber();
		
	}
	
	/**
	 * Sets the number.
	 */
	private void setNumber() {
		int max=0;
		for(String id : this.getDistrib().keySet()) {
			if(this.getDistrib().get(id) > max)
				max = this.getDistrib().get(id);
		}
		
		this.number =  max + 1;
		
	}
	
	/**
	 * Prints the.
	 */
	public void print() {
		System.out.println("Number of connected components : "+this.getNumber());
		for(int i=0; i<this.getNumber();i++) {
			for(String id : distrib.keySet()) {
				if(distrib.get(id) == i) {
					System.out.print(id+"\t");
					if(this.getNetwork().getPhysicalEntityList().keySet().contains(id)) {
						System.out.println("cpd\t"+i);
					}
					else {
						System.out.println("rxn\t"+i);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the network.
	 *
	 * @return the network
	 */
	public BioNetwork getNetwork() {
		return network;
	}
	
	/**
	 * Sets the network.
	 *
	 * @param network the new network
	 */
	public void setNetwork(BioNetwork network) {
		this.network = network;
	}
	
	/**
	 * Gets the distrib.
	 *
	 * @return the distrib
	 */
	public HashMap<String, Integer> getDistrib() {
		return distrib;
	}
	
	/**
	 * Sets the distrib.
	 *
	 * @param distrib the distrib
	 */
	public void setDistrib(HashMap<String, Integer> distrib) {
		this.distrib = distrib;
	}


	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

}
