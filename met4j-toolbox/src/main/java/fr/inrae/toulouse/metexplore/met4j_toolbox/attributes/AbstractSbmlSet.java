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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.*;
import org.kohsuke.args4j.Option;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils.SbmlPackage.*;

/**
 * <p>Abstract AbstractSbmlSet class.</p>
 *
 * @author lcottret
 */
public abstract class AbstractSbmlSet extends AbstractMet4jApplication {

    @Option(name = "-n", usage = "[0] Number of lines to skip at the beginning of the tabulated file")
    public int nSkip = 0;

    @Format(name = Sbml)
    @ParameterType(name = OutputFile)
    @Option(name = "-o", usage = "[out.sbml] SBML output file")
    public String out = "out.sbml";

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-i", usage = "Original SBML file", required = true)
    public String sbml;

    @ParameterType(name = InputFile)
    @Format(name = Tsv)
    @Option(name = "-tab", usage = "Input Tabulated file")
    public String tab;

    @ParameterType(name = EnumParameterTypes.Text)
    @Option(name = "-c", usage = "[#] Comment String in the tabulated file. The lines beginning by this string won't be read")
    public String c = "#";

    /**
     * <p>Constructor for AbstractSbmlSet.</p>
     */
    public AbstractSbmlSet() {
    }

    /**
     * <p>readSbml.</p>
     *
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     */
    protected BioNetwork readSbml() {
        return IOUtils.readSbml(this.sbml, ALL);
    }

    /**
     * <p>writeSbml.</p>
     *
     * @param network a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     */
    protected void writeSbml(BioNetwork network) {
        IOUtils.writeSbml(network, this.out);
    }


}
