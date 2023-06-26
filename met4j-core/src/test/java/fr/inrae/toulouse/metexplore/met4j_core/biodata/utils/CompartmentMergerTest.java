package fr.inrae.toulouse.metexplore.met4j_core.biodata.utils;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CompartmentMergerTest {


    BioNetwork network;
    BioReaction r1,r2,r1X,r3,rt1,rt2;
    BioMetabolite a,b,c,d,aX,bX,cX,eX;
    BioCompartment comp0,compX,compMerge;
    BioProtein p1;
    BioGene g1;
    BioEnzyme e1;

    @Before
    public void init() {
        network = new BioNetwork();
        r1 = new BioReaction("r1");
        r2 = new BioReaction("r2");
        r1X = new BioReaction("r1X");
        r3 = new BioReaction("r3");
        rt1 = new BioReaction("rt1");
        rt2 = new BioReaction("rt2");
        network.add(r1,r2,r1X,r3,rt1,rt2);

        a = new BioMetabolite("a_0", "a");
        b = new BioMetabolite("b_0", "b");
        c = new BioMetabolite("c_0", "c");
        d = new BioMetabolite("d_0", "d");
        aX = new BioMetabolite("a_X", "a");
        bX = new BioMetabolite("b_X", "b");
        cX = new BioMetabolite("c_X", "c");
        eX = new BioMetabolite("e_X", "e");
        network.add(a,b,c,d,aX,bX,cX,eX);
        comp0 = new BioCompartment("0");
        compX = new BioCompartment("X");
        compMerge = new BioCompartment("merge");
        network.add(comp0,compX);
        network.affectToCompartment(comp0, a, b, c, d);
        network.affectToCompartment(compX, aX, bX, cX, eX);

        network.affectLeft(r1, 2.0, comp0, a);
        network.affectRight(r1, 1.0, comp0, b);
        network.affectRight(r1, 1.0, comp0, c);
        r1.setReversible(false);

        network.affectLeft(r1X, 2.0, compX, aX);
        network.affectRight(r1X, 1.0, compX, bX);
        network.affectRight(r1X, 1.0, compX, cX);
        r1X.setReversible(false);

        network.affectLeft(r2, 1.0, comp0, c);
        network.affectRight(r2, 1.0, comp0, d);
        r2.setReversible(false);

        network.affectLeft(r3, 1.0, compX, cX);
        network.affectRight(r3, 1.0, compX, eX);
        r3.setReversible(false);

        network.affectLeft(rt1, 1.0, comp0, a);
        network.affectRight(rt1, 1.0, compX, aX);
        rt1.setReversible(true);

        network.affectLeft(rt2, 1.0, comp0, c);
        network.affectRight(rt2, 1.0, compX, cX);
        rt2.setReversible(true);


        e1 = new BioEnzyme("e1");
        network.add(e1);
        p1 = new BioProtein("p1");
        network.add(p1);
        g1 = new BioGene("g1", "G1");
        network.add(g1);
        network.affectGeneProduct(p1, g1);
        network.affectSubUnit(e1, 1.0, p1);
        network.affectEnzyme(r3, e1);

    }

    @Test
    public void testMerge() {
        a.setName("a");
        CompartmentMerger merger = new CompartmentMerger()
                .setNewNetworkName("myNewName")
                .setUniqCompartment(compMerge)
                .setGetUniqIdFunction(BioMetabolite::getName);

        BioNetwork newNetwork = merger.merge(network);
        assertEquals("Error while setting new name","myNewName",newNetwork.getName());
        assertTrue("Error while creating new compartment",newNetwork.containsCompartment("merge"));
        assertEquals("Error while merging compartment, wrong number of final compartments",1,newNetwork.getCompartmentsView().size());
        assertEquals("Error while merging compartment, wrong number of final metabolites",5,newNetwork.getMetabolitesView().size());
        assertEquals("Error while merging compartment, wrong number of final reactions",4,newNetwork.getReactionsView().size());
        assertFalse("Error while merging compartment, gene lost",newNetwork.getGenesView().isEmpty());
        assertFalse("Error while merging compartment, enzyme lost",newNetwork.getEnzymesView().isEmpty());
        assertFalse("Error while merging compartment, protein lost",newNetwork.getProteinsView().isEmpty());
    }

    @Test
    public void testMergeII() {
        a.setName("notA");//break default merging strategy
        CompartmentMerger merger = new CompartmentMerger()
                .setNewNetworkName("myNewName")
                .setUniqCompartment(compMerge)
                .usePalssonIdentifierConvention();

        BioNetwork newNetwork = merger.merge(network);
        assertEquals("Error while setting new name","myNewName",newNetwork.getName());
        assertTrue("Error while creating new compartment",newNetwork.containsCompartment("merge"));
        assertEquals("Error while merging compartment, wrong number of final compartments",1,newNetwork.getCompartmentsView().size());
        assertEquals("Error while merging compartment, wrong number of final metabolites",5,newNetwork.getMetabolitesView().size());
                assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("a"));
                assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("b"));
                assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("c"));
                assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("d"));
                assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("e"));
        assertEquals("Error while merging compartment, wrong number of final reactions",4,newNetwork.getReactionsView().size());
        assertFalse("Error while merging compartment, gene lost",newNetwork.getGenesView().isEmpty());
        assertFalse("Error while merging compartment, enzyme lost",newNetwork.getEnzymesView().isEmpty());
        assertFalse("Error while merging compartment, protein lost",newNetwork.getProteinsView().isEmpty());
    }

    @Test
    public void testMergeIII() {
        a.setName("notA");//break default merging strategy
        CompartmentMerger merger = new CompartmentMerger()
                .setNewNetworkName("myNewName")
                .setUniqCompartment(compMerge)
                .useBaseIdentifierRegex("^(\\w+)_\\w$");

        BioNetwork newNetwork = merger.merge(network);
        assertEquals("Error while setting new name","myNewName",newNetwork.getName());
        assertTrue("Error while creating new compartment",newNetwork.containsCompartment("merge"));
        assertEquals("Error while merging compartment, wrong number of final compartments",1,newNetwork.getCompartmentsView().size());
        assertEquals("Error while merging compartment, wrong number of final metabolites",5,newNetwork.getMetabolitesView().size());
        assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("a"));
        assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("b"));
        assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("c"));
        assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("d"));
        assertTrue("Error while merging compartment, wrong merged metabolite",newNetwork.containsMetabolite("e"));
        assertEquals("Error while merging compartment, wrong number of final reactions",4,newNetwork.getReactionsView().size());
        assertFalse("Error while merging compartment, gene lost",newNetwork.getGenesView().isEmpty());
        assertFalse("Error while merging compartment, enzyme lost",newNetwork.getEnzymesView().isEmpty());
        assertFalse("Error while merging compartment, protein lost",newNetwork.getProteinsView().isEmpty());
    }

}
