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

package fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity;

import java.util.BitSet;

public class SimilarityComputor {

    /**
     * Instantiates a new similarity computor.
     */
    private SimilarityComputor() {}

    /**
     * compute the Tanimoto coefficient
     * also known as Jaccard index
     * sim(A,B) = c/(a+b-c) = |A n B|/|A u B|
     *
     * @param fingerprint1 the first chemical fingerprint
     * @param fingerprint2 the second chemical fingerprint
     * @return the tanimoto coefficient
     * @throws IllegalArgumentException
     */
    public static double getTanimoto(BitSet fingerprint1, BitSet fingerprint2) throws IllegalArgumentException{
        if(fingerprint1.size()!=fingerprint2.size()) throw new IllegalArgumentException("bitSets must have the same size");
        BitSet intersect = (BitSet) fingerprint1.clone();
        intersect.and(fingerprint2);
        double a = Integer.valueOf(fingerprint1.cardinality()).doubleValue();
        double b = Integer.valueOf(fingerprint2.cardinality()).doubleValue();
        double c = Integer.valueOf(intersect.cardinality()).doubleValue();
        double dist = c/(a+b-c);
        return dist;
    }

    /**
     * compute the Dice coefficient
     * also known as the Czekanowski coefficent or the Sorenson coefficient
     * sim(A,B) = 2c/(a+b) = 2|A n B|/(|A|+|B|)
     *
     * @param fingerprint1 the first chemical fingerprint
     * @param fingerprint2 the second chemical fingerprint
     * @return the dice coefficient
     * @throws IllegalArgumentException
     */
    public static double getDiceCoeff(BitSet fingerprint1, BitSet fingerprint2) throws IllegalArgumentException{
        if(fingerprint1.size()!=fingerprint2.size()) throw new IllegalArgumentException("bitSets must have the same size");
        BitSet intersect = (BitSet) fingerprint1.clone();
        intersect.and(fingerprint2);
        double a = Integer.valueOf(fingerprint1.cardinality()).doubleValue();
        double b = Integer.valueOf(fingerprint2.cardinality()).doubleValue();
        double c = Integer.valueOf(intersect.cardinality()).doubleValue();
        double dist = (2*c)/(a+b);
        return dist;
    }

    /**
     * compute the cosine coefficient
     * also known as the Ochiai coeficient
     * sim(A,B) = c/sqrt(a*b)
     *
     * @param fingerprint1 the first chemical fingerprint
     * @param fingerprint2 the second chemical fingerprint
     * @return the cosine coefficient
     * @throws IllegalArgumentException
     */
    public static double getCosineCoeff(BitSet fingerprint1, BitSet fingerprint2) throws IllegalArgumentException{
        if(fingerprint1.size()!=fingerprint2.size()) throw new IllegalArgumentException("bitSets must have the same size");
        BitSet intersect = (BitSet) fingerprint1.clone();
        intersect.and(fingerprint2);
        double a = Integer.valueOf(fingerprint1.cardinality()).doubleValue();
        double b = Integer.valueOf(fingerprint2.cardinality()).doubleValue();
        double c = Integer.valueOf(intersect.cardinality()).doubleValue();
        double dist = c/(Math.sqrt(a*b));
        return dist;
    }

    /**
     * compute the Manhattan distance
     * also known as Hamming Distance or City-block distance
     * dist(A,B) = a + b - 2c = |A u B| - |A n B|
     *
     * @param fingerprint1 the first chemical fingerprint
     * @param fingerprint2 the second chemical fingerprint
     * @return the manhattan distance
     * @throws IllegalArgumentException
     */
    public static double getManhattanDist(BitSet fingerprint1, BitSet fingerprint2) throws IllegalArgumentException{
        if(fingerprint1.size()!=fingerprint2.size()) throw new IllegalArgumentException("bitSets must have the same size");
        BitSet intersect = (BitSet) fingerprint1.clone();
        intersect.and(fingerprint2);
        double a = Integer.valueOf(fingerprint1.cardinality()).doubleValue();
        double b = Integer.valueOf(fingerprint2.cardinality()).doubleValue();
        double c = Integer.valueOf(intersect.cardinality()).doubleValue();
        double dist = a+b-(2*c);
        return dist;
    }

    /**
     * compute the Euclidean distance
     * dist(A,B) = sqrt(a + b - 2c) = sqrt(|A u B| - |A n B|)
     *
     * @param fingerprint1 the first chemical fingerprint
     * @param fingerprint2 the second chemical fingerprint
     * @return the euclidean distance
     * @throws IllegalArgumentException
     */
    public static double getEuclideanDist(BitSet fingerprint1, BitSet fingerprint2) throws IllegalArgumentException{
        if(fingerprint1.size()!=fingerprint2.size()) throw new IllegalArgumentException("bitSets must have the same size");
        BitSet intersect = (BitSet) fingerprint1.clone();
        intersect.and(fingerprint2);
        double a = Integer.valueOf(fingerprint1.cardinality()).doubleValue();
        double b = Integer.valueOf(fingerprint2.cardinality()).doubleValue();
        double c = Integer.valueOf(intersect.cardinality()).doubleValue();
        double dist = Math.sqrt(a+b-(2*c));
        return dist;
    }

    /**
     * compute the Soergel distance
     * dist(A,B) = 1-c/(a+b-c)
     * range 1 to 0
     * complement of tanimoto coefficient
     *
     * @param fingerprint1 the first chemical fingerprint
     * @param fingerprint2 the second chemical fingerprint
     * @return the soergel distance
     * @throws IllegalArgumentException
     */
    public static double getSoergelDist(BitSet fingerprint1, BitSet fingerprint2) throws IllegalArgumentException{
        if(fingerprint1.size()!=fingerprint2.size()) throw new IllegalArgumentException("bitSets must have the same size");
        BitSet intersect = (BitSet) fingerprint1.clone();
        intersect.and(fingerprint2);
        double a = Integer.valueOf(fingerprint1.cardinality()).doubleValue();
        double b = Integer.valueOf(fingerprint2.cardinality()).doubleValue();
        double c = Integer.valueOf(intersect.cardinality()).doubleValue();
        double dist = (a+b-(2*c))/(a+b-c);
        return dist;
    }


    /**
     * return the number of differences between two bit set
     *
     * @param fingerprint1 the first chemical fingerprint
     * @param fingerprint2 the second chemical fingerprint
     * @return the xor distance
     * @throws IllegalArgumentException
     */
    public static double getXorDist(BitSet fingerprint1, BitSet fingerprint2) throws IllegalArgumentException{
        if(fingerprint1.size()!=fingerprint2.size()) throw new IllegalArgumentException("bitSets must have the same size");
        BitSet xor = (BitSet) fingerprint1.clone();
        xor.xor(fingerprint2);
        return Integer.valueOf(xor.cardinality()).doubleValue();
    }

}
