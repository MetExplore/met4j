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

import fr.inrae.toulouse.metexplore.met4j_io.tabulated.attributes.EntityType;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.*;

/**
 * <p>Abstract AbstractSbmlSetAny class.</p>
 *
 * @author lcottret
 */
public abstract class AbstractSbmlSetAny extends AbstractSbmlSet {

    public final String setDescription = "The ids must correspond between the tabulated file and the SBML file.\n" +
            "If prefix or suffix is different in the SBML file, use the -p or the -s options.";

    @ParameterType(name=Text)
    @Option(name="-ci", usage="[1] number of the column where are the object ids")
    public int colid=1;

    @Option(name="-p", usage="[deactivated] To match the objects in the sbml file, adds the prefix R_ to reactions and M_ to metabolites")
    public Boolean p=false;

    @Option(name="-s", usage="[deactivated] To match the objects in the sbml file, adds the suffix _comparmentID to metabolites")
    public Boolean s=false;

    @ParameterType(name=Text)
    @Option(name="-t", usage="[REACTION] Object type in the column id : REACTION;METABOLITE;GENE;PATHWAY")
    public EntityType o= EntityType.REACTION;

    /**
     * <p>Constructor for AbstractSbmlSetAny.</p>
     */
    public AbstractSbmlSetAny() {
    }

}
