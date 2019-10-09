package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioRef;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import static fr.inra.toulouse.metexplore.met4j_core.utils.StringUtils.isVoid;

import fr.inra.toulouse.metexplore.met4j_io.annotations.GenericAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inra.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.attributes.Notes;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.errors.MalformedGeneAssociationStringException;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.FluxReaction;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc.GeneSet;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML1Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import fr.inra.toulouse.metexplore.met4j_io.utils.StringUtils;

/**
 * This class is used to parse the Notes of SBML element. </br>
 * </br>
 * As Notes don't have a fixed syntax, users can define their own patterns to
 * extract information contained in the Notes of their SBMLs
 * 
 * @author Benjamin
 * @since 3.0
 */
public class NotesParser implements PackageParser, AdditionalDataTag, ReaderSBML1Compatible, ReaderSBML2Compatible,
		ReaderSBML3Compatible {

	/**
	 * The Jsbml Model
	 */
	public Model model;
	/**
	 * The BioNetwork
	 */
	public BioNetwork bionetwork;
	/**
	 * The default pattern used to retrieve reaction's pathway data
	 */
	public static final String defaultPathwayPattern = "[> ]+SUBSYSTEM:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve reaction's ec number
	 */
	public static final String defaultECPattern = "[> ]+EC.NUMBER:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve reaction's GPR data
	 */
	public static final String defaultGPRPattern = "[> ]+GENE.{0,1}ASSOCIATION:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve reaction's score
	 */
	public static final String defaultscorePattern = "[> ]+SCORE:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve reaction's status
	 */
	public static final String defaultstatusPattern = "[> ]+STATUS:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve reaction's comment
	 */
	public static final String defaultcommentPattern = "[> ]+COMMENTS:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve reaction's PubMeb references
	 */
	public static final String defaultpmidPattern = "PMID\\s*:\\s*([0-9,;+]+)";
	/**
	 * The default pattern used to retrieve metabolite's charge
	 */
	public static final String defaultchargePattern = "[> ]+CHARGE:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve metabolite's chemical formula
	 */
	public static final String defaultformulaPattern = "(?i)[> ]+FORMULA:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve element external identifiers
	 */
	public static final String defaultextDBidsPAttern = "[>]+([a-zA-Z\\._0-9 ]+):\\s*([^<]+)<";

	public static final String defaultInchiPattern = "[> ]+INCHI:\\s*([^<]+)<";

	public static final String defaultSmilesPattern = "[> ]+SMILES:\\s*([^<]+)<";

	/**
	 * The default separator in Notes values.
	 */
	public static final String defaultseparator = ",";

	/**
	 * The separator for multiple pathways present in a single pathway key. </br>
	 * </br>
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
			this.setDefaultPaterns();
	}

	/**
	 * Launch the parsing of Notes. Launches {@link #addNotes(HashMap)} or
	 * {@link #getSBMLCompartNotes(HashMap)} on the following list of the
	 * bionetwork:
	 * <ul>
	 * <li>{@link BioNetwork#getReactionsView()}
	 * <li>{@link BioNetwork#getPhysicalEntityList()}
	 * <li>{@link BioNetwork#getCompartments()}
	 * </ul>
	 * {@link BioCompartment} has a different method because it does not extends the
	 * {@link BioEntity} class
	 */
	@Override
	public void parseModel(Model model, BioNetwork bionetwork) {
		System.err.println("Starting " + this.getAssociatedPackageName() + " plugin...");

		this.setBionetwork(bionetwork);
		this.setModel(model);

		this.addNetworkNotes(model, bionetwork);

		this.addNotes(bionetwork.getReactionsView());
		this.addNotes(bionetwork.getMetabolitesView());
		this.addNotes(bionetwork.getCompartmentsView());
		
	}

	@Override
	public String getAssociatedPackageName() {
		return "note";
	}

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
	 * <li>The GPR, uses {@link #GPRPattern}. This internally uses the
	 * {@link #getGA(String, FluxReaction)} method
	 * </ul>
	 * 
	 * @param reaction The {@link BioReaction}
	 */
	private void parseNotes(BioReaction reaction) {

		String reactionNotes = ReactionAttributes.getNotes(reaction).getXHTMLasString();

		Matcher m;

		if (this.getPathwayPattern() != null
				&& (m = Pattern.compile(this.getPathwayPattern()).matcher(reactionNotes)).find()) {

			String[] pthwList = m.group(1).split(this.getPathwaySep());
			for (String val : pthwList) {
				String value = val.trim().replaceAll("[^\\p{ASCII}]", "");

				if (!isVoid(value)) {

					if (this.getBionetwork().getPathwaysView().containsId(value)) {
						this.getBionetwork().affectToPathway(reaction,
								this.getBionetwork().getPathwaysView().get(value));
					} else {
						BioPathway bionetPath = new BioPathway(value, value);
						this.getBionetwork().add(bionetPath);
						this.getBionetwork().affectToPathway(reaction, bionetPath);
					}
				}
			}
		}

		// get the ec number
		if (this.getECPattern() != null && (m = Pattern.compile(this.getECPattern()).matcher(reactionNotes)).find()) {

			String ec = m.group(1).trim();
			if (!isVoid(ec)) {
				reaction.setEcNumber(ec);
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
							try {
								ReactionAttributes.addPmid(reaction, Integer.parseInt(pmid));
							} catch (NumberFormatException e) {
								NotesParser.errorsAndWarnings.add("[Warning] Pmid " + pmid + " is not an integer");
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

				FluxReaction flx = new FluxReaction(reaction);

				flx.setReactionGeneAssociation(new GeneAssociation());
				try {
					flx.setReactionGeneAssociation(this.computeGeneAssociation(m.group(1), flx));
					flx.convertGeneAssociationstoComplexes(this.bionetwork);
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
	 * @param metabolite the metabolite as a {@link BioPhysicalEntity}
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

				metabolite.setCharge(Integer.parseInt(value));

				metaboNotes = metaboNotes.replaceAll(this.getChargePattern(), "");
			}
		}

		if (this.getInchiPattern() != null
				&& (m = Pattern.compile(this.getInchiPattern()).matcher(metaboNotes)).find()) {
			String value = m.group(1).trim();

			if (!isVoid(value)) {

				metabolite.setInchi(value);

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
	 * 
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

				dbName = m.group(1).trim();
				values = m.group(2).trim();

				if (isVoid(values)) {
					notes = notes.replace(m.group(0), "");
					m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);
					continue;

				} else {
					String[] ids = values.split(this.getSeparator());
					for (String value : ids) {
						if (!e.hasRef(dbName, value)) {
							e.addRef(new BioRef("SBML File", dbName, value, 1));
						}
					}
				}

				notes = notes.replace(m.group(0), "");
				m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);

			}
		}

	}

	/**
	 * 
	 * @param cpt
	 */
	private void parseNotes(BioCompartment cpt) {

		this.parseOtherRefs(cpt);

	}

	/**
	 * Recursive function that parse gene association logical expression strings.
	 * </br>
	 * This is an adaptation of
	 * {@link FBCParser#computeGeneAssocations(org.sbml.jsbml.ext.fbc.Association)}
	 * on Strings. </br>
	 * </br>
	 * Internally this uses {@link StringUtils#findClosingParen(char[], int)} to
	 * split the GPR according to the outer most parenthesis
	 * 
	 * @param assosString The full GPR String in the first recursion, an inner part
	 *                    of the initial GPR on the following recursions
	 * @param flxRxn      the flux reaction
	 * @return a list of {@link GeneSet}
	 * @throws MalformedGeneAssociationStringException when the GPR is malformed, ie
	 *                                                 when there is a missing
	 *                                                 parenthesis in the string
	 *                                                 that creates a confusions on
	 *                                                 the AND/OR associations
	 */
	public GeneAssociation computeGeneAssociation(String assosString, FluxReaction flxRxn)
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
						subAssos.add(tmpAssos.substring(i + 1, end));

						tmpAssos = tmpAssos.substring(0, i) + tmpAssos.substring(end + 1, tmpAssos.length());

					} catch (ArrayIndexOutOfBoundsException e) {
						throw new MalformedGeneAssociationStringException("Malformed Gene Association in reaction "
								+ flxRxn.getId() + ". This Gene Association will be ignored "
								+ "and the genes it contains will not be added to the model if they are not present in another reaction.");
					}

					break;
				}
			}
		}

		if (tmpAssos.contains(" or ") || tmpAssos.contains(" Or ") || tmpAssos.contains(" OR ")) {
			StringUtils.addAllNonEmpty(subAssos, Arrays.asList(tmpAssos.split("(?i) or ")));

			for (String s : subAssos) {
				geneAssociation.addAll(this.computeGeneAssociation(s, flxRxn));
			}

		} else if (tmpAssos.contains(" and ") || tmpAssos.contains(" And ") || tmpAssos.contains(" AND ")) {
			StringUtils.addAllNonEmpty(subAssos, Arrays.asList(tmpAssos.split("(?i) and ")));
			// foreach items in "and" block
			for (String s : subAssos) {

				for (GeneSet x : this.computeGeneAssociation(s, flxRxn)) {
					if (geneAssociation.isEmpty()) {
						geneAssociation.add(x);
					} else {
						for (GeneSet y : geneAssociation) {
							y.addAll(x);
						}
					}
				}

			}
		} else {
			tmpAssos = tmpAssos.replaceAll(" ", "");
			if (!tmpAssos.isEmpty()) {
				GeneSet x = new GeneSet();

				BioGene g = this.bionetwork.getGenesView().get(tmpAssos);
				if (g == null) {
					g = new BioGene(tmpAssos);
					this.bionetwork.add(g);
				}
				x.add(g);
				geneAssociation.add(x);
			} else {
				for (String s : subAssos) {
					geneAssociation.addAll(this.computeGeneAssociation(s, flxRxn));
				}
			}
		}

		return geneAssociation;
	}

	/**
	 * Set all patterns to their default values using the defined static fields
	 */
	public void setDefaultPaterns() {
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
	 * @return the pathwaySep
	 */
	public String getPathwaySep() {
		return pathwaySep;
	}

	/**
	 * @param pathwaySep the pathwaySep to set
	 */
	public void setPathwaySep(String pathwaySep) {
		this.pathwaySep = pathwaySep;
	}

	/**
	 * @return the pathwayPattern
	 */
	public String getPathwayPattern() {
		return pathwayPattern;
	}

	/**
	 * @param pathwayPattern the pathwayPattern to set
	 */
	public void setPathwayPattern(String pathwayPattern) {
		this.pathwayPattern = pathwayPattern;
	}

	/**
	 * @return the eCPattern
	 */
	public String getECPattern() {
		return ECPattern;
	}

	/**
	 * @param eCPattern the eCPattern to set
	 */
	public void setECPattern(String eCPattern) {
		ECPattern = eCPattern;
	}

	/**
	 * @return the gPRPattern
	 */
	public String getGPRPattern() {
		return GPRPattern;
	}

	/**
	 * @param gPRPattern the gPRPattern to set
	 */
	public void setGPRPattern(String gPRPattern) {
		GPRPattern = gPRPattern;
	}

	/**
	 * @return the scorePattern
	 */
	public String getScorePattern() {
		return scorePattern;
	}

	/**
	 * @param scorePattern the scorePattern to set
	 */
	public void setScorePattern(String scorePattern) {
		this.scorePattern = scorePattern;
	}

	/**
	 * @return the statusPattern
	 */
	public String getStatusPattern() {
		return statusPattern;
	}

	/**
	 * @param statusPattern the statusPattern to set
	 */
	public void setStatusPattern(String statusPattern) {
		this.statusPattern = statusPattern;
	}

	/**
	 * @return the commentPattern
	 */
	public String getCommentPattern() {
		return commentPattern;
	}

	/**
	 * @param commentPattern the commentPattern to set
	 */
	public void setCommentPattern(String commentPattern) {
		this.commentPattern = commentPattern;
	}

	/**
	 * @return the pmidPattern
	 */
	public String getPmidPattern() {
		return pmidPattern;
	}

	/**
	 * @param pmidPattern the pmidPattern to set
	 */
	public void setPmidPattern(String pmidPattern) {
		this.pmidPattern = pmidPattern;
	}

	/**
	 * @return the chargePattern
	 */
	public String getChargePattern() {
		return chargePattern;
	}

	/**
	 * @param chargePattern the chargePattern to set
	 */
	public void setChargePattern(String chargePattern) {
		this.chargePattern = chargePattern;
	}

	/**
	 * @return the formulaPattern
	 */
	public String getFormulaPattern() {
		return formulaPattern;
	}

	/**
	 * @param formulaPattern the formulaPattern to set
	 */
	public void setFormulaPattern(String formulaPattern) {
		this.formulaPattern = formulaPattern;
	}

	/**
	 * @return the separator
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * @param separator the separator to set
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * @return the othersAsRefs
	 */
	public boolean isOthersAsRefs() {
		return othersAsRefs;
	}

	/**
	 * @param othersAsRefs the othersAsRefs to set
	 */
	public void setOthersAsRefs(boolean othersAsRefs) {
		this.othersAsRefs = othersAsRefs;
	}

	public String getInchiPattern() {
		return inchiPattern;
	}

	public void setInchiPattern(String inchiPattern) {
		this.inchiPattern = inchiPattern;
	}

	public String getSmilesPattern() {
		return smilesPattern;
	}

	public void setSmilesPattern(String smilesPattern) {
		this.smilesPattern = smilesPattern;
	}

}
