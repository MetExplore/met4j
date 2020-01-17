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
