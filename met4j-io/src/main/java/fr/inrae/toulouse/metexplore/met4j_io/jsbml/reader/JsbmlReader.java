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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.stream.XMLStreamException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLError;
import org.sbml.jsbml.SBMLError.SEVERITY;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.validator.SBMLValidator;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.AnnotationParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.GroupPathwayParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.NotesParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;

/**
 * The main reader class. It uses the correct {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlToBioNetwork} class
 * depending on the SBML level defined in the file
 *
 * To launch with -Dlog4j.configuration="log4jmet4j.properties"
 *
 * @author Benjamin
 * @since 3.0
 * @version $Id: $Id
 */
public class JsbmlReader {

    public Boolean verbose = false;
    /**
     * The Converter used on {@link #model}
     */
    public JsbmlToBioNetwork converter;
    /**
     * The list of errors and/or warnings found by jsbml while parsing the SBML
     * File
     */
    public ArrayList<String> errorsAndWarnings = new ArrayList<String>();
    /**
     * The SBML filename
     */
    private String filename;
    /**
     * The SBML Model retrieved through jsbml's
     */
    private Model model;
    /**
     * Attribute that specifies if the input sbml is valid or not
     */
    private boolean validSBML = true;
    /**
     * Set to true to use SBML online validator
     */
    private boolean useValidator = true;

    private String xml = null;

    /**
     * Constructor
     *
     * @param filename the filename
     * @throws java.io.IOException if any.
     */
    public JsbmlReader(String filename) throws IOException {
        this.filename = filename;
        this.useValidator = false;
    }

    /**
     * Constructor
     *
     * @param filename     the filename
     * @param useValidator set the {@link #useValidator} attribute
     * @throws java.io.IOException if any.
     */
    public JsbmlReader(String filename, boolean useValidator) throws IOException {
        this.filename = filename;
        this.useValidator = useValidator;
    }

    /**
     * <p>Constructor for JsbmlReader.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @throws java.io.IOException if any.
     */
    public JsbmlReader(InputStream inputStream) throws IOException {
        this.xml = this.inputStreamToString(inputStream);
        this.useValidator = false;

    }

