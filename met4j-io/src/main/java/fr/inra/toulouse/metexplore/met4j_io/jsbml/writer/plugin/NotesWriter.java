package fr.inra.toulouse.metexplore.met4j_io.jsbml.writer.plugin;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.UniqueNamedSBase;

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inra.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;
import org.sbml.jsbml.xml.XMLNode;

import static fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

/**
 * Creates Notes for the SBML entities of the model created by
 *
 * @author Benjamin
 * @since 3.0
 */
public class NotesWriter implements PackageWriter, AdditionalDataTag {

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
     * @param doUpdates Set this to true to allow the plugin to update the saved Notes
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
        this.createNotesFromBioEntities(this.getBionetwork().getGenesView());
        this.createNotesFromBioEntities(this.getBionetwork().getEnzymesView());
        this.createNotesFromBioEntities(this.getBionetwork().getProteinsView());
        this.createNotesFromBioEntities(this.getBionetwork().getCompartmentsView());
        this.createNotesFromBioEntities(this.getBionetwork().getReactionsView());


//        SBMLWriter writer = new SBMLWriter();
//        writer.setIndentationChar('\t');
//        writer.setIndentationCount((short) 1);
//
//        System.err.println("sbml string after notes...");
//
//        SBMLDocument doc = new SBMLDocument(3, 1);
//        doc.setModel(this.getModel());
//
//        try {
//            System.err.println(writer.writeSBMLToString(doc));
//        } catch (XMLStreamException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * Create Model Notes from the saved notes if they exists
     */
    private void createModelNotes() {

        if (NetworkAttributes.getNotes(this.getBionetwork()) != null) {

            Notes notes = NetworkAttributes.getNotes(this.getBionetwork());

            if (!isVoid(notes.getXHTMLasString())) {
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
     * @param entityList the list of {@link BioEntity}
     */
    private void createNotesFromBioEntities(BioCollection<? extends BioEntity> entityList) {
        for (BioEntity ent : entityList) {
            UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(StringUtils.convertToSID(ent.getId()));

            if (sbase == null) {
                return;
            }
            Notes n = GenericAttributes.getNotes(ent);

            if (n == null) {
                n = new Notes();
            }

            for (String BDName : ent.getRefs().keySet()) {
                if (BDName.equalsIgnoreCase("SBO")) {
                    continue;
                }
                String refNotes = "";
                int i = 0;
                for (BioRef ref : ent.getRefs(BDName)) {

                    if (isVoid(ref.getId()) || ref.getId().equalsIgnoreCase("NA"))
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

            if (ent.getClass() == BioCompartment.class) {
                this.addAdditionnalNotes((BioCompartment) ent, n);
            } else if (ent.getClass() == BioMetabolite.class) {
                this.addAdditionnalNotes((BioMetabolite) ent, n);
            } else if (ent.getClass() == BioReaction.class) {
                this.addAdditionnalNotes((BioReaction) ent, n);
            }

            if (n != null && !n.isEmpty()) {
                String notesStr = n.getXHTMLasString();
                notesStr = notesStr.replaceAll("<body>", "<body xmlns=\"http://www.w3.org/1999/xhtml\">");

                try {
                    sbase.setNotes(notesStr);
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Add or replace the values present in the notes with the metabolite's
     * attribute
     *
     * @param met the metabolite as a {@link BioPhysicalEntity}
     * @param n   the Notes
     * @see Notes#addAttributeToNotes(String, String, boolean)
     */
    private void addAdditionnalNotes(BioMetabolite met, Notes n) {
        if (!isVoid(met.getChemicalFormula())) {
            n.addAttributeToNotes(MetaboliteAttributes.FORMULA, met.getChemicalFormula(), this.updateValue);
        }

        if (met.getCharge() != 0) {
            n.addAttributeToNotes(MetaboliteAttributes.CHARGE, Integer.toString(met.getCharge()), this.updateValue);
        }

        if (!isVoid(met.getInchi()) && !met.getRefs().containsKey("inchi")
                && !met.getInchi().equalsIgnoreCase("NA")) {
            n.addAttributeToNotes(MetaboliteAttributes.INCHI, met.getInchi(), this.updateValue);
        }

        if (MetaboliteAttributes.getPubchem(met) != null) {
            n.addAttributeToNotes(MetaboliteAttributes.PUBCHEM, MetaboliteAttributes.getPubchem(met), this.updateValue);
        }

        if (!isVoid(met.getSmiles()) && !met.getRefs().containsKey("smiles")
                && !met.getSmiles().equalsIgnoreCase("NA")) {
            n.addAttributeToNotes("smiles", met.getSmiles(), this.updateValue);
        }

        if (met.getMolecularWeight() != null) {
            n.addAttributeToNotes("weight", met.getMolecularWeight().toString(), this.updateValue);
        }

    }

    /**
     * Add or replace the values present in the notes with the compartment's
     * attribute
     *
     * @param n the Notes
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

                if (isVoid(ref.getId()) || ref.getId().equalsIgnoreCase("NA"))
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
     * @param bioRxn the reaction
     * @param n      the Notes
     * @see Notes#addAttributeToNotes(String, String, boolean)
     */
    private void addAdditionnalNotes(BioReaction bioRxn, Notes n) {

        BioCollection<BioPathway> pathways = this.getBionetwork().getPathwaysFromReaction(bioRxn);

        if (!pathways.isEmpty()) {
            String newPathwayNotes = "";
            int i = 0;
            for (BioPathway pthw : pathways) {
                if (i == 0) {
                    newPathwayNotes += " " + pthw.getName().replaceAll("&", "&amp;");
                } else {
                    newPathwayNotes += " || " + pthw.getName().replaceAll("&", "&amp;");
                }
                i++;
            }

            n.addAttributeToNotes(ReactionAttributes.SUBSYSTEM, newPathwayNotes, this.updateValue);
        }

        if (!isVoid(bioRxn.getEcNumber())) {
            n.addAttributeToNotes(ReactionAttributes.EC_NUMBER, bioRxn.getEcNumber(), this.updateValue);
        }

        Double score = ReactionAttributes.getScore(bioRxn);

        if (score != null) {
            n.addAttributeToNotes(ReactionAttributes.SCORE, Double.toString(score), this.updateValue);
        }

        Set<Integer> pmids = ReactionAttributes.getPmids(bioRxn);

        // Update the PMIDS
        if (pmids != null && pmids.size() > 0) {
            String newAuthorsNote = "";
            int i = 0;

            ArrayList<Integer> sortedPmids = new ArrayList<Integer>(pmids);

            Collections.sort(sortedPmids);

            for (Integer pmid : sortedPmids) {
                if (i == 0) {
                    newAuthorsNote += pmid;
                } else {
                    newAuthorsNote += "," + pmid;
                }
                i++;
            }
            n.addAttributeToNotes(GenericAttributes.PMIDS, newAuthorsNote, this.updateValue);
        }


        String geneAssociation = BioReactionUtils.getGPR(this.getBionetwork(), bioRxn, true);
        if (!isVoid(geneAssociation)) {
            n.addAttributeToNotes(ReactionAttributes.GENE_ASSOCIATION, geneAssociation, this.updateValue);
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
     * @return the bionetwork
     */
    public BioNetwork getBionetwork() {
        return bionetwork;
    }

    /**
     * @param bionetwork the bionetwork to set
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
     * @param updateValue the updateValue to set
     */
    public void setUpdateValue(boolean updateValue) {
        this.updateValue = updateValue;
    }

}
