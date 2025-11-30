package fr.inrae.toulouse.metexplore.met4j_graph;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.AttributeExporter;
import fr.inrae.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.io.ExportGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class TestExportGraph {

    /** The graph. */
    public static BioNetwork bn;
    public static CompoundGraph cg;
    public static BipartiteGraph bg;
    public static ReactionGraph rg;
    public static PathwayGraph pg;

    /** The nodes. */
    public static BioMetabolite a,b,c,d,e,f,h;

    /** The edges. */
    public static BioReaction r1,r2,r3,r4,r5,r6,r7;

    /** The pathways */
    public static BioPathway p1,p2,p3,p4;

    /** The compartment */
    public static BioCompartment comp,comp2,comp3;
    /**
     * Inits the graph.
     */
    @BeforeClass
    public static void init(){
        bn = new BioNetwork();
        comp = new BioCompartment("comp"); bn.add(comp);
        comp2 = new BioCompartment("comp2"); bn.add(comp2);
        comp3 = new BioCompartment("comp3"); bn.add(comp3);

        a = new BioMetabolite("a","1"); bn.add(a);bn.affectToCompartment(comp, a); a.setMolecularWeight(1.1);a.setChemicalFormula("BaCoN");
        b = new BioMetabolite("b","2"); bn.add(b);bn.affectToCompartment(comp, b); b.setMolecularWeight(2.2);b.setChemicalFormula("BaCoN");
        c = new BioMetabolite("c","3"); bn.add(c);bn.affectToCompartment(comp, c); c.setMolecularWeight(3.3);c.setChemicalFormula("BaCoN");
        d = new BioMetabolite("d","4"); bn.add(d);bn.affectToCompartment(comp, d); d.setMolecularWeight(4.4);d.setChemicalFormula("BaCoN");
        e = new BioMetabolite("e","5"); bn.add(e);bn.affectToCompartment(comp2, e);bn.affectToCompartment(comp3, e); e.setMolecularWeight(5.5);e.setChemicalFormula("BaCoN");
        f = new BioMetabolite("f","6"); bn.add(f);bn.affectToCompartment(comp2, f); f.setMolecularWeight(6.6);f.setChemicalFormula("BaCoN");
        h = new BioMetabolite("h","7"); bn.add(h);bn.affectToCompartment(comp, h); h.setMolecularWeight(7.7);h.setChemicalFormula("BaCoN");

        p1= new BioPathway("p1"); bn.add(p1);
        p2= new BioPathway("p2"); bn.add(p2);
        p3= new BioPathway("p3"); bn.add(p3);
        p4= new BioPathway("p4"); bn.add(p4);

        r1 = new BioReaction("r1","1"); bn.add(r1);
        bn.affectLeft(r1, 1.0, comp, a);
        bn.affectRight(r1, 1.0, comp, b);
        bn.affectRight(r1, 1.0, comp, h);
        r1.setReversible(false); r1.setEcNumber("EC:1");
        bn.affectToPathway(p1, r1);
        r2 = new BioReaction("r2","2");  bn.add(r2);
        bn.affectLeft(r2, 1.0, comp, b);
        bn.affectLeft(r2, 1.0, comp, d);
        bn.affectLeft(r2, 1.0, comp, h);
        bn.affectRight(r2, 1.0, comp, c);
        r2.setReversible(false); r2.setEcNumber("EC:2");
        bn.affectToPathway(p4, r2);
        r3 = new BioReaction("r3","3");  bn.add(r3);
        bn.affectLeft(r3, 1.0, comp3, e);
        bn.affectRight(r3, 1.0, comp, b);
        r3.setReversible(true); r3.setEcNumber("EC:3");
        bn.affectToPathway(p1, r3);
        bn.affectToPathway(p3, r3);
        r4 = new BioReaction("r4","4");  bn.add(r4);
        bn.affectLeft(r4, 1.0, comp2, e);
        bn.affectRight(r4, 1.0, comp, c);
        bn.affectRight(r4, 1.0, comp2, f);
        r4.setReversible(false); r4.setEcNumber("EC:4");
        bn.affectToPathway(p3, r4);
        r5 = new BioReaction("r5","5");  bn.add(r5);
        bn.affectLeft(r5, 1.0, comp, a);
        bn.affectRight(r5, 1.0, comp2, e);
        r5.setReversible(true); r5.setEcNumber("EC:5");
        bn.affectToPathway(p3, r5);
        r6 = new BioReaction("r6","6");  bn.add(r6);
        bn.affectLeft(r6, 1.0, comp, d);
        bn.affectRight(r6, 1.0, comp2, f);
        r6.setReversible(false); r6.setEcNumber("EC:6");
        //not in any pathway
        r7 = new BioReaction("r7","7");  bn.add(r7);
        bn.affectLeft(r7, 1.0, comp, d);
        bn.affectRight(r7, 1.0, comp2, f);
        r7.setReversible(false); r7.setEcNumber("EC:7");
        bn.affectToPathway(p2, r7);

        try{
            Bionetwork2BioGraph builder = new Bionetwork2BioGraph(bn);
            cg = builder.getCompoundGraph();
            bg = builder.getBipartiteGraph();
            rg = builder.getReactionGraph();
            pg = builder.getPathwayGraph();

        }catch(Exception e){
            fail("error while creating reaction graph builder");
        }
    }


//-------------------------------------------
    @Test
    public void testCompoundGraph2Tab() {
        StringWriter w = new StringWriter();
        ExportGraph<BioMetabolite, ReactionEdge, CompoundGraph> export = new ExportGraph<>(cg);
        export.toTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","source\tinteraction\ttarget",header);

        HashSet<String> nodes = new HashSet<>();
        for(String line : lines){
           String[] column = line.split("\t");
           assertEquals("wrong number of columns",3, column.length);
           nodes.add(column[0]);
           nodes.add(column[2]);
        }

        int nbNodes = nodes.size();
        int nbEdges = lines.size();
        assertEquals("wrong number of edges",13, nbEdges);
        assertEquals("wrong number of nodes",7, nbNodes);
    }
    @Test
    public void testCompoundGraph2nodeList() {
        StringWriter w = new StringWriter();
        ExportGraph<BioMetabolite, ReactionEdge, CompoundGraph> export = new ExportGraph<>(cg);
        export.toNodeTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","Node_id\tName\tType",header);

        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of columns",3, column.length);
            assertEquals("wrong type","Compound", column[2]);
        }

        int nbNodes = lines.size();
        assertEquals("wrong number of nodes",7, nbNodes);
    }
    @Test
    public void testCompoundGraph2gml() {
        StringWriter w = new StringWriter();
        ExportGraph<BioMetabolite, ReactionEdge, CompoundGraph> export = new ExportGraph<>(cg);
        export.toGml(w);
        String output = w.toString();

        Pattern nodeRegex = Pattern.compile("node\n?\\s*\\[");
        Pattern edgeRegex = Pattern.compile("edge\n?\\s*\\[");

        long nbNodes = nodeRegex.matcher(output).results().count();
        long nbEdges = edgeRegex.matcher(output).results().count();
        assertEquals("wrong number of edges",13, nbEdges);
        assertEquals("wrong number of nodes",7, nbNodes);
    }
    @Test
    public void testCompoundGraph2json() {
        StringWriter w = new StringWriter();
        ExportGraph<BioMetabolite, ReactionEdge, CompoundGraph> export = new ExportGraph<>(cg);
        try {
            export.toJSONgraph(w);
        } catch (IOException ex) {
            fail("error while exporting to json");
        }
        JsonObject output = JsonParser.parseString(w.toString()).getAsJsonObject();

        JsonObject graph = output.getAsJsonObject("graph");
        assertNotNull("graph from json is null", graph);

        long nbNodes = graph.getAsJsonObject("nodes").size();
        long nbEdges = graph.getAsJsonArray("edges").size();
        assertEquals("wrong number of edges",13, nbEdges);
        assertEquals("wrong number of nodes",7, nbNodes);

    }
