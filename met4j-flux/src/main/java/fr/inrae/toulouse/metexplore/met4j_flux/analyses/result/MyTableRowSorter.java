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

package fr.inrae.toulouse.metexplore.met4j_flux.analyses.result;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Comparator;

public class MyTableRowSorter<M extends TableModel> extends TableRowSorter<M> {

	public MyTableRowSorter(M model) {
		setModel(model);
	}

	public Comparator<?> getComparator(int c) {

		return new Comparator<Object>() {
			@Override
			public int compare(Object arg0, Object arg1) {

				String s1 = String.valueOf(arg0);
				String s2 = String.valueOf(arg1);

				if (!isDouble(s1)) {
					if (!isDouble(s2)) {
						return (s1.compareToIgnoreCase(s2));
					} else {
						return -1;
					}
				} else if (!isDouble(s2)) {
					return 1;
				} else {

					if (Double.parseDouble(s1) < Double.parseDouble(s2)) {
						return -1;
					} else if (Double.parseDouble(s1) > Double.parseDouble(s2)) {
						return 1;
					} else {
						return 0;
					}
				}
			}

		};

	}

	public boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
}
