/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.biodata;

import java.util.HashMap;
import java.util.HashSet;

public class QualitativeTransition extends BioControl {
	
	private HashMap<String,QualitativeParticipant> listOfInputParticipant=new HashMap<String,QualitativeParticipant>();
	
	private HashMap<String,QualitativeParticipant> listOfOutputParticipant=new HashMap<String,QualitativeParticipant>();;
	
	private HashSet<QualitativeFunction> listOfFunction=new HashSet<QualitativeFunction>();

	
	/*
	 * Constructor
	 * */
	public QualitativeTransition(String id) {
		super(id);
	}
	
	public QualitativeTransition(String id,String name) {
		super(id);
		this.setName(name);
	}
	
	public void addInputParticipant(QualitativeParticipant QP){
		this.getListOfInputParticipant().put(QP.getId(), QP);
	}
	
	public void addOutputParticipant(QualitativeParticipant QP){
		this.getListOfOutputParticipant().put(QP.getId(), QP);
	}
	
	public void addFunctionTerm(QualitativeFunction QFT){
		this.getListOfFunction().add(QFT);
	}
	
	/*
	* getter and setter
	*/	
	public HashMap<String, QualitativeParticipant> getListOfInputParticipant() {
		return listOfInputParticipant;
	}

	public HashMap<String, QualitativeParticipant> getListOfOutputParticipant() {
		return listOfOutputParticipant;
	}

	public void setListOfInputParticipant(
			HashMap<String, QualitativeParticipant> listOfInputParticipant) {
		this.listOfInputParticipant = listOfInputParticipant;
	}

	public void setListOfOutputParticipant(
			HashMap<String, QualitativeParticipant> listOfOutputParticipant) {
		this.listOfOutputParticipant = listOfOutputParticipant;
	}

	public HashSet<QualitativeFunction> getListOfFunction() {
		return listOfFunction;
	}

	public void setListOfFunction(HashSet<QualitativeFunction> listOfFunction) {
		this.listOfFunction = listOfFunction;
	}

}
