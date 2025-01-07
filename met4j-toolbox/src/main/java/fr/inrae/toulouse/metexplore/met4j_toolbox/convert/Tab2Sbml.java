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
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.JsbmlWriter;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException;
import fr.inrae.toulouse.metexplore.met4j_io.tabulated.network.Tab2BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.IOUtils;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.Set;

/**
 * <p>Tab2Sbml class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class Tab2Sbml extends AbstractMet4jApplication {

    @ParameterType(name = EnumParameterTypes.Integer)
    @Option(name = "-ci", usage = "[1] number of the column where are the reaction ids")
    public int colid = 1;

    @ParameterType(name = EnumParameterTypes.Integer)
    @Option(name = "-cf", usage = "[2] number of the column where are the reaction formulas")
    public int colformula = 2;

    @Option(name = "-rp", usage = "[deactivated] format the reaction ids in a Palsson way (R_***)")
    public Boolean rp = false;

    @Option(name = "-mp", usage = "[deactivated] format the metabolite ids in a Palsson way (M_***_c)")
    public Boolean mp = false;

    @Option(name = "-e", usage = "[_b] flag to assign metabolite as external")
    public String e = "_b";

    @Option(name = "-irr", usage = "[-->] String for irreversible reaction")
    public String i = "-->";

    @Option(name = "-rev", usage = "[<==>] String for reversible reaction")
    public String r = "<==>";

    @Format(name = EnumFormats.Sbml)
    @ParameterType(name = EnumParameterTypes.OutputFile)
    @Option(name = "-o", usage = "[out.sbml] Out sbml file")
    public String sbml = "out.sbml";

    @Format(name = EnumFormats.Tsv)
    @ParameterType(name = EnumParameterTypes.InputFile)
    @Option(name = "-i", usage = "Tabulated file", required = true)
    public String in;

    @Option(name = "-id", usage = "[NA] Model id written in the SBML file")
    public String id = "NA";

    @Option(name = "-cpt", usage = "[deactivated] Create compartment from metabolite suffixes. If this option is deactivated, only one compartment (the default compartment) will be created")
    public Boolean createCompartment = false;

    @Option(name = "-dcpt", usage = "[c] Default compartment")
    public String defaultCompartment = "c";


    @ParameterType(name = EnumParameterTypes.Integer)
    @Option(name = "-n", usage = "[0] Number of lines to skip at the beginning of the tabulated file")
    public int nSkip = 0;

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
        return "Create a Sbml File from a tabulated file that contains the reaction ids and the formulas";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException
     * @throws fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.Met4jSbmlWriterException if any.
     */
    public static void main(String[] args) throws IOException, Met4jSbmlWriterException {

        Tab2Sbml ts = new Tab2Sbml();

        ts.parseArguments(args);

        ts.run();

    }

    private void run() {

        Tab2BioNetwork tb = new Tab2BioNetwork(this.id, this.colid - 1,
                this.colformula - 1,
                this.rp, this.mp, this.e, this.i, this.r, this.createCompartment,
                this.defaultCompartment, this.nSkip);


        String fileIn = this.in;
        String sbmlFile = this.sbml;

        Boolean flag = true;
        try {
            flag = tb.createReactionsFromFile(fileIn);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in creating the network from " + fileIn);
            System.exit(1);
        }

        if (!flag) {
            System.err.println("Error in creating the network from " + fileIn);
            System.exit(1);
        }

        BioNetwork bn = tb.getBioNetwork();

        IOUtils.writeSbml(bn, sbmlFile);

        return;

    }


}



