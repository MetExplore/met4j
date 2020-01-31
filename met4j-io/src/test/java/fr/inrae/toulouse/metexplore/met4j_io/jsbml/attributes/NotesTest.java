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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NotesTest {

	Notes n;

	@Before
	public void init() {
		n = new Notes();
	}

	@Test
	public void testAddAttributeToNotes() {

		String refBase = "<notes>\n" + "<body xmlns=\"http://www.w3.org/1999/xhtml\">\n";

		n.addAttributeToNotes("ATTRIBUT", "VALUE", false);

		String ref = refBase + "<p>ATTRIBUT: VALUE</p>\n" + "</body>\n" + "</notes>";

		assertEquals(ref, n.getXHTMLasString());

		n.addAttributeToNotes("ATTRIBUT WITH  SEVERAL SPACES ", "VALUE WITH SPACES  ", false);

		ref = refBase + "<p>ATTRIBUT: VALUE</p>\n" + "<p>ATTRIBUT WITH  SEVERAL SPACES: VALUE WITH SPACES</p>\n"
				+ "</body>\n" + "</notes>";

		assertEquals(ref, n.getXHTMLasString());

		n.addAttributeToNotes("ATTRIBUT", "NEW  VALUE ", true);

		ref = refBase + "<p>ATTRIBUT: NEW  VALUE</p>\n" + "<p>ATTRIBUT WITH  SEVERAL SPACES: VALUE WITH SPACES</p>\n"
				+ "</body>\n" + "</notes>";

		assertEquals(ref, n.getXHTMLasString());

		n.addAttributeToNotes("ATTRIBUT<p#&>", "NEW  VALUE<p#&> ", true);

		ref = refBase + "<p>ATTRIBUT: NEW  VALUE</p>\n" + "<p>ATTRIBUT WITH  SEVERAL SPACES: VALUE WITH SPACES</p>\n"
				+ "<p>ATTRIBUT&lt;p#&amp;&gt;: NEW  VALUE&lt;p#&amp;&gt;</p>\n" + "</body>\n" + "</notes>";

		assertEquals(ref, n.getXHTMLasString());

	}

	@Test
	public void testIsEmpty() {

		assertTrue(n.isEmpty());

		n.addAttributeToNotes("", "", true);

		assertTrue(n.isEmpty());

		n.addAttributeToNotes("  ", "   ", true);

		assertTrue(n.isEmpty());

		n.addAttributeToNotes("ATTRIBUT", "NEW  VALUE ", true);

		assertFalse(n.isEmpty());

	}

}