//-------------------------------------------
    @Test
    public void testBipartiteGraph2Tab() {

        StringWriter w = new StringWriter();
        ExportGraph<BioEntity, BipartiteEdge, BipartiteGraph> export = new ExportGraph<>(bg);
        export.toTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","source\tinteraction\ttarget",header);

        HashSet<String> nodes = new HashSet<>();
        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of columns",3, column.length);
            nodes.add(column[0]);
            nodes.add(column[2]);
        }

        int nbNodes = nodes.size();
        int nbEdges = lines.size();
        assertEquals("wrong number of edges",22, nbEdges);
        assertEquals("wrong number of nodes",14, nbNodes);
    }
    @Test
    public void testBipartiteGraph2nodeList() {
        StringWriter w = new StringWriter();
        ExportGraph<BioEntity, BipartiteEdge, BipartiteGraph> export = new ExportGraph<>(bg);
        export.toNodeTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","Node_id\tName\tReversible\tType",header);

        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of columns",4, column.length);
            assertTrue("wrong type",column[3].equals("Compound") || column[3].equals("Reaction"));
        }

        int nbNodes = lines.size();
        assertEquals("wrong number of nodes",14, nbNodes);
    }
    @Test
    public void testBipartiteGraph2gml() {
        StringWriter w = new StringWriter();
        ExportGraph<BioEntity, BipartiteEdge, BipartiteGraph> export = new ExportGraph<>(bg);
        export.toGml(w);
        String output = w.toString();

        Pattern nodeRegex = Pattern.compile("node\n?\\s*\\[");
        Pattern edgeRegex = Pattern.compile("edge\n?\\s*\\[");

        long nbNodes = nodeRegex.matcher(output).results().count();
        long nbEdges = edgeRegex.matcher(output).results().count();
        assertEquals("wrong number of edges",22, nbEdges);
        assertEquals("wrong number of nodes",14, nbNodes);
    }
    @Test
    public void testBipartiteGraph2json() {
        StringWriter w = new StringWriter();
        ExportGraph<BioEntity, BipartiteEdge, BipartiteGraph> export = new ExportGraph<>(bg);
        try {
            export.toJSONgraph(w);
        } catch (IOException ex) {
            fail("error while exporting to json");
        }
        JsonObject output = JsonParser.parseString(w.toString()).getAsJsonObject();

        JsonObject graph = output.getAsJsonObject("graph");
        assertNotNull("graph from json is null", graph);

        long nbNodes = graph.getAsJsonObject("nodes").size();
        long nbEdges = graph.getAsJsonArray("edges").size();
        assertEquals("wrong number of edges",22, nbEdges);
        assertEquals("wrong number of nodes",14, nbNodes);
    }
