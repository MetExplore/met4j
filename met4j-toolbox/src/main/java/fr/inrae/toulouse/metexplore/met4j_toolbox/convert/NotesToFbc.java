/*
 * Copyright INRAE (2025)
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
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.*;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.HashSet;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.*;

public class NotesToFbc extends AbstractMet4jApplication {

    @Format(name= Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "input SBML file", required = true)
    public String inputPath = null;

    @Format(name= Sbml)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "output SBML file", required = true)
    public String outputPath = null;

    public static void main(String[] args) {
        NotesToFbc app = new NotesToFbc();
        CmdLineParser parser = new CmdLineParser(app);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println("Error in arguments");
            parser.printUsage(System.err);
            System.exit(1);
        }

        app.run();
    }

    private void run() {

        BioNetwork network = IOUtils.readSbml(this.inputPath, NOTES, GROUPS, ANNOTATIONS);

        JsbmlWriter writer = new JsbmlWriter(this.outputPath, network, 3, 1, false );

        HashSet<PackageWriter> pkgs = new HashSet<>();
        pkgs.add(new AnnotationWriter());
        pkgs.add(new GroupPathwayWriter());
        pkgs.add(new FBCWriter());

        try {
            writer.write(pkgs);
        } catch (Met4jSbmlWriterException e) {
            System.err.println("Error while writing the SBML file");
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    /** {@inheritDoc} */
    @Override
    public String getLongDescription() {
        return this.getShortDescription() + "" +
                "\n" +
                "Some old SBML files have notes in the html format. This application will convert them to fbc package annotations.\n" +
                "Be careful, the app reads only html notes, the existing fbc annotations will be overwritten.\n";
    }

    /** {@inheritDoc} */
    @Override
    public String getShortDescription() {
        return "Convert sbml html notes to fbc package annotations";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of(new Doi("https://doi.org/10.1515/jib-2017-0082"));
    }

}
