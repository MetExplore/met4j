/*
 * Copyright INRAE (2026)
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

package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;



public class MetabolitesCoOccurrenceTest {

    BioReaction r1;
    BioReaction r2;
    BioMetabolite m1;
    BioMetabolite m2;
    BioMetabolite m3;
    BioMetabolite m4;
    BioMetabolite cofactor;
    BioCompartment c1;
    BioPathway pathway1;
    BioEnzyme enzyme1;
    BioProtein protein1;
    BioGene gene1;

    /**
     * Test method for
     * {@link MetabolitesCoOccurrence#removeCoupledReactants(BioNetwork, List<MetabolitesCoOccurrence.ReactantPattern>)}
     */

    private BioNetwork miniNetwork() {
        BioNetwork originalNetwork = new BioNetwork("ori");

        r1 = new BioReaction("R1");
        r2 = new BioReaction("R2");
        m1 = new BioMetabolite("M1");
        m2 = new BioMetabolite("M2");
        m3 = new BioMetabolite("M3");
        m4 = new BioMetabolite("M4");
        cofactor = new BioMetabolite("cofactor");


        c1 = new BioCompartment("c1");
        pathway1 = new BioPathway("pathway1");
        enzyme1 = new BioEnzyme("enz1");
        protein1 = new BioProtein("protein1");
        gene1 = new BioGene("gene1");

        originalNetwork.add(r1, r2, m1, m2, m3, m4, c1, pathway1, protein1, gene1, enzyme1, cofactor);
        originalNetwork.affectToPathway(pathway1, r1, r2);
        originalNetwork.affectToCompartment(c1, m1, m2, m3, m4);
        originalNetwork.affectGeneProduct(protein1, gene1);
        originalNetwork.affectSubUnit(enzyme1, 1.0, protein1, cofactor);
        originalNetwork.affectLeft(r1, 2.0, c1, m1);
        originalNetwork.affectRight(r1, 1.0, c1, m2);
        originalNetwork.affectLeft(r2, 1.0, c1, m3);
        originalNetwork.affectRight(r2, 2.0, c1, m4);
        originalNetwork.affectEnzyme(r1, enzyme1);

        return originalNetwork;
    }

    @Test
    public void testRemoveCoupledReactantsDirectMatch() {
        // r1 : M1 --> M2  /  r2 : M3 --> M4
        // coll1={M1}, coll2={M2} -> matches r1 left and right
        // BUT removing them would empty the reaction, so NOTHING should be removed.
        BioNetwork network = miniNetwork();

        BioCollection<BioMetabolite> coll1 = new BioCollection<>();
        coll1.add(m1);

        BioCollection<BioMetabolite> coll2 = new BioCollection<>();
        coll2.add(m2);

        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll1, coll2)));

        assertEquals("M1 should NOT be removed from left of r1 because side would be empty", 1, network.getReaction("R1").getLeftReactantsView().size());
        assertEquals("M2 should NOT be removed from right of r1 because side would be empty", 1, network.getReaction("R1").getRightReactantsView().size());
        // r2 must not be affected
        assertEquals("r2 left should not be modified", 1, network.getReaction("R2").getLeftReactantsView().size());
        assertEquals("r2 right should not be modified", 1, network.getReaction("R2").getRightReactantsView().size());
    }

    @Test
    public void testRemoveCoupledReactantsSwappedReversibleRemoves() {
        BioNetwork network = miniNetwork();
        BioReaction reaction = network.getReaction("R1");
        reaction.setReversible(true);

        BioMetabolite m5 = new BioMetabolite("M5");
        BioMetabolite m6 = new BioMetabolite("M6");
        network.add(m5, m6);
        network.affectToCompartment(c1, m5, m6);
        network.affectLeft(reaction, 1.0, c1, m5);
        network.affectRight(reaction, 1.0, c1, m6);

        BioCollection<BioMetabolite> coll1 = new BioCollection<>();
        coll1.add(m2);
        BioCollection<BioMetabolite> coll2 = new BioCollection<>();
        coll2.add(m1);

        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll1, coll2)));

        assertFalse("M1 should be removed from left of r1 in swapped mode when reversible", reaction.getLeftsView().contains(m1));
        assertFalse("M2 should be removed from right of r1 in swapped mode when reversible", reaction.getRightsView().contains(m2));
        assertTrue("M5 should remain on left", reaction.getLeftsView().contains(m5));
        assertTrue("M6 should remain on right", reaction.getRightsView().contains(m6));
    }

    @Test
    public void testRemoveCoupledReactantsSwappedIrreversibleNoRemoval() {
        BioNetwork network = miniNetwork();
        BioReaction reaction = network.getReaction("R1");
        reaction.setReversible(false);

        BioMetabolite m5 = new BioMetabolite("M5");
        BioMetabolite m6 = new BioMetabolite("M6");
        network.add(m5, m6);
        network.affectToCompartment(c1, m5, m6);
        network.affectLeft(reaction, 1.0, c1, m5);
        network.affectRight(reaction, 1.0, c1, m6);

        BioCollection<BioMetabolite> coll1 = new BioCollection<>();
        coll1.add(m2);
        BioCollection<BioMetabolite> coll2 = new BioCollection<>();
        coll2.add(m1);

        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll1, coll2)));

        assertTrue("M1 should stay on left when swapped mode is not allowed", reaction.getLeftsView().contains(m1));
        assertTrue("M2 should stay on right when swapped mode is not allowed", reaction.getRightsView().contains(m2));
    }

    @Test
    public void testRemoveCoupledReactantsNoMatch() {
        // coll1={M1}, coll2={M3} -> does not match any reaction -> nothing is removed
        BioNetwork network = miniNetwork();

        BioCollection<BioMetabolite> coll1 = new BioCollection<>();
        coll1.add(m1);

        BioCollection<BioMetabolite> coll2 = new BioCollection<>();
        coll2.add(m3);

        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll1, coll2)));

        assertEquals("r1 left should not be modified", 1, network.getReaction("R1").getLeftReactantsView().size());
        assertEquals("r1 right should not be modified", 1, network.getReaction("R1").getRightReactantsView().size());
        assertEquals("r2 left should not be modified", 1, network.getReaction("R2").getLeftReactantsView().size());
        assertEquals("r2 right should not be modified", 1, network.getReaction("R2").getRightReactantsView().size());
    }

    @Test
    public void testRemoveCoupledReactantsNotRemoving() {
        // coll1={M3}, coll2={M4} -> matches r2
        // BUT removing them would empty the reaction, so NOTHING should be removed.
        BioNetwork network = miniNetwork();

        BioCollection<BioMetabolite> coll1 = new BioCollection<>();
        coll1.add(m3);

        BioCollection<BioMetabolite> coll2 = new BioCollection<>();
        coll2.add(m4);

        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll1, coll2)));

        assertEquals("r1 left should not be modified", 1, network.getReaction("R1").getLeftReactantsView().size());
        assertEquals("r1 right should not be modified", 1, network.getReaction("R1").getRightReactantsView().size());
        assertEquals("M3 should NOT be removed from left of r2", 1, network.getReaction("R2").getLeftReactantsView().size());
        assertEquals("M4 should NOT be removed from right of r2", 1, network.getReaction("R2").getRightReactantsView().size());
    }

    @Test
    public void testRemoveCoupledReactantsSuccess() {
        BioNetwork network = miniNetwork();
        // Add extra metabolites to allow removal
        BioMetabolite m5 = new BioMetabolite("M5");
        BioMetabolite m6 = new BioMetabolite("M6");
        network.add(m5, m6);
        network.affectToCompartment(c1, m5, m6);
        network.affectLeft(network.getReaction("R1"), 1.0, c1, m5);
        network.affectRight(network.getReaction("R1"), 1.0, c1, m6);

        // R1: 2 M1 + M5 -> M2 + M6

        BioCollection<BioMetabolite> coll1 = new BioCollection<>();
        coll1.add(m1);

        BioCollection<BioMetabolite> coll2 = new BioCollection<>();
        coll2.add(m2);

        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll1, coll2)));

        assertEquals("M1 should be removed", 1, network.getReaction("R1").getLeftReactantsView().size());
        assertTrue("M5 should remain", network.getReaction("R1").getLeftsView().contains(m5));

        assertEquals("M2 should be removed", 1, network.getReaction("R1").getRightReactantsView().size());
        assertTrue("M6 should remain", network.getReaction("R1").getRightsView().contains(m6));
    }

    @Test
    public void testRemoveCoupledReactantsBatchSupersetFirst() {
        BioNetwork network = new BioNetwork("batch-sort");

        BioCompartment c = new BioCompartment("c");
        BioMetabolite adp = new BioMetabolite("adp");
        BioMetabolite pi = new BioMetabolite("pi");
        BioMetabolite e = new BioMetabolite("e");
        BioMetabolite atp = new BioMetabolite("atp");
        BioMetabolite h2o = new BioMetabolite("h2o");

        network.add(c, adp, pi, e, atp, h2o);
        network.affectToCompartment(c, adp, pi, e, atp, h2o);

        BioReaction r1 = new BioReaction("R1");
        network.add(r1);

        // R1: adp + pi + e -> atp + h2o
        network.affectLeft(r1, 1.0, c, adp);
        network.affectLeft(r1, 1.0, c, pi);
        network.affectLeft(r1, 1.0, c, e);
        network.affectRight(r1, 1.0, c, atp);
        network.affectRight(r1, 1.0, c, h2o);

        BioCollection<BioMetabolite> subsetLeft = new BioCollection<>();
        subsetLeft.add(adp);
        BioCollection<BioMetabolite> subsetRight = new BioCollection<>();
        subsetRight.add(atp);

        BioCollection<BioMetabolite> supersetLeft = new BioCollection<>();
        supersetLeft.add(adp);
        supersetLeft.add(pi);
        BioCollection<BioMetabolite> supersetRight = new BioCollection<>();
        supersetRight.add(atp);

        List<MetabolitesCoOccurrence.ReactantPattern> pattern = List.of(
                new MetabolitesCoOccurrence.ReactantPattern(subsetLeft, subsetRight),
                new MetabolitesCoOccurrence.ReactantPattern(supersetLeft, supersetRight)
        );

        // Intentionally call subset first, then superset.
        // The API should remain robust to this ordering.
        MetabolitesCoOccurrence.removeCoupledReactants(network,pattern);

        BioCollection<BioMetabolite> leftAfter = network.getReaction("R1").getLeftsView();
        BioCollection<BioMetabolite> rightAfter = network.getReaction("R1").getRightsView();

        assertFalse("adp should be removed", leftAfter.contains(adp));
        assertFalse("pi should be removed (superset pattern must be processed first)", leftAfter.contains(pi));
        assertTrue("e should remain", leftAfter.contains(e));

        assertFalse("atp should be removed", rightAfter.contains(atp));
        assertTrue("h2o should remain", rightAfter.contains(h2o));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveCoupledReactantsNullNetwork() {
        BioCollection<BioMetabolite> coll = new BioCollection<>();
        MetabolitesCoOccurrence.removeCoupledReactants(null, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll, coll)));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveCoupledReactantsNullColl1() {
        BioNetwork network = miniNetwork();
        BioCollection<BioMetabolite> coll = new BioCollection<>();
        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(null, coll)));
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveCoupledReactantsNullColl2() {
        BioNetwork network = miniNetwork();
        BioCollection<BioMetabolite> coll = new BioCollection<>();
        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(coll, null)));
    }

    // =====================================================================
    // Tests for getCoOccurringMetaboliteSets
    // =====================================================================

    /**
     * Builds the canonical example network from the javadoc:
     * R1: ATP + C  ->  ADP + Pi + D
     * R2: ADP + Pi + E  ->  ATP + F
     */
    private BioNetwork coOccurrenceNetwork() {
        BioNetwork network = new BioNetwork("coOcc");
        BioCompartment c = new BioCompartment("c");
        BioMetabolite mATP = new BioMetabolite("ATP");
        BioMetabolite mC   = new BioMetabolite("C");
        BioMetabolite mADP = new BioMetabolite("ADP");
        BioMetabolite mPi  = new BioMetabolite("Pi");
        BioMetabolite mD   = new BioMetabolite("D");
        BioMetabolite mE   = new BioMetabolite("E");
        BioMetabolite mF   = new BioMetabolite("F");
        network.add(c, mATP, mC, mADP, mPi, mD, mE, mF);
        network.affectToCompartment(c, mATP, mC, mADP, mPi, mD, mE, mF);

        BioReaction r1 = new BioReaction("R1"); // ATP + C -> ADP + Pi + D
        BioReaction r2 = new BioReaction("R2"); // ADP + Pi + E -> ATP + F
        network.add(r1, r2);

        network.affectLeft(r1,  1.0, c, mATP);
        network.affectLeft(r1,  1.0, c, mC);
        network.affectRight(r1, 1.0, c, mADP);
        network.affectRight(r1, 1.0, c, mPi);
        network.affectRight(r1, 1.0, c, mD);

        network.affectLeft(r2,  1.0, c, mADP);
        network.affectLeft(r2,  1.0, c, mPi);
        network.affectLeft(r2,  1.0, c, mE);
        network.affectRight(r2, 1.0, c, mATP);
        network.affectRight(r2, 1.0, c, mF);

        return network;
    }

    /** Returns true if the result contains a pattern (regardless of canonical ordering). */
    private boolean containsPattern(Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result, Set<String> s1, Set<String> s2) {
        return result.keySet().stream().anyMatch(pattern ->
                (pattern.left().getIds().equals(s1) && pattern.right().getIds().equals(s2)) ||
                        (pattern.left().getIds().equals(s2) && pattern.right().getIds().equals(s1)));
    }

    /** Returns the occurrence count of a pattern, or 0 if absent. */
    private int getPatternCount(Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result, Set<String> s1, Set<String> s2) {
        return result.entrySet().stream()
                .filter(e -> (e.getKey().left().getIds().equals(s1) && e.getKey().right().getIds().equals(s2)) ||
                        (e.getKey().left().getIds().equals(s2) && e.getKey().right().getIds().equals(s1)))
                .mapToInt(Map.Entry::getValue)
                .findFirst()
                .orElse(0);
    }

    /**
     * The canonical example: ({ATP},{ADP,Pi}) appears once in R1 (left→right)
     * and once in R2 (right→left), so its count must be 2 with minOccurrences=2.
     */
    @Test
    public void testCoOccGetBasicPatternCount() {
        BioNetwork network = coOccurrenceNetwork();

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 2, 2);


        Set<String> setATP   = new HashSet<>(List.of("ATP"));
        Set<String> setADPPi = new HashSet<>(Arrays.asList("ADP", "Pi"));

        assertTrue("Pattern ({ATP},{ADP,Pi}) must be present with minOccurrences=2",
                containsPattern(result, setATP, setADPPi));
        assertEquals("Pattern ({ATP},{ADP,Pi}) must have count 2",
                2, getPatternCount(result, setATP, setADPPi));

        // Same count=2: ({ATP},{ADP}) is a strict subset of ({ATP},{ADP,Pi}) and must be removed.
        Set<String> setADP = new HashSet<>(List.of("ADP"));
        assertFalse("Subset pattern ({ATP},{ADP}) must be removed when a superset has same count",
                containsPattern(result, setATP, setADP));
    }

    /**
     * The pattern ({C},{D}) only appears in R1, so it must be absent when minOccurrences=2.
     */
    @Test
    public void testCoOccPatternAppearingOnceFilteredOut() {
        BioNetwork network = coOccurrenceNetwork();

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 2, 2);

        Set<String> setC = new HashSet<>(Arrays.asList("C"));
        Set<String> setD = new HashSet<>(Arrays.asList("D"));

        assertFalse("Pattern ({C},{D}) appears only once and must be absent with minOccurrences=2",
                containsPattern(result, setC, setD));
    }

    /**
     * With minOccurrences=3, no pattern appears often enough → result must be empty.
     */
    @Test
    public void testCoOccThresholdTooHighReturnsEmpty() {
        BioNetwork network = coOccurrenceNetwork();

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 3, 2);

        assertTrue("No pattern reaches 3 occurrences, result must be empty", result.isEmpty());
    }

    /**
     * Superset filtering must only apply when counts are equal.
     * Here ({A},{B}) appears 3 times, while ({A,X},{B}) appears 2 times: both must be kept.
     */
    @Test
    public void testCoOccKeepSubsetWhenCountsDiffer() {
        BioNetwork network = new BioNetwork("diff-count");
        BioCompartment c = new BioCompartment("c");
        BioMetabolite mA = new BioMetabolite("A");
        BioMetabolite mB = new BioMetabolite("B");
        BioMetabolite mX = new BioMetabolite("X");
        network.add(c, mA, mB, mX);
        network.affectToCompartment(c, mA, mB, mX);

        BioReaction r1 = new BioReaction("R1");
        BioReaction r2 = new BioReaction("R2");
        BioReaction r3 = new BioReaction("R3");
        network.add(r1, r2, r3);

        // R1: A + X -> B
        network.affectLeft(r1, 1.0, c, mA);
        network.affectLeft(r1, 1.0, c, mX);
        network.affectRight(r1, 1.0, c, mB);

        // R2: A + X -> B
        network.affectLeft(r2, 1.0, c, mA);
        network.affectLeft(r2, 1.0, c, mX);
        network.affectRight(r2, 1.0, c, mB);

        // R3: A -> B
        network.affectLeft(r3, 1.0, c, mA);
        network.affectRight(r3, 1.0, c, mB);

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 2, 2);

        Set<String> setA = new HashSet<>(Arrays.asList("A"));
        Set<String> setB = new HashSet<>(Arrays.asList("B"));
        Set<String> setAX = new HashSet<>(Arrays.asList("A", "X"));

        assertTrue("({A},{B}) must be kept (count=3)", containsPattern(result, setA, setB));
        assertEquals("({A},{B}) must have count 3", 3, getPatternCount(result, setA, setB));

        assertTrue("({A,X},{B}) must be kept (count=2), because count differs from its subset",
                containsPattern(result, setAX, setB));
        assertEquals("({A,X},{B}) must have count 2", 2, getPatternCount(result, setAX, setB));
    }

    /**
     * With maxSubsetSize=1, every set in every returned pattern must be a singleton.
     * The singleton pattern ({ATP},{ADP}) appears in R1 and R2 → count must be 2.
     */
    @Test
    public void testCoOccMaxSubsetSizeOneOnlySingletons() {
        BioNetwork network = coOccurrenceNetwork();

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 2, 1);

        // All sets in every pattern must be singletons
        for (MetabolitesCoOccurrence.ReactantPattern pattern : result.keySet()) {
            assertEquals("First set must be a singleton with maxSubsetSize=1",
                    1, pattern.left().size());
            assertEquals("Second set must be a singleton with maxSubsetSize=1",
                    1, pattern.right().size());
        }

        // ({ATP},{ADP}) must appear with count=2
        Set<String> setATP = new HashSet<>(Arrays.asList("ATP"));
        Set<String> setADP = new HashSet<>(Arrays.asList("ADP"));
        assertTrue("Pattern ({ATP},{ADP}) must be present with maxSubsetSize=1",
                containsPattern(result, setATP, setADP));
        assertEquals("Pattern ({ATP},{ADP}) must have count 2",
                2, getPatternCount(result, setATP, setADP));

        // ({ATP},{Pi}) must also appear with count=2 (ATP left in R1, Pi right; Pi left in R2, ATP right)
        Set<String> setPi = new HashSet<>(Arrays.asList("Pi"));
        assertTrue("Pattern ({ATP},{Pi}) must be present with maxSubsetSize=1",
                containsPattern(result, setATP, setPi));
        assertEquals("Pattern ({ATP},{Pi}) must have count 2",
                2, getPatternCount(result, setATP, setPi));

        // Larger sets must NOT appear
        Set<String> setADPPi = new HashSet<>(Arrays.asList("ADP", "Pi"));
        assertFalse("Pattern ({ATP},{ADP,Pi}) must be absent with maxSubsetSize=1",
                containsPattern(result, setATP, setADPPi));
    }

    /**
     * Each key in the result must be a List of exactly 2 sets,
     * and no pattern must be duplicated in both orderings.
     */
    @Test
    public void testCoOccResultPatternsHaveSizeTwoAndNoDuplicates() {
        BioNetwork network = coOccurrenceNetwork();

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 1, 2);

        // Verify no pattern appears in both (s1,s2) and (s2,s1) forms
        List<MetabolitesCoOccurrence.ReactantPattern> keys = new ArrayList<>(result.keySet());
        for (int i = 0; i < keys.size(); i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                MetabolitesCoOccurrence.ReactantPattern pi = keys.get(i);
                MetabolitesCoOccurrence.ReactantPattern pj = keys.get(j);
                boolean reversed = pi.left().equals(pj.right()) && pi.right().equals(pj.left());
                assertFalse("No pattern should appear twice in reversed order", reversed);
            }
        }
    }

    /**
     * A reaction whose left side is empty must be silently skipped.
     */
    @Test
    public void testCoOccReactionWithEmptyLeftSideSkipped() {
        BioNetwork network = new BioNetwork("test");
        BioCompartment c = new BioCompartment("c");
        BioMetabolite m1 = new BioMetabolite("m1");
        network.add(c, m1);
        network.affectToCompartment(c, m1);

        BioReaction r = new BioReaction("R1");
        network.add(r);
        network.affectRight(r, 1.0, c, m1); // no left side

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 1, 1);

        assertTrue("Reaction with no left side must be skipped → empty result", result.isEmpty());
    }

    /**
     * A network with no reactions must return an empty map.
     */
    @Test
    public void testCoOccEmptyNetworkReturnsEmptyMap() {
        BioNetwork network = new BioNetwork("empty");

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 1, 1);

        assertTrue("Empty network must produce an empty result", result.isEmpty());
    }

    /**
     * Three reactions all carrying the same pattern → count must be 3.
     */
    @Test
    public void testCoOccThreeOccurrences() {
        BioNetwork network = new BioNetwork("triple");
        BioCompartment c = new BioCompartment("c");
        BioMetabolite mA = new BioMetabolite("A");
        BioMetabolite mB = new BioMetabolite("B");
        BioMetabolite mX = new BioMetabolite("X");
        BioMetabolite mY = new BioMetabolite("Y");
        BioMetabolite mZ = new BioMetabolite("Z");
        network.add(c, mA, mB, mX, mY, mZ);
        network.affectToCompartment(c, mA, mB, mX, mY, mZ);

        // R1: A + X -> B + Y
        // R2: A + Y -> B + Z
        // R3: B + Z -> A + X
        // Pattern ({A},{B}) appears in R1, R2, and R3 → count=3
        BioReaction r1 = new BioReaction("R1");
        BioReaction r2 = new BioReaction("R2");
        BioReaction r3 = new BioReaction("R3");
        network.add(r1, r2, r3);

        network.affectLeft(r1,  1.0, c, mA); network.affectLeft(r1, 1.0, c, mX);
        network.affectRight(r1, 1.0, c, mB); network.affectRight(r1, 1.0, c, mY);

        network.affectLeft(r2,  1.0, c, mA); network.affectLeft(r2, 1.0, c, mY);
        network.affectRight(r2, 1.0, c, mB); network.affectRight(r2, 1.0, c, mZ);

        network.affectLeft(r3,  1.0, c, mB); network.affectLeft(r3, 1.0, c, mZ);
        network.affectRight(r3, 1.0, c, mA); network.affectRight(r3, 1.0, c, mX);

        Map<MetabolitesCoOccurrence.ReactantPattern, Integer> result =
                MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(network, 3, 1);

        Set<String> setA = new HashSet<>(Arrays.asList("A"));
        Set<String> setB = new HashSet<>(Arrays.asList("B"));

        assertTrue("Pattern ({A},{B}) must be present with minOccurrences=3",
                containsPattern(result, setA, setB));
        assertEquals("Pattern ({A},{B}) must have count 3",
                3, getPatternCount(result, setA, setB));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCoOccInvalidMaxSubsetSize() {
        MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(coOccurrenceNetwork(), 1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCoOccInvalidMinOccurrences() {
        MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(coOccurrenceNetwork(), 0, 1);
    }

    @Test(expected = NullPointerException.class)
    public void testCoOccNullNetwork() {
        MetabolitesCoOccurrence.getCoOccurringMetaboliteSets(null, 1, 1);
    }

    @Test
    public void testRemoveCoupledReactantsWithEmptyCollectionAndSide() {
        BioNetwork network = new BioNetwork("test");
        BioCompartment comp = new BioCompartment("c");
        network.add(comp);

        BioMetabolite m1 = new BioMetabolite("m1");
        BioMetabolite m2 = new BioMetabolite("m2");
        network.add(m1, m2);
        network.affectToCompartment(comp, m1, m2);

        // Case 1: Right side has reactants, Left side empty. Removing from Right.
        BioReaction r1 = new BioReaction("r1");
        network.add(r1);
        network.affectRight(r1, 1.0, comp, m1);
        network.affectRight(r1, 1.0, comp, m2);
        // r1: -> m1 + m2

        BioCollection<BioMetabolite> collEmpty = new BioCollection<>();
        BioCollection<BioMetabolite> collToRem = new BioCollection<>();
        collToRem.add(m1);

        // Matches: Left matches Empty, Right matches collToRem.
        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(collEmpty, collToRem)));

        // We expect m1 to be removed from r1.
        assertFalse("m1 should be removed from r1 rights", network.getRightReactants(r1).contains(m1));
        assertEquals("r1 should have 1 product left", 1, network.getRightReactants(r1).size());
    }

    @Test
    public void testRemoveCoupledReactantsWithEmptyCollectionAndSideReverse() {
        BioNetwork network = new BioNetwork("test");
        BioCompartment comp = new BioCompartment("c");
        network.add(comp);

        BioMetabolite m1 = new BioMetabolite("m1");
        BioMetabolite m2 = new BioMetabolite("m2");
        network.add(m1, m2);
        network.affectToCompartment(comp, m1, m2);

        // Case 2: Left side has reactants, Right side empty. Removing from Left.
        BioReaction r2 = new BioReaction("r2");
        network.add(r2);
        network.affectLeft(r2, 1.0, comp, m1);
        network.affectLeft(r2, 1.0, comp, m2);
        // r2: m1 + m2 ->

        BioCollection<BioMetabolite> collToRem = new BioCollection<>();
        collToRem.add(m1);
        BioCollection<BioMetabolite> collEmpty = new BioCollection<>();

        // Matches: Left matches collToRem, Right matches Empty.
        MetabolitesCoOccurrence.removeCoupledReactants(network, List.of(new MetabolitesCoOccurrence.ReactantPattern(collToRem, collEmpty)));

        // We expect m1 to be removed from r2.
        assertFalse("m1 should be removed from r2 lefts", network.getLeftReactants(r2).contains(m1));
        assertEquals("r2 should have 1 substrate left", 1, network.getLeftReactants(r2).size());
    }
}

