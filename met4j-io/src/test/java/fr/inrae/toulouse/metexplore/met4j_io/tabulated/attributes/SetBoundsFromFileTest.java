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
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;

public class SetBoundsFromFileTest {

    static BioNetwork network;
    static BioReaction r1;

    @BeforeClass
    public static void init() {

        network = new BioNetwork();

        r1 = new BioReaction("r1");
        network.add(r1);


    }

    @Test
    public void testAttribute() {

        SetBoundsFromFile c = new SetBoundsFromFile(1, 2, network, "", "#", 0, false, BoundsType.LOWER);
        assertTrue(c.testAttribute("12.0"));
        assertFalse(c.testAttribute("NA"));
    }

    @Test
    public void testSkipLine() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);
        String line = "r1\t12.0\n";

        Boolean flag = c.parseLine(line, 1);

        assertTrue(flag);

        assertEquals(0, c.getIdAttributeMap().size());
    }

    @Test
    public void testCorrectLine() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);

        String line = "r1\t12.0\n";

        Boolean flag = c.parseLine(line, 2);

        assertTrue(flag);

        assertEquals(1, c.getIdAttributeMap().size());

        assertTrue(c.getIdAttributeMap().containsKey("r1"));

        assertEquals("12.0", c.getIdAttributeMap().get("r1"));

    }

    @Test
    public void testCommentLine() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);

        String line = "#r1\t12.0\n";

        Boolean flag = c.parseLine(line, 2);

        assertTrue(flag);
    }

    @Test
    public void testLineWithBadNumberOfColumns() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);

        String line = "r1\n";

        Boolean flag = c.parseLine(line, 2);

        assertFalse(flag);
    }

    @Test
    public void testLineWithBadAttributeFormat() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);

        String line = "r1\t1NA\n";

        Boolean flag = c.parseLine(line, 2);

        assertFalse(flag);
    }

    @Test
    public void testLineAddPrefix() {

        BioReaction r1Bis = new BioReaction("R_r1");
        network.add(r1Bis);

        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, true, BoundsType.LOWER);

        String line = "r1\t12.0\n";

        Boolean flag = c.parseLine(line, 2);

        assertTrue(flag);

        assertTrue(c.getIdAttributeMap().containsKey("R_r1"));

        assertEquals("12.0", c.getIdAttributeMap().get("R_r1"));
    }

    @Test
    public void testEmptyLine() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, true, BoundsType.LOWER);
        String line = "\n";

        Boolean flag = c.parseLine(line, 2);

        assertTrue(flag);
    }

    @Test
    public void testLineWithAbsentId() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);

        String line = "r2\t12.0\n";

        // Returns only a Warning
        Boolean flag = c.parseLine(line, 2);

        assertTrue(flag);

        assertEquals(0, c.getIdAttributeMap().size());

    }

    @Test
    public void testLineWithEmptytId() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);

        String line = "\t12.0\n";

        Boolean flag = c.parseLine(line, 2);

        assertFalse(flag);
    }

    @Test
    public void testLineWithDuplicatedId() {
        SetBoundsFromFile c = new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER);

        String line = "r1\t12.0\n";
        c.parseLine(line, 2);
        Boolean flag = c.parseLine(line, 3);

        assertFalse(flag);
    }


    @Test
    public void setLowerBounds() throws IOException {
        SetBoundsFromFile c = Mockito.spy(new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.LOWER));

        Mockito.doReturn(true).when(c).test();

        String line = "r1\t12.0\n";

        c.parseLine(line, 2);

        c.setAttributes();

        Flux lowerBound = ReactionAttributes.getLowerBound(r1);

        assertNotNull(lowerBound);

        assertNotNull(lowerBound.value);

        assertEquals(12.0, lowerBound.value, 0.0);

    }

    @Test
    public void setUpperBounds() throws IOException {
        SetBoundsFromFile c = Mockito.spy(new SetBoundsFromFile(0, 1, network, "", "#", 1, false, BoundsType.UPPER));

        Mockito.doReturn(true).when(c).test();

        String line = "r1\t12.0\n";

        c.parseLine(line, 2);

        c.setAttributes();

        Flux upperBound = ReactionAttributes.getUpperBound(r1);

        assertNotNull(upperBound);

        assertNotNull(upperBound.value);

        assertEquals(12.0, upperBound.value, 0.0);

    }
}