/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: benjamin.merlet@toulouse.inra.fr
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

/*
 * Created on jan. 30 2014
 * B.M
 */

package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/*
 * This Class is used to keep track of the annotations present in the imported file in the BioNetwork object
 * It will be updated in the future to handle a specific parsing of the annotations
 */
public class BioAnnotation {
	
	/**
	 * The annotations are successive xml elements used to cross reference biological data.
	 */
	private String XMLasString;
	private String MetaId;
	
	
	/**
	 * Default Constructor
	 */
	public BioAnnotation(){
		this.MetaId=null;
	}
	
	/**
	 * Constructor
	 * @param xmlasString
	 */
	public BioAnnotation(String meta, String xmlasString ) {
		
		if(StringUtils.isVoid(meta)){
			XMLasString = null;
			MetaId=null;
		}else{
			XMLasString = xmlasString;
			MetaId=meta;
		}
	}
	
	
	public HashSet<String> getEncodedBy(){
		
		String annot=this.getXMLasString().replaceAll(">\\s+<", "><");
		HashSet<String> genesName=new HashSet<String>();
		Matcher m;
		String regex=".+?isEncodedBy.+?resource=\"http://identifiers\\.org/[^/]+/([^\"]+)\".+?isEncodedBy>.*";
		
		m=Pattern.compile(regex).matcher(annot);
		while (m.matches()){
			String value=m.group(1);
			genesName.add(value);
			annot=annot.replaceAll(value, "");
			m=Pattern.compile(regex).matcher(annot);
		}
		
		return genesName;
	}
	
	
	/**
	 * XMLasString Getter 
	 * @return The XMLasString parameter
	 */
	public String getXMLasString() {
		return XMLasString;
	}

	
	/**
	 * Set the 
	 * @param xmlasString
	 */
	public void setXMLasString(String xmlasString) {
		XMLasString = xmlasString;
	}

	public String getMetaId() {
		return MetaId;
	}

	public void setMetaId(String metaId) {
		MetaId = metaId;
	}
	
	/**
	 * Parse Annotation XML as a HashMap with db as key and dbid as value
	 * @return HashMap<db,dbid>
	 */
	public HashMap<String,String> getAnnotationAsSimpleHashMap(){
		String AnnXML = XMLasString;
		HashMap<String,String> AnnotationHashMap = new HashMap<String,String>();
		
		String[] lines = AnnXML.split("\n");
		for(int i=0;i<lines.length;i++){
			if(lines[i].contains("identifiers.org")){
				String[] lineparts = lines[i].split("/");
				for(int j=0;j<lineparts.length;j++){
					if(lineparts[j].contains("identifiers.org")){
						if(! lineparts[j+1].equals("inci")){
							AnnotationHashMap.put(lineparts[j+1], lineparts[j+2].replace("\"",""));
							break;
						}
					}
				}
			}
		}
				
		return AnnotationHashMap;
	}
	
	/**
	 * Creates the annotation XMLstring from Annotation HashMap
	 * @param AnnotMap
	 */
	public void setXMLasStringFromHashMap(HashMap<String,String> AnnotMap){
		String tempstr = "        <annotation>\n          <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:bqmodel=\"http://biomodels.net/model-qualifiers/\" xmlns:bqbiol=\"http://biomodels.net/biology-qualifiers/\">\n            <rdf:Description rdf:about=\""+MetaId+"\">\n";		
		for(String db:AnnotMap.keySet()){
			tempstr=tempstr+"              <bqbiol:is>\n                <rdf:Bag>\n                  <rdf:li rdf:resource=\"http://identifiers.org/"+db+"/"+AnnotMap.get(db)+"\"/>\n                </rdf:Bag>\n              </bqbiol:is>\n";
		}
		tempstr=tempstr+"            </rdf:Description>\n          </rdf:RDF>\n        </annotation>";
		XMLasString = tempstr;
	}

}