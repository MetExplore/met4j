/*
 * Copyright INRAE (2022)
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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.network;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import org.junit.Test;

import static org.junit.Assert.*;

public class BioNetwork2TabTest {

    @Test
    public void getReactionLine() {

        BioReaction r = new BioReaction("r", "reaction");
        BioMetabolite m1 = new BioMetabolite("m1", "metabolite1");
        BioMetabolite m2 = new BioMetabolite("m2", "metabolite2");
        BioCompartment c = new BioCompartment("c");
        BioGene g = new BioGene("g1");
        BioProtein p = new BioProtein("p1");
        BioEnzyme e = new BioEnzyme("e1");
        BioPathway pathway1 = new BioPathway("p1");
        BioPathway pathway2 = new BioPathway("p2");

        BioNetwork network = new BioNetwork();
        network.add(r, m1, m2, c, g, p, e, pathway1, pathway2);
        network.affectToCompartment(c, m1, m2);
        network.affectGeneProduct(p, g);
        network.affectSubUnit(e, 1.0, p);
        network.affectEnzyme(r, e);
        network.affectLeft(r, 1.0, c, m1);
        network.affectRight(r, 1.0, c, m2);
        network.affectToPathway(pathway1, r);
        network.affectToPathway(pathway2, r);

        ReactionAttributes.setLowerBound(r, new Flux(-10.0));
        ReactionAttributes.setUpperBound(r, new Flux(10.0));

        BioNetwork2Tab app = new BioNetwork2Tab(network, "", "<-->", "-->");

        String testLine = app.getReactionLine(r);
        String refLine = "r\treaction\tm1[c] <--> m2[c]\tmetabolite1[c] <--> metabolite2[c]\tNA\tp1 ; p2\tg1\t-10.0\t10.0\n";

        assertEquals(refLine, testLine);

        r.setReversible(false);
        testLine = app.getReactionLine(r);
        refLine = "r\treaction\tm1[c] --> m2[c]\tmetabolite1[c] --> metabolite2[c]\tNA\tp1 ; p2\tg1\t-10.0\t10.0\n";

        assertEquals(refLine, testLine);
    }
}