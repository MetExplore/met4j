package fr.inra.toulouse.metexplore.met4j_flux.interaction;

import fr.inra.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

import java.util.Map;

public class UndeterminedUnique extends Unique {

	public UndeterminedUnique(BioEntity entity) {
		super(entity);
	
	}

	public boolean isTrue(Map<BioEntity, Constraint> simpleConstraints) {

		return false;

	}
	
	public String toString() {
		String s = "Undetermined";
		return s;
	}
	
}
