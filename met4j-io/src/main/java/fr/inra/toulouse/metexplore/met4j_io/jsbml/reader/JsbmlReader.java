package fr.inra.toulouse.metexplore.met4j_io.jsbml.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLError;
import org.sbml.jsbml.SBMLError.SEVERITY;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.validator.SBMLValidator;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.AnnotationParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBC2Parser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inra.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;

/**
 * The main reader class. It uses the correct {@link JsbmlToBioNetwork} class
 * depending on the SBML level defined in the file
 * 
 * @author Benjamin
 * @since 3.0
 */
public class JsbmlReader {

	/**
	 * The SBML filename
	 */
	private String filename;

	/**
	 * The SBML Model retrieved through jsbml's
	 */
	private Model model;

	/**
	 * The Converter used on {@link #model}
	 */
	public JsbmlToBioNetwork converter;
	/**
	 * Attribute that specifies if the input sbml is valid or not
	 */
	private boolean validSBML = true;

	/**
	 * The list of errors and/or warnings found by jsbml while parsing the SBML
	 * File
	 */
	public ArrayList<String> errorsAndWarnings = new ArrayList<String>();

	/**
	 * Set to true to use SBML online validator
	 */
	private boolean useValidator = true;

	/**
	 * A test main method
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		// String
		String inputFile = args[0];
		
		JsbmlReader reader = new JsbmlReader(inputFile, false);

		HashSet<PackageParser> pkgs = new HashSet<PackageParser>(Arrays.asList(
				new NotesParser(true), new FBC2Parser(), new AnnotationParser(
						true)));
		BioNetwork net = reader.read(pkgs);
		if (net == null) {
			for (String err : reader.errorsAndWarnings) {
				System.err.println(err);
			}
		} else {

			for (String e : reader.errorsAndWarnings) {
				System.err.println(e);
			}
		}
	}

	/**
	 * Constructor
	 * 
	 * @param filename
	 *            the filename
	 */
	public JsbmlReader(String filename) {
		this.filename = filename;
	}

	/**
	 * Constructor
	 * 
	 * @param filename
	 *            the filename
	 * @param useValidator
	 *            set the {@link #useValidator} attribute
	 */
	public JsbmlReader(String filename, boolean useValidator) {
		this.filename = filename;
		this.useValidator = useValidator;
	}

	/**
	 * 
	 * @param userEnabledPackages
	 *            A set of user defined packages to use on this sbml file.
	 *            However, if the a package requested by a user is not supported
	 *            by the sbml level and/or version of the file, it will not be
	 *            used.
	 * @return the created Bionetwork
	 */
	public BioNetwork read(HashSet<PackageParser> userEnabledPackages) {
		try {
			this.initiateModel();
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
			this.setValidSBML(false);
		}

		if (this.isValidSBML()) {

			System.err.println("Verifying enabled Plugins...");
			ArrayList<PackageParser> verifiedPkgs = this
					.verifyPackages(userEnabledPackages);

			JsbmlToBioNetwork converter = new JsbmlToBioNetwork(this.getModel());

			this.setConverter(converter);

			try {
				converter.setPackages(verifiedPkgs);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}

			System.err.println("Parsing model " + this.getModel().getId());

			this.getConverter().parseModel();

			this.errorsAndWarnings.addAll(PackageParser.errorsAndWarnings);

			return this.getConverter().getBionet();
		} else {

			return null;
		}
	}

	/**
	 * Verifies the Set of user defined packages and orders them
	 * 
	 * @param userEnabledPackages
	 *            the packages enabled by the user
	 * @return the ordered list of packages
	 * @see parsebionet.io.jsbml.dataTags
	 */
	private ArrayList<PackageParser> verifyPackages(
			HashSet<PackageParser> userEnabledPackages) {

		ArrayList<PackageParser> start = new ArrayList<PackageParser>();
		ArrayList<PackageParser> end = new ArrayList<PackageParser>();

		if (userEnabledPackages == null) {
			System.err.println("No user package defined");
			return start;
		}

		for (PackageParser parser : userEnabledPackages) {

			if (parser.isPackageUseableOnModel(this.getModel())) {

				if (parser instanceof PrimaryDataTag) {
					start.add(parser);
				} else if (parser instanceof AdditionalDataTag) {
					end.add(parser);
				}
			}
		}
		start.addAll(end);

		return start;
	}

