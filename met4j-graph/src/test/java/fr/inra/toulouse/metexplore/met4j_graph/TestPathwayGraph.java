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

import fr.inra.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
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
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraph;
import fr.inra.toulouse.metexplore.met4j_graph.core.pathway.PathwayGraphEdge;

public class TestPathwayGraph {

	public static PathwayGraph pg;
	public static BioPathway p1,p2;
	public static BioMetabolite v1,v2,v3;
	public static BioReaction r1,r2,r3;
	public static PathwayGraphEdge e;
	public static BioNetwork bn;
	public static BioCompartment comp;
	
	@BeforeClass
	public static void init(){
		
		pg = new PathwayGraph();
		bn = new BioNetwork();
		comp = new BioCompartment("comp"); bn.add(comp);
		v1 = new BioMetabolite("v1"); bn.add(v1); bn.affectToCompartment(comp, v1);
		v2 = new BioMetabolite("v2"); bn.add(v2); bn.affectToCompartment(comp, v2);
		v3 = new BioMetabolite("v3"); bn.add(v3); bn.affectToCompartment(comp, v3);
		
		p1 = new BioPathway("p1");bn.add(p1);
		p2 = new BioPathway("p2");bn.add(p2);
		
		r1 = new BioReaction("r1");bn.add(r1);
		bn.affectLeft(v1, 1.0, comp, r1);
		bn.affectRight(v2, 1.0, comp, r1);
		r2 = new BioReaction("r2");bn.add(r2);
		bn.affectLeft(v2, 1.0, comp, r2);
		bn.affectRight(v3, 1.0, comp, r2);
		r3 = new BioReaction("r3");bn.add(r3);
		bn.affectLeft(v3, 1.0, comp, r3);
		bn.affectRight(v1, 1.0, comp, r3);
		r3.setReversible(true);
		
		bn.affectToPathway(p1, r1);
		bn.affectToPathway(p1, r3);
		bn.affectToPathway(p2, r2);
		
		BioCollection<BioMetabolite> connectingCompounds = new BioCollection<>();
		connectingCompounds.add(v1);
		connectingCompounds.add(v3);
		
		e = new PathwayGraphEdge(p1, p2, connectingCompounds);
		
		pg.addVertex(p1);
		pg.addVertex(p2);
		pg.addEdge(p1, p2, e);
		
		pg.getEdge(p1,p2).removeConnectingCompounds(v3);
		pg.getEdge(p1,p2).addConnectingCompounds(v3);


		assertEquals(2, pg.vertexSet().size());
		assertEquals(1, pg.edgeSet().size());
	}
	
	@Test
	public void testCopyEdge() {
		PathwayGraphEdge ec = pg.copyEdge(e);
		assertEquals(p1, ec.getV1());
		assertEquals(p2, ec.getV2());
		assertEquals(2, ec.getConnectingCompounds().size());
		assertTrue(ec.getConnectingCompounds().contains(v1));
		assertTrue(ec.getConnectingCompounds().contains(v3));
	}
	
	@Test
	public void testReverseEdge() {
		PathwayGraphEdge er = pg.reverseEdge(e);
		assertEquals(p1, er.getV2());
		assertEquals(p2, er.getV1());
		assertEquals(2, er.getConnectingCompounds().size());
		assertTrue(er.getConnectingCompounds().contains(v1));
		assertTrue(er.getConnectingCompounds().contains(v3));
	}
	
	
	@Test
	public void testAddEdge(){
		PathwayGraph pg2 = (PathwayGraph) pg.clone();
		pg2.addEdge(p2, p1);
		assertEquals(2, pg2.edgeSet().size());
	}
	
}
