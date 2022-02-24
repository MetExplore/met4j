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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;

public class SetPathwaysFromFileTest {


    private BioNetwork network;
    private BioReaction reaction;
    private SetPathwaysFromFile setPathwaysFromFile;
    private BioReaction reaction2;

    @Before
    public void init() throws IOException {
        network = new BioNetwork();
        reaction = new BioReaction("r");
        reaction2 = new BioReaction("r2");

        network.add(reaction, reaction2);
        setPathwaysFromFile = Mockito.spy(new SetPathwaysFromFile(0, 1, network, "", "", 0, false, false, ";"));

        Mockito.doReturn(true).when(setPathwaysFromFile).parseAttributeFile();

    }

    @Test
    public void testAttribute() {

        assertTrue(setPathwaysFromFile.testAttribute("anything"));

    }

    @Test
    public void setAttributes() throws IOException {

        String line = "r\tp1";

        Boolean flag = setPathwaysFromFile.parseLine(line, 1);

        assertTrue(flag);

        setPathwaysFromFile.setAttributes();

        assertEquals(1, network.getPathwaysView().size());
        assertTrue(network.getPathwaysView().containsId("p1"));

        BioPathway p = network.getPathwaysView().get("p1");

        assertEquals(1, network.getReactionsFromPathways(p).size());
        assertTrue(network.getReactionsFromPathways(p).containsId("r"));

        line = "r2\tp1;p2";

        flag = setPathwaysFromFile.parseLine(line, 1);

        assertTrue(flag);

        setPathwaysFromFile.setAttributes();

        assertEquals(2, network.getPathwaysView().size());

        assertTrue(network.getPathwaysView().containsId("p1"));
        assertTrue(network.getPathwaysView().containsId("p2"));

        assertEquals(2, network.getReactionsFromPathways(p).size());

        BioPathway p2 = network.getPathwaysView().get("p2");

        assertEquals(1, network.getReactionsFromPathways(p2).size());

        assertTrue(network.getReactionsFromPathways(p).containsId("r2"));

    }
}