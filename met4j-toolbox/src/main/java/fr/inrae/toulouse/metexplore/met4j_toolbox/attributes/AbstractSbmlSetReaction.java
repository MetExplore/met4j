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

package fr.inrae.toulouse.metexplore.met4j_toolbox.attributes;

import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

/**
 * <p>Abstract AbstractSbmlSetReaction class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public abstract class AbstractSbmlSetReaction extends AbstractSbmlSet {

    public final String setDescription = "The ids must correspond between the tabulated file and the SBML file.\n" +
            "If prefix R_ is present in the ids in the SBML file and not in the tabulated file, use the -p option.";

    @ParameterType(name= EnumParameterTypes.Integer)
    @Option(name="-ci", usage="[1] number of the column where are the reaction ids")
    public int colid=1;

    @Option(name="-p", usage="[deactivated] To match the objects in the sbml file, adds the prefix R_ to reactions")
    public Boolean p=false;

    /**
     * <p>Constructor for AbstractSbmlSetReaction.</p>
     */
    public AbstractSbmlSetReaction() {
        super();
    }
}
