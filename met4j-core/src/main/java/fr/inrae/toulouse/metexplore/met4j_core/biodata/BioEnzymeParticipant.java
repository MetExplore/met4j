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

import lombok.NonNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * <p>BioEnzymeParticipant class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioEnzymeParticipant extends BioParticipant {

    /**
     * Constructor
     *
     * @param physicalEntity the physical entity that is contained in the enzyme
     * @param stoichiometry  the number of unities of the physical entity
     */
    protected BioEnzymeParticipant(@NonNull BioPhysicalEntity physicalEntity, @NonNull Double stoichiometry) {
        super(physicalEntity.getId() + "__" + stoichiometry, physicalEntity, stoichiometry);
    }

    /**
     * Constructor
     *
     * @param physicalEntity the physical entity that is contained in the enzyme
     *                       The stoichiometry is put to 1.
     */
    public BioEnzymeParticipant(@NonNull BioPhysicalEntity physicalEntity) {
        this(physicalEntity, 1.0);
    }

    /**
     * {@inheritDoc}
     */
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

        return quantityStr + " " + this.getPhysicalEntity().getId();

    }
}
