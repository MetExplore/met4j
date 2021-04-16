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




/**
 * <p>BioRef class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioRef implements Comparator<BioRef>,Comparable<BioRef>{
	
	public String origin; 	//source of the annotation, automatically inferred or get from model
	public String dbName;	//name of the database
	public String logicallink="is";
	public String id;
	public String baseURI;
	public int confidenceLevel;

	/**
	 * Constructor
	 *
	 * @param origin source of the annotation
	 * @param dbName name of the database
	 * @param id reference id
	 * @param confidenceLevel a confidence level
	 */
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

	/**
	 * <p>getUri.</p>
	 *
	 * @return an url with the base url + dbName + refId
	 */
	public String getUri(){
		return baseURI+id;
	}

	/**
	 * <p>Getter for the field <code>origin</code>.</p>
	 *
	 * @return the origin of the ref
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * <p>Setter for the field <code>origin</code>.</p>
	 *
	 * @param origin the origin of the ref
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * <p>Getter for the field <code>dbName</code>.</p>
	 *
	 * @return the database name
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * <p>Setter for the field <code>dbName</code>.</p>
	 *
	 * @param dbName the database name
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return the id of the reference
	 */
	public String getId() {
		return id;
	}

	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id the id of the reference
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * <p>Getter for the field <code>confidenceLevel</code>.</p>
	 *
	 * @return the confidence level
	 */
	public int getConfidenceLevel() {
		return confidenceLevel;
	}

	/**
	 * <p>Setter for the field <code>confidenceLevel</code>.</p>
	 *
	 * @param confidenceLevel the confidence level
	 */
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}


	/**
	 * <p>Getter for the field <code>logicallink</code>.</p>
	 *
	 * @return the logical link of the reference
	 */
	public String getLogicallink() {
		return logicallink;
	}

	/**
	 * <p>Setter for the field <code>logicallink</code>.</p>
	 *
	 * @param logicallink the logical link of the reference
	 */
	public void setLogicallink(String logicallink) {
		this.logicallink = logicallink;
	}

	/** {@inheritDoc} */
	@Override
	public int compare(BioRef arg0, BioRef arg1) {		
		return arg0.getConfidenceLevel()-arg1.getConfidenceLevel();
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(BioRef o) {
		return compare(this,o);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj){
        if (obj==this) {
            return true;
        }
        if (obj instanceof BioRef) {
        	BioRef r2 = ((BioRef) obj);
        	if(this.dbName.equalsIgnoreCase(r2.dbName)){
        		if(this.id.equals(r2.id)){
					return this.logicallink.equals(r2.logicallink);
            	}
        	}
        }
        return false;
	}
	
    /** {@inheritDoc} */
	@Override
    public int hashCode() {
		return Objects.hash(this.getDbName().toUpperCase(),this.getId(),this.getLogicallink());
	}

	/**
	 * <p>Getter for the field <code>baseURI</code>.</p>
	 *
	 * @return the base url
	 */
	public String getBaseURI() {
		return baseURI;
	}

	/**
	 * <p>Setter for the field <code>baseURI</code>.</p>
	 *
	 * @param baseURI the base url
	 */
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "BioRef [origin=" + origin + ", dbName=" + dbName + ", logicallink=" + logicallink + ", id=" + id
				+ ", baseURI=" + baseURI + ", confidenceLevel=" + confidenceLevel + "]";
	}
	
	
	
	
}
