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

package fr.inrae.toulouse.metexplore.met4j_io.annotations;


import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.SbmlAnnotation;
import org.apache.xalan.trace.GenerateEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;


public class GenericAttributesTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetConstant() {
        exception.expect(IllegalArgumentException.class);

        GenericAttributes.getConstant(new BioProtein("p"));

    }

    @Test
    public void testSetConstant() {
        exception.expect(IllegalArgumentException.class);

        GenericAttributes.setConstant(new BioProtein("p"), true);
    }

    @Test
    public void getNotes() {

        BioMetabolite m = new BioMetabolite("m");

        Notes refNotes = new Notes();

        m.setAttribute(GenericAttributes.SBML_NOTES, refNotes);

        assertNotNull(GenericAttributes.getNotes(m));

        assertEquals(refNotes, GenericAttributes.getNotes(m));

    }

    @Test
    public void setNotes() {
        BioMetabolite m = new BioMetabolite("m");

        Notes refNotes = new Notes();

        GenericAttributes.setNotes(m, refNotes);

        assertNotNull(m.getAttribute(GenericAttributes.SBML_NOTES));

        assertEquals(refNotes, m.getAttribute(GenericAttributes.SBML_NOTES));

    }

    @Test
    public void setPmids() {

        Set<Integer> pmids = new HashSet<>();
        pmids.add(10001);

        BioReaction r = new BioReaction("r");

        GenericAttributes.setPmids(r, pmids);

        assertNotNull(r.getAttribute(GenericAttributes.PMIDS));

        assertEquals(pmids, r.getAttribute(GenericAttributes.PMIDS));

    }

    @Test
    public void addPmid() {

        Set<Integer> pmids = new HashSet<>();
        pmids.add(10001);

        BioReaction r = new BioReaction("r");

        GenericAttributes.setPmids(r, pmids);

        GenericAttributes.addPmid(r, 10003);

        assertNotNull(GenericAttributes.getPmids(r));

        assertEquals(2, GenericAttributes.getPmids(r).size());

        assertTrue(GenericAttributes.getPmids(r).contains(10001));
        assertTrue(GenericAttributes.getPmids(r).contains(10003));

        BioReaction r2 = new BioReaction("r2");
        GenericAttributes.addPmid(r2, 10003);

        assertNotNull(GenericAttributes.getPmids(r2));

        assertEquals(1, GenericAttributes.getPmids(r2).size());

    }

    @Test
    public void getPmids() {

        Set<Integer> pmids = new HashSet<>();
        pmids.add(10001);

        BioReaction r = new BioReaction("r");

        r.setAttribute(GenericAttributes.PMIDS, pmids);

        assertNotNull(GenericAttributes.getPmids(r));

        assertEquals(pmids, GenericAttributes.getPmids(r));

    }


    @Test
    public void getComment() {

        String comment = "This is a comment";
        BioMetabolite m = new BioMetabolite("m");

        m.setAttribute(GenericAttributes.COMMENT, comment);

        assertNotNull(GenericAttributes.getComment(m));
        assertEquals(comment, GenericAttributes.getComment(m));
    }

    @Test
    public void setComment() {

        String comment = "This is a comment";
        BioMetabolite m = new BioMetabolite("m");

        GenericAttributes.setComment(m, comment);

        assertNotNull(m.getAttribute(GenericAttributes.COMMENT));
        assertEquals(comment, m.getAttribute(GenericAttributes.COMMENT));

    }

    @Test
    public void setSboTerm() {

        String sboTerm = "SBO:1234567";

        BioMetabolite m = new BioMetabolite("m");

        GenericAttributes.setSboTerm(m, sboTerm);

        assertNotNull(m.getAttribute(GenericAttributes.SBO_TERM));

        assertEquals(sboTerm, m.getAttribute(GenericAttributes.SBO_TERM));

        sboTerm = "invalid";

        GenericAttributes.setSboTerm(m, sboTerm);

        assertNull(m.getAttribute(GenericAttributes.SBO_TERM));

    }

    @Test
    public void getSboTerm() {

        String sboTerm = "SBO:1234567";

        BioMetabolite m = new BioMetabolite("m");

        m.setAttribute(GenericAttributes.SBO_TERM, sboTerm);

        assertNotNull(GenericAttributes.getSboTerm(m));

        assertEquals(sboTerm, GenericAttributes.getSboTerm(m));

    }

    @Test
    public void getGeneric() {

        Boolean generic = true;
        BioMetabolite m = new BioMetabolite("m");

        assertEquals(false, GenericAttributes.getGeneric(m));

        m.setAttribute(GenericAttributes.GENERIC, generic);

        assertNotNull(GenericAttributes.getGeneric(m));
        assertEquals(generic, GenericAttributes.getGeneric(m));
    }

    @Test
    public void setGeneric() {
        Boolean generic = true;
        BioMetabolite m = new BioMetabolite("m");

        GenericAttributes.setGeneric(m, generic);

        assertNotNull(GenericAttributes.getGeneric(m));
        assertEquals(generic, GenericAttributes.getGeneric(m));

    }

    @Test
    public void getType() {

        String type = "amino acid";

        BioMetabolite m = new BioMetabolite("m");

        m.setAttribute(GenericAttributes.TYPE, type);

        assertNotNull(GenericAttributes.getType(m));
        assertEquals(type, GenericAttributes.getType(m));

    }

    @Test
    public void setType() {

        String type = "amino acid";

        BioMetabolite m = new BioMetabolite("m");

        GenericAttributes.setType(m, type);

        assertNotNull(m.getAttribute(GenericAttributes.TYPE));
        assertEquals(type, m.getAttribute(GenericAttributes.TYPE));
    }

    @Test
    public void setAnnotatorComments() {

        AnnotatorComment comment = new AnnotatorComment("comment", "me");
        AnnotatorComment otherComment = new AnnotatorComment("comment2", "you");

        Set<AnnotatorComment> comments = new HashSet<>();
        comments.add(comment);
        comments.add(otherComment);

        BioMetabolite m = new BioMetabolite("m");
        GenericAttributes.setAnnotatorComments(m, comments);

        assertNotNull(m.getAttribute(GenericAttributes.ANNOTATOR_COMMENTS));
        assertEquals(comments, m.getAttribute(GenericAttributes.ANNOTATOR_COMMENTS));

    }



    @Test
    public void addAnnotatorComment() {

        AnnotatorComment comment = new AnnotatorComment("comment", "me");
        BioMetabolite m = new BioMetabolite("m");

        GenericAttributes.addAnnotatorComment(m, comment);

        assertNotNull(m.getAttribute(GenericAttributes.ANNOTATOR_COMMENTS));

        assertEquals(1, GenericAttributes.getAnnotatorComments(m).size());

        AnnotatorComment comment2 = new AnnotatorComment("comment2", "me");

        GenericAttributes.addAnnotatorComment(m, comment2);

        assertEquals(2, GenericAttributes.getAnnotatorComments(m).size());


    }

    @Test
    public void getAnnotatorComments() {

        AnnotatorComment comment = new AnnotatorComment("comment", "me");
        AnnotatorComment otherComment = new AnnotatorComment("comment2", "you");

        Set<AnnotatorComment> comments = new HashSet<>();
        comments.add(comment);
        comments.add(otherComment);

        BioMetabolite m = new BioMetabolite("m");

        m.setAttribute(GenericAttributes.ANNOTATOR_COMMENTS, comments);

        assertNotNull(GenericAttributes.getAnnotatorComments(m));
        assertEquals(comments, GenericAttributes.getAnnotatorComments(m));

    }
}
