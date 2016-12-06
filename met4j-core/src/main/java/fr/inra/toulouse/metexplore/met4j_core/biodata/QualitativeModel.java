/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.HashMap;

public class QualitativeModel {
	
	
	
	private HashMap<String,QualitativeSpecie> listOfQualSpecies=new HashMap<String,QualitativeSpecie>();
	
	private HashMap<String,QualitativeTransition> listOfQualTransitions=new HashMap<String,QualitativeTransition>();
	
	public QualitativeModel(){
		
	}
		
	public QualitativeModel(QualitativeModel QM){
		this.setListOfQualSpecies(QM.getListOfQualSpecies());
		this.setListOfQualTransitions(QM.getListOfQualTransitions());
	}

	public HashMap<String,QualitativeSpecie> getListOfQualSpecies() {
		return listOfQualSpecies;
	}

	public void setListOfQualSpecies(HashMap<String,QualitativeSpecie> listOfQualSpecies) {
		this.listOfQualSpecies = listOfQualSpecies;
	}
	
	public void addQualSpecie(QualitativeSpecie QualSpecies) {
		this.listOfQualSpecies.put(QualSpecies.getId(), QualSpecies);
	}
	

	public HashMap<String,QualitativeTransition> getListOfQualTransitions() {
		return listOfQualTransitions;
	}

	public void setListOfQualTransitions(HashMap<String,QualitativeTransition> listOfQualTransitions) {
		this.listOfQualTransitions = listOfQualTransitions;
	}
	
	public void addQualTransition(QualitativeTransition QualTransition) {
		this.listOfQualTransitions.put(QualTransition.getId(), QualTransition);
	}



}
