package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.io.NodeMapping;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import static org.junit.Assert.*;

public class TestNodeMapping {
    public static CompoundGraph g;

    public static BioMetabolite a,b,c,d,e,f,x,y;

    public static ReactionEdge ab,bc,ad,de,ef,fc,bx,eb,yc;

    public static String filePath;

    @BeforeClass
    public static void init(){
        g = new CompoundGraph();
        a = new BioMetabolite("a"); a.setName("glucose"); a.setInchi("InChI=1S/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6?/m1/s1");
        g.addVertex(a);
        b = new BioMetabolite("b"); b.setName("atp"); b.setInchi("InChI=1S/C10H16N5O13P3/c11-8-5-9(13-2-12-8)15(3-14-5)10-7(17)6(16)4(26-10)1-25-30(21,22)28-31(23,24)27-29(18,19)20/h2-4,6-7,10,16-17H,1H2,(H,21,22)(H,23,24)(H2,11,12,13)(H2,18,19,20)/p-4/t4-,6-,7-,10-/m1/s1");
        g.addVertex(b);
        c = new BioMetabolite("c"); c.setName("Dihydroxyacetone phosphate"); c.setInchi("InChI=1S/C3H7O6P/c4-1-3(5)2-9-10(6,7)8/h1,3,5H,2H2,(H2,6,7,8)/p-2");
        g.addVertex(c);
        d = new BioMetabolite("d"); d.setName("glucose-6-P"); d.setInchi("InChI=1S/C6H13O9P/c7-3-2(1-14-16(11,12)13)15-6(10)5(9)4(3)8/h2-10H,1H2,(H2,11,12,13)/t2-,3-,4+,5-,6?/m1/s1");
        g.addVertex(d);
        e = new BioMetabolite("e"); e.setName("fructose-6-P"); e.setInchi("InChI=1S/C6H13O9P/c7-1-3(8)5(10)6(11)4(9)2-15-16(12,13)14/h4-7,9-11H,1-2H2,(H2,12,13,14)/p-2/t4-,5-,6-/m1/s1");
        g.addVertex(e);
        f = new BioMetabolite("f"); f.setName("fructose 1,6-bisphosphate"); f.setInchi("InChI=1S/C6H14O12P2/c7-3(1-17-19(11,12)13)5(9)6(10)4(8)2-18-20(14,15)16/h3,5-7,9-10H,1-2H2,(H2,11,12,13)(H2,14,15,16)/t3-,5-,6-/m1/s1");
        g.addVertex(f);
        x = new BioMetabolite("x"); x.setName("lipoate"); x.setInchi("InChI=1S/C8H14O2S2/c9-8(10)4-2-1-3-7-5-6-11-12-7/h7H,1-6H2,(H,9,10)");
        g.addVertex(x);
        y = new BioMetabolite("y"); y.setName("glycerol 3-phosphate"); y.setInchi("InChI=1S/C3H9O6P/c4-1-3(5)2-9-10(6,7)8/h3-5H,1-2H2,(H2,6,7,8)/p-2/t3-/m1/s1");
        g.addVertex(y);
        BioReaction abd = new BioReaction("abd");
        BioReaction efb = new BioReaction("efb");
        BioReaction bxc = new BioReaction("bxc");
        BioReaction fyc = new BioReaction("fyc");
        ab = new ReactionEdge(a,b,abd);g.addEdge(a, b, ab);
        bc = new ReactionEdge(b,c,bxc);g.addEdge(b, c, bc);
        ad = new ReactionEdge(a,d,abd);g.addEdge(a, d, ad);
        de = new ReactionEdge(d,e,new BioReaction("de"));g.addEdge(d, e, de);
        ef = new ReactionEdge(e,f,efb);g.addEdge(e, f, ef);
        fc = new ReactionEdge(f,c,new BioReaction("fc"));g.addEdge(f, c, fc);
        bx = new ReactionEdge(b,x,bxc);g.addEdge(b, x, bx);
        eb = new ReactionEdge(e,b,efb);g.addEdge(e, b, eb);
        yc = new ReactionEdge(y,c,fyc);g.addEdge(y, c, yc);

        Path tmpPath = null;
        try {
            tmpPath = Files.createTempFile("test_edgeWeightmport", ".tmp");
        } catch (IOException e1) {
            e1.printStackTrace();
            Assert.fail("Creation of the temporary directory");
        }
        filePath = "NodeTestFile.tab";
        try {
            filePath = TestUtils.copyProjectResource(filePath, tmpPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            fail("problem while reading edge weight file");
        }
    }

    @Test
    public void testMapping1(){

        CompoundGraph g2 = (CompoundGraph) g.clone();
        NodeMapping map = new NodeMapping(g2).columnSeparator("|").idColumn(2).skipHeader().createIfNotFound();
        try {
            BioCollection<BioMetabolite> res = map.map(filePath);
            assertEquals(4,res.size());
            assertTrue(res.contains(a));
            assertTrue(res.contains(b));
            assertTrue(res.contains(c));
            assertTrue(res.getIds().contains("z"));
            assertNotNull(g2.getVertex("z"));
        } catch (Exception e) {
            fail("Unable to map nodes");
        }
    }

    @Test
    public void testMapping2(){

        NodeMapping map = new NodeMapping(g).columnSeparator("|").idColumn(2).skipHeader().skipIfNotFound();
        try {
            BioCollection<BioMetabolite> res = map.map(filePath);
            assertEquals(3,res.size());
            assertTrue(res.contains(a));
            assertTrue(res.contains(b));
            assertTrue(res.contains(c));
        } catch (Exception e) {
            fail("Unable to map nodes");
        }
    }

    @Test
    public void testMapping3(){

        CompoundGraph g2 = (CompoundGraph) g.clone();
        NodeMapping map = new NodeMapping(g2).columnSeparator("|").idColumn(2).skipHeader().throwErrorIfNotFound();
        try {
            BioCollection<BioMetabolite> res = map.map(filePath);
            fail("Expected ioException to be thrown");
        } catch (Throwable e) {
            assertTrue("failed mapping do not return expected exception", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testMapping4(){

        NodeMapping map = new NodeMapping(g).columnSeparator("|").idColumn(2).skipHeader().skipIfNotFound();
        HashSet<String> toMap = new HashSet<>();
        toMap.add("a");
        toMap.add("b");
        toMap.add("c");
        try {
            BioCollection<BioMetabolite> res = map.map(toMap);
            assertEquals(3,res.size());
            assertTrue(res.contains(a));
            assertTrue(res.contains(b));
            assertTrue(res.contains(c));
        } catch (IllegalArgumentException e) {
            fail("Unable to map nodes");
        }
    }
}
