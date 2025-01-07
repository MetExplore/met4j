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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioCompartment;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioMetabolite;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.BioReaction;
import fr.inrae.toulouse.metexplore.met4j_core.biodata.collection.BioCollection;
import fr.inrae.toulouse.metexplore.met4j_io.annotations.metabolite.MetaboliteAttributes;
import fr.inrae.toulouse.metexplore.met4j_io.utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isDouble;


/**
 * <p>Tab2BioNetwork class.</p>
 *
 * @author lcottret
 * @version $Id: $Id
 */
public class Tab2BioNetwork {

    public int colId = 0;
    public int colFormula = 1;
    public Boolean formatReactionCobra = true;
    public Boolean formatMetaboliteCobra = true;
    public String flagExternal = "_b";
    public String irrReaction = "-->";
    public String revReaction = "<==>";
    public Boolean addCompartmentFromMetaboliteSuffix = false;
    public String defaultCompartmentId = "c";
    public int nSkip = 0;

    private BioNetwork bioNetwork;

    private BioCompartment defaultCompartment;
    private HashSet<String> reactions;

    // To transform metabolite id like this cpd[c] in cpd_c
    private Pattern cpdWithBracketsPattern = Pattern.compile("^.*(\\[([^\\]]*)\\])$");


    /**
     * <p>Constructor for Tab2BioNetwork.</p>
     *
     * @param networkId                          a {@link java.lang.String} object.
     * @param colId                              a int.
     * @param colFormula                         a int.
     * @param formatReactionCobra                a {@link java.lang.Boolean} object.
     * @param formatMetaboliteCobra              a {@link java.lang.Boolean} object.
     * @param flagExternal                       a {@link java.lang.String} object.
     * @param irrReaction                        a {@link java.lang.String} object.
     * @param revReaction                        a {@link java.lang.String} object.
     * @param addCompartmentFromMetaboliteSuffix a {@link java.lang.Boolean} object.
     * @param defaultCompartment                 a {@link java.lang.String} object.
     * @param nSkip                              a int.
     */
    public Tab2BioNetwork(String networkId, int colId, int colFormula, Boolean formatReactionCobra, Boolean formatMetaboliteCobra,
                          String flagExternal, String irrReaction, String revReaction,
                          Boolean addCompartmentFromMetaboliteSuffix, String defaultCompartment, int nSkip) {

        this.bioNetwork = new BioNetwork(networkId);

        this.colId = colId;
        this.colFormula = colFormula;
        this.formatReactionCobra = formatReactionCobra;
        this.formatMetaboliteCobra = formatMetaboliteCobra;
        this.flagExternal = flagExternal;
        this.irrReaction = irrReaction;
        this.revReaction = revReaction;
        this.addCompartmentFromMetaboliteSuffix = addCompartmentFromMetaboliteSuffix;
        this.defaultCompartmentId = defaultCompartment;
        this.nSkip = nSkip;

        this.defaultCompartment = new BioCompartment(this.defaultCompartmentId, this.defaultCompartmentId);

        reactions = new HashSet<>();

    }

