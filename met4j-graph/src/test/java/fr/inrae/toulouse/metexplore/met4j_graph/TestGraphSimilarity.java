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

package fr.inrae.toulouse.metexplore.met4j_graph;

import static org.junit.Assert.*;

import fr.inrae.toulouse.metexplore.met4j_graph.computation.analysis.GraphSimilarity;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.CompoundGraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.compound.ReactionEdge;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;

public class TestGraphSimilarity {
	
	public static CompoundGraph g1;
	public static CompoundGraph g2;
	
	@BeforeClass
	public static void init(){
		
		g1 = new CompoundGraph();
		g2 = new CompoundGraph();
		
		BioMetabolite a = new BioMetabolite("a");g1.addVertex(a);
		BioMetabolite b = new BioMetabolite("b");g1.addVertex(b);g2.addVertex(b);
		BioMetabolite c = new BioMetabolite("c");g1.addVertex(c);g2.addVertex(c);
		BioMetabolite d = new BioMetabolite("d");g1.addVertex(d);g2.addVertex(d);
		BioMetabolite e = new BioMetabolite("e");g1.addVertex(e);g2.addVertex(e);
		BioMetabolite f = new BioMetabolite("f");g2.addVertex(f);
		
		ReactionEdge ab = new ReactionEdge(a, b, new BioReaction("r1"));
		g1.addEdge(a, b, ab);
		ReactionEdge bc = new ReactionEdge(b, c, new BioReaction("r2"));
		g1.addEdge(b, c, bc);
		g2.addEdge(b, c, bc);
		ReactionEdge cd1 = new ReactionEdge(c, d, new BioReaction("r3"));
		ReactionEdge cd2 = new ReactionEdge(c, d, new BioReaction("r4"));
		g1.addEdge(c, d, cd1);
		g2.addEdge(c, d, cd2);
		ReactionEdge de = new ReactionEdge(d, e, new BioReaction("r5"));
		g1.addEdge(d, e, de);
		g2.addEdge(d, e, de);
		ReactionEdge ef = new ReactionEdge(e, f, new BioReaction("r6"));
		g2.addEdge(e, f, ef);
	}
	
	@Test
	public void testGetNumberOfSharedLinks() {
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim = new GraphSimilarity<>(g1, g2);
		assertEquals(3, gSim.getNumberOfSharedLinks());
		
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim2 = new GraphSimilarity<>(g2, g1);
		assertEquals(3, gSim2.getNumberOfSharedLinks());
	}
	
	
	@Test
	public void testGetTanimoto() {
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim = new GraphSimilarity<>(g1, g2);
		assertEquals(3.0/5.0, gSim.getTanimoto(), 0.0000001);
		
		GraphSimilarity<BioMetabolite, ReactionEdge, CompoundGraph> gSim2 = new GraphSimilarity<>(g2, g1);
		assertEquals(3.0/5.0, gSim2.getTanimoto(), 0.0000001);
	}

}
