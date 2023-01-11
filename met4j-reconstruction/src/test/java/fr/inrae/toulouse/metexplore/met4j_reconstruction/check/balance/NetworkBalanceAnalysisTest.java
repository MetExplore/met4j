/*
 * Copyright INRAE (2022)
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

package fr.inrae.toulouse.metexplore.met4j_reconstruction.check.balance;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NetworkBalanceAnalysisTest {

    public static BioNetwork network;
    public static BioReaction r1, r2, r3;
    public static BioMetabolite s1;
    public static BioMetabolite s2;
    public static BioMetabolite p1;
    public static BioMetabolite p2;
    private static NetworkBalanceAnalysis analysis;

    @BeforeClass
    public static void init() {

        network = new BioNetwork();
        s1 = new BioMetabolite("s1");
        s2 = new BioMetabolite("s2");
        p1 = new BioMetabolite("p1");
        p2 = new BioMetabolite("p2");
        r1 = new BioReaction("r1");
        r2 = new BioReaction("r2");
        r3 = new BioReaction("r03");

        s1.setChemicalFormula("C6H6O3");
        s2.setChemicalFormula("C2H3");
        p1.setChemicalFormula("C2H2O");
        p2.setChemicalFormula("C4H6");

        BioCompartment cpt = new BioCompartment("cpt");

        network.add(s1, s2, p1, p2, r1, r2, r3, cpt);
        network.affectToCompartment(cpt, s1, s2, p1, p2);

        network.affectLeft(r1, 1.0, cpt, s1);
        network.affectLeft(r1, 2.0, cpt, s2);
        network.affectRight(r1, 3.0, cpt, p1);
        network.affectRight(r1, 1.0, cpt, p2);

        network.affectLeft(r2, 1.0, cpt, s1);
        network.affectRight(r2, 3.0, cpt, p1);
        network.affectRight(r2, 1.0, cpt, p2);

        network.affectLeft(r3, 1.0, cpt, s1);
        network.affectRight(r3, 3.0, cpt, p1);

        // r1 : s1 + 2 s2 -> 3 p1 + 1 p2    // balanced
        // r2 : s1 -> 3 p1 + 1 p2           // not balanced
        // r3 : s1 -> 3 p1                  // balanced

        analysis = new NetworkBalanceAnalysis(network);


    }


    @Test
    public void getAllBalances() {
        List<ReactionBalanceAnalysis> allBalances = analysis.getAllBalances();

        assertEquals(3, allBalances.size());
    }

    @Test
    public void getBalanced() {
        List<ReactionBalanceAnalysis> balanced = analysis.getBalanced();

        assertEquals(2, balanced.size());
    }

    @Test
    public void getUnbalanced() {
        List<ReactionBalanceAnalysis> unbalanced = analysis.getUnbalanced();

        assertEquals(1, unbalanced.size());
    }
}