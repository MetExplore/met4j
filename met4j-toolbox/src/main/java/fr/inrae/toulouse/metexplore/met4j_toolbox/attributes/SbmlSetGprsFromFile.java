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
import fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes.SetGprsFromFile;
import org.kohsuke.args4j.Option;

import java.io.IOException;

/**
 * <p>SbmlSetGprsFromFile class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class SbmlSetGprsFromFile  extends AbstractSbmlSetReaction {


    @Option(name="-cgpr", usage="[2] number of the column where are the gprs")
    private int colgpr=2;

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    /** {@inheritDoc} */
    @Override
    public String getLongDescription() {
        return this.getShortDescription()+"\n" +
                this.setDescription +"\n" +
                "GPR must be written in a cobra way in the tabulated file as described in Schellenberger et al 2011 Nature Protocols 6(9):1290-307\n"+
                "(The GPR will be written in the SBML file in two locations:\n" +
                "- in the reaction notes <p>GENE_ASSOCIATION: ( XC_0401 ) OR ( XC_3282 )</p>" +"\n" +
                "- as fbc gene product association :" +
                "       <fbc:geneProductAssociation>\n" +
                "          <fbc:or>\n" +
                "            <fbc:geneProductRef fbc:geneProduct=\"XC_3282\"/>\n" +
                "            <fbc:geneProductRef fbc:geneProduct=\"XC_0401\"/>\n" +
                "          </fbc:or>\n" +
                "        </fbc:geneProductAssociation>\n";
    }

    /** {@inheritDoc} */
    @Override
    public String getShortDescription() {
        return "Create a new SBML file from an original sbml file and a tabulated file containing reaction ids and Gene association written in a cobra way";
    }

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.io.IOException if any.
     */
    public static void main(String[] args) throws IOException {

        SbmlSetGprsFromFile s = new SbmlSetGprsFromFile();

        s.parseArguments(args);

        s.run();
    }

    private void run() {

        BioNetwork bn = this.readSbml();

        SetGprsFromFile sgff = new SetGprsFromFile(this.colid-1, this.colgpr-1, bn, this.tab, this.c, this.nSkip, this.p, false);

        Boolean flag = true;

        try {
            flag = sgff.setAttributes();
        } catch (Exception e) {
            e.printStackTrace();
            flag=false;
        }

        if(!flag) {
            System.err.println("Error in setting gene associations");
            System.exit(0);
        }

        this.writeSbml(bn);

        System.exit(1);

    }


}
