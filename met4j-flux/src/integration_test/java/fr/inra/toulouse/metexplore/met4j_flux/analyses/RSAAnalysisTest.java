package fr.inra.toulouse.metexplore.met4j_flux.analyses;

import fr.inra.toulouse.metexplore.met4j_flux.analyses.result.RSAAnalysisResult;
import fr.inra.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inra.toulouse.metexplore.met4j_flux.input.ConstraintsFileReader;
import fr.inra.toulouse.metexplore.met4j_flux.input.SBMLQualReader;
import fr.inra.toulouse.metexplore.met4j_flux.interaction.InteractionNetwork;
import fr.inra.toulouse.metexplore.met4j_flux.interaction.RelationFactory;
import fr.inra.toulouse.metexplore.met4j_flux.utils.TestUtils;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RSAAnalysisTest {

	@Test
	public void test() {

		String sbmlQualFile = "";
		String consFile = "";

		File file;
		try {
			file = java.nio.file.Files.createTempFile("regFile", ".xml")
					.toFile();

			sbmlQualFile = TestUtils.copyProjectResource(
					"rsa/lacOperon.sbml", file);

			file = java.nio.file.Files.createTempFile("constraints", ".txt")
					.toFile();

			consFile = TestUtils.copyProjectResource(
					"rsa/ConstraintsRSA.txt", file);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		InteractionNetwork intNet = SBMLQualReader.loadSbmlQual(sbmlQualFile,
				new InteractionNetwork(), new RelationFactory());

		Map<BioEntity, Constraint> simpleConstraints = new HashMap<BioEntity, Constraint>();

		ConstraintsFileReader r = new ConstraintsFileReader(consFile, intNet);
		r.readConstraintsFile();

		simpleConstraints = r.simpleConstraints;

		RSAAnalysis rsa = new RSAAnalysis(intNet, simpleConstraints);
		
		RSAAnalysisResult res = rsa.runAnalysis();
		
		Assert.assertTrue(res.getSteadyStateConstraints().size()==15);
		
		for (Constraint c : res.getSteadyStateConstraints()){
			
			if(((BioEntity) c.getEntities().keySet().toArray()[0]).getId().equals("M_lcts_b")){
				Assert.assertTrue(c.getUb()==5.8);
				Assert.assertTrue(c.getLb()==5.8);
			}
			if(((BioEntity) c.getEntities().keySet().toArray()[0]).getId().equals("allolactose")){
				Assert.assertTrue(c.getUb()==1);
				Assert.assertTrue(c.getLb()==1);
			}
			if(((BioEntity) c.getEntities().keySet().toArray()[0]).getId().equals("betaGal")){
				Assert.assertTrue(c.getUb()==1);
				Assert.assertTrue(c.getLb()==1);
			}
		}
		
		Assert.assertTrue(res.getStatesList().size()==6);
		
		
//		res.plot();
//		
//		while (true){
//			
//		}
	}
}
