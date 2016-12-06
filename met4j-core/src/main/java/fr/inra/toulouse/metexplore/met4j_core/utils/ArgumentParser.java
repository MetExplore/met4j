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
/**
 * 
 */
package fr.inra.toulouse.metexplore.met4j_core.utils;

/**
 * @author Whit Stockwell, adapted by Ludo COTTRET
 * 
 */
import java.util.*;
public class ArgumentParser {
    public ArgumentParser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-") || args[i].startsWith("/")) {
                int loc = args[i].indexOf("=");
                String key = (loc > 0) ? args[i].substring(1, loc) :
args[i].substring(1);
                String value = (loc > 0) ? args[i].substring(loc+1) :
"";
                options.put(key, value);
            }
            else {
                params.addElement(args[i]);
            }
        }
    }

    public boolean hasOption(String opt) {
        return options.containsKey(opt);
    }

    public String getOption(String opt) {
        return (String) options.get(opt);
    }

    public String nextParam() {
        if (paramIndex < params.size()) {
            return (String) params.elementAt(paramIndex++);
        }
        return null;
    }
    private Vector params = new Vector();
    private Hashtable options = new Hashtable();
    private int paramIndex = 0;
}

