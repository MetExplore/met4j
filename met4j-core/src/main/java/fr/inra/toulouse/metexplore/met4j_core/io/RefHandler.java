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
package fr.inra.toulouse.metexplore.met4j_core.io;

/**
 * The Class RefHandler is used to store information about remote database access,
 * It allows to handle database cross ref import by checking the validity of the url
 * @author clement
 */
public class RefHandler {

	/** The database name. */
	private String dbName;
	
	/** The base url to access database entries. */
	private String baseUrl;
	
	/** The optional regex used to check if an identifier is valid. */
	private String validIdRegex = ".*";
	
	/** If the reference allowing multiple values. */
	private boolean isAllowingMultipleValues = true;
	
	
	public static final RefHandler KEGG_COMPOUND = new RefHandler("KEGG.COMPOUND","http://identifiers.org/kegg.compound/", "C\\d+");
	public static final RefHandler CHEBI_HANDLER = new RefHandler("CHEBI","http://identifiers.org/chebi/", "CHEBI:\\d+");
	public static final RefHandler PUBCHEM_COMPOUND_HANDLER = new RefHandler("PUBCHEM.COMPOUND","http://identifiers.org/pubchem.compound/", "\\d+");
	public static final RefHandler HMDB_HANDLER = new RefHandler("HMDB","http://identifiers.org/hmdb/", "HMDB\\d+");
	public static final RefHandler PUBCHEM_SUBSTANCE_HANDLER = new RefHandler("PUBCHEM.SUBSTANCE","http://identifiers.org/pubchem.substance/", "\\d+");
	public static final RefHandler KEGG_GENES_HANDLER = new RefHandler("KEGG.GENES","http://identifiers.org/kegg.genes/", ".+");
	public static final RefHandler UNIPROT_HANDLER = new RefHandler("UNIPROT","http://identifiers.org/uniprot/", ".+");
	public static final RefHandler INCHI_HANDLER = new RefHandler("INCHI","http://identifiers.org/inchi/", "InChI=\\dS?/.+", false);
	public static final RefHandler INCHIKEY_HANDLER = new RefHandler("INCHIKEY","http://identifiers.org/inchikey/", "[A-Z\\-]+", false);
	public static final RefHandler EC_NUMBER_HANDLER = new RefHandler("EC Number","http://identifiers.org/ec-code/", "[\\d\\.]+", false);
	public static final RefHandler PUBMED_HANDLER = new RefHandler("AUTHORS","http://identifiers.org/pubmed/", "(PMID:)?\\d+");
	public static final RefHandler UNIPATHWAY_HANDLER = new RefHandler("UPA","http://www.unipathway.org//upc?upid=");
	public static final RefHandler SEED_HANDLER = new RefHandler("SEED","http://identifiers.org/seed.compound/","^cpd\\d+$");
	public static final RefHandler REACTOME_HANDLER = new RefHandler("REACTOME","http://identifiers.org/reactome/","(^(REACTOME:)?R-[A-Z]{3}-[0-9]+(-[0-9]+)?$)|(^(REACT_)?\\d+$)");
	public static final RefHandler METANETX_HANDLER = new RefHandler("MXNREF","http://metanetx.org/cgi-bin/mnxweb/chem_info?chem=","^MNXM\\d+$");
	public static final RefHandler METACYC_HANDLER = new RefHandler("METACYC","http://biocyc.org/META/NEW-IMAGE?type=NIL&object=C");
	public static final RefHandler BRENDA_HANDLER = new RefHandler("BRENDA","http://identifiers.org/brenda/");
	
	/**
	 * Instantiates a new ref handler.
	 *
	 * @param dbName the database name
	 * @param baseUrl the base url to access database entries
	 */
	public RefHandler(String dbName, String baseUrl) {
		this.dbName = dbName;
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Instantiates a new ref handler.
	 *
	 * @param dbName the database name
	 * @param baseUrl the base url to access database entries
	 * @param validIdRegex the regex used to check if an identifier is valid
	 */
	public RefHandler(String dbName, String baseUrl, String validIdRegex) {
		this.dbName = dbName;
		this.baseUrl = baseUrl;
		this.validIdRegex = validIdRegex;
	}
	
	/**
	 * Instantiates a new ref handler.
	 *
	 * @param dbName the database name
	 * @param baseUrl the base url to access database entries
	 * @param validIdRegex the regex used to check if an identifier is valid
	 * @param isAllowingMultipleValues If the reference allowing multiple values
	 */
	public RefHandler(String dbName, String baseUrl, String validIdRegex, boolean isAllowingMultipleValues) {
		this.dbName = dbName;
		this.baseUrl = baseUrl;
		this.validIdRegex = validIdRegex;
		this.isAllowingMultipleValues = isAllowingMultipleValues;
	}
	
	/**
	 * Gets the database name
	 *
	 * @return the database name
	 */
	public String getDbName() {
		return dbName;
	}
	
	/**
	 * Sets the database name
	 *
	 * @param dbName the new database name
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	/**
	 * Gets the base url to access database entries
	 *
	 * @return the base url to access database entries
	 */
	public String getBaseUrl() {
		return baseUrl;
	}
	
	/**
	 * Sets the base url to access database entries
	 *
	 * @param baseUrl the new base url to access database entries
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Gets the regex used to check if an identifier is valid
	 *
	 * @return the regex used to check if an identifier is valid
	 */
	public String getValidIdRegex() {
		return validIdRegex;
	}
	
	/**
	 * Sets the regex used to check if an identifier is valid
	 *
	 * @param validIdRegex the new regex used to check if an identifier is valid
	 */
	public void setValidIdRegex(String validIdRegex) {
		this.validIdRegex = validIdRegex;
	}
	
	/**
	 * Checks if the reference allowing multiple values
	 *
	 * @return true, if the reference allowing multiple values
	 */
	public boolean isAllowingMultipleValues() {
		return isAllowingMultipleValues;
	}
	
	/**
	 * Sets if the reference allowing multiple values
	 *
	 * @param isAllowingMultipleValues the new allowing multiple values
	 */
	public void setAllowingMultipleValues(boolean isAllowingMultipleValues) {
		this.isAllowingMultipleValues = isAllowingMultipleValues;
	}

}
