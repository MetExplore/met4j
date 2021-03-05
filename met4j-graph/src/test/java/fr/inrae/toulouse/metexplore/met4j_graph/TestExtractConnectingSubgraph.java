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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_graph.computation.transform.ExtractConnectingSubgraph;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.CompoundEdge;
import fr.inrae.toulouse.metexplore.met4j_graph.core.reaction.ReactionGraph;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestExtractConnectingSubgraph {

    public static ReactionGraph rg;
    public static BioMetabolite v1,v2,v3;
    public static BioReaction r1,r2,r3,r4;
    public static BioPathway p;
    public static CompoundEdge e1,e2,e3,e4;
    public static BioNetwork bn;
    public static BioCompartment comp;

    @BeforeClass
    public static void init(){

        rg = new ReactionGraph();
        bn = new BioNetwork();
        comp = new BioCompartment("comp");bn.add(comp);

        v1 = new BioMetabolite("v1");bn.add(v1);bn.affectToCompartment(comp, v1);
        v2 = new BioMetabolite("v2");bn.add(v2);bn.affectToCompartment(comp, v2);
        v3 = new BioMetabolite("v3");bn.add(v3);bn.affectToCompartment(comp, v3);

        p = new BioPathway("p");bn.add(p);

        r1 = new BioReaction("r1");bn.add(r1);
        bn.affectLeft(r1, 1.0, comp, v1);
        bn.affectRight(r1, 1.0, comp, v2);
        bn.affectToPathway(p, r1);
        r2 = new BioReaction("r2");bn.add(r2);
        bn.affectLeft(r2, 1.0, comp, v2);
        bn.affectRight(r2, 1.0, comp, v3);
        bn.affectToPathway(p, r2);
        r3 = new BioReaction("r3");bn.add(r3);
        bn.affectLeft(r3, 1.0, comp, v2);
        bn.affectRight(r3, 1.0, comp, v3);
        r3.setReversible(true);
        r4 = new BioReaction("r4");bn.add(r4);
        bn.affectLeft(r4, 1.0, comp, v3);
        bn.affectRight(r4, 1.0, comp, v1);
        r4.setReversible(true);

        e1 = new CompoundEdge(r1, r2, v2);
        e2 = new CompoundEdge(r2, r3, v3);
        e3 = new CompoundEdge(r4, r3, v3);
        e4 = new CompoundEdge(r3, r4, v3);

        rg.addVertex(r1);
        rg.addVertex(r2);
        rg.addVertex(r3);
        rg.addVertex(r4);
        rg.addEdge(r1, r2, e1);
        rg.addEdge(r2, r3, e2);
        rg.addEdge(r4, r3, e3);
        rg.addEdge(r3, r4, e4);

        assertEquals(4, rg.vertexSet().size());
        assertEquals(4, rg.edgeSet().size());

    }

    @Test
    public void cleanGraph() {
        BioCollection<BioReaction> noi = new BioCollection<>();
        noi.add(r1);
        noi.add(r3);
        ExtractConnectingSubgraph<BioReaction, CompoundEdge> extractor = new ExtractConnectingSubgraph<>(rg, noi);
        extractor.cleanGraph();

        assertEquals(3, rg.vertexSet().size());
        assertEquals(2, rg.edgeSet().size());
        assertTrue(rg.containsVertex(r1));
        assertTrue(rg.containsVertex(r2));
        assertTrue(rg.containsVertex(r3));
        assertTrue(rg.areConnected(r1,r2));
        assertTrue(rg.areConnected(r2,r3));
    }
}