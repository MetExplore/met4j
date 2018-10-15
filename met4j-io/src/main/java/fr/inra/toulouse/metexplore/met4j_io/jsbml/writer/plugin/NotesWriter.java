package parsebionet.io.jsbml.writer.plugin;

import java.util.HashMap;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioCompartment;
import parsebionet.biodata.BioComplex;
import parsebionet.biodata.BioEntity;
import parsebionet.biodata.BioGene;
import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPathway;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioProtein;
import parsebionet.biodata.BioRef;
import parsebionet.biodata.Notes;
import parsebionet.io.jsbml.dataTags.AdditionalDataTag;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML2Compatible;
import parsebionet.io.jsbml.writer.plugin.tags.WriterSBML3Compatible;
import parsebionet.utils.StringUtils;

/**
 * Creates Notes for the SBML entities of the model created by
 * {@link parsebionet.io.jsbml.writer.BionetworkToJsbml}
 * 
 * @author Benjamin
 * @since 3.0
 */
public class NotesWriter implements PackageWriter, WriterSBML2Compatible, WriterSBML3Compatible, AdditionalDataTag {

	/**
	 * The SBML model
	 */
	public Model model;
	/**
	 * the {@link BioNetwork}
	 */
	public BioNetwork bionetwork;

	/**
	 * Set this to true to allow the plugin to update the saved Notes
	 */
	public boolean updateValue = true;

	/**
	 * Constructor
	 * 
	 * @param doUpdates
	 *            Set this to true to allow the plugin to update the saved Notes
	 */
	public NotesWriter(boolean doUpdates) {
		this.updateValue = doUpdates;
	}

	@Override
	public String getAssociatedPackageName() {
		return "note";
	}

	@Override
	public boolean isPackageUseableOnLvl(int lvl) {
		return true;
	}

	/**
	 * Create the model's Notes then launch methods on the different HashMaps
	 * present in the {@link BioNetwork} to create the Notes of the SBML
	 * elements
	 */
	@Override
	public void parseBionetwork(Model model, BioNetwork bionetwork) {
		System.err.println("Generating Model Notes...");
		this.setBionetwork(bionetwork);
		this.setModel(model);

		this.createModelNotes();
		this.createCompartNotes();

		this.createNotesFromBioEntities(this.getBionetwork().getPhysicalEntityList());
		this.createNotesFromBioEntities(this.getBionetwork().getBiochemicalReactionList());
		this.createNotesFromBioEntities(this.getBionetwork().getGeneList());
		this.createNotesFromBioEntities(this.getBionetwork().getComplexList());
		this.createNotesFromBioEntities(this.getBionetwork().getProteinList());
		this.createNotesFromBioEntities(this.getBionetwork().getCompartments());
	}

	/**
	 * Create Model Notes from the saved notes if they exists
	 */
	private void createModelNotes() {
		if (this.getBionetwork().getModelNotes() != null
				&& !this.getBionetwork().getModelNotes().getXHTMLasString().isEmpty()) {
			try {
				this.getModel().setNotes(this.getBionetwork().getModelNotes().getXHTMLasString());
			} catch (XMLStreamException e) {
				NotesWriter.errorsAndWarnings.add("Unable to create Model Notes form the saved notes");
			}
		}
	}

	/**
	 * Creates the compartments' Notes from the saved notes if they exists and
	 * their references
	 */
	private void createCompartNotes() {
		for (BioCompartment biocmp : this.getBionetwork().getCompartments().values()) {

			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(StringUtils.convertToSID(biocmp.getId()));
			if (sbase == null) {
				NotesWriter.errorsAndWarnings.add("Compartment " + biocmp.getId() + " not found in created model.");
				return;
			}

			if (biocmp.getCompartNotes() != null && !biocmp.getCompartNotes().getXHTMLasString().isEmpty()) {

			}
		}
	}

