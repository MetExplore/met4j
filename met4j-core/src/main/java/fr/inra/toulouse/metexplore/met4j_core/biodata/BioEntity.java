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
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * The root class 
 */

public abstract class BioEntity {
	
	private final String id;
	private String name;
	private ArrayList<String> synonyms = new ArrayList<String>();
	private String comment;
		
	private HashMap<String,Set<BioRef>> refs;
	

	public BioEntity(String id, String name) {
		
		if(id==null)
		{
			throw new NullPointerException();
		}

		this.id=id;

		this.setName(name);
		this.setRefs(new HashMap<String, Set<BioRef>>());
	}
	
	
	public BioEntity(String id) {

		
		if(id==null)
		{
			throw new NullPointerException();
		}

		this.id=id;
		this.setName(id);
		this.setRefs(new HashMap<String, Set<BioRef>>());
	}
	
	/**
	 * Set the name of the entity
	 * @param n : String
	 */
	public void setName (String n) {
		this.name = n;
	}
	
	/**
	 * Get the name of the entity
	 * @return the name of the entity
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get the list of the synonyms
	 * @return an ArrayList of the synonyms of the entity
	 */
	public ArrayList<String> getSynonyms() {
		return this.synonyms;
	}
	
	/**
	 * add a synonym in the list
	 * @param a String to add
	 */
	public void addSynonym(String s) {
		this.synonyms.add(s);
	}
	
	/**
	 * Set the comment on the entity
	 * @param c
	 */
	public void setComment (String c) {
		this.comment = c;
	}
	
	/**
	 * Get the comment on the entity
	 * @return the comment on the entity
	 */
	public String getComment() {
		return this.comment;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}
	
	
	/**
	 * TODO : voir la coherence du code entre les deux methodes addRef. Celle ci devrait se terminer par un this.addRef(ref)
	 * @param dbName
	 * @param dbId
	 * @param confidenceLevel
	 * @param relation
	 * @param origin
	 */
	public void addRef(String dbName, String dbId, int confidenceLevel, String relation, String origin){
		BioRef ref = new BioRef(origin, dbName, dbId, confidenceLevel);
		ref.setLogicallink(relation);
		if (this.refs.containsKey(dbName)){
			refs.get(dbName).add(ref);
		} else {
			Set<BioRef> refList = new HashSet<BioRef>();
			refList.add(ref);
			this.refs.put(dbName, refList);
		}
	}
	
	public void addRef(BioRef ref){
		String dbName = ref.getDbName();
		String dbId = ref.getId();
		if(!this.hasRef(dbName, dbId)){
			if (this.refs.containsKey(dbName)){
				refs.get(dbName).add(ref);
			} else {
				Set<BioRef> refList = new HashSet<BioRef>();
				refList.add(ref);
				this.refs.put(dbName, refList);
			}
		}
	}
	
	public HashMap<String, Set<BioRef>> getRefs(){
		return this.refs;
	}
	
	public Set<BioRef> getRefs(String dbName){
		if (this.refs.containsKey(dbName)){
			return this.refs.get(dbName);
		}
		else{
			return null;
		}
	}
	
	public boolean hasRef(String dbName, String dbId){
		if(this.refs==null || !this.refs.containsKey(dbName)){
			return false;
		}
		for(BioRef ref:this.refs.get(dbName)){
			if(ref.getId().equals(dbId)){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRef(BioRef unkRef){
		
		if(this.refs==null || !this.refs.containsKey(unkRef.dbName)){
			return false;
		}
		for(BioRef ref:this.refs.get(unkRef.dbName)){
			if(ref.getId().equals(unkRef.id) && ref.getLogicallink().equals(unkRef.logicallink)){
				return true;
			}
		}
		return false;
	}
	
	public void setRefs(HashMap<String, Set<BioRef>> refs){
		this.refs=refs;
	}
	
	@Override
	public String toString(){
		return this.getId();
	}

	
}
