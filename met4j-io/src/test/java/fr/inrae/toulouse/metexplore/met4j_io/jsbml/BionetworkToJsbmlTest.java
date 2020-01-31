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

package fr.inrae.toulouse.metexplore.met4j_io.jsbml;

import static org.junit.Assert.*;

import java.util.Set;

import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;
import org.junit.Before;
import org.junit.Test;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.KineticLaw;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.Unit;
import org.sbml.jsbml.UnitDefinition;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEnzyme;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioGene;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioPathway;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioProtein;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.compartment.CompartmentAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.network.NetworkAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.errors.JSBMLPackageWriterException;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.BioUnitDefinition;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.units.UnitSbml;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.BionetworkToJsbml;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.writer.plugin.NotesWriter;

public class BionetworkToJsbmlTest {

    public SBMLDocument doc;
    Model model;
    BioNetwork network;
    BioReaction r1, r2;
    Reaction rSbml1, rSbml2;

    BioMetabolite m1, m2;
    BioCompartment c1, c2;

    Species s1, s3;
    Compartment compartSbml1, compartSbml2;

    BioUnitDefinition unitDefinition;

    NotesWriter writer;

    @Before
    public void init() throws JSBMLPackageWriterException {

        doc = new SBMLDocument(3, 1);
        model = doc.createModel();

        network = new BioNetwork();

        r1 = new BioReaction("r1");
        r2 = new BioReaction("r2");

        r1.setEcNumber("1.2.3.4");

        r2 = new BioReaction("r2");

        ReactionAttributes.setLowerBound(r1, new Flux(15.0));
        ReactionAttributes.setUpperBound(r1, new Flux(20.0));

        ReactionAttributes.setSboTerm(r1, "SBO:1234567");

        r1.setReversible(true);
        r1.setReversible(false);

        ReactionAttributes.setLowerBound(r1, new Flux(2.0));
        ReactionAttributes.setUpperBound(r1, new Flux(3.0));

        network.add(r1);
        network.add(r2);

        BioPathway pathway1 = new BioPathway("p1", "pathway 1");
        BioPathway pathway2 = new BioPathway("p2", "pathway 2");

        network.add(pathway1);
        network.add(pathway2);

        network.affectToPathway(pathway1, r1);
        network.affectToPathway(pathway2, r1);

        m1 = new BioMetabolite("m1");
        m2 = new BioMetabolite("m2");

        m1.setCharge(3);
        m1.setChemicalFormula("CH6");
        m1.setSmiles("sMILE");

        MetaboliteAttributes.setConstant(m1, true);
        MetaboliteAttributes.setConstant(m2, false);

        MetaboliteAttributes.setSboTerm(m1, "SBO:1234567");

        MetaboliteAttributes.setHasOnlySubstanceUnits(m1, true);
        MetaboliteAttributes.setHasOnlySubstanceUnits(m2, true);

        MetaboliteAttributes.setInitialAmount(m1, 2.0);
        MetaboliteAttributes.setInitialConcentration(m1, 3.0);
        MetaboliteAttributes.setInitialConcentration(m2, 3.0);

        network.add(m1);
        network.add(m2);

        unitDefinition = new BioUnitDefinition();

        NetworkAttributes.addUnitDefinition(network, unitDefinition);

        c1 = new BioCompartment("c1");
        c2 = new BioCompartment("c2");

        CompartmentAttributes.setConstant(c1, false);
        CompartmentAttributes.setConstant(c2, true);

        CompartmentAttributes.setSboTerm(c1, "SBO:1234567");

        CompartmentAttributes.setSpatialDimensions(c1, 3);

        CompartmentAttributes.setSize(c1, 4.0);

        network.add(c1);
        network.add(c2);

        network.affectToCompartment(c1, m1);
        network.affectToCompartment(c2, m2);

        network.affectLeft(m1, 2.0, c1, r1);
        network.affectRight(m2, 3.0, c2, r1);

        BioGene g1 = new BioGene("g1");
        BioGene g2 = new BioGene("g2");
        BioGene g3 = new BioGene("g3");

        network.add(g1);
        network.add(g2);
        network.add(g3);

        BioProtein p1 = new BioProtein("p1");
        BioProtein p2 = new BioProtein("p2");
        BioProtein p3 = new BioProtein("p3");

        network.add(p1);
        network.add(p2);
        network.add(p3);

        network.affectGeneProduct(p1, g1);
        network.affectGeneProduct(p2, g2);
        network.affectGeneProduct(p3, g3);

        BioEnzyme e1 = new BioEnzyme("e1");
        BioEnzyme e2 = new BioEnzyme("e2");

        network.add(e1);
        network.add(e2);

        network.affectSubUnit(p1, 1.0, e1);
        network.affectSubUnit(p2, 1.0, e1);
        network.affectSubUnit(p3, 1.0, e2);

        network.affectEnzyme(e1, r1);
        network.affectEnzyme(e2, r1);
        network.affectEnzyme(e2, r2);

        BionetworkToJsbml converter = new BionetworkToJsbml();

        model = converter.parseBioNetwork(network);

    }