	/**
	 * For each {@link BioEntity} of the list, creates it's Notes from the saved
	 * notes if they exists and its references
	 * 
	 * @param entityList
	 *            the list of {@link BioEntity}
	 */
	private void createNotesFromBioEntities(HashMap<String, ? extends BioEntity> entityList) {
		for (BioEntity ent : entityList.values()) {
			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(StringUtils.convertToSID(ent.getId()));

			if (sbase == null) {
				return;
			}
			Notes n;
			if (ent.getEntityNotes() != null && !ent.getEntityNotes().getXHTMLasString().isEmpty()) {
				n = ent.getEntityNotes();
			} else {
				n = new Notes();
			}

			switch (ent.getClass().getSimpleName()) {
			case "BioCompartment":
				this.addAdditionnalNotes((BioCompartment) ent, n);
				break;
			case "BioPhysicalEntity":
				this.addAdditionnalNotes((BioPhysicalEntity) ent, n);
				break;
			case "BioChemicalReaction":
				this.addAdditionnalNotes((BioChemicalReaction) ent, n);
				break;

			// case "BioGene":
			// this.addAdditionnalNotes((BioGene) ent, n);
			// break;
			}

			try {
				if (n != null && !n.isEmpty()) {
					sbase.setNotes(n.getXHTMLasString());
				}
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add or replace the values present in the notes with the metabolite's
	 * attribute
	 * 
	 * @param met
	 *            the metabolite as a {@link BioPhysicalEntity}
	 * @param n
	 *            the Notes
	 * @see Notes#addAttributeToNotes(String, String, boolean)
	 */
	private void addAdditionnalNotes(BioPhysicalEntity met, Notes n) {
		if (!met.getChemicalFormula().isEmpty()) {
			n.addAttributeToNotes("FORMULA", met.getChemicalFormula(), this.updateValue);
		}

		if (!met.getCharge().isEmpty()) {
			n.addAttributeToNotes("charge", met.getCharge(), this.updateValue);
		}

		if (met.getInchi() != null && !met.getInchi().isEmpty() && !met.getRefs().containsKey("inchi")
				&& !met.getInchi().equalsIgnoreCase("NA")) {
			n.addAttributeToNotes("inchi", met.getInchi(), this.updateValue);
		}

		if (met.getPubchemCID() != null && !met.getPubchemCID().isEmpty()
				&& !met.getRefs().containsKey("pubchem.compound") && !met.getPubchemCID().equalsIgnoreCase("NA")) {
			n.addAttributeToNotes("PubChem compound", met.getPubchemCID(), this.updateValue);
		}

		if (met.getSmiles() != null && !met.getSmiles().isEmpty() && !met.getRefs().containsKey("smiles")
				&& !met.getSmiles().equalsIgnoreCase("NA")) {
			n.addAttributeToNotes("smiles", met.getSmiles(), this.updateValue);
		}

		for (String BDName : met.getRefs().keySet()) {
			if (BDName.equalsIgnoreCase("SBO")) {
				continue;
			}
			String refNotes = "";
			int i = 0;
			for (BioRef ref : met.getRefs(BDName)) {

				if (ref.getId().isEmpty() || ref.getId().equalsIgnoreCase("NA"))
					continue;

				if (i == 0) {
					refNotes += ref.getId();
				} else {
					refNotes += " || " + ref.getId();
				}
				i++;
			}
			n.addAttributeToNotes(BDName, refNotes, this.updateValue);

		}
	}

	/**
	 * Add or replace the values present in the notes with the metabolite's
	 * attribute
	 * 
	 * @param met
	 *            the metabolite as a {@link BioPhysicalEntity}
	 * @param n
	 *            the Notes
	 * @see Notes#addAttributeToNotes(String, String, boolean)
	 */
	private void addAdditionnalNotes(BioCompartment cpt, Notes n) {

		for (String BDName : cpt.getRefs().keySet()) {
			if (BDName.equalsIgnoreCase("SBO")) {
				continue;
			}
			String refNotes = "";
			int i = 0;
			for (BioRef ref : cpt.getRefs(BDName)) {

				if (ref.getId().isEmpty() || ref.getId().equalsIgnoreCase("NA"))
					continue;

				if (i == 0) {
					refNotes += ref.getId();
				} else {
					refNotes += " || " + ref.getId();
				}
				i++;
			}
			n.addAttributeToNotes(BDName, refNotes, this.updateValue);

		}

	}

	/**
	 * Add or replace the values present in the notes with the reaction's
	 * attribute
	 * 
	 * @param bioRxn
	 *            the reaction
	 * @param n
	 *            the Notes
	 * @see Notes#addAttributeToNotes(String, String, boolean)
	 */
	private void addAdditionnalNotes(BioChemicalReaction bioRxn, Notes n) {

		if (!bioRxn.getPathwayList().isEmpty()) {
			String newPathwayNotes = "";
			int i = 0;
			for (BioPathway pthw : bioRxn.getPathwayList().values()) {
				if (i == 0) {
					newPathwayNotes += " " + pthw.getName().replaceAll("&", "&amp;");
				} else {
					newPathwayNotes += " || " + pthw.getName().replaceAll("&", "&amp;");
				}
				i++;
			}

			n.addAttributeToNotes("SUBSYSTEM", newPathwayNotes, this.updateValue);
		}

		if (!bioRxn.getEcNumber().isEmpty()) {
			n.addAttributeToNotes("EC NUMBER", bioRxn.getEcNumber(), this.updateValue);
		}

		if (!bioRxn.getScore().isEmpty()) {
			n.addAttributeToNotes("CONFIDENCE SCORE", bioRxn.getScore(), this.updateValue);
		}
		// Update the PMIDS
		if (bioRxn.getPmids().size() > 0) {
			String newAuthorsNote = "";
			int i = 0;
			for (String pmid : bioRxn.getPmids()) {
				if (i == 0) {
					newAuthorsNote += pmid;
				} else {
					newAuthorsNote += " || " + pmid;
				}
				i++;
			}
			n.addAttributeToNotes("PMID", newAuthorsNote, this.updateValue);
		}

		if (!bioRxn.getEnzList().isEmpty()) {

			if (bioRxn.getEnzList().size() == 1) {
				for (BioPhysicalEntity enz : bioRxn.getEnzList().values()) {
					n.addAttributeToNotes("GENE_ASSOCIATION", createGeneAssociationString(enz), this.updateValue);
				}
			} else {
				String orAssoc = "";
				int i = 0;
				for (BioPhysicalEntity enz : bioRxn.getEnzList().values()) {
					if (i == 0) {
						orAssoc += createGeneAssociationString(enz);
					} else {
						orAssoc += " OR " + createGeneAssociationString(enz);
					}
					i++;
				}
				n.addAttributeToNotes("GENE_ASSOCIATION", orAssoc, this.updateValue);
			}

		} else {
			n.addAttributeToNotes("GENE_ASSOCIATION", "", this.updateValue);
		}

	}

	// /**
	// *
	// * @param gene
	// * @param n
	// */
	// private void addAdditionnalNotes(BioGene gene, Notes n) {
	//
	//
	// }

	/**
	 * Create the Gene Association String that needs to be added into the
	 * Reaction's Notes </br>
	 * Recursively loop into the Enzyme's constituent to construct a valid
	 * logical expression
	 * 
	 * @param enz
	 *            the enzyme or enzyme constituent
	 * @return The string representing the association
	 */
	private String createGeneAssociationString(BioPhysicalEntity enz) {

		int i;
		String assoc = "";
		switch (enz.getClass().getSimpleName()) {
		case "BioProtein":

			i = 0;

			BioGene gene = ((BioProtein) enz).getGene();

			if (gene != null) {
				String n;
				if ((n = gene.getLabel()) == null)
					n = StringUtils.convertToSID(gene.getId());

				if (i == 0) {
					assoc += n;
				} else {
					assoc += " OR " + n;
				}
				i++;
			}
			// if (((BioProtein) enz).getGene().size() > 1) {
			// assoc = "(" + assoc + ")";
			// }

			break;
		case "BioComplex":
			i = 0;
			assoc += "(";
			for (BioPhysicalEntity part : ((BioComplex) enz).getAllComponentList().values()) {
				if (i == 0) {
					assoc += createGeneAssociationString(part);
				} else {
					assoc += " AND " + createGeneAssociationString(part);
				}
				i++;
			}
			assoc += ")";
			break;
		default:
			assoc = null;
			break;
		}
		return assoc;

	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	/**
	 * @return the bionetwork
	 */
	public BioNetwork getBionetwork() {
		return bionetwork;
	}

	/**
	 * @param bionetwork
	 *            the bionetwork to set
	 */
	public void setBionetwork(BioNetwork bionetwork) {
		this.bionetwork = bionetwork;
	}

	/**
	 * @return the updateValue
	 */
	public boolean isUpdateValue() {
		return updateValue;
	}

	/**
	 * @param updateValue
	 *            the updateValue to set
	 */
	public void setUpdateValue(boolean updateValue) {
		this.updateValue = updateValue;
	}

}
