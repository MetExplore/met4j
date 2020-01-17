package fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes;

import static org.junit.Assert.*;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sbml.jsbml.Annotation;
import org.sbml.jsbml.CVTerm;
import org.sbml.jsbml.CVTerm.Qualifier;
import org.sbml.jsbml.CVTerm.Type;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;

public class SbmlAnnotationTest {
	
	SBMLDocument doc;
	Model m;
	Annotation annotation;
	Species s1;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void init() {
		doc = new SBMLDocument(2, 4);

		m = doc.createModel("model1");

		annotation = new Annotation();
		s1 = m.createSpecies("id1");

		CVTerm cvterm = new CVTerm();
		cvterm.addResource("urn.miriam.obo.go#GO%3A1234567");
		cvterm.setQualifierType(Type.BIOLOGICAL_QUALIFIER);
		cvterm.setBiologicalQualifierType(Qualifier.BQB_IS);
		
	    annotation.addCVTerm(cvterm);
	}
	
	@Test
	public void testBioAnnotation() throws XMLStreamException {
		s1.setMetaId("meta4");
		s1.setAnnotation(annotation);

		SbmlAnnotation a = new SbmlAnnotation(s1.getMetaId(), s1.getAnnotationString());
		
		assertEquals(s1.getAnnotationString(), a.getXMLasString());
		
		
	}
	@Test
	public void testBadAnnotation() {
		exception.expect(IllegalArgumentException.class);
		new SbmlAnnotation("truc", "<annotation>/annotation>");
		
	}

}
