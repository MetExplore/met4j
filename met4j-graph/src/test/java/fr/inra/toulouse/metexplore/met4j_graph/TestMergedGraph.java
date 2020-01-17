/*
 * Copyright INRAE (2020)
 *
 * contact-metexplore@inrae.fr
 *
 * This software is a computer program whose purpose is to [describe
 * functionalities and technical features of your software].
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "https://cecill.info/licences/Licence_CeCILL_V2.1-en.html".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.inra.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.inra.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inra.toulouse.metexplore.met4j_core.biodata.BioReactant;
import fr.inra.toulouse.metexplore.met4j_graph.core.BioPath;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.CompressedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.compressed.PathEdge;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MergedGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.parallel.MetaEdge;

public class TestMergedGraph {

	public static CompoundGraph cg;
	public static MergedGraph<BioMetabolite,ReactionEdge> cg2;
	public static BioMetabolite v1,v2;
	public static BioReaction r1,r2;
	public static ReactionEdge e1,e2;
	public static MetaEdge<BioMetabolite, ReactionEdge> e;
	public static BioNetwork bn;
	public static BioCompartment comp;
	
	@BeforeClass
	public static void init(){
		
		cg = new CompoundGraph();
		bn = new BioNetwork();
		comp = new BioCompartment("comp"); bn.add(comp);
		v1 = new BioMetabolite("v1"); bn.add(v1); bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2"); bn.add(v2); bn.affectToCompartment(comp, v2);
		
		r1 = new BioReaction("r1"); bn.add(r1);
		bn.affectLeft(v1, 1.0, comp, r1);
		bn.affectRight(v2, 1.0, comp, r1);
		r2 = new BioReaction("r2"); bn.add(r2);
		bn.affectLeft(v1, 1.0, comp, r2);
		bn.affectRight(v2, 1.0, comp, r2);
		
		e1 = new ReactionEdge(v1, v2, r1);
		e2 = new ReactionEdge(v1, v2, r2);
		
		cg.addVertex(v1);
		cg.addVertex(v2);
		cg.addEdge(v1, v2, e1);
		cg.addEdge(v1, v2, e2);
		
		HashSet<ReactionEdge> reactionList = new HashSet<ReactionEdge>();
		reactionList.add(e1);
		reactionList.add(e2);
		
		cg2 = new MergedGraph<BioMetabolite,ReactionEdge>();
		cg2.addVertex(v1);
		cg2.addVertex(v2);
		
		e = new MetaEdge<BioMetabolite, ReactionEdge>(v1, v2, reactionList);
		cg2.addEdge(v1, v2, e);
		
		assertEquals(2, cg2.vertexSet().size());
		assertEquals(1, cg2.edgeSet().size());
	}
	
	@Test
	public void testCopyEdge() {
		MetaEdge<BioMetabolite, ReactionEdge> ec = cg2.copyEdge(e);
		assertEquals(v1, ec.getV1());
		assertEquals(v2, ec.getV2());

		assertTrue(ec.getEdgeList().contains(e1));
		assertTrue(ec.getEdgeList().contains(e2));
	}
	
	@Test
	public void testReverseEdge() {
		MetaEdge<BioMetabolite, ReactionEdge> er = cg2.reverseEdge(e);
		assertEquals(v1, er.getV2());
		assertEquals(v2, er.getV1());

		assertTrue(er.getEdgeList().contains(e1));
		assertTrue(er.getEdgeList().contains(e2));
	}
	
	
	@Test
	public void testAddEdge(){
		MergedGraph<BioMetabolite, ReactionEdge> cg3 
		= (MergedGraph<BioMetabolite, ReactionEdge>) cg2.clone();
		cg3.addEdge(v2, v1);
		assertEquals(2, cg3.edgeSet().size());
	}
	
}
