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

package fr.inrae.toulouse.metexplore.met4j_toolbox.convert;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.tabulated.network.BioNetwork2Tab;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.IOException;

/**
 * <p>Sbml2Tab class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class Sbml2Tab extends AbstractMet4jApplication {

    @Option(name = "-i", usage = "[-->] String for irreversible reaction")
    public String i = "-->";

    @Option(name = "-r", usage = "[<==>] String for reversible reaction")
    public String r = "<==>";

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Option(name = "-out", usage = "[out.tsv] Tabulated file")
    public String out = "out.tsv";

    @ParameterType(name = EnumParameterTypes.InputFile)
    @Format(name = EnumFormats.Sbml)
    @Option(name = "-in", usage = "Sbml file", required = true)
    public String in;


    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
     */
    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        Sbml2Tab app = new Sbml2Tab();

        app.parseArguments(args);

        app.run();

    }

    private void run() {

        String fileIn = this.in;

        JsbmlReader reader = new JsbmlReader(fileIn);

        BioNetwork network = null;
        try {
            network = reader.read();
        } catch (Met4jSbmlReaderException e) {
            e.printStackTrace();
            System.err.println("Error while reading the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        BioNetwork2Tab bioNetwork2Tab = new BioNetwork2Tab(network, this.out, this.r, this.i);

        try {
            bioNetwork2Tab.write();
        } catch (IOException e) {
            System.err.println("Error while writing the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLongDescription() {
        return this.getShortDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return "Create a tabulated file from a SBML file";
    }
}
