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

package fr.inrae.toulouse.metexplore.met4j_toolbox.convert.sbml;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.AnnotationWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.GroupPathwayWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.NotesWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.PackageWriter;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.Met4jApplication;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.HashSet;

public class FbcToNotes extends Met4jApplication {

    @Option(name = "-i", usage = "input file", required = true)
    public String inputPath = null;

    @Option(name = "-o", usage = "output file", required = true)
    public String outputPath = null;

    public static void main(String[] args) throws IOException, Met4jSbmlReaderException {

        FbcToNotes f = new FbcToNotes();

        CmdLineParser parser = new CmdLineParser(f);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Error in arguments");
            parser.printUsage(System.err);
            System.exit(0);
        }

        f.run();

    }

    private void run() throws IOException, Met4jSbmlReaderException {

        JsbmlReader reader = new JsbmlReader(this.inputPath);

        BioNetwork network = reader.read();

        JsbmlWriter writer = new JsbmlWriter(this.outputPath, network, 3, 1, false );

        HashSet<PackageWriter> pkgs = new HashSet();
        pkgs.add(new AnnotationWriter());
        pkgs.add(new GroupPathwayWriter());
        pkgs.add(new NotesWriter(false));

        writer.write(pkgs);

    }


    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getDescription() {
        return "Converts FBC package annotations to sbml notes";
    }



}