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
 * @author cfrainay
 * @version $Id: $Id
 */
public class GenerateDoc {

    private static String mainTitle="met4j-toolbox";

    private static String shortDesc = "**Met4j command-line toolbox for metabolic networks**";

    private static String install="" +
            "```\n" +
            "cd met4j-toolbox\n" +
            "mvn clean compile assembly:single\n" +
            "```\n";

    private static String usage="" +
            "The toolbox can be launched using\n" +
            "```\n" +
            "java -jar met4j-toolbox-<version>-jar-with-dependencies.jar\n" +
            "```\n" +
            "which will list all the contained applications that can be called using\n" +
            "\n" +
            "```\n" +
            "java -cp met4j-toolbox-<version>-jar-with-dependencies.jar <Package>.<App name> -h\n" +
            "```\n";

    private static StringBuffer getHeader(){
        StringBuffer sb = new StringBuffer();
        sb.append("# "+mainTitle); sb.append("\n");
        sb.append(shortDesc); sb.append("\n");
        sb.append("\n## Installation\n");
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

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
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
                        String label = labelo !=null ? (String) labelo : "";
                        Object spo = myClass.getMethod("getShortDescription").invoke(obj);
                        String sp = spo !=null ? (String) spo : "";
                        sp=sp.replaceAll("\n","<br/>");
                        Object lpo = myClass.getMethod("getLongDescription").invoke(obj);
                        String lp = lpo !=null ? (String) lpo : "";
                        lp=lp.replaceAll("\n\r?","<br/>");
                        String us =getUsage(obj);
                        desc += "<tr><td>"+
                                label +
                                "</td><td>" +
                                sp+"<details><summary><small>more</small></summary>" +
                                lp+"<br/><br/>" +
                                "<pre><code>"+us+"</code></pre>" +
                                "</details>"+
                                "</td></tr>";

                        apps.get(packageName).put(id, desc);
                    } catch (Exception e) {
                        System.err.println("no description set for "+id);
                    }
                } catch (Exception e1) {
                    //method not set
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
