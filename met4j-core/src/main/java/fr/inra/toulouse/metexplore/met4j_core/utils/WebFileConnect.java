/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/
/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package fr.inra.toulouse.metexplore.met4j_core.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Web/File Connect Utility Class.
 *
 * @author Ethan Cerami.
 */
public class WebFileConnect {

    /**
     * Retrieves the Document from the Specified URL.
     *
     * @param urlStr URL String.
     * @return String Object containing the full Document Content.
     * @throws MalformedURLException URL is Malformed.
     * @throws IOException           Network Error.
     */
    public static String retrieveDocument(String urlStr)
            throws MalformedURLException, IOException {
        URL url = new URL(urlStr);
        BufferedReader in = new BufferedReader
                (new InputStreamReader(url.openStream()));
        return readFile(in);
    }

    /**
     * Retrieves the Document from the Specified File.
     *
     * @param file File Object.
     * @return String Object containing the full Document Content.
     * @throws FileNotFoundException File Not Found.
     * @throws IOException           Read Error.
     */
    public static String retrieveDocument(File file)
            throws FileNotFoundException, IOException {
        BufferedReader in = new BufferedReader
                (new FileReader(file));
        return readFile(in);
    }

    /**
     * Reads a Document from a Buffered Reader.
     */
    private static String readFile(BufferedReader in)
            throws IOException {
        StringBuffer buf = new StringBuffer();
        String str;
        while ((str = in.readLine()) != null) {
            buf.append(str + "\n");
        }
        in.close();
        return buf.toString();
    }
    
}
