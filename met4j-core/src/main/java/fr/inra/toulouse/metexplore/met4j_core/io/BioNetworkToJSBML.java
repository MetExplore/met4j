/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Level;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.CompartmentType;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.LocalParameter;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;
import org.sbml.jsbml.text.parser.ParseException;
import org.sbml.jsbml.Parameter;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Comment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Flux;
import fr.inra.toulouse.metexplore.met4j_core.biodata.Notes;
import fr.inra.toulouse.metexplore.met4j_core.biodata.UnitSbml;
import fr.inra.toulouse.metexplore.met4j_core.utils.BioChemicalReactionUtils;
import fr.inra.toulouse.metexplore.met4j_core.utils.JSBMLUtils;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;




public class BioNetworkToJSBML {

	static {
		try {
			JSBMLUtils.setDefaultLog4jConfiguration();
		} catch (IOException e) {}
		JSBMLUtils.setJSBMLlogToLevel(Level.ERROR);
	}
	
	private BioNetwork bionet;
	private SBMLDocument sbmlDoc=new SBMLDocument(3,1);
	private String filename;
	private SBMLWriter sbmlwriter;
	private Boolean checkConsistency=false;


	private boolean processing=true;



	public BioNetworkToJSBML(BioNetwork inputBionet, String outputFile) throws FileNotFoundException, IOException {

		this.setBionet(inputBionet);
		this.setFilename(outputFile);
		
		this.setWriter(new SBMLWriter());
	}

	public BioNetworkToJSBML(BioNetwork inputBionet, String outputFile, int level, int version) throws FileNotFoundException, IOException {

		this.setBionet(inputBionet);
		this.setFilename(outputFile);
		this.setSbmlDoc(new SBMLDocument(level,version));

		this.setWriter(new SBMLWriter());

	}

	public BioNetworkToJSBML(BioNetwork inputBionet, String outputFile, int level, int version, boolean check) throws FileNotFoundException, IOException {

		this.setBionet(inputBionet);
		this.setFilename(outputFile);
		this.setSbmlDoc(new SBMLDocument(level,version));
		
		this.setCheckConsistency(check);

		this.setWriter(new SBMLWriter());

	}

	public BioNetworkToJSBML(BioNetwork inputBionet, String outputFile, int level, int version, boolean check,  boolean proccess) throws FileNotFoundException, IOException {

		this.setBionet(inputBionet);
		this.setFilename(outputFile);
		this.setSbmlDoc(new SBMLDocument(level,version));
		this.setCheckConsistency(check);

		this.setProccessing(proccess);

		this.setWriter(new SBMLWriter());

	}



