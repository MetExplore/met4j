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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.GeneSetException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneAssociationsTest {

    private static GeneSet gs1, gs2, gs3, gs4, gs5, gs6, gsA, gsB, gsC, gsD, gsE, gsF, gsG, gsH, gsI;
    private static GeneAssociation ga1, ga2, refMerge;
    private static BioGene G1, G2, G3, G4, G5, G6;

    @Before
    public void init() throws GeneSetException {
        ga1 = new GeneAssociation();
        ga2 = new GeneAssociation();

        G1 = new BioGene("G1");
        G2 = new BioGene("G2");
        G3 = new BioGene("G3");
        G4 = new BioGene("G4");
        G5 = new BioGene("G5");
        G6 = new BioGene("G6");

        gs1 = new GeneSet();
        gs1.add(G1.getId());
        gs1.add(G2.getId());

        gs2 = new GeneSet();
        gs2.add(G3.getId());
        gs2.add(G4.getId());

        gs3 = new GeneSet();
        gs3.add(G5.getId());

        gs4 = new GeneSet();
        gs4.add(G5.getId());
        gs4.add(G3.getId());

        gs5 = new GeneSet();
        gs5.add(G6.getId());
        gs5.add(G2.getId());

        gs6 = new GeneSet();
        gs6.add(G6.getId());
        gs6.add(G1.getId());

        ga1.add(gs1);
        ga1.add(gs2);
        ga1.add(gs3);

        ga2.add(gs4);
        ga2.add(gs5);
        ga2.add(gs6);

        refMerge = new GeneAssociation();
        gsA = new GeneSet();
        gsA.addAll(gs1);
        gsA.addAll(gs4);

        gsB = new GeneSet();
        gsB.addAll(gs1);
        gsB.addAll(gs5);

        gsC = new GeneSet();
        gsC.addAll(gs1);
        gsC.addAll(gs6);

        gsD = new GeneSet();
        gsD.addAll(gs2);
        gsD.addAll(gs4);

        gsE = new GeneSet();
        gsE.addAll(gs2);
        gsE.addAll(gs5);

        gsF = new GeneSet();
        gsF.addAll(gs2);
        gsF.addAll(gs6);

        gsG = new GeneSet();
        gsG.addAll(gs3);
        gsG.addAll(gs4);

        gsH = new GeneSet();
        gsH.addAll(gs3);
        gsH.addAll(gs5);

        gsI = new GeneSet();
        gsI.addAll(gs3);
        gsI.addAll(gs6);
    }


    @Test
    public void mergeGeneAssociations() throws GeneSetException {


        refMerge.add(gsA);
        refMerge.add(gsB);
        refMerge.add(gsC);
        refMerge.add(gsD);
        refMerge.add(gsE);
        refMerge.add(gsF);
        refMerge.add(gsG);
        refMerge.add(gsH);
        refMerge.add(gsI);

        GeneAssociation testMerge = GeneAssociations.merge(ga1, ga2);

        assertEquals(refMerge, testMerge);
    }

    @Test
    public void testMerge3GeneAssociations() throws GeneSetException {

        GeneAssociation ga3 = new GeneAssociation();
        GeneSet gs7 = new GeneSet();
        gs7.add(G2.getId());

        ga3.add(gs7);

        gsA.add(G2.getId());
        gsB.add(G2.getId());
        gsC.add(G2.getId());
        gsD.add(G2.getId());
        gsE.add(G2.getId());
        gsF.add(G2.getId());
        gsG.add(G2.getId());
        gsH.add(G2.getId());
        gsI.add(G2.getId());

        refMerge.add(gsA);
        refMerge.add(gsB);
        refMerge.add(gsC);
        refMerge.add(gsD);
        refMerge.add(gsE);
        refMerge.add(gsF);
        refMerge.add(gsG);
        refMerge.add(gsH);
        refMerge.add(gsI);

        GeneAssociation testMerge = GeneAssociations.merge(ga1, ga2, ga3);
        assertEquals(refMerge, testMerge);

    }
}