    @Test
    public void testCreateUnits() {

        assertEquals(1, model.getUnitDefinitionCount());

        UnitDefinition u = model.getUnitDefinition(0);

        assertEquals(unitDefinition.getUnits().size(), u.getUnitCount());

        Set<String> unitKeys = unitDefinition.getUnits().keySet();

        for (int i = 0; i < u.getUnitCount(); i++) {
            Unit unit = u.getUnit(i);

            if (!unitKeys.contains(unit.getKind().getName())) {
                fail("Bad unit");
            }

            UnitSbml originalUnit = unitDefinition.getUnits().get(unit.getKind().getName());

            assertEquals(originalUnit.getExponent(), unit.getExponent(), 0.0);

            assertEquals(originalUnit.getScale(), unit.getScale(), 0.0);

            assertEquals(originalUnit.getMultiplier(), unit.getMultiplier(), 0.0);

        }

    }

    @Test
    public void testCreateCompartments() {

        assertEquals(network.getCompartmentsView().size(), model.getCompartmentCount());

        Compartment c1Test = model.getCompartment("c1");
        Compartment c2Test = model.getCompartment("c2");

        assertEquals(CompartmentAttributes.getConstant(c1), c1Test.getConstant());
        assertEquals(CompartmentAttributes.getConstant(c2), c2Test.getConstant());
        assertEquals(1234567, c1Test.getSBOTerm());

        assertEquals(CompartmentAttributes.getSpatialDimensions(c1), c1Test.getSpatialDimensions(), 0.0);

        assertEquals(CompartmentAttributes.getSize(c1), c1Test.getSize(), 0.0);

    }

    @Test
    public void testCreateSpecies() {

        assertEquals(network.getMetabolitesView().size(), model.getSpeciesCount());

        Species s1 = model.getSpecies("m1");
        Species s2 = model.getSpecies("m2");

        assertEquals(MetaboliteAttributes.getConstant(m1), s1.getConstant());
        assertEquals(MetaboliteAttributes.getConstant(m2), s2.getConstant());
        assertEquals(1234567, s1.getSBOTerm());

        assertEquals(MetaboliteAttributes.getHasOnlySubstanceUnits(m1), s1.getHasOnlySubstanceUnits());
        assertEquals(MetaboliteAttributes.getHasOnlySubstanceUnits(m2), s2.getHasOnlySubstanceUnits());
        assertEquals(Double.NaN, s1.getInitialConcentration(), 0.0);
        assertEquals(MetaboliteAttributes.getInitialAmount(m1), s1.getInitialAmount(), 0.0);
        assertEquals(Double.NaN, s2.getInitialAmount(), 0.0);
        assertEquals(MetaboliteAttributes.getInitialConcentration(m2), s2.getInitialConcentration(), 0.0);

    }

    @Test
    public void testCreateReactions() {

        assertEquals(network.getReactionsView().size(), model.getReactionCount());

        Reaction r1Test = model.getReaction("r1");
        Reaction r2Test = model.getReaction("r2");

        assertEquals(1234567, r1Test.getSBOTerm());

        assertEquals(r1.isReversible(), r1Test.isReversible());
        assertEquals(r2.isReversible(), r2Test.isReversible());

        assertEquals(network.getLeftReactants(r1).size(), r1Test.getListOfReactants().size());
        assertEquals(network.getLeftReactants(r2).size(), r2Test.getListOfReactants().size());

        assertEquals(network.getRightReactants(r1).size(), r1Test.getListOfProducts().size());
        assertEquals(network.getRightReactants(r2).size(), r2Test.getListOfProducts().size());

        assertEquals("m1", r1Test.getReactant(0).getSpecies());
        assertEquals("m2", r1Test.getProduct(0).getSpecies());

        assertEquals(2.0, r1Test.getReactant(0).getStoichiometry(), 0.0);
        assertEquals(3.0, r1Test.getProduct(0).getStoichiometry(), 0.0);

        KineticLaw law = r1Test.getKineticLaw();

        assertEquals(ReactionAttributes.getLowerBound(r1).value, law.getLocalParameter("LOWER_BOUND").getValue(), 0.0);
        assertEquals(ReactionAttributes.getUpperBound(r1).value, law.getLocalParameter("UPPER_BOUND").getValue(), 0.0);
        assertEquals(ReactionAttributes.getLowerBound(r1).unitDefinition.getId(),
                law.getLocalParameter("LOWER_BOUND").getUnits());
        assertEquals(ReactionAttributes.getUpperBound(r1).unitDefinition.getId(),
                law.getLocalParameter("UPPER_BOUND").getUnits());

    }

}
