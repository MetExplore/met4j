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
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.UnitSbml;

/**
 * This dummy class is created to be used in {@link TestLibSBMLToBionetwork_2}
 * All informations and test-case come from Recon2.v2 sbml file, and should be consistent with informations from Bionetwork build from this sbml file
 * 
 * @author clement
 */
public final class DummyBioCycHuman implements DummySbml {
	public static final String sbmlPath = "BioCycHomoSapiens.xml";
	private final String NOTES_VALUES_SEPARATOR = ",";
	private final int NUMBER_OF_REACTIONS = 2527;
	private final int NUMBER_OF_METABOLITES = 2701;
	private final int NUMBER_OF_COMPARTMENTS = 15;
	private final int NUMBER_OF_UNITS = 3;
	private final int NUMBER_OF_GENES = 3583;
	private final int NUMBER_OF_ENZYMES = 3618;
	private final int NUMBER_OF_COMPLEX = 3618;
	private final int NUMBER_OF_PROTEIN = 3609;
	private final int NUMBER_OF_REFS = 3836;
	private BioChemicalReaction dummyR;
	private BioPhysicalEntity dummyE;
	private BioProtein dummyP;
	private BioGene dummyG;
	private BioCompartment dummyCmp;
	private BioComplex dummyCpx;
	private UnitSbml dummyU;
	private BioUnitDefinition dummyUd;
	
	
	public DummyBioCycHuman() {
		
		dummyR = new BioChemicalReaction("CDPDIGLYSYN-RXN");
		dummyR.setName("CDPDIGLYSYN-RXN");
//		dummyR.setSboterm("SBO:0000176");
		dummyR.setReversibility(false);
		dummyR.setEcNumber("2.7.7.41");
		dummyR.setSpontaneous("false");
		dummyR.addPathway(new BioPathway("CDP-diacylglycerol biosynthesis"));
		BioProtein enzyme = new BioProtein("enz");
		enzyme.addGene(new BioGene("CDS2"));
		enzyme.addGene(new BioGene("CDS1"));
		enzyme.addGene(new BioGene("phosphatidate cytidylyltransferase 2"));
		enzyme.addGene(new BioGene("phosphatidate cytidylyltransferase 1"));
		dummyR.addEnz(enzyme);
//		dummyR.addPmid("8847485");
//		dummyR.addPmid("9862787");
		
		dummyE = new BioPhysicalEntity("DELTA3__45__ISOPENTENYL__45__PP");
//		dummyE.setSboterm("SBO:0000247");
		dummyE.setName("isopentenyl diphosphate");
		dummyE.setCompartment(new BioCompartment("cytosol","CCO-CYTOSOL"));
		dummyE.setHasOnlySubstanceUnit(false);
		dummyE.setBoundaryCondition(false);
		dummyE.setConstant(false);
		dummyE.setChemicalFormula("C5H9O7P2");
		dummyE.setCharge("0");
		dummyE.setInchi("InChI=1S/C5H12O7P2/c1-5(2)3-4-11-14(9,10)12-13(6,7)8/h1,3-4H2,2H3,(H,9,10)(H2,6,7,8)");
		dummyE.addRef("chebi", "CHEBI:16584", 1, "is","sbml file");
		dummyE.addRef("inchikey", "NUHSROFQTUXZQQ-UHFFFAOYSA-N", 1, "is", "sbml file");
		
		dummyUd = new BioUnitDefinition("mmol_per_gDW_per_hr","mmol_per_gDW_per_hr");
		dummyU = new UnitSbml("mole", "1", "-3", "1.0");
					
		dummyCmp = new BioCompartment("Golgi lumen","CCO-GOLGI-LUM");
		dummyCmp.setSpatialDimensions(3);
		dummyCmp.setSize(0);
		dummyCmp.setConstant(true);
							
		dummyG = new BioGene("HS03963");
//		dummyG.setSboterm("SBO:0000335");
		dummyG.setName("AAK1");
		dummyG.setHasOnlySubstanceUnit(false);
		dummyG.setBoundaryCondition(false);
		dummyG.setConstant(false);
		
//		dummyCpx = new BioComplex("51091");
//		dummyCpx.setSboterm("SBO:0000297");
//		dummyCpx.setName("ACAA2:ECHS1:HADH");
//		dummyCpx.setBoundaryCondition(true);
//		dummyCpx.setConstant(false);
//		dummyCpx.setCompartment(new BioCompartment("Mitochondria", "m"));
//		dummyCpx.addComponent(new BioPhysicalEntityParticipant("_10449_1_m", new BioPhysicalEntity("_10449_1_m")));
//		dummyCpx.addComponent(new BioPhysicalEntityParticipant("_1892_1_m", new BioPhysicalEntity("_1892_1_m")));
//		dummyCpx.addComponent(new BioPhysicalEntityParticipant("_3033_1_m", new BioPhysicalEntity("_3033_1_m")));
//		dummyCpx.addRef("uniprot", "P42765", 1, "hasPart", "sbml file");
//		dummyCpx.addRef("uniprot", "P30084", 1, "hasPart", "sbml file");
//		dummyCpx.addRef("uniprot", "Q16836", 1, "hasPart", "sbml file");
	 
		dummyP = new BioProtein("HS16984-MONOMER","tyrosine kinase");
//		dummyP.setSboterm( "SBO:0000252");
		dummyP.setBoundaryCondition( true);
		dummyP.setConstant( false);
		dummyP.setCompartment(dummyCmp);
//		dummyP.addRef("uniprot", "Q9Y6M7", 1, "is", "sbml file");
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
