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

package fr.inrae.toulouse.metexplore.met4j_core.biodata.collection;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.*;

public class BioCollectionsTest {
    BioCollection a, b;
    BioMetabolite m1, m2, m3;

    @Before
    public void init() {
        a = new BioCollection();
        b = new BioCollection();

        m1 = new BioMetabolite("m1");
        m2 = new BioMetabolite("m2");
        m3 = new BioMetabolite("m3");

        b.add(m1, m2);
        a.add(m1, m3);
    }

    @Test
    public void intersect() {

        BioCollection intersect = BioCollections.intersect(a, b);

        assertEquals(1, intersect.size());
        assertTrue(intersect.contains(m1));

    }

    @Test
    public void intersectWithNoCollection() {
        BioCollection intersect = BioCollections.intersect();
        assertEquals(0, intersect.size());

    }

    @Test
    public void union() {

        BioCollection union = BioCollections.union(a, b);

        assertEquals(3, union.size());
        assertTrue(union.contains(m1));
        assertTrue(union.contains(m2));
        assertTrue(union.contains(m3));
    }
}