    /**
     * <p>Constructor for JsbmlReader.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     * @param useValidator a boolean.
     * @throws java.io.IOException if any.
     */
    public JsbmlReader(InputStream inputStream, boolean useValidator) throws IOException {
        this.xml = this.inputStreamToString(inputStream);
        this.useValidator = useValidator;
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + System.lineSeparator());
        }

        return sb.toString();

    }

    /**
     * A test main method
     *
     * @param args the arguments
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
     * @throws java.io.IOException if any.
     */
    public static void main(String[] args) throws Met4jSbmlReaderException, IOException {
        // String
        String inputFile = args[0];

        System.err.println("Start :" + new Date());

        JsbmlReader reader = new JsbmlReader(inputFile, false);

        ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(Arrays.asList(
                new NotesParser(true), new FBCParser(), new GroupPathwayParser(), new AnnotationParser(
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

        System.err.println(net.getCompartmentsView().size() + " compartments");
        System.err.println(net.getPathwaysView().size() + " pathways");
        System.err.println(net.getReactionsView().size() + " reactions");
        System.err.println(net.getMetabolitesView().size() + " metabolites");
        System.err.println(net.getGenesView().size() + " genes");
        System.err.println(net.getEnzymesView().size() + " enzymes");

//		BioPathway p = this.getConverter().getNetwork().getPathwaysView().get("g1");
//
//		System.err.println(this.getConverter().getNetwork().getReactionsFromPathway(p).size());


        System.err.println("End :" + new Date());

    }

    /**
     * <p>read.</p>
     *
     * @param userEnabledPackages A set of user defined packages to use on this sbml file.
     *                            However, if the a package requested by a user is not supported
     *                            by the sbml level and/or version of the file, it will not be
     *                            used.
     * @return the created Bionetwork
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
     */
    public BioNetwork read(ArrayList<PackageParser> userEnabledPackages) throws Met4jSbmlReaderException {
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

            System.err.println("End Parsing model " + this.getModel().getId());

            this.errorsAndWarnings.addAll(PackageParser.errorsAndWarnings);

            return this.getConverter().getNetwork();
        } else {

            return null;
        }
    }

    /**
     * Read with all the parsers enabled
     *
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
     */
    public BioNetwork read() throws Met4jSbmlReaderException {
        ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(Arrays.asList(
                new NotesParser(true), new FBCParser(), new GroupPathwayParser(), new AnnotationParser(
                        true)));

        return this.read(pkgs);
    }

    /**
     * <p>readWithoutNotes.</p>
     *
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
     */
    public BioNetwork readWithoutNotes() throws Met4jSbmlReaderException {
        ArrayList<PackageParser> pkgs = new ArrayList<PackageParser>(Arrays.asList( new FBCParser(), new GroupPathwayParser(), new AnnotationParser(
                        true)));

        return this.read(pkgs);
    }



    /**
     * Verifies the Set of user defined packages and orders them
     *
     * @param userEnabledPackages the packages enabled by the user
     * @return the ordered list of packages
     */
    private ArrayList<PackageParser> verifyPackages(
            ArrayList<PackageParser> userEnabledPackages) {

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
     * @throws IOException        if the file does not exist or cannot be read. Thrown by
     *                            SBMLReader.read(..) method
     * @throws XMLStreamException if the XML is not formed properly. Thrown by
     *                            SBMLReader.read(..) method
     */
    private void initiateModel() throws IOException, XMLStreamException {


        SBMLDocument doc;
        if(this.xml == null) {
            File sbmlFile = new File(this.getFilename());
            doc = SBMLReader.read(sbmlFile);
        }
        else {
            doc = SBMLReader.read(xml);
        }

        if (this.useValidator) {
            System.err.println("Validating Input SBML..");
            this.setValidSBML(this.validateSBML(doc));
            System.err.println("Validation of input SBML done : " + this.validSBML);
        }

        this.setModel(doc.getModel());

    }

    /**
     * Validates the SBML document using the online validator
     *
     * @param doc the SBMLDocument object
     * @return true if SBMLValidator returns no errors, false otherwise. This
     * method returns true if the Validator only returns warnings
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
     * <p>Getter for the field <code>filename</code>.</p>
     *
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * <p>Setter for the field <code>filename</code>.</p>
     *
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
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
     * <p>Getter for the field <code>converter</code>.</p>
     *
     * @return the converter
     */
    public JsbmlToBioNetwork getConverter() {
        return converter;
    }

    /**
     * <p>Setter for the field <code>converter</code>.</p>
     *
     * @param converter the converter to set
     */
    public void setConverter(JsbmlToBioNetwork converter) {
        this.converter = converter;
    }

    /**
     * <p>isValidSBML.</p>
     *
     * @return the validSBML
     */
    public boolean isValidSBML() {
        return validSBML;
    }

    /**
     * <p>Setter for the field <code>validSBML</code>.</p>
     *
     * @param validSBML the validSBML to set
     */
    public void setValidSBML(boolean validSBML) {
        this.validSBML = validSBML;
    }

    /**
     * <p>Getter for the field <code>errorsAndWarnings</code>.</p>
     *
     * @return the errorsAndWarnings
     */
    public ArrayList<String> getErrorsAndWarnings() {
        return errorsAndWarnings;
    }

    /**
     * <p>Setter for the field <code>errorsAndWarnings</code>.</p>
     *
     * @param errorsAndWarnings the errorsAndWarnings to set
     */
    public void setErrorsAndWarnings(ArrayList<String> errorsAndWarnings) {
        this.errorsAndWarnings = errorsAndWarnings;
    }

    /**
     * <p>isUseValidator.</p>
     *
     * @return the useValidator
     */
    public boolean isUseValidator() {
        return useValidator;
    }

    /**
     * <p>Setter for the field <code>useValidator</code>.</p>
     *
     * @param useValidator the useValidator to set
     */
    public void setUseValidator(boolean useValidator) {
        this.useValidator = useValidator;
    }

}
