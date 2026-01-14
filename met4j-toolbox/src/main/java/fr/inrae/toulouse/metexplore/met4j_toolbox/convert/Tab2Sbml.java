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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 * <p>Tab2Sbml class.</p>
 *
 * @author lcottret
 */
public class Tab2Sbml extends AbstractMet4jApplication {

    @ParameterType(name = EnumParameterTypes.Integer)
    @Option(name = "-ci", usage = "[1] number of the column where are the reaction ids")
    public int colid = 1;

    @ParameterType(name = EnumParameterTypes.Integer)
    @Option(name = "-cf", usage = "[2] number of the column where are the reaction formulas")
    public int colformula = 2;

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

    @Option(name = "-dcpt", usage = "[c] Default compartment")
    public String defaultCompartment = "c";

    @Option(name = "-M_c", usage = "[false] Use Palsson et al. convention: compartment suffix in metabolite ids with _ separator")
    public Boolean usePalssonConvention = false;


    @Option(name = "-b", aliases = {"--boundary"}, usage = "set a compartment as the system boundary. All metabolites in this compartment will have the attribute `boundaryCondition` set to true in the sbml.")
    public String boundaryCompartment = null;

    @ParameterType(name = EnumParameterTypes.Integer)
    @Option(name = "-n", usage = "[0] Number of lines to skip at the beginning of the tabulated file")
    public int nSkip = 0;

    @Option(name = "-ign", aliases = {"--ignore-failed-read"}, usage = "skip lines with parsing errors instead of stopping the process")
    public boolean keepGoing = false;

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
     * @param args an array of {@link String} objects.
     * @throws IOException
     * @throws Met4jSbmlWriterException if any.
     */
    public static void main(String[] args) throws IOException, Met4jSbmlWriterException {

        Tab2Sbml ts = new Tab2Sbml();

        ts.parseArguments(args);

        ts.run();

    }

    private void run() {


        Tab2BioNetwork tb = new Tab2BioNetwork(this.id, this.colid - 1,
                this.colformula - 1,
                 this.i, this.r, null, this.nSkip);
        tb.defaultCompartmentId=this.defaultCompartment;
        if(usePalssonConvention){
            // match reactant formula parts like "2 M_glc__D_c" or "M_atp_m". The first capturing group is the stoichiometric coefficient (optional),
            // the second is the metabolite id with compartment suffix (mandatory), the third is the compartment id (mandatory)
            tb.setReactantParsing("^(\\d+\\.?\\d*)?\\s*(M_\\w+_([^_]+))$",1,2,3);
        }
        if(this.keepGoing){
            tb.setParsingFailure(Tab2BioNetwork.errorHandling.SKIP);
        }

        String fileIn = this.in;
        String sbmlFile = this.sbml;

        BioNetwork bn = new BioNetwork();
        try {
            bn = tb.convert(new BufferedReader(new FileReader(fileIn)));
            if(this.boundaryCompartment!=null){
                BioCompartment compartment = bn.getCompartment(this.boundaryCompartment);
                if(compartment==null){
                    System.err.println("Warning: compartment "+this.boundaryCompartment+" not found in the network. No boundary condition set.");
                } else {
                   for(BioEntity m : compartment.getComponentsView()){
                       if(m instanceof BioMetabolite){
                           MetaboliteAttributes.setBoundaryCondition((BioMetabolite) m, true);
                       }
                   }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in creating the network from " + fileIn);
            System.exit(1);
        }

        IOUtils.writeSbmlWithMulitLocCheck(bn, sbmlFile);

        return;

    }


}



