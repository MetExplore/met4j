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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.gpr.GPR;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.MalformedGeneAssociationStringException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociations;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneSet;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML1Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

/**
 * This class is used to parse the Notes of SBML element. <br>
 * <br>
 * As Notes don't have a fixed syntax, users can define their own patterns to
 * extract information contained in the Notes of their SBMLs
 *
 * @author Benjamin
 * @version $Id: $Id
 * @since 3.0
 */
public class NotesParser implements PackageParser, AdditionalDataTag, ReaderSBML1Compatible, ReaderSBML2Compatible,
        ReaderSBML3Compatible {

    /**
     * The default pattern used to retrieve reaction's pathway data
     */
    public static final String defaultPathwayPattern = "(?i:>\\s*SUBSYSTEM:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve reaction's ec number
     */
    public static final String defaultECPattern = "(?i:>\\s*EC.NUMBER:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve reaction's GPR data
     */
    public static final String defaultGPRPattern = "(?i:>\\s*GENE.{0,1}ASSOCIATION:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve reaction's score
     */
    public static final String defaultscorePattern = "(?i:>\\s*SCORE:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve reaction's status
     */
    public static final String defaultstatusPattern = "(?i:>\\s*STATUS:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve reaction's comment
     */
    public static final String defaultcommentPattern = "(?i:>\\s*COMMENTS:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve reaction's PubMeb references
     */
    public static final String defaultpmidPattern = "(?i:PMID\\s*:\\s*([0-9,;+]+))";
    /**
     * The default pattern used to retrieve metabolite's charge
     */
    public static final String defaultchargePattern = "(?i:>\\s*CHARGE:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve metabolite's chemical formula
     */
    public static final String defaultformulaPattern = "(?i:>\\s*FORMULA:\\s*([^<]+)<)";
    /**
     * The default pattern used to retrieve element external identifiers
     */
    public static final String defaultextDBidsPAttern = "[>]+([a-zA-Z\\._0-9 ]+):\\s*([^<]+)<";
    /**
     * Constant <code>defaultInchiPattern</code>
     */
    public static final String defaultInchiPattern = "(?i:>\\s*INCHI:\\s*([^<]+)<)";
    /**
     * Constant <code>defaultSmilesPattern</code>
     */
    public static final String defaultSmilesPattern = "(?i:>\\s*SMILES:\\s*([^<]+)<)";
    /**
     * The default separator in Notes values.
     */
    public static final String defaultseparator = ",";
    /**
     * The Jsbml Model
     */
    public Model model;
    /**
     * The BioNetwork
     */
    public BioNetwork network;
    /**
     * The separator for multiple pathways present in a single pathway key. <br>
     * <br>
     * Most of the time, {@link #separator} is equal to "," and because this
     * character is very often used in pathway names, a second separator had to be
     * defined
     */
    public String pathwaySep = " \\|\\| ";
    /**
     * User defined pattern used to retrieve reaction's pathway data
     */
    public String pathwayPattern;
    /**
     * User defined pattern used to retrieve reaction's ec number
     */
    public String ECPattern;
    /**
     * User defined pattern used to retrieve reaction's GPR data
     */
    public String GPRPattern;
    /**
     * User defined pattern used to retrieve reaction's score
     */
    public String scorePattern;
    /**
     * User defined pattern used to retrieve reaction's status
     */
    public String statusPattern;
    /**
     * User defined pattern used to retrieve reaction's comment
     */
    public String commentPattern;
    /**
     * User defined pattern used to retrieve reaction's PubMeb references
     */
    public String pmidPattern;
    /**
     * User defined pattern used to retrieve metabolite's charge
     */
    public String chargePattern;
    /**
     * User defined pattern used to retrieve metabolite's chemical formula
     */
    public String formulaPattern;

    public String inchiPattern;

    public String smilesPattern;

    /**
     * User defined separator in Notes values.
     */
    public String separator = ",";

    /**
     * Set this to true if you want unmatched key/value pairs in the Species notes
     * to be added as supplementary Identifiers
     */
    public boolean othersAsRefs = true;

    /**
     * Constructor
     *
     * @param useDefault true to use the default patterns when parsing the Notes
     */
    public NotesParser(boolean useDefault) {
        if (useDefault)
            this.setDefaultPatterns();
    }

    /**
     * Recursive function that parse gene association logical expression strings.
     * <br>
     * Internally this uses {@link fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils#findClosingParen(char[], int)} to
     * split the GPR according to the outer most parenthesis
     *
     * @param assosString The full GPR String in the first recursion, an inner part
     *                    of the initial GPR on the following recursions
     * @param network     a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     * @return a list of {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneSet}
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.MalformedGeneAssociationStringException if any.
     */
    public static GeneAssociation computeGeneAssociation(String assosString, BioNetwork network)
            throws MalformedGeneAssociationStringException {

        GeneAssociation geneAssociation = new GeneAssociation();

        ArrayList<String> subAssos = new ArrayList<String>();

        String tmpAssos = assosString;

        /**
         * This Allows to separate parenthesis block.
         */
        while (tmpAssos.contains("(")) {
            for (int i = 0, n = tmpAssos.length(); i < n; i++) {
                char c = tmpAssos.charAt(i);

                if (c == '(') {
                    try {
                        int end = StringUtils.findClosingParen(tmpAssos.toCharArray(), i);

                        String subAsso = tmpAssos.substring(i + 1, end);

                        subAssos.add(subAsso);

                        tmpAssos = tmpAssos.substring(0, i) + tmpAssos.substring(end + 1, tmpAssos.length());

                    } catch (ArrayIndexOutOfBoundsException e) {
                        throw new MalformedGeneAssociationStringException("Malformed Gene Association");
                    }

                    break;
                }
            }
        }

        if (tmpAssos.toLowerCase().contains(" or ")) {
            StringUtils.addAllNonEmpty(subAssos, Arrays.asList(tmpAssos.split("(?i) or ")));

            for (String s : subAssos) {
                geneAssociation.addAll(computeGeneAssociation(s, network));
            }

        } else if (tmpAssos.toLowerCase().contains(" and ")) {
            StringUtils.addAllNonEmpty(subAssos, Arrays.asList(tmpAssos.split("(?i) and ")));
            // foreach items in "and" block

            ArrayList<GeneAssociation> geneAssociations = new ArrayList<GeneAssociation>();

            for (String s : subAssos) {
                geneAssociations.add(computeGeneAssociation(s, network));
            }

            // Merge the geneAssociations
            geneAssociation = GeneAssociations.merge(geneAssociations.stream().toArray(GeneAssociation[]::new));

        } else {
            tmpAssos = tmpAssos.replaceAll(" ", "");
            if (!tmpAssos.isEmpty()) {
                GeneSet x = new GeneSet();

                BioGene g = network.getGenesView().get(tmpAssos);
                if (g == null) {
                    g = new BioGene(tmpAssos);
                    network.add(g);
                }
                x.add(g);
                geneAssociation.add(x);
            } else {
                for (String s : subAssos) {
                    geneAssociation.addAll(computeGeneAssociation(s, network));
                }
            }
        }

        return geneAssociation;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Launch the parsing of Notes.
     * {@link BioCompartment} has a different method because it does not extends the
     * {@link BioEntity} class
     */
    @Override
    public void parseModel(Model model, BioNetwork bionetwork) {
        System.err.println("Starting " + this.getAssociatedPackageName() + " plugin...");

        this.setNetwork(bionetwork);
        this.setModel(model);

        this.addNetworkNotes(model, bionetwork);

        if (bionetwork.getPathwaysView().size() > 0) {
            this.setPathwayPattern(null);
        }

        if (bionetwork.getGenesView().size() > 0) {
            this.setGPRPattern(null);
        }

        this.addNotes(bionetwork.getReactionsView());
        this.addNotes(bionetwork.getMetabolitesView());
        this.addNotes(bionetwork.getCompartmentsView());

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssociatedPackageName() {
        return "note";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPackageUseableOnModel(Model model) {
        return true;
    }

    /**
     * Link the {@link #model}'s notes to the bionetwork
     *
     * @param model  the sbml model
     * @param bionet the bionetwork
     */
    private void addNetworkNotes(Model model, BioNetwork bionet) {
        try {
            NetworkAttributes.setNotes(bionet, (new Notes(model.getNotesString())));
        } catch (XMLStreamException e) {
            System.err.println("Error while parsing Model notes");
            e.printStackTrace();
        }

    }

    /**
     * For each {@link BioEntity} of the list, launches the correct parseNotes
     * method
     *
     * @param list the bionetwork list of {@link BioEntity}
     */
    private void addNotes(BioCollection<? extends BioEntity> list) {

        for (BioEntity ent : list) {
            String id = ent.getId();
            UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(id);

            if (GenericAttributes.getNotes(ent) == null) {
                try {

                    if (sbase != null && sbase.isSetNotes()) {
                        GenericAttributes.setNotes(ent, new Notes(sbase.getNotesString()));

                        if (ent instanceof BioCompartment) {
                            this.parseNotes((BioCompartment) ent);
                        } else if (ent instanceof BioReaction) {
                            this.parseNotes((BioReaction) ent);
                        } else if (ent instanceof BioMetabolite) {
                            this.parseNotes((BioMetabolite) ent);
                        }
                    }

                } catch (XMLStreamException e) {
                    e.printStackTrace();
                    NotesParser.errorsAndWarnings.add(
                            "Error while parsing " + ent.getClass().getSimpleName() + " " + ent.getId() + " notes");

                }
            }
        }
    }

    /**
     * Parse The reaction's note to retrieve missing informations:
     *
     * <ul>
     * <li>The Pathways, uses {@link #pathwayPattern}
     * <li>The EC number, uses {@link #ECPattern}
     * <li>The score, uses {@link #scorePattern}
     * <li>The Status, uses {@link #statusPattern}
     * <li>The Pubmed references, uses {@link #pmidPattern}
     * <li>The comment, uses {@link #commentPattern}
     * <li>The GPR, uses {@link #GPRPattern}.
     * </ul>
     *
     * @param reaction The {@link BioReaction}
     */
    private void parseNotes(BioReaction reaction) {

        String reactionNotes = ReactionAttributes.getNotes(reaction).getXHTMLasString();

        Matcher m;

        if (this.getPathwayPattern() != null) {

            m = Pattern.compile(this.getPathwayPattern()).matcher(reactionNotes);

            while (m.find()) {

                String[] pthwList = m.group(1).split(this.getPathwaySep());
                for (String val : pthwList) {
                    String value = val.trim().replaceAll("[^\\p{ASCII}]", "");

                    if (!isVoid(value)) {

                        if (this.getNetwork().getPathwaysView().containsId(value)) {
                            this.getNetwork().affectToPathway(this.getNetwork().getPathwaysView().get(value), reaction
                            );
                        } else {
                            BioPathway bionetPath = new BioPathway(value, value);
                            this.getNetwork().add(bionetPath);
                            this.getNetwork().affectToPathway(bionetPath, reaction);
                        }
                    }
                }
            }
        }

        // get the ec number
        if (this.getECPattern() != null) {
            m = Pattern.compile(this.getECPattern()).matcher(reactionNotes);
            while (m.find()) {
                String ec = m.group(1).trim();
                if (!isVoid(ec)) {
                    String oldEcNumber = reaction.getEcNumber();
                    if (isVoid(oldEcNumber)) {
                        reaction.setEcNumber(ec);
                    } else {
                        reaction.setEcNumber(oldEcNumber + ";" + ec);
                    }
                }
            }
        }

        // get the reaction score
        if (this.getScorePattern() != null
                && (m = Pattern.compile(this.getScorePattern()).matcher(reactionNotes)).find()) {
            String value = m.group(1).trim();

            if (!isVoid(value)) {
                try {
                    ReactionAttributes.setScore(reaction, Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    NotesParser.errorsAndWarnings
                            .add("[Warning] Reaction score must be a double for reaction " + reaction.getId());
                }
            }
        }

        // get the reaction status
        if (this.getStatusPattern() != null
                && (m = Pattern.compile(this.getStatusPattern()).matcher(reactionNotes)).find()) {
            String value = m.group(1).trim();

            if (!isVoid(value)) {
                ReactionAttributes.setStatus(reaction, value);
            }
        }

        // get the PMIDS
        if (this.getPmidPattern() != null) {

            m = Pattern.compile(this.getPmidPattern()).matcher(reactionNotes);

            while (m.find()) {

                String pmidsStr = m.group(1).trim();

                if (!isVoid(pmidsStr)) {

                    String[] pmids = pmidsStr.split(this.separator);

                    for (int i = 0; i < pmids.length; i++) {
                        String pmid = pmids[i].trim();

                        if (!isVoid(pmid)) {
                            String pmidInt = pmid.replaceAll("[^\\d]", "");
                            try {
                                ReactionAttributes.addPmid(reaction, Integer.parseInt(pmidInt));
                            } catch (NumberFormatException e) {
                                NotesParser.errorsAndWarnings.add("[Warning] Pmid " + pmidInt + " is not an integer");
                            }
                        }
                    }
                }
            }
        }

        // get the note/comment field (yes there is a note field in the sbml
        // note element..)
        if (this.getCommentPattern() != null
                && (m = Pattern.compile(this.getCommentPattern()).matcher(reactionNotes)).find()) {
            String value = m.group(1).trim();

            if (!isVoid(value)) {
                ReactionAttributes.setComment(reaction, value);
            }

        }

        if (reaction.getEnzymesView().isEmpty()) {

            if (this.getGPRPattern() != null
                    && (m = Pattern.compile(this.getGPRPattern()).matcher(reactionNotes)).find()) {
                try {
                    GPR.createGPRfromString(this.network, reaction, m.group(1));
                } catch (MalformedGeneAssociationStringException e) {
                    NotesParser.errorsAndWarnings.add(e.getLocalizedMessage());
                }

            }
        }

        this.parseOtherRefs(reaction);

    }

    /**
     * Parse the notes of the metabolite to retrieve additional informations:
     * <ul>
     * <li>The Formula, uses {@link #formulaPattern}
     * <li>The Charge, uses {@link #chargePattern}
     * <li>Extenal identifiers, uses {@link #defaultextDBidsPAttern} when
     * {@link #othersAsRefs} is true
     * </ul>
     *
     * @param metabolite
     */
    private void parseNotes(BioMetabolite metabolite) {

        String metaboNotes = MetaboliteAttributes.getNotes(metabolite).getXHTMLasString();

        // metaboNotes=metaboNotes.replaceAll(">\\s+<", "><");
        Matcher m;

        if (this.getFormulaPattern() != null
                && (m = Pattern.compile(this.getFormulaPattern()).matcher(metaboNotes)).find()) {
            String value = m.group(1).trim();

            if (!isVoid(value)) {
                metabolite.setChemicalFormula(value);

                metaboNotes = metaboNotes.replaceAll(this.getFormulaPattern(), "");
            }

        }

        if (this.getChargePattern() != null
                && (m = Pattern.compile(this.getChargePattern()).matcher(metaboNotes)).find()) {
            String value = m.group(1).trim();

            if (!isVoid(value)) {

                try {
                    metabolite.setCharge((int) Double.parseDouble(value));
                } catch (NumberFormatException e) {
                    System.err.println("[WARNING][met4j-io] Be careful, charge is not in good format for the metabolite "
                            + metabolite.getId() +
                            "and won't be set");
                }


                metaboNotes = metaboNotes.replaceAll(this.getChargePattern(), "");
            }
        }

        if (this.getInchiPattern() != null
                && (m = Pattern.compile(this.getInchiPattern()).matcher(metaboNotes)).find()) {
            String value = m.group(1).trim();

            if (!isVoid(value)) {
                String inchi = value;
                inchi = inchi.replaceAll("(?i)InChI\\=", "");
                metabolite.setInchi(inchi);

                metaboNotes = metaboNotes.replaceAll(this.getInchiPattern(), "");
            }
        }

        if (this.getSmilesPattern() != null
                && (m = Pattern.compile(this.getSmilesPattern()).matcher(metaboNotes)).find()) {
            String value = m.group(1).trim();

            if (!isVoid(value)) {

                metabolite.setSmiles(value);

                metaboNotes = metaboNotes.replaceAll(this.getSmilesPattern(), "");
            }
        }

        this.parseOtherRefs(metabolite);

    }

    /**
     * @param e
     */
    private void parseOtherRefs(BioEntity e) {

        String notes = GenericAttributes.getNotes(e).getXHTMLasString();

        String dbName = null;
        String values;

        Matcher m;

        if (this.isOthersAsRefs()) {

            m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);

            while (m.find()) {

                dbName = m.group(1).trim().toLowerCase();
                values = m.group(2).trim();


                if (isVoid(values)) {
                    notes = notes.replace(m.group(0), "");
                    m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);
                    continue;

                } else {
                    if (dbName.compareToIgnoreCase(MetaboliteAttributes.INCHI) != 0) {
                        if (!dbName.matches(NotesParser.defaultchargePattern) &&
                                !dbName.matches(NotesParser.defaultECPattern) &&
                                !dbName.matches(NotesParser.defaultPathwayPattern) &&
                                !dbName.matches(NotesParser.defaultformulaPattern)) {
                            String[] ids = values.split(this.getSeparator());
                            for (String value : ids) {
                                if (!e.hasRef(dbName, value)) {
                                    e.addRef(new BioRef("SBML", dbName, value, 1));
                                }
                            }
                        }
                    } else {
                        String inchi = values;
                        inchi = inchi.replaceAll("(?i)InChI\\=", "");
                        e.addRef(new BioRef("SBML", dbName, inchi, 1));
                    }
                }
                notes = notes.replace(m.group(0), "");
                m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);
            }


        }

    }

    /**
     * @param cpt
     */
    private void parseNotes(BioCompartment cpt) {

        this.parseOtherRefs(cpt);

    }

    /**
     * Set all patterns to their default values using the defined static fields
     */
    public void setDefaultPatterns() {
        this.setPathwayPattern(defaultPathwayPattern);
        this.setECPattern(defaultECPattern);
        this.setGPRPattern(defaultGPRPattern);
        this.setScorePattern(defaultscorePattern);
        this.setStatusPattern(defaultstatusPattern);
        this.setCommentPattern(defaultcommentPattern);
        this.setPmidPattern(defaultpmidPattern);
        this.setChargePattern(defaultchargePattern);
        this.setFormulaPattern(defaultformulaPattern);
        this.setSeparator(defaultseparator);
        this.setInchiPattern(defaultInchiPattern);
        this.setSmilesPattern(defaultSmilesPattern);

    }

    /**
     * <p>Getter for the field <code>model</code>.</p>
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * <p>Setter for the field <code>model</code>.</p>
     *
     * @param model the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * <p>Getter for the field <code>network</code>.</p>
     *
     * @return the bionetwork
     */
    public BioNetwork getNetwork() {
        return network;
    }

    /**
     * <p>Setter for the field <code>network</code>.</p>
     *
     * @param network the bionetwork to set
     */
    public void setNetwork(BioNetwork network) {
        this.network = network;
    }

    /**
     * <p>Getter for the field <code>pathwaySep</code>.</p>
     *
     * @return the pathwaySep
     */
    public String getPathwaySep() {
        return pathwaySep;
    }

    /**
     * <p>Setter for the field <code>pathwaySep</code>.</p>
     *
     * @param pathwaySep the pathwaySep to set
     */
    public void setPathwaySep(String pathwaySep) {
        this.pathwaySep = pathwaySep;
    }

    /**
     * <p>Getter for the field <code>pathwayPattern</code>.</p>
     *
     * @return the pathwayPattern
     */
    public String getPathwayPattern() {
        return pathwayPattern;
    }

    /**
     * <p>Setter for the field <code>pathwayPattern</code>.</p>
     *
     * @param pathwayPattern the pathwayPattern to set
     */
    public void setPathwayPattern(String pathwayPattern) {
        this.pathwayPattern = pathwayPattern;
    }

    /**
     * <p>getECPattern.</p>
     *
     * @return the eCPattern
     */
    public String getECPattern() {
        return ECPattern;
    }

    /**
     * <p>setECPattern.</p>
     *
     * @param eCPattern the eCPattern to set
     */
    public void setECPattern(String eCPattern) {
        ECPattern = eCPattern;
    }

    /**
     * <p>getGPRPattern.</p>
     *
     * @return the gPRPattern
     */
    public String getGPRPattern() {
        return GPRPattern;
    }

    /**
     * <p>setGPRPattern.</p>
     *
     * @param gPRPattern the gPRPattern to set
     */
    public void setGPRPattern(String gPRPattern) {
        GPRPattern = gPRPattern;
    }

    /**
     * <p>Getter for the field <code>scorePattern</code>.</p>
     *
     * @return the scorePattern
     */
    public String getScorePattern() {
        return scorePattern;
    }

    /**
     * <p>Setter for the field <code>scorePattern</code>.</p>
     *
     * @param scorePattern the scorePattern to set
     */
    public void setScorePattern(String scorePattern) {
        this.scorePattern = scorePattern;
    }

    /**
     * <p>Getter for the field <code>statusPattern</code>.</p>
     *
     * @return the statusPattern
     */
    public String getStatusPattern() {
        return statusPattern;
    }

    /**
     * <p>Setter for the field <code>statusPattern</code>.</p>
     *
     * @param statusPattern the statusPattern to set
     */
    public void setStatusPattern(String statusPattern) {
        this.statusPattern = statusPattern;
    }

    /**
     * <p>Getter for the field <code>commentPattern</code>.</p>
     *
     * @return the commentPattern
     */
    public String getCommentPattern() {
        return commentPattern;
    }

    /**
     * <p>Setter for the field <code>commentPattern</code>.</p>
     *
     * @param commentPattern the commentPattern to set
     */
    public void setCommentPattern(String commentPattern) {
        this.commentPattern = commentPattern;
    }

    /**
     * <p>Getter for the field <code>pmidPattern</code>.</p>
     *
     * @return the pmidPattern
     */
    public String getPmidPattern() {
        return pmidPattern;
    }

    /**
     * <p>Setter for the field <code>pmidPattern</code>.</p>
     *
     * @param pmidPattern the pmidPattern to set
     */
    public void setPmidPattern(String pmidPattern) {
        this.pmidPattern = pmidPattern;
    }

    /**
     * <p>Getter for the field <code>chargePattern</code>.</p>
     *
     * @return the chargePattern
     */
    public String getChargePattern() {
        return chargePattern;
    }

    /**
     * <p>Setter for the field <code>chargePattern</code>.</p>
     *
     * @param chargePattern the chargePattern to set
     */
    public void setChargePattern(String chargePattern) {
        this.chargePattern = chargePattern;
    }

    /**
     * <p>Getter for the field <code>formulaPattern</code>.</p>
     *
     * @return the formulaPattern
     */
    public String getFormulaPattern() {
        return formulaPattern;
    }

    /**
     * <p>Setter for the field <code>formulaPattern</code>.</p>
     *
     * @param formulaPattern the formulaPattern to set
     */
    public void setFormulaPattern(String formulaPattern) {
        this.formulaPattern = formulaPattern;
    }

    /**
     * <p>Getter for the field <code>separator</code>.</p>
     *
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * <p>Setter for the field <code>separator</code>.</p>
     *
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * <p>isOthersAsRefs.</p>
     *
     * @return the othersAsRefs
     */
    public boolean isOthersAsRefs() {
        return othersAsRefs;
    }

    /**
     * <p>Setter for the field <code>othersAsRefs</code>.</p>
     *
     * @param othersAsRefs the othersAsRefs to set
     */
    public void setOthersAsRefs(boolean othersAsRefs) {
        this.othersAsRefs = othersAsRefs;
    }

    /**
     * <p>Getter for the field <code>inchiPattern</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getInchiPattern() {
        return inchiPattern;
    }

    /**
     * <p>Setter for the field <code>inchiPattern</code>.</p>
     *
     * @param inchiPattern a {@link java.lang.String} object.
     */
    public void setInchiPattern(String inchiPattern) {
        this.inchiPattern = inchiPattern;
    }

    /**
     * <p>Getter for the field <code>smilesPattern</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getSmilesPattern() {
        return smilesPattern;
    }

    /**
     * <p>Setter for the field <code>smilesPattern</code>.</p>
     *
     * @param smilesPattern a {@link java.lang.String} object.
     */
    public void setSmilesPattern(String smilesPattern) {
        this.smilesPattern = smilesPattern;
    }

}
