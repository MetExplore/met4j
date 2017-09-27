/*******************************************************************************
 * Copyright INRA
 * 
 *  Contact: ludovic.cottret@toulouse.inra.fr
 * 
 * 
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *  In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *  The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 ******************************************************************************/

package fr.inra.toulouse.metexplore.met4j_core.biodata;

import org.junit.Test;

/**
 * Test class extending BioParticipant
 *
 */
class BioParticipantFake extends BioParticipant {

	public BioParticipantFake(BioPhysicalEntity physicalEntity, Double quantity) {
		super(physicalEntity, quantity);
	}

}

/**
 * Test class extending BioPhysicalEntity
 */
class BioPhysicalEntityFake extends BioPhysicalEntity {

	public BioPhysicalEntityFake(String id) {
		super(id);
	}

}

public class BioParticipantTest {

	/**
	 * Test if a catch an exception if the number is nan
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetNaNQuantity() {

		BioParticipant p = new BioParticipantFake(new BioPhysicalEntityFake("test"), 2.0);
		Double nan = Double.NaN;
		p.setQuantity(nan);

	}
	
	/**
	 * Test if a catch an exception if the number is infinite
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetInfiniteQuantity() {

		BioParticipant p = new BioParticipantFake(new BioPhysicalEntityFake("test"), 2.0);
		Double inf = Double.POSITIVE_INFINITY;
		p.setQuantity(inf);
	}
	
	/**
	 * Test if a catch an exception if the number is <= 0
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetNegativeOrNullQuantity() {

		BioParticipant p = new BioParticipantFake(new BioPhysicalEntityFake("test"), 2.0);
		Double n = 0.0;
		p.setQuantity(n);
		
		n = -10.0;
		p.setQuantity(n);
	}

}
