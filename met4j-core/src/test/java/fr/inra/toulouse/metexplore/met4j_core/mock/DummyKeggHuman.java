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
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioUnitDefinition;
import fr.inra.toulouse.metexplore.met4j_core.biodata.UnitSbml;

/**
 * This dummy class is created to be used in {@link TestLibSBMLToBionetwork_2}
 * All informations and test-case come from Recon2.v2 sbml file, and should be consistent with informations from Bionetwork build from this sbml file
 * 
 * @author clement
 */
public final class DummyKeggHuman implements DummySbml {
	public static final String sbmlPath = "met4j-core/src/test/resources/keggHomoSapiens.xml";
	private final String NOTES_VALUES_SEPARATOR = ", ";
	private final int NUMBER_OF_PATHWAYS = 87;
	private final int NUMBER_OF_REACTIONS = 1845;
	private final int NUMBER_OF_METABOLITES = 1548;
	private final int NUMBER_OF_COMPARTMENTS = 1;
	private final int NUMBER_OF_UNITS = 3;
	private final int NUMBER_OF_GENES = 1396;
	private final int NUMBER_OF_ENZYMES = 1396;
	private final int NUMBER_OF_COMPLEX = 1396;
	private final int NUMBER_OF_PROTEIN = 1396;
	private final int NUMBER_OF_REFS = 15426;
//	private BioPathway dummyPath;
	private BioReaction dummyR;
	private BioPhysicalEntity dummyE;
	private BioProtein dummyP;
	private BioGene dummyG;
	private BioCompartment dummyCmp;
	private BioComplex dummyCpx;
	private UnitSbml dummyU;
	private BioUnitDefinition dummyUd;
	
	
	public DummyKeggHuman() {
		
		dummyR = new BioReaction("R00494");
		dummyR.setName("glutathione gamma-glutamylaminopeptidase");
		dummyR.setSboterm("");
		dummyR.setReversibility(false);
		dummyR.setEcNumber("2.3.2.2");
		dummyR.setSpontaneous("false");
		dummyR.addPathway(new BioPathway("Glutathione metabolism - Homo sapiens (human)"));
		dummyR.addPathway(new BioPathway("Metabolic pathways - Homo sapiens (human)"));
		BioProtein enzyme = new BioProtein("enz");
		enzyme.addGene(new BioGene("hsa:2678"));
		enzyme.addGene(new BioGene("hsa:2686"));
		enzyme.addGene(new BioGene("hsa:2687"));
		enzyme.addGene(new BioGene("hsa:124975"));
		enzyme.addGene(new BioGene("hsa:2678"));
		enzyme.addGene(new BioGene("hsa:2686"));
		enzyme.addGene(new BioGene("hsa:2687"));
		enzyme.addGene(new BioGene("hsa:124975"));
		dummyR.addEnz(enzyme);
//		dummyR.addPmid("8847485");
//		dummyR.addPmid("9862787");
		
		dummyE = new BioPhysicalEntity("C00119");
		dummyE.setSboterm("");
		dummyE.setName("5-Phospho-alpha-D-ribose 1-diphosphate");
		dummyE.setCompartment(new BioCompartment("Default", "x"));
		dummyE.setHasOnlySubstanceUnit(false);
		dummyE.setBoundaryCondition(false);
		dummyE.setConstant(false);
		dummyE.setChemicalFormula("C5H13O14P3");
		dummyE.setCharge("0");
		dummyE.setInchi("InChI=1S/C5H13O14P3/c6-3-2(1-16-20(8,9)10)17-5(4(3)7)18-22(14,15)19-21(11,12)13/h2-7H,1H2,(H,14,15)(H2,8,9,10)(H2,11,12,13)/t2-,3-,4-,5-/m1/s1");
		dummyE.addRef("chebi", "CHEBI:17111", 1, "is","sbml file");
		dummyE.addRef("inchikey", "PQGCEDQWHSBAJP-TXICZTDVSA-N", 1, "is", "sbml file");
		
		dummyUd = new BioUnitDefinition("mmol_per_gDW_per_hr","mmol_per_gDW_per_hr");
		dummyU = new UnitSbml("mole", "1", "-3", "1.0");
					
		dummyCmp = new BioCompartment("Default","x");
		dummyCmp.setSpatialDimensions(3);
		dummyCmp.setSize(0);
		dummyCmp.setConstant(true);
							
		dummyG = new BioGene("hsa:10135");
//		dummyG.setSboterm("SBO:0000335");
		dummyG.setName( "hsa:10135");
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
	 
		dummyP = new BioProtein("_51091", "hsa:51091");
		dummyP.setSboterm( "");
		dummyP.setBoundaryCondition( true);
		dummyP.setConstant( false);
		dummyP.setCompartment(dummyCmp);
//		dummyP.addRef("uniprot", "Q9Y6M7", 1, "is", "sbml file");
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
}
