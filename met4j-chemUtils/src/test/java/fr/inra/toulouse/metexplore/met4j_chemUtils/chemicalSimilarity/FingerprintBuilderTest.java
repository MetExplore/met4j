package fr.inra.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;


import java.util.BitSet;


public class FingerprintBuilderTest {

    public static BioMetabolite m1 = new BioMetabolite("m1");

    public static BioMetabolite m2 = new BioMetabolite("m2");

    private FingerprintBuilder f;

    @BeforeClass
    public static void beforeClass() {
        m1.setInchi("1/C2H6O/c1-2-3/h3H,2H2,1H3");
        m2.setSmiles("OC(=O)CC(O)(CC(O)=O)C(O)=O");
    }

    @Test
    public void getFingerprintWhenInchi() {

        FingerprintBuilder mySpy = spy(new FingerprintBuilder(0));

        BitSet fakeBitSet = new BitSet(1);
        fakeBitSet.set(0, true);
        BitSet fakeBitSet2 = new BitSet(1);
        fakeBitSet2.set(0, false);

        doReturn(fakeBitSet).when(mySpy).getFingerprintFromInChi(anyString());
        doReturn(fakeBitSet2).when(mySpy).getFingerprintFromSmiles(anyString());

        Assert.assertEquals(mySpy.getFingerprint(m1), fakeBitSet);

    }

    @Test
    public void getFingerprintFromInChi() {
    }

    @Test
    public void getFingerprintFromSmiles() {
    }

    @Test
    public void getMACCSFingerprint() {
    }

    @Test
    public void getExtendedFingerprint() {
    }

    @Test
    public void getKlekotaRothFingerprint() {
    }

    @Test
    public void getPubchemFingerprint() {
    }

    @Test
    public void getEStateFingerprint() {
    }

    @Test
    public void getSubstructureFingerprint() {
    }

    @Test
    public void getSignature() {
    }
}