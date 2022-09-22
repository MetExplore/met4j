package fr.inrae.toulouse.metexplore.met4j_mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MapperTest {

    public static StringReader r1;
    public static StringReader r2;
    public static List<String> r3;
    public static BioNetwork bn;
    public static BioMetabolite a,b,c;
    public static BioReaction x,y,z;

    @Before
    public void init() {
        r1 = new StringReader("" +
                "Name\tID\tatt\n" +
                "A\ta\t0.1\n" +
                "B\tb\t0.2\n" +
                "C\tc\t0.3\n" +
                "D\td\t0.0\n" +
                "");

        r2 = new StringReader("" +
                "x\n" +
                "y\n" +
                "z\n" +
                "0\n" +
                "");

        r3 = new ArrayList<>();
        r3.add("x");
        r3.add("y");
        r3.add("z");
        r3.add("0");


        bn = new BioNetwork();
        a=new BioMetabolite("a");
        b=new BioMetabolite("b");
        c=new BioMetabolite("c");
        BioMetabolite u = new BioMetabolite("u");
        x=new BioReaction("x");
        y=new BioReaction("y");
        z=new BioReaction("z");
        BioReaction v = new BioReaction("v");
        bn.add(a,b,c,x,y,z,u,v);
    }


    @Test
    public void testMapper1(){
        Mapper<BioReaction> m = new Mapper<>(bn, BioNetwork::getReactionsView).skipIfNotFound();
        try {
            BioCollection<BioReaction> res = m.map(r2);
            assertEquals("wrong number of mapped entries", 3, res.size());
            assertEquals("wrong number of skipped entries", 1, m.getNumberOfSkippedEntries());
            assertTrue("reaction in file not found", res.contains(x));
            assertTrue("reaction in file not found", res.contains(y));
            assertTrue("reaction in file not found", res.contains(z));
        } catch (IOException e) {
            Assert.fail("mapping failed");
            e.printStackTrace();
        }
    }

    @Test
    public void testMapper2(){
        Mapper<BioMetabolite> m = new Mapper<>(bn, BioNetwork::getMetabolitesView)
                .columnSeparator("\t")
                .idColumn(2)
                .skipHeader()
                .skipIfNotFound();
        try {
            BioCollection<BioMetabolite> res = m.map(r1);
            assertEquals("wrong number of mapped entries", 3, res.size());
            assertEquals("wrong number of skipped entries", 1, m.getNumberOfSkippedEntries());
            assertTrue("reaction in file not found", res.contains(a));
            assertTrue("reaction in file not found", res.contains(b));
            assertTrue("reaction in file not found", res.contains(c));
        } catch (IOException e) {
            Assert.fail("mapping failed");
            e.printStackTrace();
        }
    }

    @Test
    public void testMapper22(){
        Mapper<BioMetabolite> m = new Mapper<>(bn, BioNetwork::getMetabolitesView)
                .columnSeparator("\t")
                .idColumn(2)
                .skipHeader()
                .skipIfNotFound();
        try {
            Map<BioMetabolite,List<String>> res = m.mapAttributes(r1);
            assertEquals("wrong number of mapped entries", 3, res.size());
            assertEquals("wrong number of skipped entries", 1, m.getNumberOfSkippedEntries());
            assertTrue("reaction in file not found", res.keySet().contains(a));
            assertTrue("reaction in file not found", res.keySet().contains(b));
            assertTrue("reaction in file not found", res.keySet().contains(c));
            assertEquals("Wrong number of attributes", 2,res.get(a).size());
            assertEquals("Wrong number of attributes", 2,res.get(b).size());
            assertEquals("Wrong number of attributes", 2,res.get(c).size());
            assertEquals("Wrong attribute value", "A",res.get(a).get(0));
            assertEquals("Wrong attribute value", "0.1",res.get(a).get(1));
            assertEquals("Wrong attribute value", "B",res.get(b).get(0));
            assertEquals("Wrong attribute value", "0.2",res.get(b).get(1));
            assertEquals("Wrong attribute value", "C",res.get(c).get(0));
            assertEquals("Wrong attribute value", "0.3",res.get(c).get(1));
        } catch (IOException e) {
            Assert.fail("mapping failed");
            e.printStackTrace();
        }
    }

    @Test
    public void testMapper3(){
        Mapper<BioReaction> m = new Mapper<>(bn, BioNetwork::getReactionsView).skipIfNotFound();
        try {
            BioCollection<BioReaction> res = m.map(r3);
            assertEquals("wrong number of mapped entries", 3, res.size());
            assertEquals("wrong number of skipped entries", 1, m.getNumberOfSkippedEntries());
            assertTrue("reaction in file not found", res.contains(x));
            assertTrue("reaction in file not found", res.contains(y));
            assertTrue("reaction in file not found", res.contains(z));
        } catch (Exception e) {
            Assert.fail("mapping failed");
            e.printStackTrace();
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void testMapper4(){
        Mapper<BioReaction> m = new Mapper<>(bn, BioNetwork::getReactionsView).throwErrorIfNotFound();
        try {
            BioCollection<BioReaction> res = m.map(r2);
        } catch (IOException e) {
            Assert.fail("mapping failed");
            e.printStackTrace();
        }
        Assert.fail("missing entry has not raised error");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapper5(){
        Mapper<BioMetabolite> m = new Mapper<>(bn, BioNetwork::getMetabolitesView)
                .columnSeparator("\t")
                .idColumn(1)
                .skipHeader()
                .throwErrorIfNotFound();
        try {
            BioCollection<BioMetabolite> res = m.map(r1);
        } catch (IOException e) {
            Assert.fail("mapping failed");
            e.printStackTrace();
        }
        Assert.fail("missing entry has not raised error");
    }
}
