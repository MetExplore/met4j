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
 * Abstract class to set Attributes from a file
 * 
 * 6 avr. 2012 
 */
package attributes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;


/**
 * @author lcottret
 * 6 avr. 2012
 *
 */
public abstract class SetAttributeFromFile {
	
	private int colId = 0;
	private int colAttr = 1;
	private BioNetwork bn;
	private String fileIn;
	private String commentCharacter = "#";
	private int nSkip=0;
	private String object=REACTION;
	private Boolean transformInPalsson=false;
	
	public static final String REACTION = "R";
	public static final String METABOLITE = "M";
	public static final String PROTEIN = "P";
	public static final String GENE = "G";
	public static final String PATHWAY = "Pa";
	
	private Set<String> objectIds;
	
	private HashMap<String, String> idAttributeMap;
	
	
	
	
	/**
	 * 
	 * @param colId : number of the id column
	 * @param colAttr : number of the attribute column
	 * @param bn : BioNetwork
	 * @param fileIn : tabulated file containing the ids and the attributes
	 * @param c : comment character
	 * @param o : object to set ("R" : reaction, "M" : metabolite, "G" : gene, "P" : protein, "PA" : pathway)
	 * @param t : if true transform object ids in the palsson way
	 */
	public SetAttributeFromFile(int colId, int colAttr, BioNetwork bn, String fileIn, String c, int nSkip, String o, Boolean t) {
		
		this.setColId(colId);
		this.setColAttr(colAttr);
		this.setNetwork(bn);
		this.setFileIn(fileIn);
		this.setCommentCharacter(c);
		this.setnSkip(nSkip);
		this.setTransformInPalsson(t);
		this.setIdAttributeMap(new HashMap<String, String>());
		
		if(o.equalsIgnoreCase(REACTION) && o.equalsIgnoreCase(METABOLITE) && o.equalsIgnoreCase(PROTEIN) && 
				o.equalsIgnoreCase(GENE) && o.equalsIgnoreCase(PATHWAY)) {
			System.err.println("Bad identifier of object : "+o);
			System.err.println("Identifiers allowed : "+REACTION+" (reaction) "+METABOLITE+" (metabolite) "+PROTEIN+ " (protein) "+GENE+" (gene) "+PATHWAY+" (pathway)");
			System.err.println(REACTION+" is defined as default");
		}
		else {
			this.setObject(o);
		}
		
		if(this.getObject().equalsIgnoreCase(REACTION)) {
			this.objectIds = bn.getBiochemicalReactionList().keySet();
		}
		
		if(this.getObject().equalsIgnoreCase(METABOLITE)) {
			this.objectIds = bn.getPhysicalEntityList().keySet();
		}
		
		if(this.getObject().equalsIgnoreCase(PROTEIN)) {
			this.objectIds = bn.getProteinList().keySet();
		}
		
		if(this.getObject().equalsIgnoreCase(GENE)) {
			this.objectIds = bn.getGeneList().keySet();
		}
		
		if(this.getObject().equalsIgnoreCase(PATHWAY)) {
			this.objectIds = bn.getPathwayList().keySet();
		}
		
		
	}
	
