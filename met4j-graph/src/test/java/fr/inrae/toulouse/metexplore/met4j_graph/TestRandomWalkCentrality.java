package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.centrality.RandomWalkCentrality;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_mathUtils.matrix.BioMatrix;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test {@link RandomWalkCentrality}
 */
public class TestRandomWalkCentrality {
    private static RandomWalkCentrality<BioMetabolite, ReactionEdge, CompoundGraph> centrality;
    public static CompoundGraph graph;
    public static BioMetabolite x,a,b,c,y,n1,n2,n3,n4,n5,n6;

    @BeforeClass
    public static void init(){
        graph = new CompoundGraph();
        x = new BioMetabolite("X"); graph.addVertex(x);
        a = new BioMetabolite("A"); graph.addVertex(a);
        b = new BioMetabolite("B"); graph.addVertex(b);
        c = new BioMetabolite("C"); graph.addVertex(c);
        y = new BioMetabolite("Y"); graph.addVertex(y);
        n1 = new BioMetabolite("N1"); graph.addVertex(n1);
        n2 = new BioMetabolite("N2"); graph.addVertex(n2);
        n3 = new BioMetabolite("N3"); graph.addVertex(n3);
        n4 = new BioMetabolite("N4"); graph.addVertex(n4);
        n5 = new BioMetabolite("N5"); graph.addVertex(n5);
        n6 = new BioMetabolite("N6"); graph.addVertex(n6);
        graph.addEdge(x,n1);
        graph.addEdge(x,a);
        graph.addEdge(x,n2);
        graph.addEdge(x,n3);
        graph.addEdge(n1,a);
        graph.addEdge(n1,n2);
        graph.addEdge(n1,n3);
        graph.addEdge(a,n2);
        graph.addEdge(a,n3);
        graph.addEdge(n2,n3);

        graph.addEdge(a,b);
        graph.addEdge(a,c);
        graph.addEdge(b,c);

        graph.addEdge(n4,n5);
        graph.addEdge(n4,y);
        graph.addEdge(n4,n6);
        graph.addEdge(n4,b);
        graph.addEdge(n5,y);
        graph.addEdge(n5,n6);
        graph.addEdge(n5,b);
        graph.addEdge(y,n6);
        graph.addEdge(y,b);
        graph.addEdge(n6,b);

        graph.asUndirected();
        BioCollection<BioMetabolite> noi = new BioCollection<>();
        noi.add(x);
        noi.add(y);
//        noi.addAll(graph.vertexSet());
        centrality=new RandomWalkCentrality<>(graph,noi);
    }

    @Test
    public void testRandomWalkCentrality() {
        HashMap<BioMetabolite, Double> res = centrality.getCentrality();
        System.out.println(res);
        assertEquals(res.get(a), Collections.max(res.values()), 0.000000001);
        assertEquals(res.get(b), Collections.max(res.values()), 0.000000001);
        assertEquals(res.get(n1),res.get(n2), 0.000000001);
        assertEquals(res.get(n1),res.get(n3), 0.000000001);
        assertEquals(res.get(n1),res.get(n4), 0.000000001);
        assertEquals(res.get(n1),res.get(n5), 0.000000001);
        assertEquals(res.get(n1),res.get(n6), 0.000000001);
        assertTrue(res.get(c)>res.get(n1));
        assertTrue(res.get(c)<res.get(a));
    }

    @Test
    public void testRandomWalkEdgeCentrality() {
        HashMap<ReactionEdge, Double> res = centrality.getEdgesPassageTime(true,true);
        for(Map.Entry<ReactionEdge, Double> e : res.entrySet()){
            System.out.println(e.getKey().getV1().getId()+
                    "-"+e.getKey().getV2().getId()+
                    " : "+e.getValue());
        }
        BioMatrix res2 = centrality.computeNodeOfInterestTable();
        res2.print();
        assertEquals(res.get(graph.getEdge(b,a)), Collections.max(res.values()), 0.000000001);
        assertEquals(res.get(graph.getEdge(a,b)), Collections.max(res.values()), 0.000000001);

        assertTrue(res.get(graph.getEdge(a,b))>res.get(graph.getEdge(x,a)));
        assertTrue(res.get(graph.getEdge(b,a))>res.get(graph.getEdge(y,b)));
        assertEquals(res.get(graph.getEdge(y,b)), res.get(graph.getEdge(x,a)), 0.000000001);

        assertTrue(res.get(graph.getEdge(x,a))>res.get(graph.getEdge(a,c)));
        assertTrue(res.get(graph.getEdge(y,b))>res.get(graph.getEdge(b,c)));
        assertEquals(res.get(graph.getEdge(a,c)), res.get(graph.getEdge(b,c)), 0.000000001);

        assertTrue(res.get(graph.getEdge(a,c))>res.get(graph.getEdge(a,n2)));
        assertTrue(res.get(graph.getEdge(b,c))>res.get(graph.getEdge(b,n4)));
        assertEquals(res.get(graph.getEdge(b,n4)), res.get(graph.getEdge(a,n2)), 0.000000001);

        assertTrue(res.get(graph.getEdge(a,n2))>res.get(graph.getEdge(n1,n2)));
        assertTrue(res.get(graph.getEdge(b,n4))>res.get(graph.getEdge(n5,n4)));
        assertEquals(res.get(graph.getEdge(n5,n4)), res.get(graph.getEdge(n1,n2)), 0.000000001);

    }
}
