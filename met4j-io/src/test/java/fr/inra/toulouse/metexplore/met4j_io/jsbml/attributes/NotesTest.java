package fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes;

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

		ref = refBase + "<p>ATTRIBUT: VALUE</p>\n" + "<p>ATTRIBUT_WITH_SEVERAL_SPACES: VALUE_WITH_SPACES</p>\n"
				+ "</body>\n" + "</notes>";

		assertEquals(ref, n.getXHTMLasString());

		n.addAttributeToNotes("ATTRIBUT", "NEW  VALUE ", true);

		ref = refBase + "<p>ATTRIBUT: NEW_VALUE</p>\n" + "<p>ATTRIBUT_WITH_SEVERAL_SPACES: VALUE_WITH_SPACES</p>\n"
				+ "</body>\n" + "</notes>";

		assertEquals(ref, n.getXHTMLasString());

		n.addAttributeToNotes("ATTRIBUT<p#&>", "NEW  VALUE<p#&> ", true);

		ref = refBase + "<p>ATTRIBUT: NEW_VALUE</p>\n" + "<p>ATTRIBUT_WITH_SEVERAL_SPACES: VALUE_WITH_SPACES</p>\n"
				+ "<p>ATTRIBUT&lt;p#&amp;&gt;: NEW_VALUE&lt;p#&amp;&gt;</p>\n" + "</body>\n" + "</notes>";

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
