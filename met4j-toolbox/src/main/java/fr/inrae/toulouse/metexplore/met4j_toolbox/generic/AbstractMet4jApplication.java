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

package fr.inrae.toulouse.metexplore.met4j_toolbox.generic;

import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.hibernate.validator.constraints.Range;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Abstract AbstractMet4jApplication class.</p>
 *
 * @author lcottret
 */
public abstract class AbstractMet4jApplication {

    private ArrayList<HashMap<String, String>> options;

    @Option(name = "-h", usage = "prints the help", required = false)
    private Boolean h = false;

    public static String getVersion() throws IOException, XmlPullParserException {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        Model model;
        if ((new File("pom.xml")).exists())
            model = reader.read(new FileReader("pom.xml"));
        else
            model = reader.read(
                    new InputStreamReader(
                            AbstractMet4jApplication.class.getResourceAsStream(
                                    "/META-INF/maven/fr.inrae.toulouse.metexplore/met4j-toolbox/pom.xml"
                            )
                    )
            );

        return model.getVersion().replace("-SNAPSHOT", "");
    }

    /**
     * Inits the options from the field annotations
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void initOptions() throws IllegalArgumentException, IllegalAccessException {

        options = new ArrayList<>();

        // Browse each class field
        for (Field f : this.getClass().getFields()) {

            boolean isParameter = Arrays.stream(f.getDeclaredAnnotations()).anyMatch(a -> a instanceof Option);

            if (isParameter) {

                HashMap<String, String> map = new HashMap<>();

                map.put("name", f.getName());


                if (f.getType().isEnum()) {

                    Enum<?> enumValue = (Enum<?>) f.get(this);

                    Object[] possibleValues = enumValue.getDeclaringClass().getEnumConstants();

                    String choices = "";
                    int n = 0;

                    for (Object object : possibleValues) {
                        String choice = object.toString();

                        if (n > 0) {
                            choices += ",";
                        }
                        choices += choice;
                        n++;
                    }

                    map.put("choices", choices);

                    map.put("type", "select");

                } else if (f.getType().getSimpleName().equals("String")) {
                    map.put("type", "text");
                } else if (f.getType().getSimpleName().equals("int")) {
                    map.put("type", "text");
                } else if (f.getType().getSimpleName().equalsIgnoreCase("double")) {
                    map.put("type", "float");
                } else if (f.getType().getSimpleName().equalsIgnoreCase("boolean")) {
                    map.put("type", "boolean");
                } else {
                    map.put("type", f.getType().getSimpleName().toLowerCase());
                }

                String defaultValue = "";
                if (f.get(this) != null) {
                    defaultValue = f.get(this).toString();
                }
                map.put("default", defaultValue);

                for (Annotation a : f.getDeclaredAnnotations()) {
                    if (a instanceof Option option) {

                        map.put("label", option.usage());

                        if (!option.metaVar().equals("")) {
                            map.put("metaVar", option.metaVar());
                        } else {
                            map.put("metaVar", "");
                        }

                        map.put("argument", option.name());

                        String optional = "true";
                        if (option.required()) {
                            optional = "false";
                        }
                        map.put("optional", optional);
                    } else if (a instanceof Range option) {

                        map.put("min", Double.toString(option.min()));
                        map.put("max", Double.toString(option.max()));

                    } else if (a instanceof ParameterType) {
                        String parameterType = ((ParameterType) a).name().toString().toLowerCase();
                        map.put("type", parameterType);
                        if (parameterType.startsWith("output")) {
                            map.put("output", "true");
                        }
                    } else if (a instanceof Format) {
                        map.put("format", ((Format) a).name().toString().toLowerCase());
                    }
                }
                options.add(map);
            }
        }
    }

    /**
     * Prints the description of the application in json format
     *
     * @return a json representing the detailed description of the class and all
     * the parameters available for it's main method
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public String json() throws IllegalArgumentException, IllegalAccessException {

        String json = "";

        this.initOptions();

        JSONArray parameters = new JSONArray();
        parameters.addAll(options);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.getLabel());
        jsonObject.put("description", this.getLongDescription());
        jsonObject.put("short_description", this.getShortDescription());
        jsonObject.put("java_class", this.getClass().getSimpleName());

        String simplePackageName = this.getSimplePackageName();

        jsonObject.put("package", simplePackageName);

        jsonObject.put("parameters", parameters);

        json = jsonObject.toJSONString();

        return json;
    }

    public String getSimplePackageName() {
        String packageName = this.getClass().getPackage().getName();

        String[] tab = packageName.split("\\.");
        String simplePackageName = tab[tab.length - 1];

        return simplePackageName;
    }

    /**
     * Generates an XML wrapper for a Galaxy tool
     *
     * @param outputDirectory the directory where the XML wrapper will be saved
     * @param packageType the type of package (Docker, Singularity, or Conda)
     * @throws ParserConfigurationException if a DocumentBuilder cannot be created
     * @throws XmlPullParserException if an error occurs while parsing the XML
     * @throws IOException if an I/O error occurs
     * @throws IllegalAccessException if the current method does not have access to the definition of the specified class, field, method, or constructor
     * @throws TransformerException if an error occurs during the transformation process
     * @throws SAXException if any parse errors occur
     */
    public void xmlGalaxyWrapper(String outputDirectory, GalaxyPackageType packageType) throws ParserConfigurationException, IOException, IllegalAccessException, TransformerException, SAXException {

        String simplePackageName = this.getSimplePackageName();

        String packageName = this.getClass().getPackageName();

        String className = this.getClass().getSimpleName();

        File wrapperDirectory = new File(outputDirectory + "/" + simplePackageName + "/" + className);

        if (!wrapperDirectory.exists()) {
            wrapperDirectory.mkdirs();
        }

        String fileName = wrapperDirectory.getAbsolutePath() + "/" + className + ".xml";

        File file = new File(fileName);

        Boolean testExists = false;

        Boolean citationExists = false;

        NodeList testList = null;
        NodeList citationList = null;
        Node citationsTag = null;


        if (file.exists()) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(file);

            testList = doc.getElementsByTagName("test");

            if (testList.getLength() > 0) {
                testExists = true;
            }


            citationList = doc.getElementsByTagName("citation");
            NodeList citationsTags = doc.getElementsByTagName("citations");


            if (citationsTags.getLength() == 0) {
                citationsTag = citationsTags.item(0);
            } else {
                citationsTag = doc.createElement("citations");
            }

        }

