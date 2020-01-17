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

package fr.inra.toulouse.metexplore.met4j_flux.analyses;

import fr.inra.toulouse.metexplore.met4j_flux.analyses.result.KOResult;
import fr.inra.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inra.toulouse.metexplore.met4j_flux.general.CplexBind;
import fr.inra.toulouse.metexplore.met4j_flux.general.GLPKBind;
import fr.inra.toulouse.metexplore.met4j_flux.io.Utils;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.fail;

public class KoWithInteractionsIT {

    private static File tempSbmlFile = null;
    private static File tempConditionFile = null;
    private static File tempRegulationFile = null;

    @Test
    public void test() throws Met4jSbmlReaderException, IOException {

        String solver = "GLPK";
        if (System.getProperties().containsKey("solver")) {
            solver = System.getProperty("solver");
        }

        java.nio.file.Path tmpSbml = null;
        java.nio.file.Path tmpCondition = null;
        java.nio.file.Path tmpRegulation = null;

        try {
            tmpSbml = java.nio.file.Files.createTempFile("test", ".xml");
            tmpCondition = java.nio.file.Files.createTempFile("test", ".tab");
            tmpRegulation = java.nio.file.Files.createTempFile("test", ".txt");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            Assert.fail("Creation of the temporary files");
            e1.printStackTrace();
        }

        tempSbmlFile = tmpSbml.toFile();
        tempConditionFile = tmpCondition.toFile();
        tempRegulationFile = tmpRegulation.toFile();

        String sbmlFile = "";
        String conditionFile = "";
        String regFile = "";

        try {
            sbmlFile = Utils
                    .copyProjectResource(
                            "ko/test.xml",
                            tempSbmlFile);
            conditionFile = Utils
                    .copyProjectResource(
                            "ko/constraintsWithVariables.txt",
                            tempConditionFile);
            regFile = Utils
                    .copyProjectResource(
                            "ko/interactions.sbml",
                            tempRegulationFile);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Assert.fail("problem while copying the reference files");
        }

        Bind bind = null;

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

        bind.loadSbmlNetwork(sbmlFile);
        bind.loadConstraintsFile(conditionFile);
        bind.loadRegulationFile(regFile);

        bind.prepareSolver();

        /**
         * Test essential genes
         */
        KOAnalysis a = new KOAnalysis(bind, 1, null);
        KOResult res = a.runAnalysis();

        Set<String> refEssentialGenes = new HashSet<String>();
        refEssentialGenes.add("G2");
        Set<String> essentialGenes = res.getEssentialEntities().getIds();

        Assert.assertEquals(refEssentialGenes, essentialGenes);

        /**
         * Test essential reactions
         */
        a = new KOAnalysis(bind, 0, null);
        res = a.runAnalysis();

        Set<String> refEssentialReactions = new HashSet<String>();
        refEssentialReactions.add("R2");
        refEssentialReactions.add("R_D_EX");
        refEssentialReactions.add("R_C_EX");
        Set<String> essentialReactions = res.getEssentialEntities().getIds();

        Assert.assertEquals(refEssentialReactions, essentialReactions);

    }

}
