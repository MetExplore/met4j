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