        this.initOptions();

        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

        Document document = documentBuilder.newDocument();

        Element root = document.createElement("tool");
        root.setAttribute("id", "met4j_" + this.getLabel());
        root.setAttribute("name", this.getLabel());
        root.setAttribute("version", "@TOOL_VERSION@");

        Element description = document.createElement("description");
        description.setTextContent(this.getShortDescription());
        root.appendChild(description);

        Element macros = document.createElement("macros");
        root.appendChild(macros);
        Element importElt = document.createElement("import");
        importElt.setTextContent("macros.xml");
        macros.appendChild(importElt);

        Element expand1 = document.createElement("expand");
        expand1.setAttribute("macro", "bio_tools");
        root.appendChild(expand1);

        Element expand2 = document.createElement("expand");
        expand2.setAttribute("macro", "requirements");
        root.appendChild(expand2);

        Element command = document.createElement("command");
        command.setAttribute("detect_errors", "exit_code");

        String commandText = "";

        if(packageType == GalaxyPackageType.Conda) {
            // remove 'fr.inrae.toulouse.metexplore.met4j_toolbox'
            commandText = "met4j " + packageName.replace("fr.inrae.toulouse.metexplore.met4j_toolbox.", "") + "." + className;
        }
        else {
            commandText = "sh /usr/bin/met4j.sh " + simplePackageName + "." + className;
        }
       /* if(packageType.equals(GalaxyPackageType.Docker)) {
            commandText = "sh /usr/bin/met4j.sh " + packageName + "." + className;
        }
        else if(packageType.equals(GalaxyPackageType.Singularity)) {

        }
*/
        Element inputElements = document.createElement("inputs");
        List<HashMap<String, String>> inputOptions = getInputOptions();
        for (HashMap<String, String> o : inputOptions) {

            Element param = getParamFromOption(document, o);
            inputElements.appendChild(param);

            if (o.get("type").equalsIgnoreCase("boolean")) {
                commandText += " " + "$" + o.get("name") + "\n";
            } else {

                if (o.get("optional").equals("true")) {
                    if (o.get("type").startsWith("input")) {
                        // commandText += "#if str($" + o.get("name") + ") != \"None\":\n";
                        commandText += "#if str($" + o.get("name") + ") != 'None':\n";
                    } else if (o.get("type").equalsIgnoreCase("Integer") || o.get("type").equalsIgnoreCase("Float")) {
                        commandText += "#if str($" + o.get("name") + ") != 'nan':\n";
                    } else {
                        commandText += "#if str($" + o.get("name") + "):\n";
                    }
                }

                commandText += " " + o.get("argument") + " " + "\"$" + o.get("name") + "\"\n";

                if (o.get("optional").equals("true")) {
                    commandText += "#end if\n";
                }

                if (o.get("type").startsWith("input")) {
                    param.setAttribute("type", "data");
                    if (o.containsKey("format"))
                        param.setAttribute("format", o.get("format"));
                    else
                        param.setAttribute("format", "txt");
                } else {
                    param.setAttribute("type", o.get("type"));
                }

                if (o.get("type").equals("text")) {
                    Element sanitizer = document.createElement("sanitizer");
                    sanitizer.setAttribute("invalid_char", "_");
                    Element valid = document.createElement("valid");
                    valid.setAttribute("initial", "string.printable");
                    sanitizer.appendChild(valid);
                    param.appendChild(sanitizer);
                }

                if (o.get("type").equals("select")) {
                    String choices = o.get("choices");
                    String[] tabChoices = choices.split(",");
                    for (int i = 0; i < tabChoices.length; i++) {
                        String choice = tabChoices[i];
                        Element option = document.createElement("option");
                        option.setAttribute("value", choice);
                        if (choice.equals(o.get("default"))) {
                            option.setAttribute("selected", "true");
                        }
                        option.setTextContent(choice);
                        param.appendChild(option);
                    }
                }
            }

        }


