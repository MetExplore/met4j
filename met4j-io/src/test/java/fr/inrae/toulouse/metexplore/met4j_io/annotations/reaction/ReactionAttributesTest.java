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

package fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;

public class ReactionAttributesTest {

    BioReaction r;
    Flux f;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        r = new BioReaction("r");
        f = new Flux("flux");
    }

    @Test
    public void testGetLowerBound() {

        assertNull("If not set, the lower bound must equal to null", ReactionAttributes.getLowerBound(r));

        f.value = 150.0;

        r.setAttribute(ReactionAttributes.LOWER_BOUND, f);
        assertEquals("test get lower bound", 150.0, ((Flux) ReactionAttributes.getLowerBound(r)).value, 0.0);

    }

    @Test
    public void testGetUpperBound() {

        assertNull("If not set, the upper bound must equal to null", ReactionAttributes.getUpperBound(r));

        f.value = 150.0;

        r.setAttribute(ReactionAttributes.UPPER_BOUND, f);
        assertEquals("test get upper bound", 150.0, ((Flux) ReactionAttributes.getUpperBound(r)).value, 0.0);

    }

    @Test
    public void testSetLowerBound() {

        f.value = 150.0;

        ReactionAttributes.setLowerBound(r, f);

        assertEquals("test set lower bound", 150.0, ((Flux) r.getAttribute(ReactionAttributes.LOWER_BOUND)).value, 0.0);

        f.value = -150.0;

        ReactionAttributes.setLowerBound(r, f);

        assertEquals("test set lower bound", -150.0, ((Flux) r.getAttribute(ReactionAttributes.LOWER_BOUND)).value, 0.0);

        f.value = 150.0;

        r.setReversible(false);

        ReactionAttributes.setLowerBound(r, f);
        assertEquals("test set lower bound", 150.0, ((Flux) r.getAttribute(ReactionAttributes.LOWER_BOUND)).value, 0.0);

    }

    @Test
    public void testSetUpperBound() {

        f.value = 150.0;

        ReactionAttributes.setUpperBound(r, f);

        assertEquals("test set upper bound", 150.0, ((Flux) r.getAttribute(ReactionAttributes.UPPER_BOUND)).value, 0.0);

        f.value = -150.0;

        ReactionAttributes.setUpperBound(r, f);

        assertEquals("test set upper bound", -150.0, ((Flux) r.getAttribute(ReactionAttributes.UPPER_BOUND)).value, 0.0);

        r.setReversible(false);

        f.value = 150.0;
        ReactionAttributes.setUpperBound(r, f);
        assertEquals("test set upper bound", 150.0, ((Flux) r.getAttribute(ReactionAttributes.UPPER_BOUND)).value, 0.0);

    }

    @Test
    public void testSetNotes() {

        Notes notes = new Notes("<p>toto</p>");

        ReactionAttributes.setNotes(r, notes);

        assertEquals(notes, r.getAttribute(GenericAttributes.SBML_NOTES));

    }

    @Test
    public void testGetNotes() {

        Notes notes = new Notes("<p>toto</p>");

        r.setAttribute(GenericAttributes.SBML_NOTES, notes);

        assertEquals(notes, ReactionAttributes.getNotes(r));

    }

    @Test
    public void testSetScore() {

        Double score = 5.0;

        ReactionAttributes.setScore(r, score);

        assertEquals(score, r.getAttribute(ReactionAttributes.SCORE));

    }

    @Test
    public void testGetScore() {

        Double score = 5.0;

        r.setAttribute(ReactionAttributes.SCORE, score);

        assertEquals(score, ReactionAttributes.getScore(r));

    }

    @Test
    public void testSetStatus() {

        String status = "Good";

        ReactionAttributes.setStatus(r, status);

        assertEquals(status, r.getAttribute(ReactionAttributes.STATUS));

    }

    @Test
    public void testGetStatus() {

        String status = "Good";

        r.setAttribute(ReactionAttributes.STATUS, status);

        assertEquals(status, ReactionAttributes.getStatus(r));

    }

    @Test
    public void testSetPmids() {

        Set<Integer> pmids = new HashSet<Integer>();

        pmids.add(1235);
        pmids.add(111);

        ReactionAttributes.setPmids(r, pmids);

        assertEquals(pmids, r.getAttribute(GenericAttributes.PMIDS));

    }

    @Test
    public void testGetPmids() {

        Set<Integer> pmids = new HashSet<Integer>();

        pmids.add(1235);
        pmids.add(111);

        r.setAttribute(GenericAttributes.PMIDS, pmids);

        assertEquals(pmids, ReactionAttributes.getPmids(r));

    }

    @Test
    public void testAddPmid() {

        Set<Integer> pmids = new HashSet<Integer>();

        pmids.add(1235);
        pmids.add(111);

        ReactionAttributes.addPmid(r, 1235);
        ReactionAttributes.addPmid(r, 111);


        assertEquals(pmids, ReactionAttributes.getPmids(r));

    }

    @Test
    public void testSetComment() {

        String comment = "comment";

        ReactionAttributes.setComment(r, comment);

        assertEquals(comment, r.getAttribute(GenericAttributes.COMMENT));

    }

    @Test
    public void testGetComment() {

        String comment = "comment";

        r.setAttribute(GenericAttributes.COMMENT, comment);

        assertEquals(comment, ReactionAttributes.getComment(r));

    }

    @Test
    public void testSetSboTerm() {

        String sbo = "SBO:1234567";
        ReactionAttributes.setSboTerm(r, sbo);

        assertEquals((String) r.getAttribute(GenericAttributes.SBO_TERM), sbo);
    }

    @Test
    public void testGetSboTerm() {

        String sbo = "sbo";

        r.setAttribute(GenericAttributes.SBO_TERM, sbo);

        assertEquals(sbo, ReactionAttributes.getSboTerm(r));

    }

    @Test
    public void testSetFast() {

        Boolean flag = true;
        ReactionAttributes.setFast(r, flag);

        assertEquals((boolean) r.getAttribute(ReactionAttributes.FAST), true);

        flag = false;
        ReactionAttributes.setFast(r, flag);
        assertEquals((boolean) r.getAttribute(ReactionAttributes.FAST), false);

    }

    @Test
    public void testGetFast() {

        assertFalse("By default fast must be false", ReactionAttributes.getFast(r));

        Boolean flag = true;

        r.setAttribute(ReactionAttributes.FAST, flag);

        assertEquals(flag, ReactionAttributes.getFast(r));

    }

    @Test
    public void testSetKineticFormula() {

        String f = "f";
        ReactionAttributes.setKineticFormula(r, f);

        assertEquals((String) r.getAttribute(ReactionAttributes.KINETIC_FORMULA), f);
    }

    @Test
    public void testGetKineticFormula() {

        String f = "f";

        r.setAttribute(ReactionAttributes.KINETIC_FORMULA, f);

        assertEquals(f, ReactionAttributes.getKineticFormula(r));

    }

    @Test
    public void testGetFluxParams() {

        Flux f = new Flux("f");
        Flux f2 = new Flux("f2");

        FluxCollection c = new FluxCollection();
        c.add(f);
        c.add(f2);

        r.setAttribute(ReactionAttributes.FLUX_PARAMS, c);

        assertEquals(c, ReactionAttributes.getFluxParams(r));

    }

    @Test
    public void testSetFluxParams() {

        Flux f = new Flux("f");
        Flux f2 = new Flux("f2");

        FluxCollection c = new FluxCollection();
        c.add(f);
        c.add(f2);

        ReactionAttributes.setFluxParams(r, c);

        assertEquals((FluxCollection) r.getAttribute(ReactionAttributes.FLUX_PARAMS), c);

    }

    @Test
    public void testGetAdditionalFluxParams() {

        Flux f = new Flux("f");
        Flux f2 = new Flux("f2");

        FluxCollection c = new FluxCollection();
        c.add(f);
        c.add(f2);

        r.setAttribute(ReactionAttributes.ADDITIONAL_FLUX_PARAMS, c);

        assertEquals(c, ReactionAttributes.getAdditionalFluxParams(r));

    }

    @Test
    public void testSetAdditionalFluxParams() {

        Flux f = new Flux("f");
        Flux f2 = new Flux("f2");

        FluxCollection c = new FluxCollection();
        c.add(f);
        c.add(f2);

        ReactionAttributes.setAdditionalFluxParams(r, c);

        assertEquals((FluxCollection) r.getAttribute(ReactionAttributes.ADDITIONAL_FLUX_PARAMS), c);

    }

    @Test
    public void testAddFlux() {

        Flux f = new Flux("f");
        Flux f2 = new Flux("f2");

        ReactionAttributes.addFlux(r, f);
        ReactionAttributes.addFlux(r, f2);

        assertEquals(((FluxCollection) r.getAttribute(ReactionAttributes.FLUX_PARAMS)).get("f"), f);

        assertEquals(((FluxCollection) r.getAttribute(ReactionAttributes.FLUX_PARAMS)).get("f2"), f2);

    }

    @Test
    public void testGetFlux() {

        Flux f = new Flux("f");
        Flux f2 = new Flux("f2");

        ReactionAttributes.addFlux(r, f);
        ReactionAttributes.addFlux(r, f2);

        Flux test = ReactionAttributes.getFlux(r, "f");

        assertEquals(f, test);

        Flux test2 = ReactionAttributes.getFlux(r, "f2");

        assertEquals(f2, test2);

        assertNull(ReactionAttributes.getFlux(r, "nonExistant"));

    }

    @Test
    public void testGetFluxWhenNoFluxParams() {
        assertNull(ReactionAttributes.getFlux(r, "f"));
    }

    @Test
    public void testSetSpontaneous() {

        Boolean flag = true;
        ReactionAttributes.setSpontaneous(r, flag);

        assertEquals((boolean) r.getAttribute(ReactionAttributes.SPONTANEOUS), true);

        flag = false;
        ReactionAttributes.setSpontaneous(r, flag);
        assertEquals((boolean) r.getAttribute(ReactionAttributes.SPONTANEOUS), false);

    }

    @Test
    public void testGetSpontaneous() {

        assertFalse("By default fast must be false", ReactionAttributes.getSpontaneous(r));

        Boolean flag = true;

        r.setAttribute(ReactionAttributes.SPONTANEOUS, flag);

        assertEquals(flag, ReactionAttributes.getSpontaneous(r));

    }

    /**
     * Creates a mini network with the reaction r : a -> b
     * @return a {@link BioNetwork}
     */
    private BioNetwork miniNetwork() {
        BioNetwork network = new BioNetwork();
        BioMetabolite a = new BioMetabolite("a");
        BioMetabolite b = new BioMetabolite("b");
        BioCompartment cpt = new BioCompartment("cpt");
        network.add(a, b, cpt, r);
        network.affectToCompartment(cpt, a, b);
        network.affectLeft(r, 1.0, cpt, a);
        network.affectRight(r, 1.0, cpt, b);
        return network;
    }

    @Test
    public void testSetSideCompounds() {

        BioNetwork network = miniNetwork();
        Set<String> ids = new HashSet<>();
        ids.add("a");
        ids.add("b");

        ReactionAttributes.setSideCompounds(r, ids);

        BioCollection<BioMetabolite> refs = new BioCollection<>();
        refs.add(network.getMetabolite("a"), network.getMetabolite("b"));

        BioCollection<BioMetabolite> tests = (BioCollection<BioMetabolite>) r.getAttribute(ReactionAttributes.SIDE_COMPOUNDS);

        assertEquals (refs,tests);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetSideCompoundsWithAbsentMetabolite() {

        miniNetwork();
        Set<String> ids = new HashSet<>();
        ids.add("a");
        ids.add("b");
        ids.add("c");

        ReactionAttributes.setSideCompounds(r, ids);
    }

    @Test
    public void getSideCompounds() {

        BioNetwork network = miniNetwork();
        BioCollection<BioMetabolite> sideCompounds = new BioCollection<>();
        sideCompounds.add(network.getMetabolite("a"), network.getMetabolite("b"));
        r.setAttribute(ReactionAttributes.SIDE_COMPOUNDS, sideCompounds);

        assertEquals(sideCompounds, ReactionAttributes.getSideCompounds(r));

    }

    @Test
    public void getHole() {

        Boolean hole = true;

        BioReaction r = new BioReaction("r");

        assertFalse(ReactionAttributes.getHole(r));

        r.setAttribute(ReactionAttributes.HOLE, hole);

        assertTrue(ReactionAttributes.getHole(r));

    }

    @Test
    public void setHole() {

        Boolean hole = true;

        BioReaction r = new BioReaction("r");

        ReactionAttributes.setHole(r, hole);

        assertTrue((Boolean) r.getAttribute(ReactionAttributes.HOLE));

    }
}