	/**
	 * Instantiate JSBML Model Class and send it to the converter with the
	 * activated sbml packages
	 * 
	 * @throws IOException
	 *             if the file does not exist or cannot be read. Thrown by
	 *             SBMLReader.read(..) method
	 * @throws XMLStreamException
	 *             if the XML is not formed properly. Thrown by
	 *             SBMLReader.read(..) method
	 */
	private void initiateModel() throws IOException, XMLStreamException {
		File sbmlFile = new File(this.getFilename());

		SBMLDocument doc = SBMLReader.read(sbmlFile);

		if (this.useValidator) {
			System.err.println("Validating Input SBML..");
			this.setValidSBML(this.validateSBML(doc));

		} else {
			System.err.println("Validator disabled by user.");
		}

		this.setModel(doc.getModel());

	}

	/**
	 * Validates the SBML document using the online validator
	 * 
	 * @param doc
	 *            the SBMLDocument object
	 * @return true if SBMLValidator returns no errors, false otherwise. This
	 *         method returns true if the Validator only returns warnings
	 */
	public boolean validateSBML(SBMLDocument doc) {

		// TODO test if the validator is working properly before checking
		// consistency.

		doc.setConsistencyChecks(
				SBMLValidator.CHECK_CATEGORY.GENERAL_CONSISTENCY, true);
		doc.setConsistencyChecks(
				SBMLValidator.CHECK_CATEGORY.IDENTIFIER_CONSISTENCY, true);
		doc.setConsistencyChecks(
				SBMLValidator.CHECK_CATEGORY.UNITS_CONSISTENCY, false);
		doc.setConsistencyChecks(SBMLValidator.CHECK_CATEGORY.SBO_CONSISTENCY,
				false);
		doc.setConsistencyChecks(
				SBMLValidator.CHECK_CATEGORY.MATHML_CONSISTENCY, true);
		doc.setConsistencyChecks(
				SBMLValidator.CHECK_CATEGORY.OVERDETERMINED_MODEL, true);
		doc.setConsistencyChecks(
				SBMLValidator.CHECK_CATEGORY.MODELING_PRACTICE, false);

		// Online validator
		Integer code = doc.checkConsistency();

		if (code > 0) {

			HashMap<Integer, String> parsedErrors = new HashMap<Integer, String>();

			for (SBMLError err : doc.getErrorLog().getValidationErrors()) {

				StringBuilder sb = new StringBuilder();

				if (parsedErrors.containsKey(err.getCode())) {

					sb.append(parsedErrors.get(err.getCode())).append(", ")
							.append(err.getLine()).toString();

				} else {

					sb.append("SBML ").append(err.getSeverity()).append(" #")
							.append(err.getCode()).append(" on ");
					sb.append(err.getCategory()).append(":");
					sb.append(err.getShortMessage().getMessage()).append("\n")
							.append("Line(s): ").append(err.getLine());

				}
				String newMessage = sb.toString();
				parsedErrors.put(err.getCode(), newMessage);

			}

			errorsAndWarnings.addAll(parsedErrors.values());

			if (doc.getErrorLog().getNumFailsWithSeverity(SEVERITY.ERROR) != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
	 * @return the converter
	 */
	public JsbmlToBioNetwork getConverter() {
		return converter;
	}

	/**
	 * @param converter
	 *            the converter to set
	 */
	public void setConverter(JsbmlToBioNetwork converter) {
		this.converter = converter;
	}

	/**
	 * @return the validSBML
	 */
	public boolean isValidSBML() {
		return validSBML;
	}

	/**
	 * @param validSBML
	 *            the validSBML to set
	 */
	public void setValidSBML(boolean validSBML) {
		this.validSBML = validSBML;
	}

	/**
	 * @return the errorsAndWarnings
	 */
	public ArrayList<String> getErrorsAndWarnings() {
		return errorsAndWarnings;
	}

	/**
	 * @param errorsAndWarnings
	 *            the errorsAndWarnings to set
	 */
	public void setErrorsAndWarnings(ArrayList<String> errorsAndWarnings) {
		this.errorsAndWarnings = errorsAndWarnings;
	}

	/**
	 * @return the useValidator
	 */
	public boolean isUseValidator() {
		return useValidator;
	}

	/**
	 * @param useValidator
	 *            the useValidator to set
	 */
	public void setUseValidator(boolean useValidator) {
		this.useValidator = useValidator;
	}

}