	/**
	 * Test the attribute file and other things
	 * @return
	 * @throws IOException 
	 */
	public Boolean test() throws IOException {
		
		Boolean flag = true;
		
		if(this.colId < 0) {
			System.err.println("There is an error in the id column number");
		}
		
		if(this.colAttr < 0) {
			System.err.println("There is an error in the attribute column number");
		}
		
		FileInputStream in;
		BufferedReader br;
		try {
			in = new FileInputStream(this.getFileIn());
			InputStreamReader ipsr=new InputStreamReader(in);
			br = new BufferedReader(ipsr);
		} catch (Exception e) {
			System.err.println("Impossible to read the input file "+fileIn);
			return false;
		}
		
		String ligne;

		int nLines = 0;

		Set<String> ids = new HashSet<String>();
		
		while ((ligne=br.readLine())!=null){

			nLines++;
			
			if(nLines > this.nSkip) {
				if(this.getCommentCharacter().equals("") || ! ligne.matches("^"+this.getCommentCharacter()+".*")) {
					
					String[] tab = ligne.split("\\t");
					
					
					if(tab.length <= this.colId || tab.length <= this.colAttr) {
						System.err.println("********\n[Fatal Error]Bad number of columns line "+nLines+"\n********");
//						flag = false;
					}
					else {
						
						String attribute = tab[this.colAttr];
						
						if(! this.testAttribute(attribute)) {
							System.err.println("********\n[Fatal Error]Attribute \""+attribute+"\" not well formatted\n********");
//							flag = false;
						}
						
						String id = tab[this.colId];
						// remove spaces
						id = id.trim();
						
						if(this.getObject().equalsIgnoreCase(METABOLITE) && this.getTransformInPalsson()) {
							Boolean presence= false;
							
							for(String compartmentId : this.getNetwork().getCompartments().keySet()) {
								
								// To transform metabolite id like this cpd[c] in cpd_c
								Pattern cpd_Pattern = Pattern.compile("^.*(\\[([^\\]]*)\\])$");
								
								// To see if the id ends with _compartmentId
								Pattern cpt_Pattern = Pattern.compile("^.*_([^_])"+compartmentId+"$");
								
								Matcher matcherCpd = cpd_Pattern.matcher(id);

								if(matcherCpd.matches()) {
									id = id.replace(matcherCpd.group(1), "_"+matcherCpd.group(2));
								}
								
								id = id.replaceAll("[^A-Za-z0-9_]", "_");
								
								Matcher matcherCpt = cpt_Pattern.matcher(id);

								String metaboliteId;
								
								if(! matcherCpt.matches()) {
									metaboliteId = "M_"+id+"_"+compartmentId;
								}
								else {
									metaboliteId = "M_"+id;
								}
								
								if(this.getNetwork().getPhysicalEntityList().containsKey(metaboliteId)) {
									this.getIdAttributeMap().put(metaboliteId, attribute);
									presence = true;
								}
							}
							
							if(presence && ids.contains(id)) {
								System.err.println("[Warning] Duplicated id : "+id+" line "+nLines);
//								flag = false;
							}
							
							if(presence) {
								ids.add(id);
							}

							if(! presence) {
								System.err.println("[Warning] Metabolite "+id+" does not correspond to any metabolite in the network line "+nLines);
//								flag = false;
							}
						}
						else {
							
//							id = id.replaceAll("[^A-Za-z0-9_]", "_");

							if(this.transformInPalsson && this.object.equalsIgnoreCase(REACTION)) {
								id = "R_"+id;
							}

							if(id.equals("")) {
								System.err.println("********\n[Fatal Error]Empty object id empty line "+nLines+"\n********");
								flag = false;
							}

							if(ids.contains(id)) {
								System.err.println("[Warning] Duplicated id : "+id+" line "+nLines);
							}
							else {
								if(! this.objectIds.contains(id)) {
									System.err.println("[Warning] "+id+" not present in the network line "+nLines);
//									flag = false;
								}
								else {
									this.getIdAttributeMap().put(id, attribute);
									ids.add(id);
								}
							}
						}
					}
				}
			}
		}
		
		br.close();
		
		
		if (flag == false) {
			System.err.println("Input file badly formatted");
		}
		else {
			System.err.println("The input file looks good and contains "+ids.size()+" entries");
		}
		
		return flag;
		
	}
	
	
	/**
	 * Abstract function to validate the attribute
	 * @return true if attribute well formatted
	 */
	public abstract Boolean testAttribute(String attribute);
	
	/**
	 * Reads the file and sets the attributes
	 * @return
	 */
	public abstract Boolean setAttributes() throws IOException;
	
	
	/**
	 * @return the colId
	 */
	public int getColId() {
		return colId;
	}

	/**
	 * @param colId the colId to set
	 */
	public void setColId(int colId) {
		this.colId = colId;
	}

	/**
	 * @return the colAttr
	 */
	public int getColAttr() {
		return colAttr;
	}

	/**
	 * @param colAttr the colAttr to set
	 */
	public void setColAttr(int colAttr) {
		this.colAttr = colAttr;
	}

	/**
	 * @return the network
	 */
	public BioNetwork getNetwork() {
		return bn;
	}

	/**
	 * @param bn the network to set
	 */
	public void setNetwork(BioNetwork bn) {
		this.bn = bn;
	}

	/**
	 * @return the fileIn
	 */
	public String getFileIn() {
		return fileIn;
	}

	/**
	 * @param fileIn the fileIn to set
	 */
	public void setFileIn(String fileIn) {
		this.fileIn = fileIn;
	}

	/**
	 * @return the commentCharacter
	 */
	public String getCommentCharacter() {
		return commentCharacter;
	}

	/**
	 * @param commentCharacter the commentCharacter to set
	 */
	public void setCommentCharacter(String commentCharacter) {
		this.commentCharacter = commentCharacter;
	}

	/**
	 * @return the nSkip
	 */
	public int getnSkip() {
		return nSkip;
	}

	/**
	 * @param nSkip the nSkip to set
	 */
	public void setnSkip(int nSkip) {
		this.nSkip = nSkip;
	}

	/**
	 * @return the object
	 */
	public String getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * @return the transformInPalsson
	 */
	public Boolean getTransformInPalsson() {
		return transformInPalsson;
	}

	/**
	 * @param transformInPalsson the transformInPalsson to set
	 */
	public void setTransformInPalsson(Boolean transformInPalsson) {
		this.transformInPalsson = transformInPalsson;
	}

	/**
	 * @return the idAttributeMap
	 */
	public HashMap<String, String> getIdAttributeMap() {
		return idAttributeMap;
	}

	/**
	 * @param idAttributeMap the idAttributeMap to set
	 */
	public void setIdAttributeMap(HashMap<String, String> idAttributeMap) {
		this.idAttributeMap = idAttributeMap;
	}
	
	
	

}
