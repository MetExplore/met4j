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

package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * <p>SbmlToMetaboliteTable class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class SbmlToMetaboliteTable extends AbstractMet4jApplication {

    @Option(name = "-s", usage = "Sbml file", required = true)
    protected String sbml;

    @Option(name = "-o", usage = "Output file", required=true)
    protected String outputFile;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        SbmlToMetaboliteTable app = new SbmlToMetaboliteTable();

        app.parseArguments(args);

        app.run();
    }

    /**
     * <p>run.</p>
     */
    public void run() {

        BioNetwork network = this.readSbml();

        try (PrintWriter writer = new PrintWriter(new FileWriter(this.outputFile, false))) {

            writer.println("id\tname");

            for (BioMetabolite metabolite : network.getMetabolitesView()) {
                writer.println(metabolite.getId() + "\t" + metabolite.getName());
            }

        } catch (IOException e) {
            System.err.println("Error while printing metabolites");
        }


    }

    BioNetwork readSbml() {
        JsbmlReader reader = null;
        try {
            reader = new JsbmlReader(this.sbml);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unable to read the sbml file " + this.sbml);
            System.exit(0);
        }

        BioNetwork bn = null;
        try {
            bn = reader.read();
        } catch (Met4jSbmlReaderException e) {
            e.printStackTrace();
            System.err.println("Problem while reading the sbml file " + this.sbml);
            System.exit(0);
        }

        return bn;

    }


    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Returns a tabulated file with metabolite attributes from a SBML file";
    }

}
