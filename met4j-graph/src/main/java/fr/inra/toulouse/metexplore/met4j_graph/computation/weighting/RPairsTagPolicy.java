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
package fr.inra.toulouse.metexplore.met4j_graph.computation.weighting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;

/**
 * Use KEGG's RPair tag ("main","trans","cofac","ligase" and "leave") as weight.
 * Vertex without KEGG id will be removed from graph.
 * @author clement
 **/
@Deprecated
public class RPairsTagPolicy extends RPairsWeightPolicy{

	
	double mainWeight = 1.0;
	double transWeight = 5.0;
	double cofacWeight = 10.0;
	double ligaseWeight = 15.0;
	double leaveWeight = 20.0;
	
	public RPairsTagPolicy() {
		super();
	}
	
	/**
	 * Gets the pair type parsing RPair entry from KEGG REST API.
	 * Types define the role of the reactant pair in the reaction:
	 * "main","trans","cofac","ligase" and "leave"
	 * 
	 * @param rPairId the RPair idenfier
	 * @return the type
	 */
	public String getPairType(String rPairId){
        URL entry;
        BufferedReader in;
        
		try {
			entry = new URL(RPairsWeightPolicy.KEGG_REST_URL+"get/"+rPairId);
			
			String regex=".*TYPE\\s+(\\w+).*";
			
			in = new BufferedReader(
			new InputStreamReader(entry.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				Matcher m=Pattern.compile(regex).matcher(inputLine);
				if(m.matches()){
					String type = m.group(1);
					in.close();
					return type;	
				}
			}         
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * convert tag to weight.
	 * Default is 1 for "main", 5 for "trans", 10 for "cofac", 15 for "ligase", 20 for "leave".
	 * (Weighting from Faust et al. 2009)
	 * @param tag
	 * @return the weight
	 */
	public double tagToWeight(String tag){
		if(tag==null){
			return Double.NaN;
		}
		
		switch (tag){
			case "main" : return this.getMainWeight();
			case "trans" : return this.getTransWeight();
			case "cofac" : return this.getCofacWeight();
			case "ligase" : return this.getLigaseWeight();
			case "leave" : return this.getLeaveWeight();
			default : return Double.NaN;
		}
	}
		
	/**
	 * Set weight for a single edge. Use tag from RPairs as weight
	 * @param e the edge to set
	 */
	@Override
	public void setEdgeWeight(ReactionEdge e){
		BioPhysicalEntity v1 = e.getV1();
		BioPhysicalEntity v2 = e.getV2();
		String v1Id = v1.getId();
		String v2Id = v2.getId();
		Set<BioRef> v1Refs,v2Refs;
		String rPairKey = v1Id+v2Id;
		if(rPairMap.containsKey(rPairKey)){
			//RPairs entry already seen 
			g.setEdgeWeight(e, rPairMap.get(rPairKey));
			return ;
		}else{
			v1Refs = v1.getRefs("KEGG.COMPOUND");
			v2Refs = v2.getRefs("KEGG.COMPOUND");
			for(BioRef keggEntry1 : v1Refs){
				for(BioRef keggEntry2 : v2Refs){
					
					//skip pairs involving lonely proton
					if(keggEntry1.getId().matches("C00080")||keggEntry2.getId().matches("C00080")){
						g.setEdgeWeight(e, Double.NaN);
						rPairMap.put(rPairKey, Double.NaN);
						return;
					}
					
					//transport reaction case
					if(keggEntry1.getId().matches(keggEntry2.getId())){
						g.setEdgeWeight(e, Double.NaN);
						return; 
					}
					
					//get RPAIRS id from KEGG ids using KEGG REST API
					String rPairId = getRPairEntry(keggEntry1.getId(),keggEntry2.getId());
					if(rPairId!=null){
						//get tag from RPair id using KEGG REST API
						String tag = getPairType(rPairId);
						if(tag!=null){
							double weight = tagToWeight(tag);
							g.setEdgeWeight(e, weight);
							rPairMap.put(rPairKey, weight);
						}
						return;
					}
				}
			}
		}
		
		if(e.getReaction().isReversible()){
			//reversible reaction case
			for(BioRef keggEntry1 : v2Refs){
				for(BioRef keggEntry2 : v1Refs){
					//get RPAIRS id from KEGG ids using KEGG REST API
					String rPairId = getRPairEntry(keggEntry1.getId(),keggEntry2.getId());
					if(rPairId!=null){
						//get tag from RPair id using KEGG REST API
						String tag = getPairType(rPairId);
						if(tag!=null){
							double weight = tagToWeight(tag);
							g.setEdgeWeight(e, weight);
							rPairMap.put(rPairKey, weight);
						}
						return;
					}
				}
			}
		}
		
		//no rpair found
		g.setEdgeWeight(e, Double.NaN);
		rPairMap.put(rPairKey, Double.NaN);
		return;
	}

	/**
	 * @return the main tag weight
	 */
	public double getMainWeight() {
		return mainWeight;
	}

	/**
	 * @param mainWeight the main tag weight to set
	 */
	public void setMainWeight(double mainWeight) {
		this.mainWeight = mainWeight;
	}

	/**
	 * @return the trans tag weight
	 */
	public double getTransWeight() {
		return transWeight;
	}

	/**
	 * @param transWeight the trans tag weight to set
	 */
	public void setTransWeight(double transWeight) {
		this.transWeight = transWeight;
	}

	/**
	 * @return the cofac tag weight
	 */
	public double getCofacWeight() {
		return cofacWeight;
	}

	/**
	 * @param cofacWeight the cofac tag weight to set
	 */
	public void setCofacWeight(double cofacWeight) {
		this.cofacWeight = cofacWeight;
	}

	/**
	 * @return the ligase tag weight
	 */
	public double getLigaseWeight() {
		return ligaseWeight;
	}

	/**
	 * @param ligaseWeight the ligase tag weight to set
	 */
	public void setLigaseWeight(double ligaseWeight) {
		this.ligaseWeight = ligaseWeight;
	}

	/**
	 * @return the leave tag weight
	 */
	public double getLeaveWeight() {
		return leaveWeight;
	}

	/**
	 * @param leaveWeight the leave tag weight to set
	 */
	public void setLeaveWeight(double leaveWeight) {
		this.leaveWeight = leaveWeight;
	}
}
