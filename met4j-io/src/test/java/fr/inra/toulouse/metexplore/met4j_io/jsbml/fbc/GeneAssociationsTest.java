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

package fr.inra.toulouse.metexplore.met4j_io.jsbml.fbc;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioGene;
import org.junit.Test;

import static org.junit.Assert.*;

public class GeneAssociationsTest {

    @Test
    public void mergeGeneAssociations() {

        GeneAssociation ga1 = new GeneAssociation();
        GeneAssociation ga2 = new GeneAssociation();

        BioGene G1 = new BioGene("G1");
        BioGene G2 = new BioGene("G2");
        BioGene G3 = new BioGene("G3");
        BioGene G4 = new BioGene("G4");
        BioGene G5 = new BioGene("G5");
        BioGene G6 = new BioGene("G6");

        GeneSet gs1 = new GeneSet();
        gs1.add(G1);
        gs1.add(G2);

        GeneSet gs2 = new GeneSet();
        gs2.add(G3);
        gs2.add(G4);

        GeneSet gs3 = new GeneSet();
        gs3.add(G5);

        GeneSet gs4 = new GeneSet();
        gs4.add(G5);
        gs4.add(G3);

        GeneSet gs5 = new GeneSet();
        gs5.add(G6);
        gs5.add(G2);

        GeneSet gs6 = new GeneSet();
        gs6.add(G6);
        gs6.add(G1);

        ga1.add(gs1);
        ga1.add(gs2);
        ga1.add(gs3);

        ga2.add(gs4);
        ga2.add(gs5);
        ga2.add(gs6);

        GeneAssociation refMerge = new GeneAssociation();
        GeneSet gsA = new GeneSet();
        gsA.addAll(gs1);
        gsA.addAll(gs4);

        GeneSet gsB = new GeneSet();
        gsB.addAll(gs1);
        gsB.addAll(gs5);

        GeneSet gsC = new GeneSet();
        gsC.addAll(gs1);
        gsC.addAll(gs6);

        GeneSet gsD = new GeneSet();
        gsD.addAll(gs2);
        gsD.addAll(gs4);

        GeneSet gsE = new GeneSet();
        gsE.addAll(gs2);
        gsE.addAll(gs5);

        GeneSet gsF = new GeneSet();
        gsF.addAll(gs2);
        gsF.addAll(gs6);

        GeneSet gsG = new GeneSet();
        gsG.addAll(gs3);
        gsG.addAll(gs4);

        GeneSet gsH = new GeneSet();
        gsH.addAll(gs3);
        gsH.addAll(gs5);

        GeneSet gsI = new GeneSet();
        gsI.addAll(gs3);
        gsI.addAll(gs6);

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

        // Test 3 geneAssociations

        GeneAssociation ga3 = new GeneAssociation();
        GeneSet gs7 = new GeneSet();
        gs7.add(G2);

        ga3.add(gs7);

        gsA.add(G2);
        gsB.add(G2);
        gsC.add(G2);
        gsD.add(G2);
        gsE.add(G2);
        gsF.add(G2);
        gsG.add(G2);
        gsH.add(G2);
        gsI.add(G2);

        testMerge = GeneAssociations.merge(ga1, ga2, ga3);
        assertEquals(refMerge, testMerge);

    }
}