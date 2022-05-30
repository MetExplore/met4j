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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

public class GenerateJson {


    public static void main(String[] args) {
        try {


            ResourceURLFilter filter = new ResourceURLFilter() {

                public @Override
                boolean accept(URL u) {

                    String path = GenerateJson.class.getPackage().getName()
                            .replace(".", "/");

                    String s = u.getFile();
                    return s.endsWith(".class") && !s.contains("$")
                            && s.contains(path);
                }
            };

            String jsonStr = "{\"status\":\"true\",\"apps\":[";

            String path = GenerateJson.class.getPackage().getName()
                    .replace(".", "/");


            int n = 0;

            for (URL u : Resources.getResourceURLs(Resources.class, filter)) {

                String entry = u.getFile();
                int idx = entry.indexOf(path);

                entry = entry.substring(idx, entry.length() - ".class".length());

                Class<?> myClass = Class.forName(entry.replace('/', '.'));

                List<Class<?>> allSuperclasses = ClassUtils.getAllSuperclasses(myClass);

                if (allSuperclasses.contains(AbstractMet4jApplication.class)) {

                    Constructor<?> ctor = myClass.getConstructor();

                    try {
                        Object obj = ctor.newInstance();

                        Method method = obj.getClass().getMethod("json");
                        if (n > 0) {
                            jsonStr += ",";
                        }
                        jsonStr += method.invoke(obj);
                        n++;
                    } catch (InstantiationException e) {
                        // It's not a class that can be instantiated
                    }

                }
            }

            jsonStr += "]}";

            System.out.println(jsonStr);

        } catch (Exception e) {
            System.out.println("{\"status\":\"false\"}");
            e.printStackTrace();
        }
    }
}
