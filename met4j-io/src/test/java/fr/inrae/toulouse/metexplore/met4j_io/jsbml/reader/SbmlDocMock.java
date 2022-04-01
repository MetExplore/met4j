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

import org.sbml.jsbml.*;

import javax.xml.stream.XMLStreamException;

public class SbmlDocMock {

    public SBMLDocument doc;
    Model model;
    JsbmlToBioNetwork parser;
    Compartment c1, c2, c3;
    Species m1, m2, m3, m4;
    Reaction r1, r2, r3;
    SpeciesType type1, type2, type3;

    public SbmlDocMock() {

        doc = new SBMLDocument(3, 1);

        model = doc.createModel();

        model.setId("modelId");
        model.setName("modelName");

        c1 = model.createCompartment("c1");
        c1.setName("compartment1");
        c2 = model.createCompartment("c2");
        c2.setName("compartment2");
        c3 = model.createCompartment("c3");

        c1.setSize(2.0);

        c1.setSpatialDimensions(4.0);

        c1.setName("test");

        m1 = model.createSpecies("m1", "name1", c1);
        m2 = model.createSpecies("m2", "name2", c2);
        m4 = model.createSpecies("m4", "m4", c1);

        m3 = model.createSpecies("m3");
        m3.setCompartment(c1);

        m1.setConstant(true);
        m2.setConstant(false);

        m1.setInitialAmount(2.0);
        m2.setInitialAmount(3.0);


        r1 = model.createReaction("r1");
        r1.setName("name1");
        r1.setReversible(false);

        r1.setSBOTerm("SBO:0000167");

        r2 = model.createReaction("r2");

        SpeciesReference m1Ref = new SpeciesReference(m1);
        m1Ref.setStoichiometry(2.0);

        SpeciesReference m1RefBis = new SpeciesReference(m1);
        m1RefBis.setStoichiometry(3.0);

        SpeciesReference m2Ref = new SpeciesReference(m2);

        m1Ref.setConstant(true);
        m2Ref.setConstant(false);

        // This metabolite must not be taken into account
        SpeciesReference m4Ref = new SpeciesReference(m4);
        m4Ref.setStoichiometry(0.0);

        r1.addReactant(m1Ref);
        r1.addReactant(m4Ref);
        r1.addProduct(m2Ref);
        r1.addProduct(m1RefBis);

        r3 = model.createReaction("r3");
    }
}
