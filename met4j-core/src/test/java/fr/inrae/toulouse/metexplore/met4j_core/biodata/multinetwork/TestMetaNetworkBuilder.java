package fr.inrae.toulouse.metexplore.met4j_core.biodata.multinetwork;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestMetaNetworkBuilder {


    public static BioNetwork bn1, bn2, bn3, bn4;
    public static BioMetabolite a1,b1,c1,c1ex,c2,c2ex,d3, d3ex, f3, d4, d4ex;
    public static BioCompartment co1in, co1ex, co2in, co2ex, co3in, co3ex,co4in, co4ex,medium,medium2;
    public static BioReaction r1, r3, tc1, tc2, td3,td4;


    public static BioNetwork initBump() {
        HashMap<BioNetwork,String> alias = new HashMap<>();
        alias.put(bn1,"model1");
        alias.put(bn2,"model2");
        alias.put(bn3,"model3");
        alias.put(bn4,"model4");
        medium=new BioCompartment("medium");
        medium2=new BioCompartment("medium2");
        CommunityNetworkBuilder builder = new CommunityNetworkBuilder(medium);
        builder.addNewSharedCompartment(medium2);
        PrefixedMetaEntityFactory factory = new PrefixedMetaEntityFactory(alias,"pool");
        factory.setSep("_");
        builder.setEntityFactory(factory);
        builder.setAddExchangeReaction(false);
        builder.add(bn1);
        builder.add(bn2);
        builder.add(bn3);
        builder.add(bn4);
        builder.bumpCompartmentIntoSharedCompartment(bn1,co1ex,medium);
        builder.bumpCompartmentIntoSharedCompartment(bn2,co2ex,medium);
        builder.bumpCompartmentIntoSharedCompartment(bn3,co3ex,medium);
        builder.bumpCompartmentIntoSharedCompartment(bn4,co4ex,medium2);
        builder.bumpCompartmentIntoSharedCompartment(bn4,co4ex,medium);
        return builder.build();
    }

    public static BioNetwork initFuse() {
        HashMap<BioNetwork,String> alias = new HashMap<>();
        alias.put(bn1,"model1");
        alias.put(bn2,"model2");
        alias.put(bn3,"model3");
        alias.put(bn4,"model4");
        medium=new BioCompartment("medium");
        medium2=new BioCompartment("medium2");
        CommunityNetworkBuilder builder = new CommunityNetworkBuilder(medium);
        builder.addNewSharedCompartment(medium2);
        PrefixedMetaEntityFactory factory = new PrefixedMetaEntityFactory(alias,"pool");
        factory.setSep("_");
        builder.setEntityFactory(factory);
        builder.setAddExchangeReaction(false);
        builder.add(bn1,co1ex);
        builder.add(bn2,co2ex);
        builder.add(bn3,co3ex);
        builder.add(bn4);
        builder.fuseCompartmentIntoSharedCompartment(bn4,co4ex,medium2);
        return builder.build();
    }

    public static BioNetwork initFuseIncremental() {
        HashMap<BioNetwork,String> alias = new HashMap<>();
        alias.put(bn1,"model1");
        alias.put(bn2,"model2");

        BioCompartment medium0=new BioCompartment("medium");
        CommunityNetworkBuilder builder = new CommunityNetworkBuilder(medium0);
        PrefixedMetaEntityFactory factory = new PrefixedMetaEntityFactory(alias,"pool");
        factory.setSep("_");
        builder.setEntityFactory(factory);
        builder.setAddExchangeReaction(false);
        builder.add(bn1,co1ex);
        builder.add(bn2,co2ex);
        BioNetwork firstIter = builder.build();

        System.out.println(Arrays.toString(bn1.getMetabolitesView().stream().map(x -> x.getId()).toArray()));
        System.out.println(Arrays.toString(bn1.getReactionsView().stream().map(x -> x.getId()).toArray()));
        System.out.println("+");
        System.out.println(Arrays.toString(bn2.getMetabolitesView().stream().map(x -> x.getId()).toArray()));
        System.out.println(Arrays.toString(bn2.getReactionsView().stream().map(x -> x.getId()).toArray()));
        System.out.println("=");
        System.out.println(Arrays.toString(firstIter.getMetabolitesView().stream().map(x -> x.getId()).toArray()));
        System.out.println(Arrays.toString(firstIter.getReactionsView().stream().map(x -> x.getId()).toArray()));

        BioMetabolite[] pool1 = firstIter.getCompartment("medium")
                .getComponentsView().stream()
                .filter(e -> e instanceof BioMetabolite)
                .filter(m -> m.getId().startsWith("pool_"))
                .toArray(BioMetabolite[]::new);
        for(BioMetabolite pool : pool1){
            firstIter.removeOnCascade(firstIter.getReactionsFromMetabolite(pool));
        }
        firstIter.removeOnCascade(pool1);

        System.out.println("->");
        System.out.println(Arrays.toString(firstIter.getMetabolitesView().stream().map(x -> x.getId()).toArray()));
        System.out.println(Arrays.toString(firstIter.getReactionsView().stream().map(x -> x.getId()).toArray()));

        medium=new BioCompartment("medium");
        medium2=new BioCompartment("medium2");
        builder = new CommunityNetworkBuilder(medium);
        builder.addNewSharedCompartment(medium2);
        alias = new HashMap<>();
        alias.put(firstIter,"");
        alias.put(bn3,"model3");
        alias.put(bn4,"model4");
        builder.setEntityFactory(new PrefixedMetaEntityFactory(alias,"pool"));
        builder.setAddExchangeReaction(false);
        builder.add(firstIter,medium0);
        builder.add(bn3,co3ex);
        builder.add(bn4);
        builder.fuseCompartmentIntoSharedCompartment(bn4,co4ex,medium2);

        BioNetwork output = builder.build();
        System.out.println(Arrays.toString(output.getMetabolitesView().stream().map(x -> x.getId()).toArray()));
        System.out.println(Arrays.toString(output.getReactionsView().stream().map(x -> x.getId()).toArray()));
        return output;
    }

    @BeforeClass
    public static void beforeClass() {
        bn1 = new BioNetwork("bn1");
        bn2 = new BioNetwork("bn2");
        bn3 = new BioNetwork("bn3");
        bn4 = new BioNetwork("bn4");
        a1 = new BioMetabolite("a_in","a");
        b1 = new BioMetabolite("b_in","b");
        c1 = new BioMetabolite("c_in","c");
        c1ex = new BioMetabolite("c_ex","c");
        r1 = new BioReaction("r1");
        tc1 = new BioReaction("tc");
        co1in = new BioCompartment("in");
        co1ex = new BioCompartment("ex");
        bn1.add(a1,b1,c1,c1ex,r1,tc1,co1in,co1ex);
        bn1.affectToCompartment(co1in,a1,b1,c1);
        bn1.affectToCompartment(co1ex,c1ex);
        bn1.affectLeft(r1,1.0,co1in,a1);
        bn1.affectLeft(r1,1.0,co1in,b1);
        bn1.affectRight(r1,2.0,co1in,c1);
        bn1.affectLeft(tc1,1.0,co1in,c1);
        bn1.affectRight(tc1,1.0,co1ex,c1ex);
        c2 = new BioMetabolite("c_in","c");
        c2ex = new BioMetabolite("c_ex","c");
        tc2 = new BioReaction("tc");
        co2in = new BioCompartment("in");
        co2ex = new BioCompartment("ex");
        bn2.add(c2,c2ex,tc2,co2in,co2ex);
        bn2.affectToCompartment(co2in,c2);
        bn2.affectToCompartment(co2ex,c2ex);
        bn2.affectLeft(tc2,1.0,co2in,c2);
        bn2.affectRight(tc2,1.0,co2ex,c2ex);
        d3ex = new BioMetabolite("d_ex","d");
        d3 = new BioMetabolite("d_in","d");
        f3 = new BioMetabolite("f_in","f");
        r3 = new BioReaction("r2");
        td3 = new BioReaction("td");
        co3in = new BioCompartment("in");
        co3ex = new BioCompartment("ex");
        bn3.add(d3ex,d3,f3,r3,td3,co3in,co3ex);
        bn3.affectToCompartment(co3in,d3,f3);
        bn3.affectToCompartment(co3ex,d3ex);
        bn3.affectLeft(td3,1.0,co3in,d3);
        bn3.affectRight(td3,1.0,co3ex,d3ex);
        bn3.affectLeft(r3,1.0,co3in,d3);
        bn3.affectRight(r3,1.0,co3in,f3);
        d4 = new BioMetabolite("d_in","d");
        d4ex = new BioMetabolite("d_ex","d");
        td4 = new BioReaction("td");
        co4in = new BioCompartment("in");
        co4ex = new BioCompartment("ex");
        bn4.add(d4,d4ex,td4,co4in,co4ex);
        bn4.affectToCompartment(co4in,d4);
        bn4.affectToCompartment(co4ex,d4ex);
        bn4.affectLeft(td4,1.0,co4in,d4);
        bn4.affectRight(td4,1.0,co4ex,d4ex);
    }

    @Test
    public void testCompoundsFuse(){
        BioNetwork meta = initFuse();
        assertEquals(14,meta.getMetabolitesView().size());
        assertNotNull(meta.getMetabolite("model1_a_in"));
        assertNotNull(meta.getMetabolite("model1_c_in"));
        assertNotNull(meta.getMetabolite("model1_c_ex"));
        assertNotNull(meta.getMetabolite("model2_c_ex"));
        assertNotNull(meta.getMetabolite("model2_c_in"));

        assertNotNull(meta.getMetabolite("pool_medium_c_ex"));
        assertNotNull(meta.getMetabolite("pool_medium_d_ex"));
        assertNotNull(meta.getMetabolite("pool_medium2_d_ex"));
    }

    @Test
    public void testReactionsFuse() {
        BioNetwork meta = initFuse();
        assertEquals(10, meta.getReactionsView().size());
        assertNotNull(meta.getReaction("poolReaction_model2_c_ex"));
        assertNotNull(meta.getReaction("poolReaction_model1_c_ex"));
        assertNotNull(meta.getReaction("poolReaction_model3_d_ex"));
    }

    @Test
    public void testCompartmentFuse(){
        BioNetwork meta = initFuse();
        assertEquals(6,meta.getCompartmentsView().size());
    }

    @Test
    public void testCompoundsFuseIncremental(){
        BioNetwork meta = initFuseIncremental();
        assertEquals(14,meta.getMetabolitesView().size());
        assertNotNull(meta.getMetabolite("model1_a_in"));
        assertNotNull(meta.getMetabolite("model1_c_in"));
        assertNotNull(meta.getMetabolite("model1_c_ex"));
        assertNotNull(meta.getMetabolite("model2_c_ex"));
        assertNotNull(meta.getMetabolite("model2_c_in"));

        Set<String> metabolites = meta.getMetabolitesView().stream().map(x -> x.getId()).collect(Collectors.toSet());
        assertTrue(metabolites.contains("pool_medium_model1_c_ex")||metabolites.contains("pool_medium_model2_c_ex"));
        assertNotNull(meta.getMetabolite("pool_medium_d_ex"));
        assertNotNull(meta.getMetabolite("pool_medium2_d_ex"));
    }

    @Test
    public void testReactionsFuseIncremental() {
        BioNetwork meta = initFuseIncremental();
        assertEquals(10, meta.getReactionsView().size());
        assertNotNull(meta.getReaction("poolReaction_model2_c_ex"));
        assertNotNull(meta.getReaction("poolReaction_model1_c_ex"));
        assertNotNull(meta.getReaction("poolReaction_model3_d_ex"));
    }

    @Test
    public void testCompartmentFuseIncremental(){
        BioNetwork meta = initFuseIncremental();
        assertEquals(6,meta.getCompartmentsView().size());
    }


    @Test
    public void testCompoundsBump(){
        BioNetwork meta = initBump();
        assertEquals(19,meta.getMetabolitesView().size());
        assertNotNull(meta.getMetabolite("model1_a_in"));
        assertNotNull(meta.getMetabolite("model1_c_in"));
        assertNotNull(meta.getMetabolite("model1_c_ex"));
        assertNotNull(meta.getMetabolite("model2_c_ex"));
        assertNotNull(meta.getMetabolite("model2_c_in"));

        assertNotNull(meta.getMetabolite("pool_medium_c_ex_medium"));
        assertNotNull(meta.getMetabolite("pool_medium_d_ex_medium"));
        assertNotNull(meta.getMetabolite("pool_medium2_d_ex_medium2"));
    }

    @Test
    public void testReactionsBump() {
        BioNetwork meta = initBump();
        assertEquals(16, meta.getReactionsView().size());
        assertNotNull(meta.getReaction("poolReaction_model2_c_ex_medium"));
        assertNotNull(meta.getReaction("poolReaction_model1_c_ex_medium"));
        assertNotNull(meta.getReaction("poolReaction_model3_d_ex_medium"));
        assertNotNull(meta.getReaction("transport_model1_c_ex_to_medium"));
        assertNotNull(meta.getReaction("poolReaction_model4_d_ex_medium2"));
        assertNotNull(meta.getReaction("poolReaction_model4_d_ex_medium"));
        assertNotNull(meta.getReaction("transport_model4_d_ex_to_medium2"));
        assertNotNull(meta.getReaction("transport_model4_d_ex_to_medium"));
    }

    @Test
    public void testCompartmentBump(){
        BioNetwork meta = initBump();
        assertEquals(10,meta.getCompartmentsView().size());
    }
}
