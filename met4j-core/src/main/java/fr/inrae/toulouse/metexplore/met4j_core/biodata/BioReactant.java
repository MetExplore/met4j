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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 *
 */
public class BioReactant extends BioParticipant {

	private BioCompartment location = null;

	/**
	 * @param metabolite
	 * @param stoichiometry
	 * @param location
	 */
	public BioReactant(BioMetabolite metabolite, Double stoichiometry, BioCompartment location) {
		super(metabolite, stoichiometry);
		this.setLocation(location);
	}

	public BioCompartment getLocation() {
		return location;
	}

	protected void setLocation(BioCompartment location) {
		this.location = location;
	}

	public BioMetabolite getMetabolite() {
		return (BioMetabolite) this.getPhysicalEntity();
	}

	@Override
	public String toString() {

		String quantityStr = "";

		if (this.getQuantity() == Math.floor(this.getQuantity())) {

			quantityStr += this.getQuantity().intValue();

		} else {
			Locale currentLocale = Locale.getDefault();

			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(currentLocale);
			otherSymbols.setDecimalSeparator('.');
			otherSymbols.setGroupingSeparator('.');

			NumberFormat formater = new DecimalFormat("#0.00", otherSymbols);
			quantityStr = formater.format(this.getQuantity());

		}

		StringBuilder buffer = new StringBuilder(quantityStr);
		buffer.append(" ");
		buffer.append(this.getId());
		if (this.getLocation() != null) {
			buffer.append("[");
			buffer.append(this.getLocation().getId());
			buffer.append("]");
		}

		return buffer.toString();

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		BioReactant that = (BioReactant) o;
		return location.equals(that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), location);
	}
}
