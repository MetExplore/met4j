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
package fr.inrae.toulouse.metexplore.met4j_toolbox;

import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.ResourceURLFilter;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Resources;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * generate doc by creating a html table with name and description of each app
 *
 * Be careful, network is needed to crete the references from dois
 *
 * @author cfrainay
 * @version $Id: $Id
 */
public class GenerateDoc {

    private static String mainTitle="met4j-toolbox";

    private static String shortDesc = "**Met4j command-line toolbox for metabolic networks**";

    private static String install="" +
            "```\n" +
            "git clone https://forgemia.inra.fr/metexplore/met4j.git;\n" +
            "cd met4j;\n" +
            "mvn clean install \n" +
            "\n" +
            "cd met4j-toolbox\n" +
            "mvn clean package\n" +
            "```\n" +
            "\n" +
            "## Download executable jar from gitlab registry\n" +
            "\n" +
            "The executable jar is downloadable in the [met4j gitlab registry](https://forgemia.inra.fr/metexplore/met4j/-/packages).\n\n";

    private static String usage="" +
            "The toolbox can be launched using\n" +
            "```\n" +
            "java -jar target/met4j-toolbox-<version>.jar\n" +
            "```\n" +
            "which will list all the contained applications that can be called using\n" +
            "\n" +
            "```\n" +
            "java -cp target/met4j-toolbox-<version>.jar <Package>.<App name> -h\n" +
            "```\n" +
            "\n" +
            "Log4j from jsbml can be very verbose. You can make it silent by adding this command :\n" +
            "\n" +
            "```console\n" +
            "java -Dlog4j.configuration= -cp target/met4j-toolbox-<version>.jar ...\n" +
            "```\n" +
            "\n" +
            "## From singularity\n" +
            "\n" +
            "You need at least [singularity](https://sylabs.io/guides/3.5/user-guide/quick_start.html) v3.5.\n" +
            "\n" +
            "```console\n" +
            "singularity pull met4j-toolbox.sif oras://registry.forgemia.inra.fr/metexplore/met4j/met4j-singularity:latest\n" +
            "```\n" +
            "\n" +
            "If you want a specific version:\n" +
            "\n" +
            "```console\n" +
            "singularity pull met4j-toolbox.sif oras://registry.forgemia.inra.fr/metexplore/met4j/met4j-singularity:x.y.z\n" +
            "```\n" +
            "\n" +
            "If you want the last develop version:\n" +
            "\n" +
            "```console\n" +
            "singularity pull met4j-toolbox.sif oras://registry.forgemia.inra.fr/metexplore/met4j/met4j-singularity:develop\n" +
            "```\n" +
            "\n" +
            "If you want to build by yourself the singularity image:\n" +
            "\n" +
            "```console\n" +
            "cd met4j-toolbox\n" +
            "mvn package\n" +
            "cd ../\n" +
            "singularity build met4j-toolbox.sif met4j.singularity\n" +
            "```\n" +
            "\n" +
            "\n" +
            "This will download a singularity container met4j-toolbox.sif that you can directly launch.\n" +
            "\n" +
            "To list all the apps.\n" +
            "```console\n" +
            "met4j-toolbox.sif \n" +
            "```\n" +
            "\n" +
            "To launch a specific app, prefix its name with the last component of its package name. For instance:\n" +
            "\n" +
            "```console\n" +
            "met4j-toolbox.sif convert.Tab2Sbml -h -in fic.tsv -sbml fic.sbml\n" +
            "```\n" +
            "\n" +
            "By default, singularity does not see the directories that are not descendants of your home directory. To get the directories outside your home directory, you have to specify the SINGULARITY_BIND environment variable.\n" +
            "At least, to get the data in the default reference directory, you have to specify:\n" +
            "In bash:\n" +
            "```console\n" +
            "export SINGULARITY_BIND=/db\n" +
            "```\n" +
            "In csh or in tcsh\n" +
            "```console\n" +
            "setenv SINGULARITY_BIND /db\n" +
            "```\n" +
            "\n" +
            "## From docker\n" +
            "\n" +
            "First install [Docker](https://www.docker.com/).\n" +
            "\n" +
            "Pull the latest met4j image:\n" +
            "\n" +
            "```console\n" +
            "sudo docker pull metexplore/met4j:latest\n" +
            "```\n" +
            "\n" +
            "If you want a specific version:\n" +
            "\n" +
            "```console\n" +
            "sudo docker pull metexplore/met4j:x.y.z\n" +
            "```\n" +
            "\n" +
            "If you want the develop version:\n" +
            "```console\n" +
            "sudo docker pull metexplore/met4j:develop\n" +
            "```\n" +
            "\n" +
            "If you want to build by yourself the docker image:\n" +
            "\n" +
            "```console\n" +
            "cd met4j-toolbox\n" +
            "mvn package\n" +
            "cd ../\n" +
            "sudo docker build -t metexplore/met4j:myversion .\n" +
            "```\n" +
            "\n" +
            "\n" +
            "To list all the apps:\n" +
            "```console\n" +
            "sudo docker run metexplore/met4j:latest met4j.sh\n" +
            "```\n" +
            "\n" +
            "Don't forget to map volumes when you want to process local files.\n" +
            "Example:\n" +
            "\n" +
            "```console\n" +
            "sudo docker run -v /home/lcottret/work:/work \\\n" +
            " metexplore/met4j:latest met4j.sh convert.Sbml2Tab \\\n" +
            " -in /work/toy_model.xml -out /work/toy_model.tsv\n" +
            "```\n" +
            "\n" +
            "If you change the working directory, you have to specify \"sh /usr/bin/met4j.sh\":\n" +
            "\n" +
            "```console\n" +
            "sudo docker run -w /work -v /home/lcottret/work:/work \\\n" +
            " metexplore/met4j:latest sh /usr/bin/met4j.sh convert.Sbml2Tab \\\n" +
            " -in toy_model.xml -out toy_model.tsv\n" +
            "```\n" +
            "\n" +
            "### Galaxy instance\n" +
            "\n" +
            "[Galaxy](https://galaxyproject.org/) wrappers for met4j-toolbox apps are available in the [Galaxy toolshed](https://toolshed.g2.bx.psu.edu/) (master version) and in the [Galaxy test toolsdhed](https://testtoolshed.g2.bx.psu.edu/) (develop version).\n" +
            "Wrappers launch the met4j singularity container, so the server where your Galaxy instance is hosted must have Singularity installed.\n";

