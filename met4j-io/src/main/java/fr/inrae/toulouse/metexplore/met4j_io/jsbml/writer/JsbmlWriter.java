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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.stream.XMLStreamException;

import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

import org.sbml.jsbml.Model;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLError;
import org.sbml.jsbml.SBMLError.SEVERITY;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLWriter;
import org.sbml.jsbml.validator.SBMLValidator;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.AdditionalDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.dataTags.PrimaryDataTag;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.PackageWriter;


/**
 * The main writer class. It uses the correct {@link BionetworkToJsbml} class
 * depending on the SBML level defined by the user
 *
 * @author Benjamin
 * @since 3.0
 */
public class JsbmlWriter {

    /**
     * The {@link BionetworkToJsbml} corresponding to the SBML level selected by
     * the user
     */
    public BionetworkToJsbml converter;
    /**
     * The list of errors and/or warnings found while creating this SBML file
     */
    public ArrayList<String> errorsAndWarnings = new ArrayList<String>();
    /**
     * The output file name
     */
    private String filename;
    /**
     * The output directory:</br> Default to "/tmp/"
     */
    private String outoutDir = "/tmp/";
    /**
     * The input network
     */
    private BioNetwork net;
    /**
     * the created SBML Document
     */
    private SBMLDocument doc;
    /**
     * The Created model
     */
    private Model model;
    /**
     * The default SBML level used by this
     */
    private int level = 3;
    /**
     * Whether or not to use the Online SBML validator
     */
    private boolean useValidator = false;


    /**
     * Constructor
     *
     * @param outputFile   the output filename
     * @param dir          the output directory
     * @param bionet       the bionetwork to convert
     * @param lvl          the level of the SBML
     * @param useValidator whether or not to use the validator
     */
    public JsbmlWriter(String outputFile, String dir, BioNetwork bionet,
                       int lvl, int version, boolean useValidator) {
        this.filename = outputFile;
        this.setOutoutDir(dir);
        this.setNet(bionet);
        this.level = lvl;
        this.useValidator = useValidator;
        this.model = new Model();
        model.setLevel(lvl);
        model.setVersion(version);
    }

