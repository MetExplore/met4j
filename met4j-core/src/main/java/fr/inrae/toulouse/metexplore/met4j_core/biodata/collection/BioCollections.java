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

package fr.inrae.toulouse.metexplore.met4j_core.biodata.collection;


import java.util.HashSet;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioEntity;

/**
 * <p>BioCollections class.</p>
 *
 * @author cfrainay
 */
public class BioCollections{

	/**
	 * <p>intersect.</p>
	 *
	 * @param collections 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection}
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} containing the entities formed by the intersection of the entities in collections
	 * @param <E> a E object.
	 */
	@SafeVarargs
	public static <E extends BioEntity> BioCollection<E> intersect(BioCollection<E>... collections){

		if(collections.length > 0) {
			BioCollection<E> intersect = new BioCollection<>(collections[0]);
			for (int i = 1; i < collections.length; i++) {
				intersect.retainAll(collections[i]);
				if (intersect.isEmpty()) return intersect;
			}
			return intersect;
		}
		else {
			return new BioCollection<>();
		}
	}

	/**
	 * <p>union.</p>
	 *
	 * @param collections 0 or several {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection}
	 * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection} containing the entities formed by the union of the entities in collections
	 * @param <E> a E object.
	 */
	@SafeVarargs
	public static <E extends BioEntity> BioCollection<E> union(BioCollection<E>... collections){
		
		HashSet<E> union = new HashSet<>();
		for(BioCollection<E> collection : collections){
			union.addAll(collection);
		}
		return new BioCollection<>(union);
	}
}
