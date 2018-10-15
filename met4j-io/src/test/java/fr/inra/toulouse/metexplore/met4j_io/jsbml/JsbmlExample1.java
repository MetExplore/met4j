package fr.inra.toulouse.metexplore.met4j_io.jsbml;

import java.beans.PropertyChangeEvent;

import javax.swing.tree.TreeNode;

import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.Creator;
import org.sbml.jsbml.History;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sbml.jsbml.util.TreeNodeChangeListener;
import org.sbml.jsbml.util.TreeNodeRemovedEvent;

public class JsbmlExample1 implements TreeNodeChangeListener {
	
	public JsbmlExample1() throws Exception {
		
		SBMLDocument doc = new SBMLDocument(3, 1);
		doc.addTreeNodeChangeListener(this);
		// Create a new SBML model, and add a compartment to it.
		Model model = doc.createModel("test_model");
		Compartment compartment = model.createCompartment("default");
		compartment.setSize(1d);
		
		// Create a model history object and add author information to it.
		History hist = model.getHistory(); // Will create the History, if it does not exist
		Creator creator = new Creator("Given Name", "Family Name", "Organisation", "My@EMail.com");
		hist.addCreator(creator);
		// Create some sample content in the SBML model.
		Species specOne = model.createSpecies("test_spec1", compartment);
		Species specTwo = model.createSpecies("test_spec2", compartment);
		Reaction sbReaction = model.createReaction("reaction_id");
		// Add a substrate (SBO:0000015) and product (SBO:0000011) to the reaction.
		SpeciesReference subs = sbReaction.createReactant(specOne);
		subs.setSBOTerm(15);
		SpeciesReference prod = sbReaction.createProduct(specTwo);
		prod.setSBOTerm(11);
		
		
		
	}
	
	

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nodeAdded(TreeNode arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nodeRemoved(TreeNodeRemovedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
