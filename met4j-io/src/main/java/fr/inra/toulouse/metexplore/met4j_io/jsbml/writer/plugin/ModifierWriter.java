package parsebionet.io.jsbml.writer.plugin;

import java.util.Map.Entry;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.io.jsbml.dataTags.PrimaryDataTag;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML2Compatible;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML3Compatible;
import parsebionet.utils.StringUtils;

/**
 * @deprecated Modifier are not used anymore in MetExplore. Use
 *             {@link FBCWriter} to incorporate Gene/protein data to your model
 * @author Benjamin
 * @see FBCWriter
 * @since 3.0
 */
@Deprecated
public class ModifierWriter implements PackageWriter, WriterSBML2Compatible,
		WriterSBML3Compatible, PrimaryDataTag {

	/**
	 * The SBML model
	 */
	public Model model;
	/**
	 * the {@link BioNetwork}
	 */
	public BioNetwork net;

	@Override
	public String getAssociatedPackageName() {
		return "modifier";
	}

	@Override
	public boolean isPackageUseableOnLvl(int lvl) {
		if (lvl < 2)
			return false;
		return true;
	}

	@Override
	public void parseBionetwork(Model model, BioNetwork bionetwork) {
		System.err.println("Generating Model's modifiers...");

		this.setModel(model);
		this.setNet(bionetwork);

		this.createReactionsModifiers();

	}

	/**
	 * Loops through the BioNetwork's reactions to construct the modifiers from
	 * theirs list of enzyme.
	 */
	private void createReactionsModifiers() {
		for (BioChemicalReaction bioRxn : this.getNet()
				.getBiochemicalReactionList().values()) {
			Reaction reaction = model.getReaction(StringUtils
					.convertToSID(bioRxn.getId()));

			for (BioPhysicalEntity bioMod : bioRxn.getEnzList().values()) {

				if (model.getSpecies(StringUtils.convertToSID(bioMod.getId())) == null) {
					Species mod = model.createSpecies(bioMod.getId());
					mod.setCompartment(StringUtils.convertToSID(bioMod
							.getCompartment().getId()));

					mod.setName(bioMod.getName());
					mod.setBoundaryCondition(bioMod.getBoundaryCondition());

					mod.setConstant(bioMod.getConstant());

					if (!StringUtils.isVoid(bioMod.getSboterm())) {
						mod.setSBOTerm(bioMod.getSboterm());
					} else {

						switch (bioMod.getClass().getSimpleName()) {
						case "BioProtein":
							mod.setSBOTerm("SBO:0000252");
							break;
						case "BioComplex":
							mod.setSBOTerm("SBO:0000297");
							break;
						}

					}

					mod.setHasOnlySubstanceUnits(bioMod
							.getHasOnlySubstanceUnit());

					if (bioMod.getInitialQuantity().size() == 1) {
						for (Entry<String, Double> quantity : bioMod
								.getInitialQuantity().entrySet()) {
							if (quantity.getKey().equals("amount")) {
								mod.setInitialAmount(quantity.getValue());
							} else if (quantity.getKey()
									.equals("concentration")) {
								mod.setInitialConcentration(quantity.getValue());
							}
						}
					}

					reaction.createModifier(mod);
				}
			}
		}
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the net
	 */
	public BioNetwork getNet() {
		return net;
	}

	/**
	 * @param net the net to set
	 */
	public void setNet(BioNetwork net) {
		this.net = net;
	}

	

}
