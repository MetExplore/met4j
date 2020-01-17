package fr.inra.toulouse.metexplore.met4j_io.utils;

import org.junit.Test;

import static fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils.isValidSboTerm;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {


    @Test
    public void isValidSboTermTest() {
        assertFalse(isValidSboTerm("SBO:1"));
        assertFalse(isValidSboTerm("SBO:1234A67"));
        assertFalse(isValidSboTerm("SB:1234567"));
        assertTrue(isValidSboTerm("SBO:1234567"));
        assertTrue(isValidSboTerm("SBO:0000247"));
        assertTrue(isValidSboTerm("SBO: 0000247"));
        assertTrue(isValidSboTerm("SBO :0000247"));
        assertTrue(isValidSboTerm("SBO : 0000247"));
        assertTrue(isValidSboTerm("0000247"));
        assertTrue(isValidSboTerm("sbo : 0000247"));
    }
}