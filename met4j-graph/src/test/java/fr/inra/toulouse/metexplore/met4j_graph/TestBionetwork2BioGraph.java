package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import fr.inra.toulouse.metexplore.met4j_core.biodata.*;
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.bipartite.BipartiteGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inra.toulouse.metexplore.met4j_graph.io.Bionetwork2BioGraph;

public class TestBionetwork2BioGraph {
	/** The graph. */
	public static BioNetwork bn;
	
	/** The path. */
	public static Bionetwork2BioGraph builder;
	
	/** The nodes. */
	public static BioMetabolite a,b,c,d,e,f,h;
	
	/** The edges. */
	public static BioReaction r1,r2,r3,r4,r5,r6,r7;	


	/** The pathways */
	public static BioPathway p1,p2,p3,p4;

	/** The compartment */
	public static BioCompartment comp;
	/**
	 * Inits the graph.
	 */
	@BeforeClass
	public static void init(){
		bn = new BioNetwork();
		comp = new BioCompartment("comp"); bn.add(comp);
		
		a = new BioMetabolite("a"); bn.add(a);bn.affectToCompartment(comp, a);
		b = new BioMetabolite("b"); bn.add(b);bn.affectToCompartment(comp, b);
		c = new BioMetabolite("c"); bn.add(c);bn.affectToCompartment(comp, c);
		d = new BioMetabolite("d"); bn.add(d);bn.affectToCompartment(comp, d);
		e = new BioMetabolite("e"); bn.add(e);bn.affectToCompartment(comp, e);
		f = new BioMetabolite("f"); bn.add(f);bn.affectToCompartment(comp, f);
		h = new BioMetabolite("h"); bn.add(h);bn.affectToCompartment(comp, h);

		p1= new BioPathway("p1"); bn.add(p1);
		p2= new BioPathway("p2"); bn.add(p2);
		p3= new BioPathway("p3"); bn.add(p3);
		p4= new BioPathway("p4"); bn.add(p4);

		r1 = new BioReaction("r1"); bn.add(r1);
		bn.affectLeft(a, 1.0, comp, r1);
		bn.affectRight(b, 1.0, comp, r1);
		bn.affectRight(h, 1.0, comp, r1);
		r1.setReversible(false);
		bn.affectToPathway(p1, r1);
		r2 = new BioReaction("r2");  bn.add(r2);
		bn.affectLeft(b, 1.0, comp, r2);
		bn.affectLeft(d, 1.0, comp, r2);
		bn.affectLeft(h, 1.0, comp, r2);
		bn.affectRight(c, 1.0, comp, r2);
		r2.setReversible(false);
		bn.affectToPathway(p4, r2);
		r3 = new BioReaction("r3");  bn.add(r3);
		bn.affectLeft(e, 1.0, comp, r3);
		bn.affectRight(b, 1.0, comp, r3);
		r3.setReversible(true);
		bn.affectToPathway(p1, r3);
		bn.affectToPathway(p3, r3);
		r4 = new BioReaction("r4");  bn.add(r4);
		bn.affectLeft(e, 1.0, comp, r4);
		bn.affectRight(c, 1.0, comp, r4);
		bn.affectRight(f, 1.0, comp, r4);
		r4.setReversible(false);
		bn.affectToPathway(p3, r4);
		r5 = new BioReaction("r5");  bn.add(r5);
		bn.affectLeft(a, 1.0, comp, r5);
		bn.affectRight(e, 1.0, comp, r5);
		r5.setReversible(true);
		bn.affectToPathway(p3, r5);
		r6 = new BioReaction("r6");  bn.add(r6);
		bn.affectLeft(d, 1.0, comp, r6);
		bn.affectRight(f, 1.0, comp, r6);
		r6.setReversible(false);
		//not in any pathway
		r7 = new BioReaction("r7");  bn.add(r7);
		bn.affectLeft(d, 1.0, comp, r7);
		bn.affectRight(f, 1.0, comp, r7);
		r7.setReversible(false);
		bn.affectToPathway(p2, r7);

		bn.add(r1);
		bn.add(r2);
		bn.add(r3);
		bn.add(r4);
		bn.add(r5);
		bn.add(r6);
		bn.add(r7);
		
		try{
			builder = new Bionetwork2BioGraph(bn);
		}catch(Exception e){
			fail("error while creating reaction graph builder");
		}
	}
	
