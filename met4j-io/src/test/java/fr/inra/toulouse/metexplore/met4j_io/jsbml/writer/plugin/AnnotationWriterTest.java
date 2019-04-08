package fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin;


import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;

public class AnnotationWriterTest {

	public SBMLDocument doc;
	Model model;
	BioNetwork network;
	BioReaction r1, r2;
	Reaction rSbml1, rSbml2;	
	
	BioMetabolite m1, m2;
	BioCompartment c1, c2;
	
	Species s1, s3;
	Compartment compartSbml1, compartSbml2;
	
	@Before
	public void init() {
		
		doc = new SBMLDocument(3, 1);
		model = doc.createModel();
		network = new BioNetwork();
		
		r1 = new BioReaction("r1");
		
		BioRef refR1 = new BioRef("origin1", "dbName1", "id1", 1);
		r1.addRef(refR1);
				
		r2 = new BioReaction("r2");

		network.add(r1);
		network.add(r2);
		
		m1 = new BioMetabolite("m1");
		
		BioRef refR2 = new BioRef("origin2", "dbName2", "id2", 2);
		m1.addRef(refR2);
		
		m2 = new BioMetabolite("m2");
		
		network.add(m1);
		network.add(m2);
		
		c1 = new BioCompartment("c1");
		BioRef refR3 = new BioRef("origin3", "dbName3", "id3", 3);
		c1.addRef(refR3);
		
		c2 = new BioCompartment("c2");
		
		network.add(c1);
		network.add(c2);
		
		network.affectToCompartment(m1, c1);
		network.affectToCompartment(m2, c2);
		
	}
	
	
	
	@Test
	public void testModelAnnotation() {

		
	
	}

}
