/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.mock;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.io.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.io.UnitSbml;

/**
 * This dummy class is created to be used in {@link TestLibSBMLToBionetwork_2}
 * All informations and test-case come from Recon2.v2 sbml file, and should be consistent with informations from Bionetwork build from this sbml file
 * 
 * @author clement
 */
public final class DummyRattusBioModels implements DummySbml {
	public static final String sbmlPath = "RattusNorvegicus_BioModels_Fev2015.xml";
	private final String NOTES_VALUES_SEPARATOR = ", ";
	private final int NUMBER_OF_PATHWAYS = 0;
	private final int NUMBER_OF_REACTIONS = 3586;
	private final int NUMBER_OF_METABOLITES = 3404;
	private final int NUMBER_OF_COMPARTMENTS = 3;
	private final int NUMBER_OF_UNITS = 1;
	private final int NUMBER_OF_GENES = 1642;
	private final int NUMBER_OF_ENZYMES = 0;//TODO
	private final int NUMBER_OF_COMPLEX = 0;//TODO
	private final int NUMBER_OF_PROTEIN = 0;//TODO
	private final int NUMBER_OF_REFS = 0;//TODO
	private BioPathway dummyPath;
	private BioReaction dummyR;
	private BioPhysicalEntity dummyE;
	private BioProtein dummyP;
	private BioGene dummyG;
	private BioCompartment dummyCmp;
	private BioComplex dummyCpx;
	private UnitSbml dummyU;
	private BioUnitDefinition dummyUd;
	
	
	public DummyRattusBioModels() {
		
		dummyR = new BioReaction("MNXR16397_i");
		dummyR.setName("1 CMP-N-acetyl-beta-neuraminate + 1  = 1  + 1 CMP");
		dummyR.setSboterm("SBO:0000176");
		dummyR.setEcNumber("NA");
		BioProtein enzyme = new BioProtein("enz");
		enzyme.addGene(new BioGene("_362924_i"));
		enzyme.addGene(new BioGene("_64442_i"));
		enzyme.addGene(new BioGene("_64442_2_i"));
		dummyR.addEnz(enzyme);
		dummyR.addRef("KEGG","R05967",1,"is","sbml file");
		dummyR.addRef("MXNREF","MNXR16397",1,"is","sbml file");
		BioRef dummyReactionRef = new BioRef("sbml file", "ec-code", "2.4.99.4", 1);
		dummyReactionRef.setBaseURI("http://identifiers.org/ec-code/");
		dummyReactionRef.setLogicallink("isVersionOf");
		dummyR.addRef(dummyReactionRef);
		
		
		dummyCmp = new BioCompartment("intracellular","i");
		dummyCmp.setSboterm("SBO:0000290");
		dummyCmp.setSize(1);
		BioRef dummyCmpRef = new BioRef("sbml file","go","GO:0005622",1);
		dummyCmpRef.setLogicallink("is");
		dummyCmpRef.setBaseURI("http://identifiers.org/go/");
		
		dummyE = new BioPhysicalEntity("bigg_dad_5_i");
		dummyE.setSboterm("SBO:0000247");//*
		dummyE.setName("5'-deoxyadenosine");
		dummyE.setCompartment(dummyCmp);
		dummyE.setHasOnlySubstanceUnit(false);
		dummyE.setBoundaryCondition(false);
		dummyE.setConstant(false);
		dummyE.setChemicalFormula("C10H13N5O3");
		dummyE.setCharge("0");
		dummyE.setInchi("InChI=1S/C10H13N5O3/c1-4-6(16)7(17)10(18-4)15-3-14-5-8(11)12-2-13-9(5)15/h2-4,6-7,10,16-17H,1H3,(H2,11,12,13)/t4-,6-,7-,10-/m1/s1");
		dummyE.setSmiles("C[C@H]1O[C@H]([C@H](O)[C@@H]1O)N1C=NC2=C(N)N=CN=C12");
		BioRef dummyERef1 = new BioRef("sbml file", "chebi", "17319", 1);
		dummyERef1.setLogicallink("is");
		dummyERef1.setBaseURI("http://identifiers.org/chebi/CHEBI:");
		dummyE.addRef(dummyERef1);
		dummyE.addRef("BRENDA","BG832",1,"is","sbml file");
		dummyE.addRef("CHEBI","40099",1,"is","sbml file");
		dummyE.addRef("CHEBI","20493",1,"is","sbml file");
		dummyE.addRef("CHEBI","1960",1,"is","sbml file");
		dummyE.addRef("CHEBI","12061",1,"is","sbml file");
		dummyE.addRef("HMDB","HMDB01983",1,"is","sbml file");
		dummyE.addRef("INCHI","InChI=1S/C10H13N5O3/c1-4-6(16)7(17)10(18-4)15-3-14-5-8(11)12-2-13-9(5)15/h2-4,6-7,10,16-17H,1H3,(H2,11,12,13)/t4-,6-,7-,10-/m1/s1",1,"is","sbml file");
		dummyE.addRef("KEGG","C05198",1,"is","sbml file");
		dummyE.addRef("METACYC","CH33ADO",1,"is","sbml file");
		dummyE.addRef("MXNREF","MNXM316",1,"is","sbml file");
		dummyE.addRef("REACTOME","947703",1,"is","sbml file");
		dummyE.addRef("SEED","cpd03091",1,"is","sbml file");
		dummyE.addRef("UPA","UPC05198",1,"is","sbml file");
		
		dummyUd = new BioUnitDefinition("FLUX_UNIT","NA");
		dummyU = new UnitSbml("mole","NA","NA","NA");
		UnitSbml dummyU2 = new UnitSbml("second","-1","NA","NA");
		dummyUd.addUnit(dummyU);
		dummyUd.addUnit(dummyU2);
	}

	
	@Override
	public String getSbmlPath() {
		return sbmlPath;
	}

	
	public int getNumberOfPathway() {
		return NUMBER_OF_PATHWAYS;
	}
	
