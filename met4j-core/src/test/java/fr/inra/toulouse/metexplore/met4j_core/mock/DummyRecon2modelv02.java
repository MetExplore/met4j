/*
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.mock;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioComplex;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.UnitSbml;

/**
 * This dummy class is created to be used in {@link TestLibSBMLToBionetwork_2}
 * All informations and test-case come from Recon2.v2 sbml file, and should be consistent with informations from Bionetwork build from this sbml file
 * 
 * @author clement
 */
public final class DummyRecon2modelv02 implements DummySbml {
	public static final String sbmlPath = "recon2model.v02.xml";
	private final String NOTES_VALUES_SEPARATOR = "; ";
	private final int NUMBER_OF_REACTIONS = 7440;
	private final int NUMBER_OF_METABOLITES = 5063;
	private final int NUMBER_OF_COMPARTMENTS = 9;
	private final int NUMBER_OF_UNITS = 3;
	private final int NUMBER_OF_GENES = 2191;
	private final int NUMBER_OF_ENZYMES = 3470;
	private final int NUMBER_OF_COMPLEX = 1168;
	private final int NUMBER_OF_PROTEIN = 3907;
	private final int NUMBER_OF_REFS = 15334;
	private BioChemicalReaction dummyR;
	private BioPhysicalEntity dummyE;
	private BioProtein dummyP;
	private BioGene dummyG;
	private BioCompartment dummyCmp;
	private BioComplex dummyCpx;
	private UnitSbml dummyU;
	private BioUnitDefinition dummyUd;
	
	
	public DummyRecon2modelv02() {
		
		dummyR = new BioChemicalReaction("R_RE3597C");
		dummyR.setName("RE3597");
		dummyR.setSboterm("SBO:0000176");
		dummyR.setReversibility(true);
		dummyR.setEcNumber("1.1.1.184");
		dummyR.setSpontaneous("false");
		dummyR.addPathway(new BioPathway("Eicosanoid metabolism"));
		BioProtein enzyme = new BioProtein("enz");
		enzyme.addGene(new BioGene("874.1"));
		enzyme.addGene( new BioGene("873.1"));
		dummyR.addEnz(enzyme);
		dummyR.addPmid("8847485");
		dummyR.addPmid("9862787");
		
		dummyE = new BioPhysicalEntity("M_HC01401_m");
		dummyE.setSboterm("SBO:0000247");
		dummyE.setName("(S)-3-Hydroxydodecanoyl-CoA");
		dummyE.setCompartment(new BioCompartment("Mitochondria", "m"));
		dummyE.setHasOnlySubstanceUnit(false);
		dummyE.setBoundaryCondition(false);
		dummyE.setConstant(false);
		dummyE.setChemicalFormula("C33H54N7O18P3S");
		dummyE.setCharge("-4");
		dummyE.setInchi("InChI=1S/C33H58N7O18P3S/c1-4-5-6-7-8-9-10-11-21(41)16-24(43)62-15-14-35-23(42)12-13-36-31(46)28(45)33(2,3)18-55-61(52,53)58-60(50,51)54-17-22-27(57-59(47,48)49)26(44)32(56-22)40-20-39-25-29(34)37-19-38-30(25)40/h19-22,26-28,32,41,44-45H,4-18H2,1-3H3,(H,35,42)(H,36,46)(H,50,51)(H,52,53)(H2,34,37,38)(H2,47,48,49)/p-4/t21-,22+,26+,27+,28-,32+/m0/s1");
		dummyE.addRef("chebi", "CHEBI:27668", 1, "is", "sbml file");
		//dummyE.addRef("inchikey", "IJFLXRCJWPKGKJ-LXIXEQKWSA-N", 1, "sbml file");
		
		dummyUd = new BioUnitDefinition("mmol_per_gDW_per_hr","mmol_per_gDW_per_hr");
		dummyU = new UnitSbml("MOLE", "1", "-3", "1.0");
					
		dummyCmp = new BioCompartment("Golgi apparatus","g");
		dummyCmp.setSpatialDimensions(3);
		dummyCmp.setSize(1);
		dummyCmp.setConstant(false);
							
		dummyG = new BioGene("9497.1");
		dummyG.setSboterm("SBO:0000335");
		dummyG.setName( "hsa:9497");
		dummyG.setHasOnlySubstanceUnit(false);
		dummyG.setBoundaryCondition(false);
		dummyG.setConstant(false);
		
		dummyCpx = new BioComplex("_10449_1_m_1892_1_m_3033_1_m");
		dummyCpx.setSboterm("SBO:0000297");
		dummyCpx.setName("ACAA2:ECHS1:HADH");
		dummyCpx.setBoundaryCondition(true);
		dummyCpx.setConstant(true);
		dummyCpx.setCompartment(new BioCompartment("Mitochondria", "m"));
		dummyCpx.addComponent(new BioPhysicalEntityParticipant("_10449_1_m", new BioPhysicalEntity("_10449_1_m")));
		dummyCpx.addComponent(new BioPhysicalEntityParticipant("_1892_1_m", new BioPhysicalEntity("_1892_1_m")));
		dummyCpx.addComponent(new BioPhysicalEntityParticipant("_3033_1_m", new BioPhysicalEntity("_3033_1_m")));
		dummyCpx.addRef("uniprot", "P42765", 1, "hasPart", "sbml file");
		dummyCpx.addRef("uniprot", "P30084", 1, "hasPart", "sbml file");
		dummyCpx.addRef("uniprot", "Q16836", 1, "hasPart", "sbml file");
	 
		dummyP = new BioProtein("_9497_1_c", "SLC4A7");
		dummyP.setSboterm( "SBO:0000252");
		dummyP.setBoundaryCondition( true);
		dummyP.setConstant( false);
		dummyP.setCompartment(new BioCompartment("Cytoplasm", "c"));
		dummyP.addRef("uniprot", "Q9Y6M7", 1, "is", "sbml file");
	}

	
	@Override
	public String getSbmlPath() {
		return sbmlPath;
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
	public BioChemicalReaction getTestReaction() {
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
}