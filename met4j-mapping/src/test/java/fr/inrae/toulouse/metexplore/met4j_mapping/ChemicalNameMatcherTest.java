package fr.inrae.toulouse.metexplore.met4j_mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching.ChemicalAliasCreator;
import fr.inrae.toulouse.metexplore.met4j_mapping.fuzzyMatching.ChemicalNameMatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChemicalNameMatcherTest {

    public static ChemicalNameMatcher matcher;

    @Before
    public void init() {
        HashSet<String> res = new HashSet<>();
        res.add("Fructose");
        res.add("Glucose");
        res.add("pentanoic acid");
        matcher = new ChemicalNameMatcher.Builder(res)
                .DefaultProcessing()
                .build();
    }

    @Test
    public void testBestHit(){
        assertEquals("Glucose",matcher.getBestHit("alpha-D-Glucopyranose",5.0));
    }

    @Test
    public void testGetMatches(){
        List<String> match = matcher.getMatches("alpha-D-Glucopyranose",6.0,15);
        assertEquals(2,match.size());
        assertEquals("Glucose",match.get(0));
        assertEquals("Fructose",match.get(1));
    }

    @Test
    public void testGetMatchesthreshold(){
        List<String> match = matcher.getMatches("alpha-D-Glucopyranose",3.0,15);
        assertEquals(1,match.size());
        assertEquals("Glucose",match.get(0));
    }

    @Test
    public void testSearch(){
        Map<String,Double> search = matcher.search("alpha-D-Glucopyranose");
        assertEquals(3,search.size());
        assertEquals(2,search.get("Glucose"),Double.MIN_VALUE);
        assertEquals(5,search.get("Fructose"),Double.MIN_VALUE);
        assertEquals(8,search.get("pentanoic acid"),Double.MIN_VALUE);
    }
}
