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
package fr.inra.toulouse.metexplore.met4j_core.io;

/**
 * @author bmerlet
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Level;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Constraint;
import org.sbml.jsbml.Event;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.ListOf;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.ModifierSpeciesReference;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.SBase;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.SpeciesType;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.Unit.Kind;
import org.sbml.jsbml.UnitDefinition;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioAnnotation;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartmentType;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
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
import fr.inra.toulouse.metexplore.met4j_core.utils.JSBMLUtils;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;


/**
 * This class uses the jSBML library to parse sbml files. 
 * It creates and populates a BioNetwork object while doing so.
 * 
 * This class doen't take into account the extended sbml feature. 
 * 
 * @author bmerlet
 *
 */
public class JSBMLToBionetwork {

	private BioNetwork bioNetwork;

	private Set<String> Warnings=new HashSet<String>();
	private String errorThread;

	private SBMLDocument SBMLdoc;
	private Model jSBMLmodel;


	private boolean processing =true;


	public JSBMLToBionetwork(String inputFile)
	{

		System.err.println("Pre-processing File ....");
		File PreProcesFile = null;
		try {
			PreProcesFile = PreProcessSBML.process(inputFile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.err.println("File Processed.");
		bioNetwork = new BioNetwork();


		try {
			JSBMLUtils.setDefaultLog4jConfiguration();		
			JSBMLUtils.setJSBMLlogToLevel(Level.ERROR);

			this.setSBMLdoc(SBMLReader.read(PreProcesFile));
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}

		if (SBMLdoc.getErrorLog().getNumErrors()==0){
			/*
			 * Convert to Bionetwork objects
			 */
			this.convertToBioNetwork();
		}else{
			System.err.println("Error(s) while parsing the the SBML file: ");
			for (int i = 0; i < SBMLdoc.getErrorLog().getNumErrors(); i++ ){
				System.err.println(SBMLdoc.getErrorLog().getError(i).getMessage());
			}
		}


	}




	/**
	 * 
	 * @param inputFile
	 * @param process
	 */
	public JSBMLToBionetwork(String inputFile, boolean process)
	{
		this.setProccessing(process);
		bioNetwork = new BioNetwork();
		System.err.println("File : "+inputFile);


		if(this.processing){
			System.err.println("Pre-processing File ....");
			File PreProcesFile = null;
			try {
				PreProcesFile = PreProcessSBML.process(inputFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.err.println("File Processed ("+PreProcesFile.getAbsolutePath()+")");

			try {
				JSBMLUtils.setDefaultLog4jConfiguration();
				JSBMLUtils.setJSBMLlogToLevel(Level.ERROR);

				this.setSBMLdoc(SBMLReader.read(PreProcesFile));


			} catch (XMLStreamException  e1) {
				System.err.println("Error while reading the sbml file.");
				e1.printStackTrace();
			} catch (FileNotFoundException e){

			} catch ( IOException e){
				System.err.println("Error while reading the sbml file.");
			}

			PreProcesFile.delete();

		}else{

			File sbmlFile=new File(inputFile);

			try {
				JSBMLUtils.setDefaultLog4jConfiguration();
				JSBMLUtils.setJSBMLlogToLevel(Level.ERROR);

				this.setSBMLdoc(SBMLReader.read(sbmlFile));

			} catch (XMLStreamException | IOException  e1) {
				System.err.println("Error while reading the sbml file.");
				e1.printStackTrace();
			}
		}

		if (SBMLdoc.getErrorLog().getNumErrors()==0){
			/*
			 * Convert to Bionetwork objects
			 */
			this.convertToBioNetwork();
		}
		else{
			//System.err.println("Error(s) while parsing the the SBML file: ");
			for (int i = 0; i < SBMLdoc.getErrorLog().getNumErrors(); i++ ){
				System.err.println(SBMLdoc.getErrorLog().getError(i).getMessage());
			}
		}


	}




	public void convertToBioNetwork(){


		long startTime = System.nanoTime();
		jSBMLmodel=SBMLdoc.getModel();

		bioNetwork.setId(jSBMLmodel.getId());
		bioNetwork.setName(jSBMLmodel.getName());
		bioNetwork.setType("sbml"+jSBMLmodel.getLevel()+"."+jSBMLmodel.getVersion());


		if (!StringUtils.isVoid(jSBMLmodel.getMetaId())){
			try{
				bioNetwork.setModelAnnot(new BioAnnotation(jSBMLmodel.getMetaId(),jSBMLmodel.getAnnotationString()));
				bioNetwork.setModelNotes(new Notes(jSBMLmodel.getNotesString()));
			}catch(XMLStreamException e){
				e.printStackTrace();
			}
		}
		try{
			parseSbmlListOfUnitDefinitions(jSBMLmodel);
			parseSbmlListOfCompartments(jSBMLmodel);
			parseSbmlListOfReactions(jSBMLmodel);
			parseForUnusedSpecies(jSBMLmodel);

			//parseUnusedData(jSBMLmodel);


		}
		catch(Exception e){
			errorThread=e.toString()+"\n"+e.getStackTrace()[0].toString();
			System.err.println(errorThread);
			System.exit(1);
		}


		if(this.processing){

			ProcessBioNetWorkIds proc=new ProcessBioNetWorkIds(bioNetwork, false);
			proc.process();
		}


		long elapsedTime = System.nanoTime() - startTime;

		if (elapsedTime/1000000000==0){
			System.err.println("Time to generate the bionetwork Java object in miliseconde :"
					+ elapsedTime/1000000);
		}
		else{
			System.err.println("Time to generate the bionetwork Java object in seconde :"
					+ elapsedTime/1000000000);
		}

	}


	/**
	 * Adds the list of Unit Definition in the BioNetwork object
	 * @param Model object of jSBML library
	 */
	public void parseSbmlListOfUnitDefinitions(Model jSBMLmodel) {

		long numUnitDef=jSBMLmodel.getNumUnitDefinitions();

		if(numUnitDef != 0) {

			for (int i=0; i<numUnitDef; i++){

				UnitDefinition jSBMLUD=jSBMLmodel.getUnitDefinition(i);
				BioUnitDefinition bionetUD= new BioUnitDefinition(jSBMLUD.getId(),jSBMLUD.getName());

				if (jSBMLUD.getName().isEmpty()){
					bionetUD.setName(jSBMLUD.getId());
				}


				ListOf<Unit> listofunits=jSBMLUD.getListOfUnits();

				if(listofunits.size() != 0) {

					for (int n=0; n<listofunits.size(); n++){

						Unit jSBMLUnit=listofunits.get(n);

						Kind kind=jSBMLUnit.getKind();
						String Exp=""+(int)jSBMLUnit.getExponent();
						String Scale=""+jSBMLUnit.getScale();
						String Multiplier=""+jSBMLUnit.getMultiplier();

						UnitSbml bionetUnit= new UnitSbml(kind.getName().toUpperCase(), Exp, Scale, Multiplier);
						bionetUD.addUnit(bionetUnit);
					}
				}

				this.getBioNetwork().addUnitDefinition(bionetUD);

			}
		}
		else{
			Warnings.add("Warnings, no list of UnitDefinition, please check your SBML file");
		}


	}

	/**
	 * adds the list of compartments to the BioNetWork object
	 * @param  jSBMLmodel from the jSBML library
	 */
	public void parseSbmlListOfCompartments(Model jSBMLmodel) {

		long numCompart=jSBMLmodel.getNumCompartments();

		if (numCompart!=0){

			for (int i=0; i<numCompart; i++){

				Compartment jSBMLCompart=jSBMLmodel.getCompartment(i);
				String compartId=jSBMLCompart.getId();
				String compartName=jSBMLCompart.getName();

				if (compartName.isEmpty()){
					compartName=jSBMLCompart.getId();
				}

				BioCompartment bionetCompart=this.getBioNetwork().findbioCompartmentInList(compartId);
				if(bionetCompart==null){
					//if this compartment is not yet in the BioNetwork we create it and add it. 
					bionetCompart=new BioCompartment(compartName,compartId);
					this.getBioNetwork().addCompartment(bionetCompart);
				}

				/*
				 * If the compartment has an outside attribute,
				 * we need to look if this compartment is already in the bioNetwork
				 */
				if (jSBMLCompart.isSetOutside()){
					//we try to find it in the bionetwork
					BioCompartment outsideCompart=this.getBioNetwork().findbioCompartmentInList(jSBMLCompart.getOutside());

					//if it's null, this compartment doesn't exist in the BioNetwork yet
					if (outsideCompart==null){
						//model.getCompartment(modelCompart.getOutside()).getName() => name of the outside compart
						outsideCompart=new BioCompartment(jSBMLmodel.getCompartment(jSBMLCompart.getOutside()).getName(),jSBMLCompart.getOutside());
						//once it is created, it is added to the bionetwork
						this.getBioNetwork().addCompartment(outsideCompart);
					}

					//We can add it as outside compartment of the current compartment
					bionetCompart.setOutsideCompartment(outsideCompart);
				}

				/*
				 * If the compartment has an compartmentType attribute (deprecated in level3 version1 core)
				 * we need to see if that compartment Type is already set in the BioNetwork and create it if it is not the case.
				 * 
				 * same as the the outside compartment search.
				 */
				if (jSBMLCompart.isSetCompartmentType()){
					BioCompartmentType bionetCompartType=this.getBioNetwork().findbioCompartmentTypeInList(jSBMLCompart.getCompartmentType());

					if (bionetCompartType==null){
						bionetCompartType=new BioCompartmentType(jSBMLCompart.getCompartmentType(),jSBMLmodel.getCompartmentType(jSBMLCompart.getCompartmentType()).getName());

						this.getBioNetwork().addCompartmentType(bionetCompartType);
					}
					bionetCompart.setCompartmentType(bionetCompartType);	
				}

				if (jSBMLCompart.isSetUnits()){
					BioUnitDefinition bionetUnitDef=this.getBioNetwork().findUnitInUnitDefinituin(jSBMLCompart.getUnits());

					if (bionetUnitDef==null){
						System.err.println("SBML Error: The compartment "+jSBMLCompart.getName()+" reference units that are not defined in the ListOfUnitDefinition.");
					}
					else{
						bionetCompart.setUnit(bionetUnitDef);
					}
				}

				bionetCompart.setSboterm(jSBMLCompart.getSBOTermID());
				bionetCompart.setConstant(jSBMLCompart.getConstant());
				if (jSBMLCompart.isSetSize()){
					bionetCompart.setSize(jSBMLCompart.getSize());
				}else{
					bionetCompart.setSize(1);
				}
				bionetCompart.setSpatialDimensions((int) jSBMLCompart.getSpatialDimensions());

				if (jSBMLCompart.isSetAnnotation()){
					BioAnnotation bionetCompartAnnot = null;
					try {
						bionetCompartAnnot = new BioAnnotation(jSBMLCompart.getMetaId(),jSBMLCompart.getAnnotationString());
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}
					bionetCompart.setCompartAnnot(bionetCompartAnnot);
				}
				if (jSBMLCompart.isSetNotes()){
					Notes bionetCompartNotes = null;
					try {
						bionetCompartNotes = new Notes(jSBMLCompart.getNotesString());
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}
					bionetCompart.setCompartNotes(bionetCompartNotes);
				}


			}


		}
		else{
			Warnings.add("Errors while parsing the list of compartments, please check your SBML file");
		}
	}


	/**
	 * Parse the list of reactions of the sbml file, and creates species from the listOfProduct, listOfReactant and listofModifier.
	 * 
	 * @param jSBMLmodel
	 * @throws XMLStreamException 
	 */
	public void parseSbmlListOfReactions(Model jSBMLmodel) throws XMLStreamException
	{

		long numReaction=jSBMLmodel.getNumReactions();

		if ( 	numReaction !=0){

			for (int i=0; i<numReaction; i++){

				Reaction jSBMLReaction=jSBMLmodel.getReaction(i);
				String reactionId=jSBMLReaction.getId();
				String reactionName=jSBMLReaction.getName();
				if(reactionName.isEmpty()){
					reactionName=reactionId;
				}

				BioChemicalReaction bionetReaction= new BioChemicalReaction(reactionId,reactionName);

				bionetReaction.setSboterm(jSBMLReaction.getSBOTermID());
				if(jSBMLReaction.isSetAnnotation()){
					bionetReaction.setEntityAnnot(new BioAnnotation(jSBMLReaction.getMetaId(),jSBMLReaction.getAnnotationString()));
				}

				if (jSBMLReaction.isSetFast()){
					bionetReaction.setSpontaneous(new Boolean(jSBMLReaction.getFast()).toString());
				}
				else{
					bionetReaction.setSpontaneous("false");
				}

				//if reversible attribute is present and is set to false
				if (jSBMLReaction.isSetReversible() && !jSBMLReaction.getReversible()){
					bionetReaction.setReversibility(false);
				}
				else{
					bionetReaction.setReversibility(true);
				}

				/*
				 * Parse the list of reactant 
				 */
				for (int n=0;n<jSBMLReaction.getNumReactants() ;n++){

					SpeciesReference jSBMLReactant=jSBMLReaction.getReactant(n);
					Species jSBMLReactantSpecies=jSBMLmodel.getSpecies(jSBMLReactant.getSpecies());

					BioPhysicalEntity bionetSubstrate=parseParticipantSpecies(jSBMLReactantSpecies);


					String bionetMetabId=jSBMLReaction.getReactant(n).getSpecies();
					String Stoechio=jSBMLReaction.getReactant(n).getStoichiometry()+"";
					String tempParticipantId=bionetReaction.getId()+"__With__"+bionetMetabId;


					BioPhysicalEntityParticipant bionetSubsPart=new BioPhysicalEntityParticipant(tempParticipantId,bionetSubstrate,Stoechio,bionetSubstrate.getCompartment());

					if(jSBMLReaction.getReactant(n).isSetConstant()){
						bionetSubsPart.setIsConstant(jSBMLReaction.getReactant(n).getConstant());
					}
					else{
						bionetSubsPart.setIsConstant(false);
					}
					if(jSBMLReaction.getReactant(n).isSetId()){
						bionetSubsPart.setId(jSBMLReaction.getReactant(n).getId());
					}

					bionetReaction.addLeftParticipant(bionetSubsPart);

				}
				/*
				 * Parse the list of Product
				 */
				for (int n=0;n<jSBMLReaction.getNumProducts() ;n++){

					SpeciesReference jSBMLProduct=jSBMLReaction.getProduct(n);
					Species jSBMLProductSpecies=jSBMLmodel.getSpecies(jSBMLProduct.getSpecies());
					BioPhysicalEntity bionetproduct=parseParticipantSpecies(jSBMLProductSpecies);

					String bionetMetabId=jSBMLReaction.getProduct(n).getSpecies();
					String Stoechio=jSBMLReaction.getProduct(n).getStoichiometry()+"";
					String tempParticipantId=bionetReaction.getId()+"__With__"+bionetMetabId;

					BioPhysicalEntityParticipant  bionetProdPart=new BioPhysicalEntityParticipant(tempParticipantId,bionetproduct,Stoechio,bionetproduct.getCompartment());

					if(jSBMLReaction.getProduct(n).isSetConstant()){
						bionetProdPart.setIsConstant(jSBMLReaction.getProduct(n).getConstant());
					}
					else{
						bionetProdPart.setIsConstant(false);
					}
					if(jSBMLReaction.getProduct(n).isSetId()){
						bionetProdPart.setId(jSBMLReaction.getProduct(n).getId());
					}

					bionetReaction.addRightParticipant(bionetProdPart);
				}

				bionetReaction.setListOfSubstrates();
				bionetReaction.setListOfProducts();

				/*
				 * Parse the list of modifiers (if it is not defined, getNumModifiers=0)
				 */
				for (int n=0;n<jSBMLReaction.getNumModifiers();n++){

					ModifierSpeciesReference jSBMLModifier=jSBMLReaction.getModifier(n);
					Species jSBMLModifierSpecies=jSBMLmodel.getSpecies(jSBMLModifier.getSpecies());

					BioPhysicalEntity modifier=parseModifierSpecies(jSBMLModifierSpecies);

					bionetReaction.addEnz(modifier);
					this.getBioNetwork().getEnzList().put(modifier.getId(), modifier);


				}
				/*
				 * Parse the list of parameters of the kinetic law
				 */
				KineticLaw kine=jSBMLReaction.getKineticLaw();
				if (kine!=null){
					if (kine.getFormula()!=null){
						bionetReaction.setKineticFormula(kine.getFormula());
					}
					boolean hasBounds=false;
					if (jSBMLmodel.getLevel()<3){

						for (int n=0; n<kine.getNumParameters(); n++){

							BioUnitDefinition UD=this.getBioNetwork().getUnitDefinitions().get(kine.getParameter(n).getUnits());
							if(kine.getParameter(n).getId().equalsIgnoreCase("UPPER_BOUND") || kine.getParameter(n).getName().equalsIgnoreCase("UPPER_BOUND")){
								
								/**
								 * This is to make sure that the unit definition associated with the fluxes is not null
								 */
								if(UD==null && this.getBioNetwork().getUnitDefinitions().containsKey("mmol_per_gDW_per_hr")){
									UD=this.getBioNetwork().getUnitDefinitions().get("mmol_per_gDW_per_hr");
								}else if(UD==null && this.getBioNetwork().getUnitDefinitions().containsKey("FLUX_UNIT")){
									UD=this.getBioNetwork().getUnitDefinitions().get("FLUX_UNIT");
								}
								if(UD==null){
									UD=new BioUnitDefinition();
									UD.setDefault();
									this.getBioNetwork().getUnitDefinitions().put(UD.getId(), UD);
									
								}
								
								
								Flux newflux=new Flux(""+kine.getParameter(n).getValue(),UD);
								bionetReaction.setUpperBound(newflux);
								hasBounds=true;
							}
							else if(kine.getParameter(n).getId().equalsIgnoreCase("LOWER_BOUND") || kine.getParameter(n).getName().equalsIgnoreCase("LOWER_BOUND")){
								Flux newflux=new Flux(""+kine.getParameter(n).getValue(),UD);
								bionetReaction.setLowerBound(newflux);
								hasBounds=true;
							}
							else if(UD!=null){
								Flux newflux=new Flux(""+kine.getParameter(n).getValue(),UD);
								bionetReaction.addFluxParam(kine.getParameter(n).getId(), newflux);
							}
							else if(UD==null && kine.getParameter(n).getUnits().equalsIgnoreCase("dimensionless")){
								UD=new BioUnitDefinition("dimensionless","dimensionless");
								Flux newflux=new Flux(""+kine.getParameter(n).getValue(),UD);
								bionetReaction.addFluxParam(kine.getParameter(n).getId(), newflux);
							}
						}
					}
					else{
						for (int n=0; n<kine.getLocalParameterCount(); n++){

							BioUnitDefinition UD=this.getBioNetwork().getUnitDefinitions().get(kine.getLocalParameter(n).getUnits());
							if(kine.getLocalParameter(n).getId().equalsIgnoreCase("UPPER_BOUND") || kine.getLocalParameter(n).getName().equalsIgnoreCase("UPPER_BOUND")){
								
								/**
								 * This is to make sure that the unit definition associated with the fluxes is not null
								 */
								if(UD==null && this.getBioNetwork().getUnitDefinitions().containsKey("mmol_per_gDW_per_hr")){
									UD=this.getBioNetwork().getUnitDefinitions().get("mmol_per_gDW_per_hr");
								}else if(UD==null && this.getBioNetwork().getUnitDefinitions().containsKey("FLUX_UNIT")){
									UD=this.getBioNetwork().getUnitDefinitions().get("FLUX_UNIT");
								}
								if(UD==null){
									UD=new BioUnitDefinition();
									UD.setDefault();
									this.getBioNetwork().getUnitDefinitions().put(UD.getId(), UD);
								}
								
								
								Flux newflux=new Flux(""+kine.getLocalParameter(n).getValue(),UD);
								bionetReaction.setUpperBound(newflux);
								hasBounds=true;
							}
							else if(kine.getLocalParameter(n).getId().equalsIgnoreCase("LOWER_BOUND") || kine.getLocalParameter(n).getName().equalsIgnoreCase("LOWER_BOUND")){
								Flux newflux=new Flux(""+kine.getLocalParameter(n).getValue(),UD);
								bionetReaction.setLowerBound(newflux);
								hasBounds=true;
							}
							else if(UD!=null){
								Flux newflux=new Flux(""+kine.getLocalParameter(n).getValue(),UD);
								bionetReaction.addFluxParam(kine.getLocalParameter(n).getId(), newflux);
							}
							else if(UD==null && kine.getLocalParameter(n).getUnits().equalsIgnoreCase("dimensionless")){
								UD=new BioUnitDefinition("dimensionless","dimensionless");
								Flux newflux=new Flux(""+kine.getLocalParameter(n).getValue(),UD);
								bionetReaction.addFluxParam(kine.getLocalParameter(n).getId(), newflux);
							}
						}
					}
					/*
					 * This allow to override the automatic instantiation of this two fluxes in the reaction instantiation.
					 */
					if(!hasBounds){
						bionetReaction.setUpperBound(null);
						bionetReaction.setLowerBound(null);
					}
				}

				if(jSBMLReaction.isSetNotes()){
					bionetReaction.setEntityNotes(new Notes(jSBMLReaction.getNotesString()));
					parseReactionNotes(bionetReaction);
				}
				bionetReaction.setCompartment(null);
				this.getBioNetwork().addBiochemicalReaction(bionetReaction);
			}
		}
		else{
			Warnings.add("Error while parsing the list of reaction. No reaction in the file, please check your SBML file");
		}

	}

	/**
	 * looks throught the list of species for proteins or complexes not linked to reactions
	 * @param jSBMLmodel2
	 * @throws XMLStreamException 
	 */
	private void parseForUnusedSpecies(Model jSBMLmodel) throws XMLStreamException {
		long numspecies=jSBMLmodel.getNumSpecies();

		for (int i=0; i<numspecies;i++){

			Species jSBMLSpecie =jSBMLmodel.getSpecies(i);
			String specieId=jSBMLSpecie.getId();

			if(!(this.getBioNetwork().getPhysicalEntityList().containsKey(specieId)) 
					&& !(this.getBioNetwork().getProteinList().containsKey(specieId))
					&& !(this.getBioNetwork().getComplexList().containsKey(specieId))){
				//if inside the if, unused protein or complex or Metabolite!!.

				String specieName=jSBMLSpecie.getName();
				if (specieName.isEmpty()){
					specieName=specieId;
				}

				if (jSBMLSpecie.getSBOTerm()==2 || jSBMLSpecie.getSBOTerm()==247){
					this.parseParticipantSpecies(jSBMLSpecie);

				}else if(jSBMLSpecie.getSBOTerm()==252 || jSBMLSpecie.getSBOTerm()==297){

					HashSet<BioProtein> Compo=parseComplexComponent(specieId);

					if (Compo.size()==1){

						//if only one component, this modifier is a simple protein, which has already been inserted in the bionetwork
						BioProtein unusedProtein=(BioProtein) Compo.toArray()[0];
						unusedProtein.setId(specieId);
						unusedProtein.setName(specieName);

						unusedProtein.setBoundaryCondition(jSBMLSpecie.getBoundaryCondition());
						unusedProtein.setConstant(jSBMLSpecie.getConstant());
						unusedProtein.setSubstanceUnits(jSBMLSpecie.getSubstanceUnits());
						unusedProtein.setSboterm(jSBMLSpecie.getSBOTermID());

						//get the compartment
						unusedProtein.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLSpecie.getCompartment()));

						//get initial quantity if it is present, normally not for enzymes.
						if (jSBMLSpecie.isSetInitialAmount()){
							unusedProtein.addInitialQuantity("amount", jSBMLSpecie.getInitialAmount());
						}
						else if(jSBMLSpecie.isSetInitialConcentration()){
							unusedProtein.addInitialQuantity("concentration", jSBMLSpecie.getInitialConcentration());
						}


						if (jSBMLSpecie.isSetAnnotation()){
							unusedProtein.setEntityAnnot(new BioAnnotation(jSBMLSpecie.getMetaId(),jSBMLSpecie.getAnnotationString()));
							parseMetaboliteAnnot(unusedProtein);
						}

						parseIDForGene(unusedProtein);

						if (jSBMLSpecie.isSetNotes()){
							unusedProtein.setEntityNotes(new Notes(jSBMLSpecie.getNotesString()));
							parseModifierNote(unusedProtein);
						}

						AddNote(unusedProtein,"Unused");



					}else{
						//else, it has more than one component, it is a protein Complex

						BioComplex unusedComplex=new BioComplex(specieId, specieName);

						unusedComplex.setBoundaryCondition(jSBMLSpecie.getBoundaryCondition());
						unusedComplex.setConstant(jSBMLSpecie.getConstant());
						unusedComplex.setSubstanceUnits(jSBMLSpecie.getSubstanceUnits());
						unusedComplex.setSboterm(jSBMLSpecie.getSBOTermID());

						//get the compartment
						unusedComplex.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLSpecie.getCompartment()));

						//get initial quantity if it is present, normally not for enzymes.
						if (jSBMLSpecie.isSetInitialAmount()){
							unusedComplex.addInitialQuantity("amount", jSBMLSpecie.getInitialAmount());
						}
						else if(jSBMLSpecie.isSetInitialConcentration()){
							unusedComplex.addInitialQuantity("Concentration", jSBMLSpecie.getInitialConcentration());
						}

						for(BioProtein protcompo:Compo){

							protcompo.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLSpecie.getCompartment()));
							BioPhysicalEntityParticipant bionetParticipant=new BioPhysicalEntityParticipant(specieId+"__With__"+protcompo.getId(),protcompo);
							unusedComplex.addComponent(bionetParticipant);
						}


						if (jSBMLSpecie.isSetAnnotation()){
							unusedComplex.setEntityAnnot(new BioAnnotation(jSBMLSpecie.getMetaId(),jSBMLSpecie.getAnnotationString()));			
							parseMetaboliteAnnot(unusedComplex);
						}

						if (jSBMLSpecie.isSetNotes()){
							unusedComplex.setEntityNotes(new Notes(jSBMLSpecie.getNotesString()));
							parseModifierNote(unusedComplex);
						}
						AddNote(unusedComplex,"Unused");

						//add the complex to the network.
						this.getBioNetwork().addComplex(unusedComplex);

					}
				}

			}

		}

	}


	private BioPhysicalEntity parseParticipantSpecies(Species jSBMLSpecie) throws XMLStreamException {

		BioPhysicalEntity bionetSpecies=this.getBioNetwork().getBioPhysicalEntityById(jSBMLSpecie.getId());
		if (bionetSpecies!=null){
			return bionetSpecies;
		}
		else{
			String specieId=jSBMLSpecie.getId();
			String specieName=jSBMLSpecie.getName();
			if (specieName.isEmpty()){
				specieName=specieId;
			}
			bionetSpecies=new BioPhysicalEntity(specieId, specieName);

			bionetSpecies.setBoundaryCondition(jSBMLSpecie.getBoundaryCondition());
			bionetSpecies.setConstant(jSBMLSpecie.getConstant());
			bionetSpecies.setSubstanceUnits(jSBMLSpecie.getSubstanceUnits());
			bionetSpecies.setSboterm(jSBMLSpecie.getSBOTermID());

			//get the species compartment with it's id from the jSBMLSpecies.
			bionetSpecies.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLSpecie.getCompartment()));


			if (jSBMLSpecie.isSetInitialAmount()){
				bionetSpecies.addInitialQuantity("amount", jSBMLSpecie.getInitialAmount());
			}
			else if(jSBMLSpecie.isSetInitialConcentration()){
				bionetSpecies.addInitialQuantity("Concentration", jSBMLSpecie.getInitialConcentration());
			}

			if(jSBMLSpecie.isSetAnnotation()){
				bionetSpecies.setEntityAnnot(new BioAnnotation(jSBMLSpecie.getMetaId(),jSBMLSpecie.getAnnotationString()));
				parseMetaboliteAnnot(bionetSpecies);
			}

			if (jSBMLSpecie.isSetCharge()){
				bionetSpecies.setCharge(jSBMLSpecie.getCharge()+"");
			}

			if (jSBMLSpecie.isSetNotes()){
				bionetSpecies.setEntityNotes(new Notes(jSBMLSpecie.getNotesString()));
				parseMetaboliteNote(bionetSpecies);
			}			

			this.getBioNetwork().addPhysicalEntity(bionetSpecies);
		}
		return bionetSpecies;
	}

	/**
	 * 
	 * @param jSBMLModifierSpecies
	 * @return
	 * @throws XMLStreamException 
	 */
	private BioPhysicalEntity parseModifierSpecies(Species jSBMLModifierSpecies) throws XMLStreamException {

		String specieId=jSBMLModifierSpecies.getId();
		//System.err.println("modifier Id: "+modifierId);

		//we check if the modifier is in any of the list (bioprotein, biocomplex, physicalentity)
		if (this.getBioNetwork().getProteinList().containsKey(specieId)){
			return this.getBioNetwork().getProteinList().get(specieId);
		}
		else if(this.getBioNetwork().getComplexList().containsKey(specieId)){
			return this.getBioNetwork().getComplexList().get(specieId);
		}
		else if(this.getBioNetwork().getPhysicalEntityList().containsKey(specieId)){
			return this.getBioNetwork().getPhysicalEntityList().get(specieId);
		}
		//if it doesn't, create it, set its parameter and return it.
		else{

			String specieName=jSBMLModifierSpecies.getName();
			if (specieName.isEmpty()){
				specieName=specieId;
			}


			HashSet<BioProtein> Compo=parseComplexComponent(specieId);

			if (Compo.size()==1){

				//System.err.println("Id: "+specieId+" ,Name: "+specieName);

				//if only one component, this modifier is a simple protein
				BioProtein proteinModifier=(BioProtein) Compo.toArray()[0];
				proteinModifier.setId(specieId);
				proteinModifier.setName(specieName);

				proteinModifier.setBoundaryCondition(jSBMLModifierSpecies.getBoundaryCondition());
				proteinModifier.setConstant(jSBMLModifierSpecies.getConstant());
				proteinModifier.setSubstanceUnits(jSBMLModifierSpecies.getSubstanceUnits());
				proteinModifier.setSboterm(jSBMLModifierSpecies.getSBOTermID());

				//get the compartment
				proteinModifier.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLModifierSpecies.getCompartment()));

				//get initial quantity if it is present, normally not for enzymes.
				if (jSBMLModifierSpecies.isSetInitialAmount()){
					proteinModifier.addInitialQuantity("amount", jSBMLModifierSpecies.getInitialAmount());
				}
				else if(jSBMLModifierSpecies.isSetInitialConcentration()){
					proteinModifier.addInitialQuantity("concentration", jSBMLModifierSpecies.getInitialConcentration());
				}


				if (jSBMLModifierSpecies.isSetAnnotation()){
					proteinModifier.setEntityAnnot(new BioAnnotation(jSBMLModifierSpecies.getMetaId(),jSBMLModifierSpecies.getAnnotationString()));
					parseMetaboliteAnnot(proteinModifier);
				}

				parseIDForGene(proteinModifier);

				if (jSBMLModifierSpecies.isSetNotes()){
					proteinModifier.setEntityNotes(new Notes(jSBMLModifierSpecies.getNotesString()));
					parseModifierNote(proteinModifier);
				}

				return proteinModifier;

			}else{
				//else, it has more than one component, it is a protein Complex

				BioComplex complexModifier=new BioComplex(specieId, specieName);

				complexModifier.setBoundaryCondition(jSBMLModifierSpecies.getBoundaryCondition());
				complexModifier.setConstant(jSBMLModifierSpecies.getConstant());
				complexModifier.setSubstanceUnits(jSBMLModifierSpecies.getSubstanceUnits());
				complexModifier.setSboterm(jSBMLModifierSpecies.getSBOTermID());

				//get the compartment
				complexModifier.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLModifierSpecies.getCompartment()));

				//get initial quantity if it is present, normally not for enzymes.
				if (jSBMLModifierSpecies.isSetInitialAmount()){
					complexModifier.addInitialQuantity("amount", jSBMLModifierSpecies.getInitialAmount());
				}
				else if(jSBMLModifierSpecies.isSetInitialConcentration()){
					complexModifier.addInitialQuantity("Concentration", jSBMLModifierSpecies.getInitialConcentration());
				}

				for(BioProtein protcompo:Compo){

					protcompo.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLModifierSpecies.getCompartment()));
					BioPhysicalEntityParticipant bionetParticipant=new BioPhysicalEntityParticipant(specieId+"__With__"+protcompo.getId(),protcompo);
					complexModifier.addComponent(bionetParticipant);
				}


				if (jSBMLModifierSpecies.isSetAnnotation()){
					complexModifier.setEntityAnnot(new BioAnnotation(jSBMLModifierSpecies.getMetaId(),jSBMLModifierSpecies.getAnnotationString()));			
					parseMetaboliteAnnot(complexModifier);
				}

				//check if all the genes are retrieved
				complexModifier.getIsGeneticallyPossible();

				if (jSBMLModifierSpecies.isSetNotes()){
					complexModifier.setEntityNotes(new Notes(jSBMLModifierSpecies.getNotesString()));
					parseModifierNote(complexModifier);
				}
				//add the complex to the network.
				this.getBioNetwork().addComplex(complexModifier);

				return complexModifier;
			}
		}
	}


	/**
	 * 
	 * @param jSBMLmodel
	 * @throws XMLStreamException 
	 * @throws SBMLException 
	 */

	public void parseUnusedData(Model jSBMLmodel) throws SBMLException, XMLStreamException {

		SBMLWriter tmpWriter=new SBMLWriter();
		SBMLDocument tmpDoc=new SBMLDocument(jSBMLmodel.getLevel(),jSBMLmodel.getVersion());
		Model tmpModel;

		/////////////////////////////////////

		if(jSBMLmodel.getListOfConstraints().size()!=0){
			tmpModel=tmpDoc.createModel();
			for(Constraint constraint:jSBMLmodel.getListOfConstraints()){
				tmpModel.addConstraint(constraint.clone());
			}
			this.getBioNetwork().addUnusedSBMLdata("ListOfConstraints", tmpWriter.writeSBMLToString(tmpDoc));
		}

		/////////////////////////////////////

		if(jSBMLmodel.getListOfEvents().size()!=0){

			tmpModel=tmpDoc.createModel();
			for(Event event:jSBMLmodel.getListOfEvents()){
				tmpModel.addEvent(event.clone());
			}			

			this.getBioNetwork().addUnusedSBMLdata("ListOfEvents",  tmpWriter.writeSBMLToString(tmpDoc));
		}

		////////////////////////////////////

		if(jSBMLmodel.getListOfFunctionDefinitions().size()!=0){

			tmpModel=tmpDoc.createModel();
			for(FunctionDefinition fctDef:jSBMLmodel.getListOfFunctionDefinitions()){
				tmpModel.addFunctionDefinition(fctDef.clone());
			}			

			this.getBioNetwork().addUnusedSBMLdata("ListOfFunctionDefinitions", tmpWriter.writeSBMLToString(tmpDoc));
		}

		/////////////////////////////////////

		if(jSBMLmodel.getListOfInitialAssignments().size()!=0){

			tmpModel=tmpDoc.createModel();
			for(InitialAssignment initAss:jSBMLmodel.getListOfInitialAssignments()){
				tmpModel.addInitialAssignment(initAss.clone());
			}	

			this.getBioNetwork().addUnusedSBMLdata("ListOfInitialAssignments",  tmpWriter.writeSBMLToString(tmpDoc));
		}

		/////////////////////////////////////

		if(jSBMLmodel.getListOfParameters().size()!=0){

			tmpModel=tmpDoc.createModel();
			for(Parameter param:jSBMLmodel.getListOfParameters()){
				tmpModel.addParameter(param.clone());
			}	

			this.getBioNetwork().addUnusedSBMLdata("ListOfParameters",  tmpWriter.writeSBMLToString(tmpDoc));
		}

		/////////////////////////////////////

		if(jSBMLmodel.getListOfRules().size()!=0){

			tmpModel=tmpDoc.createModel();
			for(Rule rule:jSBMLmodel.getListOfRules()){
				tmpModel.addRule(rule.clone());
			}	

			this.getBioNetwork().addUnusedSBMLdata("ListOfRules",  tmpWriter.writeSBMLToString(tmpDoc));
		}

		/////////////////////////////////////

		if(jSBMLmodel.getListOfSpeciesTypes().size()!=0){

			tmpModel=tmpDoc.createModel();
			for(SpeciesType spType:jSBMLmodel.getListOfSpeciesTypes()){
				tmpModel.addSpeciesType(spType.clone());
			}	

			this.getBioNetwork().addUnusedSBMLdata("ListOfSpeciesTypes",  tmpWriter.writeSBMLToString(tmpDoc));
		}
	}

	public String ListOfToString(ListOf<? extends SBase> listOf){

		String list="";
		for ( int i=0,n=listOf.getChildCount();i<n;i++){
			list+=listOf.getChildAt(i).toString();
		}

		return list;
	}

	/**
	 * This method tries to parse the Palson identifier of an Enzyme complex to compile its component.
	 * (de la bidouille)  
	 * @param complexID
	 * @throws XMLStreamException 
	 */
	public HashSet<BioProtein> parseComplexComponent(String complexID) throws XMLStreamException {

		HashSet<BioProtein> ComponentList=new HashSet<BioProtein>();

		//we split the complexe's id, to retrieve it's component id
		String regexCompartIds="[";
		for (String compartId:this.getBioNetwork().getCompartments().keySet()){
			regexCompartIds=regexCompartIds+"("+compartId+")";
		}
		regexCompartIds=regexCompartIds+"]";


		String[] simpleIDs=complexID.split("(?<=_"+regexCompartIds+"{0,1})(?=_)()");

		if (simpleIDs.length==1){

			BioProtein bionetProt=this.getBioNetwork().getProteinList().get(simpleIDs[0]);
			if (bionetProt!=null){
				ComponentList.add(bionetProt);

			}else{
				String geneId=simpleIDs[0];
				bionetProt=new BioProtein(geneId);

				ComponentList.add(bionetProt);
				this.getBioNetwork().addProtein(bionetProt);

			}


		}else{

			for (String value:simpleIDs){

				Species jSBMLProt=this.jSBMLmodel.getSpecies(value);

				//if the protein already exists in the bionetwork, we just link it as component of the complex
				BioProtein bionetProt=this.getBioNetwork().getProteinList().get(value);
				if (bionetProt!=null){
					ComponentList.add(bionetProt);

				}else if(jSBMLProt!=null ){

					/*if it doesn't exist in the bionetwork, we check if this values points to 
					 * a species in the SBML, and that this species is a protein, we create a new
					 * BioProtein, get it's attribute and add it to the model as a protein 
					 * and to the complex as a component
					 */

					bionetProt=new BioProtein(jSBMLProt.getId(), jSBMLProt.getName());

					bionetProt.setBoundaryCondition(jSBMLProt.getBoundaryCondition());
					bionetProt.setConstant(jSBMLProt.getConstant());
					bionetProt.setSubstanceUnits(jSBMLProt.getSubstanceUnits());
					bionetProt.setSboterm(jSBMLProt.getSBOTermID());

					//get the compartment
					bionetProt.setCompartment(this.getBioNetwork().findbioCompartmentInList(jSBMLProt.getCompartment()));

					//get initial quantity if it is present, normally not present for a protein.
					if (jSBMLProt.isSetInitialAmount()){
						bionetProt.addInitialQuantity("amount", jSBMLProt.getInitialAmount());
					}
					else if(jSBMLProt.isSetInitialConcentration()){
						bionetProt.addInitialQuantity("Concentration", jSBMLProt.getInitialConcentration());
					}

					if (jSBMLProt.isSetAnnotation()){
						bionetProt.setEntityAnnot(new BioAnnotation(jSBMLProt.getMetaId(),jSBMLProt.getAnnotationString()));
						parseMetaboliteAnnot(bionetProt);
					}

					parseIDForGene(bionetProt);

					if (jSBMLProt.isSetNotes()){
						bionetProt.setEntityNotes(new Notes(jSBMLProt.getNotesString()));
						parseModifierNote(bionetProt);
					}

					this.getBioNetwork().addProtein(bionetProt);
					ComponentList.add(bionetProt);
				}
				//else => the component protein is not referenced in the sbml file as a species
				else{
					bionetProt=new BioProtein(value,value+"_TH");

					bionetProt.setBoundaryCondition(true);
					bionetProt.setConstant(false);
					bionetProt.setSboterm("SBO:0000252");
					bionetProt.setSubstanceUnits("false");

					String protNotes="<notes>"
							+ "<body xmlns=\"http://www.w3.org/1999/xhtml\">"
							+ "<p> METEXPLORE_NOTE: This theoretical protein is a component of the protein complexe "
							+ complexID+"</p>"
							+ "</body>"
							+ "</notes>";
					bionetProt.setEntityNotes(new Notes(protNotes));
					//create gene Id from Complex Id
					String geneId=value;
					if(geneId.startsWith("_")) geneId=geneId.substring(1, geneId.length());
					//remove compartment sub-string /!\ SPECIFIC TO PALSSON'S SBML
					if(Pattern.compile("^.+_\\w$", Pattern.CASE_INSENSITIVE).matcher(geneId).matches()) geneId=geneId.substring(0,geneId.length()-2);
					geneId=geneId.replaceAll("_", ".");


					BioGene newGene;
					if (this.getBioNetwork().getGeneList().containsKey(geneId)){
						newGene=this.getBioNetwork().getGeneList().get(geneId);
					}else{
						newGene=new BioGene(geneId,geneId);
						this.getBioNetwork().addGene(newGene);
					}

					bionetProt.addGene(newGene);

					this.getBioNetwork().addProtein(bionetProt);


					ComponentList.add(bionetProt);
				}
			}
		}
		return ComponentList;
	}


	public void parseIDForGene(BioProtein bionetProt) {

		if (bionetProt.getId().equals("")){
			System.out.println("Enpty gene value for :"+bionetProt.getName());
		}


		//create gene Id form protein Id
		String geneId=bionetProt.getId();
		if(geneId.startsWith("_")) geneId=geneId.substring(1, geneId.length());
		//remove compartment sub-string /!\ SPECIFIC TO PALSSON'S SBML
		if(Pattern.compile("^.+_\\w$", Pattern.CASE_INSENSITIVE).matcher(geneId).matches()) geneId=geneId.substring(0,geneId.length()-2);
		geneId=geneId.replaceAll("_", ".");

		if ( (bionetProt.getEntityAnnot()!=null) ){

			HashSet<String> geneNames=bionetProt.getEntityAnnot().getEncodedBy();

			if (!geneNames.isEmpty()){
				for ( String name :geneNames){

					BioGene newGene=this.getBioNetwork().getGeneList().get(geneId);
					if (newGene==null){
						newGene=new BioGene(geneId,name);
						newGene.setSboterm("SBO:0000335");
						this.getBioNetwork().addGene(newGene);
					}
					bionetProt.addGene(newGene);
				}
			}else{

				BioGene newGene=this.getBioNetwork().getGeneList().get(geneId);
				if (newGene==null){
					newGene=new BioGene(geneId,geneId);
					newGene.setSboterm("SBO:0000335");
					this.getBioNetwork().addGene(newGene);
				}

				bionetProt.addGene(newGene);
			}
		}
		else{
			BioGene newGene=this.getBioNetwork().getGeneList().get(geneId);
			if (newGene==null){
				newGene=new BioGene(geneId,geneId);
				newGene.setSboterm("SBO:0000335");
				this.getBioNetwork().addGene(newGene);
			}

			bionetProt.addGene(newGene);

		}

	}

	/**
	 * Parse the reaction's note to retrieve complementary or missing information
	 * Uses Palson's definition
	 * @param bionetReaction
	 */
	public void parseReactionNotes(BioChemicalReaction bionetReaction) {

		String reactionNotes=bionetReaction.getEntityNotes().getXHTMLasString();

		reactionNotes=reactionNotes.replaceAll(">\\s+<", "><").replaceAll("[\\n\\r]", "");
		Matcher m,m2;
		//get the pathway
		m = Pattern.compile(".*[> ]+SUBSYSTEM:\\s+([^<]+)<.*").matcher(reactionNotes);
		if( m.matches()){

			String[] pthwList=m.group(1).split(StringUtils.escapeSpecialRegexChars(" || "));

			for(String val:pthwList){
				String value = val.replaceAll("[^\\p{ASCII}]", "");

				if(this.getBioNetwork().getPathwayList().containsKey(value)){
					bionetReaction.addPathway(this.getBioNetwork().getPathwayList().get(value));
				}else{
					BioPathway bionetPath=new BioPathway(value,value);
					this.getBioNetwork().addPathway(bionetPath);
					bionetReaction.addPathway(bionetPath);
				}
			}
		}
		//get the ec number 
		m = Pattern.compile(".*[> ]+PROTEIN.CLASS:\\s+([^<]+)<.*").matcher(reactionNotes);
		m2 = Pattern.compile(".*[> ]+EC.Number:\\s+([^<]+)<.*").matcher(reactionNotes);
		if( m.matches()){
			String value = m.group(1);
			if (value.equals("")){
				value="NA";
			}
			bionetReaction.setEcNumber(value);
		}else if( m2.matches()){
			String value = m2.group(1);
			if (value.equals("")){
				value="NA";
			}
			bionetReaction.setEcNumber(value);
		}
		else {
			bionetReaction.setEcNumber("NA");
		}
		//get the reaction score
		m = Pattern.compile(".*[> ]+SCORE:\\s+([^<]+)<.*").matcher(reactionNotes);
		if( m.matches()){
			String value = m.group(1);
			bionetReaction.setScore(value);
		}
		//get the reaction score
		m = Pattern.compile(".*[> ]+STATUS:\\s+([^<]+)<.*").matcher(reactionNotes);
		if( m.matches()){
			String value = m.group(1);
			bionetReaction.setStatus(value);
		}
		//get the PMIDS
		m = Pattern.compile(".*[> ]+AUTHORS:\\s+([^<]+)<.*").matcher(reactionNotes);
		if (m.matches()){
			String[] Authorlist=m.group(1).split("(,\\s?)(?=PMID:)");
			for(String value:Authorlist){
				m = Pattern.compile("PMID:\\s?(\\d++).*").matcher(value);
				if (m.matches()){
					bionetReaction.addPmid(m.group(1));
				}
			}
		}
		//get the note/comment field (yes there is a note field in the sbml note element..)
		m = Pattern.compile(".*[> ]+NOTES:\\s+([^<]+)<.*").matcher(reactionNotes);
		m2 = Pattern.compile(".*[> ]+COMMENTS:\\s+([^<]+)<.*").matcher(reactionNotes);
		if( m.matches()){
			String value = m.group(1);
			if (value==""){
				value="NA";
			}
			bionetReaction.addComment(new Comment(value,"NA"));
		}
		if( m2.matches()){
			String value = m2.group(1);
			if (value==""){
				value="NA";
			}
			bionetReaction.addComment(new Comment(value,"NA"));
		}
		//get the GA information and compare it to the reaction's enzyme list 
		m = Pattern.compile(".*[> ]+GENE.{0,1}ASSOCIATION:\\s+([^<]+)<.*").matcher(reactionNotes);
		if( m.matches()){

			//System.err.println("first match: "+m.group(1));
			// separate the different combination of gene :
			// "( A and B ) or ( C and D )"  gives [ " A and B " , " C and D " ]
			String[] GeneAssoList=(m.group(1).replaceAll("[\\(\\)]", "")).split(" or ");

			//if the list of modifiers is not present or is incomplete. For compatibility with old SBMLs 
			if (GeneAssoList.length>bionetReaction.getEnzList().size()){



				//create the list of compartment ids for the regex.
				String regexCompartIds="["+this.getReactionCompart(bionetReaction)+"]";


				BioCompartment DefaultCompart;
				if (this.getBioNetwork().findbioCompartmentInList("fake_compartment")==null){
					DefaultCompart= new BioCompartment();
					DefaultCompart.setAsFakeCompartment();
					this.getBioNetwork().addCompartment(DefaultCompart);
				}else{
					DefaultCompart=this.getBioNetwork().findbioCompartmentInList("fake_compartment");
				}

				//				Matcher M;
				//				Set<BioPhysicalEntity> newEnzSet=new HashSet<BioPhysicalEntity>();

				//loop on the gene association list
				GALoop:
					for(String GeneAsso:GeneAssoList){

						//						System.err.println(bionetReaction.getId()+" has :"+GeneAsso);

						// this happens sometimes in some sbml files
						if (GeneAsso.contains("...")){
							continue;
						}

						// get single genes :
						// " A and B " gives ["A","B"]
						String[] geneList=GeneAsso.replaceAll(" ", "").split("and");

						//check if the enzyme is already present in the reaction or the network.

						String enzID_Regex="";
						String newEnzId="";
						HashMap<String,String> protIDs_REGEX=new HashMap<String,String>();

						for(String geneName:geneList){
							newEnzId+="_"+geneName.replaceAll("\\.", "_");
							String singleProtRegex="_"+geneName.replaceAll("\\.", "_")+"_"+regexCompartIds+"{0,1}";
							enzID_Regex+=singleProtRegex;
							protIDs_REGEX.put(geneName,singleProtRegex);
						}

						if (geneList.length==1){
							//length=1 enzyme is a protein

							String geneName=geneList[0];

							//							System.err.println(geneName);

							//if complex Id matches an existing one in the enzyme list of the reaction, we continue to next GA 
							for(BioPhysicalEntity rxnEnz: bionetReaction.getEnzList().values()){
								if(Pattern.compile(enzID_Regex, Pattern.CASE_INSENSITIVE).matcher(rxnEnz.getId()).matches()){
									continue GALoop;
								}
							}

							//if gene name matches an existing enzyme in the network, add it to the reaction and continue to the next GA
							for(BioPhysicalEntity networkEnz: this.getBioNetwork().getEnzList().values()){
								if(Pattern.compile(enzID_Regex, Pattern.CASE_INSENSITIVE).matcher(networkEnz.getId()).matches()){
									bionetReaction.addEnz(networkEnz);
									continue GALoop;
								}
							}

							//Looks through the protein list to find a protein matching the gene name, add it as an enzyme to the reaction and the network
							// then continue to the next GA
							for (BioProtein existingProtein:this.getBioNetwork().getProteinList().values()){
								if(Pattern.compile(enzID_Regex, Pattern.CASE_INSENSITIVE).matcher(existingProtein.getId()).matches()){
									bionetReaction.addEnz(existingProtein);
									this.getBioNetwork().getEnzList().put(existingProtein.getId(), existingProtein);
									continue GALoop;
								}
							}

							//If we are here, the protein is unknown. we need to create it:
							BioGene gene;
							if(this.getBioNetwork().getGeneList().containsKey(geneName)){
								gene=this.getBioNetwork().getGeneList().get(geneName);
							}else{
								//								System.err.println("geneId: "+ geneName);
								gene=new BioGene(geneName);
								this.getBioNetwork().addGene(gene);
							}

							BioProtein prot=new BioProtein("_"+geneName.replaceAll("\\.", "_").toUpperCase(),geneName+" (TH)");
							prot.setBoundaryCondition(true);
							prot.setConstant(false);
							prot.setSboterm("SBO:0000252");
							prot.setSubstanceUnits("false");
							prot.setCompartment(DefaultCompart);

							String protNotes="<notes>"
									+ "<body xmlns=\"http://www.w3.org/1999/xhtml\">"
									+ "<p> METEXPLORE_NOTE: This is a theoretical protein created through the Gene Association</p>"
									+ "</body>"
									+ "</notes>";
							prot.setEntityNotes(new Notes(protNotes));

							prot.addGene(gene);

							this.getBioNetwork().addProtein(prot);

							bionetReaction.addEnz(prot);
							this.getBioNetwork().getEnzList().put(prot.getId(), prot);


						}else if(geneList.length>1){

							//						System.err.println(bionetReaction.getId());
							//						System.err.println("enzyme regex: "+enzID_Regex);
							BioComplex foundEnzyme=null;

							SearchComplexLoop:
								for(BioComplex existingCplx:this.getBioNetwork().getComplexList().values() ){

									Collection<String> setRegex= protIDs_REGEX.values();
									Collection<BioPhysicalEntity> compolist = existingCplx.getAllComponentList().values();

									int needed=setRegex.size(), found=0;

									if(setRegex.size()!=compolist.size()){
										continue SearchComplexLoop;
									}

									compoLoop:
										for(BioPhysicalEntity compo:compolist){
											boolean notFound=true;
											//System.err.println("compo id: "+compo.getId());

											for(String regex:setRegex){
												//System.err.println("protein regex: "+regex);

												if(Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(compo.getId()).matches()){
													found++;
													//										System.err.println("found "+found+" out of "+needed);
													continue compoLoop;
												}
											}

											//if here, it means that component is not found, so not the good complex
											if(notFound){
												continue SearchComplexLoop;
											}
										}

									if(found==needed){

										foundEnzyme=existingCplx;
										if(!this.getBioNetwork().getEnzList().containsValue(existingCplx)){
											this.getBioNetwork().getEnzList().put(existingCplx.getId(), existingCplx);
										}
										if(!bionetReaction.getEnzList().containsValue(existingCplx)){
											bionetReaction.addEnz(existingCplx);
										}
										continue GALoop;
									}


								}

							if (foundEnzyme==null){
								//If we are here, the complex does not exist. We need to create but have to check if its component are not
								//already present. For this we use "protIDs_REGEX"

								//							System.err.println("new enzyme:"+newEnzId);

								foundEnzyme=new BioComplex(newEnzId,newEnzId);
								foundEnzyme.setBoundaryCondition(true);
								foundEnzyme.setConstant(false);
								foundEnzyme.setSboterm("SBO:0000297");
								foundEnzyme.setSubstanceUnits("false");
								foundEnzyme.setCompartment(DefaultCompart);
								this.getBioNetwork().addComplex(foundEnzyme);

								newCplxCompoLoop:
									for(Entry<String, String> geneProtRegex: protIDs_REGEX.entrySet()){
										String geneN=geneProtRegex.getKey(), prot_REGEX=geneProtRegex.getValue();

										for (BioProtein existingProtein:this.getBioNetwork().getProteinList().values()){
											if(Pattern.compile(prot_REGEX, Pattern.CASE_INSENSITIVE).matcher(existingProtein.getId()).matches()){
												foundEnzyme.addComponent(new BioPhysicalEntityParticipant(newEnzId+"__With__"+existingProtein.getId(),existingProtein));
												continue newCplxCompoLoop;
											}
										}

										//if we are here the gene has no protein in the network
										BioGene gene;
										if(this.getBioNetwork().getGeneList().containsKey(geneN)){
											gene=this.getBioNetwork().getGeneList().get(geneN);
										}else{
											//											System.err.println("geneId: "+ geneN);
											gene=new BioGene(geneN);
											this.getBioNetwork().getGeneList().put(geneN,gene);
										}

										BioProtein prot=new BioProtein("_"+geneN.replaceAll("\\.", "_").toUpperCase(),geneN+" (TH)");
										prot.setBoundaryCondition(true);
										prot.setConstant(false);
										prot.setSboterm("SBO:0000252");
										prot.setSubstanceUnits("false");
										prot.setCompartment(DefaultCompart);

										String protNotes="<notes>"
												+ "<body xmlns=\"http://www.w3.org/1999/xhtml\">"
												+ "<p> METEXPLORE_NOTE: This theoretical protein was created through the Gene Association and is a component of the protein complexe "
												+ newEnzId+"</p>"
												+ "</body>"
												+ "</notes>";
										prot.setEntityNotes(new Notes(protNotes));

										prot.addGene(gene);

										this.getBioNetwork().addProtein(prot);
										foundEnzyme.addComponent(new BioPhysicalEntityParticipant(newEnzId+"__With__"+prot.getId(),prot));

									}

								bionetReaction.addEnz(foundEnzyme);
								this.getBioNetwork().getEnzList().put(foundEnzyme.getId(), foundEnzyme);
							}
						}

					}


			}
		}
	}

	/**
	 * gets the ids of the compartment where the reaction takes place. For transport Reaction, it gets both ids 
	 * This is used to create regular expression to find ids of possible proteins used in the reaction
	 * 
	 * if this returns (c)(m), and the reaction has for GA 100.1, the regular expression will be "_100_1_[(c)(m)]?"
	 * it will not match proteins that are in lysosome (_100_1_l)
	 * 
	 * @param bionetReaction
	 * @return
	 */
	private String getReactionCompart(BioChemicalReaction bionetReaction) {
		String reactionComparts="";

		HashSet<String> ids=new HashSet<String>();

		for(BioPhysicalEntityParticipant parti:bionetReaction.leftParticipantList.values()){
			ids.add("("+parti.getLocation().getId()+")");
		}
		for(BioPhysicalEntityParticipant parti:bionetReaction.rightParticipantList.values()){
			ids.add("("+parti.getLocation().getId()+")");
		}

		for(String id:ids){
			reactionComparts+=id;
		}

		return reactionComparts;

	}

	/**
	 * Here, we will parse the notes of a metabolite the "old" way to get the missing informations
	 * @param bionetSpecies
	 */
	public void parseMetaboliteNote(BioPhysicalEntity bionetSpecies) {

		String metaboNotes=bionetSpecies.getEntityNotes().getXHTMLasString();
		metaboNotes=metaboNotes.replaceAll(">\\s+<", "><");
		Matcher m;
		//get the formula
		m = Pattern.compile(".*FORMULA:\\s*([^<]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String value = m.group(1);
			bionetSpecies.setChemicalFormula(value);
		}
		//get the Inchi
		m = Pattern.compile(".*INCHI:\\s*([^<\\s]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String value = m.group(1);
			bionetSpecies.setInchi(value);
			if(!bionetSpecies.hasRef("inchi", value)){
				bionetSpecies.addRef(new BioRef("SBML File", "inchi", value, 1));
			}
		}
		//get the SMILES :)
		m = Pattern.compile(".*SMILES:\\s*([^<\\s]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String value = m.group(1);
			bionetSpecies.setSmiles(value);
		}

		//get the InChI-Key
		m = Pattern.compile(".*INCHIKEY:\\s*([^<\\s]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String value = m.group(1);
			if(!bionetSpecies.hasRef("inchikey", value)){
				bionetSpecies.addRef(new BioRef("SBML File", "inchikey", value, 1));
			}
		}	

		//get the Kegg
		m = Pattern.compile(".*KEGG.COMPOUND:\\s*([^<]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String[] ids=m.group(1).split(" \\|\\| ");
			for (String value:ids){
				if(!bionetSpecies.hasRef("kegg.compound", value)){
					bionetSpecies.addRef(new BioRef("SBML File", "kegg.compound", value, 1));
				}
			}
		}

		//get the hmdb
		m = Pattern.compile(".*HMDB:\\s*([^<]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String[] ids=m.group(1).split(" \\|\\| ");
			for (String value:ids){
				if(!bionetSpecies.hasRef("hmdb", value)){
					bionetSpecies.addRef(new BioRef("SBML File", "hmdb", value, 1));
				}
			}
		}

		//get the chebi
		m = Pattern.compile(".*CHEBI:\\s*([^<]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String[] ids=m.group(1).split(" \\|\\| ");
			for (String value:ids){
				if(!bionetSpecies.hasRef("chebi", "CHEBI:"+value)){
					bionetSpecies.addRef(new BioRef("SBML File", "chebi", value, 1));
				}
			}
		}

		//PUBCHEM.COMPOUND
		m = Pattern.compile(".*PUBCHEM.COMPOUND:\\s*([^<]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String[] ids=m.group(1).split(" \\|\\| ");
			for (String value:ids){
				if(!bionetSpecies.hasRef("pubchem.compound", value)){
					bionetSpecies.addRef(new BioRef("SBML File", "pubchem.compound", value, 1));
				}
			}
		}

		//PUBCHEM.SUBSTANCE
		m = Pattern.compile(".*PUBCHEM.SUBSTANCE:\\s*([^<]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String[] ids=m.group(1).split(" \\|\\| ");
			for (String value:ids){
				if(!bionetSpecies.hasRef("pubchem.substance", value)){
					bionetSpecies.addRef(new BioRef("SBML File", "pubchem.substance", value, 1));
				}
			}
		}

		//KEGG.GENES
		m = Pattern.compile(".*KEGG.GENES:\\s*([^<\\s]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String[] ids=m.group(1).split(" \\|\\| ");
			for (String value:ids){
				if(!bionetSpecies.hasRef("kegg.genes", value)){
					bionetSpecies.addRef(new BioRef("SBML File", "kegg.genes", value, 1));
				}
			}
		}

		//UNIPROT
		m = Pattern.compile(".*UNIPROT:\\s*([^<\\s]+)<.*").matcher(metaboNotes);
		if( m.matches()){
			String[] ids=m.group(1).split(" \\|\\| ");
			for (String value:ids){
				if(!bionetSpecies.hasRef("uniprot", value)){
					bionetSpecies.addRef(new BioRef("SBML File", "uniprot", value, 1));
				}
			}
		}

		//get the charge if not already set
		if (bionetSpecies.getCharge()!=null){
			m = Pattern.compile(".*CHARGE:\\s*([^<]+)<.*").matcher(metaboNotes);
			if( m.matches()){
				String value = m.group(1);
				bionetSpecies.setCharge(value);
			}
		}

	}

	/**
	 * parse annotations field, extract dbId + dbName
	 * @param bionetSpecies
	 */
	public void parseMetaboliteAnnot(BioPhysicalEntity bionetSpecies) {
		BioAnnotation annot = bionetSpecies.getEntityAnnot();

		if(annot.getXMLasString()!=null){
			
			String ref = annot.getXMLasString().replaceAll(">\\s*<", "><");
//			System.out.println(ref);
			String regex1=".*?<bqbiol:([^>]+)><rdf:Bag>(.*?)</rdf:Bag>.*";
			String regex2=".*?<rdf:li rdf:resource=\"http://identifiers.org/([^/]+)/([^\"]+)\"/>.*";

			Matcher m=Pattern.compile(regex1).matcher(ref);

			while (m.matches()){
				String relation=m.group(1);

				String links=m.group(2);
				Matcher m2=Pattern.compile(regex2).matcher(links);

				while (m2.matches()){

					String dbName=m2.group(1);
					String dbId=m2.group(2);
					if(!bionetSpecies.hasRef(dbName, dbId)){
						bionetSpecies.addRef(dbName, dbId, 1, relation , "SBML File");
					}
					if(dbName.equals("inchi")){
						bionetSpecies.setInchi(dbId);
					}
					links=links.replace(dbName+"/"+dbId, "");

					m2=Pattern.compile(regex2).matcher(links);
				}
				ref=ref.replaceFirst("<bqbiol:([^>]+)><rdf:Bag>", "");
				m=Pattern.compile(regex1).matcher(ref);
			}
			
			String regex3=".*?<in:inchi xmlns:in=[^>]+>([^<]+)</in:inchi>.*";
			m=Pattern.compile(regex3).matcher(ref);
			if (m.matches() && m.group(1).startsWith("InChI=")){
				
				bionetSpecies.addRef("inchi", m.group(1), 1, "is" , "SBML File");
				bionetSpecies.setInchi(m.group(1));
			}
			
			
			
		}
//		if (bionetSpecies.getRefs().containsKey("inchi")){
//			System.err.println("specie has "+bionetSpecies.getRefs("inchi").size()+" inchi(s)");
//		}else{
//			System.err.println("specie has no inchi");
//		}

	}


	/**
	 * add a node to the entity's note using the note argument
	 * @param unusedSpecie
	 * @param note
	 */
	public void AddNote(BioEntity unusedSpecie, String note){
		String ComplementaryNote="<p>METEXPLORE_NOTE: "+note+"</p>\n</body>\n</notes>";

		if(unusedSpecie.getEntityNotes()!=null && !unusedSpecie.getEntityNotes().getXHTMLasString().equals("")){

			String existingNote=unusedSpecie.getEntityNotes().getXHTMLasString();

			existingNote=existingNote.replaceAll("</body>\\s*</notes>", ComplementaryNote);
			unusedSpecie.getEntityNotes().setXHTMLasString(existingNote);

		}else{
			String newNotes="<notes>\n<body xmlns=\"http://www.w3.org/1999/xhtml\">"+ComplementaryNote;
			unusedSpecie.setEntityNotes(new Notes(newNotes));
		}

	}

	public void parseModifierNote(BioPhysicalEntity bionetSpecies) {


	}

	public void parseModifierNote(BioComplex bionetSpecies) {


	}


	/**
	 * Get character data (CDATA) from xml document
	 * From http://www.java2s.com/Code/Java/XML/GetcharacterdataCDATAfromxmldocument.htm
	 * @param e
	 * @return
	 */
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getLastChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "";
	}

	/**
	 * @return the bioNetwork
	 */
	public BioNetwork getBioNetwork() {
		return bioNetwork;
	}

	/**
	 * @param bioNetwork the bioNetwork to set
	 */
	public void setBioNetwork(BioNetwork bioNetwork) {
		this.bioNetwork = bioNetwork;
	}

	public SBMLDocument getSBMLdoc() {
		return SBMLdoc;
	}

	public void setSBMLdoc(SBMLDocument sBMLdoc) {
		SBMLdoc = sBMLdoc;
	}

	public Set<String> getWarnings() {
		return Warnings;
	}

	public String getErrorThread() {
		return errorThread;
	}

	public void setWarnings(Set<String> warnings) {
		Warnings = warnings;
	}

	public void setErrorThread(String errorThread) {
		this.errorThread = errorThread;
	}

	public Model getjSBMLmodel() {
		return jSBMLmodel;
	}

	public void setjSBMLmodel(Model jSBMLmodel) {
		this.jSBMLmodel = jSBMLmodel;
	}

	public boolean isProccessing() {
		return processing;
	}

	public void setProccessing(boolean proccessing) {
		this.processing = proccessing;
	}


}
