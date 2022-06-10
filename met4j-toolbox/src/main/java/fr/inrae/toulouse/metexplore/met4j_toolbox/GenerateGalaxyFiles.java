/*
 * Copyright INRAE (2022)
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
package fr.inrae.toulouse.metexplore.met4j_toolbox;

import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.ResourceURLFilter;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Resources;
import org.apache.commons.lang3.ClassUtils;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GenerateGalaxyFiles extends AbstractMet4jApplication {


    @Option(name = "-o", usage = "output directory where the galaxy wrappers and the tool_conf.xml will be written", required = true)
    public String outputDirectory;

    @Option(name = "-p", usage = "Package type", required = false)
    public GalaxyPackageType packageType = GalaxyPackageType.Singularity;

    @Option(name = "-v", usage = "Met4j version", required = false)
    public String version = "latest";

    public static void main(String[] args) {

        GenerateGalaxyFiles app = new GenerateGalaxyFiles();
        app.parseArguments(args);
        app.run();
    }

    private void run() {

        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document toolConf = documentBuilder.newDocument();

            Element root = toolConf.createElement("toolbox");

            root.setAttribute("monitor", "true");
            toolConf.appendChild(root);


            Element label = toolConf.createElement("label");
            label.setAttribute("id", "met4j");
            label.setAttribute("text", "MET4J");

            root.appendChild(label);

            ResourceURLFilter filter = u -> {

                String path = GenerateGalaxyFiles.class.getPackage().getName()
                        .replace(".", "/");

                String s = u.getFile();
                return s.endsWith(".class") && !s.contains("$")
                        && s.contains(path);
            };


            File rep = new File(this.outputDirectory);

            if (rep.exists() && !rep.isDirectory()) {
                System.err.println(this.outputDirectory + " already exists and it's a file not a directory...");
                System.exit(1);
            }

            if (!rep.exists()) {
                if (!rep.mkdir()) {
                    System.err.println("Impossible to create the directory " + this.outputDirectory);
                    System.exit(1);
                }
            }

            String path = GenerateGalaxyFiles.class.getPackage().getName()
                    .replace(".", "/");

            int n = 0;

            List<URL> sortedUrls = Resources.getResourceURLs(Resources.class, filter).stream().sorted(Comparator.comparing(URL::getPath)).collect(Collectors.toList());

            String toolPackage = "";

            for (URL u : sortedUrls){

                String entry = u.getFile();
                int idx = entry.indexOf(path);

                entry = entry.substring(idx, entry.length() - ".class".length());

                Class<?> myClass = Class.forName(entry.replace('/', '.'));

                List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(myClass);

                if (myClass != this.getClass()
                        && allSuperclasses.contains(AbstractMet4jApplication.class)) {

                    Constructor<?> ctor = myClass.getConstructor();

                    try {
                        Object obj = ctor.newInstance();

                        Class methodArgs[] = new Class[3];
                        methodArgs[0] = String.class;
                        methodArgs[1] = GalaxyPackageType.class;
                        methodArgs[2] = String.class;

                        System.err.println(obj.getClass().getName());

                        Method method = obj.getClass().getMethod("xmlGalaxyWrapper", methodArgs);
                        method.invoke(obj, this.outputDirectory, this.packageType, this.version);

                        Element tool = toolConf.createElement("tool");

                        Method getPackageName = obj.getClass().getMethod("getSimplePackageName");
                        String packageName = (String) getPackageName.invoke(obj);

                        if(! packageName.equals(toolPackage)) {
                            Element section = toolConf.createElement("section");
                            section.setAttribute("id", packageName);
                            section.setAttribute("name", packageName);
                            root.appendChild(section);
                            toolPackage = packageName;
                        }

                        String className = obj.getClass().getSimpleName();

                        tool.setAttribute("file", "met4j/" + packageName + "/" + className + "/" + className + ".xml");

                        root.appendChild(tool);

                        n++;
                    } catch (InstantiationException e) {
                        // It's not a class that can be instantiated
                    }

                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            DOMSource domSource = new DOMSource(toolConf);
            StreamResult streamResult = new StreamResult(new File(outputDirectory + "/tool_conf.xml"));

            transformer.transform(domSource, streamResult);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problem while creating galaxy file tree");
            System.exit(1);
        }


    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "\n" +
                "Creates a directory for each app with inside the galaxy xml wrapper.";
    }

    @Override
    public String getShortDescription() {
        return "Create the galaxy file tree containing met4j-toolbox app wrappers";
    }
}