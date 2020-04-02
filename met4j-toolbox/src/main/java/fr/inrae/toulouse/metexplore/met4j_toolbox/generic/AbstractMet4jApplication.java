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

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public abstract class AbstractMet4jApplication {

    /**
     * @return the label
     */
    public abstract String getLabel();

    /**
     * @return the description
     */
    public abstract String getDescription();

    @Option(name = "-h", usage = "prints the help", required = false)
    private Boolean h = false;

    private void printHeader()
    {
        System.err.println(this.getLabel());
        System.err.println(this.getDescription());
    }


    protected void parseArguments(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            if(this.h == false) {
                this.printHeader();
                System.err.println("Error in arguments");
                parser.printUsage(System.err);
                System.exit(0);
            }
            else {
                this.printHeader();
                parser.printUsage(System.err);
                System.exit(1);
            }
        }

        if(this.h == true)
        {
            this.printHeader();
            parser.printUsage(System.err);
            System.exit(1);
        }
    }

}
