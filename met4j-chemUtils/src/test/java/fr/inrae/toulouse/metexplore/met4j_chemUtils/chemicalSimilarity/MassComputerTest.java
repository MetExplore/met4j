package fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.MassComputor;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import org.junit.Before;
import org.junit.Test;

public class MassComputerTest {

    public static BioNetwork bioNetwork;
    public static BioMetabolite m1;
    public static BioMetabolite m2;
    public static BioMetabolite m3;
    public static BioMetabolite m4;

    @Before
    public void init() throws Exception {
        m1 = new BioMetabolite("m1"); m1.setChemicalFormula("C8H10N4O2");m1.setMolecularWeight(3615.0);
        m2 = new BioMetabolite("m2"); m2.setChemicalFormula("C14H19NO2");
        m3 = new BioMetabolite("m3");
        m4 = new BioMetabolite("m4"); m4.setChemicalFormula("?!");
        bioNetwork = new BioNetwork();
        bioNetwork.add(m1,m2,m3,m4);
    }

    @Test
    public void testMassComputation() {
        MassComputor massComputor = new MassComputor();
        massComputor.setMolecularWeights(bioNetwork);
        assert(m1.getMolecularWeight()!=null && Math.abs(m1.getMolecularWeight()-194.19)<0.01);
        assert(m2.getMolecularWeight()!=null && Math.abs(m2.getMolecularWeight()-233.31)<0.01);
    }

    @Test
    public void testMassComputation1() {
        MassComputor massComputor = new MassComputor().setOnlyIfMissing(true).setWarn(false);
        massComputor.setMolecularWeights(bioNetwork);
        assert(m1.getMolecularWeight()!=null && Math.abs(m1.getMolecularWeight()-3615.0)<0.01);
        assert(m2.getMolecularWeight()!=null && Math.abs(m2.getMolecularWeight()-233.31)<0.01);
    }

    @Test
    public void testMassComputation2() {
        MassComputor massComputor = new MassComputor().useMonoIsotopicMass().setWarn(false);
        massComputor.setMolecularWeights(bioNetwork);
        assert(m1.getMolecularWeight()!=null && Math.abs(m1.getMolecularWeight()-194.08)<0.01);
        assert(m2.getMolecularWeight()!=null && Math.abs(m2.getMolecularWeight()-233.14)<0.01);
    }



}
