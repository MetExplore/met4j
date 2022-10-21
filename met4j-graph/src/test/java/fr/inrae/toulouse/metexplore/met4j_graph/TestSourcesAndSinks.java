package fr.inrae.toulouse.metexplore.met4j_graph;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.analyze.SourcesAndSinks;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSourcesAndSinks {


    public static CompoundGraph cg;

    public static BioMetabolite a,b,c,d,f,g,h1, h2;
    public static BioMetabolite ae,be,ce,de,d0,d2,fe,ge,he;
    public static BioMetabolite v,w,x,y,z;

    public static ReactionEdge aae, bbe, cce, dde, ffe, gge, h1he, h2he;
    public static ReactionEdge aea, beb, cec, ded, d0de, ded2, geg, heh1, heh2;
    public static ReactionEdge xa, bx, xc, cy, dx, vf, fw, wv, xh1, h2y, zx;

    public static BioCollection<BioMetabolite> ext;

    @BeforeClass
    public static void init() {
        cg = new CompoundGraph();

        //external metabolites
        ae= new BioMetabolite("ae"); cg.addVertex(ae);
        be= new BioMetabolite("be"); cg.addVertex(be);
        ce= new BioMetabolite("ce"); cg.addVertex(ce);
        de= new BioMetabolite("de"); cg.addVertex(de);
        d0= new BioMetabolite("d0e"); cg.addVertex(d0);
        d2= new BioMetabolite("d2e"); cg.addVertex(d2);
        d0de=new ReactionEdge(d0,de, new BioReaction("external_rxn"));cg.addEdge(d0de);
        ded2=new ReactionEdge(de,d2, new BioReaction("external_rxn"));cg.addEdge(ded2);
        fe= new BioMetabolite("fe"); cg.addVertex(fe);
        ge= new BioMetabolite("ge"); cg.addVertex(ge);
        he= new BioMetabolite("h1h2e"); cg.addVertex(he);
        ext = new BioCollection<>();
        ext.add(ae,be,ce,de,d0,d2,fe,ge,he);

        //internal "interface" metabolites (can be transported to external)
        a= new BioMetabolite("ai"); cg.addVertex(a);
        aae=new ReactionEdge(a,ae, new BioReaction("transport"));cg.addEdge(aae);
        aea=new ReactionEdge(ae,a, new BioReaction("transport"));cg.addEdge(aea);
        b= new BioMetabolite("bi"); cg.addVertex(b);
        bbe=new ReactionEdge(b,be, new BioReaction("transport"));cg.addEdge(bbe);
        beb=new ReactionEdge(be,b, new BioReaction("transport"));cg.addEdge(beb);
        c= new BioMetabolite("ci"); cg.addVertex(c);
        cce=new ReactionEdge(c,ce, new BioReaction("transport"));cg.addEdge(cce);
        cec=new ReactionEdge(ce,c, new BioReaction("transport"));cg.addEdge(cec);
        d= new BioMetabolite("di"); cg.addVertex(d);
        dde=new ReactionEdge(d,de, new BioReaction("transport"));cg.addEdge(dde);
        ded=new ReactionEdge(de,d, new BioReaction("transport"));cg.addEdge(ded);
        f= new BioMetabolite("fi"); cg.addVertex(f);
        ffe=new ReactionEdge(f,fe, new BioReaction("transport"));cg.addEdge(ffe);
        g= new BioMetabolite("gi"); cg.addVertex(g);
        gge=new ReactionEdge(g,ge, new BioReaction("transport"));cg.addEdge(gge);
        geg=new ReactionEdge(ge,g, new BioReaction("transport"));cg.addEdge(geg);
        h1= new BioMetabolite("h1i"); cg.addVertex(h1);
        h1he=new ReactionEdge(h1,he, new BioReaction("transport"));cg.addEdge(h1he);
        heh1=new ReactionEdge(he,h1, new BioReaction("transport"));cg.addEdge(heh1);
        h2= new BioMetabolite("h2i"); cg.addVertex(h2);
        h2he=new ReactionEdge(h2,he, new BioReaction("transport"));cg.addEdge(h2he);
        heh2=new ReactionEdge(he,h2, new BioReaction("transport"));cg.addEdge(heh2);

        //internal metabolites
        x=new BioMetabolite("xi");cg.addVertex(x);
        y=new BioMetabolite("yi");cg.addVertex(y);
        z=new BioMetabolite("zi");cg.addVertex(z);
        v=new BioMetabolite("vi");cg.addVertex(v);
        w=new BioMetabolite("wi");cg.addVertex(w);
        zx= new ReactionEdge(z,x, new BioReaction("internal_rxn"));cg.addEdge(zx);//z is internal source

        xa=new ReactionEdge(x,a, new BioReaction("internal_rxn"));cg.addEdge(xa);//a is not a source -> a is sink
        bx=new ReactionEdge(b,x, new BioReaction("internal_rxn"));cg.addEdge(bx);//b is not a sink -> b is source
        xc=new ReactionEdge(x,c, new BioReaction("internal_rxn"));cg.addEdge(xc);//c is not a source
        cy=new ReactionEdge(c,y, new BioReaction("internal_rxn"));cg.addEdge(cy);//c is not a sink -> c is intermediary
        dx=new ReactionEdge(d,x, new BioReaction("internal_rxn"));cg.addEdge(dx);//d is not a sink -> d is source
        vf=new ReactionEdge(v,f, new BioReaction("internal_rxn"));cg.addEdge(vf);//f is not a source
        fw=new ReactionEdge(f,w, new BioReaction("internal_rxn"));cg.addEdge(fw);//f is not a sink -> f is intermediary
        wv=new ReactionEdge(w,v, new BioReaction("internal_rxn"));cg.addEdge(wv);
        //g is a source, g is a sink, g is isolated
        xh1=new ReactionEdge(x,h1, new BioReaction("internal_rxn"));cg.addEdge(xh1);//h1 is not a source, h is not a source
        h2y=new ReactionEdge(h2,y, new BioReaction("internal_rxn"));cg.addEdge(h2y);//h2 is not a sink, h is not a sink -> h is intermediary

    }

    @Test
    public void testSourcesBA(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSources(true)
                .fromExternalCompartment(ext,false)
                .useBorensteinAlgorithm(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sources",3, res.size());
        assertTrue("wrong sources", res.contains(be));
        assertTrue("wrong sources", res.contains(d0));
        assertTrue("wrong sources", res.contains(ge));
    }

    @Test
    public void testNoSourcesBA(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectNonSources(true)
                .fromExternalCompartment(ext,false)
                .useBorensteinAlgorithm(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of not sources",6, res.size());
        assertTrue("wrong not sources", res.contains(ae));
        assertTrue("wrong not sources", res.contains(ce));
        assertTrue("wrong not sources", res.contains(de));
        assertTrue("wrong not sources", res.contains(d2));
        assertTrue("wrong not sources", res.contains(fe));
        assertTrue("wrong not sources", res.contains(he));
    }

    @Test
    public void testSinkBA(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSinks(true)
                .fromExternalCompartment(ext,false)
                .useBorensteinAlgorithm(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sink",4, res.size());
        assertTrue("wrong sink", res.contains(ae));
        assertTrue("wrong sink", res.contains(ge));
        assertTrue("wrong sink", res.contains(d2));
        assertTrue("wrong sink", res.contains(fe));

    }

    @Test
    public void testIntermediaryBA(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectIntermediaries(true)
                .fromExternalCompartment(ext,false)
                .useBorensteinAlgorithm(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of intermediaries",3, res.size());
        assertTrue("wrong intermediary", res.contains(ce));
        assertTrue("wrong intermediary", res.contains(de));
        assertTrue("wrong intermediary", res.contains(he));

    }

    @Test
    public void testNoSinkBA(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectNonSinks(true)
                .fromExternalCompartment(ext,false)
                .useBorensteinAlgorithm(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of not sink",5, res.size());
        assertTrue("wrong not sink", res.contains(be));
        assertTrue("wrong not sink", res.contains(ce));
        assertTrue("wrong not sink", res.contains(de));
        assertTrue("wrong not sink", res.contains(d0));
        assertTrue("wrong not sink", res.contains(he));
    }

    @Test
    public void testWholeSourcesBA(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSources(true)
                .useBorensteinAlgorithm(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sources (no external/internal adjustment)",9, res.size());
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(d0));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(be));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(b));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(z));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(ge));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(g));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(f));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(v));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(w));
    }

    @Test
    public void testWholeSinkBA(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSinks(true)
                .useBorensteinAlgorithm(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sink (no external/internal adjustment)",7, res.size());
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(d2));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(y));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(fe));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(ge));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(g));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(ae));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(a));
    }

    @Test
    public void testSources(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSources(true)
                .fromExternalCompartment(ext,true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sources",3, res.size());
        assertTrue("wrong sources", res.contains(be));
        assertTrue("wrong sources", res.contains(de));
        assertTrue("wrong sources", res.contains(ge));
    }

    @Test
    public void testNoSources(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectNonSources(true)
                .fromExternalCompartment(ext,true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of not sources",4, res.size());
        assertTrue("wrong not sources", res.contains(ae));
        assertTrue("wrong not sources", res.contains(ce));
        assertTrue("wrong not sources", res.contains(fe));
        assertTrue("wrong not sources", res.contains(he));
    }

    @Test
    public void testSink(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSinks(true)
                .fromExternalCompartment(ext,true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sink",2, res.size());
        assertTrue("wrong sink", res.contains(ae));
        assertTrue("wrong sink", res.contains(ge));

    }

    @Test
    public void testIntermediary(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectIntermediaries(true)
                .fromExternalCompartment(ext,true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of intermediaries",3, res.size());
        assertTrue("wrong intermediary", res.contains(ce));
        assertTrue("wrong intermediary", res.contains(fe));
        assertTrue("wrong intermediary", res.contains(he));

    }

    @Test
    public void testNoSink(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectNonSinks(true)
                .fromExternalCompartment(ext,true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of not sink",5, res.size());
        assertTrue("wrong not sink", res.contains(be));
        assertTrue("wrong not sink", res.contains(ce));
        assertTrue("wrong not sink", res.contains(de));
        assertTrue("wrong not sink", res.contains(fe));
        assertTrue("wrong not sink", res.contains(he));
    }

    @Test
    public void testWholeSources(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSources(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sources (no external/internal adjustment)",2, res.size());
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(d0));
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(z));
    }

    @Test
    public void testSourcesNoDegreeAdjust(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSources(true)
                .fromExternalCompartment(ext,false)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sources (no external/internal adjustment)",1, res.size());
        assertTrue("wrong sources (no external/internal adjustment)", res.contains(d0));
    }

    @Test
    public void testWholeSink(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSinks(true)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sink (no external/internal adjustment)",3, res.size());
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(d2));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(y));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(fe));
    }

    @Test
    public void testSinkNoDegreeAdjust(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSinks(true)
                .fromExternalCompartment(ext,false)
                .keepIsolated(true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sink (no external/internal adjustment)",2, res.size());
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(d2));
        assertTrue("wrong sink (no external/internal adjustment)", res.contains(fe));
    }

    @Test
    public void testSourceNoIsolated(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSources(true)
                .fromExternalCompartment(ext,true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sources (no isaolated)",2, res.size());
        assertTrue("wrong sources (no isaolated)", res.contains(be));
        assertTrue("wrong sources (no isaolated)", res.contains(de));
    }

    @Test
    public void testSinkNoIsolated(){
        SourcesAndSinks ss = new SourcesAndSinks(cg)
                .selectSinks(true)
                .fromExternalCompartment(ext,true);
        BioCollection<BioMetabolite> res = ss.getSelection();
        assertEquals("wrong number of sink (no isaolated)",1, res.size());
        assertTrue("wrong sink (no isaolated)", res.contains(ae));
    }


}