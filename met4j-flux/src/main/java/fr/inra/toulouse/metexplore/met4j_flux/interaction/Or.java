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
/**
 * 7 mars 2013 
 */
package fr.inra.toulouse.metexplore.met4j_flux.interaction;

import fr.inra.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * This class represents an Or relation.
 * 
 * <p>
 * To be true, only one relation contained in the list must be true.
 * 
 * @author lmarmiesse 7 mars 2013
 * 
 */

public class Or extends RelationWithList {

	public String toString() {
		String s = "";
		s += "(";

		int i = 0;
		for (Relation rel : list) {
			if (i != 0) {
				s += " OR ";
			}

			s += rel;

			i++;
		}

		s += (")");
		return s;
	}

	public String toFormula() {
		String s = "";
		s += "(";

		int i = 0;
		for (Relation rel : list) {
			if (i != 0) {
				s += " || ";
			}

			s += rel.toFormula();

			i++;
		}

		s += (")");
		return s;
	}

	public boolean isTrue(Map<BioEntity, Constraint> simpleConstraints) {

		for (Relation rel : list) {
			if (rel.isTrue(simpleConstraints)) {
				return true;
			}
		}
		return false;

	}

	public boolean isInverseTrue(Map<BioEntity, Constraint> simpleConstraints) {

		for (Relation rel : list) {
			if (!rel.isInverseTrue(simpleConstraints)) {
				return false;
			}
		}
		return true;
	}

	protected void makeConstraints() {

		System.err.println("Error: Unsupported condition when interactions not in solver : " + this);

		constraints = new ArrayList<Constraint>();

	}

	/**
	 * Calculates "an expression value" of the relation given omics data results
	 * in one condition
	 * 
	 * @param sampleValues
	 * 
	 * @param method
	 *            1 => And : sum ; or : mean <br/>
	 *            2 => all mean
	 * 
	 * 
	 */
	public double calculateRelationQuantitativeValue(Map<BioEntity, Double> sampleValues, int method) {
		
		//mean
		if (method == 1 || method == 2) {

			double expr = 0;
			boolean allNaN = true;
			for (Relation rel : list) {

				double expr2 = rel.calculateRelationQuantitativeValue(sampleValues, method);

				if (!Double.isNaN(expr2)) {
					allNaN = false;
					expr += expr2;
				}

			}
			if (!allNaN) {
				return expr / list.size();
			} else {
				return Double.NaN;
			}
		}
		// minimum value
		else if (method == 3) {

			List<Double> exprs = new ArrayList<Double>();
			
			boolean allNaN = true;
			for (Relation rel : list) {

				double expr2 = rel.calculateRelationQuantitativeValue(sampleValues, method);
				if (!Double.isNaN(expr2)) {
					allNaN = false;
					exprs.add(expr2);
				}

			}
			if (!allNaN) {
				return Collections.min(exprs);
			} else {
				return Double.NaN;
			}

		}
		System.err.println("Error : unknow gpr calculation method : " + method);
		System.exit(0);
		return 0;
	}

}