package fr.inrae.toulouse.metexplore.met4j_mapping;

import fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching.ChemicalAliasCreator;
import fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching.EditDistance;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EditDistanceTest {

    @Test
    public void testEditDistance1(){
        EditDistance dist = new EditDistance(5);
        assertTrue(dist.asDistance());
        assertEquals(1.0,dist.getSimilarity("aaa","aab"),Double.MAX_VALUE);
        assertEquals(2.0,dist.getSimilarity("aaa","aaaaa"),Double.MAX_VALUE);
        assertEquals(2.0,dist.getSimilarity("aaa","a"),Double.MAX_VALUE);
        assertEquals(1.0,dist.getSimilarity("aab","aaa"),Double.MAX_VALUE);
        assertEquals(2.0,dist.getSimilarity("aaaaa","aaa"),Double.MAX_VALUE);
        assertEquals(2.0,dist.getSimilarity("a","aaa"),Double.MAX_VALUE);
        assertEquals(1.0,dist.getSimilarity("aaa","aa\""),Double.MAX_VALUE);
        assertEquals(5.0,dist.getSimilarity("a","abcdefghijklmnopqr"),Double.MAX_VALUE);
    }
}
