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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;

public class SetChargesFromFileTest {


    private static BioNetwork network;
    private static BioMetabolite m;
    private static BioMetabolite m_cpd_c;


    @BeforeClass
    public static void init() {
        network = new BioNetwork();
        m = new BioMetabolite("m");
        BioMetabolite m_cpd = new BioMetabolite("M_cpd");
        BioMetabolite cpd_c = new BioMetabolite("cpd_c");
        m_cpd_c = new BioMetabolite("M_cpd_c");

        BioCompartment c = new BioCompartment("c");
        network.add(m, m_cpd, cpd_c, c, m_cpd_c);

        network.affectToCompartment(c, cpd_c, m_cpd_c);

    }

    @Test
    public void testAttribute() {
        SetChargesFromFile app = new SetChargesFromFile(1, 2, network, "", "#", 1, false, false);

        assertTrue(app.testAttribute("2"));
        assertTrue(app.testAttribute("2.0"));
        assertFalse(app.testAttribute("NA"));
    }


    @Test
    public void testParseLine() {
        SetChargesFromFile app = new SetChargesFromFile(0, 1, network, "", "#", 0, false, false);
        String line = "m\t2\n";

        Boolean flag = app.parseLine(line, 1);
        assertTrue(flag);

        assertTrue(app.getIdAttributeMap().containsKey("m"));

        line = "something\t2\n";

        flag = app.parseLine(line, 1);
        assertTrue(flag);

        // A line with an absent metabolite is accepted but not registered in the map
        assertFalse(app.getIdAttributeMap().containsKey("something"));

    }

    @Test
    public void testParseLinePrefixMetabolite() {

        SetChargesFromFile app = new SetChargesFromFile(0, 1, network, "", "#", 0, true, false);

        String line = "cpd\t2\n";

        Boolean flag = app.parseLine(line, 1);
        assertTrue(flag);

        assertTrue(app.getIdAttributeMap().containsKey("M_cpd"));

    }

    @Test
    public void testParseLineSuffixMetabolite() {

        SetChargesFromFile app = new SetChargesFromFile(0, 1, network, "", "#", 0, false, true);

        String line = "cpd\t2\n";

        Boolean flag = app.parseLine(line, 1);
        assertTrue(flag);

        assertTrue(app.getIdAttributeMap().containsKey("cpd_c"));

    }

    @Test
    public void testParseLineCompartmentBracketsMetabolite() {

        SetChargesFromFile app = new SetChargesFromFile(0, 1, network, "", "#", 0, false, false);

        String line = "cpd[c]\t2\n";

        Boolean flag = app.parseLine(line, 1);
        assertTrue(flag);

        assertTrue(app.getIdAttributeMap().containsKey("cpd_c"));

        app = new SetChargesFromFile(0, 1, network, "", "#", 0, false, true);

        flag = app.parseLine(line, 1);
        assertTrue(flag);

        assertFalse(app.getIdAttributeMap().containsKey("cpd_c"));
    }


    @Test
    public void testParseLineSuffixPrefix() {

        SetChargesFromFile app = new SetChargesFromFile(0, 1, network, "", "#", 0, true, true);

        String line = "cpd\t2\n";

        Boolean flag = app.parseLine(line, 1);
        assertTrue(flag);

        assertTrue(app.getIdAttributeMap().containsKey("M_cpd_c"));

        flag = app.parseLine(line, 1);
        assertFalse(flag);

    }

    @Test
    public void setAttributes() throws IOException {

        SetChargesFromFile app = Mockito.spy(new SetChargesFromFile(0, 1, network, "", "#", 0, true, true));

        Mockito.doReturn(true).when(app).parseAttributeFile();
        String line = "cpd\t2\n";

        app.parseLine(line, 1);

        Boolean flag = app.setAttributes();

        assertTrue(flag);

        assertEquals(2, m_cpd_c.getCharge(), 0.0);

    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorBadColumnId() throws IOException {
        SetChargesFromFile app = Mockito.spy(new SetChargesFromFile(-1, 1, network, "", "#", 0, true, true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorBadColumnAttribute() throws IOException {
        SetChargesFromFile app = Mockito.spy(new SetChargesFromFile(1, -1, network, "", "#", 0, true, true));
    }
}