	@Override
	public int getNumberOfReaction() {
		return NUMBER_OF_REACTIONS;
	}

	@Override
	public int getNumberOfMetabolite() {
		return NUMBER_OF_METABOLITES;
	}

	@Override
	public int getNumberOfCompartments() {
		return NUMBER_OF_COMPARTMENTS;
	}

	@Override
	public int getNumberOfUnits() {
		return NUMBER_OF_UNITS;
	}

	@Override
	public int getNumberOfGenes() {
		return NUMBER_OF_GENES;
	}

	@Override
	public int getNumberOfEnzyme() {
		return NUMBER_OF_ENZYMES;
	}

	@Override
	public int getNumberOfComplex() {
		return NUMBER_OF_COMPLEX;
	}

	@Override
	public int getNumberOfProtein() {
		return NUMBER_OF_PROTEIN;
	}

	@Override
	public int getNumberOfRefs() {
		return NUMBER_OF_REFS;
	}

	@Override
	public BioPhysicalEntity getTestCompound() {
		return dummyE;
	}

	@Override
	public BioReaction getTestReaction() {
		return dummyR;
	}

	@Override
	public BioProtein getTestProtein() {
		return dummyP;
	}

	@Override
	public BioGene getTestGene() {
		return dummyG;
	}

	@Override
	public BioComplex getTestComplex() {
		return dummyCpx;
	}

	@Override
	public UnitSbml getTestUnit() {
		return dummyU;
	}
	
	@Override
	public BioUnitDefinition getTestUnitDef() {
		return dummyUd;
	}
	
	@Override
	public BioCompartment getTestComp() {
		return dummyCmp;
	}

	@Override
	public String toString(){
		return this.getClass().getName();
	}


	@Override
	public String getNotesValueSeparator() {
		return NOTES_VALUES_SEPARATOR;
	}


	/**
	 * @return the dummyPath
	 */
	public BioPathway getDummyPath() {
		return dummyPath;
	}
}
