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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

//import fr.inra.toulouse.metexplore.met4j_core.utils.ResourceURLFilter;
//import fr.inra.toulouse.metexplore.met4j_core.utils.Resources;

/**
 * Main class for parsebionet List all the main classes in applications with
 * their description if available
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class Main {

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
            HashMap<String, Set<String>> packages = new HashMap<>();
            HashMap<String, String> descs = new HashMap<>();

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

                    if (!packages.containsKey(packageName)) {
                        packages.put(packageName, new HashSet<>());
                    }
                    String desc = "";
                    try {
                        desc += myClass.getMethod("getLabel").invoke(obj) + "\n"
                                + myClass.getMethod("getShortDescription").invoke(obj);

                        descs.put(id, desc);
                        packages.get(packageName).add(id);
                    } catch (Exception e) {
                        desc += "No description !\n";
                    }


                } catch (Exception e1) {
                }

            }

            System.out.println("# Met4j-Toolbox\n" +
                    "The applications are classified by package.\n"+
                    "The complete class name must be provided " +
                    "(e.g. fr.inrae.toulouse.metexplore.met4j_toolbox.attributes.SbmlSetChargesFromFile) to launch the app\n" +
                    "Launch the application with the -h parameter to get the list of the parameters and a complete description.\n");
            packages.keySet().stream().sorted().forEach(packageName -> {
                Set<String> classes = packages.get(packageName);
                if (classes.size() > 0) {
                    System.out.println("## Package " + packageName + "\n");

                    classes.stream().sorted().forEach(className -> {
                        String desc = descs.get(className);
                        System.out.println("### " + desc + "\n");
                    });
                    System.out.println("------------------------------------------");
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