	@Test
	public void testGetReactionGraph() {
		ReactionGraph g = builder.getReactionGraph();
		assertEquals("wrong number of vertices",7, g.vertexSet().size());
		assertEquals("wrong number of edges",9, g.edgeSet().size());
		
		assertEquals("wrong in-degree of reaction "+r1.getId(), 1,g.inDegreeOf(r1));
		assertEquals("wrong out-degree of reaction "+r1.getId(), 3, g.outDegreeOf(r1));
		assertEquals("wrong in-degree of reaction "+r2.getId(), 3, g.inDegreeOf(r2));
		assertEquals("wrong out-degree of reaction "+r2.getId(), 0, g.outDegreeOf(r2));
		assertEquals("wrong in-degree of reaction "+r3.getId(), 2, g.inDegreeOf(r3));
		assertEquals("wrong out-degree of reaction "+r3.getId(), 3, g.outDegreeOf(r3));
		assertEquals("wrong in-degree of reaction "+r4.getId(), 2, g.inDegreeOf(r4));
		assertEquals("wrong out-degree of reaction "+r4.getId(), 0, g.outDegreeOf(r4));
		assertEquals("wrong in-degree of reaction "+r5.getId(), 1, g.inDegreeOf(r5));
		assertEquals("wrong out-degree of reaction "+r5.getId(), 3, g.outDegreeOf(r5));
		assertEquals("wrong in-degree of reaction "+r6.getId(), 0, g.inDegreeOf(r6));
		assertEquals("wrong out-degree of reaction "+r6.getId(), 0, g.outDegreeOf(r6));
		assertEquals("wrong in-degree of reaction "+r7.getId(), 0, g.inDegreeOf(r7));
		assertEquals("wrong out-degree of reaction "+r7.getId(), 0, g.outDegreeOf(r7));
	}
	
	@Test
	public void testGetCompoundGraph(){
		CompoundGraph g = builder.getCompoundGraph();
		assertEquals("wrong number of vertices",7, g.vertexSet().size());
		assertEquals("wrong number of edges",13, g.edgeSet().size());
		
		assertEquals("wrong in-degree of compound "+a.getId(), 1, g.inDegreeOf(a));
		assertEquals("wrong out-degree of compound "+a.getId(), 3, g.outDegreeOf(a));
		assertEquals("wrong in-degree of compound "+b.getId(), 2, g.inDegreeOf(b));
		assertEquals("wrong out-degree of compound "+b.getId(), 2, g.outDegreeOf(b));
		assertEquals("wrong in-degree of compound "+c.getId(), 4, g.inDegreeOf(c));
		assertEquals("wrong out-degree of compound "+c.getId(), 0, g.outDegreeOf(c));
		assertEquals("wrong in-degree of compound "+d.getId(), 0, g.inDegreeOf(d));
		assertEquals("wrong out-degree of compound "+d.getId(), 3, g.outDegreeOf(d));
		assertEquals("wrong in-degree of compound "+e.getId(), 2, g.inDegreeOf(e));
		assertEquals("wrong out-degree of compound "+e.getId(), 4, g.outDegreeOf(e));
		assertEquals("wrong in-degree of compound "+f.getId(), 3, g.inDegreeOf(f));
		assertEquals("wrong out-degree of compound "+f.getId(), 0, g.outDegreeOf(f));
		assertEquals("wrong in-degree of compound "+h.getId(), 1, g.inDegreeOf(h));
		assertEquals("wrong out-degree of compound "+h.getId(), 1, g.outDegreeOf(h));
	}
	