        Element outputElements = document.createElement("outputs");
        List<HashMap<String, String>> outputOptions = getOutputOptions();

        for (HashMap<String, String> o : outputOptions) {

            Element param = getParamFromOption(document, o);
            outputElements.appendChild(param);
            commandText += " " + o.get("argument") + " " + "\"$" + o.get("name") + "\"\n";
        }

        Node cDataDescription = document.createCDATASection(commandText);
        command.appendChild(cDataDescription);

        root.appendChild(command);
        root.appendChild(inputElements);
        root.appendChild(outputElements);

        Element tests = document.createElement("tests");
        root.appendChild(tests);

        if (testExists) {
            for (int i = 0; i < testList.getLength(); i++) {
                Node newNode = document.importNode(testList.item(i), true);
                tests.appendChild(newNode);
            }
        }

        Element help = document.createElement("help");
        Node cHelp = document.createCDATASection(this.getLongDescription()+"\n\n@ATTRIBUTION@");
        help.appendChild(cHelp);
        root.appendChild(help);


        Element citations = document.createElement("citations");
        root.appendChild(citations);

        for (Doi doi : this.getDois()) {
            Element citation = document.createElement("citation");
            citation.setAttribute("type", "doi");
            citation.setTextContent(doi.getDoi());
            citations.appendChild(citation);
        }

        document.appendChild(root);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(wrapperDirectory.getAbsolutePath() + "/" + className + ".xml"));

        transformer.transform(domSource, streamResult);

        // remove empty lines
        String filePath = wrapperDirectory.getAbsolutePath() + "/" + className + ".xml";
        List<String> lines = Files.readAllLines(Paths.get(filePath))
                .stream()
                .filter(line -> !line.trim().isEmpty())
                .collect(Collectors.toList());

        Files.write(Paths.get(filePath), lines, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private Element getParamFromOption(Document document, HashMap<String, String> o) {

        Element param;
        if (o.get("type").equals("outputfile")) {
            param = document.createElement("data");
            param.setAttribute("name", o.get("name"));
            param.setAttribute("format", o.get("format"));
        } else {
            param = document.createElement("param");
            param.setAttribute("name", o.get("name"));
            param.setAttribute("label", o.get("label"));
            param.setAttribute("argument", o.get("argument"));
            param.setAttribute("value", o.get("default"));

            if (!o.get("type").equalsIgnoreCase("boolean")) {
                param.setAttribute("optional", o.get("optional"));
            } else {
                param.setAttribute("truevalue", o.get("argument"));
                param.setAttribute("falsevalue", o.get(""));
                param.setAttribute("type", "boolean");
                param.setAttribute("checked", o.get("default"));
            }
        }
        return param;
    }

    private List<HashMap<String, String>> getInputOptions() {
        return this.options.stream().filter(o -> !o.containsKey("type") || !o.get("type").equals("outputfile")).collect(Collectors.toList());
    }

    private List<HashMap<String, String>> getOutputOptions() {
        return this.options.stream().filter(o -> !o.containsKey("type") || o.get("type").equals("outputfile")).collect(Collectors.toList());
    }

    /**
     * <p>getLabel.</p>
     *
     * @return the label
     */
    public abstract String getLabel();

    /**
     * <p>getDescription.</p>
     *
     * @return the description
     */
    public abstract String getLongDescription();

    /**
     * <p>getDescription.</p>
     *
     * @return the description
     */
    public abstract String getShortDescription();

    public abstract Set<Doi> getDois();

    /**
     * <p>printHeader.</p>
     * <p>
     * Prints the label and the long description
     */
    public void printLongHeader() {
        System.out.println(this.getLabel());
        System.out.println(this.getLongDescription());
    }

    /**
     * <p>printHeader.</p>
     * <p>
     * Prints the label and the long description
     */
    public void printShortHeader() {
        System.out.println(this.getLabel());
        System.out.println(this.getShortDescription());
    }

    /**
     * <p>printUsage.</p>
     */
    public void printUsage() {
        CmdLineParser parser = new CmdLineParser(this);
        parser.printUsage(System.out);
    }

    /**
     * <p>parseArguments.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    protected void parseArguments(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            if (!this.h) {
                this.printShortHeader();
                System.err.println("Error in arguments");
                parser.printUsage(System.err);
                System.exit(1);
            } else {
                this.printLongHeader();
                parser.printUsage(System.err);
                System.exit(0);
            }
        }

        if (this.h) {
            this.printLongHeader();
            parser.printUsage(System.err);
            System.exit(0);
        }
    }

    public enum GalaxyPackageType {
        Docker, Singularity, Conda
    }

}
