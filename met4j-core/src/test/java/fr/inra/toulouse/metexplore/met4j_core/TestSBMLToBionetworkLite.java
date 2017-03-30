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
package fr.inra.toulouse.metexplore.met4j_core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.io.RefHandler;
import fr.inra.toulouse.metexplore.met4j_core.io.Sbml2BioNetworkLite;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummyKeggHuman;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummyRattusBioModels;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummyRecon2v02;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummyRecon2v03;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummySbml;
import fr.inra.toulouse.metexplore.met4j_core.mock.TestUtils;

@RunWith(Parameterized.class)
public class TestSBMLToBionetworkLite {

			
	public static BioNetwork bn = null;
	protected static DummySbml dummy;
	protected static String sbml= null;
	
	//add dummySbml to test here
	@Parameters(name = "{0}")
	public static Iterable<Object[]> data1() {
		return Arrays.asList(new Object[][] { 
			{new DummyRecon2v02()},
			{new DummyRecon2v03()},
			{new DummyKeggHuman()},
			{new DummyRattusBioModels()},
			
		});
	}
	
	
	//import sbml
	public TestSBMLToBionetworkLite(DummySbml dummyParam) {
		if(dummy==null || dummy!=dummyParam){
			dummy = dummyParam;
			System.out.println("import "+dummy.getSbmlPath());
			//create temporary directory
			Path tmpPath = null;
			try {
				tmpPath = Files.createTempFile("test_parseBioNet", ".tmp");
			} catch (IOException e1) {
				e1.printStackTrace();
				Assert.fail("Creation of the temporary directory");
			}
			File temp = tmpPath.toFile();
			
			//Copy resource
			try {
				sbml = TestUtils.copyProjectResource(dummy.getSbmlPath(), temp);
			} catch (IOException e) {
				e.printStackTrace();
				fail("problem while reading the sbml file");
			}
			
			//import sbml
			try{
				long t0 = System.nanoTime();
				Sbml2BioNetworkLite sbml2bn = new Sbml2BioNetworkLite(sbml, true);
				sbml2bn.setNotesValueSeparator(dummy.getNotesValueSeparator());
				sbml2bn.addDefaultRefHandlers();
				sbml2bn.addRefHandlers(new RefHandler("inchikey","http://identifiers.org/inchikey/", "[A-Z\\-]+", false));
				sbml2bn.addRefHandlers(new RefHandler("chebi","http://identifiers.org/chebi/", "(CHEBI:)?\\d+"));
				sbml2bn.addRefHandlers(new RefHandler("CHEBI","http://identifiers.org/chebi/", "(CHEBI:)?\\d+"));
				sbml2bn.addRefHandlers(new RefHandler("KEGG","http://identifiers.org/kegg/"));
				bn = sbml2bn.getBioNetwork();
				long t1 = System.nanoTime();
				System.err.println("time to import BioNetwork : "+(t1-t0)+"ns");
			}catch(Exception e){
				e.printStackTrace();
				fail("Error while parsing");
			}
		}
	}
 

	
	/**
	 *check if reaction list in bionetwork is not empty
	 *check if there is as many reaction as expected
	 *check if the bionetwork contain a given reaction and if all its attributes are as expected
	 *	(name, sbo term, reversibility, gene list, pathway list, ec number, pmids, spontaneous)
	 *check for each reaction if substrate, product, genes and pathway are present in the bionetwork
	 */
	@Test
	public void testReactions() {
		assertFalse("empty biochemical reactions list", bn.getBiochemicalReactionList().isEmpty());
		assertEquals("wrong number of biochemical reactions", dummy.getNumberOfReaction(), bn.getBiochemicalReactionList().size());
		
		try{
			BioReaction r0 = dummy.getTestReaction();
			BioReaction r = bn.getBiochemicalReactionList().get(r0.getId());
			assertEquals("error in reaction name :", r0.getName(), r.getName());
			assertEquals("error in reaction reversibility :", r0.isReversible(), r.isReversible());
			if(r0.getPathwayList()!=null){
				for(String p0 : r0.getPathwayList().keySet()){
					assertTrue("error in reaction pathway \""+p0+"\" not found in [\""+StringUtils.join(r.getPathwayList().keySet(),"\", \"")+"\"]", r.getPathwayList().containsKey(p0));
				}
				for(String p0 : r0.getPathwayList().keySet()){
					assertTrue("error in reaction pathway", r.getPathwayList().containsKey(p0));
				}
			}
			assertEquals("error in reaction ec number :", r0.getEcNumber(), r.getEcNumber());
			
				
			
		}catch(Exception e){
			e.printStackTrace();
			fail("missing reaction or attribut");
		}
		
		for(BioReaction r : bn.getBiochemicalReactionList().values()){
			for(BioPhysicalEntity s : r.getLeftList().values()){
				assertTrue("error in reaction: substrat not found in Bionetwork", bn.getPhysicalEntityList().containsValue(s));
			}
			for(BioPhysicalEntity p : r.getRightList().values()){
				assertTrue("error in reaction: product not found in Bionetwork", bn.getPhysicalEntityList().containsValue(p));
			}
			for(BioPathway path : r.getPathwayList().values()){
				assertTrue("error in reaction: pathway not found in Bionetwork", bn.getPathwayList().containsValue(path));
			}
			
		}
		
	}
	
	
	