	@Test
	public void testGetBipartiteGraph(){
		BipartiteGraph g = builder.getBipartiteGraph();
		for(BipartiteEdge e : g.edgeSet()){
			System.out.println(e.getV1().getId()+" -> "+e.getV2().getId());
		}
		assertEquals("wrong number of vertices",14, g.vertexSet().size());
		assertEquals("wrong number of edges",22, g.edgeSet().size());
		
		assertEquals("wrong in-degree of compound "+a.getId(), 1, g.inDegreeOf(a));
		assertEquals("wrong out-degree of compound "+a.getId(), 2, g.outDegreeOf(a));
		assertEquals("wrong in-degree of compound "+b.getId(), 2, g.inDegreeOf(b));
		assertEquals("wrong out-degree of compound "+b.getId(), 2, g.outDegreeOf(b));
		assertEquals("wrong in-degree of compound "+c.getId(), 2, g.inDegreeOf(c));
		assertEquals("wrong out-degree of compound "+c.getId(), 0, g.outDegreeOf(c));
		assertEquals("wrong in-degree of compound "+d.getId(), 0, g.inDegreeOf(d));
		assertEquals("wrong out-degree of compound "+d.getId(), 3, g.outDegreeOf(d));
		assertEquals("wrong in-degree of compound "+e.getId(), 2, g.inDegreeOf(e));
		assertEquals("wrong out-degree of compound "+e.getId(), 3, g.outDegreeOf(e));
		assertEquals("wrong in-degree of compound "+f.getId(), 3, g.inDegreeOf(f));
		assertEquals("wrong out-degree of compound "+f.getId(), 0, g.outDegreeOf(f));
		assertEquals("wrong in-degree of compound "+h.getId(), 1, g.inDegreeOf(h));
		assertEquals("wrong out-degree of compound "+h.getId(), 1, g.outDegreeOf(h));
		
		assertEquals("wrong in-degree of reaction "+r1.getId(), 1, g.inDegreeOf(r1));
		assertEquals("wrong out-degree of reaction "+r1.getId(), 2, g.outDegreeOf(r1));
		assertEquals("wrong in-degree of reaction "+r2.getId(), 3, g.inDegreeOf(r2));
		assertEquals("wrong out-degree of reaction "+r2.getId(), 1, g.outDegreeOf(r2));
		assertEquals("wrong in-degree of reaction "+r3.getId(), 2, g.inDegreeOf(r3));
		assertEquals("wrong out-degree of reaction "+r3.getId(), 2, g.outDegreeOf(r3));
		assertEquals("wrong in-degree of reaction "+r4.getId(), 1, g.inDegreeOf(r4));
		assertEquals("wrong out-degree of reaction "+r4.getId(), 2, g.outDegreeOf(r4));
		assertEquals("wrong in-degree of reaction "+r5.getId(), 2, g.inDegreeOf(r5));
		assertEquals("wrong out-degree of reaction "+r5.getId(), 2, g.outDegreeOf(r5));
		assertEquals("wrong in-degree of reaction "+r6.getId(), 1, g.inDegreeOf(r6));
		assertEquals("wrong out-degree of reaction "+r6.getId(), 1, g.outDegreeOf(r6));
		assertEquals("wrong in-degree of reaction "+r7.getId(), 1, g.inDegreeOf(r7));
		assertEquals("wrong out-degree of reaction "+r7.getId(), 1, g.outDegreeOf(r7));
	}

	@Test
	public void testGetPathwayGraph(){
		PathwayGraph g = builder.getPathwayGraph();
//		for(PathwayGraphEdge e : g.edgeSet()){
//			System.out.println(e.getV1().getId()+" >"+e.getConnectingCompounds().toString()+"> "+e.getV2().getId());
//		}
		assertEquals("wrong number of vertices",4, g.vertexSet().size());
		assertEquals("wrong number of edges",3, g.edgeSet().size());

		assertEquals("wrong in-degree of compound "+p1.getId(), 1, g.inDegreeOf(p1));
		assertEquals("wrong out-degree of compound "+p1.getId(), 1, g.outDegreeOf(p1));
		assertEquals("wrong in-degree of compound "+p2.getId(), 0, g.inDegreeOf(p2));
		assertEquals("wrong out-degree of compound "+p2.getId(), 0, g.outDegreeOf(p2));
		assertEquals("wrong in-degree of compound "+p3.getId(), 0, g.inDegreeOf(p3));
		assertEquals("wrong out-degree of compound "+p3.getId(), 2, g.outDegreeOf(p3));
		assertEquals("wrong in-degree of compound "+p4.getId(), 2, g.inDegreeOf(p4));
		assertEquals("wrong out-degree of compound "+p4.getId(), 0, g.outDegreeOf(p4));

		assertTrue("wrong compound connecting pathway", g.getEdge(p3,p1).getConnectingCompounds().contains(a));
		assertTrue("wrong compound connecting pathway", g.getEdge(p3,p4).getConnectingCompounds().contains(b));
		assertTrue("wrong compound connecting pathway", g.getEdge(p1,p4).getConnectingCompounds().contains(b));
		assertTrue("wrong compound connecting pathway", g.getEdge(p1,p4).getConnectingCompounds().contains(h));

		assertEquals("wrong number of compounds connecting pathway", 2 ,g.getEdge(p1,p4).getConnectingCompounds().size());
		assertEquals("wrong number of compounds connecting pathway", 1 ,g.getEdge(p3,p4).getConnectingCompounds().size());
		assertEquals("wrong number of compounds connecting pathway", 1 ,g.getEdge(p3,p1).getConnectingCompounds().size());



	}

}
