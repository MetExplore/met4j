package fr.inra.toulouse.metexplore.met4j_flux.input;

import fr.inra.toulouse.metexplore.met4j_flux.general.Bind;
import fr.inra.toulouse.metexplore.met4j_flux.general.CplexBind;
import fr.inra.toulouse.metexplore.met4j_flux.general.GLPKBind;
import fr.inra.toulouse.metexplore.met4j_flux.interaction.RelationFactory;
import fr.inra.toulouse.metexplore.met4j_flux.interaction.Unique;
import fr.inra.toulouse.metexplore.met4j_flux.io.Utils;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SBMLQualReaderIT {

    static String coliFileString = "";
    static String condFileString = "";
    static String qualString = "";

    @Test
    public void test() throws Met4jSbmlReaderException, IOException {

        File file;
        try {
            file = java.nio.file.Files.createTempFile("coli", ".xml").toFile();

            coliFileString = Utils.copyProjectResource(
                    "SBMLQual/coli_core.xml", file);

            file = java.nio.file.Files.createTempFile("cond", ".txt").toFile();

            condFileString = Utils
                    .copyProjectResource(
                            "SBMLQual/conditionsFBA.txt",
                            file);

            file = java.nio.file.Files.createTempFile("qual", ".xml").toFile();

            qualString = Utils.copyProjectResource(
                    "SBMLQual/test_myb30.xml", file);

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

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

        bind.loadSbmlNetwork(coliFileString);

        bind.loadConstraintsFile(condFileString);

        SBMLQualReader.loadSbmlQual(qualString, bind.getInteractionNetwork(),
                new RelationFactory());

        BioEntity entity1 = bind.getInteractionNetwork().getEntity("s_MYB30");
        BioEntity entity2 = bind.getInteractionNetwork().getEntity("s_MYB96");
        BioEntity entity3 = bind.getInteractionNetwork().getEntity("s_VLCFA");
        BioEntity entity4 = bind.getInteractionNetwork()
                .getEntity("s_Bacteria");

        assertTrue(bind.getInteractionNetwork().getInitialState(entity1) == 3);

        assertTrue(bind.getInteractionNetwork().getInitialState(entity2) == 1);

        assertTrue(bind.getInteractionNetwork().getInitialState(entity3) == 0);

        // /interactions

        assertTrue(bind.getInteractionNetwork().getTargetToInteractions()
                .get(entity1).getConditionalInteractions().get(0)
                .getCondition().getInvolvedEntities().get(0).getId()
                .equals("s_Bacteria"));

        assertTrue(((Unique) bind.getInteractionNetwork()
                .getTargetToInteractions().get(entity1)
                .getConditionalInteractions().get(0).getCondition()).getValue() == 6);

        assertTrue(bind.getInteractionNetwork().getTargetToInteractions()
                .get(entity1).getConditionalInteractions().get(0)
                .getConsequence().getEntity() == entity1);

        assertTrue(bind.getInteractionNetwork().getTargetToInteractions()
                .get(entity1).getConditionalInteractions().get(0)
                .getConsequence().getValue() == 1);

        assertTrue(bind.getInteractionNetwork().getTargetToInteractions()
                .get(entity1).getdefaultInteraction().getConsequence()
                .getValue() == 0);

        assertTrue(bind.getInteractionNetwork()
                .getConstraintFromState(entity1, 0).getLb() == 0.0);
        assertTrue(bind.getInteractionNetwork()
                .getConstraintFromState(entity1, 0).getUb() == 0.5);

        assertTrue(bind.getInteractionNetwork().getStateFromValue(entity1, 0.7) == 1);

        // System.out.println(bind.getInteractionNetwork().getTargetToInteractions()
        // .get(entity2).getConditionalInteractions().get(0));

    }

}