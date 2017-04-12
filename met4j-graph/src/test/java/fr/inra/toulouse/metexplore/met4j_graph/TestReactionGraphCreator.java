package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioChemicalReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntity;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPhysicalEntityParticipant;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import fr.inra.toulouse.metexplore.met4j_graph.io.ReactionGraphCreator;

public class TestReactionGraphCreator {

	/** The graph. */
	public static BioNetwork bn;
	
	/** The path. */
	public static ReactionGraphCreator builder;
	
	/** The nodes. */
	public static BioPhysicalEntity a,b,c,d,e,f,h;
	
	/** The edges. */
	public static BioChemicalReaction r1,r2,r3,r4,r5,r6;	
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
		bn.addBiochemicalReaction(r1);
		bn.addBiochemicalReaction(r2);
		bn.addBiochemicalReaction(r3);
		bn.addBiochemicalReaction(r4);
		bn.addBiochemicalReaction(r5);
		bn.addBiochemicalReaction(r6);
		
		try{
			builder = new ReactionGraphCreator(bn);
		}catch(Exception e){
			fail("error while creating reaction graph builder");
		}
	}
	
	@Test
	public void testGetReactionGraph0() {
		ReactionGraph g = builder.getReactionGraph0();
		assertEquals("wrong number of vertices",6, g.vertexSet().size());
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
	}
	
	@Test
	public void testGetReactionGraph1() {
		ReactionGraph g = builder.getReactionGraph1();
		assertEquals("wrong number of vertices",6, g.vertexSet().size());
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
	}
	
	@Test
	public void testGetReactionGraph2() {
		ReactionGraph g = builder.getReactionGraph2();
		assertEquals("wrong number of vertices",6, g.vertexSet().size());
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
	}
	
	@Test
	public void testGetReactionGraph3() {
		ReactionGraph g = builder.getReactionGraph3();
		assertEquals("wrong number of vertices",6, g.vertexSet().size());
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
	}
	
	@Test
	public void testGetReactionGraph4() {
		ReactionGraph g = builder.getReactionGraph4();
		assertEquals("wrong number of vertices",6, g.vertexSet().size());
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
	}
	
	@Test
	public void testGetReactionGraph5() {
		ReactionGraph g = builder.getReactionGraph5();
		assertEquals("wrong number of vertices",6, g.vertexSet().size());
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
	}
}
