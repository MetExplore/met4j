package parsebionet.io.jsbml.reader.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.UniqueNamedSBase;

import parsebionet.biodata.BioChemicalReaction;
import parsebionet.biodata.BioCompartment;
import parsebionet.biodata.BioEntity;
import parsebionet.biodata.BioGene;
import parsebionet.biodata.BioNetwork;
import parsebionet.biodata.BioPathway;
import parsebionet.biodata.BioPhysicalEntity;
import parsebionet.biodata.BioRef;
import parsebionet.biodata.Comment;
import parsebionet.biodata.Notes;
import parsebionet.biodata.fbc.FluxReaction;
import parsebionet.biodata.fbc.GeneAssociations;
import parsebionet.biodata.fbc.SingleGeneAssociation;
import parsebionet.io.jsbml.dataTags.AdditionalDataTag;
import parsebionet.io.jsbml.errors.MalformedGeneAssociationStringException;
import parsebionet.io.jsbml.reader.plugin.tags.ReaderSBML1Compatible;
import parsebionet.io.jsbml.reader.plugin.tags.ReaderSBML2Compatible;
import parsebionet.io.jsbml.reader.plugin.tags.ReaderSBML3Compatible;
import parsebionet.utils.StringUtils;

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
	public static final String defaultscorePattern = "[> ]+CONFIDENCE.SCORE:\\s*([^<]+)<";
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
	public static final String defaultpmidPattern = "[> ]+PMID:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve metabolite's charge
	 */
	public static final String defaultchargePattern = "[> ]+CHARGE:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve metabolite's chemical formula
	 */
	public static final String defaultformulaPattern = "[> ]+FORMULA:\\s*([^<]+)<";
	/**
	 * The default pattern used to retrieve metabolite's external identifiers
	 */
	public static final String defaultextDBidsPAttern = "[>]+([a-zA-Z\\._0-9 ]+):\\s*([^<]+)<";

	/**
	 * The default separator in Notes values.
	 */
	public static final String defaultseparator = " \\|\\| ";

	/**
	 * The separator for multiple pathways present in a single pathway key.
	 * </br>
	 * </br>
	 * Most of the time, {@link #separator} is equal to "," and because this
	 * character is very often used in pathway names, a second separator had to
	 * be defined
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
	/**
	 * User defined separator in Notes values.
	 */
	public String separator = ",";

	/**
	 * Set this to true if you want unmatched key/value pairs in the Species
	 * notes to be added as supplementary Identifiers
	 */
	public boolean othersAsRefs = true;

	/**
	 * Constructor
	 * 
	 * @param useDefault
	 *            true to use the default patterns when parsing the Notes
	 */
	public NotesParser(boolean useDefault) {
		if (useDefault)
			this.setDefaultPaterns();
	}

	/**
	 * Launch the parsing of Notes. Launches {@link #getSBMLNotes(HashMap)} or
	 * {@link #getSBMLCompartNotes(HashMap)} on the following list of the
	 * bionetwork:
	 * <ul>
	 * <li>{@link BioNetwork#getBiochemicalReactionList()}
	 * <li>{@link BioNetwork#getPhysicalEntityList()}
	 * <li>{@link BioNetwork#getCompartments()}
	 * </ul>
	 * {@link BioCompartment} has a different method because it does not extends
	 * the {@link BioEntity} class
	 */
	@Override
	public void parseModel(Model model, BioNetwork bionetwork) {
		System.err.println("Starting " + this.getAssociatedPackageName() + " plugin...");

		this.setBionetwork(bionetwork);
		this.setModel(model);

		this.getModelData(model, bionetwork);

		this.getSBMLNotes(bionetwork.getBiochemicalReactionList());
		this.getSBMLNotes(bionetwork.getPhysicalEntityList());
		// this.getSBMLNotes(bionetwork.getEnzList());
		this.getSBMLCompartNotes(bionetwork.getCompartments());

		this.getSBMLNotes(bionetwork.getCompartments());

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
	 * @param model
	 *            the sbml model
	 * @param bionet
	 *            the bionetwork
	 */
	public void getModelData(Model model, BioNetwork bionet) {
		try {
			bionet.setModelNotes(new Notes(model.getNotesString()));
		} catch (XMLStreamException e) {
			System.err.println("Error while parsing Model notes");
			e.printStackTrace();
		}

	}

	/**
	 * For each {@link BioEntity} of the list, launches the correct parseNotes
	 * method
	 * 
	 * @param list
	 *            the bionetwork list of {@link BioEntity}
	 */
	private void getSBMLNotes(HashMap<String, ? extends BioEntity> list) {

		for (Entry<String, ? extends BioEntity> entry : list.entrySet()) {
			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(entry.getKey());

			if (entry.getValue().getEntityNotes() == null) {
				try {

					if (sbase != null && sbase.isSetNotes()) {
						entry.getValue().setEntityNotes(new Notes(sbase.getNotesString()));

						switch (entry.getValue().getClass().getSimpleName()) {
						case "BioCompartment":
							this.parseNotes((BioCompartment) entry.getValue());
							break;
						case "BioChemicalReaction":
							this.parseNotes((BioChemicalReaction) entry.getValue());
							break;
						case "BioPhysicalEntity":
							this.parseNotes((BioPhysicalEntity) entry.getValue());
							break;
						}

					}

				} catch (XMLStreamException e) {

					NotesParser.errorsAndWarnings.add("Error while parsing "
							+ entry.getValue().getClass().getSimpleName() + " " + entry.getValue().getId() + " notes");

				}
			}
		}
	}

	/**
	 * For each {@link BioCompartment}, link the {@link #model}'s compartment's
	 * notes to it
	 * 
	 * @param compartments
	 *            the list of {@link BioCompartment}s of the bionetwork
	 */
	private void getSBMLCompartNotes(HashMap<String, BioCompartment> compartments) {
		for (Entry<String, BioCompartment> entry : compartments.entrySet()) {
			UniqueNamedSBase sbase = this.getModel().findUniqueNamedSBase(entry.getKey());

			try {
				if (sbase != null && sbase.isSetNotes()) {
					entry.getValue().setCompartNotes(new Notes(sbase.getNotesString()));
				}
			} catch (XMLStreamException e) {

				NotesParser.errorsAndWarnings.add("Error while parsing " + entry.getValue().getClass().getSimpleName()
						+ " " + entry.getValue().getId() + " notes");

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
	 * @param reaction
	 *            The {@link BioChemicalReaction}
	 */
	private void parseNotes(BioChemicalReaction reaction) {

		String reactionNotes = reaction.getEntityNotes().getXHTMLasString();

		Matcher m;

		if (this.getPathwayPattern() != null
				&& (m = Pattern.compile(this.getPathwayPattern()).matcher(reactionNotes)).find()) {

			String[] pthwList = m.group(1).split(this.getPathwaySep());
			for (String val : pthwList) {
				String value = val.replaceAll("[^\\p{ASCII}]", "");
				if (this.getBionetwork().getPathwayList().containsKey(value)) {
					reaction.addPathway(this.getBionetwork().getPathwayList().get(value));
				} else {
					BioPathway bionetPath = new BioPathway(value, value);
					this.getBionetwork().addPathway(bionetPath);
					reaction.addPathway(bionetPath);
				}
			}
		}

		// get the ec number
		if (this.getECPattern() != null && (m = Pattern.compile(this.getECPattern()).matcher(reactionNotes)).find()
				&& !m.group(1).equalsIgnoreCase("NA")) {
			reaction.setEcNumber(m.group(1));
		} else if (reaction.getEcNumber().isEmpty()) {
			reaction.setEcNumber("NA");
		}

		// get the reaction score
		if (this.getScorePattern() != null
				&& (m = Pattern.compile(this.getScorePattern()).matcher(reactionNotes)).find()) {
			String value = m.group(1);
			reaction.setScore(value);
		}

		// get the reaction status
		if (this.getStatusPattern() != null
				&& (m = Pattern.compile(this.getStatusPattern()).matcher(reactionNotes)).find()) {
			String value = m.group(1);
			reaction.setStatus(value);
		}

		// get the PMIDS
		if (this.getPmidPattern() != null
				&& (m = Pattern.compile(this.getPmidPattern()).matcher(reactionNotes)).find()) {
			String[] Authorlist = m.group(1).split("(" + this.separator + "\\s?)(?=)");
			for (String value : Authorlist) {
				reaction.addPmid(value.trim());
			}
		}

		// get the note/comment field (yes there is a note field in the sbml
		// note element..)
		if (this.getCommentPattern() != null
				&& (m = Pattern.compile(this.getCommentPattern()).matcher(reactionNotes)).find()) {
			String value = m.group(1);
			if (value == "") {
				value = "NA";
			}
			reaction.addComment(new Comment(value, "NA"));
		}

		if (reaction.getEnzList().isEmpty()) {

			if (this.getGPRPattern() != null
					&& (m = Pattern.compile(this.getGPRPattern()).matcher(reactionNotes)).find()) {

				FluxReaction flx = new FluxReaction(reaction);

				flx.setReactionGAs(new GeneAssociations());
				try {
					flx.getReactionGAs().setListOfUniqueGA(this.getGA(m.group(1), flx));
					flx.convertGAtoComplexes(this.bionetwork);
				} catch (MalformedGeneAssociationStringException e) {
					NotesParser.errorsAndWarnings.add(e.getLocalizedMessage());
				}

			}
		}
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
	 *            the metabolite as a {@link BioPhysicalEntity}
	 */
	private void parseNotes(BioPhysicalEntity metabolite) {

		String metaboNotes = metabolite.getEntityNotes().getXHTMLasString();
		// metaboNotes=metaboNotes.replaceAll(">\\s+<", "><");
		Matcher m;

		if (this.getFormulaPattern() != null
				&& (m = Pattern.compile(this.getFormulaPattern()).matcher(metaboNotes)).find()) {
			String value = m.group(1);

			if (!StringUtils.isVoid(value)) {
				if (metabolite.getChemicalFormula() != null && (metabolite.getChemicalFormula().isEmpty()
						|| metabolite.getChemicalFormula().equalsIgnoreCase("NA"))) {
					metabolite.setChemicalFormula(value);
				}

				metaboNotes = metaboNotes.replaceAll(this.getFormulaPattern(), "");
			}
		}

		if (this.getChargePattern() != null
				&& (m = Pattern.compile(this.getChargePattern()).matcher(metaboNotes)).find()) {
			String value = m.group(1);

			if (!StringUtils.isVoid(value)) {
				if (metabolite.getCharge() != null
						&& (metabolite.getCharge().isEmpty() || metabolite.getCharge().equalsIgnoreCase("0"))) {
					metabolite.setCharge(value);
				}

				metaboNotes = metaboNotes.replaceAll(this.getChargePattern(), "");
			}
		}

		String dbName = null;
		String values;

		if (this.isOthersAsRefs()) {

			m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(metaboNotes);

			while (m.find()) {

				dbName = m.group(1);
				values = m.group(2);

				if (StringUtils.isVoid(values)) {
					metaboNotes = metaboNotes.replace(m.group(0), "");
					m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(metaboNotes);
					continue;

				} else if (dbName.equalsIgnoreCase("xmlns")) {

				} else if (dbName.equalsIgnoreCase("INCHI")) {
					if (!metabolite.hasRef("inchi", values)) {
						metabolite.setInchi(values);
						metabolite.addRef(new BioRef("SBML File", "inchi", values, 1));
					}
				} else if (dbName.equalsIgnoreCase("SMILES")) {
					metabolite.setSmiles(values);
				} else if (dbName.equalsIgnoreCase("INCHIKEY") || dbName.equalsIgnoreCase("INCHI KEY")) {
					if (!metabolite.hasRef("inchikey", values)) {
						metabolite.addRef(new BioRef("SBML File", "inchikey", values, 1));
					}
				} else {
					String[] ids = values.split(this.getSeparator());
					for (String value : ids) {
						if (!metabolite.hasRef(dbName.toLowerCase(), value)) {
							metabolite.addRef(new BioRef("SBML File", dbName.toLowerCase(), value, 1));
						}
					}
				}

				metaboNotes = metaboNotes.replace(m.group(0), "");
				m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(metaboNotes);
			}
		}

	}

	private void parseNotes(BioCompartment cpt) {

		String notes = cpt.getEntityNotes().getXHTMLasString();

		String dbName = null;
		String values;

		Matcher m;

		if (this.isOthersAsRefs()) {

			m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);

			while (m.find()) {

				dbName = m.group(1);
				values = m.group(2);

				if (StringUtils.isVoid(values)) {
					notes = notes.replace(m.group(0), "");
					m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);
					continue;

				} else {
					String[] ids = values.split(this.getSeparator());
					for (String value : ids) {
						if (!cpt.hasRef(dbName.toLowerCase(), value)) {
							cpt.addRef(new BioRef("SBML File", dbName.toLowerCase(), value, 1));
						}
					}
				}

				notes = notes.replace(m.group(0), "");
				m = Pattern.compile(NotesParser.defaultextDBidsPAttern).matcher(notes);

			}
		}

	}

	/**
	 * Recursive function that parse gene association logical expression
	 * strings. </br>
	 * This is an adaptation of
	 * {@link FBC2Parser#getGA(org.sbml.jsbml.ext.fbc.Association)} on Strings.
	 * </br>
	 * </br>
	 * Internally this uses {@link StringUtils#findClosingParen(char[], int)} to
	 * split the GPR according to the outer most parenthesis
	 * 
	 * @param assosString
	 *            The full GPR String in the first recursion, an inner part of
	 *            the initial GPR on the following recursions
	 * @param flxRxn
	 *            the flux reaction
	 * @return a list of {@link SingleGeneAssociation}
	 * @throws MalformedGeneAssociationStringException
	 *             when the GPR is malformed, ie when there is a missing
	 *             parenthesis in the string that creates a confusions on the
	 *             AND/OR associations
	 */
	public ArrayList<SingleGeneAssociation> getGA(String assosString, FluxReaction flxRxn)
			throws MalformedGeneAssociationStringException {
		ArrayList<SingleGeneAssociation> list = new ArrayList<SingleGeneAssociation>();

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
				list.addAll(this.getGA(s, flxRxn));
			}

		} else if (tmpAssos.contains(" and ") || tmpAssos.contains(" And ") || tmpAssos.contains(" AND ")) {
			StringUtils.addAllNonEmpty(subAssos, Arrays.asList(tmpAssos.split("(?i) and ")));
			// foreach items in "and" block
			for (String s : subAssos) {
				ArrayList<SingleGeneAssociation> tmplist = new ArrayList<SingleGeneAssociation>();

				for (SingleGeneAssociation x : this.getGA(s, flxRxn)) {
					if (list.isEmpty()) {
						tmplist.add(x);
					} else {
						for (SingleGeneAssociation y : list) {
							tmplist.add(SingleGeneAssociation.concatToNewGA(x, y));
						}
					}
				}
				list = tmplist;

			}
		} else {
			tmpAssos = tmpAssos.replaceAll(" ", "");
			if (!tmpAssos.isEmpty()) {
				SingleGeneAssociation x = new SingleGeneAssociation();

				BioGene g = this.bionetwork.getGeneList().get(tmpAssos);
				if (g == null) {
					g = new BioGene(tmpAssos);
					this.bionetwork.getGeneList().put(tmpAssos, g);
				}
				x.addGene(g);
				list.add(x);
			} else {
				for (String s : subAssos) {
					list.addAll(this.getGA(s, flxRxn));
				}
			}
		}

		return list;
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

		this.setOthersAsRefs(true);
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
	 * @return the pathwaySep
	 */
	public String getPathwaySep() {
		return pathwaySep;
	}

	/**
	 * @param pathwaySep
	 *            the pathwaySep to set
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
	 * @param pathwayPattern
	 *            the pathwayPattern to set
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
	 * @param eCPattern
	 *            the eCPattern to set
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
	 * @param gPRPattern
	 *            the gPRPattern to set
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
	 * @param scorePattern
	 *            the scorePattern to set
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
	 * @param statusPattern
	 *            the statusPattern to set
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
	 * @param commentPattern
	 *            the commentPattern to set
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
	 * @param pmidPattern
	 *            the pmidPattern to set
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
	 * @param chargePattern
	 *            the chargePattern to set
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
	 * @param formulaPattern
	 *            the formulaPattern to set
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
	 * @param separator
	 *            the separator to set
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
	 * @param othersAsRefs
	 *            the othersAsRefs to set
	 */
	public void setOthersAsRefs(boolean othersAsRefs) {
		this.othersAsRefs = othersAsRefs;
	}

	/**
	 * @return the defaultpathwaypattern
	 */
	public static String getDefaultpathwaypattern() {
		return defaultPathwayPattern;
	}

	/**
	 * @return the defaultecpattern
	 */
	public static String getDefaultecpattern() {
		return defaultECPattern;
	}

	/**
	 * @return the defaultgprpattern
	 */
	public static String getDefaultgprpattern() {
		return defaultGPRPattern;
	}

	/**
	 * @return the defaultscorepattern
	 */
	public static String getDefaultscorepattern() {
		return defaultscorePattern;
	}

	/**
	 * @return the defaultstatuspattern
	 */
	public static String getDefaultstatuspattern() {
		return defaultstatusPattern;
	}

	/**
	 * @return the defaultcommentpattern
	 */
	public static String getDefaultcommentpattern() {
		return defaultcommentPattern;
	}

	/**
	 * @return the defaultpmidpattern
	 */
	public static String getDefaultpmidpattern() {
		return defaultpmidPattern;
	}

	/**
	 * @return the defaultchargepattern
	 */
	public static String getDefaultchargepattern() {
		return defaultchargePattern;
	}

	/**
	 * @return the defaultformulapattern
	 */
	public static String getDefaultformulapattern() {
		return defaultformulaPattern;
	}

	/**
	 * @return the defaultextdbidspattern
	 */
	public static String getDefaultextdbidspattern() {
		return defaultextDBidsPAttern;
	}

	/**
	 * @return the defaultseparator
	 */
	public static String getDefaultseparator() {
		return defaultseparator;
	}

}
