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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import lombok.NonNull;

import java.util.*;

public class MetabolitesCoOccurence
{

    public record ReactantPattern(BioCollection<BioMetabolite> left, BioCollection<BioMetabolite> right){};

    public static void removeCoupledReactants(BioNetwork bn, List<ReactantPattern> patterns){
        Map<BioReaction, ReactantPattern> newReactants = new HashMap<>();
        for(BioReaction r : bn.getReactionsView()){
            newReactants.put(r, new ReactantPattern(new BioCollection<>(r.getLeftsView()), new BioCollection<>(r.getRightsView())));
            for(ReactantPattern p : patterns){
                if(r.getLeftsView().containsAll(p.left) && r.getRightsView().containsAll(p.right)){
                    newReactants.get(r).left().removeAll(p.left);
                    newReactants.get(r).right().removeAll(p.right);
                }
            }
            if(newReactants.get(r).right().size()==r.getRightsView().size()
                    && newReactants.get(r).left().size()==r.getLeftsView().size()){
                continue; //nothing to remove
            }
            //check if removal would leave an empty reaction side, if it was not already empty (exchange reaction case)
            if(!(newReactants.get(r).left().isEmpty() && !r.getLeftsView().isEmpty())
                    && (!(newReactants.get(r).right().isEmpty() && !r.getRightsView().isEmpty()))){
                for(BioReactant reactant : new BioCollection<>(r.getLeftReactantsView())){
                    if(!newReactants.get(r).left().contains(reactant.getMetabolite())){
                        bn.removeLeft(reactant.getMetabolite(), reactant.getLocation(), r);
                    }
                }
                for(BioReactant reactant : new BioCollection<>(r.getRightReactantsView())){
                    if(!newReactants.get(r).right().contains(reactant.getMetabolite())){
                        bn.removeRight(reactant.getMetabolite(), reactant.getLocation(), r);
                    }
                }
            }
        }
    }

    /**
     * Detects patterns of metabolite subsets that co-occur on opposite sides of reactions.
     * <p>
     * For each reaction, all non-empty subsets (up to {@code maxSubsetSize}) of the left-side
     * metabolites are paired with all non-empty subsets of the right-side metabolites.
     * Patterns are direction-agnostic: {@code ([ATP], [ADP, Pi])} found in a forward reaction is
     * counted as the same pattern as in a reverse reaction.
     * Only patterns appearing in at least {@code minOccurrences} reactions are returned.
     * Among patterns with the same occurrence count, strict subsets are removed in favor of
     * supersets (when both sides are included, up to pattern side permutation).
     * </p>
     * <p>
     * Example: given reactions {@code ATP + C -> ADP + Pi + D} and {@code ADP + Pi + E -> ATP + F},
     * the pattern {@code ({ATP}, {ADP, Pi})} has an occurrence count of 2.
     * </p>
     * <p>
     * Complexity note: with {@code maxSubsetSize=3} and ~4 metabolites per side on average,
     * roughly 300 patterns are generated per reaction, making this tractable for networks of 1000+
     * reactions.
     * </p>
     *
     * @param network        a {@link BioNetwork}
     * @param minOccurrences minimum number of reactions a pattern must appear in (must be &ge; 1)
     * @param maxSubsetSize  maximum number of metabolites per subset; recommended 1–3 for large
     *                       networks to limit combinatorial explosion (must be &ge; 1)
     * @return a {@link Map} of patterns with their number of occurrences in the network, above the given threshold and without redundant patterns.
     * @throws IllegalArgumentException if {@code maxSubsetSize} or {@code minOccurrences} is &lt; 1
     */
    public static Map<ReactantPattern, Integer> getCoOccurringMetaboliteSets(
            @NonNull BioNetwork network, int minOccurrences, int maxSubsetSize) {

        if (maxSubsetSize < 1)  throw new IllegalArgumentException("maxSubsetSize must be >= 1");
        if (minOccurrences < 1) throw new IllegalArgumentException("minOccurrences must be >= 1");

        Map<ReactantPattern, Integer> patternCounts = getRawCoOccurringMetaboliteSets(network,maxSubsetSize);
        filterLowCountAndSubsets(patternCounts, minOccurrences);
        return patternCounts;
    }

