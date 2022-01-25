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
package fr.inrae.toulouse.metexplore.met4j_core.biodata;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;

/**
 * @author lcottret
 *
 */
public class BioReactionTest {

	public static BioNetwork network;

	public static BioCompartment cpt1, cpt2;

	public static BioMetabolite l1, l2, r1, r2;
	public static BioReactant l1Reactant, l2Reactant, r1Reactant, r2Reactant;

	public static BioReaction reaction, reaction2;

	public static BioGene g1, g2, g3;

	@Before
	public void init()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		network = new BioNetwork();

		cpt1 = new BioCompartment("cpt1");
		cpt2 = new BioCompartment("cpt2");

		l1 = new BioMetabolite("l1");
		l2 = new BioMetabolite("l2");
		r1 = new BioMetabolite("r1");
		r2 = new BioMetabolite("r2");

		l1Reactant = new BioReactant(l1, 1.0, cpt1);
		l2Reactant = new BioReactant(l2, 1.0, cpt1);
		r1Reactant = new BioReactant(r1, 1.0, cpt2);
		r2Reactant = new BioReactant(r2, 1.0, cpt2);

		reaction = new BioReaction("testreaction");
		reaction2 = new BioReaction("testreaction2");

		network.add(reaction, reaction2, cpt1, cpt2, l1, l2, r1, r2);

		network.affectToCompartment(cpt1, l1, l2);
		network.affectToCompartment(cpt2, r1, r2);

		network.addReactants(l1Reactant, l2Reactant, r1Reactant, r2Reactant);

		network.affectLeft(reaction, l1Reactant, l2Reactant);
		network.affectRight(reaction, r1Reactant, r2Reactant);

		network.affectLeft(reaction2, l1Reactant);
		network.affectRight(reaction2, l2Reactant);

		BioEnzyme e1 = new BioEnzyme("e1");
		BioProtein p1 = new BioProtein("p1");
		BioProtein p2 = new BioProtein("p2");
		BioEnzyme e2 = new BioEnzyme("e2");
		BioProtein p3 = new BioProtein("p3");
		g1 = new BioGene("g1");
		g2 = new BioGene("g2");
		g3 = new BioGene("g3");

		network.add(e1, e2, g1, g2, g3, p1, p2, p3);

		network.affectEnzyme(reaction, e1, e2);

		BioEnzymeParticipant ep1 = new BioEnzymeParticipant(p1);
		BioEnzymeParticipant ep2 = new BioEnzymeParticipant(p2);
		BioEnzymeParticipant ep3 = new BioEnzymeParticipant(p3);

		network.addEnzymeParticipants(ep1, ep2, ep3);

		network.affectSubUnit(e1, ep1);
		network.affectSubUnit(e2, ep2, ep3);

		network.affectGeneProduct(p1, g1);
		network.affectGeneProduct(p2, g2);
		network.affectGeneProduct(p3, g3);

	}

	/**
	 * Test method for
	 * {@link BioReaction#toString()}.
	 * 
	 */
	@Test
	public void testToString() {

		assertEquals("Formula badly formatted", "testreaction",
				reaction.toString());

	}

	/**
	 * Test method for
	 * {@link BioReaction#isTransportReaction()}.
	 */
	@Test
	public void testIsTransportReaction() {

		// Positive test
		assertTrue("Must be a transport reaction", reaction.isTransportReaction());


		assertFalse("Must not be a transport reaction", reaction2.isTransportReaction());
	}

	/**
	 * Test method for
	 * {@link BioReaction#getLeftsView()}.
	 */
	@Test
	public void testGetLeft() {
		BioCollection<BioPhysicalEntity> leftCpds = new BioCollection<BioPhysicalEntity>();
		leftCpds.add(l1Reactant.getPhysicalEntity());
		leftCpds.add(l2Reactant.getPhysicalEntity());

		assertArrayEquals("getLeft does not function well", leftCpds.toArray(), reaction.getLeftsView().toArray());

	}

	/**
	 * Test method for
	 * {@link BioReaction#getRightsView()}.
	 */
	@Test
	public void testGetRight() {
		BioCollection<BioPhysicalEntity> rightCpds = new BioCollection<BioPhysicalEntity>();
		rightCpds.add(r1Reactant.getPhysicalEntity());
		rightCpds.add(r2Reactant.getPhysicalEntity());

		assertArrayEquals("getLeft does not function well", rightCpds.toArray(), reaction.getRightsView().toArray());
	}

	/**
	 * Test method for
	 * {@link BioReaction#getEntities()}.
	 */
	@Test
	public void testGetEntities() {

		BioCollection<BioPhysicalEntity> entities = new BioCollection<BioPhysicalEntity>();
		entities.add(l1Reactant.getPhysicalEntity());
		entities.add(l2Reactant.getPhysicalEntity());
		entities.add(r1Reactant.getPhysicalEntity());
		entities.add(r2Reactant.getPhysicalEntity());

		// With distinct metabolites
		assertArrayEquals("getEntities does not return the good entities", entities.toArray(),
				reaction.getEntities().toArray());

	}

	/**
	 * Test method for
	 * {@link BioReaction#getLeftReactants()}.
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws ArrayComparisonFailure
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLeftReactants() throws ArrayComparisonFailure, IllegalArgumentException {

		BioCollection<BioReactant> refs = new BioCollection<>();
		refs.add(l1Reactant, l2Reactant);
		assertEquals(refs, reaction.getLeftReactants());

	}
	
	/**
	 * Test method for
	 * {@link BioReaction#getRightReactants()}.
	 * 
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws ArrayComparisonFailure
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetRightReactants() throws ArrayComparisonFailure, IllegalArgumentException {

		BioCollection<BioReactant> refs = new BioCollection<>();
		refs.add(r1Reactant, r2Reactant);
		assertEquals(refs, reaction.getRightReactants());

	}

	@Test
	public void testGetGenes() {

		BioCollection<BioGene> refs = new BioCollection<>();
		refs.add(g1, g2, g3);

		assertEquals(refs, reaction.getGenes());
	}

	/**
	 * Test method for
	 * {@link BioReaction#getMetabolitesView()}
	 */
	@Test
	public void testGetMetabolitesView() {
		BioCollection<BioMetabolite> refs = new BioCollection<>();
		refs.add(l1Reactant.getMetabolite(), l2Reactant.getMetabolite(), r1Reactant.getMetabolite(), r2Reactant.getMetabolite());

		BioCollection<BioMetabolite> tests = reaction.getMetabolitesView();

		assertEquals(refs, tests);

	}

}
