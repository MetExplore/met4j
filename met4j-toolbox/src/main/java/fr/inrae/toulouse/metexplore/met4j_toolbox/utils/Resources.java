/*
 * Copyright INRAE (2021)
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

package fr.inrae.toulouse.metexplore.met4j_toolbox.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * <p>Resources class.</p>
 *
 * @author lcottret
 */
public class Resources {
    private static void collectURL(ResourceURLFilter f, Set<URL> s, URL u) {
        if (f == null || f.accept(u)) {
            s.add(u);
        }
    }

    private static void iterateFileSystem(File r, ResourceURLFilter f,
                                          Set<URL> s) throws MalformedURLException, IOException {
        File[] files = r.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                iterateFileSystem(file, f, s);
            } else if (file.isFile()) {
                collectURL(f, s, file.toURI().toURL());
            }
        }
    }

    private static void iterateJarFile(File file, ResourceURLFilter f,
                                       Set<URL> s) throws MalformedURLException, IOException {
        JarFile jFile = new JarFile(file);
        for (Enumeration<JarEntry> je = jFile.entries(); je.hasMoreElements(); ) {
            JarEntry j = je.nextElement();
            if (!j.isDirectory()) {
                collectURL(f, s,
                        new URL("jar", "", file.toURI() + "!/" + j.getName()));
            }
        }
        jFile.close();

    }

    private static void iterateEntry(File p, ResourceURLFilter f, Set<URL> s)
            throws MalformedURLException, IOException {
        if (p.isDirectory()) {
            iterateFileSystem(p, f, s);
        } else if (p.isFile() && p.getName().toLowerCase().endsWith(".jar")) {
            iterateJarFile(p, f, s);
        }
    }

    /**
     * <p>getResourceURLs.</p>
     *
     * @return a {@link java.util.Set} object.
     * @throws java.io.IOException if any.
     * @throws java.net.URISyntaxException if any.
     */
    public static Set<URL> getResourceURLs() throws IOException,
            URISyntaxException {
        return getResourceURLs((ResourceURLFilter) null);
    }

    /**
     * <p>getResourceURLs.</p>
     *
     * @param rootClass a {@link java.lang.Class} object.
     * @return a {@link java.util.Set} object.
     * @throws java.io.IOException if any.
     * @throws java.net.URISyntaxException if any.
     */
    public static Set<URL> getResourceURLs(Class<?> rootClass) throws IOException,
            URISyntaxException {
        return getResourceURLs(rootClass, (ResourceURLFilter) null);
    }

    /**
     * <p>getResourceURLs.</p>
     *
     * @param filter a {@link fr.inrae.toulouse.metexplore.met4j_toolbox.utils.ResourceURLFilter} object.
     * @return a {@link java.util.Set} object.
     * @throws java.io.IOException if any.
     * @throws java.net.URISyntaxException if any.
     */
    public static Set<URL> getResourceURLs(ResourceURLFilter filter)
            throws IOException, URISyntaxException {
        Set<URL> collectedURLs = new HashSet<URL>();
        URLClassLoader ucl = (URLClassLoader) ClassLoader.getSystemClassLoader();
        for (URL url : ucl.getURLs()) {
            iterateEntry(new File(url.toURI()), filter, collectedURLs);
        }
        return collectedURLs;
    }

    /**
     * <p>getResourceURLs.</p>
     *
     * @param rootClass a {@link java.lang.Class} object.
     * @param filter a {@link fr.inrae.toulouse.metexplore.met4j_toolbox.utils.ResourceURLFilter} object.
     * @return a {@link java.util.Set} object.
     * @throws java.io.IOException if any.
     * @throws java.net.URISyntaxException if any.
     */
    public static Set<URL> getResourceURLs(Class<?> rootClass,
                                           ResourceURLFilter filter) throws IOException, URISyntaxException {
        Set<URL> collectedURLs = new HashSet<URL>();
        CodeSource src = rootClass.getProtectionDomain().getCodeSource();
        iterateEntry(new File(src.getLocation().toURI()), filter, collectedURLs);
        return collectedURLs;
    }

}