	public void write () throws SBMLException, FileNotFoundException, XMLStreamException, ParseException{

		this.convertBionet();

		if (checkConsistency){
			sbmlDoc.checkConsistency();
		}
		boolean failedConsistency=false;

		for (int i = 0; i < sbmlDoc.getErrorLog().getNumErrors(); i++ ){
			if (sbmlDoc.getErrorLog().getError(i).isError() || sbmlDoc.getErrorLog().getError(i).isFatal()){
//				System.err.println(sbmlDoc.getErrorLog().getError(i).getMessage());
				failedConsistency=true;
			}
		}

		if(!failedConsistency){
			//System.err.println("File Exported");

			if (this.processing){
				this.getWriter().write(this.getSbmlDoc(), filename+".unprocessed");

				System.err.println("Post-Processing generated file...");
				String[] args=new String[1];
				args[0]=filename;
				try {
					PostProcessSBML.main(args);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.err.println("Post-Processing completed");
			}else{
				this.getWriter().write(this.getSbmlDoc(), filename);
			}
			
		}
		else{
			System.err.println("Error(s) while parsing the the SBML file: ");
		}
	}

	private void convertBionet() throws XMLStreamException, ParseException{


//		if(this.processing){

			//encoding ids to SID type for libsml: always Needed!!
			System.err.println("Processing Network...");
			ProcessBioNetWorkIds proc=new ProcessBioNetWorkIds(bionet, true);
			proc.process();
			System.err.println("Done");
//		}


//		long startTime = System.nanoTime();
		Model jsbmlModel=this.getSbmlDoc().createModel(this.bionet.getId());
		jsbmlModel.setName(this.bionet.getName());
//		if (this.bionet.getModelAnnot()!=null && this.bionet.getModelAnnot().getMetaId()!=null){
//			jsbmlModel.setMetaId(this.bionet.getModelAnnot().getMetaId());
//
//			jsbmlModel.setAnnotation(new Annotation(this.bionet.getModelAnnot().getXMLasString()));
//		}
		if (this.getBionet().getModelNotes()!=null){

			jsbmlModel.setNotes(this.getBionet().getModelNotes().getXHTMLasString());
		}
		GetListOfUnitDef(jsbmlModel);
		GetListOfCompartments(jsbmlModel);
		GetListOfSpecies(jsbmlModel);
		GetListOfReactions(jsbmlModel);
		GetUnUsedData(jsbmlModel);



//		long elapsedTime = System.nanoTime() - startTime;
//
//		if (elapsedTime/1000000000==0){
//			System.err.println("Time to generate the SBML objects from the bionetwork in miliseconde : "
//					+ elapsedTime/1000000);
//		}
//		else{
//			System.err.println("Time to generate the SBML objects from the bionetwork object in seconde : "
//					+ elapsedTime/1000000000);
//		}
	}

	private void GetListOfUnitDef(Model jsbmlModel) {

		for (BioUnitDefinition bioUD: this.bionet.getUnitDefinitions().values()){
			UnitDefinition LibSBMLUD=jsbmlModel.createUnitDefinition();

			LibSBMLUD.setId(bioUD.getId());
			LibSBMLUD.setName(bioUD.getName());

			for (UnitSbml bioUnits: bioUD.getUnits().values()){
				Unit libSBMLUnit=LibSBMLUD.createUnit();
				libSBMLUnit.setExponent(Double.parseDouble(bioUnits.getExponent()));
				libSBMLUnit.setMultiplier(Double.parseDouble(bioUnits.getMultiplier()));
				libSBMLUnit.setScale(Integer.parseInt(bioUnits.getScale()));

				libSBMLUnit.setKind(Kind.valueOf(bioUnits.getKind()));
			}
		}

	}

	private void GetListOfCompartments(Model jsbmlModel) throws XMLStreamException {
		for (BioCompartment compart: this.bionet.getCompartments().values()){
			Compartment LibSBMLCompart=jsbmlModel.createCompartment();

			LibSBMLCompart.setName(compart.getName());
			LibSBMLCompart.setId(compart.getId());
			LibSBMLCompart.setConstant(compart.isConstant());
			if (!StringUtils.isVoid(compart.getSboterm())){
				LibSBMLCompart.setSBOTerm(compart.getSboterm());
			}
			LibSBMLCompart.setSize(compart.getSize());
			LibSBMLCompart.setSpatialDimensions(compart.getSpatialDimensions());

//			if (compart.getCompartAnnot()!=null && compart.getCompartAnnot().getMetaId()!=null){
//				LibSBMLCompart.setMetaId(compart.getCompartAnnot().getMetaId());
//				LibSBMLCompart.setAnnotation(new Annotation(compart.getCompartAnnot().getXMLasString()));
//			}
			
			
			//		if(bioEnt.getEntityAnnot()!=null && bioEnt.getEntityAnnot().getMetaId()!=null && bioEnt.getRefs()!=null){
//			
//			LibSBMLSpecie.setMetaId(bioEnt.getEntityAnnot().getMetaId());
//			createAnnotationFromRef(LibSBMLSpecie, bioEnt);
//			
//		}else if (bioEnt.getEntityAnnot()!=null &&  bioEnt.getEntityAnnot().getMetaId()!=null){
//			
//			LibSBMLSpecie.setMetaId(bioEnt.getEntityAnnot().getMetaId());
//			LibSBMLSpecie.setAnnotation(new Annotation(bioEnt.getEntityAnnot().getXMLasString()));
//			
//		}else if (bioEnt.getRefs()!=null){
//			
//			LibSBMLSpecie.setMetaId(this.getSbmlDoc().nextMetaId());
//			createAnnotationFromRef(LibSBMLSpecie, bioEnt);
//		}
			if(compart.getCompartNotes()!=null){
				LibSBMLCompart.setNotes(compart.getCompartNotes().getXHTMLasString());
			}
			if(compart.getOutsideCompartment()!=null && jsbmlModel.getLevel()<3){
				LibSBMLCompart.setOutside(compart.getOutsideCompartment().getId());
			}
			if(compart.getCompartmentType()!=null && jsbmlModel.getLevel()!=3 ){

				if(jsbmlModel.getCompartmentType(compart.getCompartmentType().getId())==null){
					CompartmentType cmpType=jsbmlModel.createCompartmentType();
					cmpType.setId(compart.getCompartmentType().getId());
					cmpType.setName(compart.getCompartmentType().getName());
				}
				LibSBMLCompart.setCompartmentType(compart.getCompartmentType().getId());

			}
		}
	}


	private void GetListOfSpecies(Model jsbmlModel) throws XMLStreamException {
		for (BioPhysicalEntity bioEnt: this.bionet.getPhysicalEntityList().values()){

			getSpecieData(jsbmlModel,bioEnt);
		}

		for (BioProtein bioEnt: this.bionet.getProteinList().values()){
			if(!jsbmlModel.containsSpecies(bioEnt.getId())){
				getSpecieData(jsbmlModel,bioEnt);
			}
		}

		for (BioComplex bioEnt: this.bionet.getComplexList().values()){

			if(!jsbmlModel.containsSpecies(bioEnt.getId()) ){
				getSpecieData(jsbmlModel,bioEnt);
			}
		}

	}

	/**
	 * 
	 * @param jsbmlModel
	 * @param bioEnt
	 * @return
	 * @throws XMLStreamException 
	 */
	private Species getSpecieData(Model jsbmlModel, BioPhysicalEntity bioEnt) throws XMLStreamException {

		Species LibSBMLSpecie=jsbmlModel.createSpecies();

		LibSBMLSpecie.setId(bioEnt.getId());
		LibSBMLSpecie.setName(bioEnt.getName());
		LibSBMLSpecie.setBoundaryCondition(bioEnt.getBoundaryCondition());
		if(bioEnt.getCompartment()==null){
//			System.err.println(bioEnt.getClass().getSimpleName()+" '"+bioEnt.getId()+"' named '"+bioEnt.getName()+"' a un compartiment null");
			LibSBMLSpecie.setCompartment(bioEnt.getCompartment().getId());
		}
		else if(bioEnt.getCompartment().getId()==null)	{
//			System.err.println(bioEnt.getClass().getSimpleName()+" '"+bioEnt.getId()+"' named '"+bioEnt.getName()+"' a un compartiment dont l'id est null");
			LibSBMLSpecie.setCompartment(bioEnt.getCompartment().getId());
		}
		else{
			LibSBMLSpecie.setCompartment(bioEnt.getCompartment().getId());
		}


		LibSBMLSpecie.setConstant(bioEnt.getConstant());

		if(!StringUtils.isVoid(bioEnt.getSboterm())){
			LibSBMLSpecie.setSBOTerm(bioEnt.getSboterm());
		}

		LibSBMLSpecie.setHasOnlySubstanceUnits(bioEnt.getHasOnlySubstanceUnit());

		if(bioEnt.getInitialQuantity().size()==1 ){
			for(Entry<String, Double> quantity:bioEnt.getInitialQuantity().entrySet()){
				if (quantity.getKey().equals("amount")){
					LibSBMLSpecie.setInitialAmount(quantity.getValue());
				}
				else if (quantity.getKey().equals("concentration")){
					LibSBMLSpecie.setInitialConcentration(quantity.getValue());
				}
			}
		}

		if(jsbmlModel.getLevel()!=3 && (bioEnt.getCharge()!=null || !bioEnt.getCharge().isEmpty())){
			LibSBMLSpecie.setCharge(Integer.parseInt(bioEnt.getCharge()));
		}

//		if(bioEnt.getEntityAnnot()!=null && bioEnt.getEntityAnnot().getMetaId()!=null && bioEnt.getRefs()!=null){
//			
//			LibSBMLSpecie.setMetaId(bioEnt.getEntityAnnot().getMetaId());
//			createAnnotationFromRef(LibSBMLSpecie, bioEnt);
//			
//		}else if (bioEnt.getEntityAnnot()!=null &&  bioEnt.getEntityAnnot().getMetaId()!=null){
//			
//			LibSBMLSpecie.setMetaId(bioEnt.getEntityAnnot().getMetaId());
//			LibSBMLSpecie.setAnnotation(new Annotation(bioEnt.getEntityAnnot().getXMLasString()));
//			
//		}else if (bioEnt.getRefs()!=null){
//			
//			LibSBMLSpecie.setMetaId(this.getSbmlDoc().nextMetaId());
//			createAnnotationFromRef(LibSBMLSpecie, bioEnt);
//		}

		updateNotesFromData(bioEnt, LibSBMLSpecie);

		return LibSBMLSpecie;
	}


	/* DEPRECATED
	private void OLDinjectAttributesToNotes(BioPhysicalEntity bioEnt) {
		//first inject the InChI
		if (bioEnt.getInchi()!=null){
			String Inchi=bioEnt.getInchi();
			//test if metabolite has notes
			if(bioEnt.getEntityNotes()!=null && !bioEnt.getEntityNotes().getXHTMLasString().equals("")){

				String note=bioEnt.getEntityNotes().getXHTMLasString();

				Matcher m1,m2;
				m1 = Pattern.compile(".*INCHI:\\s+([^<]+)<.*").matcher(note.replaceAll(">\\s*<", "><"));
				if (m1.matches()){
					if(m1.group(1).equalsIgnoreCase("na")){
						note=note.replaceAll(m1.group(1), Inchi);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
				}
				else{
					m2 = Pattern.compile(".*INCHI:\\s+<.*").matcher(note.replaceAll(">\\s*<", "><"));
					if (m2.matches()){
						note=note.replaceAll("INCHI: ", "INCHI: "+Inchi);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
					else{
						String ComplementaryNote="<p>INCHI: "+Inchi+"</p>\n</body>\n</notes>";
						note=note.replaceAll("</body>\\s*</notes>", ComplementaryNote);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
				}
			}else{//if it doen't have note, we create it
				String newNotes="<notes>\n<body xmlns=\"http://www.w3.org/1999/xhtml\">"
						+ "\n<p>INCHI: "+Inchi+"</p>\n</body>\n</notes>";
				bioEnt.setEntityNotes(new Notes(newNotes));
			}
		}

		//Use The same step for the Smiles
		if (bioEnt.getSmiles()!=null){
			String Smiles=bioEnt.getSmiles();
			//test if metabolite has notes
			if(bioEnt.getEntityNotes()!=null && !bioEnt.getEntityNotes().getXHTMLasString().equals("")){

				String note=bioEnt.getEntityNotes().getXHTMLasString();

				Matcher m1,m2;
				m1 = Pattern.compile(".*SMILES:\\s+([^<]+)<.*").matcher(note.replaceAll(">\\s*<", "><"));
				if (m1.matches()){
					if(m1.group(1).equalsIgnoreCase("na")){
						note=note.replaceAll(m1.group(1), Smiles);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
				}
				else{
					m2 = Pattern.compile(".*SMILES:\\s+<.*").matcher(note.replaceAll(">\\s*<", "><"));
					if (m2.matches()){
						note=note.replaceAll("SMILES: ", "SMILES: "+Smiles);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
					else{
						String ComplementaryNote="<p>SMILES: "+Smiles+"</p>\n</body>\n</notes>";
						note=note.replaceAll("</body>\\s*</notes>", ComplementaryNote);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
				}
			}else{//if it doen't have note, we create it
				String newNotes="<notes>\n<body xmlns=\"http://www.w3.org/1999/xhtml\">"
						+ "\n<p>SMILES: "+Smiles+"</p>\n</body>\n</notes>";
				bioEnt.setEntityNotes(new Notes(newNotes));
			}
		}

		//Use The same step for the PubChemCID
		if (bioEnt.getPubchemCID()!=null){
			String PubchemCID=bioEnt.getPubchemCID();
			//test if metabolite has notes
			if(bioEnt.getEntityNotes()!=null && !bioEnt.getEntityNotes().getXHTMLasString().equals("")){

				String note=bioEnt.getEntityNotes().getXHTMLasString();

				Matcher m1,m2;
				m1 = Pattern.compile(".*PUBCHEM_CID:\\s+([^<]+)<.*").matcher(note.replaceAll(">\\s*<", "><"));
				if (m1.matches()){
					if(m1.group(1).equalsIgnoreCase("na")){
						note=note.replaceAll(m1.group(1), PubchemCID);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
				}
				else{
					m2 = Pattern.compile(".*PUBCHEM_CID:\\s+<.*").matcher(note.replaceAll(">\\s*<", "><"));
					if (m2.matches()){
						note=note.replaceAll("PUBCHEM_CID: ", "PUBCHEM_CID: "+PubchemCID);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
					else{
						String ComplementaryNote="<p>PUBCHEM_CID: "+PubchemCID+"</p>\n</body>\n</notes>";
						note=note.replaceAll("</body>\\s*</notes>", ComplementaryNote);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
				}
			}else{//if it doen't have note, we create it
				String newNotes="<notes>\n<body xmlns=\"http://www.w3.org/1999/xhtml\">"
						+ "\n<p>PUBCHEM_CID: "+PubchemCID+"</p>\n</body>\n</notes>";
				bioEnt.setEntityNotes(new Notes(newNotes));
			}
		}


	}*/

	/**
	 * 
	 * @param jsbmlModel
	 * @throws XMLStreamException
	 * @throws ParseException
	 */
	private void GetListOfReactions(Model jsbmlModel) throws XMLStreamException, ParseException {
		for (BioChemicalReaction bionetReaction: this.bionet.getBiochemicalReactionList().values()){
			Reaction libSBMLReaction=jsbmlModel.createReaction();

			libSBMLReaction.setId(bionetReaction.getId());
			libSBMLReaction.setName(bionetReaction.getName());
			libSBMLReaction.setFast(Boolean.parseBoolean(bionetReaction.getSpontaneous()));
			
			if (!StringUtils.isVoid(bionetReaction.getSboterm())){
				libSBMLReaction.setSBOTerm(bionetReaction.getSboterm());
			}

			if(bionetReaction.getReversiblity().equals("reversible")){
				libSBMLReaction.setReversible(true);
			}
			else{
				libSBMLReaction.setReversible(false);
			}
			//Set the substrates of the reaction
			for (BioPhysicalEntityParticipant BionetLParticipant : bionetReaction.getLeftParticipantList().values()) {
				SpeciesReference specieRef=libSBMLReaction.createReactant();
				specieRef.setSpecies(BionetLParticipant.getPhysicalEntity().getId());
				specieRef.setStoichiometry(Double.parseDouble(BionetLParticipant.getStoichiometricCoefficient()));

				/*if(BionetLParticipant.getId()!=null){
					specieRef.setId(BionetLParticipant.getId());
				}*/
				if(this.getSbmlDoc().getLevel()==3){
					if(BionetLParticipant.getIsConstant()!=null){
						specieRef.setConstant(BionetLParticipant.getIsConstant());
					}else{
						specieRef.setConstant(false);
					}
				}
			}
			// set the products of the reaction
			for (BioPhysicalEntityParticipant BionetRParticipant : bionetReaction.getRightParticipantList().values()) {
				SpeciesReference specieRef=libSBMLReaction.createProduct();
				specieRef.setSpecies(BionetRParticipant.getPhysicalEntity().getId());
				specieRef.setStoichiometry(Double.parseDouble(BionetRParticipant.getStoichiometricCoefficient()));

				/*if(BionetRParticipant.getId()!=null){
					specieRef.setId(BionetRParticipant.getId());
				}*/
				if(this.getSbmlDoc().getLevel()==3){
					if(BionetRParticipant.getIsConstant()!=null){
						specieRef.setConstant(BionetRParticipant.getIsConstant());
					}else{
						specieRef.setConstant(false);
					}
				}
			}
			//set the enzyme/modifiers of the reactions
			for(Entry<String, BioPhysicalEntity> bionetEnz:bionetReaction.getEnzList().entrySet()){
				ModifierSpeciesReference specieMod=libSBMLReaction.createModifier();
				specieMod.setSpecies(bionetEnz.getKey());
			}

			KineticLaw Kinetic=libSBMLReaction.createKineticLaw();

			if ( !StringUtils.isVoid( bionetReaction.getKineticFormula() ) ){
				Kinetic.setFormula(bionetReaction.getKineticFormula());
			}else{
				Kinetic.setFormula("NA");
			}

			if(jsbmlModel.getLevel()<3){
				if (bionetReaction.getLowerBound()!=null && bionetReaction.getUpperBound()!=null){
					Parameter LBound=new Parameter();
					LBound.setId("LOWER_BOUND");
					LBound.setValue(Double.parseDouble(bionetReaction.getLowerBound().value));
					LBound.setUnits(bionetReaction.getLowerBound().unitDefinition.getId());

					Kinetic.addParameter(LBound);

					Parameter UBound=new Parameter();
					UBound.setId("UPPER_BOUND");
					UBound.setValue(Double.parseDouble(bionetReaction.getUpperBound().value));
					UBound.setUnits(bionetReaction.getUpperBound().unitDefinition.getId());
					Kinetic.addParameter(UBound);

				}
				for(Entry<String, Flux> moreParam:bionetReaction.getListOfAdditionalFluxParam().entrySet()){
					Parameter param=new Parameter();
					param.setId(moreParam.getKey());
					param.setValue(Double.parseDouble(moreParam.getValue().value));
					param.setUnits(moreParam.getValue().unitDefinition.getId());

					Kinetic.addParameter(param);
				}
			}else{
				if (bionetReaction.getLowerBound()!=null && bionetReaction.getUpperBound()!=null){
					LocalParameter LBound=Kinetic.createLocalParameter();
					LBound.setId("LOWER_BOUND");
					LBound.setValue(Double.parseDouble(bionetReaction.getLowerBound().value));
					LBound.setUnits(bionetReaction.getLowerBound().unitDefinition.getId());

					LocalParameter UBound=Kinetic.createLocalParameter();
					UBound.setId("UPPER_BOUND");
					UBound.setValue(Double.parseDouble(bionetReaction.getUpperBound().value));
					UBound.setUnits(bionetReaction.getUpperBound().unitDefinition.getId());
				}
				for(Entry<String, Flux> moreParam:bionetReaction.getListOfAdditionalFluxParam().entrySet()){
					
//					System.err.println("param : "+moreParam.getKey()+" in reaction "+bionetReaction.getId());
					
					LocalParameter param=Kinetic.createLocalParameter();
					param.setId(moreParam.getKey());
					param.setValue(Double.parseDouble(moreParam.getValue().value));
					param.setUnits(moreParam.getValue().unitDefinition.getId());
				}
			}

//			if (bionetReaction.getEntityAnnot()!=null && bionetReaction.getEntityAnnot().getMetaId()!=null){
//				libSBMLReaction.setMetaId(bionetReaction.getEntityAnnot().getMetaId());
//				libSBMLReaction.setAnnotation(new Annotation(bionetReaction.getEntityAnnot().getXMLasString()));
//			}

			updateNotesFromData(bionetReaction, libSBMLReaction);	

		}

	}

	/**
	 * 
	 * @param jsbmlModel
	 * @throws XMLStreamException
	 */
	private void GetUnUsedData(Model jsbmlModel) throws XMLStreamException {

		SBMLReader tmpreader=new SBMLReader();
		Model tmpmodel;
		for (Entry<String, String> data: this.bionet.getUnusedSBMLData().entrySet()){

			tmpmodel=tmpreader.readSBMLFromString(data.getValue()).getModel();

			if (data.getKey().equalsIgnoreCase("ListOfConstraints")){
				jsbmlModel.setListOfConstraints(tmpmodel.getListOfConstraints().clone());
			}
			if (data.getKey().equalsIgnoreCase("ListOfEvents")){
				jsbmlModel.setListOfEvents(tmpmodel.getListOfEvents().clone());
			}
			if (data.getKey().equalsIgnoreCase("ListOfFunctionDefinitions")){
				jsbmlModel.setListOfFunctionDefinitions(tmpmodel.getListOfFunctionDefinitions().clone());
			}
			if (data.getKey().equalsIgnoreCase("ListOfInitialAssignments")){
				jsbmlModel.setListOfInitialAssignments(tmpmodel.getListOfInitialAssignments().clone());
			}
			if (data.getKey().equalsIgnoreCase("ListOfParameters")){
				jsbmlModel.setListOfParameters(tmpmodel.getListOfParameters().clone());
			}
			if (data.getKey().equalsIgnoreCase("ListOfRules")){
				jsbmlModel.setListOfRules(tmpmodel.getListOfRules().clone());
			}
			if (data.getKey().equalsIgnoreCase("ListOfSpeciesTypes")){
				jsbmlModel.setListOfSpeciesTypes(tmpmodel.getListOfSpeciesTypes().clone());
			}

		}


	}

	/**
	 * 
	 * @param bionetReaction
	 * @param libSBMLReaction
	 * @throws XMLStreamException
	 */
	private void updateNotesFromData(BioChemicalReaction bionetReaction, Reaction libSBMLReaction) throws XMLStreamException {

		String oldNotes;
		if (bionetReaction.getEntityNotes()!=null){
			oldNotes=bionetReaction.getEntityNotes().getXHTMLasString().replaceAll(">\\s*<", "><");
		}else{
			oldNotes="<body xmlns=\"http://www.w3.org/1999/xhtml\"></body>";
		}
		
		
		String notesToAppend="";

		Matcher m,m2;
		//Update the pathways
		String newPathwayNotes="";
		int i=0;
		for(BioPathway pthw :bionetReaction.getPathwayList().values()){
			if(i==0){
				newPathwayNotes+=" "+pthw.getName().replaceAll("&", "&amp;");
			}else{
				newPathwayNotes+=" || "+pthw.getName().replaceAll("&", "&amp;");
			}
			i++;
		}		

		m = Pattern.compile(".*[> ]+SUBSYSTEM:\\s+([^<]+)<.*").matcher(oldNotes);
		if( m.matches() && !StringUtils.isVoid(newPathwayNotes)){
			oldNotes=oldNotes.replaceAll(escapeSpecialRegexChars(m.group(1)), newPathwayNotes);
		}else if (!StringUtils.isVoid(newPathwayNotes)){
			notesToAppend+="<p>SUBSYSTEM: "+newPathwayNotes+"</p>";			
		}

		//Update the ec number 
		m = Pattern.compile(".*[> ]+PROTEIN.CLASS:\\s+([^<]+)<.*").matcher(oldNotes);
		m2 = Pattern.compile(".*[> ]+EC.Number:\\s+([^<]+)<.*").matcher(oldNotes);
		if( m.matches()){
			if (!StringUtils.isVoid(bionetReaction.getEcNumber())){
				oldNotes=oldNotes.replaceAll(escapeSpecialRegexChars(m.group(1)), bionetReaction.getEcNumber());
			}
		}else if( m2.matches()){
			if (!StringUtils.isVoid(bionetReaction.getEcNumber())){
				oldNotes=oldNotes.replaceAll(escapeSpecialRegexChars(m2.group(1)), bionetReaction.getEcNumber());
			}
		}
		else if (!StringUtils.isVoid(bionetReaction.getEcNumber())){
			notesToAppend+="<p>EC Number: "+bionetReaction.getEcNumber()+"</p>";
		}

		//Update the reaction score
		m = Pattern.compile(".*[> ]+SCORE:\\s+([^<]+)<.*").matcher(oldNotes);
		if( m.matches()  && !StringUtils.isVoid(bionetReaction.getScore())){
			oldNotes=oldNotes.replaceAll("SCORE:\\s+"+escapeSpecialRegexChars(m.group(1)), "SCORE: "+bionetReaction.getScore());
		}else if (!StringUtils.isVoid(bionetReaction.getScore())){
			notesToAppend+="<p>SCORE: "+bionetReaction.getScore()+"</p>";
		}

		//Update the reaction score
		m = Pattern.compile(".*[> ]+STATUS:\\s+([^<]+)<.*").matcher(oldNotes);
		if( m.matches() && !StringUtils.isVoid(bionetReaction.getStatus())){
			oldNotes=oldNotes.replaceAll("STATUS:\\s+"+escapeSpecialRegexChars(m.group(1)), "STATUS: "+bionetReaction.getStatus());
		}else if(!StringUtils.isVoid(bionetReaction.getStatus())){
			notesToAppend+="<p>STATUS: "+bionetReaction.getStatus()+"</p>";
		}


		//Update the PMIDS
		String newAuthorsNote="";
		i=0;
		for(String pmid:bionetReaction.getPmids()){
			if(i==0){
				newAuthorsNote+="PMID:"+pmid;
			}else{
				newAuthorsNote+=", PMID:"+pmid;
			}
			i++;
		}
		m = Pattern.compile(".*[> ]+AUTHORS:\\s+([^<]+)<.*").matcher(oldNotes);
		if (m.matches() && !StringUtils.isVoid(newAuthorsNote)){
			oldNotes=oldNotes.replaceAll("AUTHORS:\\s+"+escapeSpecialRegexChars(m.group(1)), "AUTHORS: "+newAuthorsNote);
		}else if (!StringUtils.isVoid(newAuthorsNote)){
			notesToAppend+="<p>AUTHORS: "+newAuthorsNote+"</p>";			
		}

		String newCommentsNote="";
		i=0;
		for(Comment com:bionetReaction.getUserComments()){
			if(i==0){
				newCommentsNote+=com.getComment();
			}else{
				newCommentsNote+=". "+com.getComment();
			}
			i++;
		}
		//Update the comment field (yes there is a note field in the sbml note element..)
		m = Pattern.compile(".*[> ]+COMMENTS:\\s+([^<]+)<.*").matcher(oldNotes);
		if( m.matches() && !StringUtils.isVoid(newCommentsNote)){
			oldNotes=oldNotes.replaceAll("COMMENTS:\\s+"+escapeSpecialRegexChars(m.group(1)), "COMMENTS: "+newCommentsNote);
		}else if (!StringUtils.isVoid(newCommentsNote)){
			notesToAppend+="<p>COMMENTS: "+newCommentsNote+"</p>";
		}

		//get the GA information and compare it to the reaction's enzyme list 
		m = Pattern.compile(".*[> ]+GENE.{0,1}ASSOCIATION:\\s+([^<]+)<.*").matcher(oldNotes);
		if( m.matches() && !StringUtils.isVoid(BioChemicalReactionUtils.getGPR(bionetReaction).get(0))){
			oldNotes=oldNotes.replaceAll("GENE.{0,1}ASSOCIATION:\\s+"+escapeSpecialRegexChars(m.group(1)), "GENE ASSOCIATION: "+BioChemicalReactionUtils.getGPR(bionetReaction).get(0));
		}else if (!StringUtils.isVoid(BioChemicalReactionUtils.getGPR(bionetReaction).get(0))){
			notesToAppend+="<p>GENE_ASSOCIATION: "+BioChemicalReactionUtils.getGPR(bionetReaction).get(0)+"</p>";
		}


		if(!StringUtils.isVoid(notesToAppend)){
			oldNotes=oldNotes.replaceAll("</body>",notesToAppend+"</body>");
		}

		//at the end, put the \n and tabulation between each lines
		oldNotes=oldNotes.replaceAll("><", ">\n          <");
		oldNotes=oldNotes.replaceAll("html:", "");

		libSBMLReaction.appendNotes(oldNotes);

	}

	/**
	 * 
	 * @param bioEnt
	 * @param LibSBMLSpecie
	 * @throws XMLStreamException
	 */
	private void updateNotesFromData(BioPhysicalEntity bioEnt, Species LibSBMLSpecie) throws XMLStreamException {

		String oldNotes;
		if (bioEnt.getEntityNotes()!=null){
			oldNotes=bioEnt.getEntityNotes().getXHTMLasString().replaceAll(">\\s*<", "><");
		}else{
			oldNotes="<body xmlns=\"http://www.w3.org/1999/xhtml\"></body>";
		}
				
		String notesToAppend="";

		Matcher m;
		int i=0;

		//get the formula
		m = Pattern.compile(".*FORMULA:\\s*([^<]+)<.*").matcher(oldNotes);
		if( m.matches() && !StringUtils.isVoid(bioEnt.getChemicalFormula())){
			oldNotes=oldNotes.replaceAll("FORMULA:\\s*"+escapeSpecialRegexChars(m.group(1)), "FORMULA: "+bioEnt.getChemicalFormula().toUpperCase());
		}else if (!StringUtils.isVoid(bioEnt.getChemicalFormula())){
			notesToAppend+="<p>FORMULA: "+bioEnt.getChemicalFormula().toUpperCase()+"</p>";
		}


		//get the charge 
		m = Pattern.compile(".*CHARGE:\\s*([^<]+)<.*").matcher(oldNotes);
		if( m.matches() && !StringUtils.isVoid(bioEnt.getCharge())){
			oldNotes=oldNotes.replaceAll("CHARGE:\\s*"+escapeSpecialRegexChars(m.group(1)),"CHARGE: "+bioEnt.getCharge());
		}else if (!StringUtils.isVoid(bioEnt.getCharge())){
			notesToAppend+="<p>CHARGE: "+bioEnt.getCharge()+"</p>";
		}

		//Update refs
		for(String BDName:bioEnt.getRefs().keySet()){
			if (BDName.equalsIgnoreCase("SBO")){
				continue;
			}
			String refNotes="";
			i=0;
			for (BioRef ref:bioEnt.getRefs(BDName)){
				if(i==0){
					refNotes+=ref.getId();
				}else{
					refNotes+=" || "+ref.getId();
				}
				i++;
			}
			
			m = Pattern.compile(".*"+BDName.toUpperCase()+":\\s*([^<]+)<.*").matcher(oldNotes);
			if( m.matches() && !StringUtils.isVoid(refNotes)){
				oldNotes=oldNotes.replaceAll(BDName.toUpperCase()+": "+escapeSpecialRegexChars(m.group(1)), BDName.toUpperCase()+": "+refNotes);
				
			}else if (!StringUtils.isVoid(refNotes)){
				notesToAppend+="<p>"+BDName.toUpperCase()+": "+refNotes+"</p>";
			}
		}		

		if(!StringUtils.isVoid(notesToAppend)){
			oldNotes=oldNotes.replaceAll("</body>",notesToAppend+"</body>");
		}

		//at the end, put the \n and tabulation between each lines
		oldNotes=oldNotes.replaceAll("><", ">\n          <");
		oldNotes=oldNotes.replaceAll("html:", "");
		

		LibSBMLSpecie.appendNotes(oldNotes);

	}




	/**
	 * Creates new annotation from external refs
	 * @param libSBMLSpecie
	 * @param bioEnt
	 * @throws XMLStreamException 
	 */
	private void createAnnotationFromRef(Species libSBMLSpecie, BioPhysicalEntity bioEnt) throws XMLStreamException {
		String finalAnnot="<annotation><rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:bqmodel=\"http://biomodels.net/model-qualifiers/\""
				+ " xmlns:bqbiol=\"http://biomodels.net/biology-qualifiers/\">"
				+ "<rdf:Description rdf:about=\""+libSBMLSpecie.getMetaId()+"\">";

		String singleAnnotStart="<bqbiol:";
		String uri="><rdf:Bag><rdf:li rdf:resource=\"http://identifiers.org/";
		String singleAnnotEnd="\"/></rdf:Bag></bqbiol:";

		for(Entry<String, Set<BioRef>> DBentries:bioEnt.getRefs().entrySet() ){
			String dbname=DBentries.getKey();
			//			String idWithMinScore="";
			int minScore=1000;

			if (dbname.equalsIgnoreCase("sbo")){
				String sbo="";
				for(BioRef bioref :DBentries.getValue()){
					if (bioref.getConfidenceLevel()<minScore && bioref.getOrigin().equalsIgnoreCase("SBML file")){
						sbo=bioref.getId();
					}
				}
				libSBMLSpecie.setSBOTerm(sbo);
				continue;
			}

			for(BioRef bioref :DBentries.getValue()){
				
				//String id=bioref.getId().replaceAll("[(&quot;)\"]", "");
				String id=bioref.getId().replaceAll("[\"]", "");
				finalAnnot+=singleAnnotStart+bioref.getLogicallink()+uri+dbname+"/"+id+singleAnnotEnd+bioref.getLogicallink()+">";
				//				if (bioref.getConfidenceLevel()<minScore && bioref.getOrigin().equalsIgnoreCase("SBML file")){
				//					idWithMinScore=bioref.getId();
				//				}
			}
			//			finalAnnot+=singleAnnotStart+dbname+"/"+idWithMinScore+singleAnnotEnd;
		}

//		//get unimported annotations from BioAnnotation
//		ArrayList<String> unimported = extractUnimportedAnnotation(bioEnt);
//		if(!unimported.isEmpty()){
//			for(String ref:unimported){
//				finalAnnot+="<bqbiol:is><rdf:Bag>"+ref+singleAnnotEnd;
//			}
//		}


		finalAnnot+="</rdf:Description></rdf:RDF></annotation>";
		libSBMLSpecie.setAnnotation(new Annotation(finalAnnot));
	}



//	/**
//	 * parse {@link BioAnnotation} to extract annotations not imported as {@link BioRef}
//	 * @param libSBMLSpecie
//	 * @param bioEnt
//	 */
//	private static ArrayList<String> extractUnimportedAnnotation(BioPhysicalEntity bioEnt) {
//		ArrayList<String> unimported = new ArrayList<String>();
//		if(bioEnt.getEntityAnnot()!=null){
//			String bioAnnot = bioEnt.getEntityAnnot().getXMLasString();
//			bioAnnot = bioAnnot.replaceAll(">\\s+<", "><");	
//			String regex=".*(<bqbiol:[^>]+><rdf:Bag><rdf:li rdf:resource=[^<]+/></rdf:Bag></bqbiol:[^>]+>).*";		
//			Matcher m=Pattern.compile(regex).matcher(bioAnnot);
//			while (m.matches()){
//				String ref=m.group(1);
//				if(!ref.matches(".*\"http://identifiers\\.org/([^/]+)/([^\"]+)\".*")){
//					unimported.add(ref);
//				}
//				bioAnnot=bioAnnot.replace(ref, "");
//				m=Pattern.compile(regex).matcher(bioAnnot);
//			}
//		}
//		return unimported;
//	}

	/**
	 * Creates new annotation to species
	 * @param libSBMLSpecie
	 * @param bioEnt
	 * @throws XMLStreamException 
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void injectAnnotationToSpecie(Species libSBMLSpecie, BioPhysicalEntity bioEnt) throws XMLStreamException {
		String finalAnnot="<annotation><rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:bqmodel=\"http://biomodels.net/model-qualifiers/\""
				+ "xmlns:bqbiol=\"http://biomodels.net/biology-qualifiers/\">"
				+ "<rdf:Description rdf:about=\"#_meta"+bioEnt.getId()+"\">";
	
		String singleAnnotStart="<bqbiol:is><rdf:Bag><rdf:li rdf:resource=\"http://identifiers.org/";
		String singleAnnotEnd="\"/></rdf:Bag></bqbiol:is>";
	
		//get annotations from BioRef
		for(Entry<String, Set<BioRef>> DBentries:bioEnt.getRefs().entrySet() ){
			String dbname=DBentries.getKey();
			//			String idWithMinScore="";
			int minScore=1000;
	
			if (dbname.equalsIgnoreCase("sbo")){
				String sbo="";
				for(BioRef bioref :DBentries.getValue()){
					if (bioref.getConfidenceLevel()<minScore && bioref.getOrigin().equalsIgnoreCase("SBML file")){
						sbo=bioref.getId();
					}
				}
				libSBMLSpecie.setSBOTerm(sbo);
				continue;
			}
	
			for(BioRef bioref :DBentries.getValue()){
				finalAnnot+=singleAnnotStart+dbname+"/"+bioref.getId()+singleAnnotEnd;
				//				if (bioref.getConfidenceLevel()<minScore && bioref.getOrigin().equalsIgnoreCase("SBML file")){
				//					idWithMinScore=bioref.getId();
				//				}
			}
			//			finalAnnot+=singleAnnotStart+dbname+"/"+idWithMinScore+singleAnnotEnd;
		}
	
//		//get unimported annotations from BioAnnotation
//		ArrayList<String> unimported = extractUnimportedAnnotation(bioEnt);
//		if(!unimported.isEmpty()){
//			for(String ref:unimported){
//				finalAnnot+="<bqbiol:is><rdf:Bag>"+ref+singleAnnotEnd;
//			}
//		}
	
		finalAnnot+="</rdf:Description></rdf:RDF></annotation>";
	
		libSBMLSpecie.setMetaId("#_meta"+bioEnt.getId());
		libSBMLSpecie.setAnnotation(new Annotation(finalAnnot));
	}

	/**
	 * 
	 * @param bioEnt
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void injectAttributesToNotes(BioPhysicalEntity bioEnt) {

		for (Entry<String, Set<BioRef>> DBentries : bioEnt.getRefs().entrySet()){
			String dbname=DBentries.getKey();

			if (dbname.equalsIgnoreCase("sbo")){
				continue;
			}
			String idWithMinScore="";
			int minScore=1000;

			for(BioRef idset :DBentries.getValue() ){
				if (idset.getConfidenceLevel()<minScore){
					idWithMinScore=idset.getId();
				}
			}

			if(bioEnt.getEntityNotes()!=null && !bioEnt.getEntityNotes().getXHTMLasString().equals("")){

				String note=bioEnt.getEntityNotes().getXHTMLasString();

				Matcher m1,m2;
				m1 = Pattern.compile(".*"+dbname.toUpperCase()+":\\s++([^<\\s]+)<.*").matcher(note.replaceAll(">\\s*<", "><"));
				if (m1.matches()){
					String old=m1.group(1);
					note=note.replace(old, idWithMinScore);
					bioEnt.getEntityNotes().setXHTMLasString(note);

				}
				else{
					m2 = Pattern.compile(".*"+dbname.toUpperCase()+":\\s+<.*").matcher(note.replaceAll(">\\s*<", "><"));
					if (m2.matches()){
						note=note.replace(""+dbname.toUpperCase()+": ", ""+dbname.toUpperCase()+": "+idWithMinScore);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
					else{
						String ComplementaryNote="<p>"+dbname.toUpperCase()+": "+idWithMinScore+"</p>\n</body>\n</notes>";
						note=note.replaceAll("</body>\\s*</notes>", ComplementaryNote);
						bioEnt.getEntityNotes().setXHTMLasString(note);
					}
				}


			}else{
				String newNotes="<notes>\n<body xmlns=\"http://www.w3.org/1999/xhtml\">"
						+ "\n<p>"+dbname.toUpperCase()+": "+idWithMinScore+"</p>\n</body>\n</notes>";
				bioEnt.setEntityNotes(new Notes(newNotes));
			}

		}
	}







	/**
	 * Generate Palsson notes for metabolites in the sbml file
	 * @param LibSBMLSpecie
	 * @param bioEnt
	 * @throws XMLStreamException 
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void GeneratePalssonNote(Species LibSBMLSpecie, BioPhysicalEntity bioEnt) throws XMLStreamException{
		String notes="<body xmlns=\"http://www.w3.org/1999/xhtml\">\n";
		notes+="<p>FORMULA: "+bioEnt.getChemicalFormula().toUpperCase()+"</p>\n";
		notes+="<p>CHARGE: "+bioEnt.getCharge().toUpperCase()+"</p>\n";

		for(String BDName:bioEnt.getRefs().keySet()){
			notes+="<p>"+BDName.toUpperCase()+":";
			for (BioRef ref:bioEnt.getRefs(BDName)){
				notes+=" "+ref.getId()+",";
			}
			notes = notes.substring(0, notes.length()-1);
			notes+="</p>\n";
		}
		notes+="</body>";

		LibSBMLSpecie.appendNotes(notes);
	}

	/**
	 * Generate Palsson Note for reaction in the sbml file
	 * @param libSBMLReaction
	 * @param bionetReaction
	 * @throws XMLStreamException 
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void GeneratePalssonNote(Reaction libSBMLReaction, BioChemicalReaction bionetReaction) throws XMLStreamException{
		String notes="<body xmlns=\"http://www.w3.org/1999/xhtml\">\n";
		notes+="<p>SUBSYSTEM:";

		int i=0;
		for(BioPathway pthw :bionetReaction.getPathwayList().values()){
			if(i==0){
				notes+=" "+pthw.getName().replaceAll("&", "&amp;");
			}else{
				notes+=" || "+pthw.getName().replaceAll("&", "&amp;");
			}
			i++;
		}
		notes+="</p>\n";

		notes+="<p>GENE_ASSAOCIATION: "+BioChemicalReactionUtils.getGPR(bionetReaction).get(0)+"</p>\n";

		notes+="<p>EC NUMBER: "+bionetReaction.getEcNumber()+"</p>\n";

		notes+="<p>AUTHORS:";
		for (String pmid:bionetReaction.getPmids()){
			notes+=" PMID: "+pmid+",";
		}
		notes+="</p>\n";

		notes+="<p>CONFIDENCE LEVEL: "+bionetReaction.getScore()+"</p>\n";;

		notes+="</body>";


		libSBMLReaction.appendNotes(notes);

	}

	public BioNetwork getBionet() {
		return bionet;
	}
	public SBMLDocument getSbmlDoc() {
		return sbmlDoc;
	}
	public String getFilename() {
		return filename;
	}
	public SBMLWriter getWriter() {
		return sbmlwriter;
	}
	public void setBionet(BioNetwork bionet) {
		this.bionet = bionet;
	}
	public void setSbmlDoc(SBMLDocument sbmlDoc) {
		this.sbmlDoc = sbmlDoc;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public void setWriter(SBMLWriter writer) {
		this.sbmlwriter = writer;
	}

	public Boolean getCheckConsistency() {
		return checkConsistency;
	}

	public void setCheckConsistency(Boolean checkConsistency) {
		this.checkConsistency = checkConsistency;
	}

	public boolean isProccessing() {
		return processing;
	}

	public void setProccessing(boolean proccessing) {
		this.processing = proccessing;
	}
	
	Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");


	String escapeSpecialRegexChars(String str) {

		return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
	}
	
	
//	String unEscapeSpecialRegexChars(String str) {
//
//		return "\\\\"+SPECIAL_REGEX_CHARS.matcher(str).replaceAll("$0");
//	}

}
