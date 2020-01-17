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
 * 8 mars 2013
 * <p>
 * 8 mars 2013
 */
/**
 * 8 mars 2013 
 */
package fr.inra.toulouse.metexplore.met4j_flux.interaction;

import fr.inra.toulouse.metexplore.met4j_flux.analyses.result.FBAResult;
import fr.inra.toulouse.metexplore.met4j_flux.general.*;
import fr.inra.toulouse.metexplore.met4j_flux.io.Utils;
import fr.inra.toulouse.metexplore.met4j_flux.operation.OperationLe;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * @author lmarmiesse 8 mars 2013
 *
 */
public class InteractionIT {

    static BioRegulator a;
    static BioRegulator b;
    static BioRegulator c;
    static BioRegulator d;
    static BioRegulator e;
    static BioRegulator f;

    static String sbmlString = "";
    static String cond1String = "";
    static String int1String = "";
    static String cond2String = "";
    static String int2String = "";

    @BeforeClass
    public static void init() {


        File file;
        try {
            file = java.nio.file.Files.createTempFile("test", ".xml")
                    .toFile();

            sbmlString = Utils.copyProjectResource(
                    "testInteraction/test.xml", file);

            file = java.nio.file.Files.createTempFile("cond1", ".txt")
                    .toFile();

            cond1String = Utils.copyProjectResource(
                    "testInteraction/condElseTest.txt", file);

            file = java.nio.file.Files.createTempFile("int1", ".txt")
                    .toFile();

            int1String = Utils.copyProjectResource(
                    "testInteraction/intElseTest.sbml", file);

            file = java.nio.file.Files.createTempFile("cond2", ".txt")
                    .toFile();

            cond2String = Utils.copyProjectResource(
                    "testInteraction/condExtMetab.txt", file);


            file = java.nio.file.Files.createTempFile("int2", ".txt")
                    .toFile();

            int2String = Utils.copyProjectResource(
                    "testInteraction/intExtMetab.sbml", file);

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        a = new BioRegulator("a");
        b = new BioRegulator("b");
        c = new BioRegulator("c");
        d = new BioRegulator("d");
        e = new BioRegulator("e");
        f = new BioRegulator("f");
    }

    @Test
    public void test() throws Met4jSbmlReaderException, IOException {

        And rel1 = new And();
        Or rel2 = new Or();
        Unique rel3 = new Unique(c);

        rel2.addRelation(new Unique(b));
        rel2.addRelation(rel3);

        rel1.addRelation(new Unique(a));
        rel1.addRelation(rel2);

        assertTrue(rel1.toString().equals(
                "(a >= 0.0 AND (b >= 0.0 OR c >= 0.0))"));

        Unique intUnique = new Unique(f, new OperationLe(), 5.0);

        Interaction i1 = new IfThenInteraction(intUnique, rel1);
        System.err.println(i1);

        assertTrue(i1
                .toString()
                .equals("IF : (a >= 0.0 AND (b >= 0.0 OR c >= 0.0)) THEN : f <= 5.0 Begins after 0.0h, lasts 0.0h."));

        Unique u1 = new Unique(b);
        Unique u2 = new Unique(f);
        // Interaction i2 = new EqInteraction(u1,u2);

        // System.out.println(i2);

        Bind bind = null;

        String solver = "GLPK";
        if (System.getProperties().containsKey("solver")) {
            solver = System.getProperty("solver");
        }

        try {
            if (solver.equals("CPLEX")) {
                bind = new CplexBind();
            } else {
                bind = new GLPKBind();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            fail("Solver error");
        }

        fbaTest(bind);

        bind = null;


        try {
            if (solver.equals("CPLEX")) {
                bind = new CplexBind();
            } else {
                bind = new GLPKBind();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            fail("Solver error");
        }

        extMetabTest(bind);

    }

    private void fbaTest(Bind bind) throws Met4jSbmlReaderException, IOException {

        bind.loadSbmlNetwork(sbmlString);

        bind.loadConstraintsFile(cond1String);
        bind.loadRegulationFile(int1String);

        bind.prepareSolver();

        // double res = bind.FBA(new ArrayList<Constraint>(), true,
        // true).result;

        // bind.end();

        FBAResult result = new FBAResult(bind);

        DoubleResult objValue = bind.FBA(new ArrayList<Constraint>(), true,
                true);

        if (objValue.flag != 0) {

            System.err.println(objValue.result);

            System.err.println("Unfeasible");
            result.setObjValue(Double.NaN);

        } else {

            result.setObjValue(objValue.result);

        }
        result.formatResult();

//		result.plot();

        Assert.assertTrue(result.getObjValue() == 9.0);

//		System.out.println(bind.getSolvedValue(bind.getInteractionNetwork()
//				.getEntity("c")));

        Assert.assertTrue(bind.getSolvedValue(bind.getInteractionNetwork()
                .getEntity("c")) > 1.6);
        Assert.assertTrue(bind.getSolvedValue(bind.getInteractionNetwork()
                .getEntity("c")) < 1.7);

    }

    // / TEST THAT A EXTERNAL METAB AT 0 MAKES R_EX = 0

    private void extMetabTest(Bind bind) throws Met4jSbmlReaderException, IOException {

        bind.loadSbmlNetwork(sbmlString);
        bind.loadConstraintsFile(cond2String);

        bind.loadRegulationFile(int2String);

        bind.prepareSolver();

        FBAResult result = new FBAResult(bind);

        DoubleResult objValue = bind.FBA(new ArrayList<Constraint>(), true,
                true);

        if (objValue.flag != 0) {

            System.err.println(objValue.result);

            System.err.println("Unfeasible");
            result.setObjValue(Double.NaN);

        } else {

            result.setObjValue(objValue.result);

        }
        result.formatResult();

//		result.plot();

        System.out.println(result.getObjValue());

        Assert.assertTrue(result.getObjValue() == 6.0);

    }

}