//-------------------------------------------
    @Test
    public void testReactionGraph2Tab() {
        StringWriter w = new StringWriter();
        ExportGraph<BioReaction, CompoundEdge, ReactionGraph> export = new ExportGraph<>(rg);
        export.toTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","source\tinteraction\ttarget",header);

        HashSet<String> nodes = new HashSet<>();
        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of columns",3, column.length);
            nodes.add(column[0]);
            nodes.add(column[2]);
        }

        int nbNodes = nodes.size();
        int nbEdges = lines.size();
        assertEquals("wrong number of edges",9, nbEdges);
        assertEquals("wrong number of nodes",5, nbNodes); //two isolated node not represented
    }
    @Test
    public void testReactionGraph2nodeList() {
        StringWriter w = new StringWriter();
        ExportGraph<BioReaction, CompoundEdge, ReactionGraph> export = new ExportGraph<>(rg);
        export.toNodeTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","Node_id\tName\tReversible\tType",header);

        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of columns",4, column.length);
            assertTrue("wrong type",column[3].equals("Reaction"));
        }

        int nbNodes = lines.size();
        assertEquals("wrong number of nodes",7, nbNodes);
    }
    @Test
    public void testReactionGraph2gml() {
        StringWriter w = new StringWriter();
        ExportGraph<BioReaction, CompoundEdge, ReactionGraph> export = new ExportGraph<>(rg);
        export.toGml(w);
        String output = w.toString();

        Pattern nodeRegex = Pattern.compile("node\n?\\s*\\[");
        Pattern edgeRegex = Pattern.compile("edge\n?\\s*\\[");

        long nbNodes = nodeRegex.matcher(output).results().count();
        long nbEdges = edgeRegex.matcher(output).results().count();
        assertEquals("wrong number of edges",9, nbEdges);
        assertEquals("wrong number of nodes",7, nbNodes);
    }
    @Test
    public void testReactionGraph2json() {
        StringWriter w = new StringWriter();
        ExportGraph<BioReaction, CompoundEdge, ReactionGraph> export = new ExportGraph<>(rg);
        try {
            export.toJSONgraph(w);
        } catch (IOException ex) {
            fail("error while exporting to json");
        }
        JsonObject output = JsonParser.parseString(w.toString()).getAsJsonObject();

        JsonObject graph = output.getAsJsonObject("graph");
        assertNotNull("graph from json is null", graph);

        long nbNodes = graph.getAsJsonObject("nodes").size();
        long nbEdges = graph.getAsJsonArray("edges").size();
        assertEquals("wrong number of edges",9, nbEdges);
        assertEquals("wrong number of nodes",7, nbNodes);
    }