	/**
	 *check if compound list in bionetwork is not empty
	 *check if there is as many compound as expected
	 *check if the bionetwork contain a given compound and if all its attributes are as expected
	 *	(name, sbo term, compartment, boudaryCondition, HasOnlySubstanceUnit, constant, 
	 *	formula, charge, inchi, refs)
	 *check for each compound if compartment are present in the bionetwork
	 *check for each ref for each compound if database name and database id aren't empty or blank
	 */
	@Test
	public void testCompounds() {
		
		BioPhysicalEntity entity0 = dummy.getTestCompound();
		
		assertFalse("empty metabolite list", bn.getPhysicalEntityList().isEmpty());
		assertEquals("wrong number of metabolites :", dummy.getNumberOfMetabolite(), bn.getPhysicalEntityList().size());
		
		try{
			BioPhysicalEntity entity = bn.getBioPhysicalEntityById(entity0.getId());
			assertEquals("error in compound name :", entity0.getName(), entity.getName());
			assertEquals("error in compound compartment :", entity0.getCompartment().getId(), entity.getCompartment().getId());
			assertEquals("error in compound formula :", entity0.getChemicalFormula(), entity.getChemicalFormula());
			assertEquals("error in compound inchi :", entity0.getInchi(), entity.getInchi());
			for(Set<BioRef> refs0 : entity0.getRefs().values()){
				for(BioRef ref0 : refs0){
					assertTrue("error in compound refs "+ref0.dbName+" - "+ref0.id, entity.hasRef(ref0.dbName, ref0.id));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("missing compound or attribut");
		}
		
		for(BioPhysicalEntity entity : bn.getPhysicalEntityList().values()){
			assertTrue("error in compound "+entity.getId()+": compartment not found in Bionetwork: "+entity.getCompartment().getName(),bn.getCompartments().values().contains(entity.getCompartment()));
		}

	}
	
	
	/**
	 *check if compartment list in bionetwork is not empty
	 *check if there is as many compartment as expected
	 *check if the bionetwork contain a given compartment and if all its attributes are as expected
	 *	(name, spatial dimention, size, constant)
	 */
	@Test
	public void testCompartments() {
		
		BioCompartment comp0 = dummy.getTestComp();
		
		assertFalse("empty compartment list",bn.getCompartments().isEmpty());
		
		// As no modifier is added to the bionetwork, the fake compartment isn't used anymore
		assertEquals("wrong number of compartments :", dummy.getNumberOfCompartments(), bn.getCompartments().size());
		
		try{
			BioCompartment comp = bn.getCompartments().get(comp0.getId());
			assertEquals("error in compartment name :", comp0.getName(), comp.getName());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("missing compartments or attribut");
		}
	}
	
}