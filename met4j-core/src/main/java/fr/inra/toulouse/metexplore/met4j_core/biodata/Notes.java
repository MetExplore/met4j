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

import java.util.ArrayList;


/*
 * This Class is used to keep track of the notes present in the imported file in the BioNetwork object
 * It will be updated in the future to handle a specific parsing of the annotations
 */
public class Notes {
	
	/**
	 * The annotations are successive xhtml elements used to for user specific data.
	 */
	private String XHTMLasString;
	
	
	/**
	 * Default Constructor
	 */
	public Notes(){
		XHTMLasString="NA";
	}
	
	/**
	 * Constructor
	 * @param xhtmlasString
	 */
	public Notes(String xhtmlasString) {
		XHTMLasString = xhtmlasString;
	}
	
	/**
	 * XHTMLasString Getter 
	 * @return The XHTMLasString parameter
	 */
	public String getXHTMLasString() {
		return XHTMLasString;
	}

	
	/**
	 * Set the 
	 * @param xhtmlasString
	 */
	public void setXHTMLasString(String xhtmlasString) {
		XHTMLasString = xhtmlasString;
	}

	/**
	 * Add to notes
	 * 
	 */
	public void addAttrToNotes(String key,String value){
		String oldNote = this.getXHTMLasString();
//		oldNote=oldNote.replaceAll("\n", "");
//		oldNote=oldNote.replaceAll(" *", "");
		
		ArrayList<String> newAttr = new ArrayList<String>();
		
		System.err.print("New Note \n");
		
		boolean hasattr = false;
        for(String k:oldNote.split("\n")){
        	if(k.contains(key)){
        		System.err.println("has attribute");
        		hasattr = true;
        		k=k.replaceAll("^ *<p>.*</p>.*$","<p>"+key.toUpperCase()+": "+value.toUpperCase()+"</p>" );
        		newAttr.add(k);
        	}else if(k.contains("</body>") || k.contains("</notes>")){
        		continue;
        	}else{
        		newAttr.add(k);
        	}
        }
        
        if(!hasattr){
        	newAttr.add("<p>"+key.toUpperCase()+": "+value.toUpperCase()+"</p>" );
        }
        
        newAttr.add("</body>");
        newAttr.add("</notes>");
        
        String newNote = "";
        for(String attr: newAttr){
        	if(newNote.equals("")){
        		newNote = newNote+attr;
        	}else{
        		newNote = newNote+"\n"+attr;
        	}
        }
        
        XHTMLasString = newNote;
	}

}