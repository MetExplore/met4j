package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.PathwayTopologyAnalysis;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.EigenVectorCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.PathBasedCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestPathwayTopologyAnalysis {

    public static CompoundGraph toyGraph;
    public static BioNetwork toyNetwork;
    public static BioMetabolite a, b, c, d, e, f, h, g;
    public static BioReaction r1,r2,r3,r4,r5,r6,r7,r8;
    public static BioPathway x,y,z;
    public static BioCompartment na;
    public static PathBasedCentrality<BioMetabolite, ReactionEdge, CompoundGraph> toyMeasure;

    @BeforeClass
    public static void init() {

        a = new BioMetabolite("a");
        b = new BioMetabolite("b");
        c = new BioMetabolite("c");
        d = new BioMetabolite("d");
        e = new BioMetabolite("e");
        f = new BioMetabolite("f");
        h = new BioMetabolite("h");
        g = new BioMetabolite("g");

        r1 = new BioReaction("r1");r1.setReversible(true);
        r2 = new BioReaction("r2");r2.setReversible(true);
        r3 = new BioReaction("r3");r3.setReversible(true);
        r4 = new BioReaction("r4");r4.setReversible(true);
        r5 = new BioReaction("r5");r5.setReversible(true);
        r6 = new BioReaction("r6");r6.setReversible(true);
        r7 = new BioReaction("r7");r7.setReversible(true);
        r8 = new BioReaction("r8");r8.setReversible(true);

        x = new BioPathway("x");
        y = new BioPathway("y");
        z = new BioPathway("z");

        na = new BioCompartment("NA");

        ReactionEdge ab = new ReactionEdge(a, b, r1);
        ReactionEdge ba = new ReactionEdge(b, a, r1);
        ReactionEdge bc = new ReactionEdge(b, c, r2);
        ReactionEdge cb = new ReactionEdge(c, b, r2);
        ReactionEdge cd = new ReactionEdge(c, d, r3);
        ReactionEdge dc = new ReactionEdge(d, c, r3);
        ReactionEdge de = new ReactionEdge(d, e, r4);
        ReactionEdge ed = new ReactionEdge(e, d, r4);
        ReactionEdge ec = new ReactionEdge(e, c, r5);
        ReactionEdge ce = new ReactionEdge(c, e, r5);
        ReactionEdge af = new ReactionEdge(a, f, r6);
        ReactionEdge fa = new ReactionEdge(f, a, r6);
        ReactionEdge fg = new ReactionEdge(f, g, r7);
        ReactionEdge gf = new ReactionEdge(g, f, r7);
        ReactionEdge ga = new ReactionEdge(g, a, r8);
        ReactionEdge ag = new ReactionEdge(a, g, r8);

        toyNetwork=new BioNetwork();
        toyNetwork.add(a,b,c,d,e,f,g);
        toyNetwork.add(r1,r2,r3,r4,r5,r6,r7,r8);
        toyNetwork.add(x,y,z);
        toyNetwork.add(na);

        toyNetwork.affectToCompartment(na,a,b,c,d,e,f,g);

        toyNetwork.affectLeft(r1,1.0,na,a);
        toyNetwork.affectRight(r1,1.0,na,b);
        toyNetwork.affectLeft(r2,1.0,na,b);
        toyNetwork.affectRight(r2,1.0,na,c);
        toyNetwork.affectLeft(r3,1.0,na,c);
        toyNetwork.affectRight(r3,1.0,na,d);
        toyNetwork.affectLeft(r4,1.0,na,d);
        toyNetwork.affectRight(r4,1.0,na,e);
        toyNetwork.affectLeft(r5,1.0,na,e);
        toyNetwork.affectRight(r5,1.0,na,c);
        toyNetwork.affectLeft(r6,1.0,na,a);
        toyNetwork.affectRight(r6,1.0,na,f);
        toyNetwork.affectLeft(r7,1.0,na,f);
        toyNetwork.affectRight(r7,1.0,na,g);
        toyNetwork.affectLeft(r8,1.0,na,g);
        toyNetwork.affectRight(r8,1.0,na,a);

        toyNetwork.affectToPathway(x,r1,r6,r7,r8);
        toyNetwork.affectToPathway(y,r2);
        toyNetwork.affectToPathway(z,r3,r4,r5);

        toyGraph = new CompoundGraph();
        toyGraph.addVertex(a);
        toyGraph.addVertex(b);
        toyGraph.addVertex(c);
        toyGraph.addVertex(d);
        toyGraph.addVertex(e);
        toyGraph.addVertex(f);
        toyGraph.addVertex(g);
        toyGraph.addEdge(a, b, ab);
        toyGraph.addEdge(b, c, bc);
        toyGraph.addEdge(c, d, cd);
        toyGraph.addEdge(d, e, de);
        toyGraph.addEdge(e, c, ec);
        toyGraph.addEdge(a, f, af);
        toyGraph.addEdge(f, g, fg);
        toyGraph.addEdge(g, a, ga);
        toyGraph.addEdge(b, a, ba);
        toyGraph.addEdge(c, b, cb);
        toyGraph.addEdge(d, c, dc);
        toyGraph.addEdge(e, d, ed);
        toyGraph.addEdge(c, e, ce);
        toyGraph.addEdge(f, a, fa);
        toyGraph.addEdge(g, f, gf);
        toyGraph.addEdge(a, g, ag);

        toyMeasure = new PathBasedCentrality<>(toyGraph);
    }

    @Test
    public void testBetweenness() {

        HashSet<BioMetabolite> noi = new HashSet<>();
        noi.add(a);
        noi.add(b);
        noi.add(e);
        PathwayTopologyAnalysis analysis = new PathwayTopologyAnalysis(toyNetwork,toyGraph,noi);

        Map<BioMetabolite, Integer> toyBetweenness = toyMeasure.getGeodesicBetweenness();

        Map<BioPathway,Double> res = analysis.run(PathwayTopologyAnalysis.IndividualScoringStrategy.betweenness(),
                PathwayTopologyAnalysis.AggregationStrategy.rawSum());

        assertEquals(toyBetweenness.get(a)+toyBetweenness.get(b), res.get(x), 0.00000001);
        assertEquals(toyBetweenness.get(b), res.get(y),  0.00000001);
        assertEquals(toyBetweenness.get(e), res.get(z), 0.00000001);
    }

    @Test
    public void testNormalization() {

        HashSet<BioMetabolite> noi = new HashSet<>();
        noi.add(a);
        noi.add(b);
        noi.add(e);
        PathwayTopologyAnalysis analysis = new PathwayTopologyAnalysis(toyNetwork,toyGraph,noi).useNormalization();

        Map<BioMetabolite, Integer> toyBetweenness = toyMeasure.getGeodesicBetweenness();

        Map<BioPathway,Double> res = analysis.run(PathwayTopologyAnalysis.IndividualScoringStrategy.betweenness(),
                PathwayTopologyAnalysis.AggregationStrategy.rawSum());


        assertEquals(Double.valueOf(toyBetweenness.get(a)+toyBetweenness.get(b)) / Double.valueOf(toyBetweenness.get(a)+toyBetweenness.get(b)+toyBetweenness.get(f)+toyBetweenness.get(g)), res.get(x), 0.00000001);
        assertEquals(Double.valueOf(toyBetweenness.get(b)) / Double.valueOf(toyBetweenness.get(b)+toyBetweenness.get(c)), res.get(y),  0.00000001);
        assertEquals(Double.valueOf(toyBetweenness.get(e)) / Double.valueOf(toyBetweenness.get(c)+toyBetweenness.get(d)+toyBetweenness.get(e)), res.get(z), 0.00000001);
    }

    @Test
    public void testBetweennessII() {

        HashSet<BioMetabolite> noi = new HashSet<>();
        noi.add(a);
        noi.add(b);
        PathwayTopologyAnalysis analysis = new PathwayTopologyAnalysis(toyNetwork,toyGraph,noi);

        Map<BioMetabolite, Integer> toyBetweenness = toyMeasure.getGeodesicBetweenness();

        Map<BioPathway,Double> res = analysis.run(PathwayTopologyAnalysis.IndividualScoringStrategy.betweenness(),
                PathwayTopologyAnalysis.AggregationStrategy.rawSum());

        assertEquals(toyBetweenness.get(a)+toyBetweenness.get(b), res.get(x), 0.00000001);
        assertEquals(toyBetweenness.get(b), res.get(y),  0.00000001);
        assertFalse(res.containsKey(z));
    }

    @Test
    public void testOutDegree() {

        HashSet<BioMetabolite> noi = new HashSet<>();
        noi.add(a);
        noi.add(b);
        noi.add(e);
        PathwayTopologyAnalysis analysis = new PathwayTopologyAnalysis(toyNetwork,toyGraph,noi);

        Map<BioPathway,Double> res = analysis.run(PathwayTopologyAnalysis.IndividualScoringStrategy.outDegree(),
                PathwayTopologyAnalysis.AggregationStrategy.rawSum());

        assertEquals(5, res.get(x), 0.00000001);
        assertEquals(2, res.get(y),  0.00000001);
        assertEquals(2,res.get(z),  0.00000001);
    }

    @Test
    public void testMapConstructor() {

        HashSet<BioMetabolite> noi = new HashSet<>();
        noi.add(a);
        noi.add(b);
        noi.add(e);
        HashMap<BioPathway, Collection<BioMetabolite>> kb = new HashMap<>();
        BioCollection<BioMetabolite> xCpds = new BioCollection<>();xCpds.add(g,f,a,b);kb.put(x,xCpds);
        BioCollection<BioMetabolite> yCpds = new BioCollection<>();yCpds.add(b,c);kb.put(y,yCpds);
        BioCollection<BioMetabolite> zCpds = new BioCollection<>();zCpds.add(c,e,d);kb.put(z,zCpds);


        PathwayTopologyAnalysis analysis = new PathwayTopologyAnalysis(kb,toyGraph,noi);

        Map<BioPathway,Double> res = analysis.run(PathwayTopologyAnalysis.IndividualScoringStrategy.outDegree(),
                PathwayTopologyAnalysis.AggregationStrategy.rawSum());

        assertEquals(5, res.get(x), 0.00000001);
        assertEquals(2, res.get(y),  0.00000001);
        assertEquals(2,res.get(z),  0.00000001);
    }

    @Test
    public void testNeighbours() {

        HashSet<BioMetabolite> noi = new HashSet<>();
        noi.add(a);
        noi.add(b);
        noi.add(e);
        PathwayTopologyAnalysis analysis = new PathwayTopologyAnalysis(toyNetwork,toyGraph,noi);

        Map<BioPathway,Double> res = analysis.run(PathwayTopologyAnalysis.IndividualScoringStrategy.neighbors(),
                PathwayTopologyAnalysis.AggregationStrategy.rawSum());

        assertEquals(5, res.get(x), 0.00000001);
        assertEquals(2, res.get(y),  0.00000001);
        assertEquals(2,res.get(z),  0.00000001);
    }

    @Test
    public void testPageRank() {


        HashSet<BioMetabolite> noi = new HashSet<>();
        noi.add(a);
        noi.add(b);
        noi.add(e);
        PathwayTopologyAnalysis analysis = new PathwayTopologyAnalysis(toyNetwork,toyGraph,noi);

        EigenVectorCentrality<BioMetabolite,ReactionEdge,CompoundGraph> toyMeasure2 = new EigenVectorCentrality<>(toyGraph);
        HashMap<String, Double> toyPageRank = toyMeasure2.computePowerMethodPageRank(0.85,15000,0.001);

        Map<BioPathway,Double> res = analysis.run(PathwayTopologyAnalysis.IndividualScoringStrategy.pageRank(0.85,15000,0.001),
                PathwayTopologyAnalysis.AggregationStrategy.rawSum());

        assertEquals(toyPageRank.get(a.getId())+toyPageRank.get(b.getId()), res.get(x), 0.00000001);
        assertEquals(toyPageRank.get(b.getId()), res.get(y),  0.00000001);
        assertEquals(toyPageRank.get(e.getId()), res.get(z), 0.00000001);

    }
}