    /**
     * Test the input file
     *
     * @param fileIn a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean testFile(String fileIn) throws IOException {

        Boolean flag = true;

        FileInputStream in;
        BufferedReader br;
        try {
            in = new FileInputStream(fileIn);
            InputStreamReader ipsr = new InputStreamReader(in);
            br = new BufferedReader(ipsr);
        } catch (Exception e) {
            System.err.println("Impossible to read the input file " + fileIn);
            return false;
        }

        String line;

        int nLines = 0;

        reactions.clear();

        while ((line = br.readLine()) != null) {
            nLines++;

            try {
                Boolean flagLine = this.testLine(line, nLines);

                if (!flagLine) {
                    flag = false;
                }
            } catch (Exception error) {
                System.err.println("Unexpected error line " + nLines);
                flag = false;
            }
        }

        if (flag == false) {
            System.err.println("Input file badly formatted");
        } else {
            System.out.println("The input file looks good and contains " + reactions.size() + " reactions");
        }

        in.close();

        return flag;
    }


    /**
     * Check if the line is correct
     *
     * @param line
     * @param nLines
     * @return
     */
    protected Boolean testLine(String line, int nLines) {

        Boolean flag = true;

        if (nLines > this.nSkip && !line.startsWith("#")) {

            String[] tab = line.split("\\t");

            Pattern pattern = Pattern.compile("\\t");
            Matcher matcherTabulation = pattern.matcher(line);
            long nbColumns = matcherTabulation.results().count() + 1;

            if (nbColumns <= this.colId || nbColumns <= this.colFormula) {
                System.err.println("Bad number of columns line " + nLines);
                flag = false;
            } else {
                String id = tab[this.colId];
                String formula = tab[this.colFormula];

                // remove spaces
                id = id.trim();
                if (id.equals("")) {
                    System.err.println("Reaction id empty line " + nLines);
                    flag = false;
                }

                if (reactions.contains(id)) {
                    System.err.println("Duplicated reaction id : " + id + " line " + nLines);
                    flag = false;
                } else {
                    reactions.add(id);
                }

                if (!formula.contains(this.irrReaction) && !formula.contains(this.revReaction)) {
                    System.err.println("Reaction formula badly formatted line " + nLines + " : " + formula);
                    flag = false;
                }

                // in some palsson files, the compartment is specified at the beginning of the formula :
                // [c] : g3p + nad + pi <==> 13dpg + h + nadh
                Pattern compartment_Pattern = Pattern.compile("^(\\[.+\\]\\s*:\\s*).*$");
                Matcher matcher = compartment_Pattern.matcher(formula);

                if (matcher.matches()) {
                    String occurence = matcher.group(1);

                    formula = formula.replace(occurence, "");
                }

                String[] tabFormula;
                if (formula.contains(this.revReaction)) {
                    tabFormula = formula.split(this.revReaction);
                } else {
                    tabFormula = formula.split(this.irrReaction);
                }

                String leftString = tabFormula[0].trim();

                String[] lefts = {};

                if (!leftString.equals("")) {
                    lefts = leftString.split(" \\+ ");
                }

                String rightString;

                if (tabFormula.length == 2) {
                    rightString = tabFormula[1].trim();
                } else {
                    rightString = "";
                }


                String[] rights = {};
                if (!rightString.equals("")) {
                    rights = rightString.split(" \\+ ");
                }

                if (lefts.length == 0 && rights.length == 0) {
                    System.err.println("Error line " + nLines + " : reaction must have hat least one reactant (" + formula + ")");
                    flag = false;
                }

                for (String cpdId : lefts) {

                    cpdId = cpdId.trim();

                    String[] t = cpdId.split(" ");

                    if (t.length > 2) {
                        System.err.println("Some extra spaces present in metabolite " + cpdId + " line " + nLines + " : " + formula);
                        flag = false;
                    }
                }

                for (String cpdId : rights) {

                    cpdId = cpdId.trim();

                    String[] t = cpdId.split(" ");

                    if (t.length > 2) {
                        System.err.println("Some extra spaces present in metabolite " + cpdId + " line " + nLines + " : " + formula);
                        flag = false;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * Parses a line and create entities
     *
     * @param line
     * @param nLines
     * @return true if everything ok
     */
    protected Boolean parseLine(String line, int nLines) {

        Boolean flag = true;

        if (nLines > this.nSkip && !line.startsWith("#")) {

            String[] tab = line.split("\\t");

            String id = tab[this.colId].trim();

            String formula = tab[this.colFormula];

            BioReaction reaction;

            String reactionId = id;
            if (this.formatReactionCobra) {
                reactionId = StringUtils.formatReactionIdCobra(id);
            }

            reaction = new BioReaction(reactionId);


            // in some palsson files, the compartment is specified at the beginning of the formula :
            // [c] : g3p + nad + pi <==> 13dpg + h + nadh
            Pattern compartment_Pattern = Pattern.compile("^(\\[(.+)\\]\\s*:\\s*).*$");
            Matcher matcher = compartment_Pattern.matcher(formula);

            String compartmentId = "";

            if (matcher.matches()) {
                String occurence = matcher.group(1);

                compartmentId = matcher.group(2);

                formula = formula.replace(occurence, "");
            }


            String[] tabFormula;
            if (formula.contains(this.revReaction)) {
                reaction.setReversible(true);
                tabFormula = formula.split(this.revReaction);
            } else {
                reaction.setReversible(false);
                tabFormula = formula.split(this.irrReaction);
            }

            String leftString = tabFormula[0].trim();

            List<String> lefts = new ArrayList<>();
            List<String> rights = new ArrayList<>();

            if (!leftString.isEmpty()) {

                if (!leftString.equals("")) {
                    lefts = Arrays.asList(leftString.split(" \\+"));
                }
            }

            if (tabFormula.length > 1) {

                String rightString = tabFormula[1].trim();

                if (!rightString.isEmpty()) {

                    if (!rightString.equals("")) {
                        rights = Arrays.asList(rightString.split(" \\+ "));
                    }
                }
            }

            if (lefts.size() == 0 && rights.size() == 0) {
                System.err.println("Error line " + nLines + " : the reaction must have at least one reactant");
                return false;
            }

            this.bioNetwork.add(reaction);

            for (String cpdId : lefts) {
                parseReactant(reaction, compartmentId, cpdId, false);
            }

            for (String cpdId : rights) {
                parseReactant(reaction, compartmentId, cpdId, true);
            }

        }
        return flag;
    }

    /**
     * Parse
     *
     * @param reaction
     * @param compartmentId
     * @param cpdId
     * @param rightSide
     */
    private void parseReactant(BioReaction reaction, String compartmentId, String cpdId, Boolean rightSide) {
        cpdId = cpdId.trim();

        String sto = "1";

        String[] t = cpdId.split(" ");

        if (t.length == 2) {
            sto = t[0];
            sto = sto.replace("(", "").replace(")", "").trim();
            cpdId = t[1].trim();
        }

        Matcher matcherCpd = cpdWithBracketsPattern.matcher(cpdId);

        boolean addCompartment = false;

        if (matcherCpd.matches()) {
            cpdId = cpdId.replace(matcherCpd.group(1), "_" + matcherCpd.group(2));
            addCompartment = true;
        } else {
            if (!compartmentId.equals("")) {
                addCompartment = true;
                cpdId = cpdId + "_" + compartmentId;
            }
        }

        Double stoDouble = 1.0;
        if (isDouble(sto)) {
            stoDouble = Double.parseDouble(sto);
        }

        this.initReactant(reaction, cpdId, stoDouble, rightSide, addCompartment);
    }


    /**
     * Fill the network from formulas in the file
     *
     * @param fileIn a {@link java.lang.String} object.
     * @return a {@link java.lang.Boolean} object.
     * @throws java.io.IOException if any.
     */
    public Boolean createReactionsFromFile(String fileIn) throws IOException {

        Boolean flag;

        try {
            flag = this.testFile(fileIn);
        } catch (IOException e) {
            return false;
        }

        if (!flag) {
            return false;
        }

        FileInputStream in = new FileInputStream(fileIn);
        InputStreamReader ipsr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(ipsr);
        String line;

        int nLines = 0;


        if (!this.addCompartmentFromMetaboliteSuffix) {
            this.bioNetwork.add(defaultCompartment);
        }

        reactions.clear();

        while ((line = br.readLine()) != null) {

            nLines++;

            try {
                Boolean flagLine = this.parseLine(line, nLines);

                if (!flagLine) {
                    flag = false;
                }
            } catch (Exception e) {
                System.err.println("Unexpected error line " + nLines + " (" + e.getMessage() + ")");
                flag = false;
            }
        }

        br.close();

        System.out.println(bioNetwork.getReactionsView().size() + " reactions, " + bioNetwork.getMetabolitesView().size() + " metabolites and " + this.bioNetwork.getCompartmentsView().size() + " compartments created");

        return flag;
    }


    /**
     * Inits a BioMetabolite with the specified options
     *
     * @param cpdId
     * @return
     */
    private void initReactant(BioReaction reaction, String cpdId, Double coeff, Boolean rightSide, Boolean addCompartment) {

        if (coeff <= 0.0) {
            System.err.println("[WARNING] The coefficient for " + cpdId + " in the reaction " + reaction.getId() + " is not valid, it should be strictly positive. It has not been added to the reaction.");
            return;
        }

        BioCompartment compartment = defaultCompartment;

        if (this.addCompartmentFromMetaboliteSuffix || addCompartment) {
            String[] t = cpdId.split("_");

            String compartmentId = this.defaultCompartment.getId();

            if (t.length > 1) {
                compartmentId = t[t.length - 1];
            }

            BioCollection<BioCompartment> compartmentsView = this.bioNetwork.getCompartmentsView();

            if (compartmentsView.containsId(compartmentId)) {
                compartment = compartmentsView.get(compartmentId);
            } else {
                compartment = new BioCompartment(compartmentId, compartmentId);
                this.bioNetwork.add(compartment);
            }
        } else {
            if (!this.bioNetwork.contains(compartment)) {
                this.bioNetwork.add(compartment);
            }
        }


        if (this.formatMetaboliteCobra) {
            cpdId = StringUtils.formatMetaboliteIdCobra(cpdId, compartment.getId());
        }

        BioMetabolite cpd;
        BioCollection<BioMetabolite> metabolitesView = this.bioNetwork.getMetabolitesView();

        if (metabolitesView.containsId(cpdId)) {
            cpd = metabolitesView.get(cpdId);
        } else {
            cpd = new BioMetabolite(cpdId);
            this.bioNetwork.add(cpd);
        }
        this.bioNetwork.affectToCompartment(compartment, cpd);

        if (cpd.getId().endsWith(this.flagExternal)) {
            MetaboliteAttributes.setBoundaryCondition(cpd, true);
        } else {
            MetaboliteAttributes.setBoundaryCondition(cpd, false);
        }

        if (!rightSide) {
            this.bioNetwork.affectLeft(reaction, coeff, compartment, cpd);
        } else {
            this.bioNetwork.affectRight(reaction, coeff, compartment, cpd);
        }

        return;

    }

    /**
     * <p>Getter for the field <code>bioNetwork</code>.</p>
     *
     * @return a {@link fr.inrae.toulouse.metexplore.met4j_core.biodata.BioNetwork} object.
     */
    public BioNetwork getBioNetwork() {
        return bioNetwork;
    }
}
