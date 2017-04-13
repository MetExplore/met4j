package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
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
	public static BioPhysicalEntity a,b,c,d,e,f,h;
	
	/** The edges. */
	public static BioChemicalReaction r1,r2,r3,r4,r5,r6,r7;	
	/**
	 * Inits the graph.
	 */
	@BeforeClass
	public static void init(){
		bn = new BioNetwork();
		a = new BioPhysicalEntity("a"); bn.addPhysicalEntity(a);
		b = new BioPhysicalEntity("b"); bn.addPhysicalEntity(b);
		c = new BioPhysicalEntity("c"); bn.addPhysicalEntity(c);
		d = new BioPhysicalEntity("d"); bn.addPhysicalEntity(d);
		e = new BioPhysicalEntity("e"); bn.addPhysicalEntity(e);
		f = new BioPhysicalEntity("f"); bn.addPhysicalEntity(f);
		h = new BioPhysicalEntity("h"); bn.addPhysicalEntity(h);
		r1 = new BioChemicalReaction("r1");
		r1.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r1.addRightParticipant(new BioPhysicalEntityParticipant(h));
		r2 = new BioChemicalReaction("r2");
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(b));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(d));
		r2.addLeftParticipant(new BioPhysicalEntityParticipant(h));
		r2.addRightParticipant(new BioPhysicalEntityParticipant(c));
		r3 = new BioChemicalReaction("r3");
		r3.addLeftParticipant(new BioPhysicalEntityParticipant(e));
		r3.addRightParticipant(new BioPhysicalEntityParticipant(b));
		r3.setReversibility(true);
		r4 = new BioChemicalReaction("r4");
		r4.addLeftParticipant(new BioPhysicalEntityParticipant(e));
		r4.addRightParticipant(new BioPhysicalEntityParticipant(c));
		r4.addRightParticipant(new BioPhysicalEntityParticipant(f));
		r5 = new BioChemicalReaction("r5");
		r5.addLeftParticipant(new BioPhysicalEntityParticipant(a));
		r5.addRightParticipant(new BioPhysicalEntityParticipant(e));
		r5.setReversibility(true);
		r6 = new BioChemicalReaction("r6");
		r6.addLeftParticipant(new BioPhysicalEntityParticipant(d));
		r6.addRightParticipant(new BioPhysicalEntityParticipant(f));
		r7 = new BioChemicalReaction("r7");
		r7.addLeftParticipant(new BioPhysicalEntityParticipant(d));
		r7.addRightParticipant(new BioPhysicalEntityParticipant(f));
		bn.addBiochemicalReaction(r1);
		bn.addBiochemicalReaction(r2);
		bn.addBiochemicalReaction(r3);
		bn.addBiochemicalReaction(r4);
		bn.addBiochemicalReaction(r5);
		bn.addBiochemicalReaction(r6);
		bn.addBiochemicalReaction(r7);
		
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
		
		assertEquals("wrong in-degree of reaction "+r1.getId(), 1, g.inDegreeOf(r1));
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
		
		assertEquals("wrong in-degree of reaction "+a.getId(), 1, g.inDegreeOf(a));
		assertEquals("wrong out-degree of reaction "+a.getId(), 3, g.outDegreeOf(a));
		assertEquals("wrong in-degree of reaction "+b.getId(), 2, g.inDegreeOf(b));
		assertEquals("wrong out-degree of reaction "+b.getId(), 2, g.outDegreeOf(b));
		assertEquals("wrong in-degree of reaction "+c.getId(), 4, g.inDegreeOf(c));
		assertEquals("wrong out-degree of reaction "+c.getId(), 0, g.outDegreeOf(c));
		assertEquals("wrong in-degree of reaction "+d.getId(), 0, g.inDegreeOf(d));
		assertEquals("wrong out-degree of reaction "+d.getId(), 3, g.outDegreeOf(d));
		assertEquals("wrong in-degree of reaction "+e.getId(), 2, g.inDegreeOf(e));
		assertEquals("wrong out-degree of reaction "+e.getId(), 4, g.outDegreeOf(e));
		assertEquals("wrong in-degree of reaction "+f.getId(), 3, g.inDegreeOf(f));
		assertEquals("wrong out-degree of reaction "+f.getId(), 0, g.outDegreeOf(f));
		assertEquals("wrong in-degree of reaction "+h.getId(), 1, g.inDegreeOf(h));
		assertEquals("wrong out-degree of reaction "+h.getId(), 1, g.outDegreeOf(h));
	}
	
	@Test
	public void testGetBipartiteGraph(){
		BipartiteGraph g = builder.getBipartiteGraph();
		for(BipartiteEdge e : g.edgeSet()){
			System.out.println(e.getV1().getId()+" -> "+e.getV2().getId());
		}
		assertEquals("wrong number of vertices",14, g.vertexSet().size());
		assertEquals("wrong number of edges",22, g.edgeSet().size());
		
		assertEquals("wrong in-degree of reaction "+a.getId(), 1, g.inDegreeOf(a));
		assertEquals("wrong out-degree of reaction "+a.getId(), 2, g.outDegreeOf(a));
		assertEquals("wrong in-degree of reaction "+b.getId(), 2, g.inDegreeOf(b));
		assertEquals("wrong out-degree of reaction "+b.getId(), 2, g.outDegreeOf(b));
		assertEquals("wrong in-degree of reaction "+c.getId(), 1, g.inDegreeOf(c));
		assertEquals("wrong out-degree of reaction "+c.getId(), 0, g.outDegreeOf(c));
		assertEquals("wrong in-degree of reaction "+d.getId(), 0, g.inDegreeOf(d));
		assertEquals("wrong out-degree of reaction "+d.getId(), 3, g.outDegreeOf(d));
		assertEquals("wrong in-degree of reaction "+e.getId(), 2, g.inDegreeOf(e));
		assertEquals("wrong out-degree of reaction "+e.getId(), 3, g.outDegreeOf(e));
		assertEquals("wrong in-degree of reaction "+f.getId(), 3, g.inDegreeOf(f));
		assertEquals("wrong out-degree of reaction "+f.getId(), 0, g.outDegreeOf(f));
		assertEquals("wrong in-degree of reaction "+h.getId(), 1, g.inDegreeOf(h));
		assertEquals("wrong out-degree of reaction "+h.getId(), 1, g.outDegreeOf(h));
		
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

}
