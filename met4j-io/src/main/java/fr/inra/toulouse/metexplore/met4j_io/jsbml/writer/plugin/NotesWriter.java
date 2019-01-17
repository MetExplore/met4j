package fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin;


import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.Notes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin.tags.WriterSBML2Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin.tags.WriterSBML3Compatible;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;

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

		this.createNotesFromBioEntities(this.getBionetwork().getMetabolitesView());
		this.createNotesFromBioEntities(this.getBionetwork().getReactionsView());
		this.createNotesFromBioEntities(this.getBionetwork().getGenesView());
		this.createNotesFromBioEntities(this.getBionetwork().getEnzymesView());
		this.createNotesFromBioEntities(this.getBionetwork().getProteinsView());
		this.createNotesFromBioEntities(this.getBionetwork().getCompartmentsView());
	}

	/**
	 * Create Model Notes from the saved notes if they exists
	 */
	private void createModelNotes() {

		if(NetworkAttributes.getNotes(this.getBionetwork()) != null) {
		
			Notes notes = NetworkAttributes.getNotes(this.getBionetwork());

			if (!notes.getXHTMLasString().isEmpty()) {
				try {
					this.getModel().setNotes(notes.getXHTMLasString());
				} catch (XMLStreamException e) {
					NotesWriter.errorsAndWarnings.add("Unable to create Model Notes form the saved notes");
				}
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
	private void createNotesFromBioEntities(BioCollection<? extends BioEntity> entityList) {
		for (BioEntity ent : entityList) {
			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(StringUtils.convertToSID(ent.getId()));

			if (sbase == null) {
				return;
			}
			Notes n = GenericAttributes.getNotes(ent);

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
