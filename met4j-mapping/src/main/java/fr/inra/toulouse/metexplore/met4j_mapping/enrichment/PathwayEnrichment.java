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

package fr.inra.toulouse.metexplore.met4j_mapping.enrichment;

import fr.inra.toulouse.metexplore.met4j_core.biodata.*;
import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * compute Pathway enrichment statistic using one-tailed exact Fisher Test
 * @author clement
 */
public class PathwayEnrichment {

    /** The BONFERRONI adjustment method code. */
    public static final int BONFERRONI=0;

    /** The BENJAMINIHOCHBERG adjustment method code. */
    public static final int BENJAMINIHOCHBERG=1;

    /** The HOLMBONFERRONI adjustment method code. */
    public static final int HOLMBONFERRONI=2;

    /** The BioNetwork. */
    BioNetwork bn;

    /** The reaction set. */
    BioCollection<BioReaction> reactionSet;

    /**
     * Instantiates a new pathway enrichment.
     *
     * @param bn the bioNetwork
     * @param entities 
     */
    public PathwayEnrichment(BioNetwork bn, Set<? extends BioEntity> entities) {
        this.bn=bn;
//		
        this.reactionSet=new BioCollection<BioReaction>();
        for(BioEntity e : entities){
            if(e instanceof BioReaction){
                reactionSet.add((BioReaction) e);
            }else if(e instanceof BioMetabolite){
                BioMetabolite m = (BioMetabolite) e;
                reactionSet.addAll(bn.getReactionsFromSubstrate(m));
                reactionSet.addAll(bn.getReactionsFromProduct(m));
            }
        }
    }

    /**
     * Computes the enrichment : for each pathway's reaction found in the reaction set pass in constructor,
     * compute the pvalue corresponding to the probability that those occurrences where due to randomness
     *
     * @return the hash map with pathway as key and pvalue as value
     */
    public HashMap<BioPathway, Double> computeEnrichment(){
        BioCollection<BioPathway> paths = new BioCollection<BioPathway>();
        for(BioReaction r : reactionSet){
            paths.addAll(this.bn.getPathwaysFromReaction(r));
        }

        HashMap<BioPathway, Double> res = new HashMap<BioPathway, Double>();
//		for(BioPathway p : bn.getPathwaysView().values()){
        for(BioPathway p : paths){
            res.put(p, getPvalue(p));
        }
        return res;
    }

    /**
     * Computes the enrichment using a multiple testing p-value adjustment
     *
     * @param adjustmentMethod the adjustment method int code
     * @return the hash map with pathway as key and pvalue as value
     */
    public HashMap<BioPathway, Double> computeEnrichment(int adjustmentMethod){
        HashMap<BioPathway, Double> res = computeEnrichment();
        switch(adjustmentMethod)
        {
            case PathwayEnrichment.BONFERRONI:
                res = bonferroniCorrection(res);
                break;
            case PathwayEnrichment.BENJAMINIHOCHBERG:
                res = benjaminiHochbergCorrection(res);
                break;
            case PathwayEnrichment.HOLMBONFERRONI:
                res =  holmBonferroniCorrection(res);
                break;
            default:
                res = bonferroniCorrection(res);
                break;
        }
        return res;
    }

    /**
     * Gets the enrichment p-value for a given pathway.
     *
     * @param pathway the pathway
     * @return the enrichment p-value
     * @throws IllegalArgumentException if pathway not found in network
     */
    public double getPvalue(BioPathway pathway) throws IllegalArgumentException{
        if(!bn.getPathwaysView().contains(pathway)){
            throw new IllegalArgumentException("pathway not in network");
        }
        Collection<BioReaction> reactionInPathway = bn.getReactionsFromPathway(pathway);

        //build contingency table
        int a = intersect(reactionSet, reactionInPathway).size();
        int b = reactionSet.size()-a;
        int c = reactionInPathway.size()-a;
        int d = bn.getReactionsView().size()-(a+b+c);
        return exactFisherOneTailed(a,b,c,d);
    }

    /**
     * Compute the intersection of two collections of reactions (sample and pathway for example).
     *
     * @param set1 the first set
     * @param set2 the second set
     * @return the intersection of the two sets
     */
    public static HashSet<BioReaction> intersect(Collection<BioReaction> set1, Collection<BioReaction> set2){
        HashSet<BioReaction> inter = new HashSet<BioReaction>();
        for(BioReaction r : set1){
            if(set2.contains(r)) inter.add(r);
        }
        return inter;
    }

    /**
     * Compute hypergeometric probability from contingency table entries.
     * value in cells correspond to the marginal totals of each intersection groups
     *				Query	!Query
     *	Target		a		b
     *	!Target		c		d
     *
     * The probability of obtaining the set of value is computed as following:
     * p = ((a+b)!(c+d)!(a+c)!(b+d)!)/(a!b!c!d!(a+b+c+d)!)
     *
     * @param a the number of elements in target set also found in query set
     * @param b the number of elements in target set not found in query set
     * @param c the number of elements in query set not found in target set
     * @param d the number of elements not in target set nor in query set
     * @return the probability of obtaining the set of value
     */
    public static double getHypergeometricProba(int a, int b, int c, int d){
        BigDecimal numerator = fact(a+b).multiply(fact(c+d)).multiply(fact(a+c)).multiply(fact(b+d));
        BigDecimal denominator = fact(a).multiply(fact(b)).multiply(fact(c)).multiply(fact(d)).multiply(fact(a+b+c+d));
        BigDecimal res = numerator.divide(denominator, MathContext.DECIMAL64);

        return res.doubleValue();
    }

