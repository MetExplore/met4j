/**
 * Copyright INRA
 * <p>
 * Contact: ludovic.cottret@toulouse.inra.fr
 * <p>
 * <p>
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * <p>
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
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
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 * <p>
 * 5 avr. 2013
 */
/**
 * 5 avr. 2013 
 */
package fr.inra.toulouse.metexplore.met4j_flux.analyses;

import fr.inra.toulouse.metexplore.met4j_flux.analyses.result.FBAResult;
import fr.inra.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inra.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inra.toulouse.metexplore.met4j_flux.general.DoubleResult;
import fr.inra.toulouse.metexplore.met4j_flux.general.Vars;

import java.util.ArrayList;

/**
 *
 * Class to run a simple FBA analysis.
 *
 * @author lmarmiesse 5 avr. 2013
 *
 */
public class FBAAnalysis extends Analysis {

    public FBAAnalysis(Bind b) {
        super(b);
    }

    public FBAResult runAnalysis() {

        FBAResult result = new FBAResult(b);

        System.err.println(new java.util.Date());
        System.err.println("Launch FBA");
        DoubleResult objValue = b.FBA(new ArrayList<Constraint>(), true, true);
        System.err.println(new java.util.Date());
        System.err.println("End FBA");

        if (objValue.flag != 0) {

            if (Vars.verbose) {
                System.err.println(objValue.result);

                System.err.println("Unfeasible");
            }
            result.setObjValue(Double.NaN);

        } else {

            result.setObjValue(objValue.result);

        }
        System.err.println("Format FBA");
        result.formatResult();
        System.err.println(new java.util.Date());
        System.err.println("End format FBA");
        return (FBAResult) result;
    }

}
