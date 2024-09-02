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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.FBCParser;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.plugin.PackageParser;
import org.junit.Test;
import org.sbml.jsbml.SBMLDocument;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class JsbmlReaderTest {

    @Test
    public void read() throws IOException, XMLStreamException, Met4jSbmlReaderException {

        JsbmlReader reader = spy(new JsbmlReader("test"));

        SbmlDocMock docMockGenerator  = new SbmlDocMock();
        SBMLDocument doc = docMockGenerator.doc;

        doReturn(doc).when(reader).sbmlRead();

        BioNetwork network = reader.read();

        assertNotNull(network);

        assertEquals(3, network.getReactionsView().size());

        assertEquals(2, network.getGenesView().size());

        // Other tests are done on JsbmlToBioNetwork

    }

    @Test
    public void readWithoutGenes() throws IOException, XMLStreamException, Met4jSbmlReaderException {

        JsbmlReader reader = spy(new JsbmlReader("test"));

        SbmlDocMock docMockGenerator  = new SbmlDocMock();
        SBMLDocument doc = docMockGenerator.doc;

        doReturn(doc).when(reader).sbmlRead();

        BioNetwork network = reader.read(new ArrayList<PackageParser>(Arrays.asList(new FBCParser(false))));

        assertNotNull(network);

        assertEquals(3, network.getReactionsView().size());

        assertEquals(0, network.getGenesView().size());


    }

    @Test
    public void readWithoutNotes() throws IOException, XMLStreamException, Met4jSbmlReaderException {
        JsbmlReader reader = spy(new JsbmlReader("test"));

        SbmlDocMock docMockGenerator  = new SbmlDocMock();
        SBMLDocument doc = docMockGenerator.doc;

        doReturn(doc).when(reader).sbmlRead();

        BioNetwork network = reader.readWithoutNotes();

        assertNotNull(network);

        assertEquals(3, network.getReactionsView().size());
    }


}