//-------------------------------------------
    @Test
    public void testPathwayGraph2Tab() {
        StringWriter w = new StringWriter();
        ExportGraph<BioPathway, PathwayGraphEdge, PathwayGraph> export = new ExportGraph<>(pg);
        export.toTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","source\tinteraction\ttarget",header);

        HashSet<String> nodes = new HashSet<>();
        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of columns",3, column.length);
            nodes.add(column[0]);
            nodes.add(column[2]);
        }

        int nbNodes = nodes.size();
        int nbEdges = lines.size();
        assertEquals("wrong number of edges",3, nbEdges);
        assertEquals("wrong number of nodes",3, nbNodes);//one isolated node not represented
    }
    @Test
    public void testPathwayGraph2nodeList() {
        StringWriter w = new StringWriter();
        ExportGraph<BioPathway, PathwayGraphEdge, PathwayGraph> export = new ExportGraph<>(pg);
        export.toNodeTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong header","Node_id\tName\tType",header);

        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of columns",3, column.length);
            assertTrue("wrong type",column[2].equals("BioPathway"));
        }

        int nbNodes = lines.size();
        assertEquals("wrong number of nodes",4, nbNodes);
    }
    @Test
    public void testPathwayGraph2gml() {
        StringWriter w = new StringWriter();
        ExportGraph<BioPathway, PathwayGraphEdge, PathwayGraph> export = new ExportGraph<>(pg);
        export.toGml(w);
        String output = w.toString();

        Pattern nodeRegex = Pattern.compile("node\n?\\s*\\[");
        Pattern edgeRegex = Pattern.compile("edge\n?\\s*\\[");

        long nbNodes = nodeRegex.matcher(output).results().count();
        long nbEdges = edgeRegex.matcher(output).results().count();
        assertEquals("wrong number of edges",3, nbEdges);
        assertEquals("wrong number of nodes",4, nbNodes);
    }
    @Test
    public void testPathwayGraph2json() {
        StringWriter w = new StringWriter();
        ExportGraph<BioPathway, PathwayGraphEdge, PathwayGraph> export = new ExportGraph<>(pg);
        try {
            export.toJSONgraph(w);
        } catch (IOException ex) {
            fail("error while exporting to json");
        }
        JsonObject output = JsonParser.parseString(w.toString()).getAsJsonObject();

        JsonObject graph = output.getAsJsonObject("graph");
        assertNotNull("graph from json is null", graph);

        long nbNodes = graph.getAsJsonObject("nodes").size();
        long nbEdges = graph.getAsJsonArray("edges").size();
        assertEquals("wrong number of edges",3, nbEdges);
        assertEquals("wrong number of nodes",4, nbNodes);
    }
