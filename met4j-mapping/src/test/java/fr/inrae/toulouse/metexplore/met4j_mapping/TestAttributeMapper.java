package fr.inrae.toulouse.metexplore.met4j_mapping;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestAttributeMapper {
    public static BioNetwork bn;
    public static BioMetabolite a,b,c,d,e,f,g,h;
    public static BioReaction x,y,z;
    public static BioCompartment comp1, comp2;

    @Before
    public void init() {
        bn = new BioNetwork();

        comp1 = new BioCompartment("c1"); comp1.setName("compartment 1");
        comp2 = new BioCompartment("c2"); comp2.setName("compartment 2");
        bn.add(comp1,comp2);

        a=new BioMetabolite("a");a.setName("A");a.setChemicalFormula("C6H12O6");a.setMolecularWeight(180.16);
        a.addRef("source1","a_1",1,"is","origin");
        a.addRef("source1","aa_1",2,"is","origin");
        b=new BioMetabolite("b");b.setName("B");b.setChemicalFormula("C6H12O6");b.setMolecularWeight(180.16);
        b.addRef("source1","a_1",1,"is","origin");
        c=new BioMetabolite("c");c.setName("C");c.setChemicalFormula("C3H7O6P");c.setMolecularWeight(170.04);
        c.addRef("source1","c_1",1,"is","origin");
        d=new BioMetabolite("d");d.setName("D");d.setChemicalFormula("C6H13O9P");d.setMolecularWeight(260.14);
        d.addRef("source1","d_1",1,"is","origin");
        d.addRef("source1","dd_1",2,"is","origin");
        d.addRef("source2","d_2",1,"is","origin");
        e=new BioMetabolite("e");e.setName("E");e.setChemicalFormula("C6H13O9P");e.setMolecularWeight(260.14);
        e.addRef("source1","e_1",1,"is","origin");
        f=new BioMetabolite("f");f.setName("F");f.setChemicalFormula("C6H14O12P2");f.setMolecularWeight(340.12);
        f.addRef("source2","f_1",1,"is","origin");
        g=new BioMetabolite("g");g.setName("G");g.setChemicalFormula("C3H9O6P");g.setMolecularWeight(170.04);
        g.addRef("source2","g_1",1,"is","origin");
        h=new BioMetabolite("h");h.setName("H");h.setChemicalFormula("C8H14O2S2");h.setMolecularWeight(206.33);
        bn.add(a,b,c,d,e,f,g,h);
        bn.affectToCompartment(comp1,a,b,c);
        bn.affectToCompartment(comp2,d,e,f,g,h);

        x=new BioReaction("x");x.setName("X");
        x.addRef("source1","a_1",1,"is","origin");
        y=new BioReaction("y");y.setName("Y");y.setEcNumber("1.4.2.2");
        y.addRef("source1","y_1",1,"is","origin");
        y.addRef("source2","y_2",1,"is","origin");
        z=new BioReaction("z");z.setName("Z");z.setEcNumber("1.4.2.3");
        z.addRef("source2","z_1",1,"is","origin");
        bn.add(x,y,z);
        bn.affectLeft(x,1.0,comp1,a,b);
        bn.affectRight(x,1.0,comp1,c);
        bn.affectLeft(y,1.0,comp1,c);
        bn.affectRight(y,1.0,comp2,d,e);
        bn.affectLeft(z,1.0,comp2,e);
        bn.affectRight(z,1.0,comp2,f,g,h);
    }

    @Test
    public void test() {
        Set<String> query = new HashSet<>();
        query.add("a_1");//should be found; two hits expected (a and b)
        query.add("c_1");//should be found
        query.add("d_1");//should be found
        query.add("y_1");//should not be found, wrong entity type
        query.add("g_1");//should not be found, wrong source
        query.add("j_1");//should not be found, wrong id
        AttributeMapper<BioMetabolite,String> mapper = new AttributeMapper<BioMetabolite,String>(
                bn,
                BioNetwork::getMetabolitesView,
                AttributeMapper.selectByExternalId("source1")
        );
        Map<String, List<BioMetabolite>> res = mapper.map(query);

        assertEquals("wrong number of successful queries", 3,res.size());
        assertEquals("wrong number of hits for query a_1", 2,res.get("a_1").size());
        assertTrue(res.get("a_1").contains(a));
        assertTrue(res.get("a_1").contains(b));
        assertEquals("wrong number of hits for query c_1", 1,res.get("c_1").size());
        assertTrue(res.get("c_1").contains(c));
        assertEquals("wrong number of hits for query d_1", 1,res.get("d_1").size());
        assertTrue(res.get("d_1").contains(d));
    }

    @Test
    public void test2() {
        Set<String> query = new HashSet<>();
        query.add("C6H12O6");//should be found; two hits expected (a and b)
        query.add("C6H13O9P");//should be found
        query.add("C3H7O6P");//should be found
        query.add("BaCoN4LiFe+");//should not be found
        AttributeMapper<BioMetabolite,String> mapper = new AttributeMapper<BioMetabolite,String>(
                bn,
                BioNetwork::getMetabolitesView,
                AttributeMapper.selectByFormula()
        );
        Map<String, List<BioMetabolite>> res = mapper.map(query);

        assertEquals("wrong number of successful queries", 3,res.size());
        assertEquals("wrong number of hits for query C6H12O6", 2,res.get("C6H12O6").size());
        assertTrue(res.get("C6H12O6").contains(a));
        assertTrue(res.get("C6H12O6").contains(b));
        assertEquals("wrong number of hits for query C6H13O9P", 2,res.get("C6H13O9P").size());
        assertTrue(res.get("C6H13O9P").contains(e));
        assertTrue(res.get("C6H13O9P").contains(d));
        assertEquals("wrong number of hits for query C3H7O6P", 1,res.get("C3H7O6P").size());
        assertTrue(res.get("C3H7O6P").contains(c));
    }

    @Test
    public void test3() {
        Set<Double> query = new HashSet<>();
        query.add(180.18);//should be found
        query.add(340.22);//should be found
        query.add(2000.0);//should not be found
        AttributeMapper<BioMetabolite,Double> mapper = new AttributeMapper<BioMetabolite,Double>(
                bn,
                BioNetwork::getMetabolitesView,
                AttributeMapper.selectByMass(),
                AttributeMapper.useRelativeThreshold(0.05)
        );
        Map<Double, List<BioMetabolite>> res = mapper.map(query);

        assertEquals("wrong number of successful queries", 2,res.size());
        assertEquals("wrong number of hits for query a_1", 2,res.get(180.18).size());
        assertTrue(res.get(180.18).contains(a));
        assertTrue(res.get(180.18).contains(b));
        assertEquals("wrong number of hits for query d_1", 1,res.get(340.22).size());
        assertTrue(res.get(340.22).contains(f));
    }

    @Test
    public void test4() {
        Set<String> query = new HashSet<>();
        query.add("a_1");//should be found
        query.add("y_1");//should be found
        query.add("y_2");//should not be found
        query.add("z_1");//should not be found
        query.add("w_1");//should not be found
        AttributeMapper<BioReaction,String> mapper = new AttributeMapper<BioReaction,String>(
                bn,
                BioNetwork::getReactionsView,
                AttributeMapper.selectByExternalId("source1")
        );
        Map<String, List<BioReaction>> res = mapper.map(query);

        assertEquals("wrong number of successful queries", 2,res.size());
        assertEquals("wrong number of hits for query a_1", 1,res.get("a_1").size());
        assertTrue(res.get("a_1").contains(x));
        assertEquals("wrong number of hits for query y_1", 1,res.get("y_1").size());
        assertTrue(res.get("y_1").contains(y));
    }

    @Test
    public void test5() {
        Set<String> query = new HashSet<>();
        query.add("1");//should be found only if transive matching is used
        query.add("1.4");//should be found only if transive matching is used
        query.add("1.4.2");//should be found only if transive matching is used
        query.add("1.4.2.2");//should be found
        query.add("4.3.2.1");//should not be found
        query.add("noot noot");//should not be found
        AttributeMapper<BioReaction,String> mapper = new AttributeMapper<BioReaction,String>(
                bn,
                BioNetwork::getReactionsView,
                AttributeMapper.selectByEC(false)
        );
        Map<String, List<BioReaction>> res = mapper.map(query);

        assertEquals("wrong number of successful queries", 1,res.size());
        assertEquals("wrong number of hits for query 1.4.2.2", 1,res.get("1.4.2.2").size());
        assertTrue(res.get("1.4.2.2").contains(y));

        mapper = new AttributeMapper<BioReaction,String>(
                bn,
                BioNetwork::getReactionsView,
                AttributeMapper.selectByEC(true)
        );res = mapper.map(query);

        assertEquals("wrong number of successful queries", 4,res.size());
        assertEquals("wrong number of hits for query 1.4.2.2", 1,res.get("1.4.2.2").size());
        assertTrue(res.get("1.4.2.2").contains(y));
        assertEquals("wrong number of hits for query 1.4.2", 2,res.get("1.4.2").size());
        assertTrue(res.get("1.4.2").contains(y));
        assertTrue(res.get("1.4.2").contains(z));
        assertEquals("wrong number of hits for query 1.4", 2,res.get("1.4").size());
        assertTrue(res.get("1.4").contains(y));
        assertTrue(res.get("1.4").contains(z));
        assertEquals("wrong number of hits for query 1", 2,res.get("1").size());
        assertTrue(res.get("1").contains(y));
        assertTrue(res.get("1").contains(z));
    }

}
