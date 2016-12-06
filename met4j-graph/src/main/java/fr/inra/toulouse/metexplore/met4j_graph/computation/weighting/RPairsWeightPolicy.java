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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;

/**
 * Use atom conservation rate as weight, based on KEGG's RPairs data.
 * Vertex without KEGG id will be removed from graph.
 * @author clement
 */
@Deprecated
public class RPairsWeightPolicy extends WeightingPolicy<BioPhysicalEntity,ReactionEdge,CompoundGraph> {
	
	/** The KEGG REST Base URL. */
	protected static final String KEGG_REST_URL = "http://rest.kegg.jp/";
	
	/** If vertex id correspond to Kegg identifier */
	public boolean keggAsId = false;
	
	/**	used to store atom conservation value of rpairs, avoid accessing to the same entry in kegg twice	*/
	HashMap<String, Double> rPairMap = new HashMap<String, Double>();

	/** The graph. */
	public CompoundGraph g;
	
	/**
	 * Instantiates a new r pairs weight policy.
	 */
	public RPairsWeightPolicy() {}
	
	/**
	 * Instantiates a new r pairs weight policy.
	 *
	 * @param keeggAsId If vertex id correspond to Kegg identifier
	 */
	public RPairsWeightPolicy(boolean keeggAsId) {
		this.keggAsId=keeggAsId;
	}

	/* (non-Javadoc)
	 * @see parsebionet.applications.graphe.WeightingPolicy#setWeight(parsebionet.applications.graphe.CompoundGraph)
	 */
	@Override
	public void setWeight(CompoundGraph graph) {
		//init
		this.g=graph;
		rPairMap = new HashMap<String, Double>();
		if(keggAsId){
			for(BioPhysicalEntity v : g.vertexSet()){
				if(v.getRefs("KEGG.COMPOUND")==null){
					v.addRef("KEGG.COMPOUND", v.getId(), 1, "is", "original network");
				}
			}
		}else{
			//remove compound without kegg ref
			HashSet<BioPhysicalEntity> noKegg = new HashSet<BioPhysicalEntity>();
			for(BioPhysicalEntity v : g.vertexSet()){
				if(v.getRefs("KEGG.COMPOUND")==null){
					noKegg.add(v);
				}
			}
			System.err.println("removing "+noKegg.size()+" vertex with no kegg id");
			g.removeAllVertices(noKegg);
		}
		
		//set weights
		int i =1;
		int n = g.edgeSet().size();
		for(ReactionEdge e : g.edgeSet()){
			System.err.print(i+"/"+n+" "+e.getV1().getId()+" - "+e.getV2().getId()+" : ");
			setEdgeWeight(e);
			System.err.print(g.getEdgeWeight(e)+"\n");
			i++;
		}
		return;
	}
	
	/**
	 * Set weight for a single edge. Use atom conservation from RPairs as weight
	 * @param e the edge to set
	 */
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
						g.setEdgeWeight(e, 0.0);
						rPairMap.put(rPairKey, 0.0);
						return;
					}
					
					//transport reaction case
					if(keggEntry1.getId().matches(keggEntry2.getId())){
						g.setEdgeWeight(e, 1.0);
						return; 
					}
					
					//get RPAIRS id from KEGG ids using KEGG REST API
					String rPairId = getRPairEntry(keggEntry1.getId(),keggEntry2.getId());
					if(rPairId!=null){
						//get atom conservation from RPair id using KEGG REST API
						double atomConservation = getAtomConservation(rPairId,false);
						g.setEdgeWeight(e, atomConservation);
						rPairMap.put(rPairKey, atomConservation);
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
						//get atom conservation from RPair id using KEGG REST API
						double atomConservation = getAtomConservation(rPairId,true);
						g.setEdgeWeight(e, atomConservation);
						rPairMap.put(rPairKey, atomConservation);
						return;
					}
				}
			}
		}
		
		//no rpair found
		g.setEdgeWeight(e, 0.0);
		rPairMap.put(rPairKey, 0.0);
		return;
	}
	

	/**
	 * Gets the RPair identifier from couple of Kegg ids using KEGG REST API.
	 *
	 * @param compound1KeggId the souce kegg id
	 * @param compound2KeggId the target kegg id
	 * @return the RPair entry
	 */
	public String getRPairEntry(String compound1KeggId, String compound2KeggId){
		String rPairName = compound1KeggId+"_"+compound2KeggId;
		URL entry;
        BufferedReader in;
        String rPairId = null;
		try {
			entry = new URL(RPairsWeightPolicy.KEGG_REST_URL+"find/rpair/"+rPairName);
			
			String regex=".*rp:(\\S+)\\s+"+rPairName;
		
			in = new BufferedReader(
			new InputStreamReader(entry.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null){
				Matcher m=Pattern.compile(regex).matcher(inputLine);
				if(m.matches()){
					rPairId=m.group(1);
					break;
				}
			}
	        
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rPairId;
	}
	

	/**
	 * Gets the atom conservation rate parsing RPair entry from KEGG REST API.
	 *
	 * @param rPairId the RPair idenfier
	 * @return the atom conservation rate
	 */
	public double getAtomConservation(String rPairId, boolean reverse){
        URL entry;
        BufferedReader in;
        double align = 0;
        double atom = 0;
		try {
			entry = new URL(RPairsWeightPolicy.KEGG_REST_URL+"get/"+rPairId);
			
			String regex=".*ALIGN\\s+(\\d+).*";
			
			in = new BufferedReader(
			new InputStreamReader(entry.openStream()));
			String inputLine;
			boolean getNextMatch = !reverse;
			while ((inputLine = in.readLine()) != null){
				Matcher m=Pattern.compile(regex).matcher(inputLine);
				if(m.matches()){
					if(align==0){
						align=Double.parseDouble(m.group(1));
						regex=".*ATOM\\s+(\\d+).*";
					}else if(getNextMatch){
						atom=Double.parseDouble(m.group(1));
						break;
					}else{
						getNextMatch=true;
					}
				}
			}         
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return align/atom;
	}

}
