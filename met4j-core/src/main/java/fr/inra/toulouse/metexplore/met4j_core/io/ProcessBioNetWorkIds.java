/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.io;

import java.util.HashMap;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils;

public class ProcessBioNetWorkIds {

	public BioNetwork bionetwork;
	public boolean toEncode;


	public ProcessBioNetWorkIds(BioNetwork importedNetwork, boolean enc){
		this.setBionetwork(importedNetwork);
		this.setToEncode(enc);
	}



	public void process(){
		
		String netSID;
		if (this.isToEncode()){
			netSID=StringUtils.sbmlEncode(this.getBionetwork().getId());
		}else{
			netSID=StringUtils.sbmlDecode(this.getBionetwork().getId());
		}
		this.getBionetwork().setId(netSID);
				
		/*
		 * BioCompartment does not extend BioEntity, so it has be done separately
		 */
		HashMap<String, BioCompartment> tmpCmpts=new HashMap<String, BioCompartment>();		
		for (BioCompartment cmpt: this.getBionetwork().getCompartments().values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(cmpt.getId());
			}else{
				newSID=StringUtils.sbmlDecode(cmpt.getId());
			}
			cmpt.setId(newSID);
			tmpCmpts.put(newSID, cmpt);
		}
		this.getBionetwork().getCompartments().clear();
		this.getBionetwork().getCompartments().putAll(tmpCmpts);

		/*
		 * The other lists contain BioEntity derived classes 
		 */

		this.ProcessUnitDefHashMap(this.getBionetwork().getUnitDefinitions());
		this.ProcessPathwayHashMap(this.getBionetwork().getPathwayList());
		this.ProcessReactionHashMap(this.getBionetwork().getBiochemicalReactionList());
		this.ProcessBioComplexHashMap(this.getBionetwork().getComplexList());
		this.ProcessGeneHashMap(this.getBionetwork().getGeneList());
		this.ProcessProteinHashMap(this.getBionetwork().getProteinList());
		this.ProcessEntityHashMap(this.getBionetwork().getEnzymeList());
		this.ProcessEntityHashMap(this.getBionetwork().getPhysicalEntityList());

	}




	private void ProcessUnitDefHashMap(HashMap<String, BioUnitDefinition> ElementList) {

		HashMap<String, BioUnitDefinition>tmpMap=new HashMap<String, BioUnitDefinition>();

		for ( BioUnitDefinition entity: ElementList.values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(entity.getId());
			}else{
				newSID=StringUtils.sbmlDecode(entity.getId());
			}
			entity.setId(newSID);
			tmpMap.put(newSID, entity);
		}
		ElementList.clear();
		ElementList.putAll(tmpMap);
	}



	private void ProcessReactionHashMap(HashMap<String, BioReaction> ElementList){

		HashMap<String, BioReaction>tmpMap=new HashMap<String, BioReaction>();

		for ( BioReaction entity: ElementList.values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(entity.getId());
			}else{
				newSID=StringUtils.sbmlDecode(entity.getId());
			}
			entity.setId(newSID);
			tmpMap.put(newSID, entity);
			
			this.ProcessPathwayHashMap(entity.getPathwayList());
			this.ProcessEntityHashMap(entity.getEnzList());
			
		}
		ElementList.clear();
		ElementList.putAll(tmpMap);
	}



	private void ProcessPathwayHashMap(HashMap<String, BioPathway> ElementList) {

		HashMap<String, BioPathway>tmpMap=new HashMap<String, BioPathway>();

		for ( BioPathway entity: ElementList.values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(entity.getId());
			}else{
				newSID=StringUtils.sbmlDecode(entity.getId());
			}
			entity.setId(newSID);
			tmpMap.put(newSID, entity);
		}
		ElementList.clear();
		ElementList.putAll(tmpMap);
	}



	private void ProcessBioComplexHashMap(HashMap<String, BioComplex> ElementList) {
		HashMap<String, BioComplex>tmpMap=new HashMap<String, BioComplex>();

		for ( BioComplex entity: ElementList.values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(entity.getId());
			}else{
				newSID=StringUtils.sbmlDecode(entity.getId());
			}
			entity.setId(newSID);
			tmpMap.put(newSID, entity);
		}
		ElementList.clear();
		ElementList.putAll(tmpMap);

	}



	private void ProcessProteinHashMap(HashMap<String, BioProtein> ElementList) {

		HashMap<String, BioProtein>tmpMap=new HashMap<String, BioProtein>();

		for ( BioProtein entity: ElementList.values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(entity.getId());
			}else{
				newSID=StringUtils.sbmlDecode(entity.getId());
			}
			entity.setId(newSID);
			tmpMap.put(newSID, entity);
		}
		ElementList.clear();
		ElementList.putAll(tmpMap);

	}





	private void ProcessGeneHashMap(HashMap<String, BioGene> ElementList) {
		HashMap<String, BioGene>tmpMap=new HashMap<String, BioGene>();

		for ( BioGene entity: ElementList.values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(entity.getId());
			}else{
				newSID=StringUtils.sbmlDecode(entity.getId());
			}
			entity.setId(newSID);
			tmpMap.put(newSID, entity);
		}
		ElementList.clear();
		ElementList.putAll(tmpMap);

	}



	private void ProcessEntityHashMap(HashMap<String, BioPhysicalEntity> ElementList) {

		HashMap<String, BioPhysicalEntity>tmpMap=new HashMap<String, BioPhysicalEntity>();

		for ( BioPhysicalEntity entity: ElementList.values()){
			String newSID;
			if (this.isToEncode()){
				newSID=StringUtils.sbmlEncode(entity.getId());
			}else{
				newSID=StringUtils.sbmlDecode(entity.getId());
			}
			entity.setId(newSID);
			tmpMap.put(newSID, entity);
			
			
			
			if(entity.getClass().getSimpleName().compareTo("BioComplex") == 0){
				this.ProcessEntityHashMap(((BioComplex)entity).getAllComponentList());
			}
			else if(entity.getClass().getSimpleName().compareTo("BioProtein") == 0){
				this.ProcessGeneHashMap(((BioProtein)entity).getGeneList());
			}
			
			
		}
		ElementList.clear();
		ElementList.putAll(tmpMap);

	}



	public BioNetwork getBionetwork() {
		return bionetwork;
	}


	public void setBionetwork(BioNetwork bionetwork) {
		this.bionetwork = bionetwork;
	}



	public boolean isToEncode() {
		return toEncode;
	}



	public void setToEncode(boolean toEncode) {
		this.toEncode = toEncode;
	}



}
