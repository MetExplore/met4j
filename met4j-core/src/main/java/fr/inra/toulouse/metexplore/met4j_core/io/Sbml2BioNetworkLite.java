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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.UnitSbml;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

/**
 * A light version of the sbml to bionetwork import, skipping the protein and genes levels as well as flux parameters.
 * Can be set to parse notes field, adding metabolites structural information and reactions pathways and EC numbers
 * @author clement from lcottret
 */
public class Sbml2BioNetworkLite {
	
	/** The bio network. */
	BioNetwork bioNetwork = null;
	
	/** The input sbml. */
	String inputSbml;
	
	/** The document. */
	Document document;
	
	/** The compounds. */
	HashMap<String,BioPhysicalEntity> compounds;
	
	/** The reactions. */
	HashSet<BioChemicalReaction> reactions;
	
	/** The compartments. */
	HashMap<String,BioCompartment> compartments;
	
	/** If notes should be parsed. */
	boolean parseNote=false;
	
	/** the string using for separate attributes with multiple values*/
	String notesValueSeparator = "; ";
	/** the string using for separate attributes name from attribute's values in notes*/
	String notesAttributeToValueSeparator = ": ";
	/**	**/
	Collection<RefHandler> refHandlers = new ArrayList<RefHandler>();
	
	/**
	 * Instantiates a new sbml import.
	 *
	 * @param inputSbml the input sbml
	 */
	public Sbml2BioNetworkLite(String inputSbml) {
		this.inputSbml=inputSbml;
		this.compounds = new HashMap<String,BioPhysicalEntity>();
		this.reactions = new HashSet<BioChemicalReaction>();
		this.compartments = new HashMap<String,BioCompartment>();
	}
	
	/**
	 * Instantiates a new sbml import
	 *
	 * @param inputSbml the input sbml
	 * @param parseNotes if notes should be parsed
	 */
	public Sbml2BioNetworkLite(String inputSbml, boolean parseNotes) {
		this.inputSbml=inputSbml;
		this.compounds = new HashMap<String,BioPhysicalEntity>();
		this.reactions = new HashSet<BioChemicalReaction>();
		this.compartments = new HashMap<String,BioCompartment>();
		this.parseNote=parseNotes;
	}
	
