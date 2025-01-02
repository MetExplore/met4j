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
import fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes.SetPathwaysFromFile;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.utils.Doi;
import org.kohsuke.args4j.Option;

import java.util.Set;

/**
 * <p>SbmlSetPathwaysFromFile class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class SbmlSetPathwaysFromFile extends AbstractSbmlSetReaction {

    @ParameterType(name= EnumParameterTypes.Integer)
    @Option(name="-cp", usage="[2] number of the column where are the pathways")
    public int colp=2;

    @Option(name="-sep", usage="[|] Separator of pathways in the tabulated file")
    public String sep = "|";


    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    /** {@inheritDoc} */
    @Override
    public String getLongDescription() {
        return this.getShortDescription()+"\n" +
                this.setDescription+"\n" +
                "Pathways will be written in the SBML file in two ways:" +
                "- as reaction note (e.g. <p>SUBSYSTEM: purine_biosynthesis</p>)" +
                "- as SBML group:\n" +
                        "<groups:group groups:id=\"purine_biosynthesis\" groups:kind=\"classification\" groups:name=\"purine_biosynthesis\">\n" +
                        " <groups:listOfMembers>\n" +
                        "  <groups:member groups:idRef=\"R_GLUPRT\"/>\n" +
                        "  <groups:member groups:idRef=\"R_RNDR1b\"/>\n...\n";
    }

    /** {@inheritDoc} */
    @Override
    public String getShortDescription() {
        return "Set pathway to reactions in a network from a tabulated file containing the reaction ids and the pathways";
    }

    @Override
    public Set<Doi> getDois() {
        return Set.of();
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {

        SbmlSetPathwaysFromFile app = new SbmlSetPathwaysFromFile();

        app.parseArguments(args);

        app.run();

    }

    private void run() {

        BioNetwork bn = this.readSbml();

        SetPathwaysFromFile sgff = new SetPathwaysFromFile(this.colid-1, this.colp-1, bn, this.tab,
                this.c, this.nSkip, this.p, false, this.sep);

        Boolean flag = true;

        try {
            flag = sgff.setAttributes();
        } catch (Exception e) {
            flag=false;
        }

        if(!flag) {
            System.err.println("Error in "+this.getLabel());
            System.exit(1);
        }


        this.writeSbml(bn);

    }



}