    /**
     * One of the main methods. converts the {@link #net} to a jsbml object and
     * write it as a file.</br> Use the user defined set of package to add
     * additional data to output SBML
     *
     * @param pkgs Set of writer package to use
     */
    public void write(HashSet<PackageWriter> pkgs) {
        // System.err.println("Verifying packages...");

        ArrayList<PackageWriter> verifiedPkgs = this.verifyPackages(pkgs);

        System.err.println("Parsing Bionetwork " + this.getNet().getId());

        this.createConverter(verifiedPkgs);

        this.setDoc(this.getConverter().getDoc());

        this.setModel(this.getConverter().parseBioNetwork(this.getNet()));

        this.getDoc().setModel(this.getModel());
        try {

            this.writeDocument();
        } catch (SBMLException | XMLStreamException | IOException
                | ParsingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the converter that fit the user defined SBML level
     *
     * @param verifiedPkgs The list of plugins that will be applied to the converter
     */
    protected void createConverter(ArrayList<PackageWriter> verifiedPkgs) {
        BionetworkToJsbml converter = new BionetworkToJsbml(this.getModel().getLevel(), this.getModel().getVersion());
        try {
            converter.setPackages(verifiedPkgs);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        this.setConverter(converter);

    }

    /**
     * Verifies the Set of user defined packages and orders them
     *
     * @param pkgs the packages enabled by the user
     * @return the ordered list of packages
     */
    protected ArrayList<PackageWriter> verifyPackages(
            HashSet<PackageWriter> pkgs) {
        ArrayList<PackageWriter> start = new ArrayList<PackageWriter>();
        ArrayList<PackageWriter> end = new ArrayList<PackageWriter>();

        if (pkgs == null) {
            return start;
        }

        for (PackageWriter writer : pkgs) {

            if (writer.isPackageUseableOnLvl(this.getLevel())) {
                if (writer instanceof PrimaryDataTag) {
                    start.add(writer);
                } else if (writer instanceof AdditionalDataTag) {
                    end.add(writer);
                }
            }
        }

        start.addAll(end);

        return start;
    }

    /**
     * Validates the SBML document using the online validator
     *
     * @param doc the SBMLDocument object
     * @return true if SBMLValidator returns no errors, false otherwise. This
     * method returns true if the Validator only returns warnings
     */
    protected boolean validateSBML(SBMLDocument doc) {

        // TODO test if the validator is working properly before checking
        // consistency.

        doc.setConsistencyChecks(
                SBMLValidator.CHECK_CATEGORY.UNITS_CONSISTENCY, false);
        doc.setConsistencyChecks(
                SBMLValidator.CHECK_CATEGORY.IDENTIFIER_CONSISTENCY, true);
        doc.setConsistencyChecks(
                SBMLValidator.CHECK_CATEGORY.GENERAL_CONSISTENCY, true);
        doc.setConsistencyChecks(SBMLValidator.CHECK_CATEGORY.SBO_CONSISTENCY,
                false);
        doc.setConsistencyChecks(
                SBMLValidator.CHECK_CATEGORY.MATHML_CONSISTENCY, true);
        doc.setConsistencyChecks(
                SBMLValidator.CHECK_CATEGORY.OVERDETERMINED_MODEL, false);
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
     * Method that effectively writes the SBML document once the model was
     * completed.
     *
     * @throws SBMLException      if any SBML problems prevent to write the SBMLDocument.
     * @throws XMLStreamException if any problems prevent to write the SBMLDocument as XML.
     * @throws IOException        if the file is not writable
     * @throws ParsingException   if the error was found while parsing the XML string
     * @throws ValidityException  if the xml is not valid
     */
    protected void writeDocument() throws SBMLException, XMLStreamException,
            IOException, ValidityException, ParsingException {

        if (this.useValidator && !validateSBML(this.getDoc())) {
            System.err
                    .println("Unable to write File. The sbml Documents has errors.");
        } else {

            SBMLWriter writer = new SBMLWriter();
            writer.setIndentationChar('\t');
            writer.setIndentationCount((short) 1);

            this.prettifyXML(writer.writeSBMLToString(this.getDoc()));
        }

    }

    /**
     * Used to correct the XML indentation
     *
     * @param sbmlFile The sbml file as a String
     * @throws ValidityException if the xml is not valid
     * @throws IOException       if the file is not writable
     * @throws ParsingException  if the error was found while parsing the XML string
     */
    protected void prettifyXML(String sbmlFile) throws ValidityException,
            IOException, ParsingException {

        FileOutputStream out = new FileOutputStream(this.getOutoutDir()
                + this.getFilename());
        Serializer serializer = new Serializer(out);
        serializer.setIndent(2); // or whatever you like
        serializer.write(new Builder().build(sbmlFile, ""));

        serializer.flush();
        out.close();
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * @return the outoutDir
     */
    public String getOutoutDir() {
        return outoutDir;
    }

    /**
     * @param outoutDir the outoutDir to set
     */
    public void setOutoutDir(String outoutDir) {
        this.outoutDir = outoutDir;
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

    /**
     * @return the doc
     */
    public SBMLDocument getDoc() {
        return doc;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(SBMLDocument doc) {
        this.doc = doc;
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
     * @return the converter
     */
    public BionetworkToJsbml getConverter() {
        return converter;
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter(BionetworkToJsbml converter) {
        this.converter = converter;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the useValidator
     */
    public boolean isUseValidator() {
        return useValidator;
    }

    /**
     * @param useValidator the useValidator to set
     */
    public void setUseValidator(boolean useValidator) {
        this.useValidator = useValidator;
    }

    /**
     * @return the errorsAndWarnings
     */
    public ArrayList<String> getErrorsAndWarnings() {
        return errorsAndWarnings;
    }

    /**
     * @param errorsAndWarnings the errorsAndWarnings to set
     */
    public void setErrorsAndWarnings(ArrayList<String> errorsAndWarnings) {
        this.errorsAndWarnings = errorsAndWarnings;
    }

}