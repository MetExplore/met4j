/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

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
}
