package fr.inra.toulouse.metexplore.met4j_flux.output;

import fr.inra.toulouse.metexplore.met4j_flux.general.Constraint;
import fr.inra.toulouse.metexplore.met4j_flux.interaction.FFTransition;
import fr.inra.toulouse.metexplore.met4j_flux.interaction.Interaction;
import fr.inra.toulouse.metexplore.met4j_flux.interaction.InteractionNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import org.sbml.jsbml.*;
import org.sbml.jsbml.ext.qual.*;
import org.sbml.jsbml.text.parser.ParseException;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;

public class SBMLQualWriter {

	public static void writeSbmlQual(String path, InteractionNetwork intNet) {

		SBMLDocument document = new SBMLDocument(3, 1);

		Model model = document.createModel();

		QualModelPlugin qualPlugin = new QualModelPlugin(model);

		model.addExtension("qual", qualPlugin);

		Compartment c = model.createCompartment("c");
		c.setConstant(true);

		// Species
		for (String entId : intNet.getInteractionNetworkEntities().keySet()) {
			BioEntity ent = intNet.getInteractionNetworkEntities().get(entId);

			QualitativeSpecies sp = qualPlugin.createQualitativeSpecies(entId);
			sp.setCompartment(c);
			sp.setConstant(false);
			sp.setName(entId);
			sp.setInitialLevel(intNet.getInitialState(ent));
			sp.setMaxLevel(intNet.getInteractionNetworkEntitiesStates()
					.get(ent).length - 1);

			// Adding notes for translation
			if (intNet.getEntityStateConstraintTranslation().containsKey(ent)) {
				for (int state : intNet.getEntityStateConstraintTranslation()
						.get(ent).keySet()) {
					Constraint cons = intNet
							.getEntityStateConstraintTranslation().get(ent)
							.get(state);
					try {
						sp.appendNotes("State " + state + ":[" + cons.getLb()
								+ "," + cons.getUb() + "]");
					} catch (XMLStreamException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		// Transitions
		for (BioEntity ent : intNet.getTargetToInteractions().keySet()) {

			FFTransition fftr = intNet.getTargetToInteractions().get(ent);

			Transition tr = qualPlugin.createTransition("tr_" + ent.getId());
			
			Output o = new Output();
			o.setQualitativeSpecies(ent.getId());
			
			tr.addOutput(o);

			// default
			FunctionTerm defaultTerm = tr.createFunctionTerm();
			defaultTerm.setDefaultTerm(true);
			defaultTerm.setResultLevel((int) fftr.getdefaultInteraction()
					.getConsequence().getValue());

			try {
				defaultTerm.appendNotes("STARTS: "
						+ fftr.getdefaultInteraction().getTimeInfos()[0]);

				defaultTerm.appendNotes("LASTS: "
						+ fftr.getdefaultInteraction().getTimeInfos()[1]);

				defaultTerm.appendNotes("ID: " + "tr_" + ent.getId()
						+ "_default");

			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//

			for (Interaction inter : fftr.getConditionalInteractions()) {

				FunctionTerm term = tr.createFunctionTerm();
				term.setResultLevel((int) inter.getConsequence().getValue());
				term.setMetaId(inter.getName());

				String conditionMathString = inter.getCondition().toFormula();

				try {
					term.setMath(ASTNode.parseFormula(conditionMathString));
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					System.err.println("Error parsing formula : "
							+ conditionMathString);
					e1.printStackTrace();
				}

				try {
					term.appendNotes("STARTS: " + inter.getTimeInfos()[0]);
					term.appendNotes("LASTS: " + inter.getTimeInfos()[1]);
					term.appendNotes("ID: " + inter.getName());

				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		//

		try {
			SBMLWriter.write(document, path, '\t', (short) 1);
		} catch (SBMLException | FileNotFoundException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}