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

package fr.inrae.toulouse.metexplore.met4j_flux.analyses;

import fr.inrae.toulouse.metexplore.met4j_flux.analyses.result.RSAAnalysisResult;
import fr.inrae.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inrae.toulouse.metexplore.met4j_flux.input.ConstraintsFileReader;
import fr.inrae.toulouse.metexplore.met4j_flux.input.SBMLQualReader;
import fr.inrae.toulouse.metexplore.met4j_flux.interaction.InteractionNetwork;
import fr.inrae.toulouse.metexplore.met4j_flux.interaction.RelationFactory;
import fr.inrae.toulouse.metexplore.met4j_flux.io.Utils;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RSAAnalysisIT {

	@Test
	public void test() {

		String sbmlQualFile = "";
		String consFile = "";

		File file;
		try {
			file = java.nio.file.Files.createTempFile("regFile", ".xml")
					.toFile();

			sbmlQualFile = Utils.copyProjectResource(
					"rsa/lacOperon.sbml", file);

			file = java.nio.file.Files.createTempFile("constraints", ".txt")
					.toFile();

			consFile = Utils.copyProjectResource(
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
