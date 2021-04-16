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
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_io.metexplorexml.reader.MetexploreXmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.metexplorexml.writer.BioNetworkToMetexploreXml;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import org.kohsuke.args4j.Option;
import org.sbml.jsbml.text.parser.ParseException;

import java.io.IOException;

/**
 * <p>MetExploreXmlToSbml class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class MetExploreXmlToSbml extends AbstractMet4jApplication {

    @Option(name = "-i", usage = "input file", required = true)
    public String inputPath = null;

    @Option(name = "-o", usage = "output file", required = true)
    public String outputPath = null;


    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
     * @throws org.sbml.jsbml.text.parser.ParseException if any.
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException if any.
     */
    public static void main(String[] args) throws IOException, Met4jSbmlReaderException, ParseException, Met4jSbmlWriterException {

        MetExploreXmlToSbml app = new MetExploreXmlToSbml();

        app.parseArguments(args);

        app.run();

    }


    /**
     * <p>run.</p>
     *
     * @throws java.io.IOException if any.
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException if any.
     * @throws org.sbml.jsbml.text.parser.ParseException if any.
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException if any.
     */
    public void run() throws IOException, Met4jSbmlReaderException, ParseException, Met4jSbmlWriterException {

        MetexploreXmlReader reader = new MetexploreXmlReader(this.inputPath);
        reader.read();

        BioNetwork network = reader.getNetwork();

        JsbmlWriter writer = new JsbmlWriter(this.outputPath, network);

        writer.write();

    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Converts a MetExploreXml file to a SBML file";
    }
}