	/**
	 * Builds the network.
	 */
	private void build(){
		try {
			bioNetwork = new BioNetwork();
			document = XMLUtils.open(inputSbml);
			Element model = (Element) document.getElementsByTagName("model").item(0);
			bioNetwork.setId(model.getAttribute("id"));
			bioNetwork.setName(model.getAttribute("name"));
			bioNetwork.setType("sbml");
			
			buildReactions();
			buildCompounds();
			buildCompartments();
			buildUnitDefs();
			for(BioChemicalReaction rxn : reactions){
				this.bioNetwork.addBiochemicalReaction(rxn);
			}
			
			
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Builds the compartments.
	 */
	private void buildCompartments(){
		NodeList compartments = document.getElementsByTagName("listOfCompartments").item(0).getChildNodes();
		int l = compartments.getLength();
		for (int i=1; i < l; i+=2) {
			Element compartment = (Element) compartments.item(i);
			String compartmentId = compartment.getAttribute("id");
			BioCompartment cmp = this.compartments.get(compartmentId);
			if(cmp!=null){
				String compartmentName = compartment.getAttribute("name");
				cmp.setName(compartmentName);
				this.bioNetwork.addCompartment(cmp);
				this.compartments.remove(compartmentId);
			}
			if(this.compartments.isEmpty()) break;
		}
		return;
	}
	
	/**
	 * Builds the unit definitions.
	 */
	private void buildUnitDefs(){
		NodeList unitDefinitions = document.getElementsByTagName("listOfUnitDefinitions").item(0).getChildNodes();
		int l = unitDefinitions.getLength();
		for (int i=1; i < l; i+=2) {
			Element unitDefinition = (Element) unitDefinitions.item(i);
			String unitDefinitionId = unitDefinition.getAttribute("id");
			String unitDefinitionName = unitDefinition.getAttribute("name");
			BioUnitDefinition bioUnitDef = new BioUnitDefinition(unitDefinitionId,unitDefinitionName);
			
			NodeList units = unitDefinition.getElementsByTagName("listOfUnits").item(0).getChildNodes();
			int l2 = units.getLength();
			for (int i2=1; i2 < l2; i2+=2) {
						
				Element unit = (Element) units.item(i2);
				String kind = unit.getAttribute("kind").toUpperCase();
				String exponent = unit.getAttribute("exponent");
				String scale = unit.getAttribute("scale");
				String multiplier = unit.getAttribute("multiplier");
	
				UnitSbml bioUnit = new UnitSbml(kind,exponent,scale,multiplier);
				bioUnitDef.addUnit(bioUnit);
			}
			
			this.bioNetwork.addUnitDefinition(bioUnitDef);
		}
		return;
	}
		
	/**
	 * Builds the compounds.
	 */
	private void buildCompounds(){

		NodeList listOfCompounds = document.getElementsByTagName("listOfSpecies").item(0).getChildNodes();
		int l = listOfCompounds.getLength();
		for (int i=1; i < l; i+=2) {
			Element compound = (Element) listOfCompounds.item(i);
			String cpdId = compound.getAttribute("id");
			BioPhysicalEntity cpd = compounds.get(cpdId);
			if(cpd!=null){
//				String charge = compound.hasAttribute("charge") ? compound.getAttribute("charge") : "NA";
//				String formula = compound.hasAttribute("formula") ? compound.getAttribute("formula") : "NA";
//				String mass = compound.hasAttribute("mass") ? compound.getAttribute("mass") : "NA";
				String charge = compound.getAttribute("charge");
				String formula = compound.getAttribute("formula");
				String mass = compound.getAttribute("mass");
				String compartment = compound.getAttribute("compartment");
				
				BioCompartment bioCompartment=null;
				if(!compartments.containsKey(compartment)){
					bioCompartment=new BioCompartment(compartment,compartment);
					compartments.put(compartment, bioCompartment);
				}else{
					bioCompartment = this.compartments.get(compartment);
				}

				cpd.setName(compound.getAttribute("name"));
				cpd.setCompartment(bioCompartment);
				cpd.setCharge(charge);
				cpd.setChemicalFormula(formula);
				cpd.setMolecularWeight(mass);
				
				bioNetwork.addPhysicalEntity(cpd);
				if(parseNote){
					NodeList listsFromMetabolite = compound.getChildNodes();
					for (int j=0; j < listsFromMetabolite.getLength(); j++) {
						Node item = listsFromMetabolite.item(j);
						if ("notes".equals(item.getNodeName())) {
							parseNotes(cpd, item);
						}else if("annotation".equals(item.getNodeName())){
							parseAnnotations(cpd,item);
						}
					}
				}
				
				//fill holes
				if(StringUtils.isVoid(cpd.getChemicalFormula()) && !StringUtils.isVoid(cpd.getInchi())){
					Matcher m = Pattern.compile(".*InChI=1S?/([^/])/.*").matcher(cpd.getInchi());
					if( m.matches()){
						String value = m.group(1);
						cpd.setChemicalFormula(value);
					}
				}
				compounds.remove(cpdId);
			}
			if(compounds.isEmpty()) break;
		}
		return;
	}
	
	/**
	 * Parses the notes fields.
	 *
	 * @param e the entitie
	 * @param valInBody the text value in note field
	 */
	/**
	 * Parses the metabolites notes fields.
	 *
	 * @param e the compound
	 * @param valInBody the text value in note field
	 */
	private void parseNotes(BioEntity e, Node notes){
		NodeList notesChildList = notes.getChildNodes();
		for (int i= 0; i < notesChildList.getLength();i++) {
			Node notesChild = notesChildList.item(i);
			if ("body".equals(notesChild.getNodeName())) {
				NodeList itemList = notesChild.getChildNodes();
				for (int j = 1; j < itemList.getLength();j+=2) {
					String item = itemList.item(j).getTextContent();
					
					for(RefHandler refH : refHandlers){
						Matcher m = Pattern.compile("^\\s*"+refH.getDbName()+notesAttributeToValueSeparator+"(.+)\\s*$",Pattern.CASE_INSENSITIVE).matcher(item);
						if( m.matches()){
							if(refH.isAllowingMultipleValues()){
								String[] values = m.group(1).split(Pattern.quote(notesValueSeparator));
								for(String refId : values){
									if(refId.matches("^"+refH.getValidIdRegex()+"$")){
										BioRef ref = new BioRef("sbml file", refH.getDbName(),refId, 1);
										ref.setBaseURI(refH.getBaseUrl());
										ref.setLogicallink("is");
										e.addRef(ref);
									}
								}
							}else{
								if(m.group(1).matches("^"+refH.getValidIdRegex()+"$")){
									BioRef ref = new BioRef("sbml file", refH.getDbName(),m.group(1), 1);
									ref.setBaseURI(refH.getBaseUrl());
									ref.setLogicallink("is");
									e.addRef(ref);
								}
							}
						}
					}
					
					if(e instanceof BioPhysicalEntity){
						BioPhysicalEntity c = (BioPhysicalEntity) e;
						setFormulaFromNotes(c, item);
						setInChiFromNotes(c, item);
						setSMILESFromNotes(c, item);
						
					}else if(e instanceof BioChemicalReaction){
						BioChemicalReaction r = (BioChemicalReaction) e;
						setPathwayFromNotes(r, item);
						setECFromNotes(r, item);
					}
				}
			}
		}
		return;
	}
	
	private void setPathwayFromNotes(BioChemicalReaction rxn, String notesTxtContent){
		if(rxn.getPathwayList().isEmpty()){
			Matcher m = Pattern.compile(".*SUBSYSTEM"+notesAttributeToValueSeparator+"(.+).*",Pattern.CASE_INSENSITIVE).matcher(notesTxtContent);
			if( m.matches()){
				String pathways = m.group(1).replaceAll("[^\\p{ASCII}]", "");
				for(String value : pathways.split(Pattern.quote(notesValueSeparator))){
					if(bioNetwork.getPathwayList().containsKey(value)){
						rxn.addPathway(bioNetwork.getPathwayList().get(value));
					}else{
						BioPathway bionetPath=new BioPathway(value,value);
						bioNetwork.addPathway(bionetPath);
						rxn.addPathway(bionetPath);
					}
				}
			}
		}
	}
	
	private void setECFromNotes(BioChemicalReaction rxn, String notesTxtContent){
		if(StringUtils.isVoid(rxn.getEcNumber())){
			Matcher m,m2;
			m = Pattern.compile(".*PROTEIN.CLASS"+notesAttributeToValueSeparator+"(.+).*",Pattern.CASE_INSENSITIVE).matcher(notesTxtContent);
			m2 = Pattern.compile(".*EC.NUMBER"+notesAttributeToValueSeparator+"(EC-)?(.+).*",Pattern.CASE_INSENSITIVE).matcher(notesTxtContent);
			String value = "NA";
			if( m.matches()){
				value = m.group(1);
				if (value.equals("")){
					value="NA";
				}
			}else if( m2.matches()){
				value = m2.group(2);
				if (value.equals("")){
					value="NA";
				}
			}
			rxn.setEcNumber(value);
		}
	}
	
	private void setFormulaFromNotes(BioPhysicalEntity cpd, String notesTxtContent){
		if(StringUtils.isVoid(cpd.getChemicalFormula())){
			//get the formula
			Matcher m = Pattern.compile("^\\s*FORMULA"+notesAttributeToValueSeparator+"(.+)\\s*$",Pattern.CASE_INSENSITIVE).matcher(notesTxtContent);
			if( m.matches()){
				String value = m.group(1);
				cpd.setChemicalFormula(value);
			}
		}
	}
	
	private void setInChiFromNotes(BioPhysicalEntity cpd, String notesTxtContent){
		if(StringUtils.isVoid(cpd.getInchi())){
			//get the Inchi
			Matcher m = Pattern.compile("^\\s*INCHI"+notesAttributeToValueSeparator+"(.+)\\s*$",Pattern.CASE_INSENSITIVE).matcher(notesTxtContent);
			if( m.matches()){
				String value = m.group(1);
				if(value.contains(notesValueSeparator)){
					value=value.split(Pattern.quote(notesValueSeparator))[0];
				}
				cpd.setInchi(value);
			}
		}	
	}
	
	private void setSMILESFromNotes(BioPhysicalEntity cpd, String notesTxtContent){
		if(StringUtils.isVoid(cpd.getSmiles())){
			//get the SMILES :)
			Matcher m = Pattern.compile("^\\s*SMILES"+notesAttributeToValueSeparator+"(.+)\\s*$",Pattern.CASE_INSENSITIVE).matcher(notesTxtContent);
			if( m.matches()){
				String value = m.group(1);
				if(value.contains(notesValueSeparator)){
					value=value.split(Pattern.quote(notesValueSeparator))[0];
				}
				cpd.setSmiles(value);
			} 
		}
	}
	
	
	
	private void parseAnnotations(BioEntity e, Node annotation){
		
		NodeList annotationChild = annotation.getChildNodes();

		for(int i0=0; i0<annotationChild.getLength(); i0++){
			NodeList rdfChild = annotationChild.item(i0).getChildNodes();
			for(int i=0; i<rdfChild.getLength(); i++){

				NodeList description = rdfChild.item(i).getChildNodes();
				for(int j=0; j<description.getLength(); j++){
					Node item = description.item(j);

					Matcher m = Pattern.compile("^bqbiol:(.+)$").matcher(item.getNodeName());
					if( m.matches()){
						String relation = m.group(1);
						NodeList relationChild = item.getChildNodes();
						for(int k=0; k<relationChild.getLength(); k++){
							Node bag = relationChild.item(k);

							if(bag.getNodeName().equals("rdf:Bag")){
								NodeList bagContent = bag.getChildNodes();
								for(int l=1; l<relationChild.getLength(); l+=2){

									Element link = (Element) bagContent.item(l);
									if(link.getNodeName().equals("rdf:li")){
										String url = link.getAttribute("rdf:resource");
										for(RefHandler refH : refHandlers){
											Matcher m2 = Pattern.compile(refH.getBaseUrl()+"("+refH.getValidIdRegex()+")$").matcher(url);
											if(m2.matches()){
												BioRef ref = new BioRef("sbml file", refH.getDbName(), m2.group(1), 1);
												ref.setBaseURI(refH.getBaseUrl());
												ref.setLogicallink(relation);
												e.addRef(ref);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return;
	}
	
	/**
	 * Builds the reactions.
	 */
	private void buildReactions(){
		NodeList reactionsList = document.getElementsByTagName("listOfReactions").item(0).getChildNodes();
		int l = reactionsList.getLength();
		for (int i = 1; i < l; i+=2) {
			Element reaction = (Element) reactionsList.item(i);
			BioChemicalReaction rxn = new BioChemicalReaction(reaction.getAttribute("id"),reaction.getAttribute("name"));
			rxn.setReversibility("true".equals(reaction.getAttribute("reversible")));
			NodeList listsFromReaction = reaction.getChildNodes();
			int l2 = listsFromReaction.getLength();
			for (int j = 0; j < l2; j++) {
				Node node = listsFromReaction.item(j);
				if ("listOfReactants".equals(node.getNodeName())) {
					NodeList listOfReactants = node.getChildNodes();
					int l3 = listOfReactants.getLength();
					for (int k = 1; k < l3; k+=2) {
						Element reactant = (Element) listOfReactants.item(k);
						String species = reactant.getAttribute("species");
						String stochio = reactant.getAttribute("stoichiometry");
						BioPhysicalEntity c = null;
						if(!compounds.containsKey(species)){
							c = new BioPhysicalEntity(species);
							compounds.put(species, c);
						}else{
							c = compounds.get(species);
						}
						BioPhysicalEntityParticipant participant = new BioPhysicalEntityParticipant(c, stochio);
						rxn.addLeftParticipant(participant);
					}
				}

				if ("listOfProducts".equals(node.getNodeName())) {
					NodeList listOfProducts = node.getChildNodes();
					int l3 = listOfProducts.getLength();
					for (int k = 1; k < l3; k+=2) {
						Element product = (Element) listOfProducts.item(k);
						String species = product.getAttribute("species");
						String stochio = product.getAttribute("stoichiometry");
						BioPhysicalEntity c = null;
						if(!compounds.containsKey(species)){
							c = new BioPhysicalEntity(species);
							compounds.put(species, c);
						}else{
							c = compounds.get(species);
						}
						BioPhysicalEntityParticipant participant = new BioPhysicalEntityParticipant(c, stochio);
						rxn.addRightParticipant(participant);
					}
				}
				
				if ("notes".equals(node.getNodeName()) && parseNote){
					parseNotes(rxn, node);
				}else if("annotation".equals(node.getNodeName()) && parseNote){
					parseAnnotations(rxn,node);
				}
			}
			reactions.add(rxn);
		}
		return;
	}

	/**
	 * Checks if notes should be parsed.
	 *
	 * @return true, if parses the note
	 */
	public boolean isParseNote() {
		return parseNote;
	}

	/**
	 * Sets the parses the note.
	 *
	 * @param parseNote the new parses the note
	 */
	public void setParseNote(boolean parseNote) {
		this.parseNote = parseNote;
	}
	
	/**
	 * Gets the bio network.
	 *
	 * @return the bio network
	 */
	public BioNetwork getBioNetwork() {
		if(this.bioNetwork!=null){
			return this.bioNetwork;
		}
		build();
		return this.bioNetwork;
	}
	
	public String getNotesValueSeparator() {
		return notesValueSeparator;
	}

	public void setNotesValueSeparator(String notesValueSeparator) {
		this.notesValueSeparator = notesValueSeparator;
	}

	public String getNotesAttributeToValueSeparator() {
		return notesAttributeToValueSeparator;
	}

	public void setNotesAttributeToValueSeparator(
			String notesAttributeToValueSeparator) {
		this.notesAttributeToValueSeparator = notesAttributeToValueSeparator;
	}

	public Collection<RefHandler> getRefHandlers() {
		return refHandlers;
	}
	public void addRefHandlers(RefHandler refHandler) {
		this.refHandlers.add(refHandler);
	}
	public void setRefHandlers(Collection<RefHandler> refHandlers) {
		this.refHandlers = refHandlers;
	}
	
	/**
	 * Use default reference handler (chebi ids, EC numbers, HMDB ids, InChI, InChIKey, KEGG compound ids,
	 * KEGG gene ids, PubChem compound CID, PubChem substance SID, pubmed numbers, uniprot ids).
	 */
	public void addDefaultRefHandlers(){
		this.addRefHandlers(RefHandler.CHEBI_HANDLER);
		this.addRefHandlers(RefHandler.EC_NUMBER_HANDLER);
		this.addRefHandlers(RefHandler.HMDB_HANDLER);
		this.addRefHandlers(RefHandler.INCHI_HANDLER);
		this.addRefHandlers(RefHandler.INCHIKEY_HANDLER);
		this.addRefHandlers(RefHandler.KEGG_COMPOUND);
		this.addRefHandlers(RefHandler.KEGG_GENES_HANDLER);
		this.addRefHandlers(RefHandler.PUBCHEM_COMPOUND_HANDLER);
		this.addRefHandlers(RefHandler.PUBCHEM_SUBSTANCE_HANDLER);
		this.addRefHandlers(RefHandler.PUBMED_HANDLER);
		this.addRefHandlers(RefHandler.UNIPROT_HANDLER);
		this.addRefHandlers(RefHandler.UNIPATHWAY_HANDLER);
		this.addRefHandlers(RefHandler.SEED_HANDLER);
		this.addRefHandlers(RefHandler.REACTOME_HANDLER);
		this.addRefHandlers(RefHandler.METANETX_HANDLER);
		this.addRefHandlers(RefHandler.METACYC_HANDLER);
		this.addRefHandlers(RefHandler.BRENDA_HANDLER);
	}
}