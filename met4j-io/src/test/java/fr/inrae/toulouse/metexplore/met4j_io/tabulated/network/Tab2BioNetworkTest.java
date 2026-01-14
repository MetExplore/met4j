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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.network;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class Tab2BioNetworkTest {

    private Tab2BioNetwork app;
    private BioCompartment c;
    private Map<String,String> input;

    @Before
    public void init() {
        c = new BioCompartment("c", "cytosol");
        app = new Tab2BioNetwork("test", 0, 1,
                "->", "<->",
                c, 0);
        app.setParsingFailure(Tab2BioNetwork.errorHandling.SKIP);
    }


    @Test
    public void testLine() {

        String line = "r\tm1 + 2 m2 -> m3 + m4";

        assertTrue(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void testBadFormulaLeft() {
        String line = "r\tm1 + 2 a m2 -> m3 + m4\n";

        assertFalse(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void testBadFormulaRight() {
        String line = "r\tm1 + 2 m2 -> m3  m4\n";

        assertFalse(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void testEmptyRight() {
        // Must accept empty right (exchange reaction)
        String line = "r\tm1 + 2 m2 ->\n";

        assertTrue(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void testEmptyLeft() {
        // Must accept empty right (exchange reaction)
        String line = "r\t-> m1 + 2 m2\n";

        assertTrue(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void testEmptyLeftAndRight() {
        // Must have at least one reactant
        String line = "r\t-> \n";

        assertFalse(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void testEmptyFormula() {
        String line = "r\t\ttruc\n";

        assertFalse(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void testFormulaWithoutArrow() {
        String line = "r\ttruc + machin\n";

        assertFalse(app.checkFormula(line.split("\t")[1]));
    }

    @Test
    public void parseLine() {
        input = new HashMap<>();
        input.put("r","m1 + 2 m2 -> m3 + 3 m4");

        BioNetwork network = app.convert(input);

        assertEquals(1, network.getReactionsView().size());
        assertEquals(4, network.getMetabolitesView().size());
        assertEquals(1, network.getCompartmentsView().size());
        assertTrue(network.containsReaction("r"));

        BioReaction reaction = network.getReaction("r");

        assertEquals(2, reaction.getLeftReactantsView().size());
        assertEquals(2, reaction.getRightReactantsView().size());

        BioReactant l1 = reaction.getLeftReactantsView().stream()
                .filter(reactant -> reactant.getMetabolite().getId().equals("m2"))
                .findFirst().get();

        assertEquals(2, l1.getQuantity(), 0.0);

        BioReactant r1 = reaction.getRightReactantsView().stream()
                .filter(reactant -> reactant.getMetabolite().getId().equals("m4"))
                .findFirst().get();

        assertEquals(3, r1.getQuantity(), 0.0);

        assertFalse(reaction.isReversible());

    }

    @Test
    public void parseLineReversible() {
        input = new HashMap<>();
        input.put("r","m1 + 2 m2 <-> m3 + 3 m4");

        BioNetwork network = app.convert(input);
        BioReaction reaction = network.getReaction("r");
        assertTrue(reaction.isReversible());
    }
    @Test
    public void parseLineIdEmpty() {
        input = new HashMap<>();
        input.put(" ","m1 + 2 m2 -> m3 + m4");

        BioNetwork network = app.convert(input);
        assertEquals(0, network.getReactionsView().size());
        assertEquals(0, network.getMetabolitesView().size());
    }

    @Test
    public void parseLineEmptySides() {
        input = new HashMap<>();
        input.put("r"," <-> ");

        BioNetwork network = app.convert(input);
        assertEquals(0, network.getReactionsView().size());
        assertEquals(0, network.getMetabolitesView().size());
    }

    @Test
    public void parseLineIdEmptyFail() {
        app.setParsingFailure(Tab2BioNetwork.errorHandling.THROWERROR);
        input = new HashMap<>();
        input.put(" ","m1 + 2 m2 -> m3 + m4");

        try {
            app.convert(input);
            fail("Expected an exception to be thrown");
        } catch (IllegalArgumentException ignored) {
        }
        app.setParsingFailure(Tab2BioNetwork.errorHandling.SKIP);
    }

    @Test
    public void parseLineEmptySidesFail() {
        app.setParsingFailure(Tab2BioNetwork.errorHandling.THROWERROR);
        input = new HashMap<>();
        input.put("r"," <-> ");

        try {
            app.convert(input);
            fail("Expected an exception to be thrown");
        } catch (IllegalArgumentException ignored) {
        }
        app.setParsingFailure(Tab2BioNetwork.errorHandling.SKIP);
    }

    @Test
    public void parseLineEmptyLeftSide() {
        input = new HashMap<>();
        input.put("r"," <-> s2");

        BioNetwork network = app.convert(input);
        assertEquals(1, network.getReactionsView().size());
        assertEquals(1, network.getMetabolitesView().size());
    }

    @Test
    public void parseLineEmptyRightSide() {
        input = new HashMap<>();
        input.put("r","m1 <->");

        BioNetwork network = app.convert(input);
        assertEquals(1, network.getReactionsView().size());
        assertEquals(1, network.getMetabolitesView().size());
    }

    @Test
    public void parseLineWithCompartments() {
        input = new HashMap<>();
        input.put("r","m1[c] + 2 m2[p] -> m3[x] + 3 m4[r]");

        app = new Tab2BioNetwork("test", 0, 1,
                "->", "<->",
                c, 0);

        BioNetwork network = app.convert(input);

        assertEquals(1, network.getReactionsView().size());
        assertEquals(4, network.getMetabolitesView().size());
        assertEquals(4, network.getCompartmentsView().size());
        assertTrue(network.containsCompartment("p"));
        assertTrue(network.containsCompartment("c"));
        assertTrue(network.containsCompartment("r"));
        assertTrue(network.containsCompartment("x"));

    }

    @Test
    public void parseSeveralLines() {

        input = new HashMap<>();
        input.put("r","m1 + 2 m2 <-> m3 + 3 m4");
        input.put("r2","m1 + 2 m2 <-> m3 + 3 m5");

        BioNetwork network =  app.convert(input);
        assertEquals(2, network.getReactionsView().size());
        assertEquals(5, network.getMetabolitesView().size());

    }
}