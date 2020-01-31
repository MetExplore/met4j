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

/*
 * 
 */
package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import java.util.Comparator;
import java.util.Objects;

import org.apache.commons.lang3.Validate;




public class BioRef implements Comparator<BioRef>,Comparable<BioRef>{
	
	public String origin; 	//source of the annotation, automatically inferred or get from model
	public String dbName;	//name of the database
	public String logicallink="is";
	public String id;
	public String baseURI;
	public int confidenceLevel;
	
	public BioRef(String origin,String dbName,String id,int confidenceLevel) {
		
		Validate.notNull(dbName, "BioRef's database name can't be null");
		Validate.notNull(id, "BioRef's database identifier can't be null");
		
		this.origin=origin;
		this.dbName=dbName;
		this.id=id;
		this.confidenceLevel=confidenceLevel;
		// TODO : baseUri by default, not sure that we must keep this
		this.baseURI="http://identifiers.org/"+dbName+"/";
	}
	
	public String getUri(){
		return baseURI+id;
	}
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getConfidenceLevel() {
		return confidenceLevel;
	}
	
	
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}


	public String getLogicallink() {
		return logicallink;
	}

	public void setLogicallink(String logicallink) {
		this.logicallink = logicallink;
	}

	@Override
	public int compare(BioRef arg0, BioRef arg1) {		
		return arg0.getConfidenceLevel()-arg1.getConfidenceLevel();
	}

	@Override
	public int compareTo(BioRef o) {
		return compare(this,o);
	}
	
	@Override
	public boolean equals(Object obj){
        if (obj==this) {
            return true;
        }
        if (obj instanceof BioRef) {
        	BioRef r2 = ((BioRef) obj);
        	if(this.dbName.equalsIgnoreCase(r2.dbName)){
        		if(this.id.equals(r2.id)){
        			if(this.logicallink.equals(r2.logicallink)){
                		return true;
                	}
            	}
        	}
        }
        return false;
	}
	
	@Override
    public int hashCode() {
		return Objects.hash(this.getDbName().toUpperCase(),this.getId(),this.getLogicallink());
	}

	public String getBaseURI() {
		return baseURI;
	}

	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	@Override
	public String toString() {
		return "BioRef [origin=" + origin + ", dbName=" + dbName + ", logicallink=" + logicallink + ", id=" + id
				+ ", baseURI=" + baseURI + ", confidenceLevel=" + confidenceLevel + "]";
	}
	
	
	
	
}
