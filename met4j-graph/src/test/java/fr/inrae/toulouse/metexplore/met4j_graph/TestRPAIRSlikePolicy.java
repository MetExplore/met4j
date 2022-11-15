package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_chemUtils.chemicalSimilarity.FingerprintBuilder;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.AtomMappingWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.DefaultWeightPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.connect.weighting.RPAIRSlikePolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.WeightingPolicy;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.similarity.Tanimoto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRPAIRSlikePolicy {

    public static CompoundGraph g;

    public static BioMetabolite m1sm,m1ss,m1ps,m1pm;
    public static BioMetabolite m2sm1,m2sm2,m2ss,m2pm,m2ps1,m2ps2;
    public static Map<BioMetabolite, Map<BioMetabolite,Integer>> aam;
    public static ReactionEdge t1m,t1s1,t1sp1,t1sp2;
    public static ReactionEdge t2sp1,t2sp2,t2sp3,t2sp4,t2sp5,t2sp6,t2s1,t2m1,t2m2;

    public static BioReaction r1,r2;

    @BeforeClass
    public static void init(){
        //create graph
        g = new CompoundGraph();

        //create reaction 1
        r1 = new BioReaction("1");
        aam = new HashMap<BioMetabolite, Map<BioMetabolite,Integer>>();
        //add substrate metabolites
        m1sm = new BioMetabolite("glucose","glucose");
            g.addVertex(m1sm);
            aam.put(m1sm,new HashMap<>());
        m1ss = new BioMetabolite("atp","atp");
            g.addVertex(m1ss);
            aam.put(m1ss,new HashMap<>());
        //add product metabolites
        m1ps = new BioMetabolite("adp","adp");
            g.addVertex(m1ps);
        m1pm = new BioMetabolite("glucose6p","glucose-6-P");
            g.addVertex(m1pm);
        //add edges
            //main
        t1m = new ReactionEdge(m1sm,m1pm,r1);
            g.addEdge(m1sm,m1pm,t1m);
            aam.get(m1sm).put(m1pm,6);
            //side
        t1s1 = new ReactionEdge(m1ss,m1ps,r1);
            g.addEdge(m1ss,m1ps,t1s1);
            aam.get(m1ss).put(m1ps,10);
            //spurious
        t1sp1 = new ReactionEdge(m1sm,m1ps,r1);
            g.addEdge(m1sm,m1ps,t1sp1);
            aam.get(m1sm).put(m1ps,0);
        t1sp2 = new ReactionEdge(m1ss,m1pm,r1);
            g.addEdge(m1ss,m1pm,t1sp2);
            aam.get(m1ss).put(m1pm,0);
        //create reaction 2
        r2 = new BioReaction("2");
        //add substrate metabolites
        m2sm1 = new BioMetabolite("propanoate","propanoate");
            g.addVertex(m2sm1);
            aam.put(m2sm1,new HashMap<>());
        m2sm2 = new BioMetabolite("coa","coa");
            g.addVertex(m2sm2);
            aam.put(m2sm2,new HashMap<>());
        m2ss = m1ss;
        //add product metabolites
        m2pm = new BioMetabolite("propanoylcoa","propanoyl-COA");
            g.addVertex(m2pm);
        m2ps1 = new BioMetabolite("pi","pi");
            g.addVertex(m2ps1);
        m2ps2 = m1ps;
        //add edges
            //main
        t2m1 = new ReactionEdge(m2sm1,m2pm,r2);
            g.addEdge(m2sm1,m2pm,t2m1);
            aam.get(m2sm1).put(m2pm,3);
        t2m2 = new ReactionEdge(m2sm2,m2pm,r2);
            g.addEdge(m2sm2,m2pm,t2m2);
            aam.get(m2sm2).put(m2pm,21);
            //side
        t2s1 = new ReactionEdge(m2ss,m2ps2,r2);
            g.addEdge(m2ss,m2ps2,t2s1);
            aam.get(m2ss).put(m2ps2,10);
            //spurious
        t2sp1 = new ReactionEdge(m2sm1,m2ps1,r2);
            g.addEdge(m2sm1,m2ps1,t2sp1);
            aam.get(m2sm1).put(m2ps1,0);
        t2sp2 = new ReactionEdge(m2sm1,m2ps2,r2);
            g.addEdge(m2sm1,m2ps2,t2sp2);
            aam.get(m2sm1).put(m2ps2,0);
        t2sp3 = new ReactionEdge(m2sm2,m2ps1,r2);
            g.addEdge(m2sm2,m2ps1,t2sp3);
            aam.get(m2sm2).put(m2ps1,0);
        t2sp4 = new ReactionEdge(m2sm2,m2ps2,r2);
            g.addEdge(m2sm2,m2ps2,t2sp4);
            aam.get(m2sm2).put(m2ps2,0);
        t2sp5 = new ReactionEdge(m2ss,m2pm,r2);
            g.addEdge(m2ss,m2pm,t2sp5);
            aam.get(m2ss).put(m2pm,0);
        t2sp6 = new ReactionEdge(m2ss,m2ps1,r2);
            g.addEdge(m2ss,m2ps1,t2sp6);
            aam.get(m2ss).put(m2ps1,0);
    }

    /**
     * Reset weight.
     */
    @After
    public void resetWeight(){
        init();
    }

    @Test
    public void testRPAIRSlikeWeightPolicy() {
        AtomMappingWeightPolicy preprocess = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(aam);
        RPAIRSlikePolicy wp = new RPAIRSlikePolicy(preprocess).removeSpuriousTransitions();
        wp.setWeight(g);

        assertEquals(1.0,g.getEdgeWeight(t1m),Double.MIN_VALUE);
        assertEquals(1.0,g.getEdgeWeight(t2m1),Double.MIN_VALUE);
        assertEquals(1.0,g.getEdgeWeight(t2m2),Double.MIN_VALUE);

        assertEquals(0.0,g.getEdgeWeight(t1s1),Double.MIN_VALUE);
        assertEquals(0.0,g.getEdgeWeight(t2s1),Double.MIN_VALUE);

        assertTrue(!g.containsEdge(t1sp1));
        assertTrue(!g.containsEdge(t1sp2));
        assertTrue(!g.containsEdge(t2sp1));
        assertTrue(!g.containsEdge(t2sp2));
        assertTrue(!g.containsEdge(t2sp3));
        assertTrue(!g.containsEdge(t2sp4));
        assertTrue(!g.containsEdge(t2sp5));
        assertTrue(!g.containsEdge(t2sp6));

    }

    @Test
    public void testRPAIRSlikeWeightPolicyII() {
        AtomMappingWeightPolicy preprocess = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(aam);
        RPAIRSlikePolicy wp = new RPAIRSlikePolicy(preprocess);
        wp.setWeight(g);

        assertEquals(1.0,g.getEdgeWeight(t1m),Double.MIN_VALUE);
        assertEquals(1.0,g.getEdgeWeight(t2m1),Double.MIN_VALUE);
        assertEquals(1.0,g.getEdgeWeight(t2m2),Double.MIN_VALUE);

        assertEquals(0.0,g.getEdgeWeight(t1s1),Double.MIN_VALUE);
        assertEquals(0.0,g.getEdgeWeight(t2s1),Double.MIN_VALUE);

        assertEquals(-1.0,g.getEdgeWeight(t1sp1),Double.MIN_VALUE);
        assertEquals(-1.0,g.getEdgeWeight(t1sp2),Double.MIN_VALUE);
        assertEquals(-1.0,g.getEdgeWeight(t2sp1),Double.MIN_VALUE);
        assertEquals(-1.0,g.getEdgeWeight(t2sp2),Double.MIN_VALUE);
        assertEquals(-1.0,g.getEdgeWeight(t2sp3),Double.MIN_VALUE);
        assertEquals(-1.0,g.getEdgeWeight(t2sp4),Double.MIN_VALUE);
        assertEquals(-1.0,g.getEdgeWeight(t2sp5),Double.MIN_VALUE);
        assertEquals(-1.0,g.getEdgeWeight(t2sp6),Double.MIN_VALUE);

    }

    @Test
    public void testRPAIRSlikeWeightPolicyIII() {
        AtomMappingWeightPolicy preprocess = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(aam);
        RPAIRSlikePolicy wp = new RPAIRSlikePolicy(preprocess).removeSideTransitions();
        wp.setWeight(g);

        assertEquals(1.0,g.getEdgeWeight(t1m),Double.MIN_VALUE);
        assertEquals(1.0,g.getEdgeWeight(t2m1),Double.MIN_VALUE);
        assertEquals(1.0,g.getEdgeWeight(t2m2),Double.MIN_VALUE);

        assertTrue(!g.containsEdge(t1s1));
        assertTrue(!g.containsEdge(t2s1));

        assertTrue(!g.containsEdge(t1sp1));
        assertTrue(!g.containsEdge(t1sp2));
        assertTrue(!g.containsEdge(t2sp1));
        assertTrue(!g.containsEdge(t2sp2));
        assertTrue(!g.containsEdge(t2sp3));
        assertTrue(!g.containsEdge(t2sp4));
        assertTrue(!g.containsEdge(t2sp5));
        assertTrue(!g.containsEdge(t2sp6));

    }

    @Test
    public void testGetMainComponent() {
        AtomMappingWeightPolicy preprocess = new AtomMappingWeightPolicy().fromNumberOfConservedCarbons(aam);
        RPAIRSlikePolicy wp = new RPAIRSlikePolicy(preprocess);
        Set<ReactionEdge> spuriousEdges = new HashSet<>();
        spuriousEdges.add(t1sp1);
        spuriousEdges.add(t1sp2);
        spuriousEdges.add(t2sp1);
        spuriousEdges.add(t2sp2);
        spuriousEdges.add(t2sp3);
        spuriousEdges.add(t2sp4);
        spuriousEdges.add(t2sp5);
        spuriousEdges.add(t2sp6);

        Set<BioMetabolite> cc = wp.getMainComponent(g,r1, spuriousEdges);
        assertTrue(cc.contains(m1sm));
        assertTrue(cc.contains(m1pm));

        Set<BioMetabolite> cc2 = wp.getMainComponent(g,r2, spuriousEdges);
        assertTrue(cc2.contains(m2sm1));
        assertTrue(cc2.contains(m2sm2));
        assertTrue(cc2.contains(m2pm));
    }

}