    /**
     * Compute exact fisher test focused on over-representation
     * the fisher exact test compute the probability p to randomly get the given set of value
     * this version compute the probability to get at least the given overlap between the given set and the given modality :
     * sum the hypergeometric probability with increasing target/query intersection cardinality
     * @param a the number of elements in target set also found in query set
     * @param b the number of elements in target set not found in query set
     * @param c the number of elements in query set not found in target set
     * @param d the number of elements not in target set nor in query set
     * @return the probability of obtaining at least the same overlap between target and query
     */
    public static double exactFisherOneTailed(int a, int b, int c, int d){
        double res = 0.0;
        int lim = Math.min(a+c, a+b);
        for(int i=0; a+i<=lim; i++){
            res+=getHypergeometricProba(a+i, b-i, c-i, d+i);
        }
        return res;
    }

    /**
     * Compute the factorial of a number
     *
     * @param n the number
     * @return the BigDecimal corresponding to n!
     */
    public static BigDecimal fact(int n){
        BigDecimal fact = new BigDecimal("1");
        for (int i = 1; i <= n; i++) {
            fact = fact.multiply(new BigDecimal(i + ""));
        }
        return fact;
    }

    /**
     * Bonferroni p-value adjustment for multiple testing.
     * adjusted p-value = p*n	, n : number of tests
     *
     * @param pvalues the pvalues
     * @return the hash map
     */
    public HashMap<BioPathway, Double> bonferroniCorrection(HashMap<BioPathway, Double> pvalues){
        HashMap<BioPathway, Double> adjPvalues = new HashMap<BioPathway, Double>();
        for(BioPathway p : pvalues.keySet()){
            double pval = pvalues.get(p);
            double adjPval = pval*pvalues.size();
            adjPvalues.put(p, new Double(adjPval));
        }
        return adjPvalues;
    }

    /**
     * Benjamini-Hochberg p-value adjustment for multiple testing.
     * adjusted p-value = p*n/k	, n : number of tests; k : pvalue rank
     *
     * @param pvalues the pvalues
     * @return the hash map
     */
    public HashMap<BioPathway, Double> benjaminiHochbergCorrection(HashMap<BioPathway, Double> pvalues){
        ArrayList<BioPathway> orderedPaths = sortPval(pvalues);
        HashMap<BioPathway, Double> adjPvalues = new HashMap<BioPathway, Double>();
        for(int k=0;k<orderedPaths.size();k++){
            BioPathway p = orderedPaths.get(k);
            double pval = pvalues.get(p);
            double adjPval = pval*pvalues.size()/(k+1);
            adjPvalues.put(p, new Double(adjPval));
        }
        return adjPvalues;
    }

    /**
     * Holm-Bonferroni p-value adjustment for multiple testing.
     * adjusted p-value = p*(n+1-k)	, n : number of tests; k : pvalue rank
     *
     * @param pvalues the pvalues
     * @return the hash map
     */
    public HashMap<BioPathway, Double> holmBonferroniCorrection(HashMap<BioPathway, Double> pvalues){
        ArrayList<BioPathway> orderedPaths = sortPval(pvalues);
        HashMap<BioPathway, Double> adjPvalues = new HashMap<BioPathway, Double>();
        for(int k=0;k<orderedPaths.size();k++){
            BioPathway p = orderedPaths.get(k);
            double pval = pvalues.get(p);
            double adjPval = pval*(pvalues.size()+1-(k+1));
            adjPvalues.put(p, new Double(adjPval));
        }
        return adjPvalues;
    }

    /**
     * Sort pathway according to their significance
     *
     * @param map the map containing pathway as key and p-value as value
     * @return the ordered list of pathway
     */
    public ArrayList<BioPathway> sortPval(HashMap<BioPathway, Double> map){

        ArrayList<BioPathway> orderedPaths = new ArrayList<BioPathway>(map.keySet());
        Collections.sort(orderedPaths, new significanceComparator(map));

        return orderedPaths;
    }

    /**
     * The significance comparator class use for ranking
     */
    static class significanceComparator implements Comparator<BioPathway>{

        /** The pval map. */
        HashMap<BioPathway, Double> pvalMap;

        /**
         * Instantiates a new significance comparator.
         *
         * @param pvalMap the map containing pathway as key and corresponding p-value as value
         */
        public significanceComparator(HashMap<BioPathway, Double> pvalMap){
            this.pvalMap=pvalMap;
        }

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(BioPathway o1, BioPathway o2) {
            return Double.compare(pvalMap.get(o1),pvalMap.get(o2));
        }
    }

}
