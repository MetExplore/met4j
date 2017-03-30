/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: clement.frainay@toulouse.inra.fr
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.io.BioNetworkToJSBML;
import fr.inra.toulouse.metexplore.met4j_core.io.JSBMLToBionetwork;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummyKeggHuman;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummyRecon2modelv02;
import fr.inra.toulouse.metexplore.met4j_core.mock.DummySbml;

/**
 * 
 * @author clement
 * Testing data consistency of a bioNetwork before and after exporting to sbml, using {@link BioNetworkToJSBML} export function. 
 * Execute same test methods as the import test class {@link TestLibSBMLToBionetwork},
 * but doesn't create the BioNetwork to test directly from an import : 
 * import file1 > export in file2 > import file2
 */
@RunWith(Parameterized.class)
public class TestBioNetworkToJSBML extends TestJSBMLToBionetwork {

	private static boolean run = false;
	private static ArrayList<String> allRefs;
	
	@Parameters(name = "{0}")
	public static Iterable<Object[]> data1() {
		return Arrays.asList(new Object[][] { 
			{new DummyRecon2modelv02()},
			{new DummyKeggHuman()}
		});
	}
	
	
	/**
	 * add a new reference in the bionetwork created from file 1, to check if newly added references are exported
	 */
	public TestBioNetworkToJSBML(DummySbml dummyParam){
		
		super(dummyParam);
		if(run==false){
			try{
				//add new ref
				bn.getBioPhysicalEntityById(dummy.getTestCompound().getId()).addRef("dummy", "ref", 0, "is", "beyond infinity");
				allRefs=getRefs(bn);
				
				//export BioNetwork to sbml
				String outputPath = sbml.replace(".tmp", ".exported.tmp");
				new BioNetworkToJSBML(bn,outputPath ).write();
				bn = null;
				
				//import exported sbml
				JSBMLToBionetwork sbml2bn = new JSBMLToBionetwork(outputPath);
				bn = sbml2bn.getBioNetwork();
				
				//remove exported sbml
				//(new File(outputPath)).delete();
				(new File(outputPath+".unprocessed")).delete();
				
				run = true;
			}catch(Exception e){
				e.printStackTrace();
				fail("Error while exporting");
			}
		}
	}
	
	/**
	 * Override TestLibSBMLToBionetwork.testRefs to fit the new expected number of reference
	 */
	@Override
	public void testRefs(){
		
		assertTrue("error exporting newly added ref", bn.getBioPhysicalEntityById(dummy.getTestCompound().getId()).hasRef("dummy", "ref"));
		
		ArrayList<String> allRefs2 = getRefs(bn);
		
		boolean pass=true;
		assertEquals("not the same number of ref",allRefs.size() ,allRefs2.size());
		for(String r : allRefs){
			if(!allRefs2.contains(r)){
				System.err.println("missing ref: "+r);
				pass=false;
			}
		}
		if (!pass){
			fail("missing external database ref");
		}
	}
	
	
	/**
	 * 
	 * @param bn
	 * @return complete list of database ref (from compound, reaction, gene, protein) in the following format: database_name-database_id
	 */
	public ArrayList<String> getRefs(BioNetwork bn){
		ArrayList<String> allRefs = new ArrayList<String>();
		for(BioEntity e : bn.getPhysicalEntityList().values()){
			for(Set<BioRef> refs : e.getRefs().values()){
				for(BioRef ref : refs){
					allRefs.add(ref.getDbName()+"-"+ref.getId());
				}
			}
		}
		for(BioReaction e : bn.getBiochemicalReactionList().values()){
			for(Set<BioRef> refs : e.getRefs().values()){
				for(BioRef ref : refs){
					allRefs.add(ref.getDbName()+"-"+ref.getId());
				}
			}
		}
		for(BioGene e : bn.getGeneList().values()){
			for(Set<BioRef> refs : e.getRefs().values()){
				for(BioRef ref : refs){
					allRefs.add(ref.getDbName()+"-"+ref.getId());
				}
			}
		}
		for(BioProtein e : bn.getProteinList().values()){
			for(Set<BioRef> refs : e.getRefs().values()){
				for(BioRef ref : refs){
					allRefs.add(ref.getDbName()+"-"+ref.getId());
				}
			}
		}
		for(BioComplex e : bn.getComplexList().values()){
			for(Set<BioRef> refs : e.getRefs().values()){
				for(BioRef ref : refs){
					allRefs.add(ref.getDbName()+"-"+ref.getId());
				}
			}
		}
		return allRefs;
	}
	
}