    /**
     * Get the count for each pattern of metabolite subsets that co-occur on opposite sides of reactions (up to a given size).
     * Does not filter out low count pattern and keep pattern even if strict superset with same count exists.
     * @param network        a {@link BioNetwork}
     * @param maxSubsetSize maximum number of metabolites per subset; recommended 1–3 for large
     *                       networks to limit combinatorial explosion (must be &ge; 1)
     * @return a {@link Map} of patterns with their number of occurrences in the network
     */
    public static Map<ReactantPattern, Integer> getRawCoOccurringMetaboliteSets(@NonNull BioNetwork network, int maxSubsetSize){
        if (maxSubsetSize < 1)  throw new IllegalArgumentException("maxSubsetSize must be >= 1");

        Map<ReactantPattern, Integer> patternCounts = new HashMap<>();

        for (BioReaction reaction : network.getReactionsView()) {

            BioCollection<BioMetabolite> lefts = reaction.getLeftsView();
            BioCollection<BioMetabolite> rights = reaction.getRightsView();

            if (lefts.isEmpty() || rights.isEmpty()) continue; //ignore exchange reactions

            List<BioCollection<BioMetabolite>> leftSubsets  = generateSubsets(lefts,  maxSubsetSize);
            List<BioCollection<BioMetabolite>> rightSubsets = generateSubsets(rights, maxSubsetSize);

            //for each subset of substrates...
            for (BioCollection<BioMetabolite> leftSub : leftSubsets) {
                // ...and each subset of products,
                for (BioCollection<BioMetabolite> rightSub : rightSubsets) {
                    //create reaction pattern and increment count
                    ReactantPattern pattern = new ReactantPattern(leftSub,rightSub);
                    ReactantPattern swappedPattern = new ReactantPattern(rightSub, leftSub);
                    if(patternCounts.containsKey(pattern)){
                        patternCounts.put(pattern, patternCounts.get(pattern)+1);
                    } else if(patternCounts.containsKey(swappedPattern)){
                        patternCounts.put(swappedPattern, patternCounts.get(swappedPattern)+1); //check for reverse pattern
                    } else {
                        patternCounts.put(pattern, 1);
                    }
                }
            }
        }
        return patternCounts;
    }

    /**
     * For each occurrence count, drops patterns that are strict subsets of another pattern with the
     * same count (considering possible side swapping in pattern comparison), and those with count below threshold.
     */
    public static void filterLowCountAndSubsets(
            Map<ReactantPattern, Integer> patternsWithCounts, Integer minCount) {

        if(minCount==null) minCount = 0;

        //index patterns by count
        Map<Integer, List<ReactantPattern>> patternsByCount = new HashMap<>();
        for (Map.Entry<ReactantPattern, Integer> entry : patternsWithCounts.entrySet()) {
            patternsByCount.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }
        //for patterns with same count, 
        for (Map.Entry<Integer, List<ReactantPattern>> grouped : patternsByCount.entrySet()) {
            List<ReactantPattern> groupPatterns = grouped.getValue();
            //(above the min size)
            if(grouped.getKey()<minCount){
                groupPatterns.forEach(patternsWithCounts::remove);
            }else{
                groupPatterns.sort(Comparator.comparing(r -> -1*(r.right().size()+r.left().size())));
                //starting with largest patterns...
                for (int i = 0; i< groupPatterns.size(); i++) {
                    ReactantPattern candidate = groupPatterns.get(i);
                    // ... against all smaller patterns...
                    for (int j = i+1; j< groupPatterns.size(); j++) {
                        ReactantPattern potentialSubset = groupPatterns.get(j);
                        if (patternStrictlyDominates(candidate, potentialSubset)){
                            //remove smaller pattern if it is a strict subset of the candidate (considering possible side swapping)
                            patternsWithCounts.remove(potentialSubset);
                        }
                    }
                }
            }
        }
    }

    private static boolean patternStrictlyDominates(ReactantPattern sup, ReactantPattern sub) {
        return setStrictlyDominates(sup.left(), sup.right(), sub.left(), sub.right())
                || setStrictlyDominates(sup.left(), sup.right(), sub.right(), sub.left());
    }
    private static boolean setStrictlyDominates(BioCollection<BioMetabolite> supA,
                                               BioCollection<BioMetabolite> supB,
                                               BioCollection<BioMetabolite> subA,
                                               BioCollection<BioMetabolite> subB) {
        boolean includesA = supA.containsAll(subA);
        boolean includesB = supB.containsAll(subB);
        boolean strict = supA.size() > subA.size() || supB.size() > subB.size();
        return includesA && includesB && strict;
    }

    /**
     * Generates all non-empty subsets of size 1 to {@code maxSize} from the given list of IDs.
     */
    private static List<BioCollection<BioMetabolite>> generateSubsets(BioCollection<BioMetabolite> elements, int maxSize) {
        List<Set<BioMetabolite>> result = new ArrayList<>();
        List<BioMetabolite> elementsList = new ArrayList<>(elements);
        int limit = Math.min(elements.size(), maxSize);
        for (int size = 1; size <= limit; size++) {
            collectCombinations(elementsList, 0, size, new ArrayList<>(), result);
        }
        List<BioCollection<BioMetabolite>> bioResult = new ArrayList<>();
        for(Set<BioMetabolite> s : result){
            bioResult.add(new BioCollection<>(s));
        }
        return bioResult;
    }

    private static void collectCombinations(List<BioMetabolite> elements, int start, int remaining,
                                            List<BioMetabolite> current, List<Set<BioMetabolite>> result) {
        if (remaining == 0) {
            result.add(new HashSet<>(current));
            return;
        }
        for (int i = start; i <= elements.size() - remaining; i++) {
            current.add(elements.get(i));
            collectCombinations(elements, i + 1, remaining - 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
