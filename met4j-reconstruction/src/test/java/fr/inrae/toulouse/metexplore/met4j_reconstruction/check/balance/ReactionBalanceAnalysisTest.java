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
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ReactionBalanceAnalysisTest {

    private static BioNetwork network;
    private static BioReaction reaction;
    private static BioMetabolite s1;
    private static BioMetabolite s2;
    private static BioMetabolite p1;
    private static BioMetabolite p2;
    private static BioCompartment cpt;

    @Before
    public void init() {

        network = new BioNetwork();
        s1 = new BioMetabolite("s1");
        s2 = new BioMetabolite("s2");
        p1 = new BioMetabolite("p1");
        p2 = new BioMetabolite("p2");
        reaction = new BioReaction("r1");
        cpt = new BioCompartment("cpt");

        network.add(s1, s2, p1, p2, reaction, cpt);
        network.affectToCompartment(cpt, s1, s2, p1, p2);
        network.affectLeft(reaction, 1.0, cpt, s1);
        network.affectLeft(reaction, 2.0, cpt, s2);
        network.affectRight(reaction, 3.0, cpt, p1);
        network.affectRight(reaction, 1.0, cpt, p2);

        // reaction : s1 + 2 s2 -> 3 p1 + 1 p2
    }

    public void createBalanced() {
        s1.setChemicalFormula("C6H6O3");
        s2.setChemicalFormula("C2H3");
        p1.setChemicalFormula("C2H2O");
        p2.setChemicalFormula("C4H6");

    }

    public void createUnbalanced() {
        s1.setChemicalFormula("C6H6O3");
        s2.setChemicalFormula("C2H3");
        p1.setChemicalFormula("CH2O");
        p2.setChemicalFormula("C4H6");

    }

    public void createBalancedWithNull() {
        s1.setChemicalFormula("C6H9O3");
        s2.setChemicalFormula(null);
        p1.setChemicalFormula("C2H2O");
        p2.setChemicalFormula("H3");
    }

    public void createBalancedWithBadFormula() {
        s1.setChemicalFormula("C6H9O3");
        s2.setChemicalFormula("2C");
        p1.setChemicalFormula("C2H2O");
        p2.setChemicalFormula("H3");
    }

    @Test
    public void isBalanced() {
        this.createBalanced();
        ReactionBalanceAnalysis balance = new ReactionBalanceAnalysis(reaction);

        assertTrue(balance.isBalanced());

        this.createUnbalanced();

        balance = new ReactionBalanceAnalysis(reaction);

        assertFalse(balance.isBalanced());
    }

    @Test
    public void isBalancedWithNull() {
        this.createBalancedWithNull();
        ReactionBalanceAnalysis balance = new ReactionBalanceAnalysis(reaction);

        assertFalse(balance.isBalanced());

        assertTrue(balance.getMetabolitesWithBadFormula().contains(s2));
    }

    @Test
    public void isBalancedWithBadFormula() {
        this.createBalancedWithBadFormula();
        ReactionBalanceAnalysis balance = new ReactionBalanceAnalysis(reaction);

        assertFalse(balance.isBalanced());

        assertTrue(balance.getMetabolitesWithBadFormula().contains(s2));
    }

    @Test
    public void getBalances() {
        this.createBalanced();
        ReactionBalanceAnalysis balance = new ReactionBalanceAnalysis(reaction);
        HashMap<String, Double> test = balance.getBalances();

        HashMap<String, Double> ref = new HashMap<>();
        ref.put("O", 0.0);
        ref.put("C", 0.0);
        ref.put("H", 0.0);

        assertEquals(ref, test);

        this.createUnbalanced();
        balance = new ReactionBalanceAnalysis(reaction);
        test = balance.getBalances();

        ref = new HashMap<>();
        ref.put("O", 0.0);
        ref.put("C", -3.0);
        ref.put("H", 0.0);

        assertEquals(ref, test);
    }

    @Test
    public void getBalancesWithNull() {
        this.createBalancedWithNull();
        ReactionBalanceAnalysis balance = new ReactionBalanceAnalysis(reaction);
        HashMap<String, Double> test = balance.getBalances();

        HashMap<String, Double> ref = new HashMap<>();
        ref.put("O", 0.0);
        ref.put("C", 0.0);
        ref.put("H", 0.0);

        assertEquals(ref, test);
    }

    @Test
    public void getBalancesWithBadFormula() {
        this.createBalancedWithBadFormula();
        ReactionBalanceAnalysis balance = new ReactionBalanceAnalysis(reaction);
        HashMap<String, Double> test = balance.getBalances();

        HashMap<String, Double> ref = new HashMap<>();
        ref.put("O", 0.0);
        ref.put("C", 0.0);
        ref.put("H", 0.0);

        assertEquals(ref, test);
    }

    @Test
    public void isExchange() {

        this.createBalanced();

        ReactionBalanceAnalysis balance = new ReactionBalanceAnalysis(reaction);

        assertFalse(balance.isExchange());

        MetaboliteAttributes.setBoundaryCondition(s1, true);

        assertTrue(balance.isExchange());

        MetaboliteAttributes.setBoundaryCondition(s1, false);
        MetaboliteAttributes.setBoundaryCondition(p1, true);

        assertTrue(balance.isExchange());

        network.removeRight(p1, cpt, reaction);
        network.removeRight(p2, cpt, reaction);

        assertTrue(balance.isExchange());


    }
}