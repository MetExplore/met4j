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
import fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils;

import java.io.IOException;

/**
 * <p>SetFormulasFromFile class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class SetFormulasFromFile extends AbstractSetAttributesFromFile {

    /**
     * <p>Constructor for SetFormulasFromFile.</p>
     *
     * @param colId a int.
     * @param colAttr a int.
     * @param bn a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     * @param fileIn a {@link java.lang.String} object.
     * @param c a {@link java.lang.String} object.
     * @param nSkip a int.
     * @param p a {@link java.lang.Boolean} object.
     * @param s a {@link java.lang.Boolean} object.
     */
    public SetFormulasFromFile(int colId, int colAttr, BioNetwork bn, String fileIn, String c, int nSkip, Boolean p, Boolean s) {

        super(colId, colAttr, bn, fileIn, c, nSkip, EntityType.METABOLITE, p, s);

    }

    /**
     * {@inheritDoc}
     *
     * Test the name
     */
    public Boolean testAttribute(String formula) {
        if(formula.isEmpty()) {
            return true;
        }

        return StringUtils.checkMetaboliteFormula(formula);
    }

    /**
     * Reads the file and sets the attributes
     *
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean setAttributes() throws IOException {

        Boolean flag = true;

        try {
            flag = this.parseAttributeFile();
        } catch (IOException e) {
            return false;
        }

        if(!flag) {
            return false;
        }

        int n = 0;

        for(String id : this.getIdAttributeMap().keySet()) {

            n++;

            String formula = this.getIdAttributeMap().get(id);

            this.bn.getMetabolite(id).setChemicalFormula(formula);

        }


        System.err.println(n+" attributions");

        return flag;

    }

}