//----------------------------------------------------------------------
    @Test
    public void testGraph2TabAtt() {
        StringWriter w = new StringWriter();
        AttributeExporter attExport = AttributeExporter.full(bn)
                .exportEdgeAttribute("test", e -> e.getV1().getId())
                .exportNodeAttribute("test2", BioEntity::getName);
        ExportGraph<BioEntity, BipartiteEdge, BipartiteGraph> export = new ExportGraph<>(bg, attExport);
        export.toTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong Bipartite header","source\tinteraction\ttarget\ttest",header);

        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of Bipartite columns",4, column.length);
            assertEquals(column[3], column[0]);
        }
    }

    @Test
    public void testGraph2nodeListAtt() {
        StringWriter w = new StringWriter();
        AttributeExporter attExport = AttributeExporter.full(bn)
                .exportEdgeAttribute("Test", e -> e.getV1().getId())
                .exportNodeAttribute("Test2", BioEntity::getName);

        // bipartite graph
        ExportGraph export = new ExportGraph<>(bg, attExport);
        export.toNodeTab(w);
        String output = w.toString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        String header = lines.remove(0);;
        assertEquals("wrong Bipartite header","Node_id\tCompartment\tEC\tFormula\tMass\tName\tReversible\tTest2\tTransport\tType",header);

        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of Bipartite columns",10, column.length);
            assertEquals(column[7], column[5]);
            if(column[9].equals("Compound")){
                assertEquals("wrong Bipartite formula","BaCoN", column[3]);
                assertEquals("wrong Bipartite mass",column[5]+"."+column[5], column[4]);
                assertEquals("wrong Bipartite EC number","NA", column[2]);
                if(column[0].equals("e")){
                    assertTrue("wrong Bipartite compartment", column[1].contains("comp2,comp3") || column[1].contains("comp3,comp2"));
                } else if (column[0].equals("f")) {
                    assertEquals("wrong Bipartite compartment","comp2", column[1]);
                }else{
                    assertEquals("wrong Bipartite compartment","comp", column[1]);
                }

            }else{
                assertEquals("wrong Bipartite formula","NA", column[3]);
                assertEquals("wrong Bipartite mass","NA", column[4]);
                assertEquals("wrong Bipartite EC","EC:"+column[5], column[2]);
                if (column[0].equals("r3")||column[0].equals("r4")||column[0].equals("r5")||column[0].equals("r6")||column[0].equals("r7")) {
                    assertEquals("wrong transport assessment","true", column[8]);
                } else{
                    assertEquals("wrong transport assessment","false", column[8]);
                }
            }
        }

        // reaction graph
        export = new ExportGraph<>(rg, attExport);
        w = new StringWriter();
        export.toNodeTab(w);
        output = w.toString();
        lines = new ArrayList<>(Arrays.asList(output.split("\\n")));

        header = lines.remove(0);;
        assertEquals("wrong Reaction header","Node_id\tEC\tName\tReversible\tTest2\tTransport\tType",header);

        for(String line : lines){
            String[] column = line.split("\t");
            assertEquals("wrong number of Reaction columns",7, column.length);
            assertEquals(column[2], column[4]);
            assertEquals("wrong Reaction type","Reaction",column[6]);
            assertEquals("wrong Reaction EC","EC:"+column[4], column[1]);
            if(column[0].equals("r3")||column[0].equals("r4")||column[0].equals("r5")||column[0].equals("r6")||column[0].equals("r7")){
                assertEquals("wrong Reaction transport","true", column[5]);
            } else {
                assertEquals("wrong Reaction transport","false", column[5]);
            }
        }
    }

    @Test
    public void testGraph2gmlAtt() {
        StringWriter w = new StringWriter();
        AttributeExporter attExport = AttributeExporter.full(bn)
                .exportEdgeAttribute("Test", e -> e.getV1().getId())
                .exportNodeAttribute("Test2", BioEntity::getName)
                .exportNodeAttribute("Test3", v->42.0);
        ExportGraph<BioEntity, BipartiteEdge, BipartiteGraph> export = new ExportGraph<>(bg, attExport);
        export.toGml(w);
        String output = w.toString();

        //EC	Formula	Mass	Name	Reversible	Test2	Type
        Pattern ECAttRegex = Pattern.compile("EC\\s+\"EC:[0-9]\"");
        Pattern FormulaAttRegex = Pattern.compile("Formula \"BaCoN\"");
        Pattern MassAttRegex = Pattern.compile("Mass [0-9]+\\.[0-9]+");
        Pattern NameAttRegex = Pattern.compile("Name\\s+\"\\w\"");
        Pattern ReversibleAttRegex = Pattern.compile("Reversible\\s+\"(true|false)\"");
        Pattern Test2AttRegex = Pattern.compile("Test2\\s+\"\\w\"");
        Pattern Test3AttRegex = Pattern.compile("Test3\\s+42.0");
        Pattern TestAttRegex = Pattern.compile("Test\\s+\"[a-z][0-9]?\"");
        Pattern TypeAttRegex = Pattern.compile("Type\\s+\"(Compound|Reaction)\"");
        Pattern CompAttRegex = Pattern.compile("Compartment\\s+\"comp[2,3]?(,comp[2,3])?\"");
        Pattern TranspAttRegex = Pattern.compile("Transport\\s+\"(true||false)\"");


        assertEquals("anomaly in Bipartite EC attributes",7, ECAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite Formula attributes",7, FormulaAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite mass attributes",7, MassAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite compartment attributes",7, CompAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite name attributes",14, NameAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite reversibility attributes",7, ReversibleAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite test attributes",14, Test2AttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite test attributes",14, Test3AttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite test attributes",22, TestAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite type attributes",14, TypeAttRegex.matcher(output).results().count());
        assertEquals("anomaly in Bipartite transport attributes",7, TranspAttRegex.matcher(output).results().count());
    }


    @Test
    public void testGraph2jsonAtt() {
        StringWriter w = new StringWriter();
        AttributeExporter attExport = AttributeExporter.full(bn)
                .exportEdgeAttribute("Test", e -> e.getV1().getId())
                .exportNodeAttribute("Test2", BioEntity::getName)
                .exportNodeAttribute("Type", (v->{if (v instanceof BioMetabolite) return "Composé"; //override default att
                    else if (v instanceof BioReaction) return "Reaction";
                    else return v.getClass().getSimpleName();}));
        ExportGraph<BioEntity, BipartiteEdge, BipartiteGraph> export = new ExportGraph<>(bg, attExport);
        try {
            export.toJSONgraph(w);
            String output = w.toString();

            //EC	Formula	Mass	Name	Reversible	Test2	Type
            Pattern ECAttRegex = Pattern.compile("\"EC\":\"EC:[0-9]\"");
            Pattern FormulaAttRegex = Pattern.compile("\"Formula\":\"BaCoN\"");
            Pattern MassAttRegex = Pattern.compile("\"Mass\":\"[0-9]+\\.[0-9]+\"");
            Pattern NameAttRegex = Pattern.compile("\"Name\":\"\\w\"");
            Pattern ReversibleAttRegex = Pattern.compile("\"Reversible\":\"(true|false)\"");
            Pattern Test2AttRegex = Pattern.compile("\"Test2\":\"\\w\"");
            Pattern TestAttRegex = Pattern.compile("\"Test\":\"[a-z][0-9]?\"");
            Pattern TypeAttRegex = Pattern.compile("\"Type\":\"(Composé|Reaction)\"");
            Pattern CompAttRegex = Pattern.compile("\"Compartment\":\"comp[2,3]?(,comp[2,3])?\"");
            Pattern TranspAttRegex = Pattern.compile("Transport\":\"(true||false)\"");


            assertEquals("anomaly in Bipartite EC attributes",7, ECAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite Formula attributes",7, FormulaAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite mass attributes",7, MassAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite compartment attributes",7, CompAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite name attributes",14, NameAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite reversibility attributes",7, ReversibleAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite test attributes",14, Test2AttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite test attributes",22, TestAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite type attributes",14, TypeAttRegex.matcher(output).results().count());
            assertEquals("anomaly in Bipartite transport attributes",7, TranspAttRegex.matcher(output).results().count());
        } catch (IOException ex) {
           fail("error while exporting to json");
        }
    }

}
