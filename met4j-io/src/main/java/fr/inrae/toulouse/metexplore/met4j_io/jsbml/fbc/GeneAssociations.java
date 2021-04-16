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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc;

/**
 * <p>GeneAssociations class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class GeneAssociations {

    /**
     * Merge two gene associations (AND relation)
     *
     * @param ga1 a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} object.
     * @param ga2 a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} object.
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} object.
     */
    public static GeneAssociation merge(GeneAssociation ga1, GeneAssociation ga2)
    {
        GeneAssociation newGa = new GeneAssociation();
        for(GeneSet gs1 : ga1)
        {
            for(GeneSet gs2 : ga2)
            {
                    GeneSet newGs = new GeneSet();
                    newGs.addAll(gs1);
                    newGs.addAll(gs2);
                    newGa.add(newGs);
            }
        }

        return  newGa;
    }

    /**
     * Merge several gene assocations
     *
     * @param geneAssociations a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} object.
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_io.jsbml.fbc.GeneAssociation} object.
     */
    public static GeneAssociation merge(GeneAssociation... geneAssociations)
    {
        GeneAssociation newGa = new GeneAssociation();

        if(geneAssociations != null && geneAssociations.length != 0) {
            if (geneAssociations.length == 1) {
                newGa.addAll(geneAssociations[0]);
            } else {
                GeneAssociation ga1 = geneAssociations[0];
                newGa = merge(ga1);
                for (int i = 1; i < geneAssociations.length; i++) {
                    newGa = merge(newGa, geneAssociations[i]);
                }
            }
        }
        return newGa;

    }


}
