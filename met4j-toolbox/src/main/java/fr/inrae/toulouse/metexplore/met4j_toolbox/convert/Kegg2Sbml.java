/*
 * Copyright INRAE (2021)
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
import fr.inrae.toulouse.metexplore.met4j_io.kegg.Kegg2BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.*;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class Kegg2Sbml  extends AbstractMet4jApplication  {

    @Option(name="-org", usage="[] Kegg org id. Must be 3 letters (")
    public String org = "";

    @Format(name= Sbml)
    @ParameterType(name = OutputFile)
    @Option(name="-sbml", usage="[out.sbml] Out sbml file")
    public String sbml = "out.sbml";

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription()+"\n" +
                "Errors returned by this program could be due to Kegg API dysfunctions or limitations. Try later if this problem occurs.";
    }

    @Override
    public String getShortDescription() {
        return "Build a SBML file from KEGG organism-specific pathways. Uses Kegg API.";
    }

    public static void main(String[] args) throws Exception {

        Kegg2Sbml app = new Kegg2Sbml();

        app.parseArguments(args);

        app.run();

    }

    private void run() throws Exception {

        Kegg2BioNetwork k = new Kegg2BioNetwork(this.org, "reaction");

        k.createBionetworkFromKegg();

        BioNetwork network = k.getNetwork();

        JsbmlWriter writer = new JsbmlWriter(this.sbml, network);

        writer.write();

    }
}
