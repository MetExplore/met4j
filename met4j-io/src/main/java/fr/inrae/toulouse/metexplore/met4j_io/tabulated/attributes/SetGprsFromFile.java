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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.gpr.GPR;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.MalformedGeneAssociationStringException;

import java.io.IOException;

public class SetGprsFromFile extends AbstractSetAttributesFromFile {

    /**
     *
     * @param colId number of the column where are the reaction ids
     * @param colAttr number of the column where are the gpr
     * @param bn BioNetwork
     * @param fileIn tabulated file
     * @param c comment string
     * @param nSkip number of lines to skip at the beginning of the file
     * @param p if true, to match the reactions in the sbml file, the reaction ids in the tabulated file are formatted in the palsson way
     */
    public SetGprsFromFile(int colId, int colAttr, BioNetwork bn, String fileIn, String c, int nSkip, Boolean p, Boolean s) {

        super(colId, colAttr, bn, fileIn, c, nSkip, REACTION, p, s);

    }


    /**
     * Test the gpr
     * TODO : to complete
     */
    public Boolean testAttribute(String gpr) {
        return true;
    }

    /**
     * Reads the file and sets the attributes
     * @return
     * @throws IOException
     */
    public Boolean setAttributes() throws IOException {

        Boolean flag = true;

        try {
            flag = this.test();
        } catch (IOException e) {
            return false;
        }

        if(!flag) {
            return false;
        }

        int n = 0;

        BioCollection<BioReaction> reactionsView = this.getNetwork().getReactionsView();

        for(String id : this.getIdAttributeMap().keySet()) {

            BioReaction reaction = reactionsView.get(id);
            n++;

            String gpr = this.getIdAttributeMap().get(id);

            try {
                GPR.createGPRfromString(this.getNetwork(), reaction, gpr);
            } catch (MalformedGeneAssociationStringException e) {
                e.printStackTrace();
                System.err.println("GPR badly formatted line "+n);
            }
        }

        System.err.println(n+" gene associations created");

        return flag;

    }

}
