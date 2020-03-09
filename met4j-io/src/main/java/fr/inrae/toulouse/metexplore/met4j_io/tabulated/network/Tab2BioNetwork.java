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

import fr.inrae.toulouse.metexplore.met4j_core.biodata.*;
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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    }

    /**
     * Test the input file
     *
     * @param fileIn
     * @return
     * @throws IOException
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

        String ligne;

        int nLines = 0;

        Set<String> reactions = new HashSet<String>();

        while ((ligne = br.readLine()) != null) {

            nLines++;

            if (nLines > this.nSkip && !ligne.startsWith("#")) {

                String[] tab = ligne.split("\\t");

                if (tab.length < this.colId || tab.length < this.colFormula) {
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
                    Pattern compartment_Pattern = Pattern.compile("^(\\[.*\\] : ).*$");
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

                    for (String cpdId : lefts) {

                        cpdId = cpdId.trim();

                        String sto = "1";

                        String[] t = cpdId.split(" ");

                        if (t.length > 2) {
                            System.err.println("Some extra spaces present in metabolite " + cpdId + " line " + nLines + " : " + formula);
                            flag = false;
                        }

                        if (t.length == 2) {
                            sto = t[0];
                            sto = sto.replace("(", "").replace(")", "").trim();
                            cpdId = t[1].trim();
                        }


                    }

                    for (String cpdId : rights) {

                        cpdId = cpdId.trim();

                        String sto = "1";

                        String[] t = cpdId.split(" ");

                        if (t.length > 2) {
                            System.err.println("Some extra spaces present in metabolite " + cpdId + " line " + nLines + " : " + formula);
                        }

                        if (t.length == 2) {
                            sto = t[0];
                            sto = sto.replace("(", "").replace(")", "").trim();
                            cpdId = t[1].trim();
                        }
                    }

                }
            }
        }

        if (flag == false) {
            System.err.println("Input file badly formatted");
        } else {
            System.err.println("The input file looks good and contains " + reactions.size() + " reactions");
        }

        in.close();

        return flag;
    }


    /**
     * Fill the network from formulas in the file
     *
     * @param fileIn
     * @throws IOException
     */
    public Boolean createReactionsFromFile(String fileIn) throws IOException {

        Boolean flag = true;

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
        String ligne;

        int nLines = 0;


        if (!this.addCompartmentFromMetaboliteSuffix) {
            this.bioNetwork.add(defaultCompartment);
        }


        while ((ligne = br.readLine()) != null) {

            nLines++;

            if (nLines > this.nSkip && !ligne.startsWith("#")) {

                String[] tab = ligne.split("\\t");

                String id = tab[this.colId].trim();

//				id = id.replaceAll("[^A-Za-z0-9_]", "_");

//				id = StringUtils.sbmlEncode(id);

                String formula = tab[this.colFormula];

                BioReaction reaction;

                String reactionId = id;
                if (this.formatReactionCobra) {
                    reactionId = StringUtils.formatReactionIdCobra(id);
                }

                reaction = new BioReaction(reactionId);

                this.bioNetwork.add(reaction);

                // in some palsson files, the compartment is specified at the beginning of the formula :
                // [c] : g3p + nad + pi <==> 13dpg + h + nadh
                Pattern compartment_Pattern = Pattern.compile("^(\\[.*\\] : ).*$");
                Matcher matcher = compartment_Pattern.matcher(formula);

                String compartmentId = "";

                if (matcher.matches()) {
                    String occurence = matcher.group(1);

                    String[] tabOcc = occurence.split(":");

                    compartmentId = tabOcc[0].replace("[", "").replace("]", "").trim();

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


                List<String> lefts = new ArrayList<String>();


                if (!leftString.equals("")) {
                    lefts = Arrays.asList(leftString.split(" \\+"));
                }

                String rightString;

                if (tabFormula.length == 2) {
                    rightString = tabFormula[1].trim();
                } else {
                    rightString = "";
                }

                List<String> rights = new ArrayList<String>();
                if (!rightString.equals("")) {
                    rights = Arrays.asList(rightString.split(" \\+ "));
                }

                // To transform metabolite id like this cpd[c] in cpd_c
                Pattern cpd_Pattern = Pattern.compile("^.*(\\[([^\\]]*)\\])$");

                // To see if the id ends with _something
//				Pattern cpt_Pattern = Pattern.compile("^.*_([^_])*$");

                // Flag to check if external metabolite ids have been created
//				Boolean flagExt = true;
//
//				while (flagExt) {
//					flagExt = false;

                for (String cpdId : lefts) {

                    cpdId = cpdId.trim();

                    String sto = "1";

                    String[] t = cpdId.split(" ");

                    if (t.length == 2) {
                        sto = t[0];
                        sto = sto.replace("(", "").replace(")", "").trim();
                        cpdId = t[1].trim();
                    }

                    Matcher matcherCpd = cpd_Pattern.matcher(cpdId);

                    if (matcherCpd.matches()) {
                        cpdId = cpdId.replace(matcherCpd.group(1), "_" + matcherCpd.group(2));
                    } else {
                        if (!compartmentId.equals("") && !leftString.equals("")) {
                            cpdId = cpdId + "_" + compartmentId;
                        }
                    }

                    Double stoDouble = 1.0;
                    if(fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isDouble(sto)) {
                        stoDouble = Double.parseDouble(sto);
                    }

//						cpdId = cpdId.replaceAll("[^A-Za-z0-9_]", "_");

                    BioReactant reactant = this.initReactant(cpdId, stoDouble);

                    this.bioNetwork.affectLeft(reaction, reactant);

                    // We create the corresponding external metabolite
//						if(rightString.equals("")) {
//							String cpdExternalId = cpdId+this.flagExternal;
//
//							Matcher matcherCpt = cpt_Pattern.matcher(cpdId);
//
//							// If the metabolite contains compartment id in its suffix, we replace
//							// it in the external metabolite id by the external suffix
//
//							if(matcherCpt.matches()) {
//								String cptId = matcherCpt.group(1);
//								if(this.getBioNetwork().getCompartments().containsKey(cptId)) {
//									cpdExternalId = cpdId.replace("_"+cptId, this.flagExternal);
//								}
//							}
//
//							rights.add(cpdExternalId);
//						}
                }

                for (String cpdId : rights) {

                    cpdId = cpdId.trim();

                    String sto = "1";

                    String[] t = cpdId.split(" ");

                    if (t.length == 2) {
                        sto = t[0];
                        sto = sto.replace("(", "").replace(")", "").trim();
                        cpdId = t[1].trim();
                    }

                    Matcher matcherCpd = cpd_Pattern.matcher(cpdId);

                    if (matcherCpd.matches()) {
                        cpdId = cpdId.replace(matcherCpd.group(1), "_" + matcherCpd.group(2));
                    } else {
                        if (!compartmentId.equals("") && !rightString.equals("")) {
                            cpdId = cpdId + "_" + compartmentId;
                        }
                    }


//						cpdId = cpdId.replaceAll("[^A-Za-z0-9_]", "_");

                    Double stoDouble = 1.0;
                    if(fr.inrae.toulouse.metexplore.met4j_core.utils.StringUtils.isDouble(sto)) {
                        stoDouble = Double.parseDouble(sto);
                    }

//						cpdId = cpdId.replaceAll("[^A-Za-z0-9_]", "_");

                    BioReactant reactant = this.initReactant(cpdId, stoDouble);

                    this.bioNetwork.affectRight(reaction, reactant);

                    // We create the corresponding external metabolite
//						if(leftString.equals("")) {
//							String cpdExternalId = cpdId+this.flagExternal;
//
//							Matcher matcherCpt = cpt_Pattern.matcher(cpdId);
//
//							// If the metabolite contains compartment id in its suffix, we replace
//							// it in the external metabolite id by the external suffix
//
//							if(matcherCpt.matches()) {
//								String cptId = matcherCpt.group(1);
//								if(this.getBioNetwork().getCompartments().containsKey(cptId)) {
//									cpdExternalId = cpdId.replace("_"+cptId, this.flagExternal);
//								}
//							}
//
//							lefts.add(cpdExternalId);
//							flagExt=true;
//						}
//					}
                }

            }

        }

        br.close();

        System.err.println(bioNetwork.getReactionsView().size() + " reactions, " + bioNetwork.getMetabolitesView().size() + " metabolites and " + this.bioNetwork.getCompartmentsView().size() + " compartments created");

        return flag;
    }


    /**
     * Inits a BioMetabolite with the specified options
     *
     * @param cpdId
     * @return
     */
    private BioReactant initReactant(String cpdId, Double coeff) {

        BioCompartment compartment = defaultCompartment;

        if (this.addCompartmentFromMetaboliteSuffix) {
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


            this.bioNetwork.add(compartment);
            this.bioNetwork.affectToCompartment(compartment, cpd);


            if (cpd.getId().endsWith(this.flagExternal)) {
                MetaboliteAttributes.setBoundaryCondition(cpd, true);
            } else {
                MetaboliteAttributes.setBoundaryCondition(cpd, false);
            }



        return new BioReactant(cpd, coeff, compartment );

    }

    public BioNetwork getBioNetwork() {
        return bioNetwork;
    }
}
