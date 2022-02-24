/*
 * Copyright INRAE (2022)
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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;

public class SetEcsFromFileTest {


    private SetEcsFromFile setEcsFromFile;
    private BioNetwork network;
    private BioReaction r;
    private String line;

    @Before
    public void init() throws IOException {

        network = new BioNetwork();
        r = new BioReaction("r");
        network.add(r);

        line = "r\t1.2.3.5\n";

        setEcsFromFile = Mockito.spy(new SetEcsFromFile(0, 1, network, "", "", 0, false, false));

        Mockito.doReturn(true).when(setEcsFromFile).parseAttributeFile();


    }

    @Test
    public void testAttributeNormal() {
        String ec = "1.2.3.4";
        assertTrue(setEcsFromFile.testAttribute(ec));
        ec = "1.2.3.134";
        assertTrue(setEcsFromFile.testAttribute(ec));
        ec = "1.2.3.-";
        assertTrue(setEcsFromFile.testAttribute(ec));
        ec = "1.2.3";
        assertTrue(setEcsFromFile.testAttribute(ec));
        ec = "EC1.2.3.134";
        assertTrue(setEcsFromFile.testAttribute(ec));
        ec = "EC 1.2.3.134";
        assertTrue(setEcsFromFile.testAttribute(ec));

        ec = "1.2.3.4;1.4.32";
        assertTrue(setEcsFromFile.testAttribute(ec));


    }

    @Test
    public void testAttributeEmpty() {
        String ec = "";

        assertTrue(setEcsFromFile.testAttribute(ec));
    }

    @Test
    public void testAttributeBadlyFormated() {
        String ec = "NA";

        assertFalse(setEcsFromFile.testAttribute(ec));

        ec = "1.2.3.4|1.4.32";
        assertFalse(setEcsFromFile.testAttribute(ec));

        ec = "1.2.3.4;NA";
        assertFalse(setEcsFromFile.testAttribute(ec));

        ec = "1.2.3.4;";
        assertFalse(setEcsFromFile.testAttribute(ec));
    }

    @Test
    public void setAttributes() throws IOException {

        Boolean flag = setEcsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setEcsFromFile.setAttributes();

        assertEquals("1.2.3.5", r.getEcNumber());


    }

    @Test
    public void setAttributeEmpty() throws IOException {
        line = "r\t\ttruc\n";

        Boolean flag = setEcsFromFile.parseLine(line, 1);

        assertTrue(flag);

        setEcsFromFile.setAttributes();

        assertEquals(null, r.getEcNumber());

    }
}