    private static StringBuffer getHeader(){
        StringBuffer sb = new StringBuffer();
        sb.append("# "+mainTitle); sb.append("\n");
        sb.append(shortDesc); sb.append("\n");
        sb.append("\n## Installation from source\n");
        sb.append(install);
        sb.append("\n## Usage\n");
        sb.append(usage);
        sb.append("\n## Features\n");
        return sb;
    }

    private static StringBuffer getAppTable(HashMap<String, HashMap<String, String>> apps){
        StringBuffer sb = new StringBuffer();
        apps.keySet().stream().sorted().forEach(packageName -> {
            HashMap<String,String> classes = apps.get(packageName);
            if (classes.size() > 0) {

                sb.append("<table>"); sb.append("\n");
                sb.append("<thead>" +
                        "<tr>" +
                        "<th colspan=\"2\">"+
                        "Package " + packageName +
                        "</th>" +
                        "</tr>" +
                        "</thead>");sb.append("\n");
                sb.append("<tbody>");sb.append("\n");
                classes.keySet().stream().sorted().forEach(className -> {
                    String desc = classes.get(className);
                    sb.append(desc);
                    sb.append("\n");
                });
                sb.append("</tbody>");sb.append("\n");
                sb.append("</table>");sb.append("\n");

            }
        });
        return sb;
    }

    private static String getUsage(Object app){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CmdLineParser parser = new CmdLineParser(app);
        parser.printUsage(baos);
        return baos.toString(java.nio.charset.StandardCharsets.UTF_8);
    }


    public static void main(String[] args) {

        try {

            ResourceURLFilter filter = new ResourceURLFilter() {

                public boolean accept(URL u) {

                    String path = Main.class.getPackage().getName()
                            .replace(".", "/");

                    String s = u.getFile();
                    return s.endsWith(".class") && !s.contains("$")
                            && s.contains(path);
                }
            };

            String path = Main.class.getPackage().getName().replace(".", "/");
            HashMap<String, HashMap<String, String>> apps = new HashMap<>();

            for (URL u : Resources.getResourceURLs(Resources.class, filter)) {

                String entry = u.getFile();

                int idx = entry.indexOf(path);

                entry = entry
                        .substring(idx, entry.length() - ".class".length());

                Class<?> myClass = Class.forName(entry.replace('/', '.'));

                if (AbstractMet4jApplication.class.isAssignableFrom(myClass)) {

                    try {
                        myClass.getMethod("main", String[].class);
                        Constructor<?> ctor = myClass.getConstructor();

                        Object obj = ctor.newInstance();

                        String packageName = myClass.getPackageName();
                        String id = myClass.getCanonicalName();

                        if (!apps.containsKey(packageName)) {
                            apps.put(packageName, new HashMap<>());
                        }
                        String desc = "";
                        try {
                            Object labelo = myClass.getMethod("getLabel").invoke(obj);
                            String label = labelo != null ? (String) labelo : "";
                            Object spo = myClass.getMethod("getShortDescription").invoke(obj);
                            String sp = spo != null ? (String) spo : "";
                            sp = sp.replaceAll("\n", "<br/>");
                            Object lpo = myClass.getMethod("getLongDescription").invoke(obj);
                            String lp = lpo != null ? (String) lpo : "";
                            lp = lp.replaceAll("\n\r?", "<br/>");

                            Set<Doi> dois = (Set<Doi>) myClass.getMethod("getDois").invoke(obj);

                            if (dois.size() > 0) {

                                lp += "<br/><br/>References:<br/>";
                                for (Doi doiInfo : dois) {
                                    lp += "<a href=\"https://doi.org/" + doiInfo.getDoi() + "\">" + doiInfo.getAbbreviatedReference() + "</a><br/>";
                                }
                            }

                            String us = getUsage(obj);
                            desc += "<tr><td>" +
                                    label +
                                    "</td><td>" +
                                    sp + "<details><summary><small>more</small></summary>" +
                                    lp + "<br/><br/>" +
                                    "<pre><code>" + us + "</code></pre>" +
                                    "</details>" +
                                    "</td></tr>";

                            apps.get(packageName).put(id, desc);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.err.println("no description set for " + id);
                        }
                    } catch (Exception e1) {
                        //method not set
                    }
                }

            }

            BufferedWriter w = new BufferedWriter(new FileWriter(args[0]));
            w.write(GenerateDoc.getHeader().toString());
            w.write(GenerateDoc.getAppTable(apps).toString());
            w.flush();
            w.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
