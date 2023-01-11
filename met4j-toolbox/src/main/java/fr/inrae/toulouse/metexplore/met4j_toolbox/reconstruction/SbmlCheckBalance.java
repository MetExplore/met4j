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
package fr.inrae.toulouse.metexplore.met4j_toolbox.reconstruction;

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.utils.BioReactionUtils;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.JsbmlReader;
import fr.inrae.toulouse.metexplore.met4j_io.jsbml.reader.Met4jSbmlReaderException;

import fr.inrae.toulouse.metexplore.met4j_reconstruction.check.balance.NetworkBalanceAnalysis;
import fr.inrae.toulouse.metexplore.met4j_reconstruction.check.balance.ReactionBalanceAnalysis;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.AbstractMet4jApplication;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.Format;
import fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.ParameterType;
import org.kohsuke.args4j.Option;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Sbml;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumFormats.Tsv;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.InputFile;
import static fr.inrae.toulouse.metexplore.met4j_toolbox.generic.annotations.EnumParameterTypes.OutputFile;

public class SbmlCheckBalance extends AbstractMet4jApplication {

    @Format(name = Sbml)
    @ParameterType(name = InputFile)
    @Option(name = "-sbml", usage = "Original sbml file", required = true)
    public String sbml;

    @Format(name = Tsv)
    @ParameterType(name = OutputFile)
    @Option(name = "-out", usage = "[checkBalances.tsv] Output tabulated file (1st col: reaction id, 2nd col: " +
            "boolean indicating if the reaction is balanced, 3rd col: atom balances, 4th col: metabolites with bad formula")
    public String out = "checkBalances.tsv";
    private NetworkBalanceAnalysis analysis;

    public static void main(String[] args) {
        SbmlCheckBalance app = new SbmlCheckBalance();
        
        app.parseArguments(args);
        
        app.run();
    }

    private void run() {

        BioNetwork network = this.readSbml();

        analysis = new NetworkBalanceAnalysis(network);

        try {
            this.writeResults();
        } catch (IOException e) {
           e.printStackTrace();
            System.err.println("[MET4J ERROR] Error while writing the results");
        }

    }

    private void writeResults() throws IOException {

        List<ReactionBalanceAnalysis> balances = analysis.getAllBalances();

        FileWriter writer = new FileWriter(this.out);

        writer.write("#Id\tisBalanced\tisExchange\tformula\tatom balances\tmetabolites with bad formula\n");

        for (ReactionBalanceAnalysis balance : balances) {
            String id = balance.getReaction().getId();
            HashMap<String, Double> atomBalances = balance.getBalances();
            Set<String> metabolitesWithBadFormula = balance.getMetabolitesWithBadFormula().getIds();
            String formula = BioReactionUtils.getEquation(balance.getReaction(), false, true);

            writer.write(id + "\t" + balance.isBalanced() + "\t" + balance.isExchange() + "\t" + formula +"\t" + atomBalances + "\t" + (metabolitesWithBadFormula.size() > 0 ? metabolitesWithBadFormula : "") + "\n");
        }

        writer.close();

    }

    /**
     * <p>readSbml.</p>
     *
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     */
    private BioNetwork readSbml() {
        JsbmlReader reader = new JsbmlReader(this.sbml);

        BioNetwork bn = null;
        try {
            bn = reader.read();
        } catch (Met4jSbmlReaderException e) {
            e.printStackTrace();
            System.err.println("Problem while reading the sbml file " + this.sbml);
            System.exit(1);
        }

        return bn;

    }

    @Override
    public String getLabel() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getLongDescription() {
        return this.getShortDescription()+"\n"+
                "A reaction is balanced if all its reactants have a chemical formula with a good syntax and if the " +
                "quantity of each atom is the same in both sides of the reaction.\n" +
                "For each reaction, indicates if the reaction is balanced, the list of the atoms and the sum of their quantity, and the list of the metabolites " +
                "that don't have a correct chemical formula.";
    }

    @Override
    public String getShortDescription() {
        return "Check balance of all the reactions in a SBML.";
    }
}
