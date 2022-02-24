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

package fr.inrae.toulouse.metexplore.met4j_io.tabulated.network;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.Flux;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.reaction.ReactionAttributes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

/**
 * <p>BioNetwork2Tab class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class BioNetwork2Tab {

    BioNetwork network;
    String outputFile;
    String revSep = "<==>";
    String irrevSep = "-->";

    /**
     * <p>Constructor for BioNetwork2Tab.</p>
     *
     * @param bioNetwork a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     * @param outputFile a {@link java.lang.String} object.
     * @param revSep a {@link java.lang.String} object.
     * @param irrevSep a {@link java.lang.String} object.
     */
    public BioNetwork2Tab(BioNetwork bioNetwork, String outputFile, String revSep, String irrevSep) {

        this.network = bioNetwork;
        this.outputFile = outputFile;

        this.revSep = revSep;
        this.irrevSep = irrevSep;
    }

    /**
     * <p>write.</p>
     *
     * @throws java.io.IOException if any.
     */
    public void write() throws IOException {

        FileWriter fw = new FileWriter(this.outputFile);
        fw.write("#Id\tName\tFormulaIds\tFormulaNames\tEC\tpathways\tgpr\tlb\tub\n");
        /**
         * For ordering reactions in the file and thus making easy the tests and the versioning
         */
        TreeMap<String, BioReaction> reactions = new TreeMap<String, BioReaction>(
                this.network.getReactionsView().getMapView());

        for(BioReaction r : reactions.values()) {
            fw.write(this.getReactionLine(r));
        }
        fw.close();

    }


    /**
     * Get reaction information in a String
     * @param r a {@link BioReaction}
     * @return a String
     */
    protected String getReactionLine(BioReaction r) {
        String name = r.getName();
        String formulaIds = BioReactionUtils.getEquation(r, false, revSep, irrevSep, true);
        String formulaNames =BioReactionUtils.getEquation(r, true, revSep, irrevSep, true);
        String ec = r.getEcNumber() == null ? "NA" : r.getEcNumber();
        String pathways = BioReactionUtils.getPathwaysString(r, this.network,true, " ; ");
        String gpr = BioReactionUtils.getGPR(this.network, r);
        Flux lowerBound = ReactionAttributes.getLowerBound(r);
        String lb = lowerBound != null ? lowerBound.value.toString() : "NA";
        Flux upperBound = ReactionAttributes.getUpperBound(r);
        String ub = upperBound != null ? upperBound.value.toString() : "NA";

        return r.getId()+"\t"+name+"\t"+formulaIds+"\t"+formulaNames+"\t"+ec+"\t"
                + pathways + "\t" + gpr + "\t" + lb + "\t" +ub+"\n";
    }

}
