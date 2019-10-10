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
	public void test() throws Met4jSbmlReaderException {

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
