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
/**
 * 7 mars 2013 
 */
package fr.inrae.toulouse.metexplore.met4j_flux.interaction;

import fr.inrae.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inrae.toulouse.metexplore.met4j_flux.operation.Operation;
import fr.inrae.toulouse.metexplore.met4j_flux.operation.OperationGe;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class represents a Unique relation.
 * 
 * 
 * <p>
 * This type of relation doesn't contain any other relation and is the end of a
 * branch.
 * </p>
 * 
 * 
 * <p>
 * It contains an entity, a value and an operation.
 * </p>
 * 
 * @author lmarmiesse 7 mars 2013
 * 
 */
public class Unique extends Relation {

	/**
	 * Entity concerned by the relation.
	 */
	protected BioEntity entity;

	/**
	 * Value of the relation.
	 */
	protected double value;
	
	
	
	/**
	 * Priority when two uniques are true at the same time (used in TDRNA)
	 * 
	 * The higher the value, the higher the priority
	 */
	private int priority = 1;


	
	/**
	 * Operation of the relation.
	 */
	protected Operation operation;

	public Unique(BioEntity entity, Operation op, double value) {
		this.operation = op;
		this.entity = entity;
		this.value = value;
	}

	
	public Unique(BioEntity entity, Operation op, double value, int priority) {
		this.operation = op;
		this.entity = entity;
		this.value = value;
		this.priority = priority;
	}
	
	/**
	 * By default, operation is Greater or equal and the value is 0.
	 * 
	 * @param entity
	 *            Entity concerned by this relation.
	 */
	public Unique(BioEntity entity) {
		this.entity = entity;
		this.value = 0.0;
		this.operation = new OperationGe();
	}

	public BioEntity getEntity() {
		return entity;
	}
	
	public double getValue(){
		return value;
	}
	
	public void setValue(double value){
		this.value = value;
	}

	public String toString() {
		String s = "";
		s += entity.getId();

		s += operation;

		s += value;
		return s;
	}
	
	public String toFormula() {
		String s = "";
		s += entity.getId();

		s += operation.toFormula();

		s += value;
		return s;
	}

	public boolean isTrue(Map<BioEntity, Constraint> simpleConstraints) {

		if (!simpleConstraints.containsKey(entity)) {
			

			// System.err.println("unknown value for "+entity.getId()+", interaction ignored");
			return false;

		} else {

			
			
			Constraint cons = simpleConstraints.get(entity);
//			System.err.println(cons);

			return operation.isTrue(cons, value);
		}

	}
	
	public boolean isInverseTrue(Map<BioEntity, Constraint> simpleConstraints) {
		if (!simpleConstraints.containsKey(entity)) {

			// System.err.println("unknown value for "+entity.getId()+", interaction ignored");
			return false;

		} else {

			Constraint cons = simpleConstraints.get(entity);
			
		

			return operation.isInverseTrue(cons, value);
		}
	}

	public boolean isUndeterminedVariable(Map<BioEntity, Constraint> simpleConstraints) {

		if (!simpleConstraints.containsKey(entity)) {

			// System.err.println("unknown value for "+entity.getId()+", interaction ignored");
			return true;

		}
		return false;

	}

	protected void makeConstraints() {

		constraints = operation.makeConstraint(entity, value);

	}

	public List<BioEntity> getInvolvedEntities() {

		List<BioEntity> entities = new ArrayList<BioEntity>();
		entities.add(entity);
		return entities;
	}

	/**
	 * Calculates "an expression value" of the relation given omics data results in one condition
	 * @param sampleValues 
	 */
	public double calculateRelationQuantitativeValue(Map<BioEntity, Double> sampleValues, int method) {
		
		if (sampleValues.containsKey(entity)){
			return sampleValues.get(entity);
		}else{
			return Double.NaN;
		}
		
		
	}
	
	
	
	public int getPriority(){
		return priority;
	}
	
	public void setPriority(int pr){
		this.priority=